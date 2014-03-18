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
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.CoffeMachine;
import com.application.models.Review;
import com.application.takeacoffee.R;

import java.util.ArrayList;

/**
 * Created by davide on 3/13/14.
 */
public class CoffeMachineFragment extends Fragment {
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
        TableLayout tableLayout = null;
        TableRow tableRow = null;
        if(coffeMachineList != null &&  coffeMachineList.size() != 0) {
            for(CoffeMachine coffeMachineObj : coffeMachineList) {
                Log.e(TAG, "coffeMachineData - " + coffeMachineObj.getAddress() + coffeMachineObj.getName());
                LinearLayout cmfLayoutContainer = (LinearLayout)coffeMachineFragment.findViewById(R.id.coffeeMachineFragmentLayoutId);

                if(itemInTableRowCounter == 0) {
                    tableLayout = new TableLayout(this.getActivity());
  //                  tableLayout.setBackgroundColor(getResources().getColor(R.color.coffe_ligth));
    //                TableLayout.LayoutParams tableLayoutParam = (new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    //                tableLayoutParam.setMargins(0, 0, 0, 200);
    //                tableLayout.setLayoutParams(tableLayoutParam);

                    //fill my table layout
                    tableRow = new TableRow(this.getActivity());
                }

                LayoutInflater inflaterLayout = (LayoutInflater)this.getActivity().getSystemService(this.getActivity().LAYOUT_INFLATER_SERVICE);
                View coffeeMachineTemplate = inflaterLayout.inflate(R.layout.coffe_machine_template, null);

                tableRow.addView(coffeeMachineTemplate);
//                tableRow.setBackgroundColor(getResources().getColor(R.color.light_black));
/*                TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f);
                tableRow.setLayoutParams(tableRowParams);
*/
                if(itemInTableRowCounter != 0) {
                    tableLayout.addView(tableRow);
                    cmfLayoutContainer.addView(tableLayout);
                }

                //reset counter
                if(itemInTableRowCounter == 0) {
                    itemInTableRowCounter ++;
                } else {
                    itemInTableRowCounter = 0;
                }

                //set data to template
//                ((TextView)coffeMachineTemplate.findViewById(R.id.coffeMachineAddressTextId)).setText(coffeMachineObj.getAddress());
//                ((TextView)coffeMachineTemplate.findViewById(R.id.coffeMachineNameTextId)).setText(coffeMachineObj.getName());

                final String coffeMachineId = coffeMachineObj.getId();
                (coffeeMachineTemplate.findViewById(R.id.coffeeIconId)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.e(TAG,"u clicked");
                        getCoffeMachineReviewById(coffeMachineId, true);
                    }
                });
            }
        }
        return coffeMachineFragment;
    }

    private boolean getCoffeMachineReviewById(String coffeMachineId, boolean coffeMachineDataAvailable){

        if(coffeMachineDataAvailable) {
            //check if coffeMachineId exist - I DONT KNOW if tis better to use this fx instead of impl method on list
            ArrayList<Review> reviewList = coffeMachineApplication.coffeeMachineData.getReviewListByCoffeMachineId(coffeMachineId);
            if(reviewList == null) {
                Log.e(TAG,"still not implemented - no one coffeeMachine owned by this ID");
                return false;
            }
        }
        //change fragment

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out);
        ft.replace(R.id.coffeeMachineContainerLayoutId, new ReviewsFragment());
        ft.addToBackStack("back");
        ft.commit();
        return true;
    }

}
