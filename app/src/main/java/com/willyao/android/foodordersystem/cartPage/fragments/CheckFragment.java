/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 10:59 AM
 */

package com.willyao.android.foodordersystem.cartPage.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentMode;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.cartPage.CartActivity;
import com.willyao.android.foodordersystem.cartPage.MapsActivity;
import com.willyao.android.foodordersystem.cartPage.adapters.CheckoutAdapter;
import com.willyao.android.foodordersystem.homePage.HomePageActivity;
import com.willyao.android.foodordersystem.model.Food;
import com.willyao.android.foodordersystem.utils.cartItem.ShoppingCartItem;
import com.willyao.android.foodordersystem.utils.dataBase.DBManipulation;
import com.willyao.android.foodordersystem.utils.sharedPreferences.MySPhelper;

import org.json.JSONException;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by mitya on 4/22/2017.
 */

public class CheckFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.recyclerview_checkout) RecyclerView mRecyclerView;
    @BindView(R.id.checkout_total) TextView mTextTotal;
    @BindView(R.id.checkout_mobile) TextView mTextMobile;
    @BindView(R.id.checkout_edit_mobile) ImageView mTextEditMobil;
    @BindView(R.id.checkout_edit_addr) ImageView mTextEditAddress;
    @BindView(R.id.checkout_pay) Button mButtonCheckout;
    @BindView(R.id.checkout_cancel) Button mButtonCancel;
    @BindView(R.id.confirm_button) Button mConfirm;
    Unbinder unbinder;

    public static TextView mTextAddress;
    //initialize PayPal
    private static final String TAG = "PayPalCartPayment";
    private static final String CONFIG_CLIENT_ID = "Acbn4PIjWOW37bAnFAH5YhvTOfTmWXZ5HDwim4flJDtP35O0NFKCfArHsuITCzg-9LeubXiO32b7WSPP";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    public static final int RESULT_EXTRAS_INVALID = 2;
    private static PayPalConfiguration config = new PayPalConfiguration().environment(CONFIG_ENVIRONMENT).clientId(CONFIG_CLIENT_ID);
    /*=====================implement android pay=====================*/
    private SupportWalletFragment mWalletFragment;
    public static final int MASKED_WALLET_REQUEST_CODE = 888;
    public static final String WALLET_FRAGMENT_ID = "wallet_fragment";
    private MaskedWallet mMaskedWallet;
    private GoogleApiClient mGoogleApiClient;
    public static final int FULL_WALLET_REQUEST_CODE = 889;
    private FullWallet mFullWallet;
    View checkView;
    /*===============================================================*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        checkView = inflater.inflate(R.layout.fragment_checkout, container, false);
        unbinder = ButterKnife.bind(this, checkView);
        /* ======================Android Pay=======================*/
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addOnConnectionFailedListener(this)
                .enableAutoManage(getActivity(), 0, this)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .setTheme(WalletConstants.THEME_LIGHT)
                        .build())
                .build();
        // Check if WalletFragment exists
        mWalletFragment = (SupportWalletFragment) getFragmentManager()
                .findFragmentByTag(WALLET_FRAGMENT_ID);

        if (mWalletFragment == null){
            // Wallet fragment style
            WalletFragmentStyle walletFragmentStyle = new WalletFragmentStyle()
                    .setBuyButtonText(WalletFragmentStyle.BuyButtonText.BUY_WITH)
                    .setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT);

            // Wallet fragment options
            WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
                    .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                    .setFragmentStyle(walletFragmentStyle)
                    .setTheme(WalletConstants.THEME_LIGHT)
                    .setMode(WalletFragmentMode.BUY_BUTTON)
                    .build();

            // Initialize the WalletFragment
            WalletFragmentInitParams.Builder startParamsBuilder =
                    WalletFragmentInitParams.newBuilder()
                            .setMaskedWalletRequest(generateMaskedWalletRequest())
                            .setMaskedWalletRequestCode(MASKED_WALLET_REQUEST_CODE)
                            .setAccountName("Google I/O Codelab");
            mWalletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);
            mWalletFragment.initialize(startParamsBuilder.build());

            // Add the WalletFragment to the UI
            getFragmentManager().beginTransaction()
                    .replace(R.id.wallet_button_holder, mWalletFragment, WALLET_FRAGMENT_ID)
                    .commit();
        }

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFullWallet(view);
//                mFullWallet.getPaymentMethodToken();
            }
        });

        /*=============================================================================*/



        mRecyclerView.setAdapter(new CheckoutAdapter(getContext()));
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mButtonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check out here
                payOrder();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        mTextMobile.setText(MySPhelper.getInstance(getContext()).getMobile());
        mTextAddress = (TextView) checkView.findViewById(R.id.checkout_address);
        mTextAddress.setText(MySPhelper.getInstance(getContext()).getAddress());
        mTextEditMobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reset mobile here
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.dialog_set_mobile,(ViewGroup) view.findViewById(R.id.dialog_mobile));
                new AlertDialog.Builder(getActivity()).setTitle("Please Input Contact Information").setIcon(
                        android.R.drawable.ic_dialog_dialer).setView(
                        layout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog dialog = (Dialog) dialogInterface;
                        EditText inputMobile = (EditText) dialog.findViewById(R.id.dialog_et_mobile);
                        if (inputMobile.getText().toString().isEmpty()){
                            return;
                        }
                        try{
                            long number = Long.valueOf(inputMobile.getText().toString());
                            MySPhelper.getInstance(getActivity()).setMobile(inputMobile.getText().toString());
                            mTextMobile.setText(inputMobile.getText().toString());
                        }catch (Exception e){
                            Toast.makeText(getActivity(), "Please Input Correct Phone Number!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Cancel", null).show();
            }
        });
        mTextEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reset address here
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.dialog_set_location,(ViewGroup) view.findViewById(R.id.dialog_location));
                new AlertDialog.Builder(getActivity()).setTitle("Please Input Delivery Location")
                        .setIcon(R.drawable.dialog).setView(layout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog dialog = (Dialog) dialogInterface;
                        EditText inputLocation = (EditText) dialog.findViewById(R.id.dialog_et_location);
                        if (inputLocation.getText().toString().isEmpty()){
                            return;
                        }
                        mTextAddress.setText(inputLocation.getText().toString());
                    }
                })
                        .setNeutralButton("Chose on Map", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent mapAct = new Intent(getActivity(), MapsActivity.class);
                                startActivity(mapAct);
                            }
                        })
                        .setNegativeButton("Later", null)
                        .show();
            }
        });
        mTextTotal.setText(String.valueOf(ShoppingCartItem.getInstance(getContext()).getPrice() * 1.06 + 10.00));
        return checkView;
    }

    private void payOrder() {
        PayPalPayment thingToBuy = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getContext(), PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details
        PayPalItem[] items = new PayPalItem[ShoppingCartItem.getInstance(getContext()).getFoodTypeSize()];
        for (int position = 0; position < ShoppingCartItem.getInstance(getContext()).getFoodTypeSize(); position++){
            int id = ShoppingCartItem.getInstance(getContext()).getFoodInCart().get(position);
            final Food curFood = ShoppingCartItem.getInstance(getContext()).getFoodById(id);
            final int curFoodNumber = ShoppingCartItem.getInstance(getContext()).getFoodNumber(curFood);
            Log.e("PRICE & NUMBER", "price: " + curFood.getfPrice() + ", number: " + curFoodNumber);
            items[position] = new PayPalItem("Item No." + curFood.getfId(),
                    curFoodNumber,
                    new BigDecimal(String.valueOf(curFood.getfPrice())),
                    "USD",
                    curFood.getfName()
            );
        }
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal("10.00");
        BigDecimal tax = new BigDecimal("" + ShoppingCartItem.getInstance(getContext()).getPrice() * 0.06);
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, "USD", "Rush Food Inc.", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            /*Android Pay*/
            case MASKED_WALLET_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mMaskedWallet =  data
                                .getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                        Toast.makeText(getContext(), "Got Masked Wallet", Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        // The user canceled the operation
                        break;
                    case WalletConstants.RESULT_ERROR:
                        Toast.makeText(getContext(), "An Error Occurred", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case FULL_WALLET_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mFullWallet = data
                                .getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);
                        // Show the credit card number
                        Toast.makeText(getContext(),
                                "Got Full Wallet, Done!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case WalletConstants.RESULT_ERROR:
                        Toast.makeText(getContext(), "An Error Occurred", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            /*==================================================*/
            case REQUEST_CODE_PAYMENT:
                switch (resultCode) {
                    case RESULT_OK:
                        ShoppingCartItem.getInstance(getContext()).placeOrder(
                                mTextAddress.getText().toString(), mTextMobile.getText().toString());
                        ShoppingCartItem.getInstance(getContext()).clear();
                        DBManipulation.getmInstance(getActivity()).deleteAll();
                        PaymentConfirmation confirmation =
                                data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                        if (confirmation != null){
                            try {
                                Log.i(TAG, confirmation.toJSONObject().toString(4));
                                Log.i(TAG, confirmation.getPayment().toJSONObject().toString(4));
                                /**
                                 *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                                 * or consent completion.
                                 * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                                 * for more details.
                                 *
                                 * For sample mobile backend interactions, see
                                 * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                                 */
                                registerOrder();
                            }
                            catch (JSONException e) {
                                Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                            }
                        }
                        HomePageActivity.cartNumber.setText("0");
                        getActivity().finish();
                        break;
                    case RESULT_CANCELED:
                        Log.i(TAG, "The user canceled.");
                        Toast.makeText(getContext(),"Cancel", Toast.LENGTH_LONG).show();
                        break;
                    case RESULT_EXTRAS_INVALID:
                        Log.i(
                                TAG,
                                "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                        break;
                }
                break;
        }
//        if (requestCode == REQUEST_CODE_PAYMENT){
//            if (resultCode == getActivity().RESULT_OK){
//                ShoppingCartItem.getInstance(getContext()).placeOrder(
//                        mTextAddress.getText().toString(), mTextMobile.getText().toString());
//                ShoppingCartItem.getInstance(getContext()).clear();
//                DBManipulation.getmInstance(getActivity()).deleteAll();
//                PaymentConfirmation confirmation =
//                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//                if (confirmation != null){
//                    try {
//                        Log.i(TAG, confirmation.toJSONObject().toString(4));
//                        Log.i(TAG, confirmation.getPayment().toJSONObject().toString(4));
//                        /**
//                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
//                         * or consent completion.
//                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
//                         * for more details.
//                         *
//                         * For sample mobile backend interactions, see
//                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
//                         */
//                        registerOrder();
//                    }
//                    catch (JSONException e) {
//                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
//                    }
//                }
//                HomePageActivity.cartNumber.setText("0");
//                getActivity().finish();
//            } else if (resultCode == getActivity().RESULT_CANCELED) {
//                Log.i(TAG, "The user canceled.");
//                Toast.makeText(getContext(),"Cancel", Toast.LENGTH_LONG).show();
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                Log.i(
//                        TAG,
//                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
//            }
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(new Intent(getContext(), PayPalService.class));
    }

    private void registerOrder() {
        Intent cart = new Intent(getActivity(), CartActivity.class);
        startActivity(cart);
        Toast.makeText(getContext(), "Payment Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /* ================Android Pay Request a Masked Wallet object=====================*/
    private MaskedWalletRequest generateMaskedWalletRequest() {
        // This is just an example publicKey for the purpose of this codelab.
        // To learn how to generate your own visit:
        // https://github.com/android-pay/androidpay-quickstart
        String publicKey = "BO39Rh43UGXMQy5PAWWe7UGWd2a9YRjNLPEEVe+zWIbdIgALcDcnYCuHbmrrzl7h8FZjl6RCzoi5/cDrqXNRVSo=";
        PaymentMethodTokenizationParameters parameters =
                PaymentMethodTokenizationParameters.newBuilder()
                        .setPaymentMethodTokenizationType(
                                PaymentMethodTokenizationType.NETWORK_TOKEN)
                        .addParameter("publicKey", publicKey)
                        .build();

        MaskedWalletRequest maskedWalletRequest =
                MaskedWalletRequest.newBuilder()
                        .setMerchantName("Google I/O Codelab")
                        .setPhoneNumberRequired(true)
                        .setShippingAddressRequired(true)
                        .setCurrencyCode("USD")
                        .setCart(Cart.newBuilder()
                                .setCurrencyCode("USD")
                                .setTotalPrice("10.00")
                                .addLineItem(LineItem.newBuilder()
                                        .setCurrencyCode("USD")
                                        .setDescription("Google I/O Sticker")
                                        .setQuantity("1")
                                        .setUnitPrice("10.00")
                                        .setTotalPrice("10.00")
                                        .build())
                                .build())
                        .setEstimatedTotalPrice("15.00")
                        .setPaymentMethodTokenizationParameters(parameters)
                        .build();
        return maskedWalletRequest;
    }
    private FullWalletRequest generateFullWalletRequest(String googleTransactionId) {
        FullWalletRequest fullWalletRequest = FullWalletRequest.newBuilder()
                .setGoogleTransactionId(googleTransactionId)
                .setCart(Cart.newBuilder()
                        .setCurrencyCode("USD")
                        .setTotalPrice("10.10")
                        .addLineItem(LineItem.newBuilder()
                                .setCurrencyCode("USD")
                                .setDescription("Google I/O Sticker")
                                .setQuantity("1")
                                .setUnitPrice("10.00")
                                .setTotalPrice("10.00")
                                .build())
                        .addLineItem(LineItem.newBuilder()
                                .setCurrencyCode("USD")
                                .setDescription("Tax")
                                .setRole(LineItem.Role.TAX)
                                .setTotalPrice(".10")
                                .build())
                        .build())
                .build();
        return fullWalletRequest;
    }

    public void requestFullWallet(View view) {
        if (mMaskedWallet == null) {
            Toast.makeText(getContext(), "No masked wallet, can't confirm", Toast.LENGTH_SHORT).show();
            return;
        }
        Wallet.Payments.loadFullWallet(mGoogleApiClient,
                generateFullWalletRequest(mMaskedWallet.getGoogleTransactionId()),
                FULL_WALLET_REQUEST_CODE);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // GoogleApiClient failed to connect, we should log the error and retry
    }
}
