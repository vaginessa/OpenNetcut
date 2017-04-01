import com.ardikars.jxnet.*;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.packet.icmp.ICMPv4;

/**
 * Created by root on 3/19/17.
 */
public class LoopTest {

    public static void main(String[] args) {
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Jxnet.PcapOpenLive("eth0", 1500, 1, 2000, errbuf);
        Jxnet.PcapSetDirection(pcap, PcapDirection.PCAP_D_INOUT);
        PacketHandler<String> callback = (arg, pktHdr, packets) -> {
            ICMPv4 icmp = (ICMPv4) packets.get(ICMPv4.class);
            if (icmp != null) System.out.println(icmp);
        };

        Static.loop(pcap, 100, callback, null);

        Jxnet.PcapClose(pcap);

    }
}
