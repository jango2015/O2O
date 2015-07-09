package so.contacts.hub.http.bean;

/**
 * so.contacts.hub.http.bean
 * 
 * @author kzl
 * @created at 13-6-8 上午11:20
 */
public class NetSourceAuthExResponse extends BaseResponseData {
	public String token;// :[String][not null][令牌]
	/** [not null][0:授权成功，1:需要验证手机号码,2:不允许验证] **/
	public int status;// :[int][not null][0:授权成功，1:需要验证手机号码，2:不允许验证]
	public MergeTargetUser m_target_user;// :[ MergeTargetUser][null
											// able][目标用户的账户情况]
}
