package so.contacts.hub.http.bean;

import java.util.List;

public class QueryTipsResponse extends BaseResponseData {

	public List<TipsInfo> tips_list;	//	tips_list:[List<TipsInfo>][not null][Tips列表];
	
	public class TipsInfo{
		public long id;				//	id:[long][not null][tips ID]
		public int tips_location;   //tips_location:[int][not null][tips显示位置：0:拨号盘,1:联系人列表,2:动态页,4:群组,11:名片页]
		public int type;		    //type:[int][not null][tips类型，1：跳转H5连接，2：跳转本地Activity]
		public int level;			//level:[int][not null][级别，决定tips底色]
		public String f_color;		//f_color:[String][not null][字体颜色]
		public String icon;			//icon:[String][null able][图标]
		public String content;		//content:[String][not null][显示内容]
		public int clear_type;		//clear_type:[int][not null][消除类型,1:再次进入界面消失，2:点击进入消失]
		public int is_has_close;	//is_has_close:[int][not null][是否有关闭键，0：没有，1：有]
		public String time_rule;	//time_rule:[String][null able][显示时间规则，null为无限制，如：9:23为早九点到晚11点之间显示]
		public String target_url;	//target_url:[String][null able][H5页面目标地址，只有在type=1时有效]
		public ActivityInfo target_activity;	//target_activity:[ActivityInfo][null able][目标activity信息]
	}
	
	public class ActivityInfo{
		public String name;				 //name:[String][not null][目标activity名]
		public List<ActivityParam> param;//param:[List<ActivityParam>][null able][目标activity要携带的参数]
	}
	
	public class ActivityParam{
		public String key;			//key:[String][not null][参数名]
		public String value_type;	//value_type:[String][not null][ 参数类型名：取值范围：String,int,long,float,double,short,byte,char,boolean]
		public String value;		//value:[String][not null][取值]
	}
}
