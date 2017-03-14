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

package com.ardikars.jxnet.packet.protocol.lan.arp;

import com.ardikars.jxnet.DataLinkType;
import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.protocol.datalink.ethernet.EtherType;

import java.nio.ByteBuffer;

public class ARP extends Packet {  

    public static final short ARP_HEADER_LENGTH = 28;
	
	private DataLinkType hardwareType;
	private EtherType protocolType;
	private byte hardwareAddressLength;
	private byte protocolAddressLength;
	private OperationCode opCode;
	private MacAddress senderHardwareAddress;
	private Inet4Address senderProtocolAddress;
	private MacAddress targetHardwareAddress;
	private Inet4Address targetProtocolAddress;
	
	public ARP() {
	}
    
    public static ARP newInstance(byte[] bytes) {
		return ARP.newInstance(bytes, 0, bytes.length);
	}
	
	public static ARP newInstance(byte[] bytes, int offset, int length) {
        final ByteBuffer bb = ByteBuffer.wrap(bytes, offset, length);
        ARP arp = new ARP();
        arp.hardwareType = DataLinkType.valueOf(bb.getShort());
        arp.protocolType = EtherType.getInstance(bb.getShort());
        arp.hardwareAddressLength = bb.get();
        arp.protocolAddressLength = bb.get();
        arp.opCode = OperationCode.getInstance(bb.getShort());
        
        byte[] tmphw = new byte[0xff & arp.hardwareAddressLength];
        bb.get(tmphw);
        arp.senderHardwareAddress = MacAddress.valueOf(tmphw);
       
        byte[] tmpip = new byte[0xff & arp.protocolAddressLength];
        bb.get(tmpip);
        arp.senderProtocolAddress = Inet4Address.valueOf(tmpip);
        
        tmphw = new byte[0xff & arp.hardwareAddressLength];
        bb.get(tmphw);
        arp.targetHardwareAddress = MacAddress.valueOf(tmphw);
        
        tmpip = new byte[0xff & arp.protocolAddressLength];
        bb.get(tmpip);
        arp.targetProtocolAddress = Inet4Address.valueOf(tmpip);
        return arp;
    }

    public DataLinkType getHardwareType() {
        return this.hardwareType;
    }

    public ARP setHardwareType(DataLinkType hardwareType) {
        this.hardwareType = hardwareType;
        return this;
    }

    public EtherType getProtocolType() {
        return this.protocolType;
    }

    public ARP setProtocolType(EtherType protocolType) {
        this.protocolType = protocolType;
        return this;
    }

    public byte getHardwareAddressLength() {
        return (byte) (this.hardwareAddressLength & 0xff);
    }

    public ARP setHardwareAddressLength(byte hardwareAddressLength) {
        this.hardwareAddressLength = hardwareAddressLength;
        return this;
    }

    public byte getProtocolAddressLength() {
        return (byte) (this.protocolAddressLength & 0xff);
    }

    public ARP setProtocolAddressLength(byte protocolAddressLength) {
        this.protocolAddressLength = protocolAddressLength;
        return this;
    }

    public OperationCode getOpCode() {
        return this.opCode;
    }

    public ARP setOpCode(OperationCode opCode) {
        this.opCode = opCode;
        return this;
    }

    public MacAddress getSenderHardwareAddress() {
        return this.senderHardwareAddress;
    }

    public ARP setSenderHardwareAddress(MacAddress senderHardwareAddress) {
        this.senderHardwareAddress = senderHardwareAddress;
        return this;
    }

    public Inet4Address getSenderProtocolAddress() {
        return this.senderProtocolAddress;
    }

    public ARP setSenderProtocolAddress(Inet4Address senderProtocolAddress) {
        this.senderProtocolAddress = senderProtocolAddress;
        return this;
    }

    public MacAddress getTargetHardwareAddress() {
        return this.targetHardwareAddress;
    }

    public ARP setTargetHardwareAddress(MacAddress targetHardwareAddress) {
        this.targetHardwareAddress = targetHardwareAddress;
        return this;
    }

    public Inet4Address getTargetProtocolAddress() {
        return this.targetProtocolAddress;
    }

    public ARP setTargetProtocolAddress(Inet4Address targetProtocolAddress) {
        this.targetProtocolAddress = targetProtocolAddress;
        return this;
    }

    @Override
    public void setPacket(Packet packet) {
        return;
    }

    @Override
    public Packet getPacket() {
        return null;
    }

    @Override
    public void setData(byte[] data) {
        return;
    }

    @Override
    public byte[] getData() {
        return null;
    }

    @Override
    public byte[] getBytes() {
	final int length = 8 + 2 * (0xff & this.hardwareAddressLength) + 2
                * (0xff & this.protocolAddressLength);
        if (length != ARP_HEADER_LENGTH) {
            // throw exception
        }
        final byte[] data = new byte[length];
        final ByteBuffer bb = ByteBuffer.wrap(data);
        bb.putShort((short) (this.hardwareType.getValue() & 0xffff));
        bb.putShort(this.protocolType.getValue());
        bb.put(this.hardwareAddressLength);
        bb.put(this.protocolAddressLength);
        bb.putShort(this.opCode.getValue());
        bb.put(this.senderHardwareAddress.toBytes(), 0, 0xff & this.hardwareAddressLength);
        bb.put(this.senderProtocolAddress.toBytes(), 0, 0xff & this.protocolAddressLength);
        bb.put(this.targetHardwareAddress.toBytes(), 0, 0xff & this.hardwareAddressLength);
        bb.put(this.targetProtocolAddress.toBytes(), 0, 0xff & this.protocolAddressLength);
        return data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Hw Type: ").append(getHardwareType().toString());
        sb.append(", Proto Type: ").append(getProtocolType().toString());
        sb.append(", Hw Addr Length: ").append(getHardwareAddressLength());
        sb.append(", Proto Addr length: ").append(getProtocolAddressLength());
        sb.append(", OpCode: ").append(getOpCode().toString());
        sb.append(", SHA: ").append(getSenderHardwareAddress().toString());
        sb.append(", SPA: ").append(getSenderProtocolAddress().toString());
        sb.append(", THA: ").append(getTargetHardwareAddress().toString());
        sb.append(", TPA: ").append(getTargetProtocolAddress().toString())
                .append("]");
        return sb.toString();
    }

}
