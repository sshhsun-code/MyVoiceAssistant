package com.example.haizhu.myvoiceassistant.bean;

import java.util.List;

/**
 * 语音识别的结果，包括 语音识别 和 语义解析结果
 * Created by sshunsun on 2017/5/29.
 */
public class VoiceBean {
    private String raw_text;
    private List<BaiduIntent> results;

    public VoiceBean() {
    }

    public VoiceBean(String raw_text, List<BaiduIntent> results) {
        this.raw_text = raw_text;
        this.results = results;
    }

    public String getRaw_text() {
        return raw_text;
    }

    public void setRaw_text(String raw_text) {
        this.raw_text = raw_text;
    }

    public List<BaiduIntent> getResults() {
        return results;
    }

    public void setResults(List<BaiduIntent> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "VoiceBean{" +
                "results=" + results +
                ", raw_text='" + raw_text + '\'' +
                '}';
    }
}
