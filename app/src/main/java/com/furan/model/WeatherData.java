package com.furan.model;

import java.util.List;

public class WeatherData {
    private String location;
    private int temperature;
    private String description;
    private int tempMin;
    private int tempMax;
    private String updateTime;
    private int weatherCode;
    private String aqiText;
    private List<WeatherDetail> detailList;

    // 构造函数
    public WeatherData(String location, int temperature, String description,
                       int tempMin, int tempMax, String updateTime,
                       int weatherCode, String aqiText, List<WeatherDetail> detailList) {
        this.location = location;
        this.temperature = temperature;
        this.description = description;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.updateTime = updateTime;
        this.weatherCode = weatherCode;
        this.aqiText = aqiText;
        this.detailList = detailList;
    }

    // Getter方法
    public String getLocation() { return location; }
    public int getTemperature() { return temperature; }
    public String getDescription() { return description; }
    public int getTempMin() { return tempMin; }
    public int getTempMax() { return tempMax; }
    public String getUpdateTime() { return updateTime; }
    public int getWeatherCode() { return weatherCode; }
    public String getAqiText() { return aqiText; }
    public List<WeatherDetail> getDetailList() { return detailList; }
}