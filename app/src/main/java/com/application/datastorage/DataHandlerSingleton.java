package com.application.datastorage;

import android.content.Context;

/**
 * Created by davide on 08/10/14.
 */
public class DataHandlerSingleton{
    private final Context context;
    private static DataHandlerSingleton mDataHandlerSingleton;


    private DataHandlerSingleton(Context ctx) {
        this.context = ctx;

    }

    public static synchronized DataHandlerSingleton getInstance(Context ctx) {
        if(mDataHandlerSingleton == null) {
            mDataHandlerSingleton = new DataHandlerSingleton(ctx);
        }
        return mDataHandlerSingleton;
    }

}
