package so.contacts.hub.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import com.yulong.android.contacts.discover.R;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.Config;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.search.bean.SearchStrategyBean;
import so.contacts.hub.search.factory.PutaoSearchFactory;
import so.contacts.hub.search.factory.SougouSearchFactory;
import so.contacts.hub.search.rule.IMatchRule;
import so.contacts.hub.search.rule.SimilarUtils;
import so.contacts.hub.ui.yellowpage.bean.SearchProvider;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import so.putao.findplug.YelloPageItem;

@SuppressLint("UseSparseArrays")
@SuppressWarnings("rawtypes")
public class SearchStrategyController implements SearchResultListener {
	public static final String TAG = SearchStrategyController.class.getSimpleName();
	
	private Context mContext = null;

	private SearchInfo mSearchInfo;
	
	private SchduleManager mLocSchduler;
	
	private ConcurrentSchduleManager mConSchduler;
	
	private Thread mRemoteThread;
	
	private boolean mRemoteThreadStopped;
	
	private SearchResultListener mListener;
	
	private Handler mMainCallbackHandler;
	
	private IMatchRule mMatchRuleImpl;
	
	private int mConStrategyMode;
	
	private volatile AtomicBoolean mBusy;
	
	private Map<Integer, List<YelloPageItem> > mItemMaps;
	private List<YelloPageItem> mItemList;
	
	private int mRecvSolutionCnt = 0;
	
	// 总共需要执行的Solution数量
	private int mExecuSolutionTotal = 2;
	
	// 标志是否结束（结束之后不接受回调）
	private boolean mHasStopped = false;
	
	// 用网络策略
	private boolean mUseNetStrategy = false;
	
	public SearchStrategyController(Context context) {
		mContext = context;
        mBusy = new AtomicBoolean(false);
        mConStrategyMode = ConcurrentSchduleManager.SCHDULE_MODE_WAIT_ALL;
        
        mLocSchduler = new SchduleManager();
        mConSchduler = new ConcurrentSchduleManager(mConStrategyMode);
	}
	
	public synchronized void setSearchInfo(SearchInfo searchInfo, Handler mainHandler, IMatchRule matchRule, SearchResultListener listener, boolean useNetStrategy) {
        if(mBusy.get()){
        	return;
        }

		this.mSearchInfo = searchInfo;
		this.mMainCallbackHandler = mainHandler;
		this.mMatchRuleImpl = matchRule;
		this.mListener = listener;
		this.mUseNetStrategy = useNetStrategy;
		this.mItemMaps = new HashMap<Integer, List<YelloPageItem> >();
		this.mItemList = new ArrayList<YelloPageItem>();
		this.mRemoteThread = null;
		this.mRemoteThreadStopped = false;
		
		this.mHasStopped = false;
		this.mExecuSolutionTotal = 2;
		this.mRecvSolutionCnt = 0;
	}
	
	public void search() {
        if(mBusy.getAndSet(true)) {
            LogUtil.d(TAG, "search busy======================="+mBusy.get());
            return;
        }

		if(mSearchInfo == null){
			LogUtil.e(TAG, "SearchInfo not founed.");
			return;
		}
		
		this.mExecuSolutionTotal = 2;
		this.mRecvSolutionCnt = 0;

		LogUtil.d(TAG, "search ===========================> ");
		final String searchName = mSearchInfo.getWords();
		final int entry = mSearchInfo.getEntry_type();
		
		boolean needNetSearch = true;
		if ( !NetUtil.isNetworkAvailable(ContactsApp.getInstance()) ) {
			//如果没有网络，则不进行网络数据源的搜索
			needNetSearch = false;
			mExecuSolutionTotal--;
		}
		
		// 1. 初始进入立即启动搜索‘本地服务’和‘本地数据商’
		Solution localSol = createLocationSolution(0);
		if(localSol != null && localSol.getTaskList() != null && localSol.getTaskList().size()>0) {
			localSol.setHit(Solution.SOLUTION_HIT_LOCAL);
        	mLocSchduler.setSolution(localSol, SearchStrategyController.this);
        	mLocSchduler.schdule("Local DB");
		}
		if( !needNetSearch ){
			//如果没有网络，则不进行网络数据源的搜索
			return;
		}
		
		// 2. 启动本地词库搜索
		if( mSearchInfo.getEntry_type() == SearchUtils.SEARCH_ENTRY_NUMBER ){ 
			// 2.1 号码搜索
			Solution numSol = createNumSolution(1);
			numSol.setHit(Solution.SOLUTION_HIT_SERVER);
			mConSchduler.setSolution(numSol, this);
			mConSchduler.schdule("Local Strategy - Numberic");
			
		}else{ 
			// 2.2 关键字搜索
			List<SearchTask> locSearchTaskList = Config.getDatabaseHelper().getSearchConfigDB().
					qrySearchTaskList(searchName, entry, mSearchInfo);
			if( locSearchTaskList == null || locSearchTaskList.size() == 0 ){
				// 2.2.1 本地搜索库中没有数据，则从搜索配置缓存中获取
				LogUtil.d(TAG, "search from LOCAL CACHE stategy word:"+searchName);
				List<SearchStrategyBean> searchConfigList = Config.getDatabaseHelper().getYellowPageDBHelper().querySearchConfigCache(searchName);
				if( searchConfigList != null && searchConfigList.size() > 0 ){
					locSearchTaskList = SearchUtils.getSearchTaskBySearchBean(searchConfigList, searchName);
				}
				
			} 
			
			if(locSearchTaskList != null && locSearchTaskList.size() > 0) {
				// 2.2.2 本地词库搜索击中
				LogUtil.d(TAG, "search from LOCAL stategy word:"+searchName);
				Solution sol = createSolution(locSearchTaskList, false);
				sol.setHit(Solution.SOLUTION_HIT_SERVER);
				mConSchduler.setSolution(sol, this);
				mConSchduler.schdule("Local Strategy");
				
			} else if(!mUseNetStrategy) {
				// 3. 不使用网络策略,添加默认搜索源
				// added by cj 2014-12-12 start for 增加网络搜索源开关
				locSearchTaskList = SearchUtils.getDefSearchTaskList(searchName, mSearchInfo);  // 获取默认的本地搜索策略
				if(locSearchTaskList == null || locSearchTaskList.size() == 0) {
					LogUtil.e(TAG, "can not founded def search task for word:"+searchName+" useNetStrategy="+mUseNetStrategy);
					return;
				} else {
					LogUtil.d(TAG, "search from DEFAULT stategy word:"+searchName+" useNetStrategy="+mUseNetStrategy);
					Solution sol = createSolution(locSearchTaskList, false);
					sol.setHit(Solution.SOLUTION_HIT_DEFAULT);
					mConSchduler.setSolution(sol, this);
					mConSchduler.schdule("DEFAULT Strategy");
				}
				// added by cj 2014-12-12 end
			
			} else {
				mRemoteThread = new Thread() {
					@Override
					public void run() {
						// 4. 启动网络搜索
						List<SearchTask> searchTaskList = SearchUtils.getRemoteSearchTaskList(searchName);
						if(!mRemoteThreadStopped && searchTaskList != null && searchTaskList.size() > 0) {
							// 4.1 网络词库搜索击中
							LogUtil.d(TAG, "search from NET stategy word:"+searchName);
							Solution sol = createSolution(searchTaskList, false);
							sol.setHit(Solution.SOLUTION_HIT_SERVER);
							mConSchduler.setSolution(sol, SearchStrategyController.this);
							
							if(!mRemoteThreadStopped) {
								mConSchduler.schdule("Net Strategy");
							}
						} else {
							// 4. 默认搜索策略 
							searchTaskList = SearchUtils.getDefSearchTaskList(searchName, mSearchInfo);  // 获取默认的本地搜索策略
							if(mRemoteThreadStopped || searchTaskList == null || searchTaskList.size() == 0) {
								LogUtil.e(TAG, "can not founded def search task for word:"+searchName);
								return;
							} else {
								LogUtil.d(TAG, "search from DEFAULT stategy word:"+searchName);
								Solution sol = createSolution(searchTaskList, false);
								sol.setHit(Solution.SOLUTION_HIT_DEFAULT);
								mConSchduler.setSolution(sol, SearchStrategyController.this);
								
								if(!mRemoteThreadStopped) {
									mConSchduler.schdule("DEFAULT Strategy");
								}
							}
						}
					}
				};
				mRemoteThread.start();
			}
        }
	}
	
	public void searchMore() {
//        if(mBusy.getAndSet(true)) {
//            LogUtil.d(TAG, "search more busy======================="+mBusy.get());
//            return;
//        }

        mRecvSolutionCnt = 0;
        
		LogUtil.d(TAG, "enter searchMore");
		if(mLocSchduler != null && mLocSchduler.hasMore()) {
			mLocSchduler.schdule("searchMore1");
		}else{
			mRecvSolutionCnt++;
		}
		if(mConSchduler != null && mConSchduler.hasMore()) {
			mConSchduler.schdule("searchMoreN");
		}else{
			mRecvSolutionCnt++;
		}
	}

	/**
	 * 创建本地词库搜索方案
	 * @return
	 */
    private Solution createLocationSolution(int taskSort) {
        Solution sol = new Solution();
        
       	List<SearchTask> searchTaskList = new ArrayList<SearchTask>();
        
       	// 添加本地服务及数据搜索
       	SearchTask searchTask = new SearchTask();
       	SearchProvider provider = new SearchProvider();
       	provider.setId(taskSort);
       	provider.setName(ContactsApp.getInstance().getResources().getString(R.string.putao_search_provider_putao));
       	provider.setEntryType(SearchUtils.SEARCH_ENTRY_KEYWORD);
       	provider.setStatus(0);
       	provider.setServiceName(PutaoSearchFactory.class.getName());
       	searchTask.setProvider(provider);
       	searchTask.setSearchInfo(mSearchInfo);
       	searchTask.setSort(taskSort); 
       	searchTask.setType(SearchTask.SEARCH_TASK_TYPE_LOCAL);

       	// 添加作为第一条任务
       	searchTaskList.add(searchTask);
       	sol.setTaskList(searchTaskList);
        sol.setEntry(mSearchInfo.getEntry_type());
        
		sol.setInputCity(mSearchInfo.getCity());
		sol.setInputKeyword(mSearchInfo.getWords());
		sol.setInputLatitude(mSearchInfo.getLatitude());
		sol.setInputLongtitude(mSearchInfo.getLongitude());
        
		LogUtil.d(TAG, "CreateLocalSolution: \n"+sol.toString());
        return sol;
    }	
    
    /**
	 * 创建号码搜索方案
	 * @return
	 */
    private Solution createNumSolution(int taskSort) {
        Solution sol = new Solution();
        
       	// 添加本地服务及数据搜索
       	SearchTask searchTask = new SearchTask();
       	SearchProvider provider = new SearchProvider();
       	provider.setId(taskSort);
       	provider.setName(ContactsApp.getInstance().getResources().getString(R.string.putao_search_provider_sogou));
       	provider.setEntryType(SearchUtils.SEARCH_ENTRY_NUMBER);
       	provider.setStatus(0);
       	provider.setServiceName(SougouSearchFactory.class.getName());
       	searchTask.setProvider(provider);
       	searchTask.setSearchInfo(mSearchInfo);
       	searchTask.setSort(taskSort);
       	searchTask.setType(SearchTask.SEARCH_TASK_TYPE_SERVER);

       	// 添加作为第一条任务
       	List<SearchTask> searchTaskList = new ArrayList<SearchTask>();
       	searchTaskList.add(searchTask);
       	
       	sol.setTaskList(searchTaskList);
        sol.setEntry(mSearchInfo.getEntry_type());
        sol.setActivity(mContext);
        
		sol.setInputCity(mSearchInfo.getCity());
		sol.setInputKeyword(mSearchInfo.getWords());
		sol.setInputLatitude(mSearchInfo.getLatitude());
		sol.setInputLongtitude(mSearchInfo.getLongitude());
        
		LogUtil.d(TAG, "CreateLocalSolution: \n"+sol.toString());
        return sol;
    }	

    /**
     * 创建搜索方案
     * @param searchTaskList
     * @param putaoIntervene
     * @return
     */
    private Solution createSolution(List<SearchTask> searchTaskList, boolean putaoIntervene) {
        Solution sol = new Solution();
        
        if(searchTaskList == null)
        	searchTaskList = new ArrayList<SearchTask>();
        
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
        	searchTask.setSearchInfo(mSearchInfo);
        	searchTask.setSort(0);
        	searchTask.setType(SearchTask.SEARCH_TASK_TYPE_LOCAL);
        	
        	// 添加作为第一条任务
        	searchTaskList.add(0, searchTask);
        }
        
        sol.setTaskList(searchTaskList);
        sol.setEntry(mSearchInfo.getEntry_type());
        
		sol.setInputCity(mSearchInfo.getCity());
		sol.setInputKeyword(mSearchInfo.getWords());
		sol.setInputLatitude(mSearchInfo.getLatitude());
		sol.setInputLongtitude(mSearchInfo.getLongitude());
		LogUtil.d(TAG, "CreateSolution: \n"+sol.toString());
        return sol;
    }
    
    public void stop() {
    	LogUtil.d(TAG, "stop");
    	mHasStopped = true;
    	
    	if(mRemoteThread != null && !mRemoteThread.isInterrupted()) {
    		mRemoteThreadStopped = true;
    		mRemoteThread.interrupt();
    	}
    	
    	if(mLocSchduler != null) {
    		mLocSchduler.stop();
    	}
    	
    	if(mConSchduler != null) {
    		mConSchduler.stop();
    	}
    	
    	mBusy.set(false);
    }
    
    public void release() {
    	LogUtil.d(TAG, "release");
    	mHasStopped = true;
    	
    	if(mRemoteThread != null && !mRemoteThread.isInterrupted()) {
    		mRemoteThreadStopped = true;
    		mRemoteThread.interrupt();
    	}
    	
		if(mItemMaps != null) {
			mItemMaps.clear();
		}
		
		if(mItemList != null) {
			mItemList.clear();
		}
		
		if( mLocSchduler != null ){
			mLocSchduler.release();
		}
    	
    	if(mConSchduler != null) {
    		mConSchduler.release();
    	}
    	
    	mBusy.set(false);
    }
    
    /**
     * 比较相似度，并且改变搜索顺序
     */
    private int similarCompareAndUpdateSearcSort(Map<Integer, List<YelloPageItem> > itemMaps) {
    	double simval = 0.0;
    	int sort = -1;
    	
    	if(itemMaps == null || itemMaps.size() <= 1) {
    		return -1;
    	}
    	
    	for(int i=0; i<= itemMaps.size(); i++) {
    		List<YelloPageItem> list = itemMaps.get(i);
    		if(list == null || list.size() == 0) {
    			continue;
    		} else {
    			YelloPageItem item = list.get(0);
    			if(item != null && !TextUtils.isEmpty(item.getName())) {
    				double val = SimilarUtils.sim(mSearchInfo.getWords(), item.getName()); 
    				if(Double.compare(val, simval) > 0 && (val*100)>50) {
    					simval = val;
    					sort = i;
    				}
    			}
    		}
    	}
    	
    	if(sort > 0) {
    		final SearchTask firstTask = mConSchduler.liveUpToHead(sort);
    		if(firstTask != null) {
    			LogUtil.d(TAG, "liveUpToHead simval="+simval+" sort="+sort+" task="+firstTask.toString());
    			
    			// 删除itemMaps里不需要返回的数据，只返回排名第一的数据
    	    	for(int i=0; i<= itemMaps.size(); i++) {
    	    		List<YelloPageItem> list = itemMaps.get(i);
    	    		if(list == null || list.size() == 0) {
    	    			continue;
    	    		} else if(sort != i) {
    	    			itemMaps.remove(i);
    	    		}
    	    	}
    		}
    	}
    	
    	return sort;
    }
    
    /**
     * 还有更多未执行的任务,该策略是把多个任务拆解成2个solution
     * 第一个是本地服务搜索
     * 第二个是网络搜索
     * @return
     */
    public synchronized boolean hasMoreTask() {
   		return mRecvSolutionCnt < mExecuSolutionTotal;
    }
    
	@Override
	public synchronized void onResult(Solution sol, Map<Integer, List<YelloPageItem> > itemMaps, List<YelloPageItem> itemList, boolean hasMore) {
		if( mHasStopped ){
			return;
		}
		if(sol != null) {
			mRecvSolutionCnt++;
		}
		if(mListener != null) {
			if(sol.getHit() == Solution.SOLUTION_HIT_LOCAL) {
				final SearchTask task = sol.getTaskList().get(0);
				LogUtil.d(TAG, "onResult sol: "+sol.toString());
				if(mItemMaps != null && itemList != null) {
					mItemMaps.put(task.getSort(), itemList);
				}
				if(mItemList != null && itemList != null) {
					mItemList.addAll(itemList);
				}
				callback(sol, mItemMaps, mItemList, hasMore);
			} else {
				if(mConStrategyMode == ConcurrentSchduleManager.SCHDULE_MODE_WAIT_ALL) {
					
					// 如果是SCHDULE_MODE_WAIT_ALL模式，则所有返回的ConCurrentSchduleManager都是全部返回后的结果
					if(mItemMaps != null && itemList != null) {
						mItemMaps.putAll(itemMaps);
					}
					
					// mItemList暂时不返回，要返回要把不要排序的全删除
					if(mItemList != null && itemList != null) {
						mItemList.addAll(itemList);
					}
					LogUtil.d(TAG, "onResult sol:"+sol.toString());
					callback(sol, mItemMaps, mItemList, hasMore); 
				}
			}
		}
	}
	
	private void callback(final Solution sol,final Map<Integer, List<YelloPageItem> > itemMaps, final List<YelloPageItem> itemList, final boolean hasMore) {
		if( mHasStopped ){
			return;
		}
		if(mListener != null) {
			if(!hasMoreTask()) {
				mBusy.set(false); 
			}
			mListener.onResult(sol, itemMaps, itemList, hasMore);
			mItemMaps.clear();
			mItemList.clear();
		}
//        if(mMainCallbackHandler == null) {
//        } else {
//        	mMainCallbackHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mListener != null) {
//                        mBusy.set(false); 
//                        mListener.onResult(sol, itemMaps, itemList, hasMore);
//                        mItemMaps.clear();
//                        mItemList.clear();
//                    }
//                }
//            });
//        }
	}
	
	public synchronized boolean hasMore(){
		if( mConSchduler != null && mLocSchduler != null ){
			if( mConSchduler.hasMore() || mLocSchduler.hasMore() ){
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean hasLocalMore(){
		if( mLocSchduler != null ){
			return mLocSchduler.hasMore();
		}
		return false;
	}
	
	public synchronized boolean hasNetworkMore(){
		if( mConSchduler != null ){
			return mConSchduler.hasMore();
		}
		return false;
	}
	
	public synchronized String getOrderBy(int sort){
		if(mConSchduler != null){
			return mConSchduler.getOrderBy(sort);
		}
		return null;
	}
	
}
