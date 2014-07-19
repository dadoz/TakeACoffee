package com.application.takeacoffee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.listener.SwipePageAction;
import com.application.models.User;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

import static com.application.takeacoffee.fragments.ChoiceReviewFragment.*;
import static com.application.takeacoffee.fragments.ChoiceReviewFragment.createReviewsListView;


/**
 * Created by davide on 05/04/14.
 */
public class AddReviewFragment extends Fragment {
    private static final String TAG = "AddReviewFragment";
    private static CoffeeMachineDataStorageApplication coffeeMachineApplication;

    private static FragmentActivity mainActivityRef;

    private View addReviewView, addReviewButton, addReviewCustomTextButton;
    private boolean addReviewFromListView;
    private Bundle args;
    private int pagePosition;

    private static String reviewText = "Review for this machine - auto generated";

    public static AddReviewFragment create(int pageNumber, String coffeeMachineId) {
        //Log.e(TAG, "static create of " + pageNumber);
        AddReviewFragment fragment = new AddReviewFragment();
        Bundle args = new Bundle();
        args.putInt(Common.ARG_PAGE, pageNumber);
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
        fragment.setArguments(args);
        //mPageNumber = pageNumber;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)  {
        mainActivityRef = getActivity();
        addReviewView = inflater.inflate(R.layout.add_review_fragment_alternative, container, false);
        addReviewButton = addReviewView.findViewById(R.id.addReviewButtonId);
        addReviewCustomTextButton = addReviewView.findViewById(R.id.addReviewCustomTextButtonId);


        //get data from application
        coffeeMachineApplication = ((CoffeeMachineDataStorageApplication) this.getActivity()
                .getApplication());

        //get args from fragment
        args = getArguments();

        String coffeeMachineId = (String)this.getArguments().get(Common.COFFE_MACHINE_ID_KEY);
        Boolean obj = (Boolean)this.getArguments().get(Common.ADD_REVIEW_FROM_LISTVIEW);
        if(obj == null) {
            obj = new Boolean(false);
        }
        pagePosition = this.getArguments().getInt(Common.ARG_PAGE);

        setHeader(coffeeMachineId);

        addReviewFromListView = obj.booleanValue();
        boolean isPostAction = false;
        (addReviewView.findViewById(R.id.addReviewUndoImageId)).setOnClickListener(new AddMoreTextAction(isPostAction));
        isPostAction = true;
        (addReviewView.findViewById(R.id.addMoreTextOnReviewButtonId)).setOnClickListener(new AddMoreTextAction(isPostAction));

        Common.ReviewStatusEnum reviewStatus = Common.parseStatusFromPageNumber(pagePosition);

        initView(coffeeMachineId, reviewStatus);
        Common.setCustomFont(addReviewView, getActivity().getAssets());
        return addReviewView;
    }

    public void setHeader(String coffeeMachineId) {
//        CoffeeMachineActivity.setHeaderBarVisibile(false);
/*        CoffeeMachineActivity.hideAllItemsOnHeaderBar();
        mainActivityRef.findViewById(R.id.headerBarLayoutId).setVisibility(View.GONE);

        String coffeeMachineName = coffeeMachineApplication.coffeeMachineData
                .getCoffeMachineById(coffeeMachineId).getName();
        CoffeeMachineActivity.addItemOnHeaderBarById(R.layout.review_header_template,
                null, coffeeMachineName,
                false, getFragmentManager());*/

    }

    private void initView(final String coffeeMachineId, final Common.ReviewStatusEnum reviewStatus) {
//        View pageSwipeButton = mainActivityRef.findViewById(R.id.addReviewSwipeButtonId); //to be shown
        Log.d(TAG, "page" + pagePosition);
//        pageSwipeButton.setOnClickListener(new SwipePageAction(pagePosition, mainActivityRef));

        //replace view
        switch (reviewStatus) {
            case GOOD:
                hideMoreTextHeader();

                addReviewView.findViewById(R.id.headerAddMoreTextLayoutId).setBackgroundColor(mainActivityRef
                        .getResources().getColor(R.color.light_green_soft));
                addReviewView.findViewById(R.id.headerAddReviewLayoutId).setBackgroundColor(mainActivityRef
                        .getResources().getColor(R.color.light_green_soft));
                ((TextView)((ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId)).getChildAt(0))
                        .setText(Common.GOOD_STATUS_STRING);

//                addReviewView.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_green));
                addReviewView.findViewById(R.id.mainAddReviewLayoutId).setBackgroundColor(mainActivityRef
                        .getResources().getColor(R.color.light_green));
                ((TextView) addReviewView.findViewById(R.id.reviewDescriptionTextId)).setText(
                        "It seems that this coffee's really good!\nI'd suggest you to take here some drink as well.");
                break;
            case NOTSOBAD:
                hideMoreTextHeader();
                addReviewView.findViewById(R.id.headerAddMoreTextLayoutId).setBackgroundColor(mainActivityRef
                        .getResources().getColor(R.color.light_yellow_lemon_soft));
                addReviewView.findViewById(R.id.headerAddReviewLayoutId).setBackgroundColor(mainActivityRef
                        .getResources().getColor(R.color.light_yellow_lemon_soft));
                ((TextView)((ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId)).getChildAt(0))
                        .setText(Common.NOTSOBAD_STATUS_STRING);
//                addReviewView.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_yellow_lemon));
                addReviewView.findViewById(R.id.mainAddReviewLayoutId).setBackgroundColor(mainActivityRef
                        .getResources().getColor(R.color.light_yellow_lemon));

                ((TextView) addReviewView.findViewById(R.id.reviewDescriptionTextId)).setText(
                        "It's not so good as I was imaging to be.\nI guess you should try to change machine to take some good drink.");

                //((TextView) addReviewView.findViewById(R.id.reviewTextIconTextViewId)).setTextColor(
                  //      mainActivityRef.getResources().getColor(R.color.dark_black));
                break;
            case WORST:
                hideMoreTextHeader();
                addReviewView.findViewById(R.id.headerAddMoreTextLayoutId).setBackgroundColor(mainActivityRef
                        .getResources().getColor(R.color.light_violet_soft));
                addReviewView.findViewById(R.id.headerAddReviewLayoutId).setBackgroundColor(mainActivityRef
                        .getResources().getColor(R.color.light_violet_soft));
                ((TextView)((ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId)).getChildAt(0))
                        .setText(Common.TERRIBLE_STATUS_STRING);
//                addReviewView.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_violet));
                addReviewView.findViewById(R.id.mainAddReviewLayoutId).setBackgroundColor(mainActivityRef
                        .getResources().getColor(R.color.light_violet));
                ((TextView) addReviewView.findViewById(R.id.reviewDescriptionTextId)).setText(
                        "Ouch terrible drinks!\nYou must change machine 'cos drinks here are so awful!");

                break;
        }

        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set MORE text
                addReview(coffeeMachineId, reviewStatus,
                        (addReviewView.findViewById(R.id.reviewViewContainerLayoutId)).getTag() != null);
            }
        });

        addReviewCustomTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set MORE text
                addReview(coffeeMachineId, reviewStatus,
                        (addReviewView.findViewById(R.id.reviewViewContainerLayoutId)).getTag() != null);
            }
        });

    }

    public void addReview(final String coffeeMachineId, final Common.ReviewStatusEnum reviewStatus,
                                 boolean reviewWithText) {

        switch (reviewStatus) {
            case GOOD:
                reviewText = Common.GOOD_STATUS_STRING;
                break;
            case NOTSOBAD:
                reviewText = Common.NOTSOBAD_STATUS_STRING;
                break;
            case WORST:
                reviewText = Common.TERRIBLE_STATUS_STRING;
                break;
        }
        //add data to list
        User loggedUser = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();
        if(loggedUser == null) {
            Common.displayError("You must be logged in before add review!", mainActivityRef);
            return;
        }

        if(reviewWithText) {
            reviewText = ((TextView)addReviewView.findViewById(R.id.reviewEditTextId)).getText().toString();
            if(reviewText.equals(new String("")))  {
                Common.displayError("you must insert your text review!", mainActivityRef);
                return;
            }
            Common.hideKeyboard(mainActivityRef, ((EditText) addReviewView.findViewById(R.id.reviewEditTextId)
                    .findViewById(R.id.reviewEditTextId)));
        }

        coffeeMachineApplication.coffeeMachineData.addReviewByCoffeeMachineId(coffeeMachineId,
                loggedUser.getId(), loggedUser.getUsername(), reviewText,
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser().getProfilePicturePath(),
                reviewStatus);

        if(!addReviewFromListView) {
            mainActivityRef.getSupportFragmentManager().popBackStack();
            boolean isTodayReview = true;
            createReviewsListView(mainActivityRef.getSupportFragmentManager(), reviewStatus, args, isTodayReview);
        }
    }

    public class AddMoreTextAction implements View.OnClickListener {
        private final boolean isPostAction;

        public AddMoreTextAction(boolean isPostAction) {
            this.isPostAction = isPostAction;
        }

        @Override
        public void onClick(View view) {
//            final LinearLayout addReviewIconView = (LinearLayout)(parent).getChildAt(0);
//            final View addTextOnReviewView1 = (parent).getChildAt(1);
//            final View addTextOnReviewHeaderView = parentHeader.getChildAt(1);

            final ViewGroup parent = (ViewGroup)addReviewView.findViewById(R.id.reviewViewContainerLayoutId);
            final View addReviewDescriptionLayout = addReviewView.findViewById(R.id.addReviewDescriptionLayoutId);
            final View addReviewCustomTextLayout = addReviewView.findViewById(R.id.addReviewCustomTextLayoutId);
            final ViewGroup headerAddReviewLayout = (ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId);
            final ViewGroup headerAddMoreTextLayout = (ViewGroup)addReviewView.findViewById(R.id.headerAddMoreTextLayoutId);

            //final View addTextOnReviewHeaderView = parentHeader.getChildAt(1);

            final View addReviewButton = addReviewView.findViewById(R.id.addReviewButtonId);
            final View addReviewCustomTextButton = addReviewView.findViewById(R.id.addReviewCustomTextButtonId);

            if(headerAddReviewLayout.getVisibility() == View.VISIBLE) {

                headerAddReviewLayout.setVisibility(View.GONE);
                headerAddMoreTextLayout.setVisibility(View.VISIBLE);
                addReviewCustomTextLayout.setVisibility(View.VISIBLE);

                addReviewDescriptionLayout.setVisibility(View.GONE);
                addReviewView.setBackgroundColor(getResources().getColor(R.color.light_grey));
                (parent).setTag(Common.SET_MORE_TEXT_ON_REVIEW);

                addReviewButton.setVisibility(View.GONE);
                addReviewCustomTextButton.setVisibility(View.VISIBLE);

            } else {

                headerAddReviewLayout.setVisibility(View.VISIBLE);
                headerAddMoreTextLayout.setVisibility(View.GONE);
                addReviewButton.setVisibility(View.VISIBLE);
                addReviewCustomTextButton.setVisibility(View.GONE);

                parent.setTag(null);
                ((EditText) addReviewCustomTextLayout.findViewById(R.id.reviewEditTextId)).setText("");
                Common.hideKeyboard(mainActivityRef, ((EditText) addReviewCustomTextLayout.findViewById(R.id.reviewEditTextId)));
                addReviewCustomTextLayout.setVisibility(View.GONE);
                addReviewDescriptionLayout.setVisibility(View.VISIBLE);

            }
        }
    }

    public void hideMoreTextHeader() {
        View addMoreTextView = addReviewView.findViewById(R.id.headerAddMoreTextLayoutId);
        View headerAddReviewView = addReviewView.findViewById(R.id.headerAddReviewLayoutId);
        if(addMoreTextView.getVisibility() == View.VISIBLE) {
            addMoreTextView.setVisibility(View.GONE);
            headerAddReviewView.setVisibility(View.VISIBLE);
            addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.VISIBLE);
        }

    }
}