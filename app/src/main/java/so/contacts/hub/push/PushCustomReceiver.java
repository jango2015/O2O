package so.contacts.hub.push;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import so.contacts.hub.core.Config;
import so.contacts.hub.ui.web.YellowPageH5Activity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Push 接收器
 * @author zjh
 */
public class PushCustomReceiver extends BroadcastReceiver {
	public static final String TAG = "PushReceiver";
	
	@Override
	public void onReceive(final Context context, final Intent intent) {
				
	        Bundle bundle = intent.getExtras();
			LogUtil.d(TAG, "[onReceive] - " + intent.getAction() + ", extras: " + printBundle(bundle));
			
	        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
	            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
	            LogUtil.d(TAG, "[onReceive] 接收Registration Id : " + regId);
	            //send the Registration Id to your server...
	                        
	        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
	        	LogUtil.d(TAG, "[onReceive] 接收到推送下来的自定义消息: ");
	        	
	    		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
	    		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
	            String file = bundle.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
	            LogUtil.d(TAG, "[onReceive] 接收到推送下来的富媒体文件: " + bundle.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH));
	            //add ljq 2014 12 29 start 增加富媒体文本文件处理
	            if(!TextUtils.isEmpty(file)){
	                String fileCotent = parseJpushFileConent(file);
	                if(!TextUtils.isEmpty(fileCotent)){
	                    PushParseFactory.parseMsg(context, fileCotent, extras);
	                }else{
	                    PushParseFactory.parseMsg(context, message, extras);
	                }
	            }else{
	                PushParseFactory.parseMsg(context, message, extras);
	            }
	            //add ljq 2014 12 29 end 增加富媒体文本文件处理
	        
	        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
	            LogUtil.d(TAG, "[onReceive] 接收到推送下来的通知");
	            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
	            LogUtil.d(TAG, "[onReceive] 接收到推送下来的通知的ID: " + notifactionId);
	        	
	            receivedNotification(context, bundle);
	            
	        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
	            LogUtil.d(TAG, "[onReceive] 用户点击打开了通知");
	            
	            openNotification(context, bundle);
	        	
	        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
	            LogUtil.d(TAG, "[onReceive] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
	            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
	        	
	        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
	        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
	        	LogUtil.w(TAG, "[onReceive]" + intent.getAction() +" connected state change to "+connected);
	        } else {
	        	LogUtil.d(TAG, "[onReceive] Unhandled intent - " + intent.getAction());
	        }
        
	}
	/**
	 * 解析激光推送富文本文件内容 暂只支持TXT
	 * @param file
	 * @return
	 */
	private String parseJpushFileConent(String file){
        String fileCotent = null;
        if (!TextUtils.isEmpty(file)) {
            if (file.contains("/")) {
                String[] tmpStr = file.split("/");
                if (tmpStr.length > 0) {
                    String lastPath = tmpStr[tmpStr.length - 1];
                    if (!TextUtils.isEmpty(lastPath)) {
                        if (lastPath.contains(".")) {
                            String[] fileStr = lastPath.split("\\.");
                            if (fileStr.length > 0) {
                                if (fileStr[1].equals("txt")) {
                                    fileCotent = YellowUtil.loadLocalTextFileString(file);
                                    LogUtil.d(TAG, "[onReceive] 接收到推送下来的富媒体文件内容: " + fileCotent);
                                }
                            }
                        }
                    }
                }
            }
        }
        return fileCotent;
	}
	
	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} 
			else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	private void receivedNotification(Context context, Bundle bundle) {
		
	}
	
	private void openNotification(Context context, Bundle bundle){
        
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		
		String customContent = extras;
		if( !TextUtils.isEmpty(customContent) ){
			String activity = "";
			int PageIndex = -1;
			String title = "";
			String url = "";
			String entryType = "";
			String words = "";
			String category = "";
			int remindcode = -1;
			try{
				JSONObject json = new JSONObject(customContent);
				if(json.has("PageIndex")) {
					PageIndex  = json.getInt("PageIndex");
				}				
				if(json.has("activity")) {
					activity  = json.getString("activity");
				}
				if(json.has("title")) {
					title  = json.getString("title");
				}
				if(json.has("url")) {
					url  = json.getString("url");
				}
				if(json.has("words")) {
					words  = json.getString("words");
				}
				if(json.has("category")) {
					category  = json.getString("category");
				}
				if(json.has("remindcode")) {
					remindcode  = json.getInt("remindcode");
				}
				
			}catch(JSONException e){
				LogUtil.e(TAG,e.getMessage());
				e.printStackTrace();
				PageIndex = -1;
			}
			
			if( PageIndex == 2 ){
				// 点击通知，跳转到黄页Home页面
				LogUtil.i(TAG, "notification go to YellowPage Home page.");
				Intent openIntent = new Intent("android.intent.action.YELLOWPAGE");
				openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				
		        // added by cj 2014/11/08 start
		        // 增加信鸽通知点击量统计
                MobclickAgentUtil.onEvent(
                        context,
                        UMengEventIds.NOTIFICATION_PUTAO_CLICKED);
                // added by cj 2014/11/08 end
                
				try {
				    context.startActivity(openIntent);
				} catch(ActivityNotFoundException e) {
				    e.printStackTrace();
				}
			} else if(!TextUtils.isEmpty(activity)) {
				// 点击通知,打开activity
				YellowParams params = new YellowParams();
				params.setTitle(title);
				params.setUrl(url);
				params.setEntry_type(YellowParams.ENTRY_TYPE_NOTIFICATION_PAGE);
				params.setWords(words);
				params.setCategory(category);
				params.setRemindCode(remindcode);

				LogUtil.i(TAG, "notification go to "+activity);

				try {
					Intent openIntent = new Intent(context, Class.forName(activity));
					openIntent.putExtra(YellowUtil.TargetIntentParams, params);
					openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					
				    context.startActivity(openIntent);
				} catch(Exception e) {
				    e.printStackTrace();
				}

			}

	        // Activity 被打开，上报服务器统计。
	        JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));
		}
	}
	
	/**
	 * 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
	 */
	public void onNotifactionClickedResult(Context context, String message) {
		if (context == null || message == null) {
			return;
		}
		String customContent = message;
		if( !TextUtils.isEmpty(customContent) ){
			int PageIndex = -1;
			try{
				JSONObject json = new JSONObject(customContent);
				PageIndex = json.getInt("PageIndex");
			}catch(JSONException e){
				PageIndex = -1;
			}
			if( PageIndex == 2 ){
				// 发送广播，跳转到黄页Home页面
				LogUtil.i(TAG, "[XGPushClickedResult go to YellowPage Home page.");
				Intent intent = new Intent("android.intent.action.YELLOWPAGE");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
		        // added by cj 2014/11/08 start
		        // 增加信鸽通知点击量统计
                MobclickAgentUtil.onEvent(
                        context,
                        UMengEventIds.NOTIFICATION_PUTAO_CLICKED);
                // added by cj 2014/11/08 end
                
				try {
				    context.startActivity(intent);
				} catch(ActivityNotFoundException e) {
				    e.printStackTrace();
				}
			}
		}
		LogUtil.i(TAG, "[XGPushClickedResult message] " + message.toString());
	}

}
