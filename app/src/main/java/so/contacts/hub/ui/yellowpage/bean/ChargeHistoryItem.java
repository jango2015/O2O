package so.contacts.hub.ui.yellowpage.bean;

import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.util.TelAreaUtil;
import android.content.Context;
import android.text.TextUtils;

/**
 * Charge Telephone history item
 * 
 */
public class ChargeHistoryItem {

	private String phoneNum = "";
	private String phoneAddr = "";
	private String phoneOperator = "";
	
	public static final int ELEMENT_NUM = 3;
	
	public ChargeHistoryItem() {
		phoneNum = "";
		phoneAddr = "";
		phoneOperator = "";
	}
	
	public ChargeHistoryItem(Context context, String phone) {
		phoneNum = phone;
		phoneAddr = TelAreaUtil.getInstance().searchTel(phone, context);
		if( phoneAddr == null ){
			phoneAddr = "";
		}
		phoneOperator = TelAreaUtil.getInstance().getNetwork(phone, context);
		if( phoneOperator == null ){
			phoneOperator = "";
		}
	}

	public String getPhoneNum() {
		if( phoneNum == null ){
			phoneNum = "";
		}
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getPhoneAddr() {
		if( phoneAddr == null ){
			phoneAddr = "";
		}
		return phoneAddr;
	}

	public void setPhoneAddr(String phoneAddr) {
		this.phoneAddr = phoneAddr;
	}
	
	public String getProvinceAddr(){
		if( TextUtils.isEmpty(phoneAddr) ){
			return "";
		}
		String[] addrArray = phoneAddr.split(" ");
		if( addrArray != null && addrArray.length > 1 ){
			return addrArray[0];
		}
		return phoneAddr;
	}

	public String getPhoneOperator() {
		if( phoneOperator == null ){
			phoneOperator = "";
		}
		return phoneOperator;
	}

	public void setPhoneOperator(String phoneOperator) {
		this.phoneOperator = phoneOperator;
	}
	
	public String getProvinceAndOperator(){
		// 如： 广东 联通
		return getProvinceAddr() + " " + phoneOperator;
	}
	
	public boolean isNeedStoreAsHistory(){
		if(TextUtils.isEmpty(phoneAddr) && TextUtils.isEmpty(phoneOperator)){
			return false;
		}
		return true;
	}
	
	public String toHistoryBean(){
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(phoneNum + ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND);
		strBuffer.append(phoneAddr + ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND);
		strBuffer.append(phoneOperator);
		return strBuffer.toString();
	}
	
}
