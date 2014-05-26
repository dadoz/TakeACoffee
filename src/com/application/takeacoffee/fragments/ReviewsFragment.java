package com.application.takeacoffee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.Review;
import com.application.takeacoffee.R;

import java.util.ArrayList;

import static com.application.takeacoffee.CoffeeMachineActivity.addReviewByFragment;

/**
 * Created by davide on 3/16/14.
 */
public class ReviewsFragment extends Fragment {
    private static final String TAG = "ReviewFragment";
    private static final int MAX_NUMBER_TEMPLATE = 3;
    private CoffeeMachineDataStorageApplication coffeeMachineApplication;
    private Bundle args;
    private static FragmentActivity mainActivityRef;
    private View reviewsLayoutView;
//    private int choiceReviewCounter = 0;

    private ViewPager mPager;
    private static PagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();

        //get data from application
        coffeeMachineApplication = ((CoffeeMachineDataStorageApplication) this.getActivity()
                .getApplication());

        //get args from fragment
        String coffeeMachineId = (String)this.getArguments().get(Common.COFFE_MACHINE_ID_KEY);

        //change fragment
        args = new Bundle();
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);

        //check empty listview
        ArrayList<Review> reviewList = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.ReviewStatusEnum.NOTSET);
        if(reviewList == null) {
            //EMPTY listview
            View emptyView = inflater.inflate(R.layout.empty_data_layout, container, false);
            //set review header (coffee machine name)
            setHeaderReview(getFragmentManager(), args, coffeeMachineApplication, coffeeMachineId, emptyView);
            //set custom font
            Common.setCustomFont(emptyView, this.getActivity().getAssets());
            return emptyView;
        }

        //data are stored in reviewList
        reviewsLayoutView = inflater.inflate(R.layout.reviews_fragment, container, false);

        //set review header (coffee machine name)
        setHeaderReview(getFragmentManager(), args, coffeeMachineApplication, coffeeMachineId, reviewsLayoutView);
        setReviewPager(coffeeMachineId);
        //set custom font
        Common.setCustomFont(reviewsLayoutView, this.getActivity().getAssets());
        return reviewsLayoutView;
    }

    public void setReviewPager(final String coffeeMachineId) {
        //setReviewView(coffeeMachineId, initReviewPosition);
        mPager = (ViewPager) reviewsLayoutView.findViewById(R.id.reviewsPagerId);
        mPagerAdapter = new ChoiceReviewPagerAdapter(getChildFragmentManager(), coffeeMachineId);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Common.ReviewStatusEnum reviewStatus = Common.parseStatusFromPageNumber(position);
                Log.e(TAG, " - onPageSelected " + position + " - " + reviewStatus);
            }
        });
    }

    public void setHeaderReview(final FragmentManager fragmentManager,
                         final Bundle args, CoffeeMachineDataStorageApplication coffeeMachineApplication,
                         final String coffeeMachineId, View reviewsLayoutView) {
        String coffeeMachineName = coffeeMachineApplication.coffeeMachineData
                .getCoffeMachineById(coffeeMachineId).getName();
        if(coffeeMachineName != null) {
            ((TextView) this.reviewsLayoutView.findViewById(R.id.coffeeMachineNameReviewTextId))
                    .setText(coffeeMachineName);
        }

        //add review button
        (this.reviewsLayoutView.findViewById(R.id.addReviewImageViewId))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addReviewByFragment(coffeeMachineId);
                    }
                });
    }
/*
    public void setChoiceReviewHeader(final String coffeeMachineId) {
        View choiceReviewsHeader = reviewsLayoutView.findViewById(R.id.choiceReviewsHeaderLayoutId);
        setReviewView(coffeeMachineId);
        choiceReviewsHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choiceReviewCounter < MAX_NUMBER_TEMPLATE - 1) {
                    choiceReviewCounter++;
                } else {
                    choiceReviewCounter = 0;
                }
                setReviewView(coffeeMachineId);
            }
        });
    }*/

    public static ArrayList<Review> getReviewData(String coffeeMachineId,
                                                  CoffeeMachineDataStorageApplication coffeeMachineApplication,
                                                  Common.ReviewStatusEnum reviewStatus) {
        if(coffeeMachineId != null) {
            //check if coffeMachineId exist -
            ArrayList<Review> reviewList = coffeeMachineApplication.coffeeMachineData.
                    getReviewListByCoffeMachineId(coffeeMachineId);
            if(reviewList == null || reviewList.size() == 0) {
                Log.e(TAG,"error - no one coffeeMachine owned by this ID");
                return null;
            }

            if(reviewStatus != Common.ReviewStatusEnum.NOTSET) {
                ArrayList<Review> reviewListSortedByStatus = new ArrayList<Review>();
                //TODO to be refactored
                for(Review review : reviewList) {
                    if(reviewStatus == review.getStatus()) {
                        reviewListSortedByStatus.add(review);
                    }
                }
                if(reviewListSortedByStatus.size() == 0) {
                    return null;
                }
                return reviewListSortedByStatus;
            }
            return reviewList;

        }
        Log.e(TAG, "error - no coffeMachineId found");
        return null;
    }
/*
    private void initView(final View reviewLayoutView, String coffeeMachineId) {
        boolean isListViewEmpty = false;
        Animation anim = AnimationUtils.loadAnimation(mainActivityRef, R.anim.zoom_in);
        anim.setDuration(Common.ANIMATION_GROW_TIME);

        /****GOOD*****//*
        ImageView goodReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.goodReviewButtonId);
        ArrayList<Review> reviewListTemp = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.ReviewStatusEnum.GOOD);
        if(reviewListTemp == null || reviewListTemp.size() == 0) {
            goodReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_empty));
            isListViewEmpty = true;
        } else {
            goodReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_green_sample));
        }
        goodReviewButton.startAnimation(anim);
//        goodReviewButton.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.light_green)));
        goodReviewButton.setOnClickListener(new ReviewListButtonListener(isListViewEmpty,
                Common.ReviewStatusEnum.GOOD));

        /****NOTSOBAD*****//*
        isListViewEmpty = false;
        ImageView badReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.badReviewButtonId);
        badReviewButton.startAnimation(anim);
        reviewListTemp = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.ReviewStatusEnum.NOTSOBAD);
        if(reviewListTemp == null || reviewListTemp.size() == 0) {
            badReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_empty));
            isListViewEmpty = true;
        } else {
            badReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_yellow_sample));
        }
        //      badReviewButton.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.light_yellow)));
        badReviewButton.setOnClickListener(new ReviewListButtonListener(isListViewEmpty,
                                                                             Common.ReviewStatusEnum.NOTSOBAD));

        /****WORST*****//*
        isListViewEmpty = false;
        ImageView worstReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.worstReviewButtonId);
        //worstReviewButton.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.light_black)));
        reviewListTemp = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.ReviewStatusEnum.WORST);
        if(reviewListTemp == null || reviewListTemp.size() == 0) {
            worstReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_empty));
            isListViewEmpty = true;
        } else {
            worstReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_violet_sample));
        }
        worstReviewButton.startAnimation(anim);
        worstReviewButton.setOnClickListener(new ReviewListButtonListener(isListViewEmpty,
                Common.ReviewStatusEnum.WORST));


        /***LOAD MORE REVIEW**//*
        ImageView prevReviewsImageView = (ImageView)reviewLayoutView.findViewById(R.id.prevReviewsButtonId);
        prevReviewsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.displayError("load more reviews", mainActivityRef);
            }
        });
    }
*/

}

