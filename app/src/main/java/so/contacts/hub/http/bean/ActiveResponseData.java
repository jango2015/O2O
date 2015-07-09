package so.contacts.hub.http.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import so.contacts.hub.push.bean.OpConfig;
import so.contacts.hub.yellow.data.Voucher;

public class ActiveResponseData extends BaseResponseData {
	public DualCardMatch dual_card_match;//[ DualCardMatch][null able][双卡适配信息]
	public AppRecommendPackage app_recommend_info;//[AppRecommendPackage][not null][应用推荐信息];
//	public GameCenterInfo game_center_info;
	public HotKeyWordsConfig hotkey_words;//[HotKeyWordsConfig][null able][热词配置]
	public RecommendSearchWordsConfig recommend_searchWords;//[RecommendSearchWordsConfig][null able][推荐查询热词配置]
	public RemindInfo remind_info;
	public List<Voucher> voucher_list; //[优惠券列表]
	public OpConfig op_config;
	
	public class RemindInfo {
	    public int tab_remind; // [tab_remind][null able][tab页打点标识0-不显示1-显示,默认0]
	    public int max_remind; // 最大打点显示数，默认为6. add by putao_lhq
	    public int use_net_search_strategy; // 网络搜索策略开关 add by cj 
	}
	
	public class DualCardMatch {
	    public int dual_version;//[int][not null][返回的双卡适配信息的版本]
	    public int type;//[int][not null][匹配类型: 1:通用类型,2:SIM卡信息反射获取类型,3:电话服务和SIM卡信息反射获取类型]
	    public List<String> service_name;//[List<String>][not null][获取电话服务的名称。如：phone,phone2]
	    public String service_method_name;//[String][null able][获取服务反射的方法名] 
	    public String sim_method_name;//[String][null able][获取SIM卡信息的反射的方法名]
	    public List<String> sim_id;//[List<String>][not null][SIM卡标示。如：0，1 或者10，11]
	    public Map<String, ParamStatus> call_map;//[Map<String, ParamStatus][not null][拨号时带出的参数]
	    public CallLogStatus sim_call_column;//[CallLogStatus][null able][通话记录SIM卡拨号标识]
	    public List<String> call_sim_id;//用于记录拨号传递的值
	}
	
	public class HotKeyWordsConfig{
	    public int version;//[int][not null][版本]
	    public String key_words;//[String][not null][热词信息]
	}
	
	public class RecommendSearchWordsConfig{
	    public int version;//[int][not null][版本]
	    public String recommend_search_words;//[String][not null][推荐查询热词内容]
	}
	
	public class ParamStatus {
		public String class_type;//[String][not null][参数类型名：取值范围：String,int,long,float,double,short,byte,char,boolean]
		public String value;//[String][null able][参数取值,取值为空的未SIM卡标示，不为空的，请按照class_type的valueOf(String s)方法进行转型使用]
	}
	
	public class CallLogStatus {
		public String column_key;//[String][not null][字段名]
		public List<String> sim_id;//[List<String>][not null][SIM卡标示。如：0，1 或者10，11]
	}
	
	public class AppRecommendPackage implements Serializable {
		public int has_kinds; // [int][not null][是否存在推荐类别,0:隐藏入口，1:有推荐应用入口]
		public List<AppRecommendKind> app_kind_list; // [List<AppRecommendKind>][null able][推荐榜单基本信息]
		public int kinds_version; // [int][not null][榜单版本]
		public int is_reload; // [int][not null][是否需要重新加载,0:不需要，1:需要]
//		public AppRecommendInfo hot_app; // [ AppRecommendInfo][null able][热门APP信息]
	}
	
	public class AppRecommendKind implements Serializable {
		public String name; // [String][not null][榜单名字]
		public long id; // [long][not null][榜单ID]
	}
	
}
