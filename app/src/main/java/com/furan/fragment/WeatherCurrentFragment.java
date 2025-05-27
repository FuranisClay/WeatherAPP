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

    private String currentCity = "Shenyang"; // 默认城市

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
        loadWeatherData();
    }

    private void setAqiBackgroundByText(String aqiText) {
        int bgResId;

        if (aqiText == null) {
            bgResId = R.drawable.aqi_bg_unknown; // 你可以自己定义一个默认背景
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

        btnRefresh.setOnClickListener(v -> loadWeatherData());
    }

    private void setupRecyclerView() {
        detailAdapter = new WeatherDetailAdapter();
        rvWeatherDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWeatherDetails.setAdapter(detailAdapter);
    }

    /**
     * 对外公开，更新当前城市并刷新天气
     */
    public void updateCity(String city) {
        if (city != null && !city.isEmpty() && !city.equals(currentCity)) {
            currentCity = city;
            if (tvLocation != null) {
                tvLocation.setText(city);
            }
            loadWeatherData();
        }
    }

    private void loadWeatherData() {
        // UI 线程操作
        mainHandler.post(() -> btnRefresh.setEnabled(false));

        executorService.execute(() -> {
            try {
                WeatherData weatherData = apiService.getCurrentWeather(currentCity);

                mainHandler.post(() -> {
                    if (isAdded()) { // Fragment附加时才操作UI
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
        tvAqi.setText(weatherData.getAqiText());

        setWeatherIcon(weatherData.getWeatherCode());

        detailAdapter.updateData(weatherData.getDetailList());

        setAqiBackgroundByText(weatherData.getAqiText());

    }


    private void setWeatherIcon(int weatherCode) {
        int iconRes = R.drawable.ic_sunny; // 默认晴天

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
        // 断开视图引用，防止泄露
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
