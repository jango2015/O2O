package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import so.contacts.hub.thirdparty.cinema.bean.OpenPlayItem;
import so.contacts.hub.util.CalendarUtil;
import android.util.Xml;

public class OpenPlayItemXMLParser implements CinemaIXMLBaseParser {

    @Override
    public Object parse(InputStream is) throws Exception {
        List<OpenPlayItem> opiList = null;
        OpenPlayItem opi = null;
        
        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式
        
        int evtType = parser.getEventType();
        while(evtType != XmlPullParser.END_DOCUMENT){ 
            switch(evtType){ 
            case XmlPullParser.START_TAG:
                String startTag = parser.getName();
                if(startTag.equalsIgnoreCase("opiList")) {
                    opiList = new ArrayList<OpenPlayItem>();
                } else if(startTag.equalsIgnoreCase("opi")){
                    opi = new OpenPlayItem();
                } else if(startTag.equalsIgnoreCase("mpid")) {
                    opi.setMpid(Long.parseLong(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("movieid")) {
                    opi.setMovieid(Long.parseLong(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("cinemaid")) {
                    opi.setCinemaid(Long.parseLong(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("moviename")) {
                    opi.setMoviename(parser.nextText());
                } else if(startTag.equalsIgnoreCase("cinemaname")) {
                    opi.setCinemaname(parser.nextText());
                } else if(startTag.equalsIgnoreCase("playtime")) {
                    opi.setPlaytime(CalendarUtil.getDateFromString(parser.nextText(),CalendarUtil.DATE_FORMATTER_SIX));
                } else if(startTag.equalsIgnoreCase("gewaprice")) {
                    opi.setGewaprice(Integer.parseInt(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("price")) {
                    opi.setPrice(Integer.parseInt(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("servicefee")) {
                    opi.setServicefee(Integer.parseInt(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("language")) {
                    opi.setLanguage(parser.nextText());
                } else if(startTag.equalsIgnoreCase("edition")) {
                    opi.setEdition(parser.nextText());
                } else if(startTag.equalsIgnoreCase("roomid")) {
                    opi.setRoomid(Long.parseLong(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("roomname")) {
                    opi.setRoomname(parser.nextText());
                } else if(startTag.equalsIgnoreCase("roomtype")) {
                    opi.setRoomtype(parser.nextText());
                } else if(startTag.equalsIgnoreCase("lockminute")) {
                    opi.setLockminute(Integer.parseInt(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("maxseat")) {
                    opi.setMaxseat(Integer.parseInt(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("closetime")) {
                    opi.setClosetime(CalendarUtil.getDateFromString(parser.nextText(), CalendarUtil.DATE_FORMATTER_SIX));
                } else if(startTag.equalsIgnoreCase("remark")) {
                    opi.setRemark(parser.nextText());
                } else if(startTag.equalsIgnoreCase("seatAmountStatus")) {
                } 
                break;
             case XmlPullParser.END_TAG:
                 String endTag = parser.getName();
                 if(endTag.equalsIgnoreCase("opi")) {
                     opiList.add(opi);
                 }
                 break;
             default:
                 break;
            }
            evtType = parser.next();
        }
        return opiList;
    }

}
