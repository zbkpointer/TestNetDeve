package com.testnetdeve.custom.client;



import com.testnetdeve.MessageType;
import com.testnetdeve.custom.struct.AlarmMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler  extends ChannelInboundHandlerAdapter {


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	System.err.println("----------客户端数据读异常-----------");
        ctx.close();
    }



}
