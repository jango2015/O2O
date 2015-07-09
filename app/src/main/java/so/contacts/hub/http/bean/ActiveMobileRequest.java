package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class ActiveMobileRequest extends BaseRequestData<ActiveMobileResponse> {

	public String mobile; // [string][not null][length:11][手机号码]
	public String verification_code;// [string][not null][认证码]

	public ActiveMobileRequest(String mobile, String verification_code) {
		super("10004");
		this.mobile = mobile;
		this.verification_code = verification_code;
	}

	@Override
	protected ActiveMobileResponse getNewInstance() {
		return new ActiveMobileResponse();
	}

	@Override
	protected ActiveMobileResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, ActiveMobileResponse.class);
	}

}
