package so.contacts.hub.thirdparty.elong.tool;

import android.text.TextUtils;

/**
 * 艺龙H5 页面API接口
 * @author evan
 *
 */
public class ELongH5ApiUtil {

	private static final String ELONG_URL = "http://m.elong.com";
	
	/**
	 * 获取酒店列表页
	 */
	public static String getHotelListH5Url(String city, String inDate, String outDate, String price, String star, String hotword){
		return getHotelListH5Url(city, 0, 0, inDate, outDate, price, star, hotword);
	}
	
	/**
	 * 获取附近的酒店列表页
	 */
	public static String getHotelListH5Url(String city, double latitude,
			double longitude, String inDate, String outDate, String price, String star, String hotword){
		StringBuffer requestH5Url = new StringBuffer(ELONG_URL);
		requestH5Url.append("/Hotel/List?ref=putao&");
		requestH5Url.append(getUrlParamterStr(city, latitude, longitude, inDate, outDate, price, star, hotword));
		return requestH5Url.toString();
	}
	
	/**
	 * 获取酒店订单详情页
	 */
	public static String getHotelDetailH5Url(String hotelId, String roomId, int rpId, String inDate, String outDate){
		StringBuffer requestH5Url = new StringBuffer(ELONG_URL);
		requestH5Url.append("/Hotel/order?ref=putao&");
		requestH5Url.append("hotelid=" + hotelId);
		requestH5Url.append("&roomid=" + roomId);
		requestH5Url.append("&rateplanid=" + rpId);
		if( !TextUtils.isEmpty(inDate) && !TextUtils.isEmpty(outDate)){
			requestH5Url.append("&checkindate=" + inDate);
			requestH5Url.append("&checkoutdate=" + outDate);
		}
		return requestH5Url.toString();
		
	}
	
	/**
	 * 组装url
	 * @param city
	 * @param inDate
	 * @param outDate
	 * @param price
	 * @param star
	 * @param hotword
	 * @return
	 */
	private static String getUrlParamterStr(String city, double latitude,
			double longitude, String inDate, String outDate, String price, String star, String hotword){
		StringBuffer paramterStr = new StringBuffer();
		if( !TextUtils.isEmpty(city) ){
			paramterStr.append("cityname=" + city);
		}
		if( latitude != 0 && longitude != 0){
			paramterStr.append("&lat=" + latitude); 
			paramterStr.append("&lng=" + longitude);
			paramterStr.append("&lbstype=3"); // 代表经纬度来源（3：高德）
		}
		if( !TextUtils.isEmpty(inDate) ){
			paramterStr.append("&checkindate=" + inDate);
		}
		if( !TextUtils.isEmpty(outDate) ){
			paramterStr.append("&checkoutdate=" + outDate);
		}
		
		if( !TextUtils.isEmpty(price) ){
			String[] priceList = price.split("-");
			if( priceList != null ){
				if( priceList.length == 1 ){
					paramterStr.append("&minprice=" + priceList[0]);
				}else if( priceList.length == 2 ){
					paramterStr.append("&minprice=" + priceList[0]);
					paramterStr.append("&maxprice=" + priceList[1]);
				}
			}
		}
		
		if( !TextUtils.isEmpty(star) ){
			paramterStr.append("&star=" + star);
		}
		if( !TextUtils.isEmpty(hotword) ){
			paramterStr.append("&keywords=" + hotword);
		}
		
		return paramterStr.toString();
	}
	
}





























