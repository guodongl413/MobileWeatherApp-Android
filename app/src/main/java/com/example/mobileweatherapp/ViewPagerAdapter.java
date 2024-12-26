package com.example.mobileweatherapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

//public class ViewPagerAdapter extends FragmentStateAdapter {
//    private final List<Fragment> fragments;
//
//    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
//        super(fragmentActivity);
//        this.fragments = fragments;
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        return fragments.get(position);
//    }
//
//    @Override
//    public int getItemCount() {
//        return fragments.size();
//    }
//}

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragments;
    private final List<String> titles;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments, List<String> titles) {
        super(fragmentActivity);
        this.fragments = fragments;
        this.titles = titles;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    @Override
    public long getItemId(int position) {
        // 每个 Fragment 的唯一标识符
        return titles.get(position).hashCode();
    }

    @Override
    public boolean containsItem(long itemId) {
        // 检查是否包含指定的 Fragment
        for (String title : titles) {
            if (title.hashCode() == itemId) {
                return true;
            }
        }
        return false;
    }
}

