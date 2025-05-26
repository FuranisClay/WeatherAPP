package com.furan.network;

import com.furan.R;
import com.furan.model.City;
import com.furan.model.DailyWeatherData;
import com.furan.model.HourlyWeatherData;
import com.furan.model.WeatherData;
import com.furan.model.WeatherDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class WeatherApiService {

    private static final String API_KEY = "0834e2dbfe812be60ce5bb46a74aa17c";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String AIR_URL = "https://api.openweathermap.org/data/2.5/air_pollution";

    public WeatherData getCurrentWeather(String cityName) throws Exception {
        String location = URLEncoder.encode(convertCityNameToEnglishWithCountry(cityName), "UTF-8");
        String urlString = BASE_URL + "?q=" + location + "&appid=" + API_KEY + "&units=metric&lang=zh_cn";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            JSONObject json = new JSONObject(response.toString());
            double lat = json.getJSONObject("coord").getDouble("lat");
            double lon = json.getJSONObject("coord").getDouble("lon");
            String aqiText = fetchAirQuality(lat, lon);
            return parseWeatherData(json, aqiText);
        } else {
            throw new Exception("获取天气数据失败: " + connection.getResponseCode());
        }
    }

    private WeatherData parseWeatherData(JSONObject json, String aqiText) throws Exception {
        String location = json.getString("name");
        JSONObject main = json.getJSONObject("main");
        int temperature = (int) main.getDouble("temp");
        int tempMin = (int) main.getDouble("temp_min");
        int tempMax = (int) main.getDouble("temp_max");
        int humidity = main.getInt("humidity");

        JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
        String description = weather.getString("description");
        int weatherCode = weather.getInt("id");
        String iconCode = weather.getString("icon");

        JSONObject wind = json.optJSONObject("wind");
        double windSpeed = wind != null ? wind.getDouble("speed") : 0;

        String updateTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        List<WeatherDetail> detailList = new ArrayList<>();
        detailList.add(new WeatherDetail("湿度", humidity + "%"));
        detailList.add(new WeatherDetail("风速", windSpeed + " m/s"));
        detailList.add(new WeatherDetail("气压", main.optInt("pressure", 0) + " hPa"));
        detailList.add(new WeatherDetail("体感温度", (int) main.optDouble("feels_like", temperature) + "°C"));

        return new WeatherData(location, temperature, description, tempMin, tempMax,
                updateTime, getWeatherCode(weatherCode), aqiText, detailList);
    }

    private String fetchAirQuality(double lat, double lon) throws Exception {
        String urlString = AIR_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            int aqi = json.getJSONArray("list").getJSONObject(0).getJSONObject("main").getInt("aqi");

            switch (aqi) {
                case 1: return "优";
                case 2: return "良";
                case 3: return "轻度污染";
                case 4: return "中度污染";
                case 5: return "重度污染";
                default: return "未知";
            }
        } else {
            return "未知";
        }
    }

    // 城市列表，含部分中国主要城市
    private static final List<City> cityList = new ArrayList<>();

    static {
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
    }

    /**
     * 中文城市名转英文城市名(含国家码，构造形如：Beijing,CN)
     * 如果找不到对应城市，则返回原字符串。
     */
    private String convertCityNameToEnglishWithCountry(String cityName) {
        for (City city : cityList) {
            if (city.getName().equals(cityName)) {
                return city.getEnglishName() + "," + city.getCountryCode();
            }
        }
        return cityName;
    }


    /**
     * 获取未来每小时天气
     */
    public List<HourlyWeatherData> getHourlyForecast(String cityName) throws Exception {
        String location = convertCityNameToEnglishWithCountry(cityName);
        String urlString = FORECAST_URL + "?q=" + location + "&appid=" + API_KEY + "&units=metric&lang=zh_cn";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return parseHourlyData(response.toString());
        } else {
            throw new Exception("获取每小时天气失败: " + connection.getResponseCode());
        }
    }

    /**
     * 解析每小时天气
     */
    private List<HourlyWeatherData> parseHourlyData(String jsonString) throws Exception {
        List<HourlyWeatherData> hourlyList = new ArrayList<>();
        JSONObject json = new JSONObject(jsonString);
        JSONArray listArray = json.getJSONArray("list");

        int count = Math.min(listArray.length(), 8);

        for (int i = 0; i < count; i++) {
            JSONObject item = listArray.getJSONObject(i);
            long timestamp = item.getLong("dt") * 1000;
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));

            JSONObject main = item.getJSONObject("main");
            int temp = (int) main.getDouble("temp");
            int humidity = main.getInt("humidity");

            JSONObject weather = item.getJSONArray("weather").getJSONObject(0);
            String iconCode = weather.getString("icon");

            int iconRes = getWeatherIconRes(iconCode);

            hourlyList.add(new HourlyWeatherData(time, iconRes, temp + "°", humidity + "%"));
        }
        return hourlyList;
    }

    public List<DailyWeatherData> getDailyForecast(String cityName) throws Exception {
        String location = convertCityNameToEnglishWithCountry(cityName);
        String urlString = FORECAST_URL + "?q=" + location + "&appid=" + API_KEY + "&units=metric&lang=zh_cn";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return parseDailyData(response.toString());
        } else {
            throw new Exception("获取未来天气失败: " + connection.getResponseCode());
        }
    }

    /**
     * 解析未来七天天气
     */
    private List<DailyWeatherData> parseDailyData(String jsonString) throws Exception {
        List<DailyWeatherData> dailyList = new ArrayList<>();
        JSONObject json = new JSONObject(jsonString);
        JSONArray listArray = json.getJSONArray("list");

        Map<String, List<JSONObject>> dateMap = new LinkedHashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // 按日期分组
        for (int i = 0; i < listArray.length(); i++) {
            JSONObject item = listArray.getJSONObject(i);
            long timestamp = item.getLong("dt") * 1000;
            String dateStr = dateFormat.format(new Date(timestamp));
            dateMap.computeIfAbsent(dateStr, k -> new ArrayList<>()).add(item);
        }

        SimpleDateFormat weekFormat = new SimpleDateFormat("E", Locale.CHINA);

        int count = 0;
        for (String date : dateMap.keySet()) {
            if (count >= 7) break;

            List<JSONObject> dayItems = dateMap.get(date);

            double minTemp = Double.MAX_VALUE;
            double maxTemp = Double.MIN_VALUE;
            String iconCode = null;

            // 遍历当天所有时间点，找极值和最后一个图标代码
            for (JSONObject item : dayItems) {
                JSONObject main = item.getJSONObject("main");
                double tempMin = main.optDouble("temp_min", Double.NaN);
                double tempMax = main.optDouble("temp_max", Double.NaN);
                double temp = main.optDouble("temp", Double.NaN);

                if (!Double.isNaN(tempMin)) {
                    minTemp = Math.min(minTemp, tempMin);
                } else if (!Double.isNaN(temp)) {
                    minTemp = Math.min(minTemp, temp);
                }

                if (!Double.isNaN(tempMax)) {
                    maxTemp = Math.max(maxTemp, tempMax);
                } else if (!Double.isNaN(temp)) {
                    maxTemp = Math.max(maxTemp, temp);
                }

                JSONObject weather = item.getJSONArray("weather").getJSONObject(0);
                iconCode = weather.optString("icon", iconCode);
            }

            Date dateObj = dateFormat.parse(date);
            String weekDay = weekFormat.format(dateObj);

            String label;
            if (count == 0) label = "今天";
            else if (count == 1) label = "明天";
            else if (count == 2) label = "后天";
            else label = date.substring(5);

            int iconRes = getWeatherIconRes(iconCode);
            String description = getWeatherDescriptionByIcon(iconCode);

            dailyList.add(new DailyWeatherData(label, weekDay, iconRes, description,
                    String.format("%d°", (int) minTemp), String.format("%d°", (int) maxTemp)));
            count++;
        }
        return dailyList;
    }


    /**
     * 根据图标代码返回简化描述
     */
    private String getWeatherDescriptionByIcon(String iconCode) {
        if (iconCode == null || iconCode.isEmpty()) return "未知";

        switch (iconCode) {
            case "01d": case "01n": return "晴朗";         // clear sky
            case "02d": case "02n": return "少云";         // few clouds
            case "03d": case "03n": return "多云";         // scattered clouds
            case "04d": case "04n": return "阴天";         // broken clouds
            case "09d": case "09n": return "小于";         // shower rain
            case "10d": case "10n": return "阵雨";         // rain
            case "11d": case "11n": return "雷雨";         // thunderstorm
            case "13d": case "13n": return "下雪";         // snow
            case "50d": case "50n": return "雾霾";         // mist
            default: return "未知";
        }
    }





    private int getWeatherCode(int openWeatherCode) {
        if (openWeatherCode >= 200 && openWeatherCode < 300) return 3;
        if (openWeatherCode >= 300 && openWeatherCode < 400) return 3;
        if (openWeatherCode >= 500 && openWeatherCode < 600) return 4;
        if (openWeatherCode >= 600 && openWeatherCode < 700) return 5;
        if (openWeatherCode >= 700 && openWeatherCode < 800) return 2;
        if (openWeatherCode == 800) return 0;
        if (openWeatherCode > 800) return 1;
        return 0;
    }

    private static final Map<String, Integer> ICON_MAP = new HashMap<>();
    static {
        ICON_MAP.put("01d", R.drawable.ic_sunny);
        ICON_MAP.put("01n", R.drawable.ic_night_clear);
        ICON_MAP.put("02d", R.drawable.ic_partly_cloudy);
        ICON_MAP.put("02n", R.drawable.ic_partly_cloudy);
        ICON_MAP.put("03d", R.drawable.ic_scattered_clouds);
        ICON_MAP.put("03n", R.drawable.ic_scattered_clouds);
        ICON_MAP.put("04d", R.drawable.ic_broken_clouds);
        ICON_MAP.put("04n", R.drawable.ic_broken_clouds);
        ICON_MAP.put("09d", R.drawable.ic_light_rain);
        ICON_MAP.put("09n", R.drawable.ic_light_rain);
        ICON_MAP.put("10d", R.drawable.ic_rain);
        ICON_MAP.put("10n", R.drawable.ic_rain);
        ICON_MAP.put("11d", R.drawable.ic_thunderstorm);
        ICON_MAP.put("11n", R.drawable.ic_thunderstorm);
        ICON_MAP.put("13d", R.drawable.ic_snow);
        ICON_MAP.put("13n", R.drawable.ic_snow);
        ICON_MAP.put("50d", R.drawable.ic_mist);
        ICON_MAP.put("50n", R.drawable.ic_mist);
    }

    public int getWeatherIconRes(String iconCode) {
        if (iconCode == null) return R.drawable.ic_settings;
        Integer res = ICON_MAP.get(iconCode.toLowerCase(Locale.ROOT));
        return res != null ? res : R.drawable.ic_settings;
    }

}
