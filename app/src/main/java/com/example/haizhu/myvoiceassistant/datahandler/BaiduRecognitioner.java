package com.example.haizhu.myvoiceassistant.datahandler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.baidu.speech.VoiceRecognitionService;
import com.example.haizhu.myvoiceassistant.R;
import com.example.haizhu.myvoiceassistant.global.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 百度语音识别操作
 * Created by sshunsun on 2017/5/29.
 */
public class BaiduRecognitioner {

    private static RecognitionListener recognitionListener;
    private static SpeechRecognizer speechRecognizer;
    private long speechEndTime = -1;
    private static Handler mhandler;

    public static final int RECOGNIZE_SUCESS = 1;
    public static final int RECOGNIZE_ERROR = 2;
    public static final int RMSCHANGED = 3;


    public static void setHandler(Handler handler) {
        mhandler = handler;
    }

    public static void initData(Context context) {
        initListener();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context, new ComponentName(context, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(recognitionListener);
    }

    public static void bindParams(Intent intent, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean("tips_sound", true)) {
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }
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
        if (sp.contains(Constant.EXTRA_NLU)) {
            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
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
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        return slotData.toString();
    }

    public static void initListener() {
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
//                Integer rawHeight = (Integer) speechWave.getTag(VTAG);
//                if (rawHeight == null) {
//                    rawHeight = speechWave.getLayoutParams().height;
//                    speechWave.setTag(VTAG, rawHeight);
//                }
//
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) speechWave.getLayoutParams();
//                params.height = (int) (rawHeight * v * 0.01);
//                params.height = Math.max(params.height , speechWave.getMeasuredWidth());
//                speechWave.setLayoutParams(params);
                Message message = mhandler.obtainMessage(RMSCHANGED);
                message.obj = v;
                mhandler.sendMessage(message);
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

    private static void print(String msg) {
//        result.append(msg + "\n");
        Log.d("BaiduRecognitioner", "----" + msg);
    }

    public static void startRecognition() {
        speechRecognizer.cancel();
        Intent intent = new Intent();
        bindParams(intent,null);
        intent.putExtra("vad", "touch");
        speechRecognizer.startListening(intent);
    }

    public static void stopRecognition() {
        speechRecognizer.stopListening();
    }
}
