package com.ardikars.opennetcut;

import com.ardikars.jxnet.exception.JxnetException;
import com.ardikars.jxnet.util.Loader;
import com.ardikars.opennetcut.app.LoggerHandler;
import com.ardikars.opennetcut.app.Utils;
import com.ardikars.opennetcut.view.MainWindow;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    
    public static void main(String[] args) throws JxnetException {
        run();
    }
    
    private static void run() throws JxnetException {
        Utils.initialize(null, 1500, 1, 300, MainWindow.main_windows.getLogHandler());
        MainWindow.main_windows.setVisible(true);
    }
    
    static {
        try {
            Loader.loadLibrary();
        } catch (UnsatisfiedLinkError ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}