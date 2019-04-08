package com.testnetdeve.custom.server;


import com.testnetdeve.MessageType;
import com.testnetdeve.custom.database.ClientIdsEventListener;
import com.testnetdeve.custom.struct.AlarmMessage;
import com.testnetdeve.custom.struct.Header;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.*;

/**
 * 由于采用长连接通信，在正常业务运行期间，双方通过心跳和业务消息维持链路，任何一方都不需要主动关闭链接。<br/>
 * 但是在以下情况，客户端和服务端都需要关闭连接。<br/>
 * 1.当对方宕机或者重启时，会主动关闭链接，另一方读取到操作系统的通知信号，得知对方REST链路，需要关闭连接，释放自身的句柄等资源，<br/>
 * 由于采用全双工通信，双方都需要关闭连接，节省资源<br/>
 * 2.消息读写过程中发生IO异常，需要主动关闭连接。<br/>
 * 3.心跳消息读写过程发送IO异常，需要主动关闭连接。<br/>
 * 4.心跳超时，需要主动关闭连接。<br/>
 * 5.发生编码异常，需要主动关闭连接。<br/>
 * 
 * @author zbk
 * @date 2018年11月22日
 * @version 1.0
 */

public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatRespHandler.class);

	private static HashSet<String> clientIds = new HashSet<>();

	private static HashSet<String> clientIdsClone = new HashSet<>();
	//负责处理定时清空缓存
	//private final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);

	//private final ExecutorService exc = Executors.newSingleThreadExecutor();

	private ClientIdsEventListener clientIdsEventListener;



	public HeartBeatRespHandler() {

////		int count = 101;
////		for (int i = 0; i < 1000; i++) {
////			count++;
////			clientIds.add("1,1,"+String.valueOf(count));
////		}
//
//		this.addListener(new ClientIdsEventListener() {
//			@Override
//			public void onClientIdsClone(HashSet<String> hashSet) {
//				Server.exc.execute(new Runnable() {
//					@Override
//					public void run() {
//						/**
//						 * 执行数据库操作
//						 */
////						long startTime = System.currentTimeMillis();
////						MySQLDB.updateClientStatus(clientIdsClone);
////						long endTime = System.currentTimeMillis();
////						System.out.println("时间："+(endTime - startTime));
//
//						LOGGER.info("hah");
//
//						clientIdsClone.clear();
//					}
//				});
//			}
//		});
//
//		//定时任务清空住户列表
//
//		ScheduledFuture<?> scheduledFuture = Server.scheduled.scheduleAtFixedRate(new Runnable() {
//			@Override
//			public void run() {
//				UpdateClientIdRunnable updateClientIdRunnable = new UpdateClientIdRunnable();
//
//				//拿到住户表的副本进行数据库操作
//				clientIdsClone = (HashSet<String>) clientIds.clone();
//
//				clientIdsEventListener.onClientIdsClone(clientIdsClone);
//				//10s内当前住户表清空
//				clientIds.clear();
//				System.out.println("住户表已清空！！！");
//				LOGGER.info("clean the clientId list,the size of list is: ---> {}",clientIds.size());
//
//			}
//		}, 10, 10, TimeUnit.SECONDS);





	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		AlarmMessage message = (AlarmMessage) msg;

        /**
         *  获取信息中的Body信息,住户    字符串
         *  更新数据库表中状态信息
         *
         */

		Channel channel = ctx.channel();

		ChannelPipeline channelPipeline = ctx.pipeline();

		System.out.println(message);
		// 返回心跳应答消息
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
			LOGGER.info("Receive client heart beat message : ---> {} " ,message);
			//加入请求消息体内容，即住户信息
			String body = (String) message.getBody();
			clientIds.add(body);

			//回复消息
			AlarmMessage heartBeat = buildHeatBeat();
			LOGGER.info("Send heart beat response message to client : ---> {}" ,heartBeat);
			ctx.writeAndFlush(heartBeat);

			/**
			 * 测试传经整个ChannelPipeline中的ChannelHandle,from end to head
			 */

			//channel.writeAndFlush(heartBeat);

			/**
			 * 测试传经整个ChannelPipeline中的ChannelHandle,from end to head
			 */

			//channelPipeline.writeAndFlush(heartBeat);

			/**
			 * 测试，channel属性值在ChannelHandlers之间传递
			 */
			Map<String,String> clientMap = ctx.channel().attr(Server.MY_KEY).get();
            for (String key:clientMap.keySet()) {
                LOGGER.info("*** {} ***",clientMap.get(key));
            }

		} else {

			//ReferenceCountUtil.release(msg);
			ctx.fireChannelRead(msg);
		}
	}

	private AlarmMessage buildHeatBeat() {
		AlarmMessage message = new AlarmMessage();
		Header header = new Header();
		header.setType(MessageType.HEARTBEAT_RESP.value());
		message.setHeader(header);
		return message;
	}

	private void addListener(ClientIdsEventListener listener){

		clientIdsEventListener = listener;
	}

//
//	public static void multiThread(UpdateClientIdRunnable runnable, int executeCount, int coreThreadCount) {
//		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
//		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(coreThreadCount, 200, 2000, TimeUnit.SECONDS, queue);
//		for (int i = 0; i < executeCount; i++) {
//			threadPool.execute(runnable);
//		}
//	}



}
