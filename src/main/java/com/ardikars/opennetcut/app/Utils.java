package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.Jxnet;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.jxnet.util.AddrUtils;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.net.util.SubnetUtils;

public class Utils {
    
    public static String getDeviceName() {
        StringBuilder errbuf = new StringBuilder();
        String device;
        if((device = Jxnet.pcapLookupDev(errbuf)) == null) {
            return errbuf.toString();
        }
        return device;
    }
    
    public static List<Inet4Address> getHostList(String source) throws JxnetException {
        StringBuilder errbuf = new StringBuilder();
        Inet4Address netp = Inet4Address.valueOf(0);
        Inet4Address mask = Inet4Address.valueOf(0);
        int r;
        if((r= Jxnet.pcapLookupNet(source, netp, mask, errbuf)) != 0) {
            throw new JxnetException(errbuf.toString());
        }
        SubnetUtils su = new SubnetUtils(netp.toString(), mask.toString());
        String[] list = su.getInfo().getAllAddresses();
        List<Inet4Address> listAddr = new ArrayList<Inet4Address>();
        for(int i=0; i<list.length; i++) {
            listAddr.add(Inet4Address.valueOf(list[i]));
        }
        return listAddr;
    }

    public static DefaultTableModel createDefaultTableModel(String[] columnNames) {
        return new DefaultTableModel(new Object[][] {}, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch(columnIndex) {
                    case 1:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
        };
    }
    
}
