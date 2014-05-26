package com.application.takeacoffee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.commons.Common;
import com.application.takeacoffee.R;

/**
 * Created by davide on 23/05/14.
 */
public class MapFragment extends Fragment {
    public View mapView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        mapView = inflater.inflate(R.layout.map_fragment, container, false);

        Common.setCustomFont(mapView, getActivity().getAssets());
        return mapView;
    }

}
