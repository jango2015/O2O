package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

/**
 * so.contacts.hub.businessbean
 * 
 * @author kzl
 * @created at 13-5-31 下午2:41
 */
public class QueryRegContactsRequest extends
		BaseRequestData<QueryRegContactsResponse> {

	public QueryRegContactsRequest() {
		super("20012");
	}

	@Override
	protected QueryRegContactsResponse getNewInstance() {
		return new QueryRegContactsResponse();
	}

	@Override
	public QueryRegContactsResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, QueryRegContactsResponse.class);
	}
}
