package com.application.takeacoffee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.application.commons.Common;

import java.util.Random;

/**
 * Created by davide on 01/06/14.
 */
public class SplashScreenActivity extends Activity {
    private static final long SPLASH_TIMEOUT = 500;
    private static final String TAG = "SplashScreenActivity";
    static Activity mSplashActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_layout);
        mSplashActivity = this;

        Common.setCustomFont(getWindow().getDecorView(), getAssets());
        final int[] colorArray = {R.color.light_green, R.color.light_yellow_lemon, R.color.light_violet};
        final View mainView = getWindow().getDecorView();
        final int value = new Random().nextInt(Integer.MAX_VALUE) + 1;
        mainView.findViewById(R.id.splashScreenLayoutId)
                .setBackgroundColor(getResources().getColor(colorArray[value % 3]));

        //INIT server application
//        Parse.initialize(this, "61rFqlbDy0UWBfY56RcLdiJVB1EPe8ce1yUxdAEY",
//                "jHD6l2E72OG9Kul7ijtawZTkF9Zml0AwbeL0J7Ex");
/*
        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(String...params) {
                if(Common.isConnected(mSplashActivity.getApplicationContext())) {
                    CoffeeAppLogic.checkAndSetRegisteredUser(mSplashActivity.getApplicationContext());
                } else {
                    Log.e(TAG, "No internet connection!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(String data) {
                Intent intent = new Intent(SplashScreenActivity.this, CoffeeMachineActivity.class);
                startActivity(intent);
                finish();
            }
        }*/

        //TODO remove it
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, CoffeeMachineActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }

}