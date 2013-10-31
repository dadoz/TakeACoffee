package com.application.takeacoffee;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: davide
 * Date: 10/31/13
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class CoffeMachineDataStorage {
    private ArrayList<CoffeMachine> coffeMachineList;

    public CoffeMachineDataStorage() {
        //get data from JSON
        coffeMachineList = RetrieveDataFromServer.getCoffeMachineData();
    }

    public ArrayList<CoffeMachine> getCoffeMachineList(){
        return coffeMachineList;
    }

    public ArrayList<Review> getReviewListByCoffeMachineId(String coffeMachineId){
        for(CoffeMachine coffeMachineObj : coffeMachineList) {
            if(coffeMachineObj.getId().equals(coffeMachineId)) {
                return coffeMachineObj.getReviewList();
            }
        }
        return null;
    }
}
