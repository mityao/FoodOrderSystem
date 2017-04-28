/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 1:11 AM
 */

package com.willyao.android.foodordersystem.cartPage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.model.Food;
import com.willyao.android.foodordersystem.utils.cartItem.ShoppingCartItem;

/**
 * Created by mitya on 4/22/2017.
 */

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutHolder> {

    private Context mContext;

    public CheckoutAdapter(Context context) {
        mContext = context;
    }
    @Override
    public CheckoutHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.checkout_list_view, parent, false);
        CheckoutHolder checkoutHolder = new CheckoutHolder(view);
        return checkoutHolder;
    }

    @Override
    public void onBindViewHolder(CheckoutHolder holder, int position) {

        if (position == getItemCount() - 2){
            holder.mTextName.setText("");
            holder.mTextPrice.setText("$ 10.00");
            holder.mTextQuantity.setText("Shipping");
            return;
        }
        else if (position == getItemCount() - 1){
            holder.mTextName.setText("");
            holder.mTextPrice.setText("$" + (ShoppingCartItem.getInstance(mContext).getPrice() * 0.06));
            holder.mTextQuantity.setText("Tax");
            return;
        }

        int id = ShoppingCartItem.getInstance(mContext).getFoodInCart().get(position);
        final Food curFood = ShoppingCartItem.getInstance(mContext).getFoodById(id);
        final int curFoodNumber = ShoppingCartItem.getInstance(mContext).getFoodNumber(curFood);
        holder.mTextName.setText(curFood.getfName());
        holder.mTextPrice.setText("$ "+ String.valueOf(curFoodNumber * curFood.getfPrice()));
        holder.mTextQuantity.setText(String.valueOf(curFoodNumber));
    }

    @Override
    public int getItemCount() {
        return ShoppingCartItem.getInstance(mContext).getFoodTypeSize() + 2;
    }
}

class CheckoutHolder extends RecyclerView.ViewHolder{
    TextView mTextName, mTextQuantity, mTextPrice;

    public CheckoutHolder(View itemView) {
        super(itemView);
        mTextName = (TextView) itemView.findViewById(R.id.checkout_name);
        mTextPrice = (TextView) itemView.findViewById(R.id.checkout_price);
        mTextQuantity = (TextView) itemView.findViewById(R.id.checkout_quantity);
    }
}
