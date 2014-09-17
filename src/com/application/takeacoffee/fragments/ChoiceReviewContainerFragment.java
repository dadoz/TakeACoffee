package com.application.takeacoffee.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.adapters.ChoiceReviewPagerAdapter;
import com.application.commons.Common;
import com.application.commons.HeaderUtils;
import com.application.dataRequest.CoffeeAppLogic;
import com.application.dataRequest.RESTLoader;
import com.application.datastorage.DataStorageSingleton;
import com.application.models.Review;
import com.application.models.ReviewCounter;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

/**
 * Created by davide on 3/16/14.
 */
public class ChoiceReviewContainerFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse> {

    private static final String TAG = "ChoiceReviewContainerFragment";
    private DataStorageSingleton coffeeApp;

    private CoffeeAppLogic coffeeAppLogic;
    private Bundle args;
    private static FragmentActivity mainActivityRef;
    private View reviewsLayoutView;

    private ViewPager mPager;
    private static PagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        //get data from application
        coffeeApp = DataStorageSingleton.getInstance(mainActivityRef.getApplicationContext());
        coffeeAppLogic = new CoffeeAppLogic(mainActivityRef.getApplicationContext());

        //fragment bundle
        // TODO refactor it set bundle
        String coffeeMachineId = this.getArguments().getString(Common.COFFEE_MACHINE_ID_KEY);
        args = new Bundle();
        args.putString(Common.COFFEE_MACHINE_ID_KEY, coffeeMachineId);

        DateTime dateTime = new DateTime();
        long todayTimestamp = CoffeeAppLogic.TimestampHandler.getTodayTimestamp(dateTime);
        long yesterdayTimestamp = CoffeeAppLogic.TimestampHandler.getOneWeekAgoTimestamp(dateTime);;

        //init loader
//        Bundle bundle = choiceReviewLoaderBundle(coffeeMachineId, todayTimestamp, Common.DATE_NOT_SET);
        Bundle bundle = choiceReviewLoaderBundle(coffeeMachineId, yesterdayTimestamp, todayTimestamp);
        getLoaderManager().initLoader(0, bundle, this).forceLoad();
        return initView(inflater, container, coffeeMachineId);
    }

/*
    private View setReviewLoader(final LayoutInflater inflater, final ViewGroup container, final String coffeeMachineId) {
        //data are stored in reviewList
        reviewsLayoutView  = inflater.inflate(R.layout.reviews_fragment, container, false);
        reviewsLayoutView .findViewById(R.id.reviewLoaderLayoutId).setVisibility(View.VISIBLE);
        //load async data
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                DateTime dateTime = new DateTime();
                long oneWeekAgoTimestamp = CoffeeAppLogic.TimestampHandler.getOneWeekAgoTimestamp(dateTime);
                long todayTimestamp = CoffeeAppLogic.TimestampHandler.getTodayTimestamp(dateTime);
                coffeeAppLogic.setAndCountPreviousReviewList(coffeeApp, coffeeMachineId,
                        oneWeekAgoTimestamp, todayTimestamp);
                coffeeAppLogic.getUserToAllReviews(coffeeMachineId);

                return null;
            }

            @Override
            protected void onPostExecute(String string) {
                Log.e(TAG, "hey post execute " + string);
                //container.removeView(reviewLoaderView);
//                container.addView(initView(inflater, container, coffeeMachineId));
                reviewsLayoutView .findViewById(R.id.reviewLoaderLayoutId).setVisibility(View.GONE);
//                initView(inflater, container, coffeeMachineId);

            }
        }.execute("params");

//        reviewLoaderView.findViewById(R.id.reviewLoaderLayoutId).setVisibility(View.VISIBLE);
//        return reviewLoaderView;
        return null;
    }*/

    public View initView(LayoutInflater inflater, ViewGroup container, String coffeeMachineId) {
        //data are stored in reviewList
        reviewsLayoutView = inflater.inflate(R.layout.reviews_fragment, container, false);
//        inflater.inflate(R.layout.review_loader_layout, container, false);

        setLoaderView(reviewsLayoutView, true);
        setSwipePagerOnMessage();
        setReviewPager(coffeeMachineId);
        setHeader(coffeeMachineId);
        //set custom font
        Common.setCustomFont(reviewsLayoutView, this.getActivity().getAssets());
        return reviewsLayoutView;
    }

    private void setLoaderView(View view, boolean showLoader) {
        if(showLoader) {
            view.findViewById(R.id.todayReviewsContainerId).setVisibility(View.GONE);
            view.findViewById(R.id.reviewLoaderLayoutId).setVisibility(View.VISIBLE);
            return;
        }
        view.findViewById(R.id.todayReviewsContainerId).setVisibility(View.VISIBLE);
        view.findViewById(R.id.reviewLoaderLayoutId).setVisibility(View.GONE);
    }


    public View initViewOld(LayoutInflater inflater, ViewGroup container, String coffeeMachineId) {

        ArrayList<Review> reviewList = coffeeAppLogic.getReviewListByStatus(coffeeMachineId,
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
    public void setHeader(String coffeeMachineId) {
        HeaderUtils.setHeaderByFragmentId(mainActivityRef, 1, getFragmentManager(), coffeeMachineId);
        mainActivityRef.findViewById(R.id.addReviewSwipeButtonId).setVisibility(View.INVISIBLE);

    }

    public void setReviewPager(final String coffeeMachineId) {
        //boolean isTodayReview = false;
        mPager = (ViewPager) reviewsLayoutView.findViewById(R.id.reviewsPagerId);
        mPagerAdapter = new ChoiceReviewPagerAdapter(getChildFragmentManager(), coffeeMachineId);
        mPager.setAdapter(mPagerAdapter);

    }

    public void setSwipePagerOnMessage() {
//        HeaderUtils.handler.handleMessage();
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

    private Bundle choiceReviewLoaderBundle(String coffeeMachineId, long fromTimestamp, long toTimestamp) {
        //set params to get choice Review data
        //        return "{\"coffeeMachineId\" : \"PZrB82ZWVl\", \"fromReviewId\": \"qaeWjprTDF\"}";
        Bundle bundle = new Bundle();
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("coffeeMachineId", coffeeMachineId);
            paramsObj.put("toTimestamp", toTimestamp);
            paramsObj.put("fromTimestamp", fromTimestamp);
            bundle.putString("params", paramsObj.toString());
//        bundle.putString("params", "{\"coffeeMachineId\" : \" " + coffeeMachineId + "\"}");
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Loader<RESTLoader.RESTResponse> onCreateLoader(int i, Bundle bundle) {
        try {
            int verb = RESTLoader.HTTPVerb.POST;
            Uri action = Uri.parse("https://api.parse.com/1/functions/countOnReviewsWithTimestamp");

            return new RESTLoader(this.getActivity(), verb, action, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<RESTLoader.RESTResponse> restResponseLoader, RESTLoader.RESTResponse restResponse) {
        String data = restResponse.getData();
        Log.d(TAG, "this are data result" + data);

        ReviewCounter reviewCounter = restResponse.parseCountOnReviewsData(data);
        coffeeApp.addOnReviewCounterList(reviewCounter);
        setLoaderView(reviewsLayoutView, false);
    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> restResponseLoader) {

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

