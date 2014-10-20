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
import android.widget.ImageView;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppController;
import com.application.dataRequest.RestLoader;
import com.application.dataRequest.RestResponse;
import com.application.models.Review;
import com.application.models.ReviewCounter;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;
import com.application.commons.TimestampHandler;
import org.joda.time.DateTime;

/**
 * Created by davide on 26/05/14.
 */
public class DashboardReviewFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<RestResponse>,
        View.OnClickListener {
    private static final String TAG = "ChoiceReviewFragmentTAG";
    private View dashboardReviewView; // NEVER STATIC
    private Bundle args;

    private static View footerChoiceReviewView;
    private static String coffeeMachineId;
    private static FragmentActivity mainActivityRef;
    private static Common.ReviewStatusEnum reviewStatus;
    private static TextView choiceReviewCounterView;
    private boolean isListViewEmpty;
    private long fromTimestamp;
    private long toTimestamp;
    private CoffeeAppController coffeeAppController;
    private View coffeeMachineNameLabelView;
    private View coffeeMachineStatusView;
    private View reviewDashboardContainerLayout;
    private View coffeeMachineStatusTextView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityRef = (CoffeeMachineActivity) activity;
        coffeeAppController = ((CoffeeMachineActivity) mainActivityRef).getCoffeeAppController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        dashboardReviewView = inflater.inflate(R.layout.dashboard_review_template, container, false);
        Common.setCustomFont(dashboardReviewView, getActivity().getAssets());
        return dashboardReviewView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        footerChoiceReviewView = dashboardReviewView.findViewById(R.id.footerChoiceReviewLayoutId);
        coffeeMachineNameLabelView = dashboardReviewView.findViewById(R.id.coffeeMachineNameContainerId);
        coffeeMachineStatusTextView = dashboardReviewView.findViewById(R.id.coffeeMachineStatusTextId);
        reviewDashboardContainerLayout = dashboardReviewView.findViewById(R.id.reviewDashboardContainerLayoutId);


/*        choiceReviewCounterView = (TextView) dashboardReviewView
                .findViewById(R.id.choiceReviewCounterTextView);*/

        reviewStatus = Review.parseStatus(getArguments().getString(Common.REVIEW_STATUS_KEY));
        coffeeMachineId = this.getArguments().getString(Common.COFFEE_MACHINE_ID_KEY);
        args = new Bundle(); //TODO replace with bundle = bundle
        args.putAll(getArguments());

        initOnLoadView();

        //TEST
        reviewStatus = Common.ReviewStatusEnum.GOOD;
    }

    public void initOnLoadView() {
        setLoaderView(true);
        DateTime dateTime = new DateTime();
        fromTimestamp = TimestampHandler.getOneWeekAgoTimestamp(dateTime);
        toTimestamp = TimestampHandler.getTodayTimestamp(dateTime);
        Bundle bundle = RestResponse.createBundleReviewDashboard(coffeeMachineId,
                fromTimestamp, toTimestamp);
        getLoaderManager().initLoader(RestLoader.HTTPVerb.POST, bundle, this).forceLoad();

    }

    public void initView() {
        switch (reviewStatus) {
            case GOOD:
                ((TextView) coffeeMachineStatusTextView).setText(Common.GOOD_STATUS_STRING);
                ((ImageView) coffeeMachineStatusView).setImageDrawable(
                        getResources().getDrawable(R.drawable.crown_icon));
            case NOTSOBAD:
                ((TextView) coffeeMachineStatusTextView).setText(Common.NOTSOBAD_STATUS_STRING);
                ((ImageView) coffeeMachineStatusView).setImageDrawable(
                        getResources().getDrawable(R.drawable.drink_icon));
            case WORST:
                ((TextView) coffeeMachineStatusTextView).setText(Common.TERRIBLE_STATUS_STRING);
                ((ImageView) coffeeMachineStatusView).setImageDrawable(
                        getResources().getDrawable(R.drawable.skull_icon));
        }

        //set coffee machine name
        ((TextView) coffeeMachineNameLabelView).setText("coffee machine name");

//        Log.e(TAG, "review counter" + reviewCounter);
//        choiceReviewCounterView.setText(reviewCounter);

        String reviewCounter = coffeeAppController.getReviewCounterToString(coffeeMachineId,
                reviewStatus, Long.parseLong("1410400000000")); //TODO test
        isListViewEmpty = reviewCounter.equals(Common.EMPTY_REVIEW_STRING);
        reviewDashboardContainerLayout.setOnClickListener(this);
        footerChoiceReviewView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.footerChoiceReviewLayoutId :
                AddReviewFragment addReviewFragment = new AddReviewFragment();
                addReviewFragment.setArguments(args);

                mainActivityRef.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, addReviewFragment)
                        .addToBackStack("back").commit();
                break;
/*            case R.id.goodReviewButtonId :
                reviewStatus = Common.ReviewStatusEnum.GOOD;
                break;
            case R.id.notsobadReviewButtonId:
                reviewStatus = Common.ReviewStatusEnum.NOTSOBAD;
                break;
            case R.id.worsReviewButtonId :
                reviewStatus = Common.ReviewStatusEnum.WORST;
                break;*/
            case R.id.reviewDashboardContainerLayoutId :
                if(isListViewEmpty) {
                    Common.displayError(mainActivityRef.getApplicationContext(), "Empty review :(");
                    return;
                }

                ReviewListFragment fragmentObj = new ReviewListFragment();
                args.putString(Common.REVIEW_STATUS_KEY, reviewStatus.name());
                args.putLong(Common.FROM_TIMESTAMP_KEY, fromTimestamp);
                args.putLong(Common.TO_TIMESTAMP_KEY, toTimestamp);

                fragmentObj.setArguments(args);

                mainActivityRef.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, fragmentObj)
                        .addToBackStack("back")
                        .commit();
                break;
        }
    }

    @Override
    public Loader<RestResponse> onCreateLoader(int verb, Bundle params) {
        try {
            Uri action = Uri.parse(params.getString("action"));
            String requestType = params.getString("requestType");
            return new RestLoader(this.getActivity(), verb, action, params, requestType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<RestResponse> restResponseLoader, RestResponse restResponse) {
        try {
            setLoaderView(false);
//            String data = restResponse.getData();
            String filename = "count_review.json";
            String data = RestResponse.getJSONDataMockup(this.getActivity(), filename);
            ReviewCounter reviewCounter = restResponse.parseCountOnReviewsData(data);
            if(reviewCounter != null) {
                coffeeAppController.addOnReviewCounterList(reviewCounter);
            }

            Log.d(TAG, "this are data result" + data);
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<RestResponse> restResponseLoader) {

    }

    private void setLoaderView(boolean showLoader) {
        if(showLoader) {
            dashboardReviewView.findViewById(R.id.reviewDashboardContainerLayoutId).setVisibility(View.GONE);
            dashboardReviewView.findViewById(R.id.reviewLoaderLayoutId).setVisibility(View.VISIBLE);
            return;
        }
        dashboardReviewView.findViewById(R.id.reviewDashboardContainerLayoutId).setVisibility(View.VISIBLE);
        dashboardReviewView.findViewById(R.id.reviewLoaderLayoutId).setVisibility(View.GONE);
    }

    //TODO refactor it:D
/*    public class ReviewListButtonListener implements View.OnClickListener {
        private final long fromTimestamp;
        private final long toTimestamp;
        private boolean isListViewEmpty;
        private Common.ReviewStatusEnum reviewStatus;

        public ReviewListButtonListener(boolean isListViewEmpty,
                                        Common.ReviewStatusEnum reviewStatus,
                                        long fromTimestamp, long toTimestamp) {
            this.isListViewEmpty = isListViewEmpty;
            this.reviewStatus = reviewStatus;
            this.fromTimestamp = fromTimestamp;
            this.toTimestamp = toTimestamp;
        }

        @Override
        public void onClick(View view) {
                if(isListViewEmpty) {
                    Common.displayError(mainActivityRef.getApplicationContext(), "Empty review :(");
                    return;
                }

                ReviewListFragment fragmentObj = new ReviewListFragment();
                args.putString(Common.REVIEW_STATUS_KEY, reviewStatus.name());
                args.putLong(Common.FROM_TIMESTAMP_KEY, fromTimestamp);
                args.putLong(Common.TO_TIMESTAMP_KEY, toTimestamp);

                fragmentObj.setArguments(args);

                mainActivityRef.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, fragmentObj)
                        .addToBackStack("back")
                        .commit();

        }
    }
*/
}
