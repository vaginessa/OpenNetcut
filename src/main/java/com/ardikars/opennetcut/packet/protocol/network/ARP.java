package com.ardikars.opennetcut.packet.protocol.network;

import java.nio.ByteBuffer;

public class ARP {
		
    public static final short HW_TYPE_ETHERNET = 0x01;

    public static final short PROTO_TYPE_IP = 0x0800;

    public static final short OP_REQUEST = 0x01;
    public static final short OP_REPLY = 0x02;
    public static final short OP_RARP_REQUEST = 0x03;
    public static final short OP_RARP_REPLY = 0x04;
    
    public static final short ARP_HEADER_LENGTH = 28;
	
	private short hardwareType;
	private short protocolType;
	private byte hardwareAddressLength;
	private byte protocolAddressLength;
	private short opCode;
	private byte[] senderHardwareAddress;
	private byte[] senderProtocolAddress;
	private byte[] targetHardwareAddress;
	private byte[] targetProtocolAddress;
	
	public ARP() {
	}
	
	public ARP(final byte[] data, final int offset, final int length) {
		final ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
            this.hardwareType = bb.getShort();
            this.protocolType = bb.getShort();
            this.hardwareAddressLength = bb.get();
            this.protocolAddressLength = bb.get();
            this.opCode = bb.getShort();
            this.senderHardwareAddress = new byte[0xff & this.hardwareAddressLength];
            bb.get(this.senderHardwareAddress, 0, this.senderHardwareAddress.length);
            this.senderProtocolAddress = new byte[0xff & this.protocolAddressLength];
            bb.get(this.senderProtocolAddress, 0, this.senderProtocolAddress.length);
            this.targetHardwareAddress = new byte[0xff & this.hardwareAddressLength];
            bb.get(this.targetHardwareAddress, 0, this.targetHardwareAddress.length);
            this.targetProtocolAddress = new byte[0xff & this.protocolAddressLength];
            bb.get(this.targetProtocolAddress, 0, this.targetProtocolAddress.length);
	}
	
    public short getHardwareType() {
        return hardwareType;
    }

    public short getProtocolType() {
        return protocolType;
    }

    public byte getHardwareAddressLength() {
        return hardwareAddressLength;
    }

    public byte getProtocolAddressLength() {
        return protocolAddressLength;
    }

    public short getOpCode() {
        return opCode;
    }

    public byte[] getSenderHardwareAddress() {
        return senderHardwareAddress;
    }

    public byte[] getSenderProtocolAddress() {
        return senderProtocolAddress;
    }

    public byte[] getTargetHardwareAddress() {
        return targetHardwareAddress;
    }

    public byte[] getTargetProtocolAddress() {
        return targetProtocolAddress;
    }

    public ARP setHardwareType(short hardwareType) {
        this.hardwareType = hardwareType;
        return this;
    }

    public ARP setProtocolType(short protocolType) {
        this.protocolType = protocolType;
        return this;
    }

    public ARP setHardwareAddressLength(byte hardwareAddressLength) {
        this.hardwareAddressLength = hardwareAddressLength;
        return this;
    }

    public ARP setProtocolAddressLength(byte protocolAddressLength) {
        this.protocolAddressLength = protocolAddressLength;
        return this;
    }

    public ARP setOpCode(short opCode) {
        this.opCode = opCode;
        return this;
    }

    public ARP setSenderHardwareAddress(byte[] senderHardwareAddress) {
        this.senderHardwareAddress = senderHardwareAddress;
        return this;
    }

    public ARP setSenderProtocolAddress(byte[] senderProtocolAddress) {
        this.senderProtocolAddress = senderProtocolAddress;
        return this;
    }

    public ARP setTargetHardwareAddress(byte[] targetHardwareAddress) {
        this.targetHardwareAddress = targetHardwareAddress;
        return this;
    }

    public ARP setTargetProtocolAddress(byte[] targetProtocolAddress) {
        this.targetProtocolAddress = targetProtocolAddress;
        return this;
    }
	
    public byte[] toByteArray() {
	final int length = 8 + 2 * (0xff & this.hardwareAddressLength) + 2
                * (0xff & this.protocolAddressLength);
        final byte[] data = new byte[length];
        final ByteBuffer bb = ByteBuffer.wrap(data);
        bb.putShort(this.hardwareType);
        bb.putShort(this.protocolType);
        bb.put(this.hardwareAddressLength);
        bb.put(this.protocolAddressLength);
        bb.putShort(this.opCode);
        bb.put(this.senderHardwareAddress, 0, 0xff & this.hardwareAddressLength);
        bb.put(this.senderProtocolAddress, 0, 0xff & this.protocolAddressLength);
        bb.put(this.targetHardwareAddress, 0, 0xff & this.hardwareAddressLength);
        bb.put(this.targetProtocolAddress, 0, 0xff & this.protocolAddressLength);
        return data;
    }

}