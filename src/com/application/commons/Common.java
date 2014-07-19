package com.application.commons;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.application.models.Review;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Common {
	public static final String SHAREDPREF_REGISTERED_USERNAME = "SHAREDPREF_REGISTERED_USERNAME";
    public static final String SHAREDPREF_PROFILE_PIC_FILE_NAME = "SHAREDPREF_PROFILE_PIC_FILE_NAME";
    public static final String REVIEW_STATUS_KEY = "REVIEW_STATUS_ID";
    public static final String EMPTY_PIC_PATH = "EMPTY_PIC_PATH";
    public static final int VIBRATE_TIME = 15;
    public static final long ANIMATION_GROW_TIME = 400;
    public static final String EMPTY_VALUE = "EMPTY_VALUE";
    public static final String SELECTED_ITEM = "SELECTED_ITEM";
    public static final String REVIEW_ID = "REVIEW_ID";
    private static final String TAG = "Common_TAG";
    public static final String ADD_REVIEW_FROM_LISTVIEW = "ADD_REVIEW_FROM_LISTVIEW";
    public static final String COFFEE_MACHINE_FRAGMENT_TAG = "COFFEE_MACHINE_FRAGMENT_TAG";
    public static final String TERRIBLE_STATUS_STRING = "Damn Terrible";
    public static final String GOOD_STATUS_STRING = "That's Good";
    public static final String NOTSOBAD_STATUS_STRING = "Mmmmmm";
    public static final int NUM_PAGES = 3;
    public static final String ARG_REVIEW_PAGE = "REVIEW_PAGE";
    public static final String IS_TODAY_REVIEW_KEY = "IS_TODAY_REVIEW_KEY";
    public static final String FIRST_INIT_VIEW = "FIRST_INIT_VIEW";
    public static int ITEM_NOT_SELECTED = -1;
    public static String SET_MORE_TEXT_ON_REVIEW = "SET_MORE_TEXT_ON_REVIEW";
    public static final String ARG_PAGE = "page";
    public static final long DATE_NOT_SET = -99;
    public static final String PROFILE_PIC_SIZE = "300";

    public static final String NEW_USER_FRAGMENT_TAG = "NEW_USER_FRAGMENT_TAG";
    public static final String ADD_REVIEW_FRAGMENT_TAG = "ADD_REVIEW_FRAGMENT";

    public static ReviewStatusEnum parseStatusFromPageNumber(int pageNumber) {
        ReviewStatusEnum status;
        switch (pageNumber) {
            case 0:
                status = ReviewStatusEnum.GOOD;
                break;
            case 1:
                status = ReviewStatusEnum.NOTSOBAD;
                break;
            case 2:
                status = ReviewStatusEnum.WORST;
                break;
            default:
                status = ReviewStatusEnum.NOTSET;
        }
        return status;
    }

    public static ReviewStatusEnum parseStatusFromString(String reviewStatus) {
        if(reviewStatus.equals("GOOD")) {
            return ReviewStatusEnum.GOOD;
        } else if(reviewStatus.equals("NOT_BAD")) {
            return ReviewStatusEnum.NOTSOBAD;
        } else if(reviewStatus.equals("WORST")) {
            return ReviewStatusEnum.WORST;
        }
        Log.e(TAG, "status not set - sorry I'll not add this review");
        return ReviewStatusEnum.NOTSET;
    }

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
    public static int PROFILE_PIC_CIRCLE_MASK_BIGGER_SIZE = 312;
    public static String COFFE_MACHINE_ID_KEY = "coffeMachineId";

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

    public static boolean drawProfilePictureByPath(ImageView v, String profilePicturePath, Drawable defaultIcon) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            if(profilePicturePath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(profilePicturePath, options);
                v.setImageBitmap(bitmap);
            } else {
                v.setImageDrawable(defaultIcon);
            }
            return true;
        } catch(Exception e) {
            Log.e(TAG, "cannot load profile picture - set the default one");
//            e.printStackTrace();
        }
        return false;
    }


    public static String saveImageInStorage(Bitmap profileImage, File customDir) {
        File profilePicFile = new File(customDir, Common.PROFILE_PIC_FILE_NAME); //Getting a file within the dir.
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(profilePicFile);
            profileImage.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try{
                out.close();
            } catch(Throwable ignore) {
                return null;
            }
        }
//        Log.e(TAG, profilePicFile.getAbsolutePath());

        return profilePicFile.getAbsolutePath();
    }
    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int size) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(size / 2, size / 2, size / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, 0, 0, paint);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError o) {
            o.printStackTrace();
        }
        return result;
    }


    public static Bitmap getRoundedBitmap(int size, int color) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bmp);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(size/2, size/2, size/2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bmp, 0, 0, paint);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError o) {
            o.printStackTrace();
        }
        return bmp;
    }

    public static class ReviewListTimestamp {
        private final long fromTimestamp;
        private final long toTimestamp;
        private final ArrayList<Review> reviewsList;

        public ReviewListTimestamp(long fromTimestamp, long toTimestamp, ArrayList<Review> reviewsList) {
            this.fromTimestamp = fromTimestamp;
            this.toTimestamp = toTimestamp;
            this.reviewsList = reviewsList;
        }

        public long getFromTimestamp() {
            return fromTimestamp;
        }

        public long getToTimestamp() {
            return toTimestamp;
        }

        public ArrayList<Review> getReviewsList() {
            return reviewsList;
        }
    }
}
