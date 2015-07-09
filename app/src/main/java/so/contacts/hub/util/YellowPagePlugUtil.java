package so.contacts.hub.util;

import java.util.List;
import so.contacts.hub.service.PlugService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.text.TextUtils;

/**
 * 提供插件调用时的辅助功能 注：在插件页面有时需要刷新界面，根据获取的状态决定是否刷新界面，以及刷新界面那一部分
 * 
 * @author zjh
 * 
 */
public class YellowPagePlugUtil {

	private static YellowPagePlugUtil mInstance = null;

	public static YellowPagePlugUtil getInstance() {
		if (mInstance == null) {
			synchronized (YellowPagePlugUtil.class) {
				mInstance = new YellowPagePlugUtil();
			}
		}
		return mInstance;
	}

	// 刷新首页整个界面
	public static final int STATE_REFRESH_ALL_VIEW = 0;

	// 刷新常用服务打点状态
	public static final int STATE_REFRESH_OFFER_VIEW = 1;

	// 广告栏 首页面ID
	public static final int HOME_PAGE_AD_UPDATE_ID = 0;

	/**
	 * 获取刷新界面的状态 【0】：刷新首页整个界面 【1】：刷新常用服务打点状态
	 */
	private int mRefreshPlugViewState = -1;

	/**
	 * 获取过后，则清空；只保证一次刷新
	 */
	public int getRefreshPlugViewState() {
		int refreshPlugViewState = mRefreshPlugViewState;
		mRefreshPlugViewState = -1;
		return refreshPlugViewState;
	}

	public void setRefreshPlugViewState(int refreshPlugViewState) {
		mRefreshPlugViewState = refreshPlugViewState;
	}

	/**
	 * 检测PlugService是否在运行
	 * @param context
	 * @return
	 */
	public static boolean isPlugServiceWorked(Context context) {
		return isPlugServiceWorked(context, PlugService.class.getName());
	}
	
	
	/**
	 * 检测Service是否在运行
	 * @param context
	 * @param serviceName
	 * @return
	 */
	public static boolean isPlugServiceWorked(Context context, String serviceName) {
		if( TextUtils.isEmpty(serviceName) ){
			return false;
		}
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runServiceList = activityManager.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo runService : runServiceList){
			if ( serviceName.equals(runService.service.getClassName().toString()) ) {
				return true;
			}
		}
		return false;
	}
	

}
