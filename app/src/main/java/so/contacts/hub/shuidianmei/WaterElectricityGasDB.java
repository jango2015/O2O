package so.contacts.hub.shuidianmei;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.db.DatabaseHelper;
import so.contacts.hub.shuidianmei.bean.WaterElectricityGasBean;
import so.contacts.hub.util.LogUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class WaterElectricityGasDB {
    private static final String TAG = "WaterElectricityGasDB";
    
    
    SQLiteDatabase database;
    //DatabaseHelper mHelper;// modify by putao_lhq 2014年11月4日 for performance; ActiveDB -> DatabaseHelper -> ActiveDB

    public WaterElectricityGasDB(DatabaseHelper helper) {
        //mHelper = helper;//// modify by putao_lhq 2014年11月4日 for performance
        database = helper.getWritableDatabase();
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return this.database;
    }

    
    //add ljq 2014-11-28 
    public static class WaterElectricityGasTable implements BaseColumns{                                                                             
        public static final String TABLE_NAME = "yellow_weg";               // 水电煤表
        public static final String WEG_PRODUCT_ID = "weg_product_id";       //【产品编号】
        public static final String WEG_PROVINCE = "weg_province";           //【省份】
        public static final String WEG_CITY = "weg_city";                   //【城市】
        public static final String WEG_COMPANY = "weg_company";             //【办理单位】 
        public static final String WEG_RECHARGE_TYPE = "weg_recharge_type"; //【充值类型】 
    }
    
    public static String getCreateWaterElectricityGasTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(WaterElectricityGasTable.TABLE_NAME).append(" (");
        sb.append(WaterElectricityGasTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(WaterElectricityGasTable.WEG_PRODUCT_ID).append(" TEXT,");
        sb.append(WaterElectricityGasTable.WEG_PROVINCE).append(" TEXT,");
        sb.append(WaterElectricityGasTable.WEG_CITY).append(" TEXT,");
        sb.append(WaterElectricityGasTable.WEG_COMPANY).append(" TEXT,");
        sb.append(WaterElectricityGasTable.WEG_RECHARGE_TYPE).append(" INTEGER");
        sb.append(");");
        return sb.toString();
    }
    
    /**
     * 插入水电煤数据
     */
    public void insertWaterElectricityGas(WaterElectricityGasBean wegBean) {
        if(wegBean != null){
            try {
                ContentValues values = new ContentValues();
                values.put(WaterElectricityGasTable.WEG_PRODUCT_ID, wegBean.getProduct_id());
                values.put(WaterElectricityGasTable.WEG_PROVINCE, wegBean.getProvince());
                values.put(WaterElectricityGasTable.WEG_CITY, wegBean.getCity());
                values.put(WaterElectricityGasTable.WEG_COMPANY, wegBean.getCompany());
                values.put(WaterElectricityGasTable.WEG_RECHARGE_TYPE, wegBean.getWeg_type());
                long count = database.insert(WaterElectricityGasTable.TABLE_NAME, null, values);
            } catch (Exception e) {
                LogUtil.e(TAG, "insert exception: " + e);
            }
        }
    }
    
    /**
     * 插入水电煤数据
     */
    public void insertWaterElectricityGasList(List<WaterElectricityGasBean> wegBeanList) {
        if(wegBeanList != null && wegBeanList.size() > 0 ){
            try {
                for (int i = 0; i < wegBeanList.size(); i++) {
                    WaterElectricityGasBean wegBean = wegBeanList.get(i);
                    ContentValues values = new ContentValues();
                    values.put(WaterElectricityGasTable.WEG_PRODUCT_ID, wegBean.getProduct_id());
                    values.put(WaterElectricityGasTable.WEG_PROVINCE, wegBean.getProvince());
                    values.put(WaterElectricityGasTable.WEG_CITY, wegBean.getCity());
                    values.put(WaterElectricityGasTable.WEG_COMPANY, wegBean.getCompany());
                    values.put(WaterElectricityGasTable.WEG_RECHARGE_TYPE, wegBean.getWeg_type());
                    long count = database.insert(WaterElectricityGasTable.TABLE_NAME, null, values);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "insert exception: " + e);
            }
        }
    }
    
    private List<WaterElectricityGasBean> fetchWaterElectricityGasData(Cursor cursor) {
        List<WaterElectricityGasBean> item_list = null;
        if(cursor == null || cursor.getCount()==0)
                return null;
        
        item_list = new ArrayList<WaterElectricityGasBean>();
        try {
            while(!cursor.isLast()) {
                cursor.moveToNext();

                String product_id = cursor.getString(cursor
                        .getColumnIndex(WaterElectricityGasTable.WEG_PRODUCT_ID));
                String province = cursor.getString(cursor
                        .getColumnIndex(WaterElectricityGasTable.WEG_PROVINCE));
                String city = cursor.getString(cursor
                        .getColumnIndex(WaterElectricityGasTable.WEG_CITY));
                String company = cursor.getString(cursor
                        .getColumnIndex(WaterElectricityGasTable.WEG_COMPANY));
                int recharge_type = cursor.getInt(cursor
                        .getColumnIndex(WaterElectricityGasTable.WEG_RECHARGE_TYPE));
                WaterElectricityGasBean bean = new WaterElectricityGasBean();
                bean.setCity(city);
                bean.setCompany(company);
                bean.setProduct_id(product_id);
                bean.setProvince(province);
                bean.setWeg_type(recharge_type);
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
    
    public List<WaterElectricityGasBean> queryWegDataByProid(String proid){
        List<WaterElectricityGasBean> wegList = new ArrayList<WaterElectricityGasBean>();
        Cursor cursor = database.query(WaterElectricityGasTable.TABLE_NAME, null,
                WaterElectricityGasTable.WEG_PRODUCT_ID + "=?", new String[] { proid}, null, null, null);
        wegList = fetchWaterElectricityGasData(cursor);
        return wegList;
    }
    
    public List<WaterElectricityGasBean> queryWegDataByType(int type){
        List<WaterElectricityGasBean> wegList = new ArrayList<WaterElectricityGasBean>();
        Cursor cursor = database.query(WaterElectricityGasTable.TABLE_NAME, null,
                WaterElectricityGasTable.WEG_RECHARGE_TYPE + "=?", new String[] { String.valueOf(type)}, null, null, null);
        wegList = fetchWaterElectricityGasData(cursor);
        return wegList;
    }
    
    public List<WaterElectricityGasBean> queryWegDataByTypeAndCity(int type , String city){
        List<WaterElectricityGasBean> wegList = null;
        Cursor cursor = database.query(WaterElectricityGasTable.TABLE_NAME, null,
                WaterElectricityGasTable.WEG_RECHARGE_TYPE + "=? and " + WaterElectricityGasTable.WEG_CITY + "=? ", new String[] { String.valueOf(type),city}, null, null, null);
        wegList = fetchWaterElectricityGasData(cursor);
        return wegList;
    }
    
    public List<String> queryWegCityListByType(int type){
        Cursor cursor = database.query(true,WaterElectricityGasTable.TABLE_NAME, new String[] { WaterElectricityGasTable.WEG_CITY},
                WaterElectricityGasTable.WEG_RECHARGE_TYPE + "=?", new String[] { String.valueOf(type)}, null, null, null,null);
        if(cursor == null || cursor.getCount()==0)
            return null;
        
        ArrayList<String> city_list = new ArrayList<String>();
        try {
            while(!cursor.isLast()) {
                cursor.moveToNext();

                String city = cursor.getString(cursor
                        .getColumnIndex(WaterElectricityGasTable.WEG_CITY));
                
                city_list.add(city);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return city_list;
    }

}
