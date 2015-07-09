package so.contacts.hub.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import so.contacts.hub.search.Searchable;
import so.contacts.hub.ui.yellowpage.bean.SearchProvider;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.YelloPageItem;

public class SchduleManager {
    private static final String TAG = "SchduleManager";
    
    private Solution mSolution;
    
    private int mCurrentTaskIdx = 0;
    
    private SearchTask mCurrentTask;
    
    private SearchResultListener mListener;
    
    private AtomicBoolean mBusy;  // 调度中心忙，稍后再试

    private HashMap<String, Searchable> mFactoryCache;
    
    private HandlerThread mQueryThread = null;
    
    private Handler mQueryHandler = null;
    
    public SchduleManager() {
        mBusy = new AtomicBoolean(false);
        mCurrentTaskIdx = -1;
        mCurrentTask = null;
        mFactoryCache = new HashMap<String, Searchable>();
        
        mQueryThread = new HandlerThread("SchduleManager#1");
        mQueryThread.start();
        mQueryHandler = new QueryHandler(mQueryThread.getLooper());
    }
    
    public synchronized void setSolution(Solution sol, SearchResultListener listener) {
        /**
         * 修复当任务回调之前当前方法被掉引起的crash问题
         * added by cj 2014/10/15 start
         */
        if(mBusy.get() || sol == null){
        	return;
        }
        // added by cj 2014/10/15 end
        this.mSolution = sol.clone();
        this.mListener = listener;
        this.mCurrentTaskIdx = -1;
        this.mCurrentTask = null;
        if(mFactoryCache != null){
        	mFactoryCache.clear();
        }
    }
    
    public Solution getSolution() {
    	return this.mSolution;
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
        
        final int total = mSolution.getTaskList().size();
        
        boolean hasMore = false;        
        if(mCurrentTaskIdx < 0) {
            mCurrentTaskIdx++;
            mCurrentTask = mSolution.getTaskList().get(mCurrentTaskIdx);
            hasMore = mCurrentTask.isHasMore();
        } else if(mCurrentTaskIdx >= 0 && mCurrentTaskIdx < total) {
            // 若当前任务没有更多，且有未执行的任务,直到找到下一个有数据的任务
            hasMore = mCurrentTask.isHasMore();
            while(!hasMore) {
                int nextTaskIdx = mCurrentTaskIdx+1;
                if(nextTaskIdx < total) {
                    mCurrentTaskIdx = nextTaskIdx;
                    mCurrentTask = mSolution.getTaskList().get(mCurrentTaskIdx);
                } else {
                    break;
                }
                hasMore = mCurrentTask.isHasMore();
            }
        }

        if(hasMore) {
            toSearch(mCurrentTask);
        } else {
            LogUtil.i(TAG, "solution has finished.");
            mSolution.setHasMore(false);
            callback(mSolution, null, null, false);
        }
    }
    
    private void toSearch(SearchTask task) {
        Message msg = mQueryHandler
                .obtainMessage(QueryHandler.MSG_SEARCH);
        msg.obj = task;
        mQueryHandler.sendMessage(msg);
    }
    
    class QueryHandler extends Handler {

        public static final int MSG_SEARCH = 0;
        public static final int MSG_SEARCH_MORE = 1;
        public QueryHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SEARCH:
                removeMessages(MSG_SEARCH);
                if(msg.obj != null)
                    doSearch((SearchTask)msg.obj);
                break;
            default:
                break;
            }
        }
    }
    
    private void doSearch(SearchTask task) {
        if (task != null) {
            try {
                LogUtil.i(TAG, "execTask " + task.toString());
                execTask(task);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage()!=null?e.getMessage():"");
                e.printStackTrace();

                callback(mSolution, null, null, false);
            }
        }
    }

    private void execTask(SearchTask task) throws Exception {
        final SearchProvider provider = task.getProvider();
        String className = provider.getServiceName();

        Searchable searchFactory = mFactoryCache.get(className);
        if (searchFactory == null) {
            searchFactory = (Searchable)Class.forName(className).newInstance();
            mFactoryCache.put(className, searchFactory);
        }

        List<YelloPageItem> list = searchFactory.search(mSolution, task.getSearchInfo());
        boolean hasMore = searchFactory.hasMore();
        task.setHasMore(hasMore);

        LogUtil.d(TAG, "execTask id:" + task.getId() + " page:"+searchFactory.getPage()+" hasMore:" + hasMore + " result size:"
                + list.size());
        callback(mSolution, null, list, hasMore);
    }
    
    private void callback(Solution sol, final Map<Integer, List<YelloPageItem> > itemMaps, final List<YelloPageItem> itemList, boolean hasMore) {               
        mCurrentTask.setHasMore(hasMore);
        
        // 检查solution是否仍然有更多数据未搜索
        if(mCurrentTask.isHasMore() || mCurrentTaskIdx<mSolution.getTaskList().size() - 1){
        	mSolution.setHasMore(true);
        } else{
        	mSolution.setHasMore(false);
        }
        LogUtil.i(TAG, "onResult task_id: " + mCurrentTask.getId() + " sol.hasMore: " + mSolution.isHasMore()
        		+ " hasMore: " + hasMore + " size: " + (itemList!=null?itemList.size():0));
        
        if(mSolution.getMainHandler() == null) {
            if(mListener != null) {
                mBusy.set(false); 
                mListener.onResult(mSolution, itemMaps, itemList, mSolution.isHasMore());
            }
        } else {
            mSolution.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mBusy.set(false); 
                        mListener.onResult(mSolution, itemMaps, itemList, mSolution.isHasMore());
                    }
                }
            });
        }
    }
    
    public boolean hasMore() {
    	if(mSolution != null){
    		return mSolution.isHasMore();
    	}

    	return false;
    }

    // 退出时中断正在阻塞在网络请求上的线程
    // added by cj 2014/10/28 start
    public synchronized void stop() {
        LogUtil.d(TAG, "stop"); 
        mQueryThread.interrupt();
    }
    // added by cj 2014/10/28 end

    public synchronized void release() {
        LogUtil.d(TAG, "release"); 
        stop();
        
        mQueryThread.quit();
        if(mFactoryCache != null){
        	mFactoryCache.clear();
        }
    }
}
