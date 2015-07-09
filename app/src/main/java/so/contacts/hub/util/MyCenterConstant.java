package so.contacts.hub.util;



import so.contacts.hub.ui.yellowpage.YellowPageChargeTelephoneFragmentNew;
import so.contacts.hub.active.YellowPageActiveHistoryActivity;
import so.contacts.hub.msgcenter.ui.YellowPageMyOrderActivity;
import so.contacts.hub.shuidianmei.YellowPageWaterEGActivity;
import so.contacts.hub.thirdparty.cinema.ui.MovieOrderDetailsActivity;
import so.contacts.hub.train.YellowPageTrainTicketOrderHistoryH5Activity;
import so.contacts.hub.ui.web.YellowPageDianpingGroupActivity;
import so.contacts.hub.ui.yellowpage.YellowPageChargeHistoryActivity;
import so.contacts.hub.ui.yellowpage.YellowPageChargeTelephoneFragment;
import so.contacts.hub.ui.yellowpage.YellowPageMyActivity;
import so.contacts.hub.ui.yellowpage.YellowPageUserFeedbackActivity;

/**
 * 我的 中常用配置类型
 * @author evan
 *
 */
public class MyCenterConstant {

	/**
	 *  start: "我的" 中保存的信息 content_type
	 */
	// 酒店-订单 
	public static final String HIBAT_CONTENT_TYPE_HOTEL_ORDER = "Hotel_Order";

	// "黄页"-手机充值-手机号
	public static final String HIBAT_CONTENT_TYPE_CHARGE_TELE_AND_ADD_AND_OPERATORS = "Charge_Tele_Info";

	// 火车票-常用旅客-信息
	public static final String HIBAT_CONTENT_TYPE_OFFEN_TRAVELER_INFO = "train_offen_traveler_info_1";

	// 火车票-常用旅客邮寄地址-信息
	public static final String HIBAT_CONTENT_TYPE_OFFEN_MAIL_ADDRESS_INFO = "train_offen_mail_address_info";
	
	// 违章车辆-最后一次添加的违章车辆信息
    public static final String HIBAT_CONTENT_TYPE_OFFEN_TRAFFIC_VEHICLE_INFO = "offen_traffic_vehicle_info";
    
    // 电影片- 最后订票的手机号
    public static final String MOVIE_ORDER_PAY_NUM = "movie_order_pay_num";
	
	/** end: "我的" 中保存的信息 content_type */
	
	
	/**
	 * start: “我的”中打点节点 名称
	 */
	// "我的" "so.contacts.hub.ui.yellowpage.YellowPageMyActivity"
	public static final String MY_NODE = YellowPageMyActivity.class.getName(); 
	
	// "我的" "so.contacts.hub.ui.yellowpage.YellowPageMyOrderActivity"
    public static final String MY_NODE_ORDER = YellowPageMyOrderActivity.class.getName(); 
	
    // "我的"- "我的订单" - "团购订券" "so.contacts.hub.ui.web.YellowPageDianpingGroupActivity"
    public static final String MY_NODE_TUAN_ORDER = YellowPageDianpingGroupActivity.class.getName();
    
    // "我的"- "我的订单" - "充值历史" "so.contacts.hub.ui.yellowpage.YellowPageChargeHistoryActivity"
    public static final String MY_NODE_CHAGER_HISTROY_ORDER = YellowPageChargeHistoryActivity.class.getName();
    
    // "黄页"- "手机充值"
    public static final String MY_NODE_CHARGE_TELE= YellowPageChargeTelephoneFragmentNew.class.getName();
    
    // "我的"- "我的活动" "com.contacts.hub.active.YellowPageActiveHistoryActivity"
    public static final String MY_NODE_ACTIVE_HISTROY = YellowPageActiveHistoryActivity.class.getName();

    // "我的"- "我的活动" "so.contacts.hub.ui.yellowpage.YellowPageTrainTicketOrderHistoryActivity"
    public static final String MY_NODE_TONGCHENG_TRAIN = YellowPageTrainTicketOrderHistoryH5Activity.class.getName();
    
    // "常用旅客信息" 
    public static final String TRAIN_OFFEN_TRAVELER_INFO = "TRAIN_OFFEN_TRAVELER_INFO";
    
    // "常用旅客邮寄地址信息" 
    public static final String TRAIN_OFFEN_MAIL_ADDRESS_INFO = "TRAIN_OFFEN_MAIL_ADDRESS_INFO";
    
    // "设置违章车辆查询"
    public static final String TRAFFIC_OFFENCE_VEHICLE_INFO = "TRAFFIC_OFFENCE_VEHICLE_INFO";
    
    // " 用户反馈"- "so.contacts.hub.ui.yellowpage.YellowPageUserFeedbackActivity"
    public static final String MY_NODE_USER_FEEDBACK= YellowPageUserFeedbackActivity.class.getName();
    
    // "电影票"- "确认订单准备支付"
    public static final String MY_NODE_MOVIE_DETAIL = MovieOrderDetailsActivity.class.getName();

	/** end: “我的”中打点节点 名称 */
	
}
