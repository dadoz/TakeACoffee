package com.application.adapters;

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
import com.application.models.Review;
import com.application.takeacoffee.R;
import com.application.takeacoffee.fragments.AddReviewContainerFragment;
import com.application.takeacoffee.fragments.AddReviewFragment;
import com.application.takeacoffee.fragments.ReviewListFragment;

import java.util.ArrayList;

import static com.application.takeacoffee.fragments.ReviewsFragment.getReviewData;

/**
 * Created by davide on 26/05/14.
 */
public class ChoiceReviewFragment extends Fragment{
    private static final String TAG = "ChoiceReviewFragmentTAG";
    private View choiceReviewView; // NEVER STATIC
    private CoffeeMachineDataStorageApplication coffeeMachineApplication;
    private Bundle args;

    boolean isTodayReview;
    int pagePosition; //allocated only once for the class
    private static String coffeeMachineId;
    private static FragmentActivity mainActivityRef;

    public static Fragment create(int position, String id, boolean isTodayReviews) {
        coffeeMachineId = id;
        //pagePosition = position;
        Fragment fragment = new ChoiceReviewFragment();
        Bundle args = new Bundle();
        args.putInt(Common.ARG_REVIEW_PAGE, position);
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
        args.putBoolean(Common.IS_TODAY_REVIEW_KEY, isTodayReviews);
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
        isTodayReview = getArguments().getBoolean(Common.IS_TODAY_REVIEW_KEY);
        //Log.e(TAG, "on create view" + pagePosition);

        Common.setCustomFont(choiceReviewView, getActivity().getAssets());
        setReviewView(coffeeMachineId, pagePosition);
        return choiceReviewView;
    }

    public void setReviewView(String coffeeMachineId, int choiceReviewPosition) {
        ArrayList<Review> reviewListTemp = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.parseStatusFromPageNumber(choiceReviewPosition));

        ((TextView) choiceReviewView.findViewById(R.id.choiceReviewHeaderTitleTextId)).setText("Today");
        if(!isTodayReview) {
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewHeaderTitleTextId)).setText("Latest weeks");
        }

        View footerChoiceReviewView = choiceReviewView.findViewById(R.id.footerChoiceReviewLayoutId);
        //check if empty review list

        if(reviewListTemp == null || reviewListTemp.size() == 0) {
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
            choiceReviewView.findViewById(R.id.choiceReviewButtonId)
                    .setOnClickListener(new ReviewListButtonListener(reviewListTemp.size() == 0,
                            Common.parseStatusFromPageNumber(choiceReviewPosition)));
            ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                    .setText(String.valueOf(reviewListTemp.size()));
        }

        footerChoiceReviewView.findViewById(R.id.footerChoiceReviewAddReviewButtonId)
                .setOnClickListener(new AddReviewAction(mainActivityRef.getSupportFragmentManager(), choiceReviewPosition));

        switch (Common.parseStatusFromPageNumber(choiceReviewPosition)) {
            case GOOD:
                choiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_green));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_green_soft));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText("Good");
/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
                break;
            case NOTSOBAD:
                choiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon));
                ((TextView) choiceReviewView.findViewById(R.id.choiceReviewCounterTextView))
                        .setTextColor(getResources().getColor(R.color.light_black));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_yellow_lemon_soft));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText("Not so bad");
/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
                break;
            case WORST:
                choiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_violet));
                footerChoiceReviewView.setBackgroundColor(getResources()
                        .getColor(R.color.light_violet_soft));
                ((TextView) footerChoiceReviewView.findViewById(R.id.footerChoiceReviewTextId))
                        .setText("Worst");

/*                        ((ImageView)choiceReviewView.findViewById(R.id.choiceReviewButtonId))
                                .setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon));*/
                break;
        }

    }

    public class AddReviewAction implements View.OnClickListener {
        private FragmentManager fragmentManager;
        int pagePosition;
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
        private boolean isListViewEmpty;
        private Common.ReviewStatusEnum reviewStatus;

        public ReviewListButtonListener(boolean isListViewEmpty, Common.ReviewStatusEnum reviewStatus) {
            this.isListViewEmpty = isListViewEmpty;
            this.reviewStatus = reviewStatus;
        }

        @Override
        public void onClick(View view) {
            if(isListViewEmpty) {
                Common.displayError("still no one review", mainActivityRef);
//                addReviewWithTextDialog();
            } else {
                createReviewsListView(mainActivityRef.getSupportFragmentManager(), this.reviewStatus, args);
            }
        }
    }

    public static void createReviewsListView(FragmentManager fragManager,
                                             Common.ReviewStatusEnum reviewStatus, Bundle args) {
        //customDialog.setContentView(R.layout.dialog_review_layout);
        ReviewListFragment fragmentObj = new ReviewListFragment();
        args.putString(Common.REVIEW_STATUS_KEY, reviewStatus.name());
        fragmentObj.setArguments(args);

        fragManager.beginTransaction()
//                .setCustomAnimations(R.anim.fade_in,
//                        R.anim.fade_out)
                .replace(R.id.coffeeMachineContainerLayoutId, fragmentObj)
                .addToBackStack("back")
                .commit();
    }
}
