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

import com.ardikars.ann.*;
import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.opennetcut.app.ids.IDS;
import com.ardikars.opennetcut.app.StaticField;
import com.ardikars.opennetcut.view.MainWindow;
import com.ardikars.util.Utils;

import java.util.List;

public class Main {
    
    public static void main(String[] args) throws JxnetException {
        //MainWindow.main_windows.setVisible(true);
        runIDS();
        //train();
    }

    private static void train() {

        Logger<String> logger = (arg, neuron, connection, printble) -> {
            System.out.println(printble);
        };

        ParamBuilder<String> params = ParamBuilder.buildParameters(
                null, "", 100, 10000, 0.1, 0.1
        );
        NeuralNetwork nn = NeuralNetwork.initff(
                Utils.generateInputs()
                , 5
                , Utils.generateOutputs());
        nn.trainbp(ActivationFunction.Type.SIGMOID, params);
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
        //ids.stopThread();
        //PcapClose(StaticField.PCAP);
    }
}
/**
 *       |  INVALID_PACKET  |  UNCONSISTENT_SHA  |  UNPADDED_ETHERNET_FRAME  |  UNKNOWN_OUI  |  EPOCH_TIME  |
 * array(1, 1, 1, 1)           = 1
 * array(1, 1, 1, 0)           = 1
 * array(1, 1, 0, 1)           = 1
 * array(1, 0, 1, 1)           = 1
 * array(0, 0, 0, 0)           = 0
 * array(0, 1, 0, 0)           = 1
 * array(0, 0, 1, 0)           = 0
 * array(0, 0, 0, 1)           = 0
 * array(1, 1, 0, 0)           = 1
 * array(1, 0, 0, 0)           = 1
 * array(0, 1, 1, 1)           = 1
 * array(0, 0, 1, 1)           = 0
 */


