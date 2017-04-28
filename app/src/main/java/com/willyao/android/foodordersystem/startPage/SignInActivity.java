/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/19/17 4:09 PM
 */

package com.willyao.android.foodordersystem.startPage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.facebook.FacebookSdk;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.startPage.login.LoginFragment;
import com.willyao.android.foodordersystem.startPage.register.RegisterFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.sin_view_pager_tab) TabLayout tabLayout;
    //@BindView(R.id.toolbar) Toolbar toolbar;
    public static ViewPager sinViewPager;
    private static ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());//initialize facebook sdk
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        //setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading now");
        pDialog.setCancelable(false);

        sinViewPager = (ViewPager) findViewById(R.id.sin_view_pager);
        sinViewPager.setAdapter(new NumberPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(sinViewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.colorPrimaryDark),
                                   getResources().getColor(R.color.icons));
    }

    public static class NumberPagerAdapter extends FragmentPagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        // overriding getPageTitle()
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Sign in";
                case 1:
                    return "Sign up";
            }
            return null;
            //return tabTitles[position];
        }

        public NumberPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    LoginFragment tab1 = new LoginFragment();
                    return tab1;
                case 1:
                    RegisterFragment tab2 = new RegisterFragment();
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


        //---------------------------able to call fragment outside the viewpager
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
        //------------------------------------------
    }

    public static void showPDialog(){
        if (!pDialog.isShowing()){
            pDialog.show();
        }
    }
    public static void disPDialog(){
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
}
