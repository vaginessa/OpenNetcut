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

package com.ardikars.opennetcut.app.ids;

import com.ardikars.jxnet.*;
import com.ardikars.jxnet.packet.PacketHandler;
import com.ardikars.opennetcut.app.StaticField;

import static com.ardikars.jxnet.Jxnet.*;

public class IDS extends Thread {

    @Override
    public void run() {

        PacketHandler<String> packetHandler = (arg, pktHdr, packets) -> {
            Produce.produce(arg, pktHdr, packets).start();
        };

        Static.loop(StaticField.PCAP_IDS, -1, packetHandler, null);

    }

    public void stopThread() {
        PcapBreakLoop(StaticField.PCAP_IDS);
    }

}