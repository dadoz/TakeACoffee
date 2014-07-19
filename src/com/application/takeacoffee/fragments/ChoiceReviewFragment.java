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
import com.application.datastorage.CoffeeMachineDataStorageApplication;
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
    private CoffeeMachineDataStorageApplication coffeeMachineApplication;
    private Bundle args;

    int pagePosition; //allocated only once for the class
    private static String coffeeMachineId;
    private static FragmentActivity mainActivityRef;

    public static Fragment create(int position, String id) {
        coffeeMachineId = id;
        //pagePosition = position;
        Fragment fragment = new ChoiceReviewFragment();
        Bundle args = new Bundle();
        args.putInt(Common.ARG_REVIEW_PAGE, position);
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
//        args.putBoolean(Common.IS_TODAY_REVIEW_KEY, isTodayReviews);
        fragment.setArguments(args);
        //Log.e(TAG, "create fragment " + position);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        choiceReviewView = inflater.inflate(R.layout.choice_review_template, container, false);
        //get data from application
        coffeeMachineApplication = ((CoffeeMachineDataStorageApplication) this.getActivity()
                .getApplication());
        mainActivityRef = getActivity();

        //get args from fragment
        String coffeeMachineId = (String)this.getArguments().get(Common.COFFE_MACHINE_ID_KEY);
        args = new Bundle();
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
        pagePosition = getArguments().getInt(Common.ARG_REVIEW_PAGE);
        //Log.e(TAG, "on create view" + pagePosition);

        Common.setCustomFont(choiceReviewView, getActivity().getAssets());
        setReviewView(coffeeMachineId, pagePosition);
        return choiceReviewView;
    }

    public void setReviewView(String coffeeMachineId, int choiceReviewPosition) {
        Common.ReviewStatusEnum reviewStatus = Common.parseStatusFromPageNumber(choiceReviewPosition);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        long todayTimestamp = (cal.getTime()).getTime();
        cal.add(Calendar.DATE, -1);
        long yesterdayTimestamp = (cal.getTime()).getTime();
//        cal.add(Calendar.WEEK_OF_MONTH, -1);
//        long prevWeekTimestamp = (cal.getTime()).getTime();

        final ArrayList<Review> prevReviewList = ChoiceReviewContainerFragment.getReviewDataByTimestamp(coffeeMachineId,
                coffeeMachineApplication, reviewStatus, Common.DATE_NOT_SET, yesterdayTimestamp);
        final ArrayList<Review> todayReviewList = ChoiceReviewContainerFragment.getReviewDataByTimestamp(coffeeMachineId,
                coffeeMachineApplication, reviewStatus, yesterdayTimestamp, todayTimestamp);

        setReviewViewTodayAndPrevious(todayReviewList, prevReviewList, choiceReviewPosition);


/*
        int choiceReviewListStatus = -1;

        if(prevReviewList == null || prevReviewList.size() == 0  &&
                (todayReviewList != null && todayReviewList.size() != 0)) {
            //no one review for this coffeeMachine - status - previous - no one review for this coffeeMachine - status - today
            choiceReviewListStatus = 1;
        }
        if((prevReviewList != null && prevReviewList.size() != 0)  &&
                (todayReviewList == null || todayReviewList.size() == 0)) {
            //no one review for this coffeeMachine - status - previous - no one review for this coffeeMachine - status - today
            choiceReviewListStatus = 0;
        }

        if((prevReviewList != null && prevReviewList.size() != 0)  &&
                (todayReviewList != null && todayReviewList.size() != 0)) {
            choiceReviewListStatus = 2;
        }

        switch (choiceReviewListStatus) {
            case 0:
                //today = 0 rather than previous > 0
//                setReviewViewPrevious(prevReviewList, choiceReviewPosition);
                setReviewViewTodayAndPrevious(todayReviewList, choiceReviewPosition, true, prevReviewList.size());
                break;
            case 1:
                //today > 0 previous = 0
                setReviewViewTodayAndPrevious(todayReviewList, choiceReviewPosition, false, -1);
                break;
            case 2:
                //today > 0 and prev > 0
                setReviewViewTodayAndPrevious(todayReviewList, choiceReviewPosition, true, prevReviewList.size());
                break;
            case -1:
                Log.e(TAG, "bug choiceReviewListStatus failed to be assert");
        }*/
    }

    private void setReviewViewTodayAndPrevious(ArrayList<Review> todayReviewList, ArrayList<Review> prevReviewList, int choiceReviewPosition) {
        //        choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId);//to be customized
        choiceReviewView.findViewById(R.id.choiceReviewLatestContainerId).setVisibility(View.VISIBLE);//to be customized
        choiceReviewView.findViewById(R.id.choiceReviewLatestContainerNotSetId).setVisibility(View.GONE);//to be customized
        choiceReviewView.findViewById(R.id.choiceReviewHeaderPreviousId).setVisibility(View.GONE);// to be shown
        choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId).setVisibility(View.GONE);// to be shown
        View pageSwipeButton = choiceReviewView.findViewById(R.id.reviewsMachineSwipeButtonId); //to be shown

//        String prevReviewCounterString = prevReviewList == null || prevReviewList.size() == 0 ? "no review" : String.valueOf(prevReviewList.size());
        choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId).setVisibility(View.VISIBLE);// to be shown
  //      ((TextView) choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomCounterId))
  //              .setText(prevReviewCounterString);// to be shown
        if(prevReviewList == null || prevReviewList.size() == 0) {
            choiceReviewView.findViewById(R.id.choiceReviewLeftArrowId).setVisibility(View.GONE);
            choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomCounterId).setVisibility(View.VISIBLE);
        } else {
            choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomCounterId).setVisibility(View.GONE);
            choiceReviewView.findViewById(R.id.choiceReviewLeftArrowId).setVisibility(View.VISIBLE);
            choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId)
                    .setOnClickListener(new ReviewListButtonListener(prevReviewList.size() == 0,
                            Common.parseStatusFromPageNumber(choiceReviewPosition), false));
        }

/*        ((TextView) choiceReviewView.findViewById(R.id.choiceReviewHeaderTitleTextId)).setText("Today");
        if(!isTodayReview) {
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewHeaderTitleTextId)).setText("Latest weeks");
        }*/

        View footerChoiceReviewView = choiceReviewView.findViewById(R.id.footerChoiceReviewLayoutId);
        //check if empty review list

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
            boolean isTodayReview = true;
            choiceReviewView.findViewById(R.id.choiceReviewMainContentId)
                    .setOnClickListener(new ReviewListButtonListener(todayReviewList.size() == 0,
                            Common.parseStatusFromPageNumber(choiceReviewPosition), isTodayReview));
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                    .setText(String.valueOf(todayReviewList.size()));
        }

        footerChoiceReviewView.findViewById(R.id.footerChoiceReviewAddReviewButtonId)
                .setOnClickListener(new AddReviewAction(mainActivityRef.getSupportFragmentManager(), choiceReviewPosition));

        switch (Common.parseStatusFromPageNumber(choiceReviewPosition)) {
            case GOOD:
                choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId).setBackgroundColor(getResources()
                        .getColor(R.color.light_green));
                choiceReviewView.findViewById(R.id.choiceReviewMainContentId).setBackgroundColor(getResources()
                        .getColor(R.color.light_green));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_green));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText(Common.GOOD_STATUS_STRING);
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_grey));

                choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId)
                        .setBackgroundColor(getResources()
                                .getColor(R.color.light_green_soft));

                choiceReviewView.findViewById(R.id.footerChoiceReviewLayoutId)
                        .setBackgroundColor(getResources()
                                .getColor(R.color.light_green_soft));

/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
                break;
            case NOTSOBAD:
                choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId).setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon));
                choiceReviewView.findViewById(R.id.choiceReviewMainContentId).setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon));
                ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                        .setTextColor(getResources().getColor(R.color.light_black));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText(Common.NOTSOBAD_STATUS_STRING);
                ((TextView) choiceReviewView.findViewById(R.id.choiceReviewLatestTimestampTextView))
                        .setTextColor(getResources().getColor(R.color.dark_black));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_grey));

                choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId)
                        .setBackgroundColor(getResources()
                                .getColor(R.color.light_yellow_lemon_soft));

                choiceReviewView.findViewById(R.id.footerChoiceReviewLayoutId)
                        .setBackgroundColor(getResources()
                                .getColor(R.color.light_yellow_lemon_soft));

/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
                break;
            case WORST:
                choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId).setBackgroundColor(getResources()
                        .getColor(R.color.light_violet));
                choiceReviewView.findViewById(R.id.choiceReviewMainContentId).setBackgroundColor(getResources()
                        .getColor(R.color.light_violet));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_violet));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText(Common.TERRIBLE_STATUS_STRING);
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_grey));

                choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId)
                        .setBackgroundColor(getResources()
                                .getColor(R.color.light_violet_soft));

                choiceReviewView.findViewById(R.id.footerChoiceReviewLayoutId)
                        .setBackgroundColor(getResources()
                                .getColor(R.color.light_violet_soft));

/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
                break;
        }

        pageSwipeButton.setOnClickListener(new SwipePageAction(pagePosition, mainActivityRef));

    }

    /*
        public void setReviewViewPrevious(ArrayList<Review> reviewList, int choiceReviewPosition) {
    //        choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId);//to be customized
            choiceReviewView.findViewById(R.id.choiceReviewLatestContainerId).setVisibility(View.GONE);//to be customized
            choiceReviewView.findViewById(R.id.choiceReviewLatestContainerNotSetId).setVisibility(View.VISIBLE);//to be customized
            choiceReviewView.findViewById(R.id.choiceReviewHeaderPreviousId).setVisibility(View.VISIBLE);// to be shown
            View pageSwipeButton = choiceReviewView.findViewById(R.id.reviewsMachineSwipeButtonId); //to be shown


            View footerChoiceReviewView = choiceReviewView.findViewById(R.id.footerChoiceReviewLayoutId);
            footerChoiceReviewView.findViewById(R.id.footerChoiceReviewAddReviewButtonId)
                    .setOnClickListener(new AddReviewAction(mainActivityRef.getSupportFragmentManager(), choiceReviewPosition));


            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewLatestPreviousTimestampTextView))
                    .setText(reviewList.get(reviewList.size() - 1)
                            .getFormattedTimestamp().split(" ")[1]);
            boolean isTodayReview = false;
            choiceReviewView.findViewById(R.id.choiceReviewButtonId)
                    .setOnClickListener(new ReviewListButtonListener(reviewList.size() == 0,
                            Common.parseStatusFromPageNumber(choiceReviewPosition), isTodayReview));
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                    .setText(String.valueOf(reviewList.size()));

            switch (Common.parseStatusFromPageNumber(choiceReviewPosition)) {
                case GOOD:
                    choiceReviewView.findViewById(R.id.choiceReviewHeaderPreviousId).setBackgroundColor(getResources()
                            .getColor(R.color.light_green_soft));
                    choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId).setBackgroundColor(getResources()
                            .getColor(R.color.light_green));
                    choiceReviewView.findViewById(R.id.choiceReviewMainContentId).setBackgroundColor(getResources()
                            .getColor(R.color.light_green_soft));
                    footerChoiceReviewView.setBackgroundColor(getResources()
                            .getColor(R.color.light_grey));
                    ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                            .setText(Common.GOOD_STATUS_STRING);
    /*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                    .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
/*                break;
            case NOTSOBAD:
                choiceReviewView.findViewById(R.id.choiceReviewHeaderPreviousId).setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon_soft));
                choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId).setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon));
                choiceReviewView.findViewById(R.id.choiceReviewMainContentId).setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon_soft));
                ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                        .setTextColor(getResources().getColor(R.color.light_black));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon_soft));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText(Common.NOTSOBAD_STATUS_STRING);
                ((TextView) choiceReviewView.findViewById(R.id.choiceReviewLatestTimestampTextView))
                        .setTextColor(getResources().getColor(R.color.dark_black));

/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
/*                break;
            case WORST:
                choiceReviewView.findViewById(R.id.choiceReviewHeaderPreviousId).setBackgroundColor(getResources()
                        .getColor(R.color.light_violet_soft));
                choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId).setBackgroundColor(getResources()
                        .getColor(R.color.light_violet));
                choiceReviewView.findViewById(R.id.choiceReviewMainContentId).setBackgroundColor(getResources()
                        .getColor(R.color.light_violet_soft));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_violet_soft));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText(Common.TERRIBLE_STATUS_STRING);

/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
/*                break;
        }

        pageSwipeButton.setOnClickListener(new SwipePageAction(pagePosition));
    }
*//*
    public void setReviewViewTodayAndPrevious(ArrayList<Review> reviewList, int choiceReviewPosition, boolean hasPrevious, int previousReviewCount) {
//        choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId);//to be customized
        choiceReviewView.findViewById(R.id.choiceReviewLatestContainerId).setVisibility(View.VISIBLE);//to be customized
        choiceReviewView.findViewById(R.id.choiceReviewLatestContainerNotSetId).setVisibility(View.GONE);//to be customized
        choiceReviewView.findViewById(R.id.choiceReviewHeaderPreviousId).setVisibility(View.GONE);// to be shown
        choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId).setVisibility(View.GONE);// to be shown
        View pageSwipeButton = choiceReviewView.findViewById(R.id.reviewsMachineSwipeButtonId); //to be shown

        String prevReviewCounterString = hasPrevious? String.valueOf(previousReviewCount) : "no review";
/*        if(hasPrevious) {
            prevReviewCounterString = String.valueOf(previousReviewCount);
        }*/
//        choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId).setVisibility(View.VISIBLE);// to be shown
//        ((TextView) choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomCounterId))
//                .setText(prevReviewCounterString);// to be shown

/*        ((TextView) choiceReviewView.findViewById(R.id.choiceReviewHeaderTitleTextId)).setText("Today");
        if(!isTodayReview) {
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewHeaderTitleTextId)).setText("Latest weeks");
        }*/
/*
        View footerChoiceReviewView = choiceReviewView.findViewById(R.id.footerChoiceReviewLayoutId);
        //check if empty review list

        if(reviewList == null || reviewList.size() == 0) {
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                    .setText("0");
            choiceReviewView.findViewById(R.id.choiceReviewButtonId)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Common.displayError("No still review", mainActivityRef);
                        }
                    });
        } else {
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewLatestTimestampTextView))
                    .setText(reviewList.get(reviewList.size() - 1)
                            .getFormattedTimestamp().split(" ")[1]);
            boolean isTodayReview = true;
            choiceReviewView.findViewById(R.id.choiceReviewButtonId)
                    .setOnClickListener(new ReviewListButtonListener(reviewList.size() == 0,
                            Common.parseStatusFromPageNumber(choiceReviewPosition), isTodayReview));
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                    .setText(String.valueOf(reviewList.size()));
        }

        footerChoiceReviewView.findViewById(R.id.footerChoiceReviewAddReviewButtonId)
                .setOnClickListener(new AddReviewAction(mainActivityRef.getSupportFragmentManager(), choiceReviewPosition));

        switch (Common.parseStatusFromPageNumber(choiceReviewPosition)) {
            case GOOD:
                choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId).setBackgroundColor(getResources()
                        .getColor(R.color.light_green));
                choiceReviewView.findViewById(R.id.choiceReviewMainContentId).setBackgroundColor(getResources()
                        .getColor(R.color.light_green));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_green));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText(Common.GOOD_STATUS_STRING);
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_grey));

                choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId)
                        .setBackgroundColor(getResources()
                        .getColor(R.color.light_green_soft));
/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
/*                break;
            case NOTSOBAD:
                choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId).setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon));
                choiceReviewView.findViewById(R.id.choiceReviewMainContentId).setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon));
                ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                        .setTextColor(getResources().getColor(R.color.light_black));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText(Common.NOTSOBAD_STATUS_STRING);
                ((TextView) choiceReviewView.findViewById(R.id.choiceReviewLatestTimestampTextView))
                        .setTextColor(getResources().getColor(R.color.dark_black));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_grey));

                choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId)
                        .setBackgroundColor(getResources()
                                .getColor(R.color.light_yellow_lemon_soft));

/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
/*                break;
            case WORST:
                choiceReviewView.findViewById(R.id.choiceReviewHeaderTodayId).setBackgroundColor(getResources()
                        .getColor(R.color.light_violet));
                choiceReviewView.findViewById(R.id.choiceReviewMainContentId).setBackgroundColor(getResources()
                        .getColor(R.color.light_violet));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_violet));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText(Common.TERRIBLE_STATUS_STRING);
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_grey));

                choiceReviewView.findViewById(R.id.choiceReviewLatestReviewBottomBoxId)
                        .setBackgroundColor(getResources()
                                .getColor(R.color.light_violet_soft));

/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
/*                break;
        }

        pageSwipeButton.setOnClickListener(new SwipePageAction(pagePosition));

    }*/


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
        private final boolean isTodayReview;
        private boolean isListViewEmpty;
        private Common.ReviewStatusEnum reviewStatus;

        public ReviewListButtonListener(boolean isListViewEmpty, Common.ReviewStatusEnum reviewStatus, boolean isTodayReview) {
            this.isListViewEmpty = isListViewEmpty;
            this.reviewStatus = reviewStatus;
            this.isTodayReview = isTodayReview;
//            Log.e(TAG, "--- on click" + isTodayReview);
        }

        @Override
        public void onClick(View view) {
            if(isListViewEmpty) {
                Common.displayError("still no one review", mainActivityRef);
//                addReviewWithTextDialog();
            } else {
                createReviewsListView(mainActivityRef.getSupportFragmentManager(), this.reviewStatus, args, isTodayReview);
            }
        }
    }

    public static void createReviewsListView(FragmentManager fragManager,
                                             Common.ReviewStatusEnum reviewStatus, Bundle args, boolean isTodayReview) {
        //customDialog.setContentView(R.layout.dialog_review_layout);
        ReviewListFragment fragmentObj = new ReviewListFragment();
        args.putString(Common.REVIEW_STATUS_KEY, reviewStatus.name());
        args.putBoolean(Common.IS_TODAY_REVIEW_KEY, isTodayReview);
        fragmentObj.setArguments(args);

        fragManager.beginTransaction()
//                .setCustomAnimations(R.anim.fade_in,
//                        R.anim.fade_out)
                .replace(R.id.coffeeMachineContainerLayoutId, fragmentObj)
                .addToBackStack("back")
                .commit();
    }
}
