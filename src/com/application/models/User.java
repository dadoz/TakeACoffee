package com.application.models;

import java.util.ArrayList;

public class User {
	private String id;
	private String username;
	private ArrayList<Review> reviewsList;
    private String profilePicturePath;
/*
	public User(String id, String username, ArrayList<Review> reviewsList){
		this.id = id;
		this.username = username;
		this.reviewsList = reviewsList;
	}
*/
	public User(String id, String username, String profilePicturePath) {
		this.id = id;
		this.username = username;
        this.profilePicturePath = profilePicturePath;
	}
	
	public String getId(){
		return this.id;
	}

	public String getUsername(){
		return this.username;
	}

	public ArrayList<Review> getReviewList(){
		return this.reviewsList;
	}

	public void setReviewList(ArrayList<Review> reviewsList){
		this.reviewsList = reviewsList;
	}
	public void setUsername(String username){
		this.username = username;
	}

    public String getProfilePicturePath() {
        return this.profilePicturePath;
    }
    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

}
