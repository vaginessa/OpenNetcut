import com.ardikars.jxnet.*;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.packet.protocol.datalink.ethernet.Ethernet;
import com.ardikars.jxnet.packet.protocol.lan.arp.ARP;
import com.ardikars.jxnet.packet.protocol.lan.arp.OperationCode;
import com.ardikars.jxnet.packet.protocol.network.icmp.ICMP;
import com.ardikars.jxnet.packet.protocol.network.icmp.Type;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.opennetcut.app.OUI;
import com.ardikars.opennetcut.app.PacketBuilder;
import com.ardikars.opennetcut.app.StaticField;
import com.ardikars.opennetcut.app.Utils;

public class IDSTest {

    static int trapCounter = 0;

    public static void main(String[] args) {


        PacketHandler<String> packetHandler = (arg, pktHdr, packets) -> {


        };

        Static.loop(StaticField.PCAP, -1, packetHandler, null);
        Jxnet.PcapClose(StaticField.PCAP);
    }

}
