import com.ardikars.jxnet.*;
import com.ardikars.opennetcut.packet.protocol.datalink.Ethernet;
import com.ardikars.opennetcut.packet.protocol.network.ICMP;
import com.ardikars.opennetcut.packet.protocol.network.IP;
import com.ardikars.opennetcut.packet.protocol.network.IPv4;
import com.ardikars.opennetcut.packet.protocol.transport.TCP;
import static com.ardikars.opennetcut.app.Utils.BYTE;
import static com.ardikars.opennetcut.app.Utils.SHORT;

import java.nio.ByteBuffer;

import static com.ardikars.jxnet.Jxnet.PcapLoop;
import static com.ardikars.jxnet.Jxnet.PcapStats;

public class PacketTest {

    public static void main(String[] args) {
        sendICMP();
    }

    public static void caputer() {
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Jxnet.PcapOpenLive("eth0", 65535, 1, 3600, errbuf);
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
        PcapStat stat = new PcapStat();
        PcapPktHdr hdr = new PcapPktHdr();

        PcapHandler<String> callback = (String s, PcapPktHdr pcapPktHdr, ByteBuffer byteBuffer) -> {
            if (byteBuffer == null) return;
            byte[] data = new byte[byteBuffer.capacity()];
            byteBuffer.get(data);
            Ethernet ethernet = Ethernet.wrap(data);
            if (ethernet != null && ethernet.getChild() instanceof IPv4) {
                IPv4 iPv4 = (IPv4) ethernet.getChild();
                if (iPv4 != null && iPv4.getChild() instanceof ICMP) {
                    ICMP icmp = (ICMP) iPv4.getChild();
                    System.out.println(ethernet);
                    System.out.println(iPv4);
                    System.out.println(icmp);
                } else if (iPv4 != null && iPv4.getChild() instanceof TCP) {
                    TCP tcp = (TCP) iPv4.getChild();
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

        ipv4.putChild(icmp.toBytes());
        ethernet.putChild(ipv4.toBytes());
        byte[] buffer = ethernet.toBytes();
        ByteBuffer buf = ByteBuffer.allocateDirect(buffer.length);
        buf.put(buffer);
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Jxnet.PcapOpenLive("eth0", 65535, 1, 3600, errbuf);
        Jxnet.PcapSendPacket(pcap, buf, buf.capacity());
        Jxnet.PcapClose(pcap);
    }
}
