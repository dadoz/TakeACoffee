package com.application.datastorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.application.commons.BitmapCustomUtils;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppLogic;
import com.application.dataRequest.DataRequestServices;
import com.application.dataRequest.DataRequestVolleyService;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.User;
import com.application.takeacoffee.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: davide
 * Date: 10/31/13
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataStorageSingleton {
    private static final String TAG = "DataStorageSingleton";
    private static ArrayList<CoffeeMachine> coffeeMachineList;
    private static ArrayMap<Long, ArrayList<Review>> reviewListMap;
    private static SharedPreferences sharedPref;
    private static DataRequestServices dataRequestServices;
    private final ImageLoader imageLoader;
    private User registeredUser;
//    private static DataRequestDb dataRequestDb;
    private static DataStorageSingleton mDataStorage;

    private static Context context;
//    private TextView usernameToUserOnReview;
    public String profilePicturePathTemp;
    private RequestQueue requestQueue;

    //empty constructor
    //VERY IMPORTANT
    private DataStorageSingleton(Context ctx) {
        context = ctx;
        //TODO CHECK IT OUT
        File customDir = context.getApplicationContext()
                .getDir(Common.COFFEE_MACHINE_DIR, Context.MODE_PRIVATE); //Creating an internal dir;

//        context.deleteDatabase(Common.DATABASE_NAME);
        //          dataRequestDb = new DataRequestDb(context);
        dataRequestServices = new DataRequestServices(customDir);
        sharedPref = context.getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);

        reviewListMap = new ArrayMap<>();

        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(20);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

        //fill all coffee machine list
        if(loadCoffeeMachineList()) {
            for(CoffeeMachine coffeeMachine : coffeeMachineList) {
                loadReviewListByCoffeeMachineId(coffeeMachine.getId());
            }
        }
    }

    public static synchronized DataStorageSingleton getInstance(Context context) {
        //singleton instance
        if(mDataStorage == null) {
            mDataStorage = new DataStorageSingleton(context);
        }
        return mDataStorage;
    }

    public Context getContextSingleton() {
        return context;
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

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    /*********COFFEE MACHINE fx***********/
    //local
    public ArrayList<CoffeeMachine> getCoffeeMachineList() {
        return coffeeMachineList;
    }

    //local
    public CoffeeMachine getCoffeeMachineById(long coffeeMachineId) {
        if(coffeeMachineList != null) {
            for(CoffeeMachine obj: coffeeMachineList) {
                if(obj.getId() == coffeeMachineId) {
                    return obj;
                }
            }
        }
        return null;
    }

    //TODO refactor
/*  public ArrayList<Review> getReviewListByCoffeeMachineId(long coffeeMachineId) {

        return reviewListMap.get(coffeeMachineId);
        String reviewListId = null;
        CoffeeMachine coffeeMachineSelected = null;
        ArrayList<Review> reviewArrayList = null;
        for (CoffeeMachine obj : coffeeMachineList) {
            if (obj.getId() == coffeeMachineId) {
                coffeeMachineSelected = obj;
//                reviewListId = obj.getReviewListId();
            }
        }

        if(reviewListId == null || reviewListMap.get(reviewListId) == null) {
            reviewArrayList = dataRequestDb.getReviewListById(coffeeMachineId); //DB
            if(reviewArrayList == null) {
                reviewArrayList = DataRequestServices.getReviewListById(coffeeMachineId); //HTTP TODO refactor this
            }

//            reviewListId = getUniqueReviewListId();
//            coffeeMachineSelected.setReviewListId(reviewListId);
            reviewListMap.put(coffeeMachineId, reviewArrayList);
        }
        return reviewListMap.get(reviewListId);
    }*/

    public ArrayList<Review> getReviewListByStatus(long coffeeMachineId,
                                                  Common.ReviewStatusEnum reviewStatus) {
        if(coffeeMachineId != -1) {
            //check if coffeMachineId exist -
//            ArrayList<Review> reviewList = coffeeApp.
//                    getReviewListByCoffeeMachineId(coffeeMachineId);
            ArrayList<Review> reviewList = reviewListMap.get(coffeeMachineId);
            if(reviewList == null || reviewList.size() == 0) {
                Log.e(TAG, "error - no one coffeeMachine owned by this ID");
                return null;
            }

            if(reviewStatus != Common.ReviewStatusEnum.NOTSET) {
                ArrayList<Review> reviewListSortedByStatus = new ArrayList<Review>();
                //TODO to be refactored
                for(Review review : reviewList) {
                    if(reviewStatus == review.getStatus()) {
                        reviewListSortedByStatus.add(review);
                    }
                }
                if(reviewListSortedByStatus.size() == 0) {
                    return null;
                }
                return reviewListSortedByStatus;
            }
            return reviewList;

        }
        Log.e(TAG, "error - no coffeeMachineId found");
        return null;
    }

    public ArrayList<Review> getReviewListByTimestamp(long coffeeMachineId,
                                                             Common.ReviewStatusEnum reviewStatus,
                                                             long fromTimestamp, long toTimestamp) {
        if(coffeeMachineId != -1) {
            //check if coffeMachineId exist -
            ArrayList<Review> reviewList = reviewListMap.get(coffeeMachineId);

            if(reviewList == null || reviewList.size() == 0) {
                Log.e(TAG,"error - no one coffeeMachine owned by this ID");
                return null;
            }

            if(reviewStatus != Common.ReviewStatusEnum.NOTSET) {
                ArrayList<Review> reviewListSortedByStatus = new ArrayList<Review>();
                //TODO to be refactored
                for(Review review : reviewList) {
                    if(toTimestamp != Common.DATE_NOT_SET) {
                        if(reviewStatus == review.getStatus() &&
                                review.getTimestamp() > fromTimestamp &&
                                review.getTimestamp() < toTimestamp) {
                            reviewListSortedByStatus.add(review);
                        }
                    } else {
                        if(reviewStatus == review.getStatus() &&
                                review.getTimestamp() > fromTimestamp) {
                            reviewListSortedByStatus.add(review);
                        }
                    }

                }
                if(reviewListSortedByStatus.size() == 0) {
                    return null;
                }
                return reviewListSortedByStatus;
            }
            return reviewList;

        }
        Log.e(TAG, "error - no coffeMachineId found");
        return null;
    }

//    public String getUniqueReviewListId() {
//        return String.valueOf(UUID.randomUUID());
//    }

    public boolean addReviewByParams(long userId, long coffeeMachineId, String comment,
                                     Common.ReviewStatusEnum status) {
        Review review = null;
        //test
        long timestamp = new Date().getTime();
        //FIRST on HTTP req - then
        review = dataRequestServices.addReviewByParams(userId, coffeeMachineId, comment, status, timestamp);
        /**///dataRequestDb.addReviewByParams(review.getId(), userId, coffeeMachineId, comment, status, timestamp); //TODO TEST - this is gonna be replaced by DB full update


        //full update - slow but easy
//        ArrayList<Review> reviewArrayList = DataRequestServices.getReviewListById(coffeeMachineId); //with new data
//        dataRequestDb.cleanReview(reviewArrayList);
//        dataRequestDb.addAllReview(reviewArrayList);

        //other option - take out autoincrement on local db and set up id got from HTTP
        //dataRequestDb.addReviewByParams(ID, userId, coffeeMachineId, comment, status, timestamp);

        //local
        updateLocalReviewMap(coffeeMachineId, review);
        return false;
    }

    public boolean removeReviewById(long coffeeMachineId, Review reviewObj) {
        ArrayList<Review> reviewArrayList = reviewListMap.get(coffeeMachineId);
        if(reviewArrayList == null) {
            return false;
        }

        reviewArrayList.remove(reviewObj);
        /**///dataRequestDb.removeReviewById(reviewObj.getId());
        if(Common.isConnected(context)) {
            dataRequestServices.removeReviewById(reviewObj.getId());
            return true;
        }

        //TODO if cannot remove store all the local deletion
        //TODO cos you should delete them also on http server
        //saveDeletedReviewRows
        return true;
    }


    private static boolean loadCoffeeMachineList() {
        //wrong - u must check internet connnection - if there's get from HTTP otw get from DB
        /**///coffeeMachineList = dataRequestDb.getCoffeeMachineList(); //DB
        coffeeMachineList = null;
        if(coffeeMachineList  == null) {
            if(Common.isConnected(context)) {
                coffeeMachineList = dataRequestServices.getCoffeeMachineList(); //HTTP
                if(coffeeMachineList != null) {
                    /**///dataRequestDb.addAllCoffeeMachine(coffeeMachineList);
                    return true;
                }
                return false;
            }
        }
        return coffeeMachineList != null;
    }

    private static void loadReviewListByCoffeeMachineId(long coffeeMachineId) {
        ArrayList<Review> reviewArrayList;
        //connection
       if(! Common.isConnected(context)) {
//        if(false) {
            /**///reviewArrayList = dataRequestDb.getReviewListById(coffeeMachineId); //DB
           reviewArrayList = null;
            //update local reviewListMap
            if(reviewArrayList != null) {
                reviewListMap.put(coffeeMachineId, reviewArrayList);
            }
            return;
        }

        reviewArrayList = dataRequestServices.getReviewListById(coffeeMachineId); //HTTP TODO refactor this
        if(reviewArrayList != null) {
            //clean all review from db and then add data got from HTTP
            /**///dataRequestDb.removeAllReviewByCoffeeMachineId(coffeeMachineId); //wrong
            /**///dataRequestDb.addAllReview(reviewArrayList); //DB
        }

        //TEST OVERRIDE VALUES
//        reviewArrayList = dataRequestDb.getReviewListById(coffeeMachineId); //DB
        //update local reviewListMap
        if(reviewArrayList != null) {
            //clean reviewListMap
            reviewListMap.put(coffeeMachineId, reviewArrayList);
        }


    }

    private boolean updateLocalReviewMap(long coffeeMachineId, Review review) {
        ArrayList<Review> reviewArrayList = reviewListMap.get(coffeeMachineId);
        if(reviewArrayList != null) {
            reviewArrayList.add(review);
            return true;
        }

        reviewArrayList = new ArrayList<Review>();
        reviewArrayList.add(review);
        reviewListMap.put(coffeeMachineId, reviewArrayList);
        return true;



/*
        for(CoffeeMachine coffeeMachine : coffeeMachineList) {
            if(coffeeMachine.getId() == coffeeMachineId) {
                if(coffeeMachine.getReviewListId() != null) {
                    //already exist a review for this machine
                    ArrayList<Review> reviewArrayList = reviewListMap.get(coffeeMachine.getReviewListId());
                    reviewArrayList.add(review);
                    return true;
                }

                //doesnot exist review map for this coffeemachine
                String reviewListId = getUniqueReviewListId();
                coffeeMachine.setReviewListId(reviewListId);
                ArrayList<Review> reviewArrayList = new ArrayList<Review>();
                reviewArrayList.add(review);
                reviewListMap.put(reviewListId, reviewArrayList);
                return true;
            }
        }
        return false;*/
    }
    public Review getReviewById(long coffeeMachineId, long reviewId) {
        //TEST
        if(Common.isConnected(context)) {
            return dataRequestServices.getReviewById(reviewId);
        }

        //else get from map or db
        //reviewListMap.get(coffeeMachineId);
        /**///return dataRequestDb.getReviewById(reviewId);
        return null;
    }

    public boolean setRegisteredUser(long userId, String profilePicturePath, String username) {
        if(userId != Common.EMPTY_LONG_VALUE) {
            return restoreRegisteredUser(userId, profilePicturePath, username);
        }
        return registeredUser != null ? updateRegisteredUser(profilePicturePath, username) : createRegisteredUser(profilePicturePath, username);
    }

    private boolean restoreRegisteredUser(long userId, String profilePicturePath, String username) {
        registeredUser = new User(userId, profilePicturePath, username);

        return true;
    }

    private boolean updateRegisteredUser(String profilePicturePath, String username) {
        if(Common.isConnected(context)) {
            String onlineProfilePictureId = null;

            //UPLOAD PIC
            if(profilePicturePath != null) {
                onlineProfilePictureId = dataRequestServices.uploadProfilePicture(profilePicturePath);
            }

            //UPDATE USER
            if(onlineProfilePictureId  != null) {
                dataRequestServices.updateUserById(getRegisteredUserId(), onlineProfilePictureId, username); //TODO INTERNAL SERVER ERROR
            }
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

    private boolean createRegisteredUser(String profilePicturePath, String username) {
//        boolean connection = false;
        //connection
        if(Common.isConnected(context)) {
            String onlineProfilePictureId = null;
            //UPLOAD PIC
            if(profilePicturePath != null) {
                onlineProfilePictureId = dataRequestServices.uploadProfilePicture(profilePicturePath);

            }
//            registeredUser = new User(Common.LOCAL_USER_ID, profilePicturePath, username);

            registeredUser = dataRequestServices.addUserByParams(onlineProfilePictureId, username); //HTTP add
            /**///dataRequestDb.addUserByParams(registeredUser.getId(), profilePicturePath, username); //DB add to local db
            registeredUser.setProfilePicturePath(profilePicturePath); //VERY VERY IMPORTANT
        } else {
            //create local user
            registeredUser = new User(Common.LOCAL_USER_ID, profilePicturePath, username);
        }

        sharedPref.edit().putLong(Common.SHAREDPREF_REGISTERED_USER_ID, registeredUser.getId()).commit();
        if(profilePicturePath != null) {
            sharedPref.edit().putString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, profilePicturePath).commit();
        }
        if(username != null) {
            sharedPref.edit().putString(Common.SHAREDPREF_REGISTERED_USERNAME, username).commit();
        }
        return true;

    }

    public boolean isRegisteredUser() {
        return registeredUser != null;
    }

    public boolean isLocalUser() {
        return registeredUser != null &&
                registeredUser.getId() == Common.LOCAL_USER_ID;
    }

    public boolean registerLocalUser() {
        if(isLocalUser()) {
            if(Common.isConnected(context)) {

                String uploadedProfilePicturePath = dataRequestServices.uploadProfilePicture(registeredUser.getProfilePicturePath());
                User user = dataRequestServices.addUserByParams(uploadedProfilePicturePath,
                        registeredUser.getUsername());
                /**///dataRequestDb.addUserByParams(user.getId(), user.getProfilePicturePath(),
                        //user.getUsername());
                registeredUser = user;

                return true;
            }
        }
        return false;
    }

    public String getRegisteredProfilePicturePath() {
        return registeredUser.getProfilePicturePath();
    }

    public String getRegisteredUsername() {
        return registeredUser.getUsername();
    }

    public long getRegisteredUserId() {
        return registeredUser.getId();
    }

    public User getUserById(long userId) {
        /**///User user = dataRequestDb.getUserById(userId);
        User user = null;
        if(user != null) {
            return user;
        }

        //try to find out user on HTTP server
        if(Common.isConnected(context)) {
            user = dataRequestServices.getUserById(userId);
            if(user != null) {
                return user;
            }
        }
        return new User(-3, null, "Guest");
//        return new User(userId, null, "Tunnus");
    }



    public void destroy() {
        //call garbage collector to delete this class
        mDataStorage = null;
    }

    public boolean updateReviewById(long coffeeMachineId, long reviewId, String reviewCommentNew) {
        if(Common.isConnected(context)) {
            if(dataRequestServices.updateReviewById(reviewId, reviewCommentNew)) {
                //update review also on local db
                ArrayList<Review> reviewArrayList = reviewListMap.get(coffeeMachineId);
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
/*
    public boolean setProfilePictureToUserOnReview(ImageView profilePicImageView,
                                                String profilePicturePath, Bitmap defaultIcon, long userId) {
        if(profilePicturePath == null) {
            profilePicImageView.setImageBitmap(defaultIcon);
            return false;
        }

        //THIS RUN OUT MY FKING MEMORY :(
        if(isRegisteredUser() && this.checkIsMe(userId)) {
            //TODO store directly bitmap on object - this gonna store a lot of memory
            Bitmap bitmap = BitmapCustomUtils.getBitmapByFile(this.getRegisteredProfilePicturePath(),
                    defaultIcon);
            profilePicImageView.setImageBitmap(bitmap);
            return true;
        }

        //TODO check if it's stored in my local storage - might be :D
        if(Common.isConnected(context)) {
            DataRequestVolleyService.downloadProfilePicture(profilePicturePath, profilePicImageView,
                    imageLoader);
        }
        return true;
    }


    public void setUsernameToUserOnReview(TextView textView, String username, long userId) {
        if(this.isRegisteredUser() && this.checkIsMe(userId)) {
            textView.setText("Me");
            return;
        }
        textView.setText(username);

    }
*/
    public boolean checkIsMe(long userId) {
        return userId == this.getRegisteredUserId();
    }

    //TODO refactor it plez getReviewListById
/*    public ArrayList<Review> getReviewListByCoffeeMachineId(String coffeeMachineId){
        for(CoffeeMachine coffeeMachineObj : coffeeMachineList) {
            if(coffeeMachineObj.getId().equals(coffeeMachineId)) {
                return coffeeMachineObj.getReviewList();
            }
        }
        return null;
    }
    //TODO refactor it plez addReview
    public Review addReviewByCoffeeMachineId(String coffeeMachineId, String userId,
                                             String username, String reviewText,
                                             String profilePicturePath, Common.ReviewStatusEnum status) {
        Review review = null;
        for(CoffeeMachine coffeeMachineObj : coffeeMachineList) {
            if(coffeeMachineObj.getId().equals(coffeeMachineId)) {
//                String id = "fakeId"; //TODO generate id
                String id = new String(coffeeMachineId + username + new Date().getTime());
                Date date = new Date();
                review = new Review(id, userId, username, reviewText, status, profilePicturePath, date);
                coffeeMachineObj.addReviewObj(review);
            }
        }
        return review;
    }

    //TODO refactor it plez deleteReviewById
    public boolean removeReviewByCoffeeMachineId(String coffeeMachineId, Review reviewObj) {
        for(CoffeeMachine coffeeMachineObj : coffeeMachineList) {
            if(coffeeMachineObj.getId().equals(coffeeMachineId)) {
                coffeeMachineObj.getReviewList().remove(reviewObj);
            }
        }
        return false;
    }
*/

}
