package com.yippie.android.classes;

import android.content.ContentValues;
import android.database.Cursor;

import com.yippie.android.library.SqliteDB;

/**
 * Created by Zi Yang
 *
 */
public class Operation {
    private final static long oneHour = 3600000;
    private final static long oneDay = 86400000;

    public static final int CATEGORY_LIST = 1;

    /**
     * This is a function to retrieve the last update timestamp for certain operation that recorded in SQLite DB
     * @param operationId - The unique operation numeric code that represent the operation name.
     * 						Refer to public static variable in this class.
     * @return last update timestamp or 0 if operation is not registered.
     */
    public static Long getOperationTimestamp(Integer operationId)
    {
        Long lastUpdate = (long) 0;

        SqliteDB db = SqliteDB.getInstance();

        // Construct get user SQL query
        String tableName = SqliteDB.TABLE_TTL_TIMESTAMP;
        String whereQuery = "id=?";
        String whereArgs[] = {operationId.toString()};
        String limit = "1";

        // Retrieve result from database
        Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, null, limit);

        if(cursor.getCount() > 0)
        {
            // If the result is not empty
            while(cursor.moveToNext())
            {
                lastUpdate = cursor.getLong(cursor.getColumnIndex("updated"));
            }

            // Close the cursor
            cursor.close();
        }

        return lastUpdate;
    }

    /**
     * This is a function to check if the operation is outdated and update is require.
     * @param operationCode - The unique operation numeric code that represent the operation name.
     * 						  Refer to public static variable in this class.
     * @return True if update require, otherwise false
     */
    public static Boolean isUpdateRequire(Integer operationCode)
    {
        boolean isUpdateRequired = false;

        // Get last update timestamp for this operation
        long lastUpdateTimestamp = getOperationTimestamp(operationCode);
        // Get current system timestamp
        long currentTimestamp = System.currentTimeMillis();
        // Calculate how long the time has passed since last update
        long timeRange = currentTimestamp - lastUpdateTimestamp;

        switch(operationCode){
            case CATEGORY_LIST:
                isUpdateRequired = timeRange > oneDay;
                break;
        }

        return isUpdateRequired;
    }

    /**
     * This is a function to register the operation with current timestamp.
     * @param operationId - The unique operation numeric code that represent the operation name.
     * 						Refer to public static variable in this class.
     */
    public static void registerOperationTimestamp(Integer operationId)
    {
        long currentTimestamp = System.currentTimeMillis();

        // Define table name
        String tableName = SqliteDB.TABLE_TTL_TIMESTAMP;
        SqliteDB db = SqliteDB.getInstance();

        ContentValues contentValues = new ContentValues();
        contentValues.put("id", operationId);
        contentValues.put("updated", currentTimestamp);

        db.insertOrUpdateDBRow(tableName, contentValues);
    }
}
