package com.application.datastorage;

import android.app.Application;
import com.application.commons.Common;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.ReviewCounter;
import com.application.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: davide
 * Date: 10/31/13
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataStorageApplication extends Application {
    private static final String TAG = "DataStorageApplication";
    private static ArrayList<CoffeeMachine> coffeeMachineList;
    private static Map<String, ArrayList<Review>> reviewListMap;
    private User registeredUser;
    private static Map<String, User> userListMap ;

    private String profilePicturePathTemp;
    private ArrayList<Review> reviewListTemp;
    private ArrayList<ReviewCounter> reviewCounterList;

    //VERY IMPORTANT
/*    private DataStorageSingleton() {
//        context = ctx;
        //TODO CHECK IT OUT
//        File customDir = context.getApplicationContext()
//                .getDir(Common.COFFEE_MACHINE_DIR, Context.MODE_PRIVATE); //Creating an internal dir;

//        sharedPref = context.getApplicationContext().getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);

        reviewListMap = new ArrayMap<>();
        userListMap = new ArrayMap<>();
        reviewCounterList = new ArrayList<>();
    }

    public static synchronized DataStorageSingleton getInstance(Context context) {
        //singleton instance
        if(mDataStorage == null) {
            mDataStorage = new DataStorageSingleton(context);
        }
        return mDataStorage;
    }
*/
    public void DataStorageApplication() {
        reviewListMap = new HashMap<>();
        userListMap = new HashMap<>();
        reviewCounterList = new ArrayList<>();
    }

    public void setCoffeeMachineList(ArrayList<CoffeeMachine> list) {
        coffeeMachineList = list;
    }

    /*********COFFEE MACHINE fx***********/
    //local
    public ArrayList<CoffeeMachine> getCoffeeMachineList() {
        return coffeeMachineList;
    }

    public Map<String, ArrayList<Review>> getReviewListMap() {
        return reviewListMap;
    }


    public boolean isRegisteredUser() {
        return registeredUser != null;
    }

    public boolean isLocalUser() {
        return registeredUser != null &&
                registeredUser.getId() == Common.LOCAL_USER_ID;
    }

    public String getRegisteredProfilePicturePath() {
        return registeredUser.getProfilePicturePath();
    }

    public String getRegisteredUsername() {
        return registeredUser.getUsername();
    }

    public String getRegisteredUserId() {
        return registeredUser.getId();
    }

    public boolean checkIsMe(String userId) {
        return userId.compareTo(this.getRegisteredUserId()) == 0;
    }

    public void assignRegisteredUser(User registeredUser) {
        this.registeredUser = registeredUser;
    }

    public User getRegisteredUser() {
        return registeredUser;
    }

    public void destroy() {
        //call garbage collector to delete this class
        //mDataStorage = null;

        //TODO clean all stuff inside here
    }

    public boolean isCoffeeMachineListNotNull() {
        return coffeeMachineList != null;
    }

//    public File getCustomDir() {
//        return customDir;
//    }

    public Map getUserList() {
        return userListMap;
    }

    public User getUserById(String userId) {
        if(userListMap == null) {
            userListMap = new HashMap<>();
        }
        return userListMap.get(userId); //it couldnt be null :D
    }
    public void addUserOnMapByParams(String userId, User user) {
        if(userListMap == null) {
            userListMap = new HashMap<>();
        }
        userListMap.put(userId, user);
    }

    public void addUserOnMapByList(ArrayList<User> userList) {
        if(userList == null) {
            return;
        }

        if(userListMap == null) {
            userListMap = new HashMap<>();
        }

        for(User user : userList) {
            userListMap.put(user.getId(), user);
        }
    }

    public ArrayList<ReviewCounter> getReviewCounterList() {
        return reviewCounterList;
    }

    public void addOnReviewCounterList(ReviewCounter reviewCounter) {
        if(reviewCounterList == null) {
            reviewCounterList = new ArrayList<>();
        }
        reviewCounterList.add(reviewCounter);
    }

    public void setReviewListTemp(ArrayList<Review> list) {
        reviewListTemp = list;
    }

    public ArrayList<Review> getReviewListTemp() {
        return reviewListTemp;
    }

    public void setProfilePicturePathTemp(String value) {
        profilePicturePathTemp = value;
    }

    public String getProfilePicturePathTemp() {
        return profilePicturePathTemp;
    }
}
