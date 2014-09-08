package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppLogic;
import com.application.datastorage.DataStorageSingleton;
import com.application.models.Review;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

import java.util.ArrayList;

/**
 * Created by davide on 30/04/14.
 */
public class EditReviewFragment extends Fragment{
    private static Activity mainActivityRef;
    private ArrayList<Review> reviewsList;
    private DataStorageSingleton coffeeApp;
    private View editReviewView;
    private static final String TAG = "EditReviewFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        coffeeApp = DataStorageSingleton.getInstance(mainActivityRef.getApplicationContext());
        editReviewView = inflater.inflate(R.layout.edit_review_template, container, false);

//        long reviewId = getArguments().getLong(Common.REVIEW_ID);
//        String coffeeMachineId = getArguments().getLong(Common.COFFE_MACHINE_ID_KEY);
//        reviewsList = coffeeApp.getReviewListByCoffeeMachineId(coffeeMachineId);
        String reviewStatus = (String) this.getArguments().get(Common.REVIEW_STATUS_KEY);
        long reviewId = getArguments().getLong(Common.REVIEW_ID);
        String coffeeMachineId = getArguments().getString(Common.COFFE_MACHINE_ID_KEY);

        setHeader();
        initView(coffeeMachineId, reviewId, reviewStatus); //test

        Common.setCustomFont(editReviewView, getActivity().getAssets());
        return editReviewView;
    }

/*    private Review getReviewById(ArrayList<Review> reviewsList, long reviewId) {
        for(Review review:reviewsList) {
            if(review.getId() == reviewId) {
                return review;
            }
        }
        return null;
    }*/

    public void setHeader() {
        CoffeeMachineActivity.setHeaderByFragmentId(3, getFragmentManager(), Common.EMPTY_VALUE);
    }

    private void initView(final String coffeeMachineId, long reviewId, String reviewStatus) {
        //User user = coffeeApp.coffeeMachineData.getRegisteredUser();
//        Common.ReviewStatusEnum reviewStatus = Common.ReviewStatusEnum.valueOf(reviewStatus);
        final CoffeeAppLogic coffeeAppLogic = new CoffeeAppLogic(mainActivityRef.getApplicationContext());

//        final Review review = getReviewById(reviewsList, reviewId);
        final Review review = coffeeAppLogic.getReviewById(coffeeMachineId, reviewId);
        if(review != null) {

            //set review data
/*            if(user != null) {
                ((TextView) editReviewView.findViewById(R.id.reviewEditUsernameTextId)).setText(user.getUsername());
                Common.drawProfilePictureByPath((ImageView) editReviewView
                        .findViewById(R.id.profilePicReviewEditTemplateId), user.getProfilePicturePath(),
                        getResources().getDrawable(R.drawable.user_icon));
            }*/
            switch (Common.ReviewStatusEnum.valueOf(reviewStatus)) {
                case GOOD:
                    editReviewView.findViewById(R.id.editReviewTextLayoutId)
                            .setBackgroundColor(getResources().getColor(R.color.light_green_soft));
                    break;
                case NOTSOBAD:
                    editReviewView.findViewById(R.id.editReviewTextLayoutId)
                            .setBackgroundColor(getResources().getColor(R.color.light_yellow_lemon_soft));
                    break;
                case WORST:
                    editReviewView.findViewById(R.id.editReviewTextLayoutId)
                            .setBackgroundColor(getResources().getColor(R.color.light_violet_soft));
                    break;
            }

            (editReviewView.findViewById(R.id.editReviewUndoImageId)).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Common.hideKeyboard(mainActivityRef, ((EditText)editReviewView.findViewById(R.id.reviewCommentEditTextId)));
                            mainActivityRef.onBackPressed();
                        }
                    }
            );
            ((EditText)editReviewView.findViewById(R.id.reviewCommentEditTextId)).setText(review.getComment());

            editReviewView.findViewById(R.id.saveReviewButtonId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.displayError("review saved", mainActivityRef);
                    String reviewCommentNew = ((EditText)editReviewView.findViewById(R.id.reviewCommentEditTextId)).getText().toString();
                    //review.setComment(reviewCommentNew);
                    Common.hideKeyboard(mainActivityRef, ((EditText)editReviewView.findViewById(R.id.reviewCommentEditTextId)));
                    getFragmentManager().popBackStack();

                    coffeeAppLogic.updateReviewById(coffeeMachineId, review.getId(), reviewCommentNew);
                }
            });
        } else {
            Log.e(TAG, "error - no review found");
        }

    }
}