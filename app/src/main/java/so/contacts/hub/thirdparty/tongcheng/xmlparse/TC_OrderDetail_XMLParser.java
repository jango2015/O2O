package so.contacts.hub.thirdparty.tongcheng.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_OrderDetailBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_OrderDetail;
import android.text.TextUtils;
import android.util.Xml;

public class TC_OrderDetail_XMLParser implements TC_IXMLBaseParser {

	private static final String ORDERLIST_TAG = "orderList";

	private static final String ORDER_TAG = "order";

	@Override
	public TC_Response_OrderDetail parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		TC_Response_OrderDetail orderDetailObject = new TC_Response_OrderDetail();
		List<TC_OrderDetailBean> orderList = new ArrayList<TC_OrderDetailBean>();
		TC_OrderDetailBean orderDetailBean = null;
		String rspType = "";
		String rspCode = "";
		String rspDesc = "";
		XmlPullParser parser = Xml.newPullParser();	//由android.util.Xml创建一个XmlPullParser实例
    	parser.setInput(is, "UTF-8");				//设置输入流 并指明编码方式
    	
    	int evtType = parser.getEventType();
    	while(evtType != XmlPullParser.END_DOCUMENT){ 
        	switch(evtType){ 
        	case XmlPullParser.START_TAG:
				String startTag = parser.getName();
				if ( startTag.equalsIgnoreCase("rspType") ) {
					rspType = parser.nextText();
					orderDetailObject.setRspType(rspType);
				}else if ( startTag.equalsIgnoreCase("rspCode") ) {
					rspCode = parser.nextText();
					orderDetailObject.setRspCode(rspCode);
				}else if ( startTag.equalsIgnoreCase("rspDesc") ) {
					rspDesc = parser.nextText();
					orderDetailObject.setRspDesc(rspDesc);
				}else if ( startTag.equalsIgnoreCase(ORDER_TAG) ) {
					orderDetailBean = new TC_OrderDetailBean();
				}else if ( orderDetailBean != null ) {
					if (startTag.equalsIgnoreCase("serialId")) {
						orderDetailBean.setSerialId(parser.nextText());
					} else if (startTag.equalsIgnoreCase("orderStatus")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean.setOrderStatus(Integer
									.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("createDate")) {
						orderDetailBean.setCreateDate(parser.nextText());
					} else if (startTag.equalsIgnoreCase("confirmDate")) {
						orderDetailBean.setConfirmDate(parser.nextText());
					} else if (startTag.equalsIgnoreCase("checkin")) {
						orderDetailBean.setCheckin(parser.nextText());
					} else if (startTag.equalsIgnoreCase("checkout")) {
						orderDetailBean.setCheckout(parser.nextText());
					} else if (startTag.equalsIgnoreCase("arriveTime")) {
						orderDetailBean.setArriveTime(parser.nextText());
					} else if (startTag.equalsIgnoreCase("hotelId")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean.setHotelId(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("hotelName")) {
						orderDetailBean.setHotelName(parser.nextText());
					} else if (startTag.equalsIgnoreCase("hotelAddress")) {
						orderDetailBean.setHotelAddress(parser.nextText());
					} else if (startTag.equalsIgnoreCase("hotelTel")) {
						orderDetailBean.setHotelTel(parser.nextText());
					} else if (startTag.equalsIgnoreCase("roomName")) {
						orderDetailBean.setRoomName(parser.nextText());
					} else if (startTag.equalsIgnoreCase("roomQuantity")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean.setRoomQuantity(Integer
									.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("bookingMobile")) {
						orderDetailBean.setBookingMobile(parser.nextText());
					} else if (startTag.equalsIgnoreCase("guestName")) {
						orderDetailBean.setGuestName(parser.nextText());
					} else if (startTag.equalsIgnoreCase("guestMobile")) {
						orderDetailBean.setGuestMobile(parser.nextText());
					} else if (startTag.equalsIgnoreCase("orderType")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean.setOrderType(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("orderAmount")) {
						orderDetailBean.setOrderAmount(parser.nextText());
					} else if (startTag.equalsIgnoreCase("guaranteeAmount")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean.setGuaranteeAmount(Double
									.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("prizeAmount")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean
									.setPrizeAmount(Double.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("isRegret")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean.setIsRegret(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("uncertainReason")) {
						orderDetailBean.setUncertainReason(parser.nextText());
					} else if (startTag.equalsIgnoreCase("enableCanel")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean.setEnableCanel(Integer
									.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("ctipOrderId")) {
						orderDetailBean.setCtipOrderId(parser.nextText());
					} else if (startTag.equalsIgnoreCase("cashBackStatus")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean.setCashBackStatus(Integer
									.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("cashBackReason")) {
						orderDetailBean.setCashBackReason(parser.nextText());
					} else if (startTag.equalsIgnoreCase("cashBackPrice")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean.setCashBackPrice(Double
									.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("cashBackApplyStatus")) {
						String data = parser.nextText();
						if (!TextUtils.isEmpty(data)) {
							orderDetailBean.setCashBackApplyStatus(Integer
									.valueOf(data));
						}
					}
				}
        		break;
        	 case XmlPullParser.END_TAG:
        		 String endTag = parser.getName();
        		 if ( endTag.equalsIgnoreCase(ORDER_TAG) && orderDetailBean != null) { 
        			 orderList.add(orderDetailBean); 
        			 orderDetailBean = null; 
                 } else if( endTag.equalsIgnoreCase(ORDERLIST_TAG) ){
                	 if( orderDetailObject != null ){
                		 orderDetailObject.setOrderList(orderList);
                	 }
                 }
        		 break;
    		 default:
    			 break;
        	}
        	evtType = parser.next();
        }
		
		
		return orderDetailObject;
	}

}
