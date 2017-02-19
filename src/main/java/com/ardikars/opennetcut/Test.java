/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ardikars.opennetcut;

import com.ardikars.jxnet.BpfProgram;
import com.ardikars.jxnet.Jxnet;
import static com.ardikars.jxnet.Jxnet.PcapNext;
import static com.ardikars.jxnet.Jxnet.PcapStats;
import com.ardikars.jxnet.Pcap;
import com.ardikars.jxnet.PcapPktHdr;
import com.ardikars.jxnet.PcapStat;
import java.nio.ByteBuffer;
import org.apache.commons.net.ntp.TimeStamp;

public class Test {
    public static void main(String[] args) {
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Jxnet.PcapOpenLive("wlan0", 65535, 1, 3600, errbuf);
        BpfProgram fp = new BpfProgram();
        if(Jxnet.PcapCompile(pcap, fp, "ip", 1, 0xfffffff) != 0) {
            System.err.println("Failed to compile bpf");
            Jxnet.PcapClose(pcap);
            return;
          }
        if(Jxnet.PcapSetFilter(pcap, fp) != 0) {
        System.err.println("Failed to set filter");
        Jxnet.PcapClose(pcap);
        return;
       }
        PcapStat stat = new PcapStat();
        PcapPktHdr hdr = new PcapPktHdr();
        ByteBuffer buf = null;
        while ((buf = PcapNext(pcap, hdr)) != null) {
        }
        PcapStats(pcap, stat);
        System.out.println(stat.getPsRecv());
        System.out.println(stat.getPsDrop());
        System.out.println(stat.getPsIfdrop());
        System.out.println(buf);
        System.out.println("==");
        System.out.println("FINISHED");
            Jxnet.PcapClose(pcap);
    }
}
