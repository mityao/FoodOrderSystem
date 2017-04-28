/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 2:57 AM
 */

package com.willyao.android.foodordersystem.homePage.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.homePage.HomePageActivity;
import com.willyao.android.foodordersystem.utils.imageLoad.PermissionUtils;
import com.willyao.android.foodordersystem.utils.sharedPreferences.MySPhelper;
import com.willyao.android.foodordersystem.utils.volley.VolleyController;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mitya on 4/23/2017.
 */

public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_img) ImageView mImg;
    @BindView(R.id.profile_mobile) TextView mTextMobile;
    @BindView(R.id.profile_update_mobile) ImageView mTextUpdateMobile;
    @BindView(R.id.profile_address) TextView mTextAddress;
    @BindView(R.id.profile_update_address) ImageView mTextUpdateAddress;
    @BindView(R.id.profile_update_pwd) ImageView mTextUpdatePwd;
    @BindView(R.id.profile_oldpwd) EditText mEditOldPwd;
    @BindView(R.id.profile_newpwd) EditText mEditNewPwd;
    @BindView(R.id.profile_newpwd2) EditText mEditNewPwd2;
    @BindView(R.id.profile_pwd_linear) CardView mLinearPwd;
    @BindView(R.id.profile_confirm_button) Button mButtonConfirm;
    @BindView(R.id.profile_cancel_button) Button mButtonCancel;
    Unbinder unbinder;
    View proView;
    private final String ADDR_BASE_URL = "http://rjtmobile.com/ansari/fos/fosapp/fos_update_address.php?user_phone=";
    private final String PWD_BASE_URL = "http://rjtmobile.com/ansari/fos/fosapp/fos_reset_pass.php?user_phone=";
    private static final int REQ_CODE_PICK_IMAGE = 100;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                mImg.setScaleType(ImageView.ScaleType.CENTER_CROP);

                mImg.setTag(imageUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    mImg.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtils.REQ_CODE_WRITE_EXTERNAL_STORAGE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, "Select picture"),
                    REQ_CODE_PICK_IMAGE);
        }
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        proView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, proView);

        /*pick user img*/
        proView.findViewById(R.id.profile_edit_img_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtils.checkPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    PermissionUtils.requestReadExternalStoragePermission(getActivity());
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent, "Select picture"),
                            REQ_CODE_PICK_IMAGE);
                }
            }
        });

        mTextMobile.setText(MySPhelper.getInstance(getContext()).getMobile());
        mTextAddress.setText(MySPhelper.getInstance(getContext()).getAddress());
        mTextUpdateMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View layout = layoutInflater.inflate(R.layout.dialog_set_mobile, (ViewGroup) view.findViewById(R.id.dialog_mobile));
                new AlertDialog.Builder(getActivity()).setTitle("New Phone Number Please")
                        .setIcon(R.drawable.dialog)
                        .setView(layout)
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Dialog dialog = (Dialog) dialogInterface;
                                EditText inputMobile = (EditText) dialog.findViewById(R.id.dialog_et_mobile);
                                if (inputMobile.getText().toString().isEmpty()){
                                    return;
                                }
                                try {
                                    long number = Long.valueOf(inputMobile.getText().toString());
                                    MySPhelper.getInstance(getActivity()).setMobile(inputMobile.getText().toString());
                                    mTextMobile.setText(inputMobile.getText().toString());
                                } catch (Exception e){
                                    //Toast.makeText(getActivity(), "Please make sure your phone format is correct", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(proView, "Please make sure your phone format is correct", Snackbar.LENGTH_LONG).show();
                            }
                        }
                }).setNegativeButton("Later", null).show();
            }
        });

        mTextUpdateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView mPhone = (TextView) mLinearPwd.findViewById(R.id.profile_tv_pwd);
                mEditOldPwd.setHint("Phone Number");
                mPhone.setText("Mobile:");
                TextView mPwd = (TextView) mLinearPwd.findViewById(R.id.profile_tv_newpwd);
                mPwd.setText("Password:");
                mEditNewPwd.setHint("Password");
                TextView mAddr = (TextView) mLinearPwd.findViewById(R.id.profile_tv_repwd);
                mAddr.setText("New Address");
                mEditNewPwd2.setHint("Address");
                mLinearPwd.setVisibility(View.VISIBLE);
                mButtonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String phone = mEditOldPwd.getText().toString();
                        String password = mEditNewPwd.getText().toString();
                        String address = mEditNewPwd2.getText().toString();
                        postUpdateAddress(phone, password, address);
                    }
                });
            }
        });
        mLinearPwd.setVisibility(View.INVISIBLE);
        mTextUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView mPhone = (TextView) mLinearPwd.findViewById(R.id.profile_tv_pwd);
                mPhone.setText("Old Password:");
                mEditOldPwd.setHint("Old Password");
                TextView mPwd = (TextView) mLinearPwd.findViewById(R.id.profile_tv_newpwd);
                mPwd.setText("New Password:");
                mEditNewPwd.setHint("New Password");
                TextView mAddr = (TextView) mLinearPwd.findViewById(R.id.profile_tv_repwd);
                mAddr.setText("Retype:");
                mEditNewPwd2.setHint("Retype Password");
                mLinearPwd.setVisibility(View.VISIBLE);
                mButtonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String oldPwd = mEditOldPwd.getText().toString();
                        String newPwd = mEditNewPwd.getText().toString();
                        String newPwd2 = mEditNewPwd2.getText().toString();
                /*------use checkOldPwd() to check old pwd--------*/
                        if (!checkMatch(newPwd, newPwd2)) {
                            //Toast.makeText(getContext(), "New password does not match", Toast.LENGTH_LONG).show();
                            Snackbar.make(proView, "New password does not match", Snackbar.LENGTH_LONG).show();
                            mEditNewPwd.setText("");
                            mEditOldPwd.setText("");
                            mEditNewPwd2.setText("");
                            return;
                        }
                        if (newPwd.length() < 6 || newPwd2.length() < 6){
                            mEditNewPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean b) {
                                    if (mEditNewPwd.getText().toString().length() < 6){
                                        mEditNewPwd.setError("Invalid password");
                                    }
                                }
                            });
                        }
                        postUpdatePwd(oldPwd, newPwd);
                    }
                });
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLinearPwd.setVisibility(View.INVISIBLE);
                mEditNewPwd.setText("");
                mEditOldPwd.setText("");
                mEditNewPwd2.setText("");
            }
        });
        return proView;

    }

    private void postUpdateAddress(String phone, String password, final String address) {
        final String TAG = "UPDATE_ADDRESS";
        HomePageActivity.showPDialog();
        StringRequest strRequest = new StringRequest(Request.Method.GET, buildUrlAddr(phone, password, address), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                if (response.contains("Your Delivery Address updated")) {
                    Toast.makeText(getActivity(), "SUCCESSFULLY UPDATE ADDRESS", Toast.LENGTH_SHORT).show();
                    MySPhelper.getInstance(getContext()).setAddress(address);
                }
                HomePageActivity.disPDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "ERROR" + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                HomePageActivity.disPDialog();
            }
        });
        VolleyController.getmInstance().addToRequestQueue(strRequest);
    }

    private String buildUrlAddr(String phone, String password, String address) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < address.length(); i++){
            if (address.charAt(i) == ' '){
                sb.append("%20");
            } else {
                sb.append(address.charAt(i));
            }
        }
        return ADDR_BASE_URL + phone + "&user_password=" + password + "&user_add=" + sb.toString();
    }

    private void postUpdatePwd(String oldPwd, final String newPwd){
        final String TAG = "UPDATE_PWD";
        HomePageActivity.showPDialog();
        StringRequest strRequest = new StringRequest(Request.Method.GET, buildUrlPwd(oldPwd, newPwd), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                if (response.contains("password reset successfully")){
                    Toast.makeText(getActivity(), "SUCCESSFULLY UPDATE PASSWORD", Toast.LENGTH_SHORT).show();
                    MySPhelper.getInstance(getContext()).setPwd(newPwd);
                }
                HomePageActivity.disPDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "ERROR" + volleyError.getMessage());
                Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                HomePageActivity.disPDialog();
            }
        });
        VolleyController.getmInstance().addToRequestQueue(strRequest);
    }

    private String buildUrlPwd(String oldPwd, String newPwd) {
        return PWD_BASE_URL + MySPhelper.getInstance(getContext()).getMobile()
                + "&user_password=" + oldPwd +
                "&newpassword=" + newPwd;
    }

    private boolean checkMatch(String newPwd, String newPwd2) {
        return newPwd.equals(newPwd2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
