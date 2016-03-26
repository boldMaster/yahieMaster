package com.yippie.android.library;

/**
 * This is a class that handle all SQLite Database Operation
 * Important Note:
 * - Always use getReadableDatabase() if the operation involves only read.
 * - Use only getWritableDatabase() if the operation involves read and write
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteDB extends SQLiteOpenHelper
{

    // Static Variables
    private Context context;
    private SQLiteDatabase db;
    private static SqliteDB INSTANCE;
    private static final int DATABASE_VERSION = 1;			// Database Version
    private static final String DATABASE_NAME = "Yippie"; 	// Database Name
    private int openedConnection = 0;                       // This variable will used to indicated how many connected is currently opened
    public static final String TABLE_USER = "yippe_user";           // Table that store the register user
    public static final String TABLE_LOGIN = "yippie_user_login";   // Table that store the user login information
    public static final String TABLE_CATEGORY_GROUP = "yippie_category_group";  // Table that store the list of category group
    public static final String TABLE_CATEGORY = "yippie_category";              // Table that store the list of the category
    public static final String TABLE_SUB_CATEGORY = "yippie_sub_category";      // Table that store the list of the sub category
    public static final String TABLE_TTL_TIMESTAMP = "yippie_ttl_timestamp";    // Table that store the last update timestamp of each view
    public static final String TABLE_EVENT = "events"; // Table use to store event info/details
    public static final String TABLE_PARTICIPANT_EVENT = "participant_events"; // Table use to store user participated event info/details

    /**
     *  This is a function that ensure all SQL function using the same singleton SqliteDB instance.
     */
    public static SqliteDB getInstance()
    {
        // Return singleton SqliteDB instance.
        return Singleton.getInstance().sqliteDB;
    }

    /**
     * Singleton Usage.
     * This will initialize the SqliteDB class.
     * @param context (Context)
     */
    public static SqliteDB initialize(Context context)
    {
        // Use the application context, which will ensure that you don't accidentally leak an Activity's context.
        if (INSTANCE == null)
        {
            INSTANCE = new SqliteDB(context.getApplicationContext());
        }

        return INSTANCE;
    }

    // Constructor
    private SqliteDB(Context contxt)
    {
        super(contxt, DATABASE_NAME, null, DATABASE_VERSION);
        context = contxt;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Construct SQLite tables and install
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + "_id INTEGER PRIMARY KEY,"
                + "uid INTEGER ,"
                + "userName TEXT, "
                + "email TEXT, "
                + "password TEXT, "
                + "accessKey TEXT, "
                + "created INTEGER, "
                + "updated INTEGER"
                +")";

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + "_id INTEGER PRIMARY KEY,"
                + "uid INTEGER ,"
                + "userName TEXT, "
                + "email TEXT, "
                + "password TEXT, "
                + "accessKey TEXT, "
                + "created INTEGER"
                +")";

        String CREATE_CATEGORY_GROUP_TABLE = "CREATE TABLE " + TABLE_CATEGORY_GROUP + "("
                + "category_group_id INTEGER PRIMARY KEY,"
                + "category_group_name TEXT"
                +")";

        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + "category_id INTEGER PRIMARY KEY,"
                + "category_group_id INTEGER ,"
                + "category_name TEXT"
                +")";

        String CREATE_SUB_CATEGORY_TABLE = "CREATE TABLE " + TABLE_SUB_CATEGORY + "("
                + "sub_category_id INTEGER PRIMARY KEY,"
                + "category_id INTEGER ,"
                + "category_group_id INTEGER ,"
                + "sub_category_name TEXT"
                +")";

        String CREATE_TTL_TIMESTAMP_TABLE = "CREATE TABLE " + TABLE_TTL_TIMESTAMP + "("
                + "id INTEGER PRIMARY KEY,"
                + "updated INTEGER"
                + ")";

        String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + "event_id INTEGER PRIMARY KEY,"
                + "place_id INTEGER ,"
                + "advertiser_id INTEGER, "
                + "event_title TEXT, "
                + "event_desc TEXT, "
                + "total_amount INTEGER, "
                + "total_winner INTEGER, "
                + "amount_per_voucher INTEGER, "
                + "start_date timestamp, "
                + "end_date timestamp,"
                + "redeem_duration INTEGER,"
                + "updated_at timestamp "
                +")";

        String CREATE_PARTICIPANT_EVENT_TABLE = "CREATE TABLE " + TABLE_PARTICIPANT_EVENT + "("
                + "event_id INTEGER PRIMARY KEY,"
                + "place_id INTEGER ,"
                + "advertiser_id INTEGER, "
                + "event_title TEXT, "
                + "event_desc TEXT, "
                + "total_amount INTEGER, "
                + "total_winner INTEGER, "
                + "amount_per_voucher INTEGER, "
                + "start_date timestamp, "
                + "end_date timestamp,"
                + "redeem_duration INTEGER,"
                + "updated_at timestamp "
                +")";

        // Install tables
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_CATEGORY_GROUP_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_SUB_CATEGORY_TABLE);
        db.execSQL(CREATE_TTL_TIMESTAMP_TABLE);
        db.execSQL(CREATE_EVENT_TABLE);
		db.execSQL(TABLE_PARTICIPANT_EVENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUB_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TTL_TIMESTAMP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANT_EVENT);

        // Create tables again
        onCreate(db);
    }

    /**
     * Note: custom function to get readable sqlite database. Please use this function instead of using this.getReadableDatabase()
     */
    private SQLiteDatabase getReadableDB()
    {
        // Increase openedConnection by 1
        openedConnection++;
        // Get readable database instead of writable
        SQLiteDatabase sqliteDB = this.getReadableDatabase();

        if(!sqliteDB.isOpen())
        {
            // If the database connection was closed unexpectedly, we have to open it again.
            SqliteDB database = new SqliteDB(context);
            sqliteDB = database.getReadableDatabase();
        }

        return sqliteDB;
    }

    /**
     * Note: custom function to get writable sqlite database. Please use this function instead of using this.getWritableDatabase()
     */
    private SQLiteDatabase getWritableDB()
    {
        // Increase openedConnection by 1
        openedConnection++;
        // Get writable database instead of readable
        SQLiteDatabase sqliteDB = this.getWritableDatabase();

        if(!sqliteDB.isOpen())
        {
            // If the database connection was closed unexpectedly, we have to open it again.
            SqliteDB database = new SqliteDB(context);
            sqliteDB = database.getWritableDatabase();
        }

        return sqliteDB;
    }

    private void closeDB()
    {
        // Decrement the openedConnection
        // The main purpose for doing this is to prevent the database close by other activity in halfway.
        openedConnection--;

        if(openedConnection <= 0)
        {
            this.close();
        }
    }

    /**
     * This is a SQLite Database function that execute the given raw SQL Query.
     * Note: Avoid using this function unless you are facing difficulty to use getRow function, like OffSet etc.
     * @param query (String) - The sql query to execute.
     * @param queryArgs (String[]) - You may include ?s in query, which will be replaced by the values from queryArgs,
     * 								 in order that they appear in the selection. The values will be bound as Strings.
     * @return Cursor with SQL data.
     */
    public Cursor rawQuery(String query, String[] queryArgs)
    {
        // Get readable database instead of writable
        db = this.getReadableDB();

        return db.rawQuery(query, queryArgs);
    }

    /**
     * This is a SQLite Database function that retrieve the SQLite result
     *
     * @param tableName 	- The table name to compile the query against.
     * @param columns 		- A list of which columns to return. Passing null will return all columns.
     * @param selection 	- A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself).
     * 							Passing null will return all rows for the given table.
     * @param selectionArgs - You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection.
     * 							The values will be bound as Strings.
     * @param groupBy 		- A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself).
     * 							Passing null will cause the rows to not be grouped.
     * @param having 		- A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself).
     * 							Passing null will cause all row groups to be included, and is required when row grouping is not being used.
     * @param orderBy 		- How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself).
     * 							Passing null will use the default sort order, which may be unordered.
     * @param limit 		- Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     */
    public Cursor getRow(String tableName, String[]columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
    {
        // Get readable database instead of writable
        db = this.getReadableDB();

        return db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * This is a SQLite Database function that insert a new Rows to specific table.
     * Instruction
     * 1. Create a HashMap with <String,Object> type to store list of Key and Value to be insert.
     * 2. Specific the table name.
     * 3. This function will return true if operation is success, fail will get false in return.
     * @param tableName (String) - The name of the database table.
     * @param values (ContentValues) - A collection of key and value to be insert as new row to a table.
     */
    public Boolean insertDBRow(String tableName, ContentValues values)
    {
        // For insert/update, we wil need to get a writable database
        db = this.getWritableDB();

        try
        {
            // Start transaction
            db.beginTransaction();

            // Inserting Row
            db.insert(tableName, null, values);

            // Set transaction success
            db.setTransactionSuccessful();

        }
        catch(Exception e)
        {
            e.printStackTrace();

            return false;
        }
        finally
        {
            // End the transaction
            db.endTransaction();
        }

        this.closeDB();

        return true;
    }

    /**
     * This is a SQLite Database function that insert a new Rows OR update to specific table, if the primary key is already exists.
     * Instruction
     * 1. Create a HashMap with <String,Object> type to store list of Key and Value to be insert.
     * 2. Specific the table name.
     * 3. This function will return true if operation is success, fail will get false in return.
     * @param tableName (String) - The name of the database table.
     * @param values (ContentValues) - A collection of key and value to be insert as new row to a table.
     */
    public Boolean insertOrUpdateDBRow(String tableName, ContentValues values)
    {
        // For insert/update, we wil need to get a writable database
        db = this.getWritableDB();

        try
        {
            // Start transaction
            db.beginTransaction();

            // Inserting Row OR Update Row if conflict
            db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);

            // Set transaction success
            db.setTransactionSuccessful();

        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            // End the transaction
            db.endTransaction();
        }

        this.closeDB();

        return true;
    }

    /**
     * This is a SQLite Database function that update the tables in SQlite Database
     * @param tableName (String)  - The name of the table.
     * @param values (ContentValues) - A collection of key and value to be insert as new row to a table.
     * @param where (String)- A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself).
     *              Passing null will return all rows for the given table.
     * @param whereArgs (String[]) - You may include ?s in selection, which will be replaced by the values from whereArgs, in order that they appear in the selection.
     *                  The values will be bound as Strings.
     */
    public Boolean updateDBRow(String tableName, ContentValues values, String where, String[] whereArgs)
    {
        // For insert/update, we wil need to get a writable database
        db = this.getWritableDB();

        try
        {
            // Start transaction
            db.beginTransaction();

            // update
            db.update(tableName, values, where, whereArgs);

            // Set transaction success
            db.setTransactionSuccessful();

        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }
        finally
        {
            // Close the transaction
            db.endTransaction();
        }

        this.closeDB();

        return true;
    }

    /**
     * This is a SQLite Function that perform a conditional bulk insert or update.
     * Note: THe difference between this function with bulkInsertOrUpdateDBRows is that this function perform a check before insert/update.
     * 		 THis function should only be use if there are at least 2 condition to check before insert/update.
     * @param tableName (String) - The name of the table
     * @param singleInsertValues (ContentValues) - This is a list of collection of key and value to be insert as new rows to a table.
     *
     */
    public Boolean insertOrUpdateDBRowsByCondition(String tableName, ContentValues singleInsertValues, String[] updateCondition)
    {
        try
        {
            // For insert/update, we wil need to get a writable database
            db = this.getWritableDB();

            try
            {
                // Start transaction
                db.beginTransaction();

                // Construct where and whereArgs
                ArrayList<String> list = new ArrayList<>();
                String query = "SELECT * FROM " + tableName;
                String where = "";
                Integer conditionSize = updateCondition.length;
                Integer count = 1;
                for(String key : updateCondition)
                {
                    String value = singleInsertValues.get(key).toString();
                    list.add(value);
                    where = where + key + "=?";
                    where = (count < conditionSize) ? where + " AND " : where;

                    count++;
                }

                // We only combine the query if it is not null.
                if(!where.equals(""))
                {
                    query = query + " WHERE " + where;
                }

                String[] whereArgs = new String[list.size()];
                whereArgs = list.toArray(whereArgs);

                // Inserting Row or Update Rows
                try
                {
                    Cursor cursor = db.rawQuery(query, whereArgs);

                    if(cursor.getCount() > 0)
                    {
                        // Record exists, perform update
                        db.update(tableName, singleInsertValues, where, whereArgs);
                    }
                    else
                    {
                        // Record not exists, perform insert
                        db.insert(tableName, null, singleInsertValues);
                    }

                    // Close sursor
                    cursor.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                // Set transaction success
                db.setTransactionSuccessful();

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                // Close the transaction
                db.endTransaction();
            }

            this.closeDB();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * This is a SQLite Database function that delete rows in the specific table
     * @param tableName (String) - The table to delete from
     * @param where (String) - The optional WHERE clause to apply when deleting. Passing null will delete all rows.
     * @param whereArgs (String) - You may include ?s in the where clause, which will be replaced by the values from whereArgs. The values will be bound as Strings.
     */
    public Boolean deleteDBRow(String tableName, String where, String[] whereArgs)
    {
        // For insert/update, we wil need to get a writable database
        db = this.getWritableDB();

        try
        {
            // Start transaction
            db.beginTransaction();

            // Perform delete operation
            db.delete(tableName, where, whereArgs);

            // Set transaction success
            db.setTransactionSuccessful();
        }
        catch(Exception e)
        {
            return false;
        }
        finally
        {
            // Close the transaction
            db.endTransaction();
        }

        this.closeDB();

        return true;
    }

    /**
     * This is a SQLite Function that perform bulk insert.
     * Note: Please use this function if you happen to insert than 2 rows in a single action.
     * @param tableName (String) - The name of the table
     * @param bulkValues (List<ContentValues>) - This is a list of collection of key and value to be insert as new rows to a table.
     */
    public Boolean bulkInsertDBRows(String tableName, List<ContentValues> bulkValues)
    {
        // For insert/update, we wil need to get a writable database
        db = this.getWritableDB();

        try
        {
            // Start transaction
            db.beginTransaction();

            // Bulk Insert
            for(ContentValues singleInsertValues : bulkValues)
            {
                // Inserting Row
                db.insert(tableName, null, singleInsertValues);
            }

            // Set transaction success
            db.setTransactionSuccessful();

        }
        catch(Exception e)
        {
            e.printStackTrace();

            return false;
        }
        finally
        {
            // Close the transaction
            db.endTransaction();
        }

        this.closeDB();

        return true;
    }

    /**
     * This is a SQLite Function that perform bulk insert or update.
     * Note: DO NOT use this function if Primary Key is auto Increment Based.
     * Note: This function will perform the following action:
     * 		 1. If there is no such record in database, insert new record.
     * 		 2. If there is already a record with same primary key, replace old record with new values.
     * Note: Please use this function if you happen to insert than 2 rows in a single action.
     * Note: This will replace the existing record, the field will be replace with null if the require field is not presented.
     *
     * @param tableName (String) - The name of the table
     * @param bulkValues (List<ContentValues>)- This is a list of collection of key and value to be insert as new rows to a table.
     */
    public Boolean bulkInsertOrUpdateDBRows(String tableName, List<ContentValues> bulkValues)
    {
        // For insert/update, we wil need to get a writable database
        db = this.getWritableDB();

        try
        {
            // Start transaction
            db.beginTransaction();

            // Bulk Insert
            for(ContentValues singleInsertValues : bulkValues)
            {
                // Inserting Row or Update Rows
                try
                {
                    // Using insertWithOnConflict in this case, which will insert the record that not exists in table.
                    db.insertWithOnConflict(tableName, null, singleInsertValues, SQLiteDatabase.CONFLICT_REPLACE);
                }
                catch(Exception e)
                {
                    // Enter here if SQLite throw an exception, which mean current values is already in the database
                    // Perform replace operation
                    db.replace(tableName, null, singleInsertValues);
                }
            }

            // Set transaction success
            db.setTransactionSuccessful();

        }
        catch(Exception e)
        {
            e.printStackTrace();

            return false;
        }
        finally
        {
            // Lastly, close the transaction
            db.endTransaction();
        }

        this.closeDB();

        return true;
    }

    /**
     * This is a SQLite Function that perform a conditional bulk insert or update.
     * Note: The difference between this function with bulkInsertOrUpdateDBRows is that this function perform a check before insert/update.
     * 		 This function should only be use if there are at least 2 condition to check before insert/update.
     * @param tableName (String) - The name of the table
     * @param bulkValues (List<ContentValues>) - This is a list of collection of key and value to be insert as new rows to a table.
     * @param updateCondition (String[]) - This is the String array that store the update condition.
     */
    public Boolean bulkInsertOrUpdateDBRowsByCondition(String tableName, List<ContentValues> bulkValues, String[] updateCondition)
    {

        try{
            // For insert/update, we wil need to get a writable database
            db = this.getWritableDB();

            try
            {
                // Start transaction
                db.beginTransaction();

                // Bulk Insert
                for(ContentValues singleInsertValues : bulkValues)
                {
                    // Construct where and whereArgs
                    ArrayList<String> list = new ArrayList<>();
                    String query = "SELECT * FROM " + tableName;
                    String where = "";
                    Integer conditionSize = updateCondition.length;
                    Integer count = 1;
                    for(String key : updateCondition)
                    {
                        String value = singleInsertValues.get(key).toString();
                        list.add(value);
                        where = where + key + "=?";
                        where = (count < conditionSize) ? where + " AND " : where;

                        count++;
                    }

                    // We only combine the query if it is not null.
                    if(!where.equals(""))
                    {
                        query = query + " WHERE " + where;
                    }

                    String[] whereArgs = new String[list.size()];
                    whereArgs = list.toArray(whereArgs);

                    // Inserting Row or Update Rows
                    try
                    {
                        Cursor cursor = db.rawQuery(query, whereArgs);

                        if(cursor.getCount() > 0)
                        {
                            // Record exists, perform update
                            db.update(tableName, singleInsertValues, where, whereArgs);
                        }
                        else
                        {
                            // Record not exists, perform insert
                            db.insert(tableName, null, singleInsertValues);
                        }

                        // Close sursor
                        cursor.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }

                // Set transaction success
                db.setTransactionSuccessful();

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                // Close the transaction
                db.endTransaction();
            }

            this.closeDB();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * This is a SQLite Database function that clear all rows in the specify table.
     * @param tableName (String) - The target table name
     */
    public void clearRows(String tableName)
    {
        db = this.getWritableDB();
        // Delete All Rows
        db.delete(tableName, null, null);
        this.closeDB();
    }

}