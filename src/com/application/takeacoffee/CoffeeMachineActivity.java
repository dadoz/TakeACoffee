package com.application.takeacoffee;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.takeacoffee.fragments.*;

import java.util.List;

public class CoffeeMachineActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    private static CoffeeMachineDataStorageApplication coffeeMachineApplication;
    private static FragmentActivity mainActivityRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coffe_machine_layout);

        mainActivityRef = this;
        Common.setCustomFont(findViewById(R.id.scrollViewContainerId),
                this.getAssets());

        //ACTION BAR
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPref = mainActivityRef.getPreferences(0);
        if(sharedPref.getBoolean(Common.FIRST_INIT_VIEW, true)) {
            firstInitView();
            return;
        }

        initView(savedInstanceState, mainActivityRef.getWindow().getDecorView());
    }

    private void firstInitView() {
        SharedPreferences sharedPref = mainActivityRef.getPreferences(0);
        sharedPref.edit().putBoolean(Common.FIRST_INIT_VIEW, false).commit();

        LoadingFragment loadingFragment = new LoadingFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.coffeeMachineContainerLayoutId, loadingFragment, Common.COFFEE_MACHINE_FRAGMENT_TAG)
                .commit();
    }

    private static boolean initDataApplication() {
        SharedPreferences sharedPref = mainActivityRef.getPreferences(0);
        if (sharedPref != null) {
            String username = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USERNAME, Common.EMPTY_VALUE);
            if (username.compareTo(Common.EMPTY_VALUE) != 0) {
                Log.e(TAG, "this is my username" + username);
                coffeeMachineApplication = (CoffeeMachineDataStorageApplication) mainActivityRef.getApplication();
                coffeeMachineApplication.coffeeMachineData.initRegisteredUserByUsername(username);
                String profilePicPath = sharedPref.getString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, Common.EMPTY_VALUE);
                if (profilePicPath == Common.EMPTY_VALUE) {
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

    public static void setLoggedUserView(View mainView) {
        ((TextView) mainView.findViewById(R.id.loggedUserTextId)).setText(
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser().getUsername());

        LinearLayout loggedUserButton = (LinearLayout) mainView.findViewById(R.id.loggedUserButtonId);
//        loggedUserButton.setBackground((getResources().getDrawable(R.drawable.button_rounded_shape)));
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainActivityRef.getBaseContext(), "logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(mainActivityRef.getSupportFragmentManager());
            }
        });

        Common.drawProfilePictureByPath((ImageView) mainView.findViewById(R.id.loggedUserImageViewId),
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser()
                        .getProfilePicturePath(), mainActivityRef.getResources()
                        .getDrawable(R.drawable.user_icon)
        );
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

    public static void setHeaderByFragmentId(int fragmentId, FragmentManager fragmentManager, String coffeeMachineId) {
        CoffeeMachineActivity.hideAllItemsOnHeaderBar();

        switch (fragmentId) {
            case 0:
                CoffeeMachineActivity.hideAllItemsOnHeaderBar();
                CoffeeMachineActivity.setItemOnHeaderBarById(R.id.headerMapButtonId, fragmentManager, null);
                CoffeeMachineActivity.setItemOnHeaderBarById(R.id.loggedUserButtonId, fragmentManager, null);
                break;
            case 1:
//                CoffeeMachineActivity.setItemOnHeaderBarById(R.id.loggedUserButtonId, fragmentManager);
                String coffeeMachineName = coffeeMachineApplication.coffeeMachineData
                        .getCoffeMachineById(coffeeMachineId).getName();
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

/*
    public android.os.Handler handler = new android.os.Handler() {
        Message msg;
        @Override
        public void handleMessage(Message msg) {
            this.msg = msg;
            super.handleMessage(msg);
        }

        public Message getMessage() {
            return this.msg;
        }
    };*/

    @Override
    public void onBackPressed() {
        //check get back action from LoginFragment
/*        final LoginFragment fragment = (LoginFragment) getSupportFragmentManager()
                .findFragmentByTag(Common.NEW_USER_FRAGMENT_TAG);
        if (fragment != null) {
            if (fragment.isVisible()) {
                (findViewById(R.id.loggedUserButtonId)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addChangeUserFragment(getSupportFragmentManager());
                    }
                });
            }
        }*/

        //check get back action from addReview
/*        final AddReviewFragment addReviewfragment = (AddReviewFragment) getSupportFragmentManager()
                .findFragmentByTag(Common.ADD_REVIEW_FRAGMENT_TAG);
        if (addReviewfragment != null) {
            if (addReviewfragment.isVisible()) {
                //headerAddReviewLayout TODO refactoring it
                View addMoreTextView = addReviewfragment.getView()
                        .findViewById(R.id.addMoreTextLayoutId);
                View headerAddReviewView = addReviewfragment.getView()
                        .findViewById(R.id.headerAddReviewLayoutId);
                if (addMoreTextView.getVisibility() == View.VISIBLE) {
                    addMoreTextView.setVisibility(View.GONE);
                    headerAddReviewView.setVisibility(View.VISIBLE);
                    addReviewfragment.getView().findViewById(R.id.addReviewButtonId)
                            .setVisibility(View.VISIBLE);
                    return;
                }
            }
        }
*/
/*        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            List<Fragment> fragm = getSupportFragmentManager().getFragments();
            for(Fragment f : fragm) {
                Log.e(TAG, "id fragment" + f.getId());
            }
            Fragment lastFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1);
            Log.e(TAG, "id fragment" + lastFragment.getId());
        }*/
        //check get back action from coffeeMachine
/*
        final CoffeeMachineFragment coffeeMachineFragment = (CoffeeMachineFragment)getSupportFragmentManager().findFragmentByTag(Common.COFFEE_MACHINE_FRAGMENT_TAG);
        if(coffeeMachineFragment != null) {
            if(coffeeMachineFragment.isVisible()) {
                View mapContainerLayout = coffeeMachineFragment.getView().findViewById(R.id.mapContainerLayoutId);
                View coffeeMachineTableLayout = coffeeMachineFragment.getView().findViewById(R.id.coffeeMachineTableLayoutId);
                if (mapContainerLayout.getVisibility() == View.VISIBLE) {
                    mapContainerLayout.setVisibility(View.GONE);
                    coffeeMachineTableLayout.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }*/

        super.onBackPressed();
    }


}