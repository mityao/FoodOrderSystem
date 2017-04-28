/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/23/17 7:19 PM
 */

package com.willyao.android.foodordersystem.cartPage.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.willyao.android.foodordersystem.R;

/**
 * Created by mitya on 4/23/2017.
 */

public class AddressFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        return view;
    }
}
