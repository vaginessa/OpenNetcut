package com.ardikars.tulip;

public class TULIP {

    public static void main(String[] args) {
        try {
            StaticField.initialize(null, 1500, 1, 1, 2000, 1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        IDS ids = IDS.newThread();
        ids.start();

    }

    /* INVALID_PACKET | UNCONSISTENT_SHA | UNPADDED_ETHERNET_FRAME
       | UNKNOWN_OUI | EPOCH_TIME */

    public static double[][] generateInputs() {
        return new double[][] {
                array(1, 1, 1, 1, 0.1),
                array(1, 1, 1, 1, 0.5),
                array(1, 1, 1, 1, 0.9),

                array(1, 1, 1, 0, 0.1),
                array(1, 1, 0, 1, 0.5),
                array(1, 0, 1, 1, 0.9),

                array(0, 0, 0, 0, 0.1),
                array(0, 0, 0, 0, 0.5),
                array(0, 0, 0, 0, 0.9),

                array(0, 1, 0, 0, 0.1),
                array(0, 1, 0, 0, 0.5),
                array(0, 1, 0, 0, 0.9),

                array(0, 0, 1, 0, 0.1),
                array(0, 0, 1, 0, 0.5),
                array(0, 0, 1, 0, 0.9),

                array(0, 0, 0, 1, 0.1),
                array(0, 0, 0, 1, 0.5),
                array(0, 0, 0, 1, 0.9),

                array(1, 1, 0, 0, 0.1),
                array(1, 1, 0, 0, 0.5),
                array(1, 1, 0, 0, 0.9),

                array(1, 0, 0, 0, 0.1),
                array(1, 0, 0, 0, 0.5),
                array(1, 0, 0, 0, 0.9),

                array(0, 1, 1, 1, 0.1),
                array(0, 1, 1, 1, 0.5),
                array(0, 1, 1, 1, 0.9),

                array(0, 0, 1, 1, 0.1),
                array(0, 0, 1, 1, 0.5),
                array(0, 0, 1, 1, 0.9)
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

                array(0),
                array(0),
                array(0),

                array(1),
                array(1),
                array(1),

                array(0),
                array(0),
                array(1),

                array(0),
                array(0),
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

                array(0),
                array(0),
                array(1),
        };
    }

    public static double[] array(double... value) {
        return value;
    }

}
