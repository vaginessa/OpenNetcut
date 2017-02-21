/**
 * Copyright (C) 2017  Ardika Rommy Sanjaya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.BpfProgram;
import static com.ardikars.jxnet.Jxnet.*;
import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.Jxnet;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.Pcap;
import com.ardikars.jxnet.PcapAddr;
import com.ardikars.jxnet.PcapHandler;
import com.ardikars.jxnet.PcapIf;
import com.ardikars.jxnet.PcapPktHdr;
import com.ardikars.jxnet.SockAddr;
import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.jxnet.util.AddrUtils;
import com.ardikars.opennetcut.packet.PacketHandler;
import com.ardikars.opennetcut.packet.protocol.datalink.Ethernet;
import com.ardikars.opennetcut.packet.protocol.lan.ARP;
import com.ardikars.opennetcut.view.MainWindow;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("unchecked")
public class Utils {
    
    public static String getDeviceName() {
        StringBuilder errbuf = new StringBuilder();
        String device;
        if((device = PcapLookupDev(errbuf)) == null) {
            return errbuf.toString();
        }
        return device;
    }
    
    public static Inet4Address getIpAddr(String source) throws JxnetException {
        StringBuilder errbuf = new StringBuilder();
        List<PcapIf> pcapIf = new ArrayList<>();
        if (PcapFindAllDevs(pcapIf, errbuf) != 0) {
            throw new JxnetException("Failed to get Ip Address from " + source);
        }
        for (PcapIf If : pcapIf) {
            if (If.getName().equals(source)) {
                for (PcapAddr addrs : If.getAddresses()) {
                    if (addrs.getAddr().getSaFamily() == SockAddr.Family.AF_INET) {
                        return Inet4Address.valueOf(addrs.getAddr().getData());
                    }                            
                }
                break;
            }
        }
        return null;
    }
    
    public static MacAddress getMacAddrFromArp(Pcap pcap, Inet4Address currentIpAdd,
            MacAddress currentMacAddr, Inet4Address gwIpAddr) {
        Ethernet ethernet = new Ethernet().setDestinationMacAddress(MacAddress.BROADCAST)
                .setSourceMacAddress(currentMacAddr)
                .setEtherType(Ethernet.EtherType.ARP)
                .setPadding(true);
                
        ARP arp = new ARP().setHardwareType(ARP.HW_TYPE_ETHERNET)
                .setProtocolType(ARP.PROTO_TYPE_IP)
                .setHardwareAddressLength((byte) 6)
                .setProtocolAddressLength((byte) 4)
                .setOpCode(ARP.OperationCode.REQUEST)
                .setSenderHardwareAddress(currentMacAddr)
                .setSenderProtocolAddress(currentIpAdd)
                .setTargetHardwareAddress(MacAddress.ZERO)
                .setTargetProtocolAddress(gwIpAddr);
        ethernet.putChild(arp.toBytes());
        ByteBuffer buffer = ethernet.toBuffer();
        PcapPktHdr pktHdr = new PcapPktHdr();
        byte[] bytes;
        if (Jxnet.PcapSendPacket(pcap, buffer, buffer.capacity()) != 0) {
            JOptionPane.showConfirmDialog(null, "Failed to send arp packet.");
            return null;
        } else {
            ByteBuffer capBuf = null;
            while (capBuf == null) {
                capBuf = Jxnet.PcapNext(pcap, pktHdr);
                if (capBuf == null) {
                    continue;
                } else {
                    bytes = new byte[capBuf.capacity()];
                    capBuf.get(bytes);

                    if (capBuf != null && pktHdr != null) {
                        Ethernet capEth = Ethernet.wrap(bytes);
                        ARP capArp = null;
                        if (capEth.getChild() instanceof ARP) {
                            capArp = (ARP) capEth.getChild();
                            if (capArp.getOpCode() == ARP.OperationCode.REPLY) {
                                return capArp.getSenderHardwareAddress();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static MacAddress randomMacAddress() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<6; i++) {
            sb.append(String.format("%02x", random.nextInt(0xff)));
            if (i != 5) {
                sb.append(":");
            }
        }
        return MacAddress.valueOf(sb.toString());
    }
        
    public static DefaultTableModel createDefaultTableModel(String[] columnNames) {
        return new DefaultTableModel(new Object[][] {}, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 1:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                switch (column) {
                    case 1:
                        return true;
                    default:
                        return false;
                }
            }
            
        };
    }
    
    
    public static void initialize(String s, int snaplen, int promisc, int to_ms,
            LoggerHandler logHandler) throws JxnetException {

        String source = (s == null) ? getDeviceName() : s;
        
        StringBuilder errbuf = new StringBuilder();
        
        Inet4Address netaddr = Inet4Address.valueOf(0);
        Inet4Address netmask = Inet4Address.valueOf(0);
        
        if (Jxnet.PcapLookupNet(source, netaddr, netmask, errbuf) < 0) {
            if (logHandler != null) 
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: " + errbuf.toString());
            return;
        }
        
        if (netaddr.toInt() == Inet4Address.valueOf("127.0.0.1").toInt() ||
                netmask.toInt() == Inet4Address.valueOf("255.0.0.0").toInt()) {
            if (logHandler != null)
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: " + source + " is loopback interface.");
            return;
        }

        MainWindow.main_windows.setSource(source);
        MainWindow.main_windows.setSnaplen(1500);
        MainWindow.main_windows.setPromisc(1);
        MainWindow.main_windows.setToMs(150);
        
        Pcap pcap = PcapOpenLive(MainWindow.main_windows.getSource(), 
                MainWindow.main_windows.getSnaplen(),
                MainWindow.main_windows.getPromisc(),
                MainWindow.main_windows.getToMs(), errbuf);
        if (pcap == null) {
            if (logHandler != null) 
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: " + errbuf.toString());
            return;
        }
        
        if (Jxnet.PcapDatalink(pcap) != 1) {
            if (logHandler != null) 
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: " + source + " is not Ethernet link type.");
            PcapClose(pcap);
            return;
        }
        BpfProgram bp = new BpfProgram();
        if (Jxnet.PcapCompile(pcap, bp, "arp", 1, netmask.toInt()) !=0 ) {
            if (logHandler != null) 
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed to compile bpf.");
            PcapClose(pcap);
            return;
        }
        if (Jxnet.PcapSetFilter(pcap, bp) != 0) {
            if (logHandler != null) 
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed to compile arp filter.");
            PcapClose(pcap);
            return;
        }
        
        MainWindow.main_windows.setPcap(pcap);
        
        Inet4Address currentIpAddr = Utils.getIpAddr(source);
        if (currentIpAddr == null) {
            if (logHandler != null) 
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current IP Address.");
        }
        MacAddress currentMacAddr = AddrUtils.getHardwareAddress(source);
        if (currentMacAddr == null) {
            if (logHandler != null) 
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current Mac Address.");
        }
        
        MainWindow.main_windows.setCurrentIpAddr(currentIpAddr);
        MainWindow.main_windows.setCurrentHwAddr(currentMacAddr);
        MainWindow.main_windows.setNetaddr(netaddr);
        MainWindow.main_windows.setNetmask(netmask);
        
        Inet4Address gwIpAddress = AddrUtils.getGatewayAddress(source);
        if (gwIpAddress == null) {
            if (logHandler != null) 
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current Gateway IP Address.");
        }

        MainWindow.main_windows.gwIpAddr = gwIpAddress;

        MacAddress gwMacAddr = Utils.getMacAddrFromArp(
                MainWindow.main_windows.getPcap(), 
                MainWindow.main_windows.getCurrentIpAddr(),
                MainWindow.main_windows.getCurrentHwAddr(),
                MainWindow.main_windows.gwIpAddr);
        if (gwMacAddr == null) {
            if (logHandler != null) 
                logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current Gateway Mac Address.");
        }
        MainWindow.main_windows.gwMacAddr = gwMacAddr;
        MainWindow.main_windows.initMyComponents();
        logHandler.log(LoggerStatus.COMMON, "[ INFO ] :: Choosing inferface successed.");
    }
    
    public static String getPcapTmpFileName() {
        String fileName = MessageFormat.format("{0}.{1}", UUID.randomUUID(), new String("pcap").trim());
        return Paths.get(System.getProperty("java.io.tmpdir"), fileName).toString();
    }
    
    public static void copyFileUsingFileChannels(File source, File dest)
	            throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
    
    public static void openPcapFile(PacketHandler handler, LoggerHandler logHandler, String path) {
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = PcapOpenOffline(path, errbuf);
        if (pcap == null) {
            logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: " + errbuf.toString());
            return;
        }
        PcapHandler<PacketHandler> pcapHandler = (PacketHandler t, PcapPktHdr pktHdr, ByteBuffer capBuf) -> {
            int no = 1;
            byte[] bytes;
            if (capBuf != null) {
                bytes = new byte[capBuf.capacity()];
                capBuf.get(bytes);
                if (capBuf != null && pktHdr != null) {
                    Ethernet capEth = Ethernet.wrap(bytes);
                    ARP capArp = null;
                    if (capEth.getChild() instanceof ARP) {
                        capArp = (ARP) capEth.getChild();
                        if (capArp.getOpCode() == ARP.OperationCode.REPLY) {
                            handler.nextPacket(no, pktHdr, capArp);
                            no++;
                        }
                    }
                }
            }
        };
        PcapLoop(pcap, -1, pcapHandler, handler);
        PcapClose(pcap);
    }


    public static byte BYTE(int b) {
        return (byte)(b & 0xff);
    }

    public static short SHORT(int s) {
        return (short)(s & 0xffff);
    }

    public static void main(String... args) {
        System.out.println(getPcapTmpFileName());
    }
}
