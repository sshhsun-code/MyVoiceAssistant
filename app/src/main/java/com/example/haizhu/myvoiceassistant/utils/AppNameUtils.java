package com.example.haizhu.myvoiceassistant.utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.example.haizhu.myvoiceassistant.AssistantApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sshunsun on 2017/5/14.
 */
public class AppNameUtils {

    private static Map<String, String> appNames = new HashMap<>();

    public static void initAppNames() {
        appNames.clear();
        PackageManager manager = AssistantApplication.getInstance().getPackageManager();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> resolveInfolist = manager.queryIntentActivities(intent, 0);
        if (resolveInfolist != null || resolveInfolist.isEmpty()) {
            for (ResolveInfo info : resolveInfolist) {
                appNames.put(info.activityInfo.packageName, info.activityInfo.loadLabel(manager).toString());
            }
        }
    }

    public static String getpkgName(String appName) {
        String result = "";
        if (appNames.isEmpty()) {
            return result;
        }
        if (appNames.containsValue(appName)) {
            Set<String> keys = appNames.keySet();
            for (String key : keys) {
                if (appNames.get(key).equals(appName)) {
                    return key;
                }
            }
        }

        return result;
    }

}
