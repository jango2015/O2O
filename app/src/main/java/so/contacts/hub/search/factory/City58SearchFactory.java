package so.contacts.hub.search.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.city.CityListDB;
import so.contacts.hub.core.Config;
import so.contacts.hub.search.Searchable;
import so.contacts.hub.search.Solution;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.thirdparty.city58.City58ApiTool;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.putao.findplug.City58Item;
import so.putao.findplug.City58Response;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YellowPageItemCity58;
import android.content.Context;
import android.text.TextUtils;

/**
 * 
 * @author putao_lhq
 * @version 2014年9月24日
 */
public class City58SearchFactory implements Searchable {

	private SearchInfo mSearchInfo;
	private static final String TAG = "City58SearchFactory";
	private static final int MAX_COUNT = 20;//请求最大数量
	private int mPage = -1;
	private boolean mHasMore = true;
	private Context context;
	
    @SuppressWarnings("rawtypes")
	@Override
    public List<YelloPageItem> search(Solution sol, String searchInfo) {
        if (searchInfo != null) {
            mSearchInfo = Config.mGson.fromJson(searchInfo, SearchInfo.class);
        }
        return search(sol, mSearchInfo);
    }

	@SuppressWarnings("rawtypes")
	@Override
	public List<YelloPageItem> search(Solution sol, SearchInfo searchInfo) {
		LogUtil.v(TAG, "enter in 58 search searchInfo = " + searchInfo);
		mSearchInfo = searchInfo;
		context = sol.getActivity();
		String category = mSearchInfo.getCategory();
		if (category == null) {
			LogUtil.d(TAG, "enter in 58 search searchInfo category is null");
			category = "";
		}
		
		if(TextUtils.isEmpty(category)) {
		    category = searchInfo.getWords();
		}
		
        if (TextUtils.isEmpty(category)) {
            category = sol.getInputKeyword();
        }
		
		return searchData(sol.getInputCity(), sol.getInputLongtitude(), sol.getInputLatitude(), 
				category, mPage+1);
	}

	@SuppressWarnings("rawtypes")
	private List<YelloPageItem> searchData(String city, double longitude,
			double latitude,String category,int page) {
		ArrayList<YelloPageItem> results = new ArrayList<YelloPageItem>();
		int pageSize = configPage(page);
		mPage = page;
		
//		String cityAlice = "";
//		if(!TextUtils.isEmpty(city)) {
//		    cityAlice = City58ApiTool.getCityAlice(city);
////		    cityAlice = City58ApiTool.getCityCodeFromDb(city);//modify by lisheng 2014-11-07
//		}
		
        String cityAlice = ContactsAppUtils.getInstance().getDatabaseHelper().getCityListDB().getCityIdByName(city, CityListDB.CITY_SOURCE_TYPE_58);
		
		Map<String, String> paramMap = getParam(cityAlice, longitude, latitude, 
				category,pageSize);
		
		LogUtil.d(TAG, "searchData city="+cityAlice+" lon="+longitude+" lat="+latitude+" category="+category+" page="+page);
		String requestResult = City58ApiTool.requestApi(City58ApiTool.URL_GET_CATEGORY_INFO, paramMap);
		if ( TextUtils.isEmpty(requestResult) ) {
			// add by putao_lhq 2014年10月10日 for UMeng start
            if (null != context) {
                MobclickAgentUtil
                        .onEvent(
                                context,
                                UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_58_FAIL);
            }
            // add by putao_lhq 2014年10月10日 for UMeng end
			return results;
		}
		
		/**
		 * add by zjh 2014-01-14 start
		 * 解析JsonP格式数据 
		 * 注：JsonP格式需要设置paramMap.put("callback", "JSON_HEAD");
		 * 那么返回的格式数据为： JSON_HEAD(json)
		 * 不设置paramMap.put("callback", "JSON_HEAD")，默认为null
		 * 因此返回为:null(json)
		 */
		String JSONP_HEAD = "null"; //requestResult默认格式为：null(json)
		if( requestResult.startsWith(JSONP_HEAD) ){
			int len = requestResult.length();
			if( len <= JSONP_HEAD.length() + 2 ){
	            if (null != context) {
	                MobclickAgentUtil.onEvent(context, UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_58_FAIL);
	            }
				return results;
			}
			requestResult = requestResult.substring(JSONP_HEAD.length() + 1, len -1);
		}
		/** add by zjh 2014-01-14 end */
		
		City58Response businessesResponse = null;
		try {
		    businessesResponse = 
				Config.mGson.fromJson(requestResult, City58Response.class);
		} catch (Exception e){
		    LogUtil.e(TAG, e.getMessage());
		    e.printStackTrace();
		}
		
		LogUtil.v(TAG, "businessesResponse: " + businessesResponse);
		
		if(businessesResponse != null && businessesResponse.data != null && businessesResponse.data.size() > 0) {
    		for (City58Item item : businessesResponse.data) {
    			YellowPageItemCity58 yellowItem = new YellowPageItemCity58(item);
    			results.add(yellowItem);
    		}
    		
            if(mPage>=3 || businessesResponse.data.size() % MAX_COUNT != 0) {
                mHasMore = false;
            } else {
                mHasMore = true;
            }
    		// add by putao_lhq 2014年10月10日 for UMeng start
            if (null != context) {
                MobclickAgentUtil
                        .onEvent(
                                context,
                                UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_58_SUCCESS);
            }
            // add by putao_lhq 2014年10月10日 for UMeng end
		} else {
            mHasMore = false;
            // add by putao_lhq 2014年10月10日 for UMeng start
            if (null != context) {
                MobclickAgentUtil
                        .onEvent(
                                context,
                                UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_58_NO_DATA);
            }
            // add by putao_lhq 2014年10月10日 for UMeng end
		}
		
 		return results;
	}

	private int configPage(int page) {
		if (page == 0) {
			page = 1;
		}
		page = page * MAX_COUNT;
		return page;
	}

	private Map<String, String> getParam(String city,double longitude, double latitude,
			String category, int page) {
		Map<String, String> paramMap = new HashMap<String, String>();
		if (longitude != 0) {
			paramMap.put("lon", String.valueOf(longitude));
		}
		if (latitude != 0) {
			paramMap.put("lat", String.valueOf(latitude));
		}
		if(!TextUtils.isEmpty(city))
		    paramMap.put("l", city);
		paramMap.put("dist", String.valueOf(5000));
		paramMap.put("c", category);
		paramMap.put("n", String.valueOf(page));
		return paramMap;
	}

	@Override
	public boolean hasMore() {
		return mHasMore;
	}
	
    @Override
    public int getPage() {
        return mPage;
    }

}
