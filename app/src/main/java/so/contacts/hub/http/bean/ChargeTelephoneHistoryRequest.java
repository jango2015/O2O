package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;

/**
 * 话费充值历史
 *
 */
public class ChargeTelephoneHistoryRequest extends BaseRequestData<ChargeTelephoneHistoryResponse> {
	
	// 每页条数
	private int page_size;
	
	// 页码,从零开始
	private int pageNo;
	
	public ChargeTelephoneHistoryRequest(String deviceCode) {
		super(ConstantsParameter.ChargeTelephoneHistoryDataCode);
		// TODO Auto-generated constructor stub
		// 注：device_code必填
		device_code = deviceCode;
	}
	
	public ChargeTelephoneHistoryRequest(String deviceCode, int pageno, int pageSize) {
		super(ConstantsParameter.ChargeTelephoneHistoryDataCode);
		// 注：device_code必填
		device_code = deviceCode;
		pageNo = pageno;
		page_size = pageSize;
	}
	
	public void setPageSize(int pageSize){
		page_size = pageSize;
	}
	
	public void setPageNo(int pageno){
		pageNo = pageno;
	}
	

	@Override
	protected ChargeTelephoneHistoryResponse getNewInstance() {
		// TODO Auto-generated method stub
		return new ChargeTelephoneHistoryResponse();
	}
	
	@Override
	protected ChargeTelephoneHistoryResponse fromJson(String json)
			throws Throwable {
		// TODO Auto-generated method stub
		return Config.mGson.fromJson(json, ChargeTelephoneHistoryResponse.class);
	}

}
