package so.contacts.hub.businessbean;

import java.io.Serializable;

public class MobileMatchResultInfo implements Serializable {
    
	public String phone;// [String][not null][手机号码]
	public int status;// 匹配联系人状态（0：服务器同步；1：匹配）
	public int sns_id;// [int][not null][3-代表人人]
	public String id;// [String][not null][匹配到的人人用户ID]
	public String name;// [String][not null][匹配到的人人用户的名字]
	public String profile_image_url;// [String][not null][匹配到的人人的头像]
	public int flag;// sina : 微博关系，0表示已关注，1表示未关注，2 表示用户自己
	public String description;// 个人描述
	public String status_text;// 用户最新一条微博
	public String status_created_at;//   用户最新一条微博的创建时间
    public int contactId; // 业务逻辑扩展字段，记录联系人ContactID
	
}