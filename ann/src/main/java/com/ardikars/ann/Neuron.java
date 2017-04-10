package com.ardikars.ann;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neuron {
    
    private static int counter = 0;
    
    private final int ID;

    private String name;
    
    private double output;
        
    private List<Connection> connections = new ArrayList<Connection>();
    private Map<Integer,Connection> connectionMapping = new HashMap<Integer,Connection>();
     
    public Neuron(){        
        this.ID = counter;
        counter++;
    }
    
    public static void resetCounter() {
        counter = 0;
    }
    
    public int getID() {
        return this.ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name + ID;
    }
    
    public double getOutput() {
        return this.output;
    }

    public void setOutput(double output) {
        this.output = output;
    }
    
    public <T> void calculateOutput(ActivationFunctions.Type function, Logger<T> logger, T arg) {
        StringBuilder formula = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        formula.append(getName()).append("=").append(function.getDescription()).append("(");
        sb.append(function.getDescription()).append("(");
        int consSize = this.connections.size();
        double s = 0;
        for (int i=0; i<consSize; i++) {
            Connection con = this.connections.get(i);
            Neuron leftNeuron = con.getLeftNeuron();
            double weight = con.getWeight();
            double a = leftNeuron.getOutput();
            if (i < consSize - 1) {
                formula.append("(").append(leftNeuron.getName()).append("*").append(con.getName()).append(")");
                sb.append("(").append(leftNeuron.getOutput()).append("*").append(weight).append(")");
                formula.append("+");
                sb.append("+");
            } else {
                formula.append("(").append(leftNeuron.getName()).append("*").append(con.getName()).append(")");
                sb.append("(").append(leftNeuron.getOutput()).append("*").append(weight).append(")");
                formula.append(")=");
                sb.append(")=");
            }
            s = s + (weight*a);
        }
        sb.append(function.getDescription()).append("(").append(s).append(")=");
        this.output = ActivationFunctions.calculate(s, function);
        sb.append(this.output);
        formula.append(sb.toString());
        if (logger != null) {
            logger.next(arg, this, this.connections, formula);
        }
    }
         
    public void addBiasConnection(String name, Neuron n){
        Connection con = new Connection(n,this);
        con.setName(name);
        this.connections.add(con);
    }
    
    public void addConnections(String name, List<Neuron> inNeurons){
        for (int i=0; i<inNeurons.size(); i++) {
            Connection con = new Connection(inNeurons.get(i), this);
            con.setName(name+ID+i);
            this.connections.add(con);
            this.connectionMapping.put(inNeurons.get(i).ID, con);
        }
    }
     
    public Connection getConnection(int neuronID){
        return this.connectionMapping.get(neuronID);
    }
    
    public List<Connection> getConnections(){
        return this.connections;
    }

}
