package com.application.datastorage;

import com.application.models.CoffeMachine;
import com.application.models.Review;
import com.application.models.User;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: davide
 * Date: 10/31/13
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class CoffeeMachineDataStorage {
    private static final String TAG = "CoffeMachineDataStorage";
    private ArrayList<CoffeMachine> coffeMachineList;
    private boolean registeredUserStatus = false;
    private User registeredUser;
    private String currentCoffeMachineSelectedId;

    //PROFILE PIC
    private static String profilePicturePath;

    public CoffeeMachineDataStorage() {
        //get data from JSON
        coffeMachineList = RetrieveDataFromServer.getCoffeMachineData();
    }

    public ArrayList<CoffeMachine> getCoffeMachineList(){
        return coffeMachineList;
    }

    public CoffeMachine getCoffeMachineById(String id){
    	if(coffeMachineList != null){
    		for(CoffeMachine coffeMachineObj: coffeMachineList) {
    			if((coffeMachineObj.getId()).equals(id)){
            		return coffeMachineObj;    				
    			}
    		}
    	}
        return null;
    }

    public ArrayList<Review> getReviewListByCoffeMachineId(String coffeMachineId){
        for(CoffeMachine coffeMachineObj : coffeMachineList) {
            if(coffeMachineObj.getId().equals(coffeMachineId)) {
                return coffeMachineObj.getReviewList();
            }
        }
        return null;
    }

    public void initRegisteredUser(String username){
    	String id="fakeId";
    	this.registeredUser = new User(id, username);
        registeredUserStatus = true;

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
