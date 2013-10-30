package com.application.takeacoffee;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: davide
 * Date: 10/30/13
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoffeMachineReviewsActivity extends Activity {
    public static String TAG = "CoffeMachineReviewsTAG";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reviews_layout);

        try{
            String data = this.getIntent().getExtras().getString("EXTRA_COFFE_MACHINE_ID");
            ArrayList<Review> reviewList = (ArrayList<Review>)this.getIntent().getSerializableExtra("EXTRA_REVIEW_DATA");


            Log.d(TAG,">>>>>>>data" + data);
        } catch (Exception e) {
            Log.e(TAG, "error - no coffeMachineId retrieved");
        }
    }
}