package com.application.takeacoffee.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.view.Window;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.drawable.TextDrawable;
import com.application.models.Review;
import com.application.takeacoffee.R;

import java.util.ArrayList;

/**
 * Created by davide on 3/16/14.
 */
public class ReviewsFragment extends Fragment {
    private static final String TAG = "ReviewFragment";
    private CoffeeMachineDataStorageApplication coffeeMachineApplication;
    private ArrayList<Review> reviewList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

        //get data from application
        coffeeMachineApplication = ((CoffeeMachineDataStorageApplication) this.getActivity()
                .getApplication());

        //get args from fragment
        String coffeeMachineId = (String)this.getArguments().get(Common.COFFE_MACHINE_ID_KEY);
        boolean result = getReviewData(coffeeMachineId);
        Log.d(TAG,"this is the getReviewData " + result);
        if(!result) {
            View emptyView = inflater.inflate(R.layout.empty_data_layout, container, false);
            //set custom font
            Common.setCustomFont(emptyView, this.getActivity().getAssets());
            return emptyView;
        }

        //data are stored in reviewList
        View reviewsLayoutView = inflater.inflate(R.layout.reviews_layout, container, false);
        initView(reviewsLayoutView);

        //set custom font
        Common.setCustomFont(reviewsLayoutView, this.getActivity().getAssets());

        return reviewsLayoutView;
    }

    private boolean getReviewData(String coffeeMachineId) {
        if(coffeeMachineId != null) {
            //check if coffeMachineId exist - I DONT KNOW if its better to use this fx instead of impl method on list
            reviewList = coffeeMachineApplication.coffeeMachineData.
                    getReviewListByCoffeMachineId(coffeeMachineId);
            if(reviewList == null || reviewList.size() == 0) {
                Log.e(TAG,"error - no one coffeeMachine owned by this ID");
                return false;
            }
            return true;
        }
        Log.e(TAG, "error - no coffeMachineId found");
        return false;
    }

    private void initView(View reviewLayoutView) {
        ImageView goodReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.goodReviewButtonId);
        goodReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"clicked good");
                createReviewsListDialog();
            }
        });
        ImageView badReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.badReviewButtonId);
        badReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"clicked bad");
                createReviewsListDialog();
            }
        });
        ImageView worstReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.worstReviewButtonId);
        worstReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"clicked worst");
                createReviewsListDialog();
            }
        });


        worstReviewButton.setBackground(new TextDrawable("blalblalalablala", 0xff227700, 50));



/*        Button addReviewBtn = (Button)findViewById(R.id.addReviewButtonId);
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
*/
    }

    public void createReviewsListDialog() {
        final Dialog customDialog = new Dialog(getActivity());
        customDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialog_review_layout);
        setDataToReviewDialog(customDialog);
        View dialogView = customDialog.findViewById(R.id.dialogReviewLayoutId);
        Common.setCustomFont(dialogView,this.getActivity().getAssets());
        Log.e(TAG,"clicked worst");

        customDialog.show();
/*        Button dismissDialogButton = (Button)customDialog.findViewById(R.id.dismissDialogButtonId);
        dismissDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });*/
    }

    public void setDataToReviewDialog(Dialog customDialog) {
        try{
            for(Review reviewObj : reviewList) {

                LayoutInflater inflater = (LayoutInflater)this.getActivity().
                        getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layoutContainer = (LinearLayout)customDialog.findViewById(R.id.reviewsContainerLayoutId);

                View reviewTemplate = inflater.inflate(R.layout.review_template, null);
                layoutContainer.addView(reviewTemplate);
                //set data to template
                ((TextView)reviewTemplate.findViewById(R.id.reviewUsernameTextId)).setText(reviewObj.getUsername());
                ((TextView)reviewTemplate.findViewById(R.id.reviewDateTextId)).setText(reviewObj.getFormattedTimestamp());
                ((TextView)reviewTemplate.findViewById(R.id.reviewCommentTextId)).setText(reviewObj.getComment());
 /*               if(reviewObj.getStatus() == Common.ReviewStatusEnum.AWFUL){
                    (reviewTemplate.findViewById(R.id.reviewStatusLabelLayoutId)).setBackgroundColor(getResources().getColor(R.color.light_black));
                } else if(reviewObj.getStatus() == Common.ReviewStatusEnum.NOT_BAD){
                    (reviewTemplate.findViewById(R.id.reviewStatusLabelLayoutId)).setBackgroundColor(getResources().getColor(R.color.light_red));
                } else if(reviewObj.getStatus() == Common.ReviewStatusEnum.GOOD) {
                    (reviewTemplate.findViewById(R.id.reviewStatusLabelLayoutId)).setBackgroundColor(getResources().getColor(R.color.light_green));
                }*/
            }
        } catch (Exception e) {
            Log.e(TAG, "error - review list not available");
            e.printStackTrace();
        }


    }

}

