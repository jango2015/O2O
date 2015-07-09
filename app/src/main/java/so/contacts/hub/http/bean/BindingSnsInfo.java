package so.contacts.hub.http.bean;

public class BindingSnsInfo {
	public String s_id;// [String][not null][微博用户ID]
	public int sns_id;// [int][not null][1:Sina,2:QQ]
	public String sns_name; // 用户微博名字
	public String avatar; // 用户微博头像
	public String access_token;// [String][not null][授权token]
	public int is_def;// [int][not null][1:默认账号,0:非默认账号]

}