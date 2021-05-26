package com.application.extraMenu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.application.adapters.ReviewListAdapter;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppController;
import com.application.models.Review;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;
import com.application.takeacoffee.fragments.EditReviewFragment;

/**
 * Created by davide on 23/09/14.
 */
public class ExtraMenuController {

    private static final String TAG = "ExtraMenuController";
    public static CoffeeAppController coffeeAppController;

    public static void getEditReviewFragment(FragmentActivity mainActivityRef, String reviewId, String coffeeMachineId,
                                             Common.ReviewStatusEnum reviewStatus) {
        Log.e(TAG, "I'm trying loading fragment");
        //change fragment
        Bundle args = new Bundle();
        args.putString(Common.REVIEW_ID, reviewId);
        args.putString(Common.COFFEE_MACHINE_ID_KEY, coffeeMachineId);
        args.putString(Common.REVIEW_STATUS_KEY, reviewStatus.name());

        EditReviewFragment reviewsFrag = new EditReviewFragment();
        reviewsFrag.setArguments(args);

        mainActivityRef.getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeMachineContainerLayoutId, reviewsFrag)
                .addToBackStack("back")
                .commit();
    }

    public static void alertDialogDeleteReview(FragmentActivity mainActivityRef, final String coffeeMachineId,
                                               final ReviewListAdapter adapter,
                                               final Review reviewSelectedItem) {

        coffeeAppController = ((CoffeeMachineActivity) mainActivityRef).getCoffeeAppController();

        Dialog dialog = new AlertDialog.Builder(mainActivityRef)
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete your review?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        coffeeAppController.removeReviewById(coffeeMachineId, reviewSelectedItem);
                        adapter.remove(reviewSelectedItem);
                        adapter.setSelectedItemIndex(Common.ITEM_NOT_SELECTED);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                }).create();

        dialog.show();
    }


    public static void extra(View view, ReviewListAdapter listAdapter) {
        //DONT MOVE FROM HERE OTW EVERY TIME SET ON CLICK it try to exec findViewById - so slow
        try {
            View mainItemView = ((View) view.getParent().getParent()).
                    findViewById(R.id.mainItemViewId);
            View extraMenuItemView = ((View) view.getParent().getParent()).
                    findViewById(R.id.extraMenuItemViewId);
            mainItemView.setVisibility(View.VISIBLE);
            extraMenuItemView.setVisibility(View.GONE);
            //adapterView.setTag(null);
            listAdapter.setSelectedItemIndex(Common.ITEM_NOT_SELECTED);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
