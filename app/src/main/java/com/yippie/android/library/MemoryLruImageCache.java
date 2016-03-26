package com.yippie.android.library;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MemoryLruImageCache extends LruCache<Integer, Bitmap>
{
    /**
     * Constructor
     */
    public MemoryLruImageCache(final int cacheSizeInKb)
    {
        super(cacheSizeInKb);
    }

    @Override
    protected int sizeOf(final Integer key, final Bitmap bitmap)
    {
        // The cache size will be measured in bytes rather than number of items.
        return Utility.getSizeInBytes(bitmap);
    }

}
