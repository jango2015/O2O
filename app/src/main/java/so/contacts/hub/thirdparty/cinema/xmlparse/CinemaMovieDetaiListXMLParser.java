package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import so.contacts.hub.thirdparty.cinema.bean.CinemaDetail;
import so.contacts.hub.thirdparty.cinema.bean.CinemaMovieDetail;
import so.contacts.hub.util.CalendarUtil;
import android.util.Xml;

public class CinemaMovieDetaiListXMLParser implements CinemaIXMLBaseParser {

    @Override
    public List<CinemaMovieDetail> parse(InputStream is) throws Exception {
        CinemaMovieDetail cinemaMovieDetail = null;
        
        List<CinemaMovieDetail> cinemaMovieDetailList = null;
        
        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式
        
        int evtType = parser.getEventType();
        while(evtType != XmlPullParser.END_DOCUMENT){ 
            switch(evtType){ 
            case XmlPullParser.START_TAG:
                String startTag = parser.getName();
                if(startTag.equalsIgnoreCase("movieList")) {
                    cinemaMovieDetailList = new ArrayList<CinemaMovieDetail>();
                } else if(startTag.equalsIgnoreCase("movie")) {
                    cinemaMovieDetail = new CinemaMovieDetail();
                } else if(startTag.equalsIgnoreCase("movieid")) {
                    cinemaMovieDetail.setMovieid(Long.parseLong(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("moviename")) {
                    cinemaMovieDetail.setMoviename(parser.nextText());
                } else if(startTag.equalsIgnoreCase("englishname")) {
                    cinemaMovieDetail.setEnglishname(parser.nextText());
                } else if(startTag.equalsIgnoreCase("language")) {
                    cinemaMovieDetail.setLanguage(parser.nextText());
                } else if(startTag.equalsIgnoreCase("type")) {
                    cinemaMovieDetail.setType(parser.nextText());
                } else if(startTag.equalsIgnoreCase("state")) {
                    cinemaMovieDetail.setState(parser.nextText());
                } else if(startTag.equalsIgnoreCase("director")) {
                    cinemaMovieDetail.setDirector(parser.nextText());
                } else if(startTag.equalsIgnoreCase("actors")) {
                    cinemaMovieDetail.setActors(parser.nextText());
                } else if(startTag.equalsIgnoreCase("length")) {
                    cinemaMovieDetail.setLength(parser.nextText());
                } else if(startTag.equalsIgnoreCase("highlight")) {
                    cinemaMovieDetail.setHighlight(parser.nextText());
                } else if(startTag.equalsIgnoreCase("releasedate")) {
                    cinemaMovieDetail.setReleasedate(CalendarUtil.getDateFromString(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("logo")) {
                    cinemaMovieDetail.setLogo(parser.nextText());
                } else if(startTag.equalsIgnoreCase("content")) {
                    cinemaMovieDetail.setContent(parser.nextText());
                } else if(startTag.equalsIgnoreCase("imdbid")) {
                    cinemaMovieDetail.setImdbid(parser.nextText());
                } else if(startTag.equalsIgnoreCase("minprice")) {
                    cinemaMovieDetail.setMinprice(parser.nextText());
                } else if(startTag.equalsIgnoreCase("collectedtimes")) {
                    cinemaMovieDetail.setCollectedtimes(Long.parseLong(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("clickedtimes")) {
                    cinemaMovieDetail.setClickedtimes(Long.parseLong(parser.nextText()));
                } else if(startTag.equalsIgnoreCase("generalmark")) {
                    cinemaMovieDetail.setGeneralmark(parser.nextText());
                } else if(startTag.equalsIgnoreCase("gcedition")) {
                    cinemaMovieDetail.setGcedition(parser.nextText());
                } 
                break;
             case XmlPullParser.END_TAG:
                 String endTag = parser.getName();
                 if(endTag.equalsIgnoreCase("movie")) {
                     if(cinemaMovieDetailList!=null){
                         cinemaMovieDetailList.add(cinemaMovieDetail);
                     }
                 }             
                 break;
             default:
                 break;
            }
            evtType = parser.next();
        }
        if(cinemaMovieDetailList == null){
            cinemaMovieDetailList = new ArrayList<CinemaMovieDetail>();
            if(cinemaMovieDetail != null){
                cinemaMovieDetailList.add(cinemaMovieDetail);
            }
        }
        return cinemaMovieDetailList;
    }

}
