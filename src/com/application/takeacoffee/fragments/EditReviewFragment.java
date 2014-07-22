package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.Review;
import com.application.models.User;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

import java.util.ArrayList;

/**
 * Created by davide on 30/04/14.
 */
public class EditReviewFragment extends Fragment{
    private static Activity mainActivityRef;
    private ArrayList<Review> reviewsList;
    private CoffeeMachineDataStorageApplication coffeeMachineApplication;
    private static final String TAG = "EditReviewFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        View editReviewView = inflater.inflate(R.layout.edit_review_template, container, false);

        String reviewId = getArguments().getString(Common.REVIEW_ID);
        String coffeeMachineId = getArguments().getString(Common.COFFE_MACHINE_ID_KEY);
//        int pageNumber = getArguments().getInt(Common.ARG_PAGE);
        //get data from application
        coffeeMachineApplication = (CoffeeMachineDataStorageApplication) this.getActivity().getApplication();
        String reviewListId = coffeeMachineApplication
                .getReviewListIdByCoffeeMachine(coffeeMachineId);
        reviewsList = coffeeMachineApplication.getReviewListById(reviewListId);

        setHeader();
        Common.ReviewStatusEnum reviewStatus = Common.ReviewStatusEnum.valueOf(
                (String) this.getArguments().get(Common.REVIEW_STATUS_KEY));
        initView(reviewId, reviewStatus, editReviewView); //test

        Common.setCustomFont(editReviewView, getActivity().getAssets());
        return editReviewView;
    }

    private Review getReviewById(ArrayList<Review> reviewsList, String reviewId) {
        for(Review review:reviewsList) {
            if(review.getId().equals(reviewId)) {
                return review;
            }
        }
        return null;
    }

    public void setHeader() {
        CoffeeMachineActivity.setHeaderByFragmentId(3, getFragmentManager(), null);
    }

    private void initView(String reviewId, Common.ReviewStatusEnum reviewStatus, final View editReviewView) {
        //User user = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();

        final Review review = getReviewById(reviewsList, reviewId);
        if(review != null) {

            //set review data
/*            if(user != null) {
                ((TextView) editReviewView.findViewById(R.id.reviewEditUsernameTextId)).setText(user.getUsername());
                Common.drawProfilePictureByPath((ImageView) editReviewView
                        .findViewById(R.id.profilePicReviewEditTemplateId), user.getProfilePicturePath(),
                        getResources().getDrawable(R.drawable.user_icon));
            }*/
            switch (reviewStatus) {
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
                    review.setComment(reviewCommentNew);
                    Common.hideKeyboard(mainActivityRef, ((EditText)editReviewView.findViewById(R.id.reviewCommentEditTextId)));
                    getFragmentManager().popBackStack();
                }
            });
        } else {
            Log.e(TAG, "error - no review found");
        }

    }
}