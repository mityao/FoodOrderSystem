/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/19/17 2:12 PM
 */

package com.willyao.android.foodordersystem.homePage.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.willyao.android.foodordersystem.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.willyao.android.foodordersystem.homePage.HomePageActivity.City;

/**
 * Created by mitya on 4/16/2017.
 */

public class HomeFragment extends Fragment {

    @BindView(R.id.home_TV_Location) TextView cityLocationView;
    @BindView(R.id.home_tabLayout) TabLayout mTabLayout;
    @BindView(R.id.home_pager) ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private final String TAG = "Home fragment";
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTabLayout.setScrollPosition(position, 0, true);
                mTabLayout.setSelected(true);
                mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        cityLocationView.setText(City);
        Log.i(TAG, "city: " + City);
        return view;
    }

    //create a class for  pagerAdapter
    class SectionsPagerAdapter extends FragmentPagerAdapter{

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    VegTabFragment tab1 = new VegTabFragment();
                    return tab1;
                case 1:
                    NonVegTabFragment tab2 = new NonVegTabFragment();
                    return tab2;
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Veg";
                case 1:
                    return "Non Veg";
                default:
                    break;
            }
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
