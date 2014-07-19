package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.adapters.AddReviewPagerAdapter;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

/**
 * Created by davide on 26/05/14.
 */
public class AddReviewContainerFragment extends Fragment {
    private static final String TAG = "AddReviewContainerFragment";
    private ViewPager pager;
    private static PagerAdapter pagerAdapter;
    private Bundle args;

    private static CoffeeMachineDataStorageApplication coffeeMachineApplication;


    private static Activity mainActivityRef;
    private View addReviewContainerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        addReviewContainerView = inflater.inflate(R.layout.add_review_container_fragment, container, false);
        //get args from fragment
        String coffeeMachineId = (String)this.getArguments().get(Common.COFFE_MACHINE_ID_KEY);

        //change fragment
        args = new Bundle();
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);

        //get data from application
        coffeeMachineApplication = ((CoffeeMachineDataStorageApplication) this.getActivity()
                .getApplication());

        initView(coffeeMachineId);

        Common.setCustomFont(addReviewContainerView, getActivity().getAssets());
        return addReviewContainerView;
    }

    public void initView(String coffeeMachineId) {
//        setSwipePagerOnMessage();
        setAddReviewPager(coffeeMachineId);
        setHeader(coffeeMachineId);


        View pageSwipeButton = mainActivityRef.findViewById(R.id.addReviewSwipeButtonId); //to be shown
        pageSwipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwipePageByPagePosition(pager.getCurrentItem() + 1);
            }
        });
    }

    public void setHeader(String coffeeMachineId) {
        CoffeeMachineActivity.setHeaderByFragmentId(1, getFragmentManager(), coffeeMachineId);
        mainActivityRef.findViewById(R.id.addReviewSwipeButtonId).setVisibility(View.VISIBLE);

    }

    public void setAddReviewPager(String coffeeMachineId) {
        pager = (ViewPager) addReviewContainerView.findViewById(R.id.pager);
        pager.setAdapter(new AddReviewPagerAdapter(getChildFragmentManager(), coffeeMachineId));

/*        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
               Log.e(TAG, "hey onPageSelected");
            }
        });*/
    }

    public void setSwipePagerOnMessage() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "message");
                //SENDER
                if(intent != null && intent.getAction() != null) {
                    int nextPagePosition = intent.getAction().equals("SWIPE_PAGE") ? intent.getIntExtra("NEXT_PAGE", Common.ITEM_NOT_SELECTED): Log.d(TAG, "no valid action");
                    if(nextPagePosition  != Common.ITEM_NOT_SELECTED ) {
                        Log.d(TAG, "message" + nextPagePosition);
                        pager.setCurrentItem(nextPagePosition);
                    }
                }
            }
        };

        IntentFilter a = new IntentFilter();
        a.addAction("SWIPE_PAGE");
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(broadcastReceiver, a);

    }

    public void setSwipePageByPagePosition(int nextPagePosition) {
        if(nextPagePosition -1 == Common.NUM_PAGES - 1) {
            nextPagePosition = 0;
        }

        if(nextPagePosition  != Common.ITEM_NOT_SELECTED ) {
            Log.d(TAG, "message" + nextPagePosition);
            pager.setCurrentItem(nextPagePosition);
        }

    }
}
