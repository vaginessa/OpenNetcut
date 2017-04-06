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

package com.ardikars.tulip;

import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.Pcap;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.packet.PacketHelper;
import com.ardikars.jxnet.packet.arp.ARP;
import com.ardikars.jxnet.packet.arp.ARPOperationCode;
import com.ardikars.jxnet.packet.ethernet.Ethernet;
import com.ardikars.jxnet.packet.ethernet.ProtocolType;

import static com.ardikars.jxnet.Jxnet.*;

public class IDS extends Thread {

    private IDS() {
    }

    public static IDS newThread() {
        return new IDS();
    }

    @Override
    public void run() {

        PacketHandler<String> packetHandler = (arg, pktHdr, packets) -> {

            Ethernet ethernet = (Ethernet) packets.get(Ethernet.class);
            if (ethernet == null || ethernet.getEthernetType() != ProtocolType.ARP)
                return;
            ARP arp = (ARP) packets.get(ARP.class);
            if (arp == null) return;

            MacAddress ethDst = ethernet.getDestinationMacAddress();
            MacAddress ethSrc = ethernet.getSourceMacAddress();

            MacAddress sha = null;
            MacAddress tha = null;
            Inet4Address spa = null;
            Inet4Address tpa = null;

            double INVALID_PACKET = 0;
            double UNCONSISTENT_SHA = 0;
            double UNPADDED_ETHERNET_FRAME = 0;
            double UNKNOWN_OUI = 0;
            double EPOCH_TIME = 0;

            sha = arp.getSenderHardwareAddress();
            tha = arp.getTargetHardwareAddress();
            spa = arp.getSenderProtocolAddress();
            tpa = arp.getTargetProtocolAddress();

            if (arp.getOperationCode() != ARPOperationCode.ARP_REPLY ||
                    !ethDst.equals(StaticField.CURRENT_MAC_ADDRESS) ||
                    tpa.equals(StaticField.CURRENT_MAC_ADDRESS)) {
                return;
            }
            // Check

            if (!ethSrc.equals(sha) || !ethDst.equals(tha)) {
                INVALID_PACKET = 1;
            }

            MacAddress shaCache = StaticField.ARP_CACHE.get(spa);
            if  (shaCache == null) {
                StaticField.ARP_CACHE.put(spa, sha);
            } else {
                if (!sha.equals(shaCache)) {
                    UNCONSISTENT_SHA = 1.0;
                }
                StaticField.ARP_CACHE.put(spa, sha);
            }

            UNPADDED_ETHERNET_FRAME = (pktHdr.getCapLen() < 60 ? 1 : 0);

            /*if (OUI.searchVendor(arp.getSenderHardwareAddress().toString()).equals("")) {
                UNKNOWN_OUI = 1;
            }*/

            Long epochTimeCache = StaticField.EPOCH_TIME.get(spa);
            if (epochTimeCache == null || epochTimeCache == 0) {
                StaticField.EPOCH_TIME.put(spa, Long.valueOf(System.currentTimeMillis()));
            } else {
                long time = (System.currentTimeMillis() - (long) epochTimeCache);
                if (time > 2000) time = 0;
                EPOCH_TIME = (( time / 5000.0));
                StaticField.EPOCH_TIME.put(spa, Long.valueOf(System.currentTimeMillis()));
            }

            System.out.println("=================================");
            System.out.println("INVALID_PACKET          : " + INVALID_PACKET);
            System.out.println("UNCONSISTENT_SHA        : " + UNCONSISTENT_SHA);
            System.out.println("UNPADDED_ETHERNET_FRAME : " + UNPADDED_ETHERNET_FRAME);
            System.out.println("UNKNOWN_OUI             : " + UNKNOWN_OUI);
            System.out.println("EPOCH_TIME              : " + EPOCH_TIME);
            System.out.println("=================================");

            if (StaticField.ICMP_HANDLER != null) {
                ICMPTrap.run(sha).start();
            }

        };

        PacketHelper.loop(StaticField.ARP_HANDLER, -1, packetHandler, null);

    }

    public void stopThread() {
        PcapBreakLoop(StaticField.ARP_HANDLER);
    }

}
