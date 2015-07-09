package so.contacts.hub.active.bean;

import java.io.Serializable;

/**
 * 活动历史详情数据
 * @author putao_lhq
 * @version 2014年10月19日
 */
public class ActiveHistoryBean implements Serializable{

	private static final long serialVersionUID = 1L;

	public long id; //[not null] [我的活动id]
	public long activity_id;//[not null] [活动id]
	public String u_id; //[not null] [用户id]
	public int step_id; //[not null] [参与活动步骤id]
	public String participation_time; //[null able] [参加时间]
	public String description; //[活动描述]
	public String status;//
	public String target_url;//[目标地址]
	public String icon_url;//[活动图片地址]
	public String update_time;//[更新时间]
	public String name;
	public int remind;  // 是否提醒 1-提醒 0-不提醒
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: ");
		sb.append(id);
        sb.append("activity_id: ");
        sb.append(activity_id);

		sb.append(",uid: ");
		sb.append(u_id);
		
		sb.append(",step_id: ");
		sb.append(step_id);
		
		sb.append(",participation_time: ");
		sb.append(participation_time);
		
		sb.append(",description: ");
		sb.append(description);
		
		sb.append(",status: ");
		sb.append(status);
		
		sb.append(",target_url: ");
		sb.append(target_url);
		
		sb.append(",icon_url: ");
		sb.append(icon_url);
		
		sb.append(",update_time: ");
		sb.append(update_time);
		
		sb.append(",remind: ");
        sb.append(remind);
		return sb.toString();
	}
}
