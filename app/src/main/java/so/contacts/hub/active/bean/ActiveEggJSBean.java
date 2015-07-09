package so.contacts.hub.active.bean;

import java.io.Serializable;

/**
 * 
 * @author putao_lhq
 * @version 2014年10月21日
 */
public class ActiveEggJSBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public long activity_id;  	// 活动ID
	public String match_id;	// 蛋ID列表
	public int status;		//0:未开始 1:已开始 2:已结束
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("activity_id: ");
		sb.append(activity_id);
		
		sb.append(" ,match_id: ");
		sb.append(match_id);
		
		sb.append(" ,status: ");
		sb.append(status);
		return sb.toString();
	}
}
