package com.application.models;

import com.application.commons.Common.ReviewStatusEnum;



public class Review {
	
    private String id;
	private String username;
//    private boolean feedback;
	private ReviewStatusEnum status;
    private String comment;

	public Review(String id, final String username, String comment, ReviewStatusEnum notBad){

        //this.id = getUniqueID();
        this.id = id;
		this.username = username;
        this.status = notBad;
        this.comment = comment;
    }

	public String getUsername() {
		return username;
	}
	
    public String getId(){
        return this.id;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }
    public void setFeedback(ReviewStatusEnum value) {
        this.status = value;
    }

    public ReviewStatusEnum getStatus() {
        return this.status;
    }

}
