package so.contacts.hub.http.bean;

import java.util.List;

/**
 * so.contacts.hub.http.bean
 * 
 * @author kzl
 * @created at 13-6-8 上午11:17
 */
public class MergeTargetUser {
	public long u_id;// :[long][not null][目标用户的用户ID]
	public List<String> mobiles;// :[List<String>][null able][目标用户的授权号码列表]
	public List<BindingSnsInfo> binding_sns_list;// :[List<
													// BindingSnsInfo>][null
													// able][目标号码的授权社交账户情况]

	public MergeTargetUser() {
	}

	public MergeTargetUser(long u_id, List<String> mobiles,
			List<BindingSnsInfo> binding_sns_list) {
		this.u_id = u_id;
		this.mobiles = mobiles;
		this.binding_sns_list = binding_sns_list;
	}
}
