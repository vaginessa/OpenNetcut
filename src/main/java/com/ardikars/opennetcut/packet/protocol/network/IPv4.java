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

package com.ardikars.opennetcut.packet.protocol.network;

import com.ardikars.jxnet.Inet4Address;
import com.ardikars.opennetcut.packet.Packet;
import com.ardikars.opennetcut.packet.protocol.datalink.Ethernet;
import com.ardikars.opennetcut.packet.protocol.transport.TCP;

import java.nio.ByteBuffer;

public class IPv4 extends IP {
	
	public static final int IPV4_HEADER_LENGTH = 20;
	
	private byte version;
	private byte headerLength;
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
	
	public static Packet instance() {
		return new IPv4();
	}
	
	@Override
	public byte getVersion() {
		return version;
	}
	
	@Override
	public void setVersion(byte version) {
		this.version = version;
	}
	
	public byte getHeaderLength() {
		return headerLength;
	}
	
	public IPv4 setHeaderLength(byte headerLength) {
		this.headerLength = headerLength;
		return this;
	}
	
	public byte getDiffServ() {
		return diffServ;
	}
	
	public IPv4 setDiffServ(byte diffServ) {
		this.diffServ = diffServ;
		return this;
	}
	
	public byte getExpCon() {
		return expCon;
	}
	
	public IPv4 setExpCon(byte expCon) {
		this.expCon = expCon;
		return this;
	}
	
	public short getTotalLength() {
		return totalLength;
	}
	
	public IPv4 setTotalLength(short totalLength) {
		this.totalLength = totalLength;
		return this;
	}
	
	public short getIdentification() {
		return identification;
	}
	
	public IPv4 setIdentification(short identification) {
		this.identification = identification;
		return this;
	}
	
	public byte getFlags() {
		return flags;
	}
	
	public IPv4 setFlags(byte flags) {
		this.flags = flags;
		return this;
	}
	
	public short getFragmentOffset() {
		return fragmentOffset;
	}
	
	public IPv4 setFragmentOffset(short fragmentOffset) {
		this.fragmentOffset = fragmentOffset;
		return this;
	}
	
	public byte getTtl() {
		return ttl;
	}
	
	public IPv4 setTtl(byte ttl) {
		this.ttl = ttl;
		return this;
	}
	
	public Protocol getProtocol() {
		return protocol;
	}
	
	public IPv4 setProtocol(Protocol protocol) {
		this.protocol = protocol;
		return this;
	}
	
	public short getChecksum() {
		return checksum;
	}
	
	public IPv4 setChecksum(short checksum) {
		this.checksum = checksum;
		return this;
	}
	
	public Inet4Address getSourceAddress() {
		return sourceAddress;
	}
	
	public IPv4 setSourceAddress(Inet4Address sourceAddress) {
		this.sourceAddress = sourceAddress;
		return this;
	}
	
	public Inet4Address getDestinationAddress() {
		return destinationAddress;
	}
	
	public IPv4 setDestinationAddress(Inet4Address destinationAddress) {
		this.destinationAddress = destinationAddress;
		return this;
	}
	
	public byte[] getOptions() {
		return options;
	}
	
	public IPv4 setOptions(byte[] options) {
		this.options = options;
		return this;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public IPv4 setData(byte[] data) {
		this.data = data;
		return this;
	}
	
	public static IPv4 wrap(byte[] bytes) {
		return IPv4.wrap(bytes, 0, bytes.length);
	}
	
	public static IPv4 wrap(byte[] bytes, int offset, int length) {
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
		ipv4.protocol = Protocol.getProtocol(buffer.get());
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
		ipv4.rawPacket = bytes;
		return ipv4;
	}
	
	public byte[] toBytes() {
		byte[] data = new byte[IPV4_HEADER_LENGTH +
				((this.data == null) ? 0 : this.data.length) +
				((headerLength > 5) ? options.length * 5 : 0)];
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.put((byte) ((version & 0xf) << 4 | headerLength & 0xf));
		buffer.put((byte) (((diffServ << 2) & 0x3f) | expCon & 0x3));
		buffer.putShort(totalLength);
		buffer.putShort(identification);
		buffer.putShort((short) ((flags & 0x7) << 13 | fragmentOffset & 0x1fff));
		buffer.put(ttl);
		buffer.put(protocol.getType());
		buffer.putShort(checksum);
		buffer.put(sourceAddress.toBytes());
		buffer.put(destinationAddress.toBytes());
		if (options != null) {
			buffer.put(options);
		}
		
		if (checksum == 0) {
			buffer.rewind();
			int accumulation = 0;
			for (int i = 0; i < headerLength * 2; ++i) {
				accumulation += 0xffff & buffer.getShort();
			}
			accumulation = (accumulation >> 16 & 0xffff)
					+ (accumulation & 0xffff);
			checksum = (short) (~accumulation & 0xffff);
			buffer.putShort(10, checksum);
		}
		return data;
	}

	@Override
	public Packet getChild() {
		if (protocol == null) return null;
		switch (protocol) {
			case ICMP: return ICMP.wrap(this.data);
			case TCP: return TCP.wrap(this.data);
			default: return null;
		}
	}

	@Override
	public Packet putChild(byte[] data) {
		IPv4 ipv4 = this;
		ipv4.data = data;
		return ipv4;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("[")
				.append("Version: " + version)
				.append(", Internet Header Length: " + headerLength)
				.append(", Differentiated Services Code Point: " + diffServ)
				.append(", Explicit Congestion Notification: " + expCon)
				.append(", Total Length: " + totalLength)
				.append(", Identification: " + (identification & 0xffff))
				.append(", Flags: " + flags)
				.append(", Fragment Offset: " + fragmentOffset)
				.append(", Time To Live: " + ttl)
				.append(", Protocol: " + protocol)
				.append(", Header Checksum: " + (checksum & 0xffff))
				.append(", Source Address: " + sourceAddress)
				.append(", Destination Address: " + destinationAddress)
				.append(", Options: " + options)
				.append("]").toString();
	}
}
