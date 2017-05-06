package com.ardikars.opennetcut.util;

import static com.ardikars.jxnet.Jxnet.*;

import com.ardikars.jxnet.*;
import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.jxnet.exception.PcapCloseException;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.packet.PacketHelper;
import com.ardikars.jxnet.packet.arp.ARP;
import com.ardikars.jxnet.packet.arp.ARPOperationCode;
import com.ardikars.jxnet.packet.ethernet.Ethernet;
import com.ardikars.jxnet.util.AddrUtils;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.jxnet.util.Platforms;
import com.ardikars.opennetcut.app.LoggerHandler;
import com.ardikars.opennetcut.app.LoggerStatus;
import com.ardikars.opennetcut.app.PacketBuilder;
import com.ardikars.opennetcut.app.StaticField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

public class Utils {

    public static void getAddresses() {
        List<PcapIf> pcapIf = new ArrayList<PcapIf>();
        if (PcapFindAllDevs(pcapIf, StaticField.ERRBUF) != 0) {
            throw new JxnetException("Failed to get Ip Address from " + StaticField.SOURCE);
        }
        for (PcapIf If : pcapIf) {
            if (If.getName().equals(StaticField.SOURCE)) {
                for (PcapAddr addrs : If.getAddresses()) {
                    if (addrs.getAddr().getSaFamily() == SockAddr.Family.AF_INET) {
                        StaticField.CURRENT_INET4ADDRESS = Inet4Address.valueOf(addrs.getAddr().getData());
                        StaticField.CURRENT_NETMASK_ADDRESS = Inet4Address.valueOf(addrs.getNetmask().getData());
                        StaticField.CURRENT_NETWORK_ADDRESS = Inet4Address.valueOf(
                                StaticField.CURRENT_INET4ADDRESS.toInt() & StaticField.CURRENT_NETMASK_ADDRESS.toInt()
                        );
                        StaticField.CURRENT_MAC_ADDRESS = MacAddress.fromNicName(StaticField.SOURCE);
                        if (StaticField.CURRENT_MAC_ADDRESS == null) {
                            if (StaticField.LOGGER != null) {
                                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current Mac Address.");
                            }
                        }
                        break;
                    }
                }
            }
        }

    }

    public static MacAddress getGwAddrFromArp() {

        Ethernet ethernet = (Ethernet) PacketBuilder.arpBuilder(MacAddress.BROADCAST, ARPOperationCode.ARP_REQUEST,
                StaticField.CURRENT_MAC_ADDRESS, StaticField.CURRENT_INET4ADDRESS,
                MacAddress.ZERO, StaticField.GATEWAY_INET4ADDRESS);

        ByteBuffer buffer = FormatUtils.toDirectBuffer(ethernet.toBytes());
        PcapPktHdr pktHdr = new PcapPktHdr();
        byte[] bytes;
        for (int i=0; i<100; i++) {
            if (PcapSendPacket(StaticField.PCAP, buffer, buffer.capacity()) != 0) {
                JOptionPane.showConfirmDialog(null, "Failed to send arp packet.");
                return null;
            }
            Map<Class, Packet> packets = PacketHelper.next(StaticField.PCAP, pktHdr);
            if (packets == null) continue;
            ARP arp = (ARP) packets.get(ARP.class);
            if (arp == null) continue;
            if (arp.getOperationCode() == ARPOperationCode.ARP_REPLY)
                System.out.println(arp.getSenderProtocolAddress() + " == " + StaticField.GATEWAY_INET4ADDRESS);
            if (arp.getOperationCode() == ARPOperationCode.ARP_REPLY &&
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

    private static void activate(int snaplen, int promisc, int to_ms) {

        if (Platforms.isWindows()) {
            StaticField.PCAP = PcapOpenLive(StaticField.SOURCE,
                    StaticField.SNAPLEN,
                    StaticField.PROMISC,
                    StaticField.TIMEOUT, StaticField.ERRBUF);

        } else {
            StaticField.PCAP = Jxnet.PcapCreate(StaticField.SOURCE, StaticField.ERRBUF);
            Jxnet.PcapSetSnaplen(StaticField.PCAP, snaplen);
            Jxnet.PcapSetPromisc(StaticField.PCAP, promisc);
            Jxnet.PcapSetImmediateMode(StaticField.PCAP, 1);
            Jxnet.PcapSetTimeout(StaticField.PCAP, to_ms);
            Jxnet.PcapActivate(StaticField.PCAP);
        }
    }

    public static void initialize(String s, int snaplen, int promisc, int to_ms) throws JxnetException {

        StaticField.SOURCE = (s == null) ? AddrUtils.LookupDev(StaticField.ERRBUF) : s;
        StaticField.SNAPLEN = snaplen;
        StaticField.PROMISC = promisc;
        StaticField.TIMEOUT = to_ms;

        getAddresses();

        activate(snaplen, promisc, to_ms);

        if ((short) PcapDataLink(StaticField.PCAP) != DataLinkType.EN10MB.getValue()) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: " + StaticField.SOURCE + " is not Ethernet link type.");
            }
            PcapClose(StaticField.PCAP);
        } else {
            StaticField.DATALINK_TYPE = DataLinkType.EN10MB;
        }


        StaticField.GATEWAY_INET4ADDRESS = AddrUtils.getGatewayAddress(StaticField.SOURCE);
        if (StaticField.GATEWAY_INET4ADDRESS == null) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current Gateway IP Address.");
            }
        }

        StaticField.GATEWAY_MAC_ADDRESS = getGwAddrFromArp();
        if (StaticField.GATEWAY_MAC_ADDRESS == null) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed get current Gateway Mac Address.");
            }
        }

        //StaticField.LOGGER.log(LoggerStatus.COMMON, "[ INFO ] :: Choosing inferface successed.");
    }

    public static String getPcapTmpFileName() {
        String fileName = MessageFormat.format("{0}.{1}", UUID.randomUUID(), new String("pcap").trim());
        return Paths.get(System.getProperty("java.io.tmpdir"), fileName).toString();
    }

    public static void copyFileUsingFileChannels(java.io.File source, java.io.File dest)
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
        PacketHelper.loop(pcap, -1, handler, null);
        PcapClose(pcap);
    }

}
