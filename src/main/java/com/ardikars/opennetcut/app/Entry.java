package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.MacAddress;

import java.util.concurrent.TimeUnit;

public class Entry {

    private long arrivalTime;
    private MacAddress macAddress;

    public long getArrivalTime() {
        return arrivalTime;
    }

    public Entry() {
        this.arrivalTime = System.currentTimeMillis();
    }

    public long getEpochTime() {
        long end = System.currentTimeMillis();
        return (end - this.arrivalTime) / 1000;
    }

    public MacAddress getMacAddress() {
        return macAddress;
    }

    public Entry setMacAddress(MacAddress macAddress) {
        this.macAddress = macAddress;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MacAddress) {
            MacAddress macAddress = (MacAddress) obj;
            return this.macAddress.equals(macAddress);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 17 * 37 + Long.hashCode(arrivalTime) +
                super.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("[Arrival time: " + arrivalTime)
                .append("]").toString();
    }

}
