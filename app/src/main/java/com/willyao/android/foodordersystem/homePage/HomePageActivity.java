/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/27/17 10:16 AM
 */

package com.willyao.android.foodordersystem.homePage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.cartPage.CartActivity;
import com.willyao.android.foodordersystem.homePage.fragments.AmbienceFragment;
import com.willyao.android.foodordersystem.homePage.fragments.ContactFragment;
import com.willyao.android.foodordersystem.homePage.fragments.HomeFragment;
import com.willyao.android.foodordersystem.homePage.fragments.OrderHistoryFragment;
import com.willyao.android.foodordersystem.homePage.fragments.ProfileFragment;
import com.willyao.android.foodordersystem.homePage.fragments.TrackFragment;
import com.willyao.android.foodordersystem.startPage.SplashActivity;
import com.willyao.android.foodordersystem.utils.cartItem.ShoppingCartItem;
import com.willyao.android.foodordersystem.utils.sharedPreferences.MySPhelper;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomePageActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
                    GoogleApiClient.ConnectionCallbacks,
        ResultCallback<People.LoadPeopleResult>{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.nav_view) NavigationView navView;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;
    private static ProgressDialog pDialog;
    public static TextView cartNumber;
    private final String TAG = "HomePageActy";
    GoogleApiClient mGoogleApiClient;
    boolean mSignInclicked;

    public static String City;
    MaterialSearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        ButterKnife.bind(this);
        setupSearchView();
        setCity();
        init();
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

    //setup the search view on toolbar
    private void setupSearchView() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //try to set the city by search bar here, if the city name equals to "banglore"
                //or "delhi" pass the city to url, and get list of food, otherwise show city not available
                if (query.equalsIgnoreCase("banglore")){
                    HomePageActivity.City = "banglore";
                    HomePageActivity.this.recreate();

                } else if (query.equalsIgnoreCase("delhi")){
                    HomePageActivity.City = "delhi";
                    HomePageActivity.this.recreate();
                }
                Snackbar.make(findViewById(android.R.id.content), "Query: " + query, Snackbar.LENGTH_LONG)
                        .show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Log.i(TAG,"onSearchViewShown");
            }

            @Override
            public void onSearchViewClosed() {
                Log.i(TAG,"onSearchViewClosed");
            }
        });
    }

    /*try to implement search view in app bar*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Associate searchable configuration with the SearchView
        getMenuInflater().inflate(R.menu.options_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    //setup city
    private void setCity(){
        if (City == null){
            City = "banglore";
        }
    }

    private void init() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading now");
        pDialog.setCancelable(false);
            /*setup drawer*/
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /*setup number of items ordered*/
        cartNumber = (TextView) findViewById(R.id.cart_item_number);
        cartNumber.setText(String.valueOf(ShoppingCartItem.getInstance(this).getSize()));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to cart
                startActivity(new Intent(HomePageActivity.this, CartActivity.class));
            }
        });

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

        if(findViewById(R.id.main_fragment_container) != null) {
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, homeFragment).commit();
        }

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
                        fragment = new HomeFragment();
                        setTitle("Home Page");
                        break;
                    case R.id.nav_profile:
                        //switch to profile fragment
                        fragment = new ProfileFragment();
                        setTitle("Profile");
                        break;
                    case R.id.nav_history:
                        //switch to order history fragment
                        fragment = new OrderHistoryFragment();
                        setTitle("Order History");
                        break;
                    case R.id.nav_ambience:
                        fragment = new AmbienceFragment();
                        setTitle("Ambience");
                        break;
                    case R.id.nav_track:
                        //switch to track fragment
                        fragment = new TrackFragment();
                        setTitle("Track Order");
                        break;
                    case R.id.nav_help:
                        //switch to help fragment
                        fragment = new ContactFragment();
                        setTitle("Need Help?");
                        break;
                    case R.id.nav_logout:
                        //logout
                        MySPhelper.getInstance(HomePageActivity.this).clearSP();
                        LoginManager.getInstance().logOut();
                        if (mGoogleApiClient.isConnected()){
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
                        Intent splash = new Intent(HomePageActivity.this, SplashActivity.class);
                        startActivity(splash);
                        finish();
                        break;
                }
                drawerLayout.closeDrawers();

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, fragment)
                            .addToBackStack(HomePageActivity.this.getLocalClassName())
                            .commit();
                    return true;
                }
                return false;
            }
        });
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
