package com.application.takeacoffee;

import java.util.ArrayList;

public class User {
	private String id;
	private String username;
	private ArrayList<Review> reviewsList;
	
	public User(String id, String username, ArrayList<Review> reviewsList){
		this.id = id;
		this.username = username;
		this.reviewsList = reviewsList;
	}

	public User(String id, String username){
		this.id = id;
		this.username = username;		
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

}
