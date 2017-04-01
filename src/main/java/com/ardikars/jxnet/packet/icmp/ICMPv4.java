package com.ardikars.jxnet.packet.icmp;

import com.ardikars.jxnet.packet.Packet;

import java.nio.ByteBuffer;

public class ICMPv4 extends Packet {

    public static final int ICMP_HEADER_LENGTH = 4;

    private ICMPAbstractMessage message;
    private short checksum;

    private byte[] data;

    public static ICMPv4 newInstance(byte[] bytes) {
        return newInstance(bytes, 0, bytes.length);
    }

    public static ICMPv4 newInstance(byte[] bytes, int offset, int length) {
        final ByteBuffer bb = ByteBuffer.wrap(bytes, offset, length);
        ICMPv4 icmp = new ICMPv4();
        byte type = bb.get();
        byte code = bb.get();
        icmp.message = ICMPAbstractMessage.getInstance(ICMPTypeAndCode.newInstance(type, code));
        if (icmp.message == null) {
            icmp.message = ICMPUnknownMessage.UNKNOWN;
        }
        icmp.checksum = bb.getShort();
        byte[] data = new byte[bb.capacity() - ICMP_HEADER_LENGTH];
        bb.get(data);
        icmp.data = data;
        return icmp;
    }

    public ICMPv4 setMessage(ICMPAbstractMessage message) {
        this.message = message;
        return this;
    }

    public ICMPv4 setChecksum(short checksum) {
        this.checksum = checksum;
        return this;
    }

    public ICMPv4 setData(byte[] data) {
        this.data = data;
        return this;
    }

    @Override
    public Packet setPacket(Packet packet) {
        return null;
    }

    @Override
    public Packet getPacket() {
        return null;
    }

    @Override
    public byte[] getBytes() {
        int length = ICMP_HEADER_LENGTH + ((this.data == null) ? 0 : this.data.length);
        byte[] data = new byte[length];
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.put(this.message.getTypeAndCode().getType());
        bb.put(this.message.getTypeAndCode().getCode());
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
    public String toString() {
        return new StringBuilder()
                .append("[").append(message.toString())
                .append(", Checksum: ").append(checksum)
                .append("]").toString();
    }

}
