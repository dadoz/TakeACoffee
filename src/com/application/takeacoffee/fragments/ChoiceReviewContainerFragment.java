package com.application.takeacoffee.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.adapters.ChoiceReviewPagerAdapter;
import com.application.commons.Common;
import com.application.datastorage.DataStorageSingleton;
import com.application.models.Review;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;


import java.util.ArrayList;

/**
 * Created by davide on 3/16/14.
 */
public class ChoiceReviewContainerFragment extends Fragment {
    private static final String TAG = "ChoiceReviewContainerFragment";
    private DataStorageSingleton coffeeApp;
    private Bundle args;
    private static FragmentActivity mainActivityRef;
    private View reviewsLayoutView;

    private ViewPager mPager;
    private static PagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        //get data from application
        coffeeApp = DataStorageSingleton.getInstance(getActivity().getApplicationContext());

        //fragment bundle //TODO refactor it
        long coffeeMachineId = this.getArguments().getLong(Common.COFFE_MACHINE_ID_KEY);
        args = new Bundle();
        args.putLong(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);

        return initView(inflater, container, coffeeMachineId);
    }

    public View initView(LayoutInflater inflater, ViewGroup container, long coffeeMachineId) {
      //check empty listview
        ArrayList<Review> reviewList = coffeeApp.getReviewListByStatus(coffeeMachineId,
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
            return emptyView;
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
    public void setHeader(long coffeeMachineId) {
        CoffeeMachineActivity.setHeaderByFragmentId(1, getFragmentManager(), coffeeMachineId);
        mainActivityRef.findViewById(R.id.addReviewSwipeButtonId).setVisibility(View.INVISIBLE);

    }

    public void setReviewPager(final long coffeeMachineId) {
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
        String coffeeMachineName = coffeeApp.coffeeMachineData
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

}

