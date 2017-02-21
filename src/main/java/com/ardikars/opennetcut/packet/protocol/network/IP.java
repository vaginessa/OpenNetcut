/**
 * Copyright (C) 2017  Ardika Rommy Sanjaya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ardikars.opennetcut.packet.protocol.network;

import com.ardikars.opennetcut.packet.Packet;

public abstract class IP extends Packet {
	
	public enum Protocol {
		ICMP((byte) 0x1, "ICMP"),
		TCP((byte) 0x6, "TCP"),
		UDP((byte) 0x11, "UDP");
		
		private byte type;
		private String description;
		
		private Protocol(byte type, String description) {
			this.type = type;
			this.description = description;
		}
		
		public byte getType() {
			return type;
		}
		
		public String getDescription() {
			return description;
		}
		
		public static Protocol getProtocol(byte type) {
			for (Protocol t : values()) {
				if (t.getType() == type) {
					return t;
				}
			}
			return null;
		}
		
	}
	
	public abstract void setVersion(byte version);
	
	public abstract byte getVersion();
	
}
