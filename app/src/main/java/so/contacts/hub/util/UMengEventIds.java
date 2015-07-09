package so.contacts.hub.util;

import java.util.HashMap;
import java.util.Map;

import so.contacts.hub.ContactsApp;

import com.mdroid.core.util.SystemUtil;
import so.contacts.hub.util.MobclickAgentUtil;

public class UMengEventIds {
	
    public static final String EXTRA_UMENG_EVENT_ID="extra_umeng_event_id";
    public static final String EXTRA_UMENG_EVENT_IDS_SUCCESS="extra_umeng_event_ids_success";
    public static final String EXTRA_UMENG_EVENT_IDS_FAIL="extra_umeng_event_ids_fail";
    
	/** 【发现-时长】停留时长,1 */
	public static final String DISCOVER_RESUME_TIME = "discover_resume_time";

	/** 【发现-XX-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_HEADER = "discover_yellowpage_resume_time_";

	/** 【发现-电影-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_56 = "discover_yellowpage_resume_time_56";

	/** 【发现-我的】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_60 = "discover_yellowpage_resume_time_60";

	/** 【发现-充值】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_61 = "discover_yellowpage_resume_time_61";
	
	/** 【发现-查号-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_0 = "discover_yellowpage_resume_time_0";
	
	/** 【发现-招聘-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_12 = "discover_yellowpage_resume_time_12";
	
	/** 【发现-租房-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_13 = "discover_yellowpage_resume_time_13";
	
	/** 【发现-家政-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_14 = "discover_yellowpage_resume_time_14";
	
	/** 【发现-查快递-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_50 = "discover_yellowpage_resume_time_50";
	
	/** 【发现-订机票-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_54 = "discover_yellowpage_resume_time_54";
	
	/** 【发现-团购-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_62 = "discover_yellowpage_resume_time_62"; 
	
	/** 【发现-挂号-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_17 = "discover_yellowpage_resume_time_17"; 
	
	/** 【发现-打车-时长】停留时长,1 */
	public static final String DISCOVER_YELLOWPAGE_RESUME_TIME_19 = "discover_yellowpage_resume_time_19"; 

	/** 【发现-XX】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_HEADER = "discover_yellowpage_";

	/** 【发现-搜索-应用直达】入口进入次数/设备数 */
	public static final String DISCOVER_SEARCH_SERVER_ACCESS = "discover_search_server_access_";

	/** 【通知-点击拉起服务】入口进入次数/设备数 */
	public static final String DISCOVER_NOTIFICATION_SERVER_ACCESS = "discover_notification_server_access_";

	/** 【发现-查号】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_SEARCH_NUMBER = "discover_yellowpage_0";

	/** 【发现-XX-详情-电话】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DETAIL_CALL_HEADER = "discover_yellowpage_detail_call_";

	/** 【发现-XX-详情-地址】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DETAIL_LOCATION_HEADER = "discover_yellowpage_detail_location_";

	/** 【发现-XX-详情-大众点评团购】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DETAIL_DIANPING_HEADER = "discover_yellowpage_detail_dianping_";

	/** 【发现-XX-详情-在来源网站查看】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DETAIL_VIEWSOURCESITE_HEADER = "discover_yellowpage_detail_viewsourcesite_";

	/** 【发现-XX-详情-在官方网站查看】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DETAIL_VIEWOFFICIALWEBSITE_HEADER = "discover_yellowpage_detail_viewofficialwebsite_";

	/** 【发现-XX-详情-查看附近的XX】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DETAIL_NEARBY_HEADER = "discover_yellowpage_detail_nearby_";

	/** 【发现-XX-详情-快捷服务】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DETAIL_FAST_SERVICE_HEADER = "discover_yellowpage_detail_fast_service_";

	/** 【发现-XX-查看大众点评评论】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DETAIL_COMMENT_HEADER = "discover_yellowpage_detail_comment_";

	/** 【发现-搜索】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_SEARCH = "discover_yellowpage_search";

	/** 【发现-搜索-搜狗】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_SEARCH_SOUGOU = "discover_yellowpage_search_sougou";

	/** 【发现-搜索-点评】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_SEARCH_DIANPING = "discover_yellowpage_search_dianping";

	/** 【发现-搜索-搜狗-搜索成功】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_SUCCESS = "discover_yellowpage_search_sougou_success";

	/** 【发现-搜索-点评-搜索成功】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_SEARCH_DIANPING_SUCCESS = "discover_yellowpage_search_dianping_success";

	/** 【发现-搜索-搜狗-无数据】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_NO_DATA = "discover_yellowpage_search_sougou_no_data";

	/** 【发现-搜索-点评-无数据】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_SEARCH_DIANPING_NO_DATA = "discover_yellowpage_search_dianping_no_data";

	/** 【发现-搜索-搜狗-搜索异常】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_FAIL = "discover_yellowpage_search_sougou_fail";

	/** 【发现-搜索-点评-搜索异常】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_SEARCH_DIANPING_FAIL = "discover_yellowpage_search_dianping_fail";

	/** 【发现-识别】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_NUMBER_IDENTIFICATION = "discover_yellowpage_number_identification";

	/** 【发现-识别-搜狗】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_NUMBER_IDENTIFICATION_SOUGOU = "discover_yellowpage_number_identification_sougou";

	/** 【发现-识别-搜狗-成功】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_NUMBER_IDENTIFICATION_SOUGOU_SUCCESS = "discover_yellowpage_number_identification_sougou_success";

	/** 【发现-识别-搜狗-无数据】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_NUMBER_IDENTIFICATION_SOUGOU_NO_DATA = "discover_yellowpage_number_identification_sougou_no_data";

	/** 【发现-识别-搜狗-识别异常】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_NUMBER_IDENTIFICATION_SOUGOU_FAIL = "discover_yellowpage_number_identification_sougou_fail";

	/** 【发现-标识】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_NUMBER_MARK = "discover_yellowpage_number_mark";

	/** 【发现-标识-搜狗】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_NUMBER_MARK_SOUGOU = "discover_yellowpage_number_mark_sougou";

	/** 【发现-标识-搜狗-成功】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_NUMBER_MARK_SOUGOU_SUCCESS = "discover_yellowpage_number_mark_sougou_success";

	/** 【发现-标识-搜狗-无数据】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_NUMBER_MARK_SOUGOU_NO_DATA = "discover_yellowpage_number_mark_sougou_no_data";

	/** 【发现-标识-搜狗-标识异常】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_NUMBER_MARK_SOUGOU_FAIL = "discover_yellowpage_number_mark_sougou_fail";

	/** 【发现-游戏-查看】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_GAME_VIEW_HEADER = "discover_yellowpage_game_view_";

	/** 【发现-游戏-详情-下载次数】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_GAME_DETAIL_DOWNLOAD_HEADER = "discover_yellowpage_game_detail_download_";

	/** 【发现-游戏-启动】点击次数,0 */
	public static final String DISCOVER_YELLOWPAGE_GAME_START_HEADER = "discover_yellowpage_game_start_";
	
	// -------------------------------------------
	// /**【发现-充话费】入口进入次数,0*/
	// discover_yellowpage_61,【发现-充话费】入口进入次数,0

	/** 【发现-充话费-点击充值】入口进入次数 */
	public static final String DISCOVER_YELLOWPAGE_CHARGE_RECHARGE = "discover_yellowpage_Charge_Recharge";

	/** 【发现-充话费-充值成功】入口进入次数 */
	public static final String DISCOVER_YELLOWPAGE_CHARGE_SUCCESS = "discover_yellowpage_Charge_Success";

	/** 【发现-充话费-充值失败】入口进入次数 */
	public static final String DISCOVER_YELLOWPAGE_CHARGE_FAIL = "discover_yellowpage_Charge_Fail";
	
	/** 【发现-充话费-充值取消】入口进入次数 */
	public static final String DISCOVER_YELLOWPAGE_CHARGE_CANCEL = "discover_yellowpage_Charge_Cancel";

	/** 【发现-充话费-充值成功，服务器超时】入口进入次数 */
	public static final String DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_SERV_TIMEOUT = "discover_yellowpage_Charge_pay_success_server_outtime";

	/** 【发现-充话费-充值成功，连连超时】入口进入次数 */
	public static final String DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_LLIAN_TIMEOUT = "discover_yellowpage_Charge_pay_success_lianlian_outtime";
	
	/** 【发现-充话费-选择联系人】入口进入次数 */
	public static final String DISCOVER_YELLOWPAGE_CHARGE_SELECT_CONTACT = "discover_yellowpage_Charge_Select_Contact";

	/** 【发现-充话费-选择历史号码】入口进入次数 */
	public static final String DISCOVER_YELLOWPAGE_CHARGE_SELECT_HISTORY = "discover_yellowpage_Charge_Select_History";
	
	/** 【发现-我的-通过长按删除数】入口进入次数 */
	public static final String DISCOVER_YELLOWPAGE_MY_LONGCLICK_DELETE = "discover_yellowpage_Longclick_Delete";

	/** 【发现-团购-搜索】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DEAL_SEARCH = "discover_yellowpage_Deal_Search";

	/** 【发现-团购-搜索有结果次数】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DEAL_SEARCH_HAVE_DATA = "discover_yellowpage_Deal_Search_have_data";

	/** 【发现-团购-搜索结果为空数】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DEAL_SEARCH_NO_DATA = "discover_yellowpage_Deal_Search_no_data";

	/** 【发现-团购-搜索异常数】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DEAL_SEARCH_ERROR = "discover_yellowpage_Deal_Search_error";

	/** 【发现-团购-搜索结果下翻页数】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DEAL_SEARCH_NEXT_PAGE = "discover_yellowpage_Deal_Search_Next_Page";

	/** 【发现-团购-搜索结果点击进入条目】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_DEAL_SEARCH_ITEM_CLICK = "discover_yellowpage_Deal_Search_Item_Click";

	/** 【发现-类别-常用-XX】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_CATEGORY_FAST_ITEM_HEADER = "discover_yellowpage_category_fast_item_";

	/** 【发现-汇总-纠错】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_AGGREGATED_ERROR_COLLECT = "discover_yellowpage_Aggregated_Error_Collect";

	/** 【发现-汇总-纠错-提交成功】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_AGGREGATED_ERROR_COLLECT_COMMIT_SUCCESS = "discover_yellowpage_Aggregated_Error_Collect_Commit_Success";

	/** 【发现-汇总-纠错-提交失败】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_AGGREGATED_ERROR_COLLECT_COMMIT_FAIL = "discover_yellowpage_Aggregated_Error_Collect_Commit_Fail";

	/** 【发现-汇总-收藏-收藏成功】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_AGGREGATED_FAVORITE_SUCCESS = "discover_yellowpage_Aggregated_Favorite_Success";

	/** 【发现-汇总-收藏-取消成功】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_AGGREGATED_FAVORITE_CANCEL = "discover_yellowpage_Aggregated_Favorite_Cancel";

	/** 【发现-汇总-关于】入口进入次数,0 */
	public static final String DISCOVER_YELLOWPAGE_AGGREGATED_ABOUT = "discover_yellowpage_Aggregated_About";
	
	
	/////////////////////////////////////////////
	// modify 2014/09/29 by zjh start
	/** 【发现-我的-绑定手机】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_BIND_MOBILE = "discover_yellowpage_My_bind_mobile";
	
	/** 【发现-我的-绑定手机-解除绑定】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_UNBIND_MOBILE = "discover_yellowpage_My_unbind_mobile";
	
	/** 【发现-我的-退出登录】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_EXIT_LOGIN = "discove_yellowpager_exit_login";
	
	/** 【发现-我的-我的订单】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_ORDER = "discover_yellowpage_My_order";
	
	/** 【发现-我的-我的收藏】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_FAVORITE = "discover_yellowpage_My_Favorite";
	
	/** 【发现-我的-历史记录】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_HISTORY = "discover_yellowpage_My_History";
	
	/** 【发现-我的-我的订单-团购】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_ORDER_TUANGOU = "discover_yellowpage_My_Order_Tuangou";
	
	/** 【发现-我的-我的订单-酒店】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_ORDER_HOTEL = "discover_yellowpage_My_Order_Hotel";
	
	/** 【发现-我的-我的订单-充值】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CHARGE = "discover_yellowpage_My_Order_Charge";
	
	/** 【发现-我的-我的订单-火车票】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_ORDER_TRAINTICKET = "discover_yellowpage_My_Order_TrainTicket";
	
	/** 【发现-我的-添加自定义】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_SERVER_MANAGER = "discover_yellowpage_My_ServerManager";
	
	/** 【发现-我的-添加自定义-添加】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_SERVER_MANAGER_ADD = "discover_yellowpage_My_ServerManager_add";
	
	/** 【发现-我的-添加自定义-移除】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_SERVER_MANAGER_REMOVE = "discover_yellowpage_My_ServerManager_Remove";
	
	/** 【发现-我的-添加自定义-拖动】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_MY_SERVER_MANAGER_DRAG = "discover_yellowpage_My_ServerManager_Drag";
	
	/** 【发现-驾车-客运】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_DRIVER_TRANSPORT = "discover_yellowpage_Drive_Transport";
	
	/** 【发现-公交-公交换乘-查询线路】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_BUS_QUERY_PATH = "discover_yellowpage_Bus_Query_Path";
	
	/** 【发现-公交-站点线路】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_BUS_PATH = "discover_yellowpage_Bus_Path";
	
	/** 【发现-公交-站点线路-选择附近站点】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_BUS_PATH_SELECT_NEAR = "discover_yellowpage_Bus_Path_Select_Path";
	
	/** 【发现-公交-站点线路-查询】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_BUS_PATH_QUERY = "discover_yellowpage_Bus_Path_Query";
	
//	/** 【发现-酒店-订酒店-选择城市】入口进入次数/设备数 */
//	public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_CITY = "discover_yellowpage_Hotel_Order_City";
//	
//	/** 【发现-酒店-订酒店-选择入住日期（艺龙）】入口进入次数/设备数 */
//	public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_IN_DATE = "discover_hotel_order_elong_InDate";
//	
//	/** 【发现-酒店-订酒店-选择离店日期（艺龙）】入口进入次数/设备数 */
//	public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_OUT_DATE = "discover_hotel_order_elong_OutDate";
//	
//	/** 【发现-酒店-订酒店-选择价格（艺龙）】入口进入次数/设备数 */
//	public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_PRICE = "discover_hotel_order_elong_Price";
//	
//	/** 【发现-酒店-订酒店-选择星级（艺龙）】入口进入次数/设备数*/
//	public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_STAR = "discover_hotel_order_elong_Star";
//	
//	/** 【发现-酒店-订酒店-选择关键字（艺龙）】入口进入次数/设备数 */
//	public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_HOTWORD = "discover_hotel_order_elong_Hotword";
//	
//	/** 【发现-酒店-订酒店-搜索（艺龙）】入口进入次数/设备数 */
//	public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_SEARCH = "discover_hotel_order_elong_Search";
	
	/** 【发现-酒店-订酒店-选择联系人】入口进入次数 */
    public static final String DISCOVER_YELLOWPAGE_HOTEL_SELECT_CONTACT = "discover_yellowpage_hotel_Select_Contact";
	
	/**【发现-我的-我的订单-火车票】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_TONGCHENG_MYORDERHISTORY = "discover_yellowpage_Tongcheng_OrderHistory";
	
	/**【发现-火车票-选择起始站】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_TONGCHENG_SELECT_DEPARTSTATION = "discover_yellowpage_Tongcheng_SelectDepartStation";
	
	/**【发现-火车票-选择终点站】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_TONGCHENG_SELECT_ARRIVEDSTATION = "discover_yellowpage_Tongcheng_SelectArrivedStation";
	
	/**【发现-火车票-切换起始/终点站】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_TONGCHENG_SWAP_STATION = "discover_yellowpage_Tongcheng_SwapStation";
	
	/**【发现-火车票-选择日期】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_TONGCHENG_SELECT_DATE = "discover_yellowpage_Tongcheng_SelectDate";
	
	/**【发现-火车票-查询火车票】入口进入次数/设备数 */
	public static final String DISCOVER_YELLOWPAGE_TONGCHENG_TRAIN_QUERY = "discover_yellowpage_Tongcheng_Query";
	
	// modify 2014/09/29 by zjh end
	
	// add by putao_lhq 2014年10月10日 for 友盟 start
	/** 【发现-搜索-高德-搜索成功】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_SEARCH_GAODE_SUCCESS = "discover_yellowpage_search_gaode_success";
	/** 【发现-搜索-高德-搜索异常】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_SEARCH_GAODE_FAIL = "discover_yellowpage_search_gaode_fail";
	/** 【发现-搜索-高德-搜索无数据】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_SEARCH_GAODE_NO_DATA = "discover_yellowpage_search_gaode_no_data";
	
	/** 【发现-搜索-58-搜索成功】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_SEARCH_58_SUCCESS = "discover_yellowpage_search_58_success";
	/** 【发现-搜索-58-搜索异常】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_SEARCH_58_FAIL = "discover_yellowpage_search_58_fail";
	/** 【发现-搜索-58-搜索无数据】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_SEARCH_58_NO_DATA = "discover_yellowpage_search_58_no_data";
	
	/** 【发现-搜索-热词】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_SEARCH_HOT_WORDS = "discover_yellowpage_search_hot_words";
	
	/** 【发现-搜索-历史】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_SEARCH_HISTORY = "discover_yellowpage_search_hitory";
	// add by putao_lhq 2014年10月10日 for 友盟 end
	
	// add by putao_lhq 2014年10月11日 for 接入商错误上报 start
	public static final String SEARCH_ERROR_REPORT = "search_error_report";
	public static final String SEARCH_ERROR_REPORT_BUSINESS_CODE = "business";
	public static final String SEARCH_ERROR_REPORT_NET_TYPE = "net";
	public static final String SEARCH_ERROR_REPORT_ERROR_MESSAGE = "error_msg";
	public static final String[] SEARCH_ERROR_REPORT_NET = {"no_net","2G","3G","wifi","4G"};
	
	public static void reportSearchError(String business, Exception e) {
		LogUtil.d("search", "code: " + business + " error msg: " + e.getMessage());
		final int netStatus = SystemUtil
				.getNetStatus(ContactsApp.getInstance());
		if (netStatus > 0) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(UMengEventIds.SEARCH_ERROR_REPORT_BUSINESS_CODE, business);
			map.put(UMengEventIds.SEARCH_ERROR_REPORT_NET_TYPE,
					UMengEventIds.SEARCH_ERROR_REPORT_NET[netStatus]);
			map.put(UMengEventIds.SEARCH_ERROR_REPORT_ERROR_MESSAGE,
					"error: " + e.getMessage());
			MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
					UMengEventIds.SEARCH_ERROR_REPORT, map);
		}
	}
	// add by putao_lhq 2014年10月11日 for 接入商错误上报 end
	
	
	// add by zjh 2014-10-20 start
	/** 【发现-顶部通栏运营位】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_TOP = "discover_yellowpage_ad_top";
	
	/** 【发现-顶部半通栏运营位-左边】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_TOP_1 = "discover_yellowpage_ad_top_1";
	
	/** 【发现-顶部半通栏运营位-右边】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_TOP_2 = "discover_yellowpage_ad_top_2";
	
	/** 【发现-中部通栏运营位】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_MIDDLE = "discover_yellowpage_ad_middle";
	
	/** 【发现-中部半通栏运营位-左边】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_MIDDLE_1 = "discover_yellowpage_ad_middle_1";
	
	/** 【发现-中部半通栏运营位-右边】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_MIDDLE_2 = "discover_yellowpage_ad_middle_2";
	
	/** 【发现-底部通栏运营位】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_BOTTOM = "discover_yellowpage_ad_bottom";
	
	/** 【发现-底部半通栏运营位-左边】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_BOTTOM_1 = "discover_yellowpage_ad_bottom_1";
	
	/** 【发现-底部半通栏运营位-右边】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_BOTTOM_2 = "discover_yellowpage_ad_bottom_2";
	
	/** 【发现-二级-顶部通栏运营位】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_MY_ACTIVITIES_TOP = "discover_yellowpage_ad_my_activities_top";
	
	/** 【发现-二级-顶部通栏运营位-左边】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_MY_ACTIVITIES_TOP_1 = "discover_yellowpage_ad_my_activities_top_1";
	
	/** 【发现-二级-顶部通栏运营位-右边】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_MY_ACTIVITIES_TOP_2 = "discover_yellowpage_ad_my_activities_top_2";

	/** 【发现-附近的-地图模式】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_NEAR_MAP_MODE = "discover_yellowpage_near_map_mode";
	
	/** 【发现-我的-我的活动】点击次数/设备数*/
	public static final String DISCOVER_YELLOWPAGE_AD_MY_ACTIVITIES = "discover_yellowpage_ad_my_activities";
	
	// add by zjh 2014-10-20 start
	
	/** 从葡萄信鸽通知点击进入 */
    public static final String NOTIFICATION_PUTAO_CLICKED = "notification_putao_clicked";
    /** 从酷派PUSH通知点击进入 */
    /*
     * 统计使用错误，点击统计，但是在1.5.67AllInOne版本中用在了接收的位置，修复后使用NOTIFICATION_COOLPAD_ALREAD_CLICKED统计点击
     * modifiede by hyl 2014-11-25 start
     */
//    public static final String NOTIFICATION_COOLPAD_CLICKED = "notification_coolpad_clicked";
    //modifiede by hyl 2014-11-25 end
    
    /** 从酷派PUSH消息接收成功 */
    public static final String NOTIFICATION_COOLPAD_RECEIVER = "notification_coolpad_receiver";
    /** 从酷派PUSH消息接收成功 */
    public static final String NOTIFICATION_COOLPAD_ALREADY_CLICKED = "notification_coolpad_already_clicked";
    
    
    //add xcx 2014-12-29 start 新增统计埋点
    /** 【发现-应用服务首页-顶部运营位】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOME_TOP_OPERATIONAL_POSITION = "discover_yellowpage_home_top_operational_position";
    
    /** 【发现-我的-提醒中心】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_IN = "discover_yellowpage_my_msg_center_in";
    
    /** 【发现-我的-提醒中心-系统通知】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_NOTIFICATION = "discover_yellowpage_my_msg_center_notification";
    
    /** 【发现-我的-提醒中心-系统通知进入】入口进入次数*/
    //hotel HotelMessageBusiness.handleBusiness未处理
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_NOTIFICATION_IN = "discover_yellowpage_my_msg_center_notification_in";
   
    /** 【发现-我的-提醒中心-设置】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_IN = "discover_yellowpage_my_msg_center_setting_in";
    
    /** 【发现-我的-提醒中心-出现提醒数】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_ITEM_NUM = "discover_yellowpage_my_msg_center_item_num";
    
    /** 【发现-我的-提醒中心-点击提醒条目】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_CLICK_LIST_ITEM = "discover_yellowpage_my_msg_center_click_list_item";
    
    /** 【发现-我的-提醒中心-打开声音】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_SOUND_OPEN = "discover_yellowpage_my_msg_center_setting_sound_open";
    
    /** 【发现-我的-提醒中心-关闭声音】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_SOUND_CLOSE = "discover_yellowpage_my_msg_center_setting_sound_close";
    
    /** 【发现-我的-提醒中心-打开震动】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_VIBRATE_OPEN = "discover_yellowpage_my_msg_center_setting_vibrate_open";
    
    /** 【发现-我的-提醒中心-关闭震动】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_VIBRATE_CLOSE = "discover_yellowpage_my_msg_center_setting_vibrate_close";
    
    
    /** 【发现-我的-提醒中心-打开提醒项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_EVENT_OPEN = "discover_yellowpage_my_msg_center_setting_event_open";
    
    /** 【发现-我的-提醒中心-关闭提醒项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_EVENT_CLOSE = "discover_yellowpage_my_msg_center_setting_event_close";
    
    /** 【发现-我的-提醒中心-设置车辆】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_CAR_INFO_IN = "discover_yellowpage_my_msg_center_setting_car_info_in";
    
    /** 【发现-我的-提醒中心-设置车辆-保存】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_CAR_INFO_SAVE = "discover_yellowpage_my_msg_center_setting_car_info_save";
  
    /** 【发现-我的-提醒中心-火车票-出现提醒数】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_TRAIN_TICKET_ITEM_NUM = "discover_yellowpage_my_msg_center_train_ticket_item_num";
  
    /** 【发现-我的-提醒中心-酒店-出现提醒数】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_HOTEL_ITEM_NUM = "discover_yellowpage_my_msg_center_hotel_item_num";
  
    /** 【发现-我的-提醒中心-彩票-出现提醒数】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_LOTTERY_TICKET_ITEM_NUM = "discover_yellowpage_my_msg_center_lottery_ticket_item_num";
  
    /** 【发现-我的-提醒中心-电影票-出现提醒数】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_MOVIE_TICKETS_ITEM_NUM = "discover_yellowpage_my_msg_center_movie_tickets_item_num";
  
    /** 【发现-我的-提醒中心-快递-出现提醒数】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_EXPRESS_ITEM_NUM = "discover_yellowpage_my_msg_center_express_item_num";

    /** 【发现-我的-提醒中心-违章-出现提醒数】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_CAR_ILLEGAL_ITEM_NUM = "discover_yellowpage_my_msg_center_car_illegal_item_num";
    
    /** 【发现-我的-提醒中心-流量-出现提醒数】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_FLOW_ITEM_NUM = "discover_yellowpage_my_msg_center_flow_item_num";
  
    /** 【发现-我的-提醒中心-话费-出现提醒数】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_TELEPHONE_ITEM_NUM = "discover_yellowpage_my_msg_center_telephone_item_num";
  
    /**【发现-我的-提醒中心-流量-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_FLOW_ITEM_CLICK = "discover_yellowpage_my_msg_center_flow_item_click";
  
    /**【发现-我的-提醒中心-话费-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_TELEPHONE_ITEM_CLICK = "discover_yellowpage_my_msg_center_telephone_item_click";
  
    /**【发现-我的-提醒中心-火车票-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_TRAIN_TICKET_ITEM_CLICK = "discover_yellowpage_my_msg_center_train_ticket_item_click";
  
    /**【发现-我的-提醒中心-酒店-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_HOTEL_ITEM_CLICK = "discover_yellowpage_my_msg_center_hotel_item_click";
  
    /**【发现-我的-提醒中心-彩票-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_LOTTERY_TICKET_ITEM_CLICK = "discover_yellowpage_my_msg_center_lottery_ticket_item_click";
  
    /**【发现-我的-提醒中心-电影票-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_MOVIE_TICKET_ITEM_CLICK = "discover_yellowpage_my_msg_center_movie_ticket_item_click";
  
    /**【发现-我的-提醒中心-快递-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_EXPRESS_ITEM_CLICK = "discover_yellowpage_my_msg_center_express_item_click";
    
    /**【发现-我的-提醒中心-违章-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_MSG_CENTER_CAR_ILLEGA_ITEM_CLICK = "discover_yellowpage_my_msg_center_car_illega_item_click";
  
    
    
    /** 【发现-我的-订单中心】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_IN = "discover_yellowpage_my_order_center_in";
    
    /** 【发现-我的-订单中心-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_CLICK_ITEM = "discover_yellowpage_my_order_center_click_item";
    

    /** 【发现-我的-订单中心-流量-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_FLOW_ITEM_CLICK = "discover_yellowpage_my_order_center_flow_item_click";
    
    /** 【发现-我的-订单中心-话费-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_TELEPHONE_ITEM_CLICK = "discover_yellowpage_my_order_center_telephone_item_click";
    
   
    
    /** 【发现-我的-订单中心-火车票-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_TRAIN_TICKET_ITEM_CLICK = "discover_yellowpage_my_order_center_train_ticket_item_click";
    
    /** 【发现-我的-订单中心-酒店-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_HOTEL_ITEM_CLICK = "discover_yellowpage_my_order_center_hotel_item_click";
    
    /**【发现-我的-订单中心-彩票-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_LOTTERY_TICKET_ITEM_CLICK = "discover_yellowpage_my_order_center_lottery_ticket_item_click";
    
    /**【发现-我的-订单中心-电影票-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_MOVIE_TICKET_ITEM_CLICK = "discover_yellowpage_my_order_center_movie_ticket_item_click";
   
    /**【发现-我的-订单中心-电影票-立即支付】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_MOVIE_TICKET_IMMEDIATE_PAY = "discover_yellowpage_my_order_center_movie_ticket_immediate_pay";
    
    /**【发现-我的-订单中心-流量-立即支付】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_FLOW_IMMEDIATE_PAY = "discover_yellowpage_my_order_center_flow_immediate_pay";
    
    /**【发现-我的-订单中心-话费-立即支付】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_TELEPHONE_IMMEDIATE_PAY = "discover_yellowpage_my_order_center_telephone_immediate_pay";
    
    
    /**【发现-手机充值-充流量】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_PHONE_RECHARGE_FLOW_IN = "discover_yellowpage_phone_recharge_flow_in";
    
    /**【发现-手机充值-充流量-充值成功（总计）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_PHONE_RECHARGE_FLOW_SUCCESS = "discover_yellowpage_phone_recharge_flow_success";
    
    /**【发现-手机充值-充流量-充值失败（总计）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_PHONE_RECHARGE_FLOW_FAIL = "discover_yellowpage_phone_recharge_flow_fail";
    
    /**【发现-手机充值-充话费-充值成功（支付宝）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_PHONE_RECHARGE_CHARGE_ALIPAY_SUCEESS = "discover_yellowpage_phone_recharge_charge_alipay_suceess";
    
    /**【发现-手机充值-充话费-充值失败（支付宝）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_PHONE_RECHARGE_CHARGE_ALIPAY_FAIL = "discover_yellowpage_phone_recharge_charge_alipay_fail";
    
    /**【发现-手机充值-充话费-充值成功（微信）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_PHONE_RECHARGE_CHARGE_WECHAT_SUCEESS = "discover_yellowpage_phone_recharge_charge_wechat_suceess";
    
    /**【发现-手机充值-充话费-充值失败（微信）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_PHONE_RECHARGE_CHARGE_WECHAT_FAIL = "discover_yellowpage_phone_recharge_charge_wechat_fail";
    
    
    /**【发现-电影票】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_IN = "discover_yellowpage_movie_in";
    
    /**【发现-电影票-正在热映】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_RELEASED = "discover_yellowpage_movie_released";
    
    /**【发现-电影票-正在热映-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_RELEASED_LIST_ITEM_CLICK = "discover_yellowpage_movie_released_list_item_click";
    
    /**【发现-电影票-正在热映-选择影院-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_RELEASED_THEATER_LIST_ITEM_CLICK = "discover_yellowpage_movie_released_theater_list_item_click";
    
    /**【发现-电影票-正在热映-选择场次-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_RELEASED_TIME_LIST_ITEM_CLICK = "discover_yellowpage_movie_released_time_list_item_click";
    
    /**【发现-电影票-正在热映-选择场次-切换日期】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_RELEASED_TIME_SWITCHING_DATE = "discover_yellowpage_movie_released_time_switching_date";
    
    /**【发现-电影票-正在热映-选择座位-确认】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_RELEASED_SEAT_CONFIRM = "discover_yellowpage_movie_released_seat_confirm";
    
    /**【发现-电影票-正在热映-选择座位-立即支付】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_RELEASED_SEAT_IMMEDIATE_PAY = "discover_yellowpage_movie_released_seat_immediate_pay";
    
    /**【发现-电影票-正在热映-选择座位-支付成功】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_RELEASED_SEAT_PAY_SUCCESS = "discover_yellowpage_movie_released_seat_pay_success";
    
    /**【发现-电影票-正在热映-选择座位-支付失败】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_RELEASED_SEAT_PAY_FAIL = "discover_yellowpage_movie_released_seat_pay_fail";
    
   
    
    
    
    
    
    /**【发现-电影票-即将上映】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_IN = "discover_yellowpage_movie_upcoming_in";
    
    /**【发现-电影票-正在热映-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_LIST_ITEM_CLICK = "discover_yellowpage_movie_upcoming_list_item_click";
    
    
    /**【发现-电影票-即将上映-选择影院-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_THEATER_LIST_ITEM_CLICK = "discover_yellowpage_movie_upcoming_theater_list_item_click";
    
    /**【发现-电影票-即将上映-选择场次-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_TIME_LIST_ITEM_CLICK = "discover_yellowpage_movie_upcoming_time_list_item_click";
    
    /**【发现-电影票-即将上映-选择场次-切换日期】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_TIME_SWITCHING_DATE = "discover_yellowpage_movie_upcoming_time_switching_date";
    
    
    /**【发现-电影票-即将上映-选择座位-确认】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_SEAT_CONFIRM = "discover_yellowpage_movie_upcoming_seat_confirm";
    
    /**【发现-电影票-即将上映-选择座位-立即支付】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_SEAT_IMMEDIATE_PAY = "discover_yellowpage_movie_upcoming_seat_immediate_pay";
    
    /**【发现-电影票-即将上映-选择座位-支付成功】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_SEAT_PAY_SUCCESS = "discover_yellowpage_movie_upcoming_seat_pay_success";
    
    /**【发现-电影票-即将上映-选择座位-支付失败】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_SEAT_PAY_FAIL = "discover_yellowpage_movie_upcoming_seat_pay_fail";
    
    
    
    
    /**【发现-电影票-汇总-点击电影列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_ALL_LIST_ITEM_CLICK = "discover_yellowpage_movie_all_list_item_click";
    
    /**【发现-电影票-汇总-选择影院-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_ALL_THEATER_LIST_ITEM_CLICK = "discover_yellowpage_movie_all_theater_list_item_click";
   
    /**【发现-电影票-汇总-选择场次-点击列表项】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_ALL_TIME_LIST_ITEM_CLICK = "discover_yellowpage_movie_all_time_list_item_click";
    
    /**【发现-电影票-汇总-选择场次-切换日期】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_ALL_TIME_SWITCHING_DATE = "discover_yellowpage_movie_all_time_switching_date";
    
    /**【发现-电影票-汇总-选择座位-确认】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_ALL_SEAT_CONFIRM = "discover_yellowpage_movie_all_seat_confirm";
    
    /**【发现-电影票-汇总-选择座位-立即支付】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_ALL_IMMEDIATE_PAY = "discover_yellowpage_movie_all_immediate_pay";
    
    /**【发现-电影票-汇总-选择座位-支付成功】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_ALL_SEAT_PAY_SUCCESS = "discover_yellowpage_movie_all_seat_pay_success";
    
    /**【发现-电影票-汇总-选择座-支付失败】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_MOVIE_AKK_SEAT_PAY_FAIL = "discover_yellowpage_movie_akk_seat_pay_fail";
    
    /**【发现-彩票】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_LOTTERY_IN = "discover_yellowpage_lottery_in";
    
//    /**【发现-酒店】入口进入次数*/
//    public static final String DISCOVER_YELLOWPAGE_HOTEL_IN = "discover_yellowpage_hotel_in";
    
    /**【发现-酒店-点击查询】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_SEARCH = "discover_yellowpage_hotel_search";
    
    /**【发现-酒店-选择入住日期】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_DATE_IN_CLICK = "discover_yellowpage_hotel_date_in_click";
    
    /**【发现-酒店-选择离店日期】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_DATE_OUT_CLICK = "discover_yellowpage_hotel_date_out_click";
    
    /**【发现-酒店-输入关键字】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_SEARCH_KEYWORD = "discover_yellowpage_hotel_search_keyword";
    
    /**【发现-酒店-选择星级】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_STARS_CHOOSE = "discover_yellowpage_hotel_stars_choose";
    
    /**【发现-酒店-选择价格】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_PRICE_CHOOS = "discover_yellowpage_hotel_price_choos";
    
    /**【发现-酒店-选择城市】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_CITY_CHOOSE = "discover_yellowpage_hotel_city_choose";
    
    /**【发现-酒店-搜索结果页-选择酒店】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_SEARCH_RESULT_LIST_ITEM_CLICK = "discover_yellowpage_hotel_search_result_list_item_click";
    
    /**【发现-酒店-酒店详情-选择房间】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_DETAIL_ROOM_SELECT = "discover_yellowpage_hotel_detail_room_select";
    
    /**【发现-酒店-订单填写-选择房间数】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_ROOM_AMOUNT_SELECT = "discover_yellowpage_hotel_order_room_amount_select";
    
    /**【发现-酒店-订单填写-选择最晚到店时间】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_LATEST_IN_DATE = "discover_yellowpage_hotel_order_latest_in_date";
    
    /**【发现-酒店-订单填写-提交订单】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_SUBMIT = "discover_yellowpage_hotel_order_submit";
    
    /**【发现-酒店-订单填写-提交订单成功】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_SUBMIT_SUCCESS = "discover_yellowpage_hotel_order_submit_success";
    
    /**【发现-酒店-订单填写-提交订单失败】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_HOTEL_ORDER_SUBMIT_FAIL = "discover_yellowpage_hotel_order_submit_fail";
    
    
    
    /**【发现-水电煤-汇总-充值成功（总计）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_ALL_SUCCESS = "discover_yellowpage_shuidianmei_all_success";
    
    /**【发现-水电煤-汇总-充值失败（总计）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_ALL_FAIL = "discover_yellowpage_shuidianmei_all_fail";
    
    
   
    /**【发现-水电煤-水费-充值成功（支付宝）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_ALIPAY_SUCCESS = "discover_yellowpage_shuidianmei_water_alipay_success";
    
    /**【发现-水电煤-水费-充值失败（支付宝）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_ALIPAY_FAIL = "discover_yellowpage_shuidianmei_water_alipay_fail";
    
    /**【发现-水电煤-水费-充值成功（微信）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_WECHAT_SUCCESS = "discover_yellowpage_shuidianmei_water_wechat_success";
    
    /**【发现-水电煤-水费-充值失败（微信）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_WECHAT_FAIL = "discover_yellowpage_shuidianmei_water_wechat_fail";
    
    
    
    /**【发现-水电煤-电费-充值成功（支付宝）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_ALIPAY_SUCCESS = "discover_yellowpage_shuidianmei_electricity_alipay_success";
    
    /**【发现-水电煤-电费-充值失败（支付宝）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_ALIPAY_FAIL = "discover_yellowpage_shuidianmei_electricity_alipay_fail";
    
    /**【发现-水电煤-电费-充值成功（微信）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_WECHAT_SUCCESS = "discover_yellowpage_shuidianmei_electricity_wechat_success";
    
    /**【发现-水电煤-电费-充值失败（微信）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_WECHAT_FAIL = "discover_yellowpage_shuidianmei_electricity_wechat_fail";
    
    
    
    /**【发现-水电煤-燃气费-充值成功（支付宝）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_ALIPAY_SUCCESS = "discover_yellowpage_shuidianmei_gas_alipay_success";
    
    /**【发现-水电煤-燃气费-充值失败（支付宝）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_ALIPAY_FAIL = "discover_yellowpage_shuidianmei_gas_alipay_fail";
    
    /**【发现-水电煤-燃气费-充值成功（微信）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_WECHAT_SUCCESS = "discover_yellowpage_shuidianmei_gas_wechat_success";
    
    /**【发现-水电煤-燃气费-充值失败（微信）】入口进入次数*/
    public static final String DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_WECHAT_FAIL = "discover_yellowpage_shuidianmei_gas_wechat_fail";
    
    
    /**【发现-搜索-同程酒店-搜索成功】*/
    public static final String DISCOVER_YELLOWPAGE_SEARCH_TONGCHENG_SUCCESS = "discover_yellowpage_search_tongcheng_success";
    
    /**【发现-搜索-同程酒店-无结果】*/
    public static final String DISCOVER_YELLOWPAGE_SEARCH_TONGCHENG_NO_DATA = "discover_yellowpage_search_tongcheng_no_data";
    
    /**【发现-搜索-同程酒店-搜索异常】*/
    public static final String DISCOVER_YELLOWPAGE_SEARCH_TONGCHENG_FAIL = "discover_yellowpage_search_tongcheng_fail";
    
    /**包含搜索和各分类入口进入商户详情电话汇总数*/
    public static final String DISCOVER_YELLOWPAGE_DETAIL_CALL = "discover_yellowpage_detail_call_all";
    
    /**包含所有h5页面拨打电话的汇总数*/
    public static final String DISCOVER_YELLOWPAGE_H5_PAGE_CALL_ALL = "discover_yellowpage_h5_page_call_all";
   
    //add xcx 2014-12-29 end 新增统计埋点
    
}
