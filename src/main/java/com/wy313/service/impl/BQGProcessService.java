package com.wy313.service.impl;

import com.wy313.entity.Page;
import com.wy313.service.iProcessService;
import com.wy313.tools.LoadProperters;
import com.wy313.tools.MysqlStore;
import com.wy313.tools.PageDown;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.sql.*;
import java.util.*;

public class BQGProcessService implements iProcessService {
    private static String site="https://www.biqudao.com";

    @Override
    public void process(Page page) {
        //

        if(page.getUrl()==null ){
         return;
        }
      //  System.out.println(page.getUrl().length());
        //解析页面顶部分类url
        if("https://www.biqudao.com".equals(page.getUrl())){
            titleBananer(page);
        }
        //分类页面解析
        else if(page.getUrl().length()<32){
            typeurl(page);
        }
        //对应小说页面解析
        else if(page.getUrl().length()<40&&page.getUrl().endsWith("/")){
            info(page);
        }else{
            chapter(page);
        }

    }

    /**
     * 页面标题链接获取
     */
    private   void titleBananer(Page page){
        String content=page.getConetnt();
        HtmlCleaner htmlcleaner=new HtmlCleaner();
        TagNode rootnode=htmlcleaner.clean(content);
        TagNode[] elementsByAttValue = rootnode.getElementsByAttValue("class", "nav", true, true);
        TagNode[] ul = elementsByAttValue[0].getChildTags();
        TagNode[] li = ul[0].getChildTags();
        List<String> menulist=new ArrayList<String>();
        for (TagNode tagNode:li ) {
//            System.out.println(tagNode.getText().toString());
//            System.out.println(tagNode.getChildTags()[0].getAttributeByName("href"));
           String href= tagNode.getChildTags()[0].getAttributeByName("href");
            if(href!=null&&href.length()>2&&href.startsWith("/")&&href.endsWith("/")){
                menulist.add(site+href);
            }
        }
       page.setMenuList(menulist);

    }
    /**
     * 分类页面链接获取
     * list第一个元素存小说类型
     */
    private    void typeurl(Page page){
       // System.out.println("https://www.biqudao.com/bqge31190/");
        String content=page.getConetnt();
        HtmlCleaner htmlcleaner=new HtmlCleaner();
        TagNode rootnode=htmlcleaner.clean(content);
        TagNode[] li = rootnode.getElementsByAttValue("class", "r", true, true)[0].getChildTags()[1].getChildTags();
        String storytype=li[0].getChildTags()[0].getText().toString();
        storytype = storytype.split("\\[")[1].split("\\]")[0];
        //List<String> storelist=new ArrayList<>();
        List<Map<String,String>> storelist=new ArrayList<Map<String,String>>();

        for (TagNode tagNode:li     ) {
            String href=tagNode.getChildTags()[1].getChildTags()[0].getAttributeByName("href");
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put(storytype,site+href);
            storelist.add(stringStringHashMap);

        }
       // System.out.println(storelist);
      page.setPagelist(storelist);
    }
    /**
     * 对应小说页面内容
     */
    private    void info(Page page){

        String content=page.getConetnt();
        HtmlCleaner htmlcleaner=new HtmlCleaner();
        TagNode rootnode=htmlcleaner.clean(content);
        String score = PageDown.getScore(page.getUrl());
        page.setScore(score);
        String img = rootnode.getElementsByAttValue("id", "fmimg", true, true)[0].getChildTags()[0].getAttributeByName("src");
        page.setImage(img);
        String title=rootnode.getElementsByAttValue("id", "fmimg", true, true)[0].getChildTags()[0].getAttributeByName("alt");
        page.setTitle(title);
        TagNode info=rootnode.getElementsByAttValue("id","bookdetail",true,true)[0];
        String author = info.getChildTags()[0].getChildTags()[1].getText().toString();
        author=author.split("：")[1];

        page.setAuthor(author);
        String newtime=info.getChildTags()[0].getChildTags()[3].getText().toString();
        newtime=newtime.split("：")[1];
        page.setNewTime(newtime);
        TagNode introtagnode = info.getElementsByAttValue("id", "intro", true, true)[0];
        String intruo = introtagnode.getText().toString();
        page.setIntro(intruo);

        TagNode listdl = rootnode.getElementsByAttValue("id", "list", true, true)[0];
        TagNode[] dd = listdl.getChildTags()[0].getChildTags();
        boolean status=true;
        Queue<Map<Integer,String>> storelist= new LinkedList<>();
        Connection connection = MysqlStore.connectMysql(LoadProperters.getConfig("db_url"), LoadProperters.getConfig("username"), LoadProperters.getConfig("password"));
        try {
            Statement statement = connection.createStatement();
            String sql="select * from chapter ";
            int i=0;
            //判断是否已经存在数据库中
            ResultSet resultSet = statement.executeQuery("select  * from store where title='" + page.getTitle() + "' and author='" + page.getAuthor() + "'");
            if(resultSet.next()){
                TagNode[] da=null;
                for(TagNode tagNode :dd) {
                    if ("dt".equals(tagNode.getName()) && status) {
                        status = false;
                        continue;
                    }
                    if ("dt".equals(tagNode.getName())) {

                        status = true;
                    }
                    if (!status) {
                        continue;
                    }

                   da = tagNode.getChildTags();
                   if(da.length>0){
                       break;
                   }

                }
                int j=0;
                    String hre = da[0].getAttributeByName("href");
                    String name = da[0].getText().toString();
                    int id = resultSet.getInt("id");
                    ResultSet resultSet1 = statement.executeQuery(sql + "where store_id=" + id + " and chap='" + name + "' order by `order` desc limit 1");
                    if(resultSet1.next()){
                        j=resultSet1.getInt("order");
                    }
                for(TagNode tagNode :dd){
                    if("dt".equals(tagNode.getName())&&status){

                        status=false;
                        continue;
                    }
                    if("dt".equals(tagNode.getName())){

                        status=true;
                    }
                    if(!status){
                        continue;
                    }
                    i++;
                    if(j<=i){
                        continue;
                    }
                    da = tagNode.getChildTags();
                    if(da.length>0) {
                         hre=da[0].getAttributeByName("href");
                         name=da[0].getText().toString();
                        HashMap<Integer, String> integerStringHashMap = new HashMap<>();
                        integerStringHashMap.put(i,site+hre);
                        storelist.add(integerStringHashMap);
                    }
                }

            }else{
                for(TagNode tagNode :dd){
                    if("dt".equals(tagNode.getName())&&status){

                        status=false;
                        continue;
                    }
                    if("dt".equals(tagNode.getName())){

                        status=true;
                    }
                    if(!status){
                        continue;
                    }
                    i++;

                    TagNode[] da = tagNode.getChildTags();
                    if(da.length>0) {
                        String hre=da[0].getAttributeByName("href");
                        String name=da[0].getText().toString();
                        HashMap<Integer, String> integerStringHashMap = new HashMap<>();
                        integerStringHashMap.put(i,site+hre);
                        storelist.add(integerStringHashMap);
                    }
                    }
            }
            page.setStorelist(storelist);
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    /**
     * 章节内容获取
     */
    private  void chapter(Page page){

        String content=page.getConetnt();
        HtmlCleaner htmlcleaner=new HtmlCleaner();
        TagNode rootnode=htmlcleaner.clean(content);
        TagNode bookname = rootnode.getElementsByAttValue("class", "bookname", true, true)[0].getChildTags()[0];
        String chaptername=bookname.getText().toString();
        String  contents = rootnode.getElementsByAttValue("id", "content", true, true)[0].getText().toString();
        //System.out.println(content);
        if("正在手打中，请稍等片刻，内容更新后，需要重新刷新页面，才能获取最新更新".equals(content)){
            HashMap<Integer, String> integerStringHashMap = new HashMap<Integer, String>();
            integerStringHashMap.put(page.getNum(), page.getUrl());
            page.addStorelist(integerStringHashMap);
            return;
        }
        Map<Integer,Map<String,String>> chap=new HashMap<>();
        Map<String,String> chapt=new HashMap<String,String>();
        chapt.put(chaptername,contents);
        chap.put(page.getNum(),chapt);

        page.addChapts(chap);
    }
}
