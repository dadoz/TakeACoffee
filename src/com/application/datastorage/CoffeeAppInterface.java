package com.application.datastorage;

import android.app.Activity;
import android.app.LoaderManager;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.ReviewCounter;
import com.application.models.User;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by davide on 08/10/14.
 */
public interface CoffeeAppInterface {

    //local
    public CoffeeMachine getCoffeeMachineById(String coffeeMachineId);

    public boolean setProfilePictureToUserOnReview(ImageView profilePicImageView,
                                                   String profilePicturePath, Bitmap defaultIcon,
                                                   String userId);

    public void setUsernameToUserOnReview(TextView textView,
                                          String username, String userId);

    public boolean setRegisteredUser(String userId, String profilePicturePath,
                                     String username);

    boolean restoreRegisteredUser(String userId, String profilePicturePath, String username);

    boolean updateRegisteredUser(String profilePicturePath, String username);

    boolean createRegisteredUser(String profilePicturePath, String username);

    public boolean registerLocalUser();


    public Review getReviewById(String coffeeMachineId, String reviewId);

//    public boolean addReviewByParams(String userId, String coffeeMachineId, String comment,
//                                     Common.ReviewStatusEnum status);

//    boolean addReviewByParams(String userId, String coffeeMachineId, String comment,
//                              Common.ReviewStatusEnum status) throws JSONException;

    public boolean removeReviewById(String coffeeMachineId, Review reviewObj);

    public boolean updateReviewById(String coffeeMachineId, String reviewId, String reviewCommentNew);

    /******USER*******/

    public User getUserByIdInListview(String userId);

    public User getUserById(String userId);


    public boolean getCoffeeMachineIcon(String pictureIconPath, ImageView pictureImageView);

    public String getReviewCounterToString(String coffeeMachineId,
                                                  Common.ReviewStatusEnum reviewStatus,
                                                  long toTimestamp);

    public void addUserOnLocalListByList(ArrayList<User> userList);

    boolean checkIsMe(String userId);

    void resetReviewListTemp();

    void addOnReviewCounterList(ReviewCounter reviewCounter);
}
