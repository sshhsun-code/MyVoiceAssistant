package com.example.haizhu.myvoiceassistant;

import android.app.Application;

import com.example.haizhu.myvoiceassistant.utils.AppNameUtils;

/**
 * Created by sshunsun on 2017/5/22.
 */
public class AssistantApplication extends Application {

    private static AssistantApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initApplication();
    }

    private void initApplication() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
                AppNameUtils.initAppNames();
            }
        }).start();
    }

    private void initData() {

    }


    public static AssistantApplication getInstance() {
        if (application == null) {
            application = new AssistantApplication();
        }

        return application;
    }
}
