package com.yippie.android.library;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This is a class that display the image on ImageView.
 * This class will get the images from internet, and cached into Disk and Memory.
 * This class uses thread pool which enables the app to download and display multiple images simutaneously.
 *
 * Note:
 * - This class uses Drawable instead of Bitmap simply due to drawable load faster than bitmap
 * - Using Softreference is great, but it is too aggreesive, and delete cached image too often. So a linked list that holds
 *   the images references is added. It preventing the image to begin deleted, not until it reached the predefined size.
 * - InputStream uses java.net.URLConeection instead, which enable us to use web cache.
 *
 * Usage:
 * 1. Initialize ImageHandler Class
 * 2. Call displayImage(imageUrl, imageView, defaultDrawable) method will do.
 */

public class ImageHandler
{
    private final Map<ImageView, String> mImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService mThreadPool;
    private Context context;
    private Integer scaleType;

    public int THREAD_POOL_SIZE = 24; // Set max thread to 24, which we can download/load update to 24 image in a single thread.
    public static final double MAX_PERCENTAGE_ALLOW = 80.00;

    /**
     * Constructor
     */
    public ImageHandler(Context context, Integer scaleType)
    {
        this.mThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.context = context;
        this.scaleType = scaleType;
    }

    /**
     * This is a class that store the image scale type
     */
    public static class ScaleType
    {
        // Type of Bitmap Scale
        public static final Integer PLACE_IMAGE = 1;
        public static final Integer STORE_IMAGE = 2;
    }

    /**
     * Clears all instance data and stops running threads
     */
    public void reset()
    {
        ExecutorService oldThreadPool = mThreadPool;
        mThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        oldThreadPool.shutdownNow();

        mImageViews.clear();
    }

    /**
     * Function to display the image
     * @param url - The url of the image resource
     * @param imageView - The ImageView to display the image
     * @param placeHolder - The drawable that place into ImageView before the actual image is loaded.
     */
    public void displayImage(final String url, final ImageView imageView, final Drawable placeHolder)
    {
        // If the string url is null or empty, do not proceed
        if(url == null || url.length()<=0){
            return;
        }

        // Store the imageview into list
        this.mImageViews.put(imageView, url);
        // Get the from memory cache
        Bitmap bitmap = getDrawableFromMemoryCache(url);

        // Check in UI thread, so no concurrency issues
        if (bitmap != null)
        {
            // Set the image if the memory cached is found
            bitmap = getScaleBitmap(context, bitmap, scaleType);
            imageView.setImageBitmap(bitmap);
        }
        else
        {
            // If place holder is set, set it to imageview
            if(placeHolder != null)
            {
                imageView.setImageDrawable(placeHolder);
            }

            if(url.length() > 0)
            {
                // Send the process to queue
                queueJob(url, imageView);
            }
        }
    }

    /**
     * Create handler in UI thread and run it in thread to download/load image
     * @param url - The url of the image resource
     * @param imageView - The ImageView to display the image
     */
    private void queueJob(final String url, final ImageView imageView)
    {
        // Create handler in UI thread.
        final Handler handler = new Handler(new Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message msg)
            {
                // Get the image tag, we have to make sure the image view is already in the list
                String tag = mImageViews.get(imageView);
                if (tag != null && tag.equals(url))
                {
                    // If the image was successfully downloaded, then we should be able to get it from the Obj,
                    // which we set and send in thread.
                    if (msg.obj != null)
                    {
                        // Get ImageObj
                        ImageObj imageObj = (ImageObj) msg.obj;
                        // Get bitmap and convert to Drawable
                        Bitmap bitmap = imageObj.bitmap;
                        Boolean isDiskCached = imageObj.isDiskCached;
                        Boolean isSaveToDiskRequired = (!isDiskCached);

                        if(bitmap != null)
                        {
                            bitmap = getScaleBitmap(context, bitmap, scaleType);

                            // We only set the image if the imageview is not hidden
                            if (imageView.isShown()) {
                                // If the image was successfully downloaded, then we should be able to get it from the Obj,
                                // which we set and send in thread.
                                imageView.setImageBitmap(bitmap);
                            }

                            // Save image cache
                            saveBitmapToCache(url, bitmap, isSaveToDiskRequired);
                        }
                    }

                }
                return false;
            }
        });

        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {

                Bitmap image;
                Boolean isDiskCached = isDiskCacheExists(url);
                // Check if the image is diskCached
                if (isDiskCached)
                {
                    image = getBitmapFromDiskCache(url);
                } else {
                    // Download image from internet
                    image = downloadBitmap(url);
                }

                // Construct new imageObj
                ImageObj obj = new ImageObj(image, isDiskCached);

                Message message = Message.obtain();
                message.obj = obj;

                handler.sendMessage(message);

            }
        });
    }

    /**
     * Function to download image from internet based on the URl given
     * @param urlString - The url of the image resource
     */
    private Bitmap downloadBitmap(String urlString)
    {
        try
        {
            // Get the image from internet
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(true);
            connection.connect();

            InputStream is = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            // Return scaled bitmap
            return getScaleBitmap(context, bitmap, scaleType);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This is a function to check if the cache image is available in both Disk
     * @param url - The url of the image resource
     */
    private Boolean isDiskCacheExists(String url)
    {
        return Singleton.getInstance().imageDiskCache.containCache(url.hashCode());
    }

    /**
     * This is a function to retrieve the cache image from DiskCache
     * @param url - The url of the image resource
     */
    private Bitmap getBitmapFromDiskCache(String url)
    {
        return Singleton.getInstance().imageDiskCache.getImageCache(url.hashCode());
    }

    /**
     * This is a function to retrieve the cache image from Memory LruCache
     * @param url - The url of the image resource
     */
    private Bitmap getDrawableFromMemoryCache(String url)
    {
        return Singleton.getInstance().imageCache.get(url.hashCode());
    }

    /**
     * This is a function to insert new image cache
     * @param url        - The url of the image resource
     * @param bitmap     - This is the bitmap to be cache.
     * @param saveToDisk - The flag that indicates if we want to store
     */
    private void saveBitmapToCache(String url, Bitmap bitmap, Boolean saveToDisk)
    {
        Integer cacheKey = url.hashCode();
        if(bitmap != null)
        {
            // Get the percentage of the LruImageCache
            double percentageOfImageCache = Utility.getLruCacheMemoryUsagePercent(Singleton.getInstance().imageCache);

            // If the current size of the Image Cache has exceeded the max percentage allowed, we will perform the following operation:
            // 1. Perform remove cache operation
            if(percentageOfImageCache > MAX_PERCENTAGE_ALLOW)
            {
                // Once the cache hit 90% of the size, we will force clear the memory cache
                this.clearMemoryCache();
            }

            // Add new image cache
            Integer hashCacheKey = cacheKey.hashCode();
            Singleton.getInstance().imageCache.put(hashCacheKey, bitmap);
            // Add newly added image cache reference
            Singleton.getInstance().imageCacheReferenceList.addLast(hashCacheKey);

            // Disk Cache Section
            // Note: We only save the image cache to disk IF requested
            if(saveToDisk)
            {
                Singleton.getInstance().imageDiskCache.saveImageCache(cacheKey, bitmap);
            }
        }
    }

    /**
     * This is a function that clear the Memory LruCache and its reference
     */
    public void clearMemoryCache()
    {
        // Call evictAll to clear the LruCache
        Singleton.getInstance().imageCache.evictAll();
        // After that, clear the reference list as well
        Singleton.getInstance().imageCacheReferenceList.clear();
        System.gc();
        Log.e("Memory LruCache", "Cache Cleared!!!!");
    }

    /**
     * This is a function that scale the image to lower resolution.
     */
    private static Bitmap getScaleBitmap(Context context, Bitmap bitmap, Integer scaleType)
    {
        // The bitmap object must not be null
        if(bitmap != null)
        {
            // Get the width and height of the bitmap
            Integer oriWidth = bitmap.getWidth();
            Integer oriHeight = bitmap.getHeight();
            // Get the scale resolution
            BitmapDimension newScale = getScaleBitmapAspectRatio(context, scaleType, oriWidth, oriHeight);

            // Scale the bitmap
            bitmap = Bitmap.createScaledBitmap(bitmap, newScale.width, newScale.height, true);

            return bitmap;
        }

        return null;
    }

    /**
     * This is a function that return a Bitmap dimension object that contains the scaled width and height for Bitmap.
     * The function will calculate/scaling the dimension based on the type:
     * * Please refer to static variable in this class that start with SCALE_TYPE.
     * * Please input TYPE by using ImageManager.ScaleType.xxx to avoid mistake.
     * @param TYPE   - Please refer to static variable in this class that start with SCALE_TYPE.
     * @param width  - The original width of the bitmap
     * @param height - The original height of the bitmap
     */
    private static BitmapDimension getScaleBitmapAspectRatio(Context context, Integer TYPE, Integer width, Integer height)
    {
        // This option will scale the bitmap to fit the Product Image show in Product List Page (Picked Product)
        if ((TYPE.equals(ScaleType.PLACE_IMAGE)) ||
                (TYPE.equals(ScaleType.PLACE_IMAGE)))
        {
            // The formula of this scaling is
            // Get 30% of the screen width as the width and height
            HashMap<String, Integer> screenDimen = Utility.getScreenSize(context);
            // Get Screen Width
            Integer screenWidth = screenDimen.get("width");

            // Now we have the screen width, get 30% of the size
            Float onePercentageWidth = (float) screenWidth / 100;
            // The picked product image width and height should be same
            Integer scaledSize = (int) Math.floor(onePercentageWidth * 30);

            return new BitmapDimension(scaledSize, scaledSize);
        }

        // Normally we shouldn't reach here, unless the TYPE enter does not match any condition.
        // Construct a BitmapDimension Object and store the original width and height
        return new BitmapDimension(width, height);
    }

    /**
     * This is a private class to store the dimension of the bitmap.
     */
    private static class BitmapDimension
    {
        Integer width;
        Integer height;

        public BitmapDimension(Integer w, Integer h)
        {
            this.width = w;
            this.height = h;
        }
    }

    /**
     * This is a private class used in the Handler
     */
    private static class ImageObj
    {
        Boolean isDiskCached;
        Bitmap bitmap;

        public ImageObj(Bitmap bitmap, Boolean isDiskCached)
        {
            this.bitmap = bitmap;
            this.isDiskCached = isDiskCached;
        }
    }
}
