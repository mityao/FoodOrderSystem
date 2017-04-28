/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/15/17 2:58 PM
 */

package com.willyao.android.foodordersystem.utils.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mitya on 4/14/2017.
 */

public class MySPhelper {

    public static final String MSP_USER = "USER_INFO";
    public static final String MSP_KEY_NAME = "NAME";
    public static final String MSP_KEY_MOBILE = "MOBILE";
    public static final String MSP_KEY_PWD = "PWD";
    public static final String MSP_KEY_EMAIL = "EMAIL";
    public static final String MSP_KEY_ADDRESS = "ADDRESS";
    Context mContext;
    SharedPreferences mSP;
    SharedPreferences.Editor mEditor;
    private static MySPhelper mInstance = null;

    //set constructor
    public MySPhelper(Context context) {
        this.mContext = context;
        mSP = context.getSharedPreferences(MSP_USER, Context.MODE_PRIVATE);
    }

    public static MySPhelper getInstance(Context context) {
        if (mInstance == null)  {
            mInstance = new MySPhelper(context);
        }
        return mInstance;
    }

    public boolean userLoggedIn(){
        return mSP.contains(MSP_KEY_MOBILE);
    }

    //save user name
    public void setName(String name){
        mEditor = mSP.edit();
        mEditor.putString(MSP_KEY_NAME, name);
        mEditor.commit();
    }

    //save user password
    public void setPwd(String pwd){
        mEditor = mSP.edit();
        mEditor.putString(MSP_KEY_PWD, pwd);
        mEditor.commit();
    }

    //save user email
    public void setEmail(String email) {
        mEditor = mSP.edit();
        mEditor.putString(MSP_KEY_EMAIL, email);
        mEditor.commit();
    }

    //save user address
    public void setAddress(String address) {
        mEditor = mSP.edit();
        mEditor.putString(MSP_KEY_ADDRESS, address);
        mEditor.commit();
    }

    //save user mobile
    public void setMobile(String text) {
        mEditor = mSP.edit();
        mEditor.putString(MSP_KEY_MOBILE, text);
        mEditor.commit();
    }

    public String getName(){
        return mSP.getString(MSP_KEY_NAME, "name not get");
    }

    public String getPwd(){
        return mSP.getString(MSP_KEY_PWD, "pwd not get");
    }

    public String getEmail(){
        return mSP.getString(MSP_KEY_EMAIL, "email not get");
    }

    public String getAddress(){
        return mSP.getString(MSP_KEY_ADDRESS, "address not get");
    }

    public String getMobile(){
        return mSP.getString(MSP_KEY_MOBILE, "mobile not get");
    }

    public void clearSP(){
        mEditor = mSP.edit();
        mEditor.clear();
        mEditor.commit();
    }

    public void removeValue(String key){
        mEditor = mSP.edit();
        mEditor.remove(key);
        mEditor.commit();
    }
}
