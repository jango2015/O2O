package so.putao.findplug;

import java.util.ArrayList;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.ui.yellowpage.bean.SougouHmtItem;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.TelAreaUtil;
import so.contacts.hub.util.UMengEventIds;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sogou.hmt.sdk.manager.HMTNumber;
import com.sogou.hmt.sdk.manager.HMTResultItem;
import com.sogou.hmt.sdk.manager.HmtSdkManager;
import so.contacts.hub.util.MobclickAgentUtil;
import com.yulong.android.contacts.discover.R;

public class YelloPageSougouFactory extends YelloPageFactory{
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
	private static boolean mHasInit;
	
	private static YelloPageSougouFactory mInstance;
	
	public static YelloPageSougouFactory getInstance(Context c) {
		if(mInstance == null){
			mInstance = new YelloPageSougouFactory(c);
		}
		return mInstance;
	}
	
	private YelloPageSougouFactory(Context c){
		this.context = c;
	}
	
	public static void init(Context context) {
		if(mHasInit) {
			return;
		}
        if(!HmtSdkManager.getInstance().isInit()) {
        	HmtSdkManager.getInstance().init(ContactsApp.getInstance().getApplicationContext());
        }
		mHasInit = true;
	}
	@Override
    public boolean hasMore() {
		return mHasMore;
	}
	
	public ArrayList<YelloPageItem> search(String words, String city, double longitude,double latitude) {
		return search(words, city, longitude, latitude,1);
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
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Log.e("error", " words == " + words + " city == " + city + " longitude == "+ longitude + " latitude == "+ latitude + " istart == "+ istart);
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
//			    Bitmap itemlogo = item.LoadItemLogo();
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
//			    hmtItem.setItemlogo(itemlogo);
			    hmtItem.setHasLogo(isHasLogo);
			    hmtItem.setHasMore(hasMore);
			    
			    mYellPageItemList.add(new YelloPageItemSougou(hmtItem));
			}
			if (null != context) {
				MobclickAgentUtil.onEvent(context,
						UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_SUCCESS);
			}
			
			if (mSogouResultItemList.size() == 0) {
				if (null != context) {
					MobclickAgentUtil
							.onEvent(
									context,
									UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_NO_DATA);
				}
			}
			
			
			return mYellPageItemList;
		}catch(Exception e) {
			mHasMore = false;
			if(null != context){
				MobclickAgentUtil.onEvent(context, UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_SOUGOU_FAIL);
			}
			return new ArrayList<YelloPageItem>();
		}

	}
	
	int size = -1;
	
	@Override
    public ArrayList<YelloPageItem> searchMore() {
		if((!mHasMore || !mHasSearch)) {
			return new ArrayList<YelloPageItem>();
		}
		if(mSogouResultItemList != null){
		    size = mSogouResultItemList.size();
		}else{
		    size = 0;
		}
		return search(mWords, mCity, mLongitude, mLatitude, mIstart + size);
	}
	
	public ArrayList<YelloPageItem> searchMore(SearchData searchData) {
	    size = 0;
        mIstart = 1;
        mWords = searchData.keyword;
        mCity = searchData.city;
        mLatitude = searchData.latitude;
        mLongitude = searchData.longitude;
        if(TextUtils.isEmpty(mWords)) {
            mWords = searchData.category;
        }
        
        return search(mWords, mCity, mLongitude, mLatitude, mIstart + size);
    }
	
	
	@Override
	public ArrayList<YelloPageItem> search(String keyword,
			String city, double longitude, double latitude, String category,int source) {
		if(keyword == null || keyword.equals("")) {
			keyword = category;
		}
		return this.search(keyword, city, longitude, latitude);
	}
	
	public ArrayList<YelloPageItem> searchNumber(String number){
	    number  = ContactsHubUtils.formatIPNumber(number, context);
	    HMTNumber hmt = null;
	    try{
	    	if(mHasInit){
//				hmt = HmtSdkManager.getInstance().checkNumberFromLocal(number);
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
        	list.add(new YelloPageItemNumber(hmt.getMarkContent(), "icon_no_status", hmt.getNumber()));
        }else{
        	list.add(new YelloPageItemNumber(name, "icon_no_status", null));
        }
        if(TextUtils.isEmpty(phoneAddr)){
        	list.add(new YelloPageItemNumber(address, "icon_no_add", null));
        }else{
        	list.add(new YelloPageItemNumber(phoneAddr, "icon_no_add", null));
        }
        if(TextUtils.isEmpty(phoneOperator)){
        	list.add(new YelloPageItemNumber(operator, "icon_no_carrieroperator", null));
        }else{
        	operator = context.getResources().getString(R.string.putao_search_operator_full_name,phoneOperator);
        	list.add(new YelloPageItemNumber(operator, "icon_no_carrieroperator", null));
        }
    	if(hmt == null && TextUtils.isEmpty(phoneAddr) && TextUtils.isEmpty(phoneOperator)){
    		list.clear();
    	}
    	return list;
	}

}
