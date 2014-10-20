package com.application.dataRequest;

import android.widget.ImageView;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by davide on 06/09/14.
 */
public class DataRequestVolleyService {
    //TODO REFACTOR IT
/*    private final static String SERVER_URL = "http://192.168.130.112:3000";
    private final static String PROFILE_PICTURE_CONTAINER = "profile-picture-container";
    private static DataStorageSingleton coffeeApp;
    private static Context context;*/

    public static void downloadProfilePicture(String profilePicturePath, ImageView profilePicImageView,
                                              ImageLoader imageLoader, int defaultIconId) {
        imageLoader.get(profilePicturePath, ImageLoader.getImageListener(profilePicImageView, defaultIconId, defaultIconId));
    }

}
