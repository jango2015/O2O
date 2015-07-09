package so.contacts.hub.http.bean;

import java.util.List;

import so.contacts.hub.ui.yellowpage.bean.ChargeTelephoneHistoryBean;

/**
 * 话费充值历史
 * 
 */
public class ChargeTelephoneHistoryResponse extends BaseResponseData {
	
	public List<ChargeTelephoneHistoryBean> order_trace_list;

}
