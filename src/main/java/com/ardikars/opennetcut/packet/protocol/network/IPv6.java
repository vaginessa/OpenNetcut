package com.ardikars.opennetcut.packet.protocol.network;

import java.nio.ByteBuffer;

public class IPv6 {

    public static final byte IPV6_FIXED_HEADER_LENGTH = 40; // bytes

    public static final byte PROTOCOL_TCP = 0x6;
    public static final byte PROTOCOL_UDP = 0x11;
    public static final byte PROTOCOL_ICMP6 = 0x3A;
    public static final byte PROTOCOL_HOPOPT = 0x00;
    public static final byte PROTOCOL_ROUTING = 0x2B;
    public static final byte PROTOCOL_FRAG = 0x2C;
    public static final byte PROTOCOL_ESP = 0x32;
    public static final byte PROTOCOL_AH = 0x33;
    public static final byte PROTOCOL_DSTOPT = 0x3C;
    
    public static final int IPV6_BYTE_LENGTH = 16;
    
    private byte version;
    private byte trafficClass;
    private int flowLabel;
    private short payloadLength;
    private byte nextHeader;
    private byte hopLimit;
    private byte[] sourceAddress;
    private byte[] destinationAddress;;

    private byte[] payload;
    
    public IPv6() {
    	this.version = 6;
    }
    
    public IPv6(final byte[] data, final int offset, final int length) {
    	ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
    	int iscratch = bb.getInt();
        this.version = (byte) (iscratch >> 28 & 0xf);
        this.trafficClass = (byte) (iscratch >> 20 & 0xff);
        this.flowLabel = iscratch & 0xfffff;
        this.payloadLength = bb.getShort();
        this.nextHeader = bb.get();
        this.hopLimit = bb.get();
        bb.get(this.sourceAddress, 0, IPv6.IPV6_BYTE_LENGTH);
        bb.get(this.destinationAddress, 0, IPv6.IPV6_BYTE_LENGTH);
    }
    
    public byte getVersion() {
        return version;
    }
    
    public byte getTrafficClass() {
        return trafficClass;
    }
    
    public int getFlowLabel() {
        return flowLabel;
    }
    
    public short getPayloadLength() {
       return payloadLength;
    }
    
    public byte getNextHeader() {
        return nextHeader;
    }
    
    public byte getHopLimit() {
        return hopLimit;
    }
    
    public byte[] getSourceAddress() {
        return sourceAddress;
    }
    
    public byte[] getDestinationAddress() {
        return destinationAddress;
    }
    
    public byte[] getPayload() {
        return payload;
    }
    
    public IPv6 setVersion(byte version) {
        this.version = version;
        return this;
    }
    
    public IPv6 setTrafficClass(byte trafficClass) {
        this.trafficClass = trafficClass;
        return this;
    }
    
    public IPv6 setFlowLabel(int flowLabel) {
        this.flowLabel = flowLabel;
        return this;
    }
    
    public IPv6 setNextHeader(byte nextHeader) {
        this.nextHeader = nextHeader;
        return this;
    }
    
    public IPv6 setHopLimit(byte hopLimit) {
        this.hopLimit = hopLimit;
        return this;
    }
    
    public IPv6 setSourceAddress(byte[] sourceAddress) {
        this.sourceAddress = sourceAddress;
        return this;
    }
    
    public IPv6 setDestinationAddress(byte[] destinationAddress) {
        this.destinationAddress = destinationAddress;
        return this;
    }
    
    public boolean setPayload(byte[] payload) {
        if(payload != null) {
            this.payload = payload;
            return true;
        }
    	return false;
    }
    
    public byte[] toByteArray() {
    	int length = IPv6.IPV6_FIXED_HEADER_LENGTH + 
                (this.payload == null ? 0 : this.payload.length);
    	byte[] data = new byte[length];
    	ByteBuffer bb = ByteBuffer.wrap(data);
    	bb.putInt((this.version & 0xf) << 28 | (this.trafficClass & 0xff) << 20 | this.flowLabel & 0xfffff);
    	bb.putShort(this.payloadLength);
    	bb.put(this.nextHeader);
        bb.put(this.hopLimit);
        bb.put(this.sourceAddress, 0, IPv6.IPV6_BYTE_LENGTH);
        bb.put(this.destinationAddress, 0, IPv6.IPV6_BYTE_LENGTH);
    	if(this.payload != null) {
            bb.put(this.payload);
    	}
        return data;
    }
    
}
