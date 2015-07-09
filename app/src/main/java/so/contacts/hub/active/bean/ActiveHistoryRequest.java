package so.contacts.hub.active.bean;

import so.contacts.hub.http.bean.BaseRequestData;

/**
 * 
 * @author putao_lhq
 * @version 2014年10月19日
 */
public class ActiveHistoryRequest extends BaseRequestData<ActiveHistoryResponse> {

	private static final String ACTION_CODE = "170002";
	
	public ActiveHistoryRequest() {
		super(ACTION_CODE);
	}

	@Override
	protected ActiveHistoryResponse getNewInstance() {
		return new ActiveHistoryResponse();
	}

}
