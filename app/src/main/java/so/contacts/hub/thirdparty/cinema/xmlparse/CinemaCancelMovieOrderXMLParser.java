package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class CinemaCancelMovieOrderXMLParser implements CinemaIXMLBaseParser {

	@Override
	public Object parse(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
		parser.setInput(is, "UTF-8"); // 设置输入流 并指明编码方式

		int evtType = parser.getEventType();
		while (evtType != XmlPullParser.END_DOCUMENT) {
			switch (evtType) {
			case XmlPullParser.START_TAG:
				String startTag = parser.getName();
				if (startTag.equalsIgnoreCase("result")){
					return parser.nextText();
				}else if(startTag.equalsIgnoreCase("error")){
					return parser.nextText();
				}
			case XmlPullParser.END_TAG:
				String endTag = parser.getName();
				if (endTag.equalsIgnoreCase("result"))
					break;
			default:
				break;
			}
			Log.i("haha", "1");
			evtType = parser.next();
		}
		return null;
	}

}
