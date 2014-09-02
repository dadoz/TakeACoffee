package com.application.takeacoffee;

import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.commons.Common;
import com.application.datastorage.DataStorageSingleton;
import com.application.models.User;
import com.application.takeacoffee.fragments.*;

public class CoffeeMachineActivity extends FragmentActivity {
    private static final String TAG = "CoffeeMachineActivity";

    private static DataStorageSingleton coffeeApp;
    private static FragmentActivity mainActivityRef;
    private SharedPreferences sharedPref;
    public static BroadcastReceiver broadcastReceiver;

    //broadcast receiver to check internet
    public void checkInternetConnectionListener() {
        if(broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    NetworkInfo info = extras.getParcelable("networkInfo");

                    NetworkInfo.State state = info.getState();
                    if(state == NetworkInfo.State.CONNECTED) {
                        Common.displayError("HEY u're connected", context);
                    }
                    if(state == NetworkInfo.State.DISCONNECTED) {
                        Common.displayError("HEY u're offline", context);
                    }
                }
            };
        }
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coffe_machine_layout);
        Common.setCustomFont(findViewById(R.id.scrollViewContainerId),
                this.getAssets());
        mainActivityRef = this;

        sharedPref = mainActivityRef.getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);
        if(sharedPref.getBoolean(Common.FIRST_INIT_VIEW, true)) {
            firstInitView();
            return;
        }

        initView(savedInstanceState, mainActivityRef.getWindow().getDecorView());
        checkInternetConnectionListener();
    }
    @Override
    protected void onStop() {
        super.onStop();
        coffeeApp.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(broadcastReceiver, intentFilter);
    }


    private void firstInitView() {
        sharedPref.edit().putBoolean(Common.FIRST_INIT_VIEW, false).commit();

        LoadingFragment loadingFragment = new LoadingFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.coffeeMachineContainerLayoutId, loadingFragment, Common.COFFEE_MACHINE_FRAGMENT_TAG)
                .commit();
    }

    public static boolean initView(Bundle savedInstanceState, View mainView) {
        boolean loggedUser = initDataApplication();
        //change username
        if(loggedUser) {
            setLoggedUserView(mainView);
        } else {
            setNotLoggedUserView(mainView);
        }

        if(savedInstanceState == null) {
            mainActivityRef.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.coffeeMachineContainerLayoutId, new CoffeeMachineFragment(), Common.COFFEE_MACHINE_FRAGMENT_TAG)
                    .commit();
        }
        return loggedUser;
    }

    private static boolean initDataApplication() {
        SharedPreferences sharedPref = mainActivityRef.getSharedPreferences("SHARED_PREF_COFFEE_MACHINE", Context.MODE_PRIVATE);
        coffeeApp = DataStorageSingleton.getInstance(mainActivityRef.getApplicationContext());
        if(sharedPref != null) {
            String username = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USERNAME, null);
            String profilePicPath = sharedPref.getString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, null);
            long userId = sharedPref.getLong(Common.SHAREDPREF_REGISTERED_USER_ID,
                    Common.EMPTY_LONG_VALUE);
                if(userId != Common.EMPTY_LONG_VALUE) {
                    Log.e(TAG, "this is my username" + username);
                coffeeApp.setRegisteredUser(userId, profilePicPath, username); //TODO check empty value
                return true;
            } else {
                Log.e(TAG, "no username set");
                return false;
            }
        }
        return false;
    }

    public static void setLoggedUserView(View mainView) {
        ((TextView) mainView.findViewById(R.id.loggedUserTextId)).setText(
                coffeeApp.getRegisteredUsername());

        LinearLayout loggedUserButton = (LinearLayout) mainView.findViewById(R.id.loggedUserButtonId);
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainActivityRef.getBaseContext(), "logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(mainActivityRef.getSupportFragmentManager());
            }
        });

        Bitmap defaultIcon = BitmapFactory.decodeResource(mainActivityRef.getResources(), R.drawable.user_icon);
        Bitmap bitmap = Common.getRoundedBitmapByFile(coffeeApp.getRegisteredProfilePicturePath(),
               defaultIcon);
        ((ImageView) mainView.findViewById(R.id.loggedUserImageViewId)).setImageBitmap(bitmap);
    }

    public static void setNotLoggedUserView(View mainView) {
        ((TextView) mainView.findViewById(R.id.loggedUserTextId)).setText("guest");

        LinearLayout loggedUserButton = (LinearLayout) mainView.findViewById(R.id.loggedUserButtonId);
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainActivityRef.getBaseContext(), "not logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(mainActivityRef.getSupportFragmentManager());
            }
        });
    }

    public static void addChangeUserFragment(FragmentManager fragManager) {
        LoginFragment loginFragment = new LoginFragment();
        //add fragment content to add user
        fragManager.beginTransaction()
//                .setCustomAnimations(R.anim.fade_in,
//                        R.anim.fade_out)
                .replace(R.id.coffeeMachineContainerLayoutId, loginFragment,
                        Common.NEW_USER_FRAGMENT_TAG)
                .addToBackStack("back")
                .commit();
    }

    //set header bar methods

    public static void hideAllItemsOnHeaderBar() {
        ViewGroup headerBar = (ViewGroup) mainActivityRef.findViewById(R.id.headerBarLayoutId);
        for (int i = 0; i < headerBar.getChildCount(); i ++) {
            headerBar.getChildAt(i).setVisibility(View.GONE);
        }
    }

    public static void setItemOnHeaderBarById(int id, final FragmentManager fragmentManager, String coffeeMachineName) {
//        mainActivityRef.findViewById(R.id.subHeaderBarLayoutId).setVisibility(View.GONE);
        View view = mainActivityRef.findViewById(id);
        view.setVisibility(View.VISIBLE);


        switch (id) {
            case R.id.headerMapButtonId :
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MapFragment mapFragment = new MapFragment();
                        fragmentManager.beginTransaction()
                                .replace(R.id.coffeeMachineContainerLayoutId,
                                        mapFragment).addToBackStack("back").commit();
                    }
                });
                break;
            case R.id.loggedUserButtonId :
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.coffeeMachineContainerLayoutId, new LoginFragment(),
                                        Common.NEW_USER_FRAGMENT_TAG)
                                .addToBackStack("back")
                                .commit();
                    }
                });
                break;
            case R.id.coffeeMachineSettingsMapHeaderLayoutId:
                if (coffeeMachineName != null) {
                    ((TextView) view.findViewById(R.id.coffeeMachineNameReviewTextId))
                            .setText(coffeeMachineName);

                    view.findViewById(R.id.reviewsMachineMapButtonId)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MapFragment mapFragment = new MapFragment();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.coffeeMachineContainerLayoutId,
                                                    mapFragment).addToBackStack("back").commit();
                                }
                            });
                    break;
                }
        }
    }

    public static void setHeaderByFragmentId(int fragmentId, FragmentManager fragmentManager, long coffeeMachineId) {
        CoffeeMachineActivity.hideAllItemsOnHeaderBar();

        switch (fragmentId) {
            case 0:
                CoffeeMachineActivity.hideAllItemsOnHeaderBar();
                CoffeeMachineActivity.setItemOnHeaderBarById(R.id.headerMapButtonId, fragmentManager, null);
                CoffeeMachineActivity.setItemOnHeaderBarById(R.id.loggedUserButtonId, fragmentManager, null);
                break;
            case 1:
//                CoffeeMachineActivity.setItemOnHeaderBarById(R.id.loggedUserButtonId, fragmentManager);
                String coffeeMachineName = coffeeApp
                        .getCoffeeMachineById(coffeeMachineId).getName();
                CoffeeMachineActivity.setItemOnHeaderBarById(R.id.coffeeMachineSettingsMapHeaderLayoutId,
                        fragmentManager, coffeeMachineName);
/*                CoffeeMachineActivity.addItemOnHeaderBarById(R.layout.review_header_template,
                        null, coffeeMachineName,
                        false, fragmentManager);*/
                break;
            case 2:
                mainActivityRef.findViewById(R.id.headerBarLayoutId).setVisibility(View.VISIBLE);
                CoffeeMachineActivity.setItemOnHeaderBarById(R.id.headerMapLabelId, null, null);
                break;
            case 3:
                CoffeeMachineActivity.setItemOnHeaderBarById(R.id.loggedUserButtonId, fragmentManager, null);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

/*
    public static void addItemOnHeaderBarById(int layoutId, View containerView,
                                              String coffeeMachineName, boolean hideHeaderBar, final FragmentManager fragmentManager) {
  //      mainActivityRef.findViewById(R.id.subHeaderBarLayoutId).setVisibility(View.VISIBLE);
        mainActivityRef.findViewById(R.id.headerBarLayoutId).setVisibility(hideHeaderBar ? View.GONE : View.VISIBLE);

        LayoutInflater lf = (LayoutInflater) mainActivityRef.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lf.inflate(layoutId, null);
        if(view != null) {
            Common.setCustomFont(view, mainActivityRef.getAssets());
            if(containerView == null) {
    //            containerView = mainActivityRef.findViewById(R.id.subHeaderBarLayoutId);
            }
            ((ViewGroup) containerView).removeAllViews();
            ((ViewGroup) containerView).addView(view);

            switch (layoutId) {
                case R.layout.review_header_template:
                    if (coffeeMachineName != null) {
                        ((TextView) view.findViewById(R.id.coffeeMachineNameReviewTextId))
                                .setText(coffeeMachineName);

                        view.findViewById(R.id.reviewsMachineMapButtonId)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MapFragment mapFragment = new MapFragment();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.coffeeMachineContainerLayoutId,
                                                        mapFragment).addToBackStack("back").commit();
                                    }
                                });
                        break;
                    }
            }
        } else {
            Log.e(TAG, "error - template you want to inflate in headerBar is not valid");
        }
    }

    public static void setHeaderBarVisibile(boolean visibile) {
        mainActivityRef.findViewById(R.id.headerBarLayoutId).setVisibility(visibile ? View.VISIBLE : View.GONE);
    }
*/

}