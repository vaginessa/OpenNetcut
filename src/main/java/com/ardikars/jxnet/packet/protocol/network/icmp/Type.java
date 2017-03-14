package com.ardikars.jxnet.packet.protocol.network.icmp;

import com.ardikars.jxnet.util.NamedNumber;

import java.util.Map;
import java.util.HashMap;

public class Type extends NamedNumber<Byte, Type> {

    public static Type ECHO_REPLY = new Type((byte) 0, "Echo Reply");

    public static Type DESTINATION_UNREACHABLE = new Type((byte) 3, "Destination Unreachable");

    public static Type SOURCE_QUENCH = new Type((byte) 4, "Source Quench");

    public static Type REDIRECT = new Type((byte) 5, "Redirect");

    public static Type ALTERNATIVE_HOST_ADDRESS = new Type((byte) 6, "Alternative Host Address");

    public static Type ECHO = new Type((byte) 8, "Echo");

    public static Type ROUTER_ADVERTISMENT = new Type((byte) 9, "Router Advertisement");

    public static Type ROUTER_SELECTION = new Type((byte) 10, "Router Selection");

    public static Type TIME_EXCEEDED = new Type((byte) 11, "Time Exceeded");

    public static Type PARAMETER_PROBLEM = new Type((byte) 12, "Parametar Problem");

    public static Type TIMESTAMP = new Type((byte) 13, "Timestamp");

    public static Type TIMESTAMP_REPLY = new Type((byte) 14, "Timestamp Reply");

    public static Type INFORMATION_REQUEST = new Type((byte) 15, "Information Request");

    public static Type INFORMATION_REPLY = new Type((byte) 16, "Information Reply");

    public static Type ADDRESS_MASK_REQUEST = new Type((byte) 17, "Address Mask Request");

    public static Type ADDRESS_MASK_REPLY = new Type((byte) 18, "Address Mask Reply");

    public static Type TRACEROUTE = new Type((byte) 30, "Traceroute");

    public static Type DATAGRAM_CONVERSION_ERROR = new Type((byte) 31, "Datagram Conversion Error");

    public static Type MOBILE_HOST_REDIRECT = new Type((byte) 32, "Mobile Host Redirect");

    public static Type IPV6_WHARE_ARE_YOU = new Type((byte) 33, "IPv6 Where Are You");

    public static Type IPV6_I_AM_HERE = new Type((byte) 34, "IPv6 I Am Here");

    public static Type MOBILE_REGISTRATION_REQUEST = new Type((byte) 35, "Mobile Registration Request");

    public static Type MOBILE_REGISTRATION_REPLY = new Type((byte) 36, "Mobile Registration Reply");

    public static Type DOMAIN_NAME_REQUEST = new Type((byte) 37, "Domain Name Request");

    public static Type DOMAIN_NAME_REPLY = new Type((byte) 38, "Domain Name Reply");

    public static Type SKIP = new Type((byte) 39, "Skip");

    public static Type PHOTURIS = new Type((byte) 40, "Photuris");

    public Type(Byte value, String name) {
        super(value, name);
    }

    private static Map<Byte, Type> registry = new HashMap<Byte, Type>();

    static {
        registry.put(ECHO_REPLY.getValue(), ECHO_REPLY);
        registry.put(DESTINATION_UNREACHABLE.getValue(), DESTINATION_UNREACHABLE);
        registry.put(SOURCE_QUENCH.getValue(), SOURCE_QUENCH);
        registry.put(REDIRECT.getValue(), REDIRECT);
        registry.put(ALTERNATIVE_HOST_ADDRESS.getValue(), ALTERNATIVE_HOST_ADDRESS);
        registry.put(ECHO.getValue(), ECHO);
        registry.put(ROUTER_ADVERTISMENT.getValue(), ROUTER_ADVERTISMENT);
        registry.put(ROUTER_SELECTION.getValue(), ROUTER_SELECTION);
        registry.put(TIME_EXCEEDED.getValue(), TIME_EXCEEDED);
        registry.put(PARAMETER_PROBLEM.getValue(), PARAMETER_PROBLEM);
        registry.put(TIMESTAMP.getValue(), TIMESTAMP);
        registry.put(TIMESTAMP_REPLY.getValue(), TIMESTAMP_REPLY);
        registry.put(INFORMATION_REQUEST.getValue(), INFORMATION_REQUEST);
        registry.put(INFORMATION_REPLY.getValue(), INFORMATION_REPLY);
        registry.put(ADDRESS_MASK_REQUEST.getValue(), ADDRESS_MASK_REQUEST);
        registry.put(ADDRESS_MASK_REPLY.getValue(), ADDRESS_MASK_REPLY);
        registry.put(TRACEROUTE.getValue(), TRACEROUTE);
        registry.put(DATAGRAM_CONVERSION_ERROR.getValue(), DATAGRAM_CONVERSION_ERROR);
        registry.put(MOBILE_HOST_REDIRECT.getValue(), MOBILE_HOST_REDIRECT);
        registry.put(IPV6_WHARE_ARE_YOU.getValue(), IPV6_WHARE_ARE_YOU);
        registry.put(IPV6_I_AM_HERE.getValue(), IPV6_I_AM_HERE);
        registry.put(MOBILE_REGISTRATION_REQUEST.getValue(), MOBILE_REGISTRATION_REQUEST);
        registry.put(MOBILE_REGISTRATION_REPLY.getValue(), MOBILE_REGISTRATION_REPLY);
        registry.put(DOMAIN_NAME_REQUEST.getValue(), DOMAIN_NAME_REQUEST);
        registry.put(DOMAIN_NAME_REPLY.getValue(), DOMAIN_NAME_REPLY);
        registry.put(SKIP.getValue(), SKIP);
        registry.put(PHOTURIS.getValue(), PHOTURIS);
    }

    public static Type register(Type type) {
        return registry.put(type.getValue(), type);
    }

    public static Type getInstace(Byte value) {
        return registry.get(value);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
