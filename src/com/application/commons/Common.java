package com.application.commons;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Common {
	public static final String SHAREDPREF_REGISTERED_USERNAME = "SHAREDPREF_REGISTERED_USERNAME";
    public static final String SHAREDPREF_PROFILE_PIC_FILE_NAME = "SHAREDPREF_PROFILE_PIC_FILE_NAME";

    public enum ReviewStatusEnum {NOT_SET,AWFUL,NOT_BAD,GOOD};

    public static String COFFEE_MACHINE_DIR = "coffeeMachineFolder";
    public static String PROFILE_PIC_FILE_NAME = "profilePicture";


    public static int CHANGE_LOGGED_USERNAME =99;

    public static int PROFILE_PIC_CIRCLE_MASK_SIZE = 100;
    public static String COFFE_MACHINE_ID_KEY = "coffeMachineId";

    public static void setCustomFont(View fragmentView, AssetManager assets) {
        //set custom font style
        Typeface font = Typeface.createFromAsset(assets, "fonts/Qiber.otf");
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

    public static void displayError(String error, Context context) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}
