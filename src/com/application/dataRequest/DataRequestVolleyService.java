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
    private final static String SERVER_URL = "http://192.168.130.112:3000";
    private final static String PROFILE_PICTURE_CONTAINER = "profile-picture-container";
    private static DataStorageSingleton coffeeApp;
    private static Context context;

    public static void downloadProfilePicture(String profilePicturePath, ImageView profilePicImageView,
                                              ImageLoader imageLoader, int defaultIconId) {

        String url = SERVER_URL + "/api/containers/" +
                PROFILE_PICTURE_CONTAINER + "/download/" + profilePicturePath;
        imageLoader.get(url, ImageLoader.getImageListener(profilePicImageView, defaultIconId, defaultIconId));

    }

}
