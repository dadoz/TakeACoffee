package com.application.dataRequest;

import com.application.models.CoffeeMachine;
import com.application.models.User;
import retrofit.Callback;
import retrofit.http.*;

import java.util.List;

/**
 * Created by davide on 17/10/14.
 */
public interface ParseServiceInterface {

    //get coffee machine
    @GET("/" + RestLoaderRetrofit.HTTPAction.CLASSES +
            RestLoaderRetrofit.HTTPAction.COFFEE_MACHINE_REQUEST)
    List<CoffeeMachine> listCoffeeMachine();

    @POST("/users/{user}/repos")
    Object listMoreReview(@Body User user, Callback<User> cb);

    @POST("/users/{user}/repos")
    Object listReview();

    @POST("/users/{user}/repos")
    Object mapReviewCount();

    //volley http call

    //add user

    //remove user
//    getUserById
}
