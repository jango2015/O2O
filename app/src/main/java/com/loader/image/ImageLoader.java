/**
 * 
 */
package com.loader.image;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import so.contacts.hub.core.Config;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.loader.DataCache;
import com.loader.DataLoader;
import com.loader.DataLoaderParams;
import com.mdroid.core.http.IgnitedHttpResponse;
import com.mdroid.core.image.cache.DiskLruCache;
import com.mdroid.core.image.cache.Utils;
import com.yulong.android.contacts.discover.BuildConfig;

/**
 * @author Acher
 *
 */
public class ImageLoader extends DataLoader {
    
    private static final String TAG = "ImageLoader";

    private static final int FADE_IN_TIME = 200;
    
    private static final int HTTP_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    
    public static final String HTTP_CACHE_DIR = "http";

    private Resources mResources;

    private ImageLoaderParams mLoaderParams;

    public ImageLoader(Context context) {
        super(context);
        mResources = context.getResources();
    }

    @Override
    public void setDataLoaderParams(DataLoaderParams loaderParams) {
        mLoaderParams = (ImageLoaderParams) loaderParams;
    }

    @Override
	protected void fillDataInView(Object result, View view) {
		if( result == null || view == null ){
			return;
		}
		if (mLoaderParams.mFadeInBitmap) {
			// Transition drawable with a transparent drwabale and the final
			// bitmap
			final TransitionDrawable td = new TransitionDrawable(
					new Drawable[] {
							new ColorDrawable(android.R.color.transparent),
							new BitmapDrawable(mResources,
									mLoaderParams.mLoadingBitmap) });
			fillDataWithView(td, view);
			td.startTransition(FADE_IN_TIME);
		} else{
			fillDataWithView(result, view);
		}
	}

    @Override
	protected LoaderTask createLoaderTask(View view) {
		LoaderTask task = new LoaderTask(view);

//		ImageView imageView = (ImageView) view;
//
//		final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources,
//				mLoaderParams.mLoadingBitmap, task);
//		imageView.setImageDrawable(asyncDrawable);
		
		final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources,
				mLoaderParams.mLoadingBitmap, task);
		fillDataWithView(asyncDrawable, view);

		return task;
	}
    
    /**
     * @param view Any View
     * @return Retrieve the currently active work task (if any) associated with this imageView. null if there is no such task.
     */
    @Override
	protected LoaderTask getLoaderTask(View view) {
		if( view == null ){
			return null;
		}
		if( view instanceof ImageView ){
			ImageView imageView = (ImageView) view;
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getLoaderTask();
			}
		}else if( view instanceof TextView ){
			TextView tView = (TextView) view;
			final Drawable drawable = tView.getBackground();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getLoaderTask();
			}
		}
		return null;
	}
    
    /**
     * 支持对多种view显示图片的适配
     * @param result
     * @param view
     */
	protected void fillDataWithView(Object result, View view){
		if (result == null || view == null) {
			return;
		}
		if (view instanceof ImageView) {
			ImageView imgView = (ImageView) view;
			if (result instanceof Bitmap) {
				imgView.setImageBitmap((Bitmap)result);
			} else if (result instanceof Drawable) {
				imgView.setImageDrawable((Drawable)result);
			}
		} else if (view instanceof TextView) {
			TextView tView = (TextView) view;
			if (result instanceof Bitmap) {
				Drawable drawable = new BitmapDrawable(context.getResources(), (Bitmap) result);
				tView.setBackground(drawable);
			} else if (result instanceof Drawable) {
				tView.setBackground((Drawable)result);
			}
		} else {
			// Other view.
			LogUtil.i(TAG, "fillDataWithView view is not imageview or textview.");
		}
    }
    
    /**
     * A custom Drawable that will be attached to the imageView while the work
     * is in progress. Contains a reference to the actual worker task, so that
     * it can be stopped if a new binding is required, and makes sure that only
     * the last started worker process can bind its result, independently of the
     * finish order.
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<LoaderTask> taskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, LoaderTask loaderTask) {
            super(res, bitmap);
            taskReference = new WeakReference<LoaderTask>(loaderTask);
        }

        public LoaderTask getLoaderTask() {
            return taskReference.get();
        }
    }

    @Override
    public Bitmap processData(Object data) {
        // 下载并缩放图片
        
        if (BuildConfig.DEBUG) {
            LogUtil.d(TAG, "processBitmap - " + data);
        }
        Bitmap bitmap = null;
        
        if (data instanceof Integer) {// 加载本地资源图片
            int resId = Integer.parseInt(String.valueOf(data));
            bitmap = processResId(resId);
        } else {
            String dataString = String.valueOf(data);
            if (!TextUtils.isEmpty(dataString)) {
                	bitmap = processUrl(dataString);
            }
        }
//        BitmapDrawable drawable = null;
//        
//        if (bitmap != null) {
////	        if (Utils.hasHoneycomb()) {
////	            // Running on Honeycomb or newer, so wrap in a standard BitmapDrawable
////	            drawable = new BitmapDrawable(mResources, bitmap);
////	        } else {
//	            // Running on Gingerbread or older, so wrap in a RecyclingBitmapDrawable
//	            // which will recycle automagically
//	            drawable = new RecyclingBitmapDrawable(mResources, bitmap);
////	        }
//        }
        if(mLoaderParams.mRoundPx <=0){
        	return bitmap;
        }
        return ContactsHubUtils.corner(bitmap, mLoaderParams.mRoundPx,mLoaderParams.mImageWidth);
    }
    
    /**
     * The main process method, which will be called by the ImageWorker in the
     * AsyncTask background thread.
     * 
     * @param data The data to load the bitmap, in this case, a regular http URL
     * @return The downloaded and resized bitmap
     */
    public Bitmap processUrl(String data) {
        if (BuildConfig.DEBUG) {
            LogUtil.d(TAG, "processBitmap - " + data);
        }

        // Download a bitmap, write it to a file
        final File f = downloadBitmap(context, data);

        if (f != null) {
            // Return a sampled down version
            Bitmap bitmap = decodeSampledBitmapFromFile(f.toString(),
                    mLoaderParams.mImageWidth, mLoaderParams.mImageHeight);
            if (bitmap == null) {
                f.delete();// 加载图片保护：decodefile失败，删除文件。否则，第二次加载该文件同样失败
            }
            return bitmap;
        }

        return null;
    }
    
    /**
     * Download a bitmap from a URL, write it to a disk and return the File pointer. This implementation uses a simple disk cache.
     * 
     * @param context The context to use
     * @param urlString The URL to fetch
     * @return A File pointing to the fetched bitmap
     */
    public static File downloadBitmap(Context context, String urlString) {
        final File cacheDir = DiskLruCache.getDiskCacheDir(context, HTTP_CACHE_DIR);

        final DiskLruCache cache = DiskLruCache.openCache(cacheDir, HTTP_CACHE_SIZE);

        if (cache != null) {
            final File cacheFile = new File(cache.createFilePath(urlString));
            if (cache.containsKey(urlString) && cacheFile.exists()) {
                if (BuildConfig.DEBUG) {
                    LogUtil.d(TAG, "downloadBitmap - found in http cache - " + urlString);
                }
                if (cacheFile.length() < 8 * 8) {// 图片下载保护
                    cacheFile.delete();// 当下载文件小于64b时，我们主观认为图片下载不完全，或者图片有误等
                } else {
                    return cacheFile;
                }
            }

            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, "downloadBitmap - downloading - " + urlString);
            }

            Utils.disableConnectionReuseIfNecessary();
            HttpURLConnection urlConnection = null;
            BufferedOutputStream out = null;

            try {
                IgnitedHttpResponse response = Config.getApiHttp().get(urlString, false).send();
                out = new BufferedOutputStream(new FileOutputStream(cacheFile), Utils.IO_BUFFER_SIZE);
                out.write(response.getResponseBodyAsBytes());
                
                return cacheFile;
            } catch (Exception e) {
                LogUtil.e(TAG, "Error in downloadBitmap - " + e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (final IOException e) {
                        LogUtil.e(TAG, "Error in downloadBitmap - " + e);
                    }
                }
            }
        }

        return null;
    }

    public static String getFilePath(Context context, String data) {
        final File cacheDir = DiskLruCache.getDiskCacheDir(context, HTTP_CACHE_DIR);
        final DiskLruCache cache = DiskLruCache.openCache(cacheDir, HTTP_CACHE_SIZE);
        return cache != null ? cache.createFilePath(data) : null;
    }
    
    /**
     * The main processing method. This happens in a background task. In this
     * case we are just sampling down the bitmap and returning it from a
     * resource.
     * 
     * @param resId
     * @return
     */
    private Bitmap processResId(int resId) {
        if (BuildConfig.DEBUG) {
            LogUtil.d(TAG, "processBitmap - " + resId);
        }
        return decodeSampledBitmapFromResource(mResources, resId, mLoaderParams.mImageWidth, mLoaderParams.mImageHeight);
    }
    
    /**
     * Decode and sample down a bitmap from resources to the requested width and height.
     * 
     * @param res The resources object containing the image data
     * @param resId The resource id of the image data
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions that are equal to or greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        // return BitmapFactory.decodeResource(res, resId, options);
        Bitmap sourceBitmap = null;
        int retryTime = 0;
        do {
            try {
                sourceBitmap = BitmapFactory.decodeResource(res, resId, options);
            } catch (OutOfMemoryError e) {
                LogUtil.w("imageCache", "image too big (OutOfMemoryError),decodeSampledBitmapFromResource:" + resId);
            }
            options.inSampleSize += 1;
            retryTime++;
        } while (sourceBitmap == null && retryTime < 2);
        return sourceBitmap;
    }
    
    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding bitmaps using the decode* methods from {@link BitmapFactory}. 
     * This implementation calculates the closest inSampleSize that will result in the final decoded bitmap having a width and height equal to or larger than the requested width and height.
     * This implementation does not ensure a power of 2 is returned for inSampleSize which can be faster when decoding but results in a larger bitmap which isn't as useful for caching purposes.
     * 
     * @param options An options object with out* params already populated (run through a decode* method with inJustDecodeBounds==true
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float)height / (float)reqHeight);
            final int widthRatio = Math.round((float)width / (float)reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee a final image
            // with both dimensions larger than or equal to the requested height
            // and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
    
    /**
     * Decode and sample down a bitmap from a file to the requested width and height.
     * 
     * @param filename The full path of the file to decode
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions that are equal to or greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        // return BitmapFactory.decodeFile(filename, options);
        Bitmap sourceBitmap = null;
        int retryTime = 0;
        do {
            try {
                sourceBitmap = BitmapFactory.decodeFile(filename, options);
            } catch (OutOfMemoryError e) {
                LogUtil.w("imageCache", "image too big (OutOfMemoryError),decodeSampledBitmapFromFile:" + filename);
            }
            options.inSampleSize += 1;
            retryTime++;
        } while (sourceBitmap == null && retryTime < 2);

        if (options.inSampleSize == 2) {
            // 请求图片高度或宽度与图片实际高度或宽度小时，放大图片达到要求的效果
            sourceBitmap = scaleBitmap(sourceBitmap, reqWidth, reqHeight);
        }

        return sourceBitmap;
    }

    private static Bitmap scaleBitmap(Bitmap bm, int reqWidth, int reqHeight) {
        if (bm == null || bm.isRecycled()) {
            return bm;
        }

        int width = bm.getWidth();
        int height = bm.getHeight();

        if (width < reqWidth && height < reqHeight) {
            /* 计算图片放大的比例 */
            float scale = 1.25f;

            // Calculate ratios of height and width to requested height and
            // width
            final float heightRatio = (float)reqHeight / (float)height;
            final float widthRatio = (float)reqWidth / (float)width;

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image with both dimensions larger than or equal to the requested height and width.
            scale = heightRatio < widthRatio ? heightRatio : widthRatio;

            /* 生成resize后的Bitmap对象 */
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            try {
                bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            } catch (OutOfMemoryError e) {
                System.gc();
            } catch (Exception e) {
            }
        }
        return bm;
    }

    /**
     * Decode and sample down a bitmap from a file input stream to the requested width and height.
     * 
     * @param fileDescriptor The file descriptor to read from
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions that are equal to or greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap sourceBitmap = null;
        int retryTime = 0;
        do {
            try {
                sourceBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            } catch (OutOfMemoryError e) {
                System.gc();
                LogUtil.w("imageCache", "decodeSampledBitmapFromDescriptor - image too big (OutOfMemoryError),fileDescriptor:" + fileDescriptor);
            }
            options.inSampleSize += 1;
            retryTime++;
        } while (sourceBitmap == null && retryTime < 2);
        return sourceBitmap;
    }
    
    /**
     * Decode and sample down a bitmap from a file input stream to the requested width and height.
     * 
     * @param fileDescriptor The file descriptor to read from
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions that are equal to or greater than the requested width and height
     */
    public Bitmap decodeSampledBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // If we're running on Honeycomb or newer, try to use inBitmap
        if (Utils.hasHoneycomb()) {
        	DataCache cache = getDataCache();
            addInBitmapOptions(options, cache);
        }
        
        Bitmap sourceBitmap = null;
        int retryTime = 0;
        do {
            try {
                sourceBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            } catch (OutOfMemoryError e) {
                System.gc();
                LogUtil.w("imageCache", "decodeSampledBitmapFromDescriptor - image too big (OutOfMemoryError),fileDescriptor:");
            }
            options.inSampleSize += 1;
            retryTime++;
        } while (sourceBitmap == null && retryTime < 2);
        return sourceBitmap;
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

}
