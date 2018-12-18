package com.testnetdeve.custom.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class MyDataSource {

    //链表 --- 实现栈结构
    private static LinkedList<Connection> dataSources = new LinkedList<Connection>();
    //初始化连接数量
    MyDataSource() {
        //一次性创建12个连接
        for(int i = 0; i < 12; i++) {
            try {
                //1、装载驱动对象
                Class.forName("com.mysql.jdbc.Driver");
                //2、通过JDBC建立数据库连接
                Connection con =DriverManager.getConnection("jdbc:mysql://localhost:3306/admin?useSSL=false","root","199387");
                //3、将连接加入连接池中
                dataSources.add(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized static Connection getConnection() throws SQLException {
        //取出连接池中一个连接
        final Connection conn = dataSources.removeFirst(); // 删除第一个连接返回
        return conn;
    }

    //将连接放回连接池
    public  synchronized static void releaseConnection(Connection conn) {
        dataSources.add(conn);
    }


}
