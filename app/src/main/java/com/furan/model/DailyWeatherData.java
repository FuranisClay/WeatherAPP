package com.furan.model;

public class DailyWeatherData {
    private String date;
    private String week;
    private int iconRes;
    private String description;
    private String tempMin;
    private String tempMax;

    private String iconCode;


    public DailyWeatherData(String date, String week, int iconRes,
                            String description, String tempMin, String tempMax) {
        this.date = date;
        this.week = week;
        this.iconRes = iconRes;
        this.description = description;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public String getDate() { return date; }
    public String getWeek() { return week; }
    public int getIconRes() { return iconRes; }
    public String getDescription() { return description; }
    public String getTempMin() { return tempMin; }
    public String getTempMax() { return tempMax; }
}