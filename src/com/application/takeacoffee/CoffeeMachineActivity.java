package com.application.takeacoffee;

import android.app.Fragment;
import android.app.FragmentManager;
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
    private static final String NEW_USER_FRAGMENT_TAG = "NEW_USER_FRAGMENT_TAG";

    public boolean loggedUser;
    //    private ArrayList<CoffeeMachine> coffeeMachineList;
    private static CoffeeMachineDataStorageApplication coffeeMachineApplication;
    public Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();

        setContentView(R.layout.coffe_machine_layout);

        Common.setCustomFont(findViewById(R.id.scrollViewContainerId), this.getAssets());

        loggedUser = initDataApplication();
        //change username
        if(loggedUser) {
            setLoggedUserView();
        } else {
            setNotLoggedUserView();
        }

        if(savedInstanceState == null) {
            initView();
        }
    }


    private boolean initDataApplication(){
        SharedPreferences sharedPref = getPreferences(0);
        if(sharedPref!= null) {
            String username = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USERNAME, EMPTY_VALUE);
            if(username.compareTo(EMPTY_VALUE) != 0) {
                Log.e(TAG,"this is my username" + username);
                coffeeMachineApplication = (CoffeeMachineDataStorageApplication)getApplication();
                coffeeMachineApplication.coffeeMachineData.initRegisteredUser(username);
                String profilePicPath = sharedPref.getString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, EMPTY_VALUE);
                coffeeMachineApplication.coffeeMachineData.setProfilePicturePath(profilePicPath);
                return true;
            } else {
                Log.e(TAG, "no username set");
                return false;
            }
        }
        return false;
    }

    private void initView() {
        getFragmentManager().beginTransaction()
                .add(R.id.coffeeMachineContainerLayoutId, new CoffeeMachineFragment())
                .commit();
    }

    public void setLoggedUserView() {
        ((TextView)findViewById(R.id.loggedUserTextId)).setText(
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser().getUsername());

        Button loggedUserButton = (Button)findViewById(R.id.loggedUserButtonId);
        loggedUserButton.setBackground((getResources().getDrawable(R.drawable.button_rounded_shape)));
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(getFragmentManager());
            }
        });
        setProfilePicFromStorage((ImageView)findViewById(R.id.loggedUserImageViewId));
    }

    public static boolean setProfilePicFromStorage(ImageView v) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(coffeeMachineApplication.coffeeMachineData.getProfilePicturePath(), options);
            v.setImageBitmap(bitmap);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public void setNotLoggedUserView() {
        ((TextView)findViewById(R.id.loggedUserTextId)).setText("guest");

        Button loggedUserButton = (Button)findViewById(R.id.loggedUserButtonId);
        loggedUserButton.setBackground((getResources().getDrawable(R.drawable.button_rounded_shape)));
        loggedUserButton.setText("new");
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "not logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(getFragmentManager());
            }
        });
    }

    public static void addChangeUserFragment(FragmentManager fragManager) {
        Fragment newUserFragment = new NewUserFragment();
        //add fragment content to add user
        fragManager.beginTransaction()
                .setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out)
                .replace(R.id.coffeeMachineContainerLayoutId, newUserFragment, NEW_USER_FRAGMENT_TAG)
                .addToBackStack("back")
                .commit();
    }

    @Override
    public void onBackPressed() {
        final NewUserFragment fragment = (NewUserFragment)getFragmentManager().findFragmentByTag(NEW_USER_FRAGMENT_TAG);
        if(fragment != null) {
            fragment.resetChangeButton();
        }
        super.onBackPressed();
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
