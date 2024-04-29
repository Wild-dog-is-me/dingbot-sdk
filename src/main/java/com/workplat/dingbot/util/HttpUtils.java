package com.workplat.dingbot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: Odin
 * @Date: 2024/4/29 10:41
 * @Description:
 */
public class HttpUtils {

    private static int timeout;

    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    static {
        try {
            timeout = HttpUtils.getPropertyToInt("request.timeout");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 默认超时时间1分钟
     */
    public static String httpPost(String url, String bodyJsonStr) {
        return httpPost(url, bodyJsonStr, "");
    }

    /**
     * 默认超时时间1分钟
     */
    public static String httpPost(String url, String bodyJsonStr, String logMsg) {
        return httpPost(url, bodyJsonStr, timeout, logMsg);
    }

    /**
     * 默认超时时间1分钟
     */
    public static<T> T httpPost(String url, String bodyJsonStr, Class<T> t) {
        return httpPost(url, bodyJsonStr, t, "");
    }

    /**
     * 默认超时时间1分钟
     */
    public static<T> T httpPost(String url, String bodyJsonStr, Class<T> t, String logMsg) {
        return httpPost(url, bodyJsonStr, t, timeout, logMsg);
    }

    public static<T> T httpPost(String url, String bodyJsonStr, Class<T> t, int timeout) {
        return httpPost(url, bodyJsonStr, t, timeout, "");
    }

    public static<T> T httpPost(String url, String bodyJsonStr, Class<T> t, int timeout, String logMsg) {
        T instance = null;
        try {
            instance = t.newInstance();
        } catch (Exception e) {
            logMsg(e, "POST", url, logMsg);
            e.printStackTrace();
        }
        String result = httpPost(url, bodyJsonStr, timeout, logMsg);
        if(instance instanceof JSONObject) {
            JSONObject jsonObject = JSON.parseObject(result);
            return (T) jsonObject;
        }
        T resultBean = (T) JSONObject.parseObject(result, t);
        return resultBean;
    }

    public static String httpPost(String url, String bodyJsonStr, int timeout) {
        return httpPost(url, bodyJsonStr, timeout, "");
    }

    public static String httpPost(String url, String bodyJsonStr, int timeout, String logMsg) {
        logger.debug("开始发送post请求: {}, 参数jsonStr:{}", url,bodyJsonStr.length() > 200 ? (bodyJsonStr.substring(0, 200) + "...") : bodyJsonStr);
        //创建httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/json;charset=UTF-8");
        httppost.addHeader("Accept", "application/json");
        //设置连接超时时间和数据获取超时时间--单位：ms
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        httppost.setConfig(requestConfig);
        //设置http request body请求体
        if (null != bodyJsonStr) {
            //解决中文乱码问题
            StringEntity myEntity = new StringEntity(bodyJsonStr, "UTF-8");
            httppost.setEntity(myEntity);
        }
        HttpResponse response = null;
        try {
            response = httpClient.execute(httppost);
        } catch (IOException e) {
            logMsg(e, "POST", url, logMsg);
            e.printStackTrace();
        }
        String result= null;
        try {
            result = EntityUtils.toString(response.getEntity(),"UTF-8");
        } catch (IOException e) {
            logMsg(e, "POST", url, logMsg);
            e.printStackTrace();
        }
        //得到返回的字符串
        if (StringUtils.isBlank(result)) {
            logger.warn("Post远程请求接口返回体为空: {}", url);
        }
        try {
            httpClient.close();
        } catch (IOException e) {
            logMsg(e, "POST", url, logMsg);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * httpget调用http接口
     * 默认超时时间1分钟
     */
    public static String httpGet(String url) {
        return httpGet(url, "");
    }

    /**
     * httpget调用http接口
     * 默认超时时间1分钟
     */
    public static String httpGet(String url, String logMsg) {
        return httpGet(url, timeout, logMsg);
    }

    /**
     * httpget调用http接口
     * 默认超时时间1分钟
     */
    public static<T> T httpGet(String url, Class<T> t) {
        return httpGet(url, t, "");
    }

    /**
     * httpget调用http接口
     * 默认超时时间1分钟
     */
    public static<T> T httpGet(String url, Class<T> t, String logMsg) {
        return httpGet(url, t, timeout, logMsg);
    }

    /**
     * httpget调用http接口
     */
    public static<T> T httpGet(String url, Class<T> t, int timeout) {
        return httpGet(url, t, timeout, "");
    }

    /**
     * httpget调用http接口
     */
    public static<T> T httpGet(String url, Class<T> t, int timeout, String logMsg) {
        T instance = null;
        try {
            instance = t.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logMsg(e, "GET", url, logMsg);
            e.printStackTrace();
        }
        String result = httpGet(url, timeout, logMsg);
        if(instance instanceof JSONObject) {
            JSONObject jsonObject =JSON.parseObject(result);
            return (T) jsonObject;
        }
        return (T) JSONObject.parseObject(result, t);
    }

    /**
     * httpget调用http接口
     */
    public static String httpGet(String url, int timeout) {
        return httpGet(url, timeout, "");
    }
    /**
     * httpget调用http接口
     */
    public static String httpGet(String url, int timeout, String logMsg) {
        logger.debug("开始发送get请求: {}", url);
        String result = "";
        //创建httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置连接超时时间和数据获取超时时间--单位：ms
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        //创建实例方法
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Content-Type", "application/json;charset=UTF-8");
        httpget.addHeader("Accept", "application/json");
        httpget.setConfig(requestConfig);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpget);
        } catch (IOException e) {
            logMsg(e, "GET", url, logMsg);
            e.printStackTrace();
        }
        try {
            result= EntityUtils.toString(response.getEntity(),"UTF-8");
        } catch (IOException e) {
            logMsg(e, "GET", url, logMsg);
            e.printStackTrace();
        }
        //得到返回的字符串
        if (StringUtils.isBlank(result)) {
            logger.warn("Get远程请求接口返回体为空: {}", url);
        }
        try {
            httpClient.close();
        } catch (IOException e) {
            logMsg(e, "GET", url, logMsg);
            e.printStackTrace();
        }
        return result;
    }

    private static void logMsg(Exception e, String method, String url, String msg) {
        if (StringUtils.isNotBlank(msg)) {
            logger.error("{}, {}, {}:{}", e.getMessage(), msg, method, url);
        }
    }

    /** Get请求Map参数转换 */
    public static String convertGetUrlParam(String url, Map<String, Object> map) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        if (CollectionUtils.isEmpty(map)) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append("&");
        }
        String newUrl = sb.toString();
        return newUrl.substring(0, newUrl.length() - 1);
    }

    /**
     * 变量参数的获取(Int)
     * @param name 参数名
     * @return int
     */
    public static int getPropertyToInt(String name){
        String propStr = getPropertyToString(name);
        if (propStr == null || propStr.trim().equals("")){
            //logger.warn("RequestUtil：缺少配置[request.timeout], 默认值: 60000");
            return 60000;
        }
        return Integer.parseInt(propStr);
    }

    /**
     * 变量参数的获取(String)
     * @param name 参数名
     * @return String
     */
    public static String getPropertyToString(String name){
        String property = System.getProperty(name);
        if (property == null || property.trim().equals("")){
            return System.getenv(name);
        }
        return property;
    }
}
