package com.wy313.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * 页面实体类
 */
public class Page {
    //页面类容
    private  String conetnt;
    //作者
    private String author;
    //标题
    private  String title;
    //图片
    private  String image;
    //简介
    private String intro;
    //评分
    private String score;
    //最后更新时间
    private String newTime;
    //分类
    private String typeName;
    //小说主页路径
    private String mainurl;
    //路径
    private String url;
    //菜单url
    private List<?> menuList;
    //小说分类url
    private List<?> pagelist;
    //小说目录url
    private  Queue<String> Storelist;
    //章节内容
    private List<Map<String,String>> chapts=new ArrayList<>();


    public String getMainurl() {
        return mainurl;
    }

    public void setMainurl(String mainurl) {
        this.mainurl = mainurl;
    }

    public String pollStorelist(){
        return this.Storelist.poll();
    }

    public void addChapts(  Map<String,String> chap){
        this.chapts.add(chap);
    }

    public List<Map<String,String>> getChapts() {
        return chapts;
    }

    public void setChapts(List<  Map<String,String>> chapts) {
        this.chapts = chapts;
    }

    public void addStorelist(String lists){
        this.Storelist.add(lists);
    }

    public Queue<String> getStorelist() {
        return Storelist;
    }

    public void setStorelist(Queue<String> storelist) {
        Storelist = storelist;
    }

    public List<?> getPagelist() {
        return pagelist;
    }


    public void setPagelist(List<?> pagelist) {
        this.pagelist = pagelist;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public List<?> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<?> menuList) {
        this.menuList = menuList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getConetnt() {
        return conetnt;
    }

    public void setConetnt(String conetnt) {
        this.conetnt = conetnt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getNewTime() {
        return newTime;
    }

    public void setNewTime(String newTime) {
        this.newTime = newTime;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
