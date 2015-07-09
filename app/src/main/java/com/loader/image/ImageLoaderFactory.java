/**
 * 
 */
package com.loader.image;

import java.io.File;

import so.contacts.hub.util.UiHelper;
import android.content.Context;

import com.loader.DataCache;
import com.loader.DataLoader;
import com.loader.DataLoaderFactory;
import com.loader.DataLoaderParams;
import com.mdroid.core.image.cache.DiskLruCache;
import com.yulong.android.contacts.discover.R;

/**
 * @author Acher
 *
 */
public class ImageLoaderFactory extends DataLoaderFactory {
    
    private static final String UNIQUE_NAME = "images";

    
    public ImageLoaderFactory(Context context) {
        super(context);
    }
    
    /**
     * 获取通用的图片下载器（不限制图片大小）
     */
    public DataLoader getNormalLoader(boolean defaultLoader, boolean needNorner){
    	DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, "normal_loader");
    	ImageLoaderParams loaderParams = null;
    	if( needNorner ){
    		int corner = context.getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
    		loaderParams = new ImageLoaderParams(context, 0, corner);
    	}else{
    		loaderParams = new ImageLoaderParams(context);
    	}
        loaderParams.setImageCornerIn(false);
        loaderParams.setImageFadeIn(false);
        return getDataLoader(defaultLoader, cacheParams, loaderParams);
    }

    public DataLoader getAvatarLoader() {
        DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, "contacts_avatar");
        ImageLoaderParams loaderParams = new ImageLoaderParams(context,180);
//        loaderParams.setLoadingImage(R.drawable.bg_avatar_default_larger);
        loaderParams.setImageCornerIn(false);
        loaderParams.setImageFadeIn(false);
        
        return getDataLoader(true, cacheParams, loaderParams);
    }
    
    public DataLoader getMovieListLoader() {
        DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, "movie_list");
        
        /*
         * 修改图片圆角
         * modified by hyl 2014-12-31 start
         * old code:
         * ImageLoaderParams loaderParams = new ImageLoaderParams(context,280,352,-1);
         */
        ImageLoaderParams loaderParams = new ImageLoaderParams(context,280,352,15);
        //modified by hyl 2014-12-31 end
        loaderParams.setLoadingImage(R.drawable.putao_bg_pic_dianying);
        loaderParams.setImageCornerIn(false);
        loaderParams.setImageFadeIn(false);
        
        return getDataLoader(true, cacheParams, loaderParams);
    }
    
    
    public DataLoader getActiveHistoryLoader() {
        DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, "activities_history");
        ImageLoaderParams loaderParams = new ImageLoaderParams(context,-1,-1);
        loaderParams.setLoadingImage(R.drawable.putao_pic_quick_replace);
        loaderParams.setImageCornerIn(false);
        loaderParams.setImageFadeIn(false);
        
        return getDataLoader(true, cacheParams, loaderParams);
    }
    
    public DataLoader getCoolCloudAvatarLoader(Context context) {
        DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, "coolcloud_avatar");
        ImageLoaderParams loaderParams = new ImageLoaderParams(context,-1,-1);
        loaderParams.setLoadingImage(R.drawable.putao_pic_account_kuyun);
        loaderParams.setImageCornerIn(false);
        loaderParams.setImageFadeIn(false);
        return getDataLoader(true, cacheParams, loaderParams);
    }
    
    public DataLoader getYellowPageLoader(boolean isShowLoadingImage,int resId,int imageSize,int corner){
        DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, "yellow_page_images");
        ImageLoaderParams loaderParams = new ImageLoaderParams(context,imageSize,corner);
        if(isShowLoadingImage){
            loaderParams.setLoadingImage(resId);
        }
        loaderParams.setImageCornerIn(false);
        loaderParams.setImageFadeIn(false);
        
        return getDataLoader(true, cacheParams, loaderParams);
    }
    
    public DataLoader getYellowPageLoader(int resId,int imageSize){
    	int corner = context.getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
    	return getYellowPageLoader(true,resId,imageSize,corner);
    }
    
    public DataLoader getDefaultYellowPageLoader(){
    	int imageSize = context.getResources().getDimensionPixelSize(R.dimen.putao_listview_item_imgsize);
    	int resId = R.drawable.putao_icon_logo_placeholder;
    	int corner = context.getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
        return getYellowPageLoader(true,resId,imageSize,corner);
    }
    
    public DataLoader getDefaultYellowPageDealLoader(){
    	int imageSize = context.getResources().getDimensionPixelSize(R.dimen.putao_yp_detail_customs_img_width);
        int corner = context.getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
    	return getYellowPageDealLoader(true,imageSize,corner);
    }
    
    public DataLoader getYellowPageDetailLoader(boolean isShowLoadingImage,int imageSize){
    	int corner = context.getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
    	return getYellowPageDealLoader(isShowLoadingImage, imageSize, corner);
    }
    
    public DataLoader getYellowPageDealLoader(boolean isShowLoadingImage,int imageSize,int corner){
        DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, "yellow_page_deal_images");
        ImageLoaderParams loaderParams = new ImageLoaderParams(context, imageSize,corner);
        if(isShowLoadingImage){
        	loaderParams.setLoadingImage(R.drawable.putao_icon_logo_placeholder);
        }
        loaderParams.setImageCornerIn(false);
        loaderParams.setImageFadeIn(false);
        
        return getDataLoader(true, cacheParams, loaderParams);
    }

	/**
	 * 微博头像图片缓存
	 */
    public DataLoader getStatusAvatarLoader() {
		DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, UNIQUE_NAME);
		File cacheDir = DiskLruCache.getDiskCacheDir(context, ImageLoader.HTTP_CACHE_DIR);
		cacheParams.setDiskCacheDir(cacheDir);
		ImageLoaderParams loaderParams = new ImageLoaderParams(context, 80);
//        loaderParams.setLoadingImage(R.drawable.bg_avatar_default_middle);
        loaderParams.setImageCornerIn(false);
        loaderParams.setImageFadeIn(false);
		return getDataLoader(true, cacheParams, loaderParams);
    }
    
    /**
	 * 微博图片缓存
	 */
//	public DataLoader getStatusImageLoader() {
//		DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, UNIQUE_NAME);
//		File cacheDir = DiskLruCache.getDiskCacheDir(context, ImageLoader.HTTP_CACHE_DIR);
//        cacheParams.setDiskCacheDir(cacheDir);
//        
//		int threshold = UiHelper.getImageThreshold(context);
//		ImageLoaderParams loaderParams = new ImageLoaderParams(context, threshold);
//        loaderParams.setLoadingImage(R.drawable.icon_feed_default);
//        loaderParams.setLoadFailedImage(R.drawable.icon_feed_default_fail);
//        loaderParams.setImageCornerIn(false);
//        loaderParams.setImageFadeIn(false);
//
//		return getDataLoader(cacheParams, loaderParams);
//	}
	
	/**
	 * GameCenter image cache
	 * @return
	 */
    public DataLoader getGameCenterLoader() {
        DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, "game_center");
        ImageLoaderParams loaderParams = new ImageLoaderParams(context, 60);
        loaderParams.setLoadingImage(R.drawable.putao_btn_app);
        loaderParams.setImageCornerIn(false);
        loaderParams.setImageFadeIn(false);
        
        return getDataLoader(true, cacheParams, loaderParams);
    }

    /**
     * 
     * @return
     */
	public DataLoader getGameCenterHotImgLoader() {
		DataCache.DataCacheParams cacheParams = new DataCache.DataCacheParams(context, UNIQUE_NAME);
		File cacheDir = DiskLruCache.getDiskCacheDir(context, ImageLoader.HTTP_CACHE_DIR);
        cacheParams.setDiskCacheDir(cacheDir);
        
		int threshold = UiHelper.getImageThreshold(context);
		ImageLoaderParams loaderParams = new ImageLoaderParams(context, threshold);
//        loaderParams.setLoadingImage(R.drawable.icon_feed_default);
//        loaderParams.setLoadFailedImage(R.drawable.icon_feed_default_fail);
        loaderParams.setImageCornerIn(false);
        loaderParams.setImageFadeIn(false);

		return getDataLoader(true, cacheParams, loaderParams);
	}


    @Override
    public DataLoader getDataLoader(boolean defaultLoader, DataCache.DataCacheParams cacheParams, DataLoaderParams loaderParams) {
        DataLoader dataLoader = null;
        if( defaultLoader ){
        	dataLoader = new ImageLoader(context);
        }else{
        	dataLoader = new NormalImageLoader(context);
        }
        
        dataLoader.setDataCache(cacheParams);
        dataLoader.setDataLoaderParams(loaderParams);
        
        return dataLoader;
    }

    @Override
    public DataLoader getDataLoader(boolean defaultLoader) {
    	DataLoader dataLoader = null;
        if( defaultLoader ){
        	dataLoader = new ImageLoader(context);
        }else{
        	dataLoader = new NormalImageLoader(context);
        }        
        return dataLoader;
    }
    
}
