/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 10:00 AM
 */

package com.willyao.android.foodordersystem.homePage.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.cartPage.CartActivity;
import com.willyao.android.foodordersystem.model.Food;
import com.willyao.android.foodordersystem.utils.cartItem.ShoppingCartItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mitya on 4/19/2017.
 */

public class FoodDetailFragment extends Fragment {

    @BindView(R.id.food_detail_image) ImageView mImageView;
    @BindView(R.id.food_detail_collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.food_detail_category) TextView mTextCategory;
    @BindView(R.id.food_detail_recipe) TextView mTextRecipe;
    @BindView(R.id.food_detail_price) TextView mTextPrice;
    @BindView(R.id.food_detail_add) Button mButtonAdd;
    @BindView(R.id.food_detail_id) TextView mTextId;
    Food food;
    Unbinder unbinder;
    private final String TAG = "Food Detail Frgment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        collapsingToolbarLayout.setTitle("Food Name");
        setupFood();
        addToCart();
        return view;
    }

    private void setupFood() {
        food = new Food();
        food.setfName(getArguments().getString("foodName"));
        food.setfId(getArguments().getInt("foodId"));
        food.setfPrice(getArguments().getDouble("foodPrice"));
        food.setfRecepiee(getArguments().getString("foodRec"));
        food.setfCategory(getArguments().getString("foodCat"));
        food.setmImageUrl(getArguments().getString("foodImage"));
        mTextId.setText(String.valueOf(food.getfId()));
        mTextCategory.setText(food.getfCategory());
        mTextRecipe.setText(food.getfRecepiee());
        mTextPrice.setText(String.valueOf(food.getfPrice()));
        collapsingToolbarLayout.setTitle(food.getfName());
        //why fresco here is not working?
        //ImageUtils.loadFoodImage(food, mImageView);
        Picasso.with(getContext())
                .load(food.getmImageUrl())
                .placeholder(R.drawable.food_img_placeholder)
                .into(mImageView);

    }

    private void addToCart() {
        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add food to cart
                ShoppingCartItem.getInstance(getContext()).addToCart(food);
                Log.i(TAG,"food imgurl="+ food.getmImageUrl());
                Log.i(TAG,"food name="+ food.getfName());
                Log.i(TAG,"food price="+ food.getfPrice());

                TextView cartNumber = (TextView)getActivity().findViewById(R.id.cart_item_number);
                cartNumber.setText(String.valueOf(ShoppingCartItem.getInstance(getContext()).getSize()));

                new AlertDialog.Builder(getActivity()).setTitle("Successful!")
                        .setIcon(R.drawable.dialog)
                        .setMessage("Order 1 " + food.getfName() + " Successfully!")
                        .setPositiveButton("Check Order", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent cartAct = new Intent(getActivity(), CartActivity.class);
                                startActivity(cartAct);
                            }
                        })
                        .setNegativeButton("Keep Ordering", null).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
