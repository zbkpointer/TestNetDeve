package com.testnetdeve.custom.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
    private int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if(frameLength < 0){
            throw new IllegalArgumentException("frameLength must be a positive integer:" + frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= frameLength){

            ByteBuf byteBuf = in.readBytes(frameLength);

            //将帧添加到已被解码的消息列表中
            out.add(byteBuf);

        }
    }
}
