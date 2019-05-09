package com.testnetdeve.unittest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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





}
