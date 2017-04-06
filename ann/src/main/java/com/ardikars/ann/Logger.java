package com.ardikars.ann;

import java.util.List;

public interface Logger<T> {

    public void next(T arg, Neuron neuron, List<Connection> connection, StringBuilder printble);
    
}
