package com.application.dataRequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.commons.BitmapCustomUtils;
import com.application.commons.Common;
import com.application.datastorage.DataStorageSingleton;

/**
 * Created by davide on 06/09/14.
 */
public class CoffeeAppLogic {
    public static DataStorageSingleton coffeeApp;

    public static void CoffeeAppLogic() {

    }

    public static boolean setProfilePictureToUserOnReview(DataStorageSingleton singleton,
                                                   ImageView profilePicImageView,
                                                   String profilePicturePath, Bitmap defaultIcon,
                                                   long userId) {
        //TODO check out if I can put this one in a static constructor
        coffeeApp = singleton;

        if(profilePicturePath == null) {
            profilePicImageView.setImageBitmap(defaultIcon);
            return false;
        }

        //THIS RUN OUT MY FKING MEMORY :(
        if(coffeeApp.isRegisteredUser() && coffeeApp.checkIsMe(userId)) {
            //TODO store directly bitmap on object - this gonna store a lot of memory
            Bitmap bitmap = BitmapCustomUtils.getBitmapByFile(coffeeApp.getRegisteredProfilePicturePath(),
                    defaultIcon);
            profilePicImageView.setImageBitmap(bitmap);
            return true;
        }

        //TODO check if it's stored in my local storage - might be :D
        if(Common.isConnected(coffeeApp.getContextSingleton())) {
            DataRequestVolleyService.downloadProfilePicture(profilePicturePath, profilePicImageView,
                    coffeeApp.getImageLoader());
        }
        return true;
    }

    public static void setUsernameToUserOnReview(DataStorageSingleton singleton, TextView textView,
                                                 String username, long userId) {
        coffeeApp = singleton;

        if(coffeeApp.isRegisteredUser() && coffeeApp.checkIsMe(userId)) {
            textView.setText("Me");
            return;
        }
        textView.setText(username);
    }

}
