package com.application.dataRequest;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import com.application.models.Review;
import com.application.models.ReviewCounter;
import com.application.models.User;
import com.google.gson.JsonParseException;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by davide on 15/09/14.
 */
public class RESTLoader extends AsyncTaskLoader<RESTLoader.RESTResponse> {
    private static final String TAG = "RESTLoader";
    private final Uri mAction;
    private final Bundle mParams;
    private final int mVerb;
    private final String mRequestType;

    public RESTLoader(FragmentActivity activity, int verb, Uri action, Bundle params, String requestType) {
        super(activity);
        mAction = action;
        mVerb = verb;
        mParams = params;
        mRequestType = requestType;
    }

    @Override
    public RESTLoader.RESTResponse loadInBackground() {
        try {
            // At the very least we always need an action.
            if (mAction == null) {
                Log.e(TAG, "You did not define an action. REST call canceled.");
                return new RESTResponse(); // We send an empty response back. The LoaderCallbacks<RESTResponse>
                // implementation will always need to check the RESTResponse
                // and handle error cases like this.
            }

            // Here we define our base request object which we will
            // send to our REST service via HttpClient.
            HttpRequestBase request = null;

            // Let's build our request based on the HTTP verb we were
            // given.
            switch (mVerb) {
                case HTTPVerb.GET: {
                    request = new HttpGet();
                    attachUriWithQuery(request, mAction, mParams);
                    setHeader(request);
                }
                break;

                case HTTPVerb.DELETE: {
                    request = new HttpDelete();
                    attachUriWithQuery(request, mAction, mParams);
                }
                break;

                case HTTPVerb.POST: {
                    request = new HttpPost();
                    request.setURI(new URI(mAction.toString()));

                    // Attach form entity if necessary. Note: some REST APIs
                    // require you to POST JSON. This is easy to do, simply use
                    // postRequest.setHeader('Content-Type', 'application/json')
                    // and StringEntity instead. Same thing for the PUT case
                    // below.
                    setHeader(request);
                    HttpPost postRequest = (HttpPost) request;

                    if (mParams != null) {
                        StringEntity JSONStringEntitiy = new StringEntity(paramsToJSONString(mParams));
                        postRequest.setEntity(JSONStringEntitiy);
                    }
                }
                break;

                case HTTPVerb.PUT: {
                    request = new HttpPut();
                    request.setURI(new URI(mAction.toString()));

                    // Attach form entity if necessary.
                    HttpPut putRequest = (HttpPut) request;

                    if (mParams != null) {
                        //TODO need to be implemented
//                        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(mParams));
//                        putRequest.setEntity(formEntity);
                    }
                }
                break;
            }

            if (request != null) {
                HttpClient client = new DefaultHttpClient();

                // Let's send some useful debug information so we can monitor things
                // in LogCat.
                Log.d(TAG, "Executing request: "+ verbToString(mVerb) +": "+ mAction.toString());

                // Finally, we send our request using HTTP. This is the synchronous
                // long operation that we need to run on this Loader's thread.
                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();
                int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;

                // Here we create our response and send it back to the LoaderCallbacks<RESTResponse> implementation.
                RESTResponse restResponse = new RESTResponse(responseEntity != null ? EntityUtils.toString(responseEntity) : null, statusCode, mRequestType);
                return restResponse;
            }

            // Request was null if we get here, so let's just send our empty RESTResponse like usual.
            return new RESTResponse();
        } catch (URISyntaxException e) {
            Log.e(TAG, "URI syntax was incorrect. "+ verbToString(mVerb) +": "+ mAction.toString(), e);
            return new RESTResponse();
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "A UrlEncodedFormEntity was created with an unsupported encoding.", e);
            return new RESTResponse();
        } catch (ClientProtocolException e) {
            Log.e(TAG, "There was a problem when sending the request.", e);
            return new RESTResponse();
        } catch (IOException e) {
            Log.e(TAG, "There was a problem when sending the request.", e);
            return new RESTResponse();
        } catch (Exception e) {
            Log.e(TAG, "There was a problem when sending the request.", e);
            return new RESTResponse();
        }
    }

    private String paramsToJSONString(Bundle params) {
//        return "{\"coffeeMachineId\" : \"PZrB82ZWVl\", \"fromReviewId\": \"qaeWjprTDF\"}";
        return params.getString("params");
    }

/*    private List<? extends NameValuePair> paramsToList(Bundle params) {
//        {"coffeeMachineId" : "PZrB82ZWVl", "fromReviewId": "qaeWjprTDF"}

        List<NameValuePair> paramsList = new ArrayList<>();
        BasicNameValuePair pair = new BasicNameValuePair("coffeeMachineId", "PZrB82ZWVl");
        paramsList.add(pair);
        pair = new BasicNameValuePair("fromReviewId", "qaeWjprTDF");
        paramsList.add(pair);
        //TODO need to be implemented
        return paramsList;

    }*/
    private void attachUriWithQuery(HttpRequestBase request, Uri mAction, Bundle mParams) {
        //TODO need to be implemented
        request.setURI(URI.create(mAction.toString()));
    }

    private void setHeader(HttpRequestBase request) {
        request.addHeader("X-Parse-Application-Id", "61rFqlbDy0UWBfY56RcLdiJVB1EPe8ce1yUxdAEY");
        request.addHeader("X-Parse-REST-API-Key", "J37VkDdADU7jPfZSwLluAEixwJ3BmjPQJeuR1EzJ");
        request.addHeader("Content-Type", "application/json");
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


    public class RESTResponse {

        private String requestType;
        private int key;
        private String data;
        private boolean userResponse;
        private boolean reviewResponse;
        private Object userListParser;

        public RESTResponse() {
            //TODO need to be implemented
        }

        public RESTResponse(String data, int key, String requestType) {
            //TODO need to be implemented
            this.data = data;
            this.key = key;
            this.requestType = requestType;
        }

        public String getRequestType() {
            return requestType;
        }

        public int getCode() {
            return key;
        }

        public String getData() {
            return data;
        }

        public int getHttpResponseCode() {
            JSONObject object = null;
            Log.e(TAG, "data result" + data);
            assert data != null;
            try {
                object = new JSONObject(data);
                return object.getJSONObject("result").getInt("code");
            } catch (JSONException e) {
                assert object != null;
                try {
                    if(object.getJSONObject("result").keys().hasNext()) {
                        return 200;
                    }
                } catch (JSONException e1) {
                    try {
                        return object.getInt("code");
                    } catch (JSONException e2) {
//                        e2.printStackTrace();
                    }
//                    e1.printStackTrace();
                }
            }
            return -1; //NOT AVAILABLE
        }

        /***** DATA PARSER ****/
        public ReviewCounter parseCountOnReviewsData(String data) {
//            {"result":{"PZrB82ZWVl":{"GOOD":13,"NOTSOBAD":3,"WORST":1}}

            ReviewCounter reviewCounter = null;
            try {
                JSONObject objectFirst = new JSONObject(data).getJSONObject("result");
                Iterator keysIterator = objectFirst.keys();

                while(keysIterator.hasNext()) {
                    String key = keysIterator.next().toString();
                    JSONObject objectTwo = objectFirst.getJSONObject(key);

                    Iterator keysIteratorTwo = objectTwo.keys();

                    while(keysIteratorTwo.hasNext()) {
                        String keyTimestamp = keysIteratorTwo.next().toString();
                        JSONObject objectThree = objectTwo.getJSONObject(keyTimestamp);
                        //TODO to be replaced
                        reviewCounter = new ReviewCounter(key,
                                Long.parseLong(keyTimestamp),
                                objectThree.getInt("GOOD"),
                                objectThree.getInt("NOTSOBAD"),
                                objectThree.getInt("WORST"));
                    }
                }
                return reviewCounter;
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private Review reviewParser(String data) {
            JSONObject reviewJsonObj = null;
            try {
                reviewJsonObj = new JSONObject(data);
                String reviewId = reviewJsonObj.getString("objectId");
                String reviewUserId = reviewJsonObj
                        .getString("user_id_string");
                String reviewCoffeeMachineId = reviewJsonObj
                        .getString("coffee_machine_id_string");
                String reviewComment = reviewJsonObj
                        .getString("comment");
                long timestamp = reviewJsonObj
                        .getLong("timestamp");
                String reviewStatus = reviewJsonObj
                        .getString("status");

                return new Review(reviewId, reviewComment, Review.parseStatus(reviewStatus), timestamp, reviewUserId, reviewCoffeeMachineId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public boolean getHasMoreReviews() {
            try {
                JSONObject dataObject = new JSONObject(this.data);
                return (dataObject.getJSONObject("result")).getBoolean("hasMoreReviews");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public ArrayList<Review> getReviewListParser() {
            try {
                JSONObject dataObject = new JSONObject(this.data);
//                boolean hasMoreReviews = (dataObject.getJSONObject("result")).getBoolean("hasMoreReviews");
                JSONArray reviewJsonArray = (dataObject.getJSONObject("result")).getJSONArray("data");
                ArrayList<Review> reviewsList = new ArrayList<Review>();
                for (int j = 0; j < reviewJsonArray.length(); j ++) {
                    JSONObject reviewJsonObj = reviewJsonArray.getJSONObject(j);
                    reviewsList.add(reviewParser(reviewJsonObj.toString()));
                }

                return reviewsList;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public boolean isUserResponse() {
            return requestType != null && requestType.compareTo("USER_REQ") == 0;
        }

        public boolean isReviewResponse() {
            return requestType != null && requestType.compareTo("REVIEW_REQ") == 0;
        }

        public boolean isMoreReviewResponse() {
            return requestType != null && requestType.compareTo("MORE_REVIEW_REQ") == 0;
        }

        public Object getUserListParser() {
            ArrayList<User> userList = new ArrayList<User>();
            try {
                JSONArray jsonArray = new JSONObject(this.data)
                        .getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i ++) {
                    User user = userParser(jsonArray.get(i).toString());
                    if(user != null) {
                        userList.add(user);
                    }
                }
                return userList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private User userParser(String data) {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(data);
                String userId = jsonObj.getString("objectId");
                String username = jsonObj
                        .getString("username");
                String profilePicturePath = jsonObj
                        .getString("profile_picture_path");

                return new User(userId, profilePicturePath, username);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


    public class HTTPVerb {
        public static final int GET = 0;
        public static final int DELETE = 1;
        public static final int POST = 2;
        public static final int PUT = 3;
    }
}
