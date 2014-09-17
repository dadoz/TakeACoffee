package com.application.takeacoffee.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.commons.Common;
import com.application.commons.HeaderUtils;
import com.application.models.Review;
import com.application.models.Setting;
import com.application.models.User;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;
import com.facebook.android.DialogError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davide on 29/05/14.
 */
public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragmentTAG";
    private View settingsView;
    private ListView settListView;
    private FragmentActivity mainActivityRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        settingsView = inflater.inflate(R.layout.settings_fragment, container, false);

        initView();
        setHeader();
        Common.setCustomFont(settingsView, getActivity().getAssets());
        return settingsView;
    }

    private void initView() {
        ArrayList<Setting> settingList = new ArrayList<Setting>();
        settingList.add(new Setting ("enableDisableNotificationId", 0, R.drawable.more_icon, "Enable/Disable notification"));
        settingList.add(new Setting ("changeFacebookNotificationId", 1, R.drawable.facebook_icon, "Change facebook notification"));
        settingList.add(new Setting ("userSettingsId", 2, R.drawable.user_icon, "User settings"));
        settingList.add(new Setting ("ResetYourAccountId", 3, R.drawable.delete_icon, "Reset your account"));
        settListView = (ListView) settingsView.findViewById(R.id.settingsListViewId);
        settListView.setAdapter(new SettingsListAdapter(mainActivityRef, R.layout.settings_template, settingList));

        settListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });
    }

    public void setHeader() {
        HeaderUtils.hideAllItemsOnHeaderBar(mainActivityRef);
    }

    public class SettingsListAdapter extends ArrayAdapter<Setting> {
        private ArrayList<Setting> settingList;

        public SettingsListAdapter(Context context, int resource, ArrayList<Setting> settingList) {
            super(context, resource, settingList);
            this.settingList = settingList;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            Setting settingObj = settingList.get(position);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.settings_template, parent, false);

            ((ImageView) ((ViewGroup) rowView).getChildAt(0)).setImageDrawable(getResources().getDrawable(settingObj.getIconResourceId()));
            ((TextView) ((ViewGroup) rowView).getChildAt(1)).setText(settingObj.getName());

            switch (settingObj.getPosition()) {
                case 0:
                    CheckBox checkBox = new CheckBox(getContext());
                    ((ViewGroup) rowView.findViewById(R.id.checkboxContainerLayoutId)).addView(checkBox);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                            compoundButton.setChecked(status);
                            if(compoundButton.isChecked()) {
                                Common.displayError(getActivity().getApplicationContext(), "Notification enabled");
                            } else {
                                Common.displayError(getActivity().getApplicationContext(), "Notification disabled");
                            }
                        }
                    });
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }

            Common.setCustomFont(rowView, getActivity().getAssets());
            return rowView;
        }
    }
}
