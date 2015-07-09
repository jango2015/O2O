package so.contacts.hub.train;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import so.contacts.hub.db.DatabaseHelper;
import so.contacts.hub.train.bean.TongChengCity;
import so.contacts.hub.ui.yellowpage.bean.City;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * 火车票的数据库
 */
public class YellowPageTrainDB {
	
	private static final String TAG = YellowPageTrainDB.class.getSimpleName();
	
	SQLiteDatabase database;
	
	public YellowPageTrainDB(DatabaseHelper helper) {
		database = helper.getWritableDatabase();
	}

	public SQLiteDatabase getSQLiteDatabase() {
		return this.database;
	}
	
	//add by lisheng 2014-11-24 19:38:18 start
	public void insertTongChengCityList(List<TongChengCity> cityList) {
		// TODO Auto-generated method stub
		if (cityList != null && cityList.size() > 0) {
			database.beginTransaction();
			for (TongChengCity city : cityList) {
				insert(city);
			}
			database.setTransactionSuccessful();
			database.endTransaction();
		}

	}

	public void insert(TongChengCity city) {
		ContentValues values = new ContentValues();
		values.put(YellowTrainTicketTable.CITY_NAME, city.getStationName());
		values.put(YellowTrainTicketTable.CITY_PYS, city.getStationPY());
		values.put(YellowTrainTicketTable.CITY_STATION_CODE,
				city.getStationCode());
		values.put(YellowTrainTicketTable.CITY_QUAN_PY, city.getQuanPin());
		values.put(YellowTrainTicketTable.CITY_JIAN_PY, city.getJianPin());
		database.insert(YellowTrainTicketTable.TABLE_NAME, null, values);
	}

	/** 查询同城的车站列表,返回站名和混合拼音,组装成原有的City表 */
	public List<City> getTongChengCityList() {
		Cursor cursor = database.query(YellowTrainTicketTable.TABLE_NAME, null,
				null, null, null, null, YellowTrainTicketTable.CITY_PYS);
		List<City> cityList = new ArrayList<City>();
		if (cursor != null) {
			cursor.moveToFirst();
			try {
				while (!cursor.isAfterLast()) {
					City city = new City();
					city.setCityName(cursor.getString(cursor
							.getColumnIndex(YellowTrainTicketTable.CITY_NAME)));
					city.setCityPY(cursor.getString(cursor
							.getColumnIndex(YellowTrainTicketTable.CITY_PYS)));
					cityList.add(city);
					cursor.moveToNext();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) { //modify by lisheng 2014-12-15 添加空判断
					cursor.close();
				}
				
			}
		}
		return cityList;
	}

	public String getStationQuanPin(String mStartCity) {
		if (TextUtils.isEmpty(mStartCity)) {
			return null;
		}
		Cursor cursor = database.query(YellowTrainTicketTable.TABLE_NAME,
				new String[] { YellowTrainTicketTable.CITY_QUAN_PY },
				YellowTrainTicketTable.CITY_NAME + "=?",
				new String[] { mStartCity }, null, null, null, null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					return cursor
							.getString(cursor
									.getColumnIndex(YellowTrainTicketTable.CITY_QUAN_PY));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) { //modify by lisheng 2014-12-15 添加空判断
					cursor.close();
				}
			}
		}
		return null;
	}

	// add by lisheng 2014-11-24 19:47:56 start;
	public static class YellowTrainTicketTable implements BaseColumns {
		public static final String TABLE_NAME = "yellow_train_ticket";
		public static final String CITY_ID = "city_id";//
		public static final String CITY_NAME = "city_name";// 火车站名
		public static final String CITY_PYS = "city_pys";// 火车站名称拼音混合拼音
		public static final String CITY_QUAN_PY = "city_quan_py";// 火车站名称拼音全拼
		public static final String CITY_JIAN_PY = "city_jian_py";// 火车站名称拼音简拼
		public static final String CITY_STATION_CODE = "city_station_code";// 火车站code
	}

	// add by lisheng end

	// add by lisheng 2014-11-24 20:23:06
	/** 添加同城火车票的数据库 */
	public static String getCreateTrainTicketTableSQL() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS ")
				.append(YellowTrainTicketTable.TABLE_NAME).append(" (");
		sb.append(YellowTrainTicketTable._ID).append(
				" INTEGER  PRIMARY KEY autoincrement,");
		sb.append(YellowTrainTicketTable.CITY_NAME).append(" TEXT,");
		sb.append(YellowTrainTicketTable.CITY_PYS).append(" TEXT,");
		sb.append(YellowTrainTicketTable.CITY_QUAN_PY).append(" TEXT,");
		sb.append(YellowTrainTicketTable.CITY_JIAN_PY).append(" TEXT,");
		sb.append(YellowTrainTicketTable.CITY_STATION_CODE).append(" TEXT");
		sb.append(");");
		return sb.toString();
	}
	// add by lisheng end
	
	/**
	 * @param name 火车站名
	 * @return 火车站拼音, 数据库里拼音有重名的 以数字结尾的,去掉数字;
	 */
	public  String getPinYin(String name){
		if (TextUtils.isEmpty(name)) {
			return null;
		}
		String pinyin ="";
		Cursor cursor = database.query(YellowTrainTicketTable.TABLE_NAME,
				new String[] { YellowTrainTicketTable.CITY_QUAN_PY },
				YellowTrainTicketTable.CITY_NAME + "=?",
				new String[] {name}, null, null, null, null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					pinyin=cursor.getString(cursor.getColumnIndex(YellowTrainTicketTable.CITY_QUAN_PY));
					if(!TextUtils.isEmpty(pinyin)){
						Pattern p =Pattern.compile("^[a-zA-Z]*[0-9]$");
						Matcher m = p.matcher(pinyin);
						if(m.matches()){
							pinyin =pinyin.substring(0, pinyin.length()-1);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) { 
					cursor.close();
				}
			}
		}
		return pinyin;
	}
			
}
