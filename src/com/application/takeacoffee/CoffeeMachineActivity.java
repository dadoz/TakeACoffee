package com.application.takeacoffee;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.takeacoffee.fragments.CoffeeMachineFragment;
import com.application.takeacoffee.fragments.NewUserFragment;

public class CoffeeMachineActivity extends Activity {
    private static final String TAG ="MainActivity";

    private boolean loggedUser;
    private static CoffeeMachineDataStorageApplication coffeeMachineApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coffe_machine_layout);

        Common.setCustomFont(findViewById(R.id.scrollViewContainerId),
                this.getAssets());

        //ACTION BAR
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
            String username = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USERNAME, Common.EMPTY_VALUE);
            if(username.compareTo(Common.EMPTY_VALUE) != 0) {
                Log.e(TAG,"this is my username" + username);
                coffeeMachineApplication = (CoffeeMachineDataStorageApplication)getApplication();
                coffeeMachineApplication.coffeeMachineData.initRegisteredUserByUsername(username);
                String profilePicPath = sharedPref.getString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, Common.EMPTY_VALUE);
                if(profilePicPath == Common.EMPTY_VALUE) {
                    profilePicPath = null;
                }
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser().setProfilePicturePath(profilePicPath);

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

        LinearLayout loggedUserButton = (LinearLayout)findViewById(R.id.loggedUserButtonId);
//        loggedUserButton.setBackground((getResources().getDrawable(R.drawable.button_rounded_shape)));
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(getFragmentManager());
            }
        });

        Common.drawProfilePictureByPath((ImageView)findViewById(R.id.loggedUserImageViewId),
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser()
                        .getProfilePicturePath(), getResources()
                        .getDrawable(R.drawable.user_icon));
    }


    public void setNotLoggedUserView() {
        ((TextView)findViewById(R.id.loggedUserTextId)).setText("guest");

        LinearLayout loggedUserButton = (LinearLayout)findViewById(R.id.loggedUserButtonId);
//        loggedUserButton.setBackground((getResources().getDrawable(R.drawable.button_rounded_shape)));
//        loggedUserButton.setText("new");
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "not logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(getFragmentManager());
            }
        });
    }

    public static void addChangeUserFragment(FragmentManager fragManager) {
        NewUserFragment newUserFragment = new NewUserFragment();
        //add fragment content to add user
        fragManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out)
/*                .setCustomAnimations(R.anim.card_flip_left_in,
                    R.anim.card_flip_left_out,
                    R.anim.card_flip_right_in,
                    R.anim.card_flip_right_out)*/
                .replace(R.id.coffeeMachineContainerLayoutId, newUserFragment, Common.NEW_USER_FRAGMENT_TAG)
                .addToBackStack("back")
                .commit();
    }

    @Override
    public void onBackPressed() {
        final NewUserFragment fragment = (NewUserFragment)getFragmentManager().findFragmentByTag(Common.NEW_USER_FRAGMENT_TAG);
        if(fragment != null) {
            fragment.resetChangeButton();
        }
        super.onBackPressed();
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar

/*        menu.add("Save")
                .setIcon(android.R.drawable.ic_menu_save)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        menu.add("Search")
                .setIcon(android.R.drawable.ic_menu_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.add("Refresh")
                .setIcon(android.R.drawable.ic_menu_rotate)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //This uses the imported MenuItem from ActionBarSherlock
        Toast.makeText(this, "Got click: " + item.getTitle(), Toast.LENGTH_SHORT).show();
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

    /**MOVE OUT THIS FUNCTION**/
/*    public static boolean sePictureByPicPath(ImageView v, int pictureName) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeResource(mainActivityRef.getResources(), pictureName);
            Bitmap roundedBitmap = getRoundedRectBitmap(bitmap, Common.PROFILE_PIC_CIRCLE_MASK_SIZE);
            v.setImageBitmap(roundedBitmap);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static Bitmap getRoundedBitmapByColor(Bitmap bmp, int color) {
        Bitmap bmp2 = bmp.copy(bmp.getConfig(), true);
        bmp2.eraseColor(color);
        Bitmap roundedBitmap = getRoundedRectBitmap(bmp2, Common.PROFILE_PIC_CIRCLE_MASK_BIGGER_SIZE);
        return roundedBitmap;
    }*/
}
