package com.furan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.furan.activity.CitySelectorActivity;
import com.furan.fragment.DiaryFragment;
import com.furan.fragment.MusicPlayerFragment;
import com.furan.fragment.SettingsFragment;
import com.furan.fragment.WeatherCurrentFragment;
import com.furan.fragment.WeatherForecastFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_CODE_CITY_SELECTOR = 2001;

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private ViewPagerAdapter adapter;

    // 高德定位客户端
    private AMapLocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 隐私合规接口，必须先调用，参数均为true
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);

        initViews();
        setupViewPager();
        setupDrawerLayout();

        if (checkLocationPermission()) {
            try {
                startLocation();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.view_pager);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        navigationView = findViewById(R.id.nav_view);

        bottomNavigation.setOnNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.nav_current);
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.nav_forecast);
                        break;
                    case 2:
                        bottomNavigation.setSelectedItemId(R.id.nav_music);
                        break;
                    case 3:
                        bottomNavigation.setSelectedItemId(R.id.nav_diary);
                        break;
                    case 4:
                        bottomNavigation.setSelectedItemId(R.id.nav_settings);
                        break;
                }
            }
        });
    }

    private void setupDrawerLayout() {
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    // 高德定位启动
    private void startLocation() throws Exception {
        if (locationClient == null) {
            locationClient = new AMapLocationClient(getApplicationContext());
            AMapLocationClientOption option = new AMapLocationClientOption();

            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            option.setOnceLocation(true);  // 一次定位
            option.setNeedAddress(true);   // 返回地址信息

            locationClient.setLocationOption(option);

            locationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation amapLocation) {
                    if (amapLocation != null) {
                        if (amapLocation.getErrorCode() == 0) {
                            String city = amapLocation.getCity();
                            if (city != null && !city.isEmpty()) {
                                city = city.replace("市", "").trim();
                                updateCityForFragments(city);
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "定位失败：" + amapLocation.getErrorInfo(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    // 定位结束后关闭客户端
                    if (locationClient != null) {
                        locationClient.stopLocation();
                        locationClient.onDestroy();
                        locationClient = null;
                    }
                }
            });
        }
        locationClient.startLocation();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_current) {
            viewPager.setCurrentItem(0);
        } else if (itemId == R.id.nav_forecast) {
            viewPager.setCurrentItem(1);
        } else if (itemId == R.id.nav_music) {
            viewPager.setCurrentItem(2);
        } else if (itemId == R.id.nav_diary) {
            viewPager.setCurrentItem(3);
        } else if (itemId == R.id.nav_settings) {
            viewPager.setCurrentItem(4);
        } else if (itemId == R.id.nav_city) {
            Intent intent = new Intent(this, CitySelectorActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CITY_SELECTOR);
        } else if (itemId == R.id.nav_about) {
            Toast.makeText(this, "关于本应用", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 处理 CitySelectorActivity 返回的城市名
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CITY_SELECTOR && resultCode == RESULT_OK && data != null) {
            String selectedCity = data.getStringExtra("selected_city");
            if (selectedCity != null) {
                updateCityForFragments(selectedCity);
            }
        }
    }

    private void updateCityForFragments(String city) {
        // 更新当前天气fragment
        Fragment currentFragment = adapter.getFragment(0);
        if (currentFragment instanceof WeatherCurrentFragment) {
            ((WeatherCurrentFragment) currentFragment).updateCity(city);
        }

        // 更新天气预报fragment
        Fragment forecastFragment = adapter.getFragment(1);
        if (forecastFragment instanceof WeatherForecastFragment) {
            ((WeatherForecastFragment) forecastFragment).updateCity(city);
        }
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final Fragment[] fragments = new Fragment[5]; // 5个fragment

        public ViewPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new WeatherCurrentFragment();
                    break;
                case 1:
                    fragment = new WeatherForecastFragment();
                    break;
                case 2:
                    fragment = new MusicPlayerFragment();
                    break;
                case 3:
                    fragment = new DiaryFragment();
                    break;
                case 4:
                    fragment = new SettingsFragment();
                    break;
                default:
                    fragment = new WeatherCurrentFragment();
            }
            fragments[position] = fragment;
            return fragment;
        }

        @Override
        public int getItemCount() {
            return fragments.length;
        }

        public Fragment getFragment(int position) {
            return fragments[position];
        }
    }

    // 权限请求回调
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    startLocation();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(this, "需要位置权限才能获取当前城市", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
