package com.application.takeacoffee;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	public static final String TAG ="MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coffe_machine_layout);
		
		CoffeMachine[] coffeMachineArray = RetrieveDataFromServer.getCoffeMachineData();

		Log.e(TAG,"coffeMachineData - " + coffeMachineArray);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
