package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import com.application.commons.Common;
import com.application.models.Review;
import com.application.takeacoffee.R;

import java.util.ArrayList;

import static com.application.takeacoffee.CoffeeMachineActivity.setProfilePicFromStorage;

/**
 * Created by davide on 30/04/14.
 */
public class EditReviewFragment extends Fragment{
    private static Activity mainActivityRef;
    private ArrayList<Review> reviewsList;
    private static final String TAG = "EditReviewFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        View editReviewView = inflater.inflate(R.layout.edit_review_template, container, false);

        Common.setCustomFont(editReviewView, getActivity().getAssets());
        return editReviewView;
    }

    private void initView(int reviewIndex, View editReviewView) {
        //set profile pic
        String profilePicPath = reviewsList.get(reviewIndex).getProfilePicPath(); // TODO to must be refactored
        if(profilePicPath != null) {
            setProfilePicFromStorage((ImageView)editReviewView.findViewById(R.id.profilePicReviewTemplateId));
        }

//        String reviewComment = ((TextView)reviewModifiedViewStorage.findViewById(R.id.reviewCommentTextId)).getText().toString();
        String reviewComment = "old text";
        ((EditText)editReviewView.findViewById(R.id.reviewCommentEditTextId)).setText(reviewComment);

        editReviewView.findViewById(R.id.saveReviewButtonId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.e(TAG, "hey u want to save your review");
/*                String reviewCommentNew = ((EditText)editReviewView.findViewById(R.id.reviewCommentEditTextId)).getText().toString();
                reviewModifiedObjStorage.setComment(reviewCommentNew);
                ViewGroup parent = (ViewGroup)view.getParent().getParent();
                int childIndexPosition = parent.indexOfChild(editReviewView);
                parent.removeView(editReviewView);
                ((TextView)reviewModifiedViewStorage.findViewById(R.id.reviewCommentTextId)).setText(reviewCommentNew);
                parent.addView(reviewModifiedViewStorage, childIndexPosition);*/

            }
        });

    }
}