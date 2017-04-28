/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 2:47 AM
 */

package com.willyao.android.foodordersystem.startPage.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.homePage.HomePageActivity;
import com.willyao.android.foodordersystem.startPage.SignInActivity;
import com.willyao.android.foodordersystem.utils.sharedPreferences.MySPhelper;
import com.willyao.android.foodordersystem.utils.volley.VolleyController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mitya on 4/13/2017.
 */

public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{



    @BindView(R.id.sign_in_btn) Button signInBtn;
    public static EditText signinMobile, signinPwd;
    private final String LOGIN_URL = "http://rjtmobile.com/ansari/fos/fosapp/fos_login.php";
    private final String SUCCESS = "success";
    private final String TAG = "LoginIn";
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 001;
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int MOBILE_MIN_LENGTH = 10;
    LoginButton mLoginButton;
    ImageButton mFBbtn, mGGBtn;
    CallbackManager callbackManager;
    Unbinder unbinder;
    View loginView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        loginView = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, loginView);
        signinMobile = (EditText) loginView.findViewById(R.id.signin_mobile);
        signinPwd = (EditText) loginView.findViewById(R.id.signin_pwd);
        mFBbtn = (ImageButton) loginView.findViewById(R.id.fb_login_btn);
        mGGBtn = (ImageButton) loginView.findViewById(R.id.gg_login_btn);

        signinMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (signinMobile.getText().length() != MOBILE_MIN_LENGTH){
                    signinMobile.setError("Phone Number should be 10 digits");
                }
            }
        });

        signinPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (signinPwd.getText().toString().length() < PASSWORD_MIN_LENGTH){
                    signinPwd.setError("Password 6 digits at least");
                }
            }
        });
        mFBGGLogin();
        login();
        return loginView;
    }

    private void mFBGGLogin() {
        /*------------------------google login---------------------------*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGGBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        /*------------------------facebook login---------------------------*/
        mFBbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginButton.performClick();
            }
        });
        mLoginButton = new LoginButton(getContext());
        mLoginButton.setReadPermissions("email");
        // when using fragment
        mLoginButton.setFragment(this);
        // other app specific specialization
        callbackManager = CallbackManager.Factory.create();
        // callback registration
        mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App <code></code>
                final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback(){

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String id = object.getString("id");
                                    MySPhelper.getInstance(getContext()).setMobile(id);
                                    //--------------open homePage here
                                    Intent toHomePage = new Intent(getActivity(), HomePageActivity.class);
                                    startActivity(toHomePage);
                                    //Toast.makeText(getContext(), "facebook login", Toast.LENGTH_LONG).show();
                                    Snackbar.make(loginView, "facebook login", Snackbar.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                // App code
                Log.i("fblogin", "fb log in error");
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()){
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        if (result.isSuccess()){
            // Signed in successfully, show  authenticated UI
            GoogleSignInAccount account = result.getSignInAccount();
            // start another activity
            Intent intent = new Intent(getActivity(), HomePageActivity.class);
            startActivity(intent);
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void login() {
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInfo();
            }
        });
    }

    private void checkInfo() {
        SignInActivity.showPDialog();
        String loginMobile = signinMobile.getText().toString();
        String loginPwd = signinPwd.getText().toString();

        String url = LOGIN_URL + "?user_phone=" + loginMobile + "&user_password=" + loginPwd;
        Log.i(TAG, "url= " + url);

        StringRequest sRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "response= " + response);
                        if (response.toString().contains(SUCCESS)) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject userInfo = jsonArray.getJSONObject(0);
                                //save user info here
                                MySPhelper.getInstance(getContext()).setName(userInfo.getString("UserName"));
                                MySPhelper.getInstance(getContext()).setEmail(userInfo.getString("UserEmail"));
                                MySPhelper.getInstance(getContext()).setAddress(userInfo.getString("UserAddress"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                            Snackbar.make(loginView, "Login successful!", Snackbar.LENGTH_LONG).show();
                            //show the correct pwd and mobile
                            MySPhelper.getInstance(getContext()).setMobile(signinMobile.getText().toString());
                            MySPhelper.getInstance(getContext()).setPwd(signinPwd.getText().toString());
                            //suppose to jump to next activity here
                            Intent homePageIntent = new Intent(getActivity(), HomePageActivity.class);
                            startActivity(homePageIntent);
                        } else {
                            //Toast.makeText(getContext(), "Password or Mobile is unCorrect. Please try again.", Toast.LENGTH_SHORT).show();
                            Snackbar.make(loginView, "Password or Mobile is unCorrect. Please try again.", Snackbar.LENGTH_LONG).show();
                        }
                        SignInActivity.disPDialog();
                    }
                }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //error info
                            Log.e(TAG, error.getMessage(), error);
                            SignInActivity.disPDialog();
                        }
                }
        );
        VolleyController.getmInstance().addToRequestQueue(sRequest,TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
