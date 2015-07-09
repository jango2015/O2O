package so.contacts.hub.search.factory;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.Config;
import so.contacts.hub.search.Searchable;
import so.contacts.hub.search.Solution;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.ui.yellowpage.bean.GaoDePoiItem;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YellowPageItemGaoDe;
import android.content.Context;
import android.text.TextUtils;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import so.contacts.hub.util.MobclickAgentUtil;

public class GaodeSearchFactory implements Searchable {
    private static final String TAG = "GaodeSearchFactory";

    private static final int DISTACNE_LIMIT = 50*1000;//搜索距离 限制 50公里
    private static final int PAGE_SIZE = 20;//单页数量
    
    private List<YelloPageItem> mAllDianPingItemList = new ArrayList<YelloPageItem>();
    
    private SearchInfo mSearchInfo;
    private Context context;
    private boolean mHasMore = true;
    private int mPage = -1;
    private int mLimit = PAGE_SIZE;
        
    public GaodeSearchFactory(){
    }
    
    @Override
    public List<YelloPageItem> search(Solution sol, String searchInfo) {
        mSearchInfo = Config.mGson.fromJson(searchInfo, SearchInfo.class);
        return search(sol, searchInfo);
    }    

    @Override
    public List<YelloPageItem> search(Solution sol, SearchInfo searchInfo) {
        context = sol.getActivity();
        
        mSearchInfo = searchInfo;
        String keyword = mSearchInfo.getWords();
        String category = mSearchInfo.getCategory();
        if(mSearchInfo.getLimit() > 0)
        	mLimit = mSearchInfo.getLimit();
        
        if(TextUtils.isEmpty(keyword))
            keyword = sol.getInputKeyword();
        
        if(TextUtils.isEmpty(category))
            category = "";

        if(TextUtils.isEmpty(keyword) && TextUtils.isEmpty(category)) {
            mHasMore = false;
            return null;
        } else {
            return searchData(keyword, sol.getInputCity(), sol.getInputLongtitude(), sol.getInputLatitude(), 
                    category, mPage+1);
        }
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
        this.mPage = page;
        
        ArrayList<YelloPageItem> list = new ArrayList<YelloPageItem>();
        
        if(!TextUtils.isEmpty(category)){
            category = category.replaceAll(",","|");
        }
        
        PoiSearch.Query query = new PoiSearch.Query(keyword, category,city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(mLimit);// 设置每页最多返回多少条poiitem
        query.setPageNum(page);// 设置查第一页

        PoiSearch poiSearch = new PoiSearch(ContactsApp.getInstance(), query);
        
        //22.534763&longitude=113.943885
        if(latitude != 0.0 || longitude != 0.0){
            poiSearch.setBound(new SearchBound(new LatLonPoint(latitude, longitude), DISTACNE_LIMIT , true));
        }
        LogUtil.d(TAG, "searchData keyword="+keyword+" city="+city+" lon="+longitude+" lat="+latitude+" category="+category+" page="+page);
        int pageCount = 0;
        List<PoiItem> poiItems = null;
        try {
            PoiResult poiResult = poiSearch.searchPOI();
            pageCount = poiResult.getPageCount();
            poiItems = poiResult.getPois();
        } catch (AMapException e) {
            e.printStackTrace();
//            Toast.makeText(context, "搜索无结果", Toast.LENGTH_SHORT).show();
            // modify by putao_lhq 2014年10月10日 for UMeng start
            if (null != context) {
                MobclickAgentUtil
                        .onEvent(
                                context,
                                UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_GAODE_FAIL);
            }
            // modify by putao_lhq 2014年10月10日 for UMeng end
            
            // add by putao_lhq 2014年10月11日 for 接入商错误上报 start
            UMengEventIds.reportSearchError("gaode", e);
            // add by putao_lhq 2014年10月11日 for 接入商错误上报 end
        }
        if(page == 0){
            mAllDianPingItemList.clear();
        }
        if(poiItems.size()==mLimit && pageCount > (page+1)){
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
        
        // modify by putao_lhq 2014年10月10日 for UMeng start
        if (list.size() == 0) {
            if (null != context) {
                MobclickAgentUtil
                        .onEvent(
                                context,
                                UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_GAODE_NO_DATA);
            }
        } else {
            if (null != context) {
                MobclickAgentUtil
                        .onEvent(
                                context,
                                UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_GAODE_SUCCESS);
            }
        }
        // modify by putao_lhq 2014年10月10日 for UMeng end
        
        mAllDianPingItemList.addAll(list);
        return list;
    }
    
    public List<YelloPageItem> searchNumber(String number) {
        mHasMore = false;
        return null;
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
