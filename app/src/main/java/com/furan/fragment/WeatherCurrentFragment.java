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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        apiService = new WeatherApiService();
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
        rvWeatherDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWeatherDetails.setAdapter(detailAdapter);
    }

    private void loadWeatherData() {
        btnRefresh.setEnabled(false);

        executorService.execute(() -> {
            try {
                WeatherData weatherData = apiService.getCurrentWeather("北京");

                mainHandler.post(() -> {
                    updateUI(weatherData);
                    btnRefresh.setEnabled(true);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    Toast.makeText(getContext(), "获取天气数据失败", Toast.LENGTH_SHORT).show();
                    btnRefresh.setEnabled(true);
                });
            }
        });
    }

    private void updateUI(WeatherData weatherData) {
        tvLocation.setText(weatherData.getLocation());
        tvTemperature.setText(weatherData.getTemperature() + "°C");
        tvWeatherDesc.setText(weatherData.getDescription());
        tvTempRange.setText(weatherData.getTempMin() + "°C ~ " + weatherData.getTempMax() + "°C");
        tvUpdateTime.setText("更新时间: " + weatherData.getUpdateTime());
        tvAqi.setText(weatherData.getAqiText());

        // 根据天气状况设置图标
        setWeatherIcon(weatherData.getWeatherCode());

        // 更新详细信息
        detailAdapter.updateData(weatherData.getDetailList());
    }

    private void setWeatherIcon(int weatherCode) {
        int iconRes = R.drawable.ic_sunny; // 默认晴天图标

        switch (weatherCode) {
            case 0: // 晴
                iconRes = R.drawable.ic_sunny;
                break;
            case 1: // 多云
                iconRes = R.drawable.ic_cloudy;
                break;
            case 2: // 阴
                iconRes = R.drawable.ic_overcast;
                break;
            case 3: // 小雨
                iconRes = R.drawable.ic_light_rain;
                break;
            case 4: // 中雨
                iconRes = R.drawable.ic_moderate_rain;
                break;
            case 5: // 大雨
                iconRes = R.drawable.ic_heavy_rain;
                break;
        }

        ivWeatherIcon.setImageResource(iconRes);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}