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
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.packet.protocol.datalink.ethernet.Ethernet;
import com.ardikars.jxnet.packet.protocol.lan.arp.ARP;
import com.ardikars.jxnet.packet.protocol.lan.arp.OperationCode;
import com.ardikars.jxnet.packet.protocol.network.icmp.ICMP;
import com.ardikars.jxnet.packet.protocol.network.icmp.Type;
import com.ardikars.jxnet.packet.protocol.network.ip.IPv4;
import com.ardikars.jxnet.util.FormatUtils;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ardikars.jxnet.Jxnet.*;

public class IDS extends Thread {

    @Override
    public void run() {

        PacketHandler<String> packetHandler = (arg, pktHdr, packets) -> {

            Ethernet ethernet = (Ethernet) packets.get(Ethernet.class);
            ARP arp = (ARP) packets.get( ARP.class);
            MacAddress ethDst = ethernet.getDestinationMacAddress();
            MacAddress ethSrc = ethernet.getSourceMacAddress();

            MacAddress sha = null;
            MacAddress tha = null;
            Inet4Address spa = null;
            Inet4Address tpa = null;

            double UNPADDED_ETHERNET_FRAME = 0;
            double NOT_SAME_SOURCE_MAC_ADDRESS = 0;
            double NOT_SAME_DESTINATION_MAC_ADDRESS = 0;
            double UNKNOWN_OUI = 0;
            double EPOCH_TIME = 0;
            double ENABLED_IP_ROUTING = 0;
            double NOT_VALID_TPA = 0;

            if (arp != null) {
                sha = arp.getSenderHardwareAddress();
                tha = arp.getTargetHardwareAddress();
                spa = arp.getSenderProtocolAddress();
                tpa = arp.getTargetProtocolAddress();

                if (arp.getOpCode() != OperationCode.ARP_REPLY ||
                        !ethDst.equals(StaticField.CURRENT_MAC_ADDRESS) ||
                        tpa.equals(StaticField.CURRENT_MAC_ADDRESS)) {
                    return;
                }
                // Check
                UNPADDED_ETHERNET_FRAME = (pktHdr.getCapLen() < 60 ? 1 : 0);
                if (!ethSrc.equals(sha)) {
                    NOT_SAME_SOURCE_MAC_ADDRESS = 1;
                }
                if (!ethDst.equals(tha)) {
                    NOT_SAME_DESTINATION_MAC_ADDRESS = 1;
                }
                if (!arp.getTargetProtocolAddress().equals(StaticField.CURRENT_INET4ADDRESS)) {
                    NOT_VALID_TPA = 1;
                }
                if (OUI.searchVendor(arp.getSenderHardwareAddress().toString()).equals("")) {
                    UNKNOWN_OUI = 1;
                }

                Entry entry = StaticField.CACHE.get(spa.toString());
                if (entry == null) {
                    StaticField.CACHE.put(spa.toString(), new Entry()
                            .setMacAddress(sha));
                }
                if (entry.equals(sha)) {
                    StaticField.CACHE.put(spa.toString(), new Entry()
                            .setMacAddress(sha));
                    EPOCH_TIME = entry.getEpochTime();
                } else {
                    // send ICMP trap
                    StaticField.CACHE.put(spa.toString(), new Entry()
                            .setMacAddress(sha));
                    Ethernet icmpTrap = (Ethernet) PacketBuilder.
                            icmpBuilder(ethSrc,
                                    Type.ECHO_REPLY, (byte) 0x0, spa,
                                    StaticField.CURRENT_INET4ADDRESS,
                                    new byte[] {});
                    ByteBuffer buffer = FormatUtils.toDirectBuffer(icmpTrap.getBytes());
                    PcapSendPacket(StaticField.PCAP, buffer, buffer.capacity());

                    PcapPktHdr hdr = new PcapPktHdr();
                    Map<Class, Packet> pkts = Static.next(StaticField.PCAP, hdr);
                    IPv4 ipv4 = (IPv4) pkts.get(IPv4.class);
                    if (ipv4 != null) {
                        if (ipv4.getDestinationAddress() != null &&
                                ipv4.getDestinationAddress().equals(StaticField.CURRENT_INET4ADDRESS)) {
                            if (ipv4.getPacket() instanceof ICMP) {
                                ICMP icmp = (ICMP) ipv4.getPacket();
                                if (icmp.getType().equals(Type.ECHO_REPLY) && icmp.getCode() == (byte) 0x0) {
                                    ENABLED_IP_ROUTING = 1;
                                }
                            }
                        }
                    }
                }

                System.out.println("UNPADDED_ETHERNET_FRAME: " + UNPADDED_ETHERNET_FRAME);
                System.out.println("NOT_SAME_SOURCE_MAC_ADDRESS: " + NOT_SAME_SOURCE_MAC_ADDRESS);
                System.out.println("NOT_SAME_DESTINATION_MAC_ADDRESS: " + NOT_SAME_DESTINATION_MAC_ADDRESS);
                System.out.println("NOT_VALID_TPA: " + NOT_VALID_TPA);
                System.out.println("UNKNOWN_OUI: " + UNKNOWN_OUI);
                System.out.println("ENABLED_IP_ROUTING: " + ENABLED_IP_ROUTING);
                System.out.println("EPOCH_TIME: " + EPOCH_TIME);
            }
        };

        Static.loop(StaticField.PCAP, -1, packetHandler, null);

    }

    public void stopThread() {
        PcapBreakLoop(StaticField.PCAP);
    }

}

class Main {
    public static void main(String[] args) {
        Utils.initialize(null, StaticField.SNAPLEN, StaticField.PROMISC, StaticField.TIMEOUT, "arp or icmp");
        IDS ids = new IDS();
        ids.start();
        System.out.println("Started.");
        try {
            Thread.sleep(3600*15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ids.stopThread();
        PcapClose(StaticField.PCAP);
        System.out.println("Stopped");
    }
}