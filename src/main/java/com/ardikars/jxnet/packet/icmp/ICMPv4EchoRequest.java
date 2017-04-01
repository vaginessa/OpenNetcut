package com.ardikars.jxnet.packet.icmp;

public class ICMPv4EchoRequest extends ICMPAbstractMessage {

    public static final ICMPv4EchoRequest ECHO_REQUEST =
            new ICMPv4EchoRequest(ICMPTypeAndCode.newInstance((byte) 8, (byte) 0), "Type: Echo Request, Code: Echo Request");

    protected ICMPv4EchoRequest(ICMPTypeAndCode typeAndCode, String description) {
        super(typeAndCode, description);
    }

    static {
        ICMPAbstractMessage.register(ECHO_REQUEST);
    }

}
