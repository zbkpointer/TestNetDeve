package com.testnetdeve.custom.database;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBCPTest {
    // BasicDataSource 也就是DBCP所使用数据源
    private static BasicDataSource basicDataSource;

    /**
     * 读取配置文件，并且初始化连接池
     */
    private static void init(){
        // 根据上面所放类路径读取配置文件
        //取出系统的文件层次分隔符
        String separator = System.getProperty("file.separator");
        Properties properties = new Properties();
        FileInputStream inputStream =null;

        //配置文件路径
        String path = System.getProperty("user.dir")+separator+"resource"+separator+"dbcp_config.properties";

        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            // 加载properties文件
            if(inputStream != null){
                properties.load(inputStream);
            }
            // 新建一个BasicDataSource
            basicDataSource = new BasicDataSource();

            // 设置对应的参数
            basicDataSource.setUrl(properties.getProperty("db.url"));
            basicDataSource.setDriverClassName(properties.getProperty("db.driverClassName"));
            basicDataSource.setUsername(properties.getProperty("db.username"));
            basicDataSource.setPassword(properties.getProperty("db.password"));

            basicDataSource.setInitialSize(Integer.parseInt(properties.getProperty("dataSource.initialSize")));
            basicDataSource.setMaxIdle(Integer.parseInt(properties.getProperty("dataSource.maxIdle")));
            basicDataSource.setMinIdle(Integer.parseInt(properties.getProperty("dataSource.minIdle")));
            basicDataSource.setMaxWaitMillis(Integer.parseInt(properties.getProperty("dataSource.maxWait")));
            basicDataSource.setMaxTotal(Integer.parseInt(properties.getProperty("dataSource.maxActive")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得Connection
     * @return Connection
     */
    public synchronized static Connection getConnection(){
        if (basicDataSource == null){
            init();
        }
        try {
            return basicDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        try {
            Connection connection =  getConnection();
            if (connection != null) {
                String sql = "select * from fa_admin";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    System.out.printf("%s %s\n", resultSet.getString("username"), resultSet.getString("password"));
                }
            }else{
                System.out.println("获取Connection失败");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }






    }


}
