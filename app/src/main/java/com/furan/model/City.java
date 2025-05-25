package com.furan.model;

public class City {
    private String name;
    private String englishName;
    private String countryCode;

    public City(String name, String englishName, String countryCode) {
        this.name = name;
        this.englishName = englishName;
        this.countryCode = countryCode;
    }

    public String getName() { return name; }
    public String getEnglishName() { return englishName; }
    public String getCountryCode() { return countryCode; }

    public void setName(String name) { this.name = name; }
    public void setEnglishName(String englishName) { this.englishName = englishName; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
}
