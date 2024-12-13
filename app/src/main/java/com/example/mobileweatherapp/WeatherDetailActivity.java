package com.example.mobileweatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;

public class WeatherDetailActivity extends AppCompatActivity {

    private String city;
    private String region;
    private double temperature;
    private String intervalsArrayString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 获取 Intent 并解析传递的数据
        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        region = intent.getStringExtra("region");
        // 获取传递的 temperature
        temperature = intent.getDoubleExtra("temperature", 0.0);
        intervalsArrayString = intent.getStringExtra("intervalsArray");

        JSONArray intervalsArray = null;
        if (intervalsArrayString != null) {
            try {
                intervalsArray = new JSONArray(intervalsArrayString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 设置标题
        if (getSupportActionBar() != null) {
            // 使用传递过来的 city 和 region
            getSupportActionBar().setTitle(city + ", " + region);
            // 显示返回按钮（如果需要）
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // 可以根据需要设置返回按钮图标（默认是返回箭头）
            // toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        }

        // 返回按钮点击事件
        toolbar.setNavigationOnClickListener(v -> finish());

        // 如果需要在此处处理 intervalsArray 的逻辑，继续添加你的代码
        Log.d("WeatherDetailActivity", "City: " + city);
        Log.d("WeatherDetailActivity", "Region: " + region);
        Log.d("WeatherDetailActivity", "Temperature: " + temperature);
        if (intervalsArray != null) {
            // 示例：打印日志
            Log.d("WeatherDetailActivity", "Intervals: " + intervalsArray.toString());
        }

        // 初始化 TabLayout 和 ViewPager2
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        // 设置适配器
        WeatherPagerAdapter adapter = new WeatherPagerAdapter(this, intervalsArray);
        viewPager.setAdapter(adapter);

        // 将 TabLayout 与 ViewPager2 关联
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Today");
                            tab.setContentDescription("Today's Weather Tab");
                            tab.setIcon(R.drawable.today);
                            break;
                        case 1:
                            tab.setText("Weekly");
                            tab.setContentDescription("Weekly Weather Forecast Tab");
                            tab.setIcon(R.drawable.weekly_tab);
                            break;
                        case 2:
                            tab.setText("Weather Data");
                            tab.setContentDescription("Detailed Weather Information Tab");
                            tab.setIcon(R.drawable.ic_thermometer);
                            break;
                    }
                }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_twitter) {

            // 生成推文内容
            String tweetText = String.format("Check Out %s,%s's Weather! It is %.1f°F! #CSCI571WeatherSearch", city, region, temperature);

            // 使用 Twitter Intent 打开浏览器
            String twitterUrl = "https://twitter.com/intent/tweet?text=" + Uri.encode(tweetText);

            // 创建 Intent 跳转到浏览器
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
            startActivity(browserIntent);

            return true;

            // 处理 Twitter 点击事件
//            Toast.makeText(this, "Share to Twitter", Toast.LENGTH_SHORT).show();
//            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}