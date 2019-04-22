package com.testnetdeve.unittest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class AlarmProtoPrepender extends MessageToByteEncoder<ByteBuf> {



    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int dataLength = msg.readableBytes();
        ByteBuf lengthByteBuf = Unpooled.buffer();
        lengthByteBuf.writeInt(dataLength);
        ByteBuf compositeByteBuf =Unpooled.compositeBuffer();
        compositeByteBuf.writeBytes(lengthByteBuf);
        compositeByteBuf.writeBytes(msg);

        out.writeBytes(compositeByteBuf);



    }
}
