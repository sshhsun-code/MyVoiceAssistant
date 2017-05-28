package com.example.haizhu.myvoiceassistant.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.haizhu.myvoiceassistant.R;
import com.example.haizhu.myvoiceassistant.adapter.RobotChatAdapter;
import com.example.haizhu.myvoiceassistant.bean.Result;
import com.example.haizhu.myvoiceassistant.datahandler.TruingDataHandler;

import java.util.ArrayList;
import java.util.List;

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
    private ListView id_chat_listView;

    private List<Result> resultList = new ArrayList<>();
    private RobotChatAdapter chatAdapter;

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
        chatAdapter = new RobotChatAdapter(getApplicationContext(), resultList);
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

        id_chat_listView = (ListView) findViewById(R.id.id_chat_listView);

        text_chat_bottom.setVisibility(View.VISIBLE);
        voice_chat_bottom.setVisibility(View.GONE);

        result_show = (TextView) findViewById(R.id.result_show);
        image_voice_in_text.setOnClickListener(this);
        id_chat_send.setOnClickListener(this);
        id_chat_msg.setOnClickListener(this);
        image_keyboard.setOnClickListener(this);

        image_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        id_chat_listView.setAdapter(chatAdapter);
    }

    @Override
    public void onDataFinished(final Result result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result != null) {
                   addChatItem(result);
                }
            }
        });
    }

    @Override
    public void onDataError(String errInfo) {

    }

    /**
     * 更新底部layout View
     * @param isVoiceShow
     */
    private void refreshBottomLayout(boolean isVoiceShow) {
        if (text_chat_bottom == null || voice_chat_bottom == null) {
            return;
        }
        text_chat_bottom.setVisibility(isVoiceShow ? View.GONE : View.VISIBLE);
        voice_chat_bottom.setVisibility(isVoiceShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_voice_in_text:
                refreshBottomLayout(true);
                break;
            case R.id.id_chat_send:
                if (!TextUtils.isEmpty(id_chat_msg.getText())) {
                    TruingDataHandler.requestTruingAnswer(id_chat_msg.getText().toString());
                    addChatItem(id_chat_msg.getText().toString());
                }
                break;
            case R.id.image_keyboard:
                refreshBottomLayout(false);
                break;
        }
    }

    private void addChatItem(String msg) {
        Result my = new Result();
        my.setType(Result.TYPE_MY);
        my.setText(msg);
        resultList.add(my);
        try {
            chatAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addChatItem(Result result) {
        resultList.add(result);
        try {
            chatAdapter.notifyDataSetChanged();
            id_chat_listView.smoothScrollToPosition(resultList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
