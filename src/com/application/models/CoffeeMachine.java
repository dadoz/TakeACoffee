package com.application.models;

import java.util.ArrayList;

public class CoffeeMachine {
    private long id;
    private String iconPath;
	private String name;
	private String address;

	public CoffeeMachine(long id, final String name, String address , String iconPath) {
        this.id = id;
        this.iconPath = iconPath;
		this.name = name;
		this.address = address;
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

    public long getId(){
        return this.id;
    }

}
