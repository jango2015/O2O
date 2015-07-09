package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class VerificationMobileSelfRequest extends
		BaseRequestData<VerificationMobileSelfResponse> {

	public String mobile; // [string][not null][length:11][手机号码]
	public String pass_word;// [String][null able][密码]

	public VerificationMobileSelfRequest(String mobile, String pass_word) {
		super("10011");
		this.mobile = mobile;
		this.pass_word = pass_word;
	}

	@Override
	protected VerificationMobileSelfResponse getNewInstance() {
		return new VerificationMobileSelfResponse();
	}

	@Override
	protected VerificationMobileSelfResponse fromJson(String json)
			throws Throwable {
		return Config.mGson.fromJson(json, VerificationMobileSelfResponse.class);
	}

}
