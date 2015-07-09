package so.contacts.hub.thirdparty.tongcheng.xmlparse;

import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_CancelOrder;
import android.util.Xml;

public class TC_CancelOrder_XMLParser implements TC_IXMLBaseParser {

	@Override
	public TC_Response_CancelOrder parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		TC_Response_CancelOrder cancelOrderObject = new TC_Response_CancelOrder();
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
					cancelOrderObject.setRspType(rspType);
				}else if ( startTag.equalsIgnoreCase("rspCode") ) {
					rspCode = parser.nextText();
					cancelOrderObject.setRspCode(rspCode);
				}else if ( startTag.equalsIgnoreCase("rspDesc") ) {
					rspDesc = parser.nextText();
					cancelOrderObject.setRspDesc(rspDesc);
				}else if ( startTag.equalsIgnoreCase("serialId") ) {
					cancelOrderObject.setSerialId(parser.nextText());
				}
        		break;
        	 case XmlPullParser.END_TAG:
        		 break;
    		 default:
    			 break;
        	}
        	evtType = parser.next();
        }
		
		return cancelOrderObject;
	}

}
