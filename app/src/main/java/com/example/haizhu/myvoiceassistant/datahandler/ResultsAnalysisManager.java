package com.example.haizhu.myvoiceassistant.datahandler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.haizhu.myvoiceassistant.AssistantApplication;
import com.example.haizhu.myvoiceassistant.bean.BaiduIntent;
import com.example.haizhu.myvoiceassistant.bean.VoiceBean;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Arrays;
import java.util.List;

/**
 * 结果分析中心，返回要执行的场景，领域
 * Created by sshunsun on 2017/5/29.
 */
public class ResultsAnalysisManager {

    private static Context mcontext;

    private static Gson gson = new Gson();
    private static final String TAG = ResultsAnalysisManager.class.getSimpleName();

    private static final String[] domains = { //可以识别的本地操作
            "app",
            "telephone",
            "message",
            "setting",
    };

    private static List<String> domainList = (List<String>) Arrays.asList(domains);

    public static void analyseResult(String result) {
        mcontext = AssistantApplication.getInstance();
        VoiceBean voiceBean = gson.fromJson(result, VoiceBean.class);
        Log.d(TAG, "------"+voiceBean);
        String raw_text = voiceBean.getRaw_text();
        List<BaiduIntent> intentList = voiceBean.getResults();
        if (intentList.size() == 0) {
            //@Todo 直接将raw_text发送图灵机器人并return
        }
        BaiduIntent baiduIntent = voiceBean.getResults().get(0);
        String domain = baiduIntent.getDomain();//领域
        String intent = baiduIntent.getIntent();//操作
        LinkedTreeMap map = (LinkedTreeMap) baiduIntent.getObject();

        if (!domainList.isEmpty() && domainList.contains(domain)) {
            analyseIntent(domain, intent, map); //分析意图，准备离线场景的操作
        } else {
            //@Todo 直接将raw_text发送图灵机器人并return
        }
    }

    private static void analyseIntent(String domain, String intent, LinkedTreeMap map) {
        if (domain.equals("setting")) {
            Intent intent1 = new Intent();
            intent1.setClassName("com.android.settings", "com.android.settings.Settings");
        } else if (domain.equals("app")) {
            String appName = (String) map.get("name");

        } else if (domain.equals("telephone")) {


        } else if (domain.equals("message")) {


        }
    }
}
