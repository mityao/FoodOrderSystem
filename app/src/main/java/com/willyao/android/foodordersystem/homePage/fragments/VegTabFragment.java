/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/21/17 4:50 PM
 */

package com.willyao.android.foodordersystem.homePage.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.homePage.HomePageActivity;
import com.willyao.android.foodordersystem.homePage.adapters.VegAdapter;
import com.willyao.android.foodordersystem.model.Food;
import com.willyao.android.foodordersystem.utils.volley.VolleyController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mitya on 4/16/2017.
 */

public class VegTabFragment extends Fragment {

    private final String VEG_URL = "http://rjtmobile.com/ansari/fos/fosapp/fos_food.php?food_category=veg&city=";
    private final String TAG = "VEGFOOD";

    ArrayList<Food> foods = new ArrayList<>();
    private VegAdapter adapter;
    @BindView(R.id.recyclerview_veg) RecyclerView mRecyclerView;
    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Request Data From Web Service
        if (foods.size() == 0){
            objRequestMethod();
        }
        View view = inflater.inflate(R.layout.fragment_veg_tab, container, false);
        unbinder = ButterKnife.bind(this, view);
        adapter = new VegAdapter(getContext(), foods);
        adapter.setOnItemClickListenser(new VegAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                //collect data of food and pass to foodDetail fragment
                Bundle foodInfo = new Bundle();
                for (int i = 0; i < foods.size(); i++){
                    if (foods.get(i).getfId() == Integer.valueOf(data)){
                        foodInfo.putInt("foodId", foods.get(i).getfId());
                        foodInfo.putString("foodName", foods.get(i).getfName());
                        foodInfo.putString("foodCat", foods.get(i).getfCategory());
                        foodInfo.putString("foodRec", foods.get(i).getfRecepiee());
                        foodInfo.putDouble("foodPrice", foods.get(i).getfPrice());
                        foodInfo.putString("foodImage", foods.get(i).getmImageUrl());
                        Log.i(TAG, "imgurl= " + foods.get(i).getmImageUrl());
                    }
                }
                FoodDetailFragment foodDetailFragment = new FoodDetailFragment();
                foodDetailFragment.setArguments(foodInfo);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        //.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
                        .replace(R.id.main_fragment_container, foodDetailFragment)
                        .addToBackStack(VegTabFragment.class.getName())
                        .commit();
            }
        });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void objRequestMethod() {
        //internet request to get info of food
        HomePageActivity.showPDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, buildUrl(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray foodsJsonArr = response.getJSONArray("Food");
                            for (int i = 0; i < foodsJsonArr.length(); i++) {
                                JSONObject object = foodsJsonArr.getJSONObject(i);
                                String id = object.getString("FoodId");
                                String name = object.getString("FoodName");
                                String recepiee = object.getString("FoodRecepiee");
                                String price = object.getString("FoodPrice");
                                String thumb = object.getString("FoodThumb");
                                final Food curFood = new Food();
                                curFood.setfCategory("Veg");
                                curFood.setfName(name);
                                curFood.setfRecepiee(recepiee);
                                curFood.setfPrice(Double.valueOf(price));
                                curFood.setmImageUrl(thumb);
                                curFood.setfId(Integer.valueOf(id));
                                foods.add(curFood);
                                adapter.notifyData(foods);
                                //ImageUtils.loadFoodImage(curFood, );
                            }
                            HomePageActivity.disPDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "ERROR" + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                HomePageActivity.disPDialog();
            }
        });
        Log.i("URL", jsonObjectRequest.getUrl());
        VolleyController.getmInstance().addToRequestQueue(jsonObjectRequest);
    }

    //get the whole url by adding the city, so we can get the list of food
    private String buildUrl() {
        return VEG_URL + HomePageActivity.City;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
