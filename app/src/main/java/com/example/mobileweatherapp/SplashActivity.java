package com.example.mobileweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // 使用启动主题，不需要手动设置，因为会在清单中指定
//        super.onCreate(savedInstanceState);
//
//        // 延迟一段时间后跳转到MainActivity
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                finish(); // 关闭SplashActivity，使用户无法回退到此画面
//            }
//        }, 2000); // 延迟2秒，可根据需要调整
//    }

    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
