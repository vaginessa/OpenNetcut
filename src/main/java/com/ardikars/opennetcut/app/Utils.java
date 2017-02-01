package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.BpfProgram;
import static com.ardikars.jxnet.Jxnet.*;
import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.InetAddress;
import com.ardikars.jxnet.Jxnet;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.Pcap;
import com.ardikars.jxnet.PcapAddr;
import com.ardikars.jxnet.PcapIf;
import com.ardikars.jxnet.PcapPktHdr;
import com.ardikars.jxnet.SockAddr;
import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.jxnet.util.AddrUtils;
import com.ardikars.opennetcut.packet.protocol.datalink.Ethernet;
import com.ardikars.opennetcut.packet.protocol.network.ARP;
import com.ardikars.opennetcut.view.MainWindow;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.net.util.SubnetUtils;

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
        
        System.out.println(source);
        
        StringBuilder errbuf = new StringBuilder();
        
        Inet4Address netaddr = Inet4Address.valueOf(0);
        Inet4Address netmask = Inet4Address.valueOf(0);
        
        if (Jxnet.PcapLookupNet(source, netaddr, netmask, errbuf) < 0) {
            if (logHandler != null) 
                logHandler.log(-1, "[ WARNING ] :: " + errbuf.toString());
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
                logHandler.log(-1, "[ WARNING ] :: " + errbuf.toString());
        }
        
        MainWindow.main_windows.setPcap(pcap);
        
        if (Jxnet.PcapDatalink(pcap) != 1) {
            if (logHandler != null) 
                logHandler.log(-1, "[ WARNING ] :: " + source + " is not Ethernet link type.");
        }
        BpfProgram bp = new BpfProgram();
        if (Jxnet.PcapCompile(pcap, bp, "arp", 1, netmask.toInt()) !=0 ) {
            if (logHandler != null) 
                logHandler.log(-1, "[ WARNING ] :: Failed to compile bpf.");
        }
        if (Jxnet.PcapSetFilter(pcap, bp) != 0) {
            if (logHandler != null) 
                logHandler.log(-1, "[ WARNING ] :: Failed to compile arp filter.");
        } 
        
        Inet4Address currentIpAddr = Utils.getIpAddr(source);
        if (currentIpAddr == null) {
            if (logHandler != null) 
                logHandler.log(-1, "[ WARNING ] :: Failed get current IP Address.");
        }
        MacAddress currentMacAddr = AddrUtils.getHardwareAddress(source);
        if (currentMacAddr == null) {
            if (logHandler != null) 
                logHandler.log(-1, "[ WARNING ] :: Failed get current Mac Address.");
        }
        
        MainWindow.main_windows.setCurrentIpAddr(currentIpAddr);
        MainWindow.main_windows.setCurrentHwAddr(currentMacAddr);
        MainWindow.main_windows.setNetaddr(netaddr);
        MainWindow.main_windows.setNetmask(netmask);
        
        Inet4Address gwIpAddress = AddrUtils.getGatewayAddress(source);
        if (gwIpAddress == null) {
            if (logHandler != null) 
                logHandler.log(-1, "[ WARNING ] :: Failed get current Gateway IP Address.");
        }

        MainWindow.main_windows.gwIpAddr = gwIpAddress;

        MacAddress gwMacAddr = Utils.getMacAddrFromArp(
                MainWindow.main_windows.getPcap(), 
                MainWindow.main_windows.getCurrentIpAddr(),
                MainWindow.main_windows.getCurrentHwAddr(),
                MainWindow.main_windows.gwIpAddr);
        if (gwMacAddr == null) {
            if (logHandler != null) 
                logHandler.log(-1, "[ WARNING ] :: Failed get current Gateway Mac Address.");
        }
        MainWindow.main_windows.gwMacAddr = gwMacAddr;
        MainWindow.main_windows.initMyComponents();
    }
}
