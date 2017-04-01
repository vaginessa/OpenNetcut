package com.ardikars.opennetcut.app.ids;

import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.PcapPktHdr;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.ethernet.EtherType;
import com.ardikars.jxnet.packet.ethernet.Ethernet;
import com.ardikars.jxnet.packet.arp.ARP;
import com.ardikars.jxnet.packet.arp.ARPOperationCode;
import com.ardikars.opennetcut.app.OUI;
import com.ardikars.opennetcut.app.StaticField;

import java.util.Map;

public class Produce <T> extends Thread {

    private T arg;
    private PcapPktHdr pktHdr;
    private Map<Class, Packet> packets;

    private Produce(T arg, PcapPktHdr pktHdr, Map<Class, Packet> packets) {
        this.arg = arg;
        this.pktHdr = pktHdr;
        this.packets = packets;
    }

    public static <T> Produce produce(T arg, PcapPktHdr pktHdr, Map<Class, Packet> packets) {
        return new Produce(arg, pktHdr, packets);
    }

    @Override
    public void run() {
        Ethernet ethernet = (Ethernet) packets.get(Ethernet.class);
        if (ethernet.getEtherType() != EtherType.ARP)
            return;
        ARP arp = (ARP) packets.get(ARP.class);

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

        if (arp.getOpCode() != ARPOperationCode.ARP_REPLY ||
                !ethDst.equals(StaticField.CURRENT_MAC_ADDRESS) ||
                tpa.equals(StaticField.CURRENT_MAC_ADDRESS)) {
            return;
        }
        // Check

        if (!ethSrc.equals(sha) || !ethDst.equals(tha)) {
            INVALID_PACKET = 1;
        }

        String shaCache = StaticField.ARP_CACHE.get(spa.toString());
        if  (shaCache == null) {
            StaticField.ARP_CACHE.put(spa.toString(), sha.toString());
        } else {
            if (!sha.toString().equals(shaCache)) {
                UNCONSISTENT_SHA = 1.0;
            }
            StaticField.ARP_CACHE.put(spa.toString(), sha.toString());
        }

        UNPADDED_ETHERNET_FRAME = (pktHdr.getCapLen() < 60 ? 1 : 0);

        if (OUI.searchVendor(arp.getSenderHardwareAddress().toString()).equals("")) {
            UNKNOWN_OUI = 1;
        }

        Long epochTimeCache = StaticField.EPOCH_TIME.get(spa.toString());
        if (epochTimeCache == null || epochTimeCache == 0) {
            StaticField.EPOCH_TIME.put(spa.toString(), Long.valueOf(System.currentTimeMillis()));
        } else {
            long time = (System.currentTimeMillis() - (long) epochTimeCache);
            if (time > 2000) time = 0;
            EPOCH_TIME = (( time / 5000.0));
            StaticField.EPOCH_TIME.put(spa.toString(), Long.valueOf(System.currentTimeMillis()));
        }

        ICMPTrap.start(sha, StaticField.CURRENT_INET4ADDRESS, MacAddress.ZERO.toBytes());

        System.out.println("=================================");
        System.out.println("INVALID_PACKET          : " + INVALID_PACKET);
        System.out.println("UNCONSISTENT_SHA        : " + UNCONSISTENT_SHA);
        System.out.println("UNPADDED_ETHERNET_FRAME : " + UNPADDED_ETHERNET_FRAME);
        System.out.println("UNKNOWN_OUI             : " + UNKNOWN_OUI);
        System.out.println("EPOCH_TIME              : " + EPOCH_TIME);
        System.out.println("=================================");
    }

}

