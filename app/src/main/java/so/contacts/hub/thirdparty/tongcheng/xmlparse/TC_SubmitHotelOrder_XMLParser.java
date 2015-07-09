package so.contacts.hub.thirdparty.tongcheng.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;
import android.util.Xml;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelOrderBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelSameOrderBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_SubmitHotelOrder;

public class TC_SubmitHotelOrder_XMLParser implements TC_IXMLBaseParser {

	private static final String HOTELORDER_TAG = "order";

	private static final String SAMEORDERLIST_TAG = "sameOrderInfoList";

	private static final String SAMEORDER_TAG = "sameOrderInfo"; 
	
	@Override
	public TC_Response_SubmitHotelOrder parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		TC_Response_SubmitHotelOrder submitHotelOrderObject = new TC_Response_SubmitHotelOrder();

		TC_HotelOrderBean hotelOrderBean = null;
		List<TC_HotelSameOrderBean> sameOrderInfoList = new ArrayList<TC_HotelSameOrderBean>();
		TC_HotelSameOrderBean sameHotelOrderBean = null;
		
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
				if (startTag.equalsIgnoreCase("rspType")) {
					rspType = parser.nextText();
					submitHotelOrderObject.setRspType(rspType);
				} else if (startTag.equalsIgnoreCase("rspCode")) {
					rspCode = parser.nextText();
					submitHotelOrderObject.setRspCode(rspCode);
				} else if (startTag.equalsIgnoreCase("rspDesc")) {
					rspDesc = parser.nextText();
					submitHotelOrderObject.setRspDesc(rspDesc);
				} else if( startTag.equalsIgnoreCase(HOTELORDER_TAG) ){ //正常订单结果
					hotelOrderBean = new TC_HotelOrderBean();
				} else if (startTag.equalsIgnoreCase(SAMEORDER_TAG)) { //重复订单
					sameHotelOrderBean = new TC_HotelSameOrderBean();
				} else if ( startTag.equalsIgnoreCase("serialId") && hotelOrderBean != null) {
					hotelOrderBean.setSerialId(parser.nextText()); //正常订单结果
				} else if ( startTag.equalsIgnoreCase("amount") && hotelOrderBean != null) {
					String data = parser.nextText();
					if( !TextUtils.isEmpty(data) ){
						hotelOrderBean.setAmount(Double.valueOf(data));
					}
				} else if (startTag.equalsIgnoreCase("tcOrder") && sameHotelOrderBean != null) {
					sameHotelOrderBean.setTcOrder(parser.nextText()); //重复订单结果
				}else if (startTag.equalsIgnoreCase("allianceOrder") && sameHotelOrderBean != null) {
					sameHotelOrderBean.setAllianceOrder(parser.nextText()); //重复订单结果
				}
        		break;
        	 case XmlPullParser.END_TAG:
        		 String endTag = parser.getName();
        		 if( endTag.equalsIgnoreCase(HOTELORDER_TAG) ){
                	 if( submitHotelOrderObject != null ){
                		 submitHotelOrderObject.setHotelorder(hotelOrderBean);
                	 }
                 } else if( endTag.equalsIgnoreCase(SAMEORDER_TAG) && sameHotelOrderBean != null){
                	 sameOrderInfoList.add(sameHotelOrderBean);
                 } else if( endTag.equalsIgnoreCase(SAMEORDERLIST_TAG) ){
                	 if( submitHotelOrderObject != null ){
                		 submitHotelOrderObject.setSameOrderInfoList(sameOrderInfoList);
                	 }
                 }
        		 break;
    		 default:
    			 break;
        	}
        	evtType = parser.next();
        }
		
		return submitHotelOrderObject;
	}

}
