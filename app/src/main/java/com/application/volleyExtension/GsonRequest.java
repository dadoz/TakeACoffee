package com.application.volleyExtension;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by davide on 06/09/14.
 */
public class GsonRequest {
//    private Gson mGson;
//
//    public GsonRequest(int method, String url, Class<T> cls, String requestBody, Response.Listener<T> listener,
//                       Response.ErrorListener errorListener) {
//        super(method, url, errorListener);
//        mGson = new Gson();
//    }
//
//    @Override
//    protected Response<T> parseNetworkResponse(NetworkResponse response) {
//        try {
//            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//            Class<? extends T> mJavaClass = null;
//            T parsedGSON = mGson.fromJson(jsonString, mJavaClass);
//            return Response.success(parsedGSON,
//                    HttpHeaderParser.parseCacheHeaders(response));
//
//        } catch (UnsupportedEncodingException e) {
//            return Response.error(new ParseError(e));
//        } catch (JsonSyntaxException je) {
//            return Response.error(new ParseError(je));
//        }
//    }
//
//    @Override
//    protected void deliverResponse(T t) {
//
//    }
}