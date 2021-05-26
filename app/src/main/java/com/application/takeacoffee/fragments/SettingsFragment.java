package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.adapters.SettingListAdapter;
import com.application.commons.Common;
import com.application.commons.HeaderUtils;
import com.application.models.Review;
import com.application.models.Setting;
import com.application.models.User;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davide on 29/05/14.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "SettingsFragmentTAG";
    private View settingsView;
    private ListView settListView;
    private FragmentActivity mainActivityRef;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityRef = (CoffeeMachineActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        settingsView = inflater.inflate(R.layout.settings_fragment, container, false);
        settListView = (ListView) settingsView.findViewById(R.id.settingsListViewId);

        initView();
//        setHeader();
        Common.setCustomFont(settingsView, getActivity().getAssets());
        return settingsView;
    }

    private void initView() {
        ArrayList<Setting> settingList = new ArrayList<Setting>();
        settingList.add(new Setting ("enableDisableNotificationId",
                0, R.drawable.more_icon, "Enable/Disable notification"));
        settingList.add(new Setting ("changeFacebookNotificationId",
                1, R.drawable.facebook_icon, "Change facebook notification"));
        settingList.add(new Setting ("userSettingsId",
                2, R.drawable.user_icon, "User settings"));
        settingList.add(new Setting ("ResetYourAccountId",
                3, R.drawable.delete_icon, "Reset your account"));

        settListView.setAdapter(new SettingListAdapter(
                mainActivityRef, R.layout.settings_template, settingList));
        settListView.setOnItemClickListener(this);
    }

    public void setHeader() {
        HeaderUtils.hideAllItemsOnHeaderBar(mainActivityRef);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Setting settingObj = (Setting) adapterView.getAdapter().getItem(position);

        switch (settingObj.getPosition()) {
            case 0:
                break;
            case 1:
                //dialog
                Common.displayError(getActivity().getApplicationContext(), "Enable/disable facebook integration");
                break;
            case 2:
                getFragmentManager().beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, new LoginFragment())
                        .addToBackStack(null).commit();
                break;
            case 3:
                Common.displayError(getActivity().getApplicationContext(), "dialog");
                break;

        }

    }


}
