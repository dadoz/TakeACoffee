package com.application.dataRequest;

import android.app.Application;
import android.app.LoaderManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.application.commons.BitmapCustomUtils;
import com.application.commons.Common;
import com.application.datastorage.CoffeeAppInterface;
import com.application.datastorage.DataStorageApplication;
import com.application.models.ReviewCounter;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.User;
import com.application.takeacoffee.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static com.application.dataRequest.RestLoaderRetrofit.HTTPAction.ADD_REVIEW_REQUEST;

/**
 * Created by davide on 06/09/14.
 */
public class CoffeeAppController implements CoffeeAppInterface, ImageLoader.ImageCache {
    private static final String TAG = "CoffeeAppLogic";
    private final LoaderManager loaderManager;
    private DataStorageApplication coffeeApp;
    private Context context;

    private final SharedPreferences sharedPref;

    //Volley lib
    private RequestQueue requestQueue;
    private final ImageLoader imageLoader;

    private final LruCache<String, Bitmap> cache = new LruCache<>(20);
    private boolean localUser;


    public CoffeeAppController(Context ctx, Application coffeeApp, LoaderManager loaderManager) {
//        this.coffeeApp = (DataStorageApplication) ((FragmentActivity) ctx).getApplication();
        context = ctx;
        this.coffeeApp = (DataStorageApplication) coffeeApp;
        //volley
        this.requestQueue = getRequestQueue();
        this.imageLoader = new ImageLoader(requestQueue, this);

        this.loaderManager = loaderManager;
        //Creating an internal dir
//        File customDir = context.getApplicationContext()
//                .getDir(Common.COFFEE_MACHINE_DIR, Context.MODE_PRIVATE);

        //shared
        sharedPref = context.getApplicationContext()
                .getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);

    }

    //local
    @Override
    public CoffeeMachine getCoffeeMachineById(String coffeeMachineId) {
        if(coffeeApp.isCoffeeMachineListNotNull()) {
            for(CoffeeMachine obj: coffeeApp.getCoffeeMachineList()) {
                if(obj.getId().compareTo(coffeeMachineId) == 0) {
                    return obj;
                }
            }
        }
        return null;
    }

//    public static void loadCoffeeMachineList(final DataStorageApplication coffeeApp) throws ParseException {
//        if(Common.isConnected(context)) {
//            ParseDataRequest.getAllCoffeeMachines(coffeeApp);
//        }
//    }
//
//    public static void loadCoffeeMachineListAsync(final DataStorageApplication coffeeAppLocal) throws ParseException {
//        ParseDataRequest.getAllCoffeeMachinesAsync(coffeeApp);
//
//    }
//
//    public void loadReviewListByCoffeeMachineId(final String coffeeMachineId) throws ParseException {
//        if (Common.isConnected(context)) {
//            ParseDataRequest.getAllReviewsByCoffeeMachineIdToday(coffeeApp, coffeeMachineId);
//        }
//    }
//
//    public void loadReviewListByCoffeeMachineIdAsync(final String coffeeMachineId) throws ParseException {
//        ParseDataRequest.getAllReviewsByCoffeeMachineIdAsync(coffeeApp, coffeeMachineId);
//
//    }

    @Override
    public boolean setProfilePictureToUserOnReview(ImageView profilePicImageView,
                                                   String profilePicturePath, Bitmap defaultIcon,
                                                   String userId) {
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
        if(Common.isConnected(context)) {
            DataRequestVolleyService.downloadProfilePicture(profilePicturePath, profilePicImageView,
                    this.getImageLoader(), R.drawable.user_icon);
        }
        return true;
    }

    @Override
    public void setUsernameToUserOnReview(TextView textView,
                                                 String username, String userId) {
        if(coffeeApp.isRegisteredUser() && coffeeApp.checkIsMe(userId)) {
            textView.setText("Me");
            return;
        }
        textView.setText(username);
    }

    @Override
    public boolean setRegisteredUser(String userId, String profilePicturePath,
                                     String username) {
        if(userId.compareTo(Common.EMPTY_VALUE) != 0) {
            return restoreRegisteredUser(userId, profilePicturePath, username);
        }
        return coffeeApp.isRegisteredUser() ? updateRegisteredUser(profilePicturePath, username) : createRegisteredUser(profilePicturePath, username);
    }

    @Override
    public boolean restoreRegisteredUser(String userId, String profilePicturePath, String username) {
        coffeeApp.assignRegisteredUser(new User(userId, profilePicturePath, username));
        return true;
    }

    @Override
    public boolean updateRegisteredUser(String profilePicturePath, String username) {
        SharedPreferences sharedPref = this.getSharedPref();
        User registeredUser = coffeeApp.getRegisteredUser();


        if(Common.isConnected(context)) {
//            String onlineProfilePictureId = null;

            //UPLOAD PIC
            if(profilePicturePath != null) {
                ParseDataRequest.uploadProfilePicture(profilePicturePath, registeredUser.getId());
            }

            //UPDATE USER
/*            if(onlineProfilePictureId  != null) {
                ParseDataRequest.updateUserById(coffeeApp.getRegisteredUserId(), onlineProfilePictureId, username); //TODO INTERNAL SERVER ERROR
            }*/
            /**///dataRequestDb.updateUserById(getRegisteredUserId(), profilePicturePath, username);

            if(profilePicturePath != null) {
                registeredUser.setProfilePicturePath(profilePicturePath);
                sharedPref.edit().putString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, profilePicturePath).commit();
            }

            if(username != null) {
                registeredUser.setUsername(username);
                sharedPref.edit().putString(Common.SHAREDPREF_REGISTERED_USERNAME, username).commit();
            }
            return true;
        }

        Log.e(TAG, "Error cannot update user - no internet connection");
        return false;
    }

    @Override
    public boolean createRegisteredUser(String profilePicturePath, String username) {
        SharedPreferences sharedPref = this.getSharedPref();

        if(Common.isConnected(context)) {
//            String onlineProfilePictureId = null;
            //UPLOAD PIC

            String userId = ParseDataRequest.addUserByParams(coffeeApp, null, username);
            if(profilePicturePath != null) {
                ParseDataRequest.uploadProfilePicture(profilePicturePath, userId);
            }
            User rUser = new User(userId, profilePicturePath, username);
            rUser.setProfilePicturePath(profilePicturePath); //VERY VERY IMPORTANT
            coffeeApp.assignRegisteredUser(rUser); //HTTP add

//            rUser.setProfilePicturePath(profilePicturePath); //VERY VERY IMPORTANT
//            coffeeApp.assignRegisteredUser(rUser); //HTTP add
        } else {
            //create local user
            coffeeApp.assignRegisteredUser(new User(Common.LOCAL_USER_ID, profilePicturePath, username));
        }

        sharedPref.edit().putString(Common.SHAREDPREF_REGISTERED_USER_ID,
                coffeeApp.getRegisteredUser().getId()).commit();
        if(profilePicturePath != null) {
            sharedPref.edit().putString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, profilePicturePath).commit();
        }
        if(username != null) {
            sharedPref.edit().putString(Common.SHAREDPREF_REGISTERED_USERNAME, username).commit();
        }
        return true;

    }

    @Override
    public boolean registerLocalUser() {
        User registeredUser = coffeeApp.getRegisteredUser();

        if(coffeeApp.isLocalUser()) {
            if(Common.isConnected(context)) {

                String userId = ParseDataRequest.addUserByParams(coffeeApp, null,
                        registeredUser.getUsername());
                ParseDataRequest.uploadProfilePicture(registeredUser.getProfilePicturePath(), userId);

//                coffeeApp.assignRegisteredUser(user);
                registeredUser.setUserId(userId);
                return true;
            }
        }
        return false;
    }


    /**** REVIEW ****/

//    public ArrayList<Review> getReviewListByStatus(String coffeeMachineId,
//                                                   Common.ReviewStatusEnum reviewStatus) {
//        if(coffeeMachineId.compareTo(Common.EMPTY_VALUE) != 0) {
//            ArrayList<Review> reviewList = coffeeApp.getReviewListMap().get(coffeeMachineId);
//            if(reviewList == null || reviewList.size() == 0) {
//                Log.e(TAG, "error - no one coffeeMachine owned by this ID");
//                return null;
//            }
//
//            if(reviewStatus != Common.ReviewStatusEnum.NOTSET) {
//                ArrayList<Review> reviewListSortedByStatus = new ArrayList<Review>();
//                TODO to be refactored
//                for(Review review : reviewList) {
//                    if(reviewStatus == review.getStatus()) {
//                        reviewListSortedByStatus.add(review);
//                    }
//                }
//                return reviewListSortedByStatus.size() != 0 ? reviewListSortedByStatus : null;
//
///*                if(reviewListSortedByStatus.size() == 0) {
//                    return null;
//                }
//                return reviewListSortedByStatus;*/
//            }
//            return reviewList; //TODO refactor it
//        }
//        Log.e(TAG, "error - no coffeeMachineId found");
//        return null;
//    }
//
//    public ArrayList<Review> getReviewListByTimestamp(String coffeeMachineId,
//                                                      Common.ReviewStatusEnum reviewStatus,
//                                                      long fromTimestamp, long toTimestamp) {
//        ArrayList<Review> reviewListSortedByStatus = new ArrayList<Review>();
//
//        if(coffeeMachineId.compareTo(Common.EMPTY_VALUE) != 0) {
//            check if coffeeMachineId exist -
//            ArrayList<Review> reviewList = coffeeApp.getReviewListMap().get(coffeeMachineId);
//
//            if(reviewList == null || reviewList.size() == 0) {
//                Log.e(TAG,"error - no one coffeeMachine owned by this ID");
//                return null;
//            }
//
//            if(reviewStatus != Common.ReviewStatusEnum.NOTSET) {
//                TODO to be refactored
//                for(Review review : reviewList) {
//                    if(toTimestamp != Common.DATE_NOT_SET) {
//                        TO timestamp set
//                        if(reviewStatus == review.getStatus() &&
//                                review.getTimestamp() > fromTimestamp &&
//                                review.getTimestamp() < toTimestamp) {
//                            reviewListSortedByStatus.add(review);
//                        }
//                    } else {
//                        TO timestamp NOT set
//                        if(reviewStatus == review.getStatus() &&
//                                review.getTimestamp() > fromTimestamp) {
//                            reviewListSortedByStatus.add(review);
//                        }
//                    }
//
//                }
//                return reviewListSortedByStatus.size() != 0 ? reviewListSortedByStatus : null;
///*                if(reviewListSortedByStatus.size() == 0) {
//                    return null;
//                }
//                return reviewListSortedByStatus;*/
//            }
//            return reviewList; //TODO think about it
//
//        }
//        Log.e(TAG, "error - no coffeeMachineId found");
//        return null;
//    }

    @Override
    public Review getReviewById(String coffeeMachineId, String reviewId) {
        //TEST
        if(Common.isConnected(context)) {
            return ParseDataRequest.getReviewById(reviewId);
        }

        //else get from map or db
        //reviewListMap.get(coffeeMachineId);
        /**///return dataRequestDb.getReviewById(reviewId);
        return null;
    }

/*    @Override
    public boolean addReviewByParams(String userId, String coffeeMachineId, String comment,
                                     Common.ReviewStatusEnum status) throws JSONException {
        long timestamp = new Date().getTime();

        Bundle bundle = new Bundle();
        bundle.putString("ACTION", ADD_REVIEW_REQUEST);
        JSONObject params = new JSONObject();
        params.put("a", userId);
        params.put("a", coffeeMachineId);
        params.put("a", comment);
        params.put("a", status.name());
        bundle.putString("DATA", params.toString());

        loaderManager.initLoader(RestLoader.HTTPVerb.POST, bundle, (LoaderManager.LoaderCallbacks<Object>) context)
                .forceLoad();
//        ParseDataRequest.addReviewByParams(userId, coffeeMachineId, comment,status, timestamp);
        return true;
    }*/

    @Override
    public boolean removeReviewById(String coffeeMachineId, Review reviewObj) {

        ArrayList<Review> reviewArrayList = coffeeApp.getReviewListMap().get(coffeeMachineId);
        if(reviewArrayList == null) {
            return false;
        }

        reviewArrayList.remove(reviewObj);
        /**///dataRequestDb.removeReviewById(reviewObj.getId());
        if(Common.isConnected(context)) {
            ParseDataRequest.removeReviewById(reviewObj.getId());
            return true;
        }

        //TODO if cannot remove store all the local deletion
        //TODO cos you should delete them also on http server
        //saveDeletedReviewRows
        return true;
    }

//    private boolean updateLocalReviewMap(String coffeeMachineId, Review review) {
//        ArrayList<Review> reviewArrayList = coffeeApp.getReviewListMap().get(coffeeMachineId);
//        if (reviewArrayList != null) {
//            reviewArrayList.add(review);
//            return true;
//        }
//
//        reviewArrayList = new ArrayList<Review>();
//        reviewArrayList.add(review);
//        coffeeApp.getReviewListMap().put(coffeeMachineId, reviewArrayList);
//        return true;
//    }

    @Override
    public boolean updateReviewById(String coffeeMachineId, String reviewId, String reviewCommentNew) {

        if(Common.isConnected(context)) {
            if(ParseDataRequest.updateReviewById(reviewId, reviewCommentNew)) {
                //update review also on local db
                ArrayList<Review> reviewArrayList = coffeeApp.getReviewListMap().get(coffeeMachineId);
                for(Review review : reviewArrayList) {
                    if(review.getId() == (reviewId)) {
                        review.setComment(reviewCommentNew);
                        return true;
                    }
                }
            }
        }
        Log.e(TAG, "Cannot update review - you must have internet connection");
        return false;
    }

    /******USER*******/

    @Override
    public User getUserByIdInListview(String userId) {

        if(coffeeApp.checkIsMe(userId)) {
            return coffeeApp.getRegisteredUser();
        }

        User user = coffeeApp.getUserById(userId);
        if(user != null) {
            return user;
        }

        return new User(Common.NOT_VALID_USER_ID, null, "Guest");
//        return new User(userId, null, "Tunnus");
    }

    @Override
    public User getUserById(String userId) {

        if(! coffeeApp.checkIsMe(userId)) {
            User user = coffeeApp.getUserById(userId);
            if(user == null) {
                //not in local list
                if(Common.isConnected(context)) {
                    user = ParseDataRequest.getUserById(userId); // make it async
                    if(user != null) {
                        return user;
                    }
                    //else do nothing - no user found even on db
                    Log.e(TAG, "not user found even on db :(");
                }
            }
        }
       return null;
    }


//    public void getUserByIdAsync(String userId, TextView usernameTextView, ImageView profilePicImageView, ImageLoader imageLoader, int userIcon) {
//        if(coffeeApp.checkIsMe(userId)) {
//            BitmapCustomUtils.setImageByPath(coffeeApp.getRegisteredProfilePicturePath(), profilePicImageView);
//            usernameTextView.setText(coffeeApp.getRegisteredUsername());
//            coffeeApp.getRegisteredUser();
//            return;
//        }
//
//        try to find out user on HTTP server
//        if(Common.isConnected(context)) {
//            ParseDataRequest.getUserByIdAsync(userId, usernameTextView, profilePicImageView, imageLoader, userIcon); // make it async
//        }
//        return new User(Common.NOT_VALID_USER_ID, null, "Guest");
//    }

    @Override
    public boolean getCoffeeMachineIcon(String pictureIconPath, ImageView pictureImageView) {
        if(Common.isConnected(context)) {
            DataRequestVolleyService.downloadProfilePicture(pictureIconPath, pictureImageView,
                    this.getImageLoader(), R.drawable.coffee_cup_icon);
            return true;
        }
        return false;
    }

//    public void addUserToAllReviewsOnLocalList(String coffeeMachineId) {
//        ArrayList<Review> reviewList = coffeeApp.getReviewListMap().get(coffeeMachineId);
//
//        if(reviewList != null) {
//            for(Review review : reviewList) {
//                if(coffeeApp.getUserById(review.getUserId()) == null) {
//                    User user = getUserById(review.getUserId());
//                    if(user != null) {
//                        coffeeApp.addUserOnMapByParams(user.getId(), user);
//                    }
//                }
//            }
//        }
//    }

//    public static boolean checkAndSetRegisteredUser(Context ctx) {
//        SharedPreferences sharedPref = mainActivityRef.getSharedPreferences("SHARED_PREF_COFFEE_MACHINE", Context.MODE_PRIVATE);

//        SharedPreferences sharedPref = coffeeApp.getSharedPref();
//        CoffeeAppLogic coffeeAppLogic = new CoffeeAppLogic(ctx);
//
//        if(sharedPref != null) {
//            String username = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USERNAME, null);
//            String profilePicPath = sharedPref.getString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, null);
//            String userId = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USER_ID,
//                    Common.EMPTY_VALUE);
//            if(userId.compareTo(Common.EMPTY_VALUE) != 0) {
//                Log.e(TAG, "this is my username: " + username);
//                coffeeAppLogic.setRegisteredUser(userId, profilePicPath, username); //TODO check empty value
//                return true;
//            } else {
//                Log.e(TAG, "no username set");
//                return false;
//            }
//        }
//        return false;
//    }
//
//    public int getPreviousReviewCounter(String coffeeMachineId, Common.ReviewStatusEnum reviewStatus, long toTimestamp) {
//        try {
//            ArrayList<ReviewCounter> reviewCounterList = coffeeApp.getReviewCounterList();
//            for (ReviewCounter reviewCounter : reviewCounterList) {
//                if(coffeeMachineId.compareTo(reviewCounter.getKey()) == 0) {
//                    return reviewCounter.getCounterByParams(toTimestamp, reviewStatus);
//
//                }
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "error on prev rev counter");
//        }
//        return -1;
//    }
//
//    public int getReviewCounter(String coffeeMachineId,
//                                Common.ReviewStatusEnum reviewStatus,
//                                long toTimestamp) {
//        try {
//            ArrayList<ReviewCounter> reviewCounterList = coffeeApp.getReviewCounterList();
//            for (ReviewCounter reviewCounter : reviewCounterList) {
//                if(coffeeMachineId.compareTo(reviewCounter.getKey()) == 0) {
//                    return reviewCounter.getCounterByParams(toTimestamp, reviewStatus);
//                }
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "error on rev counter");
//        }
//        return -1;
//    }

    @Override
    public String getReviewCounterToString(String coffeeMachineId,
                                           Common.ReviewStatusEnum reviewStatus,
                                           long toTimestamp) {
        try {
            ArrayList<ReviewCounter> reviewCounterList = coffeeApp.getReviewCounterList();
            for (ReviewCounter reviewCounter : reviewCounterList) {
                if(coffeeMachineId.compareTo(reviewCounter.getKey()) == 0) {
                    int count = reviewCounter.getCounterByParams(toTimestamp, reviewStatus);
                    if(count != Common.REVIEW_COUNTER_ERROR) {
                        return String.valueOf(count);
                    }

                    Log.e(TAG, "getReviewCounterToString: review counter empty");
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "error on rev counter: " + e.getMessage());
        }
        return Common.EMPTY_REVIEW_STRING;
    }

    public void addUserOnLocalListByList(ArrayList<User> userList) {
        coffeeApp.addUserOnMapByList(userList);

    }

    @Override
    public boolean checkIsMe(String userId) {
        return coffeeApp.isRegisteredUser() &&
                coffeeApp.checkIsMe(userId);
    }

    @Override
    public void resetReviewListTemp() {
        coffeeApp.setReviewListTemp(null);

    }

    @Override
    public void addOnReviewCounterList(ReviewCounter reviewCounter) {
        coffeeApp.addOnReviewCounterList(reviewCounter);
    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    /*****VOLLEY lib*******/
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    //IMPLEMENTATION INTERFACE
    @Override
    public Bitmap getBitmap(String url) {
        return cache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        cache.put(url, bitmap);
    }

    //coffeeApp wrapper
    public ArrayList<CoffeeMachine> getCoffeeMachineList() {
        return coffeeApp.getCoffeeMachineList();
    }

    public String getRegisteredUsername() {
        return coffeeApp.getRegisteredUsername();
    }

    public String getRegisteredProfilePicturePath() {
        return coffeeApp.getRegisteredProfilePicturePath();
    }

    public void setCoffeeMachineList(ArrayList<CoffeeMachine> coffeeMachineList) {
        this.coffeeApp.setCoffeeMachineList(coffeeMachineList);
    }

    public boolean isLocalUser() {
        return coffeeApp.isLocalUser();
    }

    public boolean isRegisteredUser() {
        return coffeeApp.isRegisteredUser();
    }

    public String getRegisteredUserId() {
        return coffeeApp.getRegisteredUserId();
    }

    public String getProfilePicturePathTemp() {
        return coffeeApp.getProfilePicturePathTemp();
    }

    public void setProfilePicturePathTemp(String profilePicturePathTemp) {
        this.coffeeApp.setProfilePicturePathTemp(profilePicturePathTemp);
    }


}
