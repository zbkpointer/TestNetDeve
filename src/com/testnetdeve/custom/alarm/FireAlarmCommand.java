package com.testnetdeve.custom.alarm;


import com.testnetdeve.custom.alarmhandle.AlarmMessageReqHandler;

public class FireAlarmCommand implements AlarmCommand{

    @Override
    public void execute() {
        /**
         * 调用客户端的EventGroup
         * 动态增加pipeLine
         */
      // this.client.getChannel().pipeline().addLast("AlarmMessageReqHandler",new AlarmMessageReqHandler());

    }
}
