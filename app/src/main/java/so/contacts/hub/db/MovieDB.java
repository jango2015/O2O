package so.contacts.hub.db;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.shuidianmei.WaterElectricityGasDB.WaterElectricityGasTable;
import so.contacts.hub.shuidianmei.bean.WaterElectricityGasBean;
import so.contacts.hub.thirdparty.cinema.bean.MovieCity;
import so.contacts.hub.util.LogUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class MovieDB {
    private static final String TAG = "MovieDB";
    
    SQLiteDatabase database;

    public MovieDB(DatabaseHelper helper) {
        database = helper.getWritableDatabase();
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return this.database;
    }

    public static class MovieCityTable implements BaseColumns {
        public static final String TABLE_NAME = "movie_city";
        public static final String CITY_ID = "city_id";
        public static final String CITY_NAME = "city_name";
    }
    
    static String getCreateMovieCityTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(MovieCityTable.TABLE_NAME).append(" (");
        sb.append(MovieCityTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(MovieCityTable.CITY_ID).append(" TEXT,");
        sb.append(MovieCityTable.CITY_NAME).append(" TEXT");
        sb.append(");");
        return sb.toString();
    }
    
    public void clearTable(String tableName){
        database.delete(tableName, null, null);
    }

    public boolean insertMovieCity(MovieCity city) {
        long row = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(MovieCityTable.CITY_ID, city.getCitycode());
            values.put(MovieCityTable.CITY_NAME, city.getCityname());

            row = database.insert(MovieCityTable.TABLE_NAME, null, values);
        } catch (Exception e) {
            LogUtil.e(TAG, "insertSearchProvider error: " + e);
        }
        
        return row!=-1?true:false;
    }
    
    public synchronized void insertMovieCity(List<MovieCity> cityList) {
        if(cityList == null){
            return ;
        }
        try {
            for (int i = 0; i < cityList.size(); i++) {
                MovieCity city = cityList.get(i);
                ContentValues values = new ContentValues();
                values.put(MovieCityTable.CITY_ID, city.getCitycode());
                values.put(MovieCityTable.CITY_NAME, city.getCityname());
                database.insert(MovieCityTable.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "insertSearchProvider error: " + e);
        }
    }
    
    private List<MovieCity> fetchMovieCityData(Cursor cursor) {
        List<MovieCity> item_list = null;
        try {
        	if(cursor == null || cursor.getCount()==0)
        		return null;
        	
        	item_list = new ArrayList<MovieCity>();
            while(!cursor.isLast()) {
                cursor.moveToNext();

                String movie_city_id = cursor.getString(cursor
                        .getColumnIndex(MovieCityTable.CITY_ID));
                String movie_city_name = cursor.getString(cursor
                        .getColumnIndex(MovieCityTable.CITY_NAME));
                MovieCity bean = new MovieCity();
                bean.setCitycode(movie_city_id);
                bean.setCityname(movie_city_name);
                item_list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return item_list;
    }
    
    public MovieCity queryMovieCityByCid(String cid){
        MovieCity item = null;
        Cursor cursor = database.query(MovieCityTable.TABLE_NAME, null,
                MovieCityTable.CITY_ID + "=?", new String[] { cid}, null, null, null);
        List<MovieCity> list = fetchMovieCityData(cursor);
        if(list != null && list.size() > 0){
            item = list.get(0);
        }
        return item;
    }
    
    public List<MovieCity> queryMovieCityAll(){
        Cursor cursor = database.query(MovieCityTable.TABLE_NAME, null,
                null, null, null, null, null);
        List<MovieCity> list = fetchMovieCityData(cursor);
        return list;
    }
    
    /**
     * add by hyl 2015-1-6
     */
    public MovieCity queryMovieCidByCityName(String cityName){
        MovieCity item = null;
        Cursor cursor = database.query(MovieCityTable.TABLE_NAME, null,
                MovieCityTable.CITY_NAME + "=?", new String[] { cityName}, null, null, null);
        List<MovieCity> list = fetchMovieCityData(cursor);
        if(list != null && list.size() > 0){
            item = list.get(0);
        }
        return item;
    }
    
    
    
    

    
}
