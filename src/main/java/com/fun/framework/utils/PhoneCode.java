package com.fun.framework.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PhoneCode {
    /**
     * 公共方法
     * @param httpUrl
     * @param httpArg
     * @return
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = reader.readLine();
            if (strRead != null) {
                sbf.append(strRead);
                while ((strRead = reader.readLine()) != null) {
                    sbf.append("\n");
                    sbf.append(strRead);
                }
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String md5(String plainText) {
        StringBuffer buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    public static String encodeUrlString(String str, String charset) {
        String strret = null;
        if (str == null)
            return str;
        try {
            strret = java.net.URLEncoder.encode(str, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return strret;
    }

    public static String state(Integer state) {
        if (state==1){
            return "您的订单正在申请中，请等待！";
        }else if (state==2){
            return "尊敬的客户，您的申请订单已经审过！";
        }else if (state==3){
            return "尊敬的客户，你的申请已变动，请登录详细查看！";
        }else if (state==6){
            return "尊敬的客户，你的订单已解通过，请修改您的资料信息！";
        }else if (state==7){
            return "尊敬的客户，你的订单需回档，请及时处理！";
        }else{
            return "尊敬的客户，你的订单异常，请及时处理！";
        }
    }

    public static String sendms(String sign,String  contant,String userName ,String password ,String phone){
        String testContent = "【"+sign+"】"+contant+"。";
        String httpUrl = "http://api.smsbao.com/sms";
        StringBuffer httpArg = new StringBuffer();
        httpArg.append("u=").append(userName).append("&");
        httpArg.append("p=").append(md5(password)).append("&");
        httpArg.append("m=").append(phone).append("&");
        httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));
        return request(httpUrl, httpArg.toString());
    }
}
