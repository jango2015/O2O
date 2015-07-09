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

package com.mdroid.core.image.cache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.yulong.android.contacts.discover.BuildConfig;
import so.contacts.hub.util.LogUtil;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.util.LruCache;

/**
 * This class holds our bitmap caches (memory and disk). 此类包含位图缓存（内存和磁盘）。
 */
public class ImageCache {
	private static final String TAG = "ImageCache";

	// Default memory cache size
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 1; // 5MB

	// Default disk cache size
	private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

	// Compression settings when writing images to disk cache
	private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;
	private static final int DEFAULT_COMPRESS_QUALITY = 70;
	private static final int DISK_CACHE_INDEX = 0;

	// Constants to easily toggle various caches
	private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
	private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
	private static final boolean DEFAULT_CLEAR_DISK_CACHE_ON_START = false;
	private static final boolean DEFAULT_INIT_DISK_CACHE_ON_CREATE = false;

	private DiskLruCache mDiskLruCache;
	private LruCache<String, Bitmap> mMemoryCache;
	private ImageCacheParams mCacheParams;
	private final Object mDiskCacheLock = new Object();
	private boolean mDiskCacheStarting = true;

	/**
	 * Creating a new ImageCache object using the specified parameters.
	 * 根据指定的parameters来new 一个ImageCache对象
	 * 
	 * @param cacheParams
	 *            The cache parameters to use to initialize the cache
	 */
	public ImageCache(ImageCacheParams cacheParams) {
		init(cacheParams);
	}

	/**
	 * Creating a new ImageCache object using the default parameters.
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique name that will be appended to the cache directory
	 */
	public ImageCache(Context context, String uniqueName) {
		init(new ImageCacheParams(context, uniqueName));
	}

	/**
	 * Find and return an existing ImageCache stored in a {@link RetainFragment}
	 * , if not found a new one is created using the supplied params and saved
	 * to a {@link RetainFragment}.
	 * 
	 * 如果已经存在ImageCache的实体就返回，否则生成新的ImageCache对象并返回；
	 * 
	 * 在这里由于是根据FragmentManager中的非UI
	 * Fragment来储存ImageCache的，所以如果传入的FragmentManager不是同一个对象
	 * （例如在两个FragmentActivity中调用这个方法）， 那么都会生成新的
	 * RetainFragment，所以也会重新生成ImageCache，所以 每个ImageCache对象对应一个FragmentActivity
	 * 
	 * @param fragmentManager
	 *            The fragment manager to use when dealing with the retained
	 *            fragment. fragment manager 用来处理留存的fragment
	 * @param cacheParams
	 *            The cache parameters to use if creating the ImageCache 用 cache
	 *            parameters 来创建ImageCache对象
	 * @return An existing retained ImageCache object or a new one if one did
	 *         not exist
	 */
	public static ImageCache findOrCreateCache(FragmentManager fragmentManager,
			ImageCacheParams cacheParams) {

        // Search for, or create an instance of the non-UI RetainFragment
        // 搜索或创建的非UI RetainFragment的实例
//        final RetainFragment mRetainFragment = findOrCreateRetainFragment(fragmentManager);
//
//        // See if we already have an ImageCache stored in RetainFragment
//        // 查看ImageCache是否已经存在于 RetainFragment中
//        ImageCache imageCache = (ImageCache)mRetainFragment.getObject();
//
//        // No existing ImageCache, create one and store it in RetainFragment
//        // 如果没有存在ImageCache，就new一个新的ImageCache对象并保存到 RetainFragment中
//        if (imageCache == null) {
//            // if (BuildConfig.DEBUG) {
//            // Log.d(TAG, "ImageCache == null");
//            // }
//            imageCache = new ImageCache(cacheParams);
//            mRetainFragment.setObject(imageCache);
//        }

		 // Search for, or create an instance of the non-UI RetainObject
		final RetainObject mRetainObject = RetainObject.getInstance();

		// See if we already have an ImageCache instance in RetainApplication
		ImageCache imageCache = (ImageCache) mRetainObject.getObject();

		// No existing ImageCache, create one and store it in RetainApplication
		if (imageCache == null) {
		    LogUtil.d(TAG,
					"No existing ImageCache, create one and store it in RetainApplication: "
							+ cacheParams.diskCacheDir);
			imageCache = new ImageCache(cacheParams);
			mRetainObject.setObject(imageCache);
		}
		
		return imageCache;
	}

	/**
	 * Initialize the cache, providing all parameters.
	 * 
	 * 初始化缓存，提供所有参数
	 * 
	 * @param cacheParams
	 *            The cache parameters to initialize the cache
	 */
	private void init(ImageCacheParams cacheParams) {
		mCacheParams = cacheParams;

		// Set up memory cache
		// 设置内存缓存
		if (mCacheParams.memoryCacheEnabled) {
			if (BuildConfig.DEBUG) {
			    LogUtil.d(TAG, "Memory cache created (size = "
						+ mCacheParams.memCacheSize + ")");
			}
			mMemoryCache = new LruCache<String, Bitmap>(mCacheParams.memCacheSize) {
				/**
				 * Measure item size in bytes rather than units which is more
				 * practical for a bitmap cache
				 * 
				 * 这个方法在每次向LruCache存储项时，回调
				 */
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					if (BuildConfig.DEBUG) {
					    LogUtil.d(TAG, "Memory cache LruCache sizeOf()方法回调 ");
					}
					return getBitmapSize(bitmap);
				}
			};
		}

		// By default the disk cache is not initialized here as it should be
		// initialized on a separate thread due to disk access.
		// 默认情况下，disk cache是没有初始化的，这里进行应该进行初始化并放到一个单独的线程中。
		//
		// PS: 由于ImageCacheParams 中的 initDiskCacheOnCreate 默认值是 false，多以这里不执行，如果
		// mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
		// cacheParams); 放到异步线程
		// 中执行，那么 可以手动设置 ImageCacheParams 中的
		// initDiskCacheOnCreate为true，是初始化在这里执行，而不用在另起一个异步线程来单独的 执行这步
		// if (cacheParams.initDiskCacheOnCreate) {
		// // Set up disk cache
		// if (BuildConfig.DEBUG) {
		// Log.d(TAG, "cacheParams.initDiskCacheOnCreate-- " +
		// cacheParams.initDiskCacheOnCreate);
		// }
		// initDiskCache();
		// }
	}

	/**
	 * Initializes the disk cache. Note that this includes disk access so this
	 * should not be executed on the main/UI thread. By default an ImageCache
	 * does not initialize the disk cache when it is created, instead you should
	 * call initDiskCache() to initialize it on a background thread.
	 * 
	 * 初始化磁盘高速缓存。请注意，这包括磁盘访问 不应该被主/ UI线程上执行。默认情况下，一个ImageCache被创建时，不会初始化磁盘高速缓存，
	 * 相反，你应该 调用initDiskCache（）来初始化它在后台线程上。
	 */
	/*
	 * public void initDiskCache() { // Set up disk cache synchronized
	 * (mDiskCacheLock) { if (mDiskLruCache == null || mDiskLruCache.isClosed())
	 * { File diskCacheDir = mCacheParams.diskCacheDir; // 如果cacheParams 中磁盘缓存可用
	 * if (mCacheParams.diskCacheEnabled && diskCacheDir != null) { if
	 * (!diskCacheDir.exists()) { diskCacheDir.mkdirs(); } // 如果缓存路径上的可用空间
	 * >设定的空间（就是SD卡上剩余空间够大。。。） if (getUsableSpace(diskCacheDir) >
	 * mCacheParams.diskCacheSize) { try { mDiskLruCache =
	 * DiskLruCache.open(diskCacheDir, 1, 1, mCacheParams.diskCacheSize); if
	 * (BuildConfig.DEBUG) { Log.d(TAG, "Disk cache initialized"); } } catch
	 * (final IOException e) { mCacheParams.diskCacheDir = null; Log.e(TAG,
	 * "initDiskCache - " + e); } } } } mDiskCacheStarting = false;
	 * mDiskCacheLock.notifyAll(); } }
	 */
	/**
	 * Adds a bitmap to both memory and disk cache.
	 * 
	 * @param data
	 *            Unique identifier for the bitmap to store
	 * @param bitmap
	 *            The bitmap to store
	 */
	public void addBitmapToCache(String data, Bitmap bitmap) {
		if (data == null || bitmap == null) {
			return;
		}

		// Add to memory cache
		if (mMemoryCache != null && mMemoryCache.get(data) == null) {
			mMemoryCache.put(data, bitmap);
		}

		// Add to disk cache
		if (mDiskLruCache != null && !mDiskLruCache.containsKey(data)) {
			mDiskLruCache.put(data, bitmap);
		}
	}

	/**
	 * Get from memory cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap if found in cache, null otherwise
	 */
	public Bitmap getBitmapFromMemCache(String data) {
		if (mMemoryCache != null) {
	        final Bitmap memBitmap = mMemoryCache.get(data);
	        if (memBitmap != null && !memBitmap.isRecycled()) {
	            if (BuildConfig.DEBUG) {
	                LogUtil.d(TAG, "Memory cache hit");
	            }
	            return memBitmap;
	        }
		}
		return null;
	}

	/**
	 * Get from disk cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap if found in cache, null otherwise
	 */
	/*
	 * public Bitmap getBitmapFromDiskCache(String data) { final String key =
	 * hashKeyForDisk(data); synchronized (mDiskCacheLock) { while
	 * (mDiskCacheStarting) { try { mDiskCacheLock.wait(); } catch
	 * (InterruptedException e) { } } if (mDiskLruCache != null) { InputStream
	 * inputStream = null; try { final DiskLruCache.Snapshot snapshot =
	 * mDiskLruCache.get(key); if (snapshot != null) { if (BuildConfig.DEBUG) {
	 * Log.d(TAG, "Disk cache hit"); } inputStream =
	 * snapshot.getInputStream(DISK_CACHE_INDEX); if (inputStream != null) { //
	 * final Bitmap bitmap = BitmapFactory.decodeStream(inputStream); // return
	 * bitmap; return
	 * ImageResizer.decodeSampleBitmapFormStream(snapshot,DISK_CACHE_INDEX, 960,
	 * 1280); } } } catch (final IOException e) { Log.e(TAG,
	 * "getBitmapFromDiskCache - " + e); } finally { try { if (inputStream !=
	 * null) { inputStream.close(); } } catch (IOException e) { } } } return
	 * null; } }
	 */

	public Bitmap getBitmapFromDiskCache(String data) {
		if (mDiskLruCache != null) {
			return mDiskLruCache.get(data);
		}
		return null;
	}

	/**
	 * Clears both the memory and disk cache associated with this ImageCache
	 * object. Note that this includes disk access so this should not be
	 * executed on the main/UI thread.
	 */
	public void clearCache() {
		if (mMemoryCache != null) {
			mMemoryCache.evictAll();
			if (BuildConfig.DEBUG) {
			    LogUtil.d(TAG, "Memory cache cleared");
			}
			System.gc();
		}

		// synchronized (mDiskCacheLock) {
		// mDiskCacheStarting = true;
		// if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
		// try {
		// mDiskLruCache.delete();
		// if (BuildConfig.DEBUG) {
		// Log.d(TAG, "Disk cache cleared");
		// }
		// } catch (IOException e) {
		// Log.e(TAG, "clearCache - " + e);
		// }
		// mDiskLruCache = null;
		// initDiskCache();
		// }
		// }
	}

	public void removeMemoryCache(String key) {
		if (mMemoryCache != null) {
			mMemoryCache.remove(key);
		}
	}

	/**
	 * Flushes the disk cache associated with this ImageCache object. Note that
	 * this includes disk access so this should not be executed on the main/UI
	 * thread.
	 */
	public void flush() {
		// synchronized (mDiskCacheLock) {
		// if (mDiskLruCache != null) {
		// try {
		// mDiskLruCache.flush();
		// if (BuildConfig.DEBUG) {
		// Log.d(TAG, "Disk cache flushed");
		// }
		// } catch (IOException e) {
		// Log.e(TAG, "flush - " + e);
		// }
		// }
		// }
	}

	/**
	 * Closes the disk cache associated with this ImageCache object. Note that
	 * this includes disk access so this should not be executed on the main/UI
	 * thread.
	 */
	public void close() {
		// synchronized (mDiskCacheLock) {
		// if (mDiskLruCache != null) {
		// try {
		// if (!mDiskLruCache.isClosed()) {
		// mDiskLruCache.close();
		// mDiskLruCache = null;
		// if (BuildConfig.DEBUG) {
		// Log.d(TAG, "Disk cache closed");
		// }
		// }
		// } catch (IOException e) {
		// Log.e(TAG, "close - " + e);
		// }
		// }
		// }
	}

	/**
	 * A holder class that contains cache parameters.
	 * 
	 * 一个持有者类，包含缓存参数 磁盘缓存路径，内存缓存大小限制，缓存切换，图片压缩方式等
	 */
	public static class ImageCacheParams {
		/** 内存中缓存的最大限制 */
		public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
		/** 磁盘中缓存的最大限制 */
		public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
		/** 磁盘中缓存的目录dir */
		public File diskCacheDir;
		/** 图片的压缩格式类 */
		public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
		/** 压缩质量 */
		public int compressQuality = DEFAULT_COMPRESS_QUALITY;
		/** 内存缓存是否可用，用来轻松切换不同缓存 */
		public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
		/** 磁盘缓存是否可用，用来轻松切换不同缓存 */
		public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;

		public boolean clearDiskCacheOnStart = DEFAULT_CLEAR_DISK_CACHE_ON_START;
		public boolean initDiskCacheOnCreate = DEFAULT_INIT_DISK_CACHE_ON_CREATE;

		/**
		 * @param context
		 *            上下文对象；
		 * @param uniqueName
		 *            唯一的缓存路径
		 * */
		public ImageCacheParams(Context context, String uniqueName) {
			diskCacheDir = getDiskCacheDir(context, uniqueName);
		}

		public ImageCacheParams(File diskCacheDir) {
			this.diskCacheDir = diskCacheDir;
		}

		/**
		 * Sets the memory cache size based on a percentage of the device memory
		 * class. Eg. setting percent to 0.2 would set the memory cache to one
		 * fifth of the device memory class. Throws
		 * {@link IllegalArgumentException} if percent is < 0.05 or > .8.
		 * 
		 * This value should be chosen carefully based on a number of factors
		 * Refer to the corresponding Android Training class for more
		 * discussion: http://developer.android.com/training/displaying-bitmaps/
		 * 
		 * @param context
		 *            Context to use to fetch memory class;context用来获得memory
		 *            class
		 * @param percent
		 *            Percent of memory class to use to size memory
		 *            cache;percent是根据内存大小来获得缓存的大小的本分比,大小限制是：
		 *            percent>0.05f,percent<0.8
		 */
		public void setMemCacheSizePercent(Context context, float percent) {
//			if (percent < 0.05f || percent > 0.8f) {
//				throw new IllegalArgumentException(
//						"setMemCacheSizePercent - percent must be "
//								+ "between 0.05 and 0.8 (inclusive)");
//			}
//			// 返回x最接近的整数，如果x的小数部分大于等于0.5，返回值是大于x的最小整数，否则round函数返回小于等于x的最大整数
//			memCacheSize = Math.round(percent * getMemoryClass(context) * 1024
//					* 1024);
		}

		private static int getMemoryClass(Context context) {
			return ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE))
					.getMemoryClass();
		}
	}

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		// 检查是否安装介质SD卡或外部存储是内置的，如果是的话，尝试使用
		// 外部缓存目录(SD卡或者不可拆卸的外部存储)
		// 否则，使用内部缓存目录
		// PS:这里的实现是这样判断的，首先判断是否有外部（可以收到移出的SD卡和不能手动移出的外部存储）,如果有...
		// PS:file.getPath() 即使file是dir也不会返回 最后一个 File.separator(文件路径分割符)的
		String cacheDirPath = "";
		if (context != null && context.getCacheDir() != null) {
			cacheDirPath = context.getCacheDir().getPath();
		}
		
		File externalCacheDir = Utils.getExternalCacheDir(context);
		final String cachePath = (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) 
				|| !Utils.isExternalStorageRemovable()) && externalCacheDir != null ? externalCacheDir.getPath() : cacheDirPath;

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * A hashing method that changes a string (like a URL) into a hash suitable
	 * for using as a disk filename.
	 */
	public static String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
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
	 * Check if external storage is built-in or removable.
	 * 
	 * @return True if external storage is removable (like an SD card), false
	 *         otherwise.
	 */
	@TargetApi(9)
	public static boolean isExternalStorageRemovable() {
		if (Utils.hasGingerbread()) {
			// 返回主要的“外部”是可移动的存储设备。如果返回true，此设备是例如SD卡，用户可以删除。如果返回false，则内置在设备的存储，不能被物理删除。
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 * Get the external app cache directory. 获得程序的外部缓存目录，/Android/data/
	 * pagkageName/cache/
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@TargetApi(8)
	public static File getExternalCacheDir(Context context) {
		// 如果版本>=2.2，有getExternalCacheDir()方法，直接返回程序外部缓存目录（dir）
		if (Utils.hasFroyo()) {
			File file = context.getExternalCacheDir();
			if (file != null) {
				return file;// 仅file不为null时返回，在2.2系统的部分手机获取不到getExternalCacheDir
			}
		}

		// Before Froyo we need to construct the external cache dir ourselves
		// 如果版本<2.2需要自己手动拼接外部缓存目录（dir）
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	/**
	 * Check how much usable space is available at a given path.
	 * 
	 * 检查有多少可用空间是在一个给定的路径
	 * 
	 * @param path
	 *            The path to check
	 * @return The space available in bytes
	 */
	@TargetApi(9)
	public static long getUsableSpace(File path) {
		if (Utils.hasGingerbread()) {
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		// 以字节为单位，在文件系统上的一个块的大小。这对应于的Unix statfs.f_bsize字段。
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	/**
	 * Locate an existing instance of this Fragment or if not found, create and
	 * add it using FragmentManager.
	 * 
	 * @param fm
	 *            The FragmentManager manager to use.
	 * @return The existing instance of the Fragment or the new instance if just
	 *         created.
	 */
	public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
		// Check to see if we have retained the worker fragment.
		RetainFragment mRetainFragment = (RetainFragment) fm
				.findFragmentByTag(TAG);

		// If not retained (or first time running), we need to create and add
		// it.
		if (mRetainFragment == null) {
			mRetainFragment = new RetainFragment();
			// 只是添加一个fragment到 FragmentManager中，并不显示出来;
			// 在这里说明下commitAllowingStateLoss()方法，这种提交是允许状态值丢失的(但是自己传递的值不会丢失)，比如状态值很大，当调用commit()时(这个还进行系统保存)，有可能页面跳转了状态还没保存完就会报错
			fm.beginTransaction().add(mRetainFragment, TAG)
					.commitAllowingStateLoss();
		}

		return mRetainFragment;
	}

	/**
	 * A simple non-UI Fragment that stores a single Object and is retained over
	 * configuration changes. It will be used to retain the ImageCache object.
	 * 
	 * 一个简单的非UI片段，存储一个对象，并保留 配置方面的变化。它将被用于保留ImageCache对象。
	 * 
	 * 
	 */
	public static class RetainFragment extends Fragment {
		private Object mObject;

		/**
		 * Empty constructor as per the Fragment documentation
		 */
		public RetainFragment() {
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Make sure this Fragment is retained over a configuration change
			// 必须保存此fragment配置上的变化
			setRetainInstance(true);
		}

		/**
		 * Store a single object in this Fragment.
		 * 
		 * @param object
		 *            The object to store
		 */
		public void setObject(Object object) {
			mObject = object;
		}

		/**
		 * Get the stored object.
		 * 
		 * @return The stored object
		 */
		public Object getObject() {
			return mObject;
		}
	}

	/**
	 * A simple non-UI single Object that stores a single Object and is retained
	 * over configuration changes. It will be used to retain the ImageCache
	 * object.
	 */
	public static class RetainObject {
		private static RetainObject instance;

		private SoftReference<Object> mObject;

		/**
		 * Private empty constructor as per the Object documentation
		 */
		public RetainObject() {
		}

		/**
		 * Get the stored object.
		 * 
		 * @return The stored object
		 */
		public static synchronized RetainObject getInstance() {
			if (instance == null) {
				instance = new RetainObject();
			}
			return instance;
		}

		/**
		 * Store a single object in this RetainObject.
		 * 
		 * @param object
		 *            The object to store
		 */
		public void setObject(Object object) {
		    LogUtil.d(TAG,
					"RetainObject - Store a single object in this RetainObject: "
							+ object.toString());
			mObject = new SoftReference<Object>(object);
		}

		/**
		 * Get the stored object.
		 * 
		 * @return The stored object
		 */
		public Object getObject() {
			return mObject == null ? null : mObject.get();
		}
	}

}
