package so.putao.findplug;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ui.yellowpage.bean.GaoDePoiItem;
import so.contacts.hub.util.UMengEventIds;
import android.content.Context;
import android.text.TextUtils;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import so.contacts.hub.util.MobclickAgentUtil;

public class YelloPageGaoDeFactory extends YelloPageFactory{
	
    private static final int DISTACNE_LIMIT = 50*1000;//搜索距离 限制 50公里
    private static final int PAGE_SIZE = 20;//单页数量
    
	private List<YelloPageItem> mAllDianPingItemList = new ArrayList<YelloPageItem>();
	
	private Context context;
	private boolean mHasMore = true;
	private int mPage = 0;
	private String mWords;
	private String mCity;
	private double mLongitude;
	private double mLatitude;
	private String mCategory;
	
	private static YelloPageGaoDeFactory mInstance;
	
	public static YelloPageGaoDeFactory getInstance(Context c) {
		if(mInstance == null){
			mInstance = new YelloPageGaoDeFactory(c);
		}
		return mInstance;
	}
	
	private YelloPageGaoDeFactory(Context c){
		this.context = c;
	}
	
	@Override
	public ArrayList<YelloPageItem> search(String keyword, String city, double longitude,double latitude,String category,int source) {
	    mPage = 0;
	    return searchData(keyword, city, longitude, latitude, category, mPage);
	}
	
	private ArrayList<YelloPageItem> searchData(String keyword, String city, double longitude,double latitude,String category,int page){
		try {
			ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
			if(TextUtils.isEmpty(category) && TextUtils.isEmpty(keyword)){
				mHasMore = false;
				return resultList;
			}
			resultList = searchRaw(keyword, city, longitude,latitude,category,page);
			return resultList;
		} catch (Exception e) {
			mHasMore = false;
			return new ArrayList<YelloPageItem>();	
		}
	}
	
	public ArrayList<YelloPageItem> searchRaw(String keyword, String city, double longitude,double latitude,String category,int page) {
		this.mCategory = category;
		this.mCity = city;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mWords = keyword;
		this.mPage = page;
        
		ArrayList<YelloPageItem> list = new ArrayList<YelloPageItem>();
        
        if(!TextUtils.isEmpty(category)){
            category = category.replaceAll(",","|");
        }
        
        PoiSearch.Query query = new PoiSearch.Query(keyword, category,city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(PAGE_SIZE);// 设置每页最多返回多少条poiitem
        query.setPageNum(page);// 设置查第一页
        

        PoiSearch poiSearch = new PoiSearch(context, query);
        
        //22.534763&longitude=113.943885
        if(latitude != 0.0 || longitude != 0.0){
            poiSearch.setBound(new SearchBound(new LatLonPoint(latitude, longitude), DISTACNE_LIMIT , true));
        }
        int pageCount = 0;
        List<PoiItem> poiItems = null;
        try {
            PoiResult poiResult = poiSearch.searchPOI();
            pageCount = poiResult.getPageCount();
            poiItems = poiResult.getPois();
        } catch (AMapException e) {
            e.printStackTrace();
//            Toast.makeText(context, "搜索无结果", Toast.LENGTH_SHORT).show();
        }
        if(page == 0){
        	mAllDianPingItemList.clear();
        }
        if(poiItems.size()==20 && pageCount > (page+1)){
        	mHasMore = true;
        }else{
        	mHasMore = false;
        }
        for(PoiItem poiItem: poiItems) {
            GaoDePoiItem gdPoiItem = new GaoDePoiItem();
            gdPoiItem.setPoiId(poiItem.getPoiId());
            gdPoiItem.setAddress(poiItem.getSnippet());
            gdPoiItem.setName(poiItem.getTitle());
            gdPoiItem.setTelephone(poiItem.getTel());
            gdPoiItem.setDistance(poiItem.getDistance());
            gdPoiItem.setLatitude(poiItem.getLatLonPoint().getLatitude());
            gdPoiItem.setLongitude(poiItem.getLatLonPoint().getLongitude());
            gdPoiItem.setWebsite(poiItem.getWebsite());
            
        	YellowPageItemGaoDe yellowPageItemGaoDe = new YellowPageItemGaoDe(gdPoiItem);
        	list.add(yellowPageItemGaoDe);
        }
        
		if (list.size() == 0) {
			if (null != context) {
				MobclickAgentUtil
						.onEvent(
								context,
								UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_DIANPING_NO_DATA);
			}
		}
        
        mAllDianPingItemList.addAll(list);
        return list;
	}
	
	public ArrayList<YelloPageItem> searchMore(SearchData searchData) {
	    mPage  = 0;
        mWords = searchData.keyword;
        mCity = searchData.city;
        mLatitude = searchData.latitude;
        mLongitude = searchData.longitude;
        mCategory = searchData.category;
        return searchData(mWords, mCity, mLongitude, mLatitude, mCategory, mPage);
    }

	@Override
	public ArrayList<YelloPageItem> searchMore() {
		return searchData(mWords, mCity, mLongitude, mLatitude, mCategory, mPage + 1);
	}

	@Override
	public boolean hasMore() {
		return mHasMore;
	}
	
	public String getCategory(){
		return mCategory;
	}
	
}
