package so.contacts.hub.http.bean;

import java.util.List;

public class QueryMeInfoResponseData extends BaseResponseData {
	public String name;// [String][null able][用户名]
	public String school;// [String][null able][学校]
	public long uid; // [Long][null able][云端ID】
	public String company;// [String][null able][公司]
	public String job_title;//[String][null able][职位信息]
	public String website;//[String][null able][站点信息]
	public String birthday;// [String][null able][生日]
	public long birthday_l;// [long][null able][生日毫秒描述]
	public String email;// [String][null able][邮件]
	public String addr;// [String][null able][地址]
	public int sns_id;// [int][not null][0:未绑定，1：新浪微博，2：腾讯微博]
	public String s_id;// [String][null able][微博ID]
	public String access_token;// [String][null able][微博授权码]
	public String remark;// [String][null able][描述]
	public String sns_name;// [String][null able][邦定的社交账号的名字]
	public String avatar;// [String][null able][头像地址信息]
	public String mood;// [String][null able][最新心情信息]
	public int is_use_config;// [int][not null][是否使用通讯录标志,0:未处理,1:使用，2：不使用]
	public List<String> verification_mobile_list;// [List<String>][null
													// able][已经验证过的手机号码]
	public List<UserPhoneCardDomain> upcd_list;// [List<UserPhoneCardDomain>][null
												// able][用户号码卡片列表]

	public List<BindingSnsInfo> bsi_list; // [null able][绑定账号组]
}
