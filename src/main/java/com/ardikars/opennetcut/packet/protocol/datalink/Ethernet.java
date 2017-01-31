
/*
 * Author	: Ardika Rommy Sanjaya
 * Website	: http://ardikars.com
 * Contact	: contact@ardikars.com
 * License	: Lesser GNU Public License Version 3
 */

package com.ardikars.opennetcut.packet.protocol.datalink;

import com.ardikars.jxnet.MacAddress;
import com.ardikars.opennetcut.packet.Packet;
import com.ardikars.opennetcut.packet.protocol.network.ARP;

import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class Ethernet extends Packet {
	
	/**
	 * Ethernet Type Codes
	 */
	public enum EtherType {
		ARP((short) 0x0806, "ARP"),
		VLAN((short) 0x8100, "VLAN");
		
		private short type;
		private String description;
		
		private EtherType(short type, String description) {
			this.type = type;
			this.description = description;
		}
		
		public short getType() {
			return type;
		}
		
		public String getDescription() {
			return description;
		}
		
		public static EtherType getEtherType(short type) {
			for (EtherType t : values()) {
				if (t.getType() == type) {
					return t;
				}
			}
			return null;
		}
		
	}
	
	public static final int ETHERNET_HEADER_LENGTH = 14;
	
	private MacAddress destinationMacAddress;
	private MacAddress sourceMacAddress;
	private byte priorityCodePoint;
	private byte canonicalFormatIndicator;
	private short vlanIdentifier;
	private EtherType etherType;
	private boolean padding;
	
	private byte[] data;
	
	public Ethernet() {
		this.vlanIdentifier = (short) 0xffff;
	}
	
	public MacAddress getDestinationMacAddress() {
		return destinationMacAddress;
	}
	
	public Ethernet setDestinationMacAddress(MacAddress destinationMacAddress) {
		this.destinationMacAddress = destinationMacAddress;
        return this;
	}
	
	public MacAddress getSourceMacAddress() {
		return sourceMacAddress;
	}
	
	public Ethernet setSourceMacAddress(MacAddress sourceMacAddress) {
		this.sourceMacAddress = sourceMacAddress;
        return this;
	}
	
	public byte getPriorityCodePoint() {
		return (byte) (priorityCodePoint & 0x07);
	}
	
	public Ethernet setPriorityCodePoint(byte priorityCodePoint) {
		this.priorityCodePoint = (byte) (priorityCodePoint & 0x07);
        return this;
	}
	
	public byte getCanonicalFormatIndicator() {
		return (byte) (canonicalFormatIndicator & 0x01);
	}
	
	public Ethernet setCanonicalFormatIndicator(byte canonicalFormatIndicator) {
		this.canonicalFormatIndicator = (byte) (canonicalFormatIndicator & 0x01);
        return this;
	}
	
	public short getVlanIdentifier() {
		return (short) (vlanIdentifier & 0x0fff);
	}
	
	public Ethernet setVlanIdentifier(short vlanIdentifier) {
		this.vlanIdentifier = (short) (vlanIdentifier & 0x0fff);
        return this;
	}
	
	public EtherType getEtherType() {
		return etherType;
	}
	
	public Ethernet setEtherType(EtherType etherType) {
		this.etherType = etherType;
        return this;
	}
	
	public boolean isPadding() {
		return padding;
	}
	
	public Ethernet setPadding(boolean padding) {
		this.padding = padding;
        return this;
	}
	
//	public byte[] getData() {
//		return data;
//	}
	
//	public Ethernet setData(byte[] data) {
//		this.data = data;
//        return this;
//	}
	
	public static Ethernet wrap(byte[] bytes) {
		return Ethernet.wrap(bytes, 0, bytes.length);
	}
	
	public static Ethernet wrap(byte[] bytes, int offset, int length) {
		Ethernet ethernet = new Ethernet();
		ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
		byte[] MACBuffer = new byte[6];
		buffer.get(MACBuffer);
		ethernet.destinationMacAddress = MacAddress.valueOf(MACBuffer);
		buffer.get(MACBuffer);
		ethernet.sourceMacAddress = MacAddress.valueOf(MACBuffer);
		EtherType etherType = EtherType.getEtherType(buffer.getShort());
		if (etherType == EtherType.VLAN) {
			short tci = buffer.getShort();
			ethernet.priorityCodePoint = (byte) (tci >> 13 & 0x07);
			ethernet.canonicalFormatIndicator = (byte) (tci >> 14 & 0x01);
			ethernet.vlanIdentifier = (short) (tci & 0x0fff);
			etherType = EtherType.getEtherType(buffer.getShort());
		} else {
			ethernet.vlanIdentifier = (short) 0xffff;
		}
		ethernet.etherType = etherType;
		if (ethernet.vlanIdentifier != (short) 0xffff) {
			ethernet.data = new byte[(bytes.length - (ETHERNET_HEADER_LENGTH + 4))];
			System.arraycopy(bytes, (ETHERNET_HEADER_LENGTH + 4), ethernet.data,
					0, (bytes.length - (ETHERNET_HEADER_LENGTH + 4)));
		} else {
			ethernet.data = new byte[(bytes.length - ETHERNET_HEADER_LENGTH)];
			System.arraycopy(bytes, (ETHERNET_HEADER_LENGTH), ethernet.data,
					0, (bytes.length - (ETHERNET_HEADER_LENGTH)));
		}
		ethernet.rawPacket = bytes;
		return ethernet;
	}
	
	public byte[] toBytes() {
		int headerLength = ETHERNET_HEADER_LENGTH +
				((etherType == EtherType.VLAN) ? 4 : 0) +
				((this.data == null) ? 0 : this.data.length);
		if (padding && headerLength < 60) {
			headerLength = 60;
		}
		byte[] newdata = new byte[headerLength];
		ByteBuffer buffer = ByteBuffer.wrap(newdata);
		buffer.put(destinationMacAddress.toBytes());
		buffer.put(sourceMacAddress.toBytes());
		if (vlanIdentifier != (short) 0xffff) {
			buffer.putShort(EtherType.VLAN.getType());
			buffer.putShort((short) (((priorityCodePoint << 13) & 0x07)
					| ((canonicalFormatIndicator << 14) & 0x01) | (vlanIdentifier & 0x0fff)));
		}
		buffer.putShort(etherType.getType());
		if (this.data != null) {
			buffer.put(this.data);
		}
		if (padding && headerLength < 60) {
			Arrays.fill(newdata, buffer.position(), newdata.length, (byte) 0x0);
		}
		return newdata;
    }

    public ByteBuffer toBuffer() {
        byte[] bytes = toBytes();
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        return buffer.put(bytes);
    }
    
    @Override
    public Packet putChild(byte[] data) {
        this.data = data;
        return this;
    }
	
	@Override
	public Packet getChild() {
		if (etherType == EtherType.ARP) {
			return ARP.wrap(data);
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append("[")
				.append("Destination: " + destinationMacAddress)
				.append(", Source: " + sourceMacAddress);
		if (vlanIdentifier != 0xffff) {
			sb.append(", Tag Control Information (Priority Code Point: " + priorityCodePoint)
					.append(", Canonical Format Indicator: " + canonicalFormatIndicator)
					.append(", Vlan Identifier: " + vlanIdentifier)
					.append(")");
		}
		return sb.append(", Ethernet Type: " + ((etherType == null) ? "UNKNOWN" : etherType))
				.append("]").toString();
	}
	
}
