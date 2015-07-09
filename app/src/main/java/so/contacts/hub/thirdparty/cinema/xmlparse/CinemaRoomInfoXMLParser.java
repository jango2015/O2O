package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;
import android.util.Xml;
import so.contacts.hub.thirdparty.cinema.CinemaConfig;
import so.contacts.hub.thirdparty.cinema.bean.CinemaRoomInfo;
import so.contacts.hub.thirdparty.cinema.bean.CinemaRoomInfo.SeatRow;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelList;

public class CinemaRoomInfoXMLParser implements CinemaIXMLBaseParser {
	
	public static final String TAG = CinemaConfig.TAG;

	@Override
	public CinemaRoomInfo parse(InputStream is) throws Exception {
		CinemaRoomInfo cinemaRoomInfo = null;
		ArrayList<SeatRow> seatList = null; 
		SeatRow seatRow = null; 

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
				if(startTag.equalsIgnoreCase("opiSeatInfo")) {
					cinemaRoomInfo = new CinemaRoomInfo();
				} else if(startTag.equalsIgnoreCase("edition")) {
					cinemaRoomInfo.edition =parser.nextText();
				} else if(startTag.equalsIgnoreCase("opentype")) {
					
				} else if(startTag.equalsIgnoreCase("updatetime")) {
					
				} else if(startTag.equalsIgnoreCase("moviename")) {
					cinemaRoomInfo.moviename = parser.nextText();
				} else if(startTag.equalsIgnoreCase("remark")) {
					cinemaRoomInfo.remark = parser.nextText();
				} else if(startTag.equalsIgnoreCase("gewaprice")) {
					cinemaRoomInfo.gewaprice = Integer.parseInt(parser.nextText());
				} else if(startTag.equalsIgnoreCase("cinemaid")) {
					cinemaRoomInfo.cinemaid = Long.parseLong(parser.nextText());
				} else if(startTag.equalsIgnoreCase("closetime")) {
					cinemaRoomInfo.closetime = parser.nextText();
				} else if(startTag.equalsIgnoreCase("dayctime")) {
					
				} else if(startTag.equalsIgnoreCase("servicefee")) {
					cinemaRoomInfo.servicefee = Integer.parseInt(parser.nextText());
				} else if(startTag.equalsIgnoreCase("lockminute")) {
					cinemaRoomInfo.lockminute = Float.parseFloat(parser.nextText());
				} else if(startTag.equalsIgnoreCase("roomid")) {
					cinemaRoomInfo.roomid = Long.parseLong(parser.nextText());
				} else if(startTag.equalsIgnoreCase("price")) {
					cinemaRoomInfo.price = Integer.parseInt(parser.nextText());
				} else if(startTag.equalsIgnoreCase("roomname")) {
					cinemaRoomInfo.roomname = parser.nextText();
				} else if(startTag.equalsIgnoreCase("dayotime")) {
					
				} else if(startTag.equalsIgnoreCase("roomtype")) {
					cinemaRoomInfo.roomtype = parser.nextText();
				} else if(startTag.equalsIgnoreCase("cinemaname")) {
					cinemaRoomInfo.cinemaname = parser.nextText();
				} else if(startTag.equalsIgnoreCase("maxseat")) {
					cinemaRoomInfo.maxseat = Integer.parseInt(parser.nextText());
				} else if(startTag.equalsIgnoreCase("language")) {
					cinemaRoomInfo.language = parser.nextText();
				} else if(startTag.equalsIgnoreCase("playtime")) {
					cinemaRoomInfo.playtime = parser.nextText();
				} else if(startTag.equalsIgnoreCase("mpid")) {
					cinemaRoomInfo.mpid = Long.parseLong(parser.nextText());
				} else if(startTag.equalsIgnoreCase("movieid")) {
					cinemaRoomInfo.movieid = Long.parseLong(parser.nextText());
				} else if(startTag.equalsIgnoreCase("linenum")) {
					
				} else if(startTag.equalsIgnoreCase("ranknum")) {
					
				} else if(startTag.equalsIgnoreCase("seatList")) {
					seatList = new ArrayList<SeatRow>();
				} else if(startTag.equalsIgnoreCase("row")) {
					seatRow = new SeatRow();
				} else if(startTag.equalsIgnoreCase("rownum")) {
					seatRow.rownum = Integer.parseInt(parser.nextText());
				} else if(startTag.equalsIgnoreCase("rowid")) {
					seatRow.rowid = parser.nextText();
				} else if(startTag.equalsIgnoreCase("columns")) {
					seatRow.columns = parser.nextText();
				}else if(startTag.equalsIgnoreCase("error")){
				    cinemaRoomInfo = new CinemaRoomInfo();
				    cinemaRoomInfo.error_msg = parser.nextText();
				}
				break;
        	 case XmlPullParser.END_TAG:
        		 String endTag = parser.getName();
        		 if(endTag.equalsIgnoreCase("opiSeatInfo")) {
        			 
 				 } else if(endTag.equalsIgnoreCase("seatList")) {
 					cinemaRoomInfo.seatList = seatList;
 				 } else if(endTag.equalsIgnoreCase("row")) {
 					seatList.add(seatRow);
 				 }       		 
        		 break;
    		 default:
    			 break;
        	}
        	evtType = parser.next();
        }
		
		return cinemaRoomInfo;
	}

}
