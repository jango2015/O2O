package so.contacts.hub.businessbean;

import java.util.List;

public class ContactInfo {
	public String name;// :[String][not null][联系人名字]
	public String mobile;//[String][null able][号码明文] 
	public String mobile_summary;// :[String][not null][号码摘要]
	public int is_reg;// :[int][not null][0:不是葡萄用户,1:是葡萄用户]
	public int is_binding_sns_self;// :[0:不是,1:是]
	public String addr;// :[String][null able][地址]
	public String school;// :[String][null able][学校]
	public String company;// :[String][null able][公司]
	public long birthday_l;// :[long][null able][生日]
	public String email;// :[String][null able][邮件]
	public String mood;// :[String][null able][心情]
	public int relationship;// :[int][null able][联系人热度，1至6]
	public String remark;// [String][null able][描述]
	public List<String> tags;// [List<String>][null able][标签]
	public int is_enshrine; // int][null able][0:未收藏,1:收藏]
	public String approve; // [null able][认证信息]
	// public List<RelationInfo> relations;

	/* 用于更新其他信息 */
	public String s_id;// :[String][null able][联系人关联微博用户ID]
	public int sns_id;// :[int][null able][1:Sina,2:QQ,微博类型]
	public String sns_name;// :[String][null able][微博名字]
	public String avatar_img_url;// :[String][null able][微博头像]

	// public boolean isEnshrine(){
	// return is_enshrine == 1;
	// }

	// public boolean isLinked() {
	// return relations!=null && relations.size()>0;
	// }

	public boolean isBind() {
		return is_binding_sns_self == 1;
	}

	public boolean isReg() {
		return is_reg == 1;
	}

	// public boolean needChange(ContactInfo info) {
	// if (isBind()) {
	// return false;
	// } else if (isReg() && isLinked()) {
	// return info.isBind();
	// } else if (isReg()) {
	// return info.isBind() || (info.isReg() && info.isLinked());
	// } else if (isLinked()) {
	// return info.isBind() || info.isReg();
	// } else {
	// return info.isBind() || info.isLinked() || info.isReg();
	// }
	// }

}