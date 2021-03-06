package com.application.commons;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.application.models.Review;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Common {
	public static final String SHAREDPREF_REGISTERED_USERNAME = "SHAREDPREF_REGISTERED_USERNAME";
    public static final String SHAREDPREF_PROFILE_PIC_FILE_NAME = "SHAREDPREF_PROFILE_PIC_FILE_NAME";
    public static final String SHAREDPREF_REGISTERED_USER_ID = "SHAREDPREF_REGISTERED_USER_ID";
    public static final String REVIEW_STATUS_KEY = "REVIEW_STATUS_ID";
    public static final String EMPTY_PIC_PATH = "EMPTY_PIC_PATH";
    public static final int VIBRATE_TIME = 15;
    public static final long ANIMATION_GROW_TIME = 400;
    public static final String EMPTY_VALUE = "EMPTY_VALUE";
    public static final String REVIEW_ID = "REVIEW_ID";
    public static final String COFFEE_MACHINE_FRAGMENT_TAG = "COFFEE_MACHINE_FRAGMENT_TAG";
    public static final String TERRIBLE_STATUS_STRING = "Damn Terrible";
    public static final String GOOD_STATUS_STRING = "That's Good";
    public static final String NOTSOBAD_STATUS_STRING = "Mmmmmm";
    public static final int NUM_PAGES = 3;
    public static final String EMPTY_REVIEW_STRING  = "0";
    public static final String LOCAL_USER_ID = "LOCAL_USER_ID";
    public static final String FROM_TIMESTAMP_KEY = "FROM_TIMESTAMP_KEY";
    public static final String TO_TIMESTAMP_KEY = "TO_TIMESTAMP_KEY";
    public static final String SHARED_PREF = "SHARED_PREF_COFFEE_MACHINE";
    public static final String NOT_VALID_USER_ID = "NOT_VALID_USER_ID";
    public static int ITEM_NOT_SELECTED = -1;
    public static String SET_MORE_TEXT_ON_REVIEW = "SET_MORE_TEXT_ON_REVIEW";
    public static final String ARG_PAGE = "page";
    public static final long DATE_NOT_SET = -99;
    public static final String PROFILE_PIC_SIZE = "300";

    public static final String NEW_USER_FRAGMENT_TAG = "NEW_USER_FRAGMENT_TAG";

    public static String COFFEE_MACHINE_DIR = "coffeeMachineFolder";
    public static String PROFILE_PIC_FILE_NAME = "profilePicture";


    public static int PROFILE_PIC_CIRCLE_MASK_SIZE = 300;
    public static int CHANGE_LOGGED_USERNAME = 99;
    public static int ICON_SMALL_SIZE = 180;
    public static int PROFILE_PIC_CIRCLE_MASK_BIGGER_SIZE = 312;
    public static String COFFEE_MACHINE_ID_KEY = "coffeeMachineId";
    public static int REVIEW_COUNTER_ERROR = -1;

    public enum ReviewStatusEnum {
        GOOD,
        NOTSOBAD,
        NOTSET,
        WORST
    }

    public static void setCustomFontByView(AssetManager assets, View view, boolean boldSet) {
//        Typeface boldFont = Typeface.createFromAsset(assets, "fonts/ThrowMyHandsUpintheAir.ttf");
        Typeface boldFont = Typeface.createFromAsset(assets, "fonts/AmaticSC-Regular.ttf");
        if(boldSet) {
            boldFont = Typeface.createFromAsset(assets, "fonts/ThrowMyHandsUpintheAirBold.ttf");
        }

        if(view instanceof TextView || view instanceof Button) {
            ((TextView) view).setTypeface(boldFont);
        }
    }

    public static void setCustomFontByTextView(View v, AssetManager assets) {
        //set custom font style
//        Typeface font = Typeface.createFromAsset(assets, "fonts/Qiber.otf");
//        Typeface font = Typeface.createFromAsset(assets, "fonts/rabiohead.ttf");
//        Typeface font = Typeface.createFromAsset(assets, "fonts/ThrowMyHandsUpintheAir.ttf");
        Typeface font = Typeface.createFromAsset(assets, "fonts/AmaticSC-Regular.ttf");
        ((TextView) v).setTypeface(font);
    }

    public static void setCustomFont(View fragmentView, AssetManager assets) {
        //set custom font style
//        Typeface font = Typeface.createFromAsset(assets, "fonts/Qiber.otf");
//        Typeface font = Typeface.createFromAsset(assets, "fonts/rabiohead.ttf");
//        Typeface font = Typeface.createFromAsset(assets, "fonts/ThrowMyHandsUpintheAir.ttf");
        Typeface font = Typeface.createFromAsset(assets, "fonts/AmaticSC-Regular.ttf");

        ViewGroup root = (ViewGroup)fragmentView.getRootView();
        setFont(root, font);
    }

    public static void setFont(ViewGroup root, Typeface font) {
        int count = root.getChildCount();
        View v;
        for(int i = 0; i < count; i++) {
            v = root.getChildAt(i);
            if(v instanceof TextView || v instanceof Button /*etc.*/)
                ((TextView)v).setTypeface(font);
            else if(v instanceof ViewGroup)
                setFont((ViewGroup)v, font);
        }
    }

    public static void displayError(Context context, String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

    public static void hideKeyboard(Activity activityRef, EditText editTextView) {
        //hide keyboard
        InputMethodManager imm = (InputMethodManager)activityRef.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextView.getWindowToken(), 0);
    }

    public static void vibrate(Activity activityRef, int time) {
        Vibrator v = (Vibrator) activityRef.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(time);

    }
    


/*    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }*/

    public static boolean isConnected(Context context) {
        return false;
    }

}
