package so.contacts.hub;

import com.baidu.mapapi.SDKInitializer;
import com.sogou.hmt.sdk.manager.HmtSdkManager;

import cn.jpush.android.api.JPushInterface;
import so.contacts.hub.account.CoolCloudManager;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.broadcast.RevivePlugServiceReceiver;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.CrashHandler;
import so.contacts.hub.msgcenter.PTMessageCenterFactory;
import so.contacts.hub.push.CoolpadPushUtil;
import so.contacts.hub.service.SmsContentObserver;
import so.contacts.hub.util.CommonValueUtil;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.VolleyQueue;
import so.contacts.hub.util.YellowPageDataUtils;
import so.contacts.hub.util.YellowUtil;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

public class YellowPageApplicationProxy {

	private static PutaoAccount mPutaoAccount = null;
	
	private static final String TAG = "YellowPageApplicationProxy";

	private static Context sContext = null;

	private static boolean sIsCoolUIMode = false;

	private static YellowPageApplicationProxy INSTANCE;

	static YellowPageApplicationProxy sYellowPageApplicationProxy;

	public synchronized static Context getContext() {
		return sContext;
	}

	public synchronized static void setApplicationContext(Context context) {
		LogUtil.d(TAG, "setApplicationContext context = " + context);
		sContext = context;
	}

	public synchronized static void setCoolUIMode(boolean isCoolUIMode) {
		LogUtil.d(TAG, "setCoolUIMode isCoolUIMode = " + isCoolUIMode);
		sIsCoolUIMode = isCoolUIMode;
	}

	public synchronized static boolean isCoolUIVersion() {
		LogUtil.d(TAG, "isCoolUIVersion sIsCoolUIMode = " + sIsCoolUIMode);
		return sIsCoolUIMode;
	}

	public synchronized static YellowPageApplicationProxy getInstance() {
		LogUtil.d(TAG, "YellowPageApplicationProxy getInstance called ");
		if (INSTANCE == null) {
			INSTANCE = new YellowPageApplicationProxy();
		}
		return INSTANCE;
	}

	private YellowPageApplicationProxy() {
	}

	public void onCreate() {
		LogUtil.d(TAG, "onCreate called");
		if (ContactsHubUtils.isMainProcess(getContext())) {
			initYellowApp();
		}
	}

	public static void initYellowApp() {
	    LogUtil.d(TAG, "initPlug initYellowApp ="+System.currentTimeMillis());
		// 初始化程序中使用的常量
		CommonValueUtil.getInstance().initCommonData(getContext());
        // 初始化地理位置
		//modified by ffh start 2015-01-13去掉地理位置的初始化操作
//        YellowUtil.loadGpsLocation(ContactsApp.getContext());
		// 异常捕获处理
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getContext());
		
        VolleyQueue.init(getContext());

		SDKInitializer.initialize(getContext());
        if(!HmtSdkManager.getInstance().isInit()) {
            HmtSdkManager.getInstance().init(getContext());
        }
		
		addSmsListener();// add by hyl 2014-9-19 注册短信监听
		
		JPushInterface.init(ContactsApp.getContext()); // add by cj 2015-01-13 默认注册jpush
		
		// 用全局引用hold住PutaoAccount引用,避免被释放
        mPutaoAccount = PutaoAccount.getInstance();

		/** 处理酷派Push消息 */
		CoolpadPushUtil.doCoolpadPush();
		LogUtil.d(TAG, "initPlug initYellowApp end="+System.currentTimeMillis());
		
		//add by zj 2014-12-22 注册提醒中心业务
		PTMessageCenterFactory.registBussness(getContext());
	}
	
	/**
	 * 注册短信监听 add by hyl 2014-9-19
	 */
	private static void addSmsListener() {
		SmsContentObserver smsContentObserver = new SmsContentObserver(
				getContext(), new Handler());
		getContext().getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true, smsContentObserver);
	}
	
	 /**
     * 通过全局定时器，定时检测PlugService是否存活
     */
    private static void checkPlugServiceLive(){
        Intent intent = new Intent(sContext, RevivePlugServiceReceiver.class);
        intent.setAction("so.contacts.hub.check.plugservice");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        PendingIntent senderIntent = PendingIntent.getBroadcast(sContext, 0, intent, 0);
        
        AlarmManager alarmManager = (AlarmManager)sContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, 
                System.currentTimeMillis(), 1 * 60 * 1000, senderIntent); // 1000 * 60 * 30
    }
}
