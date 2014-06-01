package com.application.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.application.takeacoffee.fragments.ChoiceReviewFragment;
import com.application.commons.Common;

/**
 * A simple pager adapter that represents 5 {@link com.application.takeacoffee.fragments} objects, in
 * sequence.
 */
public class ChoiceReviewPagerAdapter extends FragmentStatePagerAdapter {
    private String coffeeMachineId;
    private boolean isTodayReviews;
    public ChoiceReviewPagerAdapter(FragmentManager fm, String coffeeMachineId, boolean isTodayReviews) {
        super(fm);
        this.coffeeMachineId = coffeeMachineId;
        this.isTodayReviews = isTodayReviews;
    }

    @Override
    public Fragment getItem(int position) {
        return ChoiceReviewFragment.create(position, coffeeMachineId, isTodayReviews);
    }

    @Override
    public int getCount() {
        return Common.NUM_PAGES;
    }
}
