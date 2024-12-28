package com.example.mobileweatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherFragment extends Fragment {

    private static final String ARG_CITY = "city";
    private static final String ARG_STATE = "state";

    private RequestQueue requestQueue;
    private boolean isFavorite = false; // 用于跟踪当前状态
    private FloatingActionButton fabRemoveFromFav; // 定义 FloatingActionButton

    private LinearLayout progressLayout;
    private RelativeLayout mainContent;

    private String query;
    JSONArray intervalsArray;
    private JSONObject valuesObj;

    // 全局变量 - IPinfo 数据
    private String city;
    private String state;
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


    public static WeatherFragment newInstance(String city, String state) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, city);
        args.putString(ARG_STATE, state);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        // 获取城市参数并加载天气
        city = getArguments().getString(ARG_CITY);
        state = getArguments().getString(ARG_STATE);
//        fetchWeatherData(city);


        // 初始化 ProgressBar 和主界面
        progressLayout = view.findViewById(R.id.progress_layout);
        mainContent = view.findViewById(R.id.main_content);

        // 显示加载动画
        showProgressBar();

        // 初始化 UI 元素
        //Card 1
        temperatureTextView = view.findViewById(R.id.temperature);
        weatherSummaryTextView = view.findViewById(R.id.weather_summary);
        cityRegionTextView = view.findViewById(R.id.city_region);
        weatherIcon = view.findViewById(R.id.weather_icon);

        //Card 2
        humidityTextView = view.findViewById(R.id.humidity_value);
        windSpeedTextView = view.findViewById(R.id.windspeed_value);
        visibilityTextView = view.findViewById(R.id.visibility_value);
        pressureTextView = view.findViewById(R.id.pressure_value);

        //Card 3
        weeklyForecastTable = view.findViewById(R.id.weekly_forecast_table);

        // 加载天气数据
        fetchWeatherData(city, state);

        // 初始化 Card 1
        CardView weatherCard = view.findViewById(R.id.weather_card);

        // 设置点击事件
        weatherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到新的 Activity
                Intent intent = new Intent(requireActivity(), WeatherDetailActivity.class);

                // 将 city 和 region 作为额外数据传递
                intent.putExtra("city", city);
                intent.putExtra("region", state);
                intent.putExtra("temperature", temperature);

                // 将 intervalsArray 转换为字符串形式传递
                if (intervalsArray != null) {
                    intent.putExtra("intervalsArray", intervalsArray.toString());
                }

                startActivity(intent);
            }
        });

        fabRemoveFromFav = view.findViewById(R.id.fab_remove_from_favorites);

        // 初始化 RequestQueue
        requestQueue = Volley.newRequestQueue(requireContext());

        // 设置点击监听器
        fabRemoveFromFav.setOnClickListener(v -> {
            deleteFavoriteCity(city);
        });

        return view;
    }

    private void fetchWeatherData(String city, String state) {

        showProgressBar(); // 显示 ProgressBar

        String apiUrl = "https://backend-dot-weather-search-project-440903.wl.r.appspot.com/api/search?street=1&city="
                + city + "&state=" + state + "&useCurrentLocation=false";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest weatherRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                response -> {
                    try {
                        // 解析天气数据
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

                        // 更新 UI
                        updateWeatherUI();
                        updateWeeklyForecast(intervalsArray);

                        showMainContent();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    // 错误处理
                });

        queue.add(weatherRequest);
    }

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
        cityRegionTextView.setText(city + ", " + state);

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
                TableRow row = new TableRow(requireContext());
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
        TextView textView = new TextView(requireContext());
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
        TextView textView = new TextView(requireContext());
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
        ImageView imageView = new ImageView(requireContext());
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
        progressLayout.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);
    }

    private void showMainContent() {
        progressLayout.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);
    }

    private void deleteFavoriteCity(String city) {
        String url = "https://backend-dot-weather-search-project-440903.wl.r.appspot.com/api/favorites/" + Uri.encode(city);

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("WeatherFragment", city + " removed from favorites");

                    // 通知主界面更新
                    notifyHomeScreenToUpdate(city);
                },
                error -> {
                    Log.e("WeatherFragment", "Failed to remove favorite");
                    error.printStackTrace();
                });

        requestQueue.add(request);
    }

    private void notifyHomeScreenToUpdate(String city) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).removeCityFromFavorites(city);
        }
    }


}
