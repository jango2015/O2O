package so.contacts.hub.widget;

import java.util.ArrayList;
import java.util.List;
import so.contacts.hub.push.bean.PushAdBean;
import so.contacts.hub.ui.yellowpage.YellowPageSearchActivity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loader.DataLoader;
import com.loader.DataLoaderListener;
import com.loader.image.ImageLoaderFactory;
import so.contacts.hub.util.MobclickAgentUtil;
import com.yulong.android.contacts.discover.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

/**
 * 广告栏Layout
 * @author zjh
 *
 */
public class AdOperatLayout extends LinearLayout {

	private static final String TAG = "AdOperatLayout";
	
	private Context mContext = null;
	
	private AdLayoutCallback mCallback = null;
	
	/**
	 * 广告图片数量(默认为1)
	 */
	private int mChildCount = 1;
	
	/**
	 * 保存下载广告图片的数量
	 */
	private int mLoadImgCount = 0;
	
	/**
	 * 图片Url
	 */
	private String[] mImgUrlList = null;
	
	/**
	 * 点击类型
	 */
	private String[] mClickTypeList = null;
	
	/**
	 * 点击跳转activity
	 */
	private String[] mClickActivityList = null;
	
	/**
     * 跳转activity参数
     */
    private ArrayList<YellowParams> mClickParams = null;
	
	/**
	 * 点击跳转地址(H5地址)
	 */
	private String[] mClickLinkList = null;
	
	/**
	 * 内容
	 */
	private String[] mTextList = null;
	
	/**
	 * 水平间隔
	 */
	private int mHorizonalGap = 10;
	
	private DataLoader mImageLoader = null;
	
	/**
	 * 广告在页面的位置
	 * 顶部：1；中部：2；底部：3
	 */
	private int mPageIndex = -1;
	
	/**
	 * 是否是首页
	 */
	private boolean mIsHome = true;
	
	private String mReqUrlTail = "";
	
	/**
	 * 广告是否已显示
	 */
	private boolean mHasShow = false;
	
	public AdOperatLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public AdOperatLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public AdOperatLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	private void init(Context context){
		mContext = context;
		mClickParams = new ArrayList<YellowParams>();
		mHorizonalGap = mContext.getResources().getDimensionPixelSize(R.dimen.putao_adlayout_hgap);
	}
	
	public void clear(){
		if( mImageLoader != null && mImgUrlList != null ){
			// 只清除该图片缓存
			for(int i = 0; i < mImgUrlList.length; i++ ){
				if( !TextUtils.isEmpty(mImgUrlList[i]) ){
					mImageLoader.removeCache(mImgUrlList[i]);
				}
			}
		}
		mImgUrlList = null;
		mClickTypeList = null;
		mClickActivityList = null;
		mClickLinkList = null;
		mTextList = null;
		mChildCount = 0;
		mLoadImgCount = 0;
		mHasShow = false;
	}
	

	public void setAdImg(boolean defaultLoader, PushAdBean adBean, boolean needNorner, int pageIndex, boolean isHome){
		setAdImg(defaultLoader, mContext, adBean, needNorner, pageIndex, isHome);
	}
	
	/**
	 * 设置广告数量/图片
	 */
	public void setAdImg(boolean defaultLoader, final Context context, PushAdBean adBean, boolean needNorner, int pageIndex, boolean isHome){

		// 根据imgUrl包含的url来判定包含几个广告
		String imgUrl = adBean.getAd_img_url();
		int serverCode = adBean.getAd_code();
		long startTime = adBean.getAd_start_time();
		long endTime = adBean.getAd_end_time();
		String clickType = adBean.getAd_click_type();
		String clickActivity = adBean.getAd_click_activity();
		String clickLink = adBean.getAd_click_link();
		String text = adBean.getAd_text();
		String paramsStr = adBean.getAd_params_str();
		boolean needRefresh = adBean.needRefresh();
		
		long currentTime = System.currentTimeMillis();
		if (endTime > 0 && endTime < currentTime) { // 时间过期，则删除
			imgUrl = null;
			LogUtil.i(TAG, "refreshAdData ad[" + serverCode + " - " + pageIndex + "] is timeout, need delete.");
		} else { // 时间没过期
			if (currentTime < startTime && startTime > 0) { // 广告还没开始显示，不需要显示
                LogUtil.i(TAG, "refreshAdData ad[" + serverCode + " - " + pageIndex + "] is not start show.");
                return;
			} else { // 广告显示时间已开始
				if( !needRefresh && mHasShow ){ // 不需要刷新, 且已经显示, 则不需要显示
					LogUtil.i(TAG, "refreshAdData has not show ago.");
					return;
				}
			}
		}
		
		mPageIndex = pageIndex;
		mIsHome = isHome;
		if( TextUtils.isEmpty(imgUrl) ){
			mImgUrlList = null;
		}else{
			mImgUrlList = imgUrl.split(",");
		}
		if( mImgUrlList != null && mImgUrlList.length > 0 ){
			mChildCount = mImgUrlList.length;
			if( !TextUtils.isEmpty(clickType) ){
				mClickTypeList = clickType.split(",");
			}else{
				mClickTypeList = new String[mChildCount];
			}
			if( !TextUtils.isEmpty(clickActivity) ){
				mClickActivityList = clickActivity.split(",");
			}else{
				mClickActivityList = new String[mChildCount];
			}
			if( !TextUtils.isEmpty(clickLink) ){
				mClickLinkList = clickLink.split(",");
			}else{
				mClickLinkList = new String[mChildCount];
			}
			if( !TextUtils.isEmpty(text) ){
				mTextList = text.split(",");
			}else{
				mTextList = new String[mChildCount];
			}
			
	        if(paramsStr !=null && paramsStr.length()>0){
	            try {
	                List<YellowParams> params = new Gson().fromJson(paramsStr, new TypeToken<List<YellowParams>>(){}.getType());
	                if(params != null){
	                    mClickParams.clear();
	                    mClickParams.addAll(params);
	                }
	            } catch (Exception e) {
	                
	            }
	        }
		}else{
			mChildCount = 0;
			// 为空，则清除该条数据;
			if( mCallback != null ){
				mCallback.deleteAdBean(serverCode, pageIndex);
			}
		}
		mLoadImgCount = 0;
		LogUtil.i(TAG, "setAdImg childCount: " + mChildCount);
		removeAllViews();
		setVisibility(View.GONE);
		if( mChildCount == 0 ){
			clear();
			return;
		}
		
		
		if( mImageLoader == null ){
			mImageLoader = new ImageLoaderFactory(mContext).getNormalLoader(defaultLoader, needNorner);
		}
		/** 
		 * searchActivityNum ：记录打开YellowPageSearchActivity的adBean的序号
		 * 说明　：　只有打开YellowPageSearchActivity的adBean才会取 YellowParams 
		 * 避免YellowParams信息被不需要YellowParams的 adBean 取走
		 */
		int searchActivityNum = 0;
		for(int i = 0; i < mChildCount; i++){
			ImageView imgView = new ImageView(mContext);
			
			final String childClickActivity = i < mClickActivityList.length ? mClickActivityList[i] : "";
			final int childClickType = i < mClickTypeList.length ? Integer.valueOf(mClickTypeList[i]) : 1;
			final String childClickLink = i < mClickLinkList.length ? mClickLinkList[i] : "";
			final String childText = i < mTextList.length ? mTextList[i] : "";
			YellowParams params = null;
			if(!TextUtils.isEmpty(childClickActivity) && childClickActivity.equals(YellowPageSearchActivity.class.getName())){
			    params = searchActivityNum < mClickParams.size() ? mClickParams.get(searchActivityNum) : null;
			    searchActivityNum ++;
			}
			final YellowParams childParams = params;    
			
			final int index = i;
			imgView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					try {
					    addUMengEvent(index);
						Intent intent = null;
						intent = new Intent(mContext, Class.forName(childClickActivity));
						LogUtil.d(TAG, "childClickActivity="+childClickActivity+" type="+childClickType);
						if( childClickType == 1 ){
							// 1:打开特定服务页面
							intent.putExtra("title", childText);
							if(childParams != null){
							    intent.putExtra(YellowUtil.TargetIntentParams, childParams);
							}
						}else if( childClickType == 2 ){
							// 2:打开特定链接H5页面
							intent.putExtra("title", childText);
                            
                            String reqUrl = childClickLink;
                            if(mCallback != null) {
                                mReqUrlTail = mCallback.getReqTailSign();
                            }
                            
                            if( !TextUtils.isEmpty(mReqUrlTail) ){
                            	if (childClickLink.indexOf("?") < 0) {
                            		reqUrl = childClickLink+"?"+mReqUrlTail;
                            	} else {
                            		reqUrl = childClickLink+"&"+mReqUrlTail;
                            	}
                            }
                            LogUtil.d(TAG, "title="+childText+" url="+childClickLink+" reqUrl="+reqUrl);
							intent.putExtra("url", reqUrl);
						}
						context.startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			imgView.setScaleType(ScaleType.FIT_XY);
			addView(imgView);
			mImageLoader.loadData(mImgUrlList[i], imgView, new DataLoaderListener() {
				
				@Override
				public void fillDataInView(Object result, View view) {
					// TODO Auto-generated method stub
					showAdImage(view, result);
				}
			});
		}
	}
	
	/**
	 * 显示网络下载图片
	 * 注：要等所有图片都下载OK了，才显示图片
	 */
	private void showAdImage(View view, Object result){
		mLoadImgCount++;
		LogUtil.i(TAG, "showAdImage loadImgCount: " + mLoadImgCount + " ,childCount: " + mChildCount);
		if( view == null || result == null ){
			return;
		}
		((ImageView) view).setImageBitmap((Bitmap) result);
		LogUtil.i(TAG, "showAdImage loadCount: " + mLoadImgCount + " ,childCount: " + mChildCount);
		if( mLoadImgCount == mChildCount ){
			requestLayout();
			setVisibility(View.VISIBLE);
			mHasShow = true;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int childCount = getChildCount();
		if( childCount == 0 ){
			return;
		}
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int childWidth = (width - mHorizonalGap * (childCount - 1) ) / childCount;
		for(int i = 0; i < childCount; i++){
			View child = getChildAt(i);
			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		int childCount = getChildCount();
		if( childCount == 0 ){
			return;
		}
		int leftPos = 0;
		for(int i = 0; i < childCount; i++){
			View child = getChildAt(i);
			int width = child.getWidth();
			int height = child.getHeight();
			int rightPos = leftPos + width;
			child.layout(leftPos, 0, rightPos, height);
			leftPos = rightPos + mHorizonalGap;
		}
	}

	/**
	 * 添加友盟事件
	 * 如果广告大于1，表示是第几个
	 */
	private void addUMengEvent(int index){
		String umengEventId = null;
		if( mIsHome ){
		    // 首页
		    if( mPageIndex == 1 ){
		        //add 2015-01-04 xcx start 新增统计埋点
		        MobclickAgentUtil.onEvent(mContext, UMengEventIds.DISCOVER_YELLOWPAGE_HOME_TOP_OPERATIONAL_POSITION);
		        //add 2015-01-04 xcx end 新增统计埋点
		        // 顶部
		        if( mChildCount == 1 ){
	                // 只有一个广告
	                umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_TOP;
	            }else if( mChildCount == 2 ){
	                // 两个广告
	                if( index == 0 ){
	                    umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_TOP_1;
	                }else if( index == 1 ){
	                    umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_TOP_2;
	                }
	            }
		    }else if( mPageIndex == 2 ){
                // 中部
                if( mChildCount == 1 ){
                    // 只有一个广告
                    umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_MIDDLE;
                }else if( mChildCount == 2 ){
                    // 两个广告
                    if( index == 0 ){
                        umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_MIDDLE_1;
                    }else if( index == 1 ){
                        umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_MIDDLE_2;
                    }
                }
            }else if( mPageIndex == 3 ){
                // 低部
                if( mChildCount == 1 ){
                    // 只有一个广告
                    umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_BOTTOM;
                }else if( mChildCount == 2 ){
                    // 两个广告
                    if( index == 0 ){
                        umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_BOTTOM_1;
                    }else if( index == 1 ){
                        umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_BOTTOM_2;
                    }
                }
            }
		}else{
		    // 二级页面
		    if( mPageIndex == 1 ){
		        // 顶部
		        if( mChildCount == 1 ){
		            // 只有一个广告
		            umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_MY_ACTIVITIES_TOP;
		        }else if( mChildCount == 2 ){
		            // 两个广告
		            if( index == 0 ){
		                umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_MY_ACTIVITIES_TOP_1;
		            }else if( index == 1 ){
		                umengEventId = UMengEventIds.DISCOVER_YELLOWPAGE_AD_MY_ACTIVITIES_TOP_2;
		            }
		        }
		    }
		}
		if( !TextUtils.isEmpty(umengEventId) && mContext != null){
			if( mIUMengCallback != null ){
				mIUMengCallback.onEvent(umengEventId);
			}else{
				MobclickAgentUtil.onEvent(mContext, umengEventId);
			}
		}
	}
	
	private IUMengCallback mIUMengCallback = null;
	
	public void setIUMengCallback(IUMengCallback iUMengCallback){
		mIUMengCallback = iUMengCallback;
	}
	
	/**
	 * 友盟事件统计 回调
	 */
	public interface IUMengCallback{
		void onEvent(String umengEventId);
	}

    public String getReqUrlTail() {
        return mReqUrlTail;
    }

    public void setReqUrlTail(String reqUrlTail) {
        this.mReqUrlTail = reqUrlTail;
    }
    
    public void setCallback(AdLayoutCallback callback) {
        this.mCallback = callback;
    }
	
    public interface AdLayoutCallback {
        String getReqTailSign();
        void deleteAdBean(int serverCode, int pageIndex);
    }
}
