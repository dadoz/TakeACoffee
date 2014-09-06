package com.application.takeacoffee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.datastorage.DataStorageSingleton;
import com.application.models.Review;
import com.application.models.User;
import com.application.takeacoffee.R;

import java.util.Calendar;

//import static com.application.takeacoffee.fragments.ChoiceReviewFragment.createReviewsListView;


/**
 * Created by davide on 05/04/14.
 */
public class AddReviewFragment extends Fragment {
    private static final String TAG = "AddReviewFragment";
    private static DataStorageSingleton coffeeApp;

    private static FragmentActivity mainActivityRef;

    private View addReviewView, addReviewButton;
    private Bundle args;
    private int pagePosition;

    private static String reviewText = "Review for this machine - auto generated";

    public static AddReviewFragment create(int pageNumber, long coffeeMachineId) {
        AddReviewFragment fragment = new AddReviewFragment();
        Bundle args = new Bundle();
        args.putInt(Common.ARG_PAGE, pageNumber);
        args.putLong(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)  {
        mainActivityRef = getActivity();
        addReviewView = inflater.inflate(R.layout.add_review_fragment_alternative, container, false);
        addReviewButton = addReviewView.findViewById(R.id.addReviewButtonId);
//        addReviewCustomTextButton = addReviewView.findViewById(R.id.addReviewCustomTextButtonId);

        //get data from application
        coffeeApp = DataStorageSingleton.getInstance(mainActivityRef.getApplicationContext());

        //get args from fragment
        args = getArguments();

        long coffeeMachineId = this.getArguments().getLong(Common.COFFE_MACHINE_ID_KEY);
        pagePosition = this.getArguments().getInt(Common.ARG_PAGE);

        setHeader(coffeeMachineId);

        boolean isPostAction = false;
        (addReviewView.findViewById(R.id.addReviewUndoImageId)).setOnClickListener(new AddMoreTextAction(isPostAction));
        isPostAction = true;
        (addReviewView.findViewById(R.id.addMoreTextOnReviewButtonId)).setOnClickListener(new AddMoreTextAction(isPostAction));

        Common.ReviewStatusEnum reviewStatus = Review.parseStatusFromPageNumber(pagePosition);

        initView(coffeeMachineId, reviewStatus);
        Common.setCustomFont(addReviewView, getActivity().getAssets());
        return addReviewView;
    }

    public void setHeader(long coffeeMachineId) {
//        CoffeeMachineActivity.setHeaderBarVisibile(false);
/*        CoffeeMachineActivity.hideAllItemsOnHeaderBar();
        mainActivityRef.findViewById(R.id.headerBarLayoutId).setVisibility(View.GONE);

        String coffeeMachineName = coffeeApp.coffeeMachineData
                .getCoffeMachineById(coffeeMachineId).getName();
        CoffeeMachineActivity.addItemOnHeaderBarById(R.layout.review_header_template,
                null, coffeeMachineName,
                false, getFragmentManager());*/

    }

    private void initView(final long coffeeMachineId, final Common.ReviewStatusEnum reviewStatus) {
        //replace view
        switch (reviewStatus) {
            case GOOD:
                hideMoreTextHeader();
                setPageBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_green_soft),
                        mainActivityRef.getResources().getColor(R.color.light_green),
                        Common.GOOD_STATUS_STRING);
                ((TextView) addReviewView.findViewById(R.id.reviewDescriptionTextId)).setText(
                        "It seems that this coffee's really good!\nI'd suggest you to take here some drink as well.");
                break;
            case NOTSOBAD:
                hideMoreTextHeader();
                setPageBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_yellow_lemon_soft),
                        mainActivityRef.getResources().getColor(R.color.light_yellow_lemon),
                        Common.NOTSOBAD_STATUS_STRING);

                ((TextView) addReviewView.findViewById(R.id.reviewDescriptionTextId)).setText(
                        "It's not so good as I was imaging to be.\nI guess you should try to change machine to take some good drink.");
                break;
            case WORST:
                hideMoreTextHeader();
                setPageBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_violet_soft),
                        mainActivityRef.getResources().getColor(R.color.light_violet),
                        Common.TERRIBLE_STATUS_STRING);
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

/*        addReviewCustomTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set MORE text
                addReview(coffeeMachineId, reviewStatus,
                        (addReviewView.findViewById(R.id.reviewViewContainerLayoutId)).getTag() != null);
            }
        });*/
    }

    private void setPageBackgroundColor(int softColorId, int colorId, String statusString) {
        addReviewView.findViewById(R.id.headerAddMoreTextLayoutId).setBackgroundColor(softColorId);
        addReviewView.findViewById(R.id.headerAddReviewLayoutId).setBackgroundColor(softColorId);
        ((TextView)((ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId)).getChildAt(0))
                .setText(statusString);

        addReviewView.findViewById(R.id.mainAddReviewLayoutId).setBackgroundColor(colorId);
    }

    public void addReview(final long coffeeMachineId, final Common.ReviewStatusEnum reviewStatus,
                                 boolean reviewWithText) {
        //add data to list
        if(! coffeeApp.isRegisteredUser()) {
            Common.displayError("You must be logged in before add review!", mainActivityRef);
            return;
        }

        if(coffeeApp.isLocalUser()) {
            if(! coffeeApp.registerLocalUser()) {
                Common.displayError("Failed to register your username - check your internet connection!", mainActivityRef);
                return;
            }
        }

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

        if(reviewWithText) {
            reviewText = ((TextView)addReviewView.findViewById(R.id.reviewEditTextId)).getText().toString();
            if(reviewText.equals(new String("")))  {
                Common.displayError("you must insert your text review!", mainActivityRef);
                return;
            }
            Common.hideKeyboard(mainActivityRef, ((EditText) addReviewView.findViewById(R.id.reviewEditTextId)
                    .findViewById(R.id.reviewEditTextId)));
        }

        //ADD new Review
        coffeeApp.addReviewByParams(coffeeApp.getRegisteredUserId(), coffeeMachineId, reviewText, reviewStatus);        //TODO replace these rows
        createReviewsListView(reviewStatus, args);

//        mainActivityRef.getSupportFragmentManager().popBackStack(); //TODO SOOOOO FUCKING WRONG -
    }

    private void createReviewsListView( Common.ReviewStatusEnum reviewStatus, Bundle args) {
        Calendar cal = Calendar.getInstance(); //TODO refactor it
        cal.add(Calendar.DATE, 0);
        long todayTimestamp = (cal.getTime()).getTime();
        cal.add(Calendar.DATE, -1);
        long yesterdayTimestamp = (cal.getTime()).getTime();

        ReviewListFragment fragmentObj = new ReviewListFragment();
        args.putString(Common.REVIEW_STATUS_KEY, reviewStatus.name());
        args.putLong(Common.FROM_TIMESTAMP_KEY, yesterdayTimestamp);
        args.putLong(Common.TO_TIMESTAMP_KEY, todayTimestamp);
        fragmentObj.setArguments(args);

        mainActivityRef.getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeMachineContainerLayoutId, fragmentObj)
                .addToBackStack("back")
                .commit();
    }

    public class AddMoreTextAction implements View.OnClickListener {
//        private final boolean isPostAction;

        public AddMoreTextAction(boolean isPostAction) {
            //this.isPostAction = isPostAction;
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
//            final View addReviewCustomTextButton = addReviewView.findViewById(R.id.addReviewCustomTextButtonId);

            if(headerAddReviewLayout.getVisibility() == View.VISIBLE) {
                headerAddReviewLayout.setVisibility(View.GONE);
                headerAddMoreTextLayout.setVisibility(View.VISIBLE);
                addReviewCustomTextLayout.setVisibility(View.VISIBLE);

                addReviewDescriptionLayout.setVisibility(View.GONE);
                addReviewView.setBackgroundColor(getResources().getColor(R.color.light_grey));
                (parent).setTag(Common.SET_MORE_TEXT_ON_REVIEW);

//                addReviewButton.setVisibility(View.GONE);
//                addReviewCustomTextButton.setVisibility(View.VISIBLE);
            } else {
                headerAddReviewLayout.setVisibility(View.VISIBLE);
                headerAddMoreTextLayout.setVisibility(View.GONE);
//                addReviewButton.setVisibility(View.VISIBLE);
//                addReviewCustomTextButton.setVisibility(View.GONE);

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