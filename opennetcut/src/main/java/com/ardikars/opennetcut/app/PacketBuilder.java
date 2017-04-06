package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.ethernet.Ethernet;
import com.ardikars.jxnet.packet.arp.ARP;
import com.ardikars.jxnet.packet.arp.ARPOperationCode;
import com.ardikars.jxnet.packet.ethernet.ProtocolType;
import com.ardikars.jxnet.packet.icmp.ICMP;
import com.ardikars.jxnet.packet.icmp.ICMPTypeAndCode;
import com.ardikars.jxnet.packet.ip.IPProtocolType;
import com.ardikars.jxnet.packet.ip.IPv4;

public class PacketBuilder {

    public static Packet arpBuilder(MacAddress dst_hwaddr, ARPOperationCode opCode,
                                    MacAddress sha, Inet4Address spa,
                                    MacAddress tha, Inet4Address tpa) {
        Packet arp = new ARP()
                .setHardwareType(StaticField.DATALINK_TYPE)
                .setProtocolType(ProtocolType.IPV4)
                .setHardwareAddressLength((byte) 6)
                .setProtocolAddressLength((byte) 4)
                .setOperationCode(opCode)
                .setSenderHardwareAddress(sha)
                .setSenderProtocolAddress(spa)
                .setTargetHardwareAddress(tha)
                .setTargetProtocolAddress(tpa)
                .build();
        Packet ethernet = new Ethernet()
                .setDestinationMacAddress(dst_hwaddr)
                .setSourceMacAddress(StaticField.CURRENT_MAC_ADDRESS)
                .setEthernetType(ProtocolType.ARP)
                .setPacket(arp);
        return ethernet;
    }

    public static Packet icmpBuilder(MacAddress dst_hwaddr, ICMPTypeAndCode msg,
                                     Inet4Address src, Inet4Address dst, byte[] data) {
        Packet icmp = new ICMP()
                .setTypeAndCode(msg)
                .setPayload(data).build();
        Packet ipv4 = new IPv4()
                .setVersion((byte) 0x4)
                .setDiffServ((byte) 0x0)
                .setExpCon((byte) 0)
                .setIdentification((short) 29257)
                .setFlags((byte) 0x02)
                .setFragmentOffset((short) 0)
                .setTtl((byte) 64)
                .setProtocol(IPProtocolType.ICMP)
                .setSourceAddress(src)
                .setDestinationAddress(dst)
                .setPacket(icmp);
        Packet ethernet = new Ethernet()
                .setDestinationMacAddress(dst_hwaddr)
                .setSourceMacAddress(StaticField.CURRENT_MAC_ADDRESS)
                .setEthernetType(ProtocolType.IPV4)
                .setPacket(ipv4);
        return ethernet;
    }

}
