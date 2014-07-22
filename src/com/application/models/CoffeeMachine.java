package com.application.models;

import java.util.ArrayList;

public class CoffeeMachine {
    private String id;
    private String iconPath;
	private String name;
	private String address;
//    public ArrayList<Review> reviewsList;
    private String coffeeMachineReviewListId;

	public CoffeeMachine(String id, final String name, String address , String iconPath,
                         String coffeeMachineReviewListId){

        //this.id = getUniqueID();
        this.iconPath = iconPath;
        this.id = id;
		this.name = name;
		this.address = address;
//        this.reviewsList = reviewsList;
        this.coffeeMachineReviewListId = coffeeMachineReviewListId;
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

/*    public void addReviewObj(Review reviewObj) {
        if(reviewsList == null) {
            reviewsList = new ArrayList<Review>();
        }
        reviewsList.add(reviewObj);
    }
*/

//    public void setReviewList(ArrayList<Review> list) {
    public void setReviewListId(String id) {
//        this.reviewsList = list;
        this.coffeeMachineReviewListId = id;
    }

    public String getReviewListId(){
        return this.coffeeMachineReviewListId ;
    }
//    private String getUniqueID(){
//        java.util.Date date= new java.util.Date();
//        long timestamp = date.getTime();
//        return "cfID_" + timestamp;
//    }
}
