import com.ardikars.jxnet.*;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.opennetcut.app.StaticField;

public class IDSTest {

    static int trapCounter = 0;

    public static void main(String[] args) {


        PacketHandler<String> packetHandler = (arg, pktHdr, packets) -> {


        };

        Static.loop(StaticField.PCAP, -1, packetHandler, null);
        Jxnet.PcapClose(StaticField.PCAP);
    }

}
