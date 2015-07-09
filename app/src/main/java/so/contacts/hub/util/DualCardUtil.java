package so.contacts.hub.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 双卡发送短信 处理
 * 注：MTK平台双卡 卡1： TelephonyManager 
 *        卡2： com.mediatek.telephony.TelephonyManagerEx
 */
public class DualCardUtil {

	/**
	 * 获取TelphonyManagerEx
	 * 注意：mtk4.4系统双卡时使用TelphonyManagerEx获取SIM卡相关信息，不能使用TelphonyManager
	 */
	public static Object getTelephonyManagerEx() {
		Object telphonyManager = null;
		try {
			Class<?> telClass = Class.forName("com.mediatek.telephony.TelephonyManagerEx");
			telphonyManager = telClass.getMethod("getDefault").invoke(telClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return telphonyManager;
	}
	
	/**
	 * 获取Sim卡2 IMSI编号
	 */
	public static String getSimCardImsi(Context context, int slotId){
		if( slotId == 1 ){
			return getSimCardImsi1(context);
		}else{
			return getSimCardImsi2(context);
		}
	}

	/**
	 * 获取Sim卡1 IMSI编号
	 */
	private static String getSimCardImsi1(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSubscriberId();
	}

	/**
	 * 获取Sim卡2 IMSI编号
	 */
	private static String getSimCardImsi2(Context context) {
		String imsi = "";
		Object telephonyManager = getTelephonyManagerEx();
		try {
			Method method = getSimServiceProductNameMethod(telephonyManager);
			if (null != method) {
				// Sim卡1的SimCardId=0; Sim卡2的SimCardId=1
				imsi = (String) method.invoke(telephonyManager, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imsi;
	}

	/**
	 * 获取Sim卡1 是否可用
	 */
	public static boolean isAvaliableWithSimCard1(Context context){
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int state = telephonyManager.getSimState();
		if (state == TelephonyManager.SIM_STATE_READY) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取Sim卡2 是否可用
	 */
	public static boolean isAvaliableWithSimCard2(Context context){
		 Object tm = getTelephonyManagerEx();
		 boolean isAvaliable = false;
		 try {
			 Method method = tm.getClass().getMethod("getSimState", int.class);
			 if (null != method) {
				 // Sim卡1的SimCardId=0; Sim卡2的SimCardId=1
				 int state = (Integer)method.invoke(tm, 1);
				 if (state == TelephonyManager.SIM_STATE_READY) {
					 isAvaliable =  true;
			     }
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		return isAvaliable;
	}

	private static Method getSimServiceProductNameMethod(Object obj) {
		Method method = null;
		try {
			method = obj.getClass().getMethod("getSubscriberId", int.class);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (null == method) {
			try {
				method = obj.getClass().getMethod("getSubscriberIdGemini",
						int.class);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		if (null == method) {
			try {
				method = obj.getClass().getMethod("getSubscriberIdExt",
						int.class);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return method;
	}

}
