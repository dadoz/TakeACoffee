package com.application.takeacoffee.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.adapters.ReviewListAdapter;
import com.application.commons.Common;
import com.application.commons.HeaderUtils;
import com.application.dataRequest.CoffeeAppLogic;
import com.application.dataRequest.RESTLoader;
import com.application.datastorage.DataStorageSingleton;
import com.application.extraMenu.ExtraMenuController;
import com.application.models.Review;
import com.application.models.User;
import com.application.takeacoffee.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by davide on 08/04/14.
 */
public class ReviewListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse>,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = "ReviewListFragment";
    private static DataStorageSingleton coffeeApp;
    private static FragmentActivity mainActivityRef = null;

    private Common.ReviewStatusEnum reviewStatus;
    private View reviewListView, emptyView, moreReviewLoaderView;
    private String coffeeMachineId;
    private ListView listView;
    private ArrayList<Review> reviewListDataStorage;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        reviewListView = inflater.inflate(R.layout.review_list_fragment, container, false);

        listView = (ListView) reviewListView.findViewById(R.id.reviewsContainerListViewId);
        moreReviewLoaderView = LayoutInflater.from(mainActivityRef.getApplicationContext())
                .inflate(R.layout.more_review_loader_layout, listView, false);

        emptyView = inflater.inflate(R.layout.empty_data_status_layout, container, false);
        coffeeApp = DataStorageSingleton.getInstance(mainActivityRef.getApplicationContext());

        coffeeMachineId = this.getArguments().getString(Common.COFFEE_MACHINE_ID_KEY);
        reviewStatus = Common.ReviewStatusEnum.valueOf(this.getArguments().
                getString(Common.REVIEW_STATUS_KEY));

        long fromTimestamp = this.getArguments().getLong(Common.FROM_TIMESTAMP_KEY);
        long toTimestamp = this.getArguments().getLong(Common.TO_TIMESTAMP_KEY);

        Bundle bundle = createBundle(coffeeMachineId, fromTimestamp, toTimestamp);

        setHeader();

        setLoaderView(true);

        if (getLoaderManager().getLoader(RESTLoader.HTTPVerb.POST) == null) {
            getLoaderManager().initLoader(RESTLoader.HTTPVerb.POST, bundle, this)
                    .forceLoad();
        } else {
            initView(reviewListDataStorage, coffeeMachineId);
        }

        Common.setCustomFont(reviewListView, getActivity().getAssets());
        return reviewListView;
    }

    public void setHeader() {
        HeaderUtils.setHeaderByFragmentId(mainActivityRef, 1, getFragmentManager(), coffeeMachineId);
        mainActivityRef.findViewById(R.id.addReviewSwipeButtonId).setVisibility(View.INVISIBLE);

    }

    private void setLoaderView(boolean showLoader) {
        setReviewListHeaderBackgroundLabel((reviewListView
                .findViewById(R.id.reviewStatusTextViewId)), true);

        if (showLoader) {
            reviewListView.findViewById(R.id.containerReviewListId).setVisibility(View.GONE);
            reviewListView.findViewById(R.id.reviewLoaderLayoutId).setVisibility(View.VISIBLE);
            return;
        }
        reviewListView.findViewById(R.id.containerReviewListId).setVisibility(View.VISIBLE);
        reviewListView.findViewById(R.id.reviewLoaderLayoutId).setVisibility(View.GONE);
    }

    public void initView(ArrayList<Review> reviewList, final String coffeeMachineId) {
        if (reviewList == null) {
            Log.e(TAG, "empty review list");
            listView.setEmptyView(emptyView);
            return;
        }

        ReviewListAdapter reviewListenerAdapter = new ReviewListAdapter(mainActivityRef,
                R.layout.review_template, reviewList, coffeeMachineId);
        listView.setAdapter(reviewListenerAdapter);

        listView.setOnItemLongClickListener(this);
    }

    public void setReviewListHeaderBackgroundLabel(final View reviewStatusText, boolean setLabel) {
        //set changes icon
        String labelStatus = " - ";
        int colorViewStatus = 0;
        switch (reviewStatus) {
            case GOOD:
                labelStatus = "Good Reviews";
                colorViewStatus = getResources().getColor(R.color.light_green);
                break;
            case NOTSOBAD:
                labelStatus = "Not so bad Reviews";
                colorViewStatus = getResources().getColor(R.color.light_yellow_lemon);
                break;
            case WORST:
                labelStatus = "Terrible Reviews";
                colorViewStatus = getResources().getColor(R.color.light_violet);
                break;
        }

        reviewStatusText.setBackgroundColor(colorViewStatus);
        if (setLabel) {
            ((TextView) reviewStatusText).setText(labelStatus);
        }
    }



    /*********BUNDLE**********/

    //TODO MOVE THEM SMWHERE ELSE
    //BUNDLE move out maybe its better
    private Bundle createBundleUser(ArrayList<String> userIdList) {
        String action = "https://api.parse.com/1/functions/getUserListByUserIdList";
        Bundle bundle = new Bundle();
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("userIdList", new JSONArray(userIdList));
            bundle.putString("params", paramsObj.toString());
            Log.d(TAG, "params" + paramsObj.toString());
            bundle.putString("action", action);
            bundle.putString("requestType", "USER_REQ");
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bundle createBundle(String coffeeMachineId, long fromTimestamp, long toTimestamp) {
        String action = "https://api.parse.com/1/functions/getReviewByTimestampLimitOnResult";

        Bundle bundle = new Bundle();
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("coffeeMachineId", coffeeMachineId);
            paramsObj.put("toTimestamp", toTimestamp);
            paramsObj.put("fromTimestamp", fromTimestamp);
            bundle.putString("params", paramsObj.toString());
            bundle.putString("action", action);
            bundle.putString("requestType", "REVIEW_REQ");
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*********LOADER**********/
    @Override
    public Loader<RESTLoader.RESTResponse> onCreateLoader(int verb, Bundle bundle) {
        try {
//            int verb = RESTLoader.HTTPVerb.POST;
            Uri action = Uri.parse(bundle.getString("action"));
            String requestType = bundle.getString("requestType");
            return new RESTLoader(this.getActivity(), verb, action, bundle, requestType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<RESTLoader.RESTResponse> restResponseLoader,
                               RESTLoader.RESTResponse restResponse) {
        setLoaderView(false);

        if (restResponse.getCode() != 200) {
            Log.e(TAG, "Error on HTTP get/post - code: " + restResponse.getCode());
            if (restResponse.isReviewResponse()) {
                listView.setEmptyView(emptyView);
            }
            return;
        }

        Log.d(TAG, "this are data result" + restResponse.getData());
//        Log.d(TAG, "this are key result" + restResponse.getCode());

        if (restResponse.isReviewResponse()) {
            reviewResponse(restResponse);
            return;
        }

        if (restResponse.isUserResponse()) {
            userResponse(restResponse);
            return;
        }

        Log.e(TAG, "error - no one response is caught");

    }

    private void userResponse(RESTLoader.RESTResponse restResponse) {
        CoffeeAppLogic coffeeAppLogic = new CoffeeAppLogic(this.getActivity()
                .getApplicationContext());
        ArrayList<User> userList = (ArrayList<User>) restResponse.getUserListParser();
        coffeeAppLogic.addUserOnLocalListByList(userList);
        if (listView.getAdapter() != null) {
            ((ReviewListAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

    private void reviewResponse(RESTLoader.RESTResponse restResponse) {
        //            if(restResponse.getHasMoreReviews()) {
//                listView.addHeaderView(moreReviewLoaderView);
//            }
        ArrayList<Review> reviewList = restResponse.getReviewListParser();

        reviewListDataStorage = reviewList;

        ArrayList<String> userIdList = new ArrayList<>();
        for (Review review : reviewList) {
            userIdList.add(review.getUserId());
        }
        Bundle bundle = createBundleUser(userIdList);
        Log.d(TAG, "hey " + bundle.getString("requestType"));
        getLoaderManager().restartLoader(RESTLoader.HTTPVerb.POST, bundle, this).forceLoad();

        initView(reviewList, coffeeMachineId);
    }


    @Override
    public void onLoaderReset(Loader loader) {
        //TODO need to be implemented
        //delete all data
        coffeeApp.setReviewListTemp(null);
    }




    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //USEFUL
        Log.e(TAG, "on item click " + position + "id - " + view.getId());
        if(view.getId() == R.id.linearLayout) {
            Log.e(TAG, " id: linearLayout");
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        Review reviewObj = (Review) adapterView.getItemAtPosition(position);
        final View mainItemView = view.findViewById(R.id.mainItemViewId);

        //TODO CHECK THIS STATEMENT
        if (mainItemView != null &&
                mainItemView.getVisibility() == View.VISIBLE &&
                coffeeApp.isRegisteredUser() &&
                coffeeApp.checkIsMe(reviewObj.getUserId())) {
            try {
                final View extraMenuItemView = view.findViewById(R.id.extraMenuItemViewId);
                ReviewListAdapter adapter = ((ReviewListAdapter) adapterView.getAdapter());

                mainItemView.setVisibility(View.GONE);
                extraMenuItemView.setVisibility(View.VISIBLE);
                setReviewListHeaderBackgroundLabel(extraMenuItemView, false);

                int prevSelectedItemPosition = adapter
                        .getSelectedItemIndex();

                //DESELECT prev item
                if (prevSelectedItemPosition != Common.ITEM_NOT_SELECTED) {
                    int index = prevSelectedItemPosition - adapterView.getFirstVisiblePosition();
                    View v = adapterView.getChildAt(index);
                    v.findViewById(R.id.mainItemViewId).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.extraMenuItemViewId).setVisibility(View.GONE);
                }

                adapter.setSelectedItemIndex(position);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }


}





    /*    private void setPreviousReviewsButtonAction() {
        ListView listView = (ListView)reviewListView.findViewById(R.id.reviewsContainerListViewId);
        final ReviewListAdapter adapter = (ReviewListAdapter)listView.getAdapter();
        final View prevReviewsButtonId = reviewListView.findViewById(R.id.prevReviewsButtonId);

        DateTime dateTime = new DateTime();
        long oneWeekAgoTimestamp = CoffeeAppLogic.TimestampHandler.getOneWeekAgoTimestamp(dateTime);
        long todayTimestamp = CoffeeAppLogic.TimestampHandler.getTodayTimestamp(dateTime);

        CoffeeAppLogic coffeeAppLogic = new CoffeeAppLogic(mainActivityRef.getApplicationContext());
        final ArrayList<Review> prevReviewList = coffeeAppLogic.getReviewListByTimestamp(coffeeMachineId,
                reviewStatus, oneWeekAgoTimestamp, todayTimestamp);
        if(prevReviewList == null) {
            Log.e(TAG, "previous review list is empty");
            prevReviewsButtonId.setVisibility(View.GONE);
            return;
        }

        prevReviewsButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Review> reviewList = adapter.getList();
                reviewList.removeAll(reviewList);
                reviewList.addAll(prevReviewList);
                adapter.notifyDataSetChanged();
                prevReviewsButtonId.setVisibility(View.GONE);
            }
        });
    }
*/
/*
    private void setReviewListHeader(final String coffeeMachineId, final View reviewStatusText,
                                 final View reviewStatusAddImageView, boolean setTextLabel) {
        setReviewListHeaderBackgroundLabel(reviewStatusText, setTextLabel);

        if(reviewStatusAddImageView != null) {
            reviewStatusAddImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "add review on " + reviewStatus);
                    Bundle args = new Bundle();
                    args.putLong(Common.COFFEE_MACHINE_ID_KEY, coffeeMachineId);
                    args.putBoolean(Common.ADD_REVIEW_FROM_LISTVIEW, true);
                }
            });
        }
    }*/


