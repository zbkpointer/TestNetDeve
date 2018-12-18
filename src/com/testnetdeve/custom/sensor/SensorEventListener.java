package com.testnetdeve.custom.sensor;

import java.util.EventListener;

public interface SensorEventListener extends EventListener {

    void onSensorChanged();

}
