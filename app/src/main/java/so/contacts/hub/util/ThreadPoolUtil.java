package so.contacts.hub.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池 工具类
 * （注：使用该类时一定要记得exitThreadPool()关闭线程池）
 */
public class ThreadPoolUtil {
	
	private static ThreadPoolUtil mInstance = null;
	
	public static ThreadPoolUtil getInstance(){
		if( mInstance == null ){
			synchronized (ThreadPoolUtil.class) {
				mInstance = new ThreadPoolUtil();
			}
		}
		return mInstance;
	}
	
	private ExecutorService mThreadPool = null;
	
	private void createThreadPool(){
		if( mThreadPool != null ){
			return;
		}
		int corePoolSize = 2;     // 线程池维护线程的最少数量
		int maximumPoolSize = 4;  //线程池维护线程的最大数量
		int keepAliveTime = 3;    //线程池维护线程所允许的空闲时间
		mThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, 
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3),  
                new ThreadPoolExecutor.DiscardOldestPolicy());
	}
	
	public void execute(Runnable runnable){
		if( mThreadPool == null ){
			createThreadPool();
		}
		mThreadPool.execute(runnable);
	}
	
	
	public void exitThreadPool(){
		if( mThreadPool != null &&  !mThreadPool.isShutdown() ){
			mThreadPool.shutdown();
			mThreadPool = null;
		}
	}
}









