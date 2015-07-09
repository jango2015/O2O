package so.contacts.hub.thirdparty.tongcheng.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import android.text.TextUtils;
import android.util.Xml;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelRoomBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelRoomsWithPolicy;

public class TC_HotelRoomsWithPolicy_XMLParser implements TC_IXMLBaseParser {

	private static final String ROOMLIST_TAG = "hotelRoomList";

	private static final String ROOM_TAG = "hotelRoomInfo";

	@Override
	public TC_Response_HotelRoomsWithPolicy parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		TC_Response_HotelRoomsWithPolicy hotelRoomsObject = new TC_Response_HotelRoomsWithPolicy();
		List<TC_HotelRoomBean> roomList = new ArrayList<TC_HotelRoomBean>();
		TC_HotelRoomBean roomBean = null;
		String imageBaseUrl = "";
		
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
					hotelRoomsObject.setRspType(rspType);
				}else if ( startTag.equalsIgnoreCase("rspCode") ) {
					rspCode = parser.nextText();
					hotelRoomsObject.setRspCode(rspCode);
				}else if ( startTag.equalsIgnoreCase("rspDesc") ) {
					rspDesc = parser.nextText();
					hotelRoomsObject.setRspDesc(rspDesc);
				}else if ( startTag.equalsIgnoreCase(ROOMLIST_TAG) ) {
					String addributeData = parser.getAttributeValue(null, "hotelId");
					if( !TextUtils.isEmpty(addributeData) ){
						hotelRoomsObject.setHotelId(addributeData);
					}
					addributeData = parser.getAttributeValue(null, "totalCount");
					if( !TextUtils.isEmpty(addributeData) ){
						hotelRoomsObject.setTotalcount(Integer.valueOf(addributeData));
					}
					addributeData = parser.getAttributeValue(null, "imageBaseUrl");
					if( !TextUtils.isEmpty(addributeData) ){
						imageBaseUrl = addributeData;
						hotelRoomsObject.setImagebaseurl(addributeData);
					}
				}else if ( startTag.equalsIgnoreCase(ROOM_TAG) ) {
					roomBean = new TC_HotelRoomBean();
				}else if ( roomBean != null ) {
					if (startTag.equalsIgnoreCase("photourl")) {
						roomBean.setPhotoUrl(imageBaseUrl + parser.nextText());
					} else if (startTag.equalsIgnoreCase("roomTypeId")) {
						roomBean.setRoomTypeId(parser.nextText());
					} else if (startTag.equalsIgnoreCase("roomName")) {
						roomBean.setRoomName(parser.nextText());
					} else if (startTag.equalsIgnoreCase("bed")) {
						roomBean.setBed(parser.nextText());
					} else if (startTag.equalsIgnoreCase("breakfast")) {
						roomBean.setBreakfast(parser.nextText());
					} else if (startTag.equalsIgnoreCase("policyId")) {
						roomBean.setPolicyId(parser.nextText());
					} else if (startTag.equalsIgnoreCase("roomAdviceAmount")) {
						roomBean.setRoomAdviceAmount(parser.nextText());
					} else if (startTag.equalsIgnoreCase("roomPrize")) {
						roomBean.setRoomPrize(parser.nextText());
					} else if (startTag.equalsIgnoreCase("avgAmount")) {
						roomBean.setAvgAmount(parser.nextText());
					} else if (startTag.equalsIgnoreCase("danBaoType")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							roomBean.setDanBaoType(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("overTime")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							roomBean.setOverTime(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("presentFlag")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							roomBean.setPresentFlag(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("guaranteeType")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							roomBean.setGuaranteeType(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("guaranteeFlag")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							roomBean.setGuaranteeFlag(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("surplusRooms")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							roomBean.setSurplusRooms(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("bookingFlag")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							roomBean.setBookingFlag(Integer.valueOf(data));
						}
					}
				}
        		break;
        	 case XmlPullParser.END_TAG:
        		 String endTag = parser.getName();
        		 if ( endTag.equalsIgnoreCase(ROOM_TAG) && roomBean != null) { 
        			 roomList.add(roomBean); 
        			 roomBean = null; 
                 } else if( endTag.equalsIgnoreCase(ROOMLIST_TAG) ){
                	 if( hotelRoomsObject != null ){
                		 hotelRoomsObject.setHotelroomlist(roomList);
                	 }
                 }
        		 break;
    		 default:
    			 break;
        	}
        	evtType = parser.next();
        }
		
		return hotelRoomsObject;
	}

}
