package com.application.takeacoffee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.datastorage.DataStorageSingleton;
import com.application.listener.SwipePageAction;
import com.application.models.Review;
import com.application.takeacoffee.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by davide on 26/05/14.
 */
public class ChoiceReviewFragment extends Fragment{
    private static final String TAG = "ChoiceReviewFragmentTAG";
    private View choiceReviewView; // NEVER STATIC
    private DataStorageSingleton coffeeApp;
    private Bundle args;

    int pagePosition; //allocated only once for the class
    private static long coffeeMachineId;
    private static FragmentActivity mainActivityRef;

    public static Fragment create(int position, long id) {
        coffeeMachineId = id;
        Fragment fragment = new ChoiceReviewFragment();
        Bundle args = new Bundle();
        args.putInt(Common.ARG_REVIEW_PAGE, position);
        args.putLong(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
//        args.putBoolean(Common.IS_TODAY_REVIEW_KEY, isTodayReviews);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        choiceReviewView = inflater.inflate(R.layout.choice_review_template, container, false);
        mainActivityRef = getActivity();
        coffeeApp = DataStorageSingleton.getInstance(getActivity().getApplicationContext());

        //get bundle args
        long coffeeMachineId = this.getArguments().getLong(Common.COFFE_MACHINE_ID_KEY);
        args = new Bundle(); //TODO replace with bundle = bundle
        args.putLong(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
        pagePosition = getArguments().getInt(Common.ARG_REVIEW_PAGE);

        Common.setCustomFont(choiceReviewView, getActivity().getAssets());
        setReviewView(coffeeMachineId, pagePosition);
        return choiceReviewView;
    }

    public void setReviewView(long coffeeMachineId, int choiceReviewPosition) {
        Common.ReviewStatusEnum reviewStatus = Review.parseStatusFromPageNumber(choiceReviewPosition);
        Calendar cal = Calendar.getInstance(); //TODO refactor it
        cal.add(Calendar.DATE, 0);
        long todayTimestamp = (cal.getTime()).getTime();
        cal.add(Calendar.DATE, -1);
        long yesterdayTimestamp = (cal.getTime()).getTime();

        final ArrayList<Review> prevReviewList = coffeeApp.getReviewListByTimestamp(coffeeMachineId,
                reviewStatus, Common.DATE_NOT_SET, yesterdayTimestamp);
        final ArrayList<Review> todayReviewList = coffeeApp.getReviewListByTimestamp(coffeeMachineId,
                reviewStatus, yesterdayTimestamp, todayTimestamp);

        //TODO REFACTOR IT
        //PREVIOUS review list
        choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId).setVisibility(View.GONE);// to be shown
        View pageSwipeButton = choiceReviewView.findViewById(R.id.reviewsMachineSwipeButtonId); //to be shown

        choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId).setVisibility(View.VISIBLE);// to be shown
        if(prevReviewList == null || prevReviewList.size() == 0) {
            choiceReviewView.findViewById(R.id.choiceReviewLeftArrowId).setVisibility(View.GONE);
            choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomCounterId).setVisibility(View.VISIBLE);
        } else {
            choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomCounterId).setVisibility(View.GONE);
            choiceReviewView.findViewById(R.id.choiceReviewLeftArrowId).setVisibility(View.VISIBLE);
            choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId)
                    .setOnClickListener(new ReviewListButtonListener(prevReviewList.size() == 0,
                            Review.parseStatusFromPageNumber(choiceReviewPosition), Common.DATE_NOT_SET,
                            yesterdayTimestamp)); //PREVIOUS LIST
        }
        View footerChoiceReviewView = choiceReviewView.findViewById(R.id.footerChoiceReviewLayoutId);
        //check if empty review list

        //TODO REFACTOR IT
        //PREVIOUS review list
        if(todayReviewList == null || todayReviewList.size() == 0) {
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                    .setText("0");
            choiceReviewView.findViewById(R.id.choiceReviewMainContentId)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Common.displayError("No still review", mainActivityRef);
                        }
                    });
        } else {
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewLatestTimestampTextView))
                    .setText(todayReviewList.get(todayReviewList.size() - 1)
                            .getFormattedTimestamp().split(" ")[1]);
            choiceReviewView.findViewById(R.id.choiceReviewMainContentId)
                    .setOnClickListener(new ReviewListButtonListener(todayReviewList.size() == 0,
                            Review.parseStatusFromPageNumber(choiceReviewPosition), yesterdayTimestamp,
                            todayTimestamp)); //TODAY LIST
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                    .setText(String.valueOf(todayReviewList.size()));
        }

        footerChoiceReviewView.findViewById(R.id.footerChoiceReviewAddReviewButtonId)
                .setOnClickListener(new AddReviewAction(mainActivityRef.getSupportFragmentManager(),
                        choiceReviewPosition));
        //TODO REFACTOR IT


/*

        switch (Common.parseStatusFromPageNumber(choiceReviewPosition)) {
            case GOOD:
                setPageBackgroundColor(footerChoiceReviewView, getResources()
                        .getColor(R.color.light_green_soft), getResources()
                        .getColor(R.color.light_green), Common.GOOD_STATUS_STRING);
                break;
            case NOTSOBAD:
                setPageTextColor(getResources().getColor(R.color.dark_black));
                setPageBackgroundColor(footerChoiceReviewView, getResources()
                        .getColor(R.color.light_yellow_lemon_soft), getResources()
                        .getColor(R.color.light_yellow_lemon), Common.NOTSOBAD_STATUS_STRING);
                break;
            case WORST:
                setPageBackgroundColor(footerChoiceReviewView, getResources()
                        .getColor(R.color.light_violet_soft), getResources()
                        .getColor(R.color.light_violet), Common.TERRIBLE_STATUS_STRING);
                break;
        }*/

        pageSwipeButton.setOnClickListener(new SwipePageAction(pagePosition, mainActivityRef));
    }

    private void setPageBackgroundColor(View footerChoiceReviewView, int softColorId, int colorId,
                                        String statusString) {
        choiceReviewView.findViewById(R.id.choiceReviewPageContainerId)
                .setBackgroundColor(colorId);
        footerChoiceReviewView.setBackgroundColor(colorId);
        ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                .setText(statusString);
        footerChoiceReviewView.setBackgroundColor(getResources()
                .getColor(R.color.light_grey));
        choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId)
                .setBackgroundColor(softColorId);
        choiceReviewView.findViewById(R.id.footerChoiceReviewLayoutId)
                .setBackgroundColor(softColorId);
    }

    private void setPageTextColor(int textColor) {
        ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                .setTextColor(textColor);
        ((TextView) choiceReviewView.findViewById(R.id.choiceReviewLatestTimestampTextView))
                .setTextColor(textColor);
    }

    public class AddReviewAction implements View.OnClickListener {
        private FragmentManager fragmentManager;
        private int pagePosition;
        public AddReviewAction(FragmentManager fm, int pagePosition) {
            this.fragmentManager = fm;
            this.pagePosition = pagePosition;
        }
        @Override
        public void onClick(View view) {
            AddReviewFragment addReviewFragment = new AddReviewFragment();
            addReviewFragment.setArguments(args);
            //args coffeeMachineId already added in
            args.putInt(Common.ARG_PAGE, this.pagePosition);

            fragmentManager.beginTransaction()
                    .replace(R.id.coffeeMachineContainerLayoutId, addReviewFragment)
                    .addToBackStack("back").commit();
        }
    }

    public class ReviewListButtonListener implements View.OnClickListener {
        private final long fromTimestamp;
        private final long toTimestamp;
        private boolean isListViewEmpty;
        private Common.ReviewStatusEnum reviewStatus;

        public ReviewListButtonListener(boolean isListViewEmpty,
                                        Common.ReviewStatusEnum reviewStatus,
                                        long fromTimestamp, long toTimestamp) {
            this.isListViewEmpty = isListViewEmpty;
            this.reviewStatus = reviewStatus;
            this.fromTimestamp = fromTimestamp;
            this.toTimestamp = toTimestamp;
        }

        @Override
        public void onClick(View view) {
            if(isListViewEmpty) {
                Common.displayError("still no one review", mainActivityRef);
//                addReviewWithTextDialog();
            } else {
                ReviewListFragment fragmentObj = new ReviewListFragment();
                args.putString(Common.REVIEW_STATUS_KEY, reviewStatus.name());
                args.putLong(Common.FROM_TIMESTAMP_KEY, fromTimestamp);
                args.putLong(Common.TO_TIMESTAMP_KEY, toTimestamp);

                fragmentObj.setArguments(args);

                mainActivityRef.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, fragmentObj)
                        .addToBackStack("back")
                        .commit();
            }
        }
    }

}
