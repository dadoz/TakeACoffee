package com.application.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import com.application.commons.Common;

/**
 * Created by davide on 15/07/14.
 */
public class SwipePageAction implements View.OnClickListener {
    private int pagePosition;
    private Activity mainActivityRef;
    public SwipePageAction(int pagePosition, Activity mainActivityRef) {
        this.pagePosition = pagePosition;
        this.mainActivityRef = mainActivityRef;
    }
    @Override
    public void onClick(View view) {
        //args coffeeMachineId already added in
//        args.putInt(Common.ARG_PAGE, this.pagePosition);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mainActivityRef);
//            int nextPagePosition = pagePosition == Common.NUM_PAGES ? 0 : pagePosition ++;
        int nextPagePosition = pagePosition + 1;
        if(pagePosition == Common.NUM_PAGES - 1) {
            nextPagePosition = 0;
        }
        localBroadcastManager.sendBroadcast(new Intent("SWIPE_PAGE").putExtra("NEXT_PAGE", nextPagePosition ));
    }
}
