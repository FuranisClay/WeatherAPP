package com.furan.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.furan.model.City;

public class CityDao {

    private SQLiteDatabase db;

    public CityDao(SQLiteDatabase database) {
        this.db = database;
    }

    // 根据中文城市名查英文+国家码
    public String getEnglishNameWithCountry(String cityName) {
        String result = cityName;  // 默认用原名
        Cursor cursor = db.rawQuery("SELECT english_name, country_code FROM city WHERE name = ?", new String[]{cityName});

        if (cursor != null && cursor.moveToFirst()) {
            String engName = cursor.getString(cursor.getColumnIndexOrThrow("english_name"));
            String country = cursor.getString(cursor.getColumnIndexOrThrow("country_code"));
            result = engName + "," + country;
        }

        if (cursor != null) cursor.close();
        return result;
    }
}
