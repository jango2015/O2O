package so.contacts.hub.search.bean;

import java.util.List;

import so.contacts.hub.http.bean.BaseResponseData;
import so.contacts.hub.ui.yellowpage.bean.SearchConfigBean;
import so.contacts.hub.ui.yellowpage.bean.SearchProvider;
import so.contacts.hub.ui.yellowpage.bean.SearchServicePoolBean;

/**
 * 远程更新响应数据
 * @author putao_lhq
 * @version 2014年10月10日
 */
public class UpdateSearchDataResponse extends BaseResponseData {

	private static final long serialVersionUID = 1L;

	public List<SearchConfigBean> configList;
	
	public List<SearchServicePoolBean> servicePoolList;
	
	public List<SearchProvider> providerList;
	
	public int data_version;
}
