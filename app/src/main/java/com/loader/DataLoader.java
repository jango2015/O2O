/**
 * 
 */

package com.loader;

import java.lang.ref.WeakReference;

import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import com.loader.DataCache.DataCacheParams;
import com.mdroid.core.image.cache.AsyncTask;
import com.mdroid.core.image.cache.ImageCache;
import com.yulong.android.contacts.discover.BuildConfig;

/**
 * @author Acher
 */
public abstract class DataLoader {

    private static final String TAG = "DataLoader";

    protected Context context;

    protected DataCache mDataCache;

    private boolean mExitTasksEarly = false;

    protected boolean mPauseWork = false;

    private final Object mPauseWorkLock = new Object();

    private DataLoaderListener mLoaderListener;

    public DataLoader(Context context) {
        this.context = context;
    }

    public void setDataCache(DataCacheParams cacheParams) {
        mDataCache = DataCache.findOrCreateCache(cacheParams);
    }

    public DataCache getDataCache() {
        return mDataCache;
    }

    public void setDataLoaderParams(DataLoaderParams loaderParams) {
    }

    public DataLoaderParams getDataLoaderParams() {
        return null;
    }

    /**
     * Load an data specified by the data parameter into an View (override
     * {@link DataLoader#processData(Object)} to define the processing logic). A
     * memory and disk cache will be used if an {@link DataCache} has been set
     * using {@link DataLoader#setDataCache(ImageCache)}. If the data is found
     * in the memory cache, it is set immediately, otherwise an
     * {@link AsyncTask} will be created to asynchronously load the data.
     * 
     * @param data The URL of the image to download, or The Id of the contact to
     *            fetch.
     * @param view The View to bind the process data to.
     */
    public void loadData(Object data, View view) {
        loadData(data, view, null);
    }

    /**
     * Load an data specified by the data parameter into an View (override
     * {@link DataLoader#processData(Object)} to define the processing logic). A
     * memory and disk cache will be used if an {@link DataCache} has been set
     * using {@link DataLoader#setDataCache(ImageCache)}. If the data is found
     * in the memory cache, it is set immediately, otherwise an
     * {@link AsyncTask} will be created to asynchronously load the data.
     * 
     * @param data The URL of the image to download, or The Id of the contact to
     *            fetch.
     * @param view The View to bind the process data to.
     * @param listener a callback to fill data in view
     */
    public void loadData(Object data, View view, DataLoaderListener listener) {
        if (data == null) {
            return;
        }

        mLoaderListener = listener;

        Object result = hitInCache(data);// 如果在缓存中命中，即数据已经存在在缓存中
        if (result != null) {
            // Result found in memory cache
            if (mLoaderListener == null) {
                fillDataInView(result, view);
            } else {// 多个视图的更新，复杂性和重用性不高，通过数据加载监听器回调Adapter中的数据填充和更新视图
                mLoaderListener.fillDataInView(result, view);
            }
            
        } else if (cancelPotentialWork(data, view)) {
            final LoaderTask task = createLoaderTask(view);

            // NOTE: This uses a custom version of AsyncTask that has been
            // pulled from the framework and slightly modified.
            // Refer to the docs at the top of the class for more info on what
            // was changed.
            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, data);
        }
    }
    
    /**
     * 找到图像时不做其他动作直接返回Bitmap
     * @param data
     * @param view
     * @return Bitmap
     */
    public Bitmap loadDataReturnBitmap(Object data,View view) {
        if (data == null) {
            return null;
        }
        mLoaderListener = null;
        Object result = hitInCache(data);// 如果在缓存中命中，即数据已经存在在缓存中
        if (result != null) {
            return (Bitmap)result;
        } else if (cancelPotentialWork(data, view)) {
            final LoaderTask task = createLoaderTask(view);
            // NOTE: This uses a custom version of AsyncTask that has been
            // pulled from the framework and slightly modified.
            // Refer to the docs at the top of the class for more info on what
            // was changed.
            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, data);
        }
        return null;
    }

    /**
     * Get from memory cache.
     * 
     * @param data Unique identifier for which item to get
     * @return The result if found in cache, null otherwise
     */
    protected Object hitInCache(Object data) {
        Object cachedResult = null;
        if (mDataCache != null) {
            cachedResult = mDataCache.getResultFromCache(String.valueOf(data));
        }
        return cachedResult;
    }

    /**
     * Called when the processing is complete and the final result should be set
     * on the View.
     * 
     * @param view
     * @param bitmap
     */
    protected void fillDataInView(Object result, View view) {
        // 试用：统一的一对一方式，将数据填充到视图并更新视图。比如，ImageView 和 Bitmap
        if (mLoaderListener == null) {
            fillDataInView(result, view);
        } else {// 多个视图的更新，复杂性和重用性不高，通过数据加载监听器回调Adapter中的数据填充和更新视图
            mLoaderListener.fillDataInView(result, view);
        }
    }
    
    /**
     * 支持对多种view的适配
     * @param result
     * @param view
     */
	protected void fillDataWithView(Object result, View view){
		if (result == null || view == null) {
			return;
		}
		fillDataWithView(result, view);
    }

    /**
     * Returns true if the current work has been canceled or if there was no
     * work in progress on this image view. Returns false if the work in
     * progress deals with the same data. The work is not stopped in that case.
     */
    public boolean cancelPotentialWork(Object data, View view) {
        final LoaderTask task = getLoaderTask(view);

        if (task != null) {
            final Object requestData = task.data;
            if (requestData == null || !requestData.equals(data)) {
                task.cancel(true);
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }

    /**
     * @param view Any View
     * @return Returns a new loader task associated with this View.
     */
    protected LoaderTask createLoaderTask(View view) {
        LoaderTask task = new LoaderTask(view);

        final WeakReference<LoaderTask> taskReference = new WeakReference<LoaderTask>(task);
        view.setTag(view.getId(), taskReference);

        return task;
    }

    /**
     * The actual AsyncTask that will asynchronously process the data.
     */
    protected class LoaderTask extends AsyncTask<Object, Void, Object> {

        private Object data;

        private final WeakReference<View> viewReference;

        public LoaderTask(View view) {
            viewReference = new WeakReference<View>(view);
        }

        /**
         * Background processing.
         */
        @Override
        protected Object doInBackground(final Object... params) {
            data = params[0];

            // Wait here if work is paused and the task is not cancelled
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            Object result = null;

            // If the result was not found in the cache and this task has not
            // been cancelled by another thread and the View that was originally
            // bound to this task is still
            // bound back to this task and our "exit early" flag is not set,
            // then call the main processData method (as implemented by a
            // subclass)
            if (result == null && !isCancelled() && getAttachedView() != null && !mExitTasksEarly) {
                result = processData(data);
            }

            // If the data was processed and the data cache is available, then
            // add the processed data to the cache for future use.
            // Note we don't check if the task was cancelled here, if it was,
            // and the thread is still running, we may as well add the processed
            // data to our cache as it might be used again in the future
            if (result != null && mDataCache != null) {
                String dataString = String.valueOf(data);
                mDataCache.addDataToCache(dataString, result);
            }
            return result;
        }

        /**
         * Once the data is processed, associates it to the view
         */
        @Override
        protected void onPostExecute(Object result) {
            // if cancel was called on this task or the "exit early" flag is set
            // then we're done
            if (isCancelled() || mExitTasksEarly) {
                result = null;
            }

            final View view = getAttachedView();
            LogUtil.d(TAG, "LoaderTask onPostExecute result: " + result + " ,view: " + view);
            if(result instanceof Bitmap && view != null){
            	view.setTag(result);
            }
            
            if (mLoaderListener == null) {
            	fillDataInView(result, view);
            } else {// 多个视图的更新，复杂性和重用性不高，通过数据加载监听器回调Adapter中的数据填充和更新视图
            	mLoaderListener.fillDataInView(result, view);
            }
        }

        @Override
        protected void onCancelled(Object result) {
            super.onCancelled(result);
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
        }

        /**
         * Returns the View associated with this task as long as the View task
         * still points to this task as well. Returns null otherwise.
         */
        private View getAttachedView() {
            final View view = viewReference.get();
            final LoaderTask task = getLoaderTask(view);

            if (this == task) {
                return view;
            }

            return null;
        }
    }

    /**
     * @param view Any View
     * @return Retrieve the currently active loader task (if any) associated
     *         with this View. null if there is no such task.
     */
    @SuppressWarnings("unchecked")
    protected LoaderTask getLoaderTask(View view) {
        if (view != null) {
			final WeakReference<LoaderTask> taskReference = (WeakReference<LoaderTask>)view
                    .getTag(view.getId());
            if (taskReference != null) {
                return taskReference.get();
            }
        }
        return null;
    }

    /**
     * The main process method, which will be called by the ImageWorker in the
     * AsyncTask background thread.
     * 
     * @param data The data to load the result, in this case, a regular http URL
     *            or id
     * @return The downloaded and resized bitmap, or Object
     */
    public abstract Object processData(Object data);

    /**
     * Cancels any pending loader attached to the provided View.
     * 
     * @param view
     */
    public void cancelWork(View view) {
        final LoaderTask loaderTask = getLoaderTask(view);
        if (loaderTask != null) {
            loaderTask.cancel(true);
            if (BuildConfig.DEBUG) {
                final Object data = loaderTask.data;
                LogUtil.d(TAG, "cancelWork - cancelled work for " + data);
            }
        }
    }

    /** 是否暂停后台数据加载 */
    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    /**
     * 来控制是否在后台下载和加载图片
     * 
     * @param exitTasksEarly 当为true时，就停止在后台下载和加载图片，为false时可以在后台下载和加载图片
     */
    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
    }

    /**
     * 通过key删除缓存
     */
    public void removeCache(Object data) {
//        if (data instanceof ContactsBean) {
//            ContactsBean contact = (ContactsBean)data;
//            data = "rawContactId" + contact.getRaw_contact_id();
//        }
        if (mDataCache != null) {
            mDataCache.removeCache(String.valueOf(data));
        }
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        if (mDataCache != null) {
            mDataCache.clearCache();
        }
    }
}
