package so.contacts.hub.thirdparty.tongcheng.xmlparse;

import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import android.text.TextUtils;
import android.util.Xml;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelInfoBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelInfo;

public class TC_HotelInfo_XMLParser implements TC_IXMLBaseParser {


	private static final String HOTELINFO_TAG = "hotelinfo";
	
	@Override
	public TC_Response_HotelInfo parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		TC_Response_HotelInfo hotelInfoObject = new TC_Response_HotelInfo();
		TC_HotelInfoBean hotelInfoBean = new TC_HotelInfoBean();
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
					hotelInfoObject.setRspType(rspType);
				}else if ( startTag.equalsIgnoreCase("rspCode") ) {
					rspCode = parser.nextText();
					hotelInfoObject.setRspCode(rspCode);
				}else if ( startTag.equalsIgnoreCase("rspDesc") ) {
					rspDesc = parser.nextText();
					hotelInfoObject.setRspDesc(rspDesc);
				}else if ( hotelInfoBean != null ) {
					if (startTag.equalsIgnoreCase("hotelId")) {
						String data = parser.nextText();
						if( !TextUtils.isEmpty(data) ){
							hotelInfoBean.setHotelId(data);
						}
					} else if (startTag.equalsIgnoreCase("hotelName")) {
						hotelInfoBean.setHotelName(parser.nextText());
					} else if (startTag.equalsIgnoreCase("starRatedName")) {
						hotelInfoBean.setStarRatedName(parser.nextText());
					} else if (startTag.equalsIgnoreCase("openingDate")) {
						hotelInfoBean.setOpeningDate(parser.nextText());
					} else if (startTag.equalsIgnoreCase("address")) {
						hotelInfoBean.setAddress(parser.nextText());
					}
				}
        		break;
        	 case XmlPullParser.END_TAG:
        		 String endTag = parser.getName();
        		 if( endTag.equalsIgnoreCase(HOTELINFO_TAG) ){
                	 if( hotelInfoObject != null ){
                		 hotelInfoObject.setHotelInfo(hotelInfoBean);
                	 }
                 }
        		 break;
    		 default:
    			 break;
        	}
        	evtType = parser.next();
        }
		
		return hotelInfoObject;
	}

}
