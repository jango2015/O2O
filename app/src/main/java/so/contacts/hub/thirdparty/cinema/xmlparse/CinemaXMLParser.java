package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;

import so.contacts.hub.thirdparty.cinema.utils.GewaApiReqMethod;

public class CinemaXMLParser {

	public static Object parseXML(InputStream inputStream, GewaApiReqMethod method) throws Exception {
		// TODO Auto-generated method stub
		Object object = null;
		CinemaIXMLBaseParser xmlBaseParser = null;
		
		switch (method) {
            case MOVIE_DETAIL:
                // 解析 影片详情
                xmlBaseParser = new CinemaMovieDetailXMLParser();
                break;
            case OPEN_CINEMA_LIST_BY_PLAYDATE:
                xmlBaseParser = new CinemaDetailXMLParser();
                break;
            case PLAYDATE_LIST:
                xmlBaseParser = new PlayDateXMLParser();
                break;
            case OPI_LIST:
                xmlBaseParser = new OpenPlayItemXMLParser();
                break;
            case TICKET_HELP:  
            	xmlBaseParser=new CinemaTicketHelpXMLParser();//CinemaUtils.inputsteam2Str(inputStream);
            	break;
            case FUTURE_MOVIE_LIST:
            case OPEN_MOVIE_LIST:    
                xmlBaseParser = new CinemaMovieDetaiListXMLParser();
                break;
            case OPEN_PARTNER_CITYLIST:
                xmlBaseParser = new CinemaMovieCityListXMLParser();
                break;
            case OPI_SEAT_INFO:
                xmlBaseParser = new CinemaRoomInfoXMLParser();
                break;
            case CANCEL_OEDER:
            	xmlBaseParser = new CinemaCancelMovieOrderXMLParser();
            	break;
            case TICKETORDER_ADD:
            case TICKETORDER_DETAIL:
                xmlBaseParser = new MovieOrderDetailXMLParser();
                break;
            default:
                break;
        }
		
		if( xmlBaseParser != null ){
			object = xmlBaseParser.parse(inputStream);
		}
		return object;
	}
}
