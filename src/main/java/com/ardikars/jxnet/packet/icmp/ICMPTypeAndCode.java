package com.ardikars.jxnet.packet.icmp;

public class ICMPTypeAndCode {

    private final byte type;
    private final byte code;

    private ICMPTypeAndCode(final byte type, final byte code) {
        this.type = type;
        this.code = code;
    }

    public static ICMPTypeAndCode newInstance(final byte type, final byte code) {
        return new ICMPTypeAndCode(type, code);
    }

    public byte getType() {
        return type;
    }

    public byte getCode() {
        return code;
    }

    @Override
    public boolean equals (final Object O) {
        if (!(O instanceof ICMPTypeAndCode)) return false;
        if (((ICMPTypeAndCode) O).type != type) return false;
        if (((ICMPTypeAndCode) O).code != code) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 17 * 37 + code + type;
    }

}
