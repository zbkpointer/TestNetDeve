package com.testnetdeve.custom.sensor;

import com.testnetdeve.custom.alarmhandle.BuildAlarm;
import com.testnetdeve.custom.client.Client;

public class InfraredSensor extends Sensor {
    public static final String name = "InfraredSensor";
    private SensorEventListener sensorEventListener;

    public InfraredSensor() {
        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged() {
                Client.getChannel().writeAndFlush(BuildAlarm.buildReqAlarmMessage("1,2,101,3"));
            }
        };
    }

    @Override
    public void addListener(SensorEventListener listener) {
        this.sensorEventListener = listener;
    }

    @Override
    public void eventHappen(boolean happen) {
        if (happen){
            this.sensorEventListener.onSensorChanged();
        }
    }
}
