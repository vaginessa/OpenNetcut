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

package com.ardikars.jxnet.packet.protocol.network.ip;

import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.packet.Packet;
import com.ardikars.jxnet.packet.protocol.network.icmp.ICMP;
import com.ardikars.jxnet.packet.protocol.transport.TCP;

import java.nio.ByteBuffer;

public class IPv4 extends Packet {
	
	public static final int IPV4_HEADER_LENGTH = 20;
	
	private byte version;
	private byte headerLength = (byte) 5;
	private byte diffServ;
	private byte expCon;
	private short totalLength;
	private short identification;
	private byte flags;
	private short fragmentOffset;
	private byte ttl;
	private Protocol protocol;
	private short checksum;
	private Inet4Address sourceAddress;
	private Inet4Address destinationAddress;
	private byte[] options;
	
	private byte[] data;

	public IPv4() {

	}

	public byte getVersion() {
		return (byte) (this.version & 0xf);
	}
	
	public IPv4 setVersion(byte version) {
		this.version = (byte) (version & 0xf);
		return this;
	}
	
	public byte getHeaderLength() {
		return (byte) (this.headerLength & 0xf);
	}
	
	public IPv4 setHeaderLength(byte headerLength) {
		this.headerLength = (byte) (headerLength & 0xf);
		return this;
	}
	
	public byte getDiffServ() {
		return (byte) (this.diffServ & 0x3f);
	}
	
	public IPv4 setDiffServ(byte diffServ) {
		this.diffServ = (byte) (diffServ & 0x3f);
		return this;
	}
	
	public byte getExpCon() {
		return (byte) (this.expCon & 0x3);
	}
	
	public IPv4 setExpCon(byte expCon) {
		this.expCon = (byte) (expCon & 0x3);
		return this;
	}
	
	public short getTotalLength() {
		return (short) (this.totalLength & 0xffff);
	}
	
	public IPv4 setTotalLength(short totalLength) {
		this.totalLength = (short) (totalLength & 0xffff);
		return this;
	}
	
	public short getIdentification() {
		return (short) (this.identification & 0xffff);
	}
	
	public IPv4 setIdentification(short identification) {
		this.identification = (short) (identification & 0xffff);
		return this;
	}
	
	public byte getFlags() {
		return (byte) (this.flags & 0x7);
	}
	
	public IPv4 setFlags(byte flags) {
		this.flags = (byte) (flags & 0x7);
		return this;
	}
	
	public short getFragmentOffset() {
		return (short) (this.fragmentOffset & 0x1fff);
	}
	
	public IPv4 setFragmentOffset(short fragmentOffset) {
		this.fragmentOffset = (short) (fragmentOffset & 0x1fff);
		return this;
	}
	
	public byte getTtl() {
		return (byte) (ttl & 0xff);
	}
	
	public IPv4 setTtl(byte ttl) {
		this.ttl = (byte) (ttl & 0xff);
		return this;
	}
	
	public Protocol getProtocol() {
		return this.protocol;
	}
	
	public IPv4 setProtocol(Protocol protocol) {
		this.protocol = protocol;
		return this;
	}
	
	public short getChecksum() {
		return (short) (this.checksum & 0xffff);
	}
	
	public IPv4 setChecksum(short checksum) {
		this.checksum = (short) (checksum & 0xffff);
		return this;
	}
	
	public Inet4Address getSourceAddress() {
		return this.sourceAddress;
	}
	
	public IPv4 setSourceAddress(Inet4Address sourceAddress) {
		this.sourceAddress = sourceAddress;
		return this;
	}
	
	public Inet4Address getDestinationAddress() {
		return this.destinationAddress;
	}
	
	public IPv4 setDestinationAddress(Inet4Address destinationAddress) {
		this.destinationAddress = destinationAddress;
		return this;
	}
	
	public byte[] getOptions() {
		return this.options;
	}
	
	public IPv4 setOptions(byte[] options) {
		this.headerLength = (byte) (this.headerLength + options.length);
		this.options = options;
		return this;
	}
	
	public static IPv4 newInstance(byte[] bytes) {
		return IPv4.newInstance(bytes, 0, bytes.length);
	}
	
	public static IPv4 newInstance(byte[] bytes, int offset, int length) {
		IPv4 ipv4 = new IPv4();
		ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
		ipv4.version = buffer.get();
		ipv4.headerLength = (byte) (ipv4.version & 0xf);
		ipv4.version = (byte) ((ipv4.version >> 4) & 0xf);
		byte tmp = buffer.get();
		ipv4.diffServ = (byte) ((tmp >> 2) & 0x3f);
		ipv4.expCon = (byte) (tmp & 0x3);
		ipv4.totalLength = buffer.getShort();
		ipv4.identification = buffer.getShort();
		short sscratch = buffer.getShort();
		ipv4.flags = (byte) (sscratch >> 13 & 0x7);
		ipv4.fragmentOffset = (short) (sscratch & 0x1fff);
		ipv4.ttl = buffer.get();
		ipv4.protocol = Protocol.getInstance(buffer.get());
		ipv4.checksum = buffer.getShort();
		byte[] IPv4Buffer = new byte[4];
		buffer.get(IPv4Buffer);
		ipv4.sourceAddress = Inet4Address.valueOf(IPv4Buffer);
		
		byte[] IPv4Buffer2 = new byte[4];
		buffer.get(IPv4Buffer2);
		ipv4.destinationAddress = Inet4Address.valueOf(IPv4Buffer2);
		if (ipv4.headerLength > 5) {
			int optionsLength = (ipv4.headerLength - 5) * 4;
			ipv4.options = new byte[optionsLength];
			buffer.get(ipv4.options);
			ipv4.data = new byte[(bytes.length - (optionsLength + IPV4_HEADER_LENGTH))];
			System.arraycopy(bytes, (IPV4_HEADER_LENGTH + optionsLength), ipv4.data,
					0, (bytes.length - (optionsLength + IPV4_HEADER_LENGTH)));
		} else {
			ipv4.data = new byte[(bytes.length - (IPV4_HEADER_LENGTH))];
			System.arraycopy(bytes, (IPV4_HEADER_LENGTH), ipv4.data,
					0, (bytes.length - IPV4_HEADER_LENGTH));
		}
		return ipv4;
	}

	@Override
	public IPv4 setPacket(Packet packet) {
		byte[] data = packet.getBytes();
		this.totalLength = (short) (this.headerLength * 4 + (data == null ? 0
				: data.length));
		this.data = data;
		return this;
	}

	@Override
	public Packet getPacket() {
		if (getProtocol() == null) return null;
		switch (getProtocol().getValue()) {
			case 1: return ICMP.newInstance(this.data);
			case 6: return TCP.newInstance(this.data);
		}
		return null;
	}

	@Override
	public byte[] getBytes() {
		byte[] data = new byte[IPV4_HEADER_LENGTH +
				((this.data == null) ? 0 : this.data.length) +
				((this.headerLength > 5) ? this.options.length * 5 : 0)];
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.put((byte) ((this.version & 0xf) << 4 | this.headerLength & 0xf));
		buffer.put((byte) (((this.diffServ << 2) & 0x3f) | this.expCon & 0x3));
		buffer.putShort(this.totalLength);
		buffer.putShort(this.identification);
		buffer.putShort((short) ((this.flags & 0x7) << 13 | this.fragmentOffset & 0x1fff));
		buffer.put(this.ttl);
		buffer.put(this.protocol.getValue());
		buffer.putShort((byte) (this.checksum & 0xffff));
		buffer.put(this.sourceAddress.toBytes());
		buffer.put(this.destinationAddress.toBytes());
		if (this.options != null && this.headerLength > 5) {
			buffer.put(this.options);
		}
		if (this.checksum == 0) {
			buffer.rewind();
			int accumulation = 0;
			for (int i = 0; i < this.headerLength * 2; ++i) {
				accumulation += 0xffff & buffer.getShort();
			}
			accumulation = (accumulation >> 16 & 0xffff)
					+ (accumulation & 0xffff);
			this.checksum = (short) (~accumulation & 0xffff);
			buffer.putShort(10, (short) (this.checksum & 0xffff));
		}
		if (this.data != null) {
			buffer.put(this.data);
		}
		return data;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("[")
				.append("Version: " + getVersion())
				.append(", Internet Header Length: " + getHeaderLength())
				.append(", Differentiated Services Code Point: " + getDiffServ())
				.append(", Explicit Congestion Notification: " + getExpCon())
				.append(", Total Length: " + getTotalLength())
				.append(", Identification: " + getIdentification())
				.append(", Flags: " + getFlags())
				.append(", Fragment Offset: " + getFragmentOffset())
				.append(", Time To Live: " + getTtl())
				.append(", Protocol: " + getProtocol().toString())
				.append(", Header Checksum: " + getClass())
				.append(", Source Address: " + getSourceAddress().toString())
				.append(", Destination Address: " + getDestinationAddress().toString())
				.append(", Options: " + getOptions())
				.append("]").toString();
	}
}
