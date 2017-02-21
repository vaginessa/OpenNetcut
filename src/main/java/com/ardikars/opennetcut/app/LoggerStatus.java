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

package com.ardikars.opennetcut.app;

public enum LoggerStatus {
    
    COMMON(0),
    PROGRESS(1),
    //SCAN(2)
    ;

    private int status;
    private LoggerStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
    
    public static LoggerStatus getLoggerStatus(int status) {
        for (LoggerStatus ls : values()) {
            if (ls.getStatus() == status) {
                return ls;
            }
        }
        return null;
    }
    
}
