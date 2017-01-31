package com.ardikars.opennetcut.packet;

import com.ardikars.jxnet.Pcap;
import com.ardikars.jxnet.PcapPktHdr;

public interface PacketHandler {
    
    void nextPacket(int no, PcapPktHdr pktHdr, Packet packet);
    
}
