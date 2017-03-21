import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.Static;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ardikars.jxnet.Jxnet.OK;
import static com.ardikars.jxnet.Jxnet.PcapLookupNet;

public class Tests {

    @Test
    public void run() {
        Map<String, Long> cache = new HashMap<String, Long>();

        long l = (long)cache.get("192.168.1.4");
        System.out.println(l);

        System.out.println("===");
    }

}
