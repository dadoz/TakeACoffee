package com.application.takeacoffee;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String TAG ="MainActivity";
	protected static final int ZBAR_SCANNER_REQUEST = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coffe_machine_layout);
		
		CoffeMachine[] coffeMachineArray = RetrieveDataFromServer.getCoffeMachineData();

		Button QRCodeScanButton = (Button)findViewById(R.id.QRCodeScanButtonId);
		QRCodeScanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, ZBarScannerActivity.class);
				startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
			}
		});
		
		Log.e(TAG,"coffeMachineData - " + coffeMachineArray);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{    
	    if (resultCode == RESULT_OK) 
	    {
	        // Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
	        // Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
	        Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
	        Toast.makeText(this, "Scan Result Type = " + data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE), Toast.LENGTH_SHORT).show();
	        // The value of type indicates one of the symbols listed in Advanced Options below.
	    } else if(resultCode == RESULT_CANCELED) {
	        Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
	    }
	}
}
