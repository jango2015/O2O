package so.contacts.hub.city;

import java.util.ArrayList;
import java.util.List;
import so.contacts.hub.db.DatabaseHelper;
import so.contacts.hub.ui.yellowpage.bean.City;
import so.contacts.hub.util.CommonValueUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 城市列表数据库
 */
public class CityListDB {
	
	public static final int CITY_TYPE_PROVINCE = 1; 	//省份
	public static final int CITY_TYPE_AREA = 2;    	//地级市
	public static final int CITY_TYPE_COUNT = 3;		//县级
	
	// 内容源城市类型
	public static final int CITY_SOURCE_TYPE_GAODE = 0;
	public static final int CITY_SOURCE_TYPE_TONGCHENG = 1;
	public static final int CITY_SOURCE_TYPE_ELONG = 2;
	public static final int CITY_SOURCE_TYPE_58 = 3;
	public static final int CITY_SOURCE_TYPE_GEWARA = 4;
	
    private SQLiteDatabase database = null;

    public CityListDB(DatabaseHelper helper) {
    	if( database == null ){
    		database = helper.getWritableDatabase();
    	}
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return database;
    }
	
	/**
	 * 城市列表
	 * add bh zjh 2014-12-13
	 */
	public static class CityDBTable {
		public static final String TABLE_NAME = "yellow_citylist";
		public static final String _ID = "_id";
		public static final String CITY_NAME = "city_name"; 			//城市名称
		public static final String CITY_PY = "city_py";					//城市拼音
		public static final String SELF_ID = "self_id";					//城市ID
		public static final String PARENT_ID = "parent_id";				//城市所属父级
		public static final String CITY_TYPE = "city_type";				//城市类别（1：省份；2：地级市；3：县级）
		public static final String DISTRICT_CODE = "district_code";		//行政区编码
		public static final String CITY_HOT = "city_hot";				//热门城市（1：是；0：不是）
		public static final String WUBA_STATE = "wuba_state";			//58同城城市是否存在（1：存在；0：不存在）
		public static final String WUBA_CODE = "wuba_code";				//58同城城市编码
		public static final String ELONG_STATE = "elong_state";			//艺龙城市是否存在（1：存在；0：不存在）
		public static final String ELONG_CODE = "elong_code";			//艺龙城市编码
		public static final String TONGCHENG_STATE = "tongcheng_state";	//同城城市是否存在（1：存在；0：不存在）
		public static final String TONGCHENG_CODE = "tongcheng_code";	//同城城市编码
		public static final String GEWARA_STATE = "gewara_state";		//格瓦拉城市是否存在（1：存在；0：不存在）
		public static final String GEWARA_CODE = "gewara_code";			//格瓦拉城市编码
		public static final String GAODE_STATE = "gaode_state";			//高德城市是否存在（1：存在；0：不存在）
		public static final String GAODE_CODE = "gaode_code";			//高德城市编码
	}
	
	public static String getCreateCityDbTableSQL() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS ").append(CityDBTable.TABLE_NAME)
				.append(" (");
		sb.append(CityDBTable._ID).append(
				" INTEGER  PRIMARY KEY autoincrement,");
		sb.append(CityDBTable.CITY_NAME).append(" TEXT,");
		sb.append(CityDBTable.CITY_PY).append(" TEXT,");
		sb.append(CityDBTable.SELF_ID).append(" INTEGER,");
		sb.append(CityDBTable.PARENT_ID).append(" INTEGER,");
		sb.append(CityDBTable.CITY_TYPE).append(" INTEGER,");
		sb.append(CityDBTable.DISTRICT_CODE).append(" TEXT,");
		sb.append(CityDBTable.CITY_HOT).append(" INTEGER,");
		sb.append(CityDBTable.WUBA_STATE).append(" INTEGER,");
		sb.append(CityDBTable.WUBA_CODE).append(" TEXT,");
		sb.append(CityDBTable.ELONG_STATE).append(" INTEGER,");
		sb.append(CityDBTable.ELONG_CODE).append(" TEXT,");
		sb.append(CityDBTable.TONGCHENG_STATE).append(" INTEGER,");
		sb.append(CityDBTable.TONGCHENG_CODE).append(" TEXT,");
		sb.append(CityDBTable.GEWARA_STATE).append(" INTEGER,");
		sb.append(CityDBTable.GEWARA_CODE).append(" TEXT, ");
		sb.append(CityDBTable.GAODE_STATE).append(" INTEGER,");
		sb.append(CityDBTable.GAODE_CODE).append(" TEXT");
		sb.append(");");
		return sb.toString();
	}
	
	public void insertCityList(List<CityBean> cityList){
		if(cityList == null || cityList.size() == 0){
			return;
		}
		database.beginTransaction();
		for(CityBean cityBean : cityList){
			insert(cityBean);
		}
		database.setTransactionSuccessful();
		database.endTransaction();
	}
	
	public void insert(CityBean cityBean){
		ContentValues values = new ContentValues();
		values.put(CityDBTable.CITY_NAME, cityBean.getCityName());
		values.put(CityDBTable.CITY_PY, cityBean.getCityPy());
		values.put(CityDBTable.SELF_ID, cityBean.getSelfId());
		values.put(CityDBTable.PARENT_ID, cityBean.getParentId());
		values.put(CityDBTable.CITY_TYPE, cityBean.getCityType());
		values.put(CityDBTable.DISTRICT_CODE, cityBean.getDistrictCode());
		values.put(CityDBTable.CITY_HOT, cityBean.getCityHot());
		values.put(CityDBTable.WUBA_STATE, cityBean.getWubaState());
		values.put(CityDBTable.WUBA_CODE, cityBean.getWubaCode());
		values.put(CityDBTable.ELONG_STATE, cityBean.getElongState());
		values.put(CityDBTable.ELONG_CODE, cityBean.getElongCode());
		values.put(CityDBTable.TONGCHENG_STATE, cityBean.getTongchengState());
		values.put(CityDBTable.TONGCHENG_CODE, cityBean.getTongchengCode());
		values.put(CityDBTable.GEWARA_STATE, cityBean.getGewaraState());
		values.put(CityDBTable.GEWARA_CODE, cityBean.getGewaraCode());
		values.put(CityDBTable.GAODE_STATE, cityBean.getGaodeState());
		values.put(CityDBTable.GAODE_CODE, cityBean.getGaodeCode());
		
		database.insert(CityDBTable.TABLE_NAME, null, values);
	}

	/**
	 * 获取所有的城市
	 */
	public List<CityBean> getAllCityData(){
		List<CityBean> cityList = null;
		Cursor cursor = null;
		try{
			cursor = database.query(CityDBTable.TABLE_NAME, null, null, null, null, null, null);
			cityList = fetchCityList(cursor);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if( cursor != null ){
				cursor.close();
			}
		}
		return cityList;
	}
	
	/**
	 * 根据条件获取所有的城市列表
	 * @return
	 */
	public List<CityBean> getCityListByTag(String key, String value){
		List<CityBean> cityList = null;
		Cursor cursor = null;
		try{
			cursor = database.query(CityDBTable.TABLE_NAME, null,
                key + "=?", new String[] { value},null, null, null);
			cityList = fetchCityList(cursor);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if( cursor != null ){
				cursor.close();
			}
		}
		return cityList;
	}
	
	public String getCityIdByName(String cityName, int sourceType){
		String cityId = "";
		String key = "";
		String selectColumn = "";
		if( sourceType == CityListDB.CITY_SOURCE_TYPE_GAODE ){
			// 高德
			key = CityDBTable.GAODE_STATE;
			selectColumn = CityDBTable.GAODE_CODE;
		}else if( sourceType == CityListDB.CITY_SOURCE_TYPE_TONGCHENG ){
			// 同城
			key = CityDBTable.TONGCHENG_STATE;
			selectColumn = CityDBTable.TONGCHENG_CODE;
		}else if( sourceType == CityListDB.CITY_SOURCE_TYPE_ELONG ){
			// 艺龙
			key = CityDBTable.ELONG_STATE;
			selectColumn = CityDBTable.ELONG_CODE;
		}else if( sourceType == CityListDB.CITY_SOURCE_TYPE_58 ){
			// 58
			key = CityDBTable.WUBA_STATE;
			selectColumn = CityDBTable.WUBA_CODE;
		}else if( sourceType == CityListDB.CITY_SOURCE_TYPE_GEWARA ){
			// 格瓦拉
			key = CityDBTable.GEWARA_STATE;
			selectColumn = CityDBTable.GEWARA_CODE;
		}else{
			return cityId;
		}
		Cursor cursor = null;
		try{
			cursor = database.query(CityDBTable.TABLE_NAME, new String[]{selectColumn},
                key + "=? and " + CityDBTable.CITY_NAME + " like ?", new String[] { "1", cityName + "%"},null, null, null);
			while ( cursor.moveToNext() ) {
				cityId = cursor.getString(0);
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if( cursor != null ){
				cursor.close();
			}
		}
		return cityId;
	}
	
	public List<City> getCityListByType(int sourceType){
		String key = "";
		String selectColumn = "";
		boolean needFilterSuffix = false;
		if( sourceType == CityListDB.CITY_SOURCE_TYPE_GAODE ){
			// 高德
			key = CityDBTable.GAODE_STATE;
			selectColumn = CityDBTable.GAODE_CODE;
			needFilterSuffix = true;
		}else if( sourceType == CityListDB.CITY_SOURCE_TYPE_TONGCHENG ){
			// 同城
			key = CityDBTable.TONGCHENG_STATE;
			selectColumn = CityDBTable.TONGCHENG_CODE;
			needFilterSuffix = true;
		}else if( sourceType == CityListDB.CITY_SOURCE_TYPE_ELONG ){
			// 艺龙
			key = CityDBTable.ELONG_STATE;
			selectColumn = CityDBTable.ELONG_CODE;
			needFilterSuffix = true;
		}else if( sourceType == CityListDB.CITY_SOURCE_TYPE_58 ){
			// 58
			key = CityDBTable.WUBA_STATE;
			selectColumn = CityDBTable.WUBA_CODE;
			needFilterSuffix = true;
		}else if( sourceType == CityListDB.CITY_SOURCE_TYPE_GEWARA ){
			// 格瓦拉
			key = CityDBTable.GEWARA_STATE;
			selectColumn = CityDBTable.GEWARA_CODE;
			needFilterSuffix = true;
		}else{
			return null;
		}
		List<City> cityList = new ArrayList<City>();
		Cursor cursor = null;
		City city = null;
		try{
			cursor = database.query(CityDBTable.TABLE_NAME, new String[]{CityDBTable.CITY_NAME, CityDBTable.CITY_PY, selectColumn},
                key + "=?", new String[] { "1"},null, null, null);
			while ( cursor.moveToNext() ) {
				city = new City();
				String cityName = cursor.getString(0);
				if( TextUtils.isEmpty(cityName) ){
					continue;
				}
                if( CommonValueUtil.getInstance().isDirectlyDivision(cityName) ){
                	continue;
                }
				if( needFilterSuffix ){
					cityName = CommonValueUtil.getInstance().filterDistrict(cityName);
				}
				city.setCityName(cityName);
				city.setCityPY(cursor.getString(1));
				city.setCityId(cursor.getString(2));
				cityList.add(city);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if( cursor != null ){
				cursor.close();
			}
		}
		return cityList;
	}
	
	/**
	 * 根据条件获取所有的城市列表
	 * @return
	 */
	public List<City> getSimpleCityListByTag(String key, String value){
		List<City> cityList = new ArrayList<City>();
		Cursor cursor = null;
		City city = null;
		try{
			cursor = database.query(CityDBTable.TABLE_NAME, null,
                key + "=?", new String[] { value},null, null, null);
			while ( cursor.moveToNext() ) {
				String cityName = cursor.getString(cursor
						.getColumnIndex(CityDBTable.CITY_NAME));
				String cityPy = cursor.getString(cursor
						.getColumnIndex(CityDBTable.CITY_PY));
				int selfId = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.SELF_ID));
				if( TextUtils.isEmpty(cityName) ){
					continue;
				}
				if( CommonValueUtil.getInstance().isDirectlyDivision(cityName) ){
					continue;
				}
				
				city = new City();
				city.setCityId(String.valueOf(selfId));
				city.setCityName(cityName);
				city.setCityPY(cityPy);
				cityList.add(city);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if( cursor != null ){
				cursor.close();
			}
		}
		return cityList;
	}
	

	/**
	 * 根据parentId获取行政区列表
	 */
	public ArrayList<String> getDistrictNameByParentId(int parent_id) {
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = database.query(CityDBTable.TABLE_NAME,
				new String[] { CityDBTable.CITY_NAME },
				CityDBTable.PARENT_ID + "=?",
				new String[] { String.valueOf(parent_id) }, null, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			try {
				while (!cursor.isAfterLast()) {
					list.add(cursor.getString(cursor
							.getColumnIndex(CityDBTable.CITY_NAME)));
					cursor.moveToNext();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cursor != null)
				cursor.close();
			}
		}
		return list;
	}

	/**
	 * @param name 行政区的名字
	 * @param parent_id 行政区的父id;
	 * @return 行政区自己的id; -1则返回错误
	 */
	public int getSelf_Id(String name, int parent_id) {
		int result = -1;
		Cursor cursor = database.query(CityDBTable.TABLE_NAME,
				new String[] { CityDBTable.SELF_ID },
				CityDBTable.CITY_NAME + "=? and "
						+ CityDBTable.PARENT_ID + "=?",
				new String[] { name, Integer.toString(parent_id) }, null, null,
				null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			try {
				result = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.SELF_ID));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cursor != null)
				cursor.close();
			}
		}
		return result;
	}
	
	public List<CityBean> fetchCityList(Cursor cursor) {
        if(cursor == null || cursor.getCount()==0){
        	return null;
        }
        List<CityBean> cityList = new ArrayList<CityBean>();
        CityBean cityBean = null;
        try{
			while ( cursor.moveToNext() ) {
				String cityName = cursor.getString(cursor
						.getColumnIndex(CityDBTable.CITY_NAME));
				if( TextUtils.isEmpty(cityName) || CommonValueUtil.getInstance().isDirectlyDivision(cityName) ){
					continue;
				}
				
				String cityPy = cursor.getString(cursor
						.getColumnIndex(CityDBTable.CITY_PY));
				int selfId = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.SELF_ID));
				int parentId = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.PARENT_ID));
				int cityType = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.CITY_TYPE));
				String districtCode = cursor.getString(cursor
						.getColumnIndex(CityDBTable.DISTRICT_CODE));
				int cityHot = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.CITY_HOT));
				int wubaState = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.WUBA_STATE));
				String wubaCode = cursor.getString(cursor
						.getColumnIndex(CityDBTable.WUBA_CODE));
				int elongState = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.ELONG_STATE));
				String elongCode = cursor.getString(cursor
						.getColumnIndex(CityDBTable.ELONG_CODE));
				int tongchengState = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.TONGCHENG_STATE));
				String tongchengCode = cursor.getString(cursor
						.getColumnIndex(CityDBTable.TONGCHENG_CODE));
				int gewaraState = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.GEWARA_STATE));
				String gewaraCode = cursor.getString(cursor
						.getColumnIndex(CityDBTable.GEWARA_CODE));
				int gaodeState = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.GAODE_STATE));
				String gaodeCode = cursor.getString(cursor
						.getColumnIndex(CityDBTable.GAODE_CODE));
				
				cityBean = new CityBean();
				cityBean.setCityName(cityName);
				cityBean.setCityPy(cityPy);
				cityBean.setSelfId(selfId);
				cityBean.setParentId(parentId);
				cityBean.setCityType(cityType);
				cityBean.setDistrictCode(districtCode);
				cityBean.setCityHot(cityHot);
				cityBean.setWubaState(wubaState);
				cityBean.setWubaCode(wubaCode);
				cityBean.setElongState(elongState);
				cityBean.setElongCode(elongCode);
				cityBean.setTongchengState(tongchengState);
				cityBean.setTongchengCode(tongchengCode);
				cityBean.setGewaraState(gewaraState);
				cityBean.setGewaraCode(gewaraCode);
				cityBean.setGaodeState(gaodeState);
				cityBean.setGaodeCode(gaodeCode);
				
				cityList.add(cityBean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if( cursor != null ){
				cursor.close();
			}
		}
        return cityList;
	}
	
	//add by lisheng 2014-12-26 start 
	/**
	 * 
	 * @param selfId
	 * @return parent_id或者-1
	 */
	public int getParentId(int selfId){
		int parentId =-1;
		Cursor cursor = database.query(CityDBTable.TABLE_NAME,
				new String[] {CityDBTable.PARENT_ID},
				CityDBTable.SELF_ID + "=?",
				new String[] {Integer.toString(selfId) }, null, null,
				null, null);
		try {
			if(cursor !=null){
				cursor.moveToFirst();
				parentId =cursor.getInt(cursor.getColumnIndex(CityDBTable.PARENT_ID));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(cursor !=null){
				cursor.close();
			}
		}
		return parentId;
	}
	
	/**
	 * 
	 * @param selfId
	 * @return ""或者city_name
	 */
	public String getSelfName(int selfId){
		String name = "";
		Cursor cursor = database.query(CityDBTable.TABLE_NAME,
				new String[] {CityDBTable.CITY_NAME},
				CityDBTable.SELF_ID + "=?",
				new String[] {Integer.toString(selfId) }, null, null,
				null, null);
		try {
			if(cursor !=null){
				cursor.moveToFirst();
				name =cursor.getString(cursor.getColumnIndex(CityDBTable.CITY_NAME));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(cursor !=null){
				cursor.close();
			}
		}
		return name;
	}
	public String getSelfId(String name){
		String id ="-1";
				Cursor cursor = database.query(CityDBTable.TABLE_NAME,
						new String[] {CityDBTable.SELF_ID},
						CityDBTable.CITY_NAME + " like ?",
						new String[] {name+"%"}, null, null,
						null, null);
				try {
					if(cursor !=null){
						cursor.moveToFirst();
						id =cursor.getString(cursor.getColumnIndex(CityDBTable.SELF_ID));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					if(cursor !=null){
						cursor.close();
					}
				}
				return id;
		
	}
	//add by lisheng end
	/**返回违章查询的所有二级城市列表,去掉异常信息,含有'县','市辖区'等字段, 且包含4个直辖市*/
	public List<City> getTrafficOffenceList(){
		List<City> cityList = new ArrayList<City>();
		Cursor cursor = null;
		Cursor municipality =null;
		City city = null;
		try{
//			database.query(CityDBTable.TABLE_NAME, new String[]{selectColumn},
//	                key + "=? and " + CityDBTable.CITY_NAME + " like ?", new String[] { "1", cityName + "%"},null, null, null);
			cursor = database.query(CityDBTable.TABLE_NAME, null,
					CityListDB.CityDBTable.CITY_TYPE+"=? and "+CityListDB.CityDBTable.CITY_NAME+" not like ? and "+CityListDB.CityDBTable.CITY_NAME+" not like ?", new String[] {String.valueOf(CityListDB.CITY_TYPE_AREA),"%县%","%市辖区%"},null, null, null);
			while ( cursor.moveToNext() ) {
				String cityName = cursor.getString(cursor
						.getColumnIndex(CityDBTable.CITY_NAME));
				String cityPy = cursor.getString(cursor
						.getColumnIndex(CityDBTable.CITY_PY));
				int selfId = cursor.getInt(cursor
						.getColumnIndex(CityDBTable.SELF_ID));
				
				city = new City();
				city.setCityId(String.valueOf(selfId));
				city.setCityName(cityName);
				city.setCityPY(cityPy);
				cityList.add(city);
			}
			municipality =database.query(CityDBTable.TABLE_NAME, null, CityListDB.CityDBTable.CITY_TYPE+"=? and "+CityListDB.CityDBTable.CITY_NAME+" like ?", new String[]{String.valueOf(CityListDB.CITY_TYPE_PROVINCE),"%市%"}, null, null, null, null);
			while ( municipality.moveToNext()) {
				String cityName = municipality.getString(municipality
						.getColumnIndex(CityDBTable.CITY_NAME));
				String cityPy = municipality.getString(municipality
						.getColumnIndex(CityDBTable.CITY_PY));
				int selfId = municipality.getInt(municipality
						.getColumnIndex(CityDBTable.SELF_ID));
				city = new City();
				city.setCityId(String.valueOf(selfId));
				city.setCityName(cityName);
				city.setCityPY(cityPy);
				cityList.add(city);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if( cursor != null ){
				cursor.close();
			}
			if(municipality!=null){
				municipality.close();
			}
		}
		return cityList;
		
	}
}
