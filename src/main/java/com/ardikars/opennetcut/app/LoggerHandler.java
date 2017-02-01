package com.ardikars.opennetcut.app;

public interface LoggerHandler <T, Z> {
    
    void log(LoggerStatus status, Z message);
    
}
