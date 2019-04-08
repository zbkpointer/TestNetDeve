package com.testnetdeve.custom.codec;

import java.io.IOException;
import java.util.Map;


import com.testnetdeve.custom.struct.AlarmMessage;
import com.testnetdeve.custom.struct.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class AlarmMessageEncoder extends MessageToByteEncoder<AlarmMessage> {

	private MarshallingEncoder marshallingEncoder;
	
	public AlarmMessageEncoder() throws IOException {
		this.marshallingEncoder = new MarshallingEncoder();
	}
	
	
	@Override
	protected void encode(ChannelHandlerContext ctx, AlarmMessage message, ByteBuf sendBuf) throws Exception {
		if(message == null || message.getHeader() == null){
			throw new Exception("编码失败,没有数据信息!");
		}
		
		//Head:
		Header header = message.getHeader();
		sendBuf.writeInt(header.getCrcCode());//校验码
		sendBuf.writeInt(header.getLength());//总长度
		sendBuf.writeLong(header.getSessionID());//会话id
		sendBuf.writeByte(header.getType());//消息类型
		sendBuf.writeByte(header.getPriority());//优先级
		
		//对附件信息进行编码
		//编码规则为：如果attachment的长度为0，表示没有可选附件，则将长度	编码设置为0
		//如果attachment长度大于0，则需要编码，规则：
		//首先对附件的个数进行编码
		sendBuf.writeInt((header.getAttachment().size())); //附件大小
		String key = null;
		byte[] keyArray = null;
		Object value = null;
		//然后对key进行编码，先编码长度，然后再将它转化为byte数组之后编码内容
		for (Map.Entry<String, Object> param : header.getAttachment()
			.entrySet()) {
		    key = param.getKey();
		    keyArray = key.getBytes("UTF-8");
		    sendBuf.writeInt(keyArray.length);//key的字符编码长度
		    sendBuf.writeBytes(keyArray);
			value = param.getValue();
		    marshallingEncoder.encode(value, sendBuf);
		}
		key = null;
		keyArray = null;
		value = null;
		
		//Body:
		Object body = message.getBody();
		//如果不为空 说明: 有数据
		if(body != null){
			//使用MarshallingEncoder
			this.marshallingEncoder.encode(body, sendBuf);
		} else {
			//如果没有数据 则进行补位 为了方便后续的 decoder操作
			sendBuf.writeInt(0);
		}
		
		//最后我们要获取整个数据包的总长度 也就是 header +  body 进行对 header length的设置

		//TODO：解码前后的数据一致，所以长度字段的值是其后所有数据的字节数之和
		//总长度是在header协议的第二个标记字段中
		//第一个参数是长度属性的索引位置

		sendBuf.setInt(4, sendBuf.readableBytes() - 8);

		
		
		
		
	}

}
