package so.contacts.hub.util;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import cn.jpush.android.api.JPushInterface;

import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMPlatformData;

/**
 * 统计数据包装类
 * @author Administrator
 *
 */
public class MobclickAgentUtil {
    
	/**
	 * 是否打开葡萄统计
	 */
	private static final boolean NEED_PUTAO_ANALYTICS = true;
	
    public static void flush(Context arg0){
        MobclickAgent.flush(arg0);
    }
    
    public static void getConfigParams(Context arg0,String arg1){
        MobclickAgent.getConfigParams(arg0, arg1);
    }
    
    //包含葡萄SDK
    public static void onEvent(Context arg0,String arg1){
    	if( NEED_PUTAO_ANALYTICS ){
    		//com.putao.analytics.MobclickAgent.onEvent(arg0, arg1);
    	}
        MobclickAgent.onEvent(arg0, arg1);
    }
    
    public static void onEvent(Context arg0,String arg1,int arg2){
        
        MobclickAgent.onEvent(arg0, arg1, arg2);
    }
    
    public static void onEvent(Context arg0,String arg1,String arg2){
        MobclickAgent.onEvent(arg0, arg1, arg2);
    }
    
    public static void onEvent(Context arg0,String arg1,String arg2,int arg3){
        MobclickAgent.onEvent(arg0, arg1, arg2,arg3);
    }
    
    //包含葡萄SDK 
    public static void onEvent(Context arg0,String arg1,Map<String, String> arg2){
    	if( NEED_PUTAO_ANALYTICS ){
    		//com.putao.analytics.MobclickAgent.onEvent(arg0, arg1, arg2);
    	}
        MobclickAgent.onEvent(arg0, arg1, arg2);
    }
    
    public static void onEventBegin(Context arg0,String arg1){
        MobclickAgent.onEventBegin(arg0, arg1);
    }
    
    public static void onEventBegin(Context arg0,String arg1,String arg2){
        MobclickAgent.onEventBegin(arg0, arg1,arg2);
    }
    
    public static void onEventDuration(Context arg0,String arg1,long arg2){
        MobclickAgent.onEventDuration(arg0, arg1,arg2);
    }
    
    public static void onEventDuration(Context arg0,String arg1,Map<String, String> arg2,long arg3){
        MobclickAgent.onEventDuration(arg0, arg1, arg2, arg3);
    }
    
    public static void onEventDuration(Context arg0,String arg1,String arg2,long arg3){
        MobclickAgent.onEventDuration(arg0, arg1, arg2, arg3);
    }
    
    public static void onEventEnd(Context arg0,String arg1){
        MobclickAgent.onEventEnd(arg0, arg1);
    }
    
    public static void onEventEnd(Context arg0,String arg1,String arg2){
        MobclickAgent.onEventEnd(arg0, arg1,arg2);
    }
    
    public static void onEventValue(Context arg0,String arg1,Map<String, String> arg2,int arg3){
        MobclickAgent.onEventValue(arg0, arg1,arg2,arg3);
    }
    
    public static void onKillProcess(Context arg0){
        MobclickAgent.onKillProcess(arg0);
    }
    
    public static void onKVEventBegin(Context arg0,String arg1,Map<String, String> arg2,String arg3){
        MobclickAgent.onKVEventBegin(arg0, arg1, arg2, arg3);
    }
    
    public static void onKVEventEnd(Context arg0,String arg1,String arg2){
        MobclickAgent.onKVEventEnd(arg0, arg1,arg2);
    }
    
    public static void onPageEnd(String arg0){
        MobclickAgent.onPageEnd(arg0);
    }
    
    public static void onPageStart(String arg0){
        MobclickAgent.onPageStart(arg0);
    }
    
    //包含葡萄SDK 
    public static void onPause(Context arg0){
    	if( NEED_PUTAO_ANALYTICS ){
    		//com.putao.analytics.MobclickAgent.onPause(arg0);
    	}
        MobclickAgent.onPause(arg0);
        
        if(arg0 instanceof Activity) {
            JPushInterface.onPause(arg0);
        }
    }
    //包含葡萄SDK 
    public static void onResume(Context arg0){
    	if( NEED_PUTAO_ANALYTICS ){
    		//com.putao.analytics.MobclickAgent.onResume(arg0);
    	}
        MobclickAgent.onResume(arg0);
        
        if(arg0 instanceof Activity) {
            JPushInterface.onResume(arg0);
        }
    }
    
    public static void onResume(Context arg0,String arg1,String arg2){
        MobclickAgent.onResume(arg0,arg1,arg2);
    }
    
    public static void onSocialEvent(Context arg0,UMPlatformData arg1){
        MobclickAgent.onSocialEvent(arg0,arg1);
    }
    
    public static void onSocialEvent(Context arg0,String arg1 , UMPlatformData arg2){
        MobclickAgent.onSocialEvent(arg0,arg1,arg2);
    }
    
    public static void onSocialEvent(Context arg0){
        MobclickAgent.onSocialEvent(arg0);
    }
    
    public static void setDebugMode(boolean arg0){
        MobclickAgent.setDebugMode(arg0);
    }
 
}
