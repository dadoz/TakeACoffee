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

public class DataRequestServices {
	// private final static String TAG ="retrieveDataFromServer";


    public static ArrayList<CoffeeMachine> getCoffeeMachineList() {
        //HTTP REQUEST
        String data = getCoffeeMachineListHttpRequest();
        //PARSE DATA AND RETURN
        return getCoffeeMachineListParser(data);
    }

    public static ArrayList<Review> getReviewListById(String id) {
        //HTTP REQUEST
        String data = getReviewListByIdHttpRequest(id);
        //PARSE DATA AND RETURN
        return getReviewListParser(data, id);
    }

    private static String getCoffeeMachineListHttpRequest() {
        //asynTask with http request - return JSON

        String data = "{\"coffee_machine_list\" : [" +
                "{\"coffee_machine_id\": \"STATIC_coffeeMachineId_1\", " +
                    "\"coffee_machine_name\": \"New Kinder\"," +
                    "\"coffee_machine_address\": \"Main Street - London\", " +
                    "\"coffee_machine_icon_name\": \"coffee1.jpg\", " +
                    "\"coffee_machine_review_list_id\": \"reviewListId_1\" }," +
                "{\"coffee_machine_id\":\"STATIC_coffeeMachineId_2\"," +
                    "\"coffee_machine_name\":\"Hey Machine\"," +
                    "\"coffee_machine_address\":\"Even village - Mexico\", " +
                    "\"coffee_machine_icon_name\":\"coffee2.jpg\"," +
                    "\"coffee_machine_review_list_id\" :\"reviewListId_2\" }," +
                "{\"coffee_machine_id\":\"STATIC_coffeeMachineId_3\"," +
                    "\"coffee_machine_name\":\"New Palace \"," +
                    "\"coffee_machine_address\":\"Main Street - London\"," +
                    "\"coffee_machine_icon_name\":\"coffee3.jpg\"," +
                    "\"coffee_machine_review_list_id\" : \"reviewListId_3\" },"+
                "{\"coffee_machine_id\":\"STATIC_coffeeMachineId_4\"," +
                    "\"coffee_machine_name\":\"Black Cat \"," +
                    "\"coffee_machine_address\":\"Main Street - London\"," +
                    "\"coffee_machine_icon_name\":\"coffee4.jpg\"," +
                    "\"coffee_machine_review_list_id\" : reviewListId_4}" +
                "]}";
        return data;
    }

    private static String getReviewListByIdHttpRequest(String reviewListId) {
        //asynTask with http request - return JSON

        String reviewList1 = "{\"reviewListId_1\" : [" +
                "{\"review_id\":\"STATIC_REVIEWID_0\"," +
                    "\"review_username\":\"Greys Losk\"," +
                    "\"review_comment\":\"This coffe machine is really terrible\"," +
                    "\"review_status\":\"WORST\"," +
                    "\"review_format_date\": \"2.07.2014\"}," +
                "{\"review_id\":\"STATIC_REVIEWID_1\"," +
                    "\"review_username\":\"Mike pp\"," +
                    "\"review_comment\":\"this is the comment on machine\"," +
                    "\"review_status\":\"NOT_BAD\"," +
                    "\"review_format_date\": \"01.06.2014\"}," +
                "{\"review_id\":\"STATIC_REVIEWID_2\"," +
                    "\"review_username\":\"Henry d\"," +
                    "\"review_comment\":\"this is the comment on machine cos I want to say thats nothing in front of your problems didnt u agree with me?\"," +
                    "\"review_status\":\"GOOD\"," +
                    "\"review_format_date\": \"06.06.2014\"}," +
                "{\"review_id\":\"STATIC_REVIEWID_3\"," +
                    "\"review_username\":\"John Helk\"," +
                    "\"review_comment\":\"this is the comment on machine\"," +
                    "\"review_status\":\"NOT_BAD\"," +
                    "\"review_format_date\": \"10.06.2014\"}," +
                "{\"review_id\":\"STATIC_REVIEWID_4\"," +
                    "\"review_username\":\"Lauri Vanaulinen\"," +
                    "\"review_comment\":\"this is the comment on machine\"," +
                    "\"review_status\":\"GOOD\"," +
                    "\"review_format_date\": \"07.06.2014\"}" +
                "]}";

        String reviewList2 = "{\"reviewListId_2\" : [" +
                "{\"review_id\":\"STATIC_REVIEWID_0\"," +
                "\"review_username\":\"Helvin Key\"," +
                "\"review_comment\":\"This coffe machine is really terrible\"," +
                "\"review_status\":\"NOT_BAD\"," +
                "\"review_format_date\": \"2.07.2014\"}" +
                "]}";

        String reviewList3 = "{\"reviewListId_3\" : [" +
                "]}";

        String reviewList4 = "{\"reviewListId_4\" : [" +
                "{\"review_id\":\"STATIC_REVIEWID_0\"," +
                "\"review_username\":\"Juan Carlos\"," +
                "\"review_comment\":\"This coffe machine is really terrible\"," +
                "\"review_status\":\"GOOD\"," +
                "\"review_format_date\": \"2.07.2014\"}" +
                "]}";

        int reviewId = Integer.parseInt(reviewListId.replace("reviewListId_", ""));
        switch (reviewId) {
            case 1:
                return reviewList1;
            case 2:
                return reviewList2;
            case 3:
                return reviewList3;
            case 4:
                return reviewList4;
            default:
                return null;
        }
    }


    private static ArrayList<CoffeeMachine> getCoffeeMachineListParser(String data) {
        try {
            JSONObject jsonObj = new JSONObject(data);
            JSONArray jsonArray = jsonObj.getJSONArray("coffee_machine_list");

            ArrayList<CoffeeMachine> coffeeMachineList = new ArrayList<CoffeeMachine> ();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject coffeeMachineObj = jsonArray.getJSONObject(i);

                String name = coffeeMachineObj.getString("coffee_machine_name");
                String address = coffeeMachineObj
                        .getString("coffee_machine_address");
                String coffeeMachineId = coffeeMachineObj
                        .getString("coffee_machine_id");
                String iconPath = coffeeMachineObj
                        .getString("coffee_machine_icon_name");

                String coffeeMachineReviewListId = coffeeMachineObj
                        .getString("coffee_machine_review_list_id");

                coffeeMachineList.add(new CoffeeMachine(coffeeMachineId, name, address, iconPath,
                        coffeeMachineReviewListId));
            }
            return coffeeMachineList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<Review> getReviewListParser(String data, String id) {
        try {
            JSONObject obj = new JSONObject(data);
            JSONArray reviewJsonArray = obj.getJSONArray(id);

            ArrayList<Review> reviewsList = new ArrayList<Review>();
            for (int j = 0; j < reviewJsonArray.length(); j++) {
                JSONObject reviewJsonObj = reviewJsonArray.getJSONObject(j);

                String reviewId = reviewJsonObj.getString("review_id");
                String reviewUsername = reviewJsonObj
                        .getString("review_username");
                String reviewComment = reviewJsonObj
                        .getString("review_comment");
                String reviewDate = reviewJsonObj
                        .getString("review_format_date");
                String reviewStatus = reviewJsonObj
                        .getString("review_status");

                //PLEASE REPLACE WITH A LIB LIKE JODATIME
//                    Date reviewDate = new Date();
//					int reviewStatus = reviewObj
                //.getInt("review_status");

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date reviewDateFormat = dateFormat.parse(reviewDate);
                reviewsList.add(new Review(reviewId, "not-available", reviewUsername,
                        reviewComment, Common.parseStatusFromString(reviewStatus), null, reviewDateFormat));
            }
            return reviewsList;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
