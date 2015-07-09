package so.contacts.hub.util;

import so.contacts.hub.ContactsApp;

import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.ui.yellowpage.YellowPageNewQueryChargeActivity.BalanceSaveInfo;
import com.yulong.android.contacts.discover.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 话费查询
 * Sim卡工具类
 *
 */
public class SIMCardUtil {

	private TelephonyManager mTelephonyManager = null;

	public SIMCardUtil(Context context) {
		if (mTelephonyManager == null) {
			mTelephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
		}
	}

	public String getNativePhoneNumber() {
		if (mTelephonyManager == null) {
			return "";
		}
		return mTelephonyManager.getLine1Number();
	}

	public String getProvidersName() {
		if (mTelephonyManager == null) {
			return "";
		}
		return mTelephonyManager.getSubscriberId();
	}
	
	public boolean isSimValid(){
		boolean isValid = true;
		switch (mTelephonyManager.getSimState()) {
		case TelephonyManager.SIM_STATE_READY:
			isValid = true;
			break;
		case TelephonyManager.SIM_STATE_ABSENT:
		case TelephonyManager.SIM_STATE_UNKNOWN:
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
		default:
			isValid = false;
			break;
		}
		return isValid;
	}
	
	/**
	 * 解析出短信余额
	 * 按照如下规则: <金额|余额> [0-9]* <元>
	 * @param smsText
	 * @return
	 */
	public static String parseMoneyofSms(int startPos, final String smsText) throws Exception{
        float money = 0.0f;
        String moneyStr = "";
        final String yuan = ContactsApp.getInstance().getResources().getString(R.string.putao_querytel_balance_rmb);
        final String balance1 = ContactsApp.getInstance().getResources().getString(R.string.putao_querytel_balance_name1);
        final String balance2 = ContactsApp.getInstance().getResources().getString(R.string.putao_querytel_balance_name2);

        if (startPos >= smsText.length())
            return "";

        // 从第startPos位置开始查找
        int endPos = smsText.indexOf(yuan, startPos);
        if (endPos < 0) {
            return "";
        }

        // 找到‘元’后向前找money
        int numPos = endPos - 1;
        while (numPos >= startPos) {
            char s = smsText.charAt(numPos);
            if (Character.isDigit(s) || s == '.') {
                numPos--;
            } else {
                break;
            }
        }

        // 再次向前查找汉字‘金额’或‘余额’
        int hzPos = numPos;
        boolean hzFlag = false;
        /**
         * 修复当startPos==0时hzPos会为-1导致substring报错
         * OLD CODE: while (hzPos >= startPos) {
         */
        while (hzPos > startPos) {
            hzPos--;
            if (smsText.substring(hzPos, numPos + 1).indexOf(balance1) >= 0
                    || smsText.substring(hzPos, numPos + 1).indexOf(balance2) >= 0) {
                hzFlag = true;
                break;
            }
        }

        if (!hzFlag) {
            // 找不到汉字‘金额’或‘余额’, 向后递归
            return parseMoneyofSms(endPos + 1, smsText);
        }

        // 找到money数字并解析
        boolean parseFlag = false;
        try {
            moneyStr = smsText.substring(numPos + 1, endPos);
            money = Float.parseFloat(moneyStr);
            parseFlag = true;
        } catch (Exception e) {
            // 解析money
        }

        if (parseFlag) {
            return String.format("%.1f", money);
        } else {
            // 递归找下一个元
            return parseMoneyofSms(endPos + 1, smsText);
        }
    }
	
	// Sim无效卡tag
	public static final int SIM_CARD_TAG_NONE = -1;

	// Sim卡1 的tag
	public static final int SIM_CARD_TAG1 = 1;

	// Sim卡1 的tag
	public static final int SIM_CARD_TAG2 = 2;

	/**
	 * 保存卡余额相关信息
	 * @param context
	 * @param card
	 */
	public static void saveBalance(Context context, BalanceSaveInfo saveInfo, int simCardTag) {
		if((simCardTag != SIM_CARD_TAG1 && simCardTag != SIM_CARD_TAG2) || saveInfo == null){
			return;
		}
        SharedPreferences pref = context.getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
        if( TextUtils.isEmpty(saveInfo.queryMoney) ){
        	BalanceSaveInfo saveInfoTemp = loadBalance(context, simCardTag);
        	if( saveInfoTemp != null && !TextUtils.isEmpty(saveInfoTemp.queryMoney)){
        		// 如果本次需要保存的余额为空，上一次查询到的余额不为空，则不保存.
        		return;
        	}
        }
        StringBuffer sb = new StringBuffer();
        sb.append(saveInfo.simTag);
        sb.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND);
        sb.append(saveInfo.queryDate);
        sb.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND);
        sb.append(saveInfo.queryMoney);

        pref.edit().putString(String.valueOf(simCardTag), sb.toString()).commit();
	}
	
	/**
	 * 从pref中获取对应卡的余额相关信息
	 * @param context
	 * @param simTag
	 * @return
	 */
	public static BalanceSaveInfo loadBalance(Context context, int simCardTag) {
        SharedPreferences pref = context.getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
        String str = pref.getString(String.valueOf(simCardTag), "");
        if( TextUtils.isEmpty(str) ){
        	return null;
        }
        String[] tmp = str.split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND);
        if( tmp == null ){
        	return null;
        }
        if( tmp.length < BalanceSaveInfo.MEMBER_SIZE ){
        	pref.edit().putString(String.valueOf(simCardTag), null).commit();
        	return null;
        }
        BalanceSaveInfo card = new BalanceSaveInfo();
        card.simTag = tmp[0];
        card.queryDate = tmp[1];
        card.queryMoney = tmp[2];
        return card;
	}
	
	/** 
	 * 判断手机是否是飞行模式 
	 * @param context 
	 * @return 
	 */  
	public static boolean getAirplaneMode(Context context){  
	    int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),  
	                          Settings.System.AIRPLANE_MODE_ON, 0) ;  
	    return (isAirplaneMode == 1)?true:false;  
	}  	
}
