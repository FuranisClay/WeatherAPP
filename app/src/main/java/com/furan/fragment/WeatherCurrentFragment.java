package com.furan.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furan.R;
import com.furan.adapter.WeatherDetailAdapter;
import com.furan.model.WeatherData;
import com.furan.network.WeatherApiService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherCurrentFragment extends Fragment {

    private TextView tvLocation, tvTemperature, tvWeatherDesc, tvTempRange, tvUpdateTime, tvAqi;
    private ImageView ivWeatherIcon;
    private ImageButton btnRefresh;
    private RecyclerView rvWeatherDetails;
    private WeatherDetailAdapter detailAdapter;

    private ExecutorService executorService;
    private Handler mainHandler;
    private WeatherApiService apiService;

    private WeatherData cachedWeatherData = null;
    private String currentCity = "Shenyang"; // 默认城市

    private boolean shouldRefresh = true; // 👉 新增：是否需要刷新标记

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        apiService = new WeatherApiService(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather_current, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldRefresh) {
            loadWeatherData();
            shouldRefresh = false;
        }
    }

    private void setAqiBackgroundByText(String aqiText) {
        int bgResId;
        if (aqiText == null) {
            bgResId = R.drawable.aqi_bg_unknown;
        } else {
            switch (aqiText) {
                case "优":
                    bgResId = R.drawable.aqi_bg_good;
                    break;
                case "良":
                    bgResId = R.drawable.aqi_bg_moderate;
                    break;
                case "轻度污染":
                    bgResId = R.drawable.aqi_bg_unhealthy_sensitive;
                    break;
                case "中度污染":
                    bgResId = R.drawable.aqi_bg_unhealthy;
                    break;
                case "重度污染":
                    bgResId = R.drawable.aqi_bg_very_unhealthy;
                    break;
                default:
                    bgResId = R.drawable.aqi_bg_unknown;
                    break;
            }
        }
        if (tvAqi != null) {
            tvAqi.setBackgroundResource(bgResId);
        }
    }

    private void initViews(View view) {
        tvLocation = view.findViewById(R.id.tv_location);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvWeatherDesc = view.findViewById(R.id.tv_weather_desc);
        tvTempRange = view.findViewById(R.id.tv_temp_range);
        tvUpdateTime = view.findViewById(R.id.tv_update_time);
        tvAqi = view.findViewById(R.id.tv_aqi);
        ivWeatherIcon = view.findViewById(R.id.iv_weather_icon);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        rvWeatherDetails = view.findViewById(R.id.rv_weather_details);

        btnRefresh.setOnClickListener(v -> {
            shouldRefresh = true; // 👉 点击刷新按钮时，设置需要刷新
            loadWeatherData();
        });
    }

    private void setupRecyclerView() {
        detailAdapter = new WeatherDetailAdapter();
        rvWeatherDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWeatherDetails.setAdapter(detailAdapter);
    }

    public void updateCity(String city) {
        if (city != null && !city.isEmpty() && !city.equals(currentCity)) {
            currentCity = city;
            if (tvLocation != null) {
                tvLocation.setText(city);
            }
            shouldRefresh = true; // 👉 城市切换，标记要刷新
            loadWeatherData();
        }
    }

    private void loadWeatherData() {
        mainHandler.post(() -> btnRefresh.setEnabled(false));

        executorService.execute(() -> {
            try {
                WeatherData weatherData = apiService.getCurrentWeather(currentCity);
                mainHandler.post(() -> {
                    if (isAdded()) {
                        if (weatherData.equals(cachedWeatherData)) {
                            btnRefresh.setEnabled(true);
                            return;
                        }
                        cachedWeatherData = weatherData;
                        updateUI(weatherData);
                        btnRefresh.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "获取天气数据失败", Toast.LENGTH_SHORT).show();
                        btnRefresh.setEnabled(true);
                    }
                });
            }
        });
    }

    private void updateUI(WeatherData weatherData) {
        if (weatherData == null) return;

        tvLocation.setText(weatherData.getLocation());
        tvTemperature.setText(weatherData.getTemperature() + "°C");
        tvWeatherDesc.setText(weatherData.getDescription());
        tvTempRange.setText(weatherData.getTempMin() + "°C ~ " + weatherData.getTempMax() + "°C");
        tvUpdateTime.setText("更新时间: " + weatherData.getUpdateTime());

        setWeatherIcon(weatherData.getWeatherCode());
        setAqiBackgroundByText(weatherData.getAqiText());
        detailAdapter.updateData(weatherData.getDetailList());
    }

    private void setWeatherIcon(int weatherCode) {
        int iconRes = R.drawable.ic_sunny;
        switch (weatherCode) {
            case 0:
                iconRes = R.drawable.ic_sunny;
                break;
            case 1:
                iconRes = R.drawable.ic_cloudy;
                break;
            case 2:
                iconRes = R.drawable.ic_overcast;
                break;
            case 3:
                iconRes = R.drawable.ic_light_rain;
                break;
            case 4:
                iconRes = R.drawable.ic_moderate_rain;
                break;
            case 5:
                iconRes = R.drawable.ic_heavy_rain;
                break;
        }
        ivWeatherIcon.setImageResource(iconRes);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        shouldRefresh = true; // 👉 防止视图销毁后不刷新
        tvLocation = null;
        tvTemperature = null;
        tvWeatherDesc = null;
        tvTempRange = null;
        tvUpdateTime = null;
        tvAqi = null;
        ivWeatherIcon = null;
        btnRefresh = null;
        rvWeatherDetails = null;
        detailAdapter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}
