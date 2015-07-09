package so.contacts.hub.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import so.contacts.hub.active.bean.ActiveHistoryBean;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.push.bean.PushAdBean;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.search.bean.SearchStrategyBean;
import so.contacts.hub.ui.yellowpage.YellowPageSearchActivity;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.Express;
import so.contacts.hub.ui.yellowpage.bean.HabitDataItem;
import so.contacts.hub.ui.yellowpage.bean.ItemBean;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MyCenterConstant;
import so.contacts.hub.util.PinyinHelper;
import so.contacts.hub.util.YellowUtil;
import so.putao.findplug.YellowPageCollectData;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class YellowPageDB {
    private static final String TAG = "YellowPageDB";

    SQLiteDatabase database;
    //DatabaseHelper mHelper;

    public YellowPageDB(DatabaseHelper helper) {
        //mHelper = helper;
        database = helper.getWritableDatabase();
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return this.database;
    }

    public static class CategoryTable implements BaseColumns {
        public static final String TABLE_NAME = "yellow_page_category";
        public static final String _ID = "_id";
        public static final String CATEGORY_ID = "category_id";
        public static final String NAME = "name";
        public static final String SHOWNAME="show_name";
        public static final String PARENT_ID = "parent_id";
        public static final String ICON = "icon";
        public static final String ICON_LOGO = "icon_logo";
        public static final String SORT = "sort";
        public static final String LAST_SORT = "last_sort";
        public static final String TARGET_ACTIVITY = "target_activity";
        public static final String TARGET_PARAMS = "target_params";
        public static final String PRESS_ICON = "press_icon";
        public static final String EDITTYPE = "editype";
        // 继续添加数据库内容
        public static final String REMIND_CODE = "remind_code";
        // 被改变的类型 add ljq 2014/10/13
        public static final String CHANGE_TYPE = "change_type";
        // 添加额外字段 保存json add by lisheng 2014-11-07
        public static final String EXPAND_PARAM = "expand_param";
        // 添加关键字标签字段 add by zjh 2014-11-21
        public static final String KEY_TAG = "key_tag";
        // 添加关键字标签排序字段 add by ljq 2014-11-24
        public static final String SEARCH_SORT = "search_sort";
    }

    public static class ItemTable implements BaseColumns {
        public static final String TABLE_NAME = "yellow_page_item";
        public static final String ITEM_ID = "item_id";
        public static final String CATEGORY_ID = "category_id";
        public static final String PROVIDER = "provider";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String ICON = "icon";
        public static final String SORT = "sort";
        public static final String TARGET_ACTIVITY = "target_activity";
        public static final String TARGET_PARAMS = "target_params";
        public static final String CONTENT = "content";
        public static final String REMIND_CODE = "remind_code";
        // 添加关键字标签字段 add by zjh 2014-11-21
        public static final String KEY_TAG = "key_tag";
        // 添加关键字标签排序字段 add by ljq 2014-11-24
        public static final String SEARCH_SORT = "search_sort";
    }

    public static class ExpressTable implements BaseColumns{
        public static final String TABLE_NAME = "yellow_page_express";
        public static final String EXPRESS_ID = "express_id";
        public static final String EXPRESS_PY = "express_py";
        public static final String EXPRESS_NAME = "express_name";
        public static final String EXPRESS_LOGO = "express_logo";
        public static final String EXPRESS_PHONE = "express_phone";
    }

    public static class CollectTable implements BaseColumns{
        public static final String TABLE_NAME = "yellow_item_collect";
        public static final String COLLECT_ID = "collect_ID";
        public static final String COLLECT_DATA_TYPE = "data_type";
        public static final String COLLECT_NAME = "collect_name";
        public static final String COLLECT_TYPE = "collect_type";
        public static final String COLLECT_CONTENT = "collect_content";
        public static final String COLLECT_TIME = "collect_time";
    }

    //add by ljq 2014-9-22
    public static class HabitDataTable implements BaseColumns{
        public static final String TABLE_NAME = "yellow_habit_data";//用户习惯记录
        //add ljq 2014-12-5 start
        public static final String SERVICE_ID = "service_id";//服务器ID
        //add ljq 2014-12-5 end
        public static final String SOURCE_TYPE = "source_type";//业务模块
        public static final String CONTENT_TYPE = "content_type";//[数据类型：MOBILE,EMAIL,SFZ,ADDR]
        public static final String CONTENT_DATA = "content_data";//[数据内容]
        public static final String ISUPLOAD = "isupload";
        public static final String TIME = "time";

    }

    public static class HabitDataVersionTable implements BaseColumns{
        public static final String TABLE_NAME = "yellow_habit_data_version";//用户习惯记录版本
        public static final String VERSION_DATA = "version_data";     //黄页本地数据version
    }

    // 表：广告 add by zjh 2014-10-09
    public static class AdTable implements BaseColumns{
        public static final String TABLE_NAME = "yellow_ad";                 // 运营广告表
        public static final String AD_CODE = "ad_code";                      //【广告所在页面】
        public static final String AD_PAGE_INDEX = "ad_page_index";          //【广告所在页面位置】1:顶部;2:中间；3：底部
        public static final String AD_IMG_URL = "ad_img_url";                //【广告图片Url】长度 = 1:一张图；2：两张图；3：三张图
        public static final String AD_CLICK_TYPE = "ad_click_type";          //【广告跳转类型】1:打开特定服务页面；2:打开特定链接H5页面
        public static final String AD_CLICK_ACTIVITY = "ad_click_activity";  //【广告跳转】打开的页面activity名称
        public static final String AD_CLICK_LINK = "ad_click_link";          //【广告跳转连接】notify_type=1：为类名；notify_type=2：为H5 Url;
        public static final String AD_TEXT = "ad_text";                      //【文本描述】
        public static final String AD_START_TIME = "ad_start_time";          //【广告开始时间】
        public static final String AD_END_TIME = "ad_end_time";              //【广告结束时间】
        public static final String AD_DATA = "ad_data";                      //【广告扩展字段】
        public static final String AD_PARAMS = "ad_params";                  //【广告参数字段】
    }

    public static class ActiveHistoryTable implements BaseColumns{
        public static final String TABLE_NAME = "yellow_activities";       // 运营活动表
        public static final String MY_ACT_ID = "my_act_id";				   //【我的活动id】
        public static final String ACT_ID = "act_id";                      //【活动id】
        public static final String ACT_USER_ID = "act_user_id";            //【用户id】
        public static final String ACT_STEP = "act_step";				   //【参加活动步骤】
        public static final String ACT_TIME = "act_time";                  //【参加活动时间】
        public static final String ACT_NAME = "act_name";                  //【活动名称】
        public static final String ACT_DES = "act_description";            //【活动描述】
        public static final String ACT_STATUS = "act_status";              //【活动状态】
        public static final String ACT_TARGET_URL = "act_target_url";      //【目标地址】
        public static final String ACT_ICON_URL = "act_icon_url";      	   //【活动图片地址】
        public static final String ACT_UP_TIME = "act_up_time";      	   //【更新时间】
        public static final String ACT_REMIND = "act_remind";              //【活动提醒标识】 1-提醒 0-不提醒
    }

    /**
     * 黄页中所有需要标记的数据的版本控制表
     * add by zjh 2014-12-05
     */
    public static class YellowDataVersionTable implements BaseColumns{
        public static final String TABLE_NAME = "yellow_data_version";  //黄页本地数据 version记录表
        public static final String TAG_DATA = "tag_data";     			//数据标记的tag
        public static final String VERSION_DATA = "version_data";       //数据version
    }

    /**
     * 搜索配置缓存表
     * add by zjh 2014-12-05
     */
    public static class SearchConfigCacheTable implements BaseColumns{
        public static final String TABLE_NAME = "search_config_cache";
        public static final String ENTRY_TYPE = "entry_type";
        public static final String WORDS = "words";
        public static final String CATEGORY = "category";
        public static final String CITY = "city";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String PAGE = "page";
        public static final String LIMIT = "limit_num";
        public static final String SOURCE = "source";
        public static final String SERVICE_NAME = "service_name";
        public static final String FACTORY = "factory";
        public static final String SORT = "sort";
        public static final String ORDER_BY = "orderby";
        public static final String OUT_TIME = "out_time";
    }

    static String getCreateCategoryTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(CategoryTable.TABLE_NAME).append(" (");
        sb.append(CategoryTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(CategoryTable.CATEGORY_ID).append(" INTEGER,");
        sb.append(CategoryTable.NAME).append(" TEXT,");
        sb.append(CategoryTable.SHOWNAME).append(" TEXT,");
        sb.append(CategoryTable.PARENT_ID).append(" INTEGER,");
        sb.append(CategoryTable.ICON).append(" TEXT,");
        sb.append(CategoryTable.ICON_LOGO).append(" TEXT,");
        sb.append(CategoryTable.SORT).append(" INTEGER,");
        sb.append(CategoryTable.LAST_SORT).append(" INTEGER,");
        sb.append(CategoryTable.TARGET_ACTIVITY).append(" TEXT,");
        sb.append(CategoryTable.TARGET_PARAMS).append(" TEXT,");
        sb.append(CategoryTable.PRESS_ICON).append(" TEXT,");
        sb.append(CategoryTable.REMIND_CODE).append(" INTEGER,");
        sb.append(CategoryTable.EDITTYPE).append(" TEXT,");
        sb.append(CategoryTable.CHANGE_TYPE).append(" INTEGER,");
        sb.append(CategoryTable.EXPAND_PARAM).append(" TEXT,");
        sb.append(CategoryTable.KEY_TAG).append(" TEXT,");
        sb.append(CategoryTable.SEARCH_SORT).append(" INTEGER");
        sb.append(");");
        return sb.toString();
    }

    static String getCreateItemTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(ItemTable.TABLE_NAME)
                .append(" (");
        sb.append(ItemTable.ITEM_ID).append(" INTEGER PRIMARY KEY ,");
        sb.append(ItemTable.CATEGORY_ID).append(" INTEGER,");
        sb.append(ItemTable.PROVIDER).append(" INTEGER,");
        sb.append(ItemTable.NAME).append(" TEXT,");
        sb.append(ItemTable.DESCRIPTION).append(" TEXT,");
        sb.append(ItemTable.ICON).append(" TEXT,");
        sb.append(ItemTable.SORT).append(" INTEGER,");
        sb.append(ItemTable.TARGET_ACTIVITY).append(" TEXT,");
        sb.append(ItemTable.TARGET_PARAMS).append(" TEXT,");
        sb.append(ItemTable.CONTENT).append(" TEXT,");
        sb.append(CategoryTable.REMIND_CODE).append(" INTEGER,");
        sb.append(ItemTable.KEY_TAG).append(" TEXT,");
        sb.append(ItemTable.SEARCH_SORT).append(" INTEGER");
        sb.append(");");
        return sb.toString();
    }

    static String getCreateExpressTableSQL(){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(ExpressTable.TABLE_NAME)
                .append(" (");
        sb.append(ExpressTable.EXPRESS_ID).append(" integer  PRIMARY KEY  autoincrement,");
        sb.append(ExpressTable.EXPRESS_NAME).append(" TEXT,");
        sb.append(ExpressTable.EXPRESS_PY).append(" TEXT,");
        sb.append(ExpressTable.EXPRESS_LOGO).append(" TEXT,");
        sb.append(ExpressTable.EXPRESS_PHONE).append(" TEXT");
        sb.append(");");
        return sb.toString();
    }

    static String getCreateCollectTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(CollectTable.TABLE_NAME).append(" (");
        sb.append(CategoryTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(CollectTable.COLLECT_ID).append(" INTEGER,");
        sb.append(CollectTable.COLLECT_DATA_TYPE).append(" INTEGER,");
        sb.append(CollectTable.COLLECT_NAME).append(" TEXT,");
        sb.append(CollectTable.COLLECT_TYPE).append(" INTEGER,");
        sb.append(CollectTable.COLLECT_CONTENT).append(" TEXT,");
        sb.append(CollectTable.COLLECT_TIME).append(" TEXT");
        sb.append(");");
        return sb.toString();
    }

    //add by hyl 2014-8-7
    static String getCreateDataVersionTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(YellowDataVersionTable.TABLE_NAME).append(" (");
        sb.append(YellowDataVersionTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(YellowDataVersionTable.TAG_DATA).append(" TEXT,");  // add by zjh 2014-12-05
        sb.append(YellowDataVersionTable.VERSION_DATA).append(" INTEGER");
        sb.append(");");
        return sb.toString();
    }

    //add by ljq start 2014-9-22
    static String getCreateHabitDataTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(HabitDataTable.TABLE_NAME).append(" (");
        sb.append(HabitDataTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(HabitDataTable.SERVICE_ID).append(" LONG,");
        sb.append(HabitDataTable.SOURCE_TYPE).append(" TEXT,");
        sb.append(HabitDataTable.CONTENT_TYPE).append(" TEXT,");
        sb.append(HabitDataTable.CONTENT_DATA).append(" TEXT,");
        sb.append(HabitDataTable.ISUPLOAD).append(" INTEGER,");
        sb.append(HabitDataTable.TIME).append(" TEXT");
        sb.append(");");
        return sb.toString();
    }

    static String getCreateHabitDataVersionTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(HabitDataVersionTable.TABLE_NAME).append(" (");
        sb.append(HabitDataVersionTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(HabitDataVersionTable.VERSION_DATA).append(" TEXT ");
        sb.append(");");
        return sb.toString();
    }

    // 创建广告表 add by zjh 2014-10-09
    static String getCreateAdTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(AdTable.TABLE_NAME).append(" (");
        sb.append(AdTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(AdTable.AD_CODE).append(" INTEGER,");
        sb.append(AdTable.AD_PAGE_INDEX).append(" INTEGER,");
        sb.append(AdTable.AD_IMG_URL).append(" TEXT,");
        sb.append(AdTable.AD_CLICK_TYPE).append(" TEXT,");
        sb.append(AdTable.AD_CLICK_ACTIVITY).append(" TEXT,");
        sb.append(AdTable.AD_CLICK_LINK).append(" TEXT,");
        sb.append(AdTable.AD_TEXT).append(" TEXT,");
        sb.append(AdTable.AD_START_TIME).append(" LONG,");
        sb.append(AdTable.AD_END_TIME).append(" LONG,");
        sb.append(AdTable.AD_DATA).append(" TEXT,");
        sb.append(AdTable.AD_PARAMS).append(" TEXT");

        sb.append(");");
        return sb.toString();
    }

    static String getCreateActiveHistoryTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(ActiveHistoryTable.TABLE_NAME).append(" (");
        sb.append(ActiveHistoryTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(ActiveHistoryTable.MY_ACT_ID).append(" INTEGER,");
        sb.append(ActiveHistoryTable.ACT_ID).append(" INTEGER,");
        sb.append(ActiveHistoryTable.ACT_USER_ID).append(" INTEGER,");
        sb.append(ActiveHistoryTable.ACT_STEP).append(" INTEGER,");
        sb.append(ActiveHistoryTable.ACT_TIME).append(" TEXT,");
        sb.append(ActiveHistoryTable.ACT_NAME).append(" TEXT,");
        sb.append(ActiveHistoryTable.ACT_DES).append(" TEXT,");
        sb.append(ActiveHistoryTable.ACT_TARGET_URL).append(" TEXT,");
        sb.append(ActiveHistoryTable.ACT_ICON_URL).append(" TEXT,");
        sb.append(ActiveHistoryTable.ACT_UP_TIME).append(" TEXT,");
        sb.append(ActiveHistoryTable.ACT_STATUS).append(" TEXT,");
        sb.append(ActiveHistoryTable.ACT_REMIND).append(" INTEGER");
        sb.append(");");
        return sb.toString();
    }

    static String getCreateSearchConfigCacheTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(SearchConfigCacheTable.TABLE_NAME).append(" (");
        sb.append(SearchConfigCacheTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(SearchConfigCacheTable.ENTRY_TYPE).append(" INTEGER,");
        sb.append(SearchConfigCacheTable.WORDS).append(" TEXT,");
        sb.append(SearchConfigCacheTable.CATEGORY).append(" TEXT,");
        sb.append(SearchConfigCacheTable.CITY).append(" TEXT,");
        sb.append(SearchConfigCacheTable.LONGITUDE).append(" TEXT,");
        sb.append(SearchConfigCacheTable.LATITUDE).append(" TEXT,");
        sb.append(SearchConfigCacheTable.PAGE).append(" INTEGER,");
        sb.append(SearchConfigCacheTable.LIMIT).append(" INTEGER,");
        sb.append(SearchConfigCacheTable.SOURCE).append(" INTEGER,");
        sb.append(SearchConfigCacheTable.SERVICE_NAME).append(" TEXT,");
        sb.append(SearchConfigCacheTable.FACTORY).append(" TEXT,");
        sb.append(SearchConfigCacheTable.SORT).append(" INTEGER,");
        sb.append(SearchConfigCacheTable.ORDER_BY).append(" TEXT,");
        sb.append(SearchConfigCacheTable.OUT_TIME).append(" LONG");
        sb.append(");");
        return sb.toString();
    }

    /**
     * 插入活动历史数据
     * @param history
     */
    public void insertActiveHisBean(ActiveHistoryBean history) {
        LogUtil.d(TAG, "insert: " + history.toString());
        try {
            ContentValues values = new ContentValues();
            values.put(ActiveHistoryTable.ACT_DES, history.description);
            values.put(ActiveHistoryTable.ACT_NAME, history.name);
            values.put(ActiveHistoryTable.ACT_ICON_URL, history.icon_url);
            values.put(ActiveHistoryTable.ACT_ID, history.activity_id);
            values.put(ActiveHistoryTable.ACT_STATUS, history.status);
            values.put(ActiveHistoryTable.ACT_STEP, history.step_id);
            values.put(ActiveHistoryTable.ACT_TARGET_URL, history.target_url);
            values.put(ActiveHistoryTable.ACT_TIME, history.participation_time);
            values.put(ActiveHistoryTable.ACT_UP_TIME, history.update_time);
            values.put(ActiveHistoryTable.ACT_USER_ID, history.u_id);
            values.put(ActiveHistoryTable.MY_ACT_ID, history.id);
            values.put(ActiveHistoryTable.ACT_REMIND, 1);
            long count = database.insert(ActiveHistoryTable.TABLE_NAME, null, values);

            LogUtil.d(TAG, "insert result: " + count);
        } catch (Exception e) {
            LogUtil.e(TAG, "insert exception: " + e);
        }
    }

    public void insertActiveHisBeanList(List<ActiveHistoryBean> historyList) {
        if(historyList != null){
            for (int i = 0; i < historyList.size(); i++) {
                if(historyList.get(i)!=null){
                    insertActiveHisBean(historyList.get(i));
                }
            }
        }
    }

    /**
     * 根据活动id和我的活动id
     * @param code
     * @return
     */
    public void updateActiveRemind(long active_id, long my_active_id){
        ContentValues cv = new ContentValues();
        cv.put(ActiveHistoryTable.ACT_REMIND, 0);
        database.update(ActiveHistoryTable.TABLE_NAME, cv,
                ActiveHistoryTable.ACT_ID + "=? and " +
                        ActiveHistoryTable.MY_ACT_ID + "=? ",
                new String[] { String.valueOf(active_id), String.valueOf(my_active_id)});
    }

    public int deleteActiveHistoryByUid(String uid){
        return database.delete(ActiveHistoryTable.TABLE_NAME, ActiveHistoryTable.ACT_USER_ID + "=?"
                ,new String[]{String.valueOf(uid)});
    }

    public ActiveHistoryBean queryActiveHistoryById(long active_id, long my_active_id){
        ActiveHistoryBean activeHistory = null;
        Cursor cursor = database.query(ActiveHistoryTable.TABLE_NAME, null,
                ActiveHistoryTable.ACT_ID + "=" + active_id +" and "+ActiveHistoryTable.MY_ACT_ID+"="+my_active_id, null, null, null, null);
        if(cursor == null)
            return null;

        try{
            cursor.moveToNext();
            activeHistory = parseCursor2ActiveHistory(cursor);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return activeHistory;
    }

    private ActiveHistoryBean parseCursor2ActiveHistory(Cursor cursor){
        ActiveHistoryBean history = null;
        if(cursor !=null){
            history = new ActiveHistoryBean();
            history.activity_id = cursor.getLong(cursor.getColumnIndex(ActiveHistoryTable.ACT_ID));
            history.description = cursor.getString(cursor.getColumnIndex(ActiveHistoryTable.ACT_DES));
            history.icon_url = cursor.getString(cursor.getColumnIndex(ActiveHistoryTable.ACT_ICON_URL));
            history.id = cursor.getLong(cursor.getColumnIndex(ActiveHistoryTable.MY_ACT_ID));
            history.name = cursor.getString(cursor.getColumnIndex(ActiveHistoryTable.ACT_NAME));
            history.participation_time = cursor.getString(cursor.getColumnIndex(ActiveHistoryTable.ACT_TIME));
            history.status = cursor.getString(cursor.getColumnIndex(ActiveHistoryTable.ACT_STATUS));
            history.target_url = cursor.getString(cursor.getColumnIndex(ActiveHistoryTable.ACT_TARGET_URL));
            history.u_id = cursor.getString(cursor.getColumnIndex(ActiveHistoryTable.ACT_USER_ID));
            history.update_time = cursor.getString(cursor.getColumnIndex(ActiveHistoryTable.ACT_UP_TIME));
            history.remind = cursor.getInt(cursor.getColumnIndex(ActiveHistoryTable.ACT_REMIND));
        }
        return history;
    }

    /**
     *
     * @param isASC
     * @return
     */
    public ArrayList<ActiveHistoryBean> queryAllActiveHistory(String uid, boolean isASC){
        String order = isASC?" ASC":" DESC";
        ActiveHistoryBean activities = null;
        ArrayList<ActiveHistoryBean> activitiesList = new ArrayList<ActiveHistoryBean>();

        Cursor cursor = database.query(ActiveHistoryTable.TABLE_NAME, null,
                ActiveHistoryTable.ACT_USER_ID+"=?",new String[] { uid},null, null,ActiveHistoryTable.ACT_UP_TIME + order);

        if(cursor == null)
            return null;
        try{
            while(cursor.moveToNext()){
                activities = parseCursor2ActiveHistory(cursor);
                activitiesList.add(activities);
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return activitiesList;
    }

    /**
     * 如果不存在则插入，存在数据则更新
     *
     */
    public void updateOrInsertHabitData(HabitDataItem habitDataItem){
        if(isExistHabitData(habitDataItem)){
            updateSameHabitDataTime(habitDataItem);
        }else{
            insert(habitDataItem);
        }
    }

    /**
     * 如果不存在则插入，存在数据则更新
     *
     */
    public int delHabitData(HabitDataItem habitDataItem){
        int count = 0 ;
        String source_type = habitDataItem.getSource_type();
        String content_type = habitDataItem.getContent_type();
        String content_data = habitDataItem.getContent_data();
        if(isExistHabitData(habitDataItem)){
            String selection = HabitDataTable.SOURCE_TYPE + "=? and "
                    + HabitDataTable.CONTENT_TYPE + "=? and " + HabitDataTable.CONTENT_DATA +"=? ";
            count = database.delete(HabitDataTable.TABLE_NAME,
                    selection, new String[] { source_type,content_type,content_data});
        }
        return count;
    }

    /**
     * 黄页中所有需要标记的数据
     */
    public static String YELLOW_DATA_VERSION_DEFAULT = "yellow_data_version"; // 黄页数据版本
    public static String YELLOW_DATA_VERSION_SEARCH = "search_version";       //搜索数据版本
    public static String YELLOW_DATA_VERSION_HABIT = "habit_data_version";	  //资料数据版本

    /**
     * 插入tag_data标记的数据版本
     */
    public void insertDataVersion(String tag_data, int version){
        if( hasDataVersion(tag_data) ){
            updateDataVersion(tag_data, version);
        }else{
            ContentValues values = new ContentValues();
            values.put(YellowDataVersionTable.TAG_DATA, tag_data);
            values.put(YellowDataVersionTable.VERSION_DATA, version);
            database.insert(YellowDataVersionTable.TABLE_NAME, null, values);
        }
    }

    private boolean hasDataVersion(String tag_data){
        boolean hasData = false;
        Cursor cursor = null;
        try{
            cursor = database.query(YellowDataVersionTable.TABLE_NAME, null,
                    YellowDataVersionTable.TAG_DATA + "=?", new String[] { tag_data}, null, null, null);
            if( cursor != null && cursor.getCount() > 0 ){
                hasData = true;
            }
        }catch (Exception e) {
            e.printStackTrace();
            hasData = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return hasData;
    }

    /**
     * 删除tag_data标记的数据版本
     */
    public void deleteDataVersion(String tag_data){
        database.delete(YellowDataVersionTable.TABLE_NAME, YellowDataVersionTable.TAG_DATA + "=?"
                ,new String[]{tag_data});
    }

    /**
     * 更新tag_data标记的数据版本
     */
    public int updateDataVersion(String tag_data, int version){
        ContentValues values = new ContentValues();
        values.put(YellowDataVersionTable.TAG_DATA, tag_data);
        values.put(YellowDataVersionTable.VERSION_DATA, version);
        int count = database.update(YellowDataVersionTable.TABLE_NAME, values, null, null);
        return count;
    }

    /**
     * 获取tag_data标记的数据版本
     */
    public int queryDataVersion(String tag_data){
        int version = 0;
        Cursor cursor = database.query(YellowDataVersionTable.TABLE_NAME, null,
                YellowDataVersionTable.TAG_DATA + "=?", new String[] { tag_data}, null, null, null);
        if( cursor == null ){
            return version;
        }
        try{
            if(cursor.moveToNext()){
                version = cursor.getInt(cursor.getColumnIndex(YellowDataVersionTable.VERSION_DATA));
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return version;
    }

    public void insertSearchConfigCacheList(List<SearchStrategyBean> searchBeanList, String searchName){
        if( searchBeanList == null || searchBeanList.size() == 0 ){
            return;
        }
        database.beginTransaction();
        long twoDaysTime = 2 * 24 * 60 * 60 * 1000; // 数据的过期时间为2天
        for(SearchStrategyBean searchBean : searchBeanList){
            String service_name = searchBean.getService_name(); // 数据源
            if( hasSearchConfigCacheData(searchName, service_name)){
                // 已经存在该条数据
                continue;
            }
            searchBean.setOut_time(System.currentTimeMillis() + twoDaysTime);

            ContentValues values = new ContentValues();
            values.put(SearchConfigCacheTable.ENTRY_TYPE, searchBean.getEntry_type());
            values.put(SearchConfigCacheTable.WORDS, searchName);
            values.put(SearchConfigCacheTable.CATEGORY, searchBean.getCategory());
            values.put(SearchConfigCacheTable.CITY, searchBean.getCity());
            values.put(SearchConfigCacheTable.LONGITUDE, searchBean.getLongitude());
            values.put(SearchConfigCacheTable.LATITUDE, searchBean.getLatitude());
            values.put(SearchConfigCacheTable.PAGE, searchBean.getPage());
            values.put(SearchConfigCacheTable.LIMIT, searchBean.getLimit());
            values.put(SearchConfigCacheTable.SOURCE, searchBean.getSource());
            values.put(SearchConfigCacheTable.SERVICE_NAME, searchBean.getService_name());
            values.put(SearchConfigCacheTable.FACTORY, searchBean.getFactory());
            values.put(SearchConfigCacheTable.SORT, searchBean.getSort());
            values.put(SearchConfigCacheTable.ORDER_BY, searchBean.getOrderby());
            values.put(SearchConfigCacheTable.OUT_TIME, searchBean.getOut_time());
            database.insert(SearchConfigCacheTable.TABLE_NAME, null, values);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public List<SearchStrategyBean> querySearchConfigCache(String keyword){
        Cursor cursor = database.query(SearchConfigCacheTable.TABLE_NAME, null,
                SearchConfigCacheTable.WORDS + "=? " ,
                new String[] { keyword}, null, null, null);
        List<SearchStrategyBean> searchBeanList = fetchSearchConfigCache(cursor);
        return searchBeanList;
    }

    public void deleteSearchConfigCache(SearchStrategyBean searchBean){
        database.delete(SearchConfigCacheTable.TABLE_NAME, SearchConfigCacheTable.WORDS + "=? and "
                        + SearchConfigCacheTable.SERVICE_NAME + "=? ",
                new String[]{searchBean.getWords(), searchBean.getService_name()});
    }

    public boolean hasSearchConfigCacheData(String keyword, String service_name){
        boolean hasData = false;
        Cursor cursor = null;
        try{
            cursor = database.query(SearchConfigCacheTable.TABLE_NAME, null,
                    SearchConfigCacheTable.WORDS + "=? and " + SearchConfigCacheTable.SERVICE_NAME + "=?",
                    new String[] { keyword, service_name}, null, null, null);
            if( cursor != null && cursor.getCount() > 0){
                hasData = true;
            }
        }catch(Exception e){
            hasData = false;
        }finally{
            if( cursor != null ){
                cursor.close();
            }
        }
        return hasData;
    }

    private List<SearchStrategyBean> fetchSearchConfigCache(Cursor cursor) {
        /*
         * 增加 cursor关闭操作
         * modified by hyl 2014-12-20 start
         * old code:
         * if(cursor == null || cursor.getCount()==0){
                return null;
           }
         */
        if(cursor == null ){
            return null;
        }else if(cursor.getCount()==0){
            cursor.close();
            return null;
        }
        //modified by hyl 2014-12-20 end
        List<SearchStrategyBean> searchBeanList = new ArrayList<SearchStrategyBean>();
        List<SearchStrategyBean> outTimeList = new ArrayList<SearchStrategyBean>(); //过期数据
        try {
            while( !cursor.isLast() ) {
                cursor.moveToNext();
                int entryType = cursor.getInt(cursor
                        .getColumnIndex(SearchConfigCacheTable.ENTRY_TYPE));
                String words = cursor.getString(cursor
                        .getColumnIndex(SearchConfigCacheTable.WORDS));
                String category = cursor.getString(cursor
                        .getColumnIndex(SearchConfigCacheTable.CATEGORY));
                String city = cursor.getString(cursor
                        .getColumnIndex(SearchConfigCacheTable.CITY));
                String longitudeStr = cursor.getString(cursor
                        .getColumnIndex(SearchConfigCacheTable.LONGITUDE));
                String latitudeStr = cursor.getString(cursor
                        .getColumnIndex(SearchConfigCacheTable.LATITUDE));
                int page = cursor.getInt(cursor
                        .getColumnIndex(SearchConfigCacheTable.PAGE));
                int limit = cursor.getInt(cursor
                        .getColumnIndex(SearchConfigCacheTable.LIMIT));
                int source = cursor.getInt(cursor
                        .getColumnIndex(SearchConfigCacheTable.SOURCE));
                String service_name = cursor.getString(cursor
                        .getColumnIndex(SearchConfigCacheTable.SERVICE_NAME));
                String factory = cursor.getString(cursor
                        .getColumnIndex(SearchConfigCacheTable.FACTORY));
                int sort = cursor.getInt(cursor
                        .getColumnIndex(SearchConfigCacheTable.SORT));
                String orderBy = cursor.getString(cursor
                        .getColumnIndex(SearchConfigCacheTable.ORDER_BY));
                long outTime = cursor.getLong(cursor
                        .getColumnIndex(SearchConfigCacheTable.OUT_TIME));

                SearchStrategyBean bean = new SearchStrategyBean();
                bean.setEntry_type(entryType);
                bean.setWords(words);
                bean.setCategory(category);
                bean.setCity(city);
                float longitude = 0;
                if( !TextUtils.isEmpty(longitudeStr) ){
                    longitude = Float.valueOf(longitudeStr);
                }
                bean.setLongitude(longitude);
                float latitude = 0;
                if( !TextUtils.isEmpty(latitudeStr) ){
                    latitude = Float.valueOf(latitudeStr);
                }
                bean.setLatitude(latitude);
                bean.setPage(page);
                bean.setLimit(limit);
                bean.setSource(source);
                bean.setService_name(service_name);
                bean.setFactory(factory);
                bean.setSort(sort);
                bean.setOrderby(orderBy);
                bean.setOut_time(outTime);

                if( outTime < System.currentTimeMillis() && outTime > 0){
                    // 时间已过期，则清除数据
                    outTimeList.add(bean);
                }else{
                    searchBeanList.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if( outTimeList.size() > 0 ){
            // 删除过期数据
            for(SearchStrategyBean bean : outTimeList){
                deleteSearchConfigCache(bean);
            }
        }
        return searchBeanList;
    }

    public void insert(HabitDataItem habitDataItem) {
        ContentValues values = new ContentValues();
        values.put(HabitDataTable.SERVICE_ID, habitDataItem.getServiceId());
        values.put(HabitDataTable.SOURCE_TYPE, habitDataItem.getSource_type());
        values.put(HabitDataTable.CONTENT_TYPE, habitDataItem.getContent_type());
        values.put(HabitDataTable.CONTENT_DATA, habitDataItem.getContent_data());
        values.put(HabitDataTable.ISUPLOAD, habitDataItem.getIsupload());
        values.put(HabitDataTable.TIME, new Date().getTime()+"");

        database.insert(HabitDataTable.TABLE_NAME, null, values);
    }

    public boolean isExistHabitData(HabitDataItem habitDataItem){
        HabitDataItem collectList = queryUniqueHabitData(habitDataItem);
        if( collectList == null ){
            return false;
        }
        return true;
    }
    /**
     * 查询广告数据
     * @param categoryId
     * @return
     */
    public List<PushAdBean> queryAdDataById(int categoryId){
        List<PushAdBean> adList = new ArrayList<PushAdBean>();
        Cursor cursor = database.query(AdTable.TABLE_NAME, null,
                AdTable.AD_CODE + "=?", new String[] { String.valueOf(categoryId)}, null, null, null);
        try{
            while (cursor.moveToNext()) {
                int adCode = cursor.getInt(cursor.getColumnIndex(AdTable.AD_CODE));
                int adPageIndex = cursor.getInt(cursor.getColumnIndex(AdTable.AD_PAGE_INDEX));
                String imgUrlList = cursor.getString(cursor.getColumnIndex(AdTable.AD_IMG_URL));
                String clickType = cursor.getString(cursor.getColumnIndex(AdTable.AD_CLICK_TYPE));
                String clickActivity = cursor.getString(cursor.getColumnIndex(AdTable.AD_CLICK_ACTIVITY));
                String clickLink = cursor.getString(cursor.getColumnIndex(AdTable.AD_CLICK_LINK));
                String text = cursor.getString(cursor.getColumnIndex(AdTable.AD_TEXT));
                long adStartTime = cursor.getLong(cursor.getColumnIndex(AdTable.AD_START_TIME));
                long adEndTime = cursor.getLong(cursor.getColumnIndex(AdTable.AD_END_TIME));
                String params = cursor.getString(cursor.getColumnIndex(AdTable.AD_PARAMS));
                PushAdBean adBean = new PushAdBean();
                adBean.setAd_code(adCode);
                adBean.setAd_page_index(adPageIndex);
                adBean.setAd_img_url(imgUrlList);
                adBean.setAd_click_type(clickType);
                adBean.setAd_click_activity(clickActivity);
                adBean.setAd_click_link(clickLink);
                adBean.setAd_text(text);
                adBean.setAd_start_time(adStartTime);
                adBean.setAd_end_time(adEndTime);
                adBean.setAd_params_str(params);
                adList.add(adBean);
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return adList;
    }

    /**
     * 删除广告数据
     */
    public int deleteAdData(PushAdBean adBean){
        return deleteAdData(adBean.getAd_code(), adBean.getAd_page_index());
    }

    public int deleteAdData(int adCode, int adPageIndex){
        return database.delete(AdTable.TABLE_NAME, AdTable.AD_CODE + "=? and "
                        + AdTable.AD_PAGE_INDEX + "=? ",
                new String[]{String.valueOf(adCode), String.valueOf(adPageIndex)});
    }

    /*
     * 清除广告数据（将所有广告数据的imgUrl设为空）
     * add by hyl 2014-11-29
     */
    public int clearAllAdData(){
        ContentValues values = new ContentValues();
        values.putNull(AdTable.AD_IMG_URL);
        return database.update(AdTable.TABLE_NAME, values, null, null);
    }

    public HabitDataItem queryUniqueHabitData(HabitDataItem dataItem){
        String source_type = dataItem.getSource_type();
        String content_type = dataItem.getContent_type();
        String content_data = dataItem.getContent_data();

        String selection = HabitDataTable.SOURCE_TYPE + "=? and "
                + HabitDataTable.CONTENT_TYPE + "=? and " + HabitDataTable.CONTENT_DATA +"=? ";
        Cursor cursor = database.query(HabitDataTable.TABLE_NAME, null,
                selection, new String[] { source_type,content_type,content_data}, null, null, null);
        List<HabitDataItem> list = doParseHabitData(cursor);
        if(list!=null && list.size()!=0){
            return list.get(0);
        }else{
            return null;
        }
    }

    public List<HabitDataItem> queryHabitDataBySourceType(String source_type){
        String selection = HabitDataTable.SOURCE_TYPE + "=? "
                ;
        Cursor cursor = database.query(HabitDataTable.TABLE_NAME, null,
                selection, new String[] { source_type}, null, null, null);
        return doParseHabitData(cursor);
    }

    public List<HabitDataItem> queryContentTypeHabitData(String source_type, String content_type,boolean isASC){
        String order = isASC?" ASC":" DESC";
        String selection = HabitDataTable.SOURCE_TYPE + "=? and "
                + HabitDataTable.CONTENT_TYPE + "=? ";
        Cursor cursor = database.query(HabitDataTable.TABLE_NAME, null,
                selection, new String[] { source_type,content_type}, null, null, HabitDataTable.TIME + order);
        List<HabitDataItem> list = doParseHabitData(cursor);
        return list;
    }

    public List<HabitDataItem> queryFuzzyContentTypeHabitData(String source_type, String content_type,boolean isASC){
        String order = isASC?" ASC":" DESC";
        String selection = HabitDataTable.SOURCE_TYPE + "=? and "
                + HabitDataTable.CONTENT_TYPE + " like ? ";
        Cursor cursor = database.query(HabitDataTable.TABLE_NAME, null,
                selection, new String[] { source_type,"%" + content_type + "%"}, null, null, HabitDataTable.TIME + order);
        List<HabitDataItem> list = doParseHabitData(cursor);
        return list;
    }

    public List<HabitDataItem> queryAllLocalHabitData(){
        String selection = HabitDataTable.ISUPLOAD + "=? " ;
        Cursor cursor = database.query(HabitDataTable.TABLE_NAME, null,
                selection, new String[] { HabitDataItem.LOCAL}, null, null, null);
        return doParseHabitData(cursor);
    }

    public List<HabitDataItem> queryAllHabitData(){
        Cursor cursor = database.query(HabitDataTable.TABLE_NAME, null,
                null, null, null, null, null);
        return doParseHabitData(cursor);
    }

    private List<HabitDataItem> doParseHabitData(Cursor cursor){
        if( cursor == null ){
            return null;
        }
        List<HabitDataItem> dataList = new ArrayList<HabitDataItem>();
        try{
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(HabitDataTable.SERVICE_ID));
                String source_type = cursor.getString(cursor.getColumnIndex(HabitDataTable.SOURCE_TYPE));
                String content_type = cursor.getString(cursor.getColumnIndex(HabitDataTable.CONTENT_TYPE));
                String content = cursor.getString(cursor.getColumnIndex(HabitDataTable.CONTENT_DATA));
                int isupload = cursor.getInt(cursor.getColumnIndex(HabitDataTable.ISUPLOAD));


                HabitDataItem dataItem = new HabitDataItem();
                dataItem.setServiceId(id);
                dataItem.setSource_type(source_type);
                dataItem.setContent_type(content_type);
                dataItem.setContent_data(content);
                dataItem.setIsupload(isupload);
                dataList.add(dataItem);
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return dataList;
    }


    public void updateSameHabitDataTime(HabitDataItem habitDataItem) {
        long id = habitDataItem.getServiceId();
        String source_type = habitDataItem.getSource_type();
        String content_type = habitDataItem.getContent_type();
        String content = habitDataItem.getContent_data();
        ContentValues cv = new ContentValues();
        String time = new Date().getTime()+"";
        if(id != 0){
            cv.put(HabitDataTable.SERVICE_ID, id);
        }
        cv.put(HabitDataTable.TIME, time);
        cv.put(HabitDataTable.ISUPLOAD, habitDataItem.getIsupload());
        database.update(HabitDataTable.TABLE_NAME, cv,
                HabitDataTable.SOURCE_TYPE + "=? and " +
                        HabitDataTable.CONTENT_TYPE + "=? and "+
                        HabitDataTable.CONTENT_DATA + "=? ",
                new String[] { source_type,content_type,content});
    }

    public void markAllHabitDataUpload() {
        ContentValues cv = new ContentValues();
        cv.put(HabitDataTable.ISUPLOAD, HabitDataItem.UPlOAD);
        database.update(HabitDataTable.TABLE_NAME, cv,
                null,null);
    }

    public void cleanHabitData(){
        database.delete(YellowPageDB.HabitDataTable.TABLE_NAME, null, null);
        deleteDataVersion(YELLOW_DATA_VERSION_HABIT);
    }

    //----add by ljq 2014-9-22--------------------------------------------

    public void inseartCollectData(YellowPageCollectData collectData){
        ContentValues values = new ContentValues();
        values.put(CollectTable.COLLECT_ID, collectData.getItemId());
        values.put(CollectTable.COLLECT_DATA_TYPE, collectData.getDataType());
        values.put(CollectTable.COLLECT_NAME, collectData.getName());
        values.put(CollectTable.COLLECT_TYPE, collectData.getType());
        values.put(CollectTable.COLLECT_CONTENT, collectData.getContent());
        values.put(CollectTable.COLLECT_TIME, collectData.getTime());
        database.insert(CollectTable.TABLE_NAME, null, values);

        // 限制历史记录为20条，杨弘修改于2014-08-16
        if (collectData.getDataType() == ConstantsParameter.YELLOWPAGE_DATA_TYPE_HISTORY) {
            List<YellowPageCollectData> allHistoryList = queryAllMyData(ConstantsParameter.YELLOWPAGE_DATA_TYPE_HISTORY);

            if(null == allHistoryList || allHistoryList.size() <= 20){
                return;
            }

            String sql = "DELETE FROM " + CollectTable.TABLE_NAME + " WHERE "
                    + CollectTable.COLLECT_DATA_TYPE + " = " + ConstantsParameter.YELLOWPAGE_DATA_TYPE_HISTORY
                    + " AND " + CollectTable.COLLECT_TIME + " NOT IN "
                    + "(SELECT " + CollectTable.COLLECT_TIME + " FROM " + CollectTable.TABLE_NAME + " WHERE "
                    + CollectTable.COLLECT_DATA_TYPE + " = " + ConstantsParameter.YELLOWPAGE_DATA_TYPE_HISTORY
                    + " ORDER BY " + CollectTable.COLLECT_TIME + " DESC LIMIT 20);";

            try {
                database.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isExistCollectData(long itemId,int dataType, int type, String name){
        List<YellowPageCollectData> collectList = queryCollectData(itemId, dataType, type, name);
        if( collectList == null || collectList.size() == 0 ){
            return false;
        }
        return true;
    }

    public YellowPageCollectData queryCollectedData(long itemId, int type, String name){
        List<YellowPageCollectData> collectList  = queryCollectData(itemId, type, name);
        if( collectList == null || collectList.size() == 0 ){
            return null;
        }
        return collectList.get(0);
    }

    public List<YellowPageCollectData> queryCollectData(long itemId, int dataType, int type, String name){
        String selection = CollectTable.COLLECT_ID + "=? and "
                + CollectTable.COLLECT_DATA_TYPE + "=? and "
                + CollectTable.COLLECT_TYPE + "=? and "
                + CollectTable.COLLECT_NAME + "=? ";
        Cursor cursor = database.query(CollectTable.TABLE_NAME, null,
                selection, new String[] { String.valueOf(itemId), String.valueOf(dataType), String.valueOf(type), name }, null, null, null);
        return doParseCollectData(cursor);
    }

    public List<YellowPageCollectData> queryCollectData(long itemId, int type, String name){
        String selection = CollectTable.COLLECT_ID + "=? and "
                + CollectTable.COLLECT_TYPE + "=? and "
                + CollectTable.COLLECT_NAME + "=? ";
        Cursor cursor = database.query(CollectTable.TABLE_NAME, null,
                selection, new String[] { String.valueOf(itemId), String.valueOf(type), name }, null, null, null);
        return doParseCollectData(cursor);
    }

    public List<YellowPageCollectData> queryAllMyData(int dataType){
        //ConstantsParameter.YELLOWPAGE_MY_TYPE_FAVORITE
        Cursor cursor = database.query(CollectTable.TABLE_NAME, null,
                CollectTable.COLLECT_DATA_TYPE + "=? ",
                new String[] { String.valueOf(dataType) }, null, null,
                CollectTable.COLLECT_TIME + " DESC");
        return doParseCollectData(cursor);
    }

    public void updateYellowPageCollerViewTime(long itemId, int type, String name, long time) {
        ContentValues cv = new ContentValues();
        cv.put(CollectTable.COLLECT_TIME, time);
        database.update(CollectTable.TABLE_NAME, cv,
                CollectTable.COLLECT_ID + "=? and " +
                        CollectTable.COLLECT_TYPE + "=? and " +
                        CollectTable.COLLECT_NAME + "=? ",
                new String[] { String.valueOf(itemId),String.valueOf(type), name});
    }

    public void updateYellowPageCollerViewTime(long itemId, int dataType, int type, String name, long time) {
        ContentValues cv = new ContentValues();
        cv.put(CollectTable.COLLECT_DATA_TYPE, dataType);
        cv.put(CollectTable.COLLECT_TIME, time);
        database.update(CollectTable.TABLE_NAME, cv,
                CollectTable.COLLECT_ID + "=? and " +
                        CollectTable.COLLECT_TYPE + "=? and " +
                        CollectTable.COLLECT_NAME + "=? ",
                new String[] { String.valueOf(itemId),String.valueOf(type), name});
    }

    private List<YellowPageCollectData> doParseCollectData(Cursor cursor){
        if( cursor == null ){
            return null;
        }
        List<YellowPageCollectData> collectList = new ArrayList<YellowPageCollectData>();
        try{
            while (cursor.moveToNext()) {
                int itemId = cursor.getInt(cursor.getColumnIndex(CollectTable.COLLECT_ID));
                int dataType = cursor.getInt(cursor.getColumnIndex(CollectTable.COLLECT_DATA_TYPE));
                String name = cursor.getString(cursor.getColumnIndex(CollectTable.COLLECT_NAME));
                int type = cursor.getInt(cursor.getColumnIndex(CollectTable.COLLECT_TYPE));
                String content = cursor.getString(cursor.getColumnIndex(CollectTable.COLLECT_CONTENT));

                YellowPageCollectData collectData = new YellowPageCollectData();
                collectData.setItemId(itemId);
                collectData.setDataType(dataType);
                collectData.setName(name);
                collectData.setType(type);
                collectData.setContent(content);
                collectList.add(collectData);
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return collectList;
    }

    public void delCollectData(long itemId, int type, String name){
        database.delete(CollectTable.TABLE_NAME, CollectTable.COLLECT_ID + "=? and "
                        + CollectTable.COLLECT_TYPE + "=? and "
                        + CollectTable.COLLECT_NAME + "=? ",
                new String[]{String.valueOf(itemId), String.valueOf(type), name});
    }

    public void clearTable(String tableName){
        database.delete(tableName, null, null);
    }

    public void insert(CategoryBean category) {
        ContentValues values = new ContentValues();
        values.put(CategoryTable.CATEGORY_ID, category.getCategory_id());
        values.put(CategoryTable.NAME, category.getName());
        values.put(CategoryTable.SHOWNAME, category.getShow_name());
        values.put(CategoryTable.PARENT_ID, category.getParent_id());
        values.put(CategoryTable.ICON, category.getIcon());
        values.put(CategoryTable.ICON_LOGO, category.getIconLogo());
        values.put(CategoryTable.SORT, category.getSort());
        values.put(CategoryTable.LAST_SORT, category.getLastSort());
        values.put(CategoryTable.TARGET_ACTIVITY, category.getTarget_activity());
        values.put(CategoryTable.TARGET_PARAMS, category.getTarget_params());
        values.put(CategoryTable.PRESS_ICON, category.getPressIcon());
        values.put(CategoryTable.EDITTYPE, category.getEditType());
        values.put(CategoryTable.REMIND_CODE, category.getRemind_code());
        values.put(CategoryTable.CHANGE_TYPE, category.getChange_type());
        values.put(CategoryTable.EXPAND_PARAM, category.getExpand_param());
        values.put(CategoryTable.KEY_TAG, category.getKey_tag());
        values.put(CategoryTable.SEARCH_SORT, category.getSearch_sort());
        database.insert(CategoryTable.TABLE_NAME, null, values);
    }

    /**
     * 插入数据且自动调整整个库的SORT和LAST_SORT的顺序
     * @param category
     */
    public void insertAndSort(CategoryBean category) {
        if(queryCategoryByCategoryId(category.getCategory_id()) != null){
            return;
        }
        //取得整个category集 除了“编辑快捷服务”
        List<CategoryBean> categorys = queryCategoryByParentId(YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN, false);
        categorys.addAll(queryCategoryByParentId(YellowUtil.YELLOW_PAGE_CATEGORY_ID_ALL, false));

        //将后序CategoryBean 的 sort_id和last_sort_id 向后移一位
        for (int i = 0; i < categorys.size(); i++) {
            CategoryBean tmp_category = categorys.get(i);
            String targetActivity = tmp_category.getTarget_activity();
            //“我的”就不用调序了
            if (MyCenterConstant.MY_NODE.equals(targetActivity)) {
                continue;
            }
            ContentValues value_sort = new ContentValues();
            List<CategoryBean> existSortList = queryCategoryBySortId(category.getSort());
            //ID不存在则不用排序
            if( existSortList != null ){
                int tmp_sort_id = tmp_category.getSort();
                if (tmp_sort_id >= category.getSort()) {
                    value_sort.put(CategoryTable.SORT, tmp_sort_id + 1);
                }
            }
            List<CategoryBean> existLastSortList = queryCategoryByLastSortId(category.getLastSort());
            //ID不存在则不用排序了
            if( existLastSortList != null ){
                int tmp_last_sort_id = tmp_category.getLastSort();
                if (tmp_last_sort_id >= category.getLastSort()) {
                    value_sort.put(CategoryTable.LAST_SORT, tmp_last_sort_id + 1);

                }
                if(value_sort.size() != 0){
                    database.update(CategoryTable.TABLE_NAME, value_sort,
                            CategoryTable.CATEGORY_ID + "=" + tmp_category.getCategory_id(), null);
                }
            }
        }

        insert(category);

    }

    /**
     * 更新CategoryBean
     * @param categoryBean
     * add by hyl 2014-8-7
     */
    public int update(CategoryBean category){
        ContentValues values = new ContentValues();
        values.put(CategoryTable.NAME, category.getName());
        values.put(CategoryTable.SHOWNAME, category.getShow_name());
        values.put(CategoryTable.PARENT_ID, category.getParent_id());
        values.put(CategoryTable.ICON, category.getIcon());
        values.put(CategoryTable.ICON_LOGO, category.getIconLogo());
        values.put(CategoryTable.SORT, category.getSort());
        values.put(CategoryTable.LAST_SORT, category.getLastSort());
        values.put(CategoryTable.TARGET_ACTIVITY, category.getTarget_activity());
        values.put(CategoryTable.TARGET_PARAMS, category.getTarget_params());
        values.put(CategoryTable.PRESS_ICON, category.getPressIcon());
        values.put(CategoryTable.EDITTYPE, category.getEditType());
        values.put(CategoryTable.CHANGE_TYPE, category.getChange_type());
        values.put(CategoryTable.KEY_TAG, category.getKey_tag());
        values.put(CategoryTable.SEARCH_SORT, category.getSearch_sort());

        int count = database.update(CategoryTable.TABLE_NAME, values,
                CategoryTable.CATEGORY_ID + "=" + category.getCategory_id(), null);
        return count;
    }

    /**
     * 更新数据且自动调整整个库的SORT和LAST_SORT的顺序
     * @param category
     */
    public void updateAndSort(CategoryBean category) {
        //不存在退出
        if(queryCategoryByCategoryId(category.getCategory_id()) == null){
            return;
        }
        //取得即将更新的category
        CategoryBean local_category = queryCategoryByCategoryId(category.getCategory_id());
        int local_category_sort_id = local_category.getSort();
        int local_category_last_sort_id = local_category.getLastSort();

        //取得整个category集 除了“编辑快捷服务”
        List<CategoryBean> categorys = queryCategoryByParentId(
                YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN, false);
        categorys.addAll(queryCategoryByParentId(YellowUtil.YELLOW_PAGE_CATEGORY_ID_ALL, false));

        for (int i = 0; i < categorys.size(); i++) {
            CategoryBean tmp_category = categorys.get(i);
            String targetActivity = tmp_category.getTarget_activity();
            //自身和“我的”就不用调序了
            if (tmp_category.getCategory_id() == local_category.getCategory_id() &&  MyCenterConstant.MY_NODE.equals(targetActivity)) {
                continue;
            }
            ContentValues value_sort = new ContentValues();
            List<CategoryBean> existSortList = queryCategoryBySortId(category.getSort());
            //ID不存在则不用排序了
            if( existSortList != null ){
                int tmp_sort_id = tmp_category.getSort();
                //从低调到高的情况
                if (category.getSort() > local_category_sort_id) {
                    if (tmp_sort_id <= category.getSort() && tmp_sort_id > local_category_sort_id) {
                        value_sort.put(CategoryTable.SORT, tmp_sort_id - 1);
                    }
                } else if (category.getSort() < local_category_sort_id) {//从高到低的情况
                    if (tmp_sort_id >= category.getSort() && tmp_sort_id < local_category_sort_id) {
                        value_sort.put(CategoryTable.SORT, tmp_sort_id + 1);
                    }
                }
            }
            List<CategoryBean> existLastSortList = queryCategoryByLastSortId(category.getLastSort());
            //ID不存在则不用排序了
            if( existLastSortList != null ){
                //再操作一次last_sort
                int tmp_last_sort_id = tmp_category.getLastSort();
                //从低调到高的情况
                if (category.getLastSort() > local_category_last_sort_id) {
                    if (tmp_last_sort_id <= category.getLastSort() && tmp_last_sort_id > local_category_last_sort_id) {
                        value_sort.put(CategoryTable.LAST_SORT, tmp_last_sort_id - 1);
                    }
                } else if (category.getLastSort() < local_category_last_sort_id) {//从高到低的情况
                    if (tmp_last_sort_id >= category.getLastSort() && tmp_last_sort_id < local_category_last_sort_id) {
                        value_sort.put(CategoryTable.LAST_SORT, tmp_last_sort_id + 1);
                    }
                }
                if(value_sort.size() != 0){
                    database.update(CategoryTable.TABLE_NAME, value_sort,
                            CategoryTable.CATEGORY_ID + "=" + tmp_category.getCategory_id(), null);
                }
            }
        }

        update(category);

    }

    /**
     * 删除数据且自动调整整个库的SORT和LAST_SORT的顺序
     * @param category
     */
    public void deleteAndSort(CategoryBean category) {
        //不存在退出
        if(queryCategoryByCategoryId(category.getCategory_id()) == null){
            return;
        }
        //取得即将删除的category
        CategoryBean local_category = queryCategoryByCategoryId(category.getCategory_id());
        int local_sort_id = local_category.getSort();
        int local_last_sort_id = local_category.getLastSort();
        //取得整个category集 除了“编辑快捷服务”
        List<CategoryBean> categorys = queryCategoryByParentId(YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN, false);
        categorys.addAll(queryCategoryByParentId(YellowUtil.YELLOW_PAGE_CATEGORY_ID_ALL, false));
        //将后序CategoryBean 的 sort_id和last_sort_id 向前移一位
        for (int i = 0; i < categorys.size(); i++) {
            CategoryBean tmp_category = categorys.get(i);
            String targetActivity = tmp_category.getTarget_activity();
            //自身和“我的”就不用调序了
            if (tmp_category.getCategory_id() == local_category.getCategory_id() &&  MyCenterConstant.MY_NODE.equals(targetActivity)) {
                continue;
            }
            ContentValues value_sort = new ContentValues();
            int tmp_sort_id = tmp_category.getSort();
            if (tmp_sort_id > local_sort_id) {
                value_sort.put(CategoryTable.SORT, tmp_sort_id - 1);

            }
            int tmp_last_sort_id = tmp_category.getLastSort();
            if (tmp_last_sort_id > local_last_sort_id) {
                value_sort.put(CategoryTable.SORT, tmp_last_sort_id - 1);
            }
            if(value_sort.size() != 0){
                database.update(CategoryTable.TABLE_NAME, value_sort,
                        CategoryTable.CATEGORY_ID + "=" + tmp_category.getCategory_id(), null);
            }
        }
        delete(category) ;

    }

    /**
     * 删除数据
     * @param category
     * @return
     * add by hyl 2014-8-7
     */
    public int delete(CategoryBean category){
        int count = database.delete(CategoryTable.TABLE_NAME,
                CategoryTable.CATEGORY_ID + "=" + category.getCategory_id(), null);
        return count;
    }

    public int delete(int parentId){
        int count = database.delete(CategoryTable.TABLE_NAME,
                CategoryTable.PARENT_ID + "=" + parentId, null);
        return count;
    }

    public void insertBatchCategory(List<CategoryBean> categoryList) {
        database.beginTransaction();
        insertCategoryList(categoryList);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private void insertCategoryList(List<CategoryBean> categoryList){
        for (CategoryBean category : categoryList) {
            ContentValues values = new ContentValues();
            values.put(CategoryTable.CATEGORY_ID, category.getCategory_id());
            values.put(CategoryTable.NAME, category.getName());
            values.put(CategoryTable.SHOWNAME, category.getShow_name());
            values.put(CategoryTable.PARENT_ID, category.getParent_id());
            values.put(CategoryTable.ICON, category.getIcon());
            values.put(CategoryTable.ICON_LOGO, category.getIconLogo());
            values.put(CategoryTable.SORT, category.getSort());
            values.put(CategoryTable.LAST_SORT, category.getLastSort());
            values.put(CategoryTable.TARGET_ACTIVITY, category.getTarget_activity());
            values.put(CategoryTable.TARGET_PARAMS, category.getTarget_params());
            values.put(CategoryTable.PRESS_ICON, category.getPressIcon());
            values.put(CategoryTable.REMIND_CODE, category.getRemind_code());
            values.put(CategoryTable.EDITTYPE, category.getEditType());
            values.put(CategoryTable.CHANGE_TYPE, category.getChange_type());
            values.put(CategoryTable.EXPAND_PARAM, category.getExpand_param());
            values.put(CategoryTable.KEY_TAG, category.getKey_tag());
            values.put(CategoryTable.SEARCH_SORT, category.getSearch_sort());
            database.insert(CategoryTable.TABLE_NAME, null, values);
        }
    }

    public void insertAdList(List<PushAdBean> adList){
        database.beginTransaction();
        insertAdData(adList);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private void insertAdData(List<PushAdBean> adList){
        for(PushAdBean adBean : adList ){
            int adCode = adBean.getAd_code();
            int adPageIndex = adBean.getAd_page_index();
            deleteAdData(adCode, adPageIndex);
            ContentValues values = new ContentValues();
            values.put(AdTable.AD_CODE, adCode);
            values.put(AdTable.AD_PAGE_INDEX, adPageIndex);
            values.put(AdTable.AD_IMG_URL, adBean.getAd_img_url());
            values.put(AdTable.AD_CLICK_TYPE, adBean.getAd_click_type());
            values.put(AdTable.AD_CLICK_ACTIVITY, adBean.getAd_click_activity());
            values.put(AdTable.AD_CLICK_LINK, adBean.getAd_click_link());
            values.put(AdTable.AD_TEXT, adBean.getAd_text());
            values.put(AdTable.AD_START_TIME, adBean.getAd_start_time());
            values.put(AdTable.AD_END_TIME, adBean.getAd_end_time());
            values.put(AdTable.AD_PARAMS, adBean.getAd_params_str());
            database.insert(AdTable.TABLE_NAME, null, values);
        }
    }

    public int getCategoryCountByCid(long categoryId){
        Cursor cursor = database.query(CategoryTable.TABLE_NAME, null,
                CategoryTable.CATEGORY_ID + "=?",
                new String[] { String.valueOf(categoryId) }, null,
                null, null);
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    /**
     * 判断parent_id的类别是否存在
     * @param parent_category_id
     * @return
     */
    public int getCategoryCount(long parent_category_id) {
        Cursor cursor = database.query(CategoryTable.TABLE_NAME, null,
                CategoryTable.PARENT_ID + "=?",
                new String[] { String.valueOf(parent_category_id) }, null,
                null, null);
		/*
         * 增加 cursor关闭操作
         * modified by hyl 2014-12-20 start
         * old code:
         *  int count = cursor.getCount();
            if (cursor != null) {
                cursor.close();
            }
            return count;
         */
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
        //modified by hyl 2014-12-20 end

    }

    /**
     * 获取首页 数据
     * @param parent_category_id
     * @return
     */
    public List<CategoryBean> queryCategoryByParentId(long parent_category_id) {
        return queryCategoryByParentId(parent_category_id, true);
    }

    /**
     * 获取首页 数据
     * @param parent_category_ids
     * @param needAddServerManager 是否需要添加"编辑快捷服务" 项
     * @return
     */
    public List<CategoryBean> queryCategoryByParentId(long parent_category_id, boolean needAddServerManager){
        String orderby = " sort asc";
        if(parent_category_id == YellowUtil.YELLOW_PAGE_CATEGORY_ID_ALL){
            orderby = " last_sort asc";
        }else if(parent_category_id == YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN){
            orderby = " sort asc";
        }
        Cursor cursor = database.query(CategoryTable.TABLE_NAME, null,
                CategoryTable.PARENT_ID + "=?",
                new String[] { String.valueOf(parent_category_id) }, null,
                null, orderby);
        List<CategoryBean> categoryList = fetchCategoryData(cursor);

        if( parent_category_id == YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN && needAddServerManager ){
            if( categoryList == null ){
                categoryList = new ArrayList<CategoryBean>();
            }
            categoryList.add(createServerManagerBean());
        }
        if( categoryList == null ){
            categoryList = new ArrayList<CategoryBean>();
        }
        return categoryList;
    }

    /**
     * 根据关键字查询葡萄静态详情数据
     * 注意：查询优先级：后台标签、名称、号码
     */
    public List<ItemBean> queryPutaoDetailByKey(String keyword, int maxNum){
        String orderby = CategoryTable.SEARCH_SORT +" asc";
        List<ItemBean> categoryList = new ArrayList<ItemBean>();
        Cursor cursor = database.query(ItemTable.TABLE_NAME, null,
                CategoryTable.KEY_TAG + " like ?",
                new String[] { String.valueOf("%" + keyword + "%") }, null,
                null, orderby);
        try {
            if (cursor == null || cursor.getCount() == 0){
                return null;
            }
            int num = 0;
            while (cursor.moveToNext()) {
                String target_activity = cursor.getString(cursor.getColumnIndex(ItemTable.TARGET_ACTIVITY));
                String content = cursor.getString(cursor.getColumnIndex(ItemTable.CONTENT));
                String target_params = cursor.getString(cursor.getColumnIndex(ItemTable.TARGET_PARAMS));
                long item_id = cursor.getLong(cursor.getColumnIndex(ItemTable.ITEM_ID));
                int remind_code = cursor.getInt(cursor.getColumnIndex(ItemTable.REMIND_CODE));
                String photoUrl = cursor.getString(cursor.getColumnIndex(ItemTable.ICON));
                String key_tag = cursor.getString(cursor.getColumnIndex(ItemTable.KEY_TAG));
                int search_sort = cursor.getInt(cursor.getColumnIndex(ItemTable.SEARCH_SORT));
                ItemBean itemBean = new ItemBean();
                itemBean.setContent(content);
                itemBean.setIcon(photoUrl);
                itemBean.setItem_id(item_id);
                itemBean.setRemind_code(remind_code);
                itemBean.setKey_tag(key_tag);
                itemBean.setSearch_sort(search_sort);
                itemBean.setTarget_params(target_params);
                if( YellowPageSearchActivity.class.getName().equals(target_activity) ){
                    // 如果是“附近的XX”，则过滤掉
                    continue;
                }
                itemBean.setTarget_activity(target_activity);

                categoryList.add(itemBean);
                if( maxNum > 0 && ++num >= maxNum ){
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return categoryList;
    }

    /**
     * 根据关键字查询葡萄静态应用数据
     * 注意：查询优先级：后台标签、名称、号码
     */
    public List<ItemBean> queryPutaoServerByKey(String keyword, int maxNum){
        String orderby = CategoryTable.SEARCH_SORT +" asc";
        List<ItemBean> categoryList = new ArrayList<ItemBean>();
        Cursor cursor = database.query(CategoryTable.TABLE_NAME, null,
                CategoryTable.KEY_TAG + " like ? and " + CategoryTable.EDITTYPE + " =?",
                new String[] { String.valueOf("%" + keyword + "%"), String.valueOf(YellowUtil.YELLOW_CATEGORY_EDITTYPE_DEFAULT) }, null,
                null, orderby);
        try {
            if (cursor == null || cursor.getCount() == 0){
                return null;
            }
            int num = 0;
            while (cursor.moveToNext()) {
                String photoUrl = cursor.getString(cursor.getColumnIndex(CategoryTable.ICON));
                if( TextUtils.isEmpty(photoUrl) ){
                    // 应用服务 图片为空，说明不是服务入口
                    continue;
                }
                String targetParam = cursor.getString(cursor.getColumnIndex(CategoryTable.TARGET_PARAMS));
                String name = cursor.getString(cursor.getColumnIndex(CategoryTable.NAME));
                String target_activity = cursor.getString(cursor.getColumnIndex(CategoryTable.TARGET_ACTIVITY));
                long categoryId = cursor.getLong(cursor.getColumnIndex(CategoryTable.CATEGORY_ID));
                int remind_code = cursor.getInt(cursor.getColumnIndex(CategoryTable.REMIND_CODE));
                String key_tag = cursor.getString(cursor.getColumnIndex(CategoryTable.KEY_TAG));
                int search_sort = cursor.getInt(cursor.getColumnIndex(ItemTable.SEARCH_SORT));

                ItemBean itemBean = new ItemBean();
                itemBean.setName(name);
                itemBean.setIcon(photoUrl);
                itemBean.setContent(targetParam);
                itemBean.setTarget_activity(target_activity);
                itemBean.setCategory_id(categoryId);
                itemBean.setRemind_code(remind_code);
                itemBean.setKey_tag(key_tag);
                itemBean.setSearch_sort(search_sort);

                categoryList.add(itemBean);
                if( maxNum > 0 && ++num >= maxNum ){
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return categoryList;
    }

    public CategoryBean queryCategoryByCategoryId(long category_id) {
        String orderby = "sort desc";
        Cursor cursor = database.query(CategoryTable.TABLE_NAME, null,
                CategoryTable.CATEGORY_ID + "=?",
                new String[] { String.valueOf(category_id) }, null, null, orderby);

        List<CategoryBean> list = fetchCategoryData(cursor);
        if(list != null && list.size() > 0)
            return list.get(0);

        return null;
    }

    public List<CategoryBean> queryCategoryBySortId(long sortid) {
        String orderby = "sort desc";
        Cursor cursor = database.query(CategoryTable.TABLE_NAME, null, CategoryTable.SORT
                + "=?", new String[] {
                String.valueOf(sortid)
        }, null, null, orderby);

        List<CategoryBean> list = fetchCategoryData(cursor);

        return list;
    }

    public List<CategoryBean> queryCategoryByLastSortId(long last_sortid) {
        String orderby = "last_sort desc";
        Cursor cursor = database.query(CategoryTable.TABLE_NAME, null, CategoryTable.LAST_SORT
                + "=?", new String[] {
                String.valueOf(last_sortid)
        }, null, null, orderby);

        List<CategoryBean> list = fetchCategoryData(cursor);

        return list;
    }

    // 按照remindCode来查询出对应的Category结构
    public CategoryBean queryCategoryByRemindCode(int remindCode) {
        String orderby = "sort desc";
        Cursor cursor = database.query(CategoryTable.TABLE_NAME, null,
                CategoryTable.REMIND_CODE + "=?",
                new String[] { String.valueOf(remindCode) }, null, null, orderby);
        List<CategoryBean> list = fetchCategoryData(cursor);
        if(list != null && list.size() > 0)
            return list.get(0);

        return null;
    }

    private List<CategoryBean> fetchCategoryData(Cursor cursor) {
        List<CategoryBean> category_list = null;
        try {
            if (cursor == null || cursor.getCount()==0)
                return null;

            category_list = new ArrayList<CategoryBean>();
            while (cursor.moveToNext()) {
                long cid = cursor.getLong(cursor
                        .getColumnIndex(CategoryTable.CATEGORY_ID));
                String name = cursor.getString(cursor
                        .getColumnIndex(CategoryTable.NAME));
                String show_name = cursor.getString(cursor
                        .getColumnIndex(CategoryTable.SHOWNAME));
                long parent_id = cursor.getLong(cursor
                        .getColumnIndex(CategoryTable.PARENT_ID));
                String icon = cursor.getString(cursor
                        .getColumnIndex(CategoryTable.ICON));
                String iconLogo = cursor.getString(cursor
                        .getColumnIndex(CategoryTable.ICON_LOGO));
                int sort = cursor.getInt(cursor
                        .getColumnIndex(CategoryTable.SORT));
                int lastSort = cursor.getInt(cursor
                        .getColumnIndex(CategoryTable.LAST_SORT));
                String targetActivity = cursor.getString(cursor
                        .getColumnIndex(CategoryTable.TARGET_ACTIVITY));
                String targetParams = cursor.getString(cursor
                        .getColumnIndex(CategoryTable.TARGET_PARAMS));
                String pressIcon = cursor.getString(cursor
                        .getColumnIndex(CategoryTable.PRESS_ICON));
                String edittype = cursor.getString(cursor
                        .getColumnIndex(CategoryTable.EDITTYPE));
                int remindCode = cursor.getInt(cursor
                        .getColumnIndex(CategoryTable.REMIND_CODE));
                int change_type = cursor.getInt(cursor
                        .getColumnIndex(CategoryTable.CHANGE_TYPE));
                String expand_param = cursor.getString(cursor
                        .getColumnIndex(CategoryTable.EXPAND_PARAM));
                String key_tag = cursor.getString(cursor
                        .getColumnIndex(CategoryTable.KEY_TAG));
                int search_sort = cursor.getInt(cursor
                        .getColumnIndex(CategoryTable.SEARCH_SORT));

                CategoryBean cb = new CategoryBean();
                cb.setCategory_id(cid);
                cb.setName(name);
                cb.setParent_id(parent_id);
                cb.setShow_name(show_name);
                cb.setIcon(icon);
                cb.setIconLogo(iconLogo);
                cb.setSort(sort);
                cb.setLastSort(lastSort);
                cb.setTarget_activity(targetActivity);
                cb.setTarget_params(targetParams);
                cb.setPressIcon(pressIcon);
                cb.setRemind_code(remindCode);
                cb.setChange_type(change_type);
                cb.setExpand_param(expand_param);
                cb.setKey_tag(key_tag);
                cb.setSearch_sort(search_sort);
                /**
                 * start modify by zjh at 2014-09-13
                 * 在编辑快捷服务中, "我的" 在"常用模块"中是不可删除（editType=1)
                 * 注：后续可考虑在数据库字段中添加editType字段，方便运营
                 */
                if( MyCenterConstant.MY_NODE.equals(targetActivity) ){
                    cb.setEditType(YellowUtil.YELLOW_CATEGORY_EDITTYPE_NOT_DEL);
                }else{
                    if(edittype != null && edittype.length()>0){
                        cb.setEditType(Integer.valueOf(edittype));
                    }else{
                        cb.setEditType(0);
                    }
                }
                /**
                 * end modify by zjh at 2014-09-13
                 */

                category_list.add(cb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return category_list;
    }

    /**
     * modify by zjh at 2014-09-12
     * 添加 "编辑快捷服务" 项
     * 注意：放在"常用"最后一个
     */
    private CategoryBean createServerManagerBean(){
        CategoryBean categoryBean = new CategoryBean();
        categoryBean.setCategory_id(RemindConfig.AddService);
        categoryBean.setName("添加");
        categoryBean.setShow_name("zh_CN:添加;en_US:Add;zh_TW:添加");
        categoryBean.setSort(100000);
        categoryBean.setLastSort(YellowUtil.YELLOW_CATEGORY_DEFAULT_LASTSORT);
        categoryBean.setIcon("putao_icon_quick_add");
        categoryBean.setPressIcon("putao_icon_quick_add_p");
        categoryBean.setTarget_activity("so.contacts.hub.ui.yellowpage.YellowPageServerManagerActivity");
        categoryBean.setTarget_params("");
        categoryBean.setParent_id(YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN); //属于常用
        categoryBean.setRemind_code(YellowUtil.YELLOW_CATEGORY_DEFAULT_REMIND_CODE);
        categoryBean.setKey_tag("");
        return categoryBean;
    }

    public void insert(ItemBean item) {
        ContentValues values = new ContentValues();
        values.put(ItemTable.ITEM_ID, item.getItem_id());
        values.put(ItemTable.CATEGORY_ID, item.getCategory_id());
        values.put(ItemTable.PROVIDER, item.getProvider());
        values.put(ItemTable.NAME, item.getName());
        values.put(ItemTable.DESCRIPTION, item.getDescription());
        values.put(ItemTable.ICON, item.getIcon());
        values.put(ItemTable.SORT, item.getSort());
        values.put(ItemTable.CONTENT, item.getContent());
        values.put(ItemTable.TARGET_ACTIVITY, item.getTarget_activity());
        values.put(ItemTable.TARGET_PARAMS, item.getTarget_params());
        values.put(ItemTable.REMIND_CODE, item.getRemind_code());
        values.put(ItemTable.KEY_TAG, item.getKey_tag());
        values.put(ItemTable.SEARCH_SORT, item.getSearch_sort());

        database.insert(ItemTable.TABLE_NAME, null, values);
    }

    /**
     * 更新item数据
     * @param item
     * @return
     */
    public int update(ItemBean item){
        ContentValues values = new ContentValues();
        values.put(ItemTable.CATEGORY_ID, item.getCategory_id());
        values.put(ItemTable.PROVIDER, item.getProvider());
        values.put(ItemTable.NAME, item.getName());
        values.put(ItemTable.DESCRIPTION, item.getDescription());
        values.put(ItemTable.ICON, item.getIcon());
        values.put(ItemTable.SORT, item.getSort());
        values.put(ItemTable.CONTENT, item.getContent());
        values.put(ItemTable.TARGET_ACTIVITY, item.getTarget_activity());
        values.put(ItemTable.TARGET_PARAMS, item.getTarget_params());
        values.put(ItemTable.KEY_TAG, item.getKey_tag());
        values.put(ItemTable.SEARCH_SORT, item.getSearch_sort());
        return database.update(ItemTable.TABLE_NAME, values, ItemTable.ITEM_ID+"="+item.getItem_id(), null);
    }

    /**
     * 删除item数据
     * @param item
     * @return
     */
    public int delete(ItemBean item){
        return database.delete(ItemTable.TABLE_NAME, ItemTable.ITEM_ID+"=" + item.getItem_id(), null);
    }

    public void insertBatchItem(List<ItemBean> itemList) {
        database.beginTransaction();
        for(ItemBean item : itemList) {
            ContentValues values = new ContentValues();
            values.put(ItemTable.ITEM_ID, item.getItem_id());
            values.put(ItemTable.CATEGORY_ID, item.getCategory_id());
            values.put(ItemTable.PROVIDER, item.getProvider());
            values.put(ItemTable.NAME, item.getName());
            values.put(ItemTable.DESCRIPTION, item.getDescription());
            values.put(ItemTable.ICON, item.getIcon());
            values.put(ItemTable.SORT, item.getSort());
            values.put(ItemTable.CONTENT, item.getContent());
            values.put(ItemTable.TARGET_ACTIVITY, item.getTarget_activity());
            values.put(ItemTable.TARGET_PARAMS, item.getTarget_params());
            values.put(ItemTable.KEY_TAG, item.getKey_tag());
            values.put(ItemTable.SEARCH_SORT, item.getSearch_sort());

            database.insert(ItemTable.TABLE_NAME, null, values);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    /**
     * 更新首页面 "常用" ，"全部"数据
     * @param offenList
     * @param allList
     */
    public void updateCategoryData(List<CategoryBean> offenList, List<CategoryBean> allList){
        database.beginTransaction();
        int delAllCount = delete(YellowUtil.YELLOW_PAGE_CATEGORY_ID_ALL); // 删除"全部"
        int delOffenCount = delete(YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN); // 删除"常用"
        if( delAllCount != 0 && delOffenCount != 0 ){

        }
        insertCategoryList(offenList); // 添加"全部"
        insertCategoryList(allList); // 添加"常用"
        database.setTransactionSuccessful();
        database.endTransaction();
    }
    /**
     * 更新首页面 "常用" ，"全部"数据 并不清除数据库
     */
    public void updateCategoryDataNotClean(List<CategoryBean> categoryList, List<ItemBean> itemList,int version){
        database.beginTransaction();


        // 更新category数据
        if (categoryList != null && categoryList.size() > 0) {
            for (CategoryBean category : categoryList) {

                // 如果sortID为-1 就表示添加到最后一位 add ljq 2014-10-25 start
                List<CategoryBean> offenCategoryList = queryCategoryByParentId(
                        YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN, false);
                // 去除"我的"
                if (offenCategoryList == null) {

                } else {
                    int size = offenCategoryList.size();
                    for (int i = 0; i < size; i++) {
                        String targetActivity = offenCategoryList.get(i).getTarget_activity();
                        if (MyCenterConstant.MY_NODE.equals(targetActivity)) {
                            offenCategoryList.remove(i);
                        }
                    }
                    // 默认-1为添加到最后一个
                    if (category.getSort() == -1) {
                        if (offenCategoryList.size() > 0) {
                            category.setSort(offenCategoryList.get(offenCategoryList.size() - 1)
                                    .getSort() + 1);
                            Log.d("ljq", "hit !!!" + category.getSort() + "");
                        } else {
                            category.setSort(0);
                        }
                    }
                }
                // 如果sortID为-1 就表示添加到最后一位 add ljq 2014-10-25 end

                switch (category.getAction()) {
                    case 0:// update
//                        LogUtil.d(TAG, "updateCategoryDataNotClean category: " + category.toString());
                        CategoryBean local_category = queryCategoryByCategoryId(category.getCategory_id());
                        if(local_category != null ){
                            //当本地类型是“全部”时 数据可以直接更新
                            if(local_category.getParent_id() == YellowUtil.YELLOW_PAGE_CATEGORY_ID_ALL ){
                                category.setChange_type(YellowUtil.YELLOW_CATEGORY_CHANGE_TYPE_SERVICE_MODITY);
                                if(category.getParent_id() == YellowUtil.YELLOW_PAGE_CATEGORY_ID_ALL){
                                    //当更新类型是“全部”时  设置 更新SORT为原SORT 避免不必要的排序移位
                                    category.setSort(local_category.getSort());
                                }
                                updateAndSort(category);
                            }else if(local_category.getParent_id() == YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN ){
                                //当类型是常用时 用户改变过的数据 类型和常用顺序不能被更新
                                if(local_category.getChange_type() == YellowUtil.YELLOW_CATEGORY_CHANGE_TYPE_USER_MODITY ){
                                    category.setSort(local_category.getSort());
//                                    category.setLastSort(local_category.getLastSort());
                                    category.setParent_id(local_category.getParent_id());
                                    update(category);
                                }else{
                                    category.setChange_type(YellowUtil.YELLOW_CATEGORY_CHANGE_TYPE_SERVICE_MODITY);
                                    if(category.getParent_id() == YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN){

                                    }else{
                                        //当更新类型是“全部”时  设置 更新SORT为原SORT 避免不必要的排序移位
                                        category.setSort(local_category.getSort());
                                    }
                                    updateAndSort(category);
                                }
                            }
                        }else{
                            break;
                        }
                        break;
                    case 1:// insert
                        insertAndSort(category);
                        break;
                    case 2:// delete
                        delete(category);
                        break;
                    default:
                        break;
                }
            }
        }
        // 更新item数据
        if (itemList != null && itemList.size() > 0) {
            for (ItemBean itemBean : itemList) {
                switch (itemBean.getAction()) {
                    case 0:// update
                        update(itemBean);
                        break;
                    case 1:// insert
                        insert(itemBean);
                        break;
                    case 2:// delete
                        delete(itemBean);
                        break;
                    default:
                        break;
                }
            }
        }
        // 更新数据版本
        updateDataVersion(YELLOW_DATA_VERSION_DEFAULT, version);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public ItemBean queryItemByItemId(long item_id) {
        Cursor cursor = database.query(ItemTable.TABLE_NAME, null,
                ItemTable.ITEM_ID + "=?",
                new String[] { String.valueOf(item_id) }, null, null, null);

        List<ItemBean> list = fetchItemData(cursor);
        if(list != null && list.size()>0)
            return list.get(0);

        return null;
    }

    public ItemBean queryItemByName(String name) {
        Cursor cursor = database.query(ItemTable.TABLE_NAME, null, ItemTable.NAME + "=?",
                new String[] {
                        name
                }, null, null, null);

        List<ItemBean> list = fetchItemData(cursor);
        if (list != null && list.size() > 0)
            return list.get(0);

        return null;
    }

    public List<ItemBean> queryItemByCategoryId(long category_id) {
        String orderby = "sort desc";
        Cursor cursor = database.query(ItemTable.TABLE_NAME, null,
                ItemTable.CATEGORY_ID + "=?",
                new String[] { String.valueOf(category_id) }, null, null, orderby);
        return fetchItemData(cursor);
    }

    public ItemBean queryItemByRemindCode(int remindCode) {
        String orderby = "sort desc";
        Cursor cursor = database.query(ItemTable.TABLE_NAME, null,
                ItemTable.CATEGORY_ID + "=?",
                new String[] { String.valueOf(remindCode) }, null, null, orderby);
        List<ItemBean> list = fetchItemData(cursor);
        if(list != null && list.size() > 0)
            return list.get(0);

        return null;
    }

    private List<ItemBean> fetchItemData(Cursor cursor) {
        List<ItemBean> item_list = null;
        try {
            if(cursor == null || cursor.getCount()==0)
                return null;

            item_list = new ArrayList<ItemBean>();
            while(!cursor.isLast()) {
                cursor.moveToNext();

                long item_id = cursor.getLong(cursor
                        .getColumnIndex(ItemTable.ITEM_ID));
                long category_id = cursor.getLong(cursor
                        .getColumnIndex(ItemTable.CATEGORY_ID));
                int provider = cursor.getInt(cursor
                        .getColumnIndex(ItemTable.PROVIDER));
                String name = cursor.getString(cursor
                        .getColumnIndex(ItemTable.NAME));
                String desc = cursor.getString(cursor
                        .getColumnIndex(ItemTable.DESCRIPTION));
                String icon = cursor.getString(cursor
                        .getColumnIndex(ItemTable.ICON));
                int sort = cursor.getInt(cursor.getColumnIndex(ItemTable.SORT));
                String targetActivity = cursor.getString(cursor
                        .getColumnIndex(ItemTable.TARGET_ACTIVITY));
                String targetParams = cursor.getString(cursor
                        .getColumnIndex(ItemTable.TARGET_PARAMS));
                String content = cursor.getString(cursor.getColumnIndex(ItemTable.CONTENT));
                int remindCode = cursor.getInt(cursor.getColumnIndex(ItemTable.REMIND_CODE));
                String key_tag = cursor.getString(cursor
                        .getColumnIndex(ItemTable.KEY_TAG));
                int search_sort = cursor.getInt(cursor
                        .getColumnIndex(ItemTable.SEARCH_SORT));
                ItemBean ib = new ItemBean();
                ib.setItem_id(item_id);
                ib.setCategory_id(category_id);
                ib.setProvider(provider);
                ib.setName(name);
                ib.setDescription(desc);
                ib.setIcon(icon);
                ib.setSort(sort);
                ib.setTarget_activity(targetActivity);
                ib.setTarget_params(targetParams);
                ib.setContent(content);
                ib.setRemind_code(remindCode);
                ib.setKey_tag(key_tag);
                ib.setSearch_sort(search_sort);

                item_list.add(ib);
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

    public void insertExpress(Express express){
        ContentValues values = new ContentValues();
        values.put(ExpressTable.EXPRESS_NAME, express.getName());
        values.put(ExpressTable.EXPRESS_PY, express.getPinyin());
        values.put(ExpressTable.EXPRESS_LOGO, express.getLogo());
        values.put(ExpressTable.EXPRESS_PHONE, express.getPhone());
        database.insert(ExpressTable.TABLE_NAME, null, values);
    }

    public Express queryExpress(String pinyin){
        Express express = null;
        Cursor cursor = database.query(ExpressTable.TABLE_NAME, null,
                ExpressTable.EXPRESS_PY + "=?", new String[] { pinyin}, null, null, null);
        if( cursor != null 	){
            try {
                while( !cursor.isLast() ) {
                    cursor.moveToNext();
                    String name = cursor.getString(cursor.getColumnIndex(ExpressTable.EXPRESS_NAME));
                    String logo = cursor.getString(cursor.getColumnIndex(ExpressTable.EXPRESS_LOGO));
                    String phone = cursor.getString(cursor.getColumnIndex(ExpressTable.EXPRESS_PHONE));

                    express = new Express();
                    express.setName(name);
                    express.setPinyin(pinyin);
                    express.setLogo(logo);
                    express.setPhone(phone);
                    express.setSortKey(PinyinHelper.getInstance().getFullPinyin(name));
                    break;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }finally{
                cursor.close();
            }
        }
        return express;
    }

    public List<Express> getExpressList(){
        Cursor cursor = database.query(ExpressTable.TABLE_NAME, null, null, null, null, null, ExpressTable.EXPRESS_PY);
        List<Express> expressList = new ArrayList<Express>();
        if(cursor != null){
            cursor.moveToFirst();
            try {
                while (!cursor.isLast()) {
                    cursor.moveToNext();
                    Express express = new Express();
                    String name = cursor.getString(cursor.getColumnIndex(ExpressTable.EXPRESS_NAME));
                    String pinyin = cursor.getString(cursor.getColumnIndex(ExpressTable.EXPRESS_PY));
                    String logo = cursor.getString(cursor.getColumnIndex(ExpressTable.EXPRESS_LOGO));
                    String phone = cursor.getString(cursor.getColumnIndex(ExpressTable.EXPRESS_PHONE));

                    express.setName(name);
                    express.setPinyin(pinyin);
                    express.setLogo(logo);
                    express.setPhone(phone);
                    express.setSortKey(PinyinHelper.getInstance().getFullPinyin(name));
                    expressList.add(express);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                cursor.close();
            }
        }
        return expressList;
    }

    public void insertLiveTitleList(
            List<CategoryBean> liveTitleLists) {
        database.beginTransaction();
        for(CategoryBean bean : liveTitleLists ){
            ContentValues values = new ContentValues();
            values.put(CategoryTable.EXPAND_PARAM, bean.getExpand_param());
            database.update(CategoryTable.TABLE_NAME, values, CategoryTable.CATEGORY_ID+"=?",
                    new String[]{String.valueOf(bean.getCategory_id())});

        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public boolean updateExpandParamById(long category_id) {
        database.beginTransaction();
        int result = 0;
        ContentValues values = new ContentValues();
        values.put(CategoryTable.EXPAND_PARAM, "");
        database.update(CategoryTable.TABLE_NAME, values,
                CategoryTable.CATEGORY_ID + "=?",
                new String[] { String.valueOf(category_id) });
        database.setTransactionSuccessful();
        database.endTransaction();
        return result == 0;
    }


    public int replaceRechargeName(){
        ContentValues values = new ContentValues();
        values.put(CategoryTable.NAME, "充值");
        values.put(CategoryTable.SHOWNAME, "zh_CN:充值;en_US:Telephone fare;zh_TW:充值");
        int count = database.update(CategoryTable.TABLE_NAME, values,
                CategoryTable.CATEGORY_ID + "=" + "61", null);
        return count;
    }
}

