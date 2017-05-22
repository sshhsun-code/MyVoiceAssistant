package com.example.haizhu.myvoiceassistant.global;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.haizhu.myvoiceassistant.AssistantApplication;

/**
 * Created by sshunsun on 2017/5/6.
 */
public class GlobalPref {
    private static SharedPreferences sharedPerences = null;
    private static SharedPreferences.Editor editor = null;
    private static GlobalPref instance = null;


    protected GlobalPref() {
    }

    public static GlobalPref getInstance(Context context) {
        if (instance == null && context != null) {
            synchronized (GlobalPref.class) {
                instance = new GlobalPref();
                initShared(context, "GlobalConfig");
            }
        }
        return instance;
    }

    public static GlobalPref getInstance() {
       return getInstance(AssistantApplication.getInstance());
    }

    private static void initShared(Context ctx, String sharedname) {
        sharedPerences = ctx.getSharedPreferences(sharedname, Context.MODE_PRIVATE);
        editor = sharedPerences.edit();
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value).commit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public void putInteger(String key, int value) {
        editor.putInt(key, value).commit();
    }

    public long getLong(String key, long defValue) {
        return sharedPerences.getLong(key, defValue);
    }

    public String getString(String key, String defValue) {
        return sharedPerences.getString(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return sharedPerences.getFloat(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPerences.getBoolean(key, defValue);
    }

    public int getInteger(String key, int defValue) {
        return sharedPerences.getInt(key, defValue);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

}
