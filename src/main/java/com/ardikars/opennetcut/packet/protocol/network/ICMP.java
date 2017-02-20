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
