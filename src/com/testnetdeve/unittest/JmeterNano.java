package com.testnetdeve.unittest;

import com.testnetdeve.NettyConstant;
import com.testnetdeve.custom.client.Client;
import com.testnetdeve.custom.proto.AlarmProto;
import com.testnetdeve.custom.proto.MessageProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.spi.SyncResolver;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;


public class JmeterNano implements JavaSamplerClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmeterNano.class);

    private int local_port;
    private String local_ip;
    private int remote_port;
    private String remote_ip;

    private static Client client ;


    public JmeterNano(){
        client = new Client();
    }


    /**
     *
     * @return Arguments
     * TODO:初始化显示参数
     */
    @Override
    public Arguments getDefaultParameters() {
        LOGGER.info(this.getClass().getName() + "getDefaultParameters()");
        Arguments paras = new Arguments();
        paras.addArgument("本机端口",String.valueOf(NettyConstant.LOCAL_PORT));
        paras.addArgument("本机IP地址",NettyConstant.LOCALIP);
        paras.addArgument("服务器端口",String.valueOf(NettyConstant.PORT));
        paras.addArgument("服务器IP地址",NettyConstant.REMOTEIP);
        return paras;
    }

    /**
     * @param context
     * TODO: 设置测试，只执行一次
     * 开启客户端与服务器的连接，设置连接参数
     */

    @Override
    public void setupTest(JavaSamplerContext context) {

        LOGGER.info(this.getClass().getName() + "setupTest");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(this.whoAmI() + "\tsetupTest()");
            this.listParameters(context);
        }

        this.local_port  = Integer.valueOf(context.getParameter("本机端口",String.valueOf(NettyConstant.LOCAL_PORT)));
        this.local_ip    = context.getParameter("本机IP地址",NettyConstant.LOCALIP);
        this.remote_port = Integer.valueOf(context.getParameter("服务器端口",String.valueOf(NettyConstant.LOCAL_PORT)));
        this.remote_ip   = context.getParameter("服务器IP地址",NettyConstant.LOCALIP);

        try {
            client = new Client();
            client.connect(remote_port, remote_ip,local_port,local_ip);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    /**
     *
     * @param javaSamplerContext
     * @return SampleResult
     * TODO: 在测试中的每一个线程都要执行这个逻辑方法
     * 发送协议数据给服务端
     */
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        LOGGER.info(this.getClass().getName() + "runTest");
        SampleResult results = new SampleResult();
        boolean success = true;

        MessageProto.MessageBase message = createMessage();
        try {
            results.setSentBytes(message.toString().getBytes("UTF-8").length);
            results.setRequestHeaders(message.getHeader().toString());
            client.getChannel().writeAndFlush(message);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        results.setResponseCode("200");
        results.setResponseMessage("成功");

        results.sampleStart();


        results.sampleEnd();
        results.setSuccessful(success);
        return results;
    }

    /**
     *
     * @param context
     * TODO：销毁资源
     */
    @Override
    public void teardownTest(JavaSamplerContext context) {
        LOGGER.info(this.getClass().getName() + "teardownTest");
        try {
            client.getChannel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private String whoAmI() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Thread.currentThread().toString());
        stringBuilder.append("@");
        stringBuilder.append(Integer.toHexString(this.hashCode()));
        return stringBuilder.toString();
    }

    private void listParameters(JavaSamplerContext context) {
        Iterator argsIt = context.getParameterNamesIterator();

        while(argsIt.hasNext()) {
            String lName = (String)argsIt.next();
            LOGGER.info(lName + "=" + context.getParameter(lName));
        }

    }


    /**
     *
     * @return MessageProto.MessageBase
     * @TODO 构建发送信息
     */
    private MessageProto.MessageBase createMessage(){
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


        return message.build();

    }


    public static void main(String[] args) {
        JmeterNano jmeterNano = null;
        try{
            jmeterNano = new JmeterNano();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jmeterNano != null){
                JavaSamplerContext javaSamplerContext = new JavaSamplerContext(jmeterNano.getDefaultParameters());
                jmeterNano.setupTest(javaSamplerContext);
                jmeterNano.runTest(javaSamplerContext);
            }

        }

    }


}
