package com.application.takeacoffee;

import com.actionbarsherlock.app.SherlockActivity;
import com.application.commons.Common;
import com.application.commons.Common.ReviewStatusEnum;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

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
		CoffeMachineDataStorageApplication storedData = (CoffeMachineDataStorageApplication)getApplication();
		final String username = storedData.coffeMachineData.getRegisteredUser().getUsername();
		
		((TextView)findViewById(R.id.registeredUsernameTextViewId)).setText(username);
		String currentCoffeMachineSelectedId = storedData.coffeMachineData.getCurrentCoffeMachineSelectedId();
		final CoffeMachine currentCoffeMachineObj = storedData.coffeMachineData.getCoffeMachineById(currentCoffeMachineSelectedId);

		final ReviewStatusEnum reviewStatusChoiced = ReviewStatusEnum.NOT_SET;
		
		//static def on RadioButton listener
		final CheckBox awefulRadioButton = (CheckBox)findViewById(R.id.awefulCheckBoxId);
		final CheckBox notBadRadioButton  = (CheckBox)findViewById(R.id.notBadCheckBoxId);
		final CheckBox goodRadioButton  = (CheckBox)findViewById(R.id.goodCheckBoxId);

		awefulRadioButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				awefulRadioButton.setChecked(true);
				notBadRadioButton.setChecked(false);
				goodRadioButton.setChecked(false);
			}
		});
		
		notBadRadioButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notBadRadioButton.setChecked(true);
				awefulRadioButton.setChecked(false);
				goodRadioButton.setChecked(false);
				
			}
		});
		
		goodRadioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goodRadioButton.setChecked(true);
				notBadRadioButton.setChecked(false);
				awefulRadioButton.setChecked(false);
			}
		});


		
		Button postButton = (Button)findViewById(R.id.postButtonId);
		postButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String reviewMessage = ((EditText)findViewById(R.id.reviewMessageEditTextId)).getText().toString();
				
				
				ReviewStatusEnum reviewStatus = ReviewStatusEnum.NOT_SET;
				if(awefulRadioButton.isChecked()){
					reviewStatus = ReviewStatusEnum.AWEFUL;
				} else if(notBadRadioButton.isChecked()){
					reviewStatus = ReviewStatusEnum.NOT_BAD;					
				} else if(goodRadioButton.isChecked()){
					reviewStatus = ReviewStatusEnum.GOOD;
				}
				
				//create new review Obj by data 
				Review reviewObj = new Review("fake_id", username, reviewMessage, reviewStatus);
	
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
