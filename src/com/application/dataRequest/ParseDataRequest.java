package com.application.dataRequest;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.application.commons.Common;
import com.application.datastorage.DataStorageApplication;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.User;
import com.parse.*;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davide on 08/09/14.
 */
public class ParseDataRequest {

    private static final String TAG = "ParseDataRequest";
    private static final int QUERY_LIMITS = 5;
    private static int reviewCountByCoffeeMachineId;
    private static Object prevRevCounterKey;
    private static String reviewCountFakeData;

    public static ArrayList<CoffeeMachine> getAllCoffeeMachines() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("coffee_machines");
        List<ParseObject> parseObjects;
        try {
            parseObjects = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        ArrayList<CoffeeMachine> list = new ArrayList<>();

        for (ParseObject parseObject : parseObjects) {
            String coffeeMachineId = parseObject.getObjectId();
            String name = parseObject.getString("name");
            String address = parseObject.getString("address");
            String iconPath = parseObject.getString("icon_path");
            list.add(new CoffeeMachine(coffeeMachineId, name, address, iconPath));
        }
        Log.d(TAG, "hey object id " + parseObjects.get(0).get("name"));
        return list;
    }

    public static void getAllCoffeeMachines(DataStorageApplication coffeeApp) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("coffee_machines");
        List<ParseObject> parseObjects;
        try {
            parseObjects = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        ArrayList<CoffeeMachine> list = new ArrayList<>();

        for (ParseObject parseObject : parseObjects) {
            String coffeeMachineId = parseObject.getObjectId();
            String name = parseObject.getString("name");
            String address = parseObject.getString("address");
            String iconPath = parseObject.getString("icon_path");
            list.add(new CoffeeMachine(coffeeMachineId, name, address, iconPath));
        }
        coffeeApp.setCoffeeMachineList(list);
        Log.d(TAG, "hey object id " + parseObjects.get(0).get("name"));
    }

    public static void getAllCoffeeMachinesAsync(final DataStorageApplication coffeeApp) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("coffee_machines");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ArrayList<CoffeeMachine> list = new ArrayList<>();
                for (ParseObject parseObject : parseObjects) {
                    String coffeeMachineId = parseObject.getObjectId();
                    String name = parseObject.getString("name");
                    String address = parseObject.getString("address");
                    String iconPath = parseObject.getString("icon_path");
                    list.add(new CoffeeMachine(coffeeMachineId, name, address, iconPath));
                }
                coffeeApp.setCoffeeMachineList(list); //TODO mmmmmmm I'm not so sure it works -.-
                Log.d(TAG, "hey object id " + parseObjects.get(0).get("name"));
            }
        });
    }

    public static void getAllReviewsByCoffeeMachineId(DataStorageApplication coffeeApp,
                                                      String coffeeMachineId){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("coffee_machine_id_string", coffeeMachineId);
        List<ParseObject> parseObjects;
        try {
            parseObjects = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        ArrayList<Review> reviewList = new ArrayList<Review>();
        for (ParseObject parseObject : parseObjects) {
            String id = parseObject.getObjectId();
            String userId = parseObject.getString("user_id_string");
            String comment = parseObject.getString("comment");
            String status = parseObject.getString("status");
            String timestamp = parseObject.getString("timestamp");

            long timestampParsed = Long.parseLong(timestamp);
            Common.ReviewStatusEnum statusParsed = Review.parseStatus(status);
            reviewList.add(new Review(id, comment, statusParsed, timestampParsed, userId, coffeeMachineId));
        }
        coffeeApp.getReviewListMap().put(coffeeMachineId, reviewList);
    }

    public static void getAllReviewsByCoffeeMachineIdAsync(final DataStorageApplication coffeeApp,
                                                      final String coffeeMachineId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("coffee_machine_id_string", coffeeMachineId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ArrayList<Review> reviewList = new ArrayList<>();
                for (ParseObject parseObject : parseObjects) {
                    String id = parseObject.getObjectId();
                    String comment = parseObject.getString("comment");
                    String status = parseObject.getString("status");
                    String timestamp = parseObject.getString("timestamp");
                    String userId = parseObject.getString("user_id_string");

                    Long timestampParsed = Long.parseLong(timestamp);
                    Common.ReviewStatusEnum statusParsed = Review.parseStatus(status);
                    reviewList.add(new Review(id, comment, statusParsed, timestampParsed, userId, coffeeMachineId));
                }

                coffeeApp.getReviewListMap().put(coffeeMachineId, reviewList);
            }
        });
    }



    public static void downloadProfilePicture(final DataStorageApplication coffeeApp,
                                              String userId, final ImageView profilePicImageView,
                                              final Drawable defaultIcon){

        final File dir = null;
//        final File dir = coffeeApp.getCustomDir();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
        query.whereEqualTo("user_id_string", userId);
//        List<ParseObject> userList = query.find();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {

                if(parseObjects != null && parseObjects.size() == 1) {
                    ParseFile profilePictureFile = (ParseFile) parseObjects.get(0).get("profile_picture_id");
                    final String profilePictureName = profilePictureFile.getName();

                    profilePictureFile.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                try {
                                    String filePath = dir.getPath() + "/" + profilePictureName;
                                    FileOutputStream fos = new FileOutputStream(filePath);
                                    fos.write(data);
                                    fos.close();
                                    profilePicImageView.setImageBitmap(BitmapFactory.
                                            decodeByteArray(data, 0, data.length));
                                    return;
                                } catch (Exception e1) {
                                    Log.e(TAG, e1.getMessage());
                                }
                            }
                            //NOT FOUND image
                            Log.e(TAG, "profile-pic not found");
                            profilePicImageView.setImageDrawable(defaultIcon);
                        }
                    });
                }
            }
        });
    }


    public static void updateUserById(String userId, String profilePicturePath,
                                      String username){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
        query.whereEqualTo("user_id_string", userId);
        List<ParseObject> userList;
        try {
            userList = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        if(userList != null && userList.size() == 1) {
            long id = -1;
            ParseObject user = userList.get(0);
            if (profilePicturePath != null) {
                user.put("profile_picture_path", profilePicturePath);
            }
            if (username != null) {
                user.put("username", username);
            }
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.d(TAG, "user updated with success");
                        return;
                    }
                    Log.e(TAG, "user not updated ");
                }
            });
        }
        Log.e(TAG, "user not found ");
    }

    public static void removeUserById(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
        query.whereEqualTo("user_id_string", userId);
        List<ParseObject> userList;
        try {
            userList = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        if(userList != null && userList.size() == 1) {
            ParseObject user = userList.get(0);
            user.put("active", false); //set user to active status as false
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.d(TAG, "user updated with success");
                        return;
                    }
                    Log.e(TAG, "user not updated ");
                }
            });
        }
        Log.e(TAG, "user not found ");
    }

    public static void getUserByIdAsync(String userId, final TextView usernameTextView,
                                        final ImageView profilePicImageView,
                                        final ImageLoader imageLoader, final int defaultIcon) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
        query.whereEqualTo("objectId", userId);

        Log.e(TAG," hey u're retrieving user");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "profile pic not found ");
                    Log.e(TAG, e.getMessage());
                    return;
                }

                if (parseObjects != null && parseObjects.size() == 1) {
                    usernameTextView.setText(parseObjects.get(0).getString("username"));
                    String profilePicturePath = parseObjects.get(0).getString("profile_picture_path");
                    if (profilePicturePath != null) {
                        DataRequestVolleyService.downloadProfilePicture(profilePicturePath, profilePicImageView,
                                imageLoader, defaultIcon);
                    }
                }

            }

        });
    }


    public static void getAllReviewsByCoffeeMachineIdToday(DataStorageApplication coffeeApp,
                                                      String coffeeMachineId) {
        //TODAY request
        DateTime dateTime = new DateTime();
        String todayTimestamp = String.valueOf((dateTime.toDateTime().withTimeAtStartOfDay().getMillis()));

        //request
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("coffee_machine_id_string", coffeeMachineId);
        query.whereGreaterThan("timestamp", todayTimestamp);
        //query.setLimit(QUERY_LIMITS);
        Log.e(TAG, "timestamp " + todayTimestamp);
        List<ParseObject> parseObjects;
        try {
            parseObjects = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        ArrayList<Review> reviewList = new ArrayList<Review>();
        for (ParseObject parseObject : parseObjects) {
            String id = parseObject.getObjectId();
            String userId = parseObject.getString("user_id_string");
            String comment = parseObject.getString("comment");
            String status = parseObject.getString("status");
            String timestamp = parseObject.getString("timestamp");

            long timestampParsed = Long.parseLong(timestamp);
            Common.ReviewStatusEnum statusParsed = Review.parseStatus(status);
            reviewList.add(new Review(id, comment, statusParsed, timestampParsed, userId, coffeeMachineId));
        }
        coffeeApp.getReviewListMap().put(coffeeMachineId, reviewList);
    }

    public static int getReviewCountByCoffeeMachineId(String coffeeMachineId, long fromTimestamp, long toTimestamp) {
        int reviewsCount = 0;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("coffee_machine_id_string", coffeeMachineId);
        query.whereGreaterThan("timestamp", String.valueOf(fromTimestamp));
        query.whereLessThan("timestamp", String.valueOf(toTimestamp));
        Log.e(TAG, "timestamp " + toTimestamp + " - " + fromTimestamp);
        try {
            reviewsCount = query.count();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return -1;
        }

//        coffeeApp.getReviewListMap().put(coffeeMachineId, reviewList);

        return reviewsCount;
    }

    public static void setReviewCountByCoffeeMachineId(DataStorageApplication coffeeApp, Common.ReviewStatusEnum status,
                                                       String coffeeMachineId, long fromTimestamp, long toTimestamp) {
        int reviewsCount = 0;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("coffee_machine_id_string", coffeeMachineId);
        query.whereEqualTo("status", status.name());
        query.whereGreaterThan("timestamp", String.valueOf(fromTimestamp));
        query.whereLessThan("timestamp", String.valueOf(toTimestamp));
        Log.e(TAG, "timestamp " + toTimestamp + " - " + fromTimestamp);
        try {
            reviewsCount = query.count();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            reviewsCount = -1;
        }

//        String prevRevCounterKey = coffeeApp.getPrevRevCounterKey(coffeeMachineId, status);
//        coffeeApp.getPrevReviewCounterList().put(prevRevCounterKey, reviewsCount);
    }
    public static boolean setReviewCountByCoffeeMachineId(DataStorageApplication coffeeApp,
                                                       String coffeeMachineId, long fromTimestamp, long toTimestamp) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("coffee_machine_id_string", coffeeMachineId);
        query.whereGreaterThan("timestamp", String.valueOf(fromTimestamp));
        query.whereLessThan("timestamp", String.valueOf(toTimestamp));
        Log.e(TAG, "timestamp " + toTimestamp + " - " + fromTimestamp);
        try {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject object : parseObjects) {
/*                String prevRevCounterKey = coffeeApp.getPrevRevCounterKey(coffeeMachineId,
                        Review.parseStatus(object.getString("status")));
                coffeeApp.incrementPrevReviewCounter(prevRevCounterKey);*/
            }
            return true;
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }


    public static boolean setAndCountReviewByCoffeeMachineId(DataStorageApplication coffeeApp,
                                                          String coffeeMachineId, long fromTimestamp, long toTimestamp) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("coffee_machine_id_string", coffeeMachineId);
        query.whereGreaterThan("timestamp", String.valueOf(fromTimestamp));
        query.whereLessThan("timestamp", String.valueOf(toTimestamp));
        Log.e(TAG, "timestamp " + toTimestamp + " - " + fromTimestamp);

        //reset
        for(Common.ReviewStatusEnum status : Common.ReviewStatusEnum.values()) {
//            String prevRevCounterKey = coffeeApp.getPrevRevCounterKey(coffeeMachineId, status);
//            coffeeApp.resetPrevReviewCounter(prevRevCounterKey);
        }

        try {
            List<ParseObject> parseObjects = query.find();
            ArrayList<Review> reviewList = new ArrayList<>();
            for(ParseObject parseObject : parseObjects) {
//                String prevRevCounterKey = coffeeApp.getPrevRevCounterKey(coffeeMachineId,
//                        Review.parseStatus(parseObject.getString("status")));
                String id = parseObject.getObjectId();
                String userId = parseObject.getString("user_id_string");
                String comment = parseObject.getString("comment");
                String status = parseObject.getString("status");
                String timestamp = parseObject.getString("timestamp");

                long timestampParsed = Long.parseLong(timestamp);
                Common.ReviewStatusEnum statusParsed = Review.parseStatus(status);
                reviewList.add(new Review(id, comment, statusParsed, timestampParsed, userId, coffeeMachineId));
//                coffeeApp.incrementPrevReviewCounter(prevRevCounterKey);
            }

            ArrayList<Review> reviewListOld = coffeeApp.getReviewListMap().get(coffeeMachineId);
            if(reviewListOld != null) {
                reviewListOld.addAll(reviewList); //TODO FIX IT every time add review on listview
            }
//            coffeeApp.getReviewListMap().put(coffeeMachineId, reviewList);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }



    public static Object getPrevRevCounterKey() {
        return prevRevCounterKey;
    }









    public static void uploadProfilePicture(String profilePicturePath, final String userId) {
        InputStream is;

        try {
            is = new FileInputStream(profilePicturePath);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return ;
        }

//        final String fileName = String.valueOf(UUID.randomUUID());
        final String fileName = "profilePicture.png";

        ParseFile file = null;

        try {
            file = new ParseFile(fileName, IOUtils.toByteArray(is));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        final ParseFile finalFile = file;
        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, e.getMessage());
                    return;
                }

                ParseObject parseObject = ParseDataRequest.getUserByIdToParseObject(userId);
                if(parseObject != null) {
                    parseObject.put("profile_picture_name", finalFile.getName());
                    parseObject.put("profile_picture_path", finalFile.getUrl());
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.d(TAG, "hey saved user file row with success");
                        }
                    });
                    Log.d(TAG, "hey upload pic successfully upload");
                    return;
                }

                Log.e(TAG, "hey upload pic failed - no user found");
            }
        }, new ProgressCallback() {
            public void done(Integer percentDone) {
                // Update your progress spinner here. percentDone will be between 0 and 100.
            }
        });
        return;
    }

    public static String addUserByParams(final DataStorageApplication coffeeApp,
                                         final String profilePicturePath, final String username) {
        ParseObject parseObject = new ParseObject("users");
        if (profilePicturePath != null) {
            parseObject.put("profile_picture_path", profilePicturePath);
        }
        if (username != null) {
            parseObject.put("username", username);
        }
        try {
            parseObject.save();
        } catch (ParseException e) {
            Log.e(TAG, "user not updated " + e.getMessage());
            return null;
        }
        return parseObject.getObjectId();
/*        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.d(TAG, "user updated with success");
                    return;
                }
                Log.e(TAG, "user not updated ");
            }
        });**/
    }


//    public static void addReviewByParams(String userId, String coffeeMachineId, String comment,
//                                         Common.ReviewStatusEnum status, long timestamp) {
//        ParseObject parseObject = new ParseObject("reviews");
//        parseObject.put("comment", comment);
//        parseObject.put("coffee_machine_id_string", coffeeMachineId);
//        parseObject.put("timestamp", String.valueOf(timestamp));
//        parseObject.put("status", status.toString());
//        parseObject.put("user_id_string", userId);
//        parseObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e != null) {
//                    Log.e(TAG, "review not inserted " + e.getMessage());
//                    return;
//                }
//                Log.d(TAG, "review inserted successfully");
//            }
//        });
//    }

    public static Review getReviewById(String reviewId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("objectId", reviewId);
        List<ParseObject> reviewList;
        try {
            reviewList = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        if(reviewList != null && reviewList.size() == 1) {
            ParseObject parseObject = reviewList.get(0);
            String id = parseObject.getObjectId();
            String comment = parseObject.getString("comment");
            String userId = parseObject.getString("user_id_string");
            String coffeeMachineId = parseObject.getString("coffee_machine_id_string");
            String timestamp = parseObject.getString("timestamp");
            String status = parseObject.getString("status");
            Long timestampParsed = Long.parseLong(timestamp);
            Common.ReviewStatusEnum statusParsed = Review.parseStatus(status);
            return new Review(id, comment, statusParsed, timestampParsed, userId, coffeeMachineId);
        }
        Log.e(TAG, "review not found ");
        return null;
    }

    public static boolean removeReviewById(String reviewId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("objectId", reviewId);
        List<ParseObject> reviewList;
        try {
            reviewList = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        if(reviewList != null && reviewList.size() == 1) {
            ParseObject review = reviewList.get(0);
            review.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(TAG, "review not deleted");
                        return;
                    }
                    Log.d(TAG, "review deleted successfully");

                }
            });
            return true;
        }
        Log.e(TAG, "review not updated ");
        return false;
    }

    public static boolean updateReviewById(String reviewId, String comment) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
        query.whereEqualTo("objectId", reviewId);
        List<ParseObject> reviewList;
        try {
            reviewList = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        if(reviewList != null && reviewList.size() == 1) {
            ParseObject review = reviewList.get(0);
            review.put("comment", comment);
            review.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(TAG, "review not updated");
                        return;
                    }
                    Log.d(TAG, "review updated successfully");
                }
            });
            return true;
        }
        Log.e(TAG, "review not updated ");
        return false;
    }

    public static User getUserById(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
        query.whereEqualTo("objectId", userId);
        List<ParseObject> userList;
        try {
            userList = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        if(userList != null && userList.size() == 1) {
            String id = userList.get(0).getObjectId();
            String profilePicturePath = userList.get(0).getString("profile_picture_path");
            String username = userList.get(0).getString("username");
            return new User(id, profilePicturePath, username);
        }
        Log.e(TAG, "user not found ");
        return null;
    }

    public static ParseObject getUserByIdToParseObject(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
        query.whereEqualTo("objectId", userId);
        List<ParseObject> userList;
        try {
            userList = query.find();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        if(userList != null && userList.size() == 1) {
            return userList.get(0);
        }
        Log.e(TAG, "user not found ");
        return null;
    }




}
