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

package com.ardikars.jxnet.packet.protocol.transport;

import com.ardikars.jxnet.packet.Packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TCP extends Packet {
	
	public static int TCP_HEADER_LENGTH = 20;
	
	private short sourcePort;
	private short destinationPort;
	private int sequence;
	private int acknowledge;
	private byte dataOffset; // 4 bit + 3 bit
	private short flags; // 9 bit
	private short windowSize;
	private short checksum;
	private short urgentPointer;
	private byte[] options;
	
	private byte[] data;
	
	public short getSourcePort() {
		return (short) (this.sourcePort & 0xffff);
	}

	public TCP setSourcePort(short sourcePort) {
		this.sourcePort = (short) (sourcePort & 0xffff);
		return this;
	}

	public short getDestinationPort() {
		return (short) (this.destinationPort & 0xffff);
	}

	public TCP setDestinationPort(short destinationPort) {
		this.destinationPort = (short) (destinationPort & 0xffff);
		return this;
	}
	
	public int getSequence() {
		return (int) (this.sequence & 0xffffffff);
	}
	
	public TCP setSequence(int sequence) {
		this.sequence = (int) (sequence & 0xffffffff);
		return this;
	}
	
	public int getAcknowledge() {
		return (int) (this.acknowledge & 0xffffffff);
	}
	
	public TCP setAcknowledge(int acknowledge) {
		this.acknowledge = (int) (acknowledge & 0xffffffff);
		return this;
	}
	
	public byte getDataOffset() {
		return (byte) (this.dataOffset & 0xf);
	}
	
	public TCP setDataOffset(byte dataOffset) {
		this.dataOffset = (byte) (dataOffset & 0xf);
		return this;
	}
	
	public short getFlags() {
		return (short) (this.flags & 0x1ff);
	}
	
	public TCP setFlags(short flags) {
		this.flags = (short) (flags & 0x1ff);
		return this;
	}
	
	public short getWindowSize() {
		return (short) (this.windowSize & 0xffff);
	}
	
	public TCP setWindowSize(short windowSize) {
		this.windowSize = (short) (windowSize & 0xffff);
		return this;
	}
	
	public short getChecksum() {
		return (short) (this.checksum & 0xffff);
	}
	
	public TCP setChecksum(short checksum) {
		this.checksum = (short) (checksum & 0xffff);
		return this;
	}
	
	public short getUrgentPointer() {
		return (short) (this.urgentPointer & 0xffff);
	}
	
	public TCP setUrgentPointer(short urgentPointer) {
		this.urgentPointer = (short) (urgentPointer & 0xffff);
		return this;
	}
	
	public byte[] getOptions() {
		return this.options;
	}
	
	public TCP setOptions(byte[] options) {
		this.options = options;
		return this;
	}
	
	public static TCP newInstance(byte[] bytes) {
		return TCP.newInstance(bytes, 0, bytes.length);
	}
	
	public static TCP newInstance(byte[] bytes, int offset, int length) {
		TCP tcp = new TCP();
		ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
		tcp.sourcePort = buffer.getShort();
		tcp.destinationPort = buffer.getShort();
		tcp.sequence = buffer.getInt();
		tcp.acknowledge = buffer.getInt();
		tcp.flags = buffer.getShort();
		tcp.dataOffset = ((byte) (tcp.flags >> 12 & 0xf));
		tcp.flags = (short) (tcp.flags & 0x1ff);
		tcp.windowSize = buffer.getShort();
		tcp.checksum = buffer.getShort();
		tcp.urgentPointer = buffer.getShort();
		if (tcp.dataOffset > 5) {
			int optionLength = (tcp.dataOffset << 2) - TCP_HEADER_LENGTH;
			if (buffer.limit() < buffer.position() + optionLength) {
				optionLength = buffer.limit() - buffer.position();
			}
			try {
				tcp.options = new byte[optionLength];
				buffer.get(tcp.options, 0, optionLength);
				tcp.data = new byte[(bytes.length - (TCP_HEADER_LENGTH + optionLength))];
				System.arraycopy(bytes, (TCP_HEADER_LENGTH + optionLength), tcp.data,
						0,
						(bytes.length - (TCP_HEADER_LENGTH + optionLength)));
			} catch (IndexOutOfBoundsException e) {
				tcp.options = null;
				tcp.data = new byte[(bytes.length - TCP_HEADER_LENGTH)];
				System.arraycopy(bytes, (TCP_HEADER_LENGTH), tcp.data, 0,
						(bytes.length - TCP_HEADER_LENGTH));
			}
		} else {
			tcp.data = new byte[(bytes.length - TCP_HEADER_LENGTH)];
			System.arraycopy(bytes, (TCP_HEADER_LENGTH), tcp.data, 0,
					(bytes.length - TCP_HEADER_LENGTH));
		}
		return tcp;
	}

	@Override
	public TCP setPacket(Packet packet) {
		byte[] data = packet.getBytes();
		this.data = data;
		return this;
	}

	@Override
	public Packet getPacket() {
		return null;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return this.data;
	}

	@Override
	public byte[] getBytes() {
		byte[] data = new byte[TCP_HEADER_LENGTH + ((options == null) ? 0 : options.length)];
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.putShort(sourcePort);
		buffer.putShort(destinationPort);
		buffer.putInt(sequence);
		buffer.putInt(acknowledge);
		buffer.putShort((short) ((flags & 0x1ff) | (dataOffset & 0xf) << 12));
		buffer.putShort(windowSize);
		buffer.putShort(checksum);
		buffer.putShort(urgentPointer);
		if (options != null)
			buffer.put(options);
		return data;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("[")
				.append("Source port: " + getSourcePort())
				.append(", Destination port: " + getDestinationPort())
				.append(", Sequence number: " + getSequence())
				.append(", Acknowledgment number: " + getAcknowledge())
				.append(", Data offset: " + getDataOffset())
				.append(", Flags: " + getFlags())
				.append(", Windows size: " + getWindowSize())
				.append(", Checksum: " + getChecksum())
				.append(", Urgent pointer: " + getUrgentPointer())
				.append(", Options: " + Arrays.toString(getOptions()))
				.append("]").toString();
	}
	
}
