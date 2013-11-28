package com.application.takeacoffee;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddReviewActivity extends SherlockActivity{
	
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

		Button postButton = (Button)findViewById(R.id.postButtonId);
		postButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String reviewMessage = ((EditText)findViewById(R.id.reviewMessageEditTextId)).getText().toString();
				//create new review Obj by data 
				Review reviewObj = new Review("fake_id", username, reviewMessage, true);
				currentCoffeMachineObj.addReviewObj(reviewObj);
				//encodeToJSONData();
				//create JSON obj for this message
				

			}
		});
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}
}
