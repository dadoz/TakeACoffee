package com.application.takeacoffee;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Created with IntelliJ IDEA.
 * User: davide
 * Date: 10/30/13
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoffeMachineReviewsActivity extends SherlockActivity {
    public static String TAG = "CoffeMachineReviewsTAG";

    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar); //Used for theme switching in samples
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reviews_layout);

        try{
            String coffeMachineId = this.getIntent().getExtras().getString("EXTRA_COFFE_MACHINE_ID");


            CoffeMachineDataStorageApplication coffeMachineApplication = ((CoffeMachineDataStorageApplication)this.getApplication());
            ArrayList<Review> reviewList = coffeMachineApplication.coffeMachineData.getReviewListByCoffeMachineId(coffeMachineId);

            if(reviewList != null && reviewList.size() != 0) {
                //Log.d(TAG,">>>>>>>data" + reviewList.get(0).getFeedback() + "----" +reviewList.get(0).getUsername());
                for(Review reviewObj : reviewList) {
                    LayoutInflater inflater = (LayoutInflater)this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout layoutContainer = (LinearLayout)findViewById(R.id.reviewsContainerLayoutId);


                    View reviewTemplate = inflater.inflate(R.layout.review_template, null);
                    layoutContainer.addView(reviewTemplate);
                    //set data to template
                    ((TextView)reviewTemplate.findViewById(R.id.reviewUsernameTextId)).setText(reviewObj.getUsername());
                    ((TextView)reviewTemplate.findViewById(R.id.reviewCommentTextId)).setText(reviewObj.getComment());
                }

            } else {
                Log.d(TAG,">>>>>>> NO REVIEWs FOUND");
            }

        } catch (Exception e) {
            Log.e(TAG, "error - no coffeMachineId retrieved - " + e.getMessage());
        }
    }
    

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar

        menu.add("Save")
            .setIcon(android.R.drawable.ic_menu_save)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        menu.add("Search")
            .setIcon(android.R.drawable.ic_menu_search)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.add("Refresh")
            .setIcon(android.R.drawable.ic_menu_rotate)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //This uses the imported MenuItem from ActionBarSherlock
        Toast.makeText(this, "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
        return true;
    }

}