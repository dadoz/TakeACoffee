package com.application.gestureDetector;

/**
 * Created by davide on 05/04/14.
 */

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.application.commons.Common;

public class SwipeDetector implements View.OnTouchListener {

    private int swipeLeftCounter;
    private static int MAX_SWIPE_COUNTER = 2;
    private static int MIN_SWIPE_COUNTER = 0;

    public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None, // when no action was detected
        Click
    }

    private static final String TAG = "SwipeDetector";
    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    private Action mSwipeDetected = Action.None;
    private String coffeeMachineId;

    public SwipeDetector(String coffeeMachineId, Common.ReviewStatusEnum reviewStatus) {
        this.swipeLeftCounter = convertStatusToCounter(reviewStatus);
        this.coffeeMachineId = coffeeMachineId;
    }

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                // Log.i(logTag, "Click On List" );
                return false; // allow other events like Click to be processed
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        mSwipeDetected = Action.LR;

                        if(swipeLeftCounter > MIN_SWIPE_COUNTER) {
                            this.swipeLeftCounter--;
                            Log.e(TAG, "LEFT swipe");
                            actionOnSwipe(this.coffeeMachineId, convertCounterToStatus(this.swipeLeftCounter));
                        }
                        return false;
                    }
                    if (deltaX > 0) {

                        mSwipeDetected = Action.RL;
                        if(swipeLeftCounter < MAX_SWIPE_COUNTER) {
                            this.swipeLeftCounter++;
                            Log.e(TAG, "RIGHT swipe");
                            actionOnSwipe(this.coffeeMachineId, convertCounterToStatus(this.swipeLeftCounter));
                        }
                        return false;
                    }
                }
            /*
             * else
             *
             * // vertical swipe detection if (Math.abs(deltaY) > MIN_DISTANCE)
             * { // top or down if (deltaY < 0) { Log.i(logTag,
             * "Swipe Top to Bottom"); mSwipeDetected = Action.TB; return false;
             * } if (deltaY > 0) { Log.i(logTag, "Swipe Bottom to Top");
             * mSwipeDetected = Action.BT; return false; } }
             */

                mSwipeDetected = Action.Click;
                return false;
            }
        }
        return false;
    }

    private void actionOnSwipe(String coffeeMachineId, Common.ReviewStatusEnum reviewStatus) {
//        AddReviewFragment.swipeFragment(coffeeMachineId, reviewStatus);
    }

    private int convertStatusToCounter(Common.ReviewStatusEnum reviewStatus) {
        switch (reviewStatus) {
            case GOOD:
                return 0;
            case NOTSOBAD:
                return 1;
            case WORST:
                return 2;
        }
        return -1;
    }

    private Common.ReviewStatusEnum convertCounterToStatus(int counter) {
        switch (counter) {
            case 0:
                return Common.ReviewStatusEnum.GOOD;
            case 1:
                return Common.ReviewStatusEnum.NOTSOBAD;
            case 2:
                return Common.ReviewStatusEnum.WORST;
        }
        return Common.ReviewStatusEnum.NOTSET;
    }

}