/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/21/17 12:01 AM
 */

package com.willyao.android.foodordersystem.utils.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.willyao.android.foodordersystem.model.Food;
import com.willyao.android.foodordersystem.utils.cartItem.ShoppingCartItem;

import java.util.ArrayList;

import static com.willyao.android.foodordersystem.utils.dataBase.DBHelper.CATEGORY;
import static com.willyao.android.foodordersystem.utils.dataBase.DBHelper.IMAGEURL;
import static com.willyao.android.foodordersystem.utils.dataBase.DBHelper.ITEMID;
import static com.willyao.android.foodordersystem.utils.dataBase.DBHelper.ITEMNAME;
import static com.willyao.android.foodordersystem.utils.dataBase.DBHelper.PRICE;
import static com.willyao.android.foodordersystem.utils.dataBase.DBHelper.QUANTITY;
import static com.willyao.android.foodordersystem.utils.dataBase.DBHelper.TABLENAME;

/**
 * Created by mitya on 4/20/2017.
 */

public class DBManipulation {
    private DBHelper mDBHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private Context mContext;
    private static String mDBName;
    private static DBManipulation mInstance;

    public DBManipulation(Context context) {
        mDBName = DBHelper.DATABASE_NAME;
        this.mContext = context;
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDb() {
        return mSQLiteDatabase;
    }

    public static DBManipulation getmInstance(Context context){
        if (mInstance == null) {
            mInstance = new DBManipulation(context);
        }
        return mInstance;
    }

    public void addAll(){
        ArrayList<Integer> foodList = ShoppingCartItem.getInstance(mContext).getFoodInCart();
        for (Integer i : foodList){
            Food curFood = ShoppingCartItem.getInstance(mContext).getFoodById(i);
            String insertCurrentFood = "INSERT INTO " + TABLENAME + "("
                    + ITEMID + ","
                    + ITEMNAME + ","
                    + QUANTITY + ","
                    + PRICE + ","
                    + CATEGORY + ","
                    + IMAGEURL
                    + ")"
                    + "VALUES ("
                    + curFood.getfId() + ","
                    + "'" + curFood.getfName() + "'" + ","
                    + ShoppingCartItem.getInstance(mContext).getFoodNumber(curFood) + ","
                    + curFood.getfPrice() + ","
                    + "'" + curFood.getfCategory() + "'" + ","
                    + "'" + curFood.getmImageUrl() + "'"
                    + ")";
            Log.e("DB", "insert value query: " + insertCurrentFood);
            mSQLiteDatabase.execSQL(insertCurrentFood);
        }
    }

    public void deleteAll(){
        mSQLiteDatabase.execSQL("delete from "+ TABLENAME);
        Log.e("DB", "Delete all: " + "delete from "+ TABLENAME);
    }
}
