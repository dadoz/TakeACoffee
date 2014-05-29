package com.application.models;

import java.util.ArrayList;

public class CoffeeMachine {
    private String id;
    private String iconPath;
	private String name;
	private String address;
    public ArrayList<Review> reviewsList;

	public CoffeeMachine(String id, final String name, String address , String iconPath,ArrayList<Review> reviewsList){

        //this.id = getUniqueID();
        this.iconPath = iconPath;
        this.id = id;
		this.name = name;
		this.address = address;
        this.reviewsList = reviewsList;
	}

	public String getName() {
		return name;
	}

    public String getIconPath() {
        return iconPath;
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
