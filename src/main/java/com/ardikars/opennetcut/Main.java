/**
 * Copyright (C) 2017  Ardika Rommy Sanjaya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

}