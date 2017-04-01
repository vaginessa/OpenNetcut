package com.ardikars.jxnet.packet.icmp;

public class ICMPv4Timestamp extends ICMPAbstractMessage {

    public static final ICMPv4Timestamp TIMESTAMP =
            new ICMPv4Timestamp(ICMPTypeAndCode.newInstance((byte) 13, (byte) 0), "Type: Timestamp, Code: Timestamp");

    protected ICMPv4Timestamp(ICMPTypeAndCode typeAndCode, String description) {
        super(typeAndCode, description);
    }

    static {
        ICMPAbstractMessage.register(TIMESTAMP);
    }

}
