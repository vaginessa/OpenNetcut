import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.Jxnet;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.Pcap;
import com.ardikars.jxnet.packet.ethernet.EtherType;
import com.ardikars.jxnet.packet.ethernet.Ethernet;
import com.ardikars.jxnet.packet.icmp.ICMPv4;
import com.ardikars.jxnet.packet.icmp.ICMPv4TimeExceeded;
import com.ardikars.jxnet.packet.ipv4.IPv4;
import com.ardikars.jxnet.packet.ipv4.Protocol;
import org.junit.Test;

import java.nio.ByteBuffer;

public class Tests {

    @Test
    public void run() {
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Jxnet.PcapOpenLive("eth0", 1500, 1, 2000, errbuf);
        Ethernet ethernet = new Ethernet()
                .setDestinationMacAddress(MacAddress.BROADCAST)
                .setSourceMacAddress(MacAddress.valueOf("de:ad:be:ef:c0:de"))
                .setEtherType(EtherType.IPV4);
        IPv4 iPv4 = new IPv4()
                .setVersion((byte) 0x4)
                .setDiffServ((byte) 0x0)
                .setExpCon((byte) 0)
                .setIdentification((short) 29257)
                .setFlags((byte) 0x02)
                .setFragmentOffset((short) 0)
                .setTtl((byte) 64)
                .setProtocol(Protocol.ICMP)
                .setSourceAddress(Inet4Address.valueOf("192.168.1.24"))
                .setDestinationAddress(Inet4Address.valueOf("192.168.1.25"));
        ICMPv4 icmp = new ICMPv4()
                .setMessage(ICMPv4TimeExceeded.TTL_EXCEEDED_IN_TRANSIT)
                .setData(MacAddress.ZERO.toBytes());
        iPv4.setPacket(icmp);
        ethernet.setPacket(iPv4);

        byte[] buf = ethernet.getBytes();
        ByteBuffer buffer = ByteBuffer.allocateDirect(buf.length);
        buffer.put(buf);
        for (int i=0; i<5; i++) {
            Jxnet.PcapSendPacket(pcap, buffer, buffer.capacity());
        }
        Jxnet.PcapClose(pcap);
    }

}
