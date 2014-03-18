package com.application.takeacoffee;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.application.commons.Common;
import com.application.commons.Common.ReviewStatusEnum;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.CoffeMachine;
import com.application.models.Review;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;

import java.util.Date;

public class AddReviewActivity extends SherlockActivity{
	
	protected static final String TAG = null;
	@Override
	protected void onCreate(Bundle savedInstanceState){
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_review_layout);
		
		initView();
	} 

	private void initView(){
        //static attachment on button click
        RelativeLayout loggedUserSettingsLayout = (RelativeLayout)findViewById(R.id.loggedUsernameLayoutId);
        loggedUserSettingsLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddReviewActivity.this, LoggedUserSettingsActivity.class);
                startActivityForResult(intent, Common.CHANGE_LOGGED_USERNAME);
            }
        });


		CoffeeMachineDataStorageApplication storedData = (CoffeeMachineDataStorageApplication)getApplication();
		final String username = storedData.coffeeMachineData.getRegisteredUser().getUsername();


		((TextView)findViewById(R.id.registeredUsernameTextViewId)).setText(username);
		String currentCoffeMachineSelectedId = storedData.coffeeMachineData.getCurrentCoffeMachineSelectedId();
		final CoffeMachine currentCoffeMachineObj = storedData.coffeeMachineData.getCoffeMachineById(currentCoffeMachineSelectedId);

//		final ReviewStatusEnum reviewStatusChoiced = ReviewStatusEnum.NOT_SET;
		
		//static def on RadioButton listener
		final LinearLayout awfulButton = (LinearLayout)findViewById(R.id.awfulLayoutId);
		final LinearLayout notBadButton  = (LinearLayout)findViewById(R.id.notBadLayoutId);
		final LinearLayout goodButton  = (LinearLayout)findViewById(R.id.goodLayoutId);


        awfulButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                awfulButton.setBackgroundColor(getResources().getColor(R.color.light_grey));
                notBadButton.setBackgroundColor(getResources().getColor(R.color.white));
                goodButton.setBackgroundColor(getResources().getColor(R.color.white));
                awfulButton.setFocusable(true);
                notBadButton.setFocusable(false);
                goodButton.setFocusable(false);
			}
		});
		
		notBadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                awfulButton.setBackgroundColor(getResources().getColor(R.color.white));
                notBadButton.setBackgroundColor(getResources().getColor(R.color.light_grey));
                goodButton.setBackgroundColor(getResources().getColor(R.color.white));
                awfulButton.setFocusable(false);
                notBadButton.setFocusable(true);
                goodButton.setFocusable(false);

			}
		});
		
		goodButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                awfulButton.setBackgroundColor(getResources().getColor(R.color.white));
                notBadButton.setBackgroundColor(getResources().getColor(R.color.white));
                goodButton.setBackgroundColor(getResources().getColor(R.color.light_grey));
                awfulButton.setFocusable(false);
                notBadButton.setFocusable(false);
                goodButton.setFocusable(true);

            }
		});


/*        EditText reviewEditText = (EditText)findViewById(R.id.reviewMessageEditTextId);
        reviewEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    // NOTE: In the author's example, he uses an identifier
                    // called searchBar. If setting this code on your EditText
                    // then use v.getWindowToken() as a reference to your
                    // EditText is passed into this callback as a TextView

                    in.hideSoftInputFromWindow(textView
                            .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return handled;
            }
        });*/
		
		Button postButton = (Button)findViewById(R.id.postButtonId);
		postButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String reviewMessage = ((EditText)findViewById(R.id.reviewMessageEditTextId)).getText().toString();

                ReviewStatusEnum reviewStatus = ReviewStatusEnum.NOT_SET;

                if(awfulButton.isFocusable()){
					reviewStatus = ReviewStatusEnum.AWFUL;
				} else if(notBadButton.isFocusable()){
					reviewStatus = ReviewStatusEnum.NOT_BAD;					
				} else if(goodButton.isFocusable()){
					reviewStatus = ReviewStatusEnum.GOOD;
				}
				//create new review Obj by data
				Review reviewObj = new Review("fake_id", username, reviewMessage, reviewStatus, new Date());
	
				Log.e(TAG,"<<<<<<<<<" + reviewMessage + "--");
				if(reviewMessage.compareTo(" ") == 0){
					Log.e(TAG,"hey");					
				}
				if(currentCoffeMachineObj != null && reviewStatus != ReviewStatusEnum.NOT_SET){
					currentCoffeMachineObj.addReviewObj(reviewObj);
					setResult(RESULT_OK);
					finish();
				} else {
					Log.e(TAG,"coffeMachine not found - trace error");
					NoValidReviewDialogFragment noValidReviewDialog = new NoValidReviewDialogFragment();	
					noValidReviewDialog.show(getFragmentManager(), TAG);
				}
				//encodeToJSONData();
				//create JSON obj for this message
			}
		});
		
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.e(TAG,"hey ------ change logged user" + requestCode + "  " + RESULT_OK);
        if(requestCode == Common.CHANGE_LOGGED_USERNAME){
            if(resultCode == RESULT_OK){
                RelativeLayout loggedUserSettingsLayout = (RelativeLayout)findViewById(R.id.loggedUsernameLayoutId);

                //refresh username on view
                CoffeeMachineDataStorageApplication storedData = (CoffeeMachineDataStorageApplication)getApplication();
                final String username = storedData.coffeeMachineData.getRegisteredUser().getUsername();
                ((TextView)findViewById(R.id.registeredUsernameTextViewId)).setText(username);

            }
        }
    }


	public class NoValidReviewDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

	        // Inflate and set the layout for the dialog
	        // Pass null as the parent view because its going in the dialog layout
	        builder.setMessage("please insert at least one messge or feedback")
	               .setPositiveButton("ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}
}
