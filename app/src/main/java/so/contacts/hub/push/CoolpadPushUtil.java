package so.contacts.hub.push;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;

import com.mdroid.core.util.SystemUtil;
import com.yulong.android.contacts.discover.R;
import com.yulong.android.cpush.clientapi.CpushManager;
import com.yulong.android.cpush.clientapi.MessageCallBack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * 处理酷派Push
 */
public class CoolpadPushUtil {
    
    private static final String TAG = "CoolpadPushUtil";
    
    public static final String CLIENTAPPID = "7001";
    
    public static final int CP_PUSH_TYPE_MSG = 1; // 酷派Push类型：消息
    
    public static final int CP_PUSH_TYPE_NOTIFY = 2; // 酷派Push类型：通知
    
    public static final int PT_PUSH_TYPE_NOTIFY = 3; // 葡萄Push类型：通知

    /**
     * 执行酷派Push
     */
    public static void doCoolpadPush(){
        CpushManager mPushManager = CpushManager.getInstance(ContactsApp.getInstance());
        mPushManager.setAsign(CLIENTAPPID);
        mPushManager.setMessageCallBack(new MessageCallBack() {

            public void receivePush(String jsonValue) {
                LogUtil.i(TAG, "doCoolpadPush receivePush jsonValue: " + jsonValue);
              String msgBody = getCoolpadPushMsgBody(jsonValue);
              if( TextUtils.isEmpty(msgBody) ){
                  LogUtil.i(TAG, "receivePush msgBody is null.");
                  return;
              }
              doParsePushMsg(msgBody);
            }

            @Override
            public void registerResult(boolean isSuccess) {
               LogUtil.i(TAG, "doCoolpadPush registerResult isSuccess: " + isSuccess);
            }

  

            @Override
            public void unRegisterResult(boolean isSuccess) {
                LogUtil.i(TAG, "doCoolpadPush unRegisterResult isSuccess: " + isSuccess);
            }

  

            @Override
            public void connectChanged(boolean netStatus) {
                LogUtil.i(TAG, "doCoolpadPush connectChanged netStatus: " + netStatus);
            }

        });
    }
    
    /**
     * 解析葡萄push内容
     * @param msgBody
     */
    public static void doPutaoPush(String msgBody) {
        sendNotification(ContactsApp.getInstance(), msgBody, PT_PUSH_TYPE_NOTIFY);
    }
    
    /**
     * 解析酷派Push内容
     * @param msg
     */
    public static void doParsePushMsg(String msgBody){
        int pushType = -1; //酷派Push的类型
        String appVersion = ""; //加APP版本
        String channel = ""; //渠道
        try {
            JSONObject obj = new JSONObject(msgBody);
            if( !obj.isNull("push_type") && obj.has("push_type") ){
                pushType = obj.getInt("push_type");
            }
            if( !obj.isNull("app_version") && obj.has("app_version") ){
                appVersion = obj.getString("app_version");
            }
            if( !obj.isNull("push_channel") && obj.has("push_channel") ){
                channel = obj.getString("push_channel");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            LogUtil.i(TAG, "doParsePushMsg parse json exception.");
        }
        
        String currentAppVersion = SystemUtil.getAppVersion(ContactsApp.getInstance());
        String currentChannel = SystemUtil.getChannelNo(ContactsApp.getInstance());
        if( !isContainStr(appVersion, currentAppVersion) ){
        	// 版本号存在 且 版本号不匹配，则不接收该Push信息
            LogUtil.i(TAG, "doParsePushMsg appVersion parse is not match.");
            return;
        }
        if( !isContainStr(channel, currentChannel) ){
            //渠道号存在 且 与渠道号不匹配，则不接收该Push信息
            LogUtil.i(TAG, "doParsePushMsg channel parse is not match.");
            return;
        }
        
        if( pushType == CP_PUSH_TYPE_NOTIFY ){
            // 通知
            sendNotification(ContactsApp.getInstance(), msgBody, pushType);
        } else if( pushType == CP_PUSH_TYPE_MSG ){
            // 消息
            //PushParseFactory.parseMsg(ContactsApp.getInstance(), msgBody);
            LogUtil.i(TAG, "doParsePushMsg need not parse msg.");
        }else{
            LogUtil.i(TAG, "doParsePushMsg pushType is exception.");
        }
    }
    
    /**
     * listStr中以";"分开的列表中是否包含str
     * @return [true]:包含;  [false]:不包含
     */
    private static boolean isContainStr(String listStr, String str){
    	if( TextUtils.isEmpty(listStr) || TextUtils.isEmpty(str)){
    		// 为空则表示包含
    		return true;
    	}
    	if( listStr.contains(";") ){
    		String[] strList = listStr.split(";");
    		for(int i = 0; i < strList.length; i++){
    			if( !TextUtils.isEmpty(strList[i]) && strList[i].equals(str) ){
    				return true;
    			}
    		}
    	}else{
    		if( listStr.equals(str) ){
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * 解析酷派Push消息body
     */
    private static String getCoolpadPushMsgBody(String pushMsg){
        String msgBody = "";
        try {
            JSONObject obj = new JSONObject(pushMsg);
            if( !obj.isNull("appmsg") && obj.has("appmsg") ){
                String appmsg = obj.getString("appmsg");
                if( TextUtils.isEmpty(appmsg) ){
                    return null;
                }
                JSONObject appmsgObj = new JSONObject(appmsg);
                if( !appmsgObj.isNull("body") && appmsgObj.has("body") ){
                    msgBody = appmsgObj.getString("body");
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            msgBody = null;
        }
        return msgBody;
    }
    
    /**
     * 执行发送通知
     */
    @SuppressWarnings("deprecation")
    private static void sendNotification(Context context, String msgBody, int type){
        String notifyTitle = "";
        String notifyContent = "";
        int intentType = -1;
        String intentActivity = "";
        String title = "";
        String url = "";
        String words = "";
        String category = "";
        int remindcode = -1;
        
        // PageIndex=-1：按照intent_activity跳转;PageIndex=0,1,2，则跳转到联系人的第几个页面
        int pageIndex = -1;
        
        JSONObject obj = null;
        try {
            obj = new JSONObject(msgBody);
            String dataStr = null;
            if (!obj.isNull("data") && obj.has("data") ){
                dataStr = obj.getString("data");
            }
            if( TextUtils.isEmpty(dataStr) ){
                LogUtil.i(TAG, "sendNotification data is null.");
                return;
            }
            
            JSONObject dataObj = new JSONObject(dataStr);
            if( !dataObj.isNull("PageIndex") && dataObj.has("PageIndex") ){
            	pageIndex = dataObj.getInt("PageIndex");
            }
            if( !dataObj.isNull("notifi_title") && dataObj.has("notifi_title") ){
            	notifyTitle = dataObj.getString("notifi_title");
            }
            if( !dataObj.isNull("notifi_content") && dataObj.has("notifi_content") ){
            	notifyContent = dataObj.getString("notifi_content");
            }
            if( !dataObj.isNull("intent_type") && dataObj.has("intent_type") ){
            	intentType = dataObj.getInt("intent_type");
            }
            if( !dataObj.isNull("intent_activity") && dataObj.has("intent_activity") ){
            	intentActivity = dataObj.getString("intent_activity");
            }
            if( !dataObj.isNull("title") && dataObj.has("title") ){
            	title = dataObj.getString("title");
            }
            if( !dataObj.isNull("url") && dataObj.has("url") ){
            	url = dataObj.getString("url");
            }
            if( !dataObj.isNull("words") && dataObj.has("words") ){
            	words = dataObj.getString("words");
            }
            if( !dataObj.isNull("category") && dataObj.has("category") ){
            	category = dataObj.getString("category");
            }
            if( !dataObj.isNull("remindcode") && dataObj.has("remindcode") ){
            	remindcode = dataObj.getInt("remindcode");
            }
            
        } catch (JSONException e) {
        	LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        notification.when = System.currentTimeMillis();
        notification.tickerText = notifyContent;
        notification.icon = R.drawable.putao_ic_launcher;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        
        /*
         * 为了统计notification点击操作，增加NotifyActivity作为中间层进行处理，将所有参数传入到NotifyActivity进行处理
         * modified by hyl 2014-11-25 start
         * old code：
            Intent notificationIntent = null;
            if( pageIndex == -1 ){
                // 跳转到指定页面
                if( intentType > 0 ){
                    try {
                        notificationIntent = new Intent(context, Class.forName(intentActivity));
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        notificationIntent = null;
                    } 
                    if( intentType == 2 ){
                        // 跳转类型为H5类型
                        notificationIntent.putExtra("url", url);
                    }
                    notificationIntent.putExtra("title", title);
                }
            }else if( pageIndex == 2 ){
                // 跳转到联系人黄页页面
                notificationIntent = new Intent("android.intent.action.YELLOWPAGE");
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }else{
                LogUtil.i(TAG, "sendNotification pageIndex: " + pageIndex);
                return ;
            }
         */
        Intent notificationIntent = null;
        if(type == CP_PUSH_TYPE_NOTIFY ){
            notificationIntent = getClickIntent(context,pageIndex,intentType,intentActivity,url,title,type,words,category,remindcode);
        } else if(type == PT_PUSH_TYPE_NOTIFY){
            notificationIntent = getClickIntent(context,pageIndex,intentType,intentActivity,url,title,type,words,category,remindcode);
        } else {
            return ;
        }
        //modified by hyl 2014-11-25 end
        
        
        if(type == CP_PUSH_TYPE_NOTIFY) {
	        // added by cj 2014/11/08 start
	        // 增加酷派通知点击量统计
	        /*
	         * 统计使用错误，该地方应该使用接收消息统计 ，而非 点击操作统计
	         * modified by hyl 2014-11-25 start
	         * old code: 
	         * MobclickAgentUtil.onEvent(context,UMengEventIds.NOTIFICATION_COOLPAD_CLICKED);
	         * 
	         */
	        MobclickAgentUtil.onEvent(context, UMengEventIds.NOTIFICATION_COOLPAD_RECEIVER);
	        //modified by hyl 2014-11-25 end
	        // added by cj 2014/11/08 end
	        
	        /*
	         * 增加酷派push消息接收成功回执
	         * add by hyl 2014-11-25 start
	         */
	        sendClickAction(context, UMengEventIds.NOTIFICATION_COOLPAD_RECEIVER);
	        //add by hyl 2014-11-25 end
        }
        
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, notifyTitle, notifyContent, contentIntent);
        notificationManager.notify(0, notification);        
    }
    
    private static Intent getClickIntent(Context context,final int pageIndex,int intentType,
            String intentActivity,String url,String title, int type, String words, String category, int remindCode) {
        Intent notificationIntent = new Intent(context,NotifyActivity.class);
        notificationIntent.putExtra("intentType", intentType);
        notificationIntent.putExtra("PageIndex", pageIndex);
        notificationIntent.putExtra("intentActivity", intentActivity);
        notificationIntent.putExtra("url", url);
        notificationIntent.putExtra("title", title);
        notificationIntent.putExtra("fromType", type);
        notificationIntent.putExtra("words", words);
        notificationIntent.putExtra("category", category);
        notificationIntent.putExtra("remindCode", remindCode);
        return notificationIntent;
    }
    
    /**
     * 本地测试采用酷派Push通道发送通知
     */
    public static void testPushNotification(){/*
        StringBuffer content = new StringBuffer();
        content.append("{");
        content.append("\"push_type\":2,");
        content.append("\"push_channel\":\"CoolPad_0001\",");
        content.append("\"app_version\":\"1.5.65\",");
        content.append("\"data\":{");
        content.append("\"notifi_title\":\"测试title\",");
        content.append("\"notifi_content\":\"测试Content\",");
        content.append("\"intent_type\":2,");
        content.append("\"intent_activity\":\"so.contacts.hub.ui.web.YellowPageTuanActivity\", ");
        content.append("\"title\":\"团购优惠100元\", ");
        content.append("\"url\":\"http://lite.m.dianping.com/zOxOda-j7H\"");
        content.append("}");
        content.append("}");
        
        CoolpadPushUtil.doParsePushMsg(content.toString());
    */}
    
    /**
     * 本地测试采用酷派Push通道发送消息-打点
     */
    public static void testPushMsgNode(){/*
        StringBuffer content = new StringBuffer();
        content.append("{");
        content.append("\"version\":1,");
        content.append("\"push_type\":1,");
        content.append("\"push_channel\":\"CoolPad_0001\",");
        content.append("\"app_version\":\"1.5.65\",");
        content.append("\"data\":[");
        content.append("{");
        content.append("\"msg_type\": 1,");
        content.append("\"type\": 1,");
        content.append("\"code\": 6,");
        content.append("\"expand_param\": {\"sub_code\": \"110,113,116,117\",\"style\": 0,\"time\": 21522953655106,\"img_url\": \"\",\"text\": \"\"}");
        content.append("}");
        content.append("]");
        content.append("}");
        
        
        CoolpadPushUtil.doParsePushMsg(content.toString());
    */}
    
    /**
     * 本地测试采用酷派Push通道发送消息-广告
     */
    public static void testPushMsgAd(){/*
        StringBuffer content = new StringBuffer();
        content.append("{");
        content.append("\"version\":1,");
        content.append("\"push_type\":1,");
        content.append("\"push_channel\":\"CoolPad_0001\",");
        content.append("\"app_version\":\"1.5.65\",");
        content.append("\"data\":[");
        content.append("{");
        content.append("\"msg_type\": 4,");
        content.append("\"code\": 0,");
        content.append("\"expand_param\": {\"page_index\": 1,\"img_url\": \"http://www.putao.cn/op/img/ad_small_01.png\",\"click_type\": \"2\",\"click_activity\": \"so.contacts.hub.ui.web.YellowPageCinemaActivity\",\"click_link\": \"http://m.gewara.com/touch/movie/index.xhtml\",\"text\": \"电影\",\"time\": 234234234}");
        content.append("}");
        content.append("]");
        content.append("}");
        
        
        CoolpadPushUtil.doParsePushMsg(content.toString());
    */}

    
    /**
     * 酷派回执
     * @param context
     * @param pushTaskId
     * @param action
     */
    public static void sendClickAction(Context context, String action) {
        Intent intent = new Intent("com.yulong.android.cpush.action.sendmessage");
        JSONObject object = new JSONObject();
        try {
            object.put("action", action);
            object.put("taskid", CLIENTAPPID);
        } catch (Exception e) {
            LogUtil.e(TAG,"",e);
        }
        intent.putExtra("jsonString", object.toString());
        context.sendBroadcast(intent);
    }


}


















