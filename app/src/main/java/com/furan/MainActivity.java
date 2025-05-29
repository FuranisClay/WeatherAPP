package com.furan;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.furan.activity.CitySelectorActivity;
import com.furan.fragment.AboutFragment;
import com.furan.fragment.DiaryFragment;
import com.furan.fragment.MusicPlayerFragment;
import com.furan.fragment.SettingsFragment;
import com.furan.fragment.WeatherCurrentFragment;
import com.furan.fragment.WeatherForecastFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener,
        LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String DEFAULT_CITY = "Shenyang"; // 默认城市

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ViewPagerAdapter adapter;
    private LocationManager locationManager;
    private Geocoder geocoder;
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;

    // 用于启动城市选择页并接收结果
    private final ActivityResultLauncher<Intent> citySelectorLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            String selectedCity = result.getData().getStringExtra("selected_city");
                            if (selectedCity != null && !selectedCity.isEmpty()) {
                                updateCityForFragments(selectedCity);
                                Toast.makeText(MainActivity.this,
                                        "已选择城市：" + selectedCity, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupViewPager();
        setupDrawerLayout();
        initLocationServices();

        // 先设置默认城市
        updateCityForFragments(DEFAULT_CITY);

        if (checkLocationPermission()) {
            startLocationUpdates();
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
        viewPager.setOffscreenPageLimit(6);
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
                    case 5: // 修复about页面的跳转
                        bottomNavigation.setSelectedItemId(R.id.nav_about);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 如果抽屉是打开的，先关闭抽屉
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        }
        // 如果当前 ViewPager 页不是首页，回到首页
        else if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0);
        }
        // 否则执行默认返回（关闭应用）
        else {
            super.onBackPressed();
        }
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

    private void initLocationServices() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());
        timeoutHandler = new Handler(Looper.getMainLooper());
    }

    private boolean checkLocationPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }
        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void startLocationUpdates() {
        if (!checkLocationPermission()) {
            return;
        }
        try {
            if (!isLocationEnabled()) {
                Toast.makeText(this, "请开启设备定位服务", Toast.LENGTH_LONG).show();
                return;
            }
            setupLocationTimeout();
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        10000,
                        100,
                        this
                );
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    onLocationChanged(lastKnownLocation);
                }
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        10000,
                        100,
                        this
                );
                // 仅当GPS不可用时使用网络定位最后位置
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (lastKnownLocation != null) {
                        onLocationChanged(lastKnownLocation);
                    }
                }
            }
            Toast.makeText(this, "正在获取位置信息...", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "定位权限不足", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "定位初始化失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isLocationEnabled() {
        return locationManager != null && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void setupLocationTimeout() {
        if (timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
        timeoutRunnable = () -> {
            stopLocationUpdates();
            Toast.makeText(MainActivity.this, "定位超时，使用默认城市：" + DEFAULT_CITY, Toast.LENGTH_LONG).show();
            // 超时后使用默认城市
            updateCityForFragments(DEFAULT_CITY);
        };
        timeoutHandler.postDelayed(timeoutRunnable, 30000);
    }

    private void stopLocationUpdates() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        if (timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
            timeoutRunnable = null;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        stopLocationUpdates();
        getCityFromLocation(location);
    }

    private void getCityFromLocation(Location location) {
        new Thread(() -> {
            String city = null;

            // 检查是否在中国境内
            if (isLocationInChina(location)) {
                // 在中国境内，尝试获取具体城市
                city = tryOnlineGeocodingAPI(location);
                if (city == null) {
                    city = getCityByCoordinateRange(location);
                }
            } else {
                // 在国外，使用默认城市
                android.util.Log.d("Location", "Location is outside China, using default city: " + DEFAULT_CITY);
                city = DEFAULT_CITY;
            }

            final String finalCity = city != null ? city : DEFAULT_CITY;
            runOnUiThread(() -> {
                updateCityForFragments(finalCity);
                if (isLocationInChina(location)) {
                    Toast.makeText(MainActivity.this, "定位成功：" + finalCity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "海外定位，使用默认城市：" + finalCity, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    // 检查坐标是否在中国境内
    private boolean isLocationInChina(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        // 中国大陆的大致坐标范围
        // 纬度：18°N - 54°N
        // 经度：73°E - 135°E
        return lat >= 18 && lat <= 54 && lon >= 73 && lon <= 135;
    }

    private String tryOnlineGeocodingAPI(Location location) {
        String apiKey = "0b050db12ce93e6aa9da87086006a341"; // 请替换为您的实际 API Key
        String url = "https://restapi.amap.com/v3/geocode/regeo?location="
                + location.getLongitude() + "," + location.getLatitude()
                + "&key=" + apiKey + "&radius=1000&extensions=all&batch=false&roadlevel=0";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "YourAppName/1.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();
                android.util.Log.d("AmapAPI", "Response: " + jsonResponse);
                JSONObject jsonObject = new JSONObject(jsonResponse);
                String status = jsonObject.optString("status");
                if ("1".equals(status)) {
                    JSONObject regeocode = jsonObject.optJSONObject("regeocode");
                    if (regeocode != null) {
                        JSONObject addressComponent = regeocode.optJSONObject("addressComponent");
                        if (addressComponent != null) {
                            String city = addressComponent.optString("city");
                            String district = addressComponent.optString("district");
                            String province = addressComponent.optString("province");

                            android.util.Log.d("AmapAPI", "City: " + city + ", District: " + district + ", Province: " + province);

                            if (city != null && !city.isEmpty() && !"[]".equals(city)) {
                                return city;
                            } else if (district != null && !district.isEmpty() && !"[]".equals(district)) {
                                if (province != null && !province.isEmpty() && !"[]".equals(province) &&
                                        (province.contains("北京") || province.contains("上海") ||
                                                province.contains("天津") || province.contains("重庆"))) {
                                    return province;
                                }
                                return district;
                            } else if (province != null && !province.isEmpty() && !"[]".equals(province)) {
                                return province;
                            }
                        }

                        String formattedAddress = regeocode.optString("formatted_address");
                        if (formattedAddress != null && !formattedAddress.isEmpty()) {
                            android.util.Log.d("AmapAPI", "Formatted address: " + formattedAddress);
                            return parseCity(formattedAddress);
                        }
                    }
                } else {
                    String info = jsonObject.optString("info");
                    android.util.Log.e("AmapAPI", "API Error - Status: " + status + ", Info: " + info);
                }
            } else {
                android.util.Log.e("AmapAPI", "HTTP Error: " + response.code() + " - " + response.message());
            }
        } catch (Exception e) {
            android.util.Log.e("AmapAPI", "Exception: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private String parseCity(String formattedAddress) {
        if (formattedAddress == null || formattedAddress.isEmpty()) {
            return null;
        }

        String[] cityKeywords = {"市", "自治区", "特别行政区"};
        String[] provinceKeywords = {"省", "自治区"};

        for (String keyword : cityKeywords) {
            int index = formattedAddress.indexOf(keyword);
            if (index > 0) {
                int start = Math.max(0, index - 10);
                String candidate = formattedAddress.substring(start, index + keyword.length());
                String[] separators = {"省", "自治区", "特别行政区"};
                for (String sep : separators) {
                    int sepIndex = candidate.lastIndexOf(sep);
                    if (sepIndex >= 0) {
                        candidate = candidate.substring(sepIndex + sep.length());
                        break;
                    }
                }
                return candidate.trim();
            }
        }

        for (String keyword : provinceKeywords) {
            int index = formattedAddress.indexOf(keyword);
            if (index > 0) {
                int start = Math.max(0, index - 10);
                String candidate = formattedAddress.substring(start, index + keyword.length());
                return candidate.trim();
            }
        }
        return null;
    }

    private String getCityByCoordinateRange(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        if (lat > 39 && lat < 41 && lon > 115 && lon < 117) {
            return "北京";
        }
        if (lat > 30 && lat < 32 && lon > 120 && lon < 122) {
            return "上海";
        }
        // 可以添加更多城市的坐标范围判断
        return null;
    }

    private void updateCityForFragments(String city) {
        // 使用Handler确保在主线程中执行
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + i);
                    if (fragment instanceof WeatherCurrentFragment) {
                        ((WeatherCurrentFragment) fragment).updateCity(city);
                    } else if (fragment instanceof WeatherForecastFragment) {
                        ((WeatherForecastFragment) fragment).updateCity(city);
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("UpdateCity", "Error updating fragments: " + e.getMessage());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // 侧边栏菜单的处理
        if (itemId == R.id.nav_city) {
            Intent intent = new Intent(this, CitySelectorActivity.class);
            citySelectorLauncher.launch(intent);
        } else if (itemId == R.id.nav_settings) {
            viewPager.setCurrentItem(4);  // 设置页跳转
        } else if (itemId == R.id.nav_about) {
            viewPager.setCurrentItem(5);
        } else {
            if (itemId == R.id.nav_current) {
                viewPager.setCurrentItem(0);
            } else if (itemId == R.id.nav_forecast) {
                viewPager.setCurrentItem(1);
            } else if (itemId == R.id.nav_music) {
                viewPager.setCurrentItem(2);
            } else if (itemId == R.id.nav_diary) {
                viewPager.setCurrentItem(3);
            }
        }

        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "定位权限被拒绝，使用默认城市：" + DEFAULT_CITY, Toast.LENGTH_LONG).show();
                updateCityForFragments(DEFAULT_CITY);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    // ViewPager适配器
    private static class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new WeatherCurrentFragment();
                case 1:
                    return new WeatherForecastFragment();
                case 2:
                    return new MusicPlayerFragment();
                case 3:
                    return new DiaryFragment();
                case 4:
                    return new SettingsFragment();
                case 5:
                    return new AboutFragment();
                default:
                    return new WeatherCurrentFragment();
            }
        }


        @Override
        public int getItemCount() {
            return 6;
        }
    }
}