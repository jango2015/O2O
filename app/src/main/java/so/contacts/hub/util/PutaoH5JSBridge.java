package so.contacts.hub.util;

import org.json.JSONObject;

import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.ui.yellowpage.YellowPageDetailActivity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

/**
 * 
 * @author putao_lhq
 * @version 2014年10月21日
 */
public class PutaoH5JSBridge{

	private static final String TAG = "PutaoH5JSBridge";
	private static final int ACTIVE_TYPE_EGG = 1;
	private static final int ACTIVE_TYPE_CLOSE = 2;
	private static final int ACTIVE_TYPE_START_SERVICE = 3;
	private static final int ACTIVE_TYPE_START_WEB = 4;
	
	private Activity mActivity;
	
	public PutaoH5JSBridge (Activity activity) {
		this.mActivity = activity;
	}
	
	@JavascriptInterface
	public void call(int type, String json_expand) {
		LogUtil.d(TAG, "call type="+type+" expand="+json_expand);
		switch (type) {
		case ACTIVE_TYPE_EGG:
			handleEggActive(json_expand);
			break;
		case ACTIVE_TYPE_CLOSE:
			this.mActivity.finish();
			break;
		case ACTIVE_TYPE_START_SERVICE:
			startLocService(json_expand);
			break;
		case ACTIVE_TYPE_START_WEB:
			startWebService(json_expand);
			break;
		default:
			break;
		}
	}
	

	/**
	 * 处理彩蛋活动
	 * @param json
	 */
	private void handleEggActive(String json) {
		if (TextUtils.isEmpty(json)) {
			return;
		}
		Intent intent = new Intent(ConstantsParameter.ACTION_REMOTE_UPDATE_ACTIVE);
		intent.putExtra("remote_update_active", json);
		mActivity.sendBroadcast(intent);
	}
	
	/**
	 * 启动本地服务
	 * @param name
	 */
	private void startLocService(String name) {
		LogUtil.i(TAG, "start local service");
		try {
			Intent intent = new Intent();
			intent.setClassName(mActivity, name);
			// modify by putao_lhq 2014年12月3日 for 循环打开webview启动不了 start
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			// modify by putao_lhq 2014年12月3日 for 循环打开webview启动不了 end
			this.mActivity.startActivityForResult(intent, 11);
		} catch (Exception e) {
			LogUtil.e(TAG, "start local service exception: " + e);
		}
	}
	
	/**
	 * 启动web页
	 * @param json_expand
	 */
	private void startWebService(String json_expand) {
		LogUtil.i(TAG, "start web service");
		try {
			if (TextUtils.isEmpty(json_expand)) {
				LogUtil.d(TAG, "url is null");
				return;
			}
			JSONObject object = new JSONObject(json_expand);
			String name = object.getString("name");
			String url = object.getString("target");
			if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url)) {
				LogUtil.d(TAG, "arguments is null");
				return;
			}
			YellowParams params = new YellowParams();
			params.setTitle(name);
			params.setUrl(url);
			Intent intent = new Intent(mActivity,
					YellowPageDetailActivity.class);
			intent.putExtra(YellowUtil.TargetIntentParams, params);
			// modify by putao_lhq 2014年12月3日 for 循环打开webview启动不了 start
			//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			// modify by putao_lhq 2014年12月3日 for 循环打开webview启动不了 end
			this.mActivity.startActivity(intent);
			this.mActivity.finish();
		} catch (Exception e) {
			LogUtil.e(TAG, "start local service exception: " + e);
		}
	}
}
