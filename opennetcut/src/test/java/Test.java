import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.PcapAddr;
import com.ardikars.jxnet.PcapIf;
import com.ardikars.jxnet.SockAddr;
import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.opennetcut.app.StaticField;

import java.util.ArrayList;
import java.util.List;

import static com.ardikars.jxnet.Jxnet.PcapFindAllDevs;

public class Test {

    @org.junit.Test
    public void run() throws Exception {

        List<PcapIf> pcapIf = new ArrayList<PcapIf>();
        if (PcapFindAllDevs(pcapIf, StaticField.ERRBUF) != 0) {
            throw new Exception(StaticField.ERRBUF.toString());
        }
        for (PcapIf If : pcapIf) {
            if (If.getName().equals("wlan0")) {
                for (PcapAddr addrs : If.getAddresses()) {
                    if (addrs.getAddr().getSaFamily() == SockAddr.Family.AF_INET) {
                        System.out.println(addrs.getAddr());
                        System.out.println(addrs.getNetmask());
                        System.out.println(addrs.getBroadAddr());
                    }
                }
                break;
            }
        }
    }

}
