package com.application.takeacoffee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppLogic;
import com.application.dataRequest.ParseDataRequest;
import com.application.datastorage.DataStorageSingleton;
import com.application.listener.SwipePageAction;
import com.application.models.Review;
import com.application.takeacoffee.R;
import org.joda.time.DateTime;

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
    private static String coffeeMachineId;
    private static FragmentActivity mainActivityRef;

    public static Fragment create(int position, String id) {
        coffeeMachineId = id;
        Fragment fragment = new ChoiceReviewFragment();
        Bundle args = new Bundle();
        args.putInt(Common.ARG_REVIEW_PAGE, position);
        args.putString(Common.COFFEE_MACHINE_ID_KEY, coffeeMachineId);
//        args.putBoolean(Common.IS_TODAY_REVIEW_KEY, isTodayReviews);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        choiceReviewView = inflater.inflate(R.layout.choice_review_template, container, false);
        mainActivityRef = getActivity();
        coffeeApp = DataStorageSingleton.getInstance(mainActivityRef.getApplicationContext());
        //get bundle args
        String coffeeMachineId = this.getArguments().getString(Common.COFFEE_MACHINE_ID_KEY);
        args = new Bundle(); //TODO replace with bundle = bundle
//        args.putString(Common.COFFEE_MACHINE_ID_KEY, coffeeMachineId);
        args.putAll(getArguments());
        pagePosition = getArguments().getInt(Common.ARG_REVIEW_PAGE);

        Common.setCustomFont(choiceReviewView, getActivity().getAssets());
        setReviewView(coffeeMachineId, pagePosition);
        return choiceReviewView;
    }

    public void setReviewView(String coffeeMachineId, int choiceReviewPosition) {
        CoffeeAppLogic coffeeAppLogic = new CoffeeAppLogic(mainActivityRef.getApplicationContext());

        Common.ReviewStatusEnum reviewStatus = Review.parseStatusFromPageNumber(choiceReviewPosition);
        DateTime dateTime = new DateTime();
//        long yesterdayTimestamp = CoffeeAppLogic.TimestampHandler.getYesterdayTimestamp(dateTime);
        long oneWeekAgoTimestamp = CoffeeAppLogic.TimestampHandler.getOneWeekAgoTimestamp(dateTime);
        long todayTimestamp = CoffeeAppLogic.TimestampHandler.getTodayTimestamp(dateTime);

        //TODO SET YESTERDAY LIST REVIEW
        int previousReviewListCount = coffeeAppLogic.getReviewCounter(coffeeMachineId, reviewStatus, todayTimestamp);
        setPreviousListChooser(previousReviewListCount, choiceReviewPosition, oneWeekAgoTimestamp, todayTimestamp);

        //TODO SET TODAY LIST REVIEW
        int reviewCounter = coffeeAppLogic.getReviewCounter(coffeeMachineId, reviewStatus, Common.DATE_NOT_SET);
        setTodayListChooser(reviewCounter, choiceReviewPosition, todayTimestamp, Common.DATE_NOT_SET);

        View footerChoiceReviewView = choiceReviewView.findViewById(R.id.footerChoiceReviewLayoutId);
        View pageSwipeButton = choiceReviewView.findViewById(R.id.reviewsMachineSwipeButtonId); //to be shown
        footerChoiceReviewView.findViewById(R.id.footerChoiceReviewAddReviewButtonId)
                .setOnClickListener(new AddReviewAction(mainActivityRef.getSupportFragmentManager(),
                        choiceReviewPosition));
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

    private void setPreviousListChooser(int reviewListCount, int position,
                                        long fromTimestamp, long toTimestamp) {
        if(reviewListCount <= 0) {
            choiceReviewView.findViewById(R.id.choiceReviewLeftArrowId).setVisibility(View.GONE);
            choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomCounterId).setVisibility(View.VISIBLE);
            return;
        }

        choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomCounterId).setVisibility(View.GONE);
        choiceReviewView.findViewById(R.id.choiceReviewLeftArrowId).setVisibility(View.VISIBLE);
        choiceReviewView.findViewById(R.id.previousContainerViewId)
                .setOnClickListener(new ReviewListButtonListener(false,
                        Review.parseStatusFromPageNumber(position), fromTimestamp,
                        toTimestamp));
    }

    private void setTodayListChooser(int reviewCounter, int position,
                                     long fromTimestamp, long toTimestamp) {
        boolean isEmptyList = false;
        if(reviewCounter <= 0) {
            isEmptyList = true;
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                    .setText("0");
        } else {
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewLatestTimestampTextView))
                        .setText("fake");
/*                    .setText(reviewList.get(reviewList.size() - 1)
                            .getFormattedTimestamp().split(" ")[1]);*/
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                    .setText(String.valueOf(reviewCounter));
        }

        choiceReviewView.findViewById(R.id.choiceReviewMainContentId)
                .setOnClickListener(new ReviewListButtonListener(isEmptyList,
                        Review.parseStatusFromPageNumber(position), fromTimestamp,
                        toTimestamp));
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
        choiceReviewView.findViewById(R.id.previousContainerViewId)
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
                Common.displayError(mainActivityRef.getApplicationContext(), "Empty review :(");
                return;
            }

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
