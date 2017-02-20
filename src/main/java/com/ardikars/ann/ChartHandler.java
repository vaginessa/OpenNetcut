package com.ardikars.ann;

@FunctionalInterface
public interface ChartHandler {

    public void next(int epoch, int maxEpech, double sse);
    
}
