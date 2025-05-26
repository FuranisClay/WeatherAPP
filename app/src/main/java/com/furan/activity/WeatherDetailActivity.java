package com.furan.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.furan.R;

public class WeatherDetailActivity extends AppCompatActivity {

    private ImageView ivWeatherIcon;
    private TextView tvDate, tvWeek, tvDescription, tvTempRange;
    private Button btnBack;
    private FrameLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        ivWeatherIcon = findViewById(R.id.iv_weather_icon);
        tvDate = findViewById(R.id.tv_date);
        tvWeek = findViewById(R.id.tv_week);
        tvDescription = findViewById(R.id.tv_description);
        tvTempRange = findViewById(R.id.tv_temp_range);
        btnBack = findViewById(R.id.btn_back);
        rootLayout = findViewById(R.id.detail_root);

        // 接收传递过来的数据
        String date = getIntent().getStringExtra("date");
        String week = getIntent().getStringExtra("week");
        int iconRes = getIntent().getIntExtra("iconRes", R.drawable.ic_sunny);
        String description = getIntent().getStringExtra("description");
        String tempRange = getIntent().getStringExtra("tempRange");

        tvDate.setText(date);
        tvWeek.setText(week);
        ivWeatherIcon.setImageResource(iconRes);
        tvDescription.setText(description);
        tvTempRange.setText(tempRange);

        if ("晴朗".equals(description)) {
            rootLayout.setBackgroundResource(R.drawable.bg_sunny);
        } else if ("少云".equals(description) || "多云".equals(description)) {
            rootLayout.setBackgroundResource(R.drawable.bg_overcast);
        } else if ("阴天".equals(description)) {
            rootLayout.setBackgroundResource(R.drawable.bg_cloudy);
        } else if ("小雨".equals(description) || "阵雨".equals(description)) {
            rootLayout.setBackgroundResource(R.drawable.bg_rain);
        } else if ("雷雨".equals(description)) {
            rootLayout.setBackgroundResource(R.drawable.bg_thunderstorm);
        } else if ("下雪".equals(description)) {
            rootLayout.setBackgroundResource(R.drawable.bg_snow);
        } else if ("雾霾".equals(description)) {
            rootLayout.setBackgroundResource(R.drawable.bg_mist);
        } else {
            rootLayout.setBackgroundResource(R.drawable.bg_sunny);
        }



        // 返回按钮
        btnBack.setOnClickListener(v -> finish());
    }
}
