package com.application.takeacoffee;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.CoffeMachine;
import com.application.models.Review;
import com.application.takeacoffee.fragments.CoffeMachineFragment;
import com.application.takeacoffee.fragments.ReviewsFragment;

import java.util.ArrayList;

//import com.dm.zbar.android.scanner.ZBarConstants;
//import com.dm.zbar.android.scanner.ZBarScannerActivity;

public class CoffeeMachineActivity extends SherlockActivity {
    public static final String TAG ="MainActivity";
    protected static final int ZBAR_SCANNER_REQUEST = 0;
    private static final String EMPTY_VALUE = "EMPTY_VALUE";
    private ArrayList<CoffeMachine> coffeMachineList;
    private CoffeeMachineDataStorageApplication coffeMachineApplication;
    public Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar); //Used for theme switching in samples
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coffe_machine_layout);

        View mainView = findViewById(R.id.scrollViewContainerId);
        context = this.getApplicationContext();
        Common.setCustomFont(mainView, this.getAssets());
        if(savedInstanceState == null) {
            initDataApplication();
            initView();
        }
    }


    private void initDataApplication(){
        SharedPreferences sharedPref = getPreferences(0);
        if(sharedPref!= null) {
            //TODO test
//    		String username = "dadoz";
            String username = sharedPref.getString(Common.REGISTERED_USERNAME, EMPTY_VALUE);
            if(username.compareTo(EMPTY_VALUE)!=0){
                Log.e(TAG,">>>>" + username);
                CoffeeMachineDataStorageApplication dataStorage = (CoffeeMachineDataStorageApplication)getApplication();
                dataStorage.coffeeMachineData.initRegisteredUser(username);
            }
        }
    }
    private void initView(){

        //STATIC BUTTON to try out scanCodeReader
/*		Button QRCodeScanButton = (Button)findViewById(R.id.findCoffeeMachineButtonId);
		QRCodeScanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(CoffeMachineActivity.this, ZBarScannerActivity.class);
//				startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
			}
		});
*/

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        CoffeMachineFragment cfFrag = new CoffeMachineFragment();
        fragmentTransaction.add(R.id.coffeeMachineContainerLayoutId, cfFrag);
        fragmentTransaction.commit();

    }

    private boolean getCoffeMachineReviewById(String coffeMachineId, boolean coffeMachineDataAvailable){

        if(coffeMachineDataAvailable) {
            //check if coffeMachineId exist - I DONT KNOW if tis better to use this fx instead of impl method on list
            ArrayList<Review> reviewList = coffeMachineApplication.coffeeMachineData.getReviewListByCoffeMachineId(coffeMachineId);
            if(reviewList == null) {
                Log.e(TAG,"still not implemented - no one coffeMaachine owned by this ID");
                return false;
            }
        }
/*
	    Intent intent = new Intent(CoffeMachineActivity.this, ReviewsActivity.class);
	    intent.putExtra("EXTRA_COFFE_MACHINE_ID", coffeMachineId);
	    startActivity(intent);
*/
        //change fragment

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.coffeeMachineContainerLayoutId, new ReviewsFragment());

        fragmentTransaction.commit();



        return true;
    }

//	@Override
/*	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
	    return super.onCreateOptionsMenu(menu);
	}
	*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
            // Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
//	        Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
//	        Toast.makeText(this, "Scan Result Type = " + data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE), Toast.LENGTH_SHORT).show();

//	        String coffeMachineId =data.getStringExtra(ZBarConstants.SCAN_RESULT);
//	        Log.d(TAG,"COFFE_MACHINE_ID >>>>>>" + coffeMachineId);
//	        getCoffeMachineReviewById(coffeMachineId,true);
            // The value of type indicates one of the symbols listed in Advanced Options below.
        } else if(resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
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
