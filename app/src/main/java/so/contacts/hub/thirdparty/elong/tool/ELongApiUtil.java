package so.contacts.hub.thirdparty.elong.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.ArrayList;
import so.contacts.hub.thirdparty.elong.bean.OrderHistoryResult;
import so.contacts.hub.core.Config;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.thirdparty.elong.bean.OrderHistoryResponseResult;
import android.text.TextUtils;
import so.contacts.hub.util.LogUtil;
import java.util.List;
import so.contacts.hub.thirdparty.elong.bean.OrderHistory;
import java.util.Date;
import com.google.gson.reflect.TypeToken;
import com.mdroid.core.http.IgnitedHttpResponse;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.thirdparty.elong.bean.ELongCityBean;
import so.contacts.hub.thirdparty.elong.bean.EnumSortType;
import so.contacts.hub.thirdparty.elong.bean.Position;
import so.contacts.hub.thirdparty.elong.request.HotelListRequest;
import so.contacts.hub.thirdparty.elong.bean.HotelListBean;
import so.contacts.hub.thirdparty.elong.bean.OrderListBean;
import so.contacts.hub.thirdparty.elong.request.OrderListRequest;
import so.contacts.hub.thirdparty.elong.bean.EnumLocal;

/**
 * 艺龙 API接口工具类
 */
public class ELongApiUtil {
	
	private static final String TAG = "ELongApiUtil";

	public static final String appUser = "4d4e1ddd9450c22cfa487176b4d3b341";
	public static final String appKey = "1031cd91555bec18f1cc38ac41ba1921";
	public static final String appSecret = "d11f4a237fca258fd271a98d37a966d0";
	public static final double version = 1.1;
	public static final EnumLocal locale = EnumLocal.zh_CN;

	public static final boolean NEED_SSL = true;

	public static final String DATA_TYPE = "json"; // "xml"

	/**
	 * 测试： el_face_test.s 
	 * 公网：el_face.s
	 */
	public static final String serverHost = "http://android1.putao.so/PT_SERVER/el_face.s";

	
	/**
	 * 获取酒店订单
	 */
	public static List<OrderHistory> getHotelOrderList(String mobile){
		OrderListRequest orderListRequest = new OrderListRequest();
		OrderListBean orderListBean = new OrderListBean();
		orderListBean.setPageIndex(1);
		orderListBean.setMobile(mobile);
		orderListRequest.setRequestData(orderListBean);
		String orderStr = orderListRequest.requestForResult();
		LogUtil.d(TAG, "getHotelOrderList: " + orderStr);
		if( TextUtils.isEmpty(orderStr) ){
			return null;
		}
		OrderHistoryResponseResult responseResult = null;
		try {
			responseResult = Config.mGson.fromJson(orderStr,
					OrderHistoryResponseResult.class);
		} catch (Exception e) {
			responseResult = null;
		}
		if( responseResult == null ){
			return null;
		}
		if( !"0".equals(responseResult.getCode()) ){
			// Code 不等于0，获取数据异常
			return null;
		}
		OrderHistoryResult historyResult = responseResult.getResult();
		if( historyResult == null ){
			return null;
		}
		List<OrderHistory> orderList = historyResult.getOrders();
		if( orderList == null || orderList.size() == 0 ){
			return null;
		}
		return orderList;
	}
	
	/**
	 * 获取酒店列表
	 */
	public static String getHotelList(String cityId, double latitude, double longitude, int radius, int pageSize, int page){
		HotelListRequest hotelListRequest = new HotelListRequest();
		Position position = new Position();
		position.setLatitude(latitude);
		position.setLongitude(longitude);
		position.setRadius(radius);
		Date arrivalDate = new Date();
		Date departureDate = ELongApiUtilHelper.addDate(arrivalDate, 1);
		HotelListBean hotelListBean = new HotelListBean();
		hotelListBean.setCityId(cityId);
		hotelListBean.setArrivalDate(arrivalDate);
		hotelListBean.setDepartureDate(departureDate);
		hotelListBean.setPosition(position);
		hotelListBean.setSort(EnumSortType.DistanceAsc);
		hotelListBean.setPageSize(pageSize);
		hotelListBean.setPageIndex(page);
		hotelListBean.setResultType("3,5");
		hotelListRequest.setRequestData(hotelListBean);
		return hotelListRequest.requestForResult();
	}
	
	/**
	 * 便利加载所有城市编码CityId和城市名称CityName
	 * @return
	 * @throws ConnectException
	 */
	public static List<ELongCityBean> initLoadCitys()  {
	    List<ELongCityBean> list = new ArrayList<ELongCityBean>();
	    
        for(char c='A'; c<='Z'; c++) {
            String url = "http://m.elong.com/Hotel/SearchHotelCityByEnName?enname="+c;
            
            try {
                IgnitedHttpResponse resp = Config.getApiHttp().post(url).send();
                String body = resp.getResponseBodyAsString();
//                LogUtil.d(TAG, "body="+body);
                
                
                List<ELongCityBean> sublist = Config.mGson.fromJson(body, new TypeToken<List<ELongCityBean>>(){}.getType());
                list.addAll(sublist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }	
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<list.size(); i++) {
            ELongCityBean bean = list.get(i);
            sb.append(bean.CityName+","+bean.CityID+"|");
        }
        LogUtil.d(TAG, sb.toString());
        
        try{
            File file = new File("/mnt/sdcard/putao_elong_cities.txt");  
            if( !file.exists() ){
                file.createNewFile();
            }
            FileWriter filerWriter = new FileWriter(file, false);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖  
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);  
            bufWriter.write(sb.toString());  
            bufWriter.newLine();  
            bufWriter.close();  
            filerWriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
	    
	    return list;
	}
	
	/**
	 * 从艺龙城市列表中查找出cityid
	 * @param cityname
	 * @return
	 */
	public static String getCityCode(String cityname) {
	    InputStream in = null;
	    BufferedReader br = null;
	    String line = null;
	    try {
            in = ContactsApp.getInstance().getApplicationContext().getAssets().open("putao_elong_cities.txt");
            br = new BufferedReader(new InputStreamReader(in));
            line = br.readLine();
            
            if(!TextUtils.isEmpty(line)) {
                int locPos = line.indexOf(cityname);
                int startPos = line.indexOf(',', locPos);
                int endPos = line.indexOf('|', startPos);
                String code = line.substring(startPos+1, endPos);
                LogUtil.d(TAG, "GetCityCode:"+code+", cityname="+cityname);
                return code;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
            return "";
        } 
	    
	    return "";
	}
	//add by lisheng start 2014-11-07
//		public static String getCityCodeFromDb(String cityname){
//			YellowPageDB db = ContactsApp.getDatabaseHelper()
//					.getYellowPageDBHelper();
//			return db.getElongCodeFromDb(cityname);
//		}
	//add by lisheng end
}
