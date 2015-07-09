package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import so.contacts.hub.thirdparty.cinema.bean.Playdate;

import android.util.Xml;

public class PlayDateXMLParser implements CinemaIXMLBaseParser {

    @Override
    public Object parse(InputStream is) throws Exception {
        List<Playdate> playDateList = null;
        Playdate playDate = null;
        
        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式
        
        int evtType = parser.getEventType();
        while (evtType != XmlPullParser.END_DOCUMENT) {
            switch (evtType) {
                case XmlPullParser.START_TAG:
                    String startTag = parser.getName();
                    if(startTag.equalsIgnoreCase("playdateList")){
                        playDateList = new ArrayList<Playdate>();
                    } else if(startTag.equalsIgnoreCase("playdate")){
                        playDate = new Playdate();
                        playDate.setPlaydate(parser.nextText());
                        playDateList.add(playDate);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    String endTag = parser.getName();
                    if(endTag.equalsIgnoreCase("playdate")) {
//                        playDateList.add(playDate);
                    }
                    break;
                default:
                    break;
            }
            evtType = parser.next();
        }
        return playDateList;
    }

}
