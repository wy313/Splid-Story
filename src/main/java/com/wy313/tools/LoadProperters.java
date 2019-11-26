package com.wy313.tools;

import java.util.Locale;
import java.util.ResourceBundle;

public class LoadProperters {
    //获取全局配置
    public  static  String getConfig(String key){
        String value="";
        Locale local= Locale.getDefault();
        try{
            ResourceBundle localRouese;
            localRouese = ResourceBundle.getBundle("config",local);
            value=localRouese.getString(key);
            return value;

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }
}
