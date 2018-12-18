package com.testnetdeve.custom.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class updateData {


    public static void updateClientStatus() throws Exception{

        long startTime = System.currentTimeMillis();
        //1、使用连接池建立数据库连接
        MyDataSource dataSource = new MyDataSource();

        //2、获得连接

        Connection conn = null;


        conn = dataSource.getConnection();
        conn.setAutoCommit(false);  //设置自动提交为false

        //3、建立SQL语句
        String sql = "update fa_client_status set onlinestatus = 0 where id = ?";

        //4、建立sql对象

        PreparedStatement psmt = conn.prepareStatement(sql);

        ResultSet rs = selectTable();

        psmt.clearBatch();
        int count = 1;
        while (rs.next()){

            psmt.setInt(1,count);
            psmt.addBatch();
            count++;

        }

        psmt.executeBatch();

        conn.commit();


        //6、归还数据库连接给连接池并关闭连接
//        psmt.close();
        dataSource.releaseConnection(conn);
        System.out.println(count);
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime));



    }


    public static ResultSet selectTable() throws Exception{

        //1、使用连接池建立数据库连接
        MyDataSource dataSource = new MyDataSource();

        //2、获得连接

        Connection conn = null;


        conn = dataSource.getConnection();
        conn.setAutoCommit(false);  //设置自动提交为false


        String querySql="select id from fa_client_status";
        PreparedStatement psmt;

        psmt =(PreparedStatement)conn.prepareStatement(querySql);
        ResultSet rs = psmt.executeQuery();

        return rs;
    }

    public static void main(String[] args) {
        try {
            updateClientStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
