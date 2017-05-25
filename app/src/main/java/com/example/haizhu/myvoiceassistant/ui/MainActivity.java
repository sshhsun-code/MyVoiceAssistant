package com.example.haizhu.myvoiceassistant.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.haizhu.myvoiceassistant.R;
import com.example.haizhu.myvoiceassistant.receiver.NetStateReceiver;
import com.example.haizhu.myvoiceassistant.utils.NetUtil;

public class MainActivity extends Activity implements NetStateReceiver.NetStateListener, View.OnClickListener{

    private boolean isConnected = false;
    private Button robot_chat;
    private Button baidu_voice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        isConnected = NetUtil.isNetOk(this);
        NetStateReceiver.registerListener(this);
    }

    private void initView() {
        robot_chat = (Button) findViewById(R.id.robot_chat);
        baidu_voice = (Button) findViewById(R.id.baidu_voice);
        robot_chat.setOnClickListener(this);
        baidu_voice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.baidu_voice:
                Intent intent = new Intent(this, BaiduVoiceActivity.class);
                startActivity(intent);
                break;
            case R.id.robot_chat:
                Intent intent2 = new Intent(this, RobotChatActivity.class);
                startActivity(intent2);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onNetStateChanged() {
        isConnected = NetUtil.isNetOk(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetStateReceiver.unregisterListener(this);
    }
}
