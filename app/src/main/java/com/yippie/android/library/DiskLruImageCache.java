package com.yippie.android.library;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class DiskLruImageCache
{
    private final File cacheDirectory;
    private long maxCacheByteSize = 1024 * 1024 * 5; // We set it as 5MB as default
    private int cacheSize = 0;
    private int cacheByteSize = 0;
    private static final int INITIAL_CAPACITY = 32;
    private static final float LOAD_FACTOR = 0.75f;
    private CompressFormat mCompressFormat = CompressFormat.JPEG;
    private int mCompressQuality = 70;

    // The synchronizedMap() method is used to return a synchronized (thread-safe) map backed by the specified map.
    // For more information, please refer to
    // 1. http://developer.android.com/reference/java/util/LinkedHashMap.html#LinkedHashMap%28int,%20float,%20boolean%29
    // 2. http://docs.oracle.com/javase/6/docs/api/java/util/HashMap.html
    private final Map<Integer, String> mLinkedHashMap = Collections.synchronizedMap(new LinkedHashMap<Integer, String>(INITIAL_CAPACITY, LOAD_FACTOR, true));

    /**
     * This is the function must be called before constructor.
     * This function will be used ensure the cache folder is exists, can be written and is directory.
     * @param cacheDir    - The directory of the application cache.
     * @param maxByteSize - The max size of the cache folder calculated in Byte.
     */
    public static DiskLruImageCache openCache(Context context, File cacheDir, long maxByteSize)
    {
        // If the cache directory is not yet exist, create one
        if (!cacheDir.exists())
        {
            cacheDir.mkdir();
        }

        if (cacheDir.isDirectory() && cacheDir.canWrite())
        {
            return new DiskLruImageCache(cacheDir, maxByteSize);
        }

        return null;
    }

    /**
     * Constructor
     * Note: Do not call the constructor directly, but perform some additional check/validation before using it instead and call it via openCache().
     * @param cacheDir 		- The directory of the application cache.
     * @param maxByteSize   - The max size of the cache folder calculated in Byte.
     */
    private DiskLruImageCache(File cacheDir, long maxByteSize)
    {
        this.cacheDirectory = cacheDir;
        this.maxCacheByteSize = maxByteSize;
    }


    /**
     * This is a function that save image as bitmap cache to disk cache.
     * @param key - A unique identifier for the bitmap.
     * @param data - The bitmap to store.
     */
    public void saveImageCache(Integer key, Bitmap data)
    {
        synchronized(mLinkedHashMap)
        {
            if(mLinkedHashMap.get(key) == null)
            {
                try
                {
                    String cacheKeyStr = String.valueOf(key);
                    final String filePath = createFilePath(cacheDirectory, cacheKeyStr);
                    if (writeBitmapToFile(data, filePath))
                    {
                        referenceAdd(key, filePath);
                        flushCache();
                    }
                }
                catch (IOException e)
                {
                    Log.e("DiskLruCache", "Error in put: " + e.getMessage());
                }
            }// if (mLinkedHashMap.get(key) == null)
        }// synchronized (mLinkedHashMap)
    }

    /**
     * This is a function that add a new cache key to HashMap.
     * It also update the overall file size and cache count
     * @param cacheKey - The unique identifier for the cache file
     * @param filePath - THe absolute path of the file location.
     */
    private void referenceAdd(Integer cacheKey, String filePath)
    {
        mLinkedHashMap.put(cacheKey, filePath);
        cacheSize = mLinkedHashMap.size();

        // Get current file size and sum with existing file size
        int curFileSize = (int) new File(filePath).length();
        cacheByteSize = cacheByteSize + curFileSize;
    }

    /**
     * Get an image from the disk cache.
     * @param cacheKey - The unique identifier for the bitmap
     * @return The retrieved cache bitmap. Null if the cache is not exists.
     */
    public Bitmap getImageCache(Integer cacheKey)
    {
        synchronized (mLinkedHashMap)
        {
            Bitmap bitmap;
            // Get file from cache
            String file = mLinkedHashMap.get(cacheKey);
            if (file != null)
            {
                bitmap = BitmapFactory.decodeFile(file);
                Log.i("DiskLruCache", "DiskCacheHit for Image with CacheKey: "+cacheKey.toString());
                return bitmap;
            }
            else
            {
                // Construct existing file path
                String cacheKeyStr = String.valueOf(cacheKey);
                String existingFile = createFilePath(cacheDirectory, cacheKeyStr);
                // Get the file using the constructed file path
                assert existingFile != null;
                File existFile = new File(existingFile);
                if (existFile.exists())
                {
                    // If the file exists, we will store the the key in reference list
                    referenceAdd(cacheKey, existingFile);
                    return BitmapFactory.decodeFile(existingFile);
                }
            }
            return null;
        }//synchronized (mLinkedHashMap)
    }

    /**
     * Get an image from the disk cache.
     * @param cacheKey - The unique identifier for the bitmap
     * @return The retrieved cache bitmap. Null if the cache is not exists.
     */
    public Drawable getDrawableCache(Integer cacheKey)
    {
        synchronized (mLinkedHashMap)
        {
            Drawable image;
            // Get file from cache
            String file = mLinkedHashMap.get(cacheKey);
            if (file != null)
            {
                image = Drawable.createFromPath(file);
                Log.i("DiskLruCache", "DiskCacheHit");
                return image;
            }
            else
            {
                // Construct existing file path
                String cacheKeyStr = String.valueOf(cacheKey);
                String existingFile = createFilePath(cacheDirectory, cacheKeyStr);
                // Get the file using the constructed file path
                assert existingFile != null;
                File existFile = new File(existingFile);
                if (existFile.exists())
                {
                    // If the file exists, we will store the the key in reference list
                    referenceAdd(cacheKey, existingFile);
                    return Drawable.createFromPath(existingFile);
                }
            }
            return null;
        }//synchronized (mLinkedHashMap)
    }

    /**
     * This is a function that check if cache file already exists in cache directory
     * @param cacheKey - The unique identifier for the cache file
     * @return true if the cache found, or else false
     */
    public boolean containCache(Integer cacheKey)
    {
        // Check if the cacheKey is in the reference
        if (mLinkedHashMap.containsKey(cacheKey))
        {
            return true;
        }

        // Now check if the actual file is exists
        String cacheKeyStr = String.valueOf(cacheKey);
        String existingFilePath = createFilePath(cacheDirectory, cacheKeyStr);
        Boolean isFileExists = new File(existingFilePath).exists();
        if (isFileExists)
        {
            // If the file is found, we have to update the Reference HashMap.
            referenceAdd(cacheKey, existingFilePath);

            return true;
        }

        return false;
    }


    /**
     * This function will removes all disk cache for this instance cache directory.
     * The directory should have already specify in constructor.
     */
    public void clearDiskCache()
    {
        clearCache(cacheDirectory);
    }

    /**
     * This function removes all disk cache entries from the given directory.
     * Note: This function should not be called directly, instead, call via clearDiskCache()
     * @param cacheDir - The file directory to remove the cache files.
     */
    private static void clearCache(File cacheDir)
    {
        // Get the list of the files in the cache folder
        final File[] files = cacheDir.listFiles();

        // Perform a for loop to delete/remove all files
        for (File file : files)
        {
            file.delete();
        }
        // end of for (int i=0; i<files.length; i++)
    }

    /**
     * This is a function that construct the image cache file path by using
     * 1. Target Cache Directory
     * 2. Image Key
     * @param cacheDir  - The defined cache directory
     * @param imageKey  - The string key(name) that used to identify the image.
     * @return filePath - The constructed image cache file path of the image cache
     */
    public static String createFilePath(File cacheDir, String imageKey)
    {
        try
        {
            // Get the ABsolute Path of the Cache Directory. E.g /root/......
            String absolutePath = cacheDir.getAbsolutePath();
            // Use URLEncoder to make sure the file name is valid
            String validFileName = URLEncoder.encode(imageKey, "UTF-8");
            // Construct File Path
            return absolutePath + File.separator + validFileName;
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("DiskLruCache", "createFilePath - " + e);
        }

        return null;
    }

    /**
     * This function is used to writes a bitmap to a file.
     * Note: Before call to this function, please call to setCompressParams() to initialize the Compress Parameter that require for Bitmap Compression
     * @param bitmap   - The bitmap image to be compressed and write to cache folder.
     * @param filePath - The path of the cache file.
     */
    private boolean writeBitmapToFile(Bitmap bitmap, String filePath) throws IOException, FileNotFoundException
    {
        OutputStream out = null;
        try
        {
            out = new BufferedOutputStream(new FileOutputStream(filePath), 1024 * 2);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    /**
     * Set the target compression format and quality for images written to the disk cache.
     * @param compressFormat - The format of the Image File
     * @param quality        - The percentage of the quality, 1-12
     */
    public void setCompressParams(CompressFormat compressFormat, int quality)
    {
        this.mCompressFormat = compressFormat;
        this.mCompressQuality = quality;
    }

    /**
     * Flush the cache image, removing the oldest entries if the total size is over the specified cache size.
     */
    private void flushCache()
    {
        Entry<Integer, String> eldestEntry;
        File oldestFile;
        long eldestFileSize;
        int count = 0;

        int MAX_DELETE_FILE = 4;
        int MAX_CACHE_FILE_ALLOW = 1000;

        while (count < MAX_DELETE_FILE && (cacheSize > MAX_CACHE_FILE_ALLOW || cacheByteSize > maxCacheByteSize))
        {
            eldestEntry = mLinkedHashMap.entrySet().iterator().next();
            oldestFile = new File(eldestEntry.getValue());
            eldestFileSize = oldestFile.length();
            mLinkedHashMap.remove(eldestEntry.getKey());
            oldestFile.delete();
            cacheSize = mLinkedHashMap.size();
            cacheByteSize -= eldestFileSize;
            count++;
        }// while (count < MAX_DELETE_FILE && (cacheSize > MAX_CACHE_FILE_ALLOW || cacheByteSize > maxCacheByteSize))
    }

}