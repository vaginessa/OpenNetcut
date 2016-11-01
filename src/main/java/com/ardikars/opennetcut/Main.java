package com.ardikars.opennetcut;

import com.ardikars.jxnet.exception.JxnetException;

public class Main {
    
    
    public static void main(String[] args) throws JxnetException {
        com.ardikars.opennetcut.view.MainWindow.main_windows.setVisible(true);
    }
    
    static {
        System.loadLibrary("jxnet");
        //Loader.loadFromJar(new String[] {"/lib/x86_64/libjxnet-linux.so"});
    }
}