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
import com.application.commons.BitmapCustomUtils;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppLogic;
import com.application.datastorage.DataStorageSingleton;
import com.application.models.User;
import com.application.takeacoffee.fragments.*;
import com.parse.Parse;
import com.parse.ParseAnalytics;

public class CoffeeMachineActivity extends FragmentActivity {
    private static final String TAG = "CoffeeMachineActivity";

    private static DataStorageSingleton coffeeApp;
//    private static FragmentActivity mainActivityRef;
    private SharedPreferences sharedPref;
    public static BroadcastReceiver broadcastReceiver;

    //broadcast receiver to check internet
/*    public void checkInternetConnectionListener() {
        if(broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    NetworkInfo info = extras.getParcelable("networkInfo");

                    NetworkInfo.State state = info.getState();
                    if(state == NetworkInfo.State.CONNECTED) {
                        Common.displayError(context, "HEY u're connected");
                    }
                    if(state == NetworkInfo.State.DISCONNECTED) {
                        Common.displayError(context, "HEY u're offline");
                    }
                }
            };
        }
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(broadcastReceiver, intentFilter);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coffe_machine_layout);

/*        sharedPref = this.getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);
        if(sharedPref.getBoolean(Common.FIRST_INIT_VIEW, true)) {
            firstInitView();
            return;
        }*/
        coffeeApp = DataStorageSingleton.getInstance(this.getApplicationContext());
        Common.setCustomFont(findViewById(R.id.scrollViewContainerId),
                this.getAssets());

        initView(savedInstanceState, this.getWindow().getDecorView());
        if(savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.coffeeMachineContainerLayoutId,
                            new CoffeeMachineFragment(),
                            Common.COFFEE_MACHINE_FRAGMENT_TAG)
                    .commit();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
//        coffeeApp.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
/*        try{
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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


/*    private void firstInitView() {
        sharedPref.edit().putBoolean(Common.FIRST_INIT_VIEW, false).commit();

        LoadingFragment loadingFragment = new LoadingFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.coffeeMachineContainerLayoutId, loadingFragment, Common.COFFEE_MACHINE_FRAGMENT_TAG)
                .commit();
    }*/

    public void initView(Bundle savedInstanceState, View mainView) {
        if(savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.coffeeMachineContainerLayoutId,
                            new CoffeeMachineFragment(),
                            Common.COFFEE_MACHINE_FRAGMENT_TAG)
                    .commit();
        }
//        return loggedUser;
    }

/*
    public static void addChangeUserFragment(FragmentManager fragManager) {
        LoginFragment loginFragment = new LoginFragment();
        fragManager.beginTransaction()
                .replace(R.id.coffeeMachineContainerLayoutId, loginFragment,
                        Common.NEW_USER_FRAGMENT_TAG)
                .addToBackStack("back")
                .commit();
    }
*/
    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if(backStackEntryCount == 0) {
            Log.e(TAG, "exit app");
            if(coffeeApp != null) {
                coffeeApp.destroy();
            }
        }
        super.onBackPressed();
    }

}