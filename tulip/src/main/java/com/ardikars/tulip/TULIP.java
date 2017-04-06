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

}
