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
