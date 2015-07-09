/**
 * 
 */
package com.loader.image;

import so.contacts.hub.util.ContactsHubUtils;
import com.loader.DataLoaderParams;
import com.yulong.android.contacts.discover.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author Acher
 *
 */
public class ImageLoaderParams extends DataLoaderParams {

    private Resources mResources;
    
    public int mImageWidth;
    public int mImageHeight;
    public int mRoundPx;
    
    /** 占位图像 */
    public Bitmap mLoadingBitmap;

    public Bitmap mLoadFailedBitmap;

    public boolean mFadeInBitmap = false;

    public boolean mCornerInBitmap = false;
    
    public ImageLoaderParams(Context context) {
        mResources = context.getResources();
    }
    
    public ImageLoaderParams(Context context, int imageWidth, int imageHeight,int roundPx) {
        this(context);
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        mRoundPx = roundPx;
    }
    
    public ImageLoaderParams(Context context, int imageSize) {
        this(context, imageSize, imageSize,0);
    }
    
    public ImageLoaderParams(Context context, int imageSize,int roundPx) {
        this(context, imageSize, imageSize,roundPx);
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is
     * running.
     * 
     * @param bitmap
     */
    public void setLoadingImage(Bitmap bitmap) {
        mLoadingBitmap = bitmap;
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is
     * running. 设置占位图像
     * 
     * @param resId
     */
    public void setLoadingImage(int resId) {
    	Bitmap bitmap = BitmapFactory.decodeResource(mResources, resId);
    	if(mRoundPx > 0){
    		mLoadingBitmap = ContactsHubUtils.corner(bitmap, mRoundPx, mImageWidth);
    	}else if(mRoundPx == 0){
    		Bitmap bitmapmask = BitmapFactory.decodeResource(mResources, R.drawable.putao_mask);
    		mLoadingBitmap = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap, bitmapmask);
    	}else{
    		mLoadingBitmap = bitmap;
    	}
        
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is
     * running.
     * 
     * @param bitmap
     */
    public void setLoadFailedImage(Bitmap bitmap) {
        mLoadFailedBitmap = bitmap;
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is
     * running. 设置占位图像
     * 
     * @param resId
     */
    public void setLoadFailedImage(int resId) {
        mLoadFailedBitmap = BitmapFactory.decodeResource(mResources, resId);
    }
    
    /**
     * If set to true, the image will fade-in once it has been loaded by the
     * background thread.
     */
    public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }

    /**
     * If set to true, the image will corner-in.
     */
    public void setImageCornerIn(boolean cornerIn) {
        mCornerInBitmap = cornerIn;
    }
}
