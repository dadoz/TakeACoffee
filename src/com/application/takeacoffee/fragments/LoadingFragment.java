package com.application.takeacoffee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.commons.Common;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

/**
 * Created by davide on 31/05/14.
 */
public class LoadingFragment extends Fragment{
    private View loadingView;
    private FragmentActivity mainActivityRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        loadingView = inflater.inflate(R.layout.loading_fragment, container, false);

        initView();
        Common.setCustomFont(loadingView, getActivity().getAssets());
        return loadingView;
    }

    private void initView() {
        loadingView.findViewById(R.id.loadingLoginButtonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, new LoginFragment(), Common.NEW_USER_FRAGMENT_TAG)
                        .commit();
            }
        });


        loadingView.findViewById(R.id.loadingLoginAsGuestLayoutId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CoffeeMachineActivity.initView(null, mainActivityRef.getWindow().getDecorView());
                mainActivityRef.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId,
                                new CoffeeMachineFragment(),
                                Common.COFFEE_MACHINE_FRAGMENT_TAG)
                        .commit();
            }
        });

    }

}
