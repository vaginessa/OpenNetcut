package com.ardikars.jxnet;

import com.ardikars.jxnet.DataLinkType;
import com.ardikars.jxnet.Pcap;
import com.ardikars.jxnet.PcapHandler;
import com.ardikars.jxnet.PcapPktHdr;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.protocol.datalink.ethernet.Ethernet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.ardikars.jxnet.Jxnet.*;

public class Static {

    public static <T> int loop(Pcap pcap, int count, PacketHandler<T> handler, T arg) {
        DataLinkType datalinkType = DataLinkType.valueOf((short)PcapDataLink(pcap));
        PcapHandler<PacketHandler<T>> callback = (tPacketHandler, pcapPktHdr, byteBuffer) -> {
            if (pcapPktHdr == null || byteBuffer == null) return;
            tPacketHandler.nextPacket(arg, pcapPktHdr, getPacket(datalinkType, byteBuffer));
        };
        return PcapLoop(pcap, count, callback, handler);
    }

    public static List<Packet> next(Pcap pcap, PcapPktHdr pcapPktHdr) {
        DataLinkType datalinkType = DataLinkType.valueOf((short)PcapDataLink(pcap));
        ByteBuffer byteBuffer = PcapNext(pcap, pcapPktHdr);
        if (pcapPktHdr == null || byteBuffer == null) return null;
        List<Packet> packets = getPacket(datalinkType, byteBuffer);
        return packets;
    }

    public static int nextEx(Pcap pcap, List<Packet> packets, PcapPktHdr pktHdr) {
        packets.clear();
        DataLinkType datalinkType = DataLinkType.valueOf((short)PcapDataLink(pcap));
        ByteBuffer buffer = ByteBuffer.allocateDirect(3600);
        if ((PcapNextEx(pcap, pktHdr, buffer)) != 0) {
            //return -1;
        }
        if (pktHdr == null || buffer == null) return -1;
        List<Packet> packetList = getPacket(datalinkType, buffer);
        for (Packet p : packetList) System.out.print(p.getClass().getName() +":");
        //packets.addAll(getPacket(datalinkType, buffer));
        return 0;
    }

    private static List<Packet> getPacket(DataLinkType datalinkType, ByteBuffer byteBuffer) {
        byte[] bytes = FormatUtils.toBytes(byteBuffer);
        List<Packet> packets = new ArrayList<Packet>();
        Packet packet = null;
        switch (datalinkType) {
            case EN10MB:
                packet = Ethernet.newInstance(bytes);
                packets.add(packet);
                break;
            case IEEE802:
                break;
        }
        while ((packet = packet.getPacket()) != null) {
            packets.add(packet);
        }
        return packets;
    }

    public static void printInfo(ByteBuffer buf) {
        System.out.println("CAP = " + buf.capacity());
        System.out.println("LIM = " + buf.limit());
        System.out.println("POS = " + buf.position());
        System.out.println("REM = " + buf.remaining());
    }

}
