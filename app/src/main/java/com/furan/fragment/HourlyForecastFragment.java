package com.furan.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furan.R;
import com.furan.adapter.HourlyWeatherAdapter;
import com.furan.model.HourlyWeatherData;
import com.furan.network.WeatherApiService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HourlyForecastFragment extends Fragment {

    private RecyclerView recyclerView;
    private HourlyWeatherAdapter adapter;

    private ExecutorService executorService;
    private Handler mainHandler;
    private WeatherApiService apiService;

    private String currentCity = "Shenyang";

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
        return inflater.inflate(R.layout.fragment_hourly_forecast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_hourly_forecast);
        adapter = new HourlyWeatherAdapter();

        // 这里改为纵向排列
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);

        loadHourlyData();
    }

    private void loadHourlyData() {
        executorService.execute(() -> {
            try {
                List<HourlyWeatherData> hourlyData = apiService.getHourlyForecast(currentCity);
                mainHandler.post(() -> adapter.updateData(hourlyData));
            } catch (Exception e) {
                mainHandler.post(() -> Toast.makeText(getContext(), "获取小时预报失败", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void setCurrentCity(String city) {
        if (city != null && !city.isEmpty() && !city.equals(currentCity)) {
            currentCity = city;
            loadHourlyData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
