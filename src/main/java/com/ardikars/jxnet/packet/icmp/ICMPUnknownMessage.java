package com.ardikars.jxnet.packet.icmp;

public class ICMPUnknownMessage extends ICMPAbstractMessage {

    public static ICMPUnknownMessage UNKNOWN =
            new ICMPUnknownMessage(ICMPTypeAndCode.newInstance((byte) -1, (byte) -1), "Type: Unknown or Deprecated, Code: Unknown or Deprecated");

    protected ICMPUnknownMessage(ICMPTypeAndCode typeAndCode, String description) {
        super(typeAndCode, description);
    }

}
