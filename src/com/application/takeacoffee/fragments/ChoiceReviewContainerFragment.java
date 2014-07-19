package com.application.takeacoffee.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.adapters.ChoiceReviewPagerAdapter;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.Review;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;


import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by davide on 3/16/14.
 */
public class ChoiceReviewContainerFragment extends Fragment {
    private static final String TAG = "ChoiceReviewContainerFragment";
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

        return initView(inflater, container, coffeeMachineId);
    }

    public View initView(LayoutInflater inflater, ViewGroup container, String coffeeMachineId) {

        //mainActivityRef.findViewById(R.id.reviewsMachineMapContainerId).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT));

        //check empty listview
        ArrayList<Review> reviewList = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.ReviewStatusEnum.NOTSET);
        if(reviewList == null) {
            //EMPTY listview
            View emptyView = inflater.inflate(R.layout.empty_data_layout, container, false);

            //set review header (coffee machine name)
            setHeader(coffeeMachineId);


            //add review button
            emptyView.findViewById(R.id.addReviewImageViewId2)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //addReviewAction(mainActivityRef.getSupportFragmentManager());
                            AddReviewContainerFragment addReviewContainerFragment = new AddReviewContainerFragment();
                            addReviewContainerFragment.setArguments(args);

                            mainActivityRef.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.coffeeMachineContainerLayoutId, addReviewContainerFragment)
                                    .addToBackStack("back").commit();

                        }
                    });
            Common.setCustomFont(emptyView, this.getActivity().getAssets());
//            Log.e(TAG, "u must not jump her bastard");
            return emptyView;
            //set custom font
        } else {
            //data are stored in reviewList
            reviewsLayoutView = inflater.inflate(R.layout.reviews_fragment, container, false);

            setSwipePagerOnMessage();

            //set review header (coffee machine name)
//          setHeaderReview(coffeeMachineId, reviewsLayoutView);
            setReviewPager(coffeeMachineId);
            setHeader(coffeeMachineId);
//          setOpenTabReviews();
            //set custom font
            Common.setCustomFont(reviewsLayoutView, this.getActivity().getAssets());
            return reviewsLayoutView;
        }
    }

/*
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
*/
    public void setHeader(String coffeeMachineId) {
        CoffeeMachineActivity.setHeaderByFragmentId(1, getFragmentManager(), coffeeMachineId);
        mainActivityRef.findViewById(R.id.addReviewSwipeButtonId).setVisibility(View.INVISIBLE);

    }

    public void setReviewPager(final String coffeeMachineId) {
        //boolean isTodayReview = false;
        mPager = (ViewPager) reviewsLayoutView.findViewById(R.id.reviewsPagerId);
        mPagerAdapter = new ChoiceReviewPagerAdapter(getChildFragmentManager(), coffeeMachineId);
        mPager.setAdapter(mPagerAdapter);

    }

    public void setSwipePagerOnMessage() {
//        CoffeeMachineActivity.handler.handleMessage();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //SENDER
                if(intent != null && intent.getAction() != null) {
                    int nextPagePosition = intent.getAction().equals("SWIPE_PAGE") ? intent.getIntExtra("NEXT_PAGE", Common.ITEM_NOT_SELECTED): Log.d(TAG, "no valid action");
                    if(nextPagePosition  != Common.ITEM_NOT_SELECTED ) {
                        mPager.setCurrentItem(nextPagePosition);
                    }
                }
            }
        };

        IntentFilter a = new IntentFilter();
        a.addAction("SWIPE_PAGE");
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(broadcastReceiver, a);

    }
/*  public void setHeaderReview(final String coffeeMachineId, View view) {
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
    }*/

/*    public void addReviewAction(FragmentManager fragmentManager) {
        AddReviewContainerFragment addReviewContainerFragment = new AddReviewContainerFragment();
        addReviewContainerFragment.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.coffeeMachineContainerLayoutId, addReviewContainerFragment)
                .addToBackStack("back").commit();
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


    public static ArrayList<Review> getReviewDataByTimestamp(String coffeeMachineId,
                                                                             CoffeeMachineDataStorageApplication coffeeMachineApplication,
                                                                             Common.ReviewStatusEnum reviewStatus,
                                                                             long fromTimestamp, long toTimestamp) {
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
                    if(toTimestamp != Common.DATE_NOT_SET) {
                        if(reviewStatus == review.getStatus() &&
                                review.getTimestamp() > fromTimestamp &&
                                review.getTimestamp() < toTimestamp) {
                            reviewListSortedByStatus.add(review);
                        }
                    } else {
                        if(reviewStatus == review.getStatus() &&
                                review.getTimestamp() > fromTimestamp) {
                            reviewListSortedByStatus.add(review);
                        }
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

