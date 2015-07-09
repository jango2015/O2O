package so.contacts.hub.search;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.db.SearchConfigDB;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.search.bean.QrySearchStrategyReq;
import so.contacts.hub.search.bean.QrySearchStrategyRsp;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.search.bean.SearchStrategyBean;
import so.contacts.hub.search.bean.UpdateSearchDataRequest;
import so.contacts.hub.search.bean.UpdateSearchDataResponse;
import so.contacts.hub.search.factory.DianpingSearchFactory;
import so.contacts.hub.search.factory.GaodeSearchFactory;
import so.contacts.hub.search.factory.PutaoSearchFactory;
import so.contacts.hub.search.factory.SougouSearchFactory;
import so.contacts.hub.ui.yellowpage.bean.SearchConfigBean;
import so.contacts.hub.ui.yellowpage.bean.SearchProvider;
import so.contacts.hub.ui.yellowpage.bean.SearchServicePoolBean;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.content.SharedPreferences;

import com.coolcloud.uac.android.common.util.TextUtils;
import com.mdroid.core.http.IgnitedHttpResponse;
import com.mdroid.core.util.SystemUtil;
import com.yulong.android.contacts.discover.R;

public class SearchUtils {
    
    public static final int SEARCH_ENTRY_CATEGORY = 1; // 搜索入口：分类搜索
    public static final int SEARCH_ENTRY_KEYWORD = 2; // 搜索入口：关键字搜索
    public static final int SEARCH_ENTRY_NUMBER = 3; // 搜索入口：号码搜索
	public static String TAG = "search";
    
    /**
     * 是否有本地数据存在，临时判断，不是非常准确。
     * TODO
     * @return
     */
    public static boolean hasData() {
        SearchConfigDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getSearchConfigDB();
        return db.hasData();
    }
    
    public static boolean initDefaultSearchConfig() {
        SearchConfigDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getSearchConfigDB();
        return db.insertDefaultConfig(ContactsApp.getInstance());
    }
    
    public static List<SearchTask> getDefSearchNumberTaskList(String searchName, SearchInfo defSearchInfo) {
        List<SearchTask> tastList = new ArrayList<SearchTask>();
        
        SearchConfigBean configBean = new SearchConfigBean();
        configBean.setId(1);
        configBean.setEntry(1);
        configBean.setKeyword(searchName);
        configBean.setSchdule(1);
        
        SearchProvider provider3 = new SearchProvider();
        provider3.setId(3);
        provider3.setName(ContactsApp.getInstance().getResources().getString(R.string.putao_search_provider_sogou));
        provider3.setEntryType(2);
        provider3.setStatus(0);
        provider3.setServiceName(SougouSearchFactory.class.getName());
        
        SearchTask task3 = new SearchTask();
        task3.setId(2);
        task3.setBussEntry(configBean.getEntry());
        task3.setHasMore(true);
        task3.setProvider(provider3);
        task3.setSort(3);
        task3.setType(SearchTask.SEARCH_TASK_TYPE_DEFAULT);
        task3.setSearchInfo(defSearchInfo);
        
        tastList.add(task3);
        
        return tastList;
    }
    
    /**
     * 获取搜索 配置 Url
     * 增加参数 channel(渠道) 和 version(版本)
     * add by zjh 2015-02-06
     * @return
     */
    private static String getSearchSoltionUrl(String searchName){
        StringBuffer strBuffer = new StringBuffer(Config.SEARCH.SEARCH_SOLTION_URL);
        strBuffer.append(searchName);
        String channelNo = SystemUtil.getChannelNo(ContactsApp.getInstance());
        if( !TextUtils.isEmpty(channelNo) ){
            strBuffer.append("&channel=");
            strBuffer.append(channelNo);
        }
        int versionCode = SystemUtil.getAppVersionCode(ContactsApp.getInstance());
        if( versionCode > 0 ){
            strBuffer.append("&version=");
            strBuffer.append(versionCode);
        }
        return strBuffer.toString();
    }
    
    /**
     * 从服务端获取搜索策略，包括搜索数据源、顺序、搜索名称和类别
     * @param searchName
     * @param defSearchInfo
     * @return
     */
    public static List<SearchTask> getRemoteSearchTaskList(String searchName) {
        final QrySearchStrategyReq requestData = new QrySearchStrategyReq();
        
        IgnitedHttpResponse httpResponse;
        List<SearchTask> tastList = null;
        try {
            //modity ljq 2015-02-06 start 同步 搜索中获取搜索策略时的请求接口添加两个参数（channel，version）
//            String url = Config.SEARCH.SEARCH_SOLTION_URL + searchName; //9200 //old code
            String url = getSearchSoltionUrl(searchName);
            //modity ljq 2015-02-06 end 同步 搜索中获取搜索策略时的请求接口添加两个参数（channel，version）
            httpResponse = Config.getApiHttp().post(url, requestData.getData()).send();
            String content = httpResponse.getResponseBodyAsString();
            
            QrySearchStrategyRsp responseData = requestData.getObject(content);
            if (responseData != null) {
                if (responseData.isSuccess()) {
                    LogUtil.d(TAG, "getRemoteSearchTaskList content="+content);
                    List<SearchStrategyBean> searchBeanList = responseData.getResult();
                    
                    if(responseData.getHits() > 0 && searchBeanList != null) {
                        tastList = getSearchTaskBySearchBean(searchBeanList, searchName);
                        // 保存搜索配置缓存数据
                        Config.getDatabaseHelper().getYellowPageDBHelper().insertSearchConfigCacheList(searchBeanList, searchName);
                    }
                }
            }
        } catch (Exception e) {
        	LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        
        return tastList;
    }
    
    public static List<SearchTask> getSearchTaskBySearchBean(List<SearchStrategyBean> searchBeanList, String searchName){
    	if( searchBeanList == null || searchBeanList.size() == 0 ){
    		return null;
    	}
    	List<SearchTask> tastList = new ArrayList<SearchTask>();
    	int size = searchBeanList.size();
    	for(int i = 0; i < size; i++){
    		SearchStrategyBean strategyBean = searchBeanList.get(i);

    		SearchInfo searchInfo = new SearchInfo();
    		searchInfo.setWords(searchName);
    		searchInfo.setCategory(strategyBean.getCategory());
    		
            SearchProvider provider = new SearchProvider();
            provider.setId(i);
            provider.setName(strategyBean.getService_name());
            provider.setEntryType(3);
            provider.setStatus(0);
            provider.setServiceName(strategyBean.getFactory());
            
            SearchTask task = new SearchTask();
            task.setId(i);
            task.setBussEntry(1);
            task.setHasMore(true);
            task.setProvider(provider);
            task.setSort(strategyBean.getSort());
            task.setOrderBy(strategyBean.getOrderby());
            task.setType(SearchTask.SEARCH_TASK_TYPE_SERVER);
            task.setSearchInfo(searchInfo);
            
            tastList.add(task);
    	}
    	return tastList;
    }
    
    public static List<SearchTask> getDefSearchTaskList(String searchName, SearchInfo defSearchInfo) {
        // 找不到配置项，使用默认搜索方案
        // 这里自定义搜索数据商和搜索顺序1大众2-高德3-搜狗
        List<SearchTask> tastList = new ArrayList<SearchTask>();
        
        SearchConfigBean configBean = new SearchConfigBean();
        configBean.setId(1);
        configBean.setEntry(1);
        configBean.setKeyword(searchName);
        configBean.setSchdule(1);
        
        SearchProvider provider = new SearchProvider();
        provider.setId(1);
        provider.setName(ContactsApp.getInstance().getResources().getString(R.string.putao_search_provider_dianping));
        provider.setEntryType(3);
        provider.setStatus(0);
        provider.setServiceName(DianpingSearchFactory.class.getName());
        
        SearchTask task = new SearchTask();
        task.setId(1);
        task.setBussEntry(configBean.getEntry());
        task.setHasMore(true);
        task.setProvider(provider);
        task.setSort(1);
        task.setType(SearchTask.SEARCH_TASK_TYPE_DEFAULT);
        task.setSearchInfo(defSearchInfo);
        
        tastList.add(task);
        
        SearchProvider provider2 = new SearchProvider();            
        provider2.setId(2);
        provider2.setName(ContactsApp.getInstance().getResources().getString(R.string.putao_search_provider_gaode));
        provider2.setEntryType(3);
        provider2.setStatus(0);
        provider2.setServiceName(GaodeSearchFactory.class.getName());
        
        SearchTask task2 = new SearchTask();
        task2.setId(2);
        task2.setBussEntry(configBean.getEntry());
        task2.setHasMore(true);
        task2.setProvider(provider2);
        task2.setSort(2);
        task2.setType(SearchTask.SEARCH_TASK_TYPE_DEFAULT);
        task2.setSearchInfo(defSearchInfo);
        
        tastList.add(task2);

        SearchProvider provider3 = new SearchProvider();
        provider3.setId(3);
        provider3.setName(ContactsApp.getInstance().getResources().getString(R.string.putao_search_provider_sogou));
        provider3.setEntryType(2);
        provider3.setStatus(0);
        provider3.setServiceName(SougouSearchFactory.class.getName());
        
        SearchTask task3 = new SearchTask();
        task3.setId(2);
        task3.setBussEntry(configBean.getEntry());
        task3.setHasMore(true);
        task3.setProvider(provider3);
        task3.setSort(3);
        task3.setType(SearchTask.SEARCH_TASK_TYPE_DEFAULT);
        task3.setSearchInfo(defSearchInfo);
        
        tastList.add(task3);
        
        return tastList;
    }
    
    /**
     * 按照关键字和入口点建立solution
     * @param entry 1-附近的搜索 2-关键字搜索 3-号码搜索
     * @param keyword
     * @param putaoIntervene 是否需要葡萄静态数据干预结果
     * @param putaoInternet 是否需要使用网络搜索方案
     * @return Solution
     */
    public static Solution createSolution(String searchName, int entry, SearchInfo defSearchInfo, boolean putaoIntervene, boolean putaoInternet) {
        Solution sol = new Solution();
        
        List<SearchTask> searchTaskList = Config.getDatabaseHelper().getSearchConfigDB().qrySearchTaskList(searchName, entry, defSearchInfo);
        if(searchTaskList == null || searchTaskList.size() == 0) {
            if(defSearchInfo == null) {
                return null;
            } else if(entry == SearchUtils.SEARCH_ENTRY_NUMBER) {  // 输入的是数字查号
            	searchTaskList = getDefSearchNumberTaskList(searchName, defSearchInfo);
            	if(searchTaskList != null) {
            		sol.setHit(Solution.SOLUTION_HIT_DEFAULT);
            	}
            } else {                                             // 入口是附近的xx和关键字查询
                if(putaoInternet) {
                    searchTaskList = getRemoteSearchTaskList(searchName);  // 网络获取搜索策略
                    if(searchTaskList != null) {
                        sol.setHit(Solution.SOLUTION_HIT_SERVER);
                    }
                } else {
            		searchTaskList = getDefSearchTaskList(searchName, defSearchInfo);  // 本地获取搜索策略
            		if(searchTaskList != null) {
                		sol.setHit(Solution.SOLUTION_HIT_DEFAULT);
                	}
            	} 
            }
        } else {
        	sol.setHit(Solution.SOLUTION_HIT_LOCAL);
        }
        
        if(searchTaskList == null) {
        	searchTaskList = new ArrayList<SearchTask>();
        }
        
        if( putaoIntervene ){
        	// 添加本地服务及数据搜索
        	SearchTask searchTask = new SearchTask();
        	SearchProvider provider = new SearchProvider();
        	provider.setId(0);
            provider.setName(ContactsApp.getInstance().getResources().getString(R.string.putao_search_provider_putao));
            provider.setEntryType(3);
            provider.setStatus(0);
            provider.setServiceName(PutaoSearchFactory.class.getName());
            searchTask.setProvider(provider);
        	searchTask.setSearchInfo(defSearchInfo);
        	searchTask.setSort(0);
        	searchTask.setType(SearchTask.SEARCH_TASK_TYPE_LOCAL);
        	
        	// 添加作为第一条任务
        	searchTaskList.add(0, searchTask);
        }
        
        if(searchTaskList != null && searchTaskList.size() > 0) {
        	sol.setTaskList(searchTaskList);        
        }
        return sol;
    }
    
    /**
     * 远程配置搜索顺序
     * add by putao_lhq
     */
    public static void doUpdateSearchDataRequest() {

    	LogUtil.d(TAG, "enter doUpdateSearchDataRequest");
        SearchConfigDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getSearchConfigDB();
        UpdateSearchDataResponse dataResponse = null;
        int dataVersion = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().
        		queryDataVersion(YellowPageDB.YELLOW_DATA_VERSION_SEARCH);
        LogUtil.d(TAG, "location version: " + dataVersion);
		UpdateSearchDataRequest dataRequest = new UpdateSearchDataRequest(dataVersion);
        IgnitedHttpResponse httpResponse;
        try {
            httpResponse = Config.getApiHttp().post(Config.SERVER, dataRequest.getData()).send();
            String content = httpResponse.getResponseBodyAsString();
            dataResponse = dataRequest.getObject(content);

            if (dataResponse != null && dataResponse.isSuccess()) {
                db.getSQLiteDatabase().beginTransaction();
                
                List<SearchConfigBean> configList = dataResponse.configList;
                List<SearchServicePoolBean> servicePoolList = dataResponse.servicePoolList;
                List<SearchProvider> providerList = dataResponse.providerList;
                // 更新search_config数据
                updateSearchConfig(db, configList);
                // 更新search_service_pool数据
                updateSearchSevicePool(db, servicePoolList);
                // 更新search_provider数据
                updateSearchProvider(db, providerList);
                
                // 更新数据版本
                LogUtil.d(TAG, "server version: " + dataResponse.data_version);
                ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().
                	updateDataVersion(YellowPageDB.YELLOW_DATA_VERSION_SEARCH, dataResponse.data_version);
                
                db.getSQLiteDatabase().setTransactionSuccessful();
                db.getSQLiteDatabase().endTransaction();
            } else {
            	LogUtil.i(TAG, "dataResponse is null or is not success");
            }
        } catch (ConnectException e) {
        	LogUtil.i(TAG, "ConnectException: " + e);
            e.printStackTrace();
        } catch (IOException e) {
        	LogUtil.i(TAG, "IOException: " + e);
            e.printStackTrace();
        }
    }

    /**
     * add by putao_lhq
     */
	private static void updateSearchProvider(SearchConfigDB db,
			List<SearchProvider> providerList) {
		LogUtil.d(TAG, "updateSearchProvider");
		if (providerList != null && providerList.size() > 0) {
		    for (SearchProvider provider : providerList) {
		    	//LogUtil.d(TAG, "updateSearchProvider: " + provider.toString());
		        switch (provider.getAction()) {
		            case 0:// update
		                db.updateSearchProvider(provider);
		                break;
		            case 1:// insert
		                db.insertSearchProvider(provider);
		                break;
		            case 2:// delete
		                db.deleteSearchProvider(provider);
		                break;
		            default:
		                break;
		        }
		    }
		}
	}

    /**
     * add by putao_lhq
     */
	private static void updateSearchSevicePool(SearchConfigDB db,
			List<SearchServicePoolBean> servicePoolList) {
		LogUtil.d(TAG, "updateSearchSevicePool");
		if (servicePoolList != null && servicePoolList.size() > 0) {
		    for (SearchServicePoolBean poolBean : servicePoolList) {
		    	//LogUtil.i(TAG, "updateSearchSevicePool: " + poolBean.toString());
		        switch (poolBean.getAction()) {
		            case 0:// update
		                db.updateSearchSevicePool(poolBean);
		                break;
		            case 1:// insert
		                db.insertSearchServicePool(poolBean);
		                break;
		            case 2:// delete
		                db.deleteSearchServicePool(poolBean);
		                break;
		            default:
		                break;
		        }
		    }
		}
	}

    /**
     * 
     * @param db
     * @param configList
     * add by putao_lhq
     */
	private static void updateSearchConfig(SearchConfigDB db,
			List<SearchConfigBean> configList) {
		LogUtil.d(TAG, "updateSearchConfig");
		if (configList != null && configList.size() > 0) {
		    for (SearchConfigBean searchConfig : configList) {
//		    	LogUtil.i(TAG, "updateSearchConfig: " + searchConfig.toString());
		        switch (searchConfig.getAction()) {
		            case 0:// update
		                db.updateSearchConfig(searchConfig);
		                break;
		            case 1:// insert
		                db.insertSearchConfig(searchConfig);
		                break;
		            case 2:// delete
		                db.deleteSearchConfig(searchConfig);
		                break;
		            default:
		                break;
		        }
		    }
		}
	}
	
	/**
	 * 是否启用网络搜索策略,默认为true
	 * @return
	 */
	public static boolean isUseNetSearchStrategy() {
		SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
				ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
		
		return pref.getBoolean(ConstantsParameter.USE_NET_SEARCH_STRATEGY, true);
	}
	
	/**
	 * 设置网络搜索策略
	 * @return
	 */	
	public static void setNetSearchStrategy(boolean useNetSearchStrategy) {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        pref.edit().putBoolean(ConstantsParameter.USE_NET_SEARCH_STRATEGY, useNetSearchStrategy).commit();
	}
	
}
