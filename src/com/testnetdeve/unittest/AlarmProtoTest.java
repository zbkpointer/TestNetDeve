package com.testnetdeve.unittest;


import com.google.protobuf.InvalidProtocolBufferException;
import com.testnetdeve.custom.proto.AlarmProto;
import com.testnetdeve.custom.proto.MessageProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.junit.Test;

public class AlarmProtoTest {


    @Test
    public void protoTest() throws InvalidProtocolBufferException {


        AlarmProto.Alarm.Builder builder = AlarmProto.Alarm.newBuilder();
        builder.setCommunity("皇后家园");
        builder.setBuildingId(31);
        builder.setBuildingPart("B");
        builder.setCellId(1);
        builder.setRoomId(204);
        builder.setAlarmCategory("火警");
        builder.setAttachment("当前时间");


        System.out.println(builder.toString());

        AlarmProto.Alarm alarm1 = decode(encode(builder.build()));

        System.out.println(alarm1.toString());

        assert  alarm1.equals(builder.build());



    }


    private static byte[] encode(AlarmProto.Alarm alarm){
        return alarm.toByteArray();
    }

    private static AlarmProto.Alarm decode(byte[] bytes) throws InvalidProtocolBufferException {

        return AlarmProto.Alarm.parseFrom(bytes);
    }



    @Test
    public void testAlarmProto(){

        AlarmProto.Alarm.Builder builder = AlarmProto.Alarm.newBuilder();

        builder.setCommunity("皇后家园");
        builder.setBuildingId(31);
        builder.setBuildingPart("B");
        builder.setCellId(1);
        builder.setRoomId(204);
        builder.setAlarmCategory("火警");
        builder.setAttachment("当前时间");

        EmbeddedChannel ch = new EmbeddedChannel();

        ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1048576,0,4,0,4));
       // ch.pipeline().addLast("frameDecoder",new ProtobufVarint32FrameDecoder());
        ch.pipeline().addLast("decoder",new ProtobufDecoder(AlarmProto.Alarm.getDefaultInstance()));
        ch.pipeline().addLast("frameEncoder",new LengthFieldPrepender(4));
      //  ch.pipeline().addLast("frameEncoder",new ProtobufVarint32LengthFieldPrepender());
        ch.pipeline().addLast("encoder",new ProtobufEncoder());

//        System.out.println(builder.build().toString());
//        //把消息序列化
//        byte[] bytes = builder.build().toByteArray();
//
//        System.out.println("序列化后的消息长度为:  " + bytes.length);
//
//        ByteBuf buf = Unpooled.copiedBuffer(bytes);
//
//        System.out.println(buf.readableBytes());
//
//        assert ch.writeInbound(buf);
//        assert ch.finish();
//
//        AlarmProto.Alarm alarm = ch.readInbound();
//
//        System.out.println(alarm.toString());


        /**
         * TODO：先写入数据进行编码，然后再读出数据进行解码
         */
        ch.writeOutbound(builder.build());

        ByteBuf byteBuf = ch.readOutbound();
        System.out.println(byteBuf.toString());

        ch.writeInbound(byteBuf);
        ch.finish();
        AlarmProto.Alarm alarm1 = ch.readInbound();

        System.out.println(alarm1.getCommunity());


    }



    @Test
    public void testAlarmProto1(){

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



        EmbeddedChannel ch = new EmbeddedChannel();

        ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1048576,0,4,0,4));
     //   ch.pipeline().addLast("frameDecoder",new ProtobufVarint32FrameDecoder());
        ch.pipeline().addLast("decoder",new ProtobufDecoder(MessageProto.MessageBase.getDefaultInstance()));
        ch.pipeline().addLast("frameEncoder",new LengthFieldPrepender(4));

     //   ch.pipeline().addLast("frameEncoder",new ProtobufVarint32LengthFieldPrepender());
        ch.pipeline().addLast("encoder",new ProtobufEncoder());

//        System.out.println(builder.build().toString());
//        //把消息序列化
//        byte[] bytes = builder.build().toByteArray();
//
//        System.out.println("序列化后的消息长度为:  " + bytes.length);
//
//        ByteBuf buf = Unpooled.copiedBuffer(bytes);
//
//        System.out.println(buf.readableBytes());
//
//        assert ch.writeInbound(buf);
//        assert ch.finish();
//
//        AlarmProto.Alarm alarm = ch.readInbound();
//
//        System.out.println(alarm.toString());


        /**
         * TODO：先写入数据进行编码，然后再读出数据进行解码
         */
        ch.writeOutbound(message.build());
        //ch.writeOutbound(message.build());
        //ch.flushInbound();

        ByteBuf buf = null;




        ByteBuf byteBuf = ch.readOutbound();
        ByteBuf byteBuf2 = ch.readOutbound();



        ByteBuf byteBuf3 = Unpooled.compositeBuffer();
        byteBuf3.writeBytes(byteBuf);
        byteBuf3.writeBytes(byteBuf2);
        System.out.println("number of readableBytes is: "+byteBuf3.readableBytes()+"\n"+"capacity of byte is: ");


        ch.writeInbound(byteBuf3);
        ch.finish();
        MessageProto.MessageBase messageBase = ch.readInbound();

        System.out.println(messageBase.getHeader().getType().getNumber());
        System.out.println(messageBase.getBody().getContext().getBuildingPart());


    }




}
