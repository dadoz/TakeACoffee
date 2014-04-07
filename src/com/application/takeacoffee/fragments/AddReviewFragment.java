package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.gestureDetector.SwipeDetector;
import com.application.takeacoffee.R;

/**
 * Created by davide on 05/04/14.
 */
public class AddReviewFragment extends Fragment {
    private static final String TAG = "AddReviewFragment";
    private static LinearLayout addReviewViewContainerId;
    private static View goodReviewView, notSoBadReviewView, worstReviewView;
    private static TextView goodReviewTab, notSoBadReviewTab, worstReviewTab;

    private static Activity mainActivityRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)  {
        mainActivityRef = getActivity();
        View addReviewView = inflater.inflate(R.layout.add_review_fragment, container, false);

        addReviewViewContainerId = (LinearLayout)addReviewView.findViewById(R.id.addReviewViewContainerLayoutId);
        goodReviewView = inflater.inflate(R.layout.good_review_fragment, container, false);
        notSoBadReviewView = inflater.inflate(R.layout.not_bad_review_fragment, container, false);
        worstReviewView = inflater.inflate(R.layout.worst_review_fragment, container, false);
        goodReviewTab = (TextView)addReviewView.findViewById(R.id.goodTabTextId);
        notSoBadReviewTab = (TextView)addReviewView.findViewById(R.id.notBadTabTextId);
        worstReviewTab = (TextView)addReviewView.findViewById(R.id.worstTabTextId);

        goodReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeReview(1);
                removeReview(2);
                initAddReview(0);
            }
        });
        notSoBadReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeReview(2);
                removeReview(0);
                initAddReview(1);
            }
        });
        worstReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeReview(1);
                removeReview(0);
                initAddReview(2);
            }
        });

        initAddReview(0);
        Common.setCustomFont(addReviewView, getActivity().getAssets());
        return addReviewView;
    }

    public static void initAddReview(int kindOfReview) {
        goodReviewTab.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_grey));
        notSoBadReviewTab.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_grey));
        worstReviewTab.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_grey));

        switch(kindOfReview) {
            case 0:
                addReviewViewContainerId.addView(goodReviewView);
                goodReviewView.setOnTouchListener(new SwipeDetector(0));
                goodReviewView.setOnClickListener(null);
                goodReviewTab.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.middle_grey));
                Common.setCustomFont(goodReviewView, mainActivityRef.getAssets());
                break;
            case 1:
                addReviewViewContainerId.addView(notSoBadReviewView);
                notSoBadReviewView.setOnTouchListener(new SwipeDetector(1));
                notSoBadReviewView.setOnClickListener(null);
                notSoBadReviewTab.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.middle_grey));
                Common.setCustomFont(notSoBadReviewView, mainActivityRef.getAssets());
                break;
            case 2:
                addReviewViewContainerId.addView(worstReviewView);
                worstReviewView.setOnTouchListener(new SwipeDetector(2));
                worstReviewView.setOnClickListener(null);
                worstReviewTab.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.middle_grey));
                Common.setCustomFont(worstReviewView, mainActivityRef.getAssets());
                break;
        }
    }

    public static void removeReview(int kindOfReview) {
        switch(kindOfReview) {
            case 0:
                addReviewViewContainerId.removeView(goodReviewView);
            case 1:
                addReviewViewContainerId.removeView(notSoBadReviewView);
            case 2:
                addReviewViewContainerId.removeView(worstReviewView);
        }
    }

    public static void swipeFragment(SwipeDetector.Action action, int swipeCounter) {
        if(action == SwipeDetector.Action.LR) {
            Log.d(TAG, "caught left swipe action - " + swipeCounter);
            removeReview(swipeCounter + 1);
            initAddReview(swipeCounter);
        } else if(action == SwipeDetector.Action.RL) {
            Log.d(TAG, "caught right swipe action");
            removeReview(swipeCounter - 1);
            initAddReview(swipeCounter);
        }
    }

}
