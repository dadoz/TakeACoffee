package com.application.takeacoffee;

public class CoffeMachine {
	public String name;
	public String address;
	public CoffeMachine(final String name, String address){
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}
	
	public String getAddress(){
		return address;
	}
	
}
