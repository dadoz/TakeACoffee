package com.application.dataRequest;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import com.application.models.Review;
import com.application.models.User;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by davide on 15/09/14.
 */
public class RestLoaderRetrofit extends AsyncTaskLoader<RestResponse> {
    private static final String TAG = "RESTLoader";
    private final String mAction;
    private final RestAdapter restAdapter;
    private final RetrofitServiceInterface parseService;
    private final Object params;

    public RestLoaderRetrofit(FragmentActivity activity, String action, Object params) {
        super(activity);
        mAction = action;
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.parse.com/1") // to baseUrl
                .setRequestInterceptor(requestInterceptor)
                .build();

        parseService = restAdapter.create(RetrofitServiceInterface.class);
        this.params = params;
    }

    @Override
    public RestResponse loadInBackground() {
        try{
            if (mAction == null) {
                //TODO HANDLE It
                return null;
            }

            Object data = null;
            Log.e(TAG, "You did not define an action. REST call canceled.");
            switch (mAction) {
                case HTTPAction.COFFEE_MACHINE_REQUEST:
                    data = parseService.listCoffeeMachine();
                    break;
                case HTTPAction.MORE_REVIEW_REQUEST:
                    User user = null;
                    data = parseService.listMoreReview(user, null);
                    break;
                case HTTPAction.REVIEW_REQUEST:
                    data = parseService.listReview();
                    break;
                case HTTPAction.REVIEW_COUNT_REQUEST:
                    data = parseService.mapReviewCount();
                    break;
                case HTTPAction.ADD_REVIEW_BY_PARAMS:
                    Review review = (Review) params;
                    data = parseService.addReviewByParams(review, null);
                    break;
            }
            return new RestResponse(data, mAction); // We send an empty response back. The LoaderCallbacks<RESTResponse>

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String verbToString(int mVerb) {
        if(mVerb == HTTPVerb.GET) {
            return "GET";
        } else if (mVerb == HTTPVerb.POST) {
            return "POST";
        } else if (mVerb == HTTPVerb.PUT) {
            return "PUT";
        } else if (mVerb == HTTPVerb.DELETE) {
            return "DELETE";
        }
        return "EMPTY VERB";
    }

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("X-Parse-Application-Id", "61rFqlbDy0UWBfY56RcLdiJVB1EPe8ce1yUxdAEY");
            request.addHeader("X-Parse-REST-API-Key", "J37VkDdADU7jPfZSwLluAEixwJ3BmjPQJeuR1EzJ");
            request.addHeader("Content-Type", "application/json");
        }
    };


    public class HTTPVerb {
        public static final int GET = 0;
        public static final int DELETE = 1;
        public static final int POST = 2;
        public static final int PUT = 3;
    }

    public class HTTPAction {
        public static final String CLASSES = "classes/";
        public static final String FUNCTIONS = "functions/";

        public static final String COFFEE_MACHINE = "coffee_machines";
        public static final String REVIEW = "review";

        public static final String ADD_REVIEW_REQUEST = "ADD_REVIEW_REQ";
        public static final String REVIEW_REQUEST = "REVIEW_REQ";
        public static final String MORE_REVIEW_REQUEST = "MORE_REVIEW_REQ";
        public static final String USER_REQUEST = "USER_REQ";
        public static final String REVIEW_COUNT_REQUEST = "REVIEW_COUNT";
        public static final String ADD_REVIEW_BY_PARAMS = "ADD_REVIEW_BY_PARAMS";
        public static final String COFFEE_MACHINE_REQUEST = "COFFEE_MACHINE_REQUEST";

    }

}
