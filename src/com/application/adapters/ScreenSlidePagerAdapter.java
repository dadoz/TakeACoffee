package com.application.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.application.commons.Common;
import com.application.takeacoffee.fragments.AddReviewFragment;

/**
 * A simple pager adapter that represents 5 {@link com.application.takeacoffee.fragments} objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private String coffeeMachineId;

    public ScreenSlidePagerAdapter(FragmentManager fm, String coffeeMachineId) {
        super(fm);
        this.coffeeMachineId = coffeeMachineId;
    }

    @Override
    public Fragment getItem(int position) {
        return AddReviewFragment.create(position, coffeeMachineId);
    }

    @Override
    public int getCount() {
        return Common.NUM_PAGES;
    }
}
