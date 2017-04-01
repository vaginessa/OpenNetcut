package com.ardikars.jxnet.packet.icmp;

public class ICMPv4RouterAdvertisement extends ICMPAbstractMessage {

    public static final ICMPv4RouterAdvertisement ROUTER_ADVERTISEMENT =
            new ICMPv4RouterAdvertisement(ICMPTypeAndCode.newInstance((byte) 9, (byte) 0), "Type: Router Advertisement, Code: Router Advertisement");

    protected ICMPv4RouterAdvertisement(ICMPTypeAndCode typeAndCode, String description) {
        super(typeAndCode, description);
    }

    static {
        ICMPAbstractMessage.register(ROUTER_ADVERTISEMENT);
    }

}
