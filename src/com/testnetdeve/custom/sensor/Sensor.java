package com.testnetdeve.custom.sensor;

public abstract class Sensor {

    public abstract void addListener(SensorEventListener listener);
    public abstract void eventHappen(boolean happen);
}
