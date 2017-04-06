
package com.ardikars.ann;

public class Connection {
    
    private static int counter = 0;
    
    private final int ID;
    
    private String name;
    
    private double weight = 0;
    private double bestWeight = 0;
    private double prevDeltaWeight = 0;
    private double deltaWeight = 0;
 
    private final Neuron leftNeuron;
    private final Neuron rightNeuron;
 
    public Connection(Neuron leftNeuron, Neuron rightNeuron) {
        this.leftNeuron = leftNeuron;
        this.rightNeuron = rightNeuron;
        this.ID = counter;
        counter++;
    }
 
    public static void resetCounter() {
        counter = 0;
    }

    public int getID() {
        return ID;
    }
    
    public double getWeight() {
        return this.weight;
    }
 
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public void setWeightAsBest() {
        this.bestWeight = weight;
    }

    public double getBestWeight() {
        return bestWeight;
    }

    public void setBestWeight(double bestWeight) {
        this.bestWeight = bestWeight;
    }
    
    public double getPrevDeltaWeight() {
        return this.prevDeltaWeight;
    }
    
    public void setDeltaWeight(double weight) {
        this.prevDeltaWeight = deltaWeight;
        this.deltaWeight = weight;
    }
 
    public Neuron getLeftNeuron() {
        return this.leftNeuron;
    }
 
    public Neuron getRightNeuron() {
        return this.rightNeuron;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
