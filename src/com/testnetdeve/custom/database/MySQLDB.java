package com.testnetdeve.custom.database;

/**
 * Created by Administrator on 2018/6/13.
 */
import com.testnetdeve.custom.struct.AlarmMessage;
import com.testnetdeve.custom.time.Time;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class MySQLDB {

    // JDBC 驱动名及数据库 URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/admin?useSSL=false";
    // 数据库的用户名与密码，需要根据自己的设置
    private static final String USER = "root";
    private static final String PASS = "199387";
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MySQLDB.class);
    private static final int THREAD_COUNT = 12;
    private static AtomicInteger countInteger = new AtomicInteger(0);
    private AtomicInteger next = new AtomicInteger(0);
    private static CountDownLatch threadCompletedCounter = new CountDownLatch(THREAD_COUNT);
    private static AtomicInteger updCounter = new AtomicInteger(0);
    private static HashSet<String> sqlList = new HashSet<>();
    private static List<String[]> updateList = new ArrayList<>();
    private static List<String[]> insertList = new ArrayList<>();

    //开启数据库连接
    private static  Connection getConn(){
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
            LOGGER.info("数据库连接成功");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 插入报警信息
     * @param strings
     * @return boolean
     */
    public static boolean insertAlertInfo(String[] strings) {
        Connection conn = getConn();
        int count =0;
        int i = 0;
        String querySql="select * from fa_alertinfo";
        String sql = "insert into fa_alertinfo (id,idofbuilding,cellofbuilding,idofroom,alertcategory,alerttime,alertstatus,alertstamp) values(?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt =conn.prepareStatement(querySql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                count++;
            }
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1,Integer.toString(count+1));
            pstmt.setString(2, strings[0]+"栋");
            pstmt.setString(3, strings[1]+"单元");
            pstmt.setString(4, strings[2]+"室");
            pstmt.setString(5, getAlarmType(strings[3]));
            pstmt.setString(6, Time.getTime());
            pstmt.setString(7,"未处理");
            pstmt.setString(8, String.valueOf(System.currentTimeMillis()));
            i = pstmt.executeUpdate();


            System.out.println("报告成功");
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i > 0 ;
    }

    /**
     * 插入住户信息
     * @param str
     * @return boolean
     */

    public synchronized static void insertClientInfo(String str){

        Connection conn =getConn();
        if(conn != null){
            if(str != null) {
                String[] strings = str.split(",", -1);
                try {
                    String insertSql = "insert into fa_client_status (idofbuilding,cellofbuilding,idofroom,onlinestatus) values(?,?,?,?)";
                    PreparedStatement prest = conn.prepareStatement(insertSql);

                    prest.setString(1, strings[0]);
                    prest.setString(2, strings[1]);
                    prest.setString(3, strings[2]);
                    //插入1表明是在线
                    prest.setInt(4, 1);
                    prest.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    /**
     * 删除住户信息
     * @param str
     * @return boolean
     */

    public static void updateClientInfo(String str){

        Connection conn =getConn();
        if(conn != null){
            if(str != null) {
                String[] strings = str.split(",", -1);
                try {
                    String insertSql = "update fa_client_status set onlinestatus = 0 where idofbuilding = ? and cellofbuilding = ? and idofroom = ?";
                    PreparedStatement prest = conn.prepareStatement(insertSql);

                    prest.setString(1, strings[0]);
                    prest.setString(2, strings[1]);
                    prest.setString(3, strings[2]);

                    prest.executeUpdate();
                    LOGGER.info("更新成功");

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public static void update(HashSet<String> hashSet){
        Connection conn = getConn();
        Iterator iterator = hashSet.iterator();//获取迭代器
        String querySql="select * from MacAddress";
        PreparedStatement pstmt;

        int count =0;

        try {

        pstmt =(PreparedStatement)conn.prepareStatement(querySql);
        ResultSet rs = pstmt.executeQuery();
        /**
         * 扫描整张表获取记录数
         */
        while(rs.next()){
            count++;
        }
        /**更新整张表状态为下线
         *
         */
        while(count != 0){
            String sql = "update MacAddress set State = '0' where ID = "+ String.valueOf(count);
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            count--;
        }



        while(iterator.hasNext()) {

            String sql = "update MacAddress set State = '1' where MacAddress = "+ iterator.next();
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            System.out.println("更新成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }



    /**
     *
     *更新住户在线状态表
     *
     */
    public synchronized static void updateClientStatus(HashSet<String> clientIds){
        if(clientIds.size() >= 1) {


            Connection conn = null;

            try {
                conn = getConn();
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Iterator iteratorClientIds = clientIds.iterator();


            long currentTime = System.currentTimeMillis();
            try {

                /**
                 * @function 更新整张表状态为下线
                 */
                String sql = "update fa_client_status set onlinestatus = 0";
                PreparedStatement prest = conn.prepareStatement(sql);

                prest.executeUpdate();
                conn.commit();


            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            long endTime = System.currentTimeMillis();

            LOGGER.info("更新下线状态的执行时间为-->" + (endTime - currentTime) + "毫秒");


            try {
                String selectSql = "select * from fa_client_status";
                PreparedStatement prest = conn.prepareStatement(selectSql);
                ResultSet rs = prest.executeQuery();
                conn.commit();
                while (rs.next()) {
                    String[] strings = new String[3];
                    strings[0] = rs.getString("idofbuilding");
                    strings[1] = rs.getString("cellofbuilding");
                    strings[2] = rs.getString("idofroom");
                    String str = strings[0] + "," + strings[1] + "," + strings[2];
                    //System.out.println(str);

                    sqlList.add(str);

                }

                LOGGER.info("数据库表中有" + sqlList.size() + "条记录");
            } catch (SQLException e) {
                e.printStackTrace();
            }


            long startSelectTime = System.currentTimeMillis();

            while (iteratorClientIds.hasNext()) {
                //缓存住户字符串
                String str = (String) iteratorClientIds.next();
                String[] strings = str.split(",", -1);
                iteratorClientIds.remove();


                if (sqlList.contains(str)) {
                    //如果数据库有此条数据，就更新
                    updateList.add(strings);
                } else {

                    insertList.add(strings);
                }

            }

            long endSelectTime = System.currentTimeMillis();
            LOGGER.info("查询执行时间：" + (endSelectTime - startSelectTime) / 1000.f + "秒");

            LOGGER.info("需要插入的数据量：" + insertList.size());

            LOGGER.info("需要更新的数据量：" + updateList.size());

            if (updateList != null) {

                Iterator updateIterator = updateList.iterator();
                updateList = null;
                try {

                    String updateSql = "update fa_client_status set onlinestatus = 1 where idofbuilding = ? " +
                            "and cellofbuilding = ? and idofroom = ?";
                    PreparedStatement prestUpdate = conn.prepareStatement(updateSql);

                    while (updateIterator.hasNext()) {
                        String[] strings = (String[]) updateIterator.next();
                        updateIterator.remove();
                        prestUpdate.setString(1, strings[0]);
                        prestUpdate.setString(2, strings[1]);
                        prestUpdate.setString(3, strings[2]);
                        prestUpdate.addBatch();
                    }
                    if (prestUpdate != null) {
                        long startUpdateTime = System.currentTimeMillis();

                        prestUpdate.executeBatch();
                        conn.commit();

                        long endUpdateTime = System.currentTimeMillis();
                        prestUpdate.clearBatch();
                        LOGGER.info("执行更新时间为：" + (endUpdateTime - startUpdateTime) / 1000.f + "秒");
                        LOGGER.info("更新成功");
                    } else {
                        LOGGER.info("没有需要更新的值");
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (insertList != null) {

                Iterator insertIterator = insertList.iterator();
                insertList = null;

                try {

                    PreparedStatement prestInsert = null;
                    String insertSql = "insert into fa_client_status (idofbuilding,cellofbuilding,idofroom,onlinestatus) values(?,?,?,?)";
                    prestInsert = conn.prepareStatement(insertSql);
                    while (insertIterator.hasNext()) {
                        String[] strings = (String[]) insertIterator.next();
                        insertIterator.remove();

                        prestInsert.setString(1, strings[0]);
                        prestInsert.setString(2, strings[1]);
                        prestInsert.setString(3, strings[2]);
                        //插入1表明是在线
                        prestInsert.setInt(4, 1);

                        prestInsert.addBatch();
                    }
                    if (prestInsert != null) {

                        prestInsert.executeBatch();
                        conn.commit();
                        prestInsert.clearBatch();
                    } else {
                        LOGGER.info("没有新的值需要插入");
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


            try {

                conn.close();
                LOGGER.info("数据库关闭");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }



    private static void updateClientIdOffline(final int count){
        Connection conn = getConn();
        PreparedStatement pstmt;
        String sql = "update fa_client_status set onlinestatus = 0 where id = "+ String.valueOf(count);

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }





    private static void multiThreads(final Iterator<String> clientIds) {

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(new Runnable() {
                public void run() {
                    String clientInfo = getNext(clientIds);
                    while (null != clientInfo) {
                        try {
                            if(select(clientInfo.split(",",-1))){
                                update(clientInfo.split(",",-1));
                            }
                            insert(clientInfo.split(",",-1));

                        } catch (Exception e) {
                            //logger.error("table weiboPerson update error ", e);
                        }
                        updCounter.incrementAndGet();
						System.out.println("Thread:" + Thread.currentThread().getName() + ", counter:" + updCounter + ", name:" + clientInfo);
                        clientInfo = getNext(clientIds);
                    }
                    threadCompletedCounter.countDown();
                }
            });
        }
        updCounter = new AtomicInteger(0);
        closeThreadPool(executor);
    }


    private static synchronized String getNext(Iterator<String> iterator){
        if(!iterator.hasNext()){
            return null;
        }
        return iterator.next();
    }

    private static void update(final String[] strings){

        Connection conn = DBCPTest.getConnection();
        if(conn != null){

            try {
                String updateSql = "update fa_client_status set onlinestatus = 1 where idofbuilding =" + strings[0] +" and cellofbuilding =" + strings[1] + " and idofroom ="+ strings[2];
                PreparedStatement prest = conn.prepareStatement(updateSql);
                prest.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


    }

    private static void insert(final String[] strings){

        Connection conn =DBCPTest.getConnection();
        if(conn != null){

            try {
                String insertSql = "insert into fa_client_status (idofbuilding,cellofbuilding,idofroom,onlinestatus) values(?,?,?,?)";
                PreparedStatement prest = conn.prepareStatement(insertSql);

                prest.setString(1, strings[0]);
                prest.setString(2, strings[1]);
                prest.setString(3, strings[2]);
                //插入1表明是在线
                prest.setInt(4,1);
                prest.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

    }




    private static boolean select(final String[] strings){
        Connection conn =DBCPTest.getConnection();
        boolean exist = false;
        try {
            //查询状态表中数据，如果有就更新，否则就插入,语句中一定注意要有空格
            String querySql = "select id from fa_client_status where idofbuilding =" + strings[0] +" and cellofbuilding =" + strings[1] + " and idofroom ="+ strings[2];
            if(conn != null){
                PreparedStatement pstmt = conn.prepareStatement(querySql);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()){
                    exist = true;
                }

                rs = null;
            }



        }catch (SQLException e){
            e.printStackTrace();
        }

        return exist;

    }


    private static void testGetNext() throws Exception {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("1");

        Iterator iterator = hashSet.iterator();

        if(iterator.hasNext()){
            System.out.println("有");
        }

        System.out.println(iterator.next());
        try {
            System.out.println(iterator.next());
        }finally {
            throw new Exception("不存在元素了");
        }

    }

    /**
     * 关闭线程池
     */
    private static void closeThreadPool(final ExecutorService executor) {
        try {
            threadCompletedCounter.await();
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    private static String getAlarmType(String str){
        String alarmType = null;
        if(Integer.valueOf(str) == 1){
            alarmType = "火警";
        }else if (Integer.valueOf(str) == 2){
            alarmType = "有毒气体报警";
        }else if(Integer.valueOf(str) == 3){
            alarmType = "红外报警";
        }else if(Integer.valueOf(str) == 4){
            alarmType = "入侵报警";
        }
        return alarmType;
    }




    public static void main(String[] args) throws SQLException {
        HashSet<String> hashSet = new HashSet<>();

        /**
         * @TEST 测试
         * 插入10000条数据
         * @ExecuteTime 约 2.6s
         */
//        for (int i = 1; i < 11; i++) {
//            for (int j = 1; j < 11; j++) {
//                for (int k = 1; k < 101; k++) {
//                    hashSet.add(String.valueOf(i)+","+String.valueOf(j)+","+String.valueOf(k));
//                }
//            }
//        }

        /**
         * @TEST
         * 更新1000条数据
         * @ExecuteTime 约 0.26s
         */
//        for (int i = 1; i < 11; i++) {
//            for (int j = 1; j < 101 ; j++) {
//                hashSet.add("1,"+String.valueOf(i)+","+String.valueOf(j));
//            }
//
//        }

        long currentTime = System.currentTimeMillis();
        try{
            MySQLDB.updateClientStatus(hashSet);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

//
//       // multiThreads(hashSet.iterator());
//

//        Iterator iterator = hashSet.iterator();
//        while (iterator.hasNext()){
//
//            System.out.println(iterator.next().toString());
//            iterator.remove();
//        }
        long endTime = System.currentTimeMillis();

        LOGGER.info("执行时间为-->"+ (endTime - currentTime)/1000.0f+"秒");

       // System.out.println("本机CPU数:"+Runtime.getRuntime().availableProcessors());

//        try {
//            testGetNext();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        String str = "1,1,1008";
//        String[] string3 = str.split(",",-1);
//        System.out.println(string3[0]);
//        System.out.println(string3[1]);
//        System.out.println(string3[2]);


    }





}
