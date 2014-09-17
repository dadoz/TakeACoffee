package com.application.takeacoffee.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.application.commons.BitmapCustomUtils;
import com.application.commons.Common;
import com.application.commons.HeaderUtils;
import com.application.dataRequest.CoffeeAppLogic;
import com.application.dataRequest.DataRequestVolleyService;
import com.application.dataRequest.ParseDataRequest;
import com.application.datastorage.DataStorageSingleton;
import com.application.models.CoffeeMachine;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;
import com.application.takeacoffee.experimental.PieChart;
import com.parse.ParseException;
import org.joda.time.DateTime;

import java.util.ArrayList;

import static com.application.commons.Common.setCustomFontByView;

/**
 * Created by davide on 3/13/14.
 */
public class CoffeeMachineFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "coffeeMachineFragment";
    private ArrayList<PieChart> pieChartList;
    private Handler mHandler;
    private static FragmentActivity mainActivityRef;
    private static LinearLayout settingsLayout;
    private static DataStorageSingleton coffeeApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mHandler = new Handler();
        mainActivityRef = getActivity();
        coffeeApp = DataStorageSingleton.getInstance(mainActivityRef.getApplicationContext());

        //get views
        final View coffeeMachineFragment = inflater.inflate(R.layout.coffe_machine_fragment, container, false);
        settingsLayout = (LinearLayout)coffeeMachineFragment.findViewById(R.id.settingsLayoutId);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initView(coffeeMachineFragment);
            }
        });

        //header stuff
        setHeader();
        //set custom font
        Common.setCustomFont(coffeeMachineFragment, this.getActivity().getAssets());
        return coffeeMachineFragment;
    }

    private void setHeader() {
        HeaderUtils.setHeaderByFragmentId(mainActivityRef, 0, getFragmentManager(), Common.EMPTY_VALUE);
    }

    private void initView(View coffeeMachineFragment) {

        //TODO REFACTOR IT
        boolean loggedUser = initDataApplication();
        //change username
        if(loggedUser) {
            setLoggedUserView(mainActivityRef.getWindow().getDecorView());
        } else {
            setNotLoggedUserView(mainActivityRef.getWindow().getDecorView());
        }

        int itemInTableRowCounter = 0;
        TableRow tableRow = null;
        //get data from application
        ArrayList<CoffeeMachine> coffeeMachineList = coffeeApp.getCoffeeMachineList();
        final CoffeeAppLogic coffeeAppLogic = new CoffeeAppLogic(mainActivityRef.getApplicationContext());

        //setMap button available
        mainActivityRef.findViewById(R.id.headerMapButtonId).setVisibility(View.VISIBLE);

        if (coffeeMachineList != null && coffeeMachineList.size() != 0) {
            for (final CoffeeMachine coffeeMachineObj : coffeeMachineList) {
//                Log.e(TAG, "coffeMachineData - " + coffeeMachineObj.getAddress() +
//                        " - name - " + coffeeMachineObj.getName());
                TableLayout cmfTableLayoutContainer = (TableLayout) coffeeMachineFragment
                        .findViewById(R.id.coffeeMachineTableLayoutId);

                if (itemInTableRowCounter == 0) {
                    tableRow = new TableRow(getActivity());
                    tableRow.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                    tableRow.setPadding(0, 40, 0, 40);
                }

                LayoutInflater inflaterLayout = (LayoutInflater) getActivity()
                        .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                final LinearLayout coffeeMachineTemplate = (LinearLayout) inflaterLayout
                        .inflate(R.layout.coffe_machine_template, null);
                coffeeMachineTemplate.setLayoutParams(new TableRow.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)); // it make no sense :O
                coffeeMachineTemplate.setGravity(Gravity.CENTER_HORIZONTAL);


                Animation anim = AnimationUtils.loadAnimation(getActivity().getBaseContext(), R.anim.zoom_in);
                anim.setDuration(Common.ANIMATION_GROW_TIME);
                coffeeMachineTemplate.startAnimation(anim);
                tableRow.addView(coffeeMachineTemplate);

                if (itemInTableRowCounter != 0 || coffeeMachineList.
                        indexOf(coffeeMachineObj) == (coffeeMachineList.size() - 1)) {
                    cmfTableLayoutContainer.addView(tableRow);
                }

                //reset counter
                if (itemInTableRowCounter == 0) {
                    itemInTableRowCounter ++;
                } else {
                    itemInTableRowCounter = 0;
                }

                //set data to template
                setCustomFontByView(getActivity().getAssets(), coffeeMachineTemplate.findViewById(
                        R.id.coffeeMachineNameTextId), false);
                ((TextView) coffeeMachineTemplate.findViewById(R.id.coffeeMachineNameTextId))
                        .setText(coffeeMachineObj.getName());
                ((TextView) coffeeMachineTemplate.findViewById(R.id.coffeeMachineNameTextId))
                        .setTextColor(getResources().getColor(R.color.light_black));

                //set coffee picture
                ImageView coffeeIconImageView = (ImageView) coffeeMachineTemplate.findViewById(R.id.coffeeIconId);
                coffeeIconImageView.setImageResource(R.drawable.coffee_cup_icon);
                coffeeAppLogic.getCoffeeMachineIcon((coffeeMachineObj.getIconPath()), coffeeIconImageView);

                (coffeeMachineTemplate.findViewById(R.id.coffeeIconId)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        //TODO MOVE THIS LOGIC on loaderThread
/*                        DateTime dateTime = new DateTime();
                        long oneWeekAgoTimestamp = CoffeeAppLogic.TimestampHandler.getOneWeekAgoTimestamp(dateTime);
                        long todayTimestamp = CoffeeAppLogic.TimestampHandler.getTodayTimestamp(dateTime);
                        coffeeAppLogic.setAndCountPreviousReviewList(coffeeApp, coffeeMachineObj.getId(),
                                oneWeekAgoTimestamp, todayTimestamp);
                        coffeeAppLogic.getUserToAllReviews(coffeeMachineObj.getId());
*/
                        beginTransactionFragReview(coffeeMachineObj.getId());

                    }
                });
            }
        }

        //set action to settings view
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Common.displayError("settings view", mainActivityRef);
                SettingsFragment settingsFragment = new SettingsFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, settingsFragment)
                        .addToBackStack("Fragment").commit();
            }
        });

    }

    private boolean beginTransactionFragReview(String coffeeMachineId) {
        //change fragment
        Bundle args = new Bundle();
        args.putString(Common.COFFEE_MACHINE_ID_KEY, coffeeMachineId);

        ChoiceReviewContainerFragment reviewsFrag = new ChoiceReviewContainerFragment();
        reviewsFrag.setArguments(args);

        getFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.fade_in,
//                        R.anim.fade_out)
                .replace(R.id.coffeeMachineContainerLayoutId, reviewsFrag)
                .addToBackStack("back")
                .commit();
        return true;
    }



    public static boolean initDataApplication() {
        SharedPreferences sharedPref = mainActivityRef.getSharedPreferences("SHARED_PREF_COFFEE_MACHINE", Context.MODE_PRIVATE);

        coffeeApp = DataStorageSingleton.getInstance(mainActivityRef.getApplicationContext());
        CoffeeAppLogic coffeeAppLogic = new CoffeeAppLogic(mainActivityRef.getApplicationContext());

        if(sharedPref != null) {
            String username = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USERNAME, null);
            String profilePicPath = sharedPref.getString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, null);
            String userId = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USER_ID,
                    Common.EMPTY_VALUE);
            if(userId.compareTo(Common.EMPTY_VALUE) != 0) {
                Log.e(TAG, "this is my username: " + username);
                coffeeAppLogic.setRegisteredUser(userId, profilePicPath, username); //TODO check empty value
                return true;
            } else {
                Log.e(TAG, "no username set");
                return false;
            }
        }
        return false;
    }

    public void setLoggedUserView(View mainView) {
        ((TextView) mainView.findViewById(R.id.loggedUserTextId)).setText(
                coffeeApp.getRegisteredUsername());

        LinearLayout loggedUserButton = (LinearLayout) mainView.findViewById(R.id.loggedUserButtonId);
        loggedUserButton.setOnClickListener(this);
/*        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainActivityRef.getBaseContext(), "logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(mainActivityRef.getSupportFragmentManager());
            }
        });*/

        Bitmap defaultIcon = BitmapFactory.decodeResource(mainActivityRef.getResources(), R.drawable.user_icon);
        Bitmap bitmap = BitmapCustomUtils.getRoundedBitmapByFile(coffeeApp.getRegisteredProfilePicturePath(),
                defaultIcon);
        ((ImageView) mainView.findViewById(R.id.loggedUserImageViewId)).setImageBitmap(bitmap);
    }

    public void setNotLoggedUserView(View mainView) {
        ((TextView) mainView.findViewById(R.id.loggedUserTextId)).setText("guest");

        LinearLayout loggedUserButton = (LinearLayout) mainView.findViewById(R.id.loggedUserButtonId);

        loggedUserButton.setOnClickListener(this);
/*
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainActivityRef.getBaseContext(), "not logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(mainActivityRef.getSupportFragmentManager());
            }
        });*/
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(mainActivityRef.getBaseContext(), "logged", Toast.LENGTH_LONG).show();

        Toast.makeText(mainActivityRef.getBaseContext(), "not logged", Toast.LENGTH_LONG).show();

        LoginFragment loginFragment = new LoginFragment();
        mainActivityRef.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.coffeeMachineContainerLayoutId, loginFragment,
                        Common.NEW_USER_FRAGMENT_TAG)
                .addToBackStack("back")
                .commit();

    }






/*
    public ArrayList<PieChart> getPieChartData() {
        ArrayList<PieChart> pieChartList = new ArrayList<PieChart>();

        int reviewNumber, reviewTotal = 8, cnt = 0;
        reviewNumber = 5;
        cnt += reviewNumber;
        pieChartList.add(new PieChart(getResources().getColor(R.color.light_green), (360 * reviewNumber) / reviewTotal));
        reviewNumber = 2;
        cnt += reviewNumber;
        pieChartList.add(new PieChart(getResources().getColor(R.color.light_yellow), (360 * reviewNumber) / reviewTotal));
        reviewNumber = 1;
        cnt += reviewNumber;
        pieChartList.add(new PieChart(getResources().getColor(R.color.light_black), (360 * reviewNumber) / reviewTotal));
        if(cnt != reviewTotal) {
            reviewNumber = reviewTotal - cnt;
            pieChartList.add(new PieChart(getResources().getColor(R.color.middle_grey), (360 * reviewNumber) / reviewTotal));
        }

        return pieChartList;
    }*/



}
