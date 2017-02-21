/**
 * Copyright (C) 2017  Ardika Rommy Sanjaya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.*;
import com.ardikars.opennetcut.packet.protocol.datalink.Ethernet;
import com.ardikars.opennetcut.packet.protocol.lan.ARP;
import com.ardikars.opennetcut.view.MainWindow;
import com.google.common.base.Stopwatch;

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
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        boolean loop = true;
        while(loop) {
            if ((byteBuffer = PcapNext(pcap, pcapPktHdr)) != null) {
                Ethernet ethernet = getEthernet(getBytes(byteBuffer));
                ARP arp = getArp(ethernet);
                if (ethernet != null && arp != null) {
                    if (arp.getOpCode() == ARP.OperationCode.REPLY) {
                        MacAddress ethDst = ethernet.getDestinationMacAddress();
                        MacAddress ethSrc = ethernet.getSourceMacAddress();
                        MacAddress sha = arp.getSenderHardwareAddress();
                        MacAddress tha = arp.getTargetHardwareAddress();
                        Inet4Address spa = arp.getSenderProtocolAddress();
                        Inet4Address tpa = arp.getTargetProtocolAddress();

                        if (ethSrc.toString().equals(sha.toString())) {

                        }
                        if (ethDst.toString().equals(tha.toString())) {

                        }
                        if (OUI.searchVendor(sha.toString()) != null) {
                            if (sha.toString().equals(MacAddress.ZERO) ||
                                    sha.toString().equals(MacAddress.BROADCAST)) {

                            } else {

                            }
                        } else {

                        }

                    }
                }
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

    private byte[] getBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return bytes;
    }

    private Ethernet getEthernet(byte[] bytes) {
        return Ethernet.wrap(bytes);
    }

    public ARP getArp(Ethernet ethernet) {
        if (ethernet.getChild() instanceof ARP) {
            return ((ARP) ethernet.getChild());
        }
        return null;
    }
}
