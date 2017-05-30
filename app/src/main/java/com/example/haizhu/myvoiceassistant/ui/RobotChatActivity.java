package com.example.haizhu.myvoiceassistant.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.speech.VoiceRecognitionService;
import com.example.haizhu.myvoiceassistant.R;
import com.example.haizhu.myvoiceassistant.adapter.RobotChatAdapter;
import com.example.haizhu.myvoiceassistant.bean.Result;
import com.example.haizhu.myvoiceassistant.datahandler.ResultsAnalysisManager;
import com.example.haizhu.myvoiceassistant.datahandler.TruingDataHandler;
import com.example.haizhu.myvoiceassistant.global.Constant;
import com.example.haizhu.myvoiceassistant.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
    private static ListView id_chat_listView;

    View speechTips;

    View speechWave;

    private static List<Result> resultList = new ArrayList<>();
    private static RobotChatAdapter chatAdapter;

    private Handler mhandler;

    private static RecognitionListener recognitionListener;
    private static SpeechRecognizer speechRecognizer;

    private String httpUrl = "";

    public static final int RECOGNIZE_SUCESS = 1;
    public static final int RECOGNIZE_ERROR = 2;
    public static final int RMSCHANGED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTranslate();
        setSoftInputMode();
        setContentView(R.layout.activity_robot_chat);
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 5:
                        boolean isHttpUrl = (boolean) msg.obj;
                        if (isHttpUrl) {
                            Intent intent = new Intent(RobotChatActivity.this, WebViewActivity.class);
                            intent.putExtra(WebViewActivity.STR_EXTRA,httpUrl);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        break;
                    case RECOGNIZE_SUCESS:

                        break;
                    case RECOGNIZE_ERROR:

                        break;
                    case RMSCHANGED:

                        break;
                }
            }
        };
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
        initListener();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext(), new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(recognitionListener);
    }

    private void initView() {
        speechTips = View.inflate(this, R.layout.bd_asr_popup_speech,null);
        speechWave = speechTips.findViewById(R.id.wave);
        speechTips.setVisibility(View.GONE);
        addContentView(speechTips, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        text_chat_bottom = (RelativeLayout) findViewById(R.id.text_chat_bottom);
        image_voice_in_text = (ImageView) findViewById(R.id.image_voice_in_text);
        id_chat_send = (Button) findViewById(R.id.id_chat_send);
        id_chat_msg = (EditText) findViewById(R.id.id_chat_msg);
        id_chat_msg.setText("");
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
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        speechTips.setVisibility(View.VISIBLE);
                        speechRecognizer.cancel();
                        Intent intent = new Intent();
                        bindParams(intent);
                        intent.putExtra("vad", "touch");
                        speechRecognizer.startListening(intent);
                        break;
                    case MotionEvent.ACTION_UP:
                        speechTips.setVisibility(View.GONE);
                        speechRecognizer.stopListening();
                        result_show.setText("暂无结果");
                        break;
                }
                return true;
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
                httpUrl = result.getUrl();
                if (!TextUtils.isEmpty(httpUrl)) { //有额外的链接内容
                    HttpUtil.checkURL(httpUrl, mhandler);
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
                id_chat_msg.setText("");
                break;
            case R.id.image_keyboard:
                refreshBottomLayout(false);
                break;
        }
    }

//    private void HandleInputMethod(boolean isShow) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (isShow) {
//
//        } else {
//            if (imm.isActive())
//            {
//                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
//                        InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        }
//    }

    public static void addChatItem(String msg) {
        if (msg.isEmpty()) {
            return;
        }
        Result my = new Result();
        my.setType(Result.TYPE_MY);
        my.setText(msg);
        resultList.add(my);
        try {
            chatAdapter.notifyDataSetChanged();
            id_chat_listView.smoothScrollToPosition(resultList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addChatItem(String msg,boolean isFrom) {
        if (msg.isEmpty()) {
            return;
        }
        Result my = new Result();
        my.setType(Result.TYPE_TEXT);
        my.setText(msg);
        resultList.add(my);
        try {
            chatAdapter.notifyDataSetChanged();
            id_chat_listView.smoothScrollToPosition(resultList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addChatItem(Result result) {
        resultList.add(result);
        try {
            chatAdapter.notifyDataSetChanged();
            id_chat_listView.smoothScrollToPosition(resultList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindParams(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
        intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
        intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
        intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);

        if (sp.contains(Constant.EXTRA_INFILE)) {
            String tmp = sp.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(Constant.EXTRA_INFILE, tmp);
        }
        if (sp.getBoolean(Constant.EXTRA_OUTFILE, false)) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        if (sp.contains(Constant.EXTRA_SAMPLE)) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }

        intent.putExtra("grammar", "asset:///baidu_speech_grammar.bsg");

        if (sp.contains(Constant.EXTRA_LANGUAGE)) {
            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }
        intent.putExtra(Constant.EXTRA_NLU, "enable");

        if (sp.contains(Constant.EXTRA_VAD)) {
            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(Constant.EXTRA_PROP)) {
            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }
        // offline asr
        {
            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            intent.putExtra(Constant.EXTRA_LICENSE_FILE_PATH, "/sdcard/easr/license-tmp-20150530.txt");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private static String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶").put("王伟").put("孙琦").put("张伟");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        return slotData.toString();
    }

    public  void initListener() {
        recognitionListener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {
                final int VTAG = 0xFF00AA01;
                Integer rawHeight = (Integer) speechWave.getTag(VTAG);
                if (rawHeight == null) {
                    rawHeight = speechWave.getLayoutParams().height;
                    speechWave.setTag(VTAG, rawHeight);
                }

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) speechWave.getLayoutParams();
                params.height = (int) (rawHeight * v * 0.01);
                params.height = Math.max(params.height , speechWave.getMeasuredWidth());
                speechWave.setLayoutParams(params);
            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                StringBuilder sb = new StringBuilder();
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        sb.append("音频问题");
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        sb.append("没有语音输入");
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        sb.append("其它客户端错误");
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        sb.append("权限不足");
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        sb.append("网络问题");
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        sb.append("没有匹配的识别结果");
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        sb.append("引擎忙");
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        sb.append("服务端错误");
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        sb.append("连接超时");
                        break;
                }
                sb.append(":" + error);
                print("识别失败：" + sb.toString());
                Message message = mhandler.obtainMessage(RECOGNIZE_ERROR);
                message.obj = sb.toString();
                mhandler.sendMessage(message);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
                String json_res = results.getString("origin_result");
                String results_nlu_json = results.getString("results_nlu");
                Message message = mhandler.obtainMessage(RECOGNIZE_SUCESS);
                message.obj = results_nlu_json;
                mhandler.sendMessage(message);  //主界面UI通信
                ResultsAnalysisManager.analyseResult(results_nlu_json);
                print("result_nlu=\n" + results_nlu_json);
                try {
                    print("origin_result=\n" + new JSONObject(json_res).toString(4));
                } catch (Exception e) {
                    print("origin_result=[warning: bad json]\n" + json_res);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int eventType, Bundle bundle) {
                switch (eventType) {
                    case 11:
                        String reason = bundle.get("reason") + "";
                        print("EVENT_ERROR, " + reason);
                        Message message = mhandler.obtainMessage(RECOGNIZE_ERROR);
                        message.obj = reason;
                        mhandler.sendMessage(message);
                        break;
                }
            }
        };
    }

    private void print(String msg) {
        result_show.append(msg + "\n");
        Log.d("BaiduRecognitioner", "----" + msg);
    }

}
