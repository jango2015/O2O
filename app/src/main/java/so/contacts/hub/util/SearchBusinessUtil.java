package so.contacts.hub.util;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.yellow.data.SearchTelResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.sogou.hmt.sdk.manager.HMTNumber;
import com.sogou.hmt.sdk.manager.HmtSdkManager;

/**
 * 
 * @author putao_lhq
 * @version 2014年9月21日
 */
public class SearchBusinessUtil {

	private HandlerThread mQueryThread;
	private QueryHandler mQueryHandler;
	private static final String TAG = "SearchBusinessUtil";
	
	private static SearchBusinessUtil sInstance;

	private Context mContext;

	private SearchBusinessUtil(Context context) {
		this.mContext = context;
		mQueryThread = new HandlerThread("SearchBusiness#");
		mQueryThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
		mQueryThread.start();
		mQueryHandler = new QueryHandler(mQueryThread.getLooper());
	}

	public static SearchBusinessUtil getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new SearchBusinessUtil(context);
		}
		return sInstance;
	}

	public void search(String number) {
		Message msg = mQueryHandler
				.obtainMessage(QueryHandler.MSG_QUERY_FROM_GAODE_START);
		msg.obj = number;
		mQueryHandler.sendMessage(msg);
	}

	public void onDestroy() {
		mQueryThread.quit();
	}

	class QueryHandler extends Handler {

		public static final int MSG_QUERY_FROM_GAODE_START = 0;
		public static final int MSG_QUERY_FROM_SOGOU = 1;
		public QueryHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			String tel = (String) msg.obj;
			switch (msg.what) {
			case MSG_QUERY_FROM_GAODE_START:
				doGaodeSearch(tel);
				break;
			case MSG_QUERY_FROM_SOGOU:
				doSogouSearch(tel);
				break;
			default:
				break;
			}
		}
	}

	private void doGaodeSearch(String tel) {
		PoiSearch.Query query = new PoiSearch.Query(tel, "", "");
		query.setPageSize(10);
		query.setPageNum(0);
		PoiSearch poiSearch = null;
		try {
			poiSearch = new PoiSearch(mContext, query);
		} catch (Exception e) {
			LogUtil.d(TAG, "new poiSearch Exception: " + e);
		}
		if (poiSearch == null) {
			poiSearch = new PoiSearch(mContext, query);
		}
		LogUtil.d(TAG, "poiSearch query: " + poiSearch.getQuery().getQueryString());
		PoiResult result;
		try {
			result = poiSearch.searchPOI();
			if(result == null || result.getPageCount() <= 0) {
				Message msg = mQueryHandler.obtainMessage(QueryHandler.MSG_QUERY_FROM_SOGOU);
				msg.obj = tel;
				mQueryHandler.sendMessage(msg);
				return;
			}
			List<PoiItem> items = result.getPois();
			LogUtil.d(TAG, "page count: " + result.getPageCount());
			ArrayList<SearchTelResult> results = new ArrayList<SearchTelResult>();
			for (PoiItem item : items) {
				LogUtil.d(TAG, "item title: " + item.getTitle());
				SearchTelResult searchResult = new SearchTelResult();
				searchResult.setAddress(item.getSnippet());
				searchResult.setPoiId(item.getPoiId());
				searchResult.setName(item.getTitle());
				searchResult.setTelephone(item.getTel());
				searchResult.setDistance(item.getDistance());
				searchResult.setLatitude(item.getLatLonPoint().getLatitude());
				searchResult.setLongitude(item.getLatLonPoint().getLongitude());
				searchResult.setWebsite(item.getWebsite());
				results.add(searchResult);
			}
			Intent intent = new Intent("com.android.action.SearchTel");
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("convert", results);
			intent.putExtras(bundle);
			mContext.sendBroadcast(intent);
		} catch (AMapException e) {
			LogUtil.d(TAG, "AMapException: " + e.getErrorMessage());
			e.printStackTrace();
		}
	}

	public void doSogouSearch(String tel) {
		HMTNumber hmtNumber = HmtSdkManager.getInstance().checkNumberFromLocal(tel);
		if (hmtNumber == null) {
			hmtNumber = HmtSdkManager.getInstance().checkNumberFromNet(tel);
		}
		
		ArrayList<SearchTelResult> results = new ArrayList<SearchTelResult>();
		SearchTelResult result = new SearchTelResult();
		results.add(result);
		if (hmtNumber.getMarkContent() != null) {
			result.setName(hmtNumber.getMarkContent());
		}
		Intent intent = new Intent("com.android.action.SearchTel");
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("convert", results);
		intent.putExtras(bundle);
		mContext.sendBroadcast(intent);
		
	}
	
}
