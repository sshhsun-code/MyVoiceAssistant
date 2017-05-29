package com.example.haizhu.myvoiceassistant.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by sshunsun on 2017/5/30.
 */
public class common {

    public static Intent getAppIntentWithPackageName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageinfo = null;
        try {
            packageinfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return null;
        }
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        List<ResolveInfo> list = packageManager.queryIntentActivities(resolveIntent, 0);

        if (list != null && !list.isEmpty()) {
            ResolveInfo resolveInfo = list.get(0);
            if (resolveInfo != null) {
                String pn = resolveInfo.activityInfo.packageName;
                String className = resolveInfo.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName(pn, className);
                intent.setComponent(cn);
                return intent;
            }
        }
        return null;
    }

    public static boolean startActivity(Context context, Intent intent) {
        boolean bResult = true;
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            bResult = false;
        } catch (Exception e) {
            e.printStackTrace();
            bResult = false;
        }
        return bResult;
    }
}
