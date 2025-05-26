package com.furan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.furan.model.CityDB;

import java.util.ArrayList;
import java.util.List;

public class CityDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "city.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CITY = "city_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CITY_CN = "city_cn";
    private static final String COLUMN_CITY_EN = "city_en";
    private static final String COLUMN_COUNTRY_CODE = "country_code";

    public CityDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CITY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CITY_CN + " TEXT NOT NULL, " +
                COLUMN_CITY_EN + " TEXT NOT NULL, " +
                COLUMN_COUNTRY_CODE + " TEXT NOT NULL" +
                ")";
        db.execSQL(createTable);

        insertInitialCities(db);
    }

    private void insertInitialCities(SQLiteDatabase db) {
        // 中国主要城市
        insertCity(db, "北京", "Beijing", "CN");
        insertCity(db, "上海", "Shanghai", "CN");
        insertCity(db, "广州", "Guangzhou", "CN");
        insertCity(db, "深圳", "Shenzhen", "CN");
        insertCity(db, "杭州", "Hangzhou", "CN");
        insertCity(db, "南京", "Nanjing", "CN");
        insertCity(db, "武汉", "Wuhan", "CN");
        insertCity(db, "成都", "Chengdu", "CN");
        insertCity(db, "重庆", "Chongqing", "CN");
        insertCity(db, "西安", "Xi'an", "CN");
        insertCity(db, "天津", "Tianjin", "CN");
        insertCity(db, "苏州", "Suzhou", "CN");
        insertCity(db, "青岛", "Qingdao", "CN");
        insertCity(db, "沈阳", "Shenyang", "CN");
        insertCity(db, "合肥", "Hefei", "CN");
        insertCity(db, "长沙", "Changsha", "CN");
        insertCity(db, "郑州", "Zhengzhou", "CN");
        insertCity(db, "大连", "Dalian", "CN");
        insertCity(db, "福州", "Fuzhou", "CN");
        insertCity(db, "昆明", "Kunming", "CN");
        insertCity(db, "哈尔滨", "Harbin", "CN");
        insertCity(db, "厦门", "Xiamen", "CN");
        insertCity(db, "宁波", "Ningbo", "CN");
        insertCity(db, "泉州", "Quanzhou", "CN");
        insertCity(db, "兰州", "Lanzhou", "CN");
        insertCity(db, "乌鲁木齐", "Urumqi", "CN");
        insertCity(db, "呼和浩特", "Hohhot", "CN");
        insertCity(db, "南昌", "Nanchang", "CN");
        insertCity(db, "珠海", "Zhuhai", "CN");
        insertCity(db, "东京", "Tokyo", "JP");
        insertCity(db, "首尔", "Seoul", "KR");
        insertCity(db, "新加坡", "Singapore", "SG");
        insertCity(db, "曼谷", "Bangkok", "TH");
        insertCity(db, "吉隆坡", "Kuala Lumpur", "MY");
        insertCity(db, "雅加达", "Jakarta", "ID");
        insertCity(db, "德里", "Delhi", "IN");
        insertCity(db, "孟买", "Mumbai", "IN");
        insertCity(db, "加德满都", "Kathmandu", "NP");
        insertCity(db, "达卡", "Dhaka", "BD");
        insertCity(db, "伦敦", "London", "GB");
        insertCity(db, "巴黎", "Paris", "FR");
        insertCity(db, "柏林", "Berlin", "DE");
        insertCity(db, "罗马", "Rome", "IT");
        insertCity(db, "马德里", "Madrid", "ES");
        insertCity(db, "莫斯科", "Moscow", "RU");
        insertCity(db, "阿姆斯特丹", "Amsterdam", "NL");
        insertCity(db, "布鲁塞尔", "Brussels", "BE");
        insertCity(db, "维也纳", "Vienna", "AT");
        insertCity(db, "斯德哥尔摩", "Stockholm", "SE");
        insertCity(db, "哥本哈根", "Copenhagen", "DK");
        insertCity(db, "赫尔辛基", "Helsinki", "FI");
        insertCity(db, "华沙", "Warsaw", "PL");
        insertCity(db, "布达佩斯", "Budapest", "HU");
        insertCity(db, "都柏林", "Dublin", "IE");
        insertCity(db, "纽约", "New York", "US");
        insertCity(db, "洛杉矶", "Los Angeles", "US");
        insertCity(db, "芝加哥", "Chicago", "US");
        insertCity(db, "休斯顿", "Houston", "US");
        insertCity(db, "迈阿密", "Miami", "US");
        insertCity(db, "多伦多", "Toronto", "CA");
        insertCity(db, "温哥华", "Vancouver", "CA");
        insertCity(db, "蒙特利尔", "Montreal", "CA");
        insertCity(db, "墨西哥城", "Mexico City", "MX");
        insertCity(db, "拉斯维加斯", "Las Vegas", "US");
        insertCity(db, "圣保罗", "São Paulo", "BR");
        insertCity(db, "里约热内卢", "Rio de Janeiro", "BR");
        insertCity(db, "布宜诺斯艾利斯", "Buenos Aires", "AR");
        insertCity(db, "利马", "Lima", "PE");
        insertCity(db, "波哥大", "Bogotá", "CO");
        insertCity(db, "圣地亚哥", "Santiago", "CL");
        insertCity(db, "卡拉卡斯", "Caracas", "VE");
        insertCity(db, "开罗", "Cairo", "EG");
        insertCity(db, "约翰内斯堡", "Johannesburg", "ZA");
        insertCity(db, "拉各斯", "Lagos", "NG");
        insertCity(db, "内罗毕", "Nairobi", "KE");
        insertCity(db, "阿克拉", "Accra", "GH");
        insertCity(db, "卡萨布兰卡", "Casablanca", "MA");
        insertCity(db, "悉尼", "Sydney", "AU");
        insertCity(db, "墨尔本", "Melbourne", "AU");
        insertCity(db, "奥克兰", "Auckland", "NZ");
        insertCity(db, "惠灵顿", "Wellington", "NZ");
        insertCity(db, "伊斯坦布尔", "Istanbul", "TR");
        insertCity(db, "迪拜", "Dubai", "AE");
        insertCity(db, "多哈", "Doha", "QA");
        insertCity(db, "利雅得", "Riyadh", "SA");
        insertCity(db, "巴格达", "Baghdad", "IQ");
        insertCity(db, "特拉维夫", "Tel Aviv", "IL");
        insertCity(db, "开普敦", "Cape Town", "ZA");
        insertCity(db, "哈瓦那", "Havana", "CU");
        insertCity(db, "蒙得维的亚", "Montevideo", "UY");
        insertCity(db, "萨格勒布", "Zagreb", "HR");
        insertCity(db, "卢布尔雅那", "Ljubljana", "SI");
        insertCity(db, "萨拉热窝", "Sarajevo", "BA");
        insertCity(db, "贝尔格莱德", "Belgrade", "RS");
        insertCity(db, "索非亚", "Sofia", "BG");
        insertCity(db, "布加勒斯特", "Bucharest", "RO");
        insertCity(db, "基辅", "Kyiv", "UA");
        insertCity(db, "明斯克", "Minsk", "BY");
        insertCity(db, "里加", "Riga", "LV");
        insertCity(db, "塔林", "Tallinn", "EE");
        insertCity(db, "维尔纽斯", "Vilnius", "LT");
        insertCity(db, "芝加哥", "Chicago", "US");
        insertCity(db, "费城", "Philadelphia", "US");
        insertCity(db, "亚特兰大", "Atlanta", "US");
        insertCity(db, "波士顿", "Boston", "US");
        insertCity(db, "西雅图", "Seattle", "US");
        insertCity(db, "底特律", "Detroit", "US");
        insertCity(db, "明尼阿波利斯", "Minneapolis", "US");
        insertCity(db, "圣地亚哥", "San Diego", "US");
        insertCity(db, "圣何塞", "San Jose", "US");
        insertCity(db, "丹佛", "Denver", "US");
        insertCity(db, "巴尔的摩", "Baltimore", "US");
        insertCity(db, "奥兰多", "Orlando", "US");
        insertCity(db, "蒙特雷", "Monterrey", "MX");
        insertCity(db, "瓜达拉哈拉", "Guadalajara", "MX");
        insertCity(db, "巴拿马城", "Panama City", "PA");
        insertCity(db, "利物浦", "Liverpool", "GB");
        insertCity(db, "曼彻斯特", "Manchester", "GB");
        insertCity(db, "伯明翰", "Birmingham", "GB");
        insertCity(db, "格拉斯哥", "Glasgow", "GB");
        insertCity(db, "布里斯托尔", "Bristol", "GB");
        insertCity(db, "香港", "Hong Kong", "HK");
        insertCity(db, "澳门", "Macau", "MO");
        insertCity(db, "东莞", "Dongguan", "CN");
        insertCity(db, "嘉兴", "Jiaxing", "CN");
        insertCity(db, "台州", "Taizhou", "CN");
        insertCity(db, "绍兴", "Shaoxing", "CN");
        insertCity(db, "温州", "Wenzhou", "CN");
        insertCity(db, "金华", "Jinhua", "CN");
        insertCity(db, "嘉峪关", "Jiayuguan", "CN");
        insertCity(db, "海口", "Haikou", "CN");
        insertCity(db, "兰州", "Lanzhou", "CN");
        insertCity(db, "乌鲁木齐", "Urumqi", "CN");
        insertCity(db, "银川", "Yinchuan", "CN");
        insertCity(db, "拉萨", "Lhasa", "CN");
        insertCity(db, "马尼拉", "Manila", "PH");
        insertCity(db, "胡志明市", "Ho Chi Minh City", "VN");
        insertCity(db, "河内", "Hanoi", "VN");
        insertCity(db, "科伦坡", "Colombo", "LK");
        insertCity(db, "珀斯", "Perth", "AU");
        insertCity(db, "布里斯班", "Brisbane", "AU");
        insertCity(db, "达喀尔", "Dakar", "SN");
        insertCity(db, "突尼斯", "Tunis", "TN");
        insertCity(db, "阿尔及尔", "Algiers", "DZ");
        insertCity(db, "库里蒂巴", "Curitiba", "BR");
        insertCity(db, "福塔莱萨", "Fortaleza", "BR");
        insertCity(db, "墨尔本", "Melbourne", "AU");
        insertCity(db, "哈利法克斯", "Halifax", "CA");
        insertCity(db, "奥斯陆", "Oslo", "NO");
        insertCity(db, "赫尔辛基", "Helsinki", "FI");
        insertCity(db, "布宜诺斯艾利斯", "Buenos Aires", "AR");
    }


    private void insertCity(SQLiteDatabase db, String cityCn, String cityEn, String countryCode) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_CN, cityCn);
        values.put(COLUMN_CITY_EN, cityEn);
        values.put(COLUMN_COUNTRY_CODE, countryCode);
        db.insert(TABLE_CITY, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        onCreate(db);
    }

    public long insertCity(CityDB city) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_CN, city.getName());
        values.put(COLUMN_CITY_EN, city.getEnglishName());
        values.put(COLUMN_COUNTRY_CODE, city.getCountryCode());

        long id = db.insert(TABLE_CITY, null, values);
        db.close();
        return id;
    }

    public void updateCity(CityDB city) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_CN, city.getName());
        values.put(COLUMN_CITY_EN, city.getEnglishName());
        values.put(COLUMN_COUNTRY_CODE, city.getCountryCode());

        db.update(TABLE_CITY, values, COLUMN_ID + " = ?", new String[]{String.valueOf(city.getId())});
        db.close();
    }

    public void deleteCity(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CITY, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<CityDB> getAllCities() {
        List<CityDB> cityList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITY,
                null, null, null, null, null, COLUMN_CITY_CN + " ASC");

        if (cursor.moveToFirst()) {
            do {
                CityDB city = new CityDB(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_CN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_EN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY_CODE))
                );
                cityList.add(city);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cityList;
    }
}
