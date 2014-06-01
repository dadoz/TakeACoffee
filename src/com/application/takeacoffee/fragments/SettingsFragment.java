package com.application.takeacoffee.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.models.Review;
import com.application.models.Setting;
import com.application.models.User;
import com.application.takeacoffee.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davide on 29/05/14.
 */
public class SettingsFragment extends Fragment {
    private View settingsView;
    private ListView settListView;
    private FragmentActivity mainActivityRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        settingsView = inflater.inflate(R.layout.settings_fragment, container, false);

        initView();
        Common.setCustomFont(settingsView, getActivity().getAssets());
        return settingsView;
    }

    private void initView() {
        ArrayList<Setting> settingList = new ArrayList<Setting>();
        settingList.add(new Setting ("id-fake", R.drawable.add_icon, "Enable/Disable notification"));
        settingList.add(new Setting ("id-fake", R.drawable.add_icon, "Change facebook notification"));
        settingList.add(new Setting ("id-fake", R.drawable.add_icon, "User"));
        settingList.add(new Setting ("id-fake", R.drawable.add_icon, "Delete my account"));
        settListView = (ListView) settingsView.findViewById(R.id.settingsListViewId);
        settListView.setAdapter(new SettingsListAdapter(mainActivityRef, R.layout.settings_template, settingList));
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

            Common.setCustomFont(rowView, getActivity().getAssets());
            return rowView;
        }
    }
}
