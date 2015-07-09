package so.contacts.hub.search.bean;

import so.contacts.hub.http.bean.BaseRequestData;

/**
 * 
 * @author putao_lhq
 * @version 2014年10月10日
 */
public class UpdateSearchDataRequest extends BaseRequestData<UpdateSearchDataResponse> {

	public int data_version; //本地数据版本
	public static final String ACTION_CODE = "160001";
	
	public UpdateSearchDataRequest(int data_version) {
		super(ACTION_CODE);
		this.data_version = data_version;
	}

	@Override
	protected UpdateSearchDataResponse getNewInstance() {
		return new UpdateSearchDataResponse();
	}

}
