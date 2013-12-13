package com.application.models;

import java.util.ArrayList;

public class CoffeMachine {
    private String id;
	private String name;
	private String address;
    public ArrayList<Review> reviewsList;

	public CoffeMachine(String id, final String name, String address , ArrayList<Review> reviewsList){

        //this.id = getUniqueID();
        this.id = id;
		this.name = name;
		this.address = address;
        this.reviewsList = reviewsList;
	}

	public String getName() {
		return name;
	}
	
	public String getAddress(){
		return address;
	}

    public String getId(){
        return this.id;
    }

    public void addReviewObj(Review reviewObj) {
        if(reviewsList == null) {
            reviewsList = new ArrayList<Review>();
        }
        reviewsList.add(reviewObj);
    }


    public void setReviewList(ArrayList<Review> list) {
        this.reviewsList = list;
    }

    public ArrayList<Review> getReviewList(){
        return this.reviewsList;
    }
//    private String getUniqueID(){
//        java.util.Date date= new java.util.Date();
//        long timestamp = date.getTime();
//        return "cfID_" + timestamp;
//    }
}
