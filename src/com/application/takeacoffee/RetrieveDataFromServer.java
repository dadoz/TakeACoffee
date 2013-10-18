package com.application.takeacoffee;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RetrieveDataFromServer {
	private final static String TAG ="retrieveDataFromServer";
	
	static CoffeMachine[] getCoffeMachineData(){
		String JSONData = getData();	
		CoffeMachine[] dataArray = parseData(JSONData);
		return dataArray;
	}
	
	private static String getData(){
		String JSONData = "{'coffe_machine_data' : [{'coffe_machine_name':'Fin Machine','coffe_machine_address':'Main Street - London'},{'coffe_machine_name':'Hey Machine','coffe_machine_address':'Even village - Mexico'}]}";
		
		return JSONData;
	}
	
	private static CoffeMachine[] parseData(String data) {
	    try {

	    	JSONObject jsonObj = (JSONObject) new JSONObject(data);   	
	    	JSONArray jsonArray = jsonObj.getJSONArray("coffe_machine_data");

	    	CoffeMachine[] dataArray = new CoffeMachine[jsonArray.length()];
	    	
	    	for(int i=0; i<jsonArray.length(); i++) {
	    		JSONObject coffeMachineObj = jsonArray.getJSONObject(i);
	    		String name = coffeMachineObj.getString("coffe_machine_name");
	    		String address = coffeMachineObj.getString("coffe_machine_address");
	    		dataArray[i] = new CoffeMachine(name, address);
	    	}
	        return dataArray;
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }
		return null;
	}
}
