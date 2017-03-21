package com.ardikars.jxnet.packet.protocol.datalink.ethernet;

import com.ardikars.jxnet.util.NamedNumber;

import java.util.HashMap;
import java.util.Map;

public final class EtherType extends NamedNumber<Short, EtherType> {

    public static final int IEEE802_3_MAX_LENGTH = 1500;

    /**
     * IPv4: 0x0800
     */
    public static final EtherType IPV4
            = new EtherType((short) 0x0800, "IPv4");

    /**
     * ARP: 0x0806
     */
    public static final EtherType ARP
            = new EtherType((short) 0x0806, "ARP");

    /**
     * IEEE 802.1Q VLAN-tagged frames: 0x8100
     */
    public static final EtherType DOT1Q_VLAN_TAGGED_FRAMES
            = new EtherType((short) 0x8100, "IEEE 802.1Q VLAN-tagged frames");

    /**
     * RARP: 0x8035
     */
    public static final EtherType RARP
            = new EtherType((short) 0x8035, "RARP");

    /**
     * Appletalk: 0x809b
     */
    public static final EtherType APPLETALK
            = new EtherType((short) 0x809b, "Appletalk");

    /**
     * IPv6: 0x86dd
     */
    public static final EtherType IPV6
            = new EtherType((short) 0x86dd, "IPv6");

    /**
     * PPP: 0x880b
     */
    public static final EtherType PPP
            = new EtherType((short) 0x880b, "PPP");

    /**
     * MPLS: 0x8847
     */
    public static final EtherType MPLS
            = new EtherType((short) 0x8847, "MPLS");

    /**
     * PPPoE Discovery Stage: 0x8863
     */
    public static final EtherType PPPOE_DISCOVERY_STAGE
            = new EtherType((short) 0x8863, "PPPoE Discovery Stage");

    /**
     * PPPoE Session Stage: 0x8864
     */
    public static final EtherType PPPOE_SESSION_STAGE
            = new EtherType((short) 0x8864, "PPPoE Session Stage");

    private static final Map<Short, EtherType> registry
            = new HashMap<Short, EtherType>();

    static {
        registry.put(IPV4.getValue(), IPV4);
        registry.put(ARP.getValue(), ARP);
        registry.put(DOT1Q_VLAN_TAGGED_FRAMES.getValue(), DOT1Q_VLAN_TAGGED_FRAMES);
        registry.put(RARP.getValue(), RARP);
        registry.put(APPLETALK.getValue(), APPLETALK);
        registry.put(IPV6.getValue(), IPV6);
        registry.put(PPP.getValue(), PPP);
        registry.put(MPLS.getValue(), MPLS);
        registry.put(PPPOE_DISCOVERY_STAGE.getValue(), PPPOE_DISCOVERY_STAGE);
        registry.put(PPPOE_SESSION_STAGE.getValue(), PPPOE_SESSION_STAGE);
    }

    /**
     * @param value value
     * @param name  name
     */
    public EtherType(Short value, String name) {
        super(value, name);
    }

    /**
     * @param value value
     * @return a EtherType object.
     */
    public static EtherType getInstance(Short value) {
        if (registry.containsKey(value)) {
            return registry.get(value);
        } else if ((value & 0xFFFF) <= IEEE802_3_MAX_LENGTH) {
            return new EtherType(value, "Length");
        } else {
            return new EtherType(value, "unknown");
        }
    }

    /**
     * @param type type
     * @return a EtherType object.
     */
    public static EtherType register(EtherType type) {
        return registry.put(type.getValue(), type);
    }

    @Override
    public String toString() {
        if ((getValue() & 0xFFFF) <= IEEE802_3_MAX_LENGTH) {
            StringBuilder sb = new StringBuilder(70);
            return sb.append("[Type: ")
                    .append(getValue() & 0xFFFF)
                    .append(", Name: ")
                    .append(getName())
                    .append("]")
                    .toString();
        } else {
            return super.toString();
        }
    }

}
