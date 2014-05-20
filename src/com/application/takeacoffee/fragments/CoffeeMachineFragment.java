package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.CoffeeMachine;
import com.application.takeacoffee.R;

import java.util.ArrayList;

import static com.application.commons.Common.setCustomFontByView;
import static com.application.takeacoffee.CoffeeMachineActivity.addReviewByFragment;
import static com.application.takeacoffee.fragments.NewUserFragment.getRoundedRectBitmap;

/**
 * Created by davide on 3/13/14.
 */
public class CoffeeMachineFragment extends Fragment {
    private static final String TAG = "coffeeMachineFragment";
    private ArrayList<CoffeeMachine> coffeeMachineList;
    private ArrayList<PieChart> pieChartList;
    private Handler mHandler;
    private static Activity mainActivityRef;
    private static LinearLayout settingsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mHandler = new Handler();
        mainActivityRef = getActivity();
        //get views
        final View coffeeMachineFragment = inflater.inflate(R.layout.coffe_machine_fragment, container, false);
        settingsLayout = (LinearLayout)coffeeMachineFragment.findViewById(R.id.settingsLayoutId);

        //get data from application
        coffeeMachineList = ((CoffeeMachineDataStorageApplication) getActivity().getApplication())
                .coffeeMachineData.getCoffeeMachineList();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initView(coffeeMachineFragment);
            }
        });

        //header stuff
        setHeaderAction(coffeeMachineFragment);
        //set custom font
        Common.setCustomFont(coffeeMachineFragment, this.getActivity().getAssets());
        return coffeeMachineFragment;
    }

    private void initView(View coffeeMachineFragment) {
        int itemInTableRowCounter = 0;
        TableRow tableRow = null;

        if (coffeeMachineList != null && coffeeMachineList.size() != 0) {
            for (final CoffeeMachine coffeeMachineObj : coffeeMachineList) {
//                Log.e(TAG, "coffeMachineData - " + coffeeMachineObj.getAddress() +
//                        " - name - " + coffeeMachineObj.getName());
                TableLayout cmfTableLayoutContainer = (TableLayout) coffeeMachineFragment
                        .findViewById(R.id.coffeeMachineTableLayoutId);

                if (itemInTableRowCounter == 0) {
                    tableRow = new TableRow(getActivity());
                    tableRow.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                    tableRow.setPadding(0, 40, 0, 40);
                }

                LayoutInflater inflaterLayout = (LayoutInflater) getActivity()
                        .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                final LinearLayout coffeeMachineTemplate = (LinearLayout) inflaterLayout
                        .inflate(R.layout.coffe_machine_template, null);
                coffeeMachineTemplate.setLayoutParams(new TableRow.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)); // it make no sense :O
                coffeeMachineTemplate.setGravity(Gravity.CENTER_HORIZONTAL);


                Animation anim = AnimationUtils.loadAnimation(getActivity().getBaseContext(), R.anim.zoom_in);
                anim.setDuration(Common.ANIMATION_GROW_TIME);
                coffeeMachineTemplate.startAnimation(anim);
                tableRow.addView(coffeeMachineTemplate);

                if (itemInTableRowCounter != 0 || coffeeMachineList.
                        indexOf(coffeeMachineObj) == (coffeeMachineList.size() - 1)) {
                    cmfTableLayoutContainer.addView(tableRow);
                }

                //reset counter
                if (itemInTableRowCounter == 0) {
                    itemInTableRowCounter++;
                } else {
                    itemInTableRowCounter = 0;
                }

                //set data to template
                setCustomFontByView(getActivity().getAssets(), coffeeMachineTemplate.findViewById(
                        R.id.coffeeMachineNameTextId), false);
                ((TextView) coffeeMachineTemplate.findViewById(R.id.coffeeMachineNameTextId))
                        .setText(coffeeMachineObj.getName());
                ((TextView) coffeeMachineTemplate.findViewById(R.id.coffeeMachineNameTextId))
                        .setTextColor(getResources().getColor(R.color.light_black));
                //coffee machine update
                ((TextView) coffeeMachineTemplate.findViewById(R.id.coffeeMachineLastUpdateTextId))
                        .setText("Last update: 01.02.14");
                setCustomFontByView(getActivity().getAssets(), coffeeMachineTemplate.findViewById(
                        R.id.coffeeMachineLastUpdateTextId), false);


                //TODO refactor please
                String iconPath = (coffeeMachineObj.getIconPath());
                int picResource = -1;
                if (iconPath.equals(new String("coffee1.jpg"))) {
                    picResource = R.drawable.coffee1;
                } else if (iconPath.equals(new String("coffee2.jpg"))) {
                    picResource = R.drawable.coffee2;
                } else if (iconPath.equals(new String("coffee3.jpg"))) {
                    picResource = R.drawable.coffee3;
                } else if (iconPath.equals(new String("coffee4.jpg"))) {
                    picResource = R.drawable.coffee4;
                }
                ImageView coffeePic = (ImageView) coffeeMachineTemplate.findViewById(R.id.coffeeIconId);

                try {
                    //make piechart
                    pieChartList = getPieChartData();

                    Bitmap bmpAbove = getRoundedBitmapByPicPath(picResource);
                    Bitmap bmpBelow = NewUserFragment.getRoundedBitmap(Common.PROFILE_PIC_CIRCLE_MASK_BIGGER_SIZE,
                            getResources().getColor(R.color.light_black));
                    Bitmap coffeeMachineBmp = Common.overlayBitmaps(bmpBelow, bmpAbove);
                    coffeePic.setImageBitmap(coffeeMachineBmp);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "failed to load profile pic from storage - load the guest one");
                    coffeePic.setImageResource(R.drawable.coffe_cup_black_white);
                }

                (coffeeMachineTemplate.findViewById(R.id.coffeeIconId)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
//                        final String coffeeMachineId = coffeeMachineObj.getId();
                        getCoffeeMachineReviewById(coffeeMachineObj.getId());
                    }
                });
            }
        }

        //set action to settings view
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.displayError("settings view", mainActivityRef);
                addReviewByFragment();

            }
        });

    }

    private void setHeaderAction(final View coffeeMachineFragment) {
        mainActivityRef.findViewById(R.id.headerMapLayoutId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View mapContainerLayout = coffeeMachineFragment.findViewById(R.id.mapContainerLayoutId);
                View coffeeMachineTableLayout = coffeeMachineFragment.findViewById(R.id.coffeeMachineTableLayoutId);
                mapContainerLayout.setVisibility(View.VISIBLE);
                coffeeMachineTableLayout.setVisibility(View.GONE);
            }
        });
    }

    private boolean getCoffeeMachineReviewById(String coffeMachineId){
        //change fragment
        Bundle args = new Bundle();
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeMachineId);

        ReviewsFragment reviewsFrag = new ReviewsFragment();
        reviewsFrag.setArguments(args);

        getFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.fade_in,
//                        R.anim.fade_out)
                .replace(R.id.coffeeMachineContainerLayoutId, reviewsFrag)
                .addToBackStack("back")
                .commit();
        return true;
    }


    public ArrayList<PieChart> getPieChartData() {
        ArrayList<PieChart> pieChartList = new ArrayList<PieChart>();

        int reviewNumber, reviewTotal = 8, cnt = 0;
        reviewNumber = 5;
        cnt += reviewNumber;
        pieChartList.add(new PieChart(getResources().getColor(R.color.light_green), (360 * reviewNumber) / reviewTotal));
        reviewNumber = 2;
        cnt += reviewNumber;
        pieChartList.add(new PieChart(getResources().getColor(R.color.light_yellow), (360 * reviewNumber) / reviewTotal));
        reviewNumber = 1;
        cnt += reviewNumber;
        pieChartList.add(new PieChart(getResources().getColor(R.color.light_black), (360 * reviewNumber) / reviewTotal));
        if(cnt != reviewTotal) {
            reviewNumber = reviewTotal - cnt;
            pieChartList.add(new PieChart(getResources().getColor(R.color.middle_grey), (360 * reviewNumber) / reviewTotal));
        }

        return pieChartList;
    }


    public static Bitmap getRoundedBitmapByPicPath(int pictureName) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(mainActivityRef.getResources(), pictureName);
            Bitmap roundedBitmap = getRoundedRectBitmap(bitmap, Common.PROFILE_PIC_CIRCLE_MASK_SIZE);

            return roundedBitmap;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
