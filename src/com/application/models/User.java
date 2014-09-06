package com.application.models;

import com.application.commons.Common;

import java.util.ArrayList;

public class User {
	private long id;
	private String username;
//	private String reviewsListId;
    private String profilePicturePath;
/*
	public User(String id, String username, ArrayList<Review> reviewsList){
		this.id = id;
		this.username = username;
		this.reviewsList = reviewsList;
	}
*/
	public User(long id, String profilePicturePath, String username) {
		this.id = id;
		this.username = username;
        this.profilePicturePath = profilePicturePath;
	}
	
	public long getId(){
		return this.id;
	}

	public String getUsername(){
		return this.username;
	}

/*	public String getReviewList(){
		return this.reviewsList;
	}

	public void setReviewList(String reviewsListId){
		this.reviewsListId = reviewsListId;
	}
	public void setUsername(String username){
		this.username = username;
	}
*/
    public String getProfilePicturePath() {
        return this.profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        try {
            if(profilePicturePath != null && profilePicturePath.equals(Common.EMPTY_PIC_PATH)) {
                this.profilePicturePath = null;
                return;
            }
            this.profilePicturePath = profilePicturePath;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
