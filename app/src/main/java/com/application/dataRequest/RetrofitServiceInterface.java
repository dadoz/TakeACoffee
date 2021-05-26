package com.application.dataRequest;

import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.User;

import java.util.List;

import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by davide on 17/10/14.
 */
public interface RetrofitServiceInterface {

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

    @POST("/" + RestLoaderRetrofit.HTTPAction.CLASSES + "reviews")
    boolean addReviewByParams(@Body Review review, Callback<Review> callback);
    //volley http call

    //add user

    //remove user
//    getUserById
}
