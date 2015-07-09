package so.contacts.hub.http.bean;

public class NetSourceAuthResponse extends BaseResponseData {
	// public String mobile;//[string][not null][验证手机号码]
	public String token;// [string][null able][令牌]
	public int is_new_user;
	// public int uid;// :[int][null able][UID]
	// public String verification_code;// :[int][null able][UID]
	// public String name;// [String][null able][用户名]
	// public String school;// [String][null able][学校]
	// public String company;// [String][null able][公司]
	// public long birthday_l;// [long][null able][生日毫秒描述]
	// public String email;// [String][null able][邮件]
	// public String addr;// [String][null able][地址]
	// public int sns_id;// [int][not null][0:未绑定，1：新浪微博，2：腾讯微博]
	// public String s_id;// [String][null able][微博ID]
	// public String access_token;// [String][null able][微博授权码]
	// public String remark;// [String][null able][描述]
	// public List<String> tags;// [List<String>][null able][标签]
	//
	// public String sns_name;// [String][null able][邦定的社交账号的名字]
	// public String avatar;// [String][null able][头像地址信息]
	// public String mood;// [String][null able][最新心情信息]
	// public List<String> verification_mobile_list;// [List<String>][null
	// able][已经验证过的手机号码]
	//
	// public List<BindingSnsInfo> bsi_list; //[null able][绑定账号组]
	// public String config; // [String][not
	// null][配置信息,格式为：是否同步头像:微博姓名备注:关联时系统自动通知:推送通知,1为是选项，0为否选项，如config传值为1:0:1:1]
	// public String config_ext; //1.6.0后配置项用JSON

	// public String getTags() {
	// if (tags != null) {
	// StringBuilder builder = new StringBuilder();
	// for (String text : tags) {
	// if (builder.length() > 0) {
	// builder.append(",");
	// }
	// builder.append(text);
	// }
	// return builder.toString();
	// }
	// return "";
	// }
}
