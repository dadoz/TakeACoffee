package com.application.datastorage;

import com.application.commons.Common;
import com.application.commons.Common.ReviewStatusEnum;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RetrieveDataFromServer {
	// private final static String TAG ="retrieveDataFromServer";

	static ArrayList<CoffeeMachine> getCoffeeMachineData() {
		String JSONData = getData();

		ArrayList<CoffeeMachine> dataArray = parseData(JSONData);
		return dataArray;
	}

	private static String getData() {
        //TODO add Date field on JSON data
		String JSONData = "{'coffe_machine_data' : [{'coffe_machine_id':'STATIC_coffeeMachineId_1', 'coffe_machine_name':'New Kinder','coffe_machine_address':'Main Street - London', 'coffe_machine_icon_name':'coffee1.jpg', 'coffe_machine_reviews' : [] }, {'coffe_machine_id':'STATIC_coffeeMachineId_2','coffe_machine_name':'Hey Machine','coffe_machine_address':'Even village - Mexico', 'coffe_machine_icon_name':'coffee2.jpg', 'coffe_machine_reviews' : [{'review_id':'STATIC_REVIEWID_0', 'review_username':'Greys Losk', 'review_comment':'This coffe machine is really terrible', 'review_status':'WORST', 'review_format_date': '06.06.2014'}, {'review_id':'STATIC_REVIEWID_1', 'review_username':'Mike pp', 'review_comment':'this is the comment on machine', 'review_status':'NOT_BAD', 'review_format_date': '06.06.2014'}, {'review_id':'STATIC_REVIEWID_2', 'review_username':'Henry d', 'review_comment':'this is the comment on machine cos I want to say thats nothing in front of your problems didnt u agree with me?', 'review_status':'GOOD', 'review_format_date': '06.06.2014'}] }, {'coffe_machine_id':'STATIC_coffeeMachineId_3', 'coffe_machine_name':'New Palace ','coffe_machine_address':'Main Street - London', 'coffe_machine_icon_name':'coffee3.jpg', 'coffe_machine_reviews' : [] }, {'coffe_machine_id':'STATIC_coffeeMachineId_4', 'coffe_machine_name':'Black Cat ','coffe_machine_address':'Main Street - London', 'coffe_machine_icon_name':'coffee4.jpg', 'coffe_machine_reviews' : [] } ]}";

		return JSONData;
	}

	private static ArrayList<CoffeeMachine> parseData(String data) {
		try {

			JSONObject jsonObj = new JSONObject(data);
			JSONArray jsonArray = jsonObj.getJSONArray("coffe_machine_data");

			ArrayList<CoffeeMachine> dataArray = new ArrayList<CoffeeMachine>();

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject coffeMachineObj = jsonArray.getJSONObject(i);

				String name = coffeMachineObj.getString("coffe_machine_name");
				String address = coffeMachineObj
						.getString("coffe_machine_address");
				String coffeeMachineId = coffeMachineObj
						.getString("coffe_machine_id");

				JSONArray reviewsArray = coffeMachineObj
						.getJSONArray("coffe_machine_reviews");
                String iconPath = coffeMachineObj
                        .getString("coffe_machine_icon_name");

                ArrayList<Review> reviewsList = new ArrayList<Review>();
				for (int j = 0; j < reviewsArray.length(); j++) {
					JSONObject reviewObj = reviewsArray.getJSONObject(j);

					String reviewId = reviewObj.getString("review_id");
					String reviewUsername = reviewObj
							.getString("review_username");
					String reviewComment = reviewObj
							.getString("review_comment");
                    String reviewDate = reviewObj
                            .getString("review_format_date");
                    String reviewStatus = reviewObj
                            .getString("review_status");


                    //PLEASE REPLACE WITH A LIB LIKE JODATIME
//                    Date reviewDate = new Date();
//					int reviewStatus = reviewObj
	//						.getInt("review_status");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                    Date df = dateFormat.parse(reviewDate);
					reviewsList.add(new Review(reviewId, "not-available", reviewUsername,
							reviewComment, Common.parseStatusFromString(reviewStatus), null, df));
				}

				dataArray.add(new CoffeeMachine(coffeeMachineId, name, address, iconPath,
						reviewsList));
			}
			return dataArray;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
	}

/*	private static String encodeToJSONData(
			ArrayList<CoffeMachine> coffeMachineList) {
		try {
			Gson g = new Gson();
			String jsonString = g.toJson(coffeMachineList);
			return jsonString;
/*			if (coffeMachineList.size() != 0) {
				for (int i = 0; i < coffeMachineList.size(); i++) {
					CoffeMachine coffeMachineObj = coffeMachineList.get(i);
					ArrayList<Review> reviewList = coffeMachineObj
							.getReviewList();
					if (reviewList.size() != 0) {
						for (int j = 0; j < reviewList.size(); j++) {
							Review reviewObj = reviewList.get(j);
						}
					}
				}
			} --close here

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
*/
}
