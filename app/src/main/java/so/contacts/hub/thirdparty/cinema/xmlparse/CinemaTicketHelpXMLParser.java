package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import so.contacts.hub.thirdparty.cinema.bean.CinemaDetail;
import so.contacts.hub.thirdparty.cinema.bean.CinemaTicketHelp;
import so.contacts.hub.thirdparty.cinema.utils.CinemaUtils;

public class CinemaTicketHelpXMLParser implements CinemaIXMLBaseParser{

	private final static String TICKET_HELP_RESULT="result";
	@Override
	public Object parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		
            
	        return CinemaUtils.inputsteam2Str(is);
		
	}
	
	
	
}
