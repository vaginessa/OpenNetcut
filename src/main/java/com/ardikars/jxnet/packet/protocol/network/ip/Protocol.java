package com.ardikars.jxnet.packet.protocol.network.ip;

import com.ardikars.jxnet.util.NamedNumber;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ardika Rommy Sanjaya
 * @since 0.0.1
 */
public class Protocol extends NamedNumber<Byte, Protocol> {

    public static final Protocol ICMP = new Protocol((byte) 1, "Internet Control Message Protocol");

    public static final Protocol IGMP = new Protocol((byte) 2, "Internet Group Management Protocol");

    public static final Protocol TCP = new Protocol((byte) 6, "Transmission Control Protocol");

    public static final Protocol UDP = new Protocol((byte) 11, "User Datagram Protocol");

    protected Protocol(Byte value, String name) {
        super(value, name);
    }

    private static Map<Byte, Protocol> registry = new HashMap<Byte, Protocol>();

    static {
        registry.put(ICMP.getValue(), ICMP);
        registry.put(IGMP.getValue(), IGMP);
        registry.put(TCP.getValue(), TCP);
        registry.put(UDP.getValue(), UDP);
    }

    public static Protocol register(Protocol protocol) {
        return registry.put(protocol.getValue(), protocol);
    }

    public static Protocol getInstance(Byte value) {
        return registry.get(value);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
