package com.ardikars.ann;

import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ANN {

    private static final Random random = new Random();

    public static double random() {
        double min = -1;
        double max = 1;
        return min + (max - min) * random.nextDouble();
    }

    public static double[] array(double... value) {
        return value;
    }

    public static double[][] generateXorInputs() {
        return new double[][] {
                array(1.0, 1.0),
                array(1.0, 0.0)
        };
    }

    public static double[][] generateXorOutputs() {
        return new double[][] { array(0.0), array(1.0) };
    }

    public static double[][] generateXorDumpOutputs() {
        return new double[][] { array(0.0, 0.0), array(1.0, 1.0), array(1.0, 1.0), array(0.0, 0.0) };
    }

    public static double[][] generateInputs() {
        return new double[][] {
                array(1, 1, 1, 1),
                array(1, 1, 1, 0),
                array(1, 1, 0, 1),
                array(1, 0, 1, 1),
                array(0, 0, 0, 0),
                array(0, 1, 0, 0),
                array(0, 0, 1, 0),
                array(0, 0, 0, 1),
                array(1, 1, 0, 0),
                array(1, 0, 0, 0),
                array(0, 1, 1, 1),
                array(0, 0, 1, 1)
        };
    }

    public static double[][] generateOutputs() {
        return new double[][] {
                array(1),
                array(1),
                array(1),
                array(1),
                array(0),
                array(1),
                array(0),
                array(0),
                array(1),
                array(1),
                array(1),
                array(0)
        };
    }

    public static double[][] generateDummyOutputs(int a, int b) {
        double[][] value = new double[a][b];
        Arrays.fill(value, array(-1));
        return value;
    }

    public static void printResult(double[][] inputs,
                                   double[][] outputs,
                                   double[][] resultOutputs,
                                   int inputLayerSize,
                                   int outputLayerSize) {
        DecimalFormat df = new DecimalFormat();
        for (int p = 0; p < inputs.length; p++) {
            System.out.print("INPUTS: ");
            for (int x = 0; x < inputLayerSize; x++) {
                System.out.print(inputs[p][x] + " ");
            }

            System.out.print("EXPECTED: ");
            for (int x = 0; x < outputLayerSize; x++) {
                System.out.print(outputs[p][x] + " ");
            }

            System.out.print("ACTUAL: ");
            for (int x = 0; x < outputLayerSize; x++) {
                System.out.print(resultOutputs[p][x] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printWeights(Layer hiddenLayer, Layer outputLayer) {
        for (Neuron n : hiddenLayer) {
            List<Connection> connections = n.getConnections();
            for (Connection con : connections) {
                System.out.println("[ "+n.getName()+","+con.getLeftNeuron().getName()+" ] = " + con.getWeight());
            }
        }
        // weights for the output layer
        for (Neuron n : outputLayer) {
            List<Connection> connections = n.getConnections();
            for (Connection con : connections) {
                System.out.println("[ "+n.getName()+","+con.getLeftNeuron().getName()+" ] = " + con.getWeight());
            }
        }
        System.out.println();
    }

    public static void writeWeigth(String path, Layer hiddenLayer, Layer outputLayer) {
        JsonWriter writer = null;
        try {
            writer = new JsonWriter(new FileWriter(path));
            writer.beginObject();
            for (Neuron n : hiddenLayer) {
                List<Connection> cons = n.getConnections();
                for (Connection con : cons) {
                    writer.name("Neuron ID").value(n.getID());
                    writer.name("Connection ID").value(con.getID());
                    writer.name("Weight").value(con.getWeight());
                }
            }
            writer.endObject();
            writer.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
