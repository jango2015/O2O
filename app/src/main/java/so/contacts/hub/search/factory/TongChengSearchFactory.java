package so.contacts.hub.search.factory;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;

import so.contacts.hub.util.ContactsAppUtils;

import so.contacts.hub.city.CityListDB;

import so.putao.findplug.YellowPageTongChengItem;

import so.contacts.hub.thirdparty.tongcheng.bean.TongChengHotelItem;

import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelBean;

import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelList;

import so.contacts.hub.thirdparty.tongcheng.util.TongChengApiUtil;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.core.Config;
import so.contacts.hub.search.Searchable;
import so.contacts.hub.search.Solution;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.YelloPageItem;

public class TongChengSearchFactory implements Searchable {
    private static final String TAG = "TongChengSearchFactory";
    
    private SearchInfo mSearchInfo = null;
    
    private boolean mHasMore = true;
    
    private int mPage = 0;
    
    public TongChengSearchFactory () {
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
        return searchData(sol.getInputKeyword(), sol.getInputCity(), sol.getInputLatitude(), sol.getInputLongtitude(), 20*1000, 20, mPage+1);
    }

    private List<YelloPageItem> searchData(String word, String city, double latitude, double longitude, int radius, int pageSize, int page) {
        this.mPage = page;
        String cityId = ContactsAppUtils.getInstance().getDatabaseHelper().getCityListDB().getCityIdByName(city, CityListDB.CITY_SOURCE_TYPE_TONGCHENG);
        LogUtil.d(TAG, "searchData word="+word+" city="+city+" cityId="+cityId+" latitude="+latitude+" longitude="+longitude+" radius="+radius+" pageSize="+pageSize+" page="+page);
        TC_Response_HotelList responseItem = TongChengApiUtil.getHotelList(word, cityId, latitude, longitude, radius, pageSize, page);
        
        if(responseItem.getRspType().equals("0")){
            if(responseItem.getRspCode().equals("0000")){
                MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_TONGCHENG_SUCCESS);
            }else if(responseItem.getRspCode().equals("0001")){
                MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_TONGCHENG_NO_DATA);
            }
        }else{
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_TONGCHENG_FAIL);
        }
        
        List<TC_HotelBean> list = responseItem.getHotelList();

        ArrayList<YelloPageItem> results = new ArrayList<YelloPageItem>();
        if(list != null) {
            int count = list.size();
            for(int i=0; i<count; i++) {
                TC_HotelBean hotel = list.get(i);
                TongChengHotelItem detail = new TongChengHotelItem();
                detail.setHotelName(hotel.getHotelName());
                detail.setAddress(hotel.getAddress());
                detail.setPhotoUrl(hotel.getImg());
                detail.setHotelId(hotel.getHotelId());
                detail.setDistance(hotel.getDistance());
                detail.setThumbNailUrl(hotel.getImg());
                detail.setLatitude(Double.valueOf(hotel.getLatitude()));
                detail.setLongitude(Double.valueOf(hotel.getLongitude()));
                detail.setStarRate(hotel.getStarRatedId());
                detail.setStarRatedName(hotel.getStarRatedName());
                detail.setMarkNum(hotel.getMarkNum());
                YellowPageTongChengItem item = new YellowPageTongChengItem(detail);
                item.setHotelid(hotel.getHotelId());
                results.add(item);
            }
            
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
