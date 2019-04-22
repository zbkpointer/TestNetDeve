package com.testnetdeve.unittest;

import com.testnetdeve.custom.server.FixedLengthFrameDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.*;


public class FixedLengthFrameDecoderTest {

    @Test
    public void testFramesDecoder(){
        ByteBuf byteBuf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            byteBuf.writeByte(i);
        }

        ByteBuf input = byteBuf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

        //写数据
        assertTrue(channel.writeInbound(input.retain()));
        assertTrue(channel.finish());

        //读数据
        ByteBuf read = channel.readInbound();
        assertEquals(byteBuf.readSlice(3),read);
        read.release();

        read = channel.readInbound();
        assertEquals(byteBuf.readSlice(3),read);
        read.release();


        read = channel.readInbound();
        assertEquals(byteBuf.readSlice(3),read);
        read.release();

        assertNull(channel.readInbound());
        byteBuf.release();




    }

    @Test
    public void testByteBuf(){
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf byteBuf = Unpooled.copiedBuffer("Netty in action",utf8);
        System.out.println((char)byteBuf.getByte(0));
        ByteBuf sliced = byteBuf.slice(0,15);
        sliced.setByte(0,'J');
        System.out.println(byteBuf.getByte(0));
        assert sliced.getByte(0) == byteBuf.getByte(0);

    }




}
