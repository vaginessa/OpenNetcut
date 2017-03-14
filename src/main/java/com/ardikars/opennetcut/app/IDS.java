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

import static com.ardikars.jxnet.Jxnet.*;

public class IDS extends Thread {

    @Override
    public void run() {

        PacketHandler<String> packetHandler = (arg, pktHdr, packets) -> {

            Ethernet ethernet = (Ethernet) Packet.parsePacket(packets, Ethernet.class);
            ARP arp = (ARP) Packet.parsePacket(packets, ARP.class);

            MacAddress ethDst = ethernet.getDestinationMacAddress();
            MacAddress ethSrc = ethernet.getSourceMacAddress();

            MacAddress sha = null;
            MacAddress tha = null;
            Inet4Address spa = null;
            Inet4Address tpa = null;

            double UNPADDED_ETHERNET_FRAME = 0;
            double SAME_SOURCE_MAC_ADDRESS = 0;
            double UNKNOWN_OUI = 0;
            double ENABLED_IP_ROUTING = 0;

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
                System.out.println(ethernet);
                System.out.println(arp);
                // Check
                ENABLED_IP_ROUTING = (ethernet.getPadding() != true ? 1 : 0);
                if (!ethSrc.equals(sha)) {
                    SAME_SOURCE_MAC_ADDRESS = 1;
                }
                if (OUI.searchVendor(arp.getSenderHardwareAddress().toString()).equals("")) {
                    UNKNOWN_OUI = 1;

                }
                if (StaticField.CACHE.get(spa) == null) {
                    StaticField.CACHE.put(spa, sha);
                } else if (!StaticField.CACHE.get(spa).equals(sha)) {
                    // send ICMP trap
                    Ethernet icmpTrap = (Ethernet) PacketBuilder.
                            icmpBuilder(ethSrc,
                                    Type.ECHO_REPLY, (byte) 0x0, spa,
                                    StaticField.CURRENT_INET4ADDRESS,
                                    new byte[] {});
                    ByteBuffer buffer = FormatUtils.toDirectBuffer(icmpTrap.getBytes());
                    PcapSendPacket(StaticField.PCAP, buffer, buffer.capacity());

                    PcapPktHdr hdr = new PcapPktHdr();
                    List<Packet> pkts = Static.next(StaticField.PCAP, hdr);
                    IPv4 ipv4 = (IPv4) Packet.parsePacket(pkts, IPv4.class);
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
                System.out.println("UNPADDED_ETHERNET_FRAME: " + UNPADDED_ETHERNET_FRAME);
                System.out.println("SAME_SOURCE_MAC_ADDRESS: " + SAME_SOURCE_MAC_ADDRESS);
                System.out.println("UNKNOWN_OUI: " + UNKNOWN_OUI);
                System.out.println("ENABLED_IP_ROUTING: " + ENABLED_IP_ROUTING);
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
        try {
            Thread.sleep(3600*30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ids.stopThread();
    }
}
