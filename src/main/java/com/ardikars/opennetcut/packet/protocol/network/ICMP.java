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

import com.ardikars.opennetcut.packet.Packet;

import java.nio.ByteBuffer;

public class ICMP extends Packet {

    public static final int ICMP_HEADER_LENGTH = 4;

    private byte type;
    private byte code;
    private short checksum;

    private byte[] data;

    public ICMP() {}

    public static ICMP wrap(byte[] bytes) {
        return ICMP.wrap(bytes, 0, bytes.length);
    }

    public static ICMP wrap(byte[] bytes, int offset, int length) {
        final ByteBuffer bb = ByteBuffer.wrap(bytes, offset, length);
        ICMP icmp = new ICMP();
        icmp.type = bb.get();
        icmp.code = bb.get();
        icmp.checksum = bb.getShort();
        byte[] data = new byte[bb.capacity() - ICMP_HEADER_LENGTH];
        bb.get(data);
        icmp.data = data;
        return icmp;
    }

    public byte getType() {
        return type;
    }

    public byte getCode() {
        return code;
    }

    public short getChecksum() {
        return checksum;
    }

    public ICMP setType(byte type) {
        this.type = type;
        return this;
    }

    public ICMP setCode(byte code) {
        this.code = code;
        return this;
    }

    public ICMP setChecksum(short checksum) {
        this.checksum = checksum;
        return this;
    }

    public byte[] toBytes() {
        int length = ICMP_HEADER_LENGTH + ((data == null) ? 0 : this.data.length);
        byte[] data = new byte[length];
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.put(this.type);
        bb.put(this.code);
        bb.putShort(this.checksum);
        if (this.data != null)
            bb.put(this.data);
        if (this.checksum == 0) {
            bb.rewind();
            int accumulation = 0;
            for (int i=0; i<length/2; i++) {
                accumulation += 0xffff & bb.getShort();
            }
            if (length % 2 > 0) {
                accumulation += (bb.get() & 0xff) << 8;
            }
            accumulation = (accumulation >> 16 & 0xffff)
                    + (accumulation & 0xffff);
            this.checksum = (short) (~accumulation & 0xffff);
            bb.putShort(2, this.checksum);
        }
        return data;
    }

    @Override
    public Packet getChild() {
        return null;
    }

    @Override
    public Packet putChild(byte[] data) {
        ICMP icmp = this;
        icmp.data = data;
        return icmp;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("[")
                .append("Type: ").append(this.type)
                .append(", Code: ").append(this.code)
                .append(", Checksum: ").append(this.checksum & 0xffff)
                .append("]").toString();
    }
}
