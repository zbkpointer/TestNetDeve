package com.testnetdeve.custom.server;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import com.testnetdeve.MessageType;
import com.testnetdeve.ResultType;
import com.testnetdeve.custom.database.MySQLDB;
import com.testnetdeve.custom.struct.Header;
import com.testnetdeve.custom.struct.AlarmMessage;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

/**
 * @author landyChris
 * @date 2017年8月31日
 * @version 1.0
 */

public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginAuthRespHandler.class);
	/**
	 * 考虑到安全，链路的建立需要通过基于IP地址或者号段的黑白名单安全认证机制，多个IP通过逗号隔开
	 */
	private volatile static  Map<String, String> nodeCheck = new ConcurrentHashMap<String, String>();

	private String[] whiteList = { "127.0.0.1", "192.168.56.1" };




	//private static ChannelLocal<Map<String,String>> clientHolder;


    /**
	 * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward to
	 * the next {@link ChannelHandler} in the {@link ChannelPipeline}.
	 * 
	 * Sub-classes may override this method to change behavior.
	 */



	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		AlarmMessage message = (AlarmMessage) msg;


		// 如果是握手请求消息，处理，其它消息透传
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
			String nodeIndex = ctx.channel().remoteAddress().toString();
			AlarmMessage loginResp = null;
			// 重复登陆，拒绝
			if (nodeCheck.containsKey(nodeIndex)) {
				LOGGER.error("重复登录,拒绝请求!");
				loginResp = buildResponse(ResultType.FAIL);
			} else {
				InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
				String ip = address.getAddress().getHostAddress();
				boolean isOK = false;
				for (String WIP : whiteList) {
					if (WIP.equals(ip)) {
						isOK = true;
						break;
					}
				}
				loginResp = isOK ? buildResponse(ResultType.SUCCESS) : buildResponse(ResultType.FAIL);
				if (isOK)
					nodeCheck.put(nodeIndex, message.getBody().toString());

					//Server.clientMap.put(nodeIndex, message.getBody().toString());
					//给Channel添加属性
				    ctx.channel().attr(Server.MY_KEY).set(nodeCheck);
					//MySQLDB.insertClientInfo((String) message.getBody());
                    System.out.println(nodeCheck.get(nodeIndex));
			}
			LOGGER.info("The login response is : {} body [{}]",loginResp,loginResp.getBody());
			ctx.writeAndFlush(loginResp);
		} else {
			ctx.fireChannelRead(msg);
		//	ctx.writeAndFlush(msg);
		}
	}

	/**
	 * 服务端接到客户端的握手请求消息后，如果IP校验通过，返回握手成功应答消息给客户端，应用层成功建立链路，否则返回验证失败信息。消息格式如下：
	 * 1.消息头的type为4
	 * 2.可选附件个数为0
	 * 3.消息体为byte类型的结果，0表示认证成功，1表示认证失败
	 * @param result
	 * @return
	 */
	private AlarmMessage buildResponse(ResultType result) {
		AlarmMessage message = new AlarmMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_RESP.value());
		message.setHeader(header);
		message.setBody(result.value());
		return message;
	}




    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        String nodeIndex = ctx.channel().remoteAddress().toString();
        ctx.close();

        if(nodeCheck.containsKey(nodeIndex)){
        	try{
				//MySQLDB.updateClientInfo(nodeCheck.get(nodeIndex));
			}catch (Exception e){
        		e.printStackTrace();
			}

            String[] clientInfo = nodeCheck.get(nodeIndex).split(",",-1);
            LOGGER.info(clientInfo[0] + "栋" + clientInfo[1] + "单元" + clientInfo[2] + "室" + "断开了连接！");

            nodeCheck.remove(nodeIndex);
            LOGGER.info("剩余客户端数：" + nodeCheck.size() + "个");

        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		//nodeCheck.remove(ctx.channel().remoteAddress().toString());// 删除缓存
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}

    public  static Map<String, String> getNodeCheck() {
        return nodeCheck;
    }

}
