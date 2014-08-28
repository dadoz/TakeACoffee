package com.application.datastorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.application.commons.Common;
import com.application.models.CoffeeMachine;
import com.application.models.Review;
import com.application.models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataRequestDb {
    private String TAG = "DataRequestDb";
    private final CoffeeMachineDbHelper coffeeMachineDbHelper;
    private SQLiteDatabase db;
    private Context context;
    private String[] coffeeMachineDbFields = {
            CoffeeMachineDbHelper.COFFEE_MACHINE_ID_KEY,
            CoffeeMachineDbHelper.NAME_KEY,
            CoffeeMachineDbHelper.ADDRESS_KEY,
            CoffeeMachineDbHelper.ICON_PATH_KEY,
    };
    private String[] reviewDbFields = {
            CoffeeMachineDbHelper.REVIEW_ID_KEY,
            CoffeeMachineDbHelper.REVIEW_COMMENT_KEY,
            CoffeeMachineDbHelper.REVIEW_STATUS_KEY,
            CoffeeMachineDbHelper.REVIEW_TIMESTAMP_KEY,
            CoffeeMachineDbHelper.REVIEW_USER_FOREIGN_KEY,
            CoffeeMachineDbHelper.REVIEW_COFFEE_MACHINE_FOREIGN_KEY
    };
    private String[] userDbFields = {
            CoffeeMachineDbHelper.USER_ID_KEY,
            CoffeeMachineDbHelper.USERNAME_KEY,
            CoffeeMachineDbHelper.PROFILE_PICTURE_PATH_KEY
    };

    public DataRequestDb(Context context) {
        this.context = context;
        coffeeMachineDbHelper = new CoffeeMachineDbHelper(context);

//        openCoffeeMachineDb();
//        isTableExists(db, CoffeeMachineDbHelper.COFFEE_MACHINE_TABLE_NAME);
    }

    public ArrayList<CoffeeMachine> getCoffeeMachineList() {
        return getCoffeeMachineListOnDb();
    }

    public void addAllCoffeeMachine(ArrayList<CoffeeMachine> coffeeMachineList) {
        for(CoffeeMachine coffeeMachine : coffeeMachineList) {
            addCoffeeMachineOnDb(coffeeMachine.getName(),
                    coffeeMachine.getAddress(), coffeeMachine.getIconPath());
        }
    }

    public boolean addAllReview(ArrayList<Review> reviewsList) {
        for(Review review : reviewsList) {
            addReviewByParamsOnDb(review.getId(), review.getUserId(), review.getCoffeeMachineId(), review.getComment(),
                    review.getStatus().name(), review.getTimestamp());
        }
        return true;
    }

    public boolean cleanAllReview() {
        return deleteAllReviewOnDb();
    }

    public boolean removeAllReviewByCoffeeMachineId(long coffeeMachineId) {
        return deleteAllReviewByCoffeeMachineIdOnDb(coffeeMachineId);

    }

    public boolean removeReviewById(long reviewId) {
        return deleteReviewByIdOnDb(reviewId);
    }

    public ArrayList<Review> getReviewList() {
        return getReviewListOnDb();
    }

    public ArrayList<Review> getReviewListById(long coffeeMachineId) {
        return getReviewListByCoffeeMachineIdOnDb(coffeeMachineId);
    }

    public Review addReviewByParams(long id, long userId, long coffeeMachineId, String comment, Common.ReviewStatusEnum status, long timestamp) {
        return addReviewByParamsOnDb(id, userId, coffeeMachineId, comment, status.name(), timestamp);
    }

    public void addUserByParams(long id, String profilePicturePath, String username) {
        addUserOnDb(id, profilePicturePath, username);
    }

    public User getUserById(long userId) {
        return getUserByIdOnDb(userId);
    }

    public boolean updateUserById(long userId, String profilePicturePath, String username) {
        return updateUserByIdOnDb(userId, profilePicturePath, username);
    }
    public Review getReviewById(long reviewId) {
        return getReviewByIdOnDb(reviewId);
    }

/*    private static String getCoffeeMachineListDbRequest() {
        return null;
    }

*/

    private void openCoffeeMachineDb() throws SQLException {
        db = coffeeMachineDbHelper.getWritableDatabase();
    }

    private void closeCoffeeMachineDb() throws SQLException {
        coffeeMachineDbHelper.close();
    }


    /**
     * COFFEE_MACHINE table - add. get. getById
     * **/
    private CoffeeMachine addCoffeeMachineOnDb(String name, String address, String iconPath){
        long rowId;
        openCoffeeMachineDb();

        ContentValues values = new ContentValues();
        values.put(CoffeeMachineDbHelper.NAME_KEY, name);
        values.put(CoffeeMachineDbHelper.ADDRESS_KEY, address);
        values.put(CoffeeMachineDbHelper.ICON_PATH_KEY, iconPath);
        rowId = db.insert(CoffeeMachineDbHelper.COFFEE_MACHINE_TABLE_NAME, null, values);

        closeCoffeeMachineDb();
        if(rowId == -1) {
            Log.e(TAG, "An error occurred while adding the document to the local DB");
            return null;
        }
        CoffeeMachine coffeeMachine = new CoffeeMachine(rowId, name, address, iconPath);
        Log.d(TAG, coffeeMachine.getId() + " coffee machine correctly stored");
        return coffeeMachine;
    }

    private ArrayList<CoffeeMachine> getCoffeeMachineListOnDb() {
        ArrayList<CoffeeMachine> coffeeMachineArrayList = new ArrayList<CoffeeMachine>();

        openCoffeeMachineDb();
        Cursor cursor = db.query(CoffeeMachineDbHelper.COFFEE_MACHINE_TABLE_NAME, coffeeMachineDbFields,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CoffeeMachine coffeeMachine = new CoffeeMachine(
                    cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3));
            coffeeMachineArrayList.add(coffeeMachine);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        closeCoffeeMachineDb();
        if(coffeeMachineArrayList.size() == 0) {
            return null;
        }
        return coffeeMachineArrayList;
    }


    /**
     * REVIEW table - add. get. getById
     * **/
//    private Review addReviewOnDb(String comment, String statusName, String timestamp, int coffeeMachineId, int userId){
      private Review addReviewByParamsOnDb(long id, long userId, long coffeeMachineId, String comment,
                                            String statusName, long timestamp) {
        long rowId;
        openCoffeeMachineDb();

        ContentValues values = new ContentValues();
          values.put(CoffeeMachineDbHelper.REVIEW_ID_KEY, id);
        values.put(CoffeeMachineDbHelper.REVIEW_USER_FOREIGN_KEY, userId);
        values.put(CoffeeMachineDbHelper.REVIEW_COFFEE_MACHINE_FOREIGN_KEY, coffeeMachineId);
        values.put(CoffeeMachineDbHelper.REVIEW_COMMENT_KEY, comment);
        values.put(CoffeeMachineDbHelper.REVIEW_STATUS_KEY, statusName);
        values.put(CoffeeMachineDbHelper.REVIEW_TIMESTAMP_KEY, timestamp);
        rowId = db.insert(CoffeeMachineDbHelper.REVIEW_TABLE_NAME, null, values);

        if(rowId == -1) {
            Log.e(TAG , "An error occurred while adding the document to the local DB");
            return null;
        } else {
            Log.d(TAG, rowId + " review correctly stored");
        }
        // id == rowId
        Review review = new Review(id,
                comment, Review.parseStatus(statusName),
                timestamp, userId, -1);
        closeCoffeeMachineDb();
        return review;
    }

    private ArrayList<Review> getReviewListOnDb() {
        ArrayList<Review> reviewArrayList = new ArrayList<Review>();

        openCoffeeMachineDb();
        Cursor cursor = db.query(CoffeeMachineDbHelper.REVIEW_TABLE_NAME, reviewDbFields,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Review review = new Review(
                    cursor.getLong(0),
                    cursor.getString(1),
                    Review.parseStatus(cursor.getString(2)),
                    cursor.getLong(3),
                    cursor.getLong(4),
                    cursor.getLong(5));
            reviewArrayList.add(review);
//            Log.e(TAG, "review - " + review.toString());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        closeCoffeeMachineDb();
        return reviewArrayList;
    }

    private ArrayList<Review> getReviewListByCoffeeMachineIdOnDb(long coffeeMachineId) {
        ArrayList<Review> reviewArrayList = new ArrayList<Review>();

        openCoffeeMachineDb();
        Cursor cursor = db.query(CoffeeMachineDbHelper.REVIEW_TABLE_NAME, reviewDbFields,
                CoffeeMachineDbHelper.REVIEW_COFFEE_MACHINE_FOREIGN_KEY + "=" + coffeeMachineId + "", null, null, null, null, null);
        if(cursor.getCount() == 0) {
            Log.d(TAG, "No review for this machine: " + coffeeMachineId);
            cursor.close();
            closeCoffeeMachineDb();
            return null;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Review review = new Review(
                    cursor.getLong(0),
                    cursor.getString(1),
                    Review.parseStatus(cursor.getString(2)),
                    cursor.getLong(3),
                    cursor.getLong(4),
                    cursor.getLong(5));
            reviewArrayList.add(review);
            Log.e(TAG, review.toString());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        closeCoffeeMachineDb();
        return reviewArrayList;
    }

    private Review getReviewByIdOnDb(long reviewId) {

        openCoffeeMachineDb();
        Cursor cursor = db.query(CoffeeMachineDbHelper.REVIEW_TABLE_NAME, reviewDbFields,
                CoffeeMachineDbHelper.REVIEW_ID_KEY + "='" + reviewId + "'", null, null, null, null, null);
        if(cursor.getCount() == 0) {
            Log.d(TAG, "couldn't find on local DB review with id:" + reviewId);
            closeCoffeeMachineDb();
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        Review review = new Review(
                cursor.getLong(0),
                cursor.getString(1),
                Review.parseStatus(cursor.getString(2)),
                cursor.getLong(3),
                cursor.getLong(4),
                cursor.getLong(5));
        cursor.close();
        closeCoffeeMachineDb();
        return review;
    }

    private boolean deleteReviewByIdOnDb(long reviewId){

        openCoffeeMachineDb();
        String delete = "DELETE FROM " + CoffeeMachineDbHelper.REVIEW_TABLE_NAME +
                " WHERE " + CoffeeMachineDbHelper.REVIEW_ID_KEY + "=" + reviewId;
        db.execSQL(delete);

        closeCoffeeMachineDb();
        return true;
    }

    private boolean deleteAllReviewOnDb() {
        openCoffeeMachineDb();

        String delete = "DELETE FROM " + CoffeeMachineDbHelper.REVIEW_TABLE_NAME;
        db.execSQL(delete);
//        rowId = db.delete(CoffeeMachineDbHelper.REVIEW_TABLE_NAME, null, null);

        Log.d(TAG, "Review table - delete all with success");

        closeCoffeeMachineDb();
        return true;
    }

    private boolean deleteAllReviewByCoffeeMachineIdOnDb(long coffeeMachineId) {
        openCoffeeMachineDb();

        String delete = "DELETE FROM " + CoffeeMachineDbHelper.REVIEW_TABLE_NAME +
                " WHERE " + CoffeeMachineDbHelper.REVIEW_COFFEE_MACHINE_FOREIGN_KEY + "=" + coffeeMachineId;
        db.execSQL(delete);
//        rowId = db.delete(CoffeeMachineDbHelper.REVIEW_TABLE_NAME, null, null);

        Log.d(TAG, "Review table - delete all with success");

        closeCoffeeMachineDb();
        return true;

    }

    private boolean updateReviewByIdOnDb(long reviewId, String comment) {
        openCoffeeMachineDb();

        String strFilter = "_id=" + reviewId;
        ContentValues args = new ContentValues();
        args.put(CoffeeMachineDbHelper.REVIEW_COMMENT_KEY, comment);
        int raw = db.update(CoffeeMachineDbHelper.REVIEW_TABLE_NAME, args, strFilter, null);
        closeCoffeeMachineDb();

        return raw != -1;
    }


    /**
     * COFFEE_MACHINE table - add. get. getById
     * **/
    private User addUserOnDb(long id, String profilePicturePath, String username){
        long rowId;
        openCoffeeMachineDb();

        ContentValues values = new ContentValues();
        values.put(CoffeeMachineDbHelper.USER_ID_KEY, id);
        values.put(CoffeeMachineDbHelper.USERNAME_KEY, username);
        values.put(CoffeeMachineDbHelper.PROFILE_PICTURE_PATH_KEY, profilePicturePath);
        rowId = db.insert(CoffeeMachineDbHelper.USERS_TABLE_NAME, null, values);

        closeCoffeeMachineDb();
        if(rowId == -1) {
            Log.e(TAG, "An error occurred while adding the document to the local DB");
            return null;
        }

        User user = new User(rowId, username, profilePicturePath);
        Log.d(TAG, user.getId() + " user correctly stored");
        return user;
    }

    private User getUserByIdOnDb(long userId) {
        openCoffeeMachineDb();

        Cursor cursor = db.query(CoffeeMachineDbHelper.USERS_TABLE_NAME, userDbFields,
                CoffeeMachineDbHelper.USER_ID_KEY + "='" + userId + "'", null, null, null, null, null);
        if(cursor.getCount() == 0) {
            Log.e(TAG, "couldn't find on local DB user with id:" + userId);
            cursor.close();
            closeCoffeeMachineDb();
            return null;
        }

        cursor.moveToFirst();
        // make sure to close the cursor
        User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        cursor.close();
        closeCoffeeMachineDb();
        return user;

    }

    private boolean updateUserByIdOnDb(long userId, String profilePicturePath, String username) {
        openCoffeeMachineDb();

        String strFilter = "_id=" + userId;
        ContentValues args = new ContentValues();
        if(username != null) {
            args.put(CoffeeMachineDbHelper.USERNAME_KEY, username);
        }

        if(profilePicturePath != null) {
            args.put(CoffeeMachineDbHelper.PROFILE_PICTURE_PATH_KEY, profilePicturePath);
        }

        int raw = db.update(CoffeeMachineDbHelper.USERS_TABLE_NAME, args, strFilter, null);
        closeCoffeeMachineDb();

        return raw != -1;
    }

    public boolean isTableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
/*        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst()) {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;*/
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = ?", new String[] {"table"});
        if (!cursor.moveToFirst()) {
            Log.e(TAG, " -- EMPTY ");
            return false;
        }
        if (!cursor.isAfterLast()) {
            do {
                String count = cursor.getString(0);
                Log.e(TAG, " -- " + count);
            }
            while (cursor.moveToNext());
        }
        cursor.close();

//        cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        cursor = db.rawQuery("PRAGMA table_info(" + CoffeeMachineDbHelper.COFFEE_MACHINE_TABLE_NAME + ")", null);
        if (!cursor.moveToFirst()) {
            Log.e(TAG, " -- ");
            return false;
        }
        if ( cursor.moveToFirst() ) {
            do {
                Log.e(TAG, " -- " + cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor = db.rawQuery("PRAGMA table_info(" + CoffeeMachineDbHelper.REVIEW_TABLE_NAME + ")", null);
        if (!cursor.moveToFirst()) {
            Log.e(TAG, " -- ");
            return false;
        }
        if ( cursor.moveToFirst() ) {
            do {
                Log.e(TAG, " -- " + cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor = db.rawQuery("PRAGMA table_info(" + CoffeeMachineDbHelper.USERS_TABLE_NAME+ ")", null);
        if (!cursor.moveToFirst()) {
            Log.e(TAG, " -- ");
            return false;
        }
        if ( cursor.moveToFirst() ) {
            do {
                Log.e(TAG, " -- " + cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return true;
    }

    /***
     * DbHelpers
     * **/
    public class CoffeeMachineDbHelper extends SQLiteOpenHelper {
        public static final String COFFEE_MACHINE_TABLE_NAME = "coffee_machines";
        public static final String COFFEE_MACHINE_ID_KEY = "_id";
        public static final String NAME_KEY = "name";
        public static final String ADDRESS_KEY = "address";
        public static final String ICON_PATH_KEY = "icon_path";

        private static final int DATABASE_VERSION = 1;

        // Recent Documents Database creation sql statement
        public static final String COFFEE_MACHINE_DATABASE_CREATE = "create table "
                + COFFEE_MACHINE_TABLE_NAME + "("
                    + COFFEE_MACHINE_ID_KEY + " integer primary key autoincrement, "
                    + NAME_KEY + " text not null, "
                    + ADDRESS_KEY + " text not null, "
                    + ICON_PATH_KEY + " text not null"
                + ");";

        public CoffeeMachineDbHelper(Context context) {
            super(context, Common.DATABASE_NAME, null, DATABASE_VERSION);
        }


        //        public static final String TABLE_MYDOCS = "recentDocumentTable";
        public static final String REVIEW_TABLE_NAME = "reviews";
        public static final String REVIEW_ID_KEY = "_id";
        public static final String REVIEW_COMMENT_KEY = "comment";
        public static final String REVIEW_STATUS_KEY = "status";
        public static final String REVIEW_TIMESTAMP_KEY = "timestamp";
        public static final String REVIEW_USER_FOREIGN_KEY = "user_id";
        public static final String REVIEW_COFFEE_MACHINE_FOREIGN_KEY = "coffee_machine_id";


        // Recent Documents Database creation sql statement
        public static final String REVIEWS_DATABASE_CREATE = "create table "
                + REVIEW_TABLE_NAME + "("
                + REVIEW_ID_KEY + " integer primary key, "
                + REVIEW_COMMENT_KEY + " text null, "
                + REVIEW_STATUS_KEY + " text not null, "
                + REVIEW_TIMESTAMP_KEY + " text not null, "
                + REVIEW_USER_FOREIGN_KEY + " integer not null, "
                + REVIEW_COFFEE_MACHINE_FOREIGN_KEY + " text not null"
                + ");";

        public static final String USERS_TABLE_NAME = "users";
        public static final String USER_ID_KEY = "_id";
        public static final String USERNAME_KEY = "username";
        public static final String PROFILE_PICTURE_PATH_KEY = "profile_picture_path";

        // Recent Documents Database creation sql statement
        public static final String USERS_DATABASE_CREATE = "create table "
                + USERS_TABLE_NAME + "("
                + USER_ID_KEY + " integer primary key, "
                + USERNAME_KEY + " text not null, "
                + PROFILE_PICTURE_PATH_KEY + " text null"
                + ");";


        /*
         * If the DB has already been created this code won't be executed
         */
        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(COFFEE_MACHINE_DATABASE_CREATE);
            database.execSQL(REVIEWS_DATABASE_CREATE);
            database.execSQL(USERS_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(CoffeeMachineDbHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + COFFEE_MACHINE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + REVIEW_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
            onCreate(db);
        }





    }

}
