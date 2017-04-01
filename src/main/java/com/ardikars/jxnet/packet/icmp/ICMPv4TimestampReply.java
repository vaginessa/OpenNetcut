package com.ardikars.jxnet.packet.icmp;

public class ICMPv4TimestampReply extends ICMPAbstractMessage {

    public static final ICMPv4TimestampReply TIMESTAMP_REPLY =
            new ICMPv4TimestampReply(ICMPTypeAndCode.newInstance((byte) 14, (byte) 0), "Type: Timestamp Reply, Code: Timestamp Reply");

    protected ICMPv4TimestampReply(ICMPTypeAndCode typeAndCode, String description) {
        super(typeAndCode, description);
    }

    static {
        ICMPAbstractMessage.register(TIMESTAMP_REPLY);
    }

}
