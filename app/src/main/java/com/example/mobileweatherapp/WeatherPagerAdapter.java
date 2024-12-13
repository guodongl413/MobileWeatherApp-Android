package com.example.mobileweatherapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.json.JSONArray;

public class WeatherPagerAdapter extends FragmentStateAdapter {

    private JSONArray intervalsArray;

    public WeatherPagerAdapter(@NonNull FragmentActivity fragmentActivity, JSONArray intervalsArray) {
        super(fragmentActivity);
        this.intervalsArray = intervalsArray;

        Log.d("WeatherPagerAdapter", "intervalsArray : " + intervalsArray);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Log.d("WeatherPagerAdapter", "intervalsArray : " + intervalsArray);
        switch (position) {
            case 0:
                return TodayFragment.newInstance(intervalsArray);  // Today Tab
            case 1:
                return WeeklyFragment.newInstance(intervalsArray); // Weekly Tab
            case 2:
                return WeatherDataFragment.newInstance(intervalsArray); // Weather Data Tab
            default:
                return TodayFragment.newInstance(intervalsArray);  // 默认返回 Today
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Tab 数量
    }
}
