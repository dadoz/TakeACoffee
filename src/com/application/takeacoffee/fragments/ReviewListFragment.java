package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.application.dataRequest.CoffeeAppController;
import com.application.dataRequest.RestLoader;
import com.application.dataRequest.RestResponse;
import com.application.models.Review;
import com.application.models.User;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;
import com.application.commons.TimestampHandler;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by davide on 08/04/14.
 */
public class ReviewListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<RestResponse>,
        AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "ReviewListFragment";
    private static FragmentActivity mainActivityRef = null;

    private Common.ReviewStatusEnum reviewStatus;
    private View reviewListView, emptyView, moreReviewLoaderView;
    private String coffeeMachineId;
    private ListView listView;
    private ArrayList<Review> reviewListDataStorage;
    private CoffeeAppController coffeeAppController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityRef = (CoffeeMachineActivity) activity;
        coffeeAppController = ((CoffeeMachineActivity) mainActivityRef).getCoffeeAppController();
//        coffeeApp = (DataStorageApplication) getActivity().getApplication();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        reviewListView = inflater.inflate(R.layout.review_list_fragment, container, false);

        listView = (ListView) reviewListView.findViewById(R.id.reviewsContainerListViewId);
        moreReviewLoaderView = LayoutInflater.from(mainActivityRef.getApplicationContext())
                .inflate(R.layout.more_review_loader_layout, listView, false);

        emptyView = inflater.inflate(R.layout.empty_data_status_layout, container, false);

        coffeeAppController = ((CoffeeMachineActivity) mainActivityRef)
                .getCoffeeAppController();

        coffeeMachineId = this.getArguments().getString(Common.COFFEE_MACHINE_ID_KEY);
        reviewStatus = Common.ReviewStatusEnum.valueOf(this.getArguments().
                getString(Common.REVIEW_STATUS_KEY));

        long fromTimestamp = this.getArguments().getLong(Common.FROM_TIMESTAMP_KEY);
        long toTimestamp = this.getArguments().getLong(Common.TO_TIMESTAMP_KEY);

        Bundle bundle = RestResponse.createBundleReview(coffeeMachineId, fromTimestamp, toTimestamp);
        setHeader();
        setLoaderView(true);

        if (getLoaderManager().getLoader(RestLoader.HTTPVerb.POST) == null) {
            getLoaderManager().initLoader(RestLoader.HTTPVerb.POST, bundle, this)
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
//        setReviewListHeaderBackgroundLabel((reviewListView
//                .findViewById(R.id.reviewStatusTextViewId)), true);

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
        listView.setOnItemClickListener(this);
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




    /*********LOADER**********/
    @Override
    public Loader<RestResponse> onCreateLoader(int verb, Bundle bundle) {
        try {
            Uri action = Uri.parse(bundle.getString("action"));
            String requestType = bundle.getString("requestType");
            return new RestLoader(this.getActivity(), verb, action, bundle, requestType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<RestResponse> restResponseLoader,
                               RestResponse restResponse) {
        setLoaderView(false);

        try {
            Log.d(TAG, "this are data result" + restResponse.getData());
    /*        if (restResponse.getCode() != 200) {
                Log.e(TAG, "Error on HTTP get/post - code: " + restResponse.getCode());
                if (restResponse.isReviewResponse()) {
                    listView.setEmptyView(emptyView);
                }
                return;
            }*/

            if(restResponse.getRequestType() == null) {
                reviewResponse(restResponse);
                return;
            }

            switch(restResponse.getRequestType()) {
                case RestResponse.REVIEW_REQUEST:
                    reviewResponse(restResponse);
                    break;
                case RestResponse.USER_REQUEST:
                    userResponse(restResponse);
                    break;
                case RestResponse.MORE_REVIEW_REQUEST:
                    moreReviewResponse(restResponse);
                    break;
                default:
                    Log.e(TAG, "error - no valid response");
                    break;
            }

            reviewResponse(restResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

/*        if (restResponse.isReviewResponse()) {
            reviewResponse(restResponse);
            return;
        }

        if (restResponse.isUserResponse()) {
            userResponse(restResponse);
            return;
        }


        if (restResponse.isMoreReviewResponse()) {
            moreReviewResponse(restResponse);
            return;
        }
*/
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //TODO need to be implemented
        //delete all data
        coffeeAppController.resetReviewListTemp();
    }

    private void moreReviewResponse(RestResponse restResponse) {
        if(! restResponse.getHasMoreReviews()) {
            listView.removeHeaderView(moreReviewLoaderView);
        }

        String filename = "reviews.json";
        String data = RestResponse.getJSONDataMockup(this.getActivity(), filename);
        ArrayList<Review> reviewList = restResponse.getReviewListParser(data);

        Collections.reverse(reviewList);

        reviewListDataStorage.addAll(0, reviewList);

        ArrayList<String> userIdList = new ArrayList<>();
        for (Review review : reviewList) {
            userIdList.add(review.getUserId());
        }
        Bundle bundle = RestResponse.createBundleUser(userIdList);
        Log.d(TAG, "hey " + bundle.getString("requestType"));
        getLoaderManager().restartLoader(RestLoader.HTTPVerb.POST, bundle, this).forceLoad();

        if (listView.getAdapter() != null) {
            try {
                ((ReviewListAdapter) ((WrapperListAdapter) listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
            } catch (Exception e) {
                ((ReviewListAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        }

    }

    private void userResponse(RestResponse restResponse) {
        ArrayList<User> userList = restResponse.getUserListParser();
        //TODO move in coffeeAppLogic
        coffeeAppController.addUserOnLocalListByList(userList);
        if (listView.getAdapter() != null) {
            try {
                ((ReviewListAdapter) ((WrapperListAdapter) listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
            } catch (Exception e) {
                ((ReviewListAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    private void reviewResponse(RestResponse restResponse) {
        if(restResponse.getHasMoreReviews()) {
            listView.addHeaderView(moreReviewLoaderView);
        }


        String filename = "reviews.json";
        String data = RestResponse.getJSONDataMockup(this.getActivity(), filename);

        ArrayList<Review> reviewList = RestResponse.getReviewListParser(data);

        reviewListDataStorage = reviewList;

        ArrayList<String> userIdList = new ArrayList<>();
        for (Review review : reviewList) {
            userIdList.add(review.getUserId());
        }
        Bundle bundle = RestResponse.createBundleUser(userIdList);
        Log.d(TAG, "hey " + bundle.getString("requestType"));
        getLoaderManager().restartLoader(RestLoader.HTTPVerb.POST, bundle, this).forceLoad();

        initView(reviewList, coffeeMachineId);
    }




    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //USEFUL
        Log.e(TAG, "on item click " + position + "id - " + view.getId());
        if(view.getId() == R.id.linearLayout) {
            Log.e(TAG, " id: linearLayout");
        }
        //more review click
        if(view.getId() == R.id.loadOlderReviewLayoutId) {
            try{
                Review firstReview = reviewListDataStorage.get(0);
                String latestReviewId = firstReview.getId();
                DateTime dateTime = new DateTime(firstReview.getTimestamp());
                long fromTimestamp = TimestampHandler.getOneWeekAgoTimestamp(dateTime);
                Bundle bundle = RestResponse.createBundleMoreReview(coffeeMachineId, latestReviewId, fromTimestamp);
                Log.d(TAG, "hey " + bundle.getString("requestType"));
                getLoaderManager().restartLoader(RestLoader.HTTPVerb.POST, bundle, this).forceLoad();
            } catch (Exception e) {
                Log.e(TAG, "failed to load more review");
            }
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(view.getId() == R.id.loadOlderReviewLayoutId) {
            return true;
        }

        Review reviewObj = (Review) adapterView.getItemAtPosition(position);
        final View mainItemView = view.findViewById(R.id.mainItemViewId);

        //TODO CHECK THIS STATEMENT
        if (mainItemView != null &&
                mainItemView.getVisibility() == View.VISIBLE &&
                coffeeAppController.checkIsMe(reviewObj.getUserId())) {
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

