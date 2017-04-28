/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/24/17 9:43 AM
 */

package com.willyao.android.foodordersystem.cartPage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.cartPage.fragments.CartFragment;
import com.willyao.android.foodordersystem.homePage.HomePageActivity;
import com.willyao.android.foodordersystem.model.Food;
import com.willyao.android.foodordersystem.utils.cartItem.ShoppingCartItem;

/**
 * Created by mitya on 4/21/2017.
 */

public class CartAdapter extends RecyclerView.Adapter<CartHolder> implements View.OnClickListener {
    private Context mContext;
    private final String TAG = "Cart Adapter";
    public CartAdapter(Context context) {
        mContext = context;
    }

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

    @Override
    public CartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_list_view, parent, false);
        CartHolder cartHolder = new CartHolder(view);
        return cartHolder;
    }

    @Override
    public void onBindViewHolder(final CartHolder holder, int position) {
        int id = ShoppingCartItem.getInstance(mContext).getFoodInCart().get(position);
        final Food curFood = ShoppingCartItem.getInstance(mContext).getFoodById(id);
        final int curFoodNumber = ShoppingCartItem.getInstance(mContext).getFoodNumber(curFood);

        holder.mTextName.setText(curFood.getfName());
        holder.mTextCategory.setText(curFood.getfCategory());
        holder.mEditQuantity.setText(String.valueOf(curFoodNumber));
        holder.mTextPrice.setText(String.valueOf(curFoodNumber * curFood.getfPrice()));
        //ImageUtils.loadFoodImageImageView(curFood, holder.mImage);
        Picasso.with(mContext)
                .load(curFood.getmImageUrl())
                .placeholder(R.drawable.food_img_placeholder)
                .into(holder.mImage);
        //holder.mImage.setImageBitmap(curFood.getmImageUrl());
        Log.i(TAG, "img url= " + curFood.getmImageUrl());
        Log.i(TAG, "food name= " + curFood.getfName());
        Log.i(TAG, "food category= " + curFood.getfCategory());
        Log.i(TAG, "food quantity= " + String.valueOf(curFoodNumber));


        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curFoodNumber == 0){
                    return;
                }
                int currentNumber = curFoodNumber - 1;
                ShoppingCartItem.getInstance(mContext).setFoodNumber(curFood, currentNumber);
                //update total number and prize in cart
                HomePageActivity.cartNumber.setText(String.valueOf(ShoppingCartItem.getInstance(mContext).getSize()));
                CartFragment.cart_total.setText(String.valueOf(ShoppingCartItem.getInstance(mContext).getPrice()));
                notifyDataSetChanged();
            }
        });

        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curFoodNumber == 99){
                    return;
                }
                int currentNumber = curFoodNumber + 1;
                ShoppingCartItem.getInstance(mContext).setFoodNumber(curFood, currentNumber);
                //update total number and prize in cart
                HomePageActivity.cartNumber.setText(String.valueOf(ShoppingCartItem.getInstance(mContext).getSize()));
                CartFragment.cart_total.setText(String.valueOf(ShoppingCartItem.getInstance(mContext).getPrice()));
                notifyDataSetChanged();
            }
        });
    }

    public void deleteData(int position){
        int id = ShoppingCartItem.getInstance(mContext).getFoodInCart().get(position);
        Food curFood = ShoppingCartItem.getInstance(mContext).getFoodById(id);
        ShoppingCartItem.getInstance(mContext).setFoodNumber(curFood, 0);

        HomePageActivity.cartNumber.setText(String.valueOf(ShoppingCartItem.getInstance(mContext).getSize()));
        CartFragment.cart_total.setText(String.valueOf(ShoppingCartItem.getInstance(mContext).getPrice()));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ShoppingCartItem.getInstance(mContext).getFoodTypeSize();
    }
}

class CartHolder extends RecyclerView.ViewHolder{
    TextView mTextName, mTextCategory, mTextPrice;
    ImageView mImage;
    TextView mEditQuantity;
    Button btn_minus;
    Button btn_plus;
    public CartHolder(View itemView) {
        super(itemView);
        mTextName = (TextView) itemView.findViewById(R.id.cart_name);
        mTextCategory = (TextView) itemView.findViewById(R.id.cart_category);
        mTextPrice = (TextView) itemView.findViewById(R.id.cart_price);
        mEditQuantity = (TextView) itemView.findViewById(R.id.cart_quantity);
        mImage = (ImageView) itemView.findViewById(R.id.cart_image);


        btn_minus = (Button) itemView.findViewById(R.id.cart_btn_minus);
        btn_plus = (Button) itemView.findViewById(R.id.cart_btn_plus);
    }
}
