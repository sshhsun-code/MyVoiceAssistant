package com.example.haizhu.myvoiceassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sshunsun on 2017/5/23.
 */
public class NetStateReceiver extends BroadcastReceiver{

    private static List<NetStateListener> listeners = new ArrayList<>();

    public static void registerListener(NetStateListener listener) {
        listeners.add(listener);
    }

    public static void unregisterListener(NetStateListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            notifyNetStateChanged();
        }
    }

    /**
     * 订阅者模式，将网络状态变化这一消息分发给订阅者
     */
    private void notifyNetStateChanged() {
        if (listeners != null && !listeners.isEmpty()) {
            for (NetStateListener listener :listeners) {
                listener.onNetStateChanged();
            }
        }
    }

    /**
     * 清空监听者集合
     */
    public static void releaseListeners() {
        if (listeners != null) {
            listeners.clear();
        }
    }

    public interface NetStateListener {
        void onNetStateChanged();
    }
}
