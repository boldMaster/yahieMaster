package com.yippie.android.classes;

import android.content.ContentValues;
import android.database.Cursor;

import com.facebook.AccessToken;
import com.yippie.android.library.SqliteDB;
import com.yippie.android.library.Utility;

/**
 * User class
 */

public class User
{
    private Integer uid;
    private Integer fid;
    private String name;
    private String email;
    private String passwordEncoded;
    private String accessKey;
    private Long created;
    private Long updated;

    // Constructor
    public User()
    {
        this.uid = 0;
        this.name = "";
        this.email = "";
        this.passwordEncoded = "";
        this.accessKey = "";
        this.created = (long) 0;
        this.updated = (long) 0;
    }

    /**
     * Constructor #2
     */
    public User(Integer uid, String name, String email, String accessKey, Long created, Long updated)
    {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.accessKey = accessKey;
        this.created = created;
        this.updated = updated;
    }

    /**
     * Facebook User Constructor
     */
    public User(Integer fid, String name, String email)
    {
        this.fid = fid;
        this.name = name;
        this.email = email;
    }

    // Getter
    public Integer getUid() {
        return uid;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPasswordEncoded(){
        return passwordEncoded;
    }

    // Setter
    public void setUid(Integer id){
        uid = id;
    }

    public void setName(String N){
        name = N;
    }

    public void setEmail(String E){
        email = E;
    }

    public void setPasswordEncoded(String PE){
        passwordEncoded = PE;
    }

    /**
     * Function to register a new user to database
     * @param email     - The email that user entered during registration
     * @param userName  - The username that user entered during registration
     * @param password  - The password that user entered during registration
     * @return The status of registration
     */
    public static Boolean registerUser(String email, String userName, String password ,AccessToken objAccessToken)
    {
        //     Boolean blnExist = isUserEmailExists(email);
        //    if(blnExist && objAccessToken!=null) {
        //       loginUser(email, userName, password, objAccessToken);
        //      return false;
        // }
        // Define table name
        String tableName = SqliteDB.TABLE_USER;
        SqliteDB db = SqliteDB.getInstance();
        // Current timestamp
        Long timestamp = Utility.getCurrentTimestamp();

        // Construct the Content Values
        ContentValues insertInfo = new ContentValues();
        insertInfo.put("userName", userName);
        insertInfo.put("email", email);
        insertInfo.put("password", password);
        insertInfo.put("created", timestamp);
        insertInfo.put("updated", timestamp);
        if(objAccessToken != null)
            insertInfo.put("accessKey",objAccessToken.toString());

        return db.insertDBRow(tableName,insertInfo);
    }

    /**
     * Function to login a user
     * @param email     - The email that user entered during registration
     * @param userName  - The username that user entered during registration
     * @param password  - The password that user entered during registration
     * @return The status of registration
     */
    public static Boolean loginUser(String email, String userName, String password)
    {
        // To login a user, we have to make sure the there is no user in login table
        // Perform a force logout
        logoutUser();

        // Define table name
        String tableName = SqliteDB.TABLE_LOGIN;
        SqliteDB db = SqliteDB.getInstance();

        // Current timestamp
        Long timestamp = Utility.getCurrentTimestamp();

        // Construct the Content Values
        ContentValues insertInfo = new ContentValues();
        insertInfo.put("userName", userName);
        insertInfo.put("email", email);
        insertInfo.put("password", password);
        insertInfo.put("created", timestamp);

        return db.insertDBRow(tableName,insertInfo);
    }

    /**
     * Function to sign out a user.
     * Note: The method used in sign out user is reset the columns in user login tables.
     */
    public static void logoutUser()
    {
        // Define table name
        String tableName = SqliteDB.TABLE_LOGIN;
        SqliteDB db = SqliteDB.getInstance();
        db.clearRows(tableName);
    }

    /**
     * Function to get current logged in user information
     */
    public static User getLoginUser()
    {
        User currentUser = null;

        // Define table name
        String tableName = SqliteDB.TABLE_LOGIN;
        // Get Sqlite DB instance
        SqliteDB db = SqliteDB.getInstance();
        // Set the data retrieve limit to max 1
        String limit = "1";

        // Retrieve result from database
        Cursor cursor = db.getRow(tableName, null, null, null, null, null, null, limit);

        if(cursor.getCount()>0)
        {
            // If the result is not empty
            while(cursor.moveToNext())
            {
                Integer uid = cursor.getInt(cursor.getColumnIndex("uid"));
                String mail = cursor.getString(cursor.getColumnIndex("email"));
                String uName = cursor.getString(cursor.getColumnIndex("userName"));
                String accessKey = cursor.getString(cursor.getColumnIndex("accessKey"));
                Long created = cursor.getLong(cursor.getColumnIndex("created"));

                currentUser = new User(uid,mail,uName,accessKey,created,null);
            }

            // Close cursor
            cursor.close();
        }

        return currentUser;
    }

    /**
     * Function to check if the user is already exists
     */
    public static Boolean isUserEmailExists(String email)
    {
        Boolean isExists = false;
        // Define table name
        String tableName = SqliteDB.TABLE_USER;
        // Get Sqlite DB instance
        SqliteDB db = SqliteDB.getInstance();
        // Set the data retrieve limit to max 1
        String limit = "1";

        // We will try to get email and password combination
        String whereQuery = "email=?";
        String whereArgs[] = {email};

        // Retrieve result from database
        Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, null, limit);

        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                isExists = true;
            }
        }

        // Close cursor
        cursor.close();

        return isExists;
    }

    /**
     * Function to check if the user is exists in database
     * @param
     */
    public static User getUser(String email, String username, String password)
    {
        User currentUser = null;

        // Define table name
        String tableName = SqliteDB.TABLE_USER;
        // Get Sqlite DB instance
        SqliteDB db = SqliteDB.getInstance();
        // Set the data retrieve limit to max 1
        String limit = "1";

        // We will try to get email and password combination
        String whereQuery = "email=? AND password=?";
        String whereArgs[] = {email, password};

        // Retrieve result from database
        Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, null, limit);

        if(cursor.getCount()<=0)
        {
            // Enter here if the result is empty, means the email and password combination are wrong
            // Try username and password comnbination

            // We will try to get email and password combination
            whereQuery = "username=? AND password=?";
            String _whereArgs[] = {username,password};

            // Retrieve result from database
            cursor = db.getRow(tableName, null, whereQuery, _whereArgs, null, null, null, limit);
        }

        if(cursor.getCount()>0)
        {
            // If the result is not empty
            while(cursor.moveToNext())
            {
                Integer uid = cursor.getInt(cursor.getColumnIndex("uid"));
                String mail = cursor.getString(cursor.getColumnIndex("email"));
                String uName = cursor.getString(cursor.getColumnIndex("userName"));
                String accessKey = cursor.getString(cursor.getColumnIndex("accessKey"));
                Long created = cursor.getLong(cursor.getColumnIndex("created"));
                Long updated = cursor.getLong(cursor.getColumnIndex("updated"));

                currentUser = new User(uid,mail,uName,accessKey,created,updated);
            }

            // Close cursor
            cursor.close();
        }

        return currentUser;
    }
}
