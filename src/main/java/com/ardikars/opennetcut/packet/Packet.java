
/*
 * Author	: Ardika Rommy Sanjaya
 * Website	: http://ardikars.com
 * Contact	: contact@ardikars.com
 * License	: Lesser GNU Public License Version 3
 */

package com.ardikars.opennetcut.packet;

public abstract class Packet {
	
	protected byte[] rawPacket;
	
    public abstract Packet putChild(byte[] data);
    
	public abstract Packet getChild();
	
	public byte[] getRawPacket() {
		return rawPacket;
	}
	
}
