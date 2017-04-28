/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 3:03 AM
 */

package com.willyao.android.foodordersystem.homePage.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.homePage.HomePageActivity;
import com.willyao.android.foodordersystem.homePage.fragments.TrackFragment;
import com.willyao.android.foodordersystem.model.Order;

import java.util.ArrayList;

/**
 * Created by mitya on 4/20/2017.
 */

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryViewHolder>{

    private Context mContext;
    private ArrayList<Order> orders;
    View orderHisAdaView;
    public OrderHistoryAdapter(Context context, ArrayList<Order> orders) {
        mContext = context;
        this.orders = orders;
    }

    @Override
    public OrderHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        orderHisAdaView = LayoutInflater.from(mContext).inflate(R.layout.order_history_list_view, parent, false);
        OrderHistoryViewHolder orderHistoryViewHolder = new OrderHistoryViewHolder(orderHisAdaView);
        return orderHistoryViewHolder;
    }

    @Override
    public void onBindViewHolder(OrderHistoryViewHolder holder, final int position) {
        holder.mTextId.setText("" + orders.get(position).getmId());
        holder.mTextName.setText(orders.get(position).getmName());
        holder.mTextQuantity.setText("" + orders.get(position).getmQuantity());
        holder.mTextTotal.setText("" + orders.get(position).getmTotal());
        holder.mTextDate.setText("" + orders.get(position).getmDate());
        holder.mTextAddress.setText(orders.get(position).getmAddress());

        holder.btn_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //transfer to track fragment
                Bundle bundle = new Bundle();
                bundle.putInt("OrderId", orders.get(position).getmId());
                TrackFragment trackFrag = new TrackFragment();
                trackFrag.setArguments(bundle);
                ((HomePageActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, trackFrag)
                        .addToBackStack(HomePageActivity.class.getName())
                        .commit();
            }
        });

        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Cancel order",Toast.LENGTH_SHORT).show();
                Snackbar.make(orderHisAdaView, "Please call us to cancel order", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void notifyData(ArrayList<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
class OrderHistoryViewHolder extends RecyclerView.ViewHolder{
    TextView mTextId, mTextName, mTextQuantity, mTextTotal, mTextDate, mTextAddress, mTextStatus;
    TextView btn_track, btn_cancel;
    public OrderHistoryViewHolder(View itemView) {
        super(itemView);
        mTextId = (TextView) itemView.findViewById(R.id.history_id);
        mTextName = (TextView) itemView.findViewById(R.id.history_name);
        mTextQuantity = (TextView) itemView.findViewById(R.id.history_quantity);
        mTextTotal = (TextView) itemView.findViewById(R.id.history_total);
        mTextDate = (TextView) itemView.findViewById(R.id.history_date);
        mTextAddress = (TextView) itemView.findViewById(R.id.history_address);

        btn_track = (TextView) itemView.findViewById(R.id.history_track);
        btn_cancel = (TextView) itemView.findViewById(R.id.history_cancel);
    }
}
