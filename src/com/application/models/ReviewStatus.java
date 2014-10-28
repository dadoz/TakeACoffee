package com.application.models;

/**
 * Created by davide on 29/05/14.
 */
public class ReviewStatus {
    private int position;
    private String name;
    private int iconId;


    public ReviewStatus(int position,String name, int iconId) {
        this.iconId = iconId;
        this.name = name;
        this.position = position;
    }

    public int getIconId() {
        return iconId;
    }

    public String getName() {
        return name;
    }


    public int getPosition() {
        return position;
    }
}
