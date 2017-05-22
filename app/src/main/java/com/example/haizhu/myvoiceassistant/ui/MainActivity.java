package com.example.haizhu.myvoiceassistant.ui;

import android.app.Activity;
import android.os.Bundle;

import com.example.haizhu.myvoiceassistant.R;
import com.example.haizhu.myvoiceassistant.receiver.NetStateReceiver;
import com.example.haizhu.myvoiceassistant.utils.NetUtil;

public class MainActivity extends Activity implements NetStateReceiver.NetStateListener{

    private boolean isConnected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        isConnected = NetUtil.isNetOk();
        NetStateReceiver.registerListener(this);
    }

    private void initView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onNetStateChanged() {
        isConnected = NetUtil.isNetOk();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetStateReceiver.unregisterListener(this);
    }
}
