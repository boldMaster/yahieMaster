/*
 * This is a singleton class that hold global variables for all activities.
 * Note: This class should only called by Yippie.java (Application File)
 */
package com.yippie.android.library;

import android.content.Context;

public class Singleton
{
    private static Singleton instance;
    public SqliteDB sqliteDB;
    public Criteria criteria;

    public static void initInstance(Context context)
    {
        if (instance == null)
        {
            // Create the instance
            instance = new Singleton(context);
        }
    }

    public static Singleton getInstance()
    {
        // Return the instance
        return instance;
    }

    // Single Constructor MUST be PRIVATE
    private Singleton(Context context)
    {
        // Initialize SQLite
        sqliteDB = SqliteDB.initialize(context);
    }
}