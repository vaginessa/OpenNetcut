package com.ardikars.opennetcut;

import com.ardikars.jxnet.BpfProgram;
import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.Jxnet;
import static com.ardikars.jxnet.Jxnet.PcapOpenLive;
import com.ardikars.jxnet.Pcap;
import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.jxnet.util.AddrUtils;
import com.ardikars.jxnet.util.Loader;
import com.ardikars.opennetcut.app.Utils;
import com.ardikars.opennetcut.view.MainWindow;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Main {
    
    
    
    public static void main(String[] args) throws JxnetException {
        run();
    }
    
    private static void run() throws JxnetException {
        
        String source = Utils.getDeviceName();
        
        StringBuilder errbuf = new StringBuilder();
        
        Inet4Address netaddr = Inet4Address.valueOf(0);
        Inet4Address netmask = Inet4Address.valueOf(0);
        
        if (Jxnet.PcapLookupNet(source, netaddr, netmask, errbuf) < 0) {
            JOptionPane.showMessageDialog(null, errbuf.toString());
            System.exit(1);
        }
                
        MainWindow.main_windows.setSource(source);
        MainWindow.main_windows.setSnaplen(1500);
        MainWindow.main_windows.setPromisc(1);
        MainWindow.main_windows.setToMs(150);
        Pcap pcap = PcapOpenLive(MainWindow.main_windows.getSource(), 
                MainWindow.main_windows.getSnaplen(),
                MainWindow.main_windows.getPromisc(),
                MainWindow.main_windows.getToMs(), errbuf);
        if (pcap == null) {
            JOptionPane.showMessageDialog(null, errbuf.toString());
            System.exit(1);
        }
        if (Jxnet.PcapDatalink(pcap) != 1) {
            Jxnet.PcapClose(pcap);
            JOptionPane.showMessageDialog(null, source+ " is not Ethernet.");
            System.exit(1);
        }
        BpfProgram bp = new BpfProgram();
        if (Jxnet.PcapCompile(pcap, bp, "arp", 1, netmask.toInt()) !=0 ) {
            Jxnet.PcapClose(pcap);
            JOptionPane.showMessageDialog(null, "Failed to compile bpf");
            System.exit(1);
        }
        if (Jxnet.PcapSetFilter(pcap, bp) != 0) {
            Jxnet.PcapClose(pcap);
            JOptionPane.showMessageDialog(null, "Failed to set filter");
            System.exit(1);
        } 
        
        MainWindow.main_windows.setPcap(pcap);
        MainWindow.main_windows.setCurrentIpAddr(Utils.getIpAddr(source));
        MainWindow.main_windows.setCurrentHwAddr(AddrUtils.getHardwareAddress(source));
        MainWindow.main_windows.setNetaddr(netaddr);
        MainWindow.main_windows.setNetmask(netmask);

        MainWindow.main_windows.initMyComponents();
        MainWindow.main_windows.setVisible(true);
    }
    
    static {
        try {
            Loader.loadLibrary();
        } catch (UnsatisfiedLinkError ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}