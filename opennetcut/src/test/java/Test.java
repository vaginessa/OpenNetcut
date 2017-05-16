import com.ardikars.jxnet.Inet4Address;
import com.ardikars.opennetcut.app.StaticField;
import org.apache.commons.net.util.SubnetUtils;

public class Test {

    public static void main(String[] args) {
        SubnetUtils su = new SubnetUtils("192.168.100.0",
                "255.255.254.0");
        for (String s : su.getInfo().getAllAddresses()) {
            System.out.println(s);
        }
    }
}
