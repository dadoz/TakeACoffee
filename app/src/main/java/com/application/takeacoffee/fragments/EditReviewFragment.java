package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppController;
import com.application.models.Review;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

/**
 * Created by davide on 30/04/14.
 */
public class EditReviewFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "EditReviewFragment";

    private static Activity mainActivityRef;
    private View editReviewView;
    static Review mReview;
    static String coffeeMachineId;
    private CoffeeAppController coffeeAppController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mainActivityRef = activity;
        coffeeAppController = ((CoffeeMachineActivity) mainActivityRef).getCoffeeAppController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        editReviewView = inflater.inflate(R.layout.edit_review_template, container, false);

        String reviewStatus = (String) this.getArguments().get(Common.REVIEW_STATUS_KEY);
        String reviewId = getArguments().getString(Common.REVIEW_ID);
        coffeeMachineId = getArguments().getString(Common.COFFEE_MACHINE_ID_KEY);

//        HeaderUtils.setHeaderByFragmentId(mainActivityRef, 3, getFragmentManager(), Common.EMPTY_VALUE);

        initView(reviewId, reviewStatus); //test
        Common.setCustomFont(editReviewView, getActivity().getAssets());
        return editReviewView;
    }

    private void initView(String reviewId, String reviewStatus) {
        mReview = coffeeAppController.getReviewById(coffeeMachineId, reviewId);
        if(mReview == null) {
            Log.e(TAG, "error - no review found");
            //TODO to be handled
            return;
        }

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

        ((EditText) editReviewView.findViewById(R.id.reviewCommentEditTextId)).setText(mReview.getComment());
        (editReviewView.findViewById(R.id.editReviewUndoImageId)).setOnClickListener(this);
        editReviewView.findViewById(R.id.saveReviewButtonId).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {

            case R.id.editReviewUndoImageId:
                Common.hideKeyboard(mainActivityRef, ((EditText)editReviewView.findViewById(R.id.reviewCommentEditTextId)));
                mainActivityRef.onBackPressed();
                break;

            case R.id.saveReviewButtonId :
                try {
                    Common.displayError(mainActivityRef.getApplicationContext(), "review saved");
                    String reviewCommentNew = ((EditText) editReviewView.findViewById(R.id.reviewCommentEditTextId)).getText().toString();
                    Common.hideKeyboard(mainActivityRef, ((EditText) editReviewView.findViewById(R.id.reviewCommentEditTextId)));
                    getFragmentManager().popBackStack();

                    coffeeAppController.updateReviewById(coffeeMachineId, mReview.getId(), reviewCommentNew);
                } catch (Exception e) {
                    e.printStackTrace();
                    Common.displayError(mainActivityRef.getApplicationContext(), "review not saved :(");
                }
                break;
        }

    }
}