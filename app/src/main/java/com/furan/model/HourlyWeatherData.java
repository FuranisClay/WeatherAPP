package com.furan.model;

public class HourlyWeatherData {
    private String time;
    private int iconRes;
    private String temperature;
    private String humidity;

    public HourlyWeatherData(String time, int iconRes, String temperature, String humidity) {
        this.time = time;
        this.iconRes = iconRes;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public String getTime() { return time; }
    public int getIconRes() { return iconRes; }
    public String getTemperature() { return temperature; }
    public String getHumidity() { return humidity; }
}