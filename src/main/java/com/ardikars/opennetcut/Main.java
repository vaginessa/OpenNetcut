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

package com.ardikars.opennetcut;

import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.opennetcut.app.ids.IDS;
import com.ardikars.opennetcut.app.StaticField;
import com.ardikars.opennetcut.app.Utils;

public class Main {
    
    public static void main(String[] args) throws JxnetException {
        //MainWindow.main_windows.setVisible(true);
        runIDS();
    }

    private static void runIDS() {
        Utils.initialize(null, StaticField.SNAPLEN, StaticField.PROMISC, StaticField.TIMEOUT);
        Utils.compile(StaticField.Pcap_IDS, StaticField.BPF_PROGRAM_IDS,
                "ether dst " + StaticField.CURRENT_MAC_ADDRESS.toString() + " and arp");
        Utils.filter(StaticField.Pcap_IDS, StaticField.BPF_PROGRAM_IDS);

        Utils.compile(StaticField.PCAP_ICMP_TRAP, StaticField.BPF_PROGRAM_ICMP_TRAP,
                "ether dst " + StaticField.CURRENT_MAC_ADDRESS.toString() + " and icmp");
        Utils.filter(StaticField.PCAP_ICMP_TRAP, StaticField.BPF_PROGRAM_ICMP_TRAP);

        IDS ids = new IDS();
        ids.start();
        System.out.println("Started.");
        try {
            Thread.sleep(3600*15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //ids.stopThread();
        //PcapClose(StaticField.PCAP);
        System.out.println("Stopped");
    }
}
