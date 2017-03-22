package com.ardikars.util;

import com.ardikars.ann.Connection;
import com.ardikars.ann.Layer;
import com.ardikars.ann.Neuron;
import com.ardikars.jxnet.*;
import com.ardikars.jxnet.File;
import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.jxnet.exception.PcapCloseException;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.packet.protocol.datalink.ethernet.Ethernet;
import com.ardikars.jxnet.packet.protocol.lan.arp.ARP;
import com.ardikars.jxnet.packet.protocol.lan.arp.OperationCode;
import com.ardikars.jxnet.util.AddrUtils;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.opennetcut.app.LoggerHandler;
import com.ardikars.opennetcut.app.LoggerStatus;
import com.ardikars.opennetcut.app.PacketBuilder;
import com.ardikars.opennetcut.app.StaticField;
import com.google.gson.stream.JsonWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;

import static com.ardikars.jxnet.Jxnet.*;
import static com.ardikars.jxnet.Jxnet.PcapClose;
import static com.ardikars.jxnet.Jxnet.PcapOpenOffline;

public class Utils {
    
    private static final Random random = new Random();
    
    public static double random() {
        double min = -1;
        double max = 1;
        return min + (max - min) * random.nextDouble();
    }

    public static double[] array(double... value) {
        return value;
    }

    public static double[][] generateXorInputs() {
        return new double[][] {
                array(1.0, 1.0),
                array(1.0, 0.0),
                array(0.0, 1.0),
                array(0.0, 0.0),
        };
    }
    
    public static double[][] generateXorOutputs() {
        return new double[][] { array(0.0), array(1.0), array(1.0), array(0.0) };
    }
    
    public static double[][] generateXorDumpOutputs() {
        return new double[][] { array(0.0, 0.0), array(1.0, 1.0), array(1.0, 1.0), array(0.0, 0.0) };
    }
    
    public static double[][] generateInputs() {
        return new double[][] {
                array(1,	1,	1,	1,	1,	1),
                array(1,	1,	1,	1,	1,	0),
                array(1,	1,	1,	1,	0,	0),
                array(1,	1,	1,	0,	0,	0),
                array(1,	1,	0,	0,	0,	0),
                array(1,	0,	0,	0,	0,	0),
                array(0,	0,	0,	0,	0,	0),
                array(0,	0,	0,	0,	0,	1),
                array(0,	0,	0,	0,	1,	1),
                array(0,	0,	0,	1,	1,	1),
                array(0,	0,	1,	1,	1,	1),
                array(0,	1,	1,	1,	1,	1),
                array(0,	0,	0,	1,	0,	0),
                array(0,	0,	0,	0,	1,	0),
                array(0,	0,	0,	1,	1,	0)
        };
    }

    public static double[][] generateOutputs() {
        return new double[][] {
                array(1),
                array(1),
                array(1),
                array(1),
                array(1),
                array(1),
                array(0),
                array(1),
                array(1),
                array(1),
                array(1),
                array(1),
                array(0),
                array(0),
                array(0)
        };
    }
    
    public static double[][] generateDummyOutputs(int a, int b) {
        double[][] value = new double[a][b];
        Arrays.fill(value, array(-1));
        return value;
    }

    public static void printResult(double[][] inputs,
                                   double[][] outputs,
                                   double[][] resultOutputs,
                                   int inputLayerSize,
                                   int outputLayerSize) {
        DecimalFormat df = new DecimalFormat();
        for (int p = 0; p < inputs.length; p++) {
            System.out.print("INPUTS: ");
            for (int x = 0; x < inputLayerSize; x++) {
                System.out.print(inputs[p][x] + " ");
            }

            System.out.print("EXPECTED: ");
            for (int x = 0; x < outputLayerSize; x++) {
                System.out.print(outputs[p][x] + " ");
            }

            System.out.print("ACTUAL: ");
            for (int x = 0; x < outputLayerSize; x++) {
                System.out.print(resultOutputs[p][x] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public static void printWeights(Layer hiddenLayer, Layer outputLayer) {
        for (Neuron n : hiddenLayer) {
            List<Connection> connections = n.getConnections();
            for (Connection con : connections) {
                System.out.println("[ "+n.getName()+","+con.getLeftNeuron().getName()+" ] = " + con.getWeight());
            }
        }
        // weights for the output layer
        for (Neuron n : outputLayer) {
            List<Connection> connections = n.getConnections();
            for (Connection con : connections) {
                System.out.println("[ "+n.getName()+","+con.getLeftNeuron().getName()+" ] = " + con.getWeight());
            }
        }
        System.out.println();
    }
    
    public static void writeWeigth(String path, Layer hiddenLayer, Layer outputLayer) {
        JsonWriter writer = null;
        try {
            writer = new JsonWriter(new FileWriter(path));
                    writer.beginObject();
            for (Neuron n : hiddenLayer) {
                List<Connection> cons = n.getConnections();
                for (Connection con : cons) {
                    writer.name("Neuron ID").value(n.getID());
                    writer.name("Connection ID").value(con.getID());
                    writer.name("Weight").value(con.getWeight());
                }
            }
                    writer.endObject();
            writer.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    // --------------------------   FOR IDS ---------------------------------- //

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

        StaticField.CURRENT_INET4ADDRESS = getCurrentInet4Address(StaticField.SOURCE);
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

        StaticField.GATEWAY_MAC_ADDRESS = getGwAddrFromArp();
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
