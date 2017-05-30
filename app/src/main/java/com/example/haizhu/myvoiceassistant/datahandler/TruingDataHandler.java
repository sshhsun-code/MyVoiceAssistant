package com.example.haizhu.myvoiceassistant.datahandler;

import com.example.haizhu.myvoiceassistant.bean.Result;
import com.example.haizhu.myvoiceassistant.utils.HttpUtil;

/**
 * Created by sshunsun on 2017/5/27.
 */
public class TruingDataHandler {
    private static QueryListener listener;

    public static void requestTruingAnswer(final String msg) {
        if (msg == null || msg.isEmpty()) {
            listener.onDataError("你说什么？");
        }
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                    Result result = HttpUtil.sendMsg(msg);
                    if (listener != null) {
                        listener.onDataFinished(result);
                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (listener != null) {
                            listener.onDataError(e.toString());
                        }
                    }
                }
        }).start();


    }

    public static void setListener(QueryListener listener1) {
        listener = listener1;
    }

    public interface QueryListener{
        void onDataFinished(Result result);
        void onDataError(String errInfo);
    }
}
