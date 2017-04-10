package com.ardikars.tulip;

import com.ardikars.ann.*;

public class TULIP {

    public static void main(String[] args) {
        train();
    }

    public static void run() {
        try {
            StaticField.initialize(null, 1500, 1, 1, 2000, 1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        IDS ids = IDS.newThread();
        ids.start();
    }

    public static void train() {
        Logger<String> longger = (arg, neuron, connection, printble) -> {

        };

        ParamBuilder<String> params = ParamBuilder
                .buildParameters(longger, null, 100, 10000, 0.1, 0.1);

        NeuralNetwork nn = NeuralNetwork.initff(generateInputs(),
                6, generateOutputs(), -1, 1);
        nn.trainbp(ActivationFunctions.Type.SIGMOID, params);

        NeuralNetwork.simuff(generateInputs(), nn.getWeight1(), nn.getWeight2(),
                ActivationFunctions.Type.SIGMOID, 6, 1);
    }

    /* INVALID_PACKET | UNCONSISTENT_SHA | UNPADDED_ETHERNET_FRAME
       | UNKNOWN_OUI | EPOCH_TIME */

    public static double[][] generateInputs() {
        return new double[][] {
                array(1, 1, 1, 1, 0.1),
                array(1, 1, 1, 1, 0.5),
                array(1, 1, 1, 1, 0.9),

                array(1, 1, 1, 0, 0.1),
                array(1, 1, 0, 1, 0.5),
                array(1, 0, 1, 1, 0.9),

                array(0, 0, 0, 0, 0.1),
                array(0, 0, 0, 0, 0.5),
                array(0, 0, 0, 0, 0.9),

                array(0, 1, 0, 0, 0.1),
                array(0, 1, 0, 0, 0.5),
                array(0, 1, 0, 0, 0.9),

                array(0, 0, 1, 0, 0.1),
                array(0, 0, 1, 0, 0.5),
                array(0, 0, 1, 0, 0.9),

                array(0, 0, 0, 1, 0.1),
                array(0, 0, 0, 1, 0.5),
                array(0, 0, 0, 1, 0.9),

                array(1, 1, 0, 0, 0.1),
                array(1, 1, 0, 0, 0.5),
                array(1, 1, 0, 0, 0.9),

                array(1, 0, 0, 0, 0.1),
                array(1, 0, 0, 0, 0.5),
                array(1, 0, 0, 0, 0.9),

                array(0, 1, 1, 1, 0.1),
                array(0, 1, 1, 1, 0.5),
                array(0, 1, 1, 1, 0.9),

                array(0, 0, 1, 1, 0.1),
                array(0, 0, 1, 1, 0.5),
                array(0, 0, 1, 1, 0.9)
        };
    }

    public static double[][] generateOutputs() {
        return new double[][] {
                array(1),
                array(1),
                array(1),

                array(1),
                array(1),
                array(1),

                array(0),
                array(0),
                array(0),

                array(1),
                array(1),
                array(1),

                array(0),
                array(0),
                array(1),

                array(0),
                array(0),
                array(1),

                array(1),
                array(1),
                array(1),

                array(1),
                array(1),
                array(1),

                array(1),
                array(1),
                array(1),

                array(0),
                array(0),
                array(1),
        };
    }

    public static double[] array(double... value) {
        return value;
    }

}
