package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.adapters.CoffeeMachineGridAdapter;
import com.application.commons.BitmapCustomUtils;
import com.application.commons.Common;
import com.application.commons.HeaderUtils;
import com.application.dataRequest.CoffeeAppController;
import com.application.dataRequest.RestLoader;
import com.application.dataRequest.RestResponse;
import com.application.models.CoffeeMachine;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

import java.util.ArrayList;

/**
 * Created by davide on 3/13/14.
 */
public class CoffeeMachineFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<RestResponse>, AdapterView.OnItemClickListener {
    private static final String TAG = "coffeeMachineFragment";
    private static FragmentActivity mainActivityRef;
    private static LinearLayout settingsLayout;
    private View coffeeMachineView;
    private View emptyCoffeeMachineView;
    private GridView coffeeMachineGridLayout;
    private CoffeeAppController coffeeAppController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityRef = (CoffeeMachineActivity) activity;
        coffeeAppController = ((CoffeeMachineActivity) mainActivityRef).getCoffeeAppController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        coffeeMachineView = inflater.inflate(R.layout.coffee_machine_fragment, container, false);
        settingsLayout = (LinearLayout) coffeeMachineView.findViewById(R.id.settingsLayoutId);
        emptyCoffeeMachineView = coffeeMachineView.findViewById(R.id.emptyCoffeeMachineLayoutId);
        coffeeMachineGridLayout = (GridView) coffeeMachineView.findViewById(R.id.coffeeMachineGridLayoutId);

/*        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                            }
        });*/
//        initDataApplication();
//        setHeader();
        Common.setCustomFont(coffeeMachineView, this.getActivity().getAssets());
        return coffeeMachineView;
    }

    private void initOnLoadView() {

    }

    public void initView() {
        coffeeMachineGridLayout.setAdapter(new CoffeeMachineGridAdapter(this.getActivity(),
                R.layout.coffe_machine_template, coffeeAppController.getCoffeeMachineList()));
        coffeeMachineGridLayout.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {

            case R.id.loggedUserButtonId :
                Toast.makeText(mainActivityRef.getBaseContext(), "logged", Toast.LENGTH_LONG).show();
                Toast.makeText(mainActivityRef.getBaseContext(), "not logged", Toast.LENGTH_LONG).show();
                LoginFragment loginFragment = new LoginFragment();
                mainActivityRef.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, loginFragment,
                                Common.NEW_USER_FRAGMENT_TAG)
                        .addToBackStack("back")
                        .commit();
                break;
            case R.id.emptyCoffeeMachineLayoutId :
                Bundle args = new Bundle();
                args.putString(Common.REVIEW_STATUS_KEY, Common.ReviewStatusEnum.GOOD.name());
                args.putString(Common.COFFEE_MACHINE_ID_KEY, null);

                DashboardReviewFragment reviewDashboardFragment = new DashboardReviewFragment();
                reviewDashboardFragment.setArguments(args);
                mainActivityRef.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, reviewDashboardFragment)
                        .addToBackStack("back")
                        .commit();
                break;
            case R.id.settingsLayoutId :
                SettingsFragment settingsFragment = new SettingsFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, settingsFragment)
                        .addToBackStack("Fragment").commit();
                break;

        }

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        CoffeeMachine coffeeMachine = (CoffeeMachine) adapterView.getAdapter().getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString(Common.COFFEE_MACHINE_ID_KEY, coffeeMachine.getId());
        bundle.putString(Common.REVIEW_STATUS_KEY, Common.ReviewStatusEnum.GOOD.name()); //THIS IS MY INITIAL STATUS

        DashboardReviewFragment reviewsFrag = new DashboardReviewFragment();
        reviewsFrag.setArguments(bundle);

        mainActivityRef.getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeMachineContainerLayoutId, reviewsFrag)
                .addToBackStack("back")
                .commit();

    }

    public static class ViewHolder {
        public TextView nameTextView;
        public ImageView iconImageView;


    }



    @Override
    public Loader<RestResponse> onCreateLoader(int verb, Bundle params) {
        Uri action = Uri.parse(params.getString("action"));
        String requestType = params.getString("requestType");

        return new RestLoader(this.getActivity(), verb, action, params, requestType);
    }

    @Override
    public void onLoadFinished(Loader<RestResponse> restResponseLoader, RestResponse restResponse) {
        Log.e(TAG, "hey - finish load resources");

        try {
            String filename = "coffee_machines.json";
            String data = RestResponse.getJSONDataMockup(this.getActivity(), filename);
//            String data = restResponse.getData();
            ArrayList<CoffeeMachine> coffeeMachinesList = restResponse.coffeeMachineParser(data);
            coffeeAppController.setCoffeeMachineList(coffeeMachinesList);
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


    //LOGGED USER CLASS - please
    public boolean initDataApplication() {
        SharedPreferences sharedPref = mainActivityRef.getSharedPreferences("SHARED_PREF_COFFEE_MACHINE", Context.MODE_PRIVATE);


        //LOAD COFFEE MACHINE DATA
        Bundle params = RestResponse.createBundleCoffeeMachine();
        this.getLoaderManager().initLoader(RestLoader.HTTPVerb.GET, params, this).forceLoad();


        if(sharedPref != null) {
            String username = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USERNAME, null);
            String profilePicPath = sharedPref.getString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, null);
            String userId = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USER_ID,
                    Common.EMPTY_VALUE);
            if(userId.compareTo(Common.EMPTY_VALUE) != 0) {
                Log.e(TAG, "this is my username: " + username);
                coffeeAppController.setRegisteredUser(userId, profilePicPath, username); //TODO check empty value
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
                coffeeAppController.getRegisteredUsername());
        LinearLayout loggedUserButton = (LinearLayout) mainView.findViewById(R.id.loggedUserButtonId);
        Bitmap defaultIcon = BitmapFactory.decodeResource(mainActivityRef.getResources(), R.drawable.user_icon);
        Bitmap bitmap = BitmapCustomUtils.getRoundedBitmapByFile(coffeeAppController.getRegisteredProfilePicturePath(),
                defaultIcon);
        ((ImageView) mainView.findViewById(R.id.loggedUserImageViewId)).setImageBitmap(bitmap);

        loggedUserButton.setOnClickListener(this);
    }

    public void setNotLoggedUserView(View mainView) {
        ((TextView) mainView.findViewById(R.id.loggedUserTextId)).setText("guest");
        LinearLayout loggedUserButton = (LinearLayout) mainView.findViewById(R.id.loggedUserButtonId);

        loggedUserButton.setOnClickListener(this);
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
