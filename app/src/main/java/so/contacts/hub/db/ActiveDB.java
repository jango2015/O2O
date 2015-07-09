package so.contacts.hub.db;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.util.LogUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class ActiveDB {
    private static final String TAG = "ActiveDB";
    
    SQLiteDatabase database;
    //DatabaseHelper mHelper;// modify by putao_lhq 2014年11月4日 for performance; ActiveDB -> DatabaseHelper -> ActiveDB

    public ActiveDB(DatabaseHelper helper) {
        //mHelper = helper;//// modify by putao_lhq 2014年11月4日 for performance
        database = helper.getWritableDatabase();
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return this.database;
    }

    public static class ActiveEggTable implements BaseColumns {
        public static final String TABLE_NAME = "active_egg";
        public static final String ACTIVE_ID = "active_id";
        public static final String EGG_ID = "egg_id";
        public static final String REQUEST_URL = "req_url";
        public static final String TRIGGER = "trigger";
        public static final String TRIGGER_TYPE = "trigger_type";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String STATUS="status";
        public static final String EXPAND_PARAM="expand_param";
    }
    
    static String getCreateActiveTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(ActiveEggTable.TABLE_NAME).append(" (");
        sb.append(ActiveEggTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(ActiveEggTable.ACTIVE_ID).append(" INTEGER,");
        sb.append(ActiveEggTable.EGG_ID).append(" INTEGER,");
        sb.append(ActiveEggTable.REQUEST_URL).append(" TEXT,");
        sb.append(ActiveEggTable.TRIGGER).append(" TEXT,");
        sb.append(ActiveEggTable.TRIGGER_TYPE).append(" INTEGER,");
        sb.append(ActiveEggTable.START_TIME).append(" TEXT,");//
        sb.append(ActiveEggTable.END_TIME).append(" TEXT,");//
        sb.append(ActiveEggTable.STATUS).append(" INTEGER,");
        sb.append(ActiveEggTable.EXPAND_PARAM).append(" TEXT");
        sb.append(");");
        return sb.toString();
    }
    
    public void clearTable(String tableName){
        database.delete(tableName, null, null);
    }

    public boolean insertActiveEgg(ActiveEggBean egg) {
        long row = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(ActiveEggTable.ACTIVE_ID, egg.active_id);
            values.put(ActiveEggTable.EGG_ID, egg.egg_id);
            values.put(ActiveEggTable.REQUEST_URL, egg.request_url);
            values.put(ActiveEggTable.TRIGGER, egg.trigger);
            values.put(ActiveEggTable.TRIGGER_TYPE, egg.trigger_type);
            values.put(ActiveEggTable.START_TIME, egg.valid_time>0?String.valueOf(egg.valid_time):String.valueOf(egg.start_time));
            values.put(ActiveEggTable.END_TIME, String.valueOf(egg.end_time));
            values.put(ActiveEggTable.STATUS, egg.status);
            values.put(ActiveEggTable.EXPAND_PARAM, egg.expand_param);

            row = database.insert(ActiveEggTable.TABLE_NAME, null, values);
        } catch (Exception e) {
            LogUtil.e(TAG, "insertSearchProvider error: " + e);
        }
        
        return row!=-1?true:false;
    }

    // 根据任务id查所有蛋
    public List<ActiveEggBean> qryActiveList(int active_id) {
        if(active_id < 0)
            return null;
        
        Cursor cursor = database.query(ActiveEggTable.TABLE_NAME, null,
                ActiveEggTable.ACTIVE_ID + "=? " ,
                new String[] { String.valueOf(active_id)  }, 
                null, null, null);
        
        /*if (cursor == null || cursor.getCount()==0)
            return null;*/
        
        List<ActiveEggBean> list = new ArrayList<ActiveEggBean>();
        try {
        	if (cursor == null || cursor.getCount()==0) {
        		return null;
        	}
            while (cursor.moveToNext()) {
                ActiveEggBean bean = fetchActiveEggBean(cursor);
                if(bean != null)
                    list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }
    
    /**
     * 根据扩展参数以及节点服务名来查询彩蛋
     * @param trigger
     * @param expand
     * @return
     */
    public ActiveEggBean qryEggTigger(String trigger, String expand) {
        if(TextUtils.isEmpty(trigger))
            return null;
        
        String orderby = ActiveEggTable._ID+" desc";
        Cursor cursor = database.query(ActiveEggTable.TABLE_NAME, null,
                ActiveEggTable.TRIGGER + "=? " + "AND " + ActiveEggTable.EXPAND_PARAM + "=? ",
                new String[] { trigger, expand }, 
                null, null, orderby);
        // delete by putao_lhq 2014年11月4日 for performance
        /*if (cursor == null || cursor.getCount()==0)
            return null;*/
        
        ActiveEggBean bean = null;
        try {
        	// add by putao_lhq 2014年11月4日 for performance start
        	if (cursor == null || cursor.getCount()==0){
        		return null;
        	}
        	// add by putao_lhq 2014年11月4日 for performance end
            if (cursor.moveToNext()) {
                bean = fetchActiveEggBean(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return bean;
    }
    
    // 根据埋点查蛋
    public ActiveEggBean qryEggTigger(String trigger) {
        if(TextUtils.isEmpty(trigger))
            return null;
        
        String orderby = ActiveEggTable._ID+" desc";
        Cursor cursor = database.query(ActiveEggTable.TABLE_NAME, null,
                ActiveEggTable.TRIGGER + "=? " ,
                new String[] { trigger }, 
                null, null, orderby);
        // delete by putao_lhq 2014年11月4日 for performance
        /*if (cursor == null || cursor.getCount()==0)
            return null;*/
        
        ActiveEggBean bean = null;
        try {
        	// add by putao_lhq 2014年11月4日 for performance start
        	if (cursor == null || cursor.getCount()==0) {
        		return null;
        	}
        	// add by putao_lhq 2014年11月4日 for performance end
            if (cursor.moveToNext()) {
                bean = fetchActiveEggBean(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return bean;
    }
    
    private ActiveEggBean fetchActiveEggBean(Cursor cursor) throws Exception {
        if (cursor == null || cursor.getCount()==0)
            return null;
        
        ActiveEggBean bean = null;
        if (true) {
            long active_id = cursor.getLong(cursor.getColumnIndex(ActiveEggTable.ACTIVE_ID));
            int egg_id = cursor.getInt(cursor.getColumnIndex(ActiveEggTable.EGG_ID));
            String req_url = cursor.getString(cursor.getColumnIndex(ActiveEggTable.REQUEST_URL));
            String trigger = cursor.getString(cursor.getColumnIndex(ActiveEggTable.TRIGGER));
            int trigger_type = cursor.getInt(cursor.getColumnIndex(ActiveEggTable.TRIGGER_TYPE));
            long start_time = Long.parseLong(cursor.getString(cursor.getColumnIndex(ActiveEggTable.START_TIME)));
            long end_time = Long.parseLong(cursor.getString(cursor.getColumnIndex(ActiveEggTable.END_TIME)));
            String expand_param = cursor.getString(cursor.getColumnIndex(ActiveEggTable.EXPAND_PARAM));

            bean = new ActiveEggBean();
            bean.active_id = active_id;
            bean.egg_id = egg_id;
            bean.request_url = req_url;
            bean.trigger = trigger;
            bean.trigger_type = trigger_type;
            bean.start_time = start_time;
            bean.end_time = end_time;
            bean.expand_param = expand_param;
        }
        
        return bean;
    }
    
    public int deleteActive(long active_id) {
        return database.delete(ActiveEggTable.TABLE_NAME, 
                ActiveEggTable.ACTIVE_ID + "=" + active_id, 
                null);
    }
    
    public int deleteActiveEgg(long active_id, int egg_id) {
        return database.delete(ActiveEggTable.TABLE_NAME, 
                ActiveEggTable.ACTIVE_ID + "=? and " + ActiveEggTable.EGG_ID + "=?", 
                new String[] { String.valueOf(active_id), String.valueOf(egg_id) });
    }
    
}
