package so.contacts.hub.http.bean;

public class ActiveMobileApplyResponse extends BaseResponseData {
	public int is_verification; // [int][not null][0:未验证,1:已验证]
	public String mcode; // [string][not null][机器码]
	public String mo_dest_num; // [string][not null][主动上行认证目标号码]
	// 注:该接口用于手机号码注册以及手机号码验证的申请,
	// 1. 在注册时要填写request中的pass_word,且token为空
	// 响应时注意判断is_verification:的值,如果为1,则不允许注册.
	// 2. 在验证手机号码时不需要填写pass_word，需要token
	// 3. 在找回手机号码时不需要填写pass_word
	public int status;// :[int][not null][0:申请验证手机号码成功，1:需要验证手机后合并,2:不允许验证]
	public MergeTargetUser m_target_user;// :[ MegerTargetUser][null
											// able][目标用户的账户情况]
	public String verification_code;//[String][null able][生成的验证码]
}
