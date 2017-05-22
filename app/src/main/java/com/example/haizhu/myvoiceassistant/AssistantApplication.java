package com.example.haizhu.myvoiceassistant;

import android.app.Application;

/**
 * Created by sshunsun on 2017/5/22.
 */
public class AssistantApplication extends Application {

    private static AssistantApplication application = null;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static AssistantApplication getInstance() {
        if (application == null) {
            application = new AssistantApplication();
        }

        return application;
    }
}
