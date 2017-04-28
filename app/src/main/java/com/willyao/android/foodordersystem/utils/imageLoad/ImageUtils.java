/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/20/17 12:29 AM
 */

package com.willyao.android.foodordersystem.utils.imageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.drawee.view.SimpleDraweeView;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.model.Food;

/**
 * Created by mitya on 4/17/2017.
 */

public class ImageUtils {

    public static void loadUserPicture(@NonNull final Context context,
                                       @NonNull ImageView imageView,
                                       @NonNull String url) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(ContextCompat.getDrawable(context, R.drawable.user_picture_placeholder))
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        view.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    /*----------------------fresco----------------------------*/
    public static void loadFoodImage(@NonNull Food food, @NonNull SimpleDraweeView imageView) {
        String imageUrl = food.getmImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            Uri imageUri = Uri.parse(imageUrl);
//            if (food.animated) {
//                DraweeController controller = Fresco.newDraweeControllerBuilder()
//                        .setUri(imageUri)
//                        .setAutoPlayAnimations(true)
//                        .build();
//                imageView.setController(controller);
//            } else {
                imageView.setImageURI(imageUri);
//            }
        }
    }

    public static void loadFoodImageImageView(@NonNull Food food, @NonNull ImageView imageView) {
        String imageUrl = food.getmImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            Uri imageUri = Uri.parse(imageUrl);
//            if (food.animated) {
//                DraweeController controller = Fresco.newDraweeControllerBuilder()
//                        .setUri(imageUri)
//                        .setAutoPlayAnimations(true)
//                        .build();
//                imageView.setController(controller);
//            } else {
            imageView.setImageURI(imageUri);
//            }
        }
    }
}
