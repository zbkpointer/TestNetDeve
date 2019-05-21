package com.testnetdeve.unittest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.PlatformDependent;
import jdk.jfr.Description;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GeneralTest {


    @Test
    public void charsetTest(){

        System.out.println(Charset.defaultCharset());

        System.out.println(Charset.isSupported("GBK"));

    }

    @Test
    public void gbkTest(){

        String str = "hello server\r\n";

        byte[] gbk_bytes = str.getBytes(Charset.forName("GBK"));

        byte[] utf8_bytes = str.getBytes(Charset.forName("UTF8"));

        ByteBuf gbk_byteBuf = Unpooled.copiedBuffer(gbk_bytes);
        ByteBuf utf8_byteBuf = Unpooled.copiedBuffer(utf8_bytes);



        for (int i = 0; i < gbk_byteBuf.readableBytes(); i++) {
            System.out.println(gbk_byteBuf.getByte(i));
        }

        System.out.println("\r");

        for (int i = 0; i < utf8_byteBuf.readableBytes(); i++) {
            System.out.println(utf8_byteBuf.getByte(i));
        }

        //判断二者是否相等，包含的值
        assert gbk_byteBuf.equals(utf8_byteBuf);
    }



    @Test

    public void cpuTest(){

        int cpuCoreNum = Runtime.getRuntime().availableProcessors();

        System.out.println(cpuCoreNum);


        byte[] bytes = "\n".getBytes();
        System.out.println(bytes.length);
    }


    @Test
    public void genecTest(){

        List<String>  list = new ArrayList();
        list.add("a");

        for (String s:list
             ) {
            System.out.println(s);
        }

       // assert list.get(0).equals("a");


    }


    @Test
    public void eventLoopGroupTest(){

        EventLoopGroup group = new NioEventLoopGroup(0);
        int count = 0;

        while (group.next() != null){
            count++;
        }
        System.out.println(count);
    }


    @Test
    public void threadCategoryTest(){


        Task1 task1 = new Task1();
        for (int i = 0; i < 10; i++) {
            new Thread(task1).start();
        }

    }


    private class Task1 implements Runnable{

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());
        }
    }

    @Test
    public void backlogNumTest(){



        System.out.println(PlatformDependent.bitMode());


        System.out.println(PlatformDependent.maxDirectMemory());

        System.out.println(ChannelOption.SO_BACKLOG);




    }



}
