package com.application.takeacoffee;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.application.commons.Common;

/**
 * Created with IntelliJ IDEA.
 * User: davide
 * Date: 10/30/13
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoffeMachineReviewsActivity extends SherlockActivity {
    public static String TAG = "CoffeMachineReviewsTAG";
    private CoffeMachineDataStorageApplication coffeMachineApplication;
    
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar); //Used for theme switching in samples
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reviews_layout);

        initView();
    }
    
	private void initView(){
        try{
            String coffeMachineId = this.getIntent().getExtras().getString("EXTRA_COFFE_MACHINE_ID");


            coffeMachineApplication = ((CoffeMachineDataStorageApplication)this.getApplication());
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
                LayoutInflater inflater = (LayoutInflater)this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layoutContainer = (LinearLayout)findViewById(R.id.reviewsContainerLayoutId);


                View emptyDataTemplate = inflater.inflate(R.layout.empty_data_layout, null);
                layoutContainer.addView(emptyDataTemplate);
                
                ((TextView)emptyDataTemplate.findViewById(R.id.emptyDataTextViewId)).setText("No reviews for this machine");
            }

        } catch (Exception e) {
            Log.e(TAG, "error - no coffeMachineId retrieved - " + e.getMessage());
        }

		Button addReviewBtn = (Button)findViewById(R.id.addReviewButtonId);
		addReviewBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!coffeMachineApplication.coffeMachineData.getRegisteredUserStatus()){
					Log.e(TAG, ">>> please insert username at least");
					
					//TEST
					String username ="dadoz";
					SharedPreferences sharedPref = getPreferences(0);
					sharedPref.edit().putString(Common.REGISTERED_USERNAME, username);
					coffeMachineApplication.coffeMachineData.initRegisteredUser(username);
					
	//				SherlockDialogFragment dialog = new RegisterUserDialogFragment();
//					dialog.show(manager, TAG);
				}
				Intent intent = new Intent(CoffeMachineReviewsActivity.this,AddReviewActivity.class);
				startActivity(intent);
			}
		});
	}
    
	public class RegisterUserDialogFragment extends SherlockDialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("hey message")
	               .setPositiveButton("ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // FIRE ZE MISSILES!
	                   }
	               })
	               .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
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