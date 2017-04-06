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
import com.ardikars.opennetcut.view.MainWindow;

public class OpenNetcut {
    
    public static void main(String[] args) throws JxnetException {
        MainWindow.main_windows.setVisible(true);
    }

}
/**
 *       |  INVALID_PACKET  |  UNCONSISTENT_SHA  |  UNPADDED_ETHERNET_FRAME  |  UNKNOWN_OUI  |  EPOCH_TIME  |
 * array(1, 1, 1, 1)           = 1
 * array(1, 1, 1, 0)           = 1
 * array(1, 1, 0, 1)           = 1
 * array(1, 0, 1, 1)           = 1
 * array(0, 0, 0, 0)           = 0
 * array(0, 1, 0, 0)           = 1
 * array(0, 0, 1, 0)           = 0
 * array(0, 0, 0, 1)           = 0
 * array(1, 1, 0, 0)           = 1
 * array(1, 0, 0, 0)           = 1
 * array(0, 1, 1, 1)           = 1
 * array(0, 0, 1, 1)           = 0
 */


