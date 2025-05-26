package com.furan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.furan.R;
import com.furan.adapter.DailyWeatherAdapter;
import com.furan.database.CityDao;
import com.furan.model.DailyWeatherData;
import com.furan.network.WeatherApiService;

import java.util.ArrayList;
import java.util.List;

public class DailyForecastFragment extends Fragment {

    private RecyclerView recyclerView;
    private DailyWeatherAdapter adapter;

    // 新增当前城市变量，默认北京
    private String currentCity = "Beijing";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_forecast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadForecastData(currentCity);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_daily_forecast);
        adapter = new DailyWeatherAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadForecastData(String city) {
        new Thread(() -> {
            try {
                WeatherApiService apiService = new WeatherApiService(getContext());
                List<DailyWeatherData> dailyData = apiService.getDailyForecast(city);

                // UI线程更新数据
                requireActivity().runOnUiThread(() -> adapter.updateData(dailyData));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    // 新增方法，外部调用更新城市并刷新数据
    public void setCurrentCity(String city) {
        if (city != null && !city.isEmpty() && !city.equals(currentCity)) {
            currentCity = city;
            loadForecastData(currentCity);
        }
    }
}