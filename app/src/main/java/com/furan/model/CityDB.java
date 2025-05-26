package com.furan.model;

public class CityDB {
    private int id;              // 数据库主键id
    private String name;         // 中文名
    private String englishName;  // 英文名
    private String countryCode;

    public CityDB(int id, String name, String englishName, String countryCode) {
        this.id = id;
        this.name = name;
        this.englishName = englishName;
        this.countryCode = countryCode;
    }

    public CityDB(String name, String englishName, String countryCode) {
        this.name = name;
        this.englishName = englishName;
        this.countryCode = countryCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
