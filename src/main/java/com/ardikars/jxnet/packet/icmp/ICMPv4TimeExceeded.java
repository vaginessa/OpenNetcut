package com.ardikars.jxnet.packet.icmp;

public class ICMPv4TimeExceeded extends ICMPAbstractMessage {

    public static final ICMPv4TimeExceeded TTL_EXCEEDED_IN_TRANSIT
            = new ICMPv4TimeExceeded(ICMPTypeAndCode.newInstance((byte) 11, (byte) 0), "Type: Time Exceeded, Code: Time-To-Live Exceeded in Transit");

    public static final ICMPv4TimeExceeded FRAGMENT_REASSEMBLY_TIME_EXCEEDED
            = new ICMPv4TimeExceeded(ICMPTypeAndCode.newInstance((byte) 11, (byte) 1), "Type: Time Exceeded, Code: Fragment Reassembly Time Exceeded");

    protected ICMPv4TimeExceeded(ICMPTypeAndCode ICMPTypeAndCode, String description) {
        super(ICMPTypeAndCode, description);
    }

    static {
        ICMPAbstractMessage.register(TTL_EXCEEDED_IN_TRANSIT);
        ICMPAbstractMessage.register(FRAGMENT_REASSEMBLY_TIME_EXCEEDED);
    }

}
