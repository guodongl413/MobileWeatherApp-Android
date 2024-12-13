package com.example.mobileweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //ProgressBar
    private LinearLayout progressLayout;
    private RelativeLayout mainContent;

    JSONArray intervalsArray;
    private JSONObject valuesObj;

    // 全局变量 - IPinfo 数据
    private String city;
    private String region;
    private double latitude;
    private double longitude;

    // 全局变量 - 天气数据
    // Card 1
    private double temperature;
    private double temperatureApparent;
    private int weatherCode;

    //Card 2
    private double humidity;
    private double windSpeed;
    private double visibility;
    private double pressure;

    //Card 3
    private double temperatureMax;
    private double temperatureMin;

    // UI 组件
    //Card 1
    private TextView temperatureTextView;
    private TextView weatherSummaryTextView;
    private TextView cityRegionTextView;
    private ImageView weatherIcon;

    //Card 2
    private TextView humidityTextView;
    private TextView windSpeedTextView;
    private TextView visibilityTextView;
    private TextView pressureTextView;

    //Card 3
    private TableLayout weeklyForecastTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("WeatherApp", "onCreate is called");

        // 初始化 ProgressBar 和主界面
        progressLayout = findViewById(R.id.progress_layout);
        mainContent = findViewById(R.id.main_content);

        // 默认显示 ProgressBar，隐藏主界面
        showProgressBar();

        // 初始化 UI 元素
        //Card 1
        temperatureTextView = findViewById(R.id.temperature);
        weatherSummaryTextView = findViewById(R.id.weather_summary);
        cityRegionTextView = findViewById(R.id.city_region);
        weatherIcon = findViewById(R.id.weather_icon);

        //Card 2
        humidityTextView = findViewById(R.id.humidity_value);
        windSpeedTextView = findViewById(R.id.windspeed_value);
        visibilityTextView = findViewById(R.id.visibility_value);
        pressureTextView = findViewById(R.id.pressure_value);

        //Card 3
        weeklyForecastTable = findViewById(R.id.weekly_forecast_table);

        // 调用 IPinfo API 获取经纬度并加载天气数据
        fetchLocationAndWeather();

        // 初始化 Card 1
        CardView weatherCard = findViewById(R.id.weather_card);

        // 设置点击事件
        weatherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到新的 Activity
                Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);

                // 将 city 和 region 作为额外数据传递
                intent.putExtra("city", city);
                intent.putExtra("region", region);
                intent.putExtra("temperature", temperature);

                // 将 intervalsArray 转换为字符串形式传递
                if (intervalsArray != null) {
                    intent.putExtra("intervalsArray", intervalsArray.toString());
                }

                startActivity(intent);
            }
        });
    }

    /**
     * 使用 IPinfo 获取位置并调用天气 API
     */
    private void fetchLocationAndWeather() {

        showProgressBar(); // 显示 ProgressBar

        // 初始化 Volley 请求队列
        RequestQueue queue = Volley.newRequestQueue(this);

        // IPinfo API URL
        String ipInfoUrl = "https://ipinfo.io/json?token=29dda6a6f80db3";

        // 请求 IPinfo API
        JsonObjectRequest ipRequest = new JsonObjectRequest(Request.Method.GET, ipInfoUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("WeatherApp", "fetchLocationAndWeather Response: " + response.toString());

                        try {
                            // 从 IPinfo 获取位置数据
                            city = response.getString("city"); // 存储城市
                            region = response.getString("region"); // 存储区域
                            String loc = response.getString("loc");
                            String[] coordinates = loc.split(",");
                            latitude = Double.parseDouble(coordinates[0]); // 存储纬度
                            longitude = Double.parseDouble(coordinates[1]); // 存储经度

                            // 使用经纬度获取天气数据
                            fetchWeatherData(latitude, longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("WeatherApp", "Response: " + latitude);
                        Log.d("WeatherApp", "Response: " + longitude);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(ipRequest);
    }

    /**
     * 使用纬度和经度获取天气数据
     * @param latitude - 纬度
     * @param longitude - 经度
     */
    private void fetchWeatherData(double latitude, double longitude) {
        // 天气 API URL
        String weatherApiUrl = "https://backend-dot-weather-search-project-440903.wl.r.appspot.com/api/search?useCurrentLocation=true&latitude=" + latitude + "&longitude=" + longitude;

        // 初始化 Volley 请求队列
        RequestQueue queue = Volley.newRequestQueue(this);

        // 请求天气数据
        JsonObjectRequest weatherRequest = new JsonObjectRequest(Request.Method.GET, weatherApiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("WeatherApp", "fetchWeatherData is called");

                        try {
                            // 解析天气数据，提取当天的数据
                            JSONObject dailyObj = response.getJSONObject("daily");
                            JSONObject dataObj = dailyObj.getJSONObject("data");
                            JSONArray timelinesArray = dataObj.getJSONArray("timelines");
                            JSONObject firstTimeline = timelinesArray.getJSONObject(0);
                            intervalsArray = firstTimeline.getJSONArray("intervals");
                            JSONObject firstInterval = intervalsArray.getJSONObject(0);
                            valuesObj = firstInterval.getJSONObject("values");

                            // 存储天气变量
                            temperature = valuesObj.getDouble("temperature");
                            temperatureApparent = valuesObj.getDouble("temperatureApparent");
                            weatherCode = valuesObj.getInt("weatherCode");

                            //Card 2
                            humidity = valuesObj.getDouble("humidity");
                            windSpeed = valuesObj.getDouble("windSpeed");
                            visibility = valuesObj.getDouble("visibility");
                            pressure = valuesObj.getDouble("pressureSeaLevel");

                            //Card 3
                            temperatureMax = valuesObj.getDouble("temperatureMax");
                            temperatureMin = valuesObj.getDouble("temperatureMin");

                            // 更新 UI
                            updateWeatherUI();
                            updateWeeklyForecast(intervalsArray);

                            showMainContent(); // 显示主界面
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("WeatherApp", "Response: " + temperature);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(weatherRequest);
    }

    /**
     * 将天气数据更新到 UI
     */
    private void updateWeatherUI() {

        //Update Card 1
        // 设置温度
        temperatureTextView.setText(String.format("%.0f°F", temperature));

        // 设置天气概况（根据 weatherCode 设置文字和图标）
        switch (weatherCode) {
            case 1000:
                weatherSummaryTextView.setText("Clear");
                weatherIcon.setImageResource(R.drawable.clear_day);
                break;
            case 1100:
                weatherSummaryTextView.setText("Mostly Clear");
                weatherIcon.setImageResource(R.drawable.mostly_clear_day);
                break;
            case 1101:
                weatherSummaryTextView.setText("Partly Cloudy");
                weatherIcon.setImageResource(R.drawable.partly_cloudy_day);
                break;
            // 添加更多 weatherCode 的处理逻辑...
            default:
                weatherSummaryTextView.setText("Clear");
                weatherIcon.setImageResource(R.drawable.clear_day);
        }

        //Update Card 2
        // 设置城市和地区
        cityRegionTextView.setText(city + ", " + region);

        // 更新湿度
        humidityTextView.setText(String.format("%.0f%%", humidity));

        // 更新风速
        windSpeedTextView.setText(String.format("%.2fmph", windSpeed));

        // 更新能见度
        visibilityTextView.setText(String.format("%.2fmi", visibility));

        // 更新气压
        pressureTextView.setText(String.format("%.1finHg", pressure));

    }

    private void updateWeeklyForecast(JSONArray intervalsArray) {
        // 清除之前的表格内容
        weeklyForecastTable.removeAllViews();

        // 添加表头
//        TableRow headerRow = new TableRow(this);
//        headerRow.addView(createTextView("Date"));
//        headerRow.addView(createTextView("Icon"));
//        headerRow.addView(createTextView("Min Temp"));
//        headerRow.addView(createTextView("Max Temp"));
//        weeklyForecastTable.addView(headerRow);

        // 遍历每一天的数据
        for (int i = 0; i < intervalsArray.length(); i++) {
            try {
                JSONObject interval = intervalsArray.getJSONObject(i);
                JSONObject values = interval.getJSONObject("values");
                String date = interval.getString("startTime").split("T")[0]; // 提取日期
                int weatherCode = values.getInt("weatherCode"); // 天气代码
                int minTemp = (int) Math.round(values.getDouble("temperatureMin")); // 最低温
                int maxTemp = (int) Math.round(values.getDouble("temperatureMax")); // 最高温

                // 创建一行
                TableRow row = new TableRow(this);
                row.addView(createDateView(date));
                row.addView(createIconView(weatherCode));
                row.addView(createTextView(String.valueOf(minTemp)));
                row.addView(createTextView(String.valueOf(maxTemp)));
                weeklyForecastTable.addView(row);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //
    private TextView createDateView(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
        textView.setText(text);

        textView.setSingleLine(true); // 确保单行显示
        textView.setEllipsize(null); // 不截断文字

        textView.setGravity(Gravity.CENTER);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }

    // 创建文本视图
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        textView.setText(text);

//        textView.setSingleLine(true); // 确保单行显示
//        textView.setEllipsize(null); // 不截断文字

        textView.setGravity(Gravity.CENTER);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }

    // 创建图标视图
    private ImageView createIconView(int weatherCode) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
//        imageView.setLayoutParams(new TableRow.LayoutParams(
//                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        switch (weatherCode) {
            case 1000:
                imageView.setImageResource(R.drawable.clear_day);
                break;
            case 1001:
                imageView.setImageResource(R.drawable.cloudy);
                break;
            // 添加更多天气代码
            default:
                imageView.setImageResource(R.drawable.clear_day);
        }
        return imageView;
    }

    private void showProgressBar() {
        progressLayout.setVisibility(View.VISIBLE); // 显示 ProgressBar
        mainContent.setVisibility(View.GONE);      // 隐藏主界面
    }

    private void showMainContent() {
        progressLayout.setVisibility(View.GONE);   // 隐藏 ProgressBar
        mainContent.setVisibility(View.VISIBLE);  // 显示主界面
    }

}
