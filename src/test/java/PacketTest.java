import com.ardikars.jxnet.*;
import com.ardikars.jxnet.util.AddrUtils;
import com.ardikars.jxnet.Static;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.protocol.network.icmp.ICMP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketTest {

    public static void main(String[] args) {
        loopTest();
    }

    /*public static void caputer() {
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Jxnet.PcapOpenLive("eth0", 65535, 1, 3600, errbuf);*/
        /*BpfProgram fp = new BpfProgram();
        if(Jxnet.PcapCompile(pcap, fp, "icmp", 1, 0xfffffff) != 0) {
            System.err.println("Failed to compile bpf");
            Jxnet.PcapClose(pcap);
            return;
        }
        if(Jxnet.PcapSetFilter(pcap, fp) != 0) {
            System.err.println("Failed to set filter");
            Jxnet.PcapClose(pcap);
            return;
        }*/
/*        PcapStat stat = new PcapStat();
        PcapPktHdr hdr = new PcapPktHdr();

        PcapHandler<String> callback = (String s, PcapPktHdr pcapPktHdr, ByteBuffer byteBuffer) -> {
            if (byteBuffer == null) return;
            byte[] data = new byte[byteBuffer.capacity()];
            byteBuffer.get(data);
            Ethernet ethernet = Ethernet.wrap(data);
            if (ethernet != null && ethernet.getPacket() instanceof IPv4) {
                IPv4 iPv4 = (IPv4) ethernet.getPacket();
                if (iPv4 != null && iPv4.getPacket() instanceof ICMP) {
                    ICMP icmp = (ICMP) iPv4.getPacket();
                    System.out.println(ethernet);
                    System.out.println(iPv4);
                    System.out.println(icmp);
                } else if (iPv4 != null && iPv4.getPacket() instanceof TCP) {
                    TCP tcp = (TCP) iPv4.getPacket();
                    System.out.println(ethernet);
                    System.out.println(iPv4);
                    System.out.println(tcp);
                }
            }
        };

        PcapLoop(pcap, 100, callback, null);

        PcapStats(pcap, stat);
        System.out.println(stat.getPsRecv());
        System.out.println(stat.getPsDrop());
        System.out.println(stat.getPsIfdrop());
        System.out.println("==");
        System.out.println("FINISHED");
        Jxnet.PcapClose(pcap);
    }
*/

/*
    public static void sendICMP() {
        Ethernet ethernet = new Ethernet()
                .setDestinationMacAddress(MacAddress.valueOf("14:cc:20:cc:b9:ec"))
                .setSourceMacAddress(MacAddress.valueOf("88:27:eb:9a:9c:5f"))
                .setEtherType(Ethernet.EtherType.IPv4);
        IPv4 ipv4 = new IPv4()
                .setHeaderLength(BYTE(5))
                .setDiffServ(BYTE(0))
                .setExpCon(BYTE(0))
                .setTotalLength(SHORT(52))
                .setIdentification(SHORT(36375))
                .setFlags(BYTE(2))
                .setFragmentOffset(SHORT(0))
                .setTtl(BYTE(64))
                .setProtocol(IP.Protocol.ICMP)
                .setSourceAddress(Inet4Address.valueOf("192.168.1.150"))
                .setDestinationAddress(Inet4Address.valueOf("192.168.1.254"));
        ICMP icmp = new ICMP()
                .setType(BYTE(9))
                .setCode(BYTE(0))
                ;

        ipv4.putPayload(icmp.toBytes());
        ethernet.putPayload(ipv4.toBytes());
        byte[] buffer = ethernet.toBytes();
        ByteBuffer buf = ByteBuffer.allocateDirect(buffer.length);
        buf.put(buffer);
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Jxnet.PcapOpenLive("eth0", 65535, 1, 3600, errbuf);
        Jxnet.PcapSendPacket(pcap, buf, buf.capacity());
        Jxnet.PcapClose(pcap);
    }*/

    private void lookupNet(String source) {
        StringBuilder errbuf = new StringBuilder();
        System.out.println(Jxnet.PcapLookupDev(errbuf));
    }


    public static void loopTest() {
        StringBuilder errbuf = new StringBuilder();
        String source = AddrUtils.LookupDev(errbuf);
        BpfProgram bpf = new BpfProgram();
        Pcap pcap = Jxnet.PcapOpenLive(source, 65535, 1, 2000, errbuf);
        //Jxnet.PcapCompile(pcap, bpf, "icmp", 1, Inet4Address.valueOf("255.255.255.0").toInt());
        //Jxnet.PcapSetFilter(pcap, bpf);
        /*PacketHandlerBeta<String> handler = (arg, pktHdr, packets) -> {
            ICMP icmp = (ICMP) Packet.parsePacket(packets, ICMP.class);
            System.out.println(icmp);
        };
        Static.loop(pcap, 50, handler, null);*/
        int i = 0;
        PcapPktHdr hdr = new PcapPktHdr();
        Map<Class, Packet> packets = new HashMap<Class, Packet>();
        while (i < 10) {
            if (Static.nextEx(pcap, hdr, packets) == 0) {
               //for (Packet p : packets) System.out.print(p.getClass().getName() +":");
                // ICMP icmp = (ICMP) Packet.parsePacket(packets, ICMP.class);
                //System.out.println(icmp);
                System.out.println(i+":");
            }
            i++;
        }
        Jxnet.PcapClose(pcap);
    }

}
