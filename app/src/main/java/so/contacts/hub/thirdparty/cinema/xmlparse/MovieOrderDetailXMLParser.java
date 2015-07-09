package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;
import android.util.Xml;
import so.contacts.hub.thirdparty.cinema.CinemaConfig;
import so.contacts.hub.thirdparty.cinema.bean.CinemaRoomInfo;
import so.contacts.hub.thirdparty.cinema.bean.DetailMovieOrder;
import so.contacts.hub.thirdparty.cinema.bean.CinemaRoomInfo.SeatRow;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelList;
import so.contacts.hub.util.InputMethodUtil;

public class MovieOrderDetailXMLParser implements CinemaIXMLBaseParser {
	
	public static final String TAG = CinemaConfig.TAG;

	@Override
	public DetailMovieOrder parse(InputStream is) throws Exception {
		DetailMovieOrder order = new DetailMovieOrder();;
		List<SeatRow > seatList = null; 
		SeatRow seatRow = null; 

		String rspType = "";
		String rspCode = "";
		String rspDesc = "";
		
//		InputStreamReader reader=new InputStreamReader(is);
//		int len=-1;
//		byte[] b=new byte[100];
//		while ((len=is.read(b))!=-1) {
//		    System.out.println(new String(b,0,len));
//        }
//		is.reset();
		
		XmlPullParser parser = Xml.newPullParser();	//由android.util.Xml创建一个XmlPullParser实例
    	parser.setInput(is, "UTF-8");				//设置输入流 并指明编码方式
    	
    	int evtType = parser.getEventType();
        while(evtType != XmlPullParser.END_DOCUMENT){ 
//            System.out.println(evtType);
//            System.out.println(parser.nextText());
        	switch(evtType){ 
        	case XmlPullParser.START_TAG:
				String startTag = parser.getName();
				if(startTag.equalsIgnoreCase("ticketOrder")) {
				} else if(startTag.equalsIgnoreCase("mpid")) {
				    order.mp_id =Long.parseLong(parser.nextText());
				} else if(startTag.equalsIgnoreCase("movieid")) {
				    order.movie_id =Long.parseLong(parser.nextText());
				} else if(startTag.equalsIgnoreCase("moviename")) {
                    order.movie_name =parser.nextText();
                } else if(startTag.equalsIgnoreCase("cinemaname")) {
					order.cinema_name = parser.nextText();
				} else if(startTag.equalsIgnoreCase("citycode")) {
					order.city_code = parser.nextText();
				} else if(startTag.equalsIgnoreCase("cityname")) {
					order.city_name = parser.nextText();
				} else if(startTag.equalsIgnoreCase("cinemaid")) {
                    order.cinema_id = Integer.parseInt(parser.nextText());
				} else if(startTag.equalsIgnoreCase("tradeno")) {
					order.trade_no = parser.nextText();
				} else if(startTag.equalsIgnoreCase("mobile")) {
					order.mobile = parser.nextText();
				} else if(startTag.equalsIgnoreCase("validtime")) {
				    order.valid_time = parser.nextText();
				} else if(startTag.equalsIgnoreCase("discount")) {
					order.discount = Integer.parseInt(parser.nextText());
				} else if(startTag.equalsIgnoreCase("disreason")) {
					order.dis_reason = parser.nextText();
				} else if(startTag.equalsIgnoreCase("amount")) {
					order.amount = Integer.parseInt(parser.nextText())*100;
				} else if(startTag.equalsIgnoreCase("unitprice")) {
					order.unit_price = Integer.parseInt(parser.nextText());
				} else if(startTag.equalsIgnoreCase("quantity")) {
					order.quantity = Integer.parseInt(parser.nextText());
				} else if(startTag.equalsIgnoreCase("addtime")) {
				    order.add_time = parser.nextText();
				} else if(startTag.equalsIgnoreCase("roomname")) {
					order.room_name = parser.nextText();
				} else if(startTag.equalsIgnoreCase("playtime")) {
					order.play_time = parser.nextText();
				} else if(startTag.equalsIgnoreCase("seat")) {
					order.seat = parser.nextText();
				} else if(startTag.equalsIgnoreCase("status")) {
					order.status = parser.nextText();
				} else if(startTag.equalsIgnoreCase("ordertitle")) {
					order.order_title = parser.nextText();
				} else if(startTag.equalsIgnoreCase("paidtime")) {
					order.paid_time = parser.nextText();
				} else if(startTag.equalsIgnoreCase("paidAmount")) {
					order.paid_amount = Integer.parseInt(parser.nextText());
				} else if(startTag.equalsIgnoreCase("payseqno")) {
				    order.payseq_no = parser.nextText();
				} else if(startTag.equalsIgnoreCase("ukey")) {
				    order.ukey = parser.nextText();
				} else if(startTag.equalsIgnoreCase("error")) {
				    order.error = parser.nextText();
				} 
				break;
        	 case XmlPullParser.END_TAG:
        		 String endTag = parser.getName();
        		 break;
    		 default:
    			 break;
        	}
        	evtType = parser.next();
        }
		
		return order;
	}

}
