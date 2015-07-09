package so.putao.findplug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.UMengUtil;
import so.contacts.hub.util.YellowUtil;
import so.putao.findplug.YelloPageDataManager.MyHandler;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import so.contacts.hub.util.MobclickAgentUtil;

public class YelloPageDataManager {

	private static final int mShowPageSize = 20;
	private static YelloPageDataManager pThis;
	private static Context mContext;
	private final boolean mShowDianping = true;
	private final boolean mShowSougou = true;

	private HandlerThread mHandlerThread;

	private HashMap<SearchData, SearchTask> mSearchTaskMap = new HashMap<SearchData, SearchTask>();
	private HashMap<String, Integer> mPhoneMap = new HashMap<String, Integer>();
	private HashMap<String, Integer> mNameMap = new HashMap<String, Integer>();

	private List<YelloPageItem> mAllRelustList = new ArrayList<YelloPageItem>();
	private List<YelloPageItem> mResultList = new ArrayList<YelloPageItem>();

	private List<YelloPageItem> mSougouResultList = new ArrayList<YelloPageItem>();
	private List<YelloPageItem> mDianpingResultList = new ArrayList<YelloPageItem>();

	private boolean mInitDianpingData = false;
	private boolean mInitSougouData = false;

	private boolean mHasMore;
	private boolean mHasMoreSougou;
	private boolean mHasMoreDianping;

	private double mLineDistance;
	private double mLineDistanceSougou = -1;
	private double mLineDistanceDianping = -1;

	private boolean mSearchWith2G = false;
	private boolean isTimeOut = false;

	public static YelloPageDataManager createInstance(Context context) {
		mContext = context;
		pThis = new YelloPageDataManager();
		YelloPageSougouFactory.init(context);
		return pThis;
	}

	public static YelloPageDataManager createInstance() {
		pThis = new YelloPageDataManager();
		return pThis;
	}

	public static YelloPageDataManager getInstance() {
		if (pThis == null) {
			pThis = new YelloPageDataManager();
		}
		return pThis;
	}

	public MyHandler getWorkHandler() {
		return new MyHandler(mHandlerThread.getLooper());
	}

	private YelloPageDataManager() {
		mHandlerThread = new HandlerThread("SougouHandlerThread");
		mHandlerThread.start();
	}

	public static void closeInstance() {
		if (getInstance() == null) {
			return;
		}
		getInstance().close();
		pThis = null;
	}

	public void close() {
		mHandlerThread.quit();
		clearSearchTask();
	}

	public synchronized void asyncSearch(SearchData searchData,
			ResultListener listener, int sourceId) {
		Log.e("error", "asyncSearch");
		SearchTask searchTask = new SearchTask(searchData, listener,
				this.getWorkHandler(), null);
		mSearchTaskMap.put(searchData, searchTask);
		Message msg = Message.obtain();
		msg.what = 0;
		msg.obj = searchData;
		msg.arg1 = sourceId;
		searchTask.myHandler.sendMessage(msg);
	}

	public synchronized void asyncSearchMore(SearchData searchData) {
		Log.e("error", "asyncSearchMore");
		SearchTask task = mSearchTaskMap.get(searchData);
		if (task == null) {
			return;
		}
		Message msg = Message.obtain();
		msg.what = 1;
		msg.obj = searchData;
		task.myHandler.sendMessage(msg);
	}

	// sml for deal
	public synchronized void asyncDealSearch(SearchData searchData,
			ResultListener listener) {
		Log.e("error", "asyncDealSearch");
		SearchTask searchTask = new SearchTask(searchData, listener,
				this.getWorkHandler(), null);
		mSearchTaskMap.put(searchData, searchTask);
		Message msg = Message.obtain();
		msg.what = 2;
		msg.obj = searchData;
		searchTask.myHandler.sendMessage(msg);
	}

	// sml for deal
	public synchronized void asyncSearchDealMore(SearchData searchData) {
		Log.e("error", "asyncSearcDealhMore");
		SearchTask task = mSearchTaskMap.get(searchData);
		if (task == null) {
			return;
		}
		Message msg = Message.obtain();
		msg.what = 3;
		msg.obj = searchData;
		task.myHandler.sendMessage(msg);
	}

	// sml for coupon
	public synchronized void asyncCouponSearch(SearchData searchData,
			ResultListener listener) {
		Log.e("error", "asyncCouponSearch");
		SearchTask searchTask = new SearchTask(searchData, listener,
				this.getWorkHandler(), null);
		mSearchTaskMap.put(searchData, searchTask);
		Message msg = Message.obtain();
		msg.what = 4;
		msg.obj = searchData;
		searchTask.myHandler.sendMessage(msg);
	}

	// sml for coupon
	public synchronized void asyncSearchCouponMore(SearchData searchData) {
		Log.e("error", "asyncSearchCouponMore");
		SearchTask task = mSearchTaskMap.get(searchData);
		if (task == null) {
			return;
		}
		Message msg = Message.obtain();
		msg.what = 5;
		msg.obj = searchData;
		task.myHandler.sendMessage(msg);
	}

	public synchronized void stopAsyncSearch(SearchData searchData) {
		SearchTask task = mSearchTaskMap.get(searchData);
		if (task == null) {
			return;
		}
		task.myHandler.setEnable(true);
		task.myHandler.removeMessages(0, task);
		mSearchTaskMap.remove(searchData);
	}

	public synchronized void putSearchTask(SearchData searchData,
			SearchTask searchTask) {
		mSearchTaskMap.put(searchData, searchTask);
	}

	public synchronized SearchTask getSearchTask(SearchData searchData) {
		return this.mSearchTaskMap.get(searchData);
	}

	public synchronized void removeSearchTask(SearchData searchData) {
		this.mSearchTaskMap.remove(searchData);
	}

	public synchronized void clearSearchTask() {
		this.mSearchTaskMap.clear();
	}

	private boolean filterNumber(List<String> numberList) {
		boolean filter = false;
		if (numberList != null && numberList.size() > 0) {
			for (String number : numberList) {
				if (!TextUtils.isEmpty(number)) {
					if (mPhoneMap.containsKey(number)) {
						filter = true;
					} else {
						mPhoneMap.put(number, 2);
					}
				}
			}
		}
		return filter;
	}

	private boolean filterName(String name) {
		boolean filter = false;
		if (!TextUtils.isEmpty(name)) {
			if (mNameMap.containsKey(name)) {
				filter = true;
			} else {
				mNameMap.put(name, 2);
			}
		}
		return filter;
	}

	private synchronized void filterResult(List<YelloPageItem> list,
			SearchData searchData, YelloPageFactory factory) {
		List<YelloPageItem> filterList = new ArrayList<YelloPageItem>();
		boolean flag = false;
		if (list != null && list.size() > 0) {
			for (YelloPageItem item : list) {
				flag = false;
				List<String> numberList = item.getNumbers();
				String name = item.getName();
				flag = filterNumber(numberList);
				flag = filterName(name);

				if (searchData.source == 1 && item.getDistance() == 0.0) {
					flag = true;
				}
				if (!flag) {
					mAllRelustList.add(item);
					filterList.add(item);
				}
			}
		}
		if (factory instanceof YelloPageDianpingFactory) {
			refreshDianpingParam(filterList);
		} else if (factory instanceof YelloPageSougouFactory) {
			refreshSougouParam(filterList);
		}

	}

	// sml for deal
	private synchronized void filterDealResult(List<YelloPageItem> list,
			SearchData searchData, YelloPageFactory factory) {
		List<YelloPageItem> filterList = new ArrayList<YelloPageItem>();
		boolean flag = false;
		if (list != null && list.size() > 0) {
			for (YelloPageItem item : list) {
				flag = false;
				List<String> numberList = item.getNumbers();
				String name = item.getName();
				flag = filterNumber(numberList);
				flag = filterName(name);

				if (searchData.source == 1 && item.getDistance() == 0.0) {
					flag = true;
				}
				if (!flag) {
					mAllRelustList.add(item);
					filterList.add(item);
				}
			}
		}
		if (factory instanceof YellowPageDianpingDealFactory) {
			refreshDianpingDealParam(filterList);
		}
		// else if(factory instanceof YelloPageSougouFactory){
		// refreshSougouParam(filterList);
		// }

	}

	// sml for coupon
	private synchronized void filterCouponResult(List<YelloPageItem> list,
			SearchData searchData, YelloPageFactory factory) {
		List<YelloPageItem> filterList = new ArrayList<YelloPageItem>();
		boolean flag = false;
		if (list != null && list.size() > 0) {
			for (YelloPageItem item : list) {
				flag = false;
				List<String> numberList = item.getNumbers();
				String name = item.getName();
				flag = filterNumber(numberList);
				flag = filterName(name);

				if (searchData.source == 1 && item.getDistance() == 0.0) {
					flag = true;
				}
				if (!flag) {
					mAllRelustList.add(item);
					filterList.add(item);
				}
			}
		}
		if (factory instanceof YellowPageCouponFactory) {
			refreshDianpingCouponParam(filterList);
		}
		// else if(factory instanceof YelloPageSougouFactory){
		// refreshSougouParam(filterList);
		// }

	}

	private void refreshSougouParam(List<YelloPageItem> filterList) {
		mHasMoreSougou = YelloPageSougouFactory.getInstance(mContext).hasMore();
		if (filterList != null && filterList.size() > 0) {
			mLineDistanceSougou = filterList.get(filterList.size() - 1)
					.getDistance();
		} else {
			mLineDistanceSougou = 0;
		}
	}

	private void refreshDianpingParam(List<YelloPageItem> filterList) {
		mHasMoreDianping = YelloPageDianpingFactory.getInstance(mContext)
				.hasMore();
		if (filterList != null && filterList.size() > 0) {
			mLineDistanceDianping = filterList.get(filterList.size() - 1)
					.getDistance();
		} else {
			mLineDistanceDianping = 0;
		}
	}

	// sml for deal
	private void refreshDianpingDealParam(List<YelloPageItem> filterList) {
		mHasMoreDianping = YellowPageDianpingDealFactory.getInstance(mContext)
				.hasMore();
		if (filterList != null && filterList.size() > 0) {
			mLineDistanceDianping = filterList.get(filterList.size() - 1)
					.getDistance();
		} else {
			mLineDistanceDianping = 0;
		}
	}

	// sml for coupon
	private void refreshDianpingCouponParam(List<YelloPageItem> filterList) {
		mHasMoreDianping = YellowPageCouponFactory.getInstance(mContext)
				.hasMore();
		if (filterList != null && filterList.size() > 0) {
			mLineDistanceDianping = filterList.get(filterList.size() - 1)
					.getDistance();
		} else {
			mLineDistanceDianping = 0;
		}
	}

	private void initSougouResult(SearchData searchData) {
		List<YelloPageItem> list = YelloPageSougouFactory.getInstance(mContext)
				.search(searchData.keyword, searchData.city,
						searchData.longitude, searchData.latitude,
						searchData.category,searchData.source);
		if (list != null && list.size() > 0) {
			mSougouResultList.addAll(list);
		}
		mInitSougouData = true;
	}

	private void searchMoreSougou(SearchData searchData) {
		List<YelloPageItem> list = YelloPageSougouFactory.getInstance(mContext)
				.searchMore();
		filterResult(list, searchData,
				YelloPageSougouFactory.getInstance(mContext));
	}

	private void initDianpingResult(SearchData searchData) {
		List<YelloPageItem> list = YelloPageDianpingFactory.getInstance(
				mContext).search(searchData.keyword, searchData.city,
				searchData.longitude, searchData.latitude, searchData.category,searchData.source);
		if (list != null && list.size() > 0) {
			mDianpingResultList.addAll(list);
		}
		mInitDianpingData = true;
		// filterResult(list, searchData,
		// YelloPageDianpingFactory.getInstance(mContext));
	}

	// sml for deal
	private void initDianpingDealResult(SearchData searchData) {
		List<YelloPageItem> list = YellowPageDianpingDealFactory.getInstance(
				mContext).search(searchData.keyword, searchData.city,
				searchData.longitude, searchData.latitude, searchData.category,searchData.source);
		if (list != null && list.size() > 0) {
			mDianpingResultList.addAll(list);
		}
		mInitDianpingData = true;
		// filterResult(list, searchData,
		// YelloPageDianpingFactory.getInstance(mContext));
	}

	// sml for coupon
	private void initDianpingCouponResult(SearchData searchData) {
		List<YelloPageItem> list = YellowPageCouponFactory
				.getInstance(mContext).search(searchData.keyword,
						searchData.city, searchData.longitude,
						searchData.latitude, searchData.category,searchData.source);
		if (list != null && list.size() > 0) {
			mDianpingResultList.addAll(list);
		}
		mInitDianpingData = true;
		// filterResult(list, searchData,
		// YelloPageDianpingFactory.getInstance(mContext));
	}

	private void searchMoreDianping(SearchData searchData) {
		List<YelloPageItem> list = YelloPageDianpingFactory.getInstance(
				mContext).searchMore();
		filterResult(list, searchData,
				YelloPageDianpingFactory.getInstance(mContext));
	}

	// sml for deal
	private void searchMoreDianpingDeal(SearchData searchData) {
		List<YelloPageItem> list = YellowPageDianpingDealFactory.getInstance(
				mContext).searchMore();
		filterDealResult(list, searchData,
				YellowPageDianpingDealFactory.getInstance(mContext));
	}

	// sml for coupon
	private void searchMoreDianpingCoupon(SearchData searchData) {
		List<YelloPageItem> list = YellowPageCouponFactory
				.getInstance(mContext).searchMore();
		filterCouponResult(list, searchData,
				YellowPageCouponFactory.getInstance(mContext));
	}

	/**
	 * 混排搜索
	 * 
	 * @param searchData
	 */
	private void searchResult(final SearchData searchData) {
		mAllRelustList.clear();
		mPhoneMap.clear();
		mNameMap.clear();
		isTimeOut = false;
		if (mShowDianping && searchData.showDianping) {
			new Thread(new DianpingThread(searchData)).start();
			// initDianpingResult(searchData);
		}

		if (mShowSougou && searchData.showSougou) {
			new Thread(new SougouThread(searchData)).start();
		}

		long timeStart = System.currentTimeMillis();
		while (true) {
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (mInitSougouData) {
				filterResult(mSougouResultList, searchData,
						YelloPageSougouFactory.getInstance(mContext));
				mSougouResultList.clear();
			}
			if (mInitDianpingData) {
				filterResult(mDianpingResultList, searchData,
						YelloPageDianpingFactory.getInstance(mContext));
				mDianpingResultList.clear();
			}

			// sml:control sogo and dianping
			if (mInitDianpingData || mInitSougouData) {
				if (mInitDianpingData) {
					if (mDianpingResultList.size() > 0) {
						filterResult(mDianpingResultList, searchData,
								YelloPageDianpingFactory.getInstance(mContext));
						mDianpingResultList.clear();
					}
					mInitDianpingData = false;
				}
				if (mInitSougouData) {
					if (mSougouResultList.size() > 0) {
						filterResult(mSougouResultList, searchData,
								YelloPageSougouFactory.getInstance(mContext));
						mSougouResultList.clear();
					}

					mInitSougouData = false;
				}

				break;
			}

			long timeEnd = System.currentTimeMillis();
			LogUtil.i("sougou", "time:" + (timeEnd - timeStart));
			if (timeEnd - timeStart > 15000) {
				isTimeOut = true;
				break;
			}
		}

		// if (mAllRelustList.size() < mShowPageSize && mAllRelustList.size() >
		// 0 && !isTimeOut) {
		// searchMoreRelust(searchData);
		// } else {
		Collections.sort(mAllRelustList, new ListComparator());
		mResultList.clear();
		int maxSize = 0;
		if (mAllRelustList.size() < mShowPageSize) {
			maxSize = mAllRelustList.size();
		} else {
			maxSize = mShowPageSize;
		}

		if (maxSize > 0) {
			for (int i = 0; i < maxSize; i++) {
				mResultList.add(mAllRelustList.get(i));
			}
			mLineDistance = mAllRelustList.get(maxSize - 1).getDistance();
			mAllRelustList.removeAll(mResultList);
		}
		mHasMore = mHasMoreSougou || mHasMoreDianping;
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return;
		}
		searchTask.resultListener.getHandler().post(new Runnable() {
			@Override
			public void run() {
				if (getSearchTask(searchData) == null) {
					return;
				}
				searchTask.resultListener.onResult(searchData,
						(ArrayList<YelloPageItem>) mResultList, mHasMore,
						isTimeOut);
			}
		});
		// }
	}

	/**
	 * mixed sml for deal
	 * 
	 * @param searchdeal
	 */
	private void searchDealResult(final SearchData searchData) {
		mAllRelustList.clear();
		mPhoneMap.clear();
		mNameMap.clear();
		isTimeOut = false;
		if (mShowDianping && searchData.showDianping) {
			new Thread(new DianpingDealThread(searchData)).start();
			// initDianpingResult(searchData);
		}

		if (mShowSougou && searchData.showSougou) {
			new Thread(new SougouThread(searchData)).start();
		}

		long timeStart = System.currentTimeMillis();
		while (true) {
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (mInitSougouData) {
				filterDealResult(mSougouResultList, searchData,
						YelloPageSougouFactory.getInstance(mContext));
				mSougouResultList.clear();
			}
			if (mInitDianpingData) {
				filterDealResult(mDianpingResultList, searchData,
						YellowPageDianpingDealFactory.getInstance(mContext));
				mDianpingResultList.clear();
			}

			// sml:control sogo and dianping
			if (mInitDianpingData || mInitSougouData) {
				if (mInitDianpingData) {
					if (mDianpingResultList.size() > 0) {
						filterDealResult(mDianpingResultList, searchData,
								YellowPageDianpingDealFactory
										.getInstance(mContext));
						mDianpingResultList.clear();
					}
					mInitDianpingData = false;
				}
				// if(mInitSougouData)
				// {
				// if(mSougouResultList.size() > 0){
				// filterDealResult(mSougouResultList,
				// searchData,YelloPageSougouFactory.getInstance(mContext));
				// mSougouResultList.clear();
				// }
				//
				// mInitSougouData = false;
				// }

				break;
			}

			long timeEnd = System.currentTimeMillis();
			LogUtil.i("sougou", "time:" + (timeEnd - timeStart));
			if (timeEnd - timeStart > 15000) {
				isTimeOut = true;
				break;
			}
		}

		// if (mAllRelustList.size() < mShowPageSize && mAllRelustList.size() >
		// 0 && !isTimeOut) {
		// searchMoreRelust(searchData);
		// } else {
		Collections.sort(mAllRelustList, new ListComparator());
		mResultList.clear();
		int maxSize = 0;
		if (mAllRelustList.size() < mShowPageSize) {
			maxSize = mAllRelustList.size();
		} else {
			maxSize = mShowPageSize;
		}

		if (maxSize > 0) {
			for (int i = 0; i < maxSize; i++) {
				mResultList.add(mAllRelustList.get(i));
			}
			mLineDistance = mAllRelustList.get(maxSize - 1).getDistance();
			mAllRelustList.removeAll(mResultList);
		}
		mHasMore = mHasMoreSougou || mHasMoreDianping;
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return;
		}
		searchTask.resultListener.getHandler().post(new Runnable() {
			@Override
			public void run() {
				if (getSearchTask(searchData) == null) {
					return;
				}
				searchTask.resultListener.onResult(searchData,
						(ArrayList<YelloPageItem>) mResultList, mHasMore,
						isTimeOut);
			}
		});
		// }
	}

	/**
	 * mixed sml for coupon
	 * 
	 * @param searchcoupon
	 */
	private void searchCouponResult(final SearchData searchData) {
		mAllRelustList.clear();
		mPhoneMap.clear();
		mNameMap.clear();
		isTimeOut = false;
		if (mShowDianping && searchData.showDianping) {
			new Thread(new DianpingCouponThread(searchData)).start();
			// initDianpingResult(searchData);
		}
		//
		// if (mShowSougou && searchData.showSougou) {
		// new Thread(new SougouThread(searchData)).start();
		// }
		//
		long timeStart = System.currentTimeMillis();
		while (true) {
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// if(mInitSougouData){
			// filterCouponResult(mSougouResultList,
			// searchData,YelloPageSougouFactory.getInstance(mContext));
			// mSougouResultList.clear();
			// }
			if (mInitDianpingData) {
				filterCouponResult(mDianpingResultList, searchData,
						YellowPageCouponFactory.getInstance(mContext));
				mDianpingResultList.clear();
			}

			// sml:control sogo and dianping
			if (mInitDianpingData || mInitSougouData) {
				if (mInitDianpingData) {
					if (mDianpingResultList.size() > 0) {
						filterCouponResult(mDianpingResultList, searchData,
								YellowPageCouponFactory.getInstance(mContext));
						mDianpingResultList.clear();
					}
					mInitDianpingData = false;
				}
				// if(mInitSougouData)
				// {
				// if(mSougouResultList.size() > 0){
				// filterDealResult(mSougouResultList,
				// searchData,YelloPageSougouFactory.getInstance(mContext));
				// mSougouResultList.clear();
				// }
				//
				// mInitSougouData = false;
				// }

				break;
			}

			long timeEnd = System.currentTimeMillis();
			LogUtil.i("sougou", "time:" + (timeEnd - timeStart));
			if (timeEnd - timeStart > 15000) {
				isTimeOut = true;
				break;
			}
		}

		// if (mAllRelustList.size() < mShowPageSize && mAllRelustList.size() >
		// 0 && !isTimeOut) {
		// searchMoreRelust(searchData);
		// } else {
		Collections.sort(mAllRelustList, new ListComparator());
		mResultList.clear();
		int maxSize = 0;
		if (mAllRelustList.size() < mShowPageSize) {
			maxSize = mAllRelustList.size();
		} else {
			maxSize = mShowPageSize;
		}

		if (maxSize > 0) {
			for (int i = 0; i < maxSize; i++) {
				mResultList.add(mAllRelustList.get(i));
			}
			mLineDistance = mAllRelustList.get(maxSize - 1).getDistance();
			mAllRelustList.removeAll(mResultList);
		}
		mHasMore = mHasMoreSougou || mHasMoreDianping;
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return;
		}
		searchTask.resultListener.getHandler().post(new Runnable() {
			@Override
			public void run() {
				if (getSearchTask(searchData) == null) {
					return;
				}
				searchTask.resultListener.onResult(searchData,
						(ArrayList<YelloPageItem>) mResultList, mHasMore,
						isTimeOut);
			}
		});
		// }
	}

	private void searchMoreRelust(final SearchData searchData) {
		Collections.sort(mAllRelustList, new ListComparator());
		if (mAllRelustList.size() > 0 && mAllRelustList.size() < mShowPageSize) {
			mLineDistance = mAllRelustList.get(mAllRelustList.size() - 1)
					.getDistance();
		} else if (mAllRelustList.size() >= mShowPageSize) {
			mLineDistance = mAllRelustList.get(mShowPageSize - 1).getDistance();
		}
		while (mLineDistance >= getValidSmallerDistance(mLineDistanceSougou,
				mLineDistanceDianping)) {
			if (!mHasMoreDianping) {
				mLineDistanceDianping = -1;
			}
			if (!mHasMoreSougou) {
				mLineDistanceSougou = -1;
			}
			if (isSmallerValidDistance(mLineDistanceDianping,
					mLineDistanceSougou)) {
				searchMoreSougou(searchData);
			} else if (isSmallerValidDistance(mLineDistanceSougou,
					mLineDistanceDianping)) {
				searchMoreDianping(searchData);
			}

			Collections.sort(mAllRelustList, new ListComparator());
			if (mAllRelustList.size() > 0
					&& mAllRelustList.size() < mShowPageSize) {
				mLineDistance = mAllRelustList.get(mAllRelustList.size() - 1)
						.getDistance();
			} else if (mAllRelustList.size() >= mShowPageSize) {
				mLineDistance = mAllRelustList.get(mShowPageSize - 1)
						.getDistance();
			}
			if (!(mHasMoreSougou || mHasMoreDianping)) {
				break;
			}
			if (mLineDistanceSougou == 0.0 || mLineDistanceDianping == 0.0) {
				int count = 0;
				for (YelloPageItem item : mAllRelustList) {
					if (item.getDistance() == 0.0) {
						count++;
					}
				}
				if (count > mShowPageSize) {
					break;
				}
			}
		}
		mResultList.clear();
		int maxSize = 0;
		if (mAllRelustList.size() > mShowPageSize) {
			maxSize = mShowPageSize;
		} else {
			maxSize = mAllRelustList.size();
		}
		if (maxSize > 0) {
			for (int i = 0; i < maxSize; i++) {
				mResultList.add(mAllRelustList.get(i));
			}
			mLineDistance = mAllRelustList.get(maxSize - 1).getDistance();
			mAllRelustList.removeAll(mResultList);
		}
		mHasMore = mHasMoreSougou || mHasMoreDianping;
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return;
		}
		searchTask.resultListener.getHandler().post(new Runnable() {
			@Override
			public void run() {
				if (getSearchTask(searchData) == null) {
					return;
				}
				searchTask.resultListener
						.onResult(searchData,
								(ArrayList<YelloPageItem>) mResultList,
								mHasMore, false);
			}
		});
	}

	// sml for deal
	private void searchMoreDealRelust(final SearchData searchData) {
		Collections.sort(mAllRelustList, new ListComparator());
		if (mAllRelustList.size() > 0 && mAllRelustList.size() < mShowPageSize) {
			mLineDistance = mAllRelustList.get(mAllRelustList.size() - 1)
					.getDistance();
		} else if (mAllRelustList.size() >= mShowPageSize) {
			mLineDistance = mAllRelustList.get(mShowPageSize - 1).getDistance();
		}
		while (mLineDistance >= getValidSmallerDistance(mLineDistanceSougou,
				mLineDistanceDianping)) {
			if (!mHasMoreDianping) {
				mLineDistanceDianping = -1;
			}
			// if (!mHasMoreSougou) {
			// mLineDistanceSougou = -1;
			// }
			// if
			// (isSmallerValidDistance(mLineDistanceDianping,mLineDistanceSougou))
			// {
			// searchMoreSougou(searchData);
			// }
			// else
			if (isSmallerValidDistance(mLineDistanceSougou,
					mLineDistanceDianping)) {
				searchMoreDianpingDeal(searchData);
			}

			Collections.sort(mAllRelustList, new ListComparator());
			if (mAllRelustList.size() > 0
					&& mAllRelustList.size() < mShowPageSize) {
				mLineDistance = mAllRelustList.get(mAllRelustList.size() - 1)
						.getDistance();
			} else if (mAllRelustList.size() >= mShowPageSize) {
				mLineDistance = mAllRelustList.get(mShowPageSize - 1)
						.getDistance();
			}
			if (!(mHasMoreSougou || mHasMoreDianping)) {
				break;
			}
			if (mLineDistanceSougou == 0.0 || mLineDistanceDianping == 0.0) {
				int count = 0;
				for (YelloPageItem item : mAllRelustList) {
					if (item.getDistance() == 0.0) {
						count++;
					}
				}
				if (count > mShowPageSize) {
					break;
				}
			}
		}
		mResultList.clear();
		int maxSize = 0;
		if (mAllRelustList.size() > mShowPageSize) {
			maxSize = mShowPageSize;
		} else {
			maxSize = mAllRelustList.size();
		}
		if (maxSize > 0) {
			for (int i = 0; i < maxSize; i++) {
				mResultList.add(mAllRelustList.get(i));
			}
			mLineDistance = mAllRelustList.get(maxSize - 1).getDistance();
			mAllRelustList.removeAll(mResultList);
		}
		mHasMore = mHasMoreSougou || mHasMoreDianping;
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return;
		}
		searchTask.resultListener.getHandler().post(new Runnable() {
			@Override
			public void run() {
				if (getSearchTask(searchData) == null) {
					return;
				}
				searchTask.resultListener
						.onResult(searchData,
								(ArrayList<YelloPageItem>) mResultList,
								mHasMore, false);
			}
		});
	}

	// sml for coupon
	private void searchMoreCouponRelust(final SearchData searchData) {
		Collections.sort(mAllRelustList, new ListComparator());
		if (mAllRelustList.size() > 0 && mAllRelustList.size() < mShowPageSize) {
			mLineDistance = mAllRelustList.get(mAllRelustList.size() - 1)
					.getDistance();
		} else if (mAllRelustList.size() >= mShowPageSize) {
			mLineDistance = mAllRelustList.get(mShowPageSize - 1).getDistance();
		}
		while (mLineDistance >= getValidSmallerDistance(mLineDistanceSougou,
				mLineDistanceDianping)) {
			if (!mHasMoreDianping) {
				mLineDistanceDianping = -1;
			}
			// if (!mHasMoreSougou) {
			// mLineDistanceSougou = -1;
			// }
			// if
			// (isSmallerValidDistance(mLineDistanceDianping,mLineDistanceSougou))
			// {
			// searchMoreSougou(searchData);
			// }
			// else
			if (isSmallerValidDistance(mLineDistanceSougou,
					mLineDistanceDianping)) {
				searchMoreDianpingCoupon(searchData);
			}

			Collections.sort(mAllRelustList, new ListComparator());
			if (mAllRelustList.size() > 0
					&& mAllRelustList.size() < mShowPageSize) {
				mLineDistance = mAllRelustList.get(mAllRelustList.size() - 1)
						.getDistance();
			} else if (mAllRelustList.size() >= mShowPageSize) {
				mLineDistance = mAllRelustList.get(mShowPageSize - 1)
						.getDistance();
			}
			if (!(mHasMoreSougou || mHasMoreDianping)) {
				break;
			}
			if (mLineDistanceSougou == 0.0 || mLineDistanceDianping == 0.0) {
				int count = 0;
				for (YelloPageItem item : mAllRelustList) {
					if (item.getDistance() == 0.0) {
						count++;
					}
				}
				if (count > mShowPageSize) {
					break;
				}
			}
		}
		mResultList.clear();
		int maxSize = 0;
		if (mAllRelustList.size() > mShowPageSize) {
			maxSize = mShowPageSize;
		} else {
			maxSize = mAllRelustList.size();
		}
		if (maxSize > 0) {
			for (int i = 0; i < maxSize; i++) {
				mResultList.add(mAllRelustList.get(i));
			}
			mLineDistance = mAllRelustList.get(maxSize - 1).getDistance();
			mAllRelustList.removeAll(mResultList);
		}
		mHasMore = mHasMoreSougou || mHasMoreDianping;
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return;
		}
		searchTask.resultListener.getHandler().post(new Runnable() {
			@Override
			public void run() {
				if (getSearchTask(searchData) == null) {
					return;
				}
				searchTask.resultListener
						.onResult(searchData,
								(ArrayList<YelloPageItem>) mResultList,
								mHasMore, false);
			}
		});
	}

	// private boolean isTheSmallestValidDistance(double a, double b, double
	// source) {
	// double smaller = getValidSmallerDistance(a, b);
	// if (smaller < 0) {
	// return true;
	// }
	// if (source < 0) {
	// return false;
	// }
	// if (source <= smaller) {
	// return true;
	// }
	// return false;
	// }

	private boolean isSmallerValidDistance(double a, double b) {
		if (b < 0) {
			return false;
		} else if (a < 0) {
			return true;
		} else if (b <= a) {
			return true;
		} else {
			return false;
		}
	}

	// private double getValidSmallestDistance(double a, double b, double c) {
	// return getValidSmallerDistance(c, getValidSmallerDistance(a, b));
	// }

	private double getValidSmallerDistance(double a, double b) {
		if (a < 0) {
			return b;
		}
		if (b < 0) {
			return a;
		}
		if (a > b) {
			return b;
		} else {
			return a;
		}
	}
	
	public boolean searchNumber(final SearchData searchData){
		if (!mShowSougou || !searchData.showSougou) {
			return false;
		}
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return false;
		}
		final ArrayList<YelloPageItem> list = YelloPageSougouFactory
				.getInstance(mContext).searchNumber(searchData.keyword);
		if (list != null) {
			searchTask.resultListener.getHandler().post(new Runnable() {
				@Override
				public void run() {
					if (getSearchTask(searchData) == null) {
						return;
					}
					searchTask.resultListener.onResult(searchData, list,
							false, false);
				}
			});
			return true;
		}
		return false;
	}
	
	public ArrayList<YelloPageItem> searchSougouList(final SearchData searchData, boolean last, final int sourceId) {
	    ArrayList<YelloPageItem> resultList = null;
	    if (!mShowSougou || !searchData.showSougou) {
            return resultList;
        }
        final SearchTask searchTask = getSearchTask(searchData);
        if (searchTask == null) {
            return resultList;
        }
        searchTask.yelloPageFactory = YelloPageSougouFactory
                .getInstance(mContext);
        final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory
                .search(searchData.keyword, searchData.city,
                        searchData.longitude, searchData.latitude,
                        searchData.category,searchData.source);
        final boolean hasMore = searchTask.yelloPageFactory.hasMore();
        if (getSearchTask(searchData) == null) {
            return resultList;
        }
        resultList = new ArrayList<YelloPageItem>();
        if (list != null && list.size() > 0) {
            resultList.addAll(list);
            /*
             * 不进行特殊规则过滤操作，该处理应该由数据源上进行控制
             * update by hyl 2014-8-20 start
             */
//            for (YelloPageItem item : list) {
//                if (searchData.source == 1 && item.getDistance() == 0.0) {
//                    continue;
//                } else {
////                  if(TextUtils.isEmpty(searchData.keyword) || item.getName().contains(searchData.keyword)){
//                        resultList.add(item);
////                  }
//                }
//            }
        }
	    
	    return resultList;
	}

	/**
	 * 搜索搜狗并且直接刷新界面
	 * @param searchData
	 * @param last
	 * @param sourceId
	 * @return true-有搜索结果 false-无搜索结果
	 */
	public boolean searchSougou(final SearchData searchData, boolean last, final int sourceId) {
	    /*
	     * 通过searchSougouList方法获取搜索结果
	     * update by hyl 2014-8-19 start
	     */
	    final ArrayList<YelloPageItem> resultList = searchSougouList(searchData, last, sourceId);
	    if(resultList == null){
            return false;
        }
        final SearchTask searchTask = getSearchTask(searchData);
        final boolean hasMore = searchTask.yelloPageFactory.hasMore();
        //update by hyl 2014-8-19 end
        
		/*
		 * 将逻辑抽出到searchSougouList方法中
		 * 注释代码以下代码
		 * update by hyl 2014-8-19 start
		if (!mShowSougou || !searchData.showSougou) {
			return false;
		}
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return false;
		}
		searchTask.yelloPageFactory = YelloPageSougouFactory
				.getInstance(mContext);
		final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory
				.search(searchData.keyword, searchData.city,
						searchData.longitude, searchData.latitude,
						searchData.category,searchData.source);
		final boolean hasMore = searchTask.yelloPageFactory.hasMore();
		if (getSearchTask(searchData) == null) {
			return false;
		}
		final ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
		if (list != null && list.size() > 0) {
			for (YelloPageItem item : list) {
				if (searchData.source == 1 && item.getDistance() == 0.0) {
					continue;
				} else {
//					if(TextUtils.isEmpty(searchData.keyword) || item.getName().contains(searchData.keyword)){
						resultList.add(item);
//					}
				}
			}
		}
		update by hyl 2014-8-19 end
		*/
        
		if (resultList.size() > 0 || last) {
			searchTask.resultListener.getHandler().post(new Runnable() {
				@Override
				public void run() {
					if (getSearchTask(searchData) == null) {
						return;
					}

					if( mContext != null && sourceId == UMengUtil.SEARCH_SOURCE_QUERY){
						MobclickAgentUtil.onEvent(mContext, UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_SOUGOU);
					}
					searchTask.resultListener.onResult(searchData, resultList,
							hasMore, false);
				}
			});
			return true;
		}
		return false;
	}

	public ArrayList<YelloPageItem> searchGaoDeList(final SearchData searchData, boolean last, final int sourceId) {
	    ArrayList<YelloPageItem> resultList = null;
	    if (!mShowSougou || !searchData.showSougou) {
            return resultList;
        }
        final SearchTask searchTask = getSearchTask(searchData);
        if (searchTask == null) {
            return resultList;
        }
        searchTask.yelloPageFactory = YelloPageGaoDeFactory.getInstance(mContext);
        final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory
                .search(searchData.keyword, searchData.city,
                        searchData.longitude, searchData.latitude,
                        searchData.category,searchData.source);
        final boolean hasMore = searchTask.yelloPageFactory.hasMore();
        if (getSearchTask(searchData) == null) {
            return resultList;
        }
        resultList = new ArrayList<YelloPageItem>();
        if (list != null && list.size() > 0) {
            resultList.addAll(list);
            /*
             * 不进行特殊规则过滤操作，该处理应该由数据源上进行控制
             * update by hyl 2014-8-20 start
             */
//            for (YelloPageItem item : list) {
//                if (searchData.source == 1 && item.getDistance() == 0.0) {
//                    if(!filterHospital(searchData,item)){
//                        resultList.add(item);
//                    }
//                    continue;
//                } else {
////                  if(TextUtils.isEmpty(searchData.keyword) || item.getName().contains(searchData.keyword)){
//                        resultList.add(item);
////                  }
//                }
//            }
          //update by hyl 2014-8-20 end
        }
	    return resultList;
	}
	
	public boolean searchGaoDe(final SearchData searchData, boolean last, final int sourceId) {
	    final ArrayList<YelloPageItem> resultList = searchGaoDeList(searchData, last, sourceId);
        if(resultList == null){
            return false;
        }
        final SearchTask searchTask = getSearchTask(searchData);
        final boolean hasMore = searchTask.yelloPageFactory.hasMore();
	    
//        if (!mShowSougou || !searchData.showSougou) {
//            return false;
//        }
//        final SearchTask searchTask = getSearchTask(searchData);
//        if (searchTask == null) {
//            return false;
//        }
//        searchTask.yelloPageFactory = YelloPageGaoDeFactory.getInstance(mContext);
//        final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory
//                .search(searchData.keyword, searchData.city,
//                        searchData.longitude, searchData.latitude,
//                        searchData.category,searchData.source);
//        final boolean hasMore = searchTask.yelloPageFactory.hasMore();
//        if (getSearchTask(searchData) == null) {
//            return false;
//        }
//        final ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
//        if (list != null && list.size() > 0) {
//            for (YelloPageItem item : list) {
//                if (searchData.source == 1 && item.getDistance() == 0.0) {
//                    if(!filterHospital(searchData,item)){
//                        resultList.add(item);
//                    }
//                    continue;
//                } else {
////                  if(TextUtils.isEmpty(searchData.keyword) || item.getName().contains(searchData.keyword)){
//                        resultList.add(item);
////                  }
//                }
//            }
//        }
        if (resultList.size() > 0 || last) {
            searchTask.resultListener.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (getSearchTask(searchData) == null) {
                        return;
                    }

                    if( mContext != null && sourceId == UMengUtil.SEARCH_SOURCE_QUERY){
                        MobclickAgentUtil.onEvent(mContext, UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_SOUGOU);
                    }
                    searchTask.resultListener.onResult(searchData, resultList,
                            hasMore, false);
                }
            });
            return true;
        }
        return false;
    }
	
	/**
	 * 搜索点评 只返回结果 不做刷新界面处理
	 * @param searchData
	 * @param last
	 * @param sourceId
	 * @return 搜索结果列表
	 */
	public ArrayList<YelloPageItem> searchDianpingList(final SearchData searchData, boolean last, final int sourceId) {
	    ArrayList<YelloPageItem> resultList = null;
	    if (!mShowDianping || !searchData.showDianping) {
            return resultList;
        }
        final SearchTask searchTask = getSearchTask(searchData);
        if (searchTask == null) {
            return resultList;
        }
        searchTask.yelloPageFactory = YelloPageDianpingFactory.getInstance(mContext);
        final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory
                .search(searchData.keyword, searchData.city,
                        searchData.longitude, searchData.latitude,
                        searchData.category,searchData.source);
        if (getSearchTask(searchData) == null) {
            return resultList;
        }
        resultList = new ArrayList<YelloPageItem>();
        if (list != null && list.size() > 0) {
            resultList.addAll(list);
            /*
             * 不进行特殊规则过滤操作，该处理应该由数据源上进行控制
             * update by hyl 2014-8-20 start
             */
//            for (YelloPageItem item : list) {
//                if (searchData.source == 1) {
//                    if(!filterHospital(searchData,item)){
//                        resultList.add(item);
//                    }
//                } else {
//                    if (TextUtils.isEmpty(YelloPageDianpingFactory.getInstance(
//                            mContext).getCategory()) && !TextUtils.isEmpty(searchData.keyword)) {
//                        if(item.getName().contains(searchData.keyword)){
//                            resultList.add(item);
//                        }
//                    }else{
//                        if(!TextUtils.isEmpty(searchData.keyword) && (searchData.keyword.equals("医院") || searchData.keyword.equals("车站"))){
//                            if (item.getName().contains(searchData.keyword)) {
//                                resultList.add(item);
//                            }
//                        }else{
//                            resultList.add(item);
//                        }
//                    }
//                }
//            }
            //update by hyl 2014-8-20 end
        }
	    return resultList;
	}
	
	/**
	 * 搜索点评并且直接刷新界面
	 * @param searchData
	 * @param last
	 * @param sourceId
	 * @return true-有搜索结果 false-无搜索结果
	 */
	public boolean searchDianping(final SearchData searchData, boolean last, final int sourceId) {
	    final ArrayList<YelloPageItem> resultList = searchDianpingList(searchData, last, sourceId);
	    if(resultList == null){
	        return false;
	    }
	    final SearchTask searchTask = getSearchTask(searchData);
	    final boolean hasMore = searchTask.yelloPageFactory.hasMore();
		/*
		 * update by hyl 2014-8-19 注释
		 * 
		 * 
		if (!mShowDianping || !searchData.showDianping) {
			return false;
		}
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return false;
		}
		searchTask.yelloPageFactory = YelloPageDianpingFactory.getInstance(mContext);
		final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory
				.search(searchData.keyword, searchData.city,
						searchData.longitude, searchData.latitude,
						searchData.category,searchData.source);
		final boolean hasMore = searchTask.yelloPageFactory.hasMore();
		if (getSearchTask(searchData) == null) {
			return false;
		}
		final ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
		if (list != null && list.size() > 0) {
			for (YelloPageItem item : list) {
				if (searchData.source == 1) {
					if(!filterHospital(searchData,item)){
					    resultList.add(item);
					}
				} else {
					if (TextUtils.isEmpty(YelloPageDianpingFactory.getInstance(
							mContext).getCategory()) && !TextUtils.isEmpty(searchData.keyword)) {
						if(item.getName().contains(searchData.keyword)){
							resultList.add(item);
						}
					}else{
						if(!TextUtils.isEmpty(searchData.keyword) && (searchData.keyword.equals("医院") || searchData.keyword.equals("车站"))){
							if (item.getName().contains(searchData.keyword)) {
								resultList.add(item);
							}
						}else{
							resultList.add(item);
						}
					}
				}
			}
		}
		update by hyl 2014-8-19 end
		*/
		if (resultList.size() > 0 || last) {
			searchTask.resultListener.getHandler().post(new Runnable() {
				@Override
				public void run() {
					if (getSearchTask(searchData) == null) {
						return;
					}
					if( mContext != null && sourceId == UMengUtil.SEARCH_SOURCE_QUERY){
						MobclickAgentUtil.onEvent(mContext, UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_DIANPING);
					}
					searchTask.resultListener.onResult(searchData, resultList,hasMore, false);
				}
			});
			return true;
		}
		return false;
	}

	/**
	 * 过滤非医院数据（将名称中不包含医院关键字的数据过滤）
	 * @param searchData
	 * @param item
	 * @return true-需要过滤 false-不需要过滤
	 */
	private boolean filterHospital(SearchData searchData,YelloPageItem item) {
	    if ((!TextUtils.isEmpty(searchData.keyword) && searchData.keyword.equals("医院"))
                || (!TextUtils.isEmpty(searchData.category) && searchData.category.equals("医院"))) {
            if (item.getDistance() > 0
                    && (!TextUtils.isEmpty(searchData.keyword)&& item.getName().contains(searchData.keyword) 
                            || !TextUtils.isEmpty(searchData.category) && item.getName().contains(searchData.category))) {
                return false;
            }
        } else if (item.getDistance() > 0) {
            return false;
        }
	    return true;
    }

    // sml for deal
	public boolean searchDianpingDeals(final SearchData searchData, boolean last) {
		if (!mShowDianping || !searchData.showDianping) {
			return false;
		}
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return false;
		}
		searchTask.yelloPageFactory = YellowPageDianpingDealFactory
				.getInstance(mContext);
		final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory
				.search(searchData.keyword, searchData.city,
						searchData.longitude, searchData.latitude,
						searchData.category,searchData.source);
		final boolean hasMore = searchTask.yelloPageFactory.hasMore();
		if (getSearchTask(searchData) == null) {
			return false;
		}
		final ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
		if (list != null && list.size() > 0) {
			for (YelloPageItem item : list) {
				if (searchData.source == 1 && item.getDistance() == 0.0) {
					continue;
				} else {
					resultList.add(item);
				}
			}
		}
		if (resultList.size() > 0 || last) {
			searchTask.resultListener.getHandler().post(new Runnable() {
				@Override
				public void run() {
					if (getSearchTask(searchData) == null) {
						return;
					}
					searchTask.resultListener.onResult(searchData, resultList,
							hasMore, false);
				}
			});
			return true;
		}
		return false;
	}

	// sml for coupon
	public boolean searchDianpingCoupons(final SearchData searchData,
			boolean last) {
		if (!mShowDianping || !searchData.showDianping) {
			return false;
		}
		final SearchTask searchTask = getSearchTask(searchData);
		if (searchTask == null) {
			return false;
		}
		searchTask.yelloPageFactory = YellowPageCouponFactory
				.getInstance(mContext);
		final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory
				.search(searchData.keyword, searchData.city,
						searchData.longitude, searchData.latitude,
						searchData.category,searchData.source);
		final boolean hasMore = searchTask.yelloPageFactory.hasMore();
		if (getSearchTask(searchData) == null) {
			return false;
		}
		final ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
		if (list != null && list.size() > 0) {
			for (YelloPageItem item : list) {
				if (searchData.source == 1 && item.getDistance() == 0.0) {
					continue;
				} else {
					resultList.add(item);
				}
			}
		}
		if (resultList.size() > 0 || last) {
			searchTask.resultListener.getHandler().post(new Runnable() {
				@Override
				public void run() {
					if (getSearchTask(searchData) == null) {
						return;
					}
					searchTask.resultListener.onResult(searchData, resultList,
							hasMore, false);
				}
			});
			return true;
		}
		return false;
	}

	class DianpingThread implements Runnable {

		private SearchData mSearchData;

		public DianpingThread(SearchData searchData) {
			this.mSearchData = searchData;
		}

		@Override
		public void run() {
			initDianpingResult(mSearchData);
		}

	}

	// sml for deal
	class DianpingDealThread implements Runnable {

		private SearchData mSearchData;

		public DianpingDealThread(SearchData searchData) {
			this.mSearchData = searchData;
		}

		@Override
		public void run() {
			initDianpingDealResult(mSearchData);
		}

	}

	// sml for coupon
	class DianpingCouponThread implements Runnable {

		private SearchData mSearchData;

		public DianpingCouponThread(SearchData searchData) {
			this.mSearchData = searchData;
		}

		@Override
		public void run() {
			initDianpingCouponResult(mSearchData);
		}

	}

	class SougouThread implements Runnable {

		private SearchData mSearchData;

		public SougouThread(SearchData searchData) {
			this.mSearchData = searchData;
		}

		@Override
		public void run() {
			initSougouResult(mSearchData);
		}

	}

	boolean search_hasMore = false;//hyl 2014-8-18
	
	class MyHandler extends Handler {
		boolean mEnable = true;

		public MyHandler(Looper looper) {
			super(looper);
		}

		public synchronized void setEnable(boolean enable) {
			this.mEnable = enable;
		}

		public synchronized boolean getEnable() {
			return this.mEnable;
		}

		@Override
		public void handleMessage(Message msg) {
			if (!getEnable()) {
				return;
			}
			switch (msg.what) {
			case 0: {
				final SearchData searchData = (SearchData) msg.obj;
				final int sourceId = msg.arg1;
				// if (NetUtil.is2G(mContext)) {
				// if (searchData.keyword != null
				// && !searchData.keyword.equals("")) {
				// // 关键字首先使用搜狗进行搜索//
				// if (searchSougou(searchData, false)) {
				// return;
				// }
				// searchDianping(searchData, true);
				//
				// } else {// 先用大众点评搜索//
				if(!TextUtils.isEmpty(searchData.keyword) && YellowUtil.isNumeric(searchData.keyword)){
					searchNumber(searchData);
				}else{
//				    if (searchDianping(searchData, false, sourceId)) {
//				        return ;
//				    }
//				    searchSougou(searchData, true, sourceId);
				    
				    //update by hyl 2014-8-19
				    //搜索点评
//				    boolean hasResult = searchDianping(searchData, false, sourceId);
//				    boolean isNeedSearchOtherSource = doSearchDianPing(hasResult);
//                    if(isNeedSearchOtherSource){
//                        //搜索高德
//                        hasResult = searchGaoDe(searchData, false, sourceId);
//                        isNeedSearchOtherSource = doSearchGaoDe(hasResult);
//                        if(isNeedSearchOtherSource){
//                            //搜索搜狗
//                            searchSougou(searchData, true, sourceId);
//                        }
//                    }
				    //update by hyl 2014-8-19 end
				    
				    /*
				     * 修改搜索逻辑:
				     * 1、先搜索点评，如果发现点评没有更多结果了 则开始搜索高德
				     * 2、高德也没有更多数据时，则开始搜索搜狗
				     * update by hyl 2014-8-19 start
				     * 
				     * old code:
				     *    if (searchDianping(searchData, false, sourceId)) {//搜索到点评数据则不再搜索
                              return ;
                          }
                          //未搜索到点评数据则开始搜索搜狗数据
                          searchSougou(searchData, true, sourceId);
				     */
				    boolean isLast = false;
				    boolean hasResult = false;
				    
				    final ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
				    ArrayList<YelloPageItem> dianpingResultList = searchDianpingList(searchData, false, sourceId);

				    if(dianpingResultList != null && dianpingResultList.size() > 0){
				        hasResult = true;
				        resultList.addAll(dianpingResultList);
				    }
				    search_hasMore = YelloPageDianpingFactory.getInstance(mContext).hasMore();
				    
				    boolean isNeedSearchOtherSource = doSearchDianPing(hasResult);
					if(isNeedSearchOtherSource){
					    //搜索高德
					    ArrayList<YelloPageItem> gaodeResultList = searchGaoDeList(searchData, false, sourceId);
					    isLast = true;
					    search_hasMore = YelloPageGaoDeFactory.getInstance(mContext).hasMore();
					    if(gaodeResultList != null && gaodeResultList.size() > 0){
	                        hasResult = true;
	                        resultList.addAll(gaodeResultList);
	                    }
					    isNeedSearchOtherSource = doSearchGaoDe(hasResult);
					    if(isNeedSearchOtherSource){
					        //搜索搜狗
					        ArrayList<YelloPageItem> sougouResultList = searchSougouList(searchData, true, sourceId);
	                        isLast = true;
	                        search_hasMore = YelloPageSougouFactory.getInstance(mContext).hasMore();
	                        
	                        if(sougouResultList != null && sougouResultList.size() > 0){
	                            hasResult = true;
	                            resultList.addAll(sougouResultList);
	                        }
					    }
					}
					final SearchTask searchTask = getSearchTask(searchData);
					if(searchTask != null && (resultList.size() > 0 || isLast)){
					    searchTask.resultListener.getHandler().post(new Runnable() {
					        @Override
					        public void run() {
					            if (getSearchTask(searchData) == null) {
					                return;
					            }
					            if( mContext != null && sourceId == UMengUtil.SEARCH_SOURCE_QUERY){
					                MobclickAgentUtil.onEvent(mContext, UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_DIANPING);
					            }
					            searchTask.resultListener.onResult(searchData, resultList,search_hasMore, false);
					        }
					    });
					}
					//update by hyl 2014-8-19 end
				}
				
				// }
				// mSearchWith2G = true;
				// } else {
				// searchResult(searchData);
				// mSearchWith2G = false;
				// }

			}
				break;
			case 1: {
				final SearchData searchData = (SearchData) msg.obj;
				// if (mSearchWith2G) {
				final SearchTask searchTask = getSearchTask(searchData);
				if (searchTask == null) {
					return;
				}
				if (searchTask.yelloPageFactory instanceof YelloPageDianpingFactory
						&& (!mShowDianping || !searchData.showDianping)) {
					return;
				}
				if (searchTask.yelloPageFactory instanceof YelloPageSougouFactory
						&& (!mShowSougou || !searchData.showSougou)) {
					return;
				}
				
				/*
				 * update by hyl 2014-8-18 start
				 * old code:
				 *  final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory.searchMore();
                    final boolean hasMore = searchTask.yelloPageFactory.hasMore();
				 */
				ArrayList<YelloPageItem> list = searchTask.yelloPageFactory.searchMore();
				search_hasMore = searchTask.yelloPageFactory.hasMore();
				
				boolean hasResult = false;
				if(list != null && list.size() > 0){
				    hasResult = true;
				}
				if(searchTask.yelloPageFactory instanceof YelloPageDianpingFactory){
				    boolean isNeedSearchOtherSource = doSearchDianPing(hasResult);
				    if(isNeedSearchOtherSource){
				        searchTask.yelloPageFactory = YelloPageGaoDeFactory.getInstance(mContext);
				        ArrayList<YelloPageItem> gaodeList = YelloPageGaoDeFactory.getInstance(mContext).searchMore(searchData);
				        search_hasMore = searchTask.yelloPageFactory.hasMore();
				        if(gaodeList != null && gaodeList.size() > 0){
		                    hasResult = true;
		                    list.addAll(gaodeList);
		                }
				        isNeedSearchOtherSource = doSearchGaoDe(hasResult);
				        if(isNeedSearchOtherSource){
				            searchTask.yelloPageFactory = YelloPageSougouFactory.getInstance(mContext);
				            ArrayList<YelloPageItem> sougouList = YelloPageSougouFactory.getInstance(mContext).searchMore(searchData);
				            list.addAll(sougouList);
				            search_hasMore = searchTask.yelloPageFactory.hasMore();
				        }
				    }
				}else if(searchTask.yelloPageFactory instanceof YelloPageGaoDeFactory){
				    boolean isNeedSearchOtherSource = doSearchGaoDe(hasResult);
                    if(isNeedSearchOtherSource){
                        searchTask.yelloPageFactory = YelloPageSougouFactory.getInstance(mContext);
                        ArrayList<YelloPageItem> sougouList = YelloPageSougouFactory.getInstance(mContext).searchMore(searchData);
                        list.addAll(sougouList);
                        search_hasMore = searchTask.yelloPageFactory.hasMore();
                    }
				}
				//update by hyl 2014-8-18 end
				
				
				if (getSearchTask(searchData) == null) {
					return;
				}

				final ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
				if (list != null && list.size() > 0) {
				    resultList.addAll(list);
				    
//					for (YelloPageItem item : list) {
//						if (searchData.source == 1) {
//						    if(!filterHospital(searchData, item)){
//						        resultList.add(item);
//						    }
//						} else {
//							if (searchTask.yelloPageFactory instanceof YelloPageSougouFactory) {
//								resultList.add(item);
//							} else if (TextUtils.isEmpty(YelloPageDianpingFactory.getInstance(mContext).getCategory())
//									&& !TextUtils.isEmpty(searchData.keyword)) {
////								if (item.getName().contains(searchData.keyword)) {
//									resultList.add(item);
////								}
//							} else {
//								if(!TextUtils.isEmpty(searchData.keyword) && (searchData.keyword.equals("医院") || searchData.keyword.equals("车站"))){
//									if (item.getName().contains(searchData.keyword)) {
//										resultList.add(item);
//									}
//								}else{
//									resultList.add(item);
//								}
//							}
//						}
//					}
				}
				searchTask.resultListener.getHandler().post(new Runnable() {
					@Override
					public void run() {
						if (getSearchTask(searchData) == null) {
							return;
						}
						searchTask.resultListener.onResult(searchData,
								resultList, search_hasMore, false);
					}
				});

				// } else {
				// searchMoreRelust(searchData);
				// }
			}
				break;
			// sml for deal
			case 2: {
				final SearchData searchData = (SearchData) msg.obj;
				// if (NetUtil.is2G(mContext)) {
				// if (searchData.keyword != null
				// && !searchData.keyword.equals("")) {
				// // 关键字首先使用搜狗进行搜索//
				// if (searchSougou(searchData, false)) {
				// return;
				// }
				// searchDianpingDeals(searchData, true);
				//
				// } else {// 先用大众点评搜索//
				if (searchDianpingDeals(searchData, true)) {
					return;
				}
				// searchSougou(searchData, true);
				// }
				// mSearchWith2G = true;
				// } else {
				// searchDealResult(searchData);
				// mSearchWith2G = false;
				// }

			}
				break;
			case 3: {
				final SearchData searchData = (SearchData) msg.obj;
				// if (mSearchWith2G) {
				final SearchTask searchTask = getSearchTask(searchData);
				if (searchTask == null) {
					return;
				}
				if (searchTask.yelloPageFactory instanceof YellowPageDianpingDealFactory
						&& (!mShowDianping || !searchData.showDianping)) {
					return;
				}
				// if (searchTask.yelloPageFactory instanceof
				// YelloPageSougouFactory
				// && (!mShowSougou || !searchData.showSougou)) {
				// return;
				// }
				final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory
						.searchMore();
				final boolean hasMore = searchTask.yelloPageFactory.hasMore();
				if (getSearchTask(searchData) == null) {
					return;
				}

				final ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
				if (list != null && list.size() > 0) {
					for (YelloPageItem item : list) {
						if (searchData.source == 1 && item.getDistance() == 0.0) {
							continue;
						} else {
							resultList.add(item);
						}
					}
				}
				searchTask.resultListener.getHandler().post(new Runnable() {
					@Override
					public void run() {
						if (getSearchTask(searchData) == null) {
							return;
						}
						searchTask.resultListener.onResult(searchData,
								resultList, hasMore, false);
					}
				});

				// } else {
				// searchMoreDealRelust(searchData);
				// }
			}
				break;

			// sml for coupon
			case 4: {
				final SearchData searchData = (SearchData) msg.obj;
				// if (NetUtil.is2G(mContext)) {
				// if (searchData.keyword != null
				// && !searchData.keyword.equals("")) {
				// // 关键字首先使用搜狗进行搜索//
				// if (searchSougou(searchData, false)) {
				// return;
				// }
				// searchDianpingCoupons(searchData, true);
				//
				// } else {// 先用大众点评搜索//
				if (searchDianpingCoupons(searchData, true)) {
					return;
				}
				// searchSougou(searchData, true);
				// }
				// mSearchWith2G = true;
				// } else {
				// searchCouponResult(searchData);
				// mSearchWith2G = false;
				// }
				//
			}
				break;
			case 5: {
				final SearchData searchData = (SearchData) msg.obj;
				// if (mSearchWith2G) {
				final SearchTask searchTask = getSearchTask(searchData);
				if (searchTask == null) {
					return;
				}
				if (searchTask.yelloPageFactory instanceof YellowPageCouponFactory
						&& (!mShowDianping || !searchData.showDianping)) {
					return;
				}
				// if (searchTask.yelloPageFactory instanceof
				// YelloPageSougouFactory
				// && (!mShowSougou || !searchData.showSougou)) {
				// return;
				// }
				final ArrayList<YelloPageItem> list = searchTask.yelloPageFactory
						.searchMore();
				final boolean hasMore = searchTask.yelloPageFactory.hasMore();
				if (getSearchTask(searchData) == null) {
					return;
				}

				final ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
				if (list != null && list.size() > 0) {
					for (YelloPageItem item : list) {
						if (searchData.source == 1 && item.getDistance() == 0.0) {
							continue;
						} else {
							resultList.add(item);
						}
					}
				}
				searchTask.resultListener.getHandler().post(new Runnable() {
					@Override
					public void run() {
						if (getSearchTask(searchData) == null) {
							return;
						}
						searchTask.resultListener.onResult(searchData,
								resultList, hasMore, false);
					}
				});

				// } else {
				// searchMoreCouponRelust(searchData);
				// }
			}
				break;

			}
		}
	}

	/**
	 * 处理点评搜索
	 * @param searchData
	 * @param last
	 * @param sourceId
	 * @return
	 */
    public boolean doSearchDianPing(boolean hasResult) {
        boolean isNeedSearchOther = false;
        if (hasResult) {//搜索有结果
            if(YelloPageDianpingFactory.getInstance(mContext).hasMore()){//还有更多数据
                isNeedSearchOther = false; 
            }else{//已经没有更多数据了
                isNeedSearchOther = true;
            }
        }else{//搜索结果
            isNeedSearchOther = true;
        }
        return isNeedSearchOther;
    }
    
    /**
     * 处理高德搜索
     * @param searchData
     * @param last
     * @param sourceId
     * @return
     */
    public boolean doSearchGaoDe(boolean hasResult) {
        boolean isNeedSearchOther = false;
        if (hasResult) {//搜索有结果
            if(YelloPageGaoDeFactory.getInstance(mContext).hasMore()){//还有更多数据
                isNeedSearchOther = false; 
            }else{//已经没有更多数据了
                isNeedSearchOther = true;
            }
        }else{//搜索结果
            isNeedSearchOther = true;
        }
        return isNeedSearchOther;
    }
    
}

class SearchTask {
	public volatile SearchData searchData;
	public volatile MyHandler myHandler;
	public volatile ResultListener resultListener;
	public volatile YelloPageFactory yelloPageFactory;

	public SearchTask(SearchData searchData, ResultListener listener,
			MyHandler myHandler, YelloPageFactory yelloPageFactory) {
		this.searchData = searchData;
		this.resultListener = listener;
		this.myHandler = myHandler;
		this.yelloPageFactory = yelloPageFactory;
	}
}
