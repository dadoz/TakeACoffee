package com.application.commons;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Common {
	public static final String REGISTERED_USERNAME = "REGISTERED_USERNAME";
	public enum ReviewStatusEnum {NOT_SET,AWFUL,NOT_BAD,GOOD};

    public static int CHANGE_LOGGED_USERNAME =99;

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


}
