package com.application.datastorage;

import android.content.Context;
import android.util.Log;
import com.application.commons.Common;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.User;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class DataRequestServices {
    private static final String TAG = "DataRequestServices";
    private static HttpRequestData httpRequestData;
//    private static final String SERVER_URL = "http://192.168.137.94:3000";
//    private static final String SERVER_URL = "http://10.0.2.2:3000";
    private static final String SERVER_URL = "http://192.168.56.1:3000";
//    private static final String SERVER_URL = "http://192.168.1.117:3000";


    public DataRequestServices(File customDir) {
        httpRequestData = new HttpRequestData(customDir);
    }
	// private final static String TAG ="retrieveDataFromServer";
    public ArrayList<CoffeeMachine> getCoffeeMachineList() {
        //HTTP REQUEST
        String data = getCoffeeMachineListHttpRequest();
        //PARSE DATA AND RETURN
        return getCoffeeMachineListParser(data);
    }

    public ArrayList<Review> getReviewListById(long id) {
        //HTTP REQUEST
        String data = getReviewListByIdHttpRequest(id);
        if(data == null) {
            Log.e(TAG, "no data retrieved from server");
            return null;
        }
        //PARSE DATA AND RETURN
        return getReviewListParser(data);
    }

    public Review addReviewByParams(long userId, long coffeeMachineId,
                                           String comment, Common.ReviewStatusEnum status,
                                           long timestamp) {
        //HTTP REQUEST
        return addReviewByParamsHttpRequest(comment, userId, coffeeMachineId,
                status.name(), timestamp);
    }

    public User addUserByParams(String profilePicturePath, String username) {
        //HTTP REQUEST
        return addUserByParamsHttpRequest(profilePicturePath, username);
    }

    private String getCoffeeMachineListHttpRequest() {
        try {
            return httpRequestData.asyncRequestData("GET",
                    new URI(SERVER_URL + "/api/coffee_machines"), null, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
        //asyncTask with http request - return JSON
/*        String data = "{\"coffee_machine_list\" : [" +
                "{\"coffee_machine_id\":\"1\", " +
                    "\"coffee_machine_name\": \"New Kinder\"," +
                    "\"coffee_machine_address\": \"Main Street - London\", " +
                    "\"coffee_machine_icon_name\": \"coffee1.jpg\"}," +
                "{\"coffee_machine_id\":\"2\"," +
                    "\"coffee_machine_name\":\"Hey Machine\"," +
                    "\"coffee_machine_address\":\"Even village - Mexico\", " +
                    "\"coffee_machine_icon_name\":\"coffee2.jpg\"}," +
                "{\"coffee_machine_id\":\"3\"," +
                    "\"coffee_machine_name\":\"New Palace \"," +
                    "\"coffee_machine_address\":\"Main Street - London\"," +
                    "\"coffee_machine_icon_name\":\"coffee3.jpg\"}," +
                "{\"coffee_machine_id\":\"4\"," +
                    "\"coffee_machine_name\":\"Black Cat \"," +
                    "\"coffee_machine_address\":\"Main Street - London\"," +
                    "\"coffee_machine_icon_name\":\"coffee4.jpg\"}" +
                "]}";
        return data;*/
    }

    private String getReviewListByIdHttpRequest(long coffeeMachineId) {
        try {
            return httpRequestData.asyncRequestData("GET", new URI(SERVER_URL
                    + "/api/reviews?filter[where][coffee_machine_id]=" + coffeeMachineId), null, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;

/*        //asynTask with http request - return JSON
        String reviewList0 = "{\"review_list\" : [" +
                "{\"id\":\"6\"," +
                "\"user_id\":\"1\"," +
//                "\"user_id\":\"Helvin Key\"," +
                "\"coffee_machine_id\":\"1\"," +
                "\"comment\":\"Hey this is review 0 on CM:1\"," +
                "\"status\":\"NOTSOBAD\"," +
                "\"timestamp\": \"2.07.2014\"}" +
                "]}";

        String reviewList1 = "{\"review_list\" : [" +
                "{\"id\":\"1\"," +
//                "\"user_id\":\"Greys Losk\"," +
                "\"user_id\":\"1\"," +
                "\"coffee_machine_id\":\"2\"," +
                "\"comment\":\"Hey this is review 1 on CM:2\"," +
                "\"status\":\"WORST\"," +
                "\"timestamp\": \"2.07.2014\"}," +
                "{\"id\":\"2\"," +
                "\"user_id\": \"2\"," +
//                "\"user_id\":\"Mike pp\"," +
                "\"coffee_machine_id\":\"2\"," +
                "\"comment\":\"Hey this is review 2 on CM:2\"," +
                "\"status\":\"NOTSOBAD\"," +
                "\"timestamp\": \"01.06.2014\"}," +
                "{\"id\":\"3\"," +
//                "\"user_id\":\"Henry d\"," +
                "\"user_id\": \"2\"," +
                "\"coffee_machine_id\":\"2\"," +
                "\"comment\":\"this is the comment on machine cos I want to say thats nothing in front of your problems didnt u agree with me?\"," +
                "\"status\":\"GOOD\"," +
                "\"timestamp\": \"06.06.2014\"}," +
                "{\"id\":\"4\"," +
//                "\"user_id\":\"John Helk\"," +
                "\"user_id\": \"2\"," +
                "\"coffee_machine_id\":\"2\"," +
                "\"comment\":\"Hey this is review 3 on CM:2\"," +
                "\"status\":\"NOTSOBAD\"," +
                "\"timestamp\": \"10.06.2014\"}," +
                "{\"id\": \"5\"," +
//                "\"user_id\":\"Lauri Vanaulinen\"," +
                "\"user_id\": \"3\"," +
                "\"coffee_machine_id\":\"2\"," +
                "\"comment\":\"Hey this is review 4 on CM:2\"," +
                "\"status\":\"GOOD\"," +
                "\"timestamp\": \"07.06.2014\"}" +
                "]}";

        String reviewList2 = "{\"review_list\" : [" +
                "]}";

        String reviewList3 = "{\"review_list\" : [" +
                "{\"id\":\"7\"," +
//                "\"user_id\":\"Juan Carlos\"," +
                "\"user_id\": \"1\"," +
                "\"coffee_machine_id\":\"4\"," +
                "\"comment\":\"Hey this is review 5 on CM:4\"," +
                "\"status\":\"GOOD\"," +
                "\"timestamp\": \"2.07.2014\"}" +
                "]}";

        //TODO take care of this - only in test mode
        switch ((int) coffeeMachineId) {
            case 1:
                return reviewList0;
            case 2:
                return reviewList1;
            case 3:
                return reviewList2;
            case 4:
                return reviewList3;
            default:
                return null;
        }*/
    }

    private Review addReviewByParamsHttpRequest(String comment, long userId,
                                                       long coffeeMachineId, String statusName,
                                                       long timestamp) {
        try {
            JSONObject params = new JSONObject();
            params.put("comment", comment);
            params.put("timestamp", timestamp);
            params.put("user_id", userId);
            params.put("coffee_machine_id", coffeeMachineId);
            params.put("status", statusName);

            String data = httpRequestData.asyncRequestData("POST", new URI(SERVER_URL
                    + "/api/reviews"), params.toString(), null);
            if(data == null) {
                return null;
            }
            return reviewParser(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

        //AsyncTASK
        //return new Review(000000000 , comment, Review.parseStatus(statusName), timestamp, userId, coffeeMachineId);
    }


    public boolean removeReviewById(long id) {
        try {
            httpRequestData.asyncRequestData("DELETE", new URI(SERVER_URL
                    + "/api/reviews/" + id), null, null);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Review getReviewById(long reviewId) {
        try {
            String data = httpRequestData.asyncRequestData("GET", new URI(SERVER_URL
                    + "/api/reviews/" + reviewId), null, null);
            return reviewParser(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateReviewById(long reviewId, String comment) {
        try {
            JSONObject params = new JSONObject();
            params.put("comment", comment);

            String data = httpRequestData.asyncRequestData("PUT", new URI(SERVER_URL
                    + "/api/reviews/" + reviewId), params.toString(), null);
            if (data == null) {
                Log.e(TAG, "couldn't find on server review with id:" + reviewId);
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;

    }

    private User addUserByParamsHttpRequest(String profilePicturePath, String username) {
        //Asynctask to add user
        try {

            JSONObject params = new JSONObject();
            params.put("username", username);
            params.put("profile_picture_path", profilePicturePath);

            String data = httpRequestData.asyncRequestData("POST", new URI(SERVER_URL
                    + "/api/users"), params.toString(), null);
            if(data == null) {
                return null;
            }
            return userParser(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

//        return new User(00000000, profilePicturePath, username);
    }


    public User getUserById(long userId) {
        try {
            String data = httpRequestData.asyncRequestData("GET", new URI(SERVER_URL
                    + "/api/users/" + userId), null, null);
            if (data == null) {
                Log.e(TAG, "couldn't find on server user with id:" + userId);
                return null;
            }
            return userParser(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserById(long userId, String profilePicturePath, String username) {
        try {
            JSONObject params = new JSONObject();
            params.put("username", username);
            params.put("profile_picture_path", profilePicturePath);

            String data = httpRequestData.asyncRequestData("PUT", new URI(SERVER_URL
                    + "/api/users/" + userId), params.toString(), null);
            if (data == null) {
                Log.e(TAG, "couldn't find on server user with id:" + userId);
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String uploadProfilePicture(String profilePicturePath) {
        //check the name (cos will be the index of the picture) -
        String PROFILE_PICTURE_CONTAINER = "profile-picture-container";
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            File image = new File("path");
            FileBody fileBody = new FileBody(image); //image should be a String
            builder.addPart("file", fileBody);
            HttpEntity entity = builder.build();

            JSONObject params = new JSONObject();
            params.put("profile_picture_path", profilePicturePath);

            String data = httpRequestData.asyncRequestData("POST-mime", new URI(SERVER_URL
                    + "/api/containers/" + PROFILE_PICTURE_CONTAINER + "/upload"), null, entity);
            if (data == null) {
                Log.e(TAG, "couldn't upload file");
                return null;
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String downloadProfilePicture(String fileName) {
        //check the name (cos will be the index of the picture) -

        //fileName = "avatar_2x.png"; //TODO - remove fake filename
        String PROFILE_PICTURE_CONTAINER = "profile-picture-container";
        try {
/*            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            File image = new File("path");
            FileBody fileBody = new FileBody(image); //image should be a String
            builder.addPart("file", fileBody);
            HttpEntity entity = builder.build();

            JSONObject params = new JSONObject();
            params.put("profile_picture_path", profilePicturePath);*/
            String data = httpRequestData.asyncRequestData("GET-mime", new URI(SERVER_URL
                    + "/api/containers/" + PROFILE_PICTURE_CONTAINER + "/download" + fileName), null, null);
            if (data == null) {
                Log.e(TAG, "couldn't upload file");
                return null;
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;


    }



    private ArrayList<CoffeeMachine> getCoffeeMachineListParser(String data) {
        if(data != null) {
            try {
//            JSONObject jsonObj = new JSONObject(data);
//            JSONArray jsonArray = jsonObj.getJSONArray("coffee_machine_list");
                JSONArray jsonArray = new JSONArray(data); //STATIC

                ArrayList<CoffeeMachine> coffeeMachineList = new ArrayList<CoffeeMachine> ();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject coffeeMachineObj = jsonArray.getJSONObject(i);

                    String name = coffeeMachineObj.getString("name");
                    String address = coffeeMachineObj
                            .getString("address");
                    long coffeeMachineId = coffeeMachineObj
                            .getLong("id");
                    String iconPath = coffeeMachineObj
                            .getString("icon_path");


                    coffeeMachineList.add(new CoffeeMachine(coffeeMachineId, name, address, iconPath));
                }
                return coffeeMachineList;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Review reviewParser(String data) {
        JSONObject reviewJsonObj = null;
        try {
            reviewJsonObj = new JSONObject(data);
            long reviewId = reviewJsonObj.getLong("id");
            long reviewUserId = reviewJsonObj
                    .getLong("user_id");
            long reviewCoffeeMachineId = reviewJsonObj
                    .getLong("coffee_machine_id");
            String reviewComment = reviewJsonObj
                    .getString("comment");
            String timestamp = reviewJsonObj
                    .getString("timestamp");
            String reviewStatus = reviewJsonObj
                    .getString("status");

            //PLEASE REPLACE WITH A LIB LIKE JODATIME
    //                    Date reviewDate = new Date();
    //					int reviewStatus = reviewObj
            //.getInt("review_status");
            //get username by userId

            return new Review(reviewId, reviewComment, Review.parseStatus(reviewStatus),
                    Long.parseLong(timestamp), reviewUserId, reviewCoffeeMachineId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Review> getReviewListParser(String data) {
        try {
//            JSONObject obj = new JSONObject(data);
//            JSONArray reviewJsonArray = obj.getJSONArray("review_list"); //STATIC
            JSONArray reviewJsonArray = new JSONArray(data); //STATIC

            ArrayList<Review> reviewsList = new ArrayList<Review>();
            for (int j = 0; j < reviewJsonArray.length(); j ++) {
                JSONObject reviewJsonObj = reviewJsonArray.getJSONObject(j);

                long reviewId = reviewJsonObj.getLong("id");
                long reviewUserId = reviewJsonObj
                        .getLong("user_id");
                long reviewCoffeeMachineId = reviewJsonObj
                        .getLong("coffee_machine_id");
                String reviewComment = reviewJsonObj
                        .getString("comment");
                String timestamp = reviewJsonObj
                        .getString("timestamp");
                String reviewStatus = reviewJsonObj
                        .getString("status");

                //PLEASE REPLACE WITH A LIB LIKE JODATIME
//                    Date reviewDate = new Date();
//					int reviewStatus = reviewObj
                //.getInt("review_status");
                //get username by userId

//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
//                long timestamp = dateFormat.parse(reviewDate).getTime();
                reviewsList.add(new Review(reviewId, reviewComment, Review.parseStatus(reviewStatus),
                        Long.parseLong(timestamp), reviewUserId, reviewCoffeeMachineId));
            }

            return reviewsList;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private User userParser(String data) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(data);
            long userId = jsonObj.getLong("id");
            String username = jsonObj
                    .getString("username");

            String profilePicturePath = null;
            try {
                profilePicturePath = jsonObj
                        .getString("profile_picture_path");
            } catch (JSONException e) {
                e.getMessage();
            }

            return new User(userId, profilePicturePath, username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
