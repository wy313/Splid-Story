package com.wy313.tools;

import com.mysql.cj.jdbc.JdbcConnection;
import com.wy313.entity.Page;


import javax.swing.*;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MysqlStore {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/store?serverTimezone=UTC&characterEncoding=UTF-8";

    /**
     * 连接数据库
     * @param db_url
     * @param username
     * @param password
     * @return
     */
    public static Connection connectMysql(String db_url, String username, String password){
        try {
            Class.forName(JDBC_DRIVER);
            try {
                Connection conn= DriverManager.getConnection(db_url,username,password);
                return conn;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("连接失败");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加小说
     * @param connection
     * @param page
     * @return
     */
    public static Boolean addStore(Connection connection,Page page){
        String store = findStore(connection, page.getTitle(), page.getAuthor());
        if(store!=null){
            System.out.println(page.getTitle()+"小说已存在");
            return true;
        }
        try {
            Statement statement = connection.createStatement();
            //插入小说

            String sql="insert into store(title,img,url,author,intro,score,newtime,typename) value('"+page.getTitle()+"','"+page.getImage()+"','"+page.getMainurl()+"','"+page.getAuthor()+"','"+page.getIntro()+"','"+page.getScore()+"','"+new Date().getTime()+"','"+page.getTypeName()+"')";
            boolean execute = statement.execute(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

         }

    /**
     * 查询小说id
     * @param connection
     * @param name
     * @param author
     * @return
     */

    public static  String findStore(Connection connection,String name,String author){
        String id=null;
        String sql="select id from store where `title`='"+name+"' and `author`='"+author+"'";
             try {
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql);
                 if(resultSet.next()){
                     id=resultSet.getString("id");
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }


             return id;
         }

    /**
     * 保存小说章节
     * @param connection
     * @param page
     * @return
     */
    public static  Boolean saverChates(Connection connection,Page page){
        String store = findStore(connection, page.getTitle(), page.getAuthor());
        if(store==null){
            addStore(connection,page);
            store = findStore(connection, page.getTitle(), page.getAuthor());
        }
        try {
            connection.createStatement().execute("set names utf8");

            Statement statement = connection.createStatement();
            List<Map<Integer,Map<String, String>>> chapts = page.getChapts();

            //prepareStatement+addBatch缓存方式
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into chapter(store_id,chap,content,create_time,`order`) values(?,?,?,?,?)");
            int i=0;
            for(Map<Integer,Map<String, String>> map :chapts){
                i++;
                int i1 = Integer.parseInt(map.keySet().toArray()[0].toString());
                preparedStatement.setInt(1,Integer.parseInt(store));
                preparedStatement.setString(2,map.get(i1).keySet().toArray()[0].toString());
                preparedStatement.setString(3,map.get(i1).values().toArray()[0].toString());
                preparedStatement.setString(4,Long.toString(new Date().getTime()));
                preparedStatement.setInt(5,i1);
                preparedStatement.addBatch();

                if(i%100==0){
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            preparedStatement.executeBatch();
          connection.commit();


            //prepareStatement方式
//            connection.setAutoCommit(false);
//            PreparedStatement preparedStatement = connection.prepareStatement("insert into chapter(store_id,chap,content,create_time) values(?,?,?,?)");
//            for(Map<String, String> map :chapts) {
//                preparedStatement.setInt(1, Integer.parseInt(store));
//                preparedStatement.setString(2, map.keySet().toArray()[0].toString());
//                preparedStatement.setString(3, map.values().toArray()[0].toString());
//                preparedStatement.setString(4, Long.toString(new Date().getTime()));
//                boolean execute = preparedStatement.execute();
//            }
//            connection.commit();


            //   //直接插入方式
//            for(Map<String,String>map:chapts){
//                String sql="insert into chapter(store_id,chap,content,create_time) values('"+store+"','"+map.keySet().toArray()[0].toString()+"','"+map.values().toArray()[0].toString()+"','"+Long.toString(new Date().getTime())+"')";
//                boolean execute = statement.execute(sql);
//
//            }




            return true;




        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
//        try {


//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
        //return true;
    }

    /**
     * 查询章节是否已经存在
     * @param connection
     * @param sotreId
     * @param name
     * @return
     */
    public static  Boolean findChates(Connection connection,String sotreId,String name){
        try {
            Statement statement = connection.createStatement();
            String sql="select * from chapter where sotre_id="+sotreId+" and chap='"+name+"'";
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
}
