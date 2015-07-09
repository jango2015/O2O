/**
 * 
 */
package so.contacts.hub.http.bean;

import java.util.List;
import java.util.Map;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;

/**
 * @author Acher
 *
 */
public class CallLogColumnReportRequest extends
		BaseRequestData<CallLogColumnReportResponse> {

	public List<Map<String,String>> column_list;//[List<Map<String,String>>][not null][通话记录字段,Map中的KEY为字段名,VALUE为该字段的值 ，报最近五条通话记录数据]
	
	/**
	 * @param actionCode
	 */
	public CallLogColumnReportRequest(List<Map<String,String>> column_list) {
		super(ConstantsParameter.CallLogColumnReportRequestCode);
		this.column_list = column_list;
	}

	/* (non-Javadoc)
	 * @see so.contacts.hub.http.bean.BaseRequestData#getNewInstance()
	 */
	@Override
	protected CallLogColumnReportResponse getNewInstance() {
		return new CallLogColumnReportResponse();
	}

	/* (non-Javadoc)
	 * @see so.contacts.hub.http.bean.BaseRequestData#fromJson(java.lang.String)
	 */
	@Override
	protected CallLogColumnReportResponse fromJson(String json)
			throws Throwable {
		return Config.mGson.fromJson(json, CallLogColumnReportResponse.class);
	}

}
