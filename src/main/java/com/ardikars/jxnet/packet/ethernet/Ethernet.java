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

package com.ardikars.jxnet.packet.ethernet;

import com.ardikars.jxnet.MacAddress;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.arp.ARP;
import com.ardikars.jxnet.packet.ip.IPv4;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Ethernet extends Packet {

	public static final int ETHERNET_HEADER_LENGTH = 14;
	public static final int VLAN_HEADER_LENGTH = 4;
	
	private MacAddress destinationMacAddress;
	private MacAddress sourceMacAddress;
	private byte priorityCodePoint;
	private byte canonicalFormatIndicator;
	private short vlanIdentifier;
	private EtherType etherType;
	private boolean padding;

	/**
	 * Ethernet paylaod
	 */
	private byte[] data;

	//private int checksum; //CRC32

	public Ethernet() {
		this.vlanIdentifier = (short) 0xffff;
	}
	
	public MacAddress getDestinationMacAddress() {
		return this.destinationMacAddress;
	}
	
	public Ethernet setDestinationMacAddress(MacAddress destinationMacAddress) {
		this.destinationMacAddress = destinationMacAddress;
        return this;
	}
	
	public MacAddress getSourceMacAddress() {
		return this.sourceMacAddress;
	}
	
	public Ethernet setSourceMacAddress(MacAddress sourceMacAddress) {
		this.sourceMacAddress = sourceMacAddress;
        return this;
	}
	
	public byte getPriorityCodePoint() {
		return (byte) (this.priorityCodePoint & 0x07);
	}
	
	public Ethernet setPriorityCodePoint(byte priorityCodePoint) {
		this.priorityCodePoint = (byte) (priorityCodePoint & 0x07);
        return this;
	}
	
	public byte getCanonicalFormatIndicator() {
		return (byte) (this.canonicalFormatIndicator & 0x01);
	}
	
	public Ethernet setCanonicalFormatIndicator(byte canonicalFormatIndicator) {
		this.canonicalFormatIndicator = (byte) (canonicalFormatIndicator & 0x01);
        return this;
	}
	
	public short getVlanIdentifier() {
		return (short) (this.vlanIdentifier & 0x0fff);
	}
	
	public Ethernet setVlanIdentifier(short vlanIdentifier) {
		this.vlanIdentifier = (short) (vlanIdentifier & 0x0fff);
        return this;
	}
	
	public EtherType getEtherType() {
		return this.etherType;
	}
	
	public Ethernet setEtherType(EtherType etherType) {
		this.etherType = etherType;
        return this;
	}
	
	public boolean getPadding() {
		return !((this.data.length + ETHERNET_HEADER_LENGTH + VLAN_HEADER_LENGTH) < 60);
	}
	
	public Ethernet setPadding(boolean padding) {
		this.padding = padding;
        return this;
	}

	public static Ethernet newInstance(byte[] bytes) {
		return Ethernet.newInstance(bytes, 0, bytes.length);
	}
	
	public static Ethernet newInstance(byte[] bytes, int offset, int length) {
		Ethernet ethernet = new Ethernet();
		ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
		byte[] MACBuffer = new byte[6];
		buffer.get(MACBuffer);
		ethernet.destinationMacAddress = MacAddress.valueOf(MACBuffer);
		buffer.get(MACBuffer);
		ethernet.sourceMacAddress = MacAddress.valueOf(MACBuffer);
		EtherType etherType = EtherType.getInstance(buffer.getShort());
		if (etherType == EtherType.DOT1Q_VLAN_TAGGED_FRAMES) {
			short tci = buffer.getShort();
			ethernet.priorityCodePoint = (byte) (tci >> 13 & 0x07);
			ethernet.canonicalFormatIndicator = (byte) (tci >> 14 & 0x01);
			ethernet.vlanIdentifier = (short) (tci & 0x0fff);
			etherType = EtherType.getInstance(buffer.getShort());
		} else {
			ethernet.vlanIdentifier = (short) 0xffff;
		}
		ethernet.etherType = etherType;
		if (ethernet.vlanIdentifier != (short) 0xffff) {
			ethernet.data = new byte[(bytes.length - (ETHERNET_HEADER_LENGTH + VLAN_HEADER_LENGTH))];
			System.arraycopy(bytes, (ETHERNET_HEADER_LENGTH + 4), ethernet.data,
					0, (bytes.length - (ETHERNET_HEADER_LENGTH + 4)));
		} else {
			ethernet.data = new byte[(bytes.length - ETHERNET_HEADER_LENGTH)];
			System.arraycopy(bytes, (ETHERNET_HEADER_LENGTH), ethernet.data,
					0, (bytes.length - (ETHERNET_HEADER_LENGTH)));
		}
		//ethernet.rawPacket = bytes;
		return ethernet;
	}

	@Override
	public byte[] getBytes() {
		int headerLength = ETHERNET_HEADER_LENGTH +
				((this.etherType == EtherType.DOT1Q_VLAN_TAGGED_FRAMES) ? VLAN_HEADER_LENGTH : 0) +
				((this.data == null) ? 0 : this.data.length);
		if (padding && headerLength < 60) {
			headerLength = 60;
		}
		byte[] newdata = new byte[headerLength];
		ByteBuffer buffer = ByteBuffer.wrap(newdata);
		buffer.put(this.destinationMacAddress.toBytes());
		buffer.put(this.sourceMacAddress.toBytes());
		if (this.vlanIdentifier != (short) 0xffff) {
			buffer.putShort(EtherType.DOT1Q_VLAN_TAGGED_FRAMES.getValue());
			buffer.putShort((short) (((this.priorityCodePoint << 13) & 0x07)
					| ((this.canonicalFormatIndicator << 14) & 0x01) | (this.vlanIdentifier & 0x0fff)));
		}
		buffer.putShort((short) (this.etherType.getValue() & 0xffff));
		if (this.data != null) {
			buffer.put(this.data);
		}
		if (padding && headerLength < 60) {
			Arrays.fill(newdata, buffer.position(), newdata.length, (byte) 0x0);
		}
		return newdata;
    }

	@Override
	public Packet getPacket() {
		if (etherType == null) return null;
		switch (etherType.getValue() & 0xffff) {
			case 0x0806: return ARP.newInstance(this.data);
			case 0x0800: return IPv4.newInstance(this.data);
			default: return null;
		}

	}

	@Override
	public Ethernet setPacket(Packet packet) {
		byte[] data = packet.getBytes();
		this.data = data;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append("[")
				.append("Destination: " + getDestinationMacAddress().toString())
				.append(", Source: " + getSourceMacAddress().toString());
		if (this.vlanIdentifier != (short) 0xffff) {
			sb.append(", Tag Control Information (Priority Code Point: " + getPriorityCodePoint())
					.append(", Canonical Format Indicator: " + getCanonicalFormatIndicator())
					.append(", Vlan Identifier: " + getVlanIdentifier())
					.append(")");
		}
		return sb.append(", Ether ICMPType: " + ((getEtherType() == null) ? "UNKNOWN" : getEtherType().getName()))
				.append("]").toString();
	}

}
