package com.testnetdeve.custom.client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import com.testnetdeve.NettyConstant;

import com.testnetdeve.custom.proto.AlarmProto;
import com.testnetdeve.custom.proto.MessageProto;
import com.testnetdeve.custom.sensor.GasSensor;
import com.testnetdeve.custom.sensor.InfraredSensor;
import com.testnetdeve.custom.sensor.Sensor;
import com.testnetdeve.custom.sensor.SmokeSensor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class Client {
	private  static Client client;

	//将Channel声明为静态变量类型，类共享这个Channel
	private  static  Channel channel;

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	private ChannelFuture future = null;
	//创建工作线程组
	private EventLoopGroup group = new NioEventLoopGroup();

	private static List<Sensor> list = new ArrayList<>();

	public Client() {
		//初始化传感器事件
		initSensor();

	}

	private void initSensor(){
		list.add(new SmokeSensor());
		list.add(new GasSensor());
		list.add(new InfraredSensor());
		list.add(new SmokeSensor());
		list.add(new InfraredSensor());
		list.add(new SmokeSensor());

	}


	private void initSensorListeners(){
		if(list != null){
			list.get(0).eventHappen(true);
			list.get(1).eventHappen(true);
			list.get(2).eventHappen(true);
			//list.get(3).eventHappen(true);
			//list.get(4).eventHappen(true);
			//list.get(5).eventHappen(true);

		}

	}

	public  static synchronized Client getClientInstance(){
		if(client == null){
			client = new Client();
		}
		return client;
	}

    public EventLoopGroup getGroup() {
        return group;
    }



	public void connect(int port, String host) throws Exception {
		// 配置客户端NIO线程组
		try {

			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new Handlers());

			// 发起异步连接操作，远程IP加本地IP
            future = b.connect(new InetSocketAddress(NettyConstant.REMOTEIP, NettyConstant.PORT),
					new InetSocketAddress(host, port)).sync();
			
			//手动发测试数据，验证是否会产生TCP粘包/拆包情况
            channel = future.channel();


			AlarmProto.Alarm.Builder builder = AlarmProto.Alarm.newBuilder();

			builder.setCommunity("皇后家园");
			builder.setBuildingId(31);
			builder.setBuildingPart("B");
			builder.setCellId(1);
			builder.setRoomId(204);
			builder.setAlarmCategory("火警");
			builder.setAttachment("当前时间");


			MessageProto.MessageBase.Body.Builder body = MessageProto.MessageBase.Body.newBuilder();
			body.setContext(builder.build());

			MessageProto.MessageBase.Header.Builder header =MessageProto.MessageBase.Header.newBuilder();
			header.setType(MessageProto.MessageBase.MessageType.SERVICE_REQ);


			MessageProto.MessageBase.Builder message = MessageProto.MessageBase.newBuilder();
			message.setHeader(header.build());
			message.setBody(body.build());

			for (int i = 0; i < 50; i++) {
				channel.writeAndFlush(message.build());
			}




         //   System.out.println(future);
        //    System.out.println(group);
        //    System.out.println(Thread.currentThread().getName());
//    		NioSocketChannel socketChannel = (NioSocketChannel) channel.parent();
//            System.out.println(socketChannel);


//            ScheduledFuture<?> scheduledFuture = channel.eventLoop().scheduleAtFixedRate(new Runnable() {
//                @Override
//                public void run() {
//                    channel.writeAndFlush(BuildAlarm.buildAlarm("1,2,101,2"));
//                }
//            },0,100000,TimeUnit.SECONDS);


//
//			for (int i = 0; i < 500; i++) {
//				AlarmMessage message = new AlarmMessage();
//				Header header = new Header();
//				header.setSessionID(1001L);
//				header.setPriority((byte) 1);
//				header.setType((byte) 0);
//				message.setHeader(header);
//				message.setBody("我是请求数据" + i);
//				c.writeAndFlush(message);
//			}

//                AlarmMessage message = new AlarmMessage();
//				Header header = new Header();
//				header.setSessionID(1001L);
//				header.setPriority((byte) 1);
//				header.setType((byte) 0);
//				message.setHeader(header);
//				message.setBody("1,2,101,2,");
//				channel.writeAndFlush(message);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("连接操作完成");
                    }else {
                        Throwable throwable = future.cause();
                        throwable.printStackTrace();
                    }
                }
            });

			//initSensorListeners();




//            try{
//
//                if(future.isSuccess()){
//                    System.out.println(channel);
//                    System.out.println("连接成功");
//                    //channel.writeAndFlush(BuildAlarm.buildAlarm("2,1,103,6"));
//                    new SmokeAlarmCommand().execute();
//                }else {
//                    System.out.println("连接失败");
//                }
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }


			future.channel().closeFuture().sync();
		} finally {
			// 所有资源释放完成之后，清空资源，再次发起重连操作
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(1);
						try {
							connect(NettyConstant.PORT, NettyConstant.REMOTEIP);// 发起重连操作
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

//	private class Handlers extends ChannelInitializer<SocketChannel>{
//
//        @Override
//        protected void initChannel(SocketChannel ch) throws Exception {
//            ch.pipeline().addLast(new AlarmMessageDecoder(1024 * 1024, 4, 4));
//            ch.pipeline().addLast(new AlarmMessageEncoder());
//            ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
//            ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
//            ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
//            ch.pipeline().addLast(new ClientHandler());
//        }
//    }

	private class Handlers extends ChannelInitializer<SocketChannel>{


		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1024,0,4,0,4));
			//ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1048576,0,4,0,4));
			// ch.pipeline().addLast("frameDecoder",new ProtobufVarint32FrameDecoder());
			ch.pipeline().addLast("decoder",new ProtobufDecoder(MessageProto.MessageBase.getDefaultInstance()));
			ch.pipeline().addLast("frameEncoder",new LengthFieldPrepender(4));

			//  ch.pipeline().addLast("frameEncoder",new ProtobufVarint32LengthFieldPrepender());
			ch.pipeline().addLast("encoder",new ProtobufEncoder());

			ch.pipeline().addLast(new ClientHandler());
		}
	}

    public ChannelFuture getFuture() {
        return future;
    }

    public synchronized static Channel getChannel() {
        return channel;
    }

	public static List<Sensor> getList() {
		return list;
	}

	public static void main(String[] args) throws Exception {
			new Client().connect(NettyConstant.LOCAL_PORT, NettyConstant.LOCALIP);
    }


}
