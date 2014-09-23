package com.application.dataRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.application.commons.BitmapCustomUtils;
import com.application.commons.Common;
import com.application.datastorage.DataStorageSingleton;
import com.application.models.ReviewCounter;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.User;
import com.application.takeacoffee.R;
import com.parse.ParseException;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by davide on 06/09/14.
 */
public class CoffeeAppLogic {
    private static final String TAG = "CoffeeAppLogic";
    private static DataStorageSingleton coffeeApp;
    private static Context context;

    public CoffeeAppLogic(Context ctx) {
        context = ctx;
        coffeeApp = DataStorageSingleton.getInstance(ctx);
    }

    //local
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

    public static void loadCoffeeMachineList(final DataStorageSingleton coffeeApp) throws ParseException {
        if(Common.isConnected(context)) {
            ParseDataRequest.getAllCoffeeMachines(coffeeApp);
        }
    }

    public static void loadCoffeeMachineListAsync(final DataStorageSingleton coffeeAppLocal) throws ParseException {
        ParseDataRequest.getAllCoffeeMachinesAsync(coffeeApp);

    }

    public void loadReviewListByCoffeeMachineId(final String coffeeMachineId) throws ParseException {
        if (Common.isConnected(context)) {
            ParseDataRequest.getAllReviewsByCoffeeMachineIdToday(coffeeApp, coffeeMachineId);
        }
    }

    public void loadReviewListByCoffeeMachineIdAsync(final String coffeeMachineId) throws ParseException {
        ParseDataRequest.getAllReviewsByCoffeeMachineIdAsync(coffeeApp, coffeeMachineId);

    }

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
                    coffeeApp.getImageLoader(), R.drawable.user_icon);
        }
        return true;
    }

    public void setUsernameToUserOnReview(TextView textView,
                                                 String username, String userId) {
        if(coffeeApp.isRegisteredUser() && coffeeApp.checkIsMe(userId)) {
            textView.setText("Me");
            return;
        }
        textView.setText(username);
    }

    public boolean setRegisteredUser(String userId, String profilePicturePath,
                                     String username) {
        if(userId.compareTo(Common.EMPTY_VALUE) != 0) {
            return restoreRegisteredUser(userId, profilePicturePath, username);
        }
        return coffeeApp.isRegisteredUser() ? updateRegisteredUser(profilePicturePath, username) : createRegisteredUser(profilePicturePath, username);
    }

    private boolean restoreRegisteredUser(String userId, String profilePicturePath, String username) {
        coffeeApp.assignRegisteredUser(new User(userId, profilePicturePath, username));
        return true;
    }

    private boolean updateRegisteredUser(String profilePicturePath, String username) {
        SharedPreferences sharedPref = coffeeApp.getSharedPref();
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
                coffeeApp.getSharedPref().edit().putString(Common.SHAREDPREF_REGISTERED_USERNAME, username).commit();
            }
            return true;
        }

        Log.e(TAG, "Error cannot update user - no internet connection");
        return false;
    }

    private boolean createRegisteredUser(String profilePicturePath, String username) {
        SharedPreferences sharedPref = coffeeApp.getSharedPref();

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

    public ArrayList<Review> getReviewListByStatus(String coffeeMachineId,
                                                   Common.ReviewStatusEnum reviewStatus) {
        if(coffeeMachineId.compareTo(Common.EMPTY_VALUE) != 0) {
            ArrayList<Review> reviewList = coffeeApp.getReviewListMap().get(coffeeMachineId);
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
                return reviewListSortedByStatus.size() != 0 ? reviewListSortedByStatus : null;

/*                if(reviewListSortedByStatus.size() == 0) {
                    return null;
                }
                return reviewListSortedByStatus;*/
            }
            return reviewList; //TODO refactor it
        }
        Log.e(TAG, "error - no coffeeMachineId found");
        return null;
    }

    public ArrayList<Review> getReviewListByTimestamp(String coffeeMachineId,
                                                      Common.ReviewStatusEnum reviewStatus,
                                                      long fromTimestamp, long toTimestamp) {
        ArrayList<Review> reviewListSortedByStatus = new ArrayList<Review>();

        if(coffeeMachineId.compareTo(Common.EMPTY_VALUE) != 0) {
            //check if coffeeMachineId exist -
            ArrayList<Review> reviewList = coffeeApp.getReviewListMap().get(coffeeMachineId);

            if(reviewList == null || reviewList.size() == 0) {
                Log.e(TAG,"error - no one coffeeMachine owned by this ID");
                return null;
            }

            if(reviewStatus != Common.ReviewStatusEnum.NOTSET) {
                //TODO to be refactored
                for(Review review : reviewList) {
                    if(toTimestamp != Common.DATE_NOT_SET) {
                        //TO timestamp set
                        if(reviewStatus == review.getStatus() &&
                                review.getTimestamp() > fromTimestamp &&
                                review.getTimestamp() < toTimestamp) {
                            reviewListSortedByStatus.add(review);
                        }
                    } else {
                        //TO timestamp NOT set
                        if(reviewStatus == review.getStatus() &&
                                review.getTimestamp() > fromTimestamp) {
                            reviewListSortedByStatus.add(review);
                        }
                    }

                }
                return reviewListSortedByStatus.size() != 0 ? reviewListSortedByStatus : null;
/*                if(reviewListSortedByStatus.size() == 0) {
                    return null;
                }
                return reviewListSortedByStatus;*/
            }
            return reviewList; //TODO think about it

        }
        Log.e(TAG, "error - no coffeeMachineId found");
        return null;
    }

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

    public boolean addReviewByParams(String userId, String coffeeMachineId, String comment,
                                     Common.ReviewStatusEnum status) {
        long timestamp = new Date().getTime();

        ParseDataRequest.addReviewByParams(userId, coffeeMachineId, comment,status, timestamp);
        return true;
    }
/*    public boolean addReviewByParams(String userId, String coffeeMachineId, String comment,
                                     Common.ReviewStatusEnum status) {
        DataRequestServices dataRequestServices = coffeeApp.getDataRequestServices();
        Review review;
        //test
        String timestamp = new Date().getTime();
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
/*        updateLocalReviewMap(coffeeMachineId, review);
        return false;
    }*/

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


/*    public void loadReviewListByCoffeeMachineId(String coffeeMachineId) {
        ArrayList<Review> reviewArrayList;
        //connection
        if(! Common.isConnected(context)) {
//        if(false) {
            /**///reviewArrayList = dataRequestDb.getReviewListById(coffeeMachineId); //DB
/*            reviewArrayList = null;
            //update local reviewListMap
            if(reviewArrayList != null) {
                coffeeApp.getReviewListMap().put(coffeeMachineId, reviewArrayList);
            }
            return;
        }

        reviewArrayList = coffeeApp.getDataRequestServices().getReviewListById(coffeeMachineId); //HTTP TODO refactor this
        if(reviewArrayList != null) {
            //clean all review from db and then add data got from HTTP
            /**///dataRequestDb.removeAllReviewByCoffeeMachineId(coffeeMachineId); //wrong
            /**///dataRequestDb.addAllReview(reviewArrayList); //DB
        /*}

        //TEST OVERRIDE VALUES
//        reviewArrayList = dataRequestDb.getReviewListById(coffeeMachineId); //DB
        //update local reviewListMap
/*        if(reviewArrayList != null) {
            //clean reviewListMap
            coffeeApp.getReviewListMap().put(coffeeMachineId, reviewArrayList);
        }


    }
*/
    private boolean updateLocalReviewMap(String coffeeMachineId, Review review) {
        ArrayList<Review> reviewArrayList = coffeeApp.getReviewListMap().get(coffeeMachineId);
        if (reviewArrayList != null) {
            reviewArrayList.add(review);
            return true;
        }

        reviewArrayList = new ArrayList<Review>();
        reviewArrayList.add(review);
        coffeeApp.getReviewListMap().put(coffeeMachineId, reviewArrayList);
        return true;
    }

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


    public void getUserByIdAsync(String userId, TextView usernameTextView, ImageView profilePicImageView, ImageLoader imageLoader, int userIcon) {
        if(coffeeApp.checkIsMe(userId)) {
            BitmapCustomUtils.setImageByPath(coffeeApp.getRegisteredProfilePicturePath(), profilePicImageView);
            usernameTextView.setText(coffeeApp.getRegisteredUsername());
            coffeeApp.getRegisteredUser();
            return;
        }

        //try to find out user on HTTP server
        if(Common.isConnected(context)) {
            ParseDataRequest.getUserByIdAsync(userId, usernameTextView, profilePicImageView, imageLoader, userIcon); // make it async
        }
//        return new User(Common.NOT_VALID_USER_ID, null, "Guest");
    }

    public boolean getCoffeeMachineIcon(String pictureIconPath, ImageView pictureImageView) {
        if(Common.isConnected(context)) {
            DataRequestVolleyService.downloadProfilePicture(pictureIconPath, pictureImageView,
                    coffeeApp.getImageLoader(), R.drawable.coffee_cup_icon);
            return true;
        }
        return false;
    }

    public void addUserToAllReviewsOnLocalList(String coffeeMachineId) {
        ArrayList<Review> reviewList = coffeeApp.getReviewListMap().get(coffeeMachineId);

        if(reviewList != null) {
            for(Review review : reviewList) {
                if(coffeeApp.getUserById(review.getUserId()) == null) {
                    User user = getUserById(review.getUserId());
                    if(user != null) {
                        coffeeApp.addUserOnMapByParams(user.getId(), user);
                    }
                }
            }
        }
    }

    public static boolean checkAndSetRegisteredUser(Context ctx) {
//        SharedPreferences sharedPref = mainActivityRef.getSharedPreferences("SHARED_PREF_COFFEE_MACHINE", Context.MODE_PRIVATE);

        coffeeApp = DataStorageSingleton.getInstance(ctx);
        SharedPreferences sharedPref = coffeeApp.getSharedPref();
        CoffeeAppLogic coffeeAppLogic = new CoffeeAppLogic(ctx);

        if(sharedPref != null) {
            String username = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USERNAME, null);
            String profilePicPath = sharedPref.getString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, null);
            String userId = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USER_ID,
                    Common.EMPTY_VALUE);
            if(userId.compareTo(Common.EMPTY_VALUE) != 0) {
                Log.e(TAG, "this is my username: " + username);
                coffeeAppLogic.setRegisteredUser(userId, profilePicPath, username); //TODO check empty value
                return true;
            } else {
                Log.e(TAG, "no username set");
                return false;
            }
        }
        return false;
    }

    public int getPreviousReviewCounter(String coffeeMachineId, Common.ReviewStatusEnum reviewStatus, long toTimestamp) {
        try {
            ArrayList<ReviewCounter> reviewCounterList = coffeeApp.getReviewCounterList();
            for (ReviewCounter reviewCounter : reviewCounterList) {
                if(coffeeMachineId.compareTo(reviewCounter.getKey()) == 0) {
                    return reviewCounter.getCounterByParams(toTimestamp, reviewStatus);

                }
            }
        } catch (Exception e) {
            Log.d(TAG, "error on prev rev counter");
        }
        return -1;
    }

    public int getReviewCounter(String coffeeMachineId, Common.ReviewStatusEnum reviewStatus, long toTimestamp) {
        try {
            ArrayList<ReviewCounter> reviewCounterList = coffeeApp.getReviewCounterList();
            for (ReviewCounter reviewCounter : reviewCounterList) {
                if(coffeeMachineId.compareTo(reviewCounter.getKey()) == 0) {
                    return reviewCounter.getCounterByParams(toTimestamp, reviewStatus);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "error on rev counter");
        }
        return -1;
    }

    public void addUserOnLocalListByList(ArrayList<User> userList) {
        coffeeApp.addUserOnMapByList(userList);

    }

/*
    public int getPreviousReviewListCountOld(String coffeeMachineId, long fromTimestamp, long toTimestamp) {
        //try to find out user on HTTP server
        if(Common.isConnected(context)) {
            return ParseDataRequest.getReviewCountByCoffeeMachineId(coffeeMachineId,
                    fromTimestamp, toTimestamp);
        }

        return 0;
    }
    public int getPreviousReviewListCount(String coffeeMachineId, Common.ReviewStatusEnum reviewStatus) {
        String prevRevKey = coffeeApp.getPrevRevCounterKey(coffeeMachineId, reviewStatus);
        ArrayList<ReviewCounter> prevReviewCounterList = coffeeApp.getPrevReviewCounterList();
        for(ReviewCounter prevReviewCounter : prevReviewCounterList) {
            if(prevReviewCounter.getKey().compareTo(prevRevKey) == 0) {
                Log.e(TAG, "counter prev " + prevReviewCounter.getCounter() + " key " + prevRevKey);
                return prevReviewCounter.getCounter();
            }
        }
        return -1;
    }


    public void setAndCountPreviousReviewList(DataStorageSingleton coffeeApp, String coffeeMachineId,
                                           long fromTimestamp, long toTimestamp) {
        //try to find out user on HTTP server
/*        if(Common.isConnected(context)) {
            for(Common.ReviewStatusEnum status : Common.ReviewStatusEnum.values()) {
                if(status != Common.ReviewStatusEnum.NOTSET) {
                    ParseDataRequest.setReviewCountByCoffeeMachineId(coffeeApp, status, coffeeMachineId,
                            fromTimestamp, toTimestamp);
                }
            }
        }*/
/*        if(Common.isConnected(context)) {
//            ParseDataRequest.setReviewCountByCoffeeMachineId(coffeeApp, coffeeMachineId, fromTimestamp, toTimestamp);
            ParseDataRequest.setAndCountReviewByCoffeeMachineId(coffeeApp, coffeeMachineId,
                    fromTimestamp, toTimestamp);

        }

    }
*/

/*    public int getReviewCounterByTimestampLocal(String coffeeMachineId, Common.ReviewStatusEnum reviewStatus,
                                           long fromTimestamp, long toTimestamp) {

        int counter = 0;
        if(coffeeMachineId.compareTo(Common.EMPTY_VALUE) != 0) {
            //check if coffeeMachineId exist -
            ArrayList<Review> reviewList = coffeeApp.getReviewListMap().get(coffeeMachineId);

            if(reviewList == null || reviewList.size() == 0) {
                Log.e(TAG,"error - no one coffeeMachine owned by this ID");
                return -1;
            }

            if(reviewStatus != Common.ReviewStatusEnum.NOTSET) {
                //TODO to be refactored
                for(Review review : reviewList) {
                    if(toTimestamp != Common.DATE_NOT_SET) {
                        //TO timestamp set
                        if(reviewStatus == review.getStatus() &&
                                review.getTimestamp() > fromTimestamp &&
                                review.getTimestamp() < toTimestamp) {
                            counter ++;
                        }
                    } else {
                        //TO timestamp NOT set
                        if(reviewStatus == review.getStatus() &&
                                review.getTimestamp() > fromTimestamp) {
                            counter ++;
                        }
                    }

                }
            }
        }

//        Log.e(TAG, "error - no coffeeMachineId found");
        return counter;
    }
*/

    public static class TimestampHandler {

        public static long getYesterdayTimestamp(DateTime dateTime) {
            return dateTime.toDateTime().minusDays(1).withTimeAtStartOfDay().getMillis();
        }

        public static long getOneWeekAgoTimestamp(DateTime dateTime) {
            return dateTime.toDateTime().minusWeeks(1).withTimeAtStartOfDay().getMillis();
        }

        public static long getTodayTimestamp(DateTime dateTime) {
            return dateTime.toDateTime().withTimeAtStartOfDay().getMillis();
        }
    }
}
