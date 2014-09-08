package com.application.dataRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.commons.BitmapCustomUtils;
import com.application.commons.Common;
import com.application.datastorage.DataStorageSingleton;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.User;
import com.application.takeacoffee.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static void loadCoffeeMachineList(final DataStorageSingleton coffeeAppLocal) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("coffee_machines");
        List<ParseObject> parseObjects = query.find();
        ArrayList<CoffeeMachine> list = new ArrayList<>();

        for (ParseObject parseObject : parseObjects) {
//                    String coffeeMachineId = parseObjects.get(i).getLong("id");
            String coffeeMachineId = parseObject.getObjectId();
            String name = parseObject.getString("name");
            String address = parseObject.getString("address");
            String iconPath = parseObject.getString("icon_path");
            list.add(new CoffeeMachine(coffeeMachineId, name, address, iconPath));
        }
        coffeeAppLocal.setCoffeeMachineList(list); //TODO mmmmmmm I'm not so sure it works -.-
        Log.d(TAG, "hey object id " + parseObjects.get(0).get("name"));
    }

    public static void loadCoffeeMachineListAsync(final DataStorageSingleton coffeeAppLocal) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("coffee_machines");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ArrayList<CoffeeMachine> list = new ArrayList<>();
                for (ParseObject parseObject : parseObjects) {

//                    String coffeeMachineId = parseObjects.get(i).getLong("id");
                    String coffeeMachineId = parseObject.getObjectId();
                    String name = parseObject.getString("name");
                    String address = parseObject.getString("address");
                    String iconPath = parseObject.getString("icon_path");
                    list.add(new CoffeeMachine(coffeeMachineId, name, address, iconPath));
                }
                coffeeAppLocal.setCoffeeMachineList(list); //TODO mmmmmmm I'm not so sure it works -.-
                Log.d(TAG, "hey object id " + parseObjects.get(0).get("name"));
            }
        });
    }
    public void loadReviewListByCoffeeMachineId(final String coffeeMachineIdLocal) throws ParseException{

        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("coffee_machine_id_string", coffeeMachineIdLocal);
        List<ParseObject> parseObjects = query.find();
        ArrayList<Review> reviewList = new ArrayList<Review>();
        for (ParseObject parseObject : parseObjects) {
            int id = -1;
            String comment = parseObject.getString("comment");
            String status = parseObject.getString("status");
            String timestamp = parseObject.getString("timestamp");
            int userId = parseObject.getInt("user_id");

            long timestampParsed = Long.parseLong(timestamp);
            Common.ReviewStatusEnum statusParsed = Review.parseStatus(status);
            reviewList.add(new Review(id, comment, statusParsed, timestampParsed, userId, coffeeMachineIdLocal));
        }

        coffeeApp.getReviewListMap().put(coffeeMachineIdLocal, reviewList);
    }


    public void loadReviewListByCoffeeMachineIdAsync(final String coffeeMachineIdLocal) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("coffee_machine_id_string", coffeeMachineIdLocal);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ArrayList<Review> reviewList = new ArrayList<>();
                for (ParseObject parseObject : parseObjects) {
                    int id = -1;
                    String comment = parseObject.getString("comment");
                    String status = parseObject.getString("status");
                    String timestamp = parseObject.getString("timestamp");
                    int userId = parseObject.getInt("user_id");

                    Long timestampParsed = Long.parseLong(timestamp);
                    Common.ReviewStatusEnum statusParsed = Review.parseStatus(status);
                    reviewList.add(new Review(id, comment, statusParsed, timestampParsed, userId, coffeeMachineIdLocal));
                }

                coffeeApp.getReviewListMap().put(coffeeMachineIdLocal, reviewList);
            }
        });
    }


    public boolean setProfilePictureToUserOnReview(ImageView profilePicImageView,
                                                   String profilePicturePath, Bitmap defaultIcon,
                                                   long userId) {
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
                                                 String username, long userId) {
        if(coffeeApp.isRegisteredUser() && coffeeApp.checkIsMe(userId)) {
            textView.setText("Me");
            return;
        }
        textView.setText(username);
    }

    public boolean setRegisteredUser(long userId, String profilePicturePath,
                                     String username) {
        if(userId != Common.EMPTY_LONG_VALUE) {
            return restoreRegisteredUser(userId, profilePicturePath, username);
        }
        return coffeeApp.isRegisteredUser() ? updateRegisteredUser(profilePicturePath, username) : createRegisteredUser(profilePicturePath, username);
    }

    private boolean restoreRegisteredUser(long userId, String profilePicturePath, String username) {
        coffeeApp.assignRegisteredUser(new User(userId, profilePicturePath, username));
        return true;
    }

    private boolean updateRegisteredUser(String profilePicturePath, String username) {
        DataRequestServices dataRequestServices = coffeeApp.getDataRequestServices();
        SharedPreferences sharedPref = coffeeApp.getSharedPref();
        User registeredUser = coffeeApp.getRegisteredUser();


        if(Common.isConnected(context)) {
            String onlineProfilePictureId = null;

            //UPLOAD PIC
            if(profilePicturePath != null) {
                onlineProfilePictureId = dataRequestServices.uploadProfilePicture(profilePicturePath);
            }

            //UPDATE USER
            if(onlineProfilePictureId  != null) {
                dataRequestServices.updateUserById(coffeeApp.getRegisteredUserId(), onlineProfilePictureId, username); //TODO INTERNAL SERVER ERROR
            }
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
        DataRequestServices dataRequestServices = coffeeApp.getDataRequestServices();

//        boolean connection = false;
        //connection
        if(Common.isConnected(context)) {
            String onlineProfilePictureId = null;
            //UPLOAD PIC
            if(profilePicturePath != null) {
                onlineProfilePictureId = dataRequestServices.uploadProfilePicture(profilePicturePath);

            }
//            registeredUser = new User(Common.LOCAL_USER_ID, profilePicturePath, username);

            User rUser = dataRequestServices.addUserByParams(onlineProfilePictureId, username);
            rUser.setProfilePicturePath(profilePicturePath); //VERY VERY IMPORTANT
            coffeeApp.assignRegisteredUser(rUser); //HTTP add
            /**///dataRequestDb.addUserByParams(registeredUser.getId(), profilePicturePath, username); //DB add to local db
        } else {
            //create local user
            coffeeApp.assignRegisteredUser(new User(Common.LOCAL_USER_ID, profilePicturePath, username));
        }

        sharedPref.edit().putLong(Common.SHAREDPREF_REGISTERED_USER_ID,
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
        DataRequestServices dataRequestServices = coffeeApp.getDataRequestServices();

        if(coffeeApp.isLocalUser()) {
            if(Common.isConnected(context)) {

                String uploadedProfilePicturePath = dataRequestServices.uploadProfilePicture(registeredUser.getProfilePicturePath());
                User user = dataRequestServices.addUserByParams(uploadedProfilePicturePath,
                        registeredUser.getUsername());
                /**///dataRequestDb.addUserByParams(user.getId(), user.getProfilePicturePath(),
                //user.getUsername());
                coffeeApp.assignRegisteredUser(user);
                return true;
            }
        }
        return false;
    }


    /**** REVIEW ****/

    public ArrayList<Review> getReviewListByStatus(String coffeeMachineId,
                                                   Common.ReviewStatusEnum reviewStatus) {
        if(coffeeMachineId.compareTo(Common.EMPTY_VALUE) != 0) {
            //check if coffeMachineId exist -
//            ArrayList<Review> reviewList = coffeeApp.
//                    getReviewListByCoffeeMachineId(coffeeMachineId);
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

    public ArrayList<Review> getReviewListByTimestamp(String coffeeMachineId,
                                                      Common.ReviewStatusEnum reviewStatus,
                                                      long fromTimestamp, long toTimestamp) {
        if(coffeeMachineId.compareTo(Common.EMPTY_VALUE) != 0) {
            //check if coffeeMachineId exist -
            ArrayList<Review> reviewList = coffeeApp.getReviewListMap().get(coffeeMachineId);

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
        Log.e(TAG, "error - no coffeeMachineId found");
        return null;
    }

    public Review getReviewById(String coffeeMachineId, long reviewId) {
        DataRequestServices dataRequestServices = coffeeApp.getDataRequestServices();
        //TEST
        if(Common.isConnected(context)) {
            return dataRequestServices.getReviewById(reviewId);
        }

        //else get from map or db
        //reviewListMap.get(coffeeMachineId);
        /**///return dataRequestDb.getReviewById(reviewId);
        return null;
    }

    public boolean addReviewByParams(long userId, String coffeeMachineId, String comment,
                                     Common.ReviewStatusEnum status) {
        return true;
    }
/*    public boolean addReviewByParams(long userId, String coffeeMachineId, String comment,
                                     Common.ReviewStatusEnum status) {
        DataRequestServices dataRequestServices = coffeeApp.getDataRequestServices();
        Review review;
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
/*        updateLocalReviewMap(coffeeMachineId, review);
        return false;
    }*/

    public boolean removeReviewById(String coffeeMachineId, Review reviewObj) {
        DataRequestServices dataRequestServices = coffeeApp.getDataRequestServices();

        ArrayList<Review> reviewArrayList = coffeeApp.getReviewListMap().get(coffeeMachineId);
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

    public boolean updateReviewById(String coffeeMachineId, long reviewId, String reviewCommentNew) {
        DataRequestServices dataRequestServices = coffeeApp.getDataRequestServices();

        if(Common.isConnected(context)) {
            if(dataRequestServices.updateReviewById(reviewId, reviewCommentNew)) {
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

    public User getUserById(long userId) {
        DataRequestServices dataRequestServices = coffeeApp.getDataRequestServices();

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

    public boolean getCoffeeMachineIcon(String pictureName, ImageView pictureImageView) {
        if(Common.isConnected(context)) {
            DataRequestVolleyService.downloadProfilePicture(pictureName, pictureImageView,
                    coffeeApp.getImageLoader(), R.drawable.coffee_cup_icon);
            return true;
        }
        return false;
    }
}
