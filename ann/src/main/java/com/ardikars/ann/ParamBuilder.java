
package com.ardikars.ann;

public class ParamBuilder<T> {

    private final Logger logger;
    private final int showEpoch;
    private final int maximumEpoch;
    private final double minimalError;
    private final double learningRate;
    
    private final double momentum;
    private final double epsilon;
    
    private final T arg;

    private ParamBuilder(Logger logger, T arg, int showEpoch, int maximumEpoch, double minimalError, double learningRate, double momentum, double epsilon) {
        this.logger = logger;
        this.showEpoch = showEpoch;
        this.maximumEpoch = maximumEpoch;
        this.minimalError = minimalError;
        this.learningRate = learningRate;
        this.momentum = momentum;
        this.epsilon = epsilon;
        this.arg = arg;
    }

    public static <T> ParamBuilder buildParameters(Logger logger, T arg, int showEpoch, int maximumEpoch, double minimalError, double learningRate) {
        return new ParamBuilder(logger, arg, showEpoch, maximumEpoch, minimalError, learningRate, 0.0, 0.0);
    }
    
    public static <T> ParamBuilder buildParameters(Logger logger, T arg, int showEpoch, int maximumEpoch, double minimalError, double learningRate, double momentum) {
        return new ParamBuilder(logger, arg, showEpoch, maximumEpoch, minimalError, learningRate, momentum, 0.0);
    }
    
    public static <T> ParamBuilder buildParameters(Logger logger, T arg, int showEpoch, int maximumEpoch, double minimalError, double learningRate, double momentum, double epsilon) {
        return new ParamBuilder(logger, arg, showEpoch, maximumEpoch, minimalError, learningRate, momentum, epsilon);
    }
    
    public static <T> ParamBuilder buildParameters(Logger logger, T arg, int maximumEpoch, double minimalError, double learningRate) {
        return new ParamBuilder(logger, arg, 0, maximumEpoch, minimalError, learningRate, 0.0, 0.0);
    }
    
    public static <T> ParamBuilder buildParameters(Logger logger, T arg, int maximumEpoch, double minimalError, double learningRate, double momentum) {
        return new ParamBuilder(logger, arg, 0, maximumEpoch, minimalError, learningRate, momentum, 0.0);
    }
    
    public static <T> ParamBuilder buildParameters(Logger logger, T arg, int maximumEpoch, double minimalError, double learningRate, double momentum, double epsilon) {
        return new ParamBuilder(logger, arg, 0, maximumEpoch, minimalError, learningRate, momentum, epsilon);
    }
    
    public Logger getLogger() {
        return logger;
    }

    public int getShowEpoch() {
        return showEpoch;
    }
    
    public int getMaximumEpoch() {
        return maximumEpoch;
    }

    public double getMinimalError() {
        return minimalError;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public double getMomentum() {
        return momentum;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public T getArg() {
        return arg;
    }
    
}
