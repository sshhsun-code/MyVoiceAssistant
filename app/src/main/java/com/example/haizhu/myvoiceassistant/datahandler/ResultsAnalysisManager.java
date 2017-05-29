package com.example.haizhu.myvoiceassistant.datahandler;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.haizhu.myvoiceassistant.AssistantApplication;
import com.example.haizhu.myvoiceassistant.bean.BaiduIntent;
import com.example.haizhu.myvoiceassistant.bean.VoiceBean;
import com.example.haizhu.myvoiceassistant.utils.AppNameUtils;
import com.example.haizhu.myvoiceassistant.utils.common;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 结果分析中心，根据要执行的场景，领域进行操作
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

    /**
     * 分析识别结果
     * @param result
     */
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

    /**
     * 分析领域和意图，进行本地操作
     * @param domain
     * @param intent
     * @param map
     */
    private static void analyseIntent(String domain, String intent, LinkedTreeMap map) {
        if (domain.equals("setting")) {
            if (!handleSettingIntent(intent, map)) {
            Intent intent1 = new Intent();
            intent1.setClassName("com.android.settings", "com.android.settings.Settings");
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mcontext.startActivity(intent1);
            }
        } else if (domain.equals("app")) {
            String appName = (String) map.get("appname");
            String pkgName = AppNameUtils.getpkgName(appName);
            if (!TextUtils.isEmpty(pkgName)) {
                Intent intent1 = common.getAppIntentWithPackageName(mcontext, pkgName);
                common.startActivity(mcontext, intent1);
            }
        } else if (domain.equals("telephone")) {


        } else if (domain.equals("message")) {


        }
    }

    private static boolean handleSettingIntent(String intent, LinkedTreeMap map) {
        if (intent.equals("set")) {
            String settingtype = (String) map.get("settingtype");
            if (settingtype.contains("wifi")) {
                WifiManager wifiManager = (WifiManager) mcontext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (settingtype.equals("wifi_on")) { //打开wifi
                    if (!wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(true);
                    }
                } else {  //关闭wifi
                    if (wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(false);
                    }
                }
                return true;
            } else if (settingtype.contains("bluetooth")) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (settingtype.equals("bluetooth_on")) { //打开蓝牙
                    if(!bluetoothAdapter.isEnabled()){
                        bluetoothAdapter.enable();  //打开蓝牙，需要BLUETOOTH_ADMIN权限
                        String Name = bluetoothAdapter.getName();
                    }
                } else {  //关闭蓝牙
                    if(bluetoothAdapter.isEnabled()){
                        bluetoothAdapter.disable();
                    }
                }
                return true;
            } else if (settingtype.contains("data")) {
                if (settingtype.equals("data_on")) { //打开data
                    setMobileData(mcontext.getApplicationContext(),true);
                } else {  //关闭data
                    setMobileData(mcontext.getApplicationContext(),false);
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static void setMobileData(Context pContext, boolean pBoolean) {
        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;

            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);

            method.invoke(mConnectivityManager, pBoolean);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("移动数据设置错误: " + e.toString());
        }
    }
}
