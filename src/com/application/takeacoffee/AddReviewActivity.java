package com.application.takeacoffee;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.application.commons.Common;
import com.application.dataRequest.CoffeeAppController;
import com.application.dataRequest.RestLoaderRetrofit;
import com.application.dataRequest.RestResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static com.application.dataRequest.RestLoaderRetrofit.HTTPAction.ADD_REVIEW_REQUEST;

public class AddReviewActivity extends FragmentActivity implements RatingBar.OnRatingBarChangeListener,
        View.OnClickListener, LoaderManager.LoaderCallbacks<RestResponse> {
    private static final String TAG = "CoffeeMachineActivity";

    public static CoffeeAppController coffeeAppController;
    private EditText reviewEditText;
    private RatingBar setStatusRatingBarView;
    private Common.ReviewStatusEnum statusRating = Common.ReviewStatusEnum.NOTSET;
    private View addReviewButtonView;
    private String coffeeMachineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_review_bottom_bar_layout);

        reviewEditText = (EditText) findViewById(R.id.reviewEditTextId);
        setStatusRatingBarView = (RatingBar) findViewById(R.id.setStatusRatingBarViewId);
        addReviewButtonView = findViewById(R.id.addReviewButtonId);

        //get coffee machine id
        coffeeMachineId = this.getIntent().getExtras().getString(Common.COFFEE_MACHINE_ID_KEY);
        coffeeAppController = new CoffeeAppController(this.getApplicationContext(), this.getApplication(), getLoaderManager());

        initView();
        Common.setCustomFont(this.getWindow().getDecorView(), this.getAssets());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void initView() {
        setStatusRatingBarView.setNumStars(5);
        setStatusRatingBarView.setStepSize(1);
        setStatusRatingBarView.setOnRatingBarChangeListener(this);

        addReviewButtonView.setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        Log.e(TAG, "rating" + rating);
        //parse rating status
        switch (Float.floatToIntBits(rating)) {
            case 1:
            case 2:
                statusRating = Common.ReviewStatusEnum.GOOD;
                break;
            case 3:
                statusRating = Common.ReviewStatusEnum.NOTSOBAD;
                break;
            case 4:
            case 5:
                statusRating = Common.ReviewStatusEnum.WORST;
                break;
        }
    }

    public boolean isRatingSet() {
        return statusRating != Common.ReviewStatusEnum.NOTSET;
    }

    public boolean isTextInserted() {
        return (reviewEditText.getText().toString().trim()).matches("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addReviewButtonId:
                //add data to list
                if(! coffeeAppController.isRegisteredUser()) {
                    Common.displayError(this.getApplicationContext(),
                            "You must be logged in before add review!");
                    return;
                }

                if(coffeeAppController.isLocalUser()) {
                    if(! coffeeAppController.registerLocalUser()) {
                        Common.displayError(this.getApplicationContext(),
                                    "Failed to register your username - check your internet connection!");
                        return;
                    }
                }

                String reviewText = "Hey - no text set";
                if(isRatingSet()) {
                    if(isTextInserted()) {
                        reviewText = reviewEditText.getText().toString().trim();
                    }

                    Common.hideKeyboard(this, reviewEditText);

                    //add review action
                    try {
                        long timestamp = new Date().getTime();

                        Bundle bundle = new Bundle();
                        bundle.putString("ACTION", ADD_REVIEW_REQUEST);
                        JSONObject params = new JSONObject();
                        params.put("a", coffeeAppController.getRegisteredUserId());
                        params.put("a", coffeeMachineId);
                        params.put("a", reviewText);
                        params.put("a", statusRating.name());
                        bundle.putString("DATA", params.toString());


//                        getLoaderManager().initLoader(RestLoader.HTTPVerb.POST, bundle, this).forceLoad();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        //finish application
        finish();
    }


    @Override
    public Loader<RestResponse> onCreateLoader(int verb, Bundle bundle) {
        //get data
        Object data = null;
        return new RestLoaderRetrofit(this, "action", data);
    }

    @Override
    public void onLoadFinished(Loader<RestResponse> restResponseLoader, RestResponse restResponse) {

    }

    @Override
    public void onLoaderReset(Loader<RestResponse> restResponseLoader) {

    }

}