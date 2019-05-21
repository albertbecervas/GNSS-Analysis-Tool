package com.abecerra.gnssanalysis.core.utils;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CustomPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments = new ArrayList<>();

    public CustomPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Our custom method that populates this Adapter with Fragments
    public void addFragments(Fragment fragment) {
        if (!fragments.contains(fragment)) {
            fragments.add(fragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void clearItems() {
        fragments.clear();
    }

}