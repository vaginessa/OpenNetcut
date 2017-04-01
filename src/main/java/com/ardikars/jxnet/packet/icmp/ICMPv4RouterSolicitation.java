package com.ardikars.jxnet.packet.icmp;

public class ICMPv4RouterSolicitation extends ICMPAbstractMessage {

    public static final ICMPv4RouterSolicitation ROUTER_SOLICITATION =
            new ICMPv4RouterSolicitation(ICMPTypeAndCode.newInstance((byte) 10, (byte) 0), "Type: Router Solicitation: Router Discovery/Selection/Solicitation");

    protected ICMPv4RouterSolicitation(ICMPTypeAndCode typeAndCode, String description) {
        super(typeAndCode, description);
    }

    static {
        ICMPAbstractMessage.register(ROUTER_SOLICITATION);
    }

}
