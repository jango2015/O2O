package so.putao.findplug;

import so.contacts.hub.thirdparty.dianping.DianPingApiTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import so.contacts.hub.util.UMengEventIds;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import so.contacts.hub.util.MobclickAgentUtil;

public class YellowPageCouponFactory extends YelloPageFactory{
	private List<YelloPageItem> mAllDianPingItemList = new ArrayList<YelloPageItem>();
	
	private Context context;
	private boolean mHasMore = true;
	private int mPage = 1;
	private String mWords;
	private String mCity;
	private double mLongitude;
	private double mLatitude;
	private String mCategory;
	private int invalid_count = 0;
	
	private static YellowPageCouponFactory mInstance;
	
	public static YellowPageCouponFactory getInstance(Context c) {
		if(mInstance == null){
			mInstance = new YellowPageCouponFactory(c);
		}
		return mInstance;
	}
	
	private YellowPageCouponFactory(Context c){
		this.context = c;
	}
	
	@Override
	public ArrayList<YelloPageItem> search(String keyword, String city, double longitude,double latitude,String category,int source) {
		return searchData(keyword, city, longitude, latitude, category, 1);
	}
	
	private ArrayList<YelloPageItem> searchData(String keyword, String city, double longitude,double latitude,String category,int page){
		try {
			ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
			if(TextUtils.isEmpty(category) && TextUtils.isEmpty(keyword)){
//				mHasMore = false;
//				return resultList;
			}
			resultList = searchRaw(keyword, city, longitude,latitude,category,page);
			return resultList;
		} catch (Exception e) {
			mHasMore = false;
			return new ArrayList<YelloPageItem>();	
		}
	}
	
	public ArrayList<YelloPageItem> searchRaw(String keyword, String city, double longitude,double latitude,String category,int page) {
		
//		if (null != context) {
//			MobclickAgentUtil.onEvent(context,
//					UMengEventIds.DISCOVER_YELLOWPAGE_COUPON_SEARCH);
//		}
		
		this.mCategory = category;
		this.mCity = city;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mWords = keyword;
		this.mPage = page;
        
//		if (mPage > 1) {
//			if (null != context) {
//				MobclickAgentUtil.onEvent(context,
//						UMengEventIds.DISCOVER_YELLOWPAGE_COUPON_SEARCH_NEXT_PAGE);
//			}
//		}
		
		ArrayList<YelloPageItem> list = new ArrayList<YelloPageItem>();
        Map<String, String> paramMap = new HashMap<String, String>();
        if(latitude != 0.0 || longitude != 0.0){ //modify by ffh 修改切换城市后大众点评搜不出来的问题
        	paramMap.put("latitude", ""+latitude);
            paramMap.put("longitude", ""+longitude);

            paramMap.put("sort", "1");
            paramMap.put("radius", "5000");
        }
        paramMap.put("city", city);
        if(category != null && !category.equals("")) {
            paramMap.put("category", category);
        }
        if(keyword != null && !keyword.equals("")) {
            paramMap.put("keyword", keyword);
        }
        paramMap.put("limit", "20");
        paramMap.put("page", String.valueOf(page));
        paramMap.put("format", "json");

        String requestResult = DianPingApiTool.requestApi(DianPingApiTool.URL_GET_FIND_COUPON_INFO, paramMap);
        Log.e("error", "requestResult == " + requestResult);
        CouponResponse businessesResponse = new Gson().fromJson(requestResult, CouponResponse.class);
        Log.e("error", "businessesResponse == " + businessesResponse);
        if(!businessesResponse.status.equals("OK")) {
//			if (null != context) {
//				MobclickAgentUtil.onEvent(context,
//						UMengEventIds.DISCOVER_YELLOWPAGE_COUPON_SEARCH_ERROR);
//			}
			mHasMore = false;
        	return list;
        }
        if(page == 1){
        	mAllDianPingItemList.clear();
        	invalid_count = 0;
        }
        if(businessesResponse.total_count > businessesResponse.coupons.size() + mAllDianPingItemList.size() + invalid_count){
        	mHasMore = true;
        }else{
        	mHasMore = false;
        }
        for(DianpingCoupon business:businessesResponse.coupons) {
//        	if(business.name.contains("(")) {
//        		business.name = business.name.substring(0, business.name.indexOf("("));
//        	}
        	YellowPageItemDianpingCoupon yellowPageItemDianping = new YellowPageItemDianpingCoupon(business);
//	        	Log.e("error", "yellowPageItemDianping.getLogoBitmap() == " + yellowPageItemDianping.getLogoBitmap());
            if(0 == business.businesses.size())
            {
            	invalid_count++;
            	continue;
            }
        	list.add(yellowPageItemDianping);
        }
        
//		if (null != context) {
//			if (list.size() == 0) {
//				MobclickAgentUtil
//						.onEvent(
//								context,
//								UMengEventIds.DISCOVER_YELLOWPAGE_COUPON_SEARCH_NO_DATA);
//			} else {
//				MobclickAgentUtil
//						.onEvent(
//								context,
//								UMengEventIds.DISCOVER_YELLOWPAGE_COUPON_SEARCH_HAVE_DATA);
//			}
//		}
        
        mAllDianPingItemList.addAll(list);
        return list;
	}

	@Override
	public ArrayList<YelloPageItem> searchMore() {
		return searchData(mWords, mCity, mLongitude, mLatitude, mCategory, mPage + 1);
	}

	@Override
	public boolean hasMore() {
		return mHasMore;
	}
	
}
