package com.yippie.android.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.util.HashMap;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Utility
{
    private static final String PUBLIC_ACCESS_KEY = "abc123";

    /**
     * Function to get the public access key that enable us to access web api
     */
    public static String getPublicAccessKey()
    {
        return PUBLIC_ACCESS_KEY;
    }

    public static String getBaseUrl() { return "http://192.168.1.41/";}

    /**
     * This is an utility function that generate MD5 Hash for the passed in string
     * @param preMD5String - THis is a string to be encrypt by MD5 Hash
     */
    public static String MD5(String preMD5String)
    {
        try
        {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(preMD5String.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Function to get current tiemstamp
     * @return current timestamp
     */
    public static Long getCurrentTimestamp()
    {
        return System.currentTimeMillis()/1000;
    }

	/**
     * This is an utility function to return the size of a bitmap.
     * @param bitmap - This is the bitmap we going to obtain its size in byte. (Cannot be null)
     */
    public static Integer getSizeInBytes(Bitmap bitmap)
    {
        // There are better function to use for Kitkat onward
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }

	/**
     * This is an utility function that get the available cache directory.
     * This function will prioritize the External Cache Directory instead of internal storage.
     * If External Storage is not available, then we only get the internal storage cache directory
     * @param context - This is the context of current state of the application/object.
     */
    public static File getStorageImageCacheDir(Context context)
    {
        // Get External Storage Cache Directory
        File cacheDir = getExternalStorageCacheDir(context);

        if(cacheDir == null)
        {
            cacheDir = getInternalStorageCacheDir(context);
        }

        String imageCacheDirPath = cacheDir.toString() + File.separator + "cache_image";

        return new File(imageCacheDirPath);
    }

	/**
     * This is an utility function to get the cache directory of the External Storage
     * @param context - This is the context of current state of the application/object.
     */
    public static File getExternalStorageCacheDir(Context context)
    {
        return context.getExternalCacheDir();
    }

    /**
     * This is an utility function to get the cache directory of the Internal Storage
     * @param context - This is the context of current state of the application/object.
     */
    public static File getInternalStorageCacheDir(Context context)
    {
        return context.getCacheDir();
    }

    /**
     * This is an Utility Function that calculate the percentage of the LruCache's memory usage.
     * Note: Do not use this function if there is no LruCache available.
     * @param cache - This is the LruCache to store the cache of the application.
     */
    public static double getLruCacheMemoryUsagePercent(LruCache<?,?> cache)
    {
        // Get Max Size and Current Size of the LruCache
        double cacheMaxSize = cache.maxSize() / 1024;
        double cacheCurSize = cache.size() / 1024;

        // Calculate the percentage of the usage.
        double percentage = (cacheCurSize / cacheMaxSize) * 100;

        Log.i("Max Size  ", String.valueOf(cacheMaxSize));
        Log.i("Cur Size  ", String.valueOf(cacheCurSize));
        Log.i("Percentage", String.valueOf(percentage));

        return percentage;
    }

    /**
     * Function to get the size of the screen
     * @param context - This is the context of current state of the application/object.
     */
    public static HashMap<String,Integer> getScreenSize(Context context)
    {
        // This HashMap will store the width and height
        HashMap<String,Integer> screenSize = new HashMap<>();

        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowmanager.getDefaultDisplay();

        // Get the size of current display
        Point size = new Point();
        display.getSize(size);
        // Note: X point = Width, Y point = Height
        Integer width = size.x;
        Integer height = size.y;

        // Store t he screen size
        screenSize.put("width", width);
        screenSize.put("height", height);

        return screenSize;
    }

    public static Date changeStringtoDate(String strDate){
        DateFormat format = new SimpleDateFormat("yyyy-MM-d k:m:s", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static long getMiliSecondFromDate(Date d){
        long milis = 0;
        return d.getTime();
    }

    public static String getTimeStamp(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String returnDate = "";
        String timestamp = new Timestamp(date.getTime()).toString();
        Date newFormatDate = null;
        try {
            newFormatDate = format.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format.format(newFormatDate);
    }
}
