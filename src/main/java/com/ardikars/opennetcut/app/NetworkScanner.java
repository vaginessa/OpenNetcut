package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.Jxnet;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.PcapDumper;
import com.ardikars.jxnet.PcapPktHdr;
import com.ardikars.opennetcut.packet.PacketHandler;
import com.ardikars.opennetcut.packet.protocol.datalink.Ethernet;
import com.ardikars.opennetcut.packet.protocol.lan.ARP;
import com.ardikars.opennetcut.view.MainWindow;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.util.SubnetUtils;

@SuppressWarnings("unchecked")
public class NetworkScanner extends Thread {
    
    private volatile boolean stop = false;
    
    private final PacketHandler handler;
    private final LoggerHandler logHandler;
    private final List<Inet4Address> ips = new ArrayList<Inet4Address>();
    private final MacAddress currentHwAddr;
    private final Inet4Address currentIpAddr;
    
    private Ethernet ethernet = new Ethernet();
    private ARP arp = new ARP();
            
    private int index;
    
    private Inet4Address ip;
    
    public NetworkScanner(PacketHandler handler, LoggerHandler logHandler) {
        SubnetUtils su = new SubnetUtils(MainWindow.main_windows.getNetaddr().toString(),
            MainWindow.main_windows.getNetmask().toString());
        String[] strips = su.getInfo().getAllAddresses();
        for (String ip : strips) {
            ips.add(Inet4Address.valueOf(ip));
        }
        this.currentIpAddr = MainWindow.main_windows.getCurrentIpAddr();
        this.currentHwAddr = MainWindow.main_windows.getCurrentHwAddr();
        this.handler = handler;
        this.logHandler = logHandler;
        this.index = 0;
    }
    
    public NetworkScanner(PacketHandler handler, Inet4Address ip, LoggerHandler logHandler) {
        this.currentIpAddr = MainWindow.main_windows.getCurrentIpAddr();
        this.currentHwAddr = MainWindow.main_windows.getCurrentHwAddr();
        this.handler = handler;
        this.logHandler = logHandler;
        this.ip = ip;
        this.index = 1;
    }
    
    @Override
    public void run() {
        if (logHandler != null)
            logHandler.log(LoggerStatus.COMMON, "[ INFO ] :: Start scanning.");
        ethernet.setDestinationMacAddress(MacAddress.BROADCAST)
                .setSourceMacAddress(currentHwAddr)
                .setEtherType(Ethernet.EtherType.ARP)
                .setPadding(true);
                
        arp.setHardwareType(ARP.HW_TYPE_ETHERNET)
                .setProtocolType(ARP.PROTO_TYPE_IP)
                .setHardwareAddressLength((byte) 6)
                .setProtocolAddressLength((byte) 4)
                .setOpCode(ARP.OperationCode.REQUEST)
                .setSenderHardwareAddress(currentHwAddr)
                .setSenderProtocolAddress(currentIpAddr)
                .setTargetHardwareAddress(MacAddress.ZERO);
        
        switch (index) {
            case 0:
                scanAll();
                break;
            case 1:
                scanByIp();
                break;
            default:
        }
    }
    
    private void scanAll() {
        MainWindow.main_windows.pcapTmpFileName = Utils.getPcapTmpFileName();
        PcapDumper dumper;
        dumper = Jxnet.PcapDumpOpen(MainWindow.main_windows.getPcap(), MainWindow.main_windows.pcapTmpFileName);
        if (dumper == null) {
            if (logHandler != null) {
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: " + Jxnet.PcapGetErr(MainWindow.main_windows.getPcap()));
            }
        }
        PcapPktHdr pktHdr = new PcapPktHdr();
        ByteBuffer buffer = null;
        byte[] bytes = null;
        int no=1;
        int ipsSize = ips.size();
        for(int i=0; i<ipsSize; i++) {
            arp.setTargetProtocolAddress(ips.get(i));
            ethernet.putChild(arp.toBytes());
            buffer = ethernet.toBuffer();
            if (Jxnet.PcapSendPacket(MainWindow.main_windows.getPcap(), buffer, buffer.capacity()) != 0) {
                if (logHandler != null) {
                    logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed to send arp packet.");
                }
                break;
            } else {
                ByteBuffer capBuf = Jxnet.PcapNext(MainWindow.main_windows.getPcap(), pktHdr);
                if (capBuf != null) {
                    bytes = new byte[capBuf.capacity()];
                    capBuf.get(bytes);
                    if (capBuf != null && pktHdr != null) {
                        Ethernet capEth = Ethernet.wrap(bytes);
                        ARP capArp = null;
                        if (capEth.getChild() instanceof ARP) {
                            capArp = (ARP) capEth.getChild();
                            if (capArp.getOpCode() == ARP.OperationCode.REPLY) {
                                Jxnet.PcapDump(dumper, pktHdr, capBuf);
                                handler.nextPacket(no, pktHdr, capArp);
                                no++;
                            }
                        }
                    }
                }
            }
            if (stop) {
                logHandler.log(LoggerStatus.PROGRESS, Integer.toString(100));
                if (!dumper.isClosed()) {
                    Jxnet.PcapDumpClose(dumper);
                    logHandler.log(LoggerStatus.COMMON, "[ INFO ] :: Scanning finished.");
                }
                return;
            }
            if (logHandler != null)
                logHandler.log(LoggerStatus.PROGRESS, Integer.toString((i * 100)/ipsSize));
        }
        if (logHandler != null) {
            logHandler.log(LoggerStatus.PROGRESS, Integer.toString(100));
            if (!dumper.isClosed()) {
                Jxnet.PcapDumpClose(dumper);
                logHandler.log(LoggerStatus.COMMON, "[ INFO ] :: Scanning finished.");
            }
        }
    }
    
    private void scanByIp() {
        MainWindow.main_windows.pcapTmpFileName = Utils.getPcapTmpFileName();
        PcapDumper dumper;
        dumper = Jxnet.PcapDumpOpen(MainWindow.main_windows.getPcap(), MainWindow.main_windows.pcapTmpFileName);
        if (dumper == null) {
            if (logHandler != null) {
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: " + Jxnet.PcapGetErr(MainWindow.main_windows.getPcap()));
            }
        }
        PcapPktHdr pktHdr = new PcapPktHdr();
        ByteBuffer buffer = null;
        byte[] bytes = null;
        int no=1;
        arp.setTargetProtocolAddress(ip);
        ethernet.putChild(arp.toBytes());
        buffer = ethernet.toBuffer();
        if (Jxnet.PcapSendPacket(MainWindow.main_windows.getPcap(), buffer, buffer.capacity()) != 0) {
            if (logHandler != null) {
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed to send arp packet.");
            }
            return;
        } else {
            ByteBuffer capBuf = Jxnet.PcapNext(MainWindow.main_windows.getPcap(), pktHdr);
            if (capBuf != null) {
                if (stop) {
                    logHandler.log(LoggerStatus.PROGRESS, Integer.toString(100));
                    if (!dumper.isClosed()) {
                        Jxnet.PcapDumpClose(dumper);
                        logHandler.log(LoggerStatus.COMMON, "[ INFO ] :: Scanning finished.");
                    }
                    return;
                }
                bytes = new byte[capBuf.capacity()];
                capBuf.get(bytes);
                if (capBuf != null && pktHdr != null) {
                    Ethernet capEth = Ethernet.wrap(bytes);
                    ARP capArp = null;
                    if (capEth.getChild() instanceof ARP) {
                        capArp = (ARP) capEth.getChild();
                        if (capArp.getOpCode() == ARP.OperationCode.REPLY) {
                            Jxnet.PcapDump(dumper, pktHdr, capBuf);
                            handler.nextPacket(no, pktHdr, capArp);
                            no++;
                        }
                    }
                }
            }
        }
        if (logHandler != null) {
            logHandler.log(LoggerStatus.PROGRESS, Integer.toString(100));
            if (!dumper.isClosed()) {
                Jxnet.PcapDumpClose(dumper);
                logHandler.log(LoggerStatus.COMMON, "[ INFO ] :: Scanning finished.");
            }
        }
    }
    
    public void stopThread() {
        this.stop = true;
    }
    
}
