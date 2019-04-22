package com.testnetdeve.unittest;

import io.netty.buffer.*;
import org.junit.Test;

import java.nio.charset.Charset;

public class ByteBufTest {

    @Test
    public void testByteBuf(){

        //设置字符集
        Charset utf8 = Charset.forName("UTF-8");

        //创建缓冲区
        ByteBuf buf = Unpooled.copiedBuffer("I love mother",utf8);
        System.out.println(buf.readableBytes());

        //打印缓冲区读和写索引
        System.out.println("Index of reading is : " + buf.readerIndex());
        System.out.println("Index of writing is : " + buf.writerIndex());


        //写字节‘c’
        buf.writeByte('c');

        //查找操作

        int i = buf.indexOf(0,buf.capacity()-1, (byte) 'o');

        System.out.println("The first 'o' exists is :" + i);

        //遍历缓冲区可读数据
        while (buf.isReadable()){
            System.out.println(buf.readByte());
        }
        System.out.println("缓冲区已读完");

        System.out.println("Index of reading is : " + buf.readerIndex());
        System.out.println("Index of writing is : " + buf.writerIndex());

        //复制缓冲区6个字节数据
        ByteBuf aByteBuf = buf.slice(0,5);


        aByteBuf.setByte(0,'a');

        System.out.println("The copied buffer's reference count is :" + aByteBuf.refCnt());

        //副本引用计数器加1
        aByteBuf.retain();

        //打印缓冲区容纳的最大值
        System.out.println("The max number of ByteBuf can hold is : " + buf.maxCapacity());

        //hexdump,字节转换成16进制
        byte[]  bytes = {'a','b','c'};
        System.out.println(ByteBufUtil.hexDump(bytes));


        //判断是否有支撑数组

        System.out.println("buf.hasArray() is :" + buf.hasArray());

        //输出引用计数器

        System.out.println("The number of buffer's reference count is :" + buf.refCnt());
        //比较两个缓冲区的首字节是否相等
        assert aByteBuf.getByte(0) == buf.getByte(0);

        //释放资源
        buf.release();

        System.out.println("The number of buffer's reference count  after releasing is :" + buf.refCnt());
        System.out.println("The copied buffer's reference count is :" + aByteBuf.refCnt());

        //让引用计数器加1


        //buf.writeInt(1);

        ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;

        ByteBuf eByteBuf = allocator.heapBuffer(8);

        eByteBuf.writeLong(1L);
        eByteBuf.writeInt(2);

        System.out.println(eByteBuf.retain().toString());

        System.out.println("The size of eByteBuf is :" + eByteBuf.capacity());

        System.out.println(eByteBuf.readableBytes());
        System.out.println(eByteBuf.getInt(4));










    }

}
