package com.ardikars.tulip;

import com.ardikars.ann.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
                .buildParameters(longger, null, 100, 100000, 0.1, 0.1);

        NeuralNetwork nn = NeuralNetwork.initff(generateInputs(),
                5, generateOutputs(), -5, 5);
        nn.trainbp(ActivationFunctions.Type.SIGMOID, params);

        Properties hiddenWeight = null;
        Properties outputWeight = null;
        FileWriter hiddenWriter;
        FileWriter outputWriter;
        try {
            hiddenWeight = new Properties();
            hiddenWriter = new FileWriter("hidden-weight.properties");
            hiddenWeight.putAll(nn.getHiddenWeight());
            hiddenWeight.store(hiddenWriter, "Hidden weight");
            hiddenWriter.close();

            outputWeight = new Properties();
            outputWriter = new FileWriter("output-weight.properties");
            outputWeight.putAll(nn.getOutputWeight());
            outputWeight.store(outputWriter, "Output weight");
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileReader hiddenReader;
        FileReader outputReader;
        try {
            hiddenWeight = new Properties();
            hiddenReader = new FileReader("hidden-weight.properties");
            hiddenWeight.load(hiddenReader);
            hiddenReader.close();

            outputWeight = new Properties();
            outputReader = new FileReader("output-weight.properties");
            outputWeight.load(outputReader);
            outputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> hiddenMap = new HashMap<String, String>((Map) hiddenWeight);
        Map<String, String> outputnMap = new HashMap<String, String>((Map) outputWeight);


        NeuralNetwork.simuff(generateInputs(), hiddenMap, outputnMap,
                ActivationFunctions.Type.SIGMOID, 5, 1);
    }

    /* INVALID_PACKET | UNCONSISTENT_SHA | UNPADDED_ETHERNET_FRAME
       | UNKNOWN_OUI | EPOCH_TIME */

    public static double[][] generateInputs() {
        return new double[][] {
                array(1, 1, 1, 1, 1),
                array(1, 1, 0, 0, 0),
                array(1, 1, 1, 1, 0),
                array(1, 1, 0, 0, 1),
                array(1, 1, 1, 0, 1),
                array(1, 1, 0, 1, 0),
                array(1, 1, 1, 0, 0),
                array(1, 1, 0, 1, 1),

                array(1, 0, 1, 1, 1),
                array(1, 0, 0, 0, 0),
                array(1, 0, 1, 1, 0),
                array(1, 0, 0, 0, 1),
                array(1, 0, 1, 0, 1),
                array(1, 0, 0, 1, 0),
                array(1, 0, 1, 0, 0),
                array(1, 0, 0, 1, 1),

                array(0, 0, 1, 1, 0),
                array(0, 0, 1, 1, 0),
                array(0, 0, 1, 1, 1),
                array(0, 0, 1, 0.5, 1),

                array(0, 0, 0, 1, 0),

                // 0
                array(0, 0, 0, 0.5, 0.5),
                array(0, 0, 0, 0.5, 0.4),
                array(0, 0, 0, 0.5, 0.3),
                array(0, 0, 0, 0.5, 0.2),
                array(0, 0, 0, 0.5, 0.1),
                array(0, 0, 0, 0.5, 0),

                array(0, 0, 1, 0.5, 0.5),
                array(0, 0, 1, 0.5, 0.4),
                array(0, 0, 1, 0.5, 0.3),
                array(0, 0, 1, 0.5, 0.2),
                array(0, 0, 1, 0.5, 0.1),
                array(0, 0, 1, 0.5, 0),

                array(0, 0, 0, 0, 0.5),
                array(0, 0, 0, 0, 0.4),
                array(0, 0, 0, 0, 0.3),
                array(0, 0, 0, 0, 0.2),
                array(0, 0, 0, 0, 0.1),
                array(0, 0, 0, 0, 0),

                // 1

                array(1, 1, 1, 1, 0),
                array(1, 1, 0, 0, 1),
                array(1, 1, 1, 1, 1),
                array(1, 1, 0, 0, 0),
                array(1, 1, 1, 0, 0),
                array(1, 1, 0, 1, 1),
                array(1, 1, 1, 0, 1),
                array(1, 1, 0, 1, 0),

                array(1, 0, 1, 1, 0),
                array(1, 0, 0, 0, 1),
                array(1, 0, 1, 1, 1),
                array(1, 0, 0, 0, 0),
                array(1, 0, 1, 0, 0),
                array(1, 0, 0, 1, 1),
                array(1, 0, 1, 0, 1),
                array(1, 0, 0, 1, 0),

                array(1, 1, 1, 0.5, 1),
                array(1, 1, 0, 0.5, 0),
                array(1, 1, 1, 0.5, 0),
                array(1, 1, 0, 0.5, 1),
                array(1, 1, 1, 0.5, 1),
                array(1, 1, 0, 0.5, 0),
                array(1, 1, 1, 0.5, 0),
                array(1, 1, 0, 0.5, 1),

                array(1, 0, 1, 0.5, 1),
                array(1, 0, 0, 0.5, 0),
                array(1, 0, 1, 0.5, 0),
                array(1, 0, 0, 0.5, 1),
                array(1, 0, 1, 0.5, 1),
                array(1, 0, 0, 0.5, 0),
                array(1, 0, 1, 0.5, 0),
                array(1, 0, 0, 0.5, 1),

                array(1, 1, 1, 0.5, 0),
                array(1, 1, 0, 0.5, 1),
                array(1, 1, 1, 0.5, 1),
                array(1, 1, 0, 0.5, 0),
                array(1, 1, 1, 0.5, 0),
                array(1, 1, 0, 0.5, 1),
                array(1, 1, 1, 0.5, 1),
                array(1, 1, 0, 0.5, 0),

                array(1, 0, 1, 0.5, 0),
                array(1, 0, 0, 0.5, 1),
                array(1, 0, 1, 0.5, 1),
                array(1, 0, 0, 0.5, 0),
                array(1, 0, 1, 0.5, 0),
                array(1, 0, 0, 0.5, 1),
                array(1, 0, 1, 0.5, 1),
                array(1, 0, 0, 0.5, 0),

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

                array(1),
                array(1),
                array(1),
                array(1),

                array(1),

                // 0
                array(0),
                array(0),
                array(0),
                array(0),
                array(0),
                array(0),

                array(0),
                array(0),
                array(0),
                array(0),
                array(0),
                array(0),

                array(0),
                array(0),
                array(0),
                array(0),
                array(0),
                array(0),

                // 1

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
                array(1),
                array(1),
                array(1),
                array(1),
                array(1),
                array(1),
                array(1),
                array(1)
        };
    }

    public static double[] array(double... value) {
        return value;
    }

}
