package com.application.takeacoffee;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
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
		User user = storedData.coffeMachineData.getRegisteredUser();
		((TextView)findViewById(R.id.registeredUsernameTextViewId)).setText(user.getUsername());
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}
}
