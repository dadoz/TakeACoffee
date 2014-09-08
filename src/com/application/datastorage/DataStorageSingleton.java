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
import com.application.queries.ParseQueries;
import com.application.takeacoffee.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private static ArrayMap<String, ArrayList<Review>> reviewListMap;
    private static SharedPreferences sharedPref;
    private static DataRequestServices dataRequestServices;
    private final ImageLoader imageLoader;
    private final CoffeeAppLogic coffeeAppLogic;
    private User registeredUser;
//    private static DataRequestDb dataRequestDb;
    private static DataStorageSingleton mDataStorage;

    private static Context context;
//    private TextView usernameToUserOnReview;
    public String profilePicturePathTemp;
    private RequestQueue requestQueue;
    private boolean coffeeMachineListNotNull;

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
        sharedPref = context.getApplicationContext().getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);

        reviewListMap = new ArrayMap<>();

        requestQueue = getRequestQueue();

        //coffeeAppLogic = new CoffeeAppLogic(ctx.getApplicationContext());
        coffeeAppLogic = null;

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
        try {
            CoffeeAppLogic.loadCoffeeMachineList(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
/*        if(loadCoffeeMachineList()) {
            for(CoffeeMachine coffeeMachine : coffeeMachineList) {
                loadReviewListByCoffeeMachineId(coffeeMachine.getId());
            }
        }*/
    }

    public static synchronized DataStorageSingleton getInstance(Context context) {
        //singleton instance
        if(mDataStorage == null) {
            mDataStorage = new DataStorageSingleton(context);
        }
        return mDataStorage;
    }

    public void setCoffeeMachineList(ArrayList<CoffeeMachine> list) {
        coffeeMachineList = list;
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

    /*
    private static boolean loadCoffeeMachineList() {
        //wrong - u must check internet connection - if there's get from HTTP otw get from DB
        /**///coffeeMachineList = dataRequestDb.getCoffeeMachineList(); //DB
/*        coffeeMachineList = null;
        if(coffeeMachineList  == null) {
            if(Common.isConnected(context)) {
                coffeeMachineList = dataRequestServices.getCoffeeMachineList(); //HTTP
                if(coffeeMachineList != null) {
                    /**///dataRequestDb.addAllCoffeeMachine(coffeeMachineList);
/*                    return true;
                }
                return false;
            }
        }
        return coffeeMachineList != null;
    }
*/
    public ArrayMap<String, ArrayList<Review>> getReviewListMap() {
        return reviewListMap;
    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    public DataRequestServices getDataRequestServices() {
        return dataRequestServices;
    }

    public Context getContextSingleton() {
        return context;
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

    public long getRegisteredUserId() {
        return registeredUser.getId();
    }

    public boolean checkIsMe(long userId) {
        return userId == this.getRegisteredUserId();
    }

    public void assignRegisteredUser(User registeredUser) {
        this.registeredUser = registeredUser;
    }

    public User getRegisteredUser() {
        return registeredUser;
    }

    public void destroy() {
        //call garbage collector to delete this class
        mDataStorage = null;
    }

    public CoffeeAppLogic getCoffeeAppLogic() {
        return coffeeAppLogic;
    }

    public boolean isCoffeeMachineListNotNull() {
        return coffeeMachineList != null;
    }

    //TODO refactor
/*  public ArrayList<Review> getReviewListByCoffeeMachineId(String coffeeMachineId) {

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



//    public String getUniqueReviewListId() {
//        return String.valueOf(UUID.randomUUID());
//    }


/*
    private boolean updateLocalReviewMap(String coffeeMachineId, Review review) {
        ArrayList<Review> reviewArrayList = reviewListMap.get(coffeeMachineId);
        if(reviewArrayList != null) {
            reviewArrayList.add(review);
            return true;
        }

        reviewArrayList = new ArrayList<Review>();
        reviewArrayList.add(review);
        reviewListMap.put(coffeeMachineId, reviewArrayList);
        return true;



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
        return false;
    }
*/



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
