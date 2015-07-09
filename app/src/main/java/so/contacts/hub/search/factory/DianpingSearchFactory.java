package so.contacts.hub.search.factory;

import so.contacts.hub.thirdparty.dianping.DianPingApiTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import so.putao.findplug.BusinessesResponse;
import so.putao.findplug.DianPingBusiness;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YellowPageItemDianping;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;

import so.contacts.hub.core.Config;
import so.contacts.hub.search.SearchUtils;
import so.contacts.hub.search.Searchable;
import so.contacts.hub.search.Solution;
import so.contacts.hub.search.bean.SearchInfo;

public class DianpingSearchFactory implements Searchable{
    private static final String TAG = "DianpingSearchFactory";
    
    private List<YelloPageItem> mAllDianPingItemList = new ArrayList<YelloPageItem>();
        
    private Context context = null;
    
    private SearchInfo mSearchInfo = null;
    
    private boolean mHasMore = true;
    
    private int mPage = 0;
    
    private int mLimit = 20;
    
    public DianpingSearchFactory(){
    }

    @Override
    public List<YelloPageItem> search(Solution sol, String searchInfo) {
        if(searchInfo != null) {
            mSearchInfo = Config.mGson.fromJson(searchInfo, SearchInfo.class);
        }
        return search(sol, searchInfo);
    }

    /*
     * input_keyword - 用户输入关键字
     * searchInfo - 本次搜索的服务配置信息，包含search_key和search_category
     */
    public List<YelloPageItem> search(Solution sol, SearchInfo searchInfo) {

        context = sol.getActivity();
        mSearchInfo = searchInfo;
        String keyword = mSearchInfo.getWords();
        String category = mSearchInfo.getCategory();
        
        if(TextUtils.isEmpty(keyword))
            keyword = sol.getInputKeyword();
        
        if(TextUtils.isEmpty(category))
            category = "";

        if(TextUtils.isEmpty(keyword) && TextUtils.isEmpty(category)) {
            mHasMore = false;
            return null;
        } else {
            // 点评不管是关键字还是类别都一起传
            return searchData(keyword, sol.getInputCity(), 
                    sol.getInputLongtitude(), sol.getInputLatitude(), 
                    category, 
                    mPage+1,
                    mSearchInfo.getLimit());
        }
    }
    
    private ArrayList<YelloPageItem> searchData(String keyword, String city, double longitude,double latitude,String category,int page, int limit){
        try {
            ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
            if(TextUtils.isEmpty(category) && TextUtils.isEmpty(keyword)){
                mHasMore = false;
                return resultList;
            }
            resultList = searchRaw(keyword, city, longitude,latitude,category,page,limit);
            return resultList;
        } catch (Exception e) {
            mHasMore = false;
            return new ArrayList<YelloPageItem>();  
        }
    }
    
    public ArrayList<YelloPageItem> searchRaw(String keyword, String city, double longitude,double latitude,String category,int page, int limit) {
        this.mPage = page;
        
        ArrayList<YelloPageItem> list = new ArrayList<YelloPageItem>();
        Map<String, String> paramMap = new HashMap<String, String>();
        if(latitude != 0.0 || longitude != 0.0){ //modify by ffh 修改切换城市后大众点评搜不出来的问题
            paramMap.put("latitude", ""+latitude);
            paramMap.put("longitude", ""+longitude);
            paramMap.put("offset_type", "1");//暂时是0//
            paramMap.put("sort", "7");
            paramMap.put("radius", "5000");
        }
        paramMap.put("city", city);
        if(category != null && !category.equals("")) {
            paramMap.put("category", category);
        }
        if(keyword != null && !keyword.equals("")) {
            paramMap.put("keyword", keyword);
        }
        paramMap.put("out_offset_type", "1");//默认是高德数据//
        paramMap.put("platform", "2");
        /*
         * 将 40 改为 20 
         * update by hyl 2014-8-18 start
         * old code : paramMap.put("limit", "40");
         */
        if(limit > 0)
        	paramMap.put("limit", String.valueOf(limit));
        else
        	paramMap.put("limit", String.valueOf(mLimit));
        //update by hyl 2014-8-18 end
        
        paramMap.put("page", String.valueOf(page));
        paramMap.put("format", "json");

        String requestResult = DianPingApiTool.requestApi(DianPingApiTool.URL_GET_FIND_BUSINESS_INFO, paramMap);
        Log.v(TAG, "requestResult == " + requestResult);
        BusinessesResponse businessesResponse = new Gson().fromJson(requestResult, BusinessesResponse.class);
        Log.v(TAG, "businessesResponse == " + businessesResponse);
        if(!businessesResponse.status.equals("OK")) {
            if (null != context) {
                MobclickAgentUtil.onEvent(context,
                        UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_DIANPING_FAIL);
            }
            mHasMore = false;
            return list;
        } else {
            if (null != context) {
                MobclickAgentUtil.onEvent(context,
                        UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_DIANPING_SUCCESS);
            }
        }
        if(page == 1){
            mAllDianPingItemList.clear();
        }
        if(businessesResponse.total_count > businessesResponse.businesses.size() + mAllDianPingItemList.size()){
            mHasMore = true;
        }else{
            mHasMore = false;
        }
        for(DianPingBusiness business:businessesResponse.businesses) {
            if(business.getName().contains("(")) {
                business.setName(business.getName().substring(0, business.getName().indexOf("(")));
            }
            YellowPageItemDianping yellowPageItemDianping = new YellowPageItemDianping(business);
            list.add(yellowPageItemDianping);
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
    
    @Override
    public boolean hasMore() {
        return mHasMore;
    }
    
    @Override
    public int getPage() {
        return mPage;
    }

}