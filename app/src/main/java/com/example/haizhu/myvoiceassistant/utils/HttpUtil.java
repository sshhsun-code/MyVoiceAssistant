package com.example.haizhu.myvoiceassistant.utils;

import com.example.haizhu.myvoiceassistant.bean.Result;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 处理访问图灵api的流程和数据
 * Created by sunqi on 2017/5/25.
 */

public class HttpUtil {

    private static String API_KEY = "8392a977aaab40de8e0de88dbd5ec6d5";
    private static String URL_STR = "http://www.tuling123.com/openapi/api";
    private static int USER_ID = 123567;

    /**
     * 拼接Url
     * @param msg
     * @return
     */
    private static String setParams(String msg)
    {
        try
        {
            msg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return URL_STR + "?key=" + API_KEY + "&info=" + msg + "&userid" + USER_ID;
    }

    public static Result sendMsg(String msg) throws Exception
    {
        String url = setParams(msg);
        String res = doGet(url);
        Gson gson = new Gson();
        Result result = gson.fromJson(res, Result.class);
        int code = result.getCode();
        switch (code) {
            case 200000:
                result.setType(Result.TYPE_URL);
                break;
            case 100000:
                result.setType(Result.TYPE_TEXT);
                break;
            case 302000:
                result.setType(Result.TYPE_NEWS);
                break;
            default:
                result.setType(Result.TYPE_TEXT);
                break;
        }
        result.setText(result.getText());
        return result;
    }

    /**
     * Get请求，获得返回数据
     * @param urlStr
     * @return
     */
    private static String doGet(String urlStr) throws Exception {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try
        {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200)
            {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1)
                {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else
            {
                throw new Exception("服务器连接错误！");
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception("服务器连接错误！");
        } finally
        {
            try
            {
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                if (baos != null)
                    baos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            conn.disconnect();
        }
    }
}
