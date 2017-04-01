package com.ardikars.jxnet.packet.arp;

import com.ardikars.jxnet.util.NamedNumber;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ardika Rommy Sanjaya
 * @since 0.0.1
 */
public final class ARPOperationCode extends NamedNumber<Short, ARPOperationCode> {

    public static final ARPOperationCode ARP_REQUEST = new ARPOperationCode((short) 0x01, "ARP Request");

    public static final ARPOperationCode ARP_REPLY = new ARPOperationCode((short) 0x02, "ARP Reply");

    public ARPOperationCode(Short value, String name) {
        super(value, name);
    }

    private static final Map<Short, ARPOperationCode> registry
            = new HashMap<Short, ARPOperationCode>();

    static {
        registry.put(ARP_REQUEST.getValue(), ARP_REQUEST);
        registry.put(ARP_REPLY.getValue(), ARP_REPLY);
    }

    public static ARPOperationCode register(ARPOperationCode operationCode) {
        return registry.put(operationCode.getValue(), operationCode);
    }

    public static ARPOperationCode getInstance(Short value) {
        return registry.get(value);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
