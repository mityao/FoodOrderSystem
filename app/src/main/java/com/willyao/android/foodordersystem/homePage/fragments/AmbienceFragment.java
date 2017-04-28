/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 2:46 PM
 */

package com.willyao.android.foodordersystem.homePage.fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.willyao.android.foodordersystem.homePage.adapters.AmbienceAdapter;
import com.willyao.android.foodordersystem.model.Ambience;
import com.willyao.android.foodordersystem.utils.volley.VolleyController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mitya on 4/25/2017.
 */

public class AmbienceFragment extends Fragment {

    @BindView(R.id.ambience_recyclerview) RecyclerView mRecyclerView;
    Unbinder unbinder;
    ArrayList<Ambience> ambiences = new ArrayList<>();
    AmbienceAdapter adapter;
    private String TAG = "AmbienceFragment";
    private final String PIC_URL = "http://rjtmobile.com/ansari/fos/fosapp/city_pics.php?&city=bangalore";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (ambiences.size() == 0){
            objRequestMethod();
        }
        View view = inflater.inflate(R.layout.fragment_ambience, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            adapter = new AmbienceAdapter(getContext(), ambiences);

            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL ));
        }
        return view;
    }

    private void objRequestMethod() {
        HomePageActivity.showPDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, PIC_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray ambienceJsonArr = response.getJSONArray("Pics");
                    for (int i = 0; i < ambienceJsonArr.length(); i++) {
                        JSONObject ab = ambienceJsonArr.getJSONObject(i);
                        String id = ab.getString("PicId");
                        String title = ab.getString("PicTitle");
                        String picUrl = ab.getString("PicUrl");
                        Ambience amb = new Ambience();
                        amb.setmId(id);
                        amb.setmTitle(title);
                        amb.setmPicUrl(picUrl);
                        ambiences.add(amb);
                        adapter.notifyData(ambiences);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HomePageActivity.disPDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "ERROR" + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                HomePageActivity.disPDialog();
            }
        });
        Log.i("URL", jsonObjReq.getUrl());
        VolleyController.getmInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
