package com.ardikars.opennetcut;

import com.ardikars.jxnet.util.Loader;

public class Main {
    public static void main(String[] args) {
        com.ardikars.opennetcut.view.MainWindow.main_windows.setVisible(true);
    }
    
    static {
        Loader.loadLibrary();
    }
}