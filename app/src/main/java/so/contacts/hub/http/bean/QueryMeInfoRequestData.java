package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;

public class QueryMeInfoRequestData extends
		BaseRequestData<QueryMeInfoResponseData> {

	public QueryMeInfoRequestData() {
		super(ConstantsParameter.QueryMeInfoRequestDataCode);
	}

	@Override
	protected QueryMeInfoResponseData fromJson(String json) {
		return Config.mGson.fromJson(json, QueryMeInfoResponseData.class);
	}

	@Override
	protected QueryMeInfoResponseData getNewInstance() {
		return new QueryMeInfoResponseData();
	}
}
