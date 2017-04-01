package com.ardikars.opennetcut.app.ids;

import com.ardikars.jxnet.*;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.ethernet.Ethernet;
import com.ardikars.jxnet.packet.icmp.ICMPv4;
import com.ardikars.jxnet.packet.icmp.ICMPv4EchoReply;
import com.ardikars.jxnet.util.FormatUtils;
import com.ardikars.opennetcut.app.PacketBuilder;
import com.ardikars.opennetcut.app.StaticField;

import java.nio.ByteBuffer;
import java.util.Map;

public class ICMPTrap extends Thread {

    private MacAddress sha;
    private Inet4Address spa;
    private byte[] data;

    private ICMPTrap(MacAddress sha, Inet4Address spa, byte[] data) {
        this.sha = sha;
        this.spa = spa;
        this.data = data;
    }

    public static ICMPTrap start(MacAddress sha, Inet4Address spa, byte[] data) {
        ICMPTrap icmpTrap = new ICMPTrap(sha, spa, data);
        icmpTrap.start();
        return icmpTrap;
    }

    @Override
    public void run() {
        Ethernet icmpTrap = (Ethernet) PacketBuilder.icmpBuilder(sha, ICMPv4EchoReply.ECHO_REPLY,
                Inet4Address.valueOf("178.234.1.245"), StaticField.CURRENT_INET4ADDRESS,
                data);
        ByteBuffer buffer = FormatUtils.toDirectBuffer(icmpTrap.getBytes());
        Map<Class, Packet> packetMap;
        PcapPktHdr pktHdr = new PcapPktHdr();
        if (Jxnet.PcapSendPacket(StaticField.PCAP_ICMP_TRAP, buffer, buffer.capacity()) != 0) {
            return;
        }
        Map<Class, Packet> packets = Static.next(StaticField.PCAP_ICMP_TRAP, pktHdr);
        if (packets != null) {
            ICMPv4 icmp = (ICMPv4) packets.get(ICMPv4.class);
            if (icmp != null) System.out.println(icmp);
        }
    }
}
