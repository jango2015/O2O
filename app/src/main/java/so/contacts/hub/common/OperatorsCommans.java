package so.contacts.hub.common;

/**
 * 话费查询
 * 运营商-常量
 *
 */
public class OperatorsCommans {

	// 移动： YE - 10086
	public static final int TAG_YIDONG= 1;

	public static final String NUM_YIDONG = "10086";

	public static final String TEXT_YIDONG = "YE";
	
	// 联通： 102 - 10010
	public static final int TAG_LIANTONG = 2;

	public static final String NUM_LIANTONG = "10010";

	public static final String TEXT_LIANTONG = "102";
	
	// 电信： 102 - 10001
	public static final int TAG_DIANXIN = 3;

	public static final String NUM_DIANXIN = "10001";

	public static final String TEXT_DIANXIN = "102";
	

	/**
	 * 追踪短信 ACTION
	 */
	public static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	
	// Android 原生 短信接收广播
	//public static final String RECV_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	// Sim卡状态监听
	public static final String SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
	
	// 酷派 短信接收广播
	public static final String COOLPAD_RECV_SMS_ACTION = "com.yulong.mms.NEW_MESSAGE_EXTERNAL";
	
    // 酷派发送短信状态接收广播
    public static final String COOLPAD_SENT_SMS_ACTION = "com.yulong.android.contacts.send.message.result";	
}



