package com.furan.model;

public class WeatherDetail {
    private String name;
    private String value;

    public WeatherDetail(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public String getValue() { return value; }
}
