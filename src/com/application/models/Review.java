package com.application.models;

import android.util.Log;
import com.application.commons.Common.ReviewStatusEnum;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;


public class Review {

    private static final String TAG = "Review";
    private long id;
//    private boolean feedback;
	private ReviewStatusEnum status;
    private String comment;
    private long timestamp;
    private long userId;
    private String coffeeMachineId;

	public Review(long id, String comment, ReviewStatusEnum status,
                  long timestamp, long userId, String coffeeMachineId) {

        this.id = id;
        this.userId = userId;
        this.status = status;
        this.comment = comment;
        this.timestamp = timestamp;
        this.coffeeMachineId = coffeeMachineId;
    }

	public String getCoffeeMachineId() {
		return coffeeMachineId;
	}

    public void setCoffeeMachineId(String coffeeMachineId) {
        this.coffeeMachineId = coffeeMachineId;
    }

    public long getId(){
        return this.id;
    }

    public String getComment() {
        return this.comment;
    }

    public long getUserId(){
        return this.userId;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public String getFormattedTimestamp(){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(timestamp));
    }

    public ReviewStatusEnum getStatus() {
        return this.status;
    }


    public long getTimestamp() {
        return this.timestamp;
    }

    public static ReviewStatusEnum parseStatusFromPageNumber(int pageNumber) {
        ReviewStatusEnum status;
        switch (pageNumber) {
            case 0:
                status = ReviewStatusEnum.GOOD;
                break;
            case 1:
                status = ReviewStatusEnum.NOTSOBAD;
                break;
            case 2:
                status = ReviewStatusEnum.WORST;
                break;
            default:
                status = ReviewStatusEnum.NOTSET;
        }
        return status;
    }

    public static ReviewStatusEnum parseStatus(String reviewStatus) {
        if(reviewStatus.equals("GOOD")) {
            return ReviewStatusEnum.GOOD;
        } else if(reviewStatus.equals("NOTSOBAD")) {
            return ReviewStatusEnum.NOTSOBAD;
        } else if(reviewStatus.equals("WORST")) {
            return ReviewStatusEnum.WORST;
        }
        Log.e(TAG, "status not set -");
        return ReviewStatusEnum.NOTSET;
    }
    
    public static Date parseDate(long timestamp) {
        return new Date();
    }

    @Override
    public String toString() {
        return "id: " + this.id +
            "userId: " + this.userId +
            "status: " + this.status +
            "comment: " + this.comment +
            "timestamp: " + this.timestamp +
            "coffeeMachineId: " + this.coffeeMachineId;
    }
}
