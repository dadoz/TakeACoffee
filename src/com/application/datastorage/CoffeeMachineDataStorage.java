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
    private String currentCoffeMachineSelectedId;

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

    public void addReviewByCoffeeMachineId(String coffeeMachineId, String userId, String username, String reviewText, String profilePicturePath, Common.ReviewStatusEnum status) {
        for(CoffeeMachine coffeeMachineObj : coffeeMachineList) {
            if(coffeeMachineObj.getId().equals(coffeeMachineId)) {
                String id = "fakeId"; //TODO generate id
                Date date = new Date();
                coffeeMachineObj.addReviewObj(new Review(id, userId, username, reviewText, status, profilePicturePath, date));
            }
        }
    }

    public void initRegisteredUser(String username){
    	String id="fakeId";
    	this.registeredUser = new User(id, username);
        registeredUserStatus = true;

    }
    public boolean removeReviewByCoffeeMachineId(String coffeeMachineId, Review reviewObj) {
        for(CoffeeMachine coffeeMachineObj : coffeeMachineList) {
            if(coffeeMachineObj.getId().equals(coffeeMachineId)) {
                String id = "fakeId"; //TODO generate id
                Date date = new Date();
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

    public void setRegisteredUser(String username){
    	this.registeredUser.setUsername(username);
    }
    
    public void setCurrentCoffeMachineSelectedId(String id){
    	currentCoffeMachineSelectedId = id;
    }

    public String getCurrentCoffeMachineSelectedId(){
    	return this.currentCoffeMachineSelectedId;
    }

    public void setProfilePicturePath(String path) {
        profilePicturePath = path;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

}
