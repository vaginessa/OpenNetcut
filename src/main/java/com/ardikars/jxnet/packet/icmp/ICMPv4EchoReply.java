package com.ardikars.jxnet.packet.icmp;

public class ICMPv4EchoReply extends ICMPAbstractMessage {

    public static final ICMPv4EchoReply ECHO_REPLY =
            new ICMPv4EchoReply(ICMPTypeAndCode.newInstance((byte) 0, (byte) 0), "Type: Echo Reply, Code: Echo Reply");

    protected ICMPv4EchoReply(ICMPTypeAndCode typeAndCode, String description) {
        super(typeAndCode, description);
    }

    static {
        ICMPAbstractMessage.register(ECHO_REPLY);
    }

}
