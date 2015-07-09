package so.contacts.hub.thirdparty.tongcheng.xmlparse;

import java.io.InputStream;

import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_CancelOrder;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelInfo;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelList;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelRoomsWithPolicy;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_OrderDetail;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_SubmitHotelOrder;

public class TC_XMLParser {

	public static Object parseXML(InputStream inputStream, Class<?> clz)
			throws Exception {
		// TODO Auto-generated method stub
		Object object = null;
		TC_IXMLBaseParser xmlBaseParser = null;
		if (clz.isAssignableFrom(TC_Response_HotelList.class)) {
			// 解析 酒店列表
			xmlBaseParser = new TC_HotelList_XMLParser();
		} else if (clz.isAssignableFrom(TC_Response_HotelInfo.class)) {
			// 解析 酒店详情
			xmlBaseParser = new TC_HotelInfo_XMLParser();
		} else if (clz.isAssignableFrom(TC_Response_SubmitHotelOrder.class)) {
			// 解析 提交酒店订单 返回结果
			xmlBaseParser = new TC_SubmitHotelOrder_XMLParser();
		} else if (clz.isAssignableFrom(TC_Response_OrderDetail.class)) {
			// 解析 提交酒店订单详情 返回结果
			xmlBaseParser = new TC_OrderDetail_XMLParser();
		} else if (clz.isAssignableFrom(TC_Response_HotelRoomsWithPolicy.class)) {
			// 解析 提交酒店房型价格（政策）
			xmlBaseParser = new TC_HotelRoomsWithPolicy_XMLParser();
		} else if (clz.isAssignableFrom(TC_Response_CancelOrder.class)) {
			// 解析 取消订单
			xmlBaseParser = new TC_CancelOrder_XMLParser();
		} else {
			// 任意类型的数据解析
		}
		if (xmlBaseParser != null) {
			object = xmlBaseParser.parse(inputStream);
		}
		return object;
	}

}
