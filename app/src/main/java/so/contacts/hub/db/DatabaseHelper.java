package so.contacts.hub.db;

import so.contacts.hub.city.CityListDB;
import so.contacts.hub.db.YellowPageDB.AdTable;
import so.contacts.hub.db.YellowPageDB.CategoryTable;
import so.contacts.hub.db.YellowPageDB.CollectTable;
import so.contacts.hub.db.YellowPageDB.ExpressTable;
import so.contacts.hub.db.YellowPageDB.HabitDataTable;
import so.contacts.hub.db.YellowPageDB.HabitDataVersionTable;
import so.contacts.hub.db.YellowPageDB.ItemTable;
import so.contacts.hub.db.YellowPageDB.YellowDataVersionTable;
import so.contacts.hub.msgcenter.MessageCenterDB;
import so.contacts.hub.msgcenter.report.MsgReportDB;
import so.contacts.hub.remind.BubbleRemindManager;
import so.contacts.hub.remind.RemindManager;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.shuidianmei.WaterElectricityGasDB;
import so.contacts.hub.train.YellowPageTrainDB;
import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DatabaseHelper";
    private static DatabaseHelper sInstance;
	private Context mContext;
	
	private AppRecommendDB appRecommendDB;	
	private YellowPageDB yellowDB;
	private SearchConfigDB searchDB;	
	private ActiveDB activeDB;
	private WaterElectricityGasDB waterElectricityGasDB;
	private CityListDB cityListDB = null;
	private MovieDB movieDB;
	private YellowPageTrainDB trainDB;
	private MsgReportDB notifyDB;
	
	// add by putao_lhq 2014年12月16日 for message center start
	private MessageCenterDB msgDB;
	
	public static synchronized DatabaseHelper getInstance(Context context) {
	    if (sInstance == null) {
	        sInstance = new DatabaseHelper(context);
	    }
	    return sInstance;
	}
	
	public MessageCenterDB getMessageCenterDB() {
	    if (msgDB == null) {
	        msgDB = new MessageCenterDB(this);
	    }
	    return msgDB;
	}
	// add by putao_lhq 2014年12月16日 for message center end
	public Context getContext() {
        return mContext;
    }
    
    public YellowPageDB getYellowPageDBHelper() {
        if(yellowDB == null) {
            yellowDB = new YellowPageDB(this);
        }
        return yellowDB;
    }
    
    public SearchConfigDB getSearchConfigDB() {
        if(searchDB == null) {
            searchDB = new SearchConfigDB(this);
        }
        return searchDB;
    }
    
    public ActiveDB getActiveDB() {
        if(activeDB == null) {
            activeDB = new ActiveDB(this);
        }
        return activeDB;
    }
    
    public MovieDB getMovieDB() {
        if(movieDB == null) {
            movieDB = new MovieDB(this);
        }
        return movieDB;
    }
    
    public WaterElectricityGasDB getWaterElectricityGasDB() {
        if(waterElectricityGasDB == null) {
            waterElectricityGasDB = new WaterElectricityGasDB(this);
        }
        return waterElectricityGasDB;
    }
    
    public CityListDB getCityListDB(){
    	if( cityListDB == null ){
    		cityListDB = new CityListDB(this);
    	}
    	return cityListDB;
    }
    
    public MsgReportDB getMsgNotifyDB() {
        if( notifyDB == null ){
            notifyDB = new MsgReportDB(this);
        }
        return notifyDB;
    }
	
	private DatabaseHelper(Context context) {
		super(context, "putao.db", null, 156);
		//version:111 更新数据
		//version:112 更新数据
		//version:113 更新数据（添加收藏 表）
		//version:114 更新数据
		//version:115 更新数据（收藏表增加字段区分收藏历史, Modify By Yanghong）
		//version:116 添加dataVersion表，modify by hyl 2014-8-7
		//version:117 我的 功能逻辑修改，清空CollectTable表数据
		//version:118 更新数据，将玩乐和银行 位置互换，修改话费充值为 充话费，电影改为看电影
		//version:119 更新数据，将美食和医院对换 ，医院和驾车对换
		//version:120 更新数据
		//version:121 更新数据
		//version:122 更新数据
		//version:123 更新数据，在catrgory中加入高德的分类
		//version:124 更新数据，高德分类 增加，打车修改
		//version:125 更新数据，高德搜索美容SPA无数据
        //version:126 解决酷派OTA升级闪退的问题		
		//version:127 更新数据（增加yellow_page_category字段，修改黄页静态数据,修改黄页静态数据:电影、团购等数据, modify by zjh 2014-09-13）
		//version:128 更新数据（修改黄页附近的xx增加words字段，搜索改造后增加服务商配置表， modify by cj 2014-09-23）
		//version:129 更新数据（增加用户习惯数据）
	    //version:130 更新数据（修改了SearchConfigDB配置数据搜索方案有错误的问题， modify by cj 2014-09-25）
		//version:131 更新数据（增加了艺龙和58同城搜索配置， modify by cj 2014-09-25）
		//version:132 更新数据（增加了58同城指定关键字对应的类别搜索， modify by cj 2014-09-26）
		//version:133 添加yellow_ad表,在category和category_item中增加remind_code字段，modify by zjh 2014-10-09
		//version:134 在category表中增加edittype字段，modify by ljq 2014-10-10
		//version:135 添加search_version表,保存搜索配置版本, modify by putao_lhq 2014-10-10
		//version:136 在category表中增加change_type字段，modify by ljq 2014-10-14
		//version:137 在广告表yellow_ad中增加ad_start_time字段，modify by zjh 2014-10-18
		//version:138 增加活动彩蛋表，added by cj 2014-10-19
		//version:139 增加活动历史表，added by ljq 2014-10-19
		//version:140 更新数据，added by zjh 2014-10-20
		//version:141 更新数据，added by putao_lhq 2014-10-29
		//version:143 更新产品修改首页服务图标数据，包括了5个推荐，added by cj 2014-11-08
		//version:147 更新数据在AdTable表增加ad_params字段,功能:冬至吃饺子  add by ljq 2014-11-26
		//version:148
			//version:143 更新数据,1.在Category表里增加expand_param字段;2.增加city_db表,保存合并后的elong和58的城市列表数据 add by lisheng 2014-11-07 14:14:44
			//version:144 更新数据在yellow_page_category,yellow_page_item表增加key_tag字段，删除tag_icon字段，并更新静态数据
			//version:145添加同城火车票的数据库
			//version:146 更新数据在yellow_page_category,yellow_page_item表增加search_sort字段,并更新静态数据 add by ljq 2014-11-25
		
			/* 
			 * 1、更新数据,增加保存火车票订单的数据  add by lisheng 2014-11-27
			 * 2、更新数据,增加全国行政区的数据  add by lisheng 2014-11-29
			 * 3、更新数据,增加水电煤数据 add by ljq 2014-11-29
			 * 4、统一黄页中所有需要标记的数据的版本控制表  add by zjh 2014-12-05
			 * 5、增加搜索数据源缓存表  add by zjh 2014-12-05
			 * 6、更新数据,更换高德v1.3.0,搜索附近的需要传word参数，所以先清除item数据再重新家族 add by cj 2014-12-05
             * 7、更新数据,在HabitDataTable表增加 SERVICE_ID字段 add by ljq 2014-12-05
             * 8、更新数据，在快递表中增加logo、phone字段 add by zjh 2014-12-08
			 */
		//version:150 1.7 从150开始，添加消息中心表，以及订单对应订单表 add by putao_lhq 2014-12-17
		
		//version:151 1.7 添加电影的数据库
		//version:152 1.6.0代码同步 数据修改
		//version:156 1.7.13修改订酒店<-->彩票 数据修改
		this.mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	    LogUtil.i(TAG, "onCreate start");
		db.beginTransaction();

		db.execSQL(YellowPageDB.getCreateCategoryTableSQL());
		db.execSQL(YellowPageDB.getCreateItemTableSQL());
		db.execSQL(YellowPageDB.getCreateExpressTableSQL());
		db.execSQL(YellowPageDB.getCreateCollectTableSQL());
		
		//add by hyl 2014-8-7
		db.execSQL(YellowPageDB.getCreateDataVersionTableSQL());
	
		// add by change 2014-09-20 for search config start
		db.execSQL(SearchConfigDB.getCreateServiceConfigTableSQL());
		db.execSQL(SearchConfigDB.getCreateSearchProviderSQL());
		db.execSQL(SearchConfigDB.getCreateSearchServicePoolSQL());
		// add by change 2014-09-20 end 
		
	    //add by ljq 2014-9-22 start
        db.execSQL(YellowPageDB.getCreateHabitDataTableSQL());
        //add by ljq 2014-9-22 end
        
        //add by zjh 2014-10-09 start
        db.execSQL(YellowPageDB.getCreateAdTableSQL());
        //add by zjh 2014-10-09 end
        
        // add by cj 2014-10-19 start
        db.execSQL(ActiveDB.getCreateActiveTableSQL());
        // add by cj 2014-10-19 for end
        
        // add by ljq 2014-10-19 start
        db.execSQL(YellowPageDB.getCreateActiveHistoryTableSQL());
        // add by ljq 2014-10-19 end
        //add by ls end
        
        //add by lisheng 2014-11-24 20:13:32 start
        db.execSQL(YellowPageTrainDB.getCreateTrainTicketTableSQL());
        //add by lisheng end;
        
        //add by zjh 2014-12-05
        db.execSQL(YellowPageDB.getCreateSearchConfigCacheTableSQL());
        //add by zjh 2014-12-05 
        
        //add by ljq 2014-12-12
        db.execSQL(WaterElectricityGasDB.getCreateWaterElectricityGasTableSQL());
        //add by ljq 2014-12-12
        
		// add by zjh 2014-12-13
		db.execSQL(CityListDB.getCreateCityDbTableSQL());
		// add by zjh 2014-12-13
        
		// add by putao_lhq 2014年12月17日 start 
		db.execSQL(MessageCenterDB.getCreateMessageTableSQL());
		db.execSQL(MessageCenterDB.getCreateOrderTableSQL());
		// add by putao_lhq 2014年12月17日 end
		
        //add by ljq 2014-12-22
        db.execSQL(MovieDB.getCreateMovieCityTableSQL());
        //add by ljq 2014-12-22
		
        //add by cj 2015-01-22
        db.execSQL(MsgReportDB.getCreateMsgNotifyTableSQL());
        //add by cj 2015-01-22
        
		db.setTransactionSuccessful();
		db.endTransaction();
		LogUtil.i(TAG, "onCreate end");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    LogUtil.i(TAG, "onUpgrade oldVersion="+oldVersion+" newVersion="+newVersion);
	    if(oldVersion < 113){
	    	// 添加收藏 表
	        try{
	    	db.execSQL(YellowPageDB.getCreateCollectTableSQL());
            } catch (Exception e){
                e.printStackTrace();
            }   
	    }
	    
	    // add by Yanghong start 2014-08-06
	    if(oldVersion < 115){
	    	// 收藏表增加DataType字段
//			String sql1 = "SELECT B.NAME as " + CollectTable.TABLE_NAME
//					+ ", A.name as " + CollectTable.COLLECT_DATA_TYPE
//					+ " FROM sys.columns A WHERE A.name = '"
//					+ CollectTable.COLLECT_DATA_TYPE + "'";

	    	//			String sql = "select name from " + CollectTable.TABLE_NAME
//					+ " where " + CollectTable.COLLECT_ID
//					+ " in (select id from " + CollectTable.TABLE_NAME
//					+ " where name='" + CollectTable.COLLECT_DATA_TYPE + "') ";
			
			try {
				db.execSQL("ALTER TABLE " + CollectTable.TABLE_NAME
						+ " ADD COLUMN " + CollectTable.COLLECT_DATA_TYPE + " INTEGER");
			} catch (Exception e) {
			}
			
	    }
	    // add by Yanghong end
	    
	    //add by hyl 2014-8-7 start
	    if(oldVersion < 116){
	        try{
	        db.execSQL(YellowPageDB.getCreateDataVersionTableSQL());
            } catch (Exception e){
                e.printStackTrace();
            }   
	    }
	    //add by hyl 2014-8-7 end
	    
	    if( oldVersion < 117 ){
	        try{
	    	clearCollectData(db);
            } catch (Exception e){
                e.printStackTrace();
            }   
	    }

        //add by cj 2014-11-04 start
        //合并tag1.2.2 关于126版本的更新
        if( oldVersion < 126 ){
            try{
                clearOldData(db);
            } catch (Exception e){
            }
        }
        //add by cj 2014-11-04 end

        //add by zjh 2014-09-13 start
        if( oldVersion < 127 ){            
            // 更改了所有黄页的数据，需要清除所有黄页静态数据
            db.delete(CategoryTable.TABLE_NAME, null, null);
            try {
                //增加yellow_page_category字段: last_sort, icon_logo
                db.execSQL("ALTER TABLE " + CategoryTable.TABLE_NAME
                        + " ADD COLUMN " + CategoryTable.ICON_LOGO + " TEXT;");
                db.execSQL("ALTER TABLE " + CategoryTable.TABLE_NAME
                        + " ADD COLUMN " + CategoryTable.LAST_SORT + " INTEGER;");
                
            } catch (Exception e) {
            }
        }
        //add by zjh 2014-09-13 end
	    
        // add by change 2014-09-23 for search config start
	    if( oldVersion < 128 ) {
            try {
                db.execSQL(SearchConfigDB.getCreateServiceConfigTableSQL());
                db.execSQL(SearchConfigDB.getCreateSearchProviderSQL());
                db.execSQL(SearchConfigDB.getCreateSearchServicePoolSQL());
            } catch (Exception e){
                e.printStackTrace();
            }            
	    }
        // add by change 2014-09-23 end 

        //add by ljq 2014-09-23 start
        if( oldVersion < 129 ){
            try {
                db.execSQL(YellowPageDB.getCreateHabitDataTableSQL());
                db.execSQL(YellowPageDB.getCreateHabitDataVersionTableSQL());
            } catch (Exception e){
                e.printStackTrace();
            }            
        }
        
        //add by ljq 2014-09-23 start
        if(oldVersion < 132){
	        /*
	         * update by hyl 2014-8-7 start
	         * old code:
	         *     //重新加载数据
	         *     reloadCategoryData(db);
                   reloadItemTable(db);
                   reloadCityData(db);
                   reloadExpressData(db);
	         */
	        //clearOldData(db);    //delete by cj 2014-10-09 end
	        //update by hyl 2014-8-7 end
	    }
        
        // add by zjh 2014-10-09 start
        if( oldVersion < 133 ){
            //add by cj 2014-10-09 start
            clearOldData(db);
            try {
                db.execSQL("ALTER TABLE " + CategoryTable.TABLE_NAME
                        + " ADD COLUMN " + CategoryTable.REMIND_CODE + " Integer;");
                db.execSQL("ALTER TABLE " + ItemTable.TABLE_NAME
                        + " ADD COLUMN " + ItemTable.REMIND_CODE + " INTEGER;");
                
            } catch (Exception e) {
                e.printStackTrace();
            }            
            //add by cj 2014-10-09 end

            try {
        	db.execSQL(YellowPageDB.getCreateAdTableSQL());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        // add by zjh 2014-10-09 end

        // add by ljq 2014-10-10 start
        if( oldVersion < 134 ){
            clearOldData(db);
            try {
                db.execSQL("ALTER TABLE " + CategoryTable.TABLE_NAME
                        + " ADD COLUMN " + CategoryTable.EDITTYPE + " TEXT;");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // add by ljq 2014-10-10 end

        // add by putao_lhq 2014-10-10 start
        if (oldVersion < 135) {
            try{
                db.execSQL(SearchConfigDB.getCreateDataVersionTableSQL());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        // add by putao_lhq 2014-10-10 end
        
        // add by ljq 2014-10-14 start
        if( oldVersion < 136 ){
            clearOldData(db);
            try {
                db.execSQL("ALTER TABLE " + CategoryTable.TABLE_NAME
                        + " ADD COLUMN " + CategoryTable.CHANGE_TYPE + " INTEGER;");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        // add by ljq 2014-10-14 end
        
        //add by zjh 2014-10-18 start
	    if( oldVersion < 137 ){
	    	// 在广告表yellow_ad中增加ad_start_time字段
	    	clearOperateAdData(db);
	    	try{
	    		//增加ad_start_time字段
				db.execSQL("ALTER TABLE " + AdTable.TABLE_NAME
						+ " ADD COLUMN " + AdTable.AD_START_TIME + " LONG;");
	    	}catch(Exception e){
	    		
	    	}
	    }
	    //add by zjh 2014-10-18 end
	    
        //add by cj 2014-10-19 start
        if( oldVersion < 138 ){
            db.execSQL(ActiveDB.getCreateActiveTableSQL());
        }
        //add by cj 2014-10-19 end
        
        //add by ljq 2014-10-19 start
        if( oldVersion < 139 ){
            db.execSQL(YellowPageDB.getCreateActiveHistoryTableSQL());
        }
        //add by ljq 2014-10-19 end
        
        // add by zjh 2014-10-20 start
        //modify by putao_lhq from 140 to 141
        if( oldVersion < 141 ){
        	clearOldData(db);
        }
        // add by zjh 2014-10-20 end
        
        // add by cj 2014/11/08 start
        if( oldVersion < 143 ){
            clearOldData(db);
            // 数据库版本更新后还需更新141版本(1.5.66)的默认5个‘荐’
            // 先清除当前老版本的打点和气泡，再设置默认加载气泡,设置当前系统版本1.5.67下的opconfig版本为0
            RemindManager.getInstance().cleanAndSave();
            BubbleRemindManager.getInstance().cleanAndSave();
            RemindUtils.setDefBubles(true);            
            RemindUtils.setRemindVersion(0);
        }
        // add by cj 2014/11/08 end
        
        // add by ljq 2014-11-26 start
        if( oldVersion < 147 ){
            clearOperateAdData(db);
            try {
                db.execSQL("ALTER TABLE " + AdTable.TABLE_NAME
                        + " ADD COLUMN " + AdTable.AD_PARAMS + " TEXT;");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // add by ljq 2014-11-26 end
        
        // deleted by cj 2014-12-12 for 删除liveTitle功能 start
		// add by ls 2014-11-07 11:57:20
		/*if (oldVersion < 143) {
			clearOldData(db);
			try {
				// 增加yellow_page_category字段: EXPAND_PARAM
				db.execSQL("ALTER TABLE " + CategoryTable.TABLE_NAME
						+ " ADD COLUMN " + CategoryTable.EXPAND_PARAM + " TEXT;");
				// 创建城市数据库,保存艺龙和58同城的城市代码;
				db.execSQL(YellowPageDB.getCreateCityDbTableSQL());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		// add by ls 2014-11-07 11:58:17
        // deleted by cj 2014-12-12 end

		// add by zjh 2014-11-21 start
		/*if( oldVersion < 144 ){
			clearOldData(db);
			try {
				// 注：SQLite中删除字段只能通过删除表，然后再创建表来实现
				// 删除yellow_page_category字段: tag_icon, 增加字段: key_tag
				db.execSQL("DROP TABLE " + CategoryTable.TABLE_NAME + " ;");
				db.execSQL(YellowPageDB.getCreateCategoryTableSQL());
				
				// 删除yellow_page_item字段: tag_icon, 增加字段: key_tag
				db.execSQL("DROP TABLE " + ItemTable.TABLE_NAME + " ;");
				db.execSQL(YellowPageDB.getCreateItemTableSQL());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		// add by zjh 2014-11-21 end
		
		//add by lisheng 2014-11-24 20:35:10 start
		/*if (oldVersion < 145) {
			clearOldData(db);
			try {
				// 添加同城火车票车站的数据;
				db.execSQL(YellowPageDB.getCreateTongChengTableSQL());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		//add by lisheng end
		
        // add by ljq 2014-11-25 start
        /*if( oldVersion < 146 ){
            clearOldData(db);
            try {
                db.execSQL("ALTER TABLE " + CategoryTable.TABLE_NAME
                        + " ADD COLUMN " + CategoryTable.SEARCH_SORT + " INTEGER;");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        // add by ljq 2014-11-25 end

		// modified by cj 2014-12-12 for v1.6.0用148版本合并143-147多个数据操作 start 
        // add by zjh 2014-12-06 start
        if( oldVersion < 148 ||
                oldVersion < 149 || oldVersion < 150){
        	clearOldData(db);
        	try {
				// 注：SQLite中删除字段只能通过删除表，然后再创建表来实现
				// zjh 删除yellow_page_category字段: tag_icon, 增加字段: key_tag
				db.execSQL("DROP TABLE " + CategoryTable.TABLE_NAME + " ;");
				db.execSQL(YellowPageDB.getCreateCategoryTableSQL());
				// zjh 删除yellow_page_item字段: tag_icon, 增加字段: key_tag
				db.execSQL("DROP TABLE " + ItemTable.TABLE_NAME + " ;");
				db.execSQL(YellowPageDB.getCreateItemTableSQL());
			} catch (SQLException e) {
				LogUtil.e(TAG, e.getMessage());
				e.printStackTrace();
			}
            
            try {
            	// ls 添加同城火车票车站的数据;
            	db.execSQL(YellowPageTrainDB.getCreateTrainTicketTableSQL());
			} catch (SQLException e) {
				LogUtil.e(TAG, e.getMessage());
				e.printStackTrace();
			}
            
            try {
            	db.execSQL(WaterElectricityGasDB.getCreateWaterElectricityGasTableSQL());//add by ljq 2014-11-29
			} catch (SQLException e) {
				LogUtil.e(TAG, e.getMessage());
				e.printStackTrace();
			}
            
            try {
				// add by zjh 2014-12-05 start
				db.execSQL("DROP TABLE " + SearchConfigDB.DataVersionTable.TABLE_NAME + " ;");
				db.execSQL("DROP TABLE " + HabitDataVersionTable.TABLE_NAME + " ;");
				db.execSQL("ALTER TABLE " + YellowDataVersionTable.TABLE_NAME
						+ " ADD COLUMN " + YellowDataVersionTable.TAG_DATA + " TEXT;");
				// add by zjh 2014-12-05 end

            } catch (SQLException e) {
				LogUtil.e(TAG, e.getMessage());
				e.printStackTrace();
			}
            
            try {
				// add by zjh 2014-12-05 start
				db.execSQL(YellowPageDB.getCreateSearchConfigCacheTableSQL());
				// add by zjh 2014-12-05 end

            } catch (SQLException e) {
				LogUtil.e(TAG, e.getMessage());
				e.printStackTrace();
			}
            
            try {
		        //add by ljq 2014-12-05 start
                //增加HabitDataTable字段: service_id
                db.execSQL("ALTER TABLE " + HabitDataTable.TABLE_NAME
                        + " ADD COLUMN " + HabitDataTable.SERVICE_ID + " LONG;");
                //add by ljq end

            } catch (SQLException e) {
				LogUtil.e(TAG, e.getMessage());
				e.printStackTrace();
			}
            
            try {
				// add by zjh 2014-12-08 start
                // 在快递表中增加logo、phone字段
                db.execSQL("ALTER TABLE " + ExpressTable.TABLE_NAME
						+ " ADD COLUMN " + ExpressTable.EXPRESS_LOGO + " TEXT;");
                db.execSQL("ALTER TABLE " + ExpressTable.TABLE_NAME
						+ " ADD COLUMN " + ExpressTable.EXPRESS_PHONE + " TEXT;");
				// add by zjh 2014-12-08 end
				
			} catch (SQLException e) {
				LogUtil.e(TAG, e.getMessage());
				e.printStackTrace();
			}

        }
        // add by zjh 2014-12-06 end
		// modified by cj 2014-12-12 end
        
        /**
         * 酷派1.7数据库版本号为：155
         * modify by zjh 2015-01-23
         * 
         * 注：删除add by xcx 2014-12-25 start 替换elong搜索需要清除搜索数据库 记录，在clearOldData(db)中有
         */
        if (oldVersion < 155) {
            //add by hyl 2014-12-26 start 同步1.6.03代码 修改了category数据
            clearOldData(db);
            ////add by hyl 2014-12-26 end
            
            /**
             * add by zjh 2014-12-13 start
             * 统一修改所有城市列表
             */
            try{
            	// 删除如下三张表
            	db.execSQL("DROP TABLE " + DeleteDBInfo.CityTable.TABLE_NAME + " ;");
            	db.execSQL("DROP TABLE " + DeleteDBInfo.ChinaDistrictTable.TABLE_NAME + " ;");
            	db.execSQL("DROP TABLE " + DeleteDBInfo.CityDbTable.TABLE_NAME + " ;");
            	
            	// 修改同城火车票表名
            	db.execSQL("ALTER TABLE " + DeleteDBInfo.TongChengCityTable.TABLE_NAME + " RENAME TO " 
            			+ YellowPageTrainDB.YellowTrainTicketTable.TABLE_NAME + " ;");
            	
            	// 创建通用城市列表数据库
        		db.execSQL(CityListDB.getCreateCityDbTableSQL());
            }catch(Exception e){
            	
            }
            /** add by zjh 2014-12-13 end */
            
            try {
            	/**
            	 * 添加消息中心表以及订单中心表
            	 * modify by putao_lhq 
            	 */ 
            	db.execSQL(MessageCenterDB.getCreateMessageTableSQL());
            	db.execSQL(MessageCenterDB.getCreateOrderTableSQL());
            	// modify by putao_lhq end
            	
            	// add by xcx 2015-01-12 start
            	db.execSQL(MovieDB.getCreateMovieCityTableSQL());
            	db.execSQL(MsgReportDB.getCreateMsgNotifyTableSQL());
            	
                db.execSQL("ALTER TABLE " + MessageCenterDB.OrderTable.TABLE_NAME
                        + " ADD COLUMN " + MessageCenterDB.OrderTable.ORDER_COUPON_IDS + " TEXT;");
                // add by xcx 2015-01-06 end
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        //add by cj 2015-03-06 start  修改了category数据,订酒店和彩票调整位置
        if (oldVersion < 156) {
            clearOldData(db);
        }
        //add by cj 2015-03-06 end
        
        LogUtil.i(TAG, "onUpgrade end");
	}
	
	/**
	 * 清除老数据
	 * @param db
	 */
	private void clearOldData(SQLiteDatabase db) {
	    try{
    	    clearCategoryData(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
	    try{
            clearItemTable(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
	    try{
            clearExpressData(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
	    try{
            clearSearchData(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
	    try {
	    	clearTongChengCityData(db);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    try {
			clearCityListData(db);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void clearTongChengCityData(SQLiteDatabase db) {
		db.delete(YellowPageTrainDB.YellowTrainTicketTable.TABLE_NAME, null, null);
	}

	private void clearCategoryData(SQLiteDatabase db){
        db.delete(YellowPageDB.CategoryTable.TABLE_NAME, null, null);
	}
	
	private void clearItemTable(SQLiteDatabase db){
	    db.delete(YellowPageDB.ItemTable.TABLE_NAME, null, null);
	}
	
	private void clearExpressData(SQLiteDatabase db){
	    db.delete(YellowPageDB.ExpressTable.TABLE_NAME, null, null);
    }
	
	private void clearCollectData(SQLiteDatabase db){
	    db.delete(YellowPageDB.CollectTable.TABLE_NAME, null, null);
	}
	
	private void clearHabitData(SQLiteDatabase db){
	    db.delete(YellowPageDB.HabitDataTable.TABLE_NAME, null, null);
	    db.delete(YellowPageDB.HabitDataVersionTable.TABLE_NAME, null, null);
	}
	
	private void clearOperateAdData(SQLiteDatabase db){
		db.delete(YellowPageDB.AdTable.TABLE_NAME, null, null);
	}
	
	private void clearCityListData(SQLiteDatabase db) {
		db.delete(CityListDB.CityDBTable.TABLE_NAME, null, null);
	}
	
    private void clearSearchData(SQLiteDatabase db) {
        /**
         * 清除搜索老数据时，增加try catch动作，避免表不存在时清除数据导致当前更新线程挂掉，引起其他数据库操作中断
         * modify by change at 2014/09/26 start
         */
        try {
            db.delete(SearchConfigDB.SearchConfigTable.TABLE_NAME, null, null);
            db.delete(SearchConfigDB.SearchServicePoolTable.TABLE_NAME, null, null);
            db.delete(SearchConfigDB.SearchProviderTable.TABLE_NAME, null, null);
        } catch (SQLiteException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        // modify by change at 2014/09/26 end
    }

	/**
	 * @return
	 */
	public AppRecommendDB getAppRecommendDB() {
		if(appRecommendDB == null){
			appRecommendDB = new AppRecommendDB(this);
		}
		return appRecommendDB;
	}

	public YellowPageTrainDB getTrainDBHelper() {
		if (trainDB == null) {
			trainDB = new YellowPageTrainDB(this);
		}
		return trainDB;
	}
	
}
