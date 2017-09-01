package com.lpan.utils.weixinShare;

import com.alibaba.fastjson.JSONObject;
import com.lpan.utils.jedis.RedisUtil;
import com.lpan.utils.tools.StringUtils;
import com.lpan.utils.tools.WebContext;
import org.apache.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信js 签名类
 *
 * @author Administrator
 * @version V1.0 创建时间：2015年6月24日 下午4:00:29
 *          Copyright 2015 by PreTang
 */
public class WeixinUtil {

    private static final Logger log = Logger.getLogger(WeixinUtil.class);

    public static final String appID = "wxb35ada164b420099";
    public static final String appSecret = "a537fa3715d6428cb77481af113ff3b1";

    /**
     * 获取微信js 加密数据
     *
     * @return 返回wx.config需要的数据
     */
    public static Map<String, String> sign() {
        String jsapi_ticket = getAccessTicket();
        if (StringUtils.isNullOrEmpty(jsapi_ticket)) {
            return new HashMap<String, String>();
        }
        String url = getCurrentUrl();
        if (StringUtils.isNullOrEmpty(url)) {
            return new HashMap<String, String>();
        }
        return Sign.sign(jsapi_ticket, url);
    }

    @SuppressWarnings("unchecked")
    private static String getAccessToken() {
        Map<String, Object> cacheToken =  (Map<String, Object>) RedisUtil.getObject("weixinAccessToken");
        if (cacheToken != null && !cacheToken.isEmpty()) {
            long timeout = (long) cacheToken.get("timeout");
            if (timeout < System.currentTimeMillis()) {
                String token = (String) cacheToken.get("access_token");
                if (!StringUtils.isNullOrEmpty(token)) {
                    return (String) cacheToken.get("access_token");
                }
            }
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appID + "&secret="
                + appSecret;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String retStr = sendRequest(url);
            map = (Map<String, Object>) JSONObject.parse(retStr);
            if (map.containsKey("access_token")) {
                map.put("timeout", System.currentTimeMillis() + 7200 * 1000);
                RedisUtil.setObject("weixinAccessToken", (Serializable) map, 7200);
            } else {
                log.error("获取weixinToken失败:" + retStr);
                return "";
            }
        } catch (Exception e) {
            return "";
        }
        return (String) map.get("access_token");
    }

    @SuppressWarnings("unchecked")
    private static String getAccessTicket() {
        Map<String, Object> cacheTicket  =  (Map<String, Object>) RedisUtil.getObject("hmfWeixinAccessTicket");
        if (cacheTicket != null && !cacheTicket.isEmpty() && cacheTicket.containsKey("ticket")) {
            long timeout = 0;
            if (cacheTicket.containsKey("timeout")) {
                timeout = (long) cacheTicket.get("timeout");
            }

            if (timeout > System.currentTimeMillis()) {
                String token = (String) cacheTicket.get("ticket");
                if (!StringUtils.isNullOrEmpty(token)) {
                    return (String) cacheTicket.get("ticket");
                }
            }
        }
        String access_token = getAccessToken();
        if (StringUtils.isNullOrEmpty(access_token)) {
            return "";
        }
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + access_token + "&type=jsapi";
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String retStr = sendRequest(url);
            map = (Map<String, Object>) JSONObject.parse(retStr);
            if (map.containsKey("ticket")) {
                map.put("timeout", System.currentTimeMillis() + 7200 * 1000);
                RedisUtil.setObject("hmfWeixinAccessTicket", (Serializable) map, 7200);
            } else {
                log.error("获取weixinTicket失败:" + retStr);
                return "";
            }
        } catch (Exception e) {
            return "";
        }
        return (String) map.get("ticket");

    }
    //发送微信请求获取数据
    private static String sendRequest(String httpsURL) throws Exception {
        URL myurl = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
        InputStream ins = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader in = new BufferedReader(isr);
        StringBuffer buffer = new StringBuffer();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }
        in.close();
        return buffer.toString();
    }

    // 获取当前url
    private static String getCurrentUrl() {
        HttpServletRequest request = WebContext.currentRequest();
        if (request == null) {
            return "";
        }
        StringBuffer buffer = request.getRequestURL();
 
        if (buffer == null) {
            return "";
        }
        String params = request.getQueryString();
        if(!StringUtils.isNullOrEmpty(params)){
            buffer.append("?").append(params);
        }
        String url = buffer.toString();
        return url;
    }
    public static void main(String[] args) {
        Map<String, String> map = WeixinUtil.sign();
        System.out.println(map.get("timeout"));
    }
}
