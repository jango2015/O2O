package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;

public class QueryTipsRequest extends BaseRequestData<QueryTipsResponse> {

	public QueryTipsRequest() {
		super(ConstantsParameter.TIPS_REQUEST_CODE);
	}

	@Override
	protected QueryTipsResponse getNewInstance() {
		return new QueryTipsResponse();
	}

	@Override
	protected QueryTipsResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, QueryTipsResponse.class);
	}

}
