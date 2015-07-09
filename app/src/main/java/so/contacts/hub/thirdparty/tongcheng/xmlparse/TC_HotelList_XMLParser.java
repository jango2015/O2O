package so.contacts.hub.thirdparty.tongcheng.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import android.text.TextUtils;
import android.util.Xml;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelList;

public class TC_HotelList_XMLParser implements TC_IXMLBaseParser {
	
	private static final String HOTELLIST_TAG = "hotelList";

	private static final String HOTEL_TAG = "hotel";

	@Override
	public TC_Response_HotelList parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		TC_Response_HotelList hotelListObject = new TC_Response_HotelList();
		List<TC_HotelBean> hotelList = new ArrayList<TC_HotelBean>();
		TC_HotelBean hotelBean = null;
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
					hotelListObject.setRspType(rspType);
				}else if ( startTag.equalsIgnoreCase("rspCode") ) {
					rspCode = parser.nextText();
					hotelListObject.setRspCode(rspCode);
				}else if ( startTag.equalsIgnoreCase("rspDesc") ) {
					rspDesc = parser.nextText();
					hotelListObject.setRspDesc(rspDesc);
				}else if ( startTag.equalsIgnoreCase(HOTELLIST_TAG) ) {
					String addributeData = parser.getAttributeValue(null, "page");
					if( !TextUtils.isEmpty(addributeData) ){
						hotelListObject.setPage(Integer.valueOf(addributeData));
					}
					addributeData = parser.getAttributeValue(null, "pageSize");
					if( !TextUtils.isEmpty(addributeData) ){
						hotelListObject.setPageSize(Integer.valueOf(addributeData));
					}
					addributeData = parser.getAttributeValue(null, "totalPage");
					if( !TextUtils.isEmpty(addributeData) ){
						hotelListObject.setTotalPage(Integer.valueOf(addributeData));
					}
					addributeData = parser.getAttributeValue(null, "imageBaseUrl");
					if( !TextUtils.isEmpty(addributeData) ){
						imageBaseUrl = addributeData;
						hotelListObject.setImageBaseUrl(addributeData);
					}
				}else if ( startTag.equalsIgnoreCase(HOTEL_TAG) ) {
					hotelBean = new TC_HotelBean();
				}else if ( hotelBean != null ) {
					if (startTag.equalsIgnoreCase("hotelId")) {
						hotelBean.setHotelId(parser.nextText());
					} else if (startTag.equalsIgnoreCase("hotelName")) {
						hotelBean.setHotelName(parser.nextText());
					} else if (startTag.equalsIgnoreCase("address")) {
						hotelBean.setAddress(parser.nextText());
					} else if (startTag.equalsIgnoreCase("longitude")) {
						hotelBean.setLongitude(parser.nextText());
					} else if (startTag.equalsIgnoreCase("latitude")) {
						hotelBean.setLatitude(parser.nextText());
					} else if (startTag.equalsIgnoreCase("distance")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							hotelBean.setDistance(Double.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("img")) {
						hotelBean.setImg(imageBaseUrl + parser.nextText());
					} else if (startTag.equalsIgnoreCase("bonusRate")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							hotelBean.setBonusRate(Float.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("lowestPrice")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							hotelBean.setLowestPrice(Double.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("commentTotal")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							hotelBean.setCommentTotal(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("commentGood")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							hotelBean.setCommentGood(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("commentMid")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							hotelBean.setCommentMid(Integer.valueOf(data));
						}
					} else if (startTag.equalsIgnoreCase("starRatedName")) {
						hotelBean.setStarRatedName(parser.nextText());
					} else if (startTag.equalsIgnoreCase("starRatedId")) {
					    String data = parser.nextText();
					    if( !TextUtils.isEmpty(data) ){
					        hotelBean.setStarRatedId(Integer.valueOf(data));
					    }
                    } else if (startTag.equalsIgnoreCase("bizSectionName")) {
						hotelBean.setBizSectionName(parser.nextText());
					} else if (startTag.equalsIgnoreCase("bizSectionId")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							hotelBean.setBizSectionId(Integer.valueOf(data));
						}
					}
				}
        		break;
        	 case XmlPullParser.END_TAG:
        		 String endTag = parser.getName();
        		 if ( endTag.equalsIgnoreCase(HOTEL_TAG) && hotelBean != null) { 
        			 hotelList.add(hotelBean); 
        			 hotelBean = null; 
                 } else if( endTag.equalsIgnoreCase(HOTELLIST_TAG) ){
                	 if( hotelListObject != null ){
                		 hotelListObject.setHotelList(hotelList);
                	 }
                 }
        		 break;
    		 default:
    			 break;
        	}
        	evtType = parser.next();
        }
		
		return hotelListObject;
	}

}
