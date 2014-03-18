package com.application.takeacoffee.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.Review;
import com.application.takeacoffee.AddReviewActivity;
import com.application.takeacoffee.R;
import com.application.takeacoffee.ReviewsActivity;

import java.util.ArrayList;

/**
 * Created by davide on 3/16/14.
 */
public class ReviewsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.reviews_layout,container,false);
    }

/*    private void initView(){
        try{

            //store coffeMachineId
            String coffeMachineId = this.getIntent().getExtras().getString("EXTRA_COFFE_MACHINE_ID");

            coffeMachineApplication = ((CoffeeMachineDataStorageApplication)this.getApplication());
            coffeMachineApplication.coffeeMachineData.setCurrentCoffeMachineSelectedId(coffeMachineId);

            ArrayList<Review> reviewList = coffeMachineApplication.coffeeMachineData.getReviewListByCoffeMachineId(coffeMachineId);

            if(reviewList != null && reviewList.size() != 0) {
                //Log.d(TAG,">>>>>>>data" + reviewList.get(0).getFeedback() + "----" +reviewList.get(0).getUsername());
                for(Review reviewObj : reviewList) {
                    LayoutInflater inflater = (LayoutInflater)this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout layoutContainer = (LinearLayout)findViewById(R.id.reviewsContainerLayoutId);


                    View reviewTemplate = inflater.inflate(R.layout.review_template, null);
                    layoutContainer.addView(reviewTemplate);
                    //set data to template
                    ((TextView)reviewTemplate.findViewById(R.id.reviewUsernameTextId)).setText(reviewObj.getUsername());
                    ((TextView)reviewTemplate.findViewById(R.id.reviewDateTextId)).setText(reviewObj.getFormattedTimestamp());
                    ((TextView)reviewTemplate.findViewById(R.id.reviewCommentTextId)).setText(reviewObj.getComment());
                    if(reviewObj.getStatus() == Common.ReviewStatusEnum.AWFUL){
                        ((LinearLayout)reviewTemplate.findViewById(R.id.reviewStatusLabelLayoutId)).setBackgroundColor(getResources().getColor(R.color.light_black));
                    } else if(reviewObj.getStatus() == Common.ReviewStatusEnum.NOT_BAD){
                        ((LinearLayout)reviewTemplate.findViewById(R.id.reviewStatusLabelLayoutId)).setBackgroundColor(getResources().getColor(R.color.light_red));
                    } else if(reviewObj.getStatus() == Common.ReviewStatusEnum.GOOD) {
                        ((LinearLayout)reviewTemplate.findViewById(R.id.reviewStatusLabelLayoutId)).setBackgroundColor(getResources().getColor(R.color.light_green));
                    }
                }

            } else {
                Log.d(TAG, ">>>>>>> NO REVIEWs FOUND");
                LayoutInflater inflater = (LayoutInflater)this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layoutContainer = (LinearLayout)findViewById(R.id.reviewsContainerLayoutId);


                View emptyDataTemplate = inflater.inflate(R.layout.empty_data_layout, null);
                layoutContainer.addView(emptyDataTemplate);

                ((TextView)emptyDataTemplate.findViewById(R.id.emptyDataTextViewId)).setText("No reviews for this machine");
            }

        } catch (Exception e) {
            Log.e(TAG, "error - no coffeMachineId retrieved - " + e.getMessage());
        }

        Button addReviewBtn = (Button)findViewById(R.id.addReviewButtonId);
        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if(!coffeMachineApplication.coffeeMachineData.getRegisteredUserStatus()){
                    Log.e(TAG, ">>> please insert username at least");

                    DialogFragment dialog = new RegisterUserDialogFragment();
                    dialog.show(getFragmentManager(), TAG);
                } else {
                    Intent intent = new Intent(ReviewsActivity.this,AddReviewActivity.class);
                    startActivityForResult(intent, ADD_REVIEW_RESULT);
                }
            }
        });

    }
*/

}

