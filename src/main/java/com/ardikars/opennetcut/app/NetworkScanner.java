package com.ardikars.opennetcut.app;

import com.ardikars.opennetcut.packet.protocol.datalink.Ethernet;
import com.ardikars.opennetcut.packet.protocol.network.ARP;
import java.util.List;

public class NetworkScanner extends Thread {

    private List<byte[]> ips;
    private byte[] myMac;
    private byte[] myIp;
    
    public NetworkScanner() {
    }
    
    @Override
    public void run() {
        Ethernet ethernet = new Ethernet()
                .setDestinationMACAddress(new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff})
                .setSourceMACAddress(myMac)
                .setEtherType(Ethernet.EtherType.ARP.toShort());
        ARP arp = new ARP()
                .setHardwareType(ARP.HW_TYPE_ETHERNET)
                .setProtocolType(ARP.PROTO_TYPE_IP)
                .setHardwareAddressLength((byte) 6)
                .setProtocolAddressLength((byte) 4)
                .setOpCode(ARP.OP_RARP_REQUEST)
                .setSenderHardwareAddress(myMac)
                .setSenderProtocolAddress(myIp)
                .setTargetHardwareAddress(new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        for(int i=0; i<ips.size(); i++) {
            arp.setTargetProtocolAddress(ips.get(i));
        }
    }
}
