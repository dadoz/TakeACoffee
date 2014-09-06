package com.application.commons;

import android.app.Activity;
import android.graphics.*;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by davide on 05/09/14.
 */
public class BitmapCustomUtils {
    private static final String TAG = "BitmapCustomUtils";

    //bmpBelow below - bmpAbove above
    public static Bitmap overlayBitmaps(Bitmap bmpBelow, Bitmap bmpAbove) {
        Bitmap bitmapOverlay = Bitmap.createBitmap(bmpBelow.getWidth(), bmpBelow.getHeight(), bmpBelow.getConfig());
        Canvas canvas = new Canvas(bitmapOverlay);
        canvas.drawBitmap(bmpBelow, new Matrix(), null);
        int floatImageSize = (bmpBelow.getWidth() - bmpAbove.getWidth()) / 2;
        canvas.drawBitmap(bmpAbove, floatImageSize, floatImageSize, null);
        return  bitmapOverlay;
    }

    public static String saveImageInStorage(Bitmap profileImage, File customDir, String filename) {
        if(profileImage == null) {
            return null;
        }

        if(filename == null) {
            filename = Common.PROFILE_PIC_FILE_NAME;
        }
        File profilePicFile = new File(customDir, filename); //Getting a file within the dir.
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
            return com.application.commons.BitmapCustomUtils.getRoundedRectBitmap(bitmap, Common.PROFILE_PIC_CIRCLE_MASK_SIZE); //TODO add check on null bitmap and ret default-icon
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapByFile(String profilePicturePath, Bitmap defaultIcon) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(profilePicturePath, options);
            return bitmap != null ? bitmap : defaultIcon;
        } catch(Exception e) {
            Log.e(TAG, "cannot load profile picture - set the default one " + e.getMessage());
        }
        return defaultIcon;
    }

    public static Bitmap getRoundedBitmapByFile(String profilePicturePath, Bitmap defaultIcon) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(profilePicturePath, options);
            Bitmap roundedPic = com.application.commons.BitmapCustomUtils.getRoundedRectBitmap(bitmap, Common.PROFILE_PIC_CIRCLE_MASK_SIZE);
            return roundedPic != null ? roundedPic : defaultIcon;
        } catch(Exception e) {
            Log.e(TAG, "cannot load profile picture - set the default one");
//            e.printStackTrace();
        }
        return defaultIcon;
    }

    public static Bitmap getRoundedBitmapByBitmap(Bitmap bmp, Bitmap defaultIcon) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap roundedPic = com.application.commons.BitmapCustomUtils.getRoundedRectBitmap(bmp, Common.PROFILE_PIC_CIRCLE_MASK_SIZE);
            return roundedPic != null ? roundedPic : defaultIcon;
        } catch(Exception e) {
            Log.e(TAG, "cannot load profile picture - set the default one");
//            e.printStackTrace();
        }
        return defaultIcon;
    }

}
