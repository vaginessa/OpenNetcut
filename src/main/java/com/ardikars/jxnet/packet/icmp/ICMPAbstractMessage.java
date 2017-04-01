package com.ardikars.jxnet.packet.icmp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class ICMPAbstractMessage {

    private ICMPTypeAndCode typeAndCode;
    private String description;

    private static Map<ICMPTypeAndCode, ICMPAbstractMessage> registry = new HashMap<ICMPTypeAndCode, ICMPAbstractMessage>();

    protected ICMPAbstractMessage(ICMPTypeAndCode typeAndCode, String description) {
        this.typeAndCode = typeAndCode;
        this.description = description;
    }

    public static ICMPAbstractMessage register(ICMPAbstractMessage message) {
        return registry.put(message.typeAndCode, message);
    }

    public static ICMPAbstractMessage getInstance(ICMPTypeAndCode value) {
        return registry.get(value);
    }

    public ICMPTypeAndCode getTypeAndCode() {
        return typeAndCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ICMPAbstractMessage)) return false;
        if (obj.getClass() != getClass()) return false;
        ICMPTypeAndCode tc = (ICMPTypeAndCode) obj;
        if (tc.getCode() == typeAndCode.getCode()
                && tc.getType() == typeAndCode.getType()) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Arrays.hashCode(description.getBytes());
    }

    @Override
    public String toString() {
        return description;
    }

    static {
        try {
            Class.forName("com.ardikars.jxnet.packet.icmp.ICMPv4EchoReply");
            Class.forName("com.ardikars.jxnet.packet.icmp.ICMPv4EchoRequest");
            Class.forName("com.ardikars.jxnet.packet.icmp.ICMPv4ParameterProblem");
            Class.forName("com.ardikars.jxnet.packet.icmp.ICMPv4Redirect");
            Class.forName("com.ardikars.jxnet.packet.icmp.ICMPv4RouterAdvertisement");
            Class.forName("com.ardikars.jxnet.packet.icmp.ICMPv4RouterSolicitation");
            Class.forName("com.ardikars.jxnet.packet.icmp.ICMPv4TimeExceeded");
            Class.forName("com.ardikars.jxnet.packet.icmp.ICMPv4Timestamp");
            Class.forName("com.ardikars.jxnet.packet.icmp.ICMPv4TimestampReply");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
