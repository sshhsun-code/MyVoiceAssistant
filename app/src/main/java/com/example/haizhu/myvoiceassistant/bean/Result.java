package com.example.haizhu.myvoiceassistant.bean;

/**
 * 访问图灵api得到的结果
 * Created by sunqi on 2017/5/25.
 */

public class Result {
    public static final int TYPE_TEXT = 1;//纯文本类型
    public static final int TYPE_URL = 2;//url类型
    public static final int TYPE_NEWS = 3;//新闻类型
    private int code;
    private String text;
    private String url;
    private int type;

    public Result() {
    }

    public Result(int code, String text, String url, int type) {
        this.code = code;
        this.text = text;
        this.url = url;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", text='" + text + '\'' +
                ", url='" + url + '\'' +
                ", type=" + type +
                '}';
    }
}
