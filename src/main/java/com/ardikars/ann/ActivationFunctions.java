package com.ardikars.ann;

public class ActivationFunctions {
    
    public enum Type {
        
        BINARY(1, "Binary"),
        SIGMOID(2, "Sigmoid");
        
        private final int type;
        private final String description;
        
        private Type(int type, String description) {
            this.type = type;
            this.description = description;
        }

        public int getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }
        
        public static Type getActivationFunction(int type) {
            for (Type t : values()) {
                if (t.getType() == type) {
                    return t;
                }
            }
            return null;
        }
        
    }
    
    public static double calculate(double value, Type type) {
        switch (type) {
            case BINARY:
                if (value < 0 || value > 1) {
                    if (value < 0)
                        return 0;
                    else
                        return 1;
                }
                return value;
            case SIGMOID:
                return (1.0 / (1.0 + (Math.exp(-value))));
            default:
                return value;
        }
    }
    
}
