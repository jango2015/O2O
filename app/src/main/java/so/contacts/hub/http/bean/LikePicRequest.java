package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class LikePicRequest extends BaseRequestData<LikePicResponse> {

	public long pic_id;

	public LikePicRequest(long pic_id) {
		super("40006");
		this.pic_id = pic_id;
	}

	@Override
	protected LikePicResponse getNewInstance() {
		return new LikePicResponse();
	}

	@Override
	protected LikePicResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, LikePicResponse.class);
	}

}
