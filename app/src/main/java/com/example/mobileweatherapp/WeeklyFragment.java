package com.example.mobileweatherapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.common.hichartsclasses.HIArearange;
import com.highsoft.highcharts.common.hichartsclasses.HILine;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeeklyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeeklyFragment extends Fragment {

    private static final String INTERVALS_KEY = "intervalsArray";

    public static WeeklyFragment newInstance(JSONArray intervalsArray) {
        WeeklyFragment fragment = new WeeklyFragment();
        Bundle args = new Bundle();
        args.putString(INTERVALS_KEY, intervalsArray.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HIChartView chartView = view.findViewById(R.id.weekly_chart);

        // 获取传递的数据
        String intervalsArrayString = getArguments().getString(INTERVALS_KEY);
        Log.d("WeeklyFragment", "intervalsArrayString : " + intervalsArrayString);
        try {
            JSONArray intervalsArray = new JSONArray(intervalsArrayString);
            Log.d("WeeklyFragment", "intervalsArray : " + intervalsArray);

            // 解析数据
            List<Number> temperatureMin = new ArrayList<>();
            List<Number> temperatureMax = new ArrayList<>();
            List<String> dates = new ArrayList<>();

            for (int i = 0; i < intervalsArray.length(); i++) {
                JSONObject interval = intervalsArray.getJSONObject(i);
                JSONObject values = interval.getJSONObject("values");
                temperatureMin.add(values.getDouble("temperatureMin"));
                temperatureMax.add(values.getDouble("temperatureMax"));
                dates.add(interval.getString("startTime").split("T")[0]); // 提取日期部分
            }

            Log.d("WeeklyFragment", "temperatureMin : " + temperatureMin);
            Log.d("WeeklyFragment", "temperatureMax : " + temperatureMax);
            Log.d("WeeklyFragment", "dates : " + dates);

//            // 设置图表选项
//            HIOptions options = configureChartOptions(dates, temperatureMin, temperatureMax);
//
//            // 应用图表选项
//            chartView.setOptions(options);

            try {
                HIOptions options = configureChartOptions(dates, temperatureMin, temperatureMax);
//                HIOptions options = configureSimpleLineChart();
                chartView.setOptions(options);
                Log.d("WeeklyFragment", "Chart options successfully set.");
            } catch (Exception e) {
                Log.e("WeeklyFragment", "Error setting chart options: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HIOptions configureChartOptions(List<String> dates, List<Number> temperatureMin, List<Number> temperatureMax) {
        HIOptions options = new HIOptions();

        try {
            // 设置标题
            HITitle title = new HITitle();
            title.setText("Temperature Range");
            options.setTitle(title);

            // 配置 X 轴
            HIXAxis xAxis = new HIXAxis();
            xAxis.setCategories(dates); // 设置日期为 X 轴
            options.setXAxis(new ArrayList<>(List.of(xAxis)));

            // 配置 Y 轴
            HIYAxis yAxis = new HIYAxis();
            HITitle yAxisTitle = new HITitle();
            yAxisTitle.setText("Temperature");
            yAxis.setTitle(yAxisTitle);
            options.setYAxis(new ArrayList<>(List.of(yAxis)));

            // 配置 AreaRange 图表数据
            HIArearange areaRange = new HIArearange();
            areaRange.setName("Temperature Range");
            ArrayList<Object[]> data = new ArrayList<>();
            for (int i = 0; i < temperatureMin.size(); i++) {
                data.add(new Object[]{i, temperatureMin.get(i), temperatureMax.get(i)});
            }
            areaRange.setData(data);

            // 配置颜色
            HIGradient gradient = new HIGradient(0.0f, 0.0f, 0.0f, 1.0f);
            LinkedList<HIStop> stops = new LinkedList<>();
//            stops.add(new HIStop(0, HIColor.initWithRGB(30, 144, 255))); // 蓝色
//            stops.add(new HIStop(1, HIColor.initWithRGB(255, 140, 0)));  // 橙色

            stops.add(new HIStop(0, HIColor.initWithRGB(255, 140, 0))); // 蓝色
            stops.add(new HIStop(1, HIColor.initWithRGB(30, 144, 255)));  // 橙色

            areaRange.setColor(HIColor.initWithLinearGradient(gradient, stops));

            options.setSeries(new ArrayList<>(List.of(areaRange)));
            Log.d("WeeklyFragment", "HIOptions successfully configured.");
        } catch (Exception e) {
            Log.e("WeeklyFragment", "Error configuring HIOptions: " + e.getMessage());
            e.printStackTrace();
        }
        return options;
    }

    private HIOptions configureSimpleLineChart() {
        HIOptions options = new HIOptions();

        // 设置标题
        HITitle title = new HITitle();
        title.setText("Simple Line Chart");
        options.setTitle(title);

        // 配置 X 轴
        HIXAxis xAxis = new HIXAxis();
        xAxis.setCategories(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun"));
        options.setXAxis(new ArrayList<>(Collections.singletonList(xAxis)));

        // 配置 Y 轴
        HIYAxis yAxis = new HIYAxis();
        HITitle yAxisTitle = new HITitle();
        yAxisTitle.setText("Temperature");
        yAxis.setTitle(yAxisTitle);
        options.setYAxis(new ArrayList<>(Collections.singletonList(yAxis)));

        // 配置系列数据
        HILine line = new HILine();
        line.setName("Temperature");
        ArrayList<Object> data = new ArrayList<>();
        Collections.addAll(data, 29.9, 71.5, 106.4, 129.2, 144.0, 176.0);
        line.setData(data);
        options.setSeries(new ArrayList<>(Collections.singletonList(line)));

        return options;
    }


}