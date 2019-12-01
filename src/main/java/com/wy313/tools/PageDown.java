package com.wy313.tools;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 页面下载类
 */
public class PageDown {

    /**
     * 下载页面
     * @param url
     * @return主体内容
     */

    public  static String downinfo(String url){
        String content = null;

        try {
            SSLConnectionSocketFactory sslsf = createSSLConnSocketFactory();
            HttpClientBuilder builde = HttpClients.custom().setSSLSocketFactory(sslsf);

            CloseableHttpClient client = builde.build();
            HttpGet requst = new HttpGet(url);
            requst.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            requst.setHeader("Accept-Language","zh-CN,zh;q=0.9");
            requst.setHeader("Connection","keep-alive");
            requst.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");

            HttpResponse response;
            response = null;
            try {
                response  =client.execute(requst);
                System.out.println(response.getStatusLine().getStatusCode());
                HttpEntity entity = response.getEntity();
                content= EntityUtils.toString(entity,"UTF-8");


            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return content;
    }

    /**
     * 获取分数
     * @return
     * @throws Exception
     */
    public static  String getScore(String url){
        StringBuffer content=new StringBuffer();
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher id= p.matcher(url);
        String ids=id.replaceAll("").trim().toString();
        url="https://www.biqudao.com/ad.php"+"?action=GetScore&BookId="+ids+"&t="+new Date().getTime();
       // System.out.println(url);
        try {
            SSLConnectionSocketFactory sslsf = createSSLConnSocketFactory();
            HttpClientBuilder builde = HttpClients.custom().setSSLSocketFactory(sslsf);

            CloseableHttpClient client = builde.build();
            HttpGet requst = new HttpGet(url);
            requst.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            requst.setHeader("Accept-Language","zh-CN,zh;q=0.9");
            requst.setHeader("Connection","keep-alive");
            requst.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");

            HttpResponse response;
            response = null;
            try {
                response  =client.execute(requst);
                System.out.println(response.getStatusLine().getStatusCode());
                HttpEntity entity = response.getEntity();
                content.append( EntityUtils.toString(entity,"UTF-8"));

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        int t = content.lastIndexOf("\"t\":");
        int s = content.indexOf(",\"s\":[");
        int end = content.indexOf("]}");
        String sum=content.substring(t+4,s);

        if(Integer.parseInt(sum)>1){
            String ars=content.substring(s+7,end);
           String[] ar = ars.split(",");
           for(int i=0;i<ar.length;i++){
               if(ar[i].length()<1){
                   ar[i]="0";
               }
           }
            content.delete(0,content.length());
            double v = (Integer.parseInt(ar[1]) * 2 + Integer.parseInt(ar[1]) * 4 + Integer.parseInt(ar[2]) * 6 + Integer.parseInt(ar[3]) * 8 + Integer.parseInt(ar[4]) * 10) * 1.0 / (Integer.parseInt(sum) - Integer.parseInt(ar[0]));

            content.append(String.format("%.1f",v));
        }else{
            content.delete(0,content.length());
            content.append(sum);

        }
        return content.toString();
    }

    // ssl通道证书的创建
    private static SSLConnectionSocketFactory createSSLConnSocketFactory()
            throws Exception {
        SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(
                        new File(
                                "trust.keystore"),
                        "123456".toCharArray(), new TrustSelfSignedStrategy())   //文件和密码要对应
                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        return sslsf;
    }

    public static void main(String[] args) {
        Object downinfo = downinfo("https://www.biqudao.com/bqge178758/");
        System.out.println(downinfo);
    }

}
