package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import so.contacts.hub.thirdparty.cinema.bean.CinemaDetail;
import so.contacts.hub.thirdparty.cinema.bean.CinemaMovieDetail;
import so.contacts.hub.thirdparty.cinema.bean.MovieCity;
import so.contacts.hub.util.CalendarUtil;
import android.util.Xml;

public class CinemaMovieCityListXMLParser implements CinemaIXMLBaseParser {

    @Override
    public List<MovieCity> parse(InputStream is) throws Exception {
        MovieCity movieCity = null;
        
        List<MovieCity> movieCities = null;
        
        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式
        
        int evtType = parser.getEventType();
        while(evtType != XmlPullParser.END_DOCUMENT){ 
            switch(evtType){ 
            case XmlPullParser.START_TAG:
                String startTag = parser.getName();
                if(startTag.equalsIgnoreCase("cityList")) {
                    movieCities = new ArrayList<MovieCity>();
                } else if(startTag.equalsIgnoreCase("citycode")) {
                    if(movieCity != null){
                        movieCities.add(movieCity);
                    }
                    movieCity = new MovieCity();
                    movieCity.setCitycode(parser.nextText());
                } else if(startTag.equalsIgnoreCase("cityname")) {
                    movieCity.setCityname(parser.nextText());
                }  
                break;
             case XmlPullParser.END_TAG:
                 String endTag = parser.getName();
//                 if(endTag.equalsIgnoreCase("movie")) {
//                     if(movieCities!=null){
//                         movieCities.add(movieCity);
//                     }
//                 }             
                 break;
             default:
                 break;
            }
            evtType = parser.next();
        }
        if(movieCities == null){
            movieCities = new ArrayList<MovieCity>();
            if(movieCity != null){
                movieCities.add(movieCity);
            }
        }
        return movieCities;
    }

}
