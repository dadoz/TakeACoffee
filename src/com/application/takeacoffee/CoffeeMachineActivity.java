package com.application.takeacoffee;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.adapters.ScreenSlidePagerAdapter;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.takeacoffee.fragments.AddReviewFragment;
import com.application.takeacoffee.fragments.CoffeeMachineFragment;
import com.application.takeacoffee.fragments.NewUserFragment;

public class CoffeeMachineActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    private static ViewPager mPager;
    private static PagerAdapter mPagerAdapter;

    private boolean loggedUser;
    private static CoffeeMachineDataStorageApplication coffeeMachineApplication;
    static FragmentActivity mainApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coffe_machine_layout);
        mainApplication = this;
        Common.setCustomFont(findViewById(R.id.scrollViewContainerId),
                this.getAssets());

        //ACTION BAR
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        loggedUser = initDataApplication();
        //change username
        if (loggedUser) {
            setLoggedUserView();
        } else {
            setNotLoggedUserView();
        }

        if (savedInstanceState == null) {
            initView();
        }
    }

    public static void addReviewByFragment(String coffeeMachineId) {
        try {
            mPager = (ViewPager) mainApplication.findViewById(R.id.pager);
            mPager.setVisibility(View.VISIBLE);

            mainApplication.findViewById(R.id.coffeeMachineContainerLayoutId).setVisibility(View.GONE);
            mPagerAdapter = new ScreenSlidePagerAdapter(mainApplication.getSupportFragmentManager(), coffeeMachineId);
            mPager.setAdapter(mPagerAdapter);

            mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    try {
                        ViewGroup headerAddReviewLayout = (ViewGroup) mainApplication.findViewById(R.id.headerTabReviewLayoutId);
                        //reset color background of tabs
                        for (int i = 0; i < Common.NUM_PAGES; i++) {
                            headerAddReviewLayout.getChildAt(i)
                                    .setBackgroundColor(mainApplication.getResources()
                                            .getColor(R.color.light_grey));
                        }

                        Common.ReviewStatusEnum reviewStatus = Common.parseStatusFromPageNumber(position);
                        Log.e(TAG, " - onPageSelected " + position);
                        switch (reviewStatus) {
                            case GOOD:
                                headerAddReviewLayout.getChildAt(position)
                                        .setBackgroundColor(mainApplication.getResources()
                                                .getColor(R.color.light_green));
                                break;
                            case NOTSOBAD:
                                headerAddReviewLayout.getChildAt(position)
                                        .setBackgroundColor(mainApplication.getResources()
                                                .getColor(R.color.light_yellow_lemon));
                                break;
                            case WORST:
                                headerAddReviewLayout.getChildAt(position)
                                        .setBackgroundColor(mainApplication.getResources()
                                                .getColor(R.color.light_violet));
                                break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideAddReviewView() {
        try {
            mPager = (ViewPager) mainApplication.findViewById(R.id.pager);
            mPager.setVisibility(View.GONE);
            mainApplication.findViewById(R.id.coffeeMachineContainerLayoutId).setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean initDataApplication() {
        SharedPreferences sharedPref = getPreferences(0);
        if (sharedPref != null) {
            String username = sharedPref.getString(Common.SHAREDPREF_REGISTERED_USERNAME, Common.EMPTY_VALUE);
            if (username.compareTo(Common.EMPTY_VALUE) != 0) {
                Log.e(TAG, "this is my username" + username);
                coffeeMachineApplication = (CoffeeMachineDataStorageApplication) getApplication();
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

    private void initView() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.coffeeMachineContainerLayoutId, new CoffeeMachineFragment(), Common.COFFEE_MACHINE_FRAGMENT_TAG)
                .commit();
    }

    public void replaceFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeMachineContainerLayoutId, new CoffeeMachineFragment(), Common.COFFEE_MACHINE_FRAGMENT_TAG)
                .commit();

    }

    public void setLoggedUserView() {
        ((TextView) findViewById(R.id.loggedUserTextId)).setText(
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser().getUsername());

        LinearLayout loggedUserButton = (LinearLayout) findViewById(R.id.loggedUserButtonId);
//        loggedUserButton.setBackground((getResources().getDrawable(R.drawable.button_rounded_shape)));
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(getSupportFragmentManager());
            }
        });

        Common.drawProfilePictureByPath((ImageView) findViewById(R.id.loggedUserImageViewId),
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser()
                        .getProfilePicturePath(), getResources()
                        .getDrawable(R.drawable.user_icon)
        );
    }


    public void setNotLoggedUserView() {
        ((TextView) findViewById(R.id.loggedUserTextId)).setText("guest");

        LinearLayout loggedUserButton = (LinearLayout) findViewById(R.id.loggedUserButtonId);
//        loggedUserButton.setBackground((getResources().getDrawable(R.drawable.button_rounded_shape)));
//        loggedUserButton.setText("new");
        loggedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "not logged", Toast.LENGTH_LONG).show();
                addChangeUserFragment(getSupportFragmentManager());
            }
        });
    }

    public static void addChangeUserFragment(FragmentManager fragManager) {
        NewUserFragment newUserFragment = new NewUserFragment();
        //add fragment content to add user
        fragManager.beginTransaction()
//                .setCustomAnimations(R.anim.fade_in,
//                        R.anim.fade_out)
                .replace(R.id.coffeeMachineContainerLayoutId, newUserFragment, Common.NEW_USER_FRAGMENT_TAG)
                .addToBackStack("back")
                .commit();
    }

    @Override
    public void onBackPressed() {
        //
        if (mPager != null && mPager.getVisibility() == View.VISIBLE) {
            mPager.setVisibility(View.GONE);
            mainApplication.findViewById(R.id.coffeeMachineContainerLayoutId).setVisibility(View.VISIBLE);
            mainApplication.findViewById(R.id.headerTabReviewLayoutId).setVisibility(View.GONE);
            return;
        }

        //check get back action from NewUserFragment
        final NewUserFragment fragment = (NewUserFragment) getSupportFragmentManager().findFragmentByTag(Common.NEW_USER_FRAGMENT_TAG);
        if (fragment != null) {
            if (fragment.isVisible()) {
                (findViewById(R.id.loggedUserButtonId)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    Common.displayError("change username", view.getContext());
                        addChangeUserFragment(getSupportFragmentManager());
                    }
                });
            }
        }

        //check get back action from addReview
        final AddReviewFragment addReviewfragment = (AddReviewFragment) getSupportFragmentManager().findFragmentByTag(Common.ADD_REVIEW_FRAGMENT_TAG);
        if (addReviewfragment != null) {
            if (addReviewfragment.isVisible()) {
                //headerAddReviewLayout TODO refactoring it
                View addMoreTextView = addReviewfragment.getView().findViewById(R.id.addMoreTextLayoutId);
                View headerAddReviewView = addReviewfragment.getView().findViewById(R.id.headerAddReviewLayoutId);
                if (addMoreTextView.getVisibility() == View.VISIBLE) {
                    addMoreTextView.setVisibility(View.GONE);
                    headerAddReviewView.setVisibility(View.VISIBLE);
                    addReviewfragment.getView().findViewById(R.id.addReviewButtonId).setVisibility(View.VISIBLE);
                    return;
                }
            }
        }

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