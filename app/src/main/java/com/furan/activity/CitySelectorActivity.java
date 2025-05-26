package com.furan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furan.database.CityDatabaseHelper;
import com.furan.model.CityDB;
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
        // 点击返回图标，关闭 Activity
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new CityAdapter();
        rvCities.setLayoutManager(new LinearLayoutManager(this));
        rvCities.setAdapter(adapter);



        adapter.setOnCityClickListener(city -> {
            Intent intent = new Intent();
            intent.putExtra("selected_city", city.getName());  // 把选中的城市名放进Intent
            setResult(RESULT_OK, intent);   // 设置返回结果
            finish();                      // 关闭选择城市页面
        });

    }


    private void loadCityData() {
        CityDatabaseHelper dbHelper = new CityDatabaseHelper(this);

        List<CityDB> cityVList = dbHelper.getAllCities();
        cityList = convertCityVtoCity(cityVList);

        filteredCityList = new ArrayList<>(cityList);
        adapter.updateData(filteredCityList);
    }



    // CityV转City的转换方法
    private List<City> convertCityVtoCity(List<CityDB> cityVList) {
        List<City> cityList = new ArrayList<>();
        for (CityDB cityV : cityVList) {
            City city = new City(cityV.getName(), cityV.getEnglishName(), cityV.getCountryCode());
            cityList.add(city);
        }
        return cityList;
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
        if (cityList == null) return;

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

}
