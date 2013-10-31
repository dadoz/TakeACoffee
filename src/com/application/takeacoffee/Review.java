package com.application.takeacoffee;

import java.util.ArrayList;

public class Review {
    private String id;
	private String username;
    private boolean feedback;
    private String comment;

	public Review(String id, final String username, String comment, boolean feedback){

        //this.id = getUniqueID();
        this.id = id;
		this.username = username;
        this.feedback = feedback;
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

    public void setFeedback(boolean value) {
        this.feedback = value;
    }

    public boolean getFeedback() {
        return this.feedback;
    }

}
