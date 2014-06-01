package com.application.models;

/**
 * Created by davide on 29/05/14.
 */
public class Setting {
    private String id;
    private String name;
    private int iconResourceId;

    public Setting(String id, int iconResourceId, String name) {
        this.id = id;
        this.iconResourceId = iconResourceId;
        this.name = name;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getName() {
        return name;
    }
}
