package com.application.takeacoffee.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.CoffeMachine;
import com.application.takeacoffee.R;

import java.util.ArrayList;

/**
 * Created by davide on 3/13/14.
 */
public class CoffeeMachineFragment extends Fragment {
    private static final String TAG = "coffeMachineFragment";
    private CoffeeMachineDataStorageApplication coffeMachineApplication;
    private ArrayList<CoffeMachine> coffeMachineList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        /***get data from JSON****/
        //CoffeMachine[] coffeMachineList = RetrieveDataFromServer.getCoffeMachineData();
        //get data from application
        coffeMachineApplication = ((CoffeeMachineDataStorageApplication) this.getActivity().getApplication());
        coffeMachineList = coffeMachineApplication.coffeeMachineData.getCoffeMachineList();
        View coffeMachineFragment = inflater.inflate(R.layout.coffe_machine_fragment, container, false);

        int itemInTableRowCounter = 0;
//        TableLayout tableLayout = null;
        TableRow tableRow = null;
        if(coffeMachineList != null &&  coffeMachineList.size() != 0) {
            for(CoffeMachine coffeMachineObj : coffeMachineList) {
                Log.e(TAG, "coffeMachineData - " + coffeMachineObj.getAddress() + coffeMachineObj.getName());
                TableLayout cmfTableLayoutContainer = (TableLayout)coffeMachineFragment.findViewById(R.id.coffeeMachineTableLayoutId);

                if(itemInTableRowCounter == 0) {
                    //fill my table layout
                    tableRow = new TableRow(this.getActivity());
                }

                LayoutInflater inflaterLayout = (LayoutInflater)this.getActivity().getSystemService(this.getActivity().LAYOUT_INFLATER_SERVICE);
                LinearLayout coffeeMachineTemplate = (LinearLayout)inflaterLayout.inflate(R.layout.coffe_machine_template, null);
                coffeeMachineTemplate.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1.0f));
                tableRow.addView(coffeeMachineTemplate);

                if(itemInTableRowCounter != 0) {
                  cmfTableLayoutContainer.addView(tableRow);
                }

                //reset counter
                if(itemInTableRowCounter == 0) {
                    itemInTableRowCounter ++;
                } else {
                    itemInTableRowCounter = 0;
                }

                //set data to template
//                ((TextView)coffeMachineTemplate.findViewById(R.id.coffeMachineAddressTextId)).setText(coffeMachineObj.getAddress());
                ((TextView)coffeeMachineTemplate.findViewById(R.id.coffeeMachineNameTextId)).setText(coffeMachineObj.getName());

                final String coffeMachineId = coffeMachineObj.getId();
                (coffeeMachineTemplate.findViewById(R.id.coffeeIconId)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.e(TAG, "u clicked");
                        getCoffeeMachineReviewById(coffeMachineId);
                    }
                });
            }
        }

        //set custom font
        Common.setCustomFont(coffeMachineFragment, this.getActivity().getAssets());
        return coffeMachineFragment;
    }

    private boolean getCoffeeMachineReviewById(String coffeMachineId){
        //change fragment
        Bundle args = new Bundle();
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeMachineId);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out);

        ReviewsFragment reviewsFrag = new ReviewsFragment();
        reviewsFrag.setArguments(args);
        ft.replace(R.id.coffeeMachineContainerLayoutId, reviewsFrag);
        ft.addToBackStack("back");
        ft.commit();
        return true;
    }

}
