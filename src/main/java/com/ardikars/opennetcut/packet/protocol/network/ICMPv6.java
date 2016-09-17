package com.ardikars.opennetcut.packet.protocol.network;

import java.nio.ByteBuffer;

public class ICMPv6 {
	
    public static final int ICMPV6_HEADER_LENGTH = 4;

    private byte type;
    private byte code;
    private short checksum;

    private byte[] messageBody;

    public ICMPv6 setType(byte type) {
        this.type = type;
        return this;
    }

    public ICMPv6 setCode(byte code) {
        this.code = code;
        return this;
    }

    public ICMPv6 setChecksum(short checksum) {
        this.checksum = checksum;
        return this;
    }

    public ICMPv6 setMessageBody(byte[] messageByte) {
        this.messageBody = messageByte;
        return this;
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

    public byte[] getMessageBody() {
        return messageBody;
    }

    public byte[] toByteArray() {
        int length = ICMPv6.ICMPV6_HEADER_LENGTH + (this.messageBody == null ? 0 : this.messageBody.length);
        final byte[] data = new byte[length];
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.put(type);
        bb.put(code);
        bb.putShort(checksum);
        return data;
    }
    
    public short calculateChecksum(byte[] buf) {
        int length = buf.length;
        int i = 0;

        long sum = 0;
        long data;

        // Handle all pairs
        while (length > 1) {
            // Corrected to include @Andy's edits and various comments on Stack Overflow
            data = (((buf[i] << 8) & 0xFF00) | ((buf[i + 1]) & 0xFF));
            sum += data;
            // 1's complement carry bit correction in 16-bits (detecting sign extension)
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }
            i += 2;
            length -= 2;
        }
        // Handle remaining byte in odd length buffers
        if (length > 0) {
          // Corrected to include @Andy's edits and various comments on Stack Overflow
            sum += (buf[i] << 8 & 0xFF00);
            // 1's complement carry bit correction in 16-bits (detecting sign extension)
                if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }
        }
        // Final 1's complement value correction to 16-bits
        sum = ~sum;
        sum = sum & 0xFFFF;
        return (short) sum;
  }
    
}
