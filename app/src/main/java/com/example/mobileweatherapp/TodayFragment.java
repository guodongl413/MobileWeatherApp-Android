package com.example.mobileweatherapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TodayFragment extends Fragment {

    private JSONArray intervalsArray;
    private JSONObject currentlyData;
    private GridLayout gridLayout;

    public TodayFragment() {
        // Required empty public constructor
    }

    public static TodayFragment newInstance(JSONArray intervalsArray) {

        Log.d("TodayFragment", "intervalsArray" + intervalsArray.toString());
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putString("intervalsArray", intervalsArray.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                String intervalsArrayString = getArguments().getString("intervalsArray");
                intervalsArray = new JSONArray(intervalsArrayString);

                Log.d("TodayFragment", "intervalsArray : " + intervalsArray);

                // 获取 "现在" 数据
                if (intervalsArray.length() > 0) {
                    currentlyData = intervalsArray.getJSONObject(0).getJSONObject("values");
                }
                Log.d("TodayFragment", "currentlyData : " + currentlyData);
            } catch (JSONException e) {
                Log.e("TodayFragment", "Error parsing intervals array", e);
                intervalsArray = new JSONArray();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        gridLayout = view.findViewById(R.id.today_grid);

        if (currentlyData != null) {
            setupWeatherCards();
        }

        return view;
    }

    private void setupWeatherCards() {
        // 定义天气卡片的数据
        int[] icons = {
                R.drawable.wind, R.drawable.pressure, R.drawable.rain_card,
                R.drawable.ic_thermometer, R.drawable.clear_day, R.drawable.humidity,
                R.drawable.visibility, R.drawable.cloud_cover, R.drawable.uv
        };
        String[] descriptions = {
                "Wind Speed", "Pressure", "Precipitation", "Temperature", "Weather",
                "Humidity", "Visibility", "Cloud Cover", "UV Index"
        };
        String[] keys = {
                "windSpeed", "pressureSeaLevel", "precipitationIntensity",
                "temperature", "icon", "humidity", "visibility", "cloudCover", "uvIndex"
        };
        String[] units = {
                "mph", "inHg", "%", "°F", "", "%", "mi", "%", ""
        };

        for (int i = 0; i < keys.length; i++) {
            try {
                View cardView = getLayoutInflater().inflate(R.layout.item_weather_card, gridLayout, false);

                ImageView iconView = cardView.findViewById(R.id.card_icon);
//                TextView descriptionView = cardView.findViewById(R.id.card_description);
                TextView valueView = cardView.findViewById(R.id.card_value);
                TextView descriptionView = cardView.findViewById(R.id.card_description);

                // 设置图标
//                iconView.setImageResource(icons[i]);

                // 设置描述
                descriptionView.setText(descriptions[i]);

                // 设置值和单位
//                String value = formatValue(currentlyData, keys[i], units[i]);
//                valueView.setText(value);

                // 关键修改：处理 "Weather Icon" 卡片
                if (keys[i].equals("icon")) {
                    try {
                        int weatherCode = currentlyData.getInt("weatherCode");
                        switch (weatherCode) {
                            case 1000:
                                iconView.setImageResource(R.drawable.clear_day);
                                valueView.setText("Clear"); // 设置 value
                                break;
                            case 1001:
                                iconView.setImageResource(R.drawable.cloudy);
                                valueView.setText("Cloudy"); // 设置 value
                                break;
                            case 1100:
                                iconView.setImageResource(R.drawable.mostly_clear_day);
                                valueView.setText("Mostly Clear");
                                break;
                            case 1101:
                                iconView.setImageResource(R.drawable.partly_cloudy_day);
                                valueView.setText("Partly Cloudy");
                                break;
                            case 1102:
                                iconView.setImageResource(R.drawable.mostly_cloudy);
                                valueView.setText("Mostly Cloudy");
                                break;
                            case 1003:
                                iconView.setImageResource(R.drawable.partly_cloudy_day);
                                valueView.setText("Partly Cloudy");
                                break;
                            case 1103:
                                iconView.setImageResource(R.drawable.partly_cloudy_day);
                                valueView.setText("Partly Cloudy");
                                break;
                            case 4000:
                                iconView.setImageResource(R.drawable.drizzle);
                                valueView.setText("Drizzle");
                                break;
                            case 4001:
                                iconView.setImageResource(R.drawable.rain);
                                valueView.setText("Rain");
                                break;
                            case 4200:
                                iconView.setImageResource(R.drawable.rain_light);
                                valueView.setText("Light Rain");
                                break;
                            case 4201:
                                iconView.setImageResource(R.drawable.rain_heavy);
                                valueView.setText("Heavy Rain");
                                break;
                            case 5000:
                                iconView.setImageResource(R.drawable.snow);
                                valueView.setText("Snow");
                                break;
                            case 5001:
                                iconView.setImageResource(R.drawable.flurries);
                                valueView.setText("Flurries");
                                break;
                            case 5100:
                                iconView.setImageResource(R.drawable.snow_light);
                                valueView.setText("Light Snow");
                                break;
                            case 5101:
                                iconView.setImageResource(R.drawable.snow_heavy);
                                valueView.setText("Heavy Snow");
                                break;
                            case 6000:
                                iconView.setImageResource(R.drawable.freezing_drizzle);
                                valueView.setText("Freezing Drizzle");
                                break;
                            case 6001:
                                iconView.setImageResource(R.drawable.freezing_rain);
                                valueView.setText("Freezing Rain");
                                break;
                            case 6200:
                                iconView.setImageResource(R.drawable.freezing_rain);
                                valueView.setText("Freezing Rain");
                                break;
                            case 6201:
                                iconView.setImageResource(R.drawable.freezing_rain_heavy);
                                valueView.setText("Heavy Freezing Rain");
                                break;
                            case 7000:
                                iconView.setImageResource(R.drawable.ice_pellets);
                                valueView.setText("Ice Pellets");
                                break;
                            case 7101:
                                iconView.setImageResource(R.drawable.ice_pellets_heavy);
                                valueView.setText("Heavy Ice Pellets");
                                break;
                            case 7102:
                                iconView.setImageResource(R.drawable.ice_pellets_light);
                                valueView.setText("Light Ice Pellets");
                                break;
                            case 8000:
                                iconView.setImageResource(R.drawable.tstorm);
                                valueView.setText("Thunderstorm");
                                break;
                            default:
                                iconView.setImageResource(R.drawable.clear_day); // 默认图标
                                valueView.setText("Clear"); // 默认 value
                                break;
                        }
                    } catch (JSONException e) {
                        Log.e("TodayFragment", "Error getting weatherCode", e);
                        iconView.setImageResource(R.drawable.clear_day); // 发生异常时的默认图标
                        valueView.setText("N/A"); // 发生异常时的默认value
                    }
                } else { // 其他卡片正常设置
                    String value = formatValue(currentlyData, keys[i], units[i]);
                    iconView.setImageResource(icons[i]); //设置对应的icon
                    valueView.setText(value);

                }

                // 创建 GridLayout.LayoutParams 并设置 columnWeight
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.width = 0;
                gridLayout.addView(cardView, params);

//                gridLayout.addView(cardView);
            } catch (Exception e) {
                Log.e("TodayFragment", "Error creating weather card", e);
            }
        }
    }

    private String formatValue(JSONObject data, String key, String unit) {
        try {
            switch (key) {
                case "windSpeed":
                case "pressureSeaLevel":
                case "precipitationIntensity":
                case "temperature":
                    return String.format("%.0f %s", data.getDouble(key), unit);
                case "humidity":
                case "visibility":
                case "cloudCover":
                case "uvIndex":
                    double value = data.getDouble(key);
                    return String.format("%.1f %s", value, unit);
                case "icon":
                    return data.getString(key);
                default:
                    return "N/A";
            }
        } catch (JSONException e) {
            Log.e("TodayFragment", "Error formatting value for " + key, e);
            return "N/A";
        }
    }

}