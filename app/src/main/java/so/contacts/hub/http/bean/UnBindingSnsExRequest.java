package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;

public class UnBindingSnsExRequest extends
		BaseRequestData<UnBindingSnsExResponse> {
	public String s_id;
	public int sns_id;

	public UnBindingSnsExRequest(String s_id, int sns_id) {
		super(ConstantsParameter.UnBindingSnsExRequestCode);
		this.s_id = s_id;
		this.sns_id = sns_id;
	}

	@Override
	protected UnBindingSnsExResponse fromJson(String json) {
		return Config.mGson.fromJson(json, UnBindingSnsExResponse.class);
	}

	@Override
	protected UnBindingSnsExResponse getNewInstance() {
		return new UnBindingSnsExResponse();
	}

}
