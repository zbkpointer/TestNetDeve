package com.testnetdeve.custom.sensor;

import com.testnetdeve.custom.alarmhandle.BuildAlarm;
import com.testnetdeve.custom.client.Client;

public class SmokeSensor extends Sensor {
    private final String name = "SmokeSensor";
    private SensorEventListener sensorEventListener;


    public SmokeSensor() {
        //初始化就要添加回调监听事件
        this.addListener(new SensorEventListener() {
            @Override
            public void onSensorChanged() {
                //Client.getChannel().writeAndFlush(BuildAlarm.buildReqAlarmMessage("1,2,101,1"));
            }
        });
    }



    @Override
    public void addListener(SensorEventListener listener) {
        this.sensorEventListener = listener;
    }

    @Override
    public void eventHappen(boolean happen)
    {
        if (happen){
            sensorEventListener.onSensorChanged();
        }
    }
}
