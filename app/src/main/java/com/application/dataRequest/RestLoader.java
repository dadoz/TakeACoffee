package com.application.dataRequest;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


import androidx.fragment.app.FragmentActivity;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by davide on 15/09/14.
 */
public class RestLoader extends AsyncTaskLoader<RestResponse> {
    private static final String TAG = "RESTLoader";
    private final Uri mAction;
    private final Bundle mParams;
    private final int mVerb;
    private final String mRequestType;

    public RestLoader(FragmentActivity activity, int verb, Uri action, Bundle params, String requestType) {
        super(activity);
        mAction = action;
        mVerb = verb;
        mParams = params;
        mRequestType = requestType;
    }


//    @Override
    public RestResponse loadInBackground() {
//        try {
//            // At the very least we always need an action.
//            if (mAction == null) {
//                Log.e(TAG, "You did not define an action. REST call canceled.");
//                return new RestResponse(); // We send an empty response back. The LoaderCallbacks<RestResponse>
//                // implementation will always need to check the RestResponse
//                // and handle error cases like this.
//            }
//
//            // Here we define our base request object which we will
//            // send to our REST service via HttpClient.
//            HttpRequestBase request = null;
//
//            // Let's build our request based on the HTTP verb we were
//            // given.
//            switch (mVerb) {
//                case HTTPVerb.GET: {
//                    request = new HttpGet();
//                    attachUriWithQuery(request, mAction, mParams);
//                    setHeader(request);
//                }
//                break;
//
//                case HTTPVerb.DELETE: {
//                    request = new HttpDelete();
//                    attachUriWithQuery(request, mAction, mParams);
//                }
//                break;
//
//                case HTTPVerb.POST: {
//                    request = new HttpPost();
//                    request.setURI(new URI(mAction.toString()));
//
//                    // Attach form entity if necessary. Note: some REST APIs
//                    // require you to POST JSON. This is easy to do, simply use
//                    // postRequest.setHeader('Content-Type', 'application/json')
//                    // and StringEntity instead. Same thing for the PUT case
//                    // below.
//                    setHeader(request);
//                    HttpPost postRequest = (HttpPost) request;
//
//                    if (mParams != null) {
//                        StringEntity JSONStringEntitiy = new StringEntity(paramsToJSONString(mParams));
//                        postRequest.setEntity(JSONStringEntitiy);
//                    }
//                }
//                break;
//
//                case HTTPVerb.PUT: {
//                    request = new HttpPut();
//                    request.setURI(new URI(mAction.toString()));
//
//                    // Attach form entity if necessary.
//                    HttpPut putRequest = (HttpPut) request;
//
//                    if (mParams != null) {
//                        //TODO need to be implemented
////                        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(mParams));
////                        putRequest.setEntity(formEntity);
//                    }
//                }
//                break;
//            }
//
//            if (request != null) {
//                HttpClient client = new DefaultHttpClient();
//
//                // Let's send some useful debug information so we can monitor things
//                // in LogCat.
//                Log.d(TAG, "Executing request: "+ verbToString(mVerb) +": "+ mAction.toString());
//
//                // Finally, we send our request using HTTP. This is the synchronous
//                // long operation that we need to run on this Loader's thread.
//                HttpResponse response = client.execute(request);
//
//                HttpEntity responseEntity = response.getEntity();
//                StatusLine responseStatus = response.getStatusLine();
//                int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
//
//                // Here we create our response and send it back to the LoaderCallbacks<RestResponse> implementation.
//                RestResponse RestResponse = new RestResponse(responseEntity != null ? EntityUtils.toString(responseEntity) : null, statusCode, mRequestType);
//                return RestResponse;
//            }
//
//            // Request was null if we get here, so let's just send our empty RestResponse like usual.
//            return new RestResponse();
//        } catch (URISyntaxException e) {
//            Log.e(TAG, "URI syntax was incorrect. "+ verbToString(mVerb) +": "+ mAction.toString(), e);
//            return new RestResponse();
//        } catch (UnsupportedEncodingException e) {
//            Log.e(TAG, "A UrlEncodedFormEntity was created with an unsupported encoding.", e);
//            return new RestResponse();
//        } catch (ClientProtocolException e) {
//            Log.e(TAG, "There was a problem when sending the request.", e);
//            return new RestResponse();
//        } catch (IOException e) {
//            Log.e(TAG, "There was a problem when sending the request.", e);
//            return new RestResponse();
//        } catch (Exception e) {
//            Log.e(TAG, "There was a problem when sending the request.", e);
//            return new RestResponse();
//        }
        return null;
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
//    private void attachUriWithQuery(HttpRequestBase request, Uri mAction, Bundle mParams) {
//        //TODO need to be implemented
//        request.setURI(URI.create(mAction.toString()));
//    }

//    private void setHeader(HttpRequestBase request) {
//        request.addHeader("X-Parse-Application-Id", "61rFqlbDy0UWBfY56RcLdiJVB1EPe8ce1yUxdAEY");
//        request.addHeader("X-Parse-REST-API-Key", "J37VkDdADU7jPfZSwLluAEixwJ3BmjPQJeuR1EzJ");
//        request.addHeader("Content-Type", "application/json");
//    }

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


    public class HTTPVerb {
        public static final int GET = 0;
        public static final int DELETE = 1;
        public static final int POST = 2;
        public static final int PUT = 3;
    }
}
