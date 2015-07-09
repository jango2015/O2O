package so.contacts.hub.http.bean;

import java.util.List;

/**
 * so.contacts.hub.http.bean
 * 
 * @author kzl
 * @created at 13-5-31 下午2:44
 */
public class QueryRegContactsResponse extends BaseResponseData {
	public List<String> reg_mobile_summary_list;// :[List<String>][null
												// able][开通的号码摘要列表]
}
