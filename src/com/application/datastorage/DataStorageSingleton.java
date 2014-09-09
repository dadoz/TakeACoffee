package com.application.datastorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.util.LruCache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppLogic;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.User;
import com.application.queries.ParseQueries;
import com.parse.ParseException;

import java.io.File;
import java.util.ArrayList;

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
    private final ImageLoader imageLoader;
    private User registeredUser;
    private static DataStorageSingleton mDataStorage;

    private static Context context;
    public String profilePicturePathTemp;
    private RequestQueue requestQueue;
    private boolean coffeeMachineListNotNull;
    private File customDir;

    //VERY IMPORTANT
    private DataStorageSingleton(Context ctx) {
        context = ctx;
        //TODO CHECK IT OUT
        File customDir = context.getApplicationContext()
                .getDir(Common.COFFEE_MACHINE_DIR, Context.MODE_PRIVATE); //Creating an internal dir;

        sharedPref = context.getApplicationContext().getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);

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

//        ParseQueries.parseQuery3(context.getAssets());
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

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    /*********COFFEE MACHINE fx***********/
    //local
    public ArrayList<CoffeeMachine> getCoffeeMachineList() {
        return coffeeMachineList;
    }

    public ArrayMap<String, ArrayList<Review>> getReviewListMap() {
        return reviewListMap;
    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
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
        mDataStorage = null;
    }

    public boolean isCoffeeMachineListNotNull() {
        return coffeeMachineList != null;
    }

    public File getCustomDir() {
        return customDir;
    }
}
