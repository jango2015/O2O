package so.contacts.hub.businessbean;

import java.io.Serializable;

public class ShowContactsBean implements Serializable {
	
	private static final long serialVersionUID = 5790171946486503930L;
	
	public static final int SHOW_ACCOUNT_TYPE = 1;
	public static final int SHOW_SNS_TYPE = 2;
	
	// 选择要显示的联系人 以json串的形式保存到shareprefence里面
	public int showType = SHOW_ACCOUNT_TYPE;
	public boolean showSim; // 显示sim联系人
	public boolean showSina = true; // 显示新浪好友
	public boolean showTencent = true; // 显示腾讯好友
	public boolean showRenren = true; // 显示人人好友
	public boolean showMe = true; // 显示“我”
	public boolean mergeContacts; // 合并联系人
	
	public boolean isShowAccount() {
		return showType == SHOW_ACCOUNT_TYPE ;//|| !Config.getUser().isBind();
	}
	
	public boolean isShowSns() {
		return showType == SHOW_SNS_TYPE;
	}
}
