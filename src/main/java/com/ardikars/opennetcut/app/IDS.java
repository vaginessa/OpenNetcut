package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.*;
import com.ardikars.opennetcut.view.MainWindow;

import java.nio.ByteBuffer;

import static com.ardikars.jxnet.Jxnet.*;
public class IDS extends Thread {

    private volatile boolean stop = false;

    @Override
    public void run() {
        StringBuilder errbuf = new StringBuilder();

        String source = MainWindow.main_windows.getSource();
        int snaplen = MainWindow.main_windows.getSnaplen();
        int promisc = MainWindow.main_windows.getPromisc();
        int to_ms = MainWindow.main_windows.getToMs();

        Inet4Address address = MainWindow.main_windows.getNetaddr();
        Inet4Address netmask = MainWindow.main_windows.getNetmask();

        Pcap pcap;
        if ((pcap = PcapOpenLive(source, snaplen, promisc, to_ms, errbuf)) == null) {
            System.err.println(errbuf.toString());
        }
        BpfProgram fp = new BpfProgram();
        if(PcapCompile(pcap, fp, "arp", 1, netmask.toInt()) != 0) {
            System.err.println("Failed to compile bpf");
            PcapClose(pcap);
            return;
        }
        if(PcapSetFilter(pcap, fp) != 0) {
            System.err.println("Failed to set filter");
            PcapClose(pcap);
            return;
        }

        ByteBuffer byteBuffer = null;
        PcapPktHdr pcapPktHdr = new PcapPktHdr();
        boolean loop = true;
        while(loop) {
            if ((byteBuffer = PcapNext(pcap, pcapPktHdr)) != null) {
            }
            if (stop) {
                PcapClose(pcap);
                loop = stop;
            }
        }

    }

    public void stopThread() {
        stop = true;
    }
}
