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

import java.io.FileDescriptor;

import com.yulong.android.contacts.discover.BuildConfig;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.Utils;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * A simple subclass of {@link ImageWorker} that resizes images from resources
 * given a target width and height. Useful for when the input images might be
 * too large to simply load directly into memory.
 */
public class ImageResizer extends ImageWorker {
	private static final String TAG = "ImageResizer";
	protected int mImageWidth;
	protected int mImageHeight;

	/**
	 * Initialize providing a single target image size (used for both width and
	 * height); 初始化提供了一个单一的目标图像尺寸（宽度和 高度）;
	 * 
	 * @param context
	 * @param imageWidth
	 * @param imageHeight
	 */
	public ImageResizer(Context context, int imageWidth, int imageHeight) {
		super(context);
		setImageSize(imageWidth, imageHeight);
	}

	/**
	 * Initialize providing a single target image size (used for both width and
	 * height);
	 * 
	 * 初始化提供了单一的目标图像尺寸(长和宽相同)
	 * 
	 * @param context
	 * @param imageSize
	 */
	public ImageResizer(Context context, int imageSize) {
		super(context);
		setImageSize(imageSize);
	}

	/**
	 * Set the target image width and height.
	 * 
	 * @param width
	 * @param height
	 */
	public void setImageSize(int width, int height) {
		mImageWidth = width;
		mImageHeight = height;
	}

	/**
	 * Set the target image size (width and height will be the same).
	 * 设置目标图片的大小（宽度和高度相同）
	 * 
	 * @param size
	 */
	public void setImageSize(int size) {
		setImageSize(size, size);
	}

	/**
	 * The main processing method. This happens in a background task. In this
	 * case we are just sampling down the bitmap and returning it from a
	 * resource.
	 * 
	 * @param resId
	 * @return
	 */
	private Bitmap processBitmap(int resId) {
		if (BuildConfig.DEBUG) {
		    LogUtil.d(TAG, "processBitmap - " + resId);
		}
		return decodeSampledBitmapFromResource(mResources, resId, mImageWidth,
				mImageHeight);
	}

	@Override
	protected Bitmap processBitmap(Object data) {
		return processBitmap(Integer.parseInt(String.valueOf(data)));
	}

	/**
	 * Decode and sample down a bitmap from resources to the requested width and
	 * height.
	 * 
	 * @param res
	 *            The resources object containing the image data
	 * @param resId
	 *            The resource id of the image data
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		// return BitmapFactory.decodeResource(res, resId, options);
		Bitmap sourceBitmap = null;
		int retryTime = 0;
		do {
			try {
				sourceBitmap = BitmapFactory
						.decodeResource(res, resId, options);
			} catch (OutOfMemoryError e) {
			    LogUtil.w("imageCache",
						"image too big (OutOfMemoryError),decodeSampledBitmapFromResource:"
								+ resId);
			}
			options.inSampleSize += 1;
			retryTime++;
		} while (sourceBitmap == null && retryTime < 2);
		return sourceBitmap;
	}

	/**
	 * Decode and sample down a bitmap from a file to the requested width and
	 * height.
	 * 
	 * @param filename
	 *            The full path of the file to decode
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		// return BitmapFactory.decodeFile(filename, options);
		Bitmap sourceBitmap = null;
		int retryTime = 0;
		do {
			try {
				sourceBitmap = BitmapFactory.decodeFile(filename, options);
			} catch (OutOfMemoryError e) {
			    LogUtil.w("imageCache",
						"image too big (OutOfMemoryError),decodeSampledBitmapFromFile:"
								+ filename);
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
			final float heightRatio = (float) reqHeight / (float) height;
			final float widthRatio = (float) reqWidth / (float) width;

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee a final image
			// with both dimensions larger than or equal to the requested height
			// and width.
			scale = heightRatio < widthRatio ? heightRatio : widthRatio;

			/* 生成resize后的Bitmap对象 */
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			try {
			    Bitmap newBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
			    Utils._BitmapRecyle(bm);
			    bm = newBitmap;
			} catch (OutOfMemoryError e) {
				System.gc();
			} catch (Exception e) {
			}
		}
		return bm;
	}

	/**
	 * Decode and sample down a bitmap from a file input stream to the requested
	 * width and height.
	 * 
	 * @param fileDescriptor
	 *            The file descriptor to read from
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromDescriptor(
			FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap sourceBitmap = null;
		int retryTime = 0;
		do {
			try {
				sourceBitmap = BitmapFactory.decodeFileDescriptor(
						fileDescriptor, null, options);
			} catch (OutOfMemoryError e) {
			    LogUtil.w("imageCache",
						"decodeSampledBitmapFromDescriptor - image too big (OutOfMemoryError),fileDescriptor:"
								+ fileDescriptor);
			}
			options.inSampleSize += 1;
			retryTime++;
		} while (sourceBitmap == null && retryTime < 2);
		return sourceBitmap;
	}

	//
	// public static Bitmap decodeSampleBitmapFormStream(DiskLruCache.Snapshot
	// snapshot,int index, int reqWidth, int reqHeight){
	// InputStream inputStream = null;
	// final BitmapFactory.Options options = new BitmapFactory.Options();
	// options.inJustDecodeBounds = true;
	// inputStream = snapshot.getInputStream(index);
	// BitmapFactory.decodeStream(inputStream, null, options);
	//
	// options.inSampleSize = calculateInSampleSize(options, reqWidth,
	// reqHeight);
	// // Decode bitmap with inSampleSize set
	// options.inJustDecodeBounds = false;
	//
	// Bitmap bitmap = null;
	// int retryTime = 0;
	// do {
	// try {
	// inputStream = snapshot.getInputStream(index);
	// bitmap = BitmapFactory.decodeStream(inputStream, null, options);
	// } catch (OutOfMemoryError e) {
	// Log.w("imageCache",
	// "decodeSampledBitmapFromDescriptor - image too big (OutOfMemoryError),inputStream:");
	// }
	// options.inSampleSize += 1;
	// retryTime++;
	// } while (bitmap == null && retryTime<2);
	// if (inputStream != null) {
	// try {
	// inputStream.close();
	// } catch (IOException e) {
	// Log.e(TAG, e.getMessage(), e);
	// }
	// }
	// return bitmap;
	// }
	//

	/**
	 * Calculate an inSampleSize for use in a {@link BitmapFactory.Options}
	 * object when decoding bitmaps using the decode* methods from
	 * {@link BitmapFactory}. This implementation calculates the closest
	 * inSampleSize that will result in the final decoded bitmap having a width
	 * and height equal to or larger than the requested width and height. This
	 * implementation does not ensure a power of 2 is returned for inSampleSize
	 * which can be faster when decoding but results in a larger bitmap which
	 * isn't as useful for caching purposes.
	 * 
	 * @param options
	 *            An options object with out* params already populated (run
	 *            through a decode* method with inJustDecodeBounds==true
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

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
}
