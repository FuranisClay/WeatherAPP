package com.furan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.furan.R;
import com.furan.adapter.CityAdapter;
import com.furan.model.City;
import java.util.ArrayList;
import java.util.List;

public class CitySelectorActivity extends AppCompatActivity {

    private TextInputEditText etSearch;
    private RecyclerView rvCities;
    private CityAdapter adapter;
    private List<City> cityList;
    private List<City> filteredCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selector);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadCityData();
        setupSearch();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        rvCities = findViewById(R.id.rv_cities);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new CityAdapter();
        rvCities.setLayoutManager(new LinearLayoutManager(this));
        rvCities.setAdapter(adapter);

        adapter.setOnCityClickListener(city -> {
            Intent intent = new Intent();
            intent.putExtra("selected_city", city.getName());
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void loadCityData() {
        cityList = new ArrayList<>();
        // 添加一些示例城市数据
        cityList.add(new City("北京", "Beijing", "CN"));
        cityList.add(new City("上海", "Shanghai", "CN"));
        cityList.add(new City("广州", "Guangzhou", "CN"));
        cityList.add(new City("深圳", "Shenzhen", "CN"));
        cityList.add(new City("杭州", "Hangzhou", "CN"));
        cityList.add(new City("南京", "Nanjing", "CN"));
        cityList.add(new City("武汉", "Wuhan", "CN"));
        cityList.add(new City("成都", "Chengdu", "CN"));
        cityList.add(new City("重庆", "Chongqing", "CN"));
        cityList.add(new City("西安", "Xi'an", "CN"));

        filteredCityList = new ArrayList<>(cityList);
        adapter.updateData(filteredCityList);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCities(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterCities(String query) {
        filteredCityList.clear();

        if (query.isEmpty()) {
            filteredCityList.addAll(cityList);
        } else {
            for (City city : cityList) {
                if (city.getName().toLowerCase().contains(query.toLowerCase()) ||
                        city.getEnglishName().toLowerCase().contains(query.toLowerCase())) {
                    filteredCityList.add(city);
                }
            }
        }

        adapter.updateData(filteredCityList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
