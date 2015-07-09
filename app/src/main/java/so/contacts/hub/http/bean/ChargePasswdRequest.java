package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class ChargePasswdRequest extends BaseRequestData<ChargePasswdResponse> {

	public String new_pass_word;// [String][not null][新密码]

	public ChargePasswdRequest(String newPassword) {
		super("10008");
		this.new_pass_word = newPassword;
	}

	@Override
	protected ChargePasswdResponse getNewInstance() {
		return new ChargePasswdResponse();
	}

	@Override
	protected ChargePasswdResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, ChargePasswdResponse.class);
	}

}
