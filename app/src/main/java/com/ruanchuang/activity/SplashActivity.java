package com.ruanchuang.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.ruanchuang.activity.designlibrary.R;
import com.runachuang.massorganizationsignin.utils.UpdateFile;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by joho on 2016/5/29.
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private LinearLayout ll_root;
    private BmobQuery<UpdateFile> bmobQuery = new BmobQuery<UpdateFile>();
    private boolean isUpdate;
    private String pathName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ruanchuang.apk";
    private File file = new File(pathName);
    private ProgressDialog progressDialog;
    private BmobFile mBmobfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //加载布局
        setContentView(R.layout.activity_splash);

        //初始化后端云
        Bmob.initialize(this, "776b3b7501ee6b0f02ac4f665f0ea323");

        // 检测是否是第一次启动应用
        if (checkFirst()) {
            //初始化控件
            ll_root = (LinearLayout) findViewById(R.id.ll_root);

            //添加一个透明动画
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(2000);
            ll_root.startAnimation(alphaAnimation);

            //进入应用
            ententSignIn(2000);
        }
    }

    //检测是否是第一次启动
    //如果是就返回false 这样就不会执行下面的步骤
    //如果不是就返回true 这样就会执行下面的步骤
    private boolean checkFirst() {
        boolean b = true;
        //创建SharedPreferences
        SharedPreferences sp = getSharedPreferences("first", MODE_PRIVATE);
        if (sp.getBoolean("first", true)) {
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("first", false);
            edit.commit();
            b = false;
            Intent intent = new Intent(SplashActivity.this, HelloActivity.class);
            startActivity(intent);
            finish();
        }
        return b;
    }

    //进入登陆界面
    private void ententSignIn(final long time) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                    Intent intent = new Intent(SplashActivity.this, LognInActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
