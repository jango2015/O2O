/**
 * @date	: 
 * @author	:
 * @descrip	:
 */
package so.contacts.hub.remind.utils;

import so.contacts.hub.db.DatabaseHelper;
import so.contacts.hub.util.LogUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class RemindDbHelper {
    private static final String TAG = "RemindDbHelper";
    private SQLiteDatabase mDb;

    public RemindDbHelper(DatabaseHelper helper) {
        mDb = helper.getWritableDatabase();
    }

    public static String getCreateTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(Table.TABLE_NAME).append(" (");
        sb.append(Table._ID).append(" INTEGER  PRIMARY KEY  autoincrement,");
        sb.append(Table.COL_ID).append(" LONG  ,");
        sb.append(Table.REMIND_MAPS).append(" TEXT");
        sb.append(");");
        
        LogUtil.i(TAG, "getCreateTableSQL ==> "+sb.toString());
        return sb.toString();
    }

    public String getNodes() {
        StringBuffer sb = new StringBuffer();
        Cursor cursor = null;
        try {
            String whereClause = Table.COL_ID + " = ?";
            String[] whereArgs = new String[] { String
                    .valueOf(Table.VOL_ID) };
            
            cursor = mDb.query(Table.TABLE_NAME, Table.PROJECTION, whereClause,
                    whereArgs, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    String s = cursor.getString(cursor.getColumnIndex(Table.REMIND_MAPS));
                    sb.append(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return sb.toString();
    }
    
    public void save(String remind_data) {
        try {
            mDb.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(Table.COL_ID, Table.VOL_ID);
            values.put(Table.REMIND_MAPS, remind_data);
            
            String whereClause = Table.COL_ID + " = ?";
            String[] whereArgs = new String[] { String
                    .valueOf(Table.VOL_ID) };
            
            if(exist()) {
                mDb.update(Table.TABLE_NAME, values, whereClause,
                        whereArgs);
            } else {
                mDb.insert(Table.TABLE_NAME, null, values);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } finally {
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
        }
    }
    
    public boolean exist() {
        Cursor cursor = null;
        int count = 0;
        try {
            String whereClause = Table.COL_ID + " = ? ";
            String[] whereArgs = new String[] { String.valueOf(Table.VOL_ID) };
            
            cursor = mDb.query(Table.TABLE_NAME, Table.PROJECTION, whereClause,
                    whereArgs, null, null, null);
            if (cursor != null) {
                count = cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count>0?true:false;
    }

    public void delAll() {
        mDb.delete(Table.TABLE_NAME, null, null);
    }

    public static class Table implements BaseColumns {

        public static final String TABLE_NAME = "remind_data";
      
        public static final String COL_ID = "ID";
        public static final int VOL_ID = 1; 
        
        public static final String REMIND_MAPS = "remain_node_maps"; 

        public static final String[] PROJECTION = { _ID, REMIND_MAPS };
    }
}
