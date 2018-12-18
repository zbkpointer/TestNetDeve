package com.testnetdeve.custom.alarm;

import com.testnetdeve.custom.alarm.AlarmCommandAdapter;
import com.testnetdeve.custom.alarmhandle.BuildAlarm;
import com.testnetdeve.custom.client.Client;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SmokeAlarmCommand implements AlarmCommand {

    @Override
    public void execute() {
        /**
         * 调用客户端的EventGroup
         * 动态增加pipeLine
         */
        //客户端单例能够得到
        //但是得不到通道的实例
//        ScheduledFuture<?> scheduledFuture=Client.getClientInstance().getGroup().next()
//                .scheduleAtFixedRate(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("我可以执行了");
//                   // System.out.println(Thread.currentThread().getName());
//                   // Client.getChannel().writeAndFlush(BuildAlarm.buildAlarm("1,2,101,2"));
//                    System.out.println(Client.getChannel());
//
//                }
//            },0,3,TimeUnit.SECONDS);

        System.out.println(Client.getChannel().pipeline().first());
       // System.out.println(Client.getChannel());

    }

    public static void main(String[] args) {
        SmokeAlarmCommand smokeAlarmCommand = new SmokeAlarmCommand();
        smokeAlarmCommand.execute();
    }

}
