package com.application.takeacoffee;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.takeacoffee.fragments.CoffeeMachineFragment;
import com.application.takeacoffee.fragments.NewUserFragment;

public class CoffeeMachineActivity extends SherlockActivity {
    public static final String TAG ="MainActivity";
    private static final String EMPTY_VALUE = "EMPTY_VALUE";
//    private ArrayList<CoffeeMachine> coffeeMachineList;
    private CoffeeMachineDataStorageApplication coffeeMachineApplication;
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
            boolean loggedUser = initDataApplication();
            initView(loggedUser);
        }
    }


    private boolean initDataApplication(){
        SharedPreferences sharedPref = getPreferences(0);
        if(sharedPref!= null) {
            String username = sharedPref.getString(Common.REGISTERED_USERNAME, EMPTY_VALUE);
            if(username.compareTo(EMPTY_VALUE) != 0) {
                Log.e(TAG,"this is my username" + username);
                coffeeMachineApplication = (CoffeeMachineDataStorageApplication)getApplication();
                coffeeMachineApplication.coffeeMachineData.initRegisteredUser(username);
                return true;
            } else {
                Log.e(TAG, "no username set");
                return false;
            }
        }
        return false;
    }

    private void initView(final boolean loggedUser) {
        //change username
        if(loggedUser) {
            setLoggedUserView();
        } else {
            setNotLoggedUserView();
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        CoffeeMachineFragment cfFrag = new CoffeeMachineFragment();
        fragmentTransaction.add(R.id.coffeeMachineContainerLayoutId, cfFrag);
        fragmentTransaction.commit();
    }

    public void setLoggedUserView() {
        Button loggedUserButton = (Button)findViewById(R.id.loggedUserButtonId);
        ((TextView)findViewById(R.id.loggedUserTextId)).setText(
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser().getUsername());
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "logged", Toast.LENGTH_LONG).show();
            }
        });

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(coffeeMachineApplication.coffeeMachineData.getProfilePicturePath(), options);
            ((ImageView)findViewById(R.id.loggedUserImageViewId)).setImageBitmap(bitmap);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void setNotLoggedUserView() {
        Button loggedUserButton = (Button)findViewById(R.id.loggedUserButtonId);

        ((TextView)findViewById(R.id.loggedUserTextId)).setText("guest");
        loggedUserButton.setText("new");
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "not logged", Toast.LENGTH_LONG).show();

                //add fragment content to add user
                getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out)
                    .replace(R.id.coffeeMachineContainerLayoutId, new NewUserFragment())
                    .addToBackStack("back")
                    .commit();
            }
        });
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

//	@Override
/*	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
	    return super.onCreateOptionsMenu(menu);
	}
	*/

}
