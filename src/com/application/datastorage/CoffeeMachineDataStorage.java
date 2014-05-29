package com.application.datastorage;

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
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class CoffeeMachineDataStorage {
    private static final String TAG = "CoffeMachineDataStorage";
    private ArrayList<CoffeeMachine> coffeeMachineList;
    private boolean registeredUserStatus = false;
    private User registeredUser;

    //PROFILE PIC
    private static String profilePicturePath;

    public CoffeeMachineDataStorage() {
        //get data from JSON
        coffeeMachineList = RetrieveDataFromServer.getCoffeeMachineData();
    }

    public ArrayList<CoffeeMachine> getCoffeeMachineList(){
        return coffeeMachineList;
    }

    public CoffeeMachine getCoffeMachineById(String id){
    	if(coffeeMachineList != null){
    		for(CoffeeMachine coffeMachineObj: coffeeMachineList) {
    			if((coffeMachineObj.getId()).equals(id)){
            		return coffeMachineObj;    				
    			}
    		}
    	}
        return null;
    }

    public ArrayList<Review> getReviewListByCoffeMachineId(String coffeeMachineId){
        for(CoffeeMachine coffeeMachineObj : coffeeMachineList) {
            if(coffeeMachineObj.getId().equals(coffeeMachineId)) {
                return coffeeMachineObj.getReviewList();
            }
        }
        return null;
    }

    public Review addReviewByCoffeeMachineId(String coffeeMachineId, String userId, String username, String reviewText, String profilePicturePath, Common.ReviewStatusEnum status) {
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

    public boolean removeReviewByCoffeeMachineId(String coffeeMachineId, Review reviewObj) {
        for(CoffeeMachine coffeeMachineObj : coffeeMachineList) {
            if(coffeeMachineObj.getId().equals(coffeeMachineId)) {
                coffeeMachineObj.getReviewList().remove(reviewObj);
            }
        }
        return false;
    }

    public boolean getRegisteredUserStatus(){
    	return this.registeredUserStatus;
    }

    public User getRegisteredUser(){
    	return this.registeredUser;
    }


/*    public void setProfilePicturePath(String path) {
        profilePicturePath = path;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }
*/
}
