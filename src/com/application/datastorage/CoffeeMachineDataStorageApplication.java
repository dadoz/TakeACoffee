package com.application.datastorage;

import android.app.Application;
import android.support.v4.util.ArrayMap;
import com.application.commons.Common;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: davide
 * Date: 10/31/13
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class CoffeeMachineDataStorageApplication extends Application {
    private static final String TAG = "CoffeeMachineDataStorageApplication";
    private ArrayList<CoffeeMachine> coffeeMachineList;
    private ArrayMap<String, ArrayList<Review>> reviewMap;
    private boolean registeredUserStatus = false;
    private User registeredUser;

    public CoffeeMachineDataStorageApplication() {
        reviewMap = new ArrayMap<String, ArrayList<Review>>();
        coffeeMachineList = DataRequestServices.getCoffeeMachineList();
    }

    public ArrayList<CoffeeMachine> getCoffeeMachineList() {
        return coffeeMachineList;
    }

    public CoffeeMachine getCoffeeMachineById(String id) {
        if(coffeeMachineList != null) {
            for(CoffeeMachine obj: coffeeMachineList) {
                if((obj.getId()).equals(id)) {
                    return obj;
                }
            }
        }
        return null;
    }

    public ArrayList<Review> getReviewListById(String reviewId) {
        if(reviewMap.get(reviewId) == null) {
            ArrayList<Review> list = DataRequestServices.getReviewListById(reviewId);
            reviewMap.put(reviewId, list);
            return list;
        }
        return reviewMap.get(reviewId);
    }

    public String getReviewListIdByCoffeeMachine(String coffeeMachineId) {
        for(CoffeeMachine obj : coffeeMachineList) {
            if(obj.getId().equals(coffeeMachineId)) {
                return obj.getReviewListId();
            }
        }
        return null;
    }

    public boolean addReviewByParams(String reviewListId, String userId,
                                     String username, String reviewText,
                                     String profilePicturePath, Common.ReviewStatusEnum status) {

        //String reviewListId = getReviewListIdByCoffeeMachine(coffeeMachineId);
        ArrayList<Review> reviewList = reviewMap.get(reviewListId);
        String id = reviewListId + username + new Date().getTime();
        reviewList.add(new Review(
                id, userId, username, reviewText, status,
                profilePicturePath, new Date()));
        return true;
    }

    public boolean removeReviewById(String reviewListId, Review reviewObj) {
        reviewMap.get(reviewListId).remove(reviewObj);
        return true;
    }


    /*********LOGGED USER ************/

    public void initRegisteredUserByUsername(String username) {
        String id="userId";
        this.registeredUser = new User(id, username, null);
        registeredUserStatus = true;

    }

    public void initRegisteredUserByProfilePicturePath(String profilePicturePath) {
        String id="userId";
        this.registeredUser = new User(id, null, profilePicturePath);
        registeredUserStatus = true;

    }

    public boolean getRegisteredUserStatus(){
        return this.registeredUserStatus;
    }

    public User getRegisteredUser(){
        return this.registeredUser;
    }

    //TODO refactor it plez getReviewListById
/*    public ArrayList<Review> getReviewListByCoffeMachineId(String coffeeMachineId){
        for(CoffeeMachine coffeeMachineObj : coffeeMachineList) {
            if(coffeeMachineObj.getId().equals(coffeeMachineId)) {
                return coffeeMachineObj.getReviewList();
            }
        }
        return null;
    }
    //TODO refactor it plez addReview
    public Review addReviewByCoffeeMachineId(String coffeeMachineId, String userId,
                                             String username, String reviewText,
                                             String profilePicturePath, Common.ReviewStatusEnum status) {
        Review review = null;
        for(CoffeeMachine coffeeMachineObj : coffeeMachineList) {
            if(coffeeMachineObj.getId().equals(coffeeMachineId)) {
//                String id = "fakeId"; //TODO generate id
                String id = new String(coffeeMachineId + username + new Date().getTime());
                Date date = new Date();
                review = new Review(id, userId, username, reviewText, status, profilePicturePath, date);
                coffeeMachineObj.addReviewObj(review);
            }
        }
        return review;
    }

    //TODO refactor it plez deleteReviewById
    public boolean removeReviewByCoffeeMachineId(String coffeeMachineId, Review reviewObj) {
        for(CoffeeMachine coffeeMachineObj : coffeeMachineList) {
            if(coffeeMachineObj.getId().equals(coffeeMachineId)) {
                coffeeMachineObj.getReviewList().remove(reviewObj);
            }
        }
        return false;
    }
*/

}
