package com.furan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.furan.R;

public class WeatherForecastFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private String currentCity = "北京"; // 默认城市

    private DailyForecastFragment dailyFragment;
    private HourlyForecastFragment hourlyFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather_forecast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupViewPager();
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.forecast_view_pager);
    }

    private void setupViewPager() {
        dailyFragment = new DailyForecastFragment();
        hourlyFragment = new HourlyForecastFragment();

        ForecastPagerAdapter adapter = new ForecastPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "7天预报" : "24小时预报");
        }).attach();
    }

    /**
     * 对外方法，更新城市并通知内部子Fragment刷新
     */
    public void updateCity(String city) {
        if (city != null && !city.isEmpty() && !city.equals(currentCity)) {
            currentCity = city;
            if (dailyFragment != null) {
                dailyFragment.setCurrentCity(city);
            }
            if (hourlyFragment != null) {
                hourlyFragment.setCurrentCity(city);
            }
        }
    }

    private class ForecastPagerAdapter extends FragmentStateAdapter {

        public ForecastPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return dailyFragment;
            } else {
                return hourlyFragment;
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
