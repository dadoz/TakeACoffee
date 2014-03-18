package com.application.takeacoffee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.Review;

public class LoggedUserSettingsActivity extends SherlockActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logged_user_settings_layout);

        initView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);

	}

    public void initView() {


        final CoffeeMachineDataStorageApplication coffeMachineApplication = ((CoffeeMachineDataStorageApplication)this.getApplication());

        Button changeUsernameButton = (Button)findViewById(R.id.changeUsernameButtonId);
        changeUsernameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String newUsername = ((EditText)findViewById(R.id.newUsernameEditTextId)).getText().toString();

                SharedPreferences sharedPref = getPreferences(0);
                sharedPref.edit().putString(Common.REGISTERED_USERNAME, newUsername);
                coffeMachineApplication.coffeeMachineData.setRegisteredUser(newUsername);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
