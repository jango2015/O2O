package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;

/**
 * so.contacts.hub.http.bean 用户合并账号验证接口[10016]
 * 
 * @author kzl
 * @created at 13-6-8 上午11:34
 */
public class MegerUserDataByMobileRequest extends
		BaseRequestData<MegerUserDataByMobileResponse> {
	public String mobile;// :[String][not null][要验证的手机号码]
	public String v_code;// :[String][not null][收到的短信验证码]
	public BindingSnsInfo binding_info;// :[ BindingSnsInfo][null
										// able][要授权的社交账号信息]

	public MegerUserDataByMobileRequest(String mobile, String v_code,
			BindingSnsInfo binding_info) {
		super(ConstantsParameter.MegerUserDataByMobileRequestCode);
		this.mobile = mobile;
		this.v_code = v_code;
		this.binding_info = binding_info;
	}

	@Override
	protected MegerUserDataByMobileResponse getNewInstance() {
		return new MegerUserDataByMobileResponse();
	}

	@Override
	protected MegerUserDataByMobileResponse fromJson(String json)
			throws Throwable {
		return Config.mGson.fromJson(json, MegerUserDataByMobileResponse.class);

	}
}
