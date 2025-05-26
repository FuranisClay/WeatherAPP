package com.furan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.furan.MainActivity;
import com.furan.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 动画时长：2秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView splashLogo = findViewById(R.id.splash_logo);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        splashLogo.startAnimation(fadeIn);

        // 延时跳转到主界面
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 关闭SplashActivity
        }, SPLASH_DURATION);
    }
}
