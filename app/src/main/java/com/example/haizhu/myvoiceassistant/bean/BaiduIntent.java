package com.example.haizhu.myvoiceassistant.bean;

/**
 * 百度语音识别结果的语义解析
 * Created by sshunsun on 2017/5/29.
 */
public class BaiduIntent {
    private String domain;
    private String intent;
    private Object object;

    public BaiduIntent(String domain, String intent, Object object) {
        this.domain = domain;
        this.intent = intent;
        this.object = object;
    }

    public BaiduIntent() {

    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "BaiduIntent{" +
                "domain='" + domain + '\'' +
                ", intent='" + intent + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
