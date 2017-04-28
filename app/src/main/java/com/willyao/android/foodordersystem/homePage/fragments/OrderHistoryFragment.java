/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 3:03 AM
 */

package com.willyao.android.foodordersystem.homePage.fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.homePage.HomePageActivity;
import com.willyao.android.foodordersystem.homePage.adapters.OrderHistoryAdapter;
import com.willyao.android.foodordersystem.model.Order;
import com.willyao.android.foodordersystem.utils.sharedPreferences.MySPhelper;
import com.willyao.android.foodordersystem.utils.volley.VolleyController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mitya on 4/20/2017.
 */

public class OrderHistoryFragment extends Fragment {

    @BindView(R.id.order_history_recyclerview) RecyclerView mRecyclerView;
    Unbinder unbinder;

    ArrayList<Order> orders = new ArrayList<>();
    OrderHistoryAdapter adapter;
    private final String ORDER_HISTORY_URL  = "http://rjtmobile.com/ansari/fos/fosapp/order_recent.php?&user_phone=";
    private final String TAG = "ORDER_HISTORY";
    View orderHisView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        orderHisView = inflater.inflate(R.layout.fragment_order_history, container, false);
        unbinder = ButterKnife.bind(this, orderHisView);
        if (orders.size() == 0) {
            objRequestMethod();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            adapter = new OrderHistoryAdapter(getContext(), orders);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        return orderHisView;
    }

    private void objRequestMethod() {
        HomePageActivity.showPDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, buildUrl(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());
                try {
                    JSONArray orderJsonArray = response.getJSONArray("Order Detail");
                    for (int i = 0; i < orderJsonArray.length(); i++) {
                        JSONObject order = orderJsonArray.getJSONObject(i);
                        int id = order.getInt("OrderId");
                        String name = order.getString("OrderName");
                        int quantity = order.getInt("OrderQuantity");
                        double totalOrder = order.getDouble("TotalOrder");
                        String orderDeliverAdd = order.getString("OrderDeliverAdd");
                        String orderDate = order.getString("OrderDate");
                        String orderStatus = order.getString("OrderStatus");
                        final Order curOrder = new Order();
                        curOrder.setmId(id);
                        curOrder.setmName(name);
                        curOrder.setmQuantity(quantity);
                        curOrder.setmTotal(totalOrder);
//                        curOrder.setAddress(orderDeliverAdd);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            curOrder.setmAddress(MySPhelper.getInstance(getContext()).getAddress());
                        }
                        curOrder.setmDate(orderDate);
                        curOrder.setmStatus(orderStatus);
                        orders.add(curOrder);
                        adapter.notifyData(orders);
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
                //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Snackbar.make(orderHisView, error.getMessage(), Snackbar.LENGTH_LONG).show();
                HomePageActivity.disPDialog();
            }
        });
        VolleyController.getmInstance().addToRequestQueue(jsonObjReq);
    }

    private String buildUrl() {
        return ORDER_HISTORY_URL + MySPhelper.getInstance(getActivity()).getMobile();
//        return ORDER_HISTORY_URL + "55565454";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
