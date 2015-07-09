package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class MobileLoginRequest extends BaseRequestData<MobileLoginResponse> {

	public String mobile; // [String][not null][手机号码]
	public String pass_word; // [String][not null][密码]

	public MobileLoginRequest(String mobile, String password) {
		super("10009");
		this.mobile = mobile;
		this.pass_word = password;
	}

	@Override
	protected MobileLoginResponse getNewInstance() {
		return new MobileLoginResponse();
	}

	@Override
	protected MobileLoginResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, MobileLoginResponse.class);
	}

}
