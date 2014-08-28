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
    public static final long LOCAL_USER_ID = -2;
    public static final long EMPTY_LONG_VALUE = -1;
    public static final String FROM_TIMESTAMP_KEY = "FROM_TIMESTAMP_KEY";
    public static final String TO_TIMESTAMP_KEY = "TO_TIMESTAMP_KEY";
    public static final String SHARED_PREF = "SHARED_PREF_COFFEE_MACHINE";
    private static final long PROFILE_PIC_MIN_HEIGHT = 300;
    private static final long PROFILE_PIC_MIN_WIDTH = 300;
    public static int ITEM_NOT_SELECTED = -1;
    public static String SET_MORE_TEXT_ON_REVIEW = "SET_MORE_TEXT_ON_REVIEW";
    public static final String ARG_PAGE = "page";
    public static final long DATE_NOT_SET = -99;
    public static final String PROFILE_PIC_SIZE = "300";

    public static final String NEW_USER_FRAGMENT_TAG = "NEW_USER_FRAGMENT_TAG";
    public static final String ADD_REVIEW_FRAGMENT_TAG = "ADD_REVIEW_FRAGMENT";

    public static final String DATABASE_NAME = "takeacoffeedb";

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

    public static String saveImageInStorage(Bitmap profileImage, File customDir) {
        if(profileImage == null) {
            return null;
        }

        File profilePicFile = new File(customDir, Common.PROFILE_PIC_FILE_NAME); //Getting a file within the dir.
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(profilePicFile);
            profileImage.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return profilePicFile.getAbsolutePath();
    }

    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int size) {
        Bitmap result;
        if(bitmap != null) {
            try {
                bitmap = getScaledBitmap(bitmap, size); //NOT CHECKING ERROR COS IF NOT RETURN NOT CHANGED BITMAP
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
                return result;
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError o) {
                o.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private static Bitmap getScaledBitmap(Bitmap bitmap, int size) {
        //TODO set up in a function
        if(bitmap != null && bitmap.getHeight() != size && bitmap.getWidth() != size) {
            //resize bitmap to accord on size
            float ratio;
            float scaledWidth = -1;
            float scaledHeight = -1;
            if((bitmap.getHeight() < size)) {
                ratio = (float) size / (float) bitmap.getHeight();
                scaledHeight = size;
                scaledWidth = (float) bitmap.getWidth() * ratio;
            }

            if((bitmap.getWidth() < size) &&
                    scaledHeight != -1) {
                ratio = (float)  size / (float) bitmap.getWidth();
                scaledWidth = size;
                scaledHeight = (float) bitmap.getWidth() * ratio;
            }

            if(scaledHeight == -1 && scaledWidth == -1) {
                //scale on width even if they are same
                if(bitmap.getWidth() <= bitmap.getHeight()) {
                    ratio = (float)  size / (float) bitmap.getWidth();
                    scaledWidth = size;
                    scaledHeight = (float) bitmap.getHeight() * ratio;
                } else if(bitmap.getWidth() > bitmap.getHeight()) {
                    //scale on height
                    ratio = (float) size / (float) bitmap.getHeight();
                    scaledHeight = size;
                    scaledWidth = (float) bitmap.getWidth() * ratio;
                }
            }

//            Log.d(TAG, "h: " + scaledHeight + "w: " + scaledWidth);
//            Log.d(TAG, "h: " + (int) scaledHeight + "w: " + (int) scaledWidth);
            return Bitmap.createScaledBitmap(bitmap, (int) scaledWidth, (int) scaledHeight, false);
        }

        //return old bitmap
        return bitmap;
    }
/*
    public static Bitmap getRoundedBitmap(int size, int color) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bmp);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(size/2, size/2, size/2, paint); //all right man :)
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bmp, 0, 0, paint);
            return bmp;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError o) {
            o.printStackTrace();
        }
        return null;
    }*/

    public static Bitmap getRoundedBitmapByResource(int pictureName, Activity mainActivityRef) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(mainActivityRef.getResources(), pictureName);
            return Common.getRoundedRectBitmap(bitmap, Common.PROFILE_PIC_CIRCLE_MASK_SIZE); //TODO add check on null bitmap and ret default-icon
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getRoundedBitmapByFile(String profilePicturePath, Bitmap defaultIcon) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(profilePicturePath, options);
            Bitmap roundedPic = Common.getRoundedRectBitmap(bitmap, Common.PROFILE_PIC_CIRCLE_MASK_SIZE);
            return roundedPic != null ? roundedPic : defaultIcon;
        } catch(Exception e) {
            Log.e(TAG, "cannot load profile picture - set the default one");
//            e.printStackTrace();
        }
        return defaultIcon;
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

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

/*    public static boolean isConnected(Context context) {
        return true;
    }
*/
}
