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
import com.furan.adapter.HourlyWeatherAdapter;
import com.furan.model.HourlyWeatherData;
import java.util.ArrayList;
import java.util.List;

public class HourlyForecastFragment extends Fragment {

    private RecyclerView recyclerView;
    private HourlyWeatherAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hourly_forecast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadHourlyData();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_hourly_forecast);
        adapter = new HourlyWeatherAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadHourlyData() {
        // 模拟24小时数据
        List<HourlyWeatherData> hourlyData = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            String time = String.format("%02d:00", i);
            int temp = 20 + (int)(Math.random() * 10);
            int humidity = 50 + (int)(Math.random() * 30);
            int iconRes = i % 3 == 0 ? R.drawable.ic_sunny :
                    i % 3 == 1 ? R.drawable.ic_cloudy : R.drawable.ic_light_rain;

            hourlyData.add(new HourlyWeatherData(time, iconRes, temp + "°", humidity + "%"));
        }

        adapter.updateData(hourlyData);
    }
}
