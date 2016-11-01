package com.ardikars.opennetcut.packet.protocol.datalink;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

public class Ethernet {
	
    public static final short VLAN_UNTAGGED = (short) 0xffff;

    public static final short ETHERNET_HEADER_LENGTH = 14; // bytes

    public static final short VLAN_HEADER_LENGTH = 4; // bytes

    public static final short DATALAYER_ADDRESS_LENGTH = 6; // bytes
	
    public enum EtherType {
        
    	ARP(Ethernet.TYPE_ARP, "arp"),
        IPV4(Ethernet.TYPE_IPV4, "ipv4"),
        IPV6(Ethernet.TYPE_IPV6, "ipv6"),
        VLAN(Ethernet.TYPE_VLAN, "vlan"),
        UNKNOWN(0, "unknown");
    	
    	private final int type;
    	
    	private final String description;
    	
    	public static String toString(int type) {
            for(EtherType etherType : values()) {
                if(etherType.type == type) {
                    return etherType.description;
                }
            }
            return null;
    	};
    	
    	private EtherType(int type, String description) {
            this.type = type;
            this.description = description;
    	}
    	
    	public int getType() {
            return type;
        }
    	
    	public String getDescription() {
            return description;
        }
    	
    	public short toShort() {
            return (type > Short.MAX_VALUE ? Short.MAX_VALUE : type < Short.MIN_VALUE ? Short.MIN_VALUE : (short) type);
    	}
    }
    
    public static final short TYPE_ARP = (short) 0x0806;
    public static final short TYPE_IPV4 = (short) 0x0800;
    public static final short TYPE_IPV6 = (short) 0x86dd;
    public static final short TYPE_VLAN = (short) 0x8100;

    private byte[] destinationMACAddress;
    private byte[] sourceMACAddress;
    private byte priorityCode;
    private short vlanID;
    private short etherType;
    private boolean pad = false;

    private byte[] payload;

    private boolean fcs;

    public Ethernet() {
        this.vlanID = VLAN_UNTAGGED;
    }

    public Ethernet(final byte[] data, final int offset, final int length) {
        if(length < ETHERNET_HEADER_LENGTH) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
        byte[] macBuf = new byte[6];
        bb.get(macBuf);
        this.destinationMACAddress = macBuf;
        bb.get(macBuf);
        this.sourceMACAddress = macBuf;
        short ethType = bb.getShort();
        if (ethType == TYPE_VLAN) {
            final short tci = bb.getShort();
            this.priorityCode = (byte) (tci >> 13 & 0x07);
            this.vlanID = (short) (tci & 0x0fff);
            ethType = bb.getShort();
        } else {
            this.vlanID = Ethernet.VLAN_UNTAGGED;
        }
        this.etherType = ethType;
    }

    public byte[] getDestinationMACAddress() {
        return destinationMACAddress;
    }

    public byte[] getSourceMACAddress() {
        return sourceMACAddress;
    }

    public byte getPriorityCode() {
        return priorityCode;
    }

    public short getVlanID() {
        return vlanID;
    }

    public short getEtherType() {
        return etherType;
    }

    public boolean getPad() {
        return pad;
    }

    public byte[] getPayload() {
        return payload;
    }

    public Ethernet setDestinationMACAddress(byte[] destinationMACAddress) {
        this.destinationMACAddress = destinationMACAddress;
        return this;
    }

    public Ethernet setSourceMACAddress(byte[] sourceMACAddress) {
        this.sourceMACAddress = sourceMACAddress;
        return this;
    }

    public Ethernet setPriorityCode(byte priorityCode) {
        this.priorityCode = priorityCode;
        return this;
    }

    public Ethernet setVlanID(short vlanID) {
        this.vlanID = vlanID;
        return this;
    }

    public Ethernet setPad(boolean pad) {
        this.pad = pad;
        return this;
    }

    public Ethernet setEtherType(final short etherType) {
        this.etherType = etherType;
        return this;
    }

    public boolean setPayload(final byte[] payload) {
        if(payload != null) {
            this.payload = payload; 
            return true;
        }
        return false;
    }

    public Ethernet setFcs(boolean fcs) {
        this.fcs = fcs;
        return this;
    }
	
	

    public byte[] toByteArray() {
        int length = 14 + (vlanID == (short) 0xffff ? 0 : 4)
                + (payload == null ? 0 : payload.length)
                + (fcs == false ? 0 : 4);
        if (pad && length < 60) {
            length = 60;
        }
        final byte[] data = new byte[length];
        final ByteBuffer bb = ByteBuffer.wrap(data);
        bb.put(destinationMACAddress);
        bb.put(sourceMACAddress);
        if (vlanID != VLAN_UNTAGGED) {
            bb.putShort(Ethernet.TYPE_VLAN);
            bb.putShort((short) (priorityCode << 13 | vlanID & 0xffff));
        }
        bb.putShort(etherType);
        if (payload != null) {
            bb.put(payload);
        }
        if (pad) {
            Arrays.fill(data, bb.position(), data.length, (byte) 0x00);
        }
        if (fcs) {
            CRC32 crc32 = new CRC32();
            crc32.update(data);
            bb.putInt((int)crc32.getValue());
        }
        return data;
    }
	
}