/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loader;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import so.contacts.hub.util.LogUtil;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.mdroid.core.image.cache.DiskLruCache;
import com.mdroid.core.image.cache.Utils;
import com.yulong.android.contacts.discover.BuildConfig;

/**
 * This class holds our bitmap caches (memory and disk). 此类包含位图缓存（内存和磁盘）。
 */
public class DataCache {
    
    private static final String TAG = "DataCache";

    // Default memory cache size
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 10; // 1MB
    
    // Default disk cache size
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 20; // 20MB
    

    // Constants to easily toggle various caches
    private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;

    private DiskLruCache mDiskLruCache;
    private LruCache<String, Object> mMemCache;

    private DataCacheParams mCacheParams;

    private Set<SoftReference<Bitmap>> mReusableBitmaps;

    /**
     * Creating a new ImageCache object using the specified parameters. 根据指定的parameters来new 一个ImageCache对象
     * 
     * @param cacheParams The cache parameters to use to initialize the cache
     */
    public DataCache(DataCacheParams cacheParams) {
        init(cacheParams);
    }

    /**
     * Creating a new ImageCache object using the default parameters.
     * 
     * @param context The context to use
     * @param uniqueName A unique name that will be appended to the cache directory
     */
    public DataCache(Context context, String uniqueName) {
        init(new DataCacheParams(context, uniqueName));
    }

    /**
     * Find and return an existing ImageCache stored in a {@link RetainCache} , if not found a new one is created using the supplied params and saved to a {@link RetainCache}. 
     * 
     * @param cacheParams The cache parameters to use if creating the DataCache
     * @return An existing retained DataCache object or a new one if one did not exist
     */
    public static DataCache findOrCreateCache(DataCacheParams cacheParams) {
        // // Search for, or create an instance of the non-UI RetainObject
        final RetainCache mRetainObject = RetainCache.getInstance();

        // See if we already have an ImageCache instance in RetainApplication
        DataCache dataCache = (DataCache)mRetainObject.getCache(cacheParams.uniqueName);

        // No existing ImageCache, create one and store it in RetainApplication
        if (dataCache == null) {
            dataCache = new DataCache(cacheParams);
            mRetainObject.setCache(cacheParams.uniqueName, dataCache);
        }
        
        return dataCache;
    }

    /**
     * Get the size in bytes of a bitmap.
     * 
     * 获得Bitmap的byte大小
     * 
     * @param bitmap
     * @return size in bytes
     */
    @TargetApi(12)
    public static int getBitmapSize(Bitmap bitmap) {
        if (Utils.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
    
    /**
     * Initialize the cache, providing all parameters. 初始化缓存，提供所有参数
     * 
     * @param cacheParams The cache parameters to initialize the cache
     */
    private void init(DataCacheParams cacheParams) {
        mCacheParams = cacheParams;

        // Set up memory cache
        // 设置内存缓存
        if (mCacheParams.memoryCacheEnabled) {
            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, "Memory cache created (size = " + mCacheParams.memCacheSize + ")");
            }
            
            // If we're running on Honeycomb or newer, create a set of reusable bitmaps that can be
            // populated into the inBitmap field of BitmapFactory.Options. Note that the set is
            // of SoftReferences which will actually not be very effective due to the garbage
            // collector being aggressive clearing Soft/WeakReferences. A better approach
            // would be to use a strongly references bitmaps, however this would require some
            // balancing of memory usage between this set and the bitmap LruCache. It would also
            // require knowledge of the expected size of the bitmaps. From Honeycomb to JellyBean
            // the size would need to be precise, from KitKat onward the size would just need to
            // be the upper bound (due to changes in how inBitmap can re-use bitmaps).
            if (Utils.hasHoneycomb()) {
                mReusableBitmaps =
                        Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
            }
            
            mMemCache = new LruCache<String, Object>(mCacheParams.memCacheSize) {
                /**
                 * Measure item size in bytes rather than units which is more
                 * practical for a bitmap cache
                 * 
                 * 这个方法在每次向LruCache存储项时，回调
                 */
                @Override
                protected int sizeOf(String key, Object obj) {
                    if (BuildConfig.DEBUG) {
                        LogUtil.d(TAG, "Memory cache LruCache sizeOf()方法回调 ");
                    }
                    if(obj instanceof Bitmap){
                        return getBitmapSize((Bitmap)obj);
                    }
                    return 0;
                }
            };
            if(mCacheParams.diskCacheDir != null){
                mDiskLruCache = DiskLruCache.openCache(mCacheParams.diskCacheDir, mCacheParams.diskCacheSize);
            }
        }
    }

    
    
    /**
     * Adds a bitmap to both memory and disk cache.
     * 
     * @param data Unique identifier for the bitmap to store
     * @param bitmap The bitmap to store
     */
    public void addDataToCache(String data, Object result) {
        if (data == null || result == null) {
            return;
        }
        // Add to memory cache
        if (mMemCache != null) {
            mMemCache.put(data, result);
        }
        // Add to disk cache
        if (mDiskLruCache != null && !mDiskLruCache.containsKey(data)) {
            //联系人头像不保存文件
            if (!(data.contains("contactId") || data.contains("ContactId")) 
                    && result instanceof Bitmap) {
                mDiskLruCache.put(data, (Bitmap)result);
            }
        }
    }

    /**
     * Get from memory cache.
     * 
     * @param data Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Object getResultFromCache(String data) {
        Log.e("mat","getResultFromCache");
        Log.e("mat","data == " + data);
        if (mMemCache != null) {
            final Object mCacheResult = mMemCache.get(data);
            if (mCacheResult != null) {
                if (BuildConfig.DEBUG) {
                    LogUtil.d(TAG, "Memory cache hit");
                }
                if(mCacheResult instanceof Bitmap && !((Bitmap)mCacheResult).isRecycled()) {
                    Log.e("mat","mCacheResult == " + ((Bitmap)mCacheResult).getByteCount());
                }
                return mCacheResult;
            }
        }
        return null;
    }

    /**
     * Clears both the memory and disk cache associated with this ImageCache object. Note that this includes disk access so this should not be executed on the main/UI thread.
     */
    public void clearCache() {
        if (mMemCache != null) {
            mMemCache.evictAll();
            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, "Memory cache cleared");
            }
            System.gc();
        }
    }

    public void removeCache(String key) {
        if (mMemCache != null) {
            mMemCache.remove(key);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void addInBitmapOptions(BitmapFactory.Options options, DataCache cache) {
        // inBitmap only works with mutable bitmaps so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        if (cache != null) {
            // Try and find a bitmap to use for inBitmap
            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {
                options.inBitmap = inBitmap;
            }
        }
    }

    /**
     * @param options - BitmapFactory.Options with out* options populated
     * @return Bitmap that case be used for inBitmap
     */
    public Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;

        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            synchronized (mReusableBitmaps) {
                final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        // Check to see it the item can be used for inBitmap
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again
                            iterator.remove();
                            break;
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }

        return bitmap;
    }
    
    /**
     * @param candidate - Bitmap to check
     * @param targetOptions - Options that have the out* value populated
     * @return true if <code>candidate</code> can be used for inBitmap re-use with
     *      <code>targetOptions</code>
     */
//    @TargetApi(VERSION_CODES.KITKAT)
    private static boolean canUseForInBitmap(
            Bitmap candidate, BitmapFactory.Options targetOptions) {
          // hyl 2014-3-17 注释 不判断是否为4.4
//        if (!Utils.hasKitKat()) {
            // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
            return candidate.getWidth() == targetOptions.outWidth
                    && candidate.getHeight() == targetOptions.outHeight
                    && targetOptions.inSampleSize == 1;
//        }

        // From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap
        // is smaller than the reusable bitmap candidate allocation byte count.
        // hyl 2014-3-17 注释
//        int width = targetOptions.outWidth / targetOptions.inSampleSize;
//        int height = targetOptions.outHeight / targetOptions.inSampleSize;
//        int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
//        return byteCount <= candidate.getAllocationByteCount();
    }

    /**
     * Return the byte usage per pixel of a bitmap based on its configuration.
     * @param config The bitmap configuration.
     * @return The byte usage per pixel.
     */
    private static int getBytesPerPixel(Config config) {
        if (config == Config.ARGB_8888) {
            return 4;
        } else if (config == Config.RGB_565) {
            return 2;
        } else if (config == Config.ARGB_4444) {
            return 2;
        } else if (config == Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }

    /**
     * A holder class that contains cache parameters. 一个持有者类，包含缓存参数 磁盘缓存路径，内存缓存大小限制，缓存切换，图片压缩方式等
     */
    public static class DataCacheParams {
        
        private Context context;
        
        private String uniqueName;
        
        /** 内存中缓存的最大限制 */
        public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;

        /** 内存缓存是否可用，用来轻松切换不同缓存 */
        public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
        
        /** 磁盘中缓存的目录dir */
        public File   diskCacheDir;
        
        /** 磁盘中缓存的最大限制 */
        public int    diskCacheSize    = DEFAULT_DISK_CACHE_SIZE;

        /**
         * @param context 上下文对象；
         * @param uniqueName 唯一的缓存路径
         */
        public DataCacheParams(Context context, String uniqueName) {
            this.context = context;
            this.uniqueName = uniqueName;
        }

        public void setDiskCacheDir(File diskCacheDir) {
            this.diskCacheDir = diskCacheDir;
        }
        
        /**
         * Sets the memory cache size based on a percentage of the device memory class. Eg. setting percent to 0.2 would set the memory cache to one fifth of the device memory class. Throws {@link IllegalArgumentException} if percent is < 0.05 or > .8. This value should be chosen carefully based on
         * a number of factors Refer to the corresponding Android Training class for more discussion: http://developer.android.com/training/displaying-bitmaps/
         * 
         * @param context Context to use to fetch memory class;context用来获得memory class
         * @param percent Percent of memory class to use to size memory cache;percent是根据内存大小来获得缓存的大小的本分比,大小限制是： percent>0.05f,percent<0.8
         */
        public void setMemCacheSizePercent(Context context, float percent) {
            /**
             * 将动态设置内存大小方法注释 ，因为目前手机分配的内存将会比较大（getMemoryClass方法在豪成512内存手机上都能获取到96M）
             * 所以为了控制内存的大小 固定设置图片缓存值为5M (DEFAULT_MEM_CACHE_SIZE = 5) 不再进行动态分配
             * hyl 2014-3-8 
             */
//            if (percent < 0.05f || percent > 0.8f) {
//                throw new IllegalArgumentException("setMemCacheSizePercent - percent must be " + "between 0.05 and 0.8 (inclusive)");
//            }
//            // 返回x最接近的整数，如果x的小数部分大于等于0.5，返回值是大于x的最小整数，否则round函数返回小于等于x的最大整数
//            int memClass = getMemoryClass();
//            memCacheSize = Math.round(percent * memClass * 1024 * 1024);
        }

        private int getMemoryClass() {
            return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        }
    }

    /**
     * A simple non-UI single Object that stores a single Object and is retained over configuration changes. It will be used to retain the ImageCache object.
     */
    public static class RetainCache {
        
        private static RetainCache instance;
        
        private Map<String, SoftReference<DataCache>> mRetainMap;

        /**
         * Private empty constructor as per the Object documentation
         */
        public RetainCache() {
        }

        /**
         * Get the stored object.
         * 
         * @return The stored object
         */
        public static synchronized RetainCache getInstance() {
            if (instance == null) {
                instance = new RetainCache();
            }
            return instance;
        }

        /**
         * Store a single object in this RetainObject.
         * 
         * @param object The object to store
         */
        public void setCache(String uniqueName, DataCache cache) {
            LogUtil.d(TAG, "RetainObject - Store a single object in this RetainObject: " + cache.toString());
            SoftReference<DataCache> mCachedObject = new SoftReference<DataCache>(cache);
            if (mRetainMap == null) {
                mRetainMap = new HashMap<String, SoftReference<DataCache>>();
            }
            mRetainMap.put(uniqueName, mCachedObject);
        }

        /**
         * Get the stored object.
         * 
         * @return The stored object
         */
        public Object getCache(String uniqueName) {
            if (mRetainMap != null) {
                SoftReference<DataCache> mCachedObject = mRetainMap.get(uniqueName);
                if (mCachedObject != null) {
                    return mCachedObject.get();
                }
            }
            return null;
        }
    }

}
