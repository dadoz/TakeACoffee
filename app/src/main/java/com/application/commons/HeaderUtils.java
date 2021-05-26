package com.application.commons;

import android.app.Activity;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.dataRequest.CoffeeAppController;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;
import com.application.takeacoffee.fragments.LoginFragment;
import com.application.takeacoffee.fragments.MapFragment;

/**
 * Created by davide on 15/09/14.
 */
public class HeaderUtils {
//    private static Activity mainActivityRef;

    public HeaderUtils() {
    }
    //set header bar methods

    public static void hideAllItemsOnHeaderBar(Activity mainActivityRef) {
        ViewGroup headerBar = (ViewGroup) mainActivityRef.findViewById(R.id.headerBarLayoutId);
        for (int i = 0; i < headerBar.getChildCount(); i ++) {
            headerBar.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private static void setItemOnHeaderBarById(Activity mainActivityRef, int id, final FragmentManager fragmentManager, String coffeeMachineName) {
//        mainActivityRef.findViewById(R.id.subHeaderBarLayoutId).setVisibility(View.GONE);
        View view = mainActivityRef.findViewById(id);
        view.setVisibility(View.VISIBLE);


        switch (id) {
            case R.id.headerMapButtonId :
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MapFragment mapFragment = new MapFragment();
                        fragmentManager.beginTransaction()
                                .replace(R.id.coffeeMachineContainerLayoutId,
                                        mapFragment).addToBackStack("back").commit();
                    }
                });
                break;
            case R.id.loggedUserButtonId :
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.coffeeMachineContainerLayoutId, new LoginFragment(),
                                        Common.NEW_USER_FRAGMENT_TAG)
                                .addToBackStack("back")
                                .commit();
                    }
                });
                break;
            case R.id.coffeeMachineSettingsMapHeaderLayoutId:
                if (coffeeMachineName != null) {
                    ((TextView) view.findViewById(R.id.coffeeMachineNameReviewTextId))
                            .setText(coffeeMachineName);

                    view.findViewById(R.id.reviewsMachineMapButtonId)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MapFragment mapFragment = new MapFragment();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.coffeeMachineContainerLayoutId,
                                                    mapFragment).addToBackStack("back").commit();
                                }
                            });
                    break;
                }
        }
    }

    public static void setHeaderByFragmentId(Activity mainActivityRef, int fragmentId, FragmentManager fragmentManager, String coffeeMachineId) {
        hideAllItemsOnHeaderBar(mainActivityRef);
        CoffeeAppController coffeeAppController = ((CoffeeMachineActivity) mainActivityRef).getCoffeeAppController();

        switch (fragmentId) {
            case 0:
                hideAllItemsOnHeaderBar(mainActivityRef);
                setItemOnHeaderBarById(mainActivityRef, R.id.headerMapButtonId, fragmentManager, null);
                setItemOnHeaderBarById(mainActivityRef, R.id.loggedUserButtonId, fragmentManager, null);
                break;
            case 1:
//                CoffeeMachineActivity.setItemOnHeaderBarById(R.id.loggedUserButtonId, fragmentManager);
                String coffeeMachineName = coffeeAppController
                        .getCoffeeMachineById(coffeeMachineId).getName();
                setItemOnHeaderBarById(mainActivityRef, R.id.coffeeMachineSettingsMapHeaderLayoutId,
                        fragmentManager, coffeeMachineName);
/*                CoffeeMachineActivity.addItemOnHeaderBarById(R.layout.review_header_template,
                        null, coffeeMachineName,
                        false, fragmentManager);*/
                break;
            case 2:
                mainActivityRef.findViewById(R.id.headerBarLayoutId).setVisibility(View.VISIBLE);
                setItemOnHeaderBarById(mainActivityRef, R.id.headerMapLabelId, null, null);
                break;
            case 3:
                setItemOnHeaderBarById(mainActivityRef, R.id.loggedUserButtonId, fragmentManager, null);
        }

    }


}
