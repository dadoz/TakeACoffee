package com.application.queries;

import android.content.res.AssetManager;
import android.util.Log;
import com.application.dataRequest.ParseDataRequest;
import com.parse.*;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by davide on 08/09/14.
 */
public class ParseQueries {

    private static final String TAG = "ParseQueries";

    public static void parseQuery() {
        //update db

        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
//        query.whereEqualTo("coffee_machine_id", 3);
        //PZrB82ZWVl
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                for (ParseObject parseObject : parseObjects) {
                    String userIdString = "NULL";
                    if (parseObject.getInt("user_id") == 49) {
                        userIdString = "ZGWVingNjV";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 39) {
                        userIdString = "JHFX7ZbSlr";
                        parseObject.put("user_id_string", userIdString);

                    } else if (parseObject.getInt("user_id") == 32) {
                        userIdString = "0hp29UPPgT";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 21) {
                        userIdString = "qPdDlflwVe";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 20) {
                        userIdString = "yIwCG1BIfS";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 18) {
                        userIdString = "ZVWARifF9A";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 19) {
                        userIdString = "eyDG6SadD6";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 17) {
                        userIdString = "xE8sKA07HX";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 14) {
                        userIdString = "U9iYVNF5RV";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 10) {
                        userIdString = "ny1VpPcxKP";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 7) {
                        userIdString = "lZz2P4TQ6B";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 5) {
                        userIdString = "fYZ7jYYyeI";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 4) {
                        userIdString = "om3HoSItux";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 3) {
                        userIdString = "hhNi6TghZy";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 2) {
                        userIdString = "0HwpT6hmFc";
                        parseObject.put("user_id_string", userIdString);
                    } else if (parseObject.getInt("user_id") == 1) {
                        userIdString = "dGbETKZD8k";
                        parseObject.put("user_id_string", userIdString);
                    }


                    parseObject.saveInBackground();
                }
            }
        });

    }

    public static void parseQuery2() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("reviews");
//        query.whereEqualTo("coffee_machine_id", 3);
        //
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                for (ParseObject parseObject : parseObjects) {
                    if (parseObject.getInt("coffee_machine_id") == 4) {
                        parseObject.put("coffee_machine_id_string", "PZrB82ZWVl");
                        parseObject.saveInBackground();
                    }
                }

            }
        });
    }

    public static void parseQuery3(AssetManager assets) {
        InputStream is = null;

        String profilePicturePath = "pictures/coffee4.png";
        try {
//            is = new FileInputStream(profilePicturePath);
            is = assets.open(profilePicturePath);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "error- " + e.getMessage());
            return ;
        } catch (IOException e) {
            e.printStackTrace();
        }

//        final String fileName = String.valueOf(UUID.randomUUID());
        final String fileName = "coffeeIcon4.png";

        ParseFile file = null;

        try {
            file = new ParseFile(fileName, IOUtils.toByteArray(is));
        } catch (IOException e) {
            Log.e(TAG, "error- " + e.getMessage());
            return;
        }

        final ParseFile finalFile = file;
        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "error- " + e.getMessage());
                    return;
                }
                ParseQuery<ParseObject> query = ParseQuery.getQuery("coffee_machines");
                query.whereEqualTo("objectId", "kFFMaPaytU");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if(parseObjects != null) {
                            ParseObject parseObject = parseObjects.get(0);
                            if(parseObject != null) {
                                parseObject.put("icon_path", finalFile.getUrl());
                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.d(TAG, "hey saved coffee file row with success");
                                    }
                                });
                                Log.d(TAG, "hey upload pic successfully upload");
                            }
                        }
                    }
                });
            }
        }, new ProgressCallback() {
            public void done(Integer percentDone) {
                // Update your progress spinner here. percentDone will be between 0 and 100.
            }
        });
    }


}
