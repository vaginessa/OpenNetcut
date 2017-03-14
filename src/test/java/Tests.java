import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.Static;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ardikars.jxnet.Jxnet.OK;
import static com.ardikars.jxnet.Jxnet.PcapLookupNet;

public class Tests {

    @Test
    public void run() {
        ByteBuffer buf = ByteBuffer.allocateDirect(2);
        buf.put((byte) 1);
        buf.put((byte) 2);
        buf.flip();
        if (buf.hasRemaining()) {
            System.out.println("HAH");
        }
        Static.printInfo(buf);
    }

    private void fill(List<String> list) {
        //list.add("1 str");
        //list.add("2 str");
        //list.add("3 str");
        list = newList();
    }

    private List<String> newList() {
        List<String> str = new ArrayList<String>();
        str.add("satu");
        str.add("dua");
        str.add("tiga");
        return str;
    }

}
