package com.application.takeacoffee;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
            String coffeMachineId = this.getIntent().getExtras().getString("EXTRA_COFFE_MACHINE_ID");


            CoffeMachineDataStorageApplication coffeMachineApplication = ((CoffeMachineDataStorageApplication)this.getApplication());
            ArrayList<Review> reviewList = coffeMachineApplication.coffeMachineData.getReviewListByCoffeMachineId(coffeMachineId);

            if(reviewList != null && reviewList.size() != 0) {
                //Log.d(TAG,">>>>>>>data" + reviewList.get(0).getFeedback() + "----" +reviewList.get(0).getUsername());
                for(Review reviewObj : reviewList) {
                    LayoutInflater inflater = (LayoutInflater)this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout layoutContainer = (LinearLayout)findViewById(R.id.reviewsContainerLayoutId);


                    View reviewTemplate = inflater.inflate(R.layout.review_template, null);
                    layoutContainer.addView(reviewTemplate);
                    //set data to template
                    ((TextView)reviewTemplate.findViewById(R.id.reviewUsernameTextId)).setText(reviewObj.getUsername());
                    ((TextView)reviewTemplate.findViewById(R.id.reviewCommentTextId)).setText(reviewObj.getComment());
                }

            } else {
                Log.d(TAG,">>>>>>> NO REVIEWs FOUND");
            }

        } catch (Exception e) {
            Log.e(TAG, "error - no coffeMachineId retrieved - " + e.getMessage());
        }
    }
}