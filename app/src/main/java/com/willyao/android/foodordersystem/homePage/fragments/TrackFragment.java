/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/23/17 5:48 PM
 */

package com.willyao.android.foodordersystem.homePage.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.homePage.HomePageActivity;
import com.willyao.android.foodordersystem.model.Order;
import com.willyao.android.foodordersystem.utils.volley.VolleyController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mitya on 4/23/2017.
 */

public class TrackFragment extends Fragment {

    @BindView(R.id.track_edit_search) EditText mEditOrderSearch;
    @BindView(R.id.track_search) ImageView mTextSearch;
    @BindView(R.id.track_id) TextView mTextID;
    @BindView(R.id.track_total) TextView mTextTotal;
    @BindView(R.id.track_date) TextView mTextDate;
    @BindView(R.id.track_image_status) ImageView mImageStatus;
    @BindView(R.id.track_status) TextView mTextStatus;
    @BindView(R.id.track_detail_blcok) LinearLayout mLinearLayout;
    Unbinder unbinder;
    private View trackView;

    private final String TRACK_BASE_URL = "http://rjtmobile.com/ansari/fos/fosapp/order_track.php?&order_id=";
    private int orderId;
    private Order order;
    private final int[] imageResources = {R.mipmap.packing, R.mipmap.delivery, R.mipmap.eating, R.mipmap.alert};

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Track");
    }

    public TrackFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        trackView= inflater.inflate(R.layout.fragment_track, container, false);
        unbinder = ButterKnife.bind(this, trackView);
        order = new Order();
        mTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    orderId = Integer.valueOf(mEditOrderSearch.getText().toString().trim());
                    mLinearLayout.setVisibility(View.VISIBLE);
                    getData(trackView);
                /*--------insert code to get data---*/
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("ERROR", e.toString());
                    Toast.makeText(getActivity(), "Please Check Order ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Bundle orderBundle = this.getArguments();
        if (orderBundle != null) {
            orderId = orderBundle.getInt("OrderId");
            getData(trackView);
            mLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mLinearLayout.setVisibility(View.INVISIBLE);
        }

        return trackView;
    }

    private void getData(final View trackView) {
        final String TAG = "TRACK_FRAGMENT";
        HomePageActivity.showPDialog();
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, buildUrl(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray orderArray = response.getJSONArray("Order Detail");
                    for (int i = 0; i < orderArray.length(); i++) {
                        JSONObject o = orderArray.getJSONObject(i);
                        int id = o.getInt("OrderId");
                        double totalOrder = o.getDouble("TotalOrder");
                        String status = o.getString("OrderStatus");
                        String date = o.getString("OrderDate");

                        order.setmId(id);
                        order.setmTotal(totalOrder);
                        order.setmStatus(status);
                        order.setmDate(date);
                    }
                    displayData();
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
        Log.e("URL", jsonObjReq.getUrl());
        VolleyController.getmInstance().addToRequestQueue(jsonObjReq);
    }

    private String buildUrl() {
        return TRACK_BASE_URL + orderId;
    }

    private void displayData() {
        mTextID.setText("" + order.getmId());
        mTextDate.setText(order.getmDate());
        mTextTotal.setText("" + order.getmTotal());
        mTextStatus.setText(parseWord(order.getmStatus()));
        mImageStatus.setImageResource(parseImage(order.getmStatus()));
    }

    private String parseWord(String s) {
        if (s.equals("1")) {
            return "Packing";
        } else if (s.equals("2")) {
            return "On the way";
        } else if (s.equals("3")) {
            return "Delivered";
        } else {
            return "Error";
        }
    }

    private int parseImage(String s) {
        if (s.equals("1")) {
            return imageResources[0];
        } else if (s.equals("2")) {
            return imageResources[1];
        } else if (s.equals("3")) {
            return imageResources[2];
        } else {
            return imageResources[3];
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
