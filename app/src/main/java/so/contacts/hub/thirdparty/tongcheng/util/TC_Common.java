package so.contacts.hub.thirdparty.tongcheng.util;

import so.contacts.hub.core.Config;

public class TC_Common {

	// 请求头 - 协议当前使用的版本号
	public static String TC_VERSION = "20111128102912";

	// 请求头 - 帐户标识号
	public static String TC_ACCOUNT_ID = "6f0e6cc1-ba25-4ad7-8208-c747282ba01f";

	// 请求头 - 服务名称
	public static String TC_ACCOUNT_PW = "385fa7cdc06b3495";
	
	
	//////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	
	
	// 酒店搜索URL
	public static String TC_URL_SEARCH_HOTEL = Config.TC_HOTEL.TC_URL_SEARCH_HOTEL;
	
	// 订单搜索URL
	public static String TC_URL_SEARCH_ORDER = Config.TC_HOTEL.TC_URL_SEARCH_ORDER;
	
	// 区域查找URL
	public static String TC_URL_SEARCH_DIVISION = Config.TC_HOTEL.TC_URL_SEARCH_DIVISION;

	/**
	 * 搜索结果排序策略 start
	 */
	//搜索结果排序策略： 5为按照距离由近及远排序
	public static final int TC_SEARCH_SORT_TYPE_DISTANCE = 5;
	
	//搜索结果排序策略：6为同程推荐排序
	public static final int TC_SEARCH_SORT_TYPE_DEFAULT = 6;
	/** 搜索结果排序策略  end*/
	
	// 酒店订单详情打车跳转到快滴页面
	public static final String TC_KUAIDIACTIVITY = Config.TC_HOTEL.TC_KUAIDIACTIVITY;
	
	// 订单详情打车URL
	public static String TC_URL_KUAIDI = Config.TC_HOTEL.TC_URL_KUAIDI;
}
