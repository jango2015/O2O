package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class DelUserPhoneCardRequest extends
		BaseRequestData<DelUserPhoneCardResponse> {

	public long upc_id;// [long][not null][删除号码的ID]

	public DelUserPhoneCardRequest(long upc_id) {
		super("10013");
		this.upc_id = upc_id;
	}

	@Override
	protected DelUserPhoneCardResponse getNewInstance() {
		return new DelUserPhoneCardResponse();
	}

	@Override
	protected DelUserPhoneCardResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, DelUserPhoneCardResponse.class);
	}
}
