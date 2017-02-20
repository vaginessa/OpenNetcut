package com.ardikars.ann;

import java.util.ArrayList;

public class Layer extends ArrayList<Neuron> {
    
    private static int counter = 0;
    
    private final int ID;
    
    public Layer() {
        this.ID = counter;
        counter++;
    }

    public static void resetCounter() {
        counter = 0;
    }
    
    public int getID() {
        return ID;
    }   
    
}
