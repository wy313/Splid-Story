package com.wy313.service.impl;

import com.wy313.entity.Page;
import com.wy313.service.iStoreService;
import com.wy313.tools.LoadProperters;
import com.wy313.tools.MysqlStore;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * MySQL存储实现类
 */
public class MysqlStoreService implements iStoreService {
    @Override
    public void store(Page page) {
        Connection connection = MysqlStore.connectMysql(LoadProperters.getConfig("db_url"), LoadProperters.getConfig("username"), LoadProperters.getConfig("password"));
        Boolean store = MysqlStore.addStore(connection, page);
        if(store){

            Boolean chates = MysqlStore.saverChates(connection, page);
            if(chates){
                System.out.println("章节插入成功");
            }else{
                System.out.println("章节插入失败");
            }
        }else{
            System.out.println("小说添加失败");
        }

        //数据库连接关闭
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
