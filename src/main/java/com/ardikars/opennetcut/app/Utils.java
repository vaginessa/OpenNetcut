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

import com.ardikars.jxnet.*;

import static com.ardikars.jxnet.Jxnet.*;

import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.jxnet.exception.PcapCloseException;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.util.AddrUtils;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.jxnet.Static;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.packet.protocol.datalink.ethernet.Ethernet;
import com.ardikars.jxnet.packet.protocol.lan.arp.ARP;
import com.ardikars.jxnet.packet.protocol.lan.arp.OperationCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("unchecked")
public class Utils {

    public static Inet4Address getCurrentInet4Address(String source) throws JxnetException {
        List<PcapIf> pcapIf = new ArrayList<>();
        if (PcapFindAllDevs(pcapIf, StaticField.ERRBUF) != 0) {
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
    
    public static MacAddress getGwAddrFromArp() {

        Ethernet ethernet = (Ethernet) PacketBuilder.arpBuilder(MacAddress.BROADCAST, OperationCode.ARP_REQUEST,
                StaticField.CURRENT_MAC_ADDRESS, StaticField.CURRENT_INET4ADDRESS,
                MacAddress.ZERO, StaticField.GATEWAY_INET4ADDRESS);

        ByteBuffer buffer = FormatUtils.toDirectBuffer(ethernet.getBytes());
        PcapPktHdr pktHdr = new PcapPktHdr();
        byte[] bytes;
        for (int i=0; i<100; i++) {
            if (PcapSendPacket(StaticField.PCAP, buffer, buffer.capacity()) != 0) {
                JOptionPane.showConfirmDialog(null, "Failed to send arp packet.");
                return null;
            }
            Map<Class, Packet> packets = Static.next(StaticField.PCAP, pktHdr);
            if (packets == null) continue;
            ARP arp = (ARP) packets.get(ARP.class);
            if (arp == null) continue;
            if (arp.getOpCode() == OperationCode.ARP_REPLY)
                System.out.println(arp.getSenderProtocolAddress() + " == " + StaticField.GATEWAY_INET4ADDRESS);
            if (arp.getOpCode() == OperationCode.ARP_REPLY &&
                    arp.getSenderProtocolAddress().equals(StaticField.GATEWAY_INET4ADDRESS)) {
                return arp.getSenderHardwareAddress();
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


    public static boolean compile(Pcap pcap, BpfProgram bpfProgram, String filter) throws PcapCloseException {
        if (pcap.isClosed()) {
            throw new PcapCloseException();
        }
        if (PcapCompile(pcap, bpfProgram, filter,
                StaticField.OPTIMIZE, StaticField.CURRENT_NETMASK_ADDRESS.toInt()) != 0) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed to compile bpf.");
                return false;
            }
            return true;
        }
        return false;
    }

    public static void filter(Pcap pcap, BpfProgram bpfProgram) throws PcapCloseException {
        if (pcap.isClosed()) {
            throw new PcapCloseException();
        }
        if (PcapSetFilter(pcap, bpfProgram) != 0) {
            if (StaticField.LOGGER != null)
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed to compile filter.");
        }
    }

    public static void initialize(String s, int snaplen, int promisc, int to_ms) throws JxnetException {

        StaticField.SOURCE = (s == null) ? AddrUtils.LookupDev(StaticField.ERRBUF) : s;
        StaticField.SNAPLEN = snaplen;
        StaticField.PROMISC = promisc;
        StaticField.TIMEOUT = to_ms;

        if (PcapLookupNet(StaticField.SOURCE, StaticField.CURRENT_NETWORK_ADDRESS,
                StaticField.CURRENT_NETMASK_ADDRESS, StaticField.ERRBUF) < 0) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: " + StaticField.ERRBUF.toString());
            }
        }

        StaticField.PCAP = PcapOpenLive(StaticField.SOURCE,
                StaticField.SNAPLEN,
                StaticField.PROMISC,
                StaticField.TIMEOUT, StaticField.ERRBUF);
        StaticField.Pcap_IDS = PcapOpenLive(StaticField.SOURCE,
                StaticField.SNAPLEN,
                StaticField.PROMISC,
                StaticField.TIMEOUT, StaticField.ERRBUF);
        StaticField.PCAP_ICMP_TRAP = PcapOpenLive(StaticField.SOURCE,
                StaticField.SNAPLEN,
                StaticField.PROMISC,
                StaticField.TIMEOUT, StaticField.ERRBUF);

        if (StaticField.PCAP == null || StaticField.Pcap_IDS == null || StaticField.PCAP_ICMP_TRAP == null) {
            if (StaticField.LOGGER != null)
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: " + StaticField.ERRBUF.toString());
        }

        if ((short) PcapDataLink(StaticField.PCAP) != DataLinkType.EN10MB.getValue()) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: " + StaticField.SOURCE + " is not Ethernet link type.");
            }
            PcapClose(StaticField.PCAP);
            PcapClose(StaticField.Pcap_IDS);
            PcapClose(StaticField.PCAP_ICMP_TRAP);
        } else {
            StaticField.DATALINK_TYPE = DataLinkType.EN10MB;
        }

        StaticField.CURRENT_INET4ADDRESS = Utils.getCurrentInet4Address(StaticField.SOURCE);
        if (StaticField.CURRENT_INET4ADDRESS == null) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current IP Address.");
            }
        }

        StaticField.CURRENT_MAC_ADDRESS = AddrUtils.getHardwareAddress(StaticField.SOURCE);
        if (StaticField.CURRENT_MAC_ADDRESS == null) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current Mac Address.");
            }
        }

        StaticField.GATEWAY_INET4ADDRESS = AddrUtils.getGatewayAddress(StaticField.SOURCE);
        if (StaticField.GATEWAY_INET4ADDRESS == null) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current Gateway IP Address.");
            }
        }

        if (StaticField.CURRENT_INET4ADDRESS.equals(Inet4Address.valueOf("127.0.0.1")) ||
                StaticField.CURRENT_INET4ADDRESS.equals(Inet4Address.valueOf("255.0.0.0"))) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: " + StaticField.SOURCE + " is loopback interface.");
            }
        }

        StaticField.GATEWAY_MAC_ADDRESS = Utils.getGwAddrFromArp();
        if (StaticField.GATEWAY_MAC_ADDRESS == null) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current Gateway Mac Address.");
            }
        }

        System.out.println("Gateway hw addr: " + StaticField.GATEWAY_MAC_ADDRESS);

        //StaticField.LOGGER.log(LoggerStatus.COMMON, "[ INFO ] :: Choosing inferface successed.");
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
        Static.loop(pcap, -1, handler, null);
        PcapClose(pcap);
    }


    public static byte BYTE(int b) {
        return (byte)(b & 0xff);
    }

    public static short SHORT(int s) {
        return (short)(s & 0xffff);
    }


}
