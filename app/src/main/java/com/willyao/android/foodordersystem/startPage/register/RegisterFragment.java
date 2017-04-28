/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 2:42 AM
 */

package com.willyao.android.foodordersystem.startPage.register;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.startPage.SignInActivity;
import com.willyao.android.foodordersystem.startPage.login.LoginFragment;
import com.willyao.android.foodordersystem.utils.volley.VolleyController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.willyao.android.foodordersystem.startPage.login.LoginFragment.MOBILE_MIN_LENGTH;
import static com.willyao.android.foodordersystem.startPage.login.LoginFragment.PASSWORD_MIN_LENGTH;
import static com.willyao.android.foodordersystem.startPage.login.LoginFragment.signinPwd;

/**
 * Created by mitya on 4/13/2017.
 */

public class RegisterFragment extends Fragment {
    private final String REG_URL = "http://rjtmobile.com/ansari/fos/fosapp/fos_reg.php";
    private final String TAG = "REGISTER";
    private final String REGSUCCESS = "successfully";

    @BindView(R.id.sign_up_username) EditText signUpUsername;
    @BindView(R.id.sign_up_mobile) EditText signUpMobile;
    @BindView(R.id.sign_up_address) EditText signUpAddress;
    @BindView(R.id.sign_up_email) EditText signUpEmail;
    @BindView(R.id.sign_up_pwd) EditText signUpPwd;
    @BindView(R.id.sign_up_pwd2) EditText signUpPwd2;
    @BindView(R.id.sign_up_btn) Button signUpBtn;
    @BindView(R.id.to_sign_in) TextView toSignIn;
    Unbinder unbinder;
    ViewPager viewPager;
    FragmentManager fm;
    View regView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        regView = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, regView);
        setupUI();
        signUpMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (signUpMobile.getText().length() != MOBILE_MIN_LENGTH){
                    signUpMobile.setError("Phone Number should be 10 digits");
                }
            }
        });

        signUpPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (signUpPwd.getText().toString().length() < PASSWORD_MIN_LENGTH){
                    signUpPwd.setError("Password 6 digits at least");
                }
            }
        });

        signUpPwd2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (signUpPwd2.getText().toString().length() < PASSWORD_MIN_LENGTH){
                    signUpPwd2.setError("Password 6 digits at least");
                }
            }
        });
        return regView;
    }

    private void setupUI() {

        //if already havs account then go back to sign in
        toSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager = SignInActivity.sinViewPager;
                viewPager.setCurrentItem(0);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //password and retypepassword show be same
                if (!signUpPwd.getText().toString().equals(signUpPwd2.getText().toString())){
                    //Toast.makeText(getActivity(), "Password and confirm password not same. Please try again.", Toast.LENGTH_SHORT).show();
                    Snackbar.make(regView, "Password and confirm password not same. Please try again.", Snackbar.LENGTH_LONG).show();
                    return;
                } else if (signUpMobile.getText().length() != MOBILE_MIN_LENGTH){
                    //Toast.makeText(getActivity(), "Phone number not valid. Please try again.", Toast.LENGTH_SHORT).show();
                    Snackbar.make(regView, "Phone number not valid. Please try again.", Snackbar.LENGTH_LONG).show();
                } else if (signUpPwd.getText().toString().length() < PASSWORD_MIN_LENGTH){
                    //Toast.makeText(getActivity(), "Password not valid. Please try again.", Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, "Password not valid. Please try again.", Snackbar.LENGTH_LONG).show();
                } else if (signUpPwd2.getText().toString().length() < PASSWORD_MIN_LENGTH){
                    //Toast.makeText(getActivity(), "Password not valid. Please try again.", Toast.LENGTH_SHORT).show();
                    Snackbar.make(regView, "Password not valid. Please try again.", Snackbar.LENGTH_LONG).show();
                } else if (!isEmailValid(signUpEmail.getText().toString().trim())){
                    //Toast.makeText(getActivity(), "Email not valid. Please try again.", Toast.LENGTH_SHORT).show();
                    Snackbar.make(regView, "Email not valid. Please try again.", Snackbar.LENGTH_LONG).show();
                }
                register();
            }
        });


        signUpEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!isEmailValid(signUpEmail.getText().toString().trim())){
                    signUpEmail.setError("Not a valid Email Address");
                }
            }
        });

        Log.i(TAG, "register email= " + signUpEmail.getText().toString().trim());
    }
    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private String buildUrl(){
        return REG_URL +
                "?user_name=" + signUpUsername.getText().toString() +
                "&user_email=" + signUpEmail.getText().toString() +
                "&user_phone=" + signUpMobile.getText().toString() +
                "&user_password=" + signUpPwd.getText().toString() +
                "&user_add=" + signUpAddress.getText().toString();
    }
    private void register() {
        SignInActivity.showPDialog();
        StringRequest mRequest = new StringRequest(Request.Method.POST, buildUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains(REGSUCCESS)) {
                            //register success, go to order
                            viewPager = SignInActivity.sinViewPager;
                            viewPager.setCurrentItem(0);
                            SignInActivity.NumberPagerAdapter adapter = new SignInActivity.NumberPagerAdapter(fm);
                            LoginFragment loginFragment = (LoginFragment) adapter.getRegisteredFragment(0);
                            loginFragment.signinMobile.setText(signUpMobile.getText().toString());
                            signinPwd.setText(signUpPwd.getText().toString());
                        } else {
                            //Toast.makeText(getActivity(), "Phone number already existed. Please try again!", Toast.LENGTH_SHORT).show();
                            Snackbar.make(regView, "Phone number already existed. Please try again!", Snackbar.LENGTH_LONG).show();
                            signUpMobile.setText("");
                            signUpPwd.setText("");
                            signUpPwd2.setText("");
                        }
                        SignInActivity.disPDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
                SignInActivity.disPDialog();
            }
        });
        VolleyController.getmInstance().addToRequestQueue(mRequest, TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
