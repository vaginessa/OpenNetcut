package com.ardikars.opennetcut.packet.protocol.lan;

import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.opennetcut.packet.Packet;
import java.nio.ByteBuffer;

public class ARP extends Packet {  
		
    public enum OperationCode {
        REQUEST((short) 0x01, "REQUEST"),
        REPLY((short) 0x02, "REPLY"),
        ;

        private short op;
        private String description;
        
        private OperationCode(short op, String description) {
            this.op = op;
            this.description = description;
        }

        public short getOp() {
            return op;
        }

        public String getDescription() {
            return description;
        }
        
        public static OperationCode getOperationCode(short op) {
            for (OperationCode s : values()) {
                if (s.getOp() == op) {
                    return s;
                }
            }
            return null;
        }
        
    }
    public static final short HW_TYPE_ETHERNET = 0x01;

    public static final short PROTO_TYPE_IP = 0x0800;

    
    public static final short ARP_HEADER_LENGTH = 28;
	
	private short hardwareType;
	private short protocolType;
	private byte hardwareAddressLength;
	private byte protocolAddressLength;
	private OperationCode opCode;
	private MacAddress senderHardwareAddress;
	private Inet4Address senderProtocolAddress;
	private MacAddress targetHardwareAddress;
	private Inet4Address targetProtocolAddress;
	
	public ARP() {
	}
    
    public static ARP wrap(byte[] bytes) {
		return ARP.wrap(bytes, 0, bytes.length);
	}
	
	public static ARP wrap(byte[] bytes, int offset, int length) {
        final ByteBuffer bb = ByteBuffer.wrap(bytes, offset, length);
        ARP arp = new ARP();
        arp.hardwareType = bb.getShort();
        arp.protocolType = bb.getShort();
        arp.hardwareAddressLength = bb.get();
        arp.protocolAddressLength = bb.get();
        arp.opCode = OperationCode.getOperationCode(bb.getShort());
        
        byte[] tmphw = new byte[0xff & arp.hardwareAddressLength];
        bb.get(tmphw);
        arp.senderHardwareAddress = MacAddress.valueOf(tmphw);
       
        byte[] tmpip = new byte[0xff & arp.protocolAddressLength];
        bb.get(tmpip);
        arp.senderProtocolAddress = Inet4Address.valueOf(tmpip);
        
        tmphw = new byte[0xff & arp.hardwareAddressLength];
        bb.get(tmphw);
        arp.targetHardwareAddress = MacAddress.valueOf(tmphw);
        
        tmpip = new byte[0xff & arp.protocolAddressLength];
        bb.get(tmpip);
        arp.targetProtocolAddress = Inet4Address.valueOf(tmpip);
        return arp;
    }
    
    public short getHardwareType() {
        return hardwareType;
    }

    public short getProtocolType() {
        return protocolType;
    }

    public byte getHardwareAddressLength() {
        return hardwareAddressLength;
    }

    public byte getProtocolAddressLength() {
        return protocolAddressLength;
    }

    public OperationCode getOpCode() {
        return opCode;
    }

    public MacAddress getSenderHardwareAddress() {
        return senderHardwareAddress;
    }

    public Inet4Address getSenderProtocolAddress() {
        return senderProtocolAddress;
    }

    public MacAddress getTargetHardwareAddress() {
        return targetHardwareAddress;
    }

    public Inet4Address getTargetProtocolAddress() {
        return targetProtocolAddress;
    }

    public ARP setHardwareType(short hardwareType) {
        this.hardwareType = hardwareType;
        return this;
    }

    public ARP setProtocolType(short protocolType) {
        this.protocolType = protocolType;
        return this;
    }

    public ARP setHardwareAddressLength(byte hardwareAddressLength) {
        this.hardwareAddressLength = hardwareAddressLength;
        return this;
    }

    public ARP setProtocolAddressLength(byte protocolAddressLength) {
        this.protocolAddressLength = protocolAddressLength;
        return this;
    }

    public ARP setOpCode(OperationCode opCode) {
        this.opCode = opCode;
        return this;
    }

    public ARP setSenderHardwareAddress(MacAddress senderHardwareAddress) {
        this.senderHardwareAddress = senderHardwareAddress;
        return this;
    }

    public ARP setSenderProtocolAddress(Inet4Address senderProtocolAddress) {
        this.senderProtocolAddress = senderProtocolAddress;
        return this;
    }

    public ARP setTargetHardwareAddress(MacAddress targetHardwareAddress) {
        this.targetHardwareAddress = targetHardwareAddress;
        return this;
    }

    public ARP setTargetProtocolAddress(Inet4Address targetProtocolAddress) {
        this.targetProtocolAddress = targetProtocolAddress;
        return this;
    }
	
    public byte[] toBytes() {
	final int length = 8 + 2 * (0xff & this.hardwareAddressLength) + 2
                * (0xff & this.protocolAddressLength);
        final byte[] data = new byte[length];
        final ByteBuffer bb = ByteBuffer.wrap(data);
        bb.putShort(this.hardwareType);
        bb.putShort(this.protocolType);
        bb.put(this.hardwareAddressLength);
        bb.put(this.protocolAddressLength);
        bb.putShort(this.opCode.getOp());
        bb.put(this.senderHardwareAddress.toBytes(), 0, 0xff & this.hardwareAddressLength);
        bb.put(this.senderProtocolAddress.toBytes(), 0, 0xff & this.protocolAddressLength);
        bb.put(this.targetHardwareAddress.toBytes(), 0, 0xff & this.hardwareAddressLength);
        bb.put(this.targetProtocolAddress.toBytes(), 0, 0xff & this.protocolAddressLength);
        return data;
    }

    @Override
    public Packet putChild(byte[] data) {
        return null;
    }
    
    @Override
    public Packet getChild() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ Hw Type: ").append(this.hardwareType);
        sb.append(", Proto Type: ").append(this.protocolType);
        sb.append(", Hw Addr Length: ").append(this.hardwareAddressLength);
        sb.append(", Proto Addr length: ").append(this.protocolAddressLength);
        sb.append(", OpCode: ").append(opCode);
        sb.append(", SHA: ").append(this.senderHardwareAddress);
        sb.append(", SPA: ").append(this.senderProtocolAddress);
        sb.append(", THA: ").append(this.targetHardwareAddress);
        sb.append(", TPA: ").append(this.targetProtocolAddress)
                .append("]");
        return sb.toString();
    }
    
    

}