package com.example.haizhu.myvoiceassistant.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.haizhu.myvoiceassistant.R;
import com.example.haizhu.myvoiceassistant.receiver.NetStateReceiver;
import com.example.haizhu.myvoiceassistant.utils.HttpUtil;

/**
 * 根据返回的结果中的链接,展示网页
 * Created by sshunsun on 2017/5/23.
 */
public class WebViewActivity extends Activity implements NetStateReceiver.NetStateListener{

    private WebView mwb;
    private Handler mhandler;

    private ImageView top_back;
    private String httpUrlStr = "";

    public static final String STR_EXTRA = "str_extra";

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTranslate();
        WebView.enableSlowWholeDocumentDraw();
        setContentView(R.layout.activity_webview_show);
        Intent intent = getIntent();
        if (!TextUtils.isEmpty(intent.getStringExtra(STR_EXTRA)))
        {
            httpUrlStr = intent.getStringExtra(STR_EXTRA);
        }
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        boolean isHttpUrl = (boolean) msg.obj;
                        if (isHttpUrl) {
                            WebShow();
                        } else {
                            Toast.makeText(WebViewActivity.this, "访问链接错误，已帮您跳转到百度",Toast.LENGTH_LONG).show();
                            WebShow("https://www.baidu.com/");
                        }
                        break;
                }
            }
        };
        top_back = (ImageView) findViewById(R.id.top_back);
        top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mwb = (WebView) findViewById(R.id.webview_show);

    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpUtil.checkURL(httpUrlStr, mhandler);

    }

    private void WebShow() {
        WebShow(httpUrlStr);
    }

    private void WebShow(String url) {
        mwb.canZoomIn();
        mwb.canZoomOut();
        mwb.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mwb.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mwb.getSettings().setJavaScriptEnabled(true);
        mwb.loadUrl(url);
        mwb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Toast.makeText(WebViewActivity.this, "页面加载完成", Toast.LENGTH_SHORT)
                        .show();
            }
        });
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


        @Override
    public void onNetStateChanged() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mwb.canGoBack()) {
            mwb.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyWebView();
    }

    public void destroyWebView() {

        if(mwb != null) {
            mwb.clearHistory();
            mwb.clearCache(true);
            mwb.loadUrl("about:blank"); // clearView() should be changed to loadUrl("about:blank"), since clearView() is deprecated now
            mwb.freeMemory();
            mwb.pauseTimers();
            mwb = null; // Note that mWebView.destroy() and mWebView = null do the exact same thing
        }

    }
}
