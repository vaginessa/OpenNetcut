package com.ardikars.tulip;

import com.ardikars.ann.*;
import org.apache.commons.cli.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TULIP {

    public static void main(String[] args) throws ParseException {

        String source = null;

        Options options = new Options();
        options.addOption("h", false, "Help.");
        options.addOption("train", true, "Train.");
        options.addOption("t", true, "Time.");
        options.addOption("i", true, "Interface.");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args, true);

        if (commandLine.hasOption("h")) {
            System.out.println("Help.");
            System.out.println("train: Train.");
            System.out.println("t: Time.");
            System.out.println("i: Interface.");
            System.exit(0);
        }

        if (commandLine.hasOption("train")) {
            System.out.println("Train option selected.");
            System.exit(0);
        }

        if (commandLine.hasOption("t")) {
            StaticField.TIME = ((int) Integer.parseInt(commandLine.getOptionValue("t")));
        }

        if (commandLine.hasOption("i")) {
            source = commandLine.getOptionValue("i");
        }

        tulip("wlan0");

    }

    public static void tulip(String source) {

        Properties hiddenWeight = null;
        Properties outputWeight = null;

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

        StaticField.hiddenMap = hiddenMap;
        StaticField.outputMap = outputnMap;

        try {
            StaticField.initialize(source, 1500, 1, 1, 2000, 1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        IDS ids = IDS.newThread();

        System.out.println("Start tulip.");
        ids.start();
    }

    public static void train() {
        Logger<String> longger = (arg, neuron, connection, printble) -> {

        };

        ParamBuilder<String> params = ParamBuilder
                .buildParameters(longger, null, 100, 100000, 0.1, 0.1);

        NeuralNetwork nn = NeuralNetwork.initff(inputs(),
                5, outputs(), -5, 5);
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
                array(0, 0, 0, 0.5, 0),

                array(0, 0, 1, 0.5, 0.5),
                array(0, 0, 1, 0.5, 0),

                array(0, 0, 0, 0, 0.5),
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

                array(0, 0, 0, 0, 0.5),
                array(0, 0, 0, 0, 0.6),
                array(0, 0, 0, 0, 0.7),
                array(0, 0, 0, 0, 0.8),
                array(0, 0, 0, 0, 0.9),
                array(0, 0, 0, 0, 1.0),


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
                array(1),

                array(1),
                array(1),
                array(1),
                array(1),
                array(1),
                array(1),
        };
    }

    public static double[][] inputs() {
        return new double[][] {
                array(1, 0, 0, 0, 0),
                array(1, 1, 0, 0, 0),
                array(0, 0, 0, 0, 0.0),
                array(0, 0, 0, 0, 0.5),
                array(0, 0, 0, 0, 1,0)
        };
    };

    public static double[][] outputs() {
        return new double[][] {
                array(1),
                array(1),
                array(0),
                array(1),
                array(1),
        };
    }

    public static double[] array(double... value) {
        return value;
    }

}
