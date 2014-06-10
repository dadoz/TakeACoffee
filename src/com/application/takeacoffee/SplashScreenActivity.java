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
public class SplashScreenActivity extends Activity{
    private static final long SPLASH_TIMEOUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_layout);
        Common.setCustomFont(getWindow().getDecorView(), getAssets());
        final int[] colorArray = {R.color.light_green, R.color.light_yellow_lemon, R.color.light_violet};
        View mainView = getWindow().getDecorView();
        int value = new Random().nextInt(Integer.MAX_VALUE) + 1;
        mainView.findViewById(R.id.splashScreenLayoutId)
                .setBackgroundColor(getResources().getColor(colorArray[value % 3]));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, CoffeeMachineActivity.class));
                finish();
            }
        }, SPLASH_TIMEOUT);
    }

}