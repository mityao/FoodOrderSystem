/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/21/17 11:12 AM
 */

package com.willyao.android.foodordersystem.utils.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mitya on 4/20/2017.
 */

public class DBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "myDb";
    public static final String TABLENAME = "cart";
    public static final String ITEMID = "id";
    public static final String ITEMNAME = "name";
    public static final String QUANTITY = "quantity";
    public static final String PRICE = "price";
    public static final String CATEGORY = "category";
    public static final String IMAGEURL = "image";
    public static final int VERSION = 1;

    private Context mContext = null;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e("sqlite", "create table");
        String createTable = " CREATE TABLE " + TABLENAME + "("
                + ITEMID + " INTEGER PRIMARY KEY, " + ITEMNAME + " TEXT, "
                + QUANTITY + " INTEGER, " + PRICE + " DECIMAL(10,2), "
                + CATEGORY + " TEXT, "+ IMAGEURL + " TEXT)";
        Log.e("DB", "create table query: " + createTable);
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        onCreate(sqLiteDatabase);
    }
}
