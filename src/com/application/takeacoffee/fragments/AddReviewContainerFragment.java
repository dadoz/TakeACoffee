package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.adapters.AddReviewPagerAdapter;
import com.application.commons.Common;
import com.application.takeacoffee.R;

/**
 * Created by davide on 26/05/14.
 */
public class AddReviewContainerFragment extends Fragment {
    private static final String TAG = "AddReviewContainerFragment";
    private static final int INIT_POS_ADDREVIEW_TAB = 0;
    private ViewPager pager;
    private static PagerAdapter pagerAdapter;
    private Bundle args;

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

        View headerTabReviewLayout = mainActivityRef.findViewById(R.id.headerTabReviewLayoutId);

        setAddReviewPager(coffeeMachineId, (ViewGroup) headerTabReviewLayout);
        setAddReviewHeader();

        Common.setCustomFont(addReviewContainerView, getActivity().getAssets());
        return addReviewContainerView;
    }

    public void setAddReviewHeader() {
        View headerMapLayout = mainActivityRef.findViewById(R.id.headerMapLayoutId);
        headerMapLayout.setVisibility(View.GONE);
/*        ViewGroup headerTabReviewLayout = (ViewGroup) mainActivityRef.findViewById(R.id.headerTabReviewLayoutId);
        headerTabReviewLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < Common.NUM_PAGES; i++) {
            headerTabReviewLayout.getChildAt(i)
                    .setBackgroundColor(mainActivityRef.getResources()
                            .getColor(R.color.light_grey));
        }
        headerTabReviewLayout.getChildAt(INIT_POS_ADDREVIEW_TAB)
                .setBackgroundColor(mainActivityRef.getResources()
                        .getColor(R.color.light_green));*/
    }

    public void setAddReviewPager(String coffeeMachineId, final ViewGroup headerTabReviewLayout) {
        pager = (ViewPager) addReviewContainerView.findViewById(R.id.pager);
        pager.setAdapter(new AddReviewPagerAdapter(getChildFragmentManager(), coffeeMachineId));
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                try {
                    //reset color background of tabs
/*                    for (int i = 0; i < Common.NUM_PAGES; i++) {
                        headerTabReviewLayout.getChildAt(i)
                                .setBackgroundColor(mainActivityRef.getResources()
                                        .getColor(R.color.light_grey));
                    }

                    Common.ReviewStatusEnum reviewStatus = Common.parseStatusFromPageNumber(position);
                    switch (reviewStatus) {
                        case GOOD:
                            headerTabReviewLayout.getChildAt(position)
                                    .setBackgroundColor(mainActivityRef.getResources()
                                            .getColor(R.color.light_green));
                            break;
                        case NOTSOBAD:
                            headerTabReviewLayout.getChildAt(position)
                                    .setBackgroundColor(mainActivityRef.getResources()
                                            .getColor(R.color.light_yellow_lemon));
                            break;
                        case WORST:
                            headerTabReviewLayout.getChildAt(position)
                                    .setBackgroundColor(mainActivityRef.getResources()
                                            .getColor(R.color.light_violet));
                            break;
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
