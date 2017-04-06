import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.MacAddress;

/**
 * Created by root on 06/04/17.
 */
public class First {

    public static void main(String[] args) {
        Inet4Address first = Inet4Address.valueOf("192.168.1.1");
        Inet4Address second = Inet4Address.valueOf(first.toInt());
        System.out.println(first.hashCode() == second.hashCode());

        MacAddress pertama = MacAddress.valueOf("de:ad:be:ef:c0:fe");
        MacAddress kedua = MacAddress.valueOf(pertama.toBytes());
        System.out.println(pertama.hashCode() == kedua.hashCode());
    }
}
