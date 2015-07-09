package so.contacts.hub.search.factory;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.search.Searchable;
import so.contacts.hub.search.Solution;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.ui.yellowpage.bean.SougouHmtItem;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.TelAreaUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YelloPageItemNumber;
import so.putao.findplug.YelloPageItemSougou;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sogou.hmt.sdk.manager.HMTNumber;
import com.sogou.hmt.sdk.manager.HMTResultItem;
import com.sogou.hmt.sdk.manager.HmtSdkManager;

import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

@SuppressWarnings("rawtypes")
public class SougouSearchFactory implements Searchable {
    private static final String TAG = "SougouSearchFactory";
    private Context context;
    private volatile ArrayList<HMTResultItem> mSogouResultItemList;
    private volatile ArrayList<YelloPageItem> mYellPageItemList;
    private String mWords;
    private String mCity;
    private double mLongitude;
    private double mLatitude;
    private int mIstart = 1;
    private volatile boolean mHasMore;
    private boolean mHasSearch = false;
    
    private int size = -1;
    private int mLimit = 20;

    private SearchInfo mSearchInfo;
    
    public SougouSearchFactory(){
    }
    

    @Override
    public List<YelloPageItem> search(Solution sol, String searchInfo) {
        return null;
    }    

    public ArrayList<YelloPageItem> search(Solution sol, SearchInfo searchInfo) {
        context = sol.getActivity();
        mSearchInfo = searchInfo;
        
        if(mSearchInfo.getLimit() > 0)
        	mLimit = mSearchInfo.getLimit();
        
        if(!TextUtils.isEmpty(sol.getInputKeyword()) && YellowUtil.isNumeric(sol.getInputKeyword())){
            return searchNumber(sol.getInputKeyword());
        } else {
            String keyword = mSearchInfo.getWords();
            String category = mSearchInfo.getCategory();
            
            if(TextUtils.isEmpty(keyword))
                keyword = category;
            
            if(TextUtils.isEmpty(keyword))
                keyword = sol.getInputKeyword();
            
            if(mSogouResultItemList != null){
                size = mSogouResultItemList.size();
            }else{
                size = 0;
            }
            
            if(!TextUtils.isEmpty(keyword)) {
                return search(keyword, sol.getInputCity(), sol.getInputLongtitude(), sol.getInputLatitude(), mIstart+size);            

            } else {
                mHasMore = false;
                return null;
            }
        }
    }
    
	public ArrayList<YelloPageItem> search(String words, String city, double longitude,double latitude,int istart) {
        try{
            mHasSearch = true;
            this.mWords = words;
            this.mCity = city;
            this.mLongitude = longitude;
            this.mLatitude = latitude;
            this.mIstart = istart;
            while(!HmtSdkManager.getInstance().isInit()) {
                try {
                	HmtSdkManager.getInstance().init(ContactsApp.getContext());
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            Log.d(TAG, " words == " + words + " city == " + city + " longitude == "+ longitude + " latitude == "+ latitude + " istart == "+ istart);
            mSogouResultItemList = HmtSdkManager.getInstance().search(words, city, longitude, latitude, istart);
            mYellPageItemList = new ArrayList<YelloPageItem>();
            if(mSogouResultItemList.size() == 0){
                mHasMore = false;
            } else {
                mHasMore = mSogouResultItemList.get(mSogouResultItemList.size() - 1).hasMore;
                if(mHasMore) {
                    mSogouResultItemList.remove(mSogouResultItemList.size() - 1);
                }
            }
            for(HMTResultItem item:mSogouResultItemList) {
                SougouHmtItem hmtItem = new SougouHmtItem();
                
                String disString = item.getDianpingRate();
                double distance = item.getDistance();
                String[] hitWordArray = item.getHitWordArray();
                String logoUrl = item.getLogoUrl();
                String merchantDetailUrl = item.getMerchantDetailUrl();
                String merchantTag = item.getMerchantTag();
                String name = item.getName();
                int nextStart = item.getNextStart();
                String number = item.getNumber();
                int resultType = item.getResultType();
//              Bitmap itemlogo = item.LoadItemLogo();
                boolean isHasLogo = item.isHasLogo();
                boolean hasMore = item.hasMore;
                boolean behasMore = item.beHasMore();
                
                hmtItem.setDisString(disString);
                hmtItem.setDistance(distance);
                hmtItem.setHitWordArray(hitWordArray);
                hmtItem.setPhotoUrl(logoUrl);
                hmtItem.setMerchantDetailUrl(merchantDetailUrl);
                hmtItem.setMerchantTag(merchantTag);
                hmtItem.setName(name);
                hmtItem.setNextStart(nextStart);
                hmtItem.setNumber(number);
                hmtItem.setResultType(resultType);
//              hmtItem.setItemlogo(itemlogo);
                hmtItem.setHasLogo(isHasLogo);
                hmtItem.setHasMore(hasMore);
                
                mYellPageItemList.add(new YelloPageItemSougou(hmtItem));
            }
            if (null != context) {
//                com.putao.analytics.MobclickAgentUtil.onEvent(context,
//                        UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_SUCCESS);
//                MobclickAgentUtil.onEvent(context,
//                        UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_SUCCESS);
            }
            
            if (mSogouResultItemList.size() == 0) {
//                if (null != context) {
//                    com.putao.analytics.MobclickAgentUtil
//                    .onEvent(
//                            context,
//                            UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_NO_DATA);
//                    MobclickAgentUtil
//                            .onEvent(
//                                    context,
//                                    UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_NO_DATA);
//                }
            }
            
            
            return mYellPageItemList;
        }catch(Exception e) {
            mHasMore = false;
            if(null != context){
                MobclickAgentUtil.onEvent(context, UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_FAIL);
            }
            // add by putao_lhq 2014年10月11日 for 接入商错误上报 start
            UMengEventIds.reportSearchError("sougou", e);
            // add by putao_lhq 2014年10月11日 for 接入商错误上报 end
            return new ArrayList<YelloPageItem>();
        }

    }
    
    public ArrayList<YelloPageItem> searchNumber(String number){
    	mHasMore = false;
        number  = ContactsHubUtils.formatIPNumber(number, context);
        HMTNumber hmt = null;

        try{
            if(HmtSdkManager.getInstance().isInit()){
            	hmt = HmtSdkManager.getInstance().checkNumberFromLocal(number);
                if(hmt == null){
                    hmt = HmtSdkManager.getInstance().checkNumberFromNet(number);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        String phoneAddr = TelAreaUtil.getInstance().searchTel(number, context);
        String phoneOperator = TelAreaUtil.getInstance().getNetwork(number, context);
        ArrayList<YelloPageItem> list = new ArrayList<YelloPageItem>();
        String name = context.getResources().getString(R.string.putao_search_name_unknown);
        String address = context.getResources().getString(R.string.putao_search_address_unknown);
        String operator = context.getResources().getString(R.string.putao_search_operator_unknown);
        if(hmt != null && hmt.getMarkNumber() == 0){
            list.add(new YelloPageItemNumber(hmt.getMarkContent(), "putao_icon_no_status", hmt.getNumber()));
        }else{
            list.add(new YelloPageItemNumber(name, "putao_icon_no_status", null));
        }
        if(TextUtils.isEmpty(phoneAddr)){
            list.add(new YelloPageItemNumber(address, "putao_icon_no_add", null));
        }else{
            list.add(new YelloPageItemNumber(phoneAddr, "putao_icon_no_add", null));
        }
        if(TextUtils.isEmpty(phoneOperator)){
            list.add(new YelloPageItemNumber(operator, "putao_icon_no_carrieroperator", null));
        }else{
            operator = context.getResources().getString(R.string.putao_search_operator_full_name,phoneOperator);
            list.add(new YelloPageItemNumber(operator, "putao_icon_no_carrieroperator", null));
        }
        if(hmt == null && TextUtils.isEmpty(phoneAddr) && TextUtils.isEmpty(phoneOperator)){
            list.clear();
        }
        return list;
    }

    @Override
    public boolean hasMore() {
        return mHasMore;
    }
    
    @Override
    public int getPage() {
        return 1;
    }

}
