package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class ActiveMobileApplyRequest extends
		BaseRequestData<ActiveMobileApplyResponse> {

	public String mobile; // [string][not null][length:11][验证手机号码]
	public String pass_word; // [string][null able][设置密码]响应
	public int is_binding;// [int][not null][0:找回密码,1:登陆或者验证]

	public ActiveMobileApplyRequest(String mobile, String password,
			int is_binding) {
		super("10003");
		this.mobile = mobile;
		this.pass_word = password;
		this.is_binding = is_binding;
	}

	@Override
	protected ActiveMobileApplyResponse getNewInstance() {
		return new ActiveMobileApplyResponse();
	}

	@Override
	protected ActiveMobileApplyResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, ActiveMobileApplyResponse.class);
	}

}
