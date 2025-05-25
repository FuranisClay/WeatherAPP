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
import com.furan.model.DailyWeatherData;
import java.util.ArrayList;
import java.util.List;

public class DailyForecastFragment extends Fragment {

    private RecyclerView recyclerView;
    private DailyWeatherAdapter adapter;

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
        loadForecastData();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_daily_forecast);
        adapter = new DailyWeatherAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadForecastData() {
        // 模拟数据，实际应从API获取
        List<DailyWeatherData> dailyData = new ArrayList<>();

        dailyData.add(new DailyWeatherData("今天", "周一", R.drawable.ic_sunny, "晴", "18°", "28°"));
        dailyData.add(new DailyWeatherData("明天", "周二", R.drawable.ic_cloudy, "多云", "16°", "26°"));
        dailyData.add(new DailyWeatherData("后天", "周三", R.drawable.ic_light_rain, "小雨", "14°", "22°"));
        dailyData.add(new DailyWeatherData("05/25", "周四", R.drawable.ic_overcast, "阴", "12°", "20°"));
        dailyData.add(new DailyWeatherData("05/26", "周五", R.drawable.ic_sunny, "晴", "15°", "25°"));
        dailyData.add(new DailyWeatherData("05/27", "周六", R.drawable.ic_cloudy, "多云", "17°", "27°"));
        dailyData.add(new DailyWeatherData("05/28", "周日", R.drawable.ic_light_rain, "小雨", "16°", "24°"));

        adapter.updateData(dailyData);
    }
}
