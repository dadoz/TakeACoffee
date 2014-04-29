package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.User;
import com.application.takeacoffee.R;

import static com.application.takeacoffee.fragments.ReviewsFragment.createReviewsListView;

/**
 * Created by davide on 05/04/14.
 */
public class AddReviewFragment extends Fragment {
    private static final String TAG = "AddReviewFragment";
//    private static LinearLayout addReviewAction, addReviewTextBarContainer;
//    private static LinearLayout addReviewAction;
//    private static ScrollView addReviewViewContainerLayout;
//    private static ImageView addReviewWithoutTextImageView, addReviewWithMoreTextImageView;
    private static LinearLayout addMoreTextOnReviewLayout;
    private static View choiceTypeReviewLayout;
    private static TextView goodReviewTab, notSoBadReviewTab, worstReviewTab;//, addTextbarTextHeader;
    private static LinearLayout addReviewButton;
    private static EditText reviewEditText;

    private static Activity mainActivityRef;
    private static Bundle args;
    private static CoffeeMachineDataStorageApplication coffeeMachineApplication;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)  {
        mainActivityRef = getActivity();
        View addReviewView = inflater.inflate(R.layout.add_review_fragment, container, false);

        //get data from application
        coffeeMachineApplication = ((CoffeeMachineDataStorageApplication) this.getActivity()
                .getApplication());

        //get args from fragment
        String coffeeMachineId = (String)this.getArguments().get(Common.COFFE_MACHINE_ID_KEY);

        //change fragment
        args = new Bundle();
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);

//        addReviewButton = (Button)addReviewView.findViewById(R.id.addReviewButtonId);
        addReviewButton = (LinearLayout)addReviewView.findViewById(R.id.addReviewButtonId);
        reviewEditText = (EditText)addReviewView.findViewById(R.id.reviewEditTextId);
        reviewEditText.setVisibility(View.GONE);

//        addReviewViewContainerLayout = (ScrollView)addReviewView.findViewById(R.id.addReviewViewContainerLayoutId);
        addMoreTextOnReviewLayout = (LinearLayout)addReviewView.findViewById(R.id.addMoreTextOnReviewLayoutId);

        choiceTypeReviewLayout = addReviewView.findViewById(R.id.choiceTypeReviewLayoutId);
        //addReviewTextBarContainer = (LinearLayout)addReviewView.findViewById(R.id.addReviewTextBarContainerId);
//        addReviewAction = (LinearLayout)addReviewView.findViewById(R.id.addReviewActionId);
//        addReviewWithoutTextImageView = (ImageView)addReviewView.findViewById(R.id.addReviewWithoutTextImageViewId);
//        addReviewWithMoreTextImageView = (ImageView)addReviewView.findViewById(R.id.addReviewWithMoreTextImageViewId);
        //TAB
        goodReviewTab = (TextView)addReviewView.findViewById(R.id.goodTabTextId);
        notSoBadReviewTab = (TextView)addReviewView.findViewById(R.id.notBadTabTextId);
        worstReviewTab = (TextView)addReviewView.findViewById(R.id.worstTabTextId);

        initView(coffeeMachineId);
        Common.setCustomFont(addReviewView, getActivity().getAssets());
        return addReviewView;
    }

    public static void initView(final String coffeeMachineId) {
//        addReviewTextBarContainer.setVisibility(View.GONE);
        //init reviewStatus
        choiceTypeReviewLayout.setTag(Common.ReviewStatusEnum.GOOD);

        goodReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReviewButton.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_green));
                ((TextView)addReviewButton.getChildAt(0)).setTextColor(mainActivityRef
                        .getResources().getColor(R.color.light_black));
                choiceTypeReviewLayout.setTag(Common.ReviewStatusEnum.GOOD);

            }
        });
        notSoBadReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReviewButton.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_yellow));
                ((TextView)addReviewButton.getChildAt(0)).setTextColor(mainActivityRef
                        .getResources().getColor(R.color.light_black));
                choiceTypeReviewLayout.setTag(Common.ReviewStatusEnum.NOTSOBAD);
            }
        });
        worstReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReviewButton.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_black));
                ((TextView)addReviewButton.getChildAt(0)).setTextColor(mainActivityRef
                        .getResources().getColor(R.color.light_grey));
                choiceTypeReviewLayout.setTag(Common.ReviewStatusEnum.WORST);
            }
        });


        addMoreTextOnReviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reviewEditText.isActivated()) {
                    //NO MORE TEXT
                    reviewEditText.setActivated(false);
                    reviewEditText.setVisibility(View.GONE);
                } else {
                    //set MORE TEXT
                    reviewEditText.setActivated(true);
                    reviewEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set MORE text
                if(reviewEditText != null && reviewEditText.isActivated()) {
                    Common.hideKeyboard(mainActivityRef, reviewEditText);
                    addReview(coffeeMachineId, true);
                    return;
                }
                addReview(coffeeMachineId, false);
            }
        });

    }


    public static void addReview(final String coffeeMachineId, boolean reviewWithText) {
        String reviewText = "Review for this machine - auto generated";

        if(reviewWithText) {
            //get text by editText
            reviewText = reviewEditText.getText().toString();
            if(reviewText.equals(new String("")))  {
                Common.displayError("you must insert your text review!", mainActivityRef);
                return;
            }
        }
        Common.ReviewStatusEnum reviewStatus = Common.ReviewStatusEnum.valueOf(choiceTypeReviewLayout
                .getTag().toString());
//                Log.d(TAG, "reviewStatus " + Common.ReviewStatusEnum.valueOf(reviewStatusValue));
        mainActivityRef.getFragmentManager().popBackStack();

        //add data to list
        User loggedUser = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();
        if(loggedUser == null) {
            Common.displayError("You must be logged in before add review!", mainActivityRef);
            return;
        }

        coffeeMachineApplication.coffeeMachineData.addReviewByCoffeeMachineId(coffeeMachineId,
                loggedUser.getId(), loggedUser.getUsername(), reviewText,
                coffeeMachineApplication.coffeeMachineData.getProfilePicturePath(), reviewStatus);
        createReviewsListView(mainActivityRef.getFragmentManager(), reviewStatus, args);

    }
  /*  public static void swipeFragment(SwipeDetector.Action action, int swipeCounter) {
        if(action == SwipeDetector.Action.LR) {
            Log.d(TAG, "caught left swipe action - " + swipeCounter);
            removeReview(swipeCounter + 1);
            initAddReview(swipeCounter);
        } else if(action == SwipeDetector.Action.RL) {
            Log.d(TAG, "caught right swipe action");
            removeReview(swipeCounter - 1);
            initAddReview(swipeCounter);
        }
    }*/

}