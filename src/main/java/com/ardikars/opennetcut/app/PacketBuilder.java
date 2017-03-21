package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.DataLinkType;
import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.protocol.datalink.ethernet.EtherType;
import com.ardikars.jxnet.packet.protocol.datalink.ethernet.Ethernet;
import com.ardikars.jxnet.packet.protocol.lan.arp.ARP;
import com.ardikars.jxnet.packet.protocol.lan.arp.OperationCode;
import com.ardikars.jxnet.packet.protocol.network.icmp.ICMP;
import com.ardikars.jxnet.packet.protocol.network.icmp.Type;
import com.ardikars.jxnet.packet.protocol.network.ip.IPv4;
import com.ardikars.jxnet.packet.protocol.network.ip.Protocol;
import com.ardikars.jxnet.util.FormatUtils;

public class PacketBuilder {

    public static Packet arpBuilder(MacAddress dst_hwaddr, OperationCode opCode,
                                    MacAddress sha, Inet4Address spa,
                                    MacAddress tha, Inet4Address tpa) {
        ARP arp = new ARP()
                .setHardwareType(StaticField.DATALINK_TYPE)
                .setProtocolType(EtherType.IPV4)
                .setHardwareAddressLength((byte) 6)
                .setProtocolAddressLength((byte) 4)
                .setOpCode(opCode)
                .setSenderHardwareAddress(sha)
                .setSenderProtocolAddress(spa)
                .setTargetHardwareAddress(tha)
                .setTargetProtocolAddress(tpa);
        Ethernet ethernet = new Ethernet()
                .setDestinationMacAddress(dst_hwaddr)
                .setSourceMacAddress(StaticField.CURRENT_MAC_ADDRESS)
                .setEtherType(EtherType.ARP);
        ethernet.setPacket(arp);
        return ethernet;
    }

    public static Packet icmpBuilder(MacAddress dst_hwaddr, Type type, byte code,
                                    Inet4Address src, Inet4Address dst, byte[] data) {
        ICMP icmp = new ICMP()
                .setType(type)
                .setCode(code);
        icmp.setData(data);

        IPv4 iPv4 = new IPv4()
                .setVersion((byte) 0x4)
                .setDiffServ((byte) 0x0)
                .setExpCon((byte) 0)
                .setIdentification((short) 29257)
                .setFlags((byte) 0x02)
                .setFragmentOffset((short) 0)
                .setTtl((byte) 64)
                .setProtocol(Protocol.ICMP)
                .setSourceAddress(src)
                .setDestinationAddress(dst);
        iPv4.setPacket(icmp);

        Ethernet ethernet = new Ethernet()
                .setDestinationMacAddress(dst_hwaddr)
                .setSourceMacAddress(StaticField.CURRENT_MAC_ADDRESS)
                .setEtherType(EtherType.IPV4)
                .setPadding(true);
        ethernet.setPacket(iPv4);
        return ethernet;
    }

}
