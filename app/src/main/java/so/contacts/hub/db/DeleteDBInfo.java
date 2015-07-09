package so.contacts.hub.db;

import android.provider.BaseColumns;

/**
 * 删除的表的信息
 * @author Administrator
 *
 */
public class DeleteDBInfo {

	/**
	 * delete by zjh 2014-12-13
	 */
	public static class CityTable implements BaseColumns{
		public static final String TABLE_NAME = "yellow_page_city";
		public static final String CITY_ID = "city_id";
		public static final String CITY_NAME = "city_name";
		public static final String CITY_PYS = "city_pys";//城市名称拼音全拼
	}
	
	/**
	 * delete by zjh 2014-12-13
	 */
	public static class ChinaDistrictTable{
		public static final String TABLE_NAME = "china_district";
		public static final String _ID = "_id";
		public static final String DISTRICT_NAME = "district_name";//省,市,县,区的名字
		public static final String DISTRICT_PARENT_ID = "parent_id";//省,市,县,区对应的父id
		public static final String DISTRICT_SELF_ID = "self_id";//省,市,县,区自己的id
		public static final String DISTRICT_CODE="district_code";	//对应的行政区码
	}
	
	/**
	 * delete by zjh 2014-12-13
	 */
	public static class CityDbTable {
		public static final String TABLE_NAME = "city_db";
		public static final String _ID = "_id";
		public static final String CITYNAME = "city_name";
		public static final String DISTRICT_IDENTIFY_CODE = "district_identify_code";
		public static final String CITY58_CODE = "_58code";
		public static final String ELONG_CODE = "elong_code";
		public static final String RAILWAY_STATION = "railway_station";
	}
	
	/**
	 * delete by zjh 2014-12-13
	 */
	public static class TongChengCityTable implements BaseColumns{
		public static final String TABLE_NAME = "yellow_page_tongcheng_city";
		public static final String CITY_ID = "city_id";//
		public static final String CITY_NAME = "city_name";//火车站名
		public static final String CITY_PYS = "city_pys";//火车站名称拼音混合拼音
		public static final String CITY_QUAN_PY = "city_quan_py";//火车站名称拼音全拼
		public static final String CITY_JIAN_PY = "city_jian_py";//火车站名称拼音简拼
		public static final String CITY_STATION_CODE = "city_station_code";//火车站code
	}
}
