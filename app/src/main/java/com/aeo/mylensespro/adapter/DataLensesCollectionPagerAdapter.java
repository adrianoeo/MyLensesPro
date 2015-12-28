package com.aeo.mylensespro.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.fragment.LeftDataFragment;
import com.aeo.mylensespro.fragment.RightDataFragment;

import java.util.HashMap;
import java.util.Map;

public class DataLensesCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUMBER_FRAGMENTS = 2;
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();
    private Context context;

    public DataLensesCollectionPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = LeftDataFragment.newInstance();
            mPageReferenceMap.put(position, fragment);
        } else {
            fragment = RightDataFragment.newInstance();
            mPageReferenceMap.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return NUMBER_FRAGMENTS;
    }

    public Fragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = context.getString(R.string.tabLeftLens);
                break;
            case 1:
                title = context.getString(R.string.tabRightLens);
                break;
        }

        return title;
    }
}
