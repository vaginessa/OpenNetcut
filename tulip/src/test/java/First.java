import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.MacAddress;

/**
 * Created by root on 06/04/17.
 */
public class First {

    public static void main(String[] args) {
        System.out.println(MacAddress.fromNicName("eth0"));
    }
}
