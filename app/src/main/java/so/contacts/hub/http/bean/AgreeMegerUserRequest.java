package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;

/**
 * so.contacts.hub.http.bean 用户同意合并账号接口[10015]
 * 
 * @author kzl
 * @created at 13-6-8 上午11:27
 */
public class AgreeMegerUserRequest extends
		BaseRequestData<AgreeMegerUserResponse> {
	public long target_u_id;// :[long][not null][要合并的目标用户的ID]

	public AgreeMegerUserRequest(long target_u_id) {
		super(ConstantsParameter.AgreeMegerUserRequestCode);
		this.target_u_id = target_u_id;
	}

	@Override
	protected AgreeMegerUserResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, AgreeMegerUserResponse.class);
	}

	@Override
	protected AgreeMegerUserResponse getNewInstance() {
		return new AgreeMegerUserResponse();
	}
}
