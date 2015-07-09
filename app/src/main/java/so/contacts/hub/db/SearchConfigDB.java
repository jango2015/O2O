package so.contacts.hub.db;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.core.Config;
import so.contacts.hub.search.SearchTask;
import so.contacts.hub.search.SearchUtils;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.ui.yellowpage.bean.SearchConfigBean;
import so.contacts.hub.ui.yellowpage.bean.SearchProvider;
import so.contacts.hub.ui.yellowpage.bean.SearchServicePoolBean;
import so.contacts.hub.util.LogUtil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.yulong.android.contacts.discover.R;

public class SearchConfigDB {
    private static final String TAG = "SearchConfigDB";
    
    SQLiteDatabase database;
    //DatabaseHelper mHelper;// delete by putao_lhq 2014年11月4日 for performance

    public SearchConfigDB(DatabaseHelper helper) {
        //mHelper = helper;
        database = helper.getWritableDatabase();
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return this.database;
    }
    
    public static class SearchConfigTable implements BaseColumns {
        public static final String TABLE_NAME = "search_config";
        public static final String _ID = "_id";
        public static final String KEYWORD = "keyword";
        public static final String ENTRY = "entry";
        public static final String SCHDULE="schdule";        
    }

    public static class SearchServicePoolTable implements BaseColumns {
        public static final String TABLE_NAME = "search_service_pool";
        public static final String _ID = "_id";
        public static final String CONFIG_ID = "config_id";
        public static final String PID = "pid";
        public static final String SORT = "sort";
        public static final String SEARCH_INFO = "search_info";
    }
    
    public static class SearchProviderTable implements BaseColumns{
        public static final String TABLE_NAME = "search_provider";
        public static final String _ID = "_id";
        public static final String NAME = "name";
        public static final String ENTRY_TYPE = "entry_type";
        public static final String STATUS = "status";
        public static final String SERVICE_NAME = "service_name";
    }

    /**
     * 搜索数据版本
     * @author putao_lhq
     * @version 2014年10月10日
     */
    public static class DataVersionTable implements BaseColumns{
	    public static final String TABLE_NAME = "search_version";
        public static final String VERSION_DATA = "search_data";     
	}
    
    static String getCreateServiceConfigTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(SearchConfigTable.TABLE_NAME).append(" (");
        sb.append(SearchConfigTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(SearchConfigTable.KEYWORD).append(" TEXT,");
        sb.append(SearchConfigTable.ENTRY).append(" INTEGER,");
        sb.append(SearchConfigTable.SCHDULE).append(" INTEGER");        
        sb.append(");");
        return sb.toString();
    }

    static String getCreateSearchServicePoolSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(SearchServicePoolTable.TABLE_NAME)
                .append(" (");
        sb.append(SearchServicePoolTable._ID).append(" INTEGER PRIMARY KEY autoincrement,");
        sb.append(SearchServicePoolTable.CONFIG_ID).append(" INTEGER,");
        sb.append(SearchServicePoolTable.PID).append(" INTEGER,");
        sb.append(SearchServicePoolTable.SORT).append(" INTEGER,");
        sb.append(SearchServicePoolTable.SEARCH_INFO).append(" TEXT");
        sb.append(");");
        return sb.toString();
    }
    
    static String getCreateSearchProviderSQL(){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(SearchProviderTable.TABLE_NAME)
                .append(" (");
        sb.append(SearchProviderTable._ID).append(" INTEGER  PRIMARY KEY  autoincrement,");
        sb.append(SearchProviderTable.NAME).append(" TEXT,");
        sb.append(SearchProviderTable.ENTRY_TYPE).append(" INTEGER,");
        sb.append(SearchProviderTable.STATUS).append(" INTEGER, ");
        sb.append(SearchProviderTable.SERVICE_NAME).append(" TEXT");
        sb.append(");");
        return sb.toString();
    }
	
    //add by putao_lhq
    static String getCreateDataVersionTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(DataVersionTable.TABLE_NAME).append(" (");
        sb.append(DataVersionTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(DataVersionTable.VERSION_DATA).append(" INTEGER");
        sb.append(");");
        return sb.toString();
    }
    
    public void clearTable(String tableName){
        database.delete(tableName, null, null);
    }
    
    /**
     * 插入关键字配置数据
     * @param config
     * modify by putao_lhq 增加数据查询，如果已存在数据则进行更新操作。
     */
    public void insertSearchConfig(SearchConfigBean config) {
    	if (null == config) {
    		return;
    	}
    	Cursor cursor = null;
    	try {
    		cursor = querySearchConfig(config.getId());
    		if (cursor != null && cursor.getCount() > 0) {
    			updateSearchConfig(config);
    		} else {
    			ContentValues values = new ContentValues();
    			values.put(SearchConfigTable._ID, config.getId());
    			values.put(SearchConfigTable.KEYWORD, config.getKeyword());
    			values.put(SearchConfigTable.ENTRY, config.getEntry());
    			values.put(SearchConfigTable.SCHDULE, config.getSchdule());
    			
    			database.insert(SearchConfigTable.TABLE_NAME, null, values);
    		}
			
		} catch (Exception e) {
			LogUtil.e(TAG, "insertSearchConfig error: " + e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
    }
    
    /**
     * 查询关键字配置数据根据关键字配置id
     * @param id
     * @return
     */
    public Cursor querySearchConfig(int id) {
    	if (id <= 0) {
    		return null;
    	}
    	return database.query(SearchConfigTable.TABLE_NAME, null, 
    			SearchConfigTable._ID + "=?", 
    			new String[]{String.valueOf(id)}, null, null, null);
    }
    
    /**
     * 插入搜索服务池
     * @param pool
     */
    public void insertSearchServicePool(SearchServicePoolBean pool) {
    	if (null == pool) {
    		return;
    	}
    	Cursor cursor = null;
    	try {
    		cursor = querySearchServicePool(pool.getId());
    		if (cursor != null && cursor.getCount() > 0) {
    			updateSearchSevicePool(pool);
    		} else {
    			ContentValues values = new ContentValues();
    			values.put(SearchServicePoolTable._ID, pool.getId());
    			values.put(SearchServicePoolTable.CONFIG_ID, pool.getConfigId());
    			values.put(SearchServicePoolTable.PID, pool.getPid());
    			values.put(SearchServicePoolTable.SORT, pool.getSort());
    			values.put(SearchServicePoolTable.SEARCH_INFO, pool.getSearchInfo());
    			
    			database.insert(SearchServicePoolTable.TABLE_NAME, null, values);
    		}
		} catch (Exception e) {
			LogUtil.e(TAG, "insertSearchServicePool error: " + e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
    }

    /**
     * 查询搜索服务池
     * @param id
     * @return
     */
    public Cursor querySearchServicePool(int id) {
    	if (id <= 0) {
    		return null;
    	}
    	return database.query(SearchServicePoolTable.TABLE_NAME, null, 
    			SearchServicePoolTable._ID + "=?", 
    			new String[]{String.valueOf(id)}, null, null, null);
    }
    
    /**
     * 
     * @param provider
     */
    public void insertSearchProvider(SearchProvider provider) {
    	if (null == provider) {
    		return;
    	}
    	Cursor cursor = null;
    	try {
    		cursor = querySearchProvider(provider.getId());
    		if (cursor!= null && cursor.getCount() > 0) {
    			updateSearchProvider(provider);
    		} else {
    			ContentValues values = new ContentValues();
    			values.put(SearchProviderTable._ID, provider.getId());
    			values.put(SearchProviderTable.NAME, provider.getName());
    			values.put(SearchProviderTable.ENTRY_TYPE, provider.getEntryType());
    			values.put(SearchProviderTable.STATUS, provider.getStatus());
    			values.put(SearchProviderTable.SERVICE_NAME, provider.getServiceName());
    			
    			database.insert(SearchProviderTable.TABLE_NAME, null, values);
    		}
		} catch (Exception e) {
			LogUtil.e(TAG, "insertSearchProvider error: " + e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
    }
    
    /**
     * 
     * @return
     */
    public Cursor querySearchProvider(int id) {
    	if (id <= 0) {
    		return null;
    	}
    	return database.query(SearchProviderTable.TABLE_NAME, null, 
    			SearchProviderTable._ID + "=?", 
    			new String[]{String.valueOf(id)}, null, null, null);
    }
    
    public boolean hasData() {  
    	
        Cursor cursor = database.query(SearchConfigTable.TABLE_NAME, null, null,
                null, null, null, null);
        
        if(cursor != null && cursor.getCount() > 0) {
        	cursor.close();
        	return true;
        } else if (cursor != null && cursor.getCount() == 0) {
        	cursor.close();
        }
        
        return false;
    }
    
    public SearchConfigBean qrySearchConfig(String keyword, int entry) {
        if(TextUtils.isEmpty(keyword))
            keyword = "";
        
        Cursor cursor = database.query(SearchConfigTable.TABLE_NAME, null,
                SearchConfigTable.KEYWORD + "=? and "+SearchConfigTable.ENTRY+"=?" ,
                new String[] { keyword, String.valueOf(entry)  }, 
                null, null, null);

        // delete by putao_lhq 2014年11月4日 for performance
        /*if (cursor == null || cursor.getCount()==0)
            return null;*/
        
        SearchConfigBean bean = null;
        try {
        	// add by putao_lhq 2014年11月4日 for performance start
        	if (cursor == null || cursor.getCount()==0) {
        		return null;
        	}
        	// add by putao_lhq 2014年11月4日 for performance end
            if (cursor.moveToNext()) {
                int id = cursor.getInt(cursor
                        .getColumnIndex(SearchConfigTable._ID));
                int schdule = cursor.getInt(cursor.getColumnIndex(SearchConfigTable.SCHDULE));
                
                bean = new SearchConfigBean();
                bean.setId(id);
                bean.setKeyword(keyword);
                bean.setEntry(entry);
                bean.setSchdule(schdule);
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
    
    public SearchProvider qrySearchProviderById(int id) {       
        Cursor cursor = database.query(SearchProviderTable.TABLE_NAME, null,
                SearchProviderTable._ID + "=? and status=0" ,
                new String[] { String.valueOf(id) }, 
                null, null, null);
        // delete by putao_lhq 2014年11月4日 for performance
        /*if (cursor == null || cursor.getCount()==0)
            return null;*/
        
        SearchProvider bean = null;
        try {
        	// add by putao_lhq 2014年11月4日 for performance start
        	if (cursor == null || cursor.getCount()==0) {
        		return null;
        	}
        	// add by putao_lhq 2014年11月4日 for performance end
            if (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(SearchProviderTable.NAME));
                int entryType = cursor.getInt(cursor.getColumnIndex(SearchProviderTable.ENTRY_TYPE));
                int status = cursor.getInt(cursor.getColumnIndex(SearchProviderTable.STATUS));
                String serviceName = cursor.getString(cursor.getColumnIndex(SearchProviderTable.SERVICE_NAME));
                
                bean = new SearchProvider();
                bean.setId(id);
                bean.setName(name);
                bean.setStatus(status);
                bean.setEntryType(entryType);
                bean.setServiceName(serviceName);
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
    
    public List<SearchServicePoolBean> qrySearchServicePoolByConfigId(int config_id) {       
        String orderby = " sort asc";
        Cursor cursor = database.query(SearchServicePoolTable.TABLE_NAME, null,
                SearchServicePoolTable.CONFIG_ID + "=? " ,
                new String[] { String.valueOf(config_id) }, 
                null, null, orderby);
        // delete by putao_lhq 2014年11月4日 for performance
        /*if (cursor == null || cursor.getCount()==0)
            return null;*/
        
        List<SearchServicePoolBean> list = new ArrayList<SearchServicePoolBean>();
        try {
        	// add by putao_lhq 2014年11月4日 for performance start
        	if (cursor == null || cursor.getCount()==0) {
        		return null;
        	}
        	// add by putao_lhq 2014年11月4日 for performance end
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(SearchServicePoolTable._ID));
                int pid = cursor.getInt(cursor.getColumnIndex(SearchServicePoolTable.PID));
                int sort = cursor.getInt(cursor.getColumnIndex(SearchServicePoolTable.SORT));
                String searchInfo = cursor.getString(cursor.getColumnIndex(SearchServicePoolTable.SEARCH_INFO));
                
                SearchServicePoolBean bean = new SearchServicePoolBean();
                bean.setId(id);
                bean.setPid(pid);
                bean.setConfigId(config_id);
                bean.setSort(sort);
                bean.setSearchInfo(searchInfo);
                
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
        
    // 按关键字和入口查询
    /**
     * 按照搜索关键字和入口位置查询，如果是从附近的xx进入，
     * 那么keyword必须是传入：附近的xx
     * @param keyword 搜索关键字/附近的xx
     * @param entry 1-分类入口（附近的xx）
     *              2-关键字搜索
     *              3-号码查询入口
     * @return
     */
    public List<SearchTask> qrySearchTaskList(String keyword, int entry, SearchInfo defSearchInfo) {

        SearchConfigBean configBean = qrySearchConfig(keyword, entry); // 配置项只能有一条
        if(configBean == null) {
            configBean = qrySearchConfig("", entry); // 如果关键字和入口查不到，则再查入口
            if(configBean == null) {
                return null;
            }
        }
       
        List<SearchServicePoolBean> pools = qrySearchServicePoolByConfigId(configBean.getId());
        if(pools == null || pools.size() == 0)
            return null;
        
        List<SearchTask> taskList = new ArrayList<SearchTask>();        
        for(int i = 0; i < pools.size(); i++) {
            SearchServicePoolBean service = pools.get(i);
            SearchProvider provider = qrySearchProviderById(service.getPid());
            if(provider == null){
            	continue;
            }
            
            SearchTask task = new SearchTask();
            task.setId(service.getId());
            task.setBussEntry(configBean.getEntry());
            task.setHasMore(true);
            task.setProvider(provider);
            task.setSort(service.getSort());
            
            SearchInfo searchInfo = null;
            if(!TextUtils.isEmpty(service.getSearchInfo())) {
                try {
                    searchInfo = Config.mGson.fromJson(service.getSearchInfo(), SearchInfo.class);
                } catch (JsonSyntaxException e){
                    LogUtil.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
            
            if(searchInfo == null){
            	searchInfo = defSearchInfo;
            }
            
            task.setSearchInfo(searchInfo);
            taskList.add(task);
        }
        
        return taskList;
    }
    
    public boolean insertDefaultConfig(Context context) {
        database.beginTransaction();

        // 添加默认的数据提供商
        SearchProvider provider = new SearchProvider();
        provider.setId(1);
        provider.setName(context.getResources().getString(R.string.putao_search_provider_dianping));
        provider.setEntryType(3);
        provider.setStatus(0);
        provider.setServiceName("so.contacts.hub.search.factory.DianpingSearchFactory");
        
        insertSearchProvider(provider);
        
        provider.setId(2);
        provider.setName(context.getResources().getString(R.string.putao_search_provider_gaode));
        provider.setEntryType(3);
        provider.setStatus(0);
        provider.setServiceName("so.contacts.hub.search.factory.GaodeSearchFactory");
        
        insertSearchProvider(provider);

        provider.setId(3);
        provider.setName(context.getResources().getString(R.string.putao_search_provider_sogou));
        provider.setEntryType(2);
        provider.setStatus(0);
        provider.setServiceName("so.contacts.hub.search.factory.SougouSearchFactory");
        
        insertSearchProvider(provider);
        
        provider.setId(4);
        provider.setName(context.getResources().getString(R.string.putao_search_provider_58city));
        provider.setEntryType(1);
        provider.setStatus(0);
        provider.setServiceName("so.contacts.hub.search.factory.City58SearchFactory");
        
        insertSearchProvider(provider);
        
        provider.setId(5);
        provider.setName(context.getResources().getString(R.string.putao_search_provider_elong));
        provider.setEntryType(3);
        provider.setStatus(0);
        provider.setServiceName("so.contacts.hub.search.factory.ELongSearchFactory");
        
        insertSearchProvider(provider);
        
        //add xcx 2014_12_25 start 新增同程搜索
        provider.setId(6);
        provider.setName(context.getResources().getString(R.string.putao_search_provider_tongcheng));
        provider.setEntryType(3);
        provider.setStatus(0);
        provider.setServiceName("so.contacts.hub.search.factory.TongChengSearchFactory");
        
        insertSearchProvider(provider);
        //add xcx 2014_12_25 end 新增同程搜索
        
        // 添加关键字配置项
        SearchConfigBean config = new SearchConfigBean();        
        
        config.setId(1);
        config.setKeyword(context.getResources().getString(R.string.putao_search_word_recently_hotel));
        config.setEntry(1);
        config.setSchdule(1);
        insertSearchConfig(config);

        config.setId(2);
        config.setKeyword(context.getResources().getString(R.string.putao_search_word_hotel));
        config.setEntry(2);
        config.setSchdule(1);
        insertSearchConfig(config);

        config.setId(3);
        config.setKeyword(context.getResources().getString(R.string.putao_search_word_banjia));
        config.setEntry(2);
        config.setSchdule(1);
        insertSearchConfig(config);
        
        config.setId(4);
        config.setKeyword(context.getResources().getString(R.string.putao_search_word_baomu));
        config.setEntry(2);
        config.setSchdule(1);
        insertSearchConfig(config);

        // 添加任务池
        SearchServicePoolBean pool = new SearchServicePoolBean();
        
        pool.setId(1);
        pool.setConfigId(1);
        //modify xcx 2014_12_25 start 同程搜索替换elong
        pool.setPid(6);
        //modify xcx 2014_12_25 end 同程搜索替换elong
        pool.setSort(1);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_hotel)); // 附近的酒店
        insertSearchServicePool(pool);

        pool.setId(2);
        pool.setConfigId(1);
        pool.setPid(1);
        pool.setSort(2);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_hotel)); // 附近的酒店
        insertSearchServicePool(pool);

        pool.setId(3);
        pool.setConfigId(1);
        pool.setPid(2);
        pool.setSort(3);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_hotel)); // 附近的酒店
        insertSearchServicePool(pool);

        pool.setId(4);
        pool.setConfigId(1);
        pool.setPid(3);
        pool.setSort(4);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_hotel)); // 附近的酒店
        insertSearchServicePool(pool);

        pool.setId(5);
        pool.setConfigId(2);
        //modify xcx 2014_12_25 start 同程搜索替换elong
        pool.setPid(6);
        //modify xcx 2014_12_25 end 同程搜索替换elong
        pool.setSort(1);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_hotel)); // 酒店关键字查询
        insertSearchServicePool(pool);

        pool.setId(6);
        pool.setConfigId(2);
        pool.setPid(1);
        pool.setSort(2);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_hotel)); // 酒店关键字查询
        insertSearchServicePool(pool);

        pool.setId(7);
        pool.setConfigId(2);
        pool.setPid(2);
        pool.setSort(3);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_hotel)); // 酒店关键字查询
        insertSearchServicePool(pool);
        
        pool.setId(8);
        pool.setConfigId(2);
        pool.setPid(3);
        pool.setSort(4);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_hotel)); // 酒店关键字查询
        insertSearchServicePool(pool);
        
        pool.setId(9);
        pool.setConfigId(3);
        pool.setPid(4);
        pool.setSort(1);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_banjia)); // 酒店关键字查询
        insertSearchServicePool(pool);
        
        pool.setId(10);
        pool.setConfigId(3);
        pool.setPid(1);
        pool.setSort(2);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_banjia2)); // 酒店关键字查询
        insertSearchServicePool(pool);

        pool.setId(11);
        pool.setConfigId(3);
        pool.setPid(2);
        pool.setSort(3);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_banjia2)); // 酒店关键字查询
        insertSearchServicePool(pool);
        
        pool.setId(12);
        pool.setConfigId(3);
        pool.setPid(3);
        pool.setSort(4);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_banjia2)); // 酒店关键字查询
        insertSearchServicePool(pool);
        
        pool.setId(13);
        pool.setConfigId(4);
        pool.setPid(4);
        pool.setSort(1);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_baomu)); // 酒店关键字查询
        insertSearchServicePool(pool);
        
        pool.setId(14);
        pool.setConfigId(4);
        pool.setPid(1);
        pool.setSort(2);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_baomu2)); // 酒店关键字查询
        insertSearchServicePool(pool);

        pool.setId(15);
        pool.setConfigId(4);
        pool.setPid(2);
        pool.setSort(3);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_baomu2)); // 酒店关键字查询
        insertSearchServicePool(pool);
        
        pool.setId(16);
        pool.setConfigId(4);
        pool.setPid(3);
        pool.setSort(4);
        pool.setSearchInfo(context.getResources().getString(R.string.putao_search_service_baomu2)); // 酒店关键字查询
        insertSearchServicePool(pool);

        database.setTransactionSuccessful();
        database.endTransaction();
        return true;

    }

    /**
     * 更新SearchConfigTable
     * @param bean 更新数据
     * @return 所更新数据总数
     * add by putao_lhq
     */
    public int updateSearchConfig(SearchConfigBean bean) {
    	try {
    		ContentValues values = new ContentValues();
    		values.put(SearchConfigTable._ID, bean.getId());
    		values.put(SearchConfigTable.ENTRY, bean.getEntry());
    		values.put(SearchConfigTable.KEYWORD, bean.getKeyword());
    		values.put(SearchConfigTable.SCHDULE, bean.getSchdule());
    		int count = database.update(SearchConfigTable.TABLE_NAME, 
    				values, SearchConfigTable._ID + "=" + bean.getId(), null);
    		LogUtil.d(SearchUtils.TAG, "updateSearchConfig result: " + count);
    		if (count <= 0) {
    			insertSearchConfig(bean);
    		}
    		return count;
		} catch (Exception e) {
			LogUtil.e(TAG, "updateSearchConfig exception: " + e);
			return -1;
		}
    }
    
    /**
     * 更新SearchServicePoolTable
     * @param bean 更新数据
     * @return 所更新数据总数
     * add by putao_lhq
     */
    public int updateSearchSevicePool(SearchServicePoolBean pool) {
    	try {
    		ContentValues values = new ContentValues();
    		values.put(SearchServicePoolTable._ID, pool.getId());
    		values.put(SearchServicePoolTable.CONFIG_ID, pool.getConfigId());
    		values.put(SearchServicePoolTable.PID, pool.getPid());
    		values.put(SearchServicePoolTable.SEARCH_INFO, pool.getSearchInfo());
    		values.put(SearchServicePoolTable.SORT, pool.getSort());
    		
    		int count = database.update(SearchServicePoolTable.TABLE_NAME, 
    				values, SearchServicePoolTable._ID + "=" + pool.getId(), null);
    		LogUtil.d(SearchUtils.TAG, "updateSearchSevicePool result: " + count);
    		if (count <= 0) {
    			insertSearchServicePool(pool);
    		}
    		return count;
		} catch (Exception e) {
			LogUtil.e(TAG, "updateSearchSevicePool exception " + e);
			return -1;
		}
    }
    
    /**
     * 更新SearchProviderTable
     * @param bean 更新数据
     * @return 所更新数据总数
     * add by putao_lhq
     */
    public int updateSearchProvider(SearchProvider provider) {
    	ContentValues values = new ContentValues();
    	values.put(SearchProviderTable._ID, provider.getId());
    	values.put(SearchProviderTable.ENTRY_TYPE, provider.getEntryType());
    	values.put(SearchProviderTable.NAME, provider.getName());
    	values.put(SearchProviderTable.SERVICE_NAME, provider.getServiceName());
    	values.put(SearchProviderTable.STATUS, provider.getStatus());
    	
    	int count = database.update(SearchProviderTable.TABLE_NAME, 
    			values, SearchProviderTable._ID + "=" + provider.getId(), null);
//    	LogUtil.d(SearchUtils.TAG, "updateSearchProvider result: " + count);
    	if (count <= 0) {
    		insertSearchProvider(provider);
    	}
    	return count;
    }
    
    /**
     * 从search_config中删除数据
     * @param bean 需要删除的数据
     * @return
     * add by putao_lhq
     */
    public int deleteSearchConfig(SearchConfigBean bean) {
    	return database.delete(SearchConfigTable.TABLE_NAME, SearchConfigTable._ID + "=" + bean.getId(), null);
    }
    
    /**
     * 从search_service_pool中删除数据
     * @param bean 需要删除的数据
     * @return
     * add by putao_lhq
     */
    public int deleteSearchServicePool(SearchServicePoolBean bean) {
    	return database.delete(SearchServicePoolTable.TABLE_NAME, SearchServicePoolTable._ID + "=" + bean.getId(), null);
    }
    
    /**
     * 从search_provider中删除数据
     * @param bean 需要删除的数据
     * @return
     * add by putao_lhq
     */
    public int deleteSearchProvider(SearchProvider bean) {
    	return database.delete(SearchProviderTable.TABLE_NAME, SearchProviderTable._ID + "=" + bean.getId(), null);
    }
    
}
