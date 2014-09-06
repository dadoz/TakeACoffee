package com.application.dataRequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.application.commons.BitmapCustomUtils;
import com.application.datastorage.DataStorageSingleton;
import com.application.takeacoffee.R;

/**
 * Created by davide on 06/09/14.
 */
public class DataRequestVolleyService {
    //TODO REFACTOR IT
    private final static String SERVER_URL = "http://192.168.137.94:3000";
    private final static String PROFILE_PICTURE_CONTAINER = "profile-picture-container";
    private static DataStorageSingleton coffeeApp;
    private static Context context;

    public static void downloadProfilePicture(String profilePicturePath, ImageView profilePicImageView,
                                              ImageLoader imageLoader) {
        String url = SERVER_URL + "/api/containers/" +
                PROFILE_PICTURE_CONTAINER + "/download/" + profilePicturePath;
        imageLoader.get(url, ImageLoader.getImageListener(profilePicImageView, R.drawable.user_icon, R.drawable.user_icon));

    }

    public static void uploadProfilePicture(DataStorageSingleton singleton) {
    }

    public void downloadProfilePictureVolley(Context context, String fileName,
                                             final ImageView mImageView,
                                             final Bitmap defaultIcon) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String PROFILE_PICTURE_CONTAINER = "profile-picture-container";
        String url = SERVER_URL + "/api/containers/" +
                PROFILE_PICTURE_CONTAINER + "/download/" + fileName;
        // Request a string response from the provided URL.
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
//                        Log.e(TAG, "hey " + response.toString());


//                        Bitmap bmp = BitmapFactory.decodeStream(response.isMutable());
                        Bitmap bmpRounded = BitmapCustomUtils.getRoundedBitmapByBitmap(response,
                                defaultIcon);
                        mImageView.setImageBitmap(bmpRounded);

                    }
                }, 0, 0, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mImageView.setImageBitmap(defaultIcon);
                    }
                });
        queue.add(imageRequest);
    }

}
