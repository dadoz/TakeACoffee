package com.application.commons;

import com.application.models.Review;

import java.util.ArrayList;

/**
 * Created by davide on 05/09/14.
 */
public class ReviewListTimestamp {
    private final long fromTimestamp;
    private final long toTimestamp;
    private final ArrayList<Review> reviewsList;

    public ReviewListTimestamp(long fromTimestamp, long toTimestamp, ArrayList<Review> reviewsList) {
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.reviewsList = reviewsList;
    }

    public long getFromTimestamp() {
        return fromTimestamp;
    }

    public long getToTimestamp() {
        return toTimestamp;
    }

    public ArrayList<Review> getReviewsList() {
        return reviewsList;
    }
}
