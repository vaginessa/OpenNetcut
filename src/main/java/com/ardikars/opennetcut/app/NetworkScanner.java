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
import static com.ardikars.jxnet.Jxnet.*;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.jxnet.Static;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.protocol.datalink.ethernet.EtherType;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.jxnet.packet.protocol.datalink.ethernet.Ethernet;
import com.ardikars.jxnet.packet.protocol.lan.arp.ARP;
import com.ardikars.jxnet.packet.protocol.lan.arp.OperationCode;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ardikars.util.Utils;
import org.apache.commons.net.util.SubnetUtils;

@SuppressWarnings("unchecked")
public class NetworkScanner extends Thread {
    
    private volatile boolean stop = false;
    
    private final PacketHandler handler;
    private final List<Inet4Address> ips = new ArrayList<Inet4Address>();
    
    private Ethernet ethernet = new Ethernet();
    private ARP arp = new ARP();

    private Inet4Address scanIP;
            
    private int index;

    public NetworkScanner(PacketHandler handler) {
        SubnetUtils su = new SubnetUtils(StaticField.CURRENT_NETWORK_ADDRESS.toString(),
                StaticField.CURRENT_NETMASK_ADDRESS.toString());
        String[] strips = su.getInfo().getAllAddresses();
        for (String ip : strips) {
            ips.add(Inet4Address.valueOf(ip));
        }
        this.handler = handler;
        this.index = 0;
    }
    
    public NetworkScanner(PacketHandler handler, Inet4Address ip) {
        this.handler = handler;
        this.scanIP = ip;
        this.index = 1;
    }
    
    @Override
    public void run() {
        if (StaticField.LOGGER != null)
            StaticField.LOGGER.log(LoggerStatus.COMMON, "[ INFO ] :: Start scanning.");
        ethernet.setDestinationMacAddress(MacAddress.BROADCAST)
                .setSourceMacAddress(StaticField.CURRENT_MAC_ADDRESS)
                .setEtherType(EtherType.ARP)
                .setPadding(true);
                
        arp.setHardwareType(DataLinkType.EN10MB)
                .setProtocolType(EtherType.IPV4)
                .setHardwareAddressLength((byte) 6)
                .setProtocolAddressLength((byte) 4)
                .setOpCode(OperationCode.ARP_REQUEST)
                .setSenderHardwareAddress(StaticField.CURRENT_MAC_ADDRESS)
                .setSenderProtocolAddress(StaticField.CURRENT_INET4ADDRESS)
                .setTargetHardwareAddress(MacAddress.ZERO);
        
        switch (index) {
            case 0:
                scanAll();
                break;
            case 1:
                scanByIp();
                break;
            default:
        }
    }
    
    private void scanAll() {
        StaticField.RANDOM_STRING = Utils.getPcapTmpFileName();
        PcapDumper dumper;
        dumper = Jxnet.PcapDumpOpen(StaticField.PCAP, StaticField.RANDOM_STRING);
        if (dumper == null) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: " + Jxnet.PcapGetErr(StaticField.PCAP));
            }
        }
        PcapPktHdr pktHdr = new PcapPktHdr();
        ByteBuffer buffer = null;

        int no=1;
        int ipsSize = ips.size();
        for(int i=0; i<ipsSize; i++) {
            arp.setTargetProtocolAddress(ips.get(i));
            ethernet.setPacket(arp);
            buffer = FormatUtils.toDirectBuffer(ethernet.getBytes());
            if (PcapSendPacket(StaticField.PCAP, buffer, buffer.capacity()) != 0) {
                if (StaticField.LOGGER != null) {
                    StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed to send arp packet.");
                }
                break;
            } else {
                Map<Class, Packet> packets = Static.next(StaticField.PCAP, pktHdr);
                if (packets != null) {
                    ARP capArp = (ARP) packets.get(ARP.class);
                    if (capArp.getOpCode() == OperationCode.ARP_REPLY) {
                        Jxnet.PcapDump(dumper, pktHdr, FormatUtils.toDirectBuffer(capArp.getBytes()));
                        this.handler.nextPacket(no, pktHdr, packets);
                        no++;
                    }
                }
            }
            if (stop) {
                StaticField.LOGGER.log(LoggerStatus.PROGRESS, Integer.toString(100));
                if (!dumper.isClosed()) {
                    Jxnet.PcapDumpClose(dumper);
                    StaticField.LOGGER.log(LoggerStatus.COMMON, "[ INFO ] :: Scanning finished.");
                }
                return;
            }
            if (StaticField.LOGGER != null)
                StaticField.LOGGER.log(LoggerStatus.PROGRESS, Integer.toString((i * 100)/ipsSize));
        }
        if (StaticField.LOGGER != null) {
            StaticField.LOGGER.log(LoggerStatus.PROGRESS, Integer.toString(100));
            if (!dumper.isClosed()) {
                Jxnet.PcapDumpClose(dumper);
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ INFO ] :: Scanning finished.");
            }
        }
    }
    
    private void scanByIp() {
        StaticField.RANDOM_STRING = Utils.getPcapTmpFileName();
        PcapDumper dumper;
        dumper = Jxnet.PcapDumpOpen(StaticField.PCAP, StaticField.RANDOM_STRING);
        if (dumper == null) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: " + Jxnet.PcapGetErr(StaticField.PCAP));
            }
        }
        PcapPktHdr pktHdr = new PcapPktHdr();
        ByteBuffer buffer = null;
        byte[] bytes = null;
        int no=1;
        arp.setTargetProtocolAddress(scanIP);
        ethernet.setPacket(arp);
        buffer = ByteBuffer.wrap(ethernet.getBytes());
        if (Jxnet.PcapSendPacket(StaticField.PCAP, buffer, buffer.capacity()) != 0) {
            if (StaticField.LOGGER != null) {
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed to send arp packet.");
            }
            return;
        } else {
            Map<Class, Packet> packets = Static.next(StaticField.PCAP, pktHdr);
            if (packets != null) {
                if (stop) {
                    StaticField.LOGGER.log(LoggerStatus.PROGRESS, Integer.toString(100));
                    if (!dumper.isClosed()) {
                        Jxnet.PcapDumpClose(dumper);
                        StaticField.LOGGER.log(LoggerStatus.COMMON, "[ INFO ] :: Scanning finished.");
                    }
                    return;
                }
                ARP capArp = (ARP) packets.get(ARP.class);
                if (capArp != null) {
                    if (capArp.getOpCode() == OperationCode.ARP_REPLY) {
                        Jxnet.PcapDump(dumper, pktHdr, FormatUtils.toDirectBuffer(capArp.getBytes()));
                        handler.nextPacket(no, pktHdr, packets);
                        no++;
                    }
                }
            }
        }
        if (StaticField.LOGGER != null) {
            StaticField.LOGGER.log(LoggerStatus.PROGRESS, Integer.toString(100));
            if (!dumper.isClosed()) {
                Jxnet.PcapDumpClose(dumper);
                StaticField.LOGGER.log(LoggerStatus.COMMON, "[ INFO ] :: Scanning finished.");
            }
        }
    }
    
    public void stopThread() {
        this.stop = true;
    }
    
}
