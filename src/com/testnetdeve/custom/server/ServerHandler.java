package com.testnetdeve.custom.server;

import com.testnetdeve.custom.proto.MessageProto;
import com.testnetdeve.custom.struct.Header;
import com.testnetdeve.custom.struct.AlarmMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

	/**
	 * 当我们通道进行激活的时候 触发的监听方法
	 */
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println(ctx.channel());
//
//    	System.err.println("--------通道激活------------");
//    }
//
    /**
     * 当我们的通道里有数据进行读取的时候 触发的监听方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
//    	AlarmMessage requestMessage = (AlarmMessage)msg;
    	
//    	System.err.println("Server receive message from Client: " + requestMessage.getBody());

//    	AlarmMessage responseMessage = new AlarmMessage();
//		Header header = new Header();
//		header.setSessionID(2002L);
//		header.setPriority((byte)2);
//		header.setType((byte)1);
//		responseMessage.setHeader(header);
//		responseMessage.setBody("我是响应数据: " + requestMessage.getBody());
//		ctx.writeAndFlush(responseMessage);

        //职责链末端，销毁消息对象

//        MessageProto.MessageBase messageBase = (MessageProto.MessageBase)msg;
//        ctx.writeAndFlush(messageBase);
//
//        System.out.println(messageBase.getHeader().getType().getNumber());
//        System.out.println(messageBase.getBody().getContext().getBuildingPart());

//        String str = (String) msg;
//        System.out.println(str);
        /**
         * 计算业务的执行时间
         */
//        long startTime = System.nanoTime();
//        ctx.writeAndFlush((String)msg+"\n").addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                long elapsedNanos = System.nanoTime() - startTime;
//                System.out.println("the elapsed Nanoseconds is :" + elapsedNanos + "*10(-9)s");
//            }
//        });// loop message back

        ctx.writeAndFlush((String)msg+"\n");
        ReferenceCountUtil.release(msg);
    }
    
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//       System.err.println("--------数据读取完毕----------");
//    }
//
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();

        ctx.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    System.out.println(future.channel() + "-->has been closed");
                    System.out.println(Server.numClose++);
                }
            }
        });

    	System.err.println("--------服务器数据读异常----------: ");


    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
