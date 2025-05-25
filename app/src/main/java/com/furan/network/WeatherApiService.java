package com.furan.network;

import com.furan.R;
import com.furan.model.WeatherData;
import com.furan.model.WeatherDetail;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherApiService {

    private static final String API_KEY = "your_api_key_here";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public WeatherData getCurrentWeather(String cityName) throws Exception {
        // 构建API请求URL
        String urlString = BASE_URL + "?q=" + cityName + "&appid=" + API_KEY + "&units=metric&lang=zh_cn";

        // 发送HTTP请求
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

            return parseWeatherData(response.toString());
        } else {
            throw new Exception("获取天气数据失败: " + connection.getResponseCode());
        }
    }

    private WeatherData parseWeatherData(String jsonString) throws Exception {
        JSONObject json = new JSONObject(jsonString);

        String location = json.getString("name");
        JSONObject main = json.getJSONObject("main");
        int temperature = (int) main.getDouble("temp");
        int tempMin = (int) main.getDouble("temp_min");
        int tempMax = (int) main.getDouble("temp_max");
        int humidity = main.getInt("humidity");

        JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
        String description = weather.getString("description");
        int weatherCode = weather.getInt("id");

        JSONObject wind = json.optJSONObject("wind");
        double windSpeed = wind != null ? wind.getDouble("speed") : 0;

        String updateTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // 创建详细信息列表
        List<WeatherDetail> detailList = new ArrayList<>();
        detailList.add(new WeatherDetail("湿度", humidity + "%"));
        detailList.add(new WeatherDetail("风速", windSpeed + " m/s"));
        detailList.add(new WeatherDetail("气压", main.optInt("pressure", 0) + " hPa"));
        detailList.add(new WeatherDetail("体感温度", (int) main.optDouble("feels_like", temperature) + "°C"));

        return new WeatherData(location, temperature, description, tempMin, tempMax,
                updateTime, getWeatherCode(weatherCode), "良 65", detailList);
    }

    private int getWeatherCode(int openWeatherCode) {
        // 将OpenWeatherMap的天气代码转换为内部代码
        if (openWeatherCode >= 200 && openWeatherCode < 300) return 3; // 雷雨
        if (openWeatherCode >= 300 && openWeatherCode < 400) return 3; // 小雨
        if (openWeatherCode >= 500 && openWeatherCode < 600) return 4; // 雨
        if (openWeatherCode >= 600 && openWeatherCode < 700) return 5; // 雪
        if (openWeatherCode >= 700 && openWeatherCode < 800) return 2; // 雾霾
        if (openWeatherCode == 800) return 0; // 晴
        if (openWeatherCode > 800) return 1; // 多云
        return 0;
    }
}