package com.application.takeacoffee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.application.commons.Common;

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                View mainView = getWindow().getDecorView();

                mainView.findViewById(R.id.splashScreenLayoutId)
                        .setBackgroundColor(getResources().getColor(R.color.light_yellow_lemon));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        View mainView = getWindow().getDecorView();

                        mainView.findViewById(R.id.splashScreenLayoutId)
                                .setBackgroundColor(getResources().getColor(R.color.light_violet));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(SplashScreenActivity.this, CoffeeMachineActivity.class));
                                finish();
                            }
                        }, SPLASH_TIMEOUT);
                    }
                }, SPLASH_TIMEOUT);
            }
        }, SPLASH_TIMEOUT);
    }

}
