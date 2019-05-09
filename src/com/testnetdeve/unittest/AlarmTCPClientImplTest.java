package com.testnetdeve.unittest;

import org.junit.Test;

import java.nio.charset.Charset;

public class AlarmTCPClientImplTest {



    @Test
    public void alarmTcpClientImplTest(){
        AlarmTCPClientImpl alarmTcpClient = new AlarmTCPClientImpl();
        alarmTcpClient.setCharset(Charset.forName("GBK").toString());
        System.out.println(alarmTcpClient.getCharset());


    }
}
