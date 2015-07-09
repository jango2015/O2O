package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class NewVersionRequestData extends
		BaseRequestData<NewVersionResponseData> {

	public NewVersionRequestData() {
		super("00002");
	}

	@Override
	protected NewVersionResponseData fromJson(String json) {
		return Config.mGson.fromJson(json, NewVersionResponseData.class);
	}

	@Override
	protected NewVersionResponseData getNewInstance() {
		return new NewVersionResponseData();
	}
}
