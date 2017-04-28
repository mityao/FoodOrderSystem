/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/24/17 10:11 AM
 */

package com.willyao.android.foodordersystem.model;

import android.graphics.Bitmap;

/**
 * Created by mitya on 4/16/2017.
 */

public class Food {

    private int fId;
    private String fName;
    private String fRecepiee;
    private double fPrice;
    private String fCategory;
    private String fCity;
    private String fImageUrl;

    public Bitmap getmImage() {
        return mImage;
    }

    public void setmImage(Bitmap mImage) {
        this.mImage = mImage;
    }

    private Bitmap mImage;

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getfRecepiee() {
        return fRecepiee;
    }

    public void setfRecepiee(String fRecepiee) {
        this.fRecepiee = fRecepiee;
    }

    public double getfPrice() {
        return fPrice;
    }

    public void setfPrice(double fPrice) {
        this.fPrice = fPrice;
    }

    public String getfCategory() {
        return fCategory;
    }

    public void setfCategory(String ctegory) {
        fCategory = ctegory;
    }

    public String getmCity() {
        return fCity;
    }

    public void setmCity(String fCity) {
        this.fCity = fCity;
    }

    public String getmImageUrl() {
        return fImageUrl;
    }

    public void setmImageUrl(String fImageUrl) {
        this.fImageUrl = fImageUrl;
    }
}
