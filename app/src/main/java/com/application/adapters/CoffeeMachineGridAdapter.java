package com.application.adapters;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppController;
import com.application.models.CoffeeMachine;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;
import com.application.takeacoffee.fragments.CoffeeMachineFragment;

import java.util.ArrayList;

/****ADAPTER****/
public class CoffeeMachineGridAdapter extends ArrayAdapter<CoffeeMachine> {
    private static final String TAG = "ReviewListAdapter";
    private final CoffeeAppController coffeeAppController;
    private ArrayList<CoffeeMachine> coffeeMachineList;
    private FragmentActivity mainActivityRef;

    public CoffeeMachineGridAdapter(FragmentActivity activity, int resource,
                                    ArrayList<CoffeeMachine> coffeeMachineList) {
        super(activity.getApplicationContext(), resource, coffeeMachineList);
        this.mainActivityRef = activity;
        this.coffeeMachineList = coffeeMachineList;
        this.coffeeAppController = ((CoffeeMachineActivity) mainActivityRef).getCoffeeAppController();
        //SAVE MEMORY DEFAULT ICON ALLOCATION
//        this.defaultIcon = BitmapFactory.decodeResource(mainActivityRef.getResources(), R.drawable.user_icon);

    }

    public ArrayList<CoffeeMachine> getList() {
        return this.coffeeMachineList;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        CoffeeMachine coffeeMachine = coffeeMachineList.get(position);

        CoffeeMachineFragment.ViewHolder holder;
//            if(convertView == null) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.coffe_machine_template, null);

        holder = new CoffeeMachineFragment.ViewHolder();
        holder.nameTextView = ((TextView) convertView.findViewById(R.id.coffeeMachineNameTextId));
        holder.iconImageView = (ImageView) convertView.findViewById(R.id.coffeeIconId);
        convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }

        holder.nameTextView.setText(coffeeMachine.getName());
        holder.nameTextView.setTextColor(mainActivityRef
                .getResources().getColor(R.color.light_black));
        holder.iconImageView.setImageResource(R.drawable.coffee_cup_icon);

        //retrieve icon from server volley
        coffeeAppController.getCoffeeMachineIcon((coffeeMachine.getIconPath()), holder.iconImageView);

        //TODO this gave me problem
        Common.setCustomFont(convertView, mainActivityRef.getAssets());
        return convertView;
    }
}
