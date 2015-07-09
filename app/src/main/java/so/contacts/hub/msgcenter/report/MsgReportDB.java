package so.contacts.hub.msgcenter.report;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.db.DatabaseHelper;
import so.contacts.hub.db.YellowPageDB.AdTable;
import so.contacts.hub.db.YellowPageDB.CategoryTable;
import so.contacts.hub.util.LogUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class MsgReportDB {
    private static final String TAG = "MsgNotifyDB";
    
    SQLiteDatabase database;

    public MsgReportDB(DatabaseHelper helper) {
        database = helper.getWritableDatabase();
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return this.database;
    }

    public static class MsgNotifyTable implements BaseColumns {
        public static final String TABLE_NAME = "msg_notify";
        public static final String TYPE = "type";
        public static final String ACTION = "action";
        public static final String REPORT_CONTENT = "report_content";
    }
    
    public static String getCreateMsgNotifyTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(MsgNotifyTable.TABLE_NAME).append(" (");
        sb.append(MsgNotifyTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(MsgNotifyTable.TYPE).append(" INTEGER,");
        sb.append(MsgNotifyTable.REPORT_CONTENT).append(" TEXT");
        sb.append(");");
        return sb.toString();
    }
    
    public void clearTable(String tableName){
        database.delete(tableName, null, null);
    }

    public boolean insertMsgNotify(MsgReport report) {
        long row = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(MsgNotifyTable.TYPE, report.getType());
            values.put(MsgNotifyTable.REPORT_CONTENT, report.getReportContent());

            row = database.insert(MsgNotifyTable.TABLE_NAME, null, values);
        } catch (Exception e) {
            LogUtil.e(TAG, "insertSearchProvider error: " + e);
        }
        
        return row!=-1?true:false;
    }
    
    public void insertMsgNotify(List<MsgReport> reportList) {
        if(reportList == null){
            return ;
        }
        try {
            database.beginTransaction();
            for (int i = 0; i < reportList.size(); i++) {
            	MsgReport report = reportList.get(i);
                ContentValues values = new ContentValues();
                values.put(MsgNotifyTable.TYPE, report.getType());
                values.put(MsgNotifyTable.REPORT_CONTENT, report.getReportContent());
                database.insert(MsgNotifyTable.TABLE_NAME, null, values);
            }
            database.setTransactionSuccessful();
            database.endTransaction();
        } catch (Exception e) {
            LogUtil.e(TAG, "insertSearchProvider error: " + e);
        }
    }
    
    private List<MsgReport> fetchMsgNotifyData(Cursor cursor) {
        List<MsgReport> item_list = null;
        if(cursor == null || cursor.getCount()==0)
                return null;
        
        item_list = new ArrayList<MsgReport>();
        try {
            while(!cursor.isLast()) {
                cursor.moveToNext();

                int id = cursor.getInt(cursor.getColumnIndex(MsgNotifyTable._ID));
                int type = cursor.getInt(cursor
                        .getColumnIndex(MsgNotifyTable.TYPE));
                String report = cursor.getString(cursor
                        .getColumnIndex(MsgNotifyTable.REPORT_CONTENT));
                MsgReport bean = new MsgReport();
                bean.setId(id);
                bean.setType(type);
                bean.setReportContent(report);
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
    
    public MsgReport queryMsgNotifyByCid(String cid){
    	MsgReport item = null;
        Cursor cursor = database.query(MsgNotifyTable.TABLE_NAME, null,
        		MsgNotifyTable.TYPE + "=?", new String[] { cid}, null, null, null);
        List<MsgReport> list = fetchMsgNotifyData(cursor);
        if(list != null && list.size() > 0){
            item = list.get(0);
        }
        return item;
    }
    
    public List<MsgReport> queryMsgNotifyAll(){
        Cursor cursor = database.query(MsgNotifyTable.TABLE_NAME, null,
                null, null, null, null, MsgNotifyTable._ID+" DESC");
        List<MsgReport> list = fetchMsgNotifyData(cursor);
        return list;
    }
    
    public int delete(MsgReport msg) {
        if(msg == null) {
            return -1;
        }
        
        int count = database.delete(MsgNotifyTable.TABLE_NAME, 
                MsgNotifyTable._ID + "=" + msg.getId(), null);
        
        return count;
    }
}
