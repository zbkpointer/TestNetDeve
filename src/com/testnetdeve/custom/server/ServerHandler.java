package com.testnetdeve.custom.server;

import com.testnetdeve.custom.struct.Header;
import com.testnetdeve.custom.struct.AlarmMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	/**
	 * 当我们通道进行激活的时候 触发的监听方法
	 */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().id());
    
    	System.err.println("--------通道激活------------");
    }
	
    /**
     * 当我们的通道里有数据进行读取的时候 触发的监听方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx /*NETTY服务上下文*/, Object msg /*实际的传输数据*/) throws Exception {
    	
    	AlarmMessage requestMessage = (AlarmMessage)msg;
    	
    	System.err.println("Server receive message from Client: " + requestMessage.getBody());

//    	AlarmMessage responseMessage = new AlarmMessage();
//		Header header = new Header();
//		header.setSessionID(2002L);
//		header.setPriority((byte)2);
//		header.setType((byte)1);
//		responseMessage.setHeader(header);
//		responseMessage.setBody("我是响应数据: " + requestMessage.getBody());
//		ctx.writeAndFlush(responseMessage);

        //职责链末端，销毁消息对象、
        ReferenceCountUtil.release(msg);
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
       System.err.println("--------数据读取完毕----------");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	System.err.println("--------服务器数据读异常----------: ");
    	cause.printStackTrace();
        ctx.close();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
