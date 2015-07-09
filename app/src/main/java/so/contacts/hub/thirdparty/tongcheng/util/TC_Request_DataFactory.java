package so.contacts.hub.thirdparty.tongcheng.util;

import java.util.Hashtable;

public class TC_Request_DataFactory {
	
	/**
	 * 获取数字签名
	 * @param serviceName
	 * @param reqTime
	 * @return
	 */
	public static String getRequestDigitalSign(String serviceName, String reqTime){
		String digitalSign = "";
		
		///获取请求头 以及 数字签名 start
		Hashtable<String, String> ht = new Hashtable<String, String>(); // 将参数放入Hashtable中，便于操作
		ht.put("version", TC_Common.TC_VERSION); 			// 接口协议版本号，详见接口协议文档
		ht.put("accountId", TC_Common.TC_ACCOUNT_ID); 	// API帐户ID(小写)，待申请审批通过后发放
		ht.put("accountKey", TC_Common.TC_ACCOUNT_PW); 	// API帐户密钥，待申请审批通过后发放
		ht.put("serviceName", serviceName); 					// 调用接口的方法名称
		ht.put("reqTime", reqTime); 							// 当前日期
		try {
			digitalSign = TC_Tool.createDigitalSign(ht);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return digitalSign;
	}
	
	/**
	 * 获取请求头
	 * @param serviceName
	 * @param digitalSign
	 * @param reqTime
	 * @return
	 */
	public static String getRequestHeadBySignAndTime(String serviceName, String digitalSign, String reqTime){
		String strRequest = ""; // 请求XML字符串
		strRequest = strRequest + "<header>";
		strRequest = strRequest + "<version>" + TC_Common.TC_VERSION + "</version>";
		strRequest = strRequest + "<accountID>" + TC_Common.TC_ACCOUNT_ID + "</accountID>";
		strRequest = strRequest + "<serviceName>" + serviceName + "</serviceName>";
		try {
			strRequest = strRequest + "<digitalSign>" + digitalSign + "</digitalSign>";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		strRequest = strRequest + "<reqTime>" + reqTime + "</reqTime>";
		strRequest = strRequest + "</header>";
		
		return strRequest;
	}

	/**
	 * 获取请求头
	 * @param serviceName
	 * @return
	 */
	public static String getRequestHead(String serviceName){
		Hashtable<String, String> ht = new Hashtable<String, String>(); // 将参数放入Hashtable中，便于操作

		ht.put("version", TC_Common.TC_VERSION); 			// 接口协议版本号，详见接口协议文档
		ht.put("accountId", TC_Common.TC_ACCOUNT_ID); 	// API帐户ID(小写)，待申请审批通过后发放
		ht.put("accountKey", TC_Common.TC_ACCOUNT_PW); 	// API帐户密钥，待申请审批通过后发放
		ht.put("serviceName", serviceName); 					// 调用接口的方法名称
		ht.put("reqTime", TC_Tool.getFormatData()); 							// 当前日期
		
		String strRequest = ""; // 请求XML字符串
		strRequest = strRequest + "<header>";
		strRequest = strRequest + "<version>" + ht.get("version") + "</version>";
		strRequest = strRequest + "<accountID>" + ht.get("accountId") + "</accountID>";
		strRequest = strRequest + "<serviceName>" + ht.get("serviceName") + "</serviceName>";
		try {
			strRequest = strRequest + "<digitalSign>" + TC_Tool.createDigitalSign(ht) + "</digitalSign>";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		strRequest = strRequest + "<reqTime>" + ht.get("reqTime") + "</reqTime>";
		strRequest = strRequest + "</header>";
		
		return strRequest;
	}
	
	public static String getBodyWithDivisionInfoByName(String cityName){
		StringBuffer bodyData = new StringBuffer();
		bodyData.append("<body>");
		if( cityName != null && cityName != "" ){
			bodyData.append("<divisionName>" + cityName + "</divisionName>");
		}
		bodyData.append("</body>");
		return bodyData.toString();
	}
	
	public static String getBodyWithDivisionInfoByProvinceId(String provinceId){
		StringBuffer bodyData = new StringBuffer();
		bodyData.append("<body>");
		if( provinceId != null && provinceId != "" ){
			bodyData.append("<provinceId>" + provinceId + "</provinceId>");
		}
		bodyData.append("</body>");
		return bodyData.toString();
	}
	
}
