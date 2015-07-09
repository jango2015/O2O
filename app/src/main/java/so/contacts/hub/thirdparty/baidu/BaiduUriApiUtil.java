package so.contacts.hub.thirdparty.baidu;

import so.contacts.hub.util.LogUtil;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import so.contacts.hub.core.Config;
import android.util.Xml;
import com.mdroid.core.http.IgnitedHttpResponse;

/**
 * 百度地图URI APIv2.0 工具类封装 参见 http://developer.baidu.com/map/index.php?title=uri
 * 
 * @author change
 */
public class BaiduUriApiUtil {

	private static final String TAG = "BaiduUriApiUtil";

	// 反向地址解析(地址查询)
	public static final String HOST_GEOCODER_URL = "http://api.map.baidu.com/geocoder";

	// 公交、地铁线路查询
	public static final String HOST_LINE_URL = "http://api.map.baidu.com/line";

	// 公交、驾车、步行线路导航
	public static final String HOST_DIRECTION_URL = "http://api.map.baidu.com/direction";

	// POI 地点查询
	public static final String HOST_PLACE_SEARCH_URL = "http://api.map.baidu.com/place/search";

	// public static final String HOST_SEARCH_URL =
	// "http://map.baidu.com/mobile/webapp/search/search/qt=s&wd=327&c=340";

	/**
	 * 通过百度地图ur来获取地址xml信息，并解析出街道信息
	 * 
	 * @param lat
	 *            经度
	 * @param lng
	 *            维度
	 * @param coord_type
	 *            允许的值为bd09ll、gcj02、wgs84。bd09ll表示百度经纬度坐标，
	 *            gcj02表示经过国测局加密的坐标，wgs84表示gps获取的坐标。
	 * @return 地址信息
	 *         http://api.map.baidu.com/geocoder?location=22.537634,113.924695
	 *         &coord_type=gcj02
	 */
	public static String getStreetName(double lat, double lng, String coord_type) {
		String street = "";
		StringBuffer requrl = new StringBuffer(HOST_GEOCODER_URL);
		requrl.append("?");
		requrl.append("location=");
		requrl.append(lat).append(",").append(lng);
		requrl.append("&coord_type=").append(coord_type);
		// requrl.append("&src=putao|yellowpage");

		LogUtil.d(TAG, "getStreetName requrl=" + requrl.toString());
		IgnitedHttpResponse resp = null;
		String body = "";
		try {
			resp = Config.getApiHttp().post(requrl.toString()).send();
			// body = resp.getResponseBodyAsString();
			LogUtil.d(TAG,
					"getStreetName body=" + resp.getResponseBodyAsString());

		} catch (ConnectException e) {
			e.printStackTrace();
			LogUtil.e(TAG, e.getMessage());
			return street;
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.e(TAG, e.getMessage());
			return street;
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(resp.getResponseBody());
			// 获取根节点
			Element root = document.getDocumentElement();
			BaiduGeocoder geo = new BaiduGeocoder();

			parserBaiduGeocoder(root, geo);
		} catch (ParserConfigurationException e) {
			LogUtil.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			LogUtil.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LogUtil.e(TAG, e.getMessage());
			e.printStackTrace();
		}

		return street;
	}

	private static void parserBaiduGeocoder(Element element, BaiduGeocoder geo) {
		// http://blog.csdn.net/chenzheng_java/article/details/6223426
		NodeList nodeList = element.getChildNodes();
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++) {
			Node elm = nodeList.item(i);
			String tagName = elm.getNodeName();

			if ("result".equals(tagName)
					&& elm.getNodeType() == Document.ELEMENT_NODE) {
				if (geo == null)
					geo = new BaiduGeocoder();
			} else if ("lat".equals(tagName)
					&& elm.getNodeType() == Document.ELEMENT_NODE) {
				if (geo != null)
					geo.lat = elm.getTextContent();
			} else if ("lng".equals(tagName)
					&& elm.getNodeType() == Document.ELEMENT_NODE) {
				if (geo != null)
					geo.lng = elm.getTextContent();
			} else if ("formatted_address".equals(tagName)
					&& elm.getNodeType() == Document.ELEMENT_NODE) {
				if (geo != null)
					geo.format_address = elm.getTextContent();
			} else if ("street".equals(tagName)
					&& elm.getNodeType() == Document.ELEMENT_NODE) {
				if (geo != null)
					geo.street = elm.getTextContent();
			}
		}
	}

	public static List<BaiduPOIAddress> getAddressList(String city,
			String keyword) {
		String street = "";
		StringBuffer requrl = new StringBuffer(HOST_PLACE_SEARCH_URL);
		requrl.append("?");
		requrl.append("&query=");
		requrl.append(keyword);
		requrl.append("&city=").append(city);

		LogUtil.d(TAG, "getAddressList requrl=" + requrl.toString());
		List<BaiduPOIAddress> list = new ArrayList<BaiduPOIAddress>();
		IgnitedHttpResponse resp = null;
		try {
			resp = Config.getApiHttp().post(requrl.toString()).send();
			LogUtil.d(TAG,
					"getAddressList body=" + resp.getResponseBodyAsString());

		} catch (ConnectException e) {
			e.printStackTrace();
			return list;
		} catch (IOException e) {
			e.printStackTrace();
			return list;
		}

		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(resp.getResponseBody(), "UTF-8");

			int evtType = parser.getEventType();
			while (evtType != XmlPullParser.END_DOCUMENT) {
				BaiduPOIAddress poi = null;
				switch (evtType) {
				case XmlPullParser.START_TAG:
					String tagName = parser.getName();
					if ("result".equals(tagName)) {
						poi = new BaiduPOIAddress();

					} else if ("name".equals(parser.getName())) {
						poi.name = parser.getText();
					} else if ("lat".equals(parser.getName())) {
						poi.lat = Double.parseDouble(parser.getText());
					} else if ("lng".equals(parser.getName())) {
						poi.lng = Double.parseDouble(parser.getText());
					}

				case XmlPullParser.END_TAG:
					if ("result".equals(parser.getText())) {
						street = parser.getAttributeValue(0);
						break;
					} else if ("name".equals(parser.getName())) {
						parser.getText();
					}
					break;
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

	public static class BaiduGeocoder {
		public String street;
		public String format_address;
		public String lat;
		public String lng;
		public int codecity;
	};

	// 通过POI反查地址
	public static class BaiduPOIAddress {
		public String name;
		public double lat;
		public double lng;
		public String address;
		public String uid;
	}
}
