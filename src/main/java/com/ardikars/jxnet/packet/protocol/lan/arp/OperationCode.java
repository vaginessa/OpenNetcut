package com.ardikars.jxnet.packet.protocol.lan.arp;

import com.ardikars.jxnet.util.NamedNumber;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ardika Rommy Sanjaya
 * @since 0.0.1
 */
public final class OperationCode extends NamedNumber<Short, OperationCode> {

    public static final OperationCode ARP_REQUEST = new OperationCode((short) 0x01, "ARP Request");

    public static final OperationCode ARP_REPLY = new OperationCode((short) 0x02, "ARP Reply");

    public OperationCode(Short value, String name) {
        super(value, name);
    }

    private static final Map<Short, OperationCode> registry
            = new HashMap<Short, OperationCode>();

    static {
        registry.put(ARP_REQUEST.getValue(), ARP_REQUEST);
        registry.put(ARP_REPLY.getValue(), ARP_REPLY);
    }

    public static OperationCode register(OperationCode operationCode) {
        return registry.put(operationCode.getValue(), operationCode);
    }

    public static OperationCode getInstance(Short value) {
        return registry.get(value);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
