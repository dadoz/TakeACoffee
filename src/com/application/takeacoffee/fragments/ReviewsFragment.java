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
import com.application.adapters.ChoiceReviewPagerAdapter;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.Review;
import com.application.takeacoffee.R;

import java.util.ArrayList;

/**
 * Created by davide on 3/16/14.
 */
public class ReviewsFragment extends Fragment {
    private static final String TAG = "ReviewsFragment";
    private CoffeeMachineDataStorageApplication coffeeMachineApplication;
    private Bundle args;
    private static FragmentActivity mainActivityRef;
    private View reviewsLayoutView;

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
            setHeaderReview(coffeeMachineId, emptyView);
            setAddReviewHeader();
            //add review button
            emptyView.findViewById(R.id.addReviewImageViewId2)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addReviewAction(getFragmentManager());
                        }
                    });

            //set custom font
            Common.setCustomFont(emptyView, this.getActivity().getAssets());
            return emptyView;
        }

        //data are stored in reviewList
        reviewsLayoutView = inflater.inflate(R.layout.reviews_fragment, container, false);

        //set review header (coffee machine name)
        setHeaderReview(coffeeMachineId, reviewsLayoutView);
        setReviewPager(coffeeMachineId);
        setAddReviewHeader();
        setOpenTabReviews();
        //set custom font
        Common.setCustomFont(reviewsLayoutView, this.getActivity().getAssets());
        return reviewsLayoutView;
    }

    private void setOpenTabReviews() {
        reviewsLayoutView.findViewById(R.id.todayReviewsCollapsedButtonId)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setVisibility(View.GONE);
                        reviewsLayoutView.findViewById(R.id.prevReviewsCollapsedButtonId)
                                .setVisibility(View.VISIBLE);

                        reviewsLayoutView.findViewById(R.id.todayReviewsContainerId)
                                .setVisibility(View.VISIBLE);
                        reviewsLayoutView.findViewById(R.id.previousReviewsContainerId)
                                .setVisibility(View.GONE);
                    }
                });
        reviewsLayoutView.findViewById(R.id.prevReviewsCollapsedButtonId)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    reviewsLayoutView.findViewById(R.id.todayReviewsCollapsedButtonId)
                            .setVisibility(View.VISIBLE);

                    reviewsLayoutView.findViewById(R.id.todayReviewsContainerId)
                            .setVisibility(View.GONE);
                    reviewsLayoutView.findViewById(R.id.previousReviewsContainerId)
                            .setVisibility(View.VISIBLE);
                }
            });

    }

    public void setAddReviewHeader() {
        View headerMapLayout = mainActivityRef.findViewById(R.id.headerMapLayoutId);
        headerMapLayout.setVisibility(View.GONE);
    }

    public void setReviewPager(final String coffeeMachineId) {
        boolean isTodayReview = true;
        mPager = (ViewPager) reviewsLayoutView.findViewById(R.id.todayReviewsPagerId);
        mPagerAdapter = new ChoiceReviewPagerAdapter(getChildFragmentManager(), coffeeMachineId, isTodayReview);
        mPager.setAdapter(mPagerAdapter);

        isTodayReview = false; //TODO REFACTOR
        ViewPager mPager2 = (ViewPager) reviewsLayoutView.findViewById(R.id.previousReviewsPagerId);
        ChoiceReviewPagerAdapter mPagerAdapter2 = new ChoiceReviewPagerAdapter(getChildFragmentManager(), coffeeMachineId, isTodayReview);
        mPager2.setAdapter(mPagerAdapter2);
/*        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener())*/
    }

    public void setHeaderReview(final String coffeeMachineId, View view) {
        //SMTHING WRONG - TODO REFACTOR IT
        String coffeeMachineName = coffeeMachineApplication.coffeeMachineData
                .getCoffeMachineById(coffeeMachineId).getName();
        if(coffeeMachineName != null) {
            ((TextView) view.findViewById(R.id.coffeeMachineNameReviewTextId))
                    .setText(coffeeMachineName);
        }
        view.findViewById(R.id.reviewsMachineMapButtonId)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MapFragment mapFragment = new MapFragment();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.coffeeMachineContainerLayoutId,
                                        mapFragment).addToBackStack("back").commit();
                    }
                });
    }

    public void addReviewAction(FragmentManager fragmentManager) {
        AddReviewContainerFragment addReviewContainerFragment = new AddReviewContainerFragment();
        addReviewContainerFragment.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.coffeeMachineContainerLayoutId, addReviewContainerFragment)
                .addToBackStack("back").commit();
    }

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

}

