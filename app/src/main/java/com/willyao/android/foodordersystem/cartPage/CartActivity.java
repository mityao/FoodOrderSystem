/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 2:58 PM
 */

package com.willyao.android.foodordersystem.cartPage;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.cartPage.fragments.CartFragment;
import com.willyao.android.foodordersystem.homePage.HomePageActivity;
import com.willyao.android.foodordersystem.homePage.fragments.AmbienceFragment;
import com.willyao.android.foodordersystem.homePage.fragments.ContactFragment;
import com.willyao.android.foodordersystem.homePage.fragments.ProfileFragment;
import com.willyao.android.foodordersystem.homePage.fragments.TrackFragment;
import com.willyao.android.foodordersystem.startPage.SplashActivity;
import com.willyao.android.foodordersystem.utils.sharedPreferences.MySPhelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        ResultCallback<People.LoadPeopleResult>{

    @BindView(R.id.toolbar_container) Toolbar toolbar;
    @BindView(R.id.cart_nav_view) NavigationView navView;
    @BindView(R.id.cart_drawer_layout) DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;
    GoogleApiClient mGoogleApiClient;
    boolean mSignInclicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        init();

        Fresco.initialize(this);
        if (findViewById(R.id.cart_container) != null) {
            CartFragment cartFragment = new CartFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.cart_container, cartFragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    private void init() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer);
        drawerLayout.setDrawerListener(drawerToggle);
        setupDrawer(drawerLayout);
    }

    private void setupDrawer(final DrawerLayout drawerLayout) {
        View navHeaderView = navView.inflateHeaderView(R.layout.nav_header_view);
        /*setup user info in nav header*/
        TextView header_name = (TextView) navHeaderView.findViewById(R.id.nav_header_user_name);
        TextView header_mobile = (TextView) navHeaderView.findViewById(R.id.nav_header_mobile);
        ImageView header_portrait = (ImageView) navHeaderView.findViewById(R.id.nav_header_user_picture);
        header_name.setText(MySPhelper.getInstance(this).getName());
        header_mobile.setText(MySPhelper.getInstance(this).getMobile());
        header_portrait.setImageResource(R.drawable.user_ghost);//set up user portrait

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.isChecked()) {
                    drawerLayout.closeDrawers();
                    return true;
                }
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        //switch to home fragment
                        Intent home = new Intent(CartActivity.this, HomePageActivity.class);
                        startActivity(home);
                        finish();
                        break;
                    case R.id.nav_profile:
                        //switch to profile fragment
                        fragment = new ProfileFragment();
                        break;
//                    case R.id.nav_history:
//                        //switch to order history fragment
//                        fragment = new OrderHistoryFragment();
//                        break;
                    case R.id.nav_track:
                        //switch to track fragment
                        fragment = new TrackFragment();
                        break;
                    case R.id.nav_ambience:
                        fragment = new AmbienceFragment();
                        break;
                    case R.id.nav_help:
                        //switch to help fragment
                        fragment = new ContactFragment();
                        break;
                    case R.id.nav_logout:
                        //logout
                        MySPhelper.getInstance(CartActivity.this).clearSP();
                        LoginManager.getInstance().logOut();
                        if (mGoogleApiClient.isConnected()) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            Snackbar.make(findViewById(android.R.id.content), "Log out", Snackbar.LENGTH_LONG)
                                                    .show();
                                        }
                                    }
                            );
                        }
                        Intent splash = new Intent(CartActivity.this, SplashActivity.class);
                        startActivity(splash);
                        finish();
                        break;
                }
                drawerLayout.closeDrawers();

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.cart_container, fragment)
                            .addToBackStack(CartActivity.this.getLocalClassName())
                            .commit();
                    return true;
                }
                return false;
            }
        });

    }
    //----------------setup hamburger icon-------------------
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }//for details?

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }//for details?

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mSignInclicked = false;
        // updateUI(true);
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("LOGOUT", "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {

    }
    //---------------------------------------------------------
}
