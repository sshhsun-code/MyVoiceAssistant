package com.example.haizhu.myvoiceassistant.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.haizhu.myvoiceassistant.R;
import com.example.haizhu.myvoiceassistant.bean.Result;
import com.example.haizhu.myvoiceassistant.datahandler.TruingDataHandler;

/**
 * Created by sshunsun on 2017/5/26.
 */
public class RobotChatActivity extends Activity implements View.OnClickListener, TruingDataHandler.QueryListener{

    private RelativeLayout text_chat_bottom;
    private ImageView image_voice_in_text;
    private Button id_chat_send;
    private EditText id_chat_msg;

    private RelativeLayout voice_chat_bottom;
    private TextView image_voice;
    private ImageView image_keyboard;

    private TextView result_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTranslate();
        setSoftInputMode();
        setContentView(R.layout.activity_robot_chat);
//        setSoftInputMode();
        initView();
        iniData();
    }

    private void setStatusBarTranslate() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setSoftInputMode() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    private void iniData() {
        TruingDataHandler.setListener(this);
    }

    private void initView() {
        text_chat_bottom = (RelativeLayout) findViewById(R.id.text_chat_bottom);
        image_voice_in_text = (ImageView) findViewById(R.id.image_voice_in_text);
        id_chat_send = (Button) findViewById(R.id.id_chat_send);
        id_chat_msg = (EditText) findViewById(R.id.id_chat_msg);
        id_chat_msg.setSelection(id_chat_msg.getText().length());


        voice_chat_bottom = (RelativeLayout) findViewById(R.id.voice_chat_bottom);
        image_voice = (TextView) findViewById(R.id.image_voice);
        image_keyboard = (ImageView) findViewById(R.id.image_keyboard);

        result_show = (TextView) findViewById(R.id.result_show);
        image_voice_in_text.setOnClickListener(this);
        id_chat_send.setOnClickListener(this);
        id_chat_msg.setOnClickListener(this);
        image_voice.setOnClickListener(this);
        image_keyboard.setOnClickListener(this);
    }

    @Override
    public void onDataFinished(final Result result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result != null) {
                    result_show.append("\n");
                    result_show.append(result.toString());
                }
            }
        });
    }

    @Override
    public void onDataError(String errInfo) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_chat_bottom:
                break;
            case R.id.id_chat_send:
                if (!TextUtils.isEmpty(id_chat_msg.getText())) {
                    TruingDataHandler.requestTruingAnswer(id_chat_msg.getText().toString());
                }
                break;
            case R.id.id_chat_msg:
                break;
            case R.id.image_voice:
                break;
            case R.id.image_keyboard:
                break;
        }
    }
}
