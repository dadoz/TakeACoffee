package com.application.takeacoffee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.commons.Common;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by davide on 23/05/14.
 */
public class MapFragment extends Fragment {
    private View mapView;
    private GoogleMap mMap;
    private FragmentActivity mainActivityRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        mainActivityRef = getActivity();
        mapView = inflater.inflate(R.layout.map_fragment, container, false);
        mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        initView();
        setHeader();
        Common.setCustomFont(mapView, getActivity().getAssets());
        return mapView;
    }

    public void initView() {
        mainActivityRef.findViewById(R.id.headerMapButtonId).setVisibility(View.GONE);

        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            // The Map is verified. It is now safe to manipulate the map.
            mMap.setMyLocationEnabled(true);
            final LatLng PERTH = new LatLng(45.0631, 7.6611);
            Marker perth = mMap.addMarker(new MarkerOptions()
                    .position(PERTH)
                    .draggable(true));
        }
    }
    private void setHeader() {
        CoffeeMachineActivity.setHeaderByFragmentId(2, getFragmentManager(), Common.EMPTY_VALUE);
    }

    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.map));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }
}
