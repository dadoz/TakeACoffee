package com.application.takeacoffee;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppLogic;
import com.application.dataRequest.ParseDataRequest;
import com.application.datastorage.DataStorageSingleton;
import com.application.models.CoffeeMachine;
import com.google.android.gms.identity.intents.AddressConstants;
import com.parse.Parse;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by davide on 01/06/14.
 */
public class SplashScreenActivity extends Activity{
    private static final long SPLASH_TIMEOUT = 500;
    private static final String TAG = "SplashScreenActivity";
    static Activity mSplashActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_layout);
        Common.setCustomFont(getWindow().getDecorView(), getAssets());
        final int[] colorArray = {R.color.light_green, R.color.light_yellow_lemon, R.color.light_violet};
        final View mainView = getWindow().getDecorView();
        final int value = new Random().nextInt(Integer.MAX_VALUE) + 1;
        mainView.findViewById(R.id.splashScreenLayoutId)
                .setBackgroundColor(getResources().getColor(colorArray[value % 3]));

        mSplashActivity = this;
        //INIT server application
        Parse.initialize(this, "61rFqlbDy0UWBfY56RcLdiJVB1EPe8ce1yUxdAEY",
                "jHD6l2E72OG9Kul7ijtawZTkF9Zml0AwbeL0J7Ex");

        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(String...params) {
                if(Common.isConnected(mSplashActivity.getApplicationContext())) {
                    DataStorageSingleton coffeeApp = DataStorageSingleton.getInstance(mSplashActivity.getApplicationContext());
//                    CoffeeMachine coffeeMachineObj = coffeeApp.getCoffeeMachineList().get(0);
                    CoffeeAppLogic.checkAndSetRegisteredUser();
/*                        CoffeeAppLogic coffeeAppLogic = new CoffeeAppLogic(mSplashActivity.getApplicationContext());
                        try {
                            for(CoffeeMachine coffeeMachineObj : coffeeApp.getCoffeeMachineList()) {
                                coffeeAppLogic.loadReviewListByCoffeeMachineId(coffeeMachineObj.getId());
                                coffeeAppLogic.getUserToAllReviews(coffeeMachineObj.getId());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/
                } else {
                    Common.displayError(mSplashActivity.getApplicationContext(), "No internet connection!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(String data) {
                Intent intent = new Intent(SplashScreenActivity.this, CoffeeMachineActivity.class);
                startActivity(intent);
                finish();
            }
        }.execute("hey");

        Log.e(TAG, "thread 1-bis");

        //TODO remove it
/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, CoffeeMachineActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMEOUT);*/
    }

}