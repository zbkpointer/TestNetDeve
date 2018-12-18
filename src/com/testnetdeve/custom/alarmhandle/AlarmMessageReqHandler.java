package com.testnetdeve.custom.alarmhandle;

import com.testnetdeve.MessageType;
import com.testnetdeve.custom.struct.AlarmMessage;
import com.testnetdeve.custom.struct.Header;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AlarmMessageReqHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("发送报警信息");
        ctx.writeAndFlush(BuildAlarm.buildReqAlarmMessage("1,2,101,2"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("--------数据读异常----------: ");
        cause.printStackTrace();
        ctx.close();
    }


}



