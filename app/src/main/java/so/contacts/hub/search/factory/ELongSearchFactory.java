package so.contacts.hub.search.factory;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.core.Config;
import so.contacts.hub.search.Searchable;
import so.contacts.hub.search.Solution;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.thirdparty.elong.bean.ELongHotelItem;
import so.contacts.hub.thirdparty.elong.bean.HotelList;
import so.contacts.hub.thirdparty.elong.bean.Hotels;
import so.contacts.hub.thirdparty.elong.tool.ELongApiUtil;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YellowPageELongItem;

public class ELongSearchFactory implements Searchable {
    private static final String TAG = "ELongSearchFactory";
    
    private SearchInfo mSearchInfo = null;
    
    private boolean mHasMore = true;
    
    private int mPage = 0;
    
    public ELongSearchFactory () {
    }
    
    @Override
    public List<YelloPageItem> search(Solution sol, String searchInfo) {
        if(searchInfo != null) {
            mSearchInfo = Config.mGson.fromJson(searchInfo, SearchInfo.class);
        }
        return search(sol, mSearchInfo);
    }

    @Override
    public List<YelloPageItem> search(Solution sol, SearchInfo searchInfo) {
        mSearchInfo = searchInfo;
        return searchData(sol.getInputCity(), sol.getInputLatitude(), sol.getInputLongtitude(), 20*1000, 20, mPage+1);
    }

    private List<YelloPageItem> searchData(String city, double latitude, double longitude, int radius, int pageSize, int page) {
        this.mPage = page;
        String cityId = ELongApiUtil.getCityCode(city);
//        String cityId = ELongApiUtil.getCityCodeFromDb(city);//modify by lisheng 2014-11-07 
        LogUtil.d(TAG, "searchData city="+city+" cityId="+cityId+" latitude="+latitude+" longitude="+longitude+" radius="+radius+" pageSize="+pageSize+" page="+page);
        String body = ELongApiUtil.getHotelList(cityId, latitude, longitude, radius, pageSize, page);
        
        LogUtil.d(TAG, "search result="+body);
        
        HotelList list = null;
        try{
            list = Config.mGson.fromJson(body, HotelList.class);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        ArrayList<YelloPageItem> results = new ArrayList<YelloPageItem>();
        if(list != null) {
            int count = list.Result.Hotels.size();
            for(int i=0; i<list.Result.Hotels.size(); i++) {
                Hotels hotel = list.Result.Hotels.get(i);
                ELongHotelItem detail = hotel.Detail;
                detail.setPhotoUrl(detail.getThumbNailUrl());
                detail.setHotelId(hotel.HotelId);
                detail.setDistance(hotel.Distance);
                YellowPageELongItem item = new YellowPageELongItem(detail);
                item.setHotelid(hotel.HotelId);
                results.add(item);
            }
            
//            if(count == 0 || count < pageSize || page >= 2) {
//                mHasMore = false;
//            } else {
//                mHasMore = true;
//            }
            
            mHasMore = false;

        } else {
            mHasMore = false;
        }
        return results;
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
