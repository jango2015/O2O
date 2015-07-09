package so.contacts.hub.search;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import so.contacts.hub.search.rule.IMatchRule;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.YelloPageItem;

@SuppressLint("UseSparseArrays")
@SuppressWarnings("rawtypes")
public class ConcurrentSchduleManager implements SearchResultListener {
    private static final String TAG = "ConcurrentSchduleManager";
    
    private Solution mSolution;
    
    private SearchResultListener mListener;
    
    private IMatchRule mMatchRule;
    
    private AtomicBoolean mBusy;  // 调度中心忙，稍后再试
    
    private List<SchduleManager> mConManagerList;
    
    private boolean mHasMore = true;
    
    public static final int SCHDULE_MODE_WAIT_ALL = 0; 		// 等待所有搜索结果后再返回 
    public static final int SCHDULE_MODE_FIFO = 1;     	 	// 先来先返回
    public static final int SCHDULE_MODE_SORTED = 2;     	// 按照排序先后返回
    
    private int mSchduleMode;
    
    private Map<Integer, List<YelloPageItem> > mWaitMaps;
    
    private int mTaskTotals;						// 任务队列总数量
    
    private int mRecvCallbackTotal;					// 接收回调总数
    private List<YelloPageItem> mWaitList = null;	// 等待所有数据返回后一起返回
    
    private boolean mCallbackWaits[];           	// 回调等待返回的任务 
    private short mCallbackIdx;						// 返回的索引值

    public ConcurrentSchduleManager() {
        mBusy = new AtomicBoolean(false);
        mSchduleMode = SCHDULE_MODE_WAIT_ALL;
        mHasMore = true;
    }
    
    public ConcurrentSchduleManager(int mode) {
        mBusy = new AtomicBoolean(false);
        mSchduleMode = mode;
        mHasMore = true;
    }
    
    public synchronized void setSolution(Solution sol, SearchResultListener listener) {
        /**
         * 修复当任务回调之前当前方法被掉引起的crash问题
         * added by cj 2014/10/15 start
         */
        if(mBusy.get() || sol == null || sol.getTaskList() == null || sol.getTaskList().size() == 0){
        	return;
        }
        // added by cj 2014/10/15 end
        mHasMore = true;
        this.mSolution = sol.clone();
        this.mListener = listener;
        if( mConManagerList != null ){
        	for(SchduleManager schduleManager : mConManagerList){
        		schduleManager.release();
        	}
        	mConManagerList.clear();
        }
        this.mConManagerList = new ArrayList<SchduleManager>();
        this.mWaitMaps = new HashMap<Integer, List<YelloPageItem>>();
        this.mWaitList = new ArrayList<YelloPageItem>();
        this.mTaskTotals = sol.getTaskList().size();
        this.mRecvCallbackTotal = 0;
        this.mCallbackWaits = new boolean[mTaskTotals];
        this.mCallbackIdx = -1;

        int i = 0;
        for(SearchTask task : sol.getTaskList()) {
        	SchduleManager schManager = new SchduleManager();
            Solution t_sol = sol.cloneButAddTask(task);
            t_sol.setHasMore(true);         //创建搜索任务时，均为true
            t_sol.setMainHandler(null);		// 让子任务使用非主线程回调
            t_sol.setHit(sol.getHit());
        	schManager.setSolution(t_sol, this);
        	
            mCallbackWaits[i++] = false;
            this.mConManagerList.add(schManager);
        }
    }
    
    public void schdule(String title) {
        if(mBusy.getAndSet(true)) {
            LogUtil.d(TAG, "schdule "+title+" busy======================="+mBusy.get());
            return;
        }
        
        if(mSolution == null || mListener == null) {
            LogUtil.d(TAG, "schdule mBusy="+mBusy.get()+" mSolution="+mSolution+" return 1");
            if(mListener != null) {
                mBusy.set(false);
                mListener.onResult(mSolution, null, null, false);
            }
            return;
        } else if(mSolution != null && mSolution.getTaskList().size() == 0) {
            LogUtil.d(TAG, "schdule mBusy="+mBusy.get()+" return 2");
            if(mListener != null) {
                mBusy.set(false);
                mListener.onResult(null, null, null, false);
            }
            return;
        }
        
        for(SchduleManager schManager : this.mConManagerList) {
        	schManager.schdule(title);
        }
    }

    // 退出时中断正在阻塞在网络请求上的线程
    // added by cj 2014/10/28 start
    public synchronized void stop() {
        LogUtil.d(TAG, "stop"); 
        
        if(mConManagerList != null) {
	        for(SchduleManager schManager : this.mConManagerList) {
	        	schManager.stop();
	        }
        }
    }
    // added by cj 2014/10/28 end

    public synchronized void release() {
        LogUtil.d(TAG, "release"); 
        
        if(mWaitMaps != null) {
        	mWaitMaps.clear();
        }
        
        if(mWaitList != null) {
        	mWaitList.clear();
        }
        
        if(mConManagerList != null) {
	        for(SchduleManager schManager : this.mConManagerList) {
	        	schManager.release();
	        }
        }
    }
    
    /**
     * 升级之前排序在sort位置的任务到第一个，其他任务全部设置为hasMore=false
     * @param sort
     */
    public SearchTask liveUpToHead(int sort) {
    	SearchTask firstTask = null;
    	synchronized (mConManagerList) {
    		for(SchduleManager schMgt : mConManagerList) {
    			Solution sol = schMgt.getSolution();
    			SearchTask task = sol.getTaskList().get(0);
    			if(1 == sort) {
    				task.setSort(1);
    				firstTask = task;
    			} else {
    				sol.setHasMore(false);
    				task.setHasMore(false);
    			}
    		}
		}
    	
    	return firstTask;
    }
    
    private boolean calcHasMore() {
    	boolean hasMore = false;
    	if( mConManagerList != null ){
    		for(SchduleManager schManager : this.mConManagerList) {
    			if(schManager.getSolution() != null && schManager.getSolution().isHasMore()) {
    				hasMore = true;
    				break;
    			}
    		}
    	}
    	LogUtil.i(TAG, "calcHasMore hasMore: " + hasMore);
        return hasMore;
    }
    
    public boolean hasMore() {
    	return mHasMore;
    }
    
	@Override
	public synchronized void onResult(Solution sol, Map<Integer, List<YelloPageItem> > itemMaps, List<YelloPageItem> itemList,
			boolean hasMore) {
		LogUtil.d(TAG, "onResult mode="+mSchduleMode+" "+sol.toString());
		
		++mRecvCallbackTotal;
		if (mSchduleMode == SCHDULE_MODE_WAIT_ALL) {
			LogUtil.d(TAG, "onResult mRecvCallbackTotal="+(mRecvCallbackTotal+1)+" sol.hasMore="+mHasMore+" itemList.size="+(itemList!=null?itemList.size():0)+" hasMore="+hasMore);
			
			final SearchTask task = sol.getTaskList().get(0);
			int sort = task.getSort();
			
			// 把所有任务添加到mWaitMaps中
			// 如果全部接收完成，则按照顺序加入到mWaitList队列中
			mWaitMaps.put(sort, itemList);
			if(mRecvCallbackTotal == mTaskTotals) {
				// 所有本地、网络搜索任务完成后，按sort顺序返回
				for(int i=0; i<=mTaskTotals; i++) {  // i<=mTaskTotals是必须
					List<YelloPageItem> list = mWaitMaps.get(i);
					if(list != null && list.size() > 0) {
						mWaitList.addAll(list);
					}
				}
				callback(sol, mWaitMaps, mWaitList, hasMore);
			}

		} else if (mSchduleMode == SCHDULE_MODE_FIFO) {
			
			callback(sol, mWaitMaps, itemList, hasMore);
		} else if (mSchduleMode == SCHDULE_MODE_SORTED) {
			/**
			 * BUG:该功能未完全调通，该mode禁止使用
			 */
			if(sol.getTaskList() != null && sol.getTaskList().size() == 1) {
				int sort = sol.getTaskList().get(0).getSort();
				if(mCallbackIdx+1 == sort) {
					LogUtil.d(TAG, "onResult sort="+sort+" itemList.size="+(itemList!=null?itemList.size():0)+" hasMore="+hasMore);
					callback(sol, mWaitMaps, itemList, hasMore);
					++mCallbackIdx;
					
					while(mCallbackIdx+1 < mCallbackWaits.length && mCallbackWaits[mCallbackIdx+1]) {
						LogUtil.d(TAG, "onResult sort="+sort+" itemList.size="+(itemList!=null?itemList.size():0)+" hasMore="+hasMore);
						callback(sol, mWaitMaps, itemList, hasMore);
						++mCallbackIdx;
					}
				}
			}
		}
	}
    
    private void callback(Solution sol, final Map<Integer, List<YelloPageItem> > itemMaps, final List<YelloPageItem> itemList, boolean hasMore) {
        // 检查solution是否仍然有更多数据未搜索
    	mHasMore = calcHasMore();
    	mSolution.setHasMore(mHasMore);

        if(mSolution.getMainHandler() == null) {
            if(mListener != null) {
            	if(mRecvCallbackTotal == mTaskTotals) {
            		mBusy.set(false); 
            		mRecvCallbackTotal = 0;
            	}
                mListener.onResult(mSolution, itemMaps, itemList, mHasMore);
				mWaitList.clear();
				mWaitMaps.clear();
            }
        } else {
            mSolution.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                    	if(mRecvCallbackTotal == mTaskTotals) {
                    		mBusy.set(false); 
                    		mRecvCallbackTotal = 0;
                    	}
                        mListener.onResult(mSolution, itemMaps, itemList, mHasMore);
                        mWaitList.clear();
                        mWaitMaps.clear();
                    }
                }
            });
        }
    }
    
    public String getOrderBy(int sort){
    	if( mSolution != null ){
    		List<SearchTask> taskList = mSolution.getTaskList();
    		if( taskList == null || taskList.size() == 0 ){
    			return null;
    		}
    		for(SearchTask task : taskList){
    			if(sort == task.getSort()){
    				return task.getOrderBy();
    			}
    		}
    	}
    	return null;
    }

}
