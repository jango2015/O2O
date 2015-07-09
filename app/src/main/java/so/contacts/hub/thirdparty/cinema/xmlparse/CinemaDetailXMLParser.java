package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import so.contacts.hub.thirdparty.cinema.bean.CinemaDetail;
import android.util.Xml;

public class CinemaDetailXMLParser implements CinemaIXMLBaseParser {

    @Override
    public List<CinemaDetail> parse(InputStream is) throws Exception {
        CinemaDetail cinemaDetail = null;
        
        List<CinemaDetail> cinemaList = null;
        
        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式
        
        int evtType = parser.getEventType();
        while(evtType != XmlPullParser.END_DOCUMENT){ 
            switch(evtType){ 
            case XmlPullParser.START_TAG:
                String startTag = parser.getName();
                if(startTag.equalsIgnoreCase("cinemaList")) {
                    cinemaList = new ArrayList<CinemaDetail>();
                } else if(startTag.equalsIgnoreCase("cinema")){
                    cinemaDetail = new CinemaDetail();
                } else if(startTag.equalsIgnoreCase("cinemaid")) {
                    cinemaDetail.setCinemaid(Long.parseLong(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("cinemaname")) {
                    cinemaDetail.setCinemaname(parser.nextText());
                } else if(startTag.equalsIgnoreCase("englishname")) {
                    cinemaDetail.setEnglishname(parser.nextText());
                } else if(startTag.equalsIgnoreCase("citycode")) {
                    cinemaDetail.setCitycode(parser.nextText());
                } else if(startTag.equalsIgnoreCase("cityname")) {
                    cinemaDetail.setCityname(parser.nextText());
                } else if(startTag.equalsIgnoreCase("countycode")) {
                    cinemaDetail.setCountycode(parser.nextText());
                } else if(startTag.equalsIgnoreCase("countyname")) {
                    cinemaDetail.setCountyname(parser.nextText());
                } else if(startTag.equalsIgnoreCase("indexarea")) {
                    cinemaDetail.setIndexarea(parser.nextText());
                } else if(startTag.equalsIgnoreCase("contactphone")) {
                    cinemaDetail.setContactphone(parser.nextText());
                } else if(startTag.equalsIgnoreCase("address")) {
                    cinemaDetail.setAddress(parser.nextText());
                } else if(startTag.equalsIgnoreCase("opentime")) {
                    cinemaDetail.setOpentime(parser.nextText());
                } else if(startTag.equalsIgnoreCase("logo")) {
                    cinemaDetail.setLogo(parser.nextText());
                } else if(startTag.equalsIgnoreCase("content")) {
                    cinemaDetail.setContent(parser.nextText());
                } else if(startTag.equalsIgnoreCase("pointx")) {
                    cinemaDetail.setPointx(parser.nextText());
                } else if(startTag.equalsIgnoreCase("pointy")) {
                    cinemaDetail.setPointy(parser.nextText());
                } else if(startTag.equalsIgnoreCase("collectedtimes")) {
                    cinemaDetail.setCollectedtimes(Long.parseLong(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("clickedtimes")) {
                    cinemaDetail.setClickedtimes(Long.parseLong(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("generalmark")) {
                    cinemaDetail.setGeneralmark(parser.nextText());
                } else if(startTag.equalsIgnoreCase("bpointx")) {
                    cinemaDetail.setBpointx(parser.nextText());
                } else if(startTag.equalsIgnoreCase("bpointy")) {
                    cinemaDetail.setBpointy(parser.nextText());
                } else if(startTag.equalsIgnoreCase("feature")) {
                    cinemaDetail.setFeature(parser.nextText());
                } else if(startTag.equalsIgnoreCase("transport")) {
                    cinemaDetail.setTransport(parser.nextText());
                } else if(startTag.equalsIgnoreCase("linename")) {
                    cinemaDetail.setLinename(parser.nextText());
                } else if(startTag.equalsIgnoreCase("stationname")) {
                    cinemaDetail.setLinename(parser.nextText());
                } else if(startTag.equalsIgnoreCase("exitnumber")) {
                    cinemaDetail.setExitnumber(parser.nextText());
                } else if(startTag.equalsIgnoreCase("popcorn")) {
                    cinemaDetail.setPopcorn(parser.nextText());
                } else if(startTag.equalsIgnoreCase("diaryid")) {
                    cinemaDetail.setDiaryid(Long.parseLong(parser.nextText()));
                }
                break;
             case XmlPullParser.END_TAG:
                 String endTag = parser.getName();
                 if(endTag.equalsIgnoreCase("cinema")) {
                     cinemaList.add(cinemaDetail);
                 }
                 break;
             default:
                 break;
            }
            evtType = parser.next();
        }
        return cinemaList;
    }

}
