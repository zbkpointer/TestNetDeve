package com.testnetdeve.custom.server;

import com.testnetdeve.NettyConstant;
import com.testnetdeve.custom.proto.MessageProto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.*;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.*;

public class Server {
    public static final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);

    public static final ExecutorService exc = Executors.newSingleThreadExecutor();

    //存储客户端信息
    public static final Map<String,String> clientMap = new ConcurrentHashMap<>();

    static final EventExecutorGroup group = new DefaultEventExecutorGroup(1500);

    int[] a = new int[4];

    public static int numClose = 0;

    //构建缓存


    public final static AttributeKey<Map<String,String>> MY_KEY =  AttributeKey.valueOf("zbk");

    public static void main(String[] args) throws Exception {


        //1 用于接受客户端连接的线程工作组
        EventLoopGroup boss = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*3);
        //2 用于对接受客户端连接读写操作的线程工作组
        EventLoopGroup work = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*3);

        //执行高耗时操作
       // EventExecutor extractExecutor = new UnorderedThreadPoolEventExecutor(10);

       // ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();



        //事件被EventExecutorGroup的某个EventExecutor执行，从ChannelPipeline中移除
        //EventExecutorGroup eventExecutor = new DefaultEventExecutorGroup(1);


        //BusinessHandler businessHandler = new BusinessHandler();

        //3 辅助类。用于帮助创建NETTY服务
        ServerBootstrap b = new ServerBootstrap();

        b.group(boss, work)	//绑定两个工作线程组
                .channel(NioServerSocketChannel.class)	//设置NIO的模式
                .option(ChannelOption.SO_BACKLOG, 200*20)	//设置TCP最大连接数
                .option(ChannelOption.SO_RCVBUF, 16*1024)	// 设置 接受数据的缓存大小
                .option(ChannelOption.WRITE_SPIN_COUNT,8)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)	// 设置保持连接
               // .childOption(ChannelOption.SO_SNDBUF, 32*1024)
                // 初始化绑定服务通道
                .childHandler(new ChildChandler());


//        eventExecutor.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//
//                HashSet<String> clients = new HashSet<>();
//
//                Map<String,String> map = LoginAuthRespHandler.getNodeCheck();
//
//                //遍历整个map
//                for (String key:map.keySet()) {
//
//                    clients.add(map.get(key));
//                }
//
//                System.out.println("在线住户数量为：" + clients.size());
//
//                //数据库表状态更新
//                MySQLDB.updateClientStatus(clients);
//
//
//
//            }
//        },10,10,TimeUnit.SECONDS);



        ChannelFuture cf = b.bind(NettyConstant.REMOTEIP,NettyConstant.PORT).sync();

        /**
         * 打印服务器socket连接配置
         */
        ServerSocketChannelConfig channelConfig = (ServerSocketChannelConfig) cf.channel().config();
        Map<ChannelOption<?>,Object> configMap = channelConfig.getOptions();
        for (Map.Entry<ChannelOption<?>, Object> option:
             configMap.entrySet()) {
            System.out.println(option);
        }





        System.out.println("Netty server start ok on: "
                + (NettyConstant.REMOTEIP + " : " + NettyConstant.PORT));

        //释放连接
        cf.channel().closeFuture().sync();
        work.shutdownGracefully();
        boss.shutdownGracefully();
    }



    private static class ChildChandler extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel sc) throws Exception {

            sc.pipeline().addLast("lineDecoder",new LineBasedFrameDecoder(60));
            sc.pipeline().addLast("decoder",new StringDecoder(Charset.forName("UTF8")));
            sc.pipeline().addLast("encoder",new StringEncoder(Charset.forName("UTF8")));




//sc.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1048576,0,4,0,4));
//            // ch.pipeline().addLast("frameDecoder",new ProtobufVarint32FrameDecoder());
//            sc.pipeline().addLast("decoder",new ProtobufDecoder(MessageProto.MessageBase.getDefaultInstance()));
//            sc.pipeline().addLast("frameEncoder",new LengthFieldPrepender(4));
//
//            //  ch.pipeline().addLast("frameEncoder",new ProtobufVarint32LengthFieldPrepender());
//            sc.pipeline().addLast("encoder",new ProtobufEncoder());


//                        sc.pipeline().addLast(new AlarmMessageDecoder(1024*1024*5, 4, 4));
//                        sc.pipeline().addLast(new AlarmMessageEncoder());
//                        sc.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(50));
//                        sc.pipeline().addLast("LoginAuthHandler",new LoginAuthRespHandler());
//                        sc.pipeline().addLast("HeartBeatHandler",new HeartBeatRespHandler());
//                       // sc.pipeline().addLast("AlarmMessageHandle",new AlarmMessageRespHandler());
//                       // sc.pipeline().addLast(eventExecutor,new BusinessHandler());
            sc.pipeline().addLast(Server.group,"serverHandler",new ServerHandler());





        }
    }


}