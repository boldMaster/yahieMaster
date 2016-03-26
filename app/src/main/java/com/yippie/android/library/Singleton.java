/*
 * This is a singleton class that hold global variables for all activities.
 * Note: This class should only called by Yippie.java (Application File)
 */
package com.yippie.android.library;

import android.app.ActivityManager;
import android.content.Context;

import com.yippie.android.classes.CriteriaV2;

import java.io.File;
import java.util.LinkedList;

public class Singleton
{
    private static Singleton instance;
    public SqliteDB sqliteDB;
    public Criteria criteria;
    public CriteriaV2 criteriaV2;
    public LinkedList<Integer> imageCacheReferenceList;
    public MemoryLruImageCache imageCache;
    public DiskLruImageCache imageDiskCache;

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

        // Initialize Image Cache Memory Reference Linked List
        imageCacheReferenceList = new LinkedList<>();

        // Intialize Critiria
        criteriaV2 = new CriteriaV2();

        // Initialize LruCache
        int memoryClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = memoryClass * 1024 * 1024 / 8;
        imageCache = new MemoryLruImageCache(cacheSize);

        // Initialize Disk Cache
        File cacheDir = Utility.getStorageImageCacheDir(context);
        imageDiskCache = DiskLruImageCache.openCache(context, cacheDir, 1024 * 1024 * 20);
    }
}