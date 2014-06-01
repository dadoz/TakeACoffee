package com.application.takeacoffee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.User;
import com.application.takeacoffee.R;

import static com.application.takeacoffee.fragments.ChoiceReviewFragment.createReviewsListView;


/**
 * Created by davide on 05/04/14.
 */
public class AddReviewFragment extends Fragment {
    private static final String TAG = "AddReviewFragment";
    private static CoffeeMachineDataStorageApplication coffeeMachineApplication;

    private static FragmentActivity mainActivityRef;

    private View addReviewView, addReviewButton;
    private boolean addReviewFromListView;
    private Bundle args;

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
        int pageNumber = this.getArguments().getInt(Common.ARG_PAGE);


        addReviewFromListView = obj.booleanValue();
        boolean isPostAction = false;
        (addReviewView.findViewById(R.id.addReviewUndoImageId)).setOnClickListener(new AddMoreTextAction(isPostAction));
        isPostAction = true;
        (addReviewView.findViewById(R.id.addMoreTextOnReviewButtonId)).setOnClickListener(new AddMoreTextAction(isPostAction));

        Common.ReviewStatusEnum reviewStatus = Common.parseStatusFromPageNumber(pageNumber);
        initView(coffeeMachineId, reviewStatus);
        Common.setCustomFont(addReviewView, getActivity().getAssets());
        return addReviewView;
    }

    private void initView(final String coffeeMachineId, final Common.ReviewStatusEnum reviewStatus) {
        //replace view
        switch (reviewStatus) {
            case GOOD:
                hideMoreTextHeader();
                addReviewView.findViewById(R.id.headerAddReviewLayoutId).setBackgroundColor(
                        mainActivityRef.getResources().getColor(R.color.light_green_soft));
                ((TextView)((ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId)).getChildAt(0))
                        .setText(Common.GOOD_STATUS_STRING);

                addReviewView.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_green));
                break;
            case NOTSOBAD:
                hideMoreTextHeader();
                addReviewView.findViewById(R.id.headerAddReviewLayoutId).setBackgroundColor(
                        mainActivityRef.getResources().getColor(R.color.light_yellow_lemon_soft));
                ((TextView)((ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId)).getChildAt(0))
                        .setText(Common.NOTSOBAD_STATUS_STRING);
                addReviewView.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_yellow_lemon));
                ((TextView) addReviewView.findViewById(R.id.reviewTextIconTextViewId)).setTextColor(
                        mainActivityRef.getResources().getColor(R.color.dark_black));
                break;
            case WORST:
                hideMoreTextHeader();
                addReviewView.findViewById(R.id.headerAddReviewLayoutId).setBackgroundColor(
                        mainActivityRef.getResources().getColor(R.color.light_violet_soft));
                ((TextView)((ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId)).getChildAt(0))
                        .setText(Common.TERRIBLE_STATUS_STRING);
                addReviewView.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_violet));
                break;
        }

        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set MORE text
                addReview(coffeeMachineId, reviewStatus,
                        (addReviewView.findViewById(R.id.reviewTextIconTextViewId)).getTag() != null);
            }
        });

    }

    public void addReview(final String coffeeMachineId, final Common.ReviewStatusEnum reviewStatus,
                                 boolean reviewWithText) {
        //add data to list
        User loggedUser = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();
        if(loggedUser == null) {
            Common.displayError("You must be logged in before add review!", mainActivityRef);
            return;
        }

        if(reviewWithText) {
            reviewText = ((TextView)addReviewView.findViewById(R.id.reviewTextIconTextViewId)).getText().toString();
            if(reviewText.equals(new String("")))  {
                Common.displayError("you must insert your text review!", mainActivityRef);
                return;
            }
        }

        coffeeMachineApplication.coffeeMachineData.addReviewByCoffeeMachineId(coffeeMachineId,
                loggedUser.getId(), loggedUser.getUsername(), reviewText,
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser().getProfilePicturePath(),
                reviewStatus);

        if(!addReviewFromListView) {
            mainActivityRef.getSupportFragmentManager().popBackStack();
            createReviewsListView(mainActivityRef.getSupportFragmentManager(), reviewStatus, args);
        }
    }

    public class AddMoreTextAction implements View.OnClickListener {
        private final boolean isPostAction;

        public AddMoreTextAction(boolean isPostAction) {
            this.isPostAction = isPostAction;
        }

        @Override
        public void onClick(View view) {
//            final ViewGroup parent = (ViewGroup)view.getParent().getParent();
            final ViewGroup parent = (ViewGroup)addReviewView.findViewById(R.id.reviewViewContainerLayoutId);
            final ViewGroup parentHeader = (ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId).getParent();
            final LinearLayout addReviewIconView = (LinearLayout)(parent).getChildAt(0);
            final LinearLayout addTextOnReviewView = (LinearLayout)(parentHeader).getChildAt(1);

            if(addTextOnReviewView.getVisibility() == View.GONE) {
//                addReviewIconView.setVisibility(View.GONE);
                addTextOnReviewView.setVisibility(View.VISIBLE);
                addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.GONE);
/*
                (addTextOnReviewView.findViewById(R.id.addMoreTextOnReviewButtonId))
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String reviewText = ((EditText) addTextOnReviewView.findViewById(R.id.reviewEditTextId)).getText().toString();

                                addReviewIconView.setVisibility(View.VISIBLE);
                                ((TextView)addReviewIconView.findViewById(R.id.reviewTextIconTextViewId)).setText(reviewText);
                                (addReviewIconView.findViewById(R.id.reviewTextIconTextViewId)).setTag(Common.SET_MORE_TEXT_ON_REVIEW);
                                addTextOnReviewView.setVisibility(View.GONE);
                                addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.VISIBLE);
                                Common.hideKeyboard(mainActivityRef, ((EditText) addTextOnReviewView.findViewById(R.id.reviewEditTextId)));
                            }
                        });*/

            } else if(addTextOnReviewView.getVisibility() == View.VISIBLE) {
                if(isPostAction) {
                    if(((EditText) addTextOnReviewView.findViewById(R.id.reviewEditTextId))
                            .getText().toString().trim().equals("")) {
                        Common.displayError("you must type some review at least", mainActivityRef);
                        return;
                    }
                    ((TextView) addReviewIconView.findViewById(R.id.reviewTextIconTextViewId))
                            .setText(((EditText) addTextOnReviewView.findViewById(R.id.reviewEditTextId))
                                    .getText().toString());
                    (addReviewIconView.findViewById(R.id.reviewTextIconTextViewId)).setTag(Common.SET_MORE_TEXT_ON_REVIEW);
                }

                ((EditText) addTextOnReviewView.findViewById(R.id.reviewEditTextId)).setText("");
                Common.hideKeyboard(mainActivityRef, ((EditText) addTextOnReviewView.findViewById(R.id.reviewEditTextId)));
                addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.VISIBLE);
                addTextOnReviewView.setVisibility(View.GONE);
                addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.VISIBLE);

            }
        }
    }

    public void hideMoreTextHeader() {
        View addMoreTextView = addReviewView.findViewById(R.id.addMoreTextLayoutId);
        View headerAddReviewView = addReviewView.findViewById(R.id.headerAddReviewLayoutId);
        if(addMoreTextView.getVisibility() == View.VISIBLE) {
            addMoreTextView.setVisibility(View.GONE);
            headerAddReviewView.setVisibility(View.VISIBLE);
            addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.VISIBLE);
        }

    }
}