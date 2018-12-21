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

@ChannelHandler.Sharable
public class BusinessHandler extends ChannelDuplexHandler {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ScheduledFuture<?> sf = ctx.executor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                HashSet<String> clients = new HashSet<>();
                Map<String,String> map = LoginAuthRespHandler.getNodeCheck();
                System.out.println(map.size());

                //遍历整个map
                for (String key:map.keySet()) {

                    clients.add(map.get(key));
                }
                //数据库表状态更新\
                try{

                    MySQLDB.updateClientStatus(clients);

                }catch (Exception e){

                    e.printStackTrace();
                }

                System.out.println("在线住户数量为：" + clients.size());

                //清空缓存
                map.clear();
                clients.clear();
            }
        },10,10,TimeUnit.SECONDS);
    }
}
