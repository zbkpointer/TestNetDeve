package com.testnetdeve.custom.alarmhandle;

import com.testnetdeve.MessageType;
import com.testnetdeve.custom.struct.AlarmMessage;
import com.testnetdeve.custom.struct.Header;

public class BuildAlarm {

    public static AlarmMessage buildReqAlarmMessage(String str){
        AlarmMessage alarmMessage = new AlarmMessage();
        Header header = new Header();
        header.setType(MessageType.SERVICE_REQ.value());
        alarmMessage.setBody(str);
        alarmMessage.setHeader(header);
        return alarmMessage;

    }

    public static AlarmMessage buildRespAlarmMessage(String str){
        AlarmMessage alarmMessage = new AlarmMessage();
        Header header = new Header();
        header.setType(MessageType.SERVICE_RESP.value());
        alarmMessage.setBody(str);
        alarmMessage.setHeader(header);
        return alarmMessage;

    }
}
