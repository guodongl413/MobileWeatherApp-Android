package com.example.mobileweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("WeatherApp", "onCreate is called");

        // 初始化自定义 Toolbar
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        // 可选：设置标题或菜单项
        getSupportActionBar().setTitle("WeatherApp");

        // 初始化 UI 组件
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        // 添加默认的 Home 页面
        fragments.add(new HomeWeatherFragment());
        titles.add("Home");

        // 加载收藏城市
        loadFavoriteCities();

        // 设置 ViewPager 和 TabLayout
        viewPagerAdapter = new ViewPagerAdapter(this, fragments, titles);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // 设置 Tab 标题
            tab.setText(titles.get(position));

            // 设置内容描述
            tab.setContentDescription("Tab " + titles.get(position));
        }).attach();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String action = data.getStringExtra("action");
            String city = data.getStringExtra("city");

            if ("add".equals(action)) {
                String state = data.getStringExtra("state");
                fragments.add(WeatherFragment.newInstance(city, state));
                titles.add(city);
            } else if ("remove".equals(action)) {
                int index = titles.indexOf(city);
                if (index != -1) {
                    fragments.remove(index);
                    titles.remove(index);
                }
            }

            // 通知适配器数据发生变化
            viewPagerAdapter.notifyDataSetChanged();

            // 重新绑定 TabLayout 和 ViewPager2
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                tab.setText(titles.get(position));
            }).attach();
        }
    }


    // 从后端 API 获取收藏城市
    private void loadFavoriteCities() {
        String url = "https://backend-dot-weather-search-project-440903.wl.r.appspot.com/api/favorites";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject cityObj = response.getJSONObject(i);
                            String city = cityObj.getString("city");
                            String state = cityObj.getString("state");

                            // 添加 Fragment
                            fragments.add(WeatherFragment.newInstance(city, state));
                            titles.add(city);
                        }

                        // 更新适配器
                        viewPagerAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to load favorite cities", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                });

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // 获取搜索视图并设置监听器
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setIcon(R.drawable.search); // 强制设置图标
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        searchView.setQueryHint("Search for a city...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交逻辑
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 自动完成逻辑
                fetchAutocompleteSuggestions(newText);
                return true;
            }
        });

        return true;
    }
//
    private void performSearch(String query) {
        if (query != null && !query.trim().isEmpty()) {
            Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
            intent.putExtra("query", query); // 将搜索关键词传递到搜索结果页面
            Log.d("MainActivity", "query" + query);
//            startActivity(intent);
            // 使用 startActivityForResult 传入 requestCode = 100
            startActivityForResult(intent, 100);
        }
    }

    private void fetchAutocompleteSuggestions(String input) {
        String autocompleteApiUrl = "https://backend-dot-weather-search-project-440903.wl.r.appspot.com/api/autocomplete?input=" + input;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, autocompleteApiUrl, null,
                response -> {
                    try {
                        JSONArray predictions = response.getJSONArray("predictions");
                        List<String> suggestions = new ArrayList<>();

                        for (int i = 0; i < predictions.length(); i++) {
                            JSONObject prediction = predictions.getJSONObject(i);
                            suggestions.add(prediction.getString("description"));
                        }

                        // 更新 RecyclerView
                        updateAutocompleteSuggestions(suggestions);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }

    private void updateAutocompleteSuggestions(List<String> suggestions) {
        RecyclerView recyclerView = findViewById(R.id.autocomplete_recycler);

        if (suggestions.isEmpty()) {
            recyclerView.setVisibility(View.GONE); // 隐藏列表
        } else {
            recyclerView.setVisibility(View.VISIBLE);

            // 初始化或更新适配器
            AutocompleteAdapter adapter = new AutocompleteAdapter(suggestions, suggestion -> {
                // 填充搜索框并执行搜索
                SearchView searchView = findViewById(R.id.action_search);
                searchView.setQuery(suggestion, false); // 填充搜索框
                performSearch(suggestion); // 执行搜索
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

}
