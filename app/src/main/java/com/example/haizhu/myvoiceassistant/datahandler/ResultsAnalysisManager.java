package com.example.haizhu.myvoiceassistant.datahandler;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.haizhu.myvoiceassistant.AssistantApplication;
import com.example.haizhu.myvoiceassistant.bean.BaiduIntent;
import com.example.haizhu.myvoiceassistant.bean.VoiceBean;
import com.example.haizhu.myvoiceassistant.ui.RobotChatActivity;
import com.example.haizhu.myvoiceassistant.utils.AppNameUtils;
import com.example.haizhu.myvoiceassistant.utils.common;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
    private static HandlerCallBack callBack;
    private static Handler mhandler = new Handler(Looper.getMainLooper());

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
        Log.d(TAG, "------" + voiceBean);
        final String raw_text = voiceBean.getRaw_text();
        RobotChatActivity.addChatItem(raw_text);
        List<BaiduIntent> intentList = voiceBean.getResults();
        if (intentList.size() == 0) {
            if (!raw_text.isEmpty()) {
                TruingDataHandler.requestTruingAnswer(raw_text);
                return;
            }
            return;
        }
        BaiduIntent baiduIntent = voiceBean.getResults().get(0);
        String domain = baiduIntent.getDomain();//领域
        String intent = baiduIntent.getIntent();//操作
        LinkedTreeMap map = (LinkedTreeMap) baiduIntent.getObject();

        if (!domainList.isEmpty() && domainList.contains(domain)) {
            analyseIntent(domain, intent, map, new HandlerCallBack() {
                @Override
                public void success() {
                    RobotChatActivity.addChatItem("好的，马上"+raw_text,true);
                }

                @Override
                public void error(String error) {
                    RobotChatActivity.addChatItem(error,true);
                }
            }); //分析意图，准备离线场景的操作
        } else {
            TruingDataHandler.requestTruingAnswer(raw_text);
            return;
        }
    }

    /**
     * 分析领域和意图，进行本地操作
     * @param domain
     * @param intent
     * @param map
     */
    private static void analyseIntent(String domain, final String intent, LinkedTreeMap map,HandlerCallBack callBack) {
        if (domain.equals("setting")) {  //打开系统设置
            if (!handleSettingIntent(intent, map, callBack)) {
                Intent intent1 = new Intent();
                intent1.setClassName("com.android.settings", "com.android.settings.Settings");
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mcontext.startActivity(intent1);
            }
        } else if (domain.equals("app")) {  //打开应用
            String appName = (String) map.get("appname");
            final String pkgName = AppNameUtils.getpkgName(appName);
            if (!TextUtils.isEmpty(pkgName)) {
                callBack.success();
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (intent.contains("uninstall")) {
                            Intent intent2 = new Intent();
                            intent2.setAction(Intent.ACTION_DELETE);
                            intent2.setData(Uri.parse("package:"+pkgName));
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mcontext.startActivity(intent2);
                        } else {
                        Intent intent1 = common.getAppIntentWithPackageName(mcontext, pkgName);
                        common.startActivity(mcontext, intent1);
                        }
                    }
                },3000);
            } else {
                callBack.error("无法找到:"+appName);
            }
        } else if (domain.equals("telephone")) { //打电话
            String name = (String) map.get("name");
            getNumberAndCall(name, callBack);
        } else if (domain.equals("message")) {
            ArrayList<String> names = (ArrayList<String>) map.get("name");
            if (names == null || names.isEmpty()) {
                callBack.error("没有指定联系人");
            }
            String name = names.get(0);
            String msgbody = (String) map.get("msgbody");
            getNumberAndSend(name, msgbody,callBack);
        }
    }

    private static boolean handleSettingIntent(String intent, LinkedTreeMap map, HandlerCallBack callBack) {
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
                callBack.success();
                return true;
            } else if (settingtype.contains("bluetooth")) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (settingtype.equals("bluetooth_on")) { //打开蓝牙
                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();  //打开蓝牙，需要BLUETOOTH_ADMIN权限
                        String Name = bluetoothAdapter.getName();
                    }
                } else {  //关闭蓝牙
                    if (bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.disable();
                    }
                }
                callBack.success();
                return true;
            } else if (settingtype.contains("data")) {
                if (settingtype.equals("data_on")) { //打开data
                    setMobileData(mcontext.getApplicationContext(), true);
                } else {  //关闭data
                    setMobileData(mcontext.getApplicationContext(), false);
                }
                callBack.success();
                return true;
            } else {
                callBack.error("无法识别指令");
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

    /**
     * 通过输入获取电话号码
     */
    private static void getNumberAndCall(String name ,HandlerCallBack callBack) {
        //使用ContentResolver查找联系人数据
        Cursor cursor = mcontext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //遍历查询结果，找到所需号码
        while (cursor.moveToNext()) {
            //获取联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人的名字
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (name.equals(contactName)) {
                //使用ContentResolver查找联系人的电话号码
                Cursor phone = mcontext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phone.moveToNext()) {
                    String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        callBack.success();
                        mcontext.startActivity(intent);
                        return;
                    } else {
                        callBack.error("没有电话权限");
                    }
                }
            }
        }
        callBack.error("找不到联系人 :" + name);
    }

    private static void getNumberAndSend(String name, String msgbody, HandlerCallBack callBack) {
        //使用ContentResolver查找联系人数据
        Cursor cursor = mcontext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //遍历查询结果，找到所需号码
        while (cursor.moveToNext()) {
            //获取联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人的名字
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (name.equals(contactName)) {
                //使用ContentResolver查找联系人的电话号码
                Cursor phone = mcontext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phone.moveToNext()) {
                    String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    SmsManager manager = SmsManager.getDefault();
                    ArrayList<String> list = manager.divideMessage(msgbody);  //因为一条短信有字数限制，因此要将长短信拆分
                    for(String text:list){
                        manager.sendTextMessage(phoneNumber, null, text, null, null);
                    }
                    callBack.success();
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent SmsIntent = new Intent();
                            SmsIntent.setClassName("com.android.mms","com.android.mms.ui.ConversationList");
                            SmsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mcontext.startActivity(SmsIntent);
                        }
                    },3000);
                    return;
                }
            }
        }
        callBack.error("找不到联系人 :" + name);
    }

    static interface HandlerCallBack {
        void success();
        void error(String  error);
    }
}
