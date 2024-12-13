package com.example.mobileweatherapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highsoft.highcharts.common.hichartsclasses.*;
import com.highsoft.highcharts.core.HIFunction;

import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.common.hichartsclasses.HIBackground;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HIData;
import com.highsoft.highcharts.common.hichartsclasses.HIDataLabels;
import com.highsoft.highcharts.common.hichartsclasses.HIGauge;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPane;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions;
import com.highsoft.highcharts.common.hichartsclasses.HISolidgauge;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherDataFragment extends Fragment {

    private String renderIconsString = "function renderIcons() {" +
            "                            if(!this.series[0].icon) {" +
            "                               this.series[0].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8]).attr({'stroke': '#303030','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[0].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[0].points[0].shapeArgs.innerR -(this.series[0].points[0].shapeArgs.r - this.series[0].points[0].shapeArgs.innerR) / 2); if(!this.series[1].icon) {this.series[1].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8,'M', 8, -8, 'L', 16, 0, 8, 8]).attr({'stroke': '#ffffff','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[1].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[1].points[0].shapeArgs.innerR -(this.series[1].points[0].shapeArgs.r - this.series[1].points[0].shapeArgs.innerR) / 2); if(!this.series[2].icon) {this.series[2].icon = this.renderer.path(['M', 0, 8, 'L', 0, -8, 'M', -8, 0, 'L', 0, -8, 8, 0]).attr({'stroke': '#303030','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[2].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[2].points[0].shapeArgs.innerR -(this.series[2].points[0].shapeArgs.r - this.series[2].points[0].shapeArgs.innerR) / 2);}";

    private static final String INTERVALS_KEY = "intervalsArray";

    public static WeatherDataFragment newInstance(JSONArray intervalsArray) {
        WeatherDataFragment fragment = new WeatherDataFragment();
        Bundle args = new Bundle();
        args.putString(INTERVALS_KEY, intervalsArray.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HIChartView chartView = view.findViewById(R.id.weather_data_chart);

        // 获取传递的数据
        String intervalsArrayString = getArguments().getString(INTERVALS_KEY);
        try {
            JSONArray intervalsArray = new JSONArray(intervalsArrayString);

            // 解析 Cloud Cover, Precipitation 和 Humidity 数据
            List<Double> cloudCoverList = new ArrayList<>();
            List<Double> precipitationList = new ArrayList<>();
            List<Double> humidityList = new ArrayList<>();

            for (int i = 0; i < intervalsArray.length(); i++) {
                JSONObject interval = intervalsArray.getJSONObject(i);
                JSONObject values = interval.getJSONObject("values");

                cloudCoverList.add(values.getDouble("cloudCover"));
                precipitationList.add(values.getDouble("precipitationProbability"));
                humidityList.add(values.getDouble("humidity"));
            }

            // 取第一个时间点的数据作为当前显示数据
            double cloudCover = cloudCoverList.get(0);
            double precipitation = precipitationList.get(0);
            double humidity = humidityList.get(0);

            // 设置图表选项
            HIOptions options = configureChartOptions(cloudCover, precipitation, humidity);

            // 应用图表选项
            chartView.setOptions(options);
            chartView.reload(); // 刷新图表
            Log.d("WeatherDataFragment", "Chart options successfully set.");

        } catch (Exception e) {
            Log.e("WeatherDataFragment", "Error creating chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HIOptions configureChartOptions(double cloudCover, double precipitation, double humidity) {
        HIOptions options = new HIOptions();

        // 设置 Chart 类型
        HIChart chart = new HIChart();
        chart.setType("solidgauge");

        // 添加自定义图标的渲染事件
        chart.setEvents(new HIEvents());
        chart.getEvents().setRender(new HIFunction(renderIconsString));

        options.setChart(chart);

        // 设置标题
        HITitle title = new HITitle();
        title.setText("Stat Summary");
        options.setTitle(title);

        // 设置 Pane 和背景颜色
        HIPane pane = new HIPane();
        pane.setStartAngle(0);
        pane.setEndAngle(360);

        HIBackground paneBackground1 = new HIBackground();
        paneBackground1.setOuterRadius("112%");
        paneBackground1.setInnerRadius("88%");
        paneBackground1.setBackgroundColor(HIColor.initWithRGBA(106, 165, 231, 0.35)); // 蓝色背景
        paneBackground1.setBorderWidth(0);

        HIBackground paneBackground2 = new HIBackground();
        paneBackground2.setOuterRadius("87%");
        paneBackground2.setInnerRadius("63%");
        paneBackground2.setBackgroundColor(HIColor.initWithRGBA(255, 99, 71, 0.35)); // 橙色背景
        paneBackground2.setBorderWidth(0);

        HIBackground paneBackground3 = new HIBackground();
        paneBackground3.setOuterRadius("62%");
        paneBackground3.setInnerRadius("38%");
        paneBackground3.setBackgroundColor(HIColor.initWithRGBA(130, 238, 106, 0.35)); // 绿色背景
        paneBackground3.setBorderWidth(0);

        pane.setBackground(new ArrayList<>(Arrays.asList(paneBackground1, paneBackground2, paneBackground3)));
        options.setPane(new ArrayList<>(Collections.singletonList(pane)));

        // 设置 Y 轴
        HIYAxis yAxis = new HIYAxis();
        yAxis.setMin(0);
        yAxis.setMax(100);
        yAxis.setLineWidth(0);
        yAxis.setTickPositions(new ArrayList<>()); // 移除刻度线
        options.setYAxis(new ArrayList<>(Collections.singletonList(yAxis)));

        // 设置 PlotOptions
        HIPlotOptions plotOptions = new HIPlotOptions();
        HISolidgauge solidGauge = new HISolidgauge();
        HIDataLabels dataLabels = new HIDataLabels();
        dataLabels.setEnabled(false); // 禁用默认标签
        solidGauge.setDataLabels(new ArrayList<>(Collections.singletonList(dataLabels)));
        solidGauge.setLinecap("round");
        solidGauge.setStickyTracking(false);
        solidGauge.setRounded(true);
        plotOptions.setSolidgauge(solidGauge);
        options.setPlotOptions(plotOptions);

        // 添加数据系列
        HISolidgauge solidgauge1 = new HISolidgauge();
        solidgauge1.setName("Cloud Cover");
        HIData data1 = new HIData();
        data1.setColor(HIColor.initWithRGB(106, 165, 231)); // 蓝色
        data1.setRadius("112%");
        data1.setInnerRadius("88%");
        data1.setY(cloudCover);
        solidgauge1.setData(new ArrayList<>(Collections.singletonList(data1)));

        HISolidgauge solidgauge2 = new HISolidgauge();
        solidgauge2.setName("Precipitation");
        HIData data2 = new HIData();
        data2.setColor(HIColor.initWithRGB(255, 99, 71)); // 橙色
        data2.setRadius("87%");
        data2.setInnerRadius("63%");
        data2.setY(precipitation);
        solidgauge2.setData(new ArrayList<>(Collections.singletonList(data2)));

        HISolidgauge solidgauge3 = new HISolidgauge();
        solidgauge3.setName("Humidity");
        HIData data3 = new HIData();
        data3.setColor(HIColor.initWithRGB(130, 238, 106)); // 绿色
        data3.setRadius("62%");
        data3.setInnerRadius("38%");
        data3.setY(humidity);
        solidgauge3.setData(new ArrayList<>(Collections.singletonList(data3)));

        options.setSeries(new ArrayList<>(Arrays.asList(solidgauge1, solidgauge2, solidgauge3)));

        return options;
    }
}
