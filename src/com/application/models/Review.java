package com.application.models;

import com.application.commons.Common.ReviewStatusEnum;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Review {
	
    private String id;
	private String username;
//    private boolean feedback;
	private ReviewStatusEnum status;
    private String comment;
    private Date date;

	public Review(String id, final String username, String comment, ReviewStatusEnum status, Date date){

        //this.id = getUniqueID();
        this.id = id;
		this.username = username;
        this.status = status;
        this.comment = comment;
        this.date = date;
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

    public Date getDate(){
        return this.date;
    }

    public String getFormattedTimestamp(){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(this.date);
    }
    public ReviewStatusEnum getStatus() {
        return this.status;
    }

}
