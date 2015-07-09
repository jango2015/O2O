package so.contacts.hub.active.bean;

import java.util.List;

import so.contacts.hub.http.bean.BaseResponseData;

/**
 * 活动历史请求响应类.
 * @author putao_lhq
 * @version 2014年10月19日
 */
public class ActiveHistoryResponse extends BaseResponseData {

	private static final long serialVersionUID = 1L;
	
	public List<ActiveHistoryBean> history_list;//[我的活动列表]

}
