package com.application.takeacoffee;

import android.content.*;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppController;
import com.application.takeacoffee.fragments.*;

public class CoffeeMachineActivity extends FragmentActivity {
    private static final String TAG = "CoffeeMachineActivity";

    public static BroadcastReceiver broadcastReceiver;
    public static CoffeeAppController coffeeAppController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coffe_machine_layout);

        coffeeAppController = new CoffeeAppController(this.getApplicationContext(), this.getApplication(), getLoaderManager());
        Common.setCustomFont(findViewById(R.id.scrollViewContainerId),
                this.getAssets());

        initView(savedInstanceState);
        if(savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.coffeeMachineContainerLayoutId,
                            new CoffeeMachineFragment(),
                            Common.COFFEE_MACHINE_FRAGMENT_TAG)
                    .commit();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void initView(Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.coffeeMachineContainerLayoutId,
                            new CoffeeMachineFragment(),
                            Common.COFFEE_MACHINE_FRAGMENT_TAG)
                    .commit();
        }
    }

    public CoffeeAppController getCoffeeAppController() {
        return coffeeAppController;
    }

    @Override
    public void onBackPressed() {
//        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
//        if(backStackEntryCount == 0) {
//            Log.e(TAG, "exit app");
//            if(coffeeApp != null) {
//                coffeeApp.destroy();
//            }
//        }
        super.onBackPressed();
    }

}