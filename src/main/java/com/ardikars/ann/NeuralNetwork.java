package com.ardikars.ann;

import com.ardikars.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeuralNetwork {

    private Logger logger = null;
    
    private int maximumEpoch;
    private double minimalError;

    public final Layer inputLayer = new Layer();
    public final Layer hiddenLayer = new Layer();
    public final Layer outputLayer = new Layer();
    
    private final Neuron bias1 = new Neuron();
    private final Neuron bias2 = new Neuron();
    private final int[] layers;
 
    private double epsilon;
 
    private double learningRate;
    private double momentum;
    
    private double inputs[][];
    private double expectedOutputs[][];
    
    private double resultOutputs[][];
    private double output[];
    
    private ActivationFunctions.Type activationFunction;
    
    private NeuralNetwork(double[][] inputs, int hidden, double[][] outputs) {
        this.bias1.setName("type");
        this.bias2.setName("type");
        this.bias1.setOutput(1);
        this.bias2.setOutput(1);
        this.inputs = inputs;
        this.expectedOutputs = outputs;
        this.layers = new int[] { inputs[0].length, hidden, outputs[0].length };
        
        for (int i = 0; i < this.layers.length; i++) {
            switch (i) {
                case 0:
                    // input layer
                    for (int j = 0; j < this.layers[i]; j++) {
                        Neuron neuron = new Neuron();
                        neuron.setName("type");
                        this.inputLayer.add(neuron);
                    }   break;
                case 1:
                    // hidden layer\
                    for (int j = 0; j < this.layers[i]; j++) {
                        Neuron neuron = new Neuron();
                        neuron.setName("Z");
                        neuron.addConnections("V", this.inputLayer);
                        neuron.addBiasConnection("A",this.bias1);
                        this.hiddenLayer.add(neuron);
                    }   break;
                case 2:
                    // output layer\
                    for (int j = 0; j < this.layers[i]; j++) {
                        Neuron neuron = new Neuron();
                        neuron.setName("code");
                        neuron.addConnections("W", this.hiddenLayer);
                        neuron.addBiasConnection("B", this.bias2);
                        this.outputLayer.add(neuron);
                    }   break;
                default:
                    System.out.println("!Error NeuralNetwork init");
                    break;
            }
        }
        Neuron.resetCounter();
        Connection.resetCounter();
    }
    
    public static NeuralNetwork initff(double[][] inputs, int hidden, double[][] outputs) {
        
        NeuralNetwork network = new NeuralNetwork(inputs, hidden, outputs);
        network.resultOutputs = Utils.generateDummyOutputs(outputs.length, outputs[0].length); // dummy output
        // initialize random weights
        network.hiddenLayer.stream().map((neuron) -> neuron.getConnections()).forEach((connections) -> {
            connections.stream().forEach((conn) -> {
                double newWeight = Utils.random();
                conn.setWeight(newWeight);
            });
        });
        network.outputLayer.stream().map((neuron) -> neuron.getConnections()).forEach((connections) -> {
            connections.stream().forEach((conn) -> {
                double newWeight = Utils.random();
                conn.setWeight(newWeight);
            });
        });
        return network;
    }
    
    public NeuralNetwork trainbp(ActivationFunctions.Type activationFunction,
            ParamBuilder params) {
        this.logger = params.getLogger();
        
        int showEpoch = params.getShowEpoch();

        this.maximumEpoch = params.getMaximumEpoch();
        this.minimalError = params.getMinimalError();
        this.learningRate = params.getLearningRate();
    
        this.momentum = params.getMomentum();
        this.epsilon = params.getEpsilon();
        
        this.activationFunction = activationFunction;
        
        int epoch = 0;
        
        int i;
        // Train neural network until minError reached or maxSteps exceeded
        double error = 1;
        for (i = 0; i < this.maximumEpoch && error > this.minimalError; i++) {
            error = 0;
            for (int p = 0; p < this.inputs.length; p++) {
                setInput(this.inputs[p]);
 
                activate(params.getArg());
 
                this.output = getOutput();
                this.resultOutputs[p] = this.output;
                for (int j = 0; j < this.expectedOutputs[p].length; j++) {
                    double err = Math.pow(this.output[j] - this.expectedOutputs[p][j], 2);
                    error += err;
                }
                applyBackpropagation(this.expectedOutputs[p]);
            }
            if (showEpoch > 0) {
                if (i == epoch) {
                    epoch += showEpoch;
                    System.out.println("[ INFO ] :: Epoch = " + (i) + ", SSE = " + error);
                }
            }
        }
        if (i == maximumEpoch) {
            System.out.println("[ WARNING ] :: " + "Epoch = " + (i) + ", SSE = " + error);
        } else {
            System.out.println("\n[ Result ] :: " + "Epoch = " + (i) + ", SSE = " + error);
            Utils.printWeights(this.hiddenLayer, this.outputLayer);
            Utils.writeWeigth("weight.json", hiddenLayer, outputLayer);
        }
        return this;
    }
    
    public static <T> NeuralNetwork simuff(double[][] inputs, Map<String, Double> weight1, Map<String, Double> weight2,
                                           ActivationFunctions.Type activationFunction, int hidden, int output) {
        double[][] outputs = new double[inputs.length][output];
        NeuralNetwork network = new NeuralNetwork(inputs, hidden, outputs);
        network.resultOutputs = Utils.generateDummyOutputs(outputs.length, outputs[0].length);
        network.activationFunction = activationFunction;
            
        network.hiddenLayer.stream().forEach((n) -> {
            n.getConnections().stream().forEach((con) -> {
                String key = "["+n.getID()+","+con.getID()+"]";
                double newWeight = weight1.get(key);
                con.setWeight(newWeight);
            });
        });
        network.outputLayer.stream().forEach((n) -> {
            n.getConnections().stream().forEach((con) -> {
                String key = "["+n.getID()+","+con.getID()+"]";
                double newWeight = weight2.get(key);
                con.setWeight(newWeight);
            });
        });
               
        double error = 1;
        int i;
        for (i = 0; i < 1 && error > network.minimalError; i++) {
            error = 0;
            for (int p = 0; p < network.inputs.length; p++) {
                network.setInput(network.inputs[p]);
 
                network.activate(null);
 
                network.output = network.getOutput();
                network.resultOutputs[p] = network.output;
                
            }
        }
//        Utils.printWeights(network.hiddenLayer, network.outputLayer);
        Utils.printResult(network.inputs, network.expectedOutputs, network.resultOutputs,
                network.inputLayer.size(), network.outputLayer.size());
        return network;
    }

    public Neuron getBias1() {
        return bias1;
    }

    public Neuron getBias2() {
        return bias2;
    }
    
    
    
    
    // ---- >  for training  < ---- //
    
    private void setInput(double inputs[]) {
        for (int i = 0; i < this.inputLayer.size(); i++) {
            this.inputLayer.get(i).setOutput(inputs[i]);
        }
    }
 
    public double[] getOutput() {
        double[] outputs = new double[this.outputLayer.size()];
        for (int i = 0; i < this.outputLayer.size(); i++)
            outputs[i] = this.outputLayer.get(i).getOutput();
        return outputs;
    }
 
    private <T> void activate(T arg) {
        this.hiddenLayer.stream().forEach((n) -> {
            n.calculateOutput(activationFunction, this.logger, arg);
        });
        this.outputLayer.stream().forEach((n) -> {
            n.calculateOutput(activationFunction, this.logger, arg);
        });
    }
 
    public void applyBackpropagation(double expectedOutput[]) {
        //System.out.println("Apply backpropagation.");
        // error check, normalize value ]0;1[
//        for (int i = 0; i < expectedOutput.length; i++) {
//            expectedOutput[i] = ActivationFunctions.calculate(expectedOutput[i], ActivationFunctions.ICMPType.BINARY);
//        }

        int i = 0;
        for (Neuron n : this.outputLayer) {
            List<Connection> connections = n.getConnections();
            for (Connection con : connections) {
                StringBuilder formula = new StringBuilder();
                formula.append(con.getName()).append("_delta_weight=");
                double ak = n.getOutput();
                double ai = con.getLeftNeuron().getOutput();
                double desiredOutput = expectedOutput[i];
                formula.append("-learning rate * (-").append(n.getName()).append("*(").append("1").
                        append("-").append(n.getName()).append(")*").append(con.getLeftNeuron().getName()).
                        append("*").append("(disiredOutput").
                        append("[").append(i).append("]").append("-").append(n.getName()).append("))=").
                        append(-this.learningRate).append("*(").
                        append(-ak).append("*").append("(1-").append(ak).append(")").append("*").
                        append(ai).append("*").append("(").append(desiredOutput).append("-").append(ak).
                        append("))=");
                double partialDerivative = -ak * (1 - ak) * ai * (desiredOutput - ak);
                double deltaWeight = -this.learningRate * partialDerivative;
                formula.append(deltaWeight).append("\n");
                formula.append(con.getName()).append("_weight=").append(con.getName()).append("_weight").append("+")
                        .append(con.getName()).append("_delta_weigth").append("+").append("momentum").append("*")
                        .append("prev_delta_weight")
                        .append("=").append(con.getWeight()).append("+")
                        .append(deltaWeight).append("+").append(this.momentum).append("*")
                        .append(con.getPrevDeltaWeight()).append("=");
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + this.momentum * con.getPrevDeltaWeight());
                formula.append(newWeight + this.momentum * con.getPrevDeltaWeight());
                if (this.logger != null) {
                    this.logger.next(null, n, connections, formula);
                }
            }
            i++;
        }
 
        // update weights for the hidden layer
        for (Neuron n : this.hiddenLayer) {
            List<Connection> connections = n.getConnections();
            for (Connection con : connections) {
                double aj = n.getOutput();
                double ai = con.getLeftNeuron().getOutput();
                double sumKoutputs = 0;
                int j = 0;
                StringBuilder formula = new StringBuilder();
                StringBuilder sumkstr = new StringBuilder();
                formula.append(con.getName()).append("_delta_weigth=").append("-learnig rate")
                        .append("*").append(n.getName()).append("*").append("(1-").append(n.getName()).append(")*")
                        .append(con.getLeftNeuron().getName()).append("*");
                sumkstr.append(-this.learningRate).append("*").append(aj).append("*(1-").append(aj).append(")")
                        .append("*").append(ai).append("*");
                for (Neuron out_neu : this.outputLayer) {
                    double wjk = out_neu.getConnection(n.getID()).getWeight();
                    double desiredOutput = expectedOutput[j];
                    double ak = out_neu.getOutput();
                    formula.append("-(disiredOutput[").append(j).append("]")
                            .append("-").append(out_neu.getName()).append(")")
                            .append("*").append(out_neu.getName()).append("*(")
                            .append("1-").append(out_neu.getName()).append(")")
                            .append("*").append(out_neu.getConnection(n.getID()).getName())
                            .append("_weigth");
                    sumkstr.append("-(").append(desiredOutput).append("-").append(ak)
                            .append(")*").append(ak).append("*(1-").append(ak).append(")")
                            .append("*").append(wjk);
                    if (j < this.outputLayer.size() - 1) {
                        formula.append("+");
                        sumkstr.append("+");
                    }
                    j++;
                    sumKoutputs = sumKoutputs
                            + (-(desiredOutput - ak) * ak * (1 - ak) * wjk);
                }
                
                formula.append("=").append(sumkstr.toString());
                double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
                double deltaWeight = -this.learningRate * partialDerivative;
                formula.append("=").append(deltaWeight).append("\n")
                        .append(con.getName()).append("_weigth=")
                        .append(con.getName()).append("_weight").append("+")
                        .append(con.getName()).append("_delta_weigth").append("+")
                        .append("momentum").append("*").append(con.getName()).append("prev_delta_weigth=");
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                formula.append(con.getWeight()).append("+").append(deltaWeight).append("+")
                        .append(this.momentum).append("*").append(con.getPrevDeltaWeight()).append("=");
                con.setWeight(newWeight + this.momentum * con.getPrevDeltaWeight());
                formula.append(newWeight + this.momentum * con.getPrevDeltaWeight());
                if (this.logger != null) {
                    this.logger.next(null, n, connections, formula);
                }
            }
        }
    }
    
    public Map<String, Double> getWeight1() {
        Map<String, Double> weights = new HashMap<String, Double>();
        for (Neuron n : this.hiddenLayer) {
            List<Connection> cons = n.getConnections();
            for (Connection con : cons) {
                String id_con = "["+n.getID()+","+con.getID()+"]";
                weights.put(id_con, con.getWeight());
            }
        }
        return weights;
    }
    
    public Map<String, Double> getWeight2() {
        Map<String, Double> weights = new HashMap<String, Double>();
        for (Neuron n : this.outputLayer) {
            List<Connection> cons = n.getConnections();
            for (Connection con : cons) {
                String id_con = "["+n.getID()+","+con.getID()+"]";
                weights.put(id_con, con.getWeight());
            }
        }
        return weights;
    }
    
}