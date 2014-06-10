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
    private String profilePicPath;
    private String userId;

	public Review(String id, String userId, final String username, String comment, ReviewStatusEnum status, String profilePicPath, Date date) {

        //this.id = getUniqueID();
        this.id = id;
        this.userId = userId;
		this.username = username;
        this.status = status;
        this.comment = comment;
        this.date = date;
        this.profilePicPath = profilePicPath;
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

    public String getUserId(){
        return this.userId;
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

    public String getProfilePicPath() {
        return this.profilePicPath;
    }

    public long getTimestamp() {
        return date.getTime();
    }
}
