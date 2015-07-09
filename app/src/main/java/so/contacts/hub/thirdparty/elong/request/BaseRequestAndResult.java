package so.contacts.hub.thirdparty.elong.request;

import android.util.Log;
import com.google.gson.Gson;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.thirdparty.elong.tool.ELongApiUtilHelper;
import so.contacts.hub.thirdparty.elong.tool.ELongApiUtil;
import so.contacts.hub.thirdparty.elong.bean.BaseRequst;

/**
 * @param <T> 请求的数据类型
 */
public abstract class BaseRequestAndResult<T> {
	
	private static final String TAG = "BaseRequestAndResult";
	
	public String requestForResult(){
		BaseRequst<T> requestData = new BaseRequst<T>();
		requestData.Version = ELongApiUtil.version;
		requestData.Local = ELongApiUtil.locale;
		requestData.Request = getRequestData();
    	
    	// 执行操作并返回数据
		String requestUrl = getRequestUrl(getRequestMethod(), requestData);
		LogUtil.i(TAG, "[requestUrl]: " + requestUrl);
    	return ELongApiUtilHelper.doHttpGet(requestUrl);
	}
	
	public abstract T getRequestData();
	
	public abstract String getRequestMethod();
	
	public abstract void setRequestData(T t);
	
	private static String getRequestUrl(String method, Object object) {
		String data = objectToJson(object);
		Log.i(TAG, "doClick urlTemp: " + data);
		long epoch = System.currentTimeMillis() / 1000;
		String sign = ELongApiUtilHelper.md5(epoch + ELongApiUtilHelper.md5(data + ELongApiUtil.appKey) + ELongApiUtil.appSecret);
		String requestStr = "";
		if( !ELongApiUtil.serverHost.startsWith("http://") && !ELongApiUtil.serverHost.startsWith("https://")){
			requestStr += "http" + (ELongApiUtil.NEED_SSL ? "s" : "") + "://";
		}
		requestStr +=  ELongApiUtil.serverHost + "?format=" + ELongApiUtil.DATA_TYPE + "&method="; 
		requestStr += method;
		requestStr += "&user=" + ELongApiUtil.appUser + "&timestamp=";
		requestStr += epoch;
		requestStr += "&signature=";
		requestStr += sign;
		requestStr += "&data=" + ELongApiUtilHelper.encodeUri(data);
		return requestStr;
	}
	
	private static String objectToJson(Object obj) {
		String str = ""; // JSON.toJSONStringWithDateFormat(value, "yyyy-MM-dd HH:mm:ss");
		str = new Gson().toJson(obj);
		return str;
	}
	
}
