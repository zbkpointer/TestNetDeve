package com.testnetdeve.custom.server;

import com.testnetdeve.MessageType;
import com.testnetdeve.custom.alarmhandle.BuildAlarm;
import com.testnetdeve.custom.behavior.OpenExplorer;
import com.testnetdeve.custom.database.MySQLDB;
import com.testnetdeve.custom.struct.AlarmMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.*;

public class AlarmMessageRespHandler extends ChannelInboundHandlerAdapter {

 //   private static ExecutorService exc = Executors.newSingleThreadExecutor();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AlarmMessage message = (AlarmMessage) msg;
        String[] readMessageSplit = null;

        if (message.getHeader() != null && message.getHeader().getType() == MessageType.SERVICE_REQ.value()){
            String body = (String)message.getBody();
            if(body != null) {
                readMessageSplit = body.split(",", -1);

                /**
                 * @trouble 数据库插入成功，但是浏览器只打开最后一个报警页面
                 * @howtohandle 如何解决
                 * @A 数据库插入操作执行完毕，浏览器在最后一次操作后打开浏览器
                 * @B 打开浏览器的方法(关注)
                 */
//                String[] finalReadMessageSplit = readMessageSplit;
//                exc.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        MySQLDB.insert(finalReadMessageSplit);
//                        try {
//                           OpenExplorer.browse("http://localhost:8888/alertPrompt.php");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }                                                                                                                                                                                                             //                        ctx.writeAndFlush(BuildAlarm.buildRespAlarmMessage("已经处理"));
//                    }
//                });

                    MySQLDB.insertAlertInfo(readMessageSplit);
                    Thread.sleep(500);
                    OpenExplorer.browse("http://localhost:8888/alertPrompt.php");
                    ctx.writeAndFlush(BuildAlarm.buildRespAlarmMessage("已经处理"));





            }


        }else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("--------服务器数据读异常----------: ");
        cause.printStackTrace();
        ctx.close();
    }


}
