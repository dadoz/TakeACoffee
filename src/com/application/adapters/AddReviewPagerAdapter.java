package com.application.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.application.commons.Common;
import com.application.takeacoffee.fragments.AddReviewFragment;

public class AddReviewPagerAdapter extends FragmentStatePagerAdapter {
    private long coffeeMachineId;

    public AddReviewPagerAdapter(FragmentManager fm, long coffeeMachineId) {
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
