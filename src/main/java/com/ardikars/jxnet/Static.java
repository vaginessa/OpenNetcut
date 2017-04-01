package com.ardikars.jxnet;

import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.ethernet.Ethernet;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.ardikars.jxnet.Jxnet.*;

public class Static {

    public static synchronized <T> int loop(Pcap pcap, int count, PacketHandler<T> handler, T arg) {
        DataLinkType datalinkType = DataLinkType.valueOf((short)PcapDataLink(pcap));
        PcapHandler<PacketHandler<T>> callback = (tPacketHandler, pcapPktHdr, byteBuffer) -> {
            if (pcapPktHdr == null || byteBuffer == null) return;
            tPacketHandler.nextPacket(arg, pcapPktHdr, getPacket(datalinkType, byteBuffer));
        };
        return PcapLoop(pcap, count, callback, handler);
    }

    public static Map<Class, Packet> next(Pcap pcap, PcapPktHdr pcapPktHdr) {
        DataLinkType datalinkType = DataLinkType.valueOf((short)PcapDataLink(pcap));
        ByteBuffer byteBuffer = PcapNext(pcap, pcapPktHdr);
        if (byteBuffer == null) return null;
        return getPacket(datalinkType, byteBuffer);
    }

    public static int nextEx(Pcap pcap, PcapPktHdr pktHdr, Map<Class, Packet> packets) {
        packets.clear();
        DataLinkType datalinkType = DataLinkType.valueOf((short)PcapDataLink(pcap));
        ByteBuffer buffer = ByteBuffer.allocateDirect(3600);
        int ret = PcapNextEx(pcap, pktHdr, buffer);
        if (ret != 0) {
            //return -1;
        }
        if (pktHdr == null || buffer == null) return -1;
        Map pkts = getPacket(datalinkType, buffer);
        packets.putAll(pkts);
        return 0;
    }

    private static Map<Class, Packet> getPacket(DataLinkType datalinkType, ByteBuffer byteBuffer) {
        byte[] bytes = FormatUtils.toBytes(byteBuffer);
        //List<Packet> packets = new ArrayList<Packet>();
        Map<Class, Packet> pkts = new HashMap<Class, Packet>();
        Packet packet = null;
        switch (datalinkType) {
            case EN10MB:
                packet = Ethernet.newInstance(bytes);
                //packets.add(packet);
                pkts.put(packet.getClass(), packet);
                break;
            case IEEE802:
                break;
        }
        while ((packet = packet.getPacket()) != null) {
            //packets.add(packet);
            pkts.put(packet.getClass(), packet);
        }
        return pkts;
    }

    public static void printInfo(ByteBuffer buf) {
        System.out.println("CAP = " + buf.capacity());
        System.out.println("LIM = " + buf.limit());
        System.out.println("POS = " + buf.position());
        System.out.println("REM = " + buf.remaining());
    }

}
