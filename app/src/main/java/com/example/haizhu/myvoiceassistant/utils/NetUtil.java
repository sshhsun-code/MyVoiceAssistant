package com.example.haizhu.myvoiceassistant.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.haizhu.myvoiceassistant.AssistantApplication;

/**
 * Created by sshunsun on 2017/5/22.
 */
public class NetUtil {

    public static boolean isNetOk() {
        Context context = AssistantApplication.getInstance().getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null) {
            return false;
        }
        if (netInfo.isConnected()) {
            return true;
        }
        //补刀逻辑，再次判断一下wifi和移动网络的状态
        NetworkInfo mobNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
           return false;
        }else {
            return true;
        }
    }

}
