package com.testnetdeve.custom.time;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/6/15.
 */

public class Time {

    public static String getTime(){

        Date date= new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置时间显示格式
        String str = sdf.format(date);//将当前时间格式化为需要的类型
        return str;
    }
}
