package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;

/**
 * 用户登陆后授权社交账号[10014]
 */
public class NetSourceAuthExRequest extends
		BaseRequestData<NetSourceAuthExResponse> {
	public String s_id;// :[string][not null][社交域账号]
	public int sns_id;// :[int][not null][1:新浪,2:腾讯,3:人人]
	public String access_token;// :[String][not null][社交域登录标识]

	public NetSourceAuthExRequest(String s_id, int sns_id, String access_token) {
		super(ConstantsParameter.NetSourceAuthExRequestCode);
		this.s_id = s_id;
		this.sns_id = sns_id;
		this.access_token = access_token;
	}

	@Override
	protected NetSourceAuthExResponse fromJson(String json) {
		return Config.mGson.fromJson(json, NetSourceAuthExResponse.class);
	}

	@Override
	protected NetSourceAuthExResponse getNewInstance() {
		return new NetSourceAuthExResponse();
	}
}
