package com.testnetdeve.custom.server;

import com.testnetdeve.custom.database.MySQLDB;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;


public class BusinessHandler extends ChannelDuplexHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessHandler.class);

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info(ctx.name() + "已加入" + ctx.pipeline());
        ScheduledFuture<?> sf = executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                HashSet<String> clients = new HashSet<>();
                Map<String,String> map = LoginAuthRespHandler.getNodeCheck();


                //遍历整个map
                for (String key:map.keySet()) {

                    clients.add(map.get(key));
                }
                //数据库表状态更新

                System.out.println("在线住户数量为：" + clients.size());

                try{

                    MySQLDB.updateClientStatus(clients);

                }catch (Exception e){

                    e.printStackTrace();
                }


            }
        },10,10,TimeUnit.SECONDS);


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}

