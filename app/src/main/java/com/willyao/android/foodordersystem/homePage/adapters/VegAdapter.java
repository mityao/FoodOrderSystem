/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/20/17 1:25 AM
 */

package com.willyao.android.foodordersystem.homePage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.model.Food;
import com.willyao.android.foodordersystem.utils.imageLoad.ImageUtils;

import java.util.ArrayList;

/**
 * Created by mitya on 4/16/2017.
 */

public class VegAdapter extends RecyclerView.Adapter<VegHolder> implements View.OnClickListener{

    private Context mContext;
    private ArrayList<Food> foods;
    private final String TAG = "VegAdapter";
    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListenser(OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener = listener;
    }
    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view,String.valueOf(view.getTag()));
        }
        else{
            Log.e("CLICK", "ERROR");
        }
    }

    public VegAdapter(Context context, ArrayList<Food> foods){
        mContext = context;
        this.foods = foods;
    }

    @Override
    public VegHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.food_list_view, parent, false);
        VegHolder vegHolder = new VegHolder(view);
        view.setOnClickListener(this);
        return vegHolder;
    }

    @Override
    public void onBindViewHolder(VegHolder holder, int position) {
        holder.mTextCategory.setVisibility(View.INVISIBLE);
        holder.mTextCateTitle.setVisibility(View.INVISIBLE);

        //holder.mTextId.setText(String.valueOf(foods.get(position).getId()));
        final Food food = foods.get(position);
        holder.mTextName.setText(food.getfName());
        holder.mTextPrice.setText(String.valueOf(food.getfPrice()));
        //fresco to load image
        ImageUtils.loadFoodImage(food, holder.mImageView);
        Log.i(TAG, "food url= " + food.getmImageUrl());
        holder.itemView.setTag(food.getfId());
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public void notifyData(ArrayList<Food> foods) {
        this.foods = foods;
        notifyDataSetChanged();
    }
}
class VegHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView mImageView;
    TextView mTextId, mTextName, mTextCategory, mTextPrice, mTextCateTitle;

    public VegHolder(View itemView) {
        super(itemView);
        mImageView = (SimpleDraweeView) itemView.findViewById(R.id.food_img);
        mTextName = (TextView) itemView.findViewById(R.id.food_name);
        mTextPrice = (TextView) itemView.findViewById(R.id.food_price);
        mTextCategory = (TextView) itemView.findViewById(R.id.food_category);
        mTextCateTitle = (TextView) itemView.findViewById(R.id.food_category_title);
    }
}
