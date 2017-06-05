package com.ardikars.opennetcut.util;

import static com.ardikars.jxnet.Jxnet.*;

import com.ardikars.jxnet.*;
import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.packet.PacketHelper;
import com.ardikars.jxnet.packet.arp.ARP;
import com.ardikars.jxnet.packet.arp.ARPOperationCode;
import com.ardikars.jxnet.packet.ethernet.Ethernet;
import com.ardikars.jxnet.util.AddrUtils;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.jxnet.util.Platforms;
import com.ardikars.jxnet.util.Preconditions;
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
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

import static com.ardikars.opennetcut.util.Language.*;

public class Utils {

    public static MacAddress getGwHwAddrFromArp() {

        Ethernet ethernet = (Ethernet) PacketBuilder.arpBuilder(MacAddress.BROADCAST, ARPOperationCode.ARP_REQUEST,
                StaticField.MAC_ADDRESS, StaticField.ADDRESS,
                MacAddress.ZERO, StaticField.GATEWAY_ADDRESS);

        ByteBuffer buffer = FormatUtils.toDirectBuffer(ethernet.toBytes());
        PcapPktHdr pktHdr = new PcapPktHdr();
        byte[] bytes;
        for (int i=0; i<100; i++) {
            if (PcapSendPacket(StaticField.PCAP, buffer, buffer.capacity()) != 0) {
                JOptionPane.showMessageDialog(null, FAILED_TO_SEND_PACKET);
                return null;
            }
            Map<Class, Packet> packets = PacketHelper.next(StaticField.PCAP, pktHdr);
            if (packets == null) continue;
            ARP arp = (ARP) packets.get(ARP.class);
            if (arp == null) continue;
            if (arp.getOperationCode() == ARPOperationCode.ARP_REPLY &&
                    arp.getSenderProtocolAddress().equals(StaticField.GATEWAY_ADDRESS)) {
                return arp.getSenderHardwareAddress();
            }
            try{Thread.sleep(StaticField.TIMEOUT);}catch(InterruptedException e){System.out.println(e);}
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

    public static Pcap openLive(String filter) {

        Pcap pcap = Jxnet.PcapCreate(StaticField.SOURCE, StaticField.ERRBUF);
        if (pcap == null) {
            return null;
        }

        if (Jxnet.PcapSetSnaplen(pcap, StaticField.SNAPLEN) != 0) {
            String err = Jxnet.PcapGetErr(pcap);
            Jxnet.PcapClose(pcap);
            StaticField.ERRBUF.append(err);
            return null;
        }

        if (Jxnet.PcapSetPromisc(pcap, StaticField.PROMISC) != 0 ) {
            String err = Jxnet.PcapGetErr(pcap);
            Jxnet.PcapClose(pcap);
            StaticField.ERRBUF.append(err);
            return null;
        }

        if (!Platforms.isWindows()) {
            if (Jxnet.PcapSetImmediateMode(pcap, StaticField.IMMEDIATE) != 0) {
                String err = Jxnet.PcapGetErr(pcap);
                Jxnet.PcapClose(pcap);
                StaticField.ERRBUF.append(err);
                return null;
            }
        }

        if (Jxnet.PcapSetTimeout(pcap, StaticField.TIMEOUT) != 0) {
            String err = Jxnet.PcapGetErr(pcap);
            Jxnet.PcapClose(pcap);
            StaticField.ERRBUF.append(err);
            return null;
        }

        if (Jxnet.PcapActivate(pcap) != 0) {
            String err = Jxnet.PcapGetErr(pcap);
            Jxnet.PcapClose(pcap);
            StaticField.ERRBUF.append(err);
            return null;
        }

        if (!Platforms.isWindows()) {
            if (Jxnet.PcapSetDirection(pcap, PcapDirection.PCAP_D_IN) != 0) {
                String err = Jxnet.PcapGetErr(pcap);
                Jxnet.PcapClose(pcap);
                StaticField.ERRBUF.append(err);
                return null;
            }
        }

        if (Jxnet.PcapCompile(pcap, StaticField.BPF_PROGRAM, filter, StaticField.OPTIMIZE,
                StaticField.NETMASK_ADDRESS.toInt()) != 0 ) {
            String err = Jxnet.PcapGetErr(pcap);
            Jxnet.PcapClose(pcap);
            StaticField.ERRBUF.append(err);
            return null;
        }

        if (Jxnet.PcapSetFilter(pcap, StaticField.BPF_PROGRAM) != 0) {
            String err = Jxnet.PcapGetErr(pcap);
            Jxnet.PcapClose(pcap);
            StaticField.ERRBUF.append(err);
            return null;
        }

        return pcap;
    }


    public static void initialize(String s, int snaplen, int promisc, int to_ms) throws JxnetException {

        if (StaticField.SOURCE == null) {
            StaticField.SOURCE = LookupNetworkInterface(
                    StaticField.ADDRESS,
                    StaticField.NETMASK_ADDRESS,
                    StaticField.NETWORK_ADDRESS,
                    StaticField.BROADCAST_ADDRESS,
                    StaticField.DESTINATION_ADDRESS,
                    StaticField.MAC_ADDRESS,
                    StaticField.ERRBUF
            );
        } else {
            StaticField.SOURCE = LookupNetworkInterface(
                    s,
                    StaticField.ADDRESS,
                    StaticField.NETMASK_ADDRESS,
                    StaticField.NETWORK_ADDRESS,
                    StaticField.BROADCAST_ADDRESS,
                    StaticField.DESTINATION_ADDRESS,
                    StaticField.MAC_ADDRESS,
                    StaticField.ERRBUF
            );
        }
        StaticField.SNAPLEN = snaplen;
        StaticField.PROMISC = promisc;
        StaticField.TIMEOUT = to_ms;

        if (StaticField.SOURCE == null) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + CHECK_YOUR_NETWORK_CONNECTION);
            }
        }

        if ((StaticField.PCAP = openLive("arp")) == null) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + CHECK_YOUR_NETWORK_CONNECTION);
            }
        }

        if ((short) PcapDataLink(StaticField.PCAP) != DataLinkType.EN10MB.getValue()) {
            if (JOptionPane.showConfirmDialog(null, StaticField.SOURCE + ": " + NOT_ETHERNET_TYPE, "[ " + WARNING + " ]", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                  Jxnet.PcapClose(StaticField.PCAP);
                  //System.exit(1);
            }
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + StaticField.SOURCE + " is not Ethernet link type.");
            }
        } else {
            StaticField.DATALINK_TYPE = DataLinkType.EN10MB;
        }


        try {
            StaticField.GATEWAY_ADDRESS = AddrUtils.GetGatewayAddress();
        } catch (IOException e) {
		    if (JOptionPane.showConfirmDialog(null, NOT_CONNECTED, "[ " + WARNING + " ]", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                  Jxnet.PcapClose(StaticField.PCAP);
                  //System.exit(1);
            }
            e.printStackTrace();
        }
        if (StaticField.GATEWAY_ADDRESS == null) {
            if (StaticField.LOGGER != null) {
		        if (JOptionPane.showConfirmDialog(null, NOT_CONNECTED, "[ " + WARNING + " ]", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    Jxnet.PcapClose(StaticField.PCAP);
                    //System.exit(1);
                }
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + FAILED_TO_GET_INET4_ADDRESS);
            }
        }

	    System.out.println("Interface           : " + StaticField.SOURCE);
        System.out.println("Address             : " + StaticField.ADDRESS + "" +
                " (" + StaticField.MAC_ADDRESS + ")");
        System.out.println("Netmask Address     : " + StaticField.NETMASK_ADDRESS);
        System.out.println("Network Address     : " + StaticField.NETWORK_ADDRESS);
	    System.out.print("Gateway             : " + StaticField.GATEWAY_ADDRESS);
                
        StaticField.GATEWAY_MAC_ADDRESS = getGwHwAddrFromArp();
        if (StaticField.GATEWAY_MAC_ADDRESS == null) {
            if (StaticField.LOGGER != null) {
		        if (JOptionPane.showConfirmDialog(null, FAILED_TO_GET_GATEWAY_MAC_ADDRESS, "[ " + WARNING + " ]", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    Jxnet.PcapClose(StaticField.PCAP);
                    //System.exit(1);
                }
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + FAILED_TO_GET_GATEWAY_MAC_ADDRESS);
            }
        } else {
            System.out.println(" (" + StaticField.GATEWAY_MAC_ADDRESS + ")");
            StaticField.LOGGER.log(LoggerStatus.COMMON, "[ "+ INFORMATION +" ] :: " + SUCCESS);
        }
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

    @SuppressWarnings("unchecked")
    public static void openPcapFile(PacketHandler handler, LoggerHandler logHandler, String path) {
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = PcapOpenOffline(path, errbuf);
        if (pcap == null) {
            logHandler.log(LoggerStatus.COMMON, "[ " + WARNING + " ] :: " + errbuf.toString());
            return;
        }
        PacketHelper.loop(pcap, -1, handler, null);
        PcapClose(pcap);
    }

    /**
     * Get network interface information.
     * @param address ipv4 address.
     * @param netmask netmask address.
     * @param netaddr network address.
     * @param broadaddr broadcast address.
     * @param dstaddr destination address.
     * @param macAddress mac address.
     * @param errbuf error buffer.
     * @return interface name.
     */
    public static String LookupNetworkInterface(Inet4Address address,
                                                Inet4Address netmask,
                                                Inet4Address netaddr,
                                                Inet4Address broadaddr,
                                                Inet4Address dstaddr,
                                                MacAddress macAddress,
                                                StringBuilder errbuf) {

        Preconditions.CheckNotNull(address);
        Preconditions.CheckNotNull(netmask);
        Preconditions.CheckNotNull(netaddr);
        Preconditions.CheckNotNull(broadaddr);
        Preconditions.CheckNotNull(dstaddr);
        Preconditions.CheckNotNull(errbuf);

        List<PcapIf> ifs = new ArrayList<PcapIf>();
        if (Jxnet.PcapFindAllDevs(ifs, errbuf) != Jxnet.OK) {
            return null;
        }

        errbuf.setLength(0);

        for (PcapIf dev : ifs) {
            for (PcapAddr addr : dev.getAddresses()) {
                if (addr.getAddr().getData() == null || addr.getBroadAddr().getData() == null ||
                        addr.getNetmask().getData() == null) {
                    continue;
                }
                if (addr.getAddr().getSaFamily() == SockAddr.Family.AF_INET &&
                        !Inet4Address.valueOf(addr.getAddr().getData()).equals(Inet4Address.ZERO) &&
                        !Inet4Address.valueOf(addr.getAddr().getData()).equals(Inet4Address.LOCALHOST) &&
                        !Inet4Address.valueOf(addr.getBroadAddr().getData()).equals(Inet4Address.ZERO) &&
                        !Inet4Address.valueOf(addr.getNetmask().getData()).equals(Inet4Address.ZERO)
                        ) {
                    address.update(Inet4Address.valueOf(addr.getAddr().getData()));
                    netmask.update(Inet4Address.valueOf(addr.getNetmask().getData()));
                    netaddr.update(Inet4Address.valueOf(address.toInt() & netmask.toInt()));
                    broadaddr.update(Inet4Address.valueOf(addr.getBroadAddr().getData()));
                    if (addr.getDstAddr().getData() != null) {
                        dstaddr.update(Inet4Address.valueOf(addr.getDstAddr().getData()));
                    } else {
                        dstaddr.update(Inet4Address.ZERO);
                    }
                    macAddress.update(MacAddress.fromNicName(dev.getName()));
                    return dev.getName();
                }
            }
        }
        return null;
    }

    /**
     * Get network interface information.
     * @param source interface name.
     * @param address ipv4 address.
     * @param netmask netmask address.
     * @param netaddr network address.
     * @param broadaddr broadcast address.
     * @param dstaddr destination address.
     * @param macAddress mac address.
     * @param errbuf error buffer.
     * @return interface name.
     */
    public static String LookupNetworkInterface(String source, Inet4Address address,
                                                Inet4Address netmask,
                                                Inet4Address netaddr,
                                                Inet4Address broadaddr,
                                                Inet4Address dstaddr,
                                                MacAddress macAddress,
                                                StringBuilder errbuf) {

        Preconditions.CheckNotNull(source);
        Preconditions.CheckNotNull(address);
        Preconditions.CheckNotNull(netmask);
        Preconditions.CheckNotNull(netaddr);
        Preconditions.CheckNotNull(broadaddr);
        Preconditions.CheckNotNull(dstaddr);
        Preconditions.CheckNotNull(errbuf);

        List<PcapIf> ifs = new ArrayList<PcapIf>();
        if (Jxnet.PcapFindAllDevs(ifs, errbuf) != Jxnet.OK) {
            return null;
        }

        for (PcapIf dev : ifs) {
            if (dev.getName().equals(source)) {
                for (PcapAddr addr : dev.getAddresses()) {
                    if (addr.getAddr().getData() == null || addr.getBroadAddr().getData() == null ||
                            addr.getNetmask().getData() == null) {
                        continue;
                    }
                    if (addr.getAddr().getSaFamily() == SockAddr.Family.AF_INET &&
                            !Inet4Address.valueOf(addr.getAddr().getData()).equals(Inet4Address.ZERO) &&
                            !Inet4Address.valueOf(addr.getAddr().getData()).equals(Inet4Address.LOCALHOST) &&
                            !Inet4Address.valueOf(addr.getBroadAddr().getData()).equals(Inet4Address.ZERO) &&
                            !Inet4Address.valueOf(addr.getNetmask().getData()).equals(Inet4Address.ZERO)
                            ) {
                        address.update(Inet4Address.valueOf(addr.getAddr().getData()));
                        netmask.update(Inet4Address.valueOf(addr.getNetmask().getData()));
                        netaddr.update(Inet4Address.valueOf(address.toInt() & netmask.toInt()));
                        broadaddr.update(Inet4Address.valueOf(addr.getBroadAddr().getData()));
                        if (addr.getDstAddr().getData() != null) {
                            dstaddr.update(Inet4Address.valueOf(addr.getDstAddr().getData()));
                        } else {
                            dstaddr.update(Inet4Address.ZERO);
                        }
                        macAddress.update(MacAddress.fromNicName(dev.getName()));
                        return dev.getName();
                    }
                }
            }
        }
        return null;
    }


}

