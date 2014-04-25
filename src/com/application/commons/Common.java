package com.application.commons;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Common {
	public static final String SHAREDPREF_REGISTERED_USERNAME = "SHAREDPREF_REGISTERED_USERNAME";
    public static final String SHAREDPREF_PROFILE_PIC_FILE_NAME = "SHAREDPREF_PROFILE_PIC_FILE_NAME";
    public static final String REVIEW_STATUS_KEY = "REVIEW_STATUS_ID";
    public static final String EMPTY_PIC_PATH = "EMPTY_PIC_PATH";
    public static final int VIBRATE_TIME = 15;
    public static final long ANIMATION_GROW_TIME = 400;

    public enum ReviewStatusEnum {
        GOOD,
        NOTSOBAD,
        NOTSET, WORST
    };

    public static String COFFEE_MACHINE_DIR = "coffeeMachineFolder";
    public static String PROFILE_PIC_FILE_NAME = "profilePicture";


    public static int CHANGE_LOGGED_USERNAME = 99;

    public static int ICON_SMALL_SIZE = 180;
    public static int PROFILE_PIC_CIRCLE_MASK_SIZE = 300;
    public static int PROFILE_PIC_CIRCLE_MASK_BIGGER_SIZE = 340;
    public static String COFFE_MACHINE_ID_KEY = "coffeMachineId";

    public static void setCustomFontByView(AssetManager assets, View view, boolean boldSet) {
        Typeface boldFont = Typeface.createFromAsset(assets, "fonts/ThrowMyHandsUpintheAir.ttf");
        if(boldSet) {
            boldFont = Typeface.createFromAsset(assets, "fonts/ThrowMyHandsUpintheAirBold.ttf");
        }

        if(view instanceof TextView || view instanceof Button) {
            ((TextView) view).setTypeface(boldFont);
        }
    }

    public static void setCustomFont(View fragmentView, AssetManager assets) {
        //set custom font style
//        Typeface font = Typeface.createFromAsset(assets, "fonts/Qiber.otf");
//        Typeface font = Typeface.createFromAsset(assets, "fonts/rabiohead.ttf");
        Typeface font = Typeface.createFromAsset(assets, "fonts/ThrowMyHandsUpintheAir.ttf");

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
    
    //bmpBelow below - bmpAbove above
    public static Bitmap overlayBitmaps(Bitmap bmpBelow, Bitmap bmpAbove) {
        Bitmap bitmapOverlay = Bitmap.createBitmap(bmpBelow.getWidth(), bmpBelow.getHeight(), bmpBelow.getConfig());
        Canvas canvas = new Canvas(bitmapOverlay);
        canvas.drawBitmap(bmpBelow, new Matrix(), null);
        int floatImageSize = (bmpBelow.getWidth() - bmpAbove.getWidth()) / 2;
        canvas.drawBitmap(bmpAbove, floatImageSize, floatImageSize, null);
        return  bitmapOverlay;
    }


}
