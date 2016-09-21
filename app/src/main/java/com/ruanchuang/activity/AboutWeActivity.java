package com.ruanchuang.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.ruanchuang.activity.designlibrary.R;

import net.steamcrafted.loadtoast.LoadToast;

/**
 * Created by joho on 2016/5/29.
 */
public class AboutWeActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressDialog progressDialog;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;
    protected LoadToast lt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutwe);
        lt = new LoadToast(this);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webView.loadUrl("http://mp.weixin.qq.com/s?__biz=MzA5NjgyODU1Mw==&mid=406781047&idx=1&sn=5f3a38ea27dbc0869def2df2de022310&scene=20#wechat_redirect");

        //获取网络状态
        mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        lt.setText("加载中...").setBackgroundColor(0xff6689DB).setTextColor(Color.WHITE).setTranslationY(100).show();

        //加载数据
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100){
                    if (mNetworkInfo != null) {
                        lt.success();
                    }else {
                        Toast.makeText(AboutWeActivity.this, "加载失败，请检查网络", Toast.LENGTH_SHORT).show();
                        lt.error();
                    }
                }
            }
        });

    }

}
