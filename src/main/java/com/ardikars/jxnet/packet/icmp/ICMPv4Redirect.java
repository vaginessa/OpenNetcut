package com.ardikars.jxnet.packet.icmp;

public class ICMPv4Redirect extends ICMPAbstractMessage {

    public static final ICMPv4Redirect REDIRECT_FOR_NETWORK =
            new ICMPv4Redirect(ICMPTypeAndCode.newInstance((byte) 5, (byte) 0), "Type: Redirect, Code: Redirect for Network");

    public static final ICMPv4Redirect REDIRECT_FOR_HOST =
            new ICMPv4Redirect(ICMPTypeAndCode.newInstance((byte) 5, (byte) 1), "Type: Redirect, Code: Redirect for Host");

    public static final ICMPv4Redirect REDIRECT_FOR_TOS_AND_NETWORK =
            new ICMPv4Redirect(ICMPTypeAndCode.newInstance((byte) 5, (byte) 2), "Type: Redirect, Code: Redirect for Type of Service and Network");

    public static final ICMPv4Redirect REDIRECT_FOR_TOS_AND_HOST =
            new ICMPv4Redirect(ICMPTypeAndCode.newInstance((byte) 5, (byte) 3), "Type: Redirect, Code: Redirect for Type of Service and Host");

    protected ICMPv4Redirect(ICMPTypeAndCode typeAndCode, String description) {
        super(typeAndCode, description);
    }

    static {
        ICMPAbstractMessage.register(REDIRECT_FOR_NETWORK);
        ICMPAbstractMessage.register(REDIRECT_FOR_HOST);
        ICMPAbstractMessage.register(REDIRECT_FOR_TOS_AND_NETWORK);
        ICMPAbstractMessage.register(REDIRECT_FOR_TOS_AND_HOST);
    }

}
