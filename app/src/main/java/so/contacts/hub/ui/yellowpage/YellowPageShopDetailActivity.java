package so.contacts.hub.ui.yellowpage;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import so.contacts.hub.adapter.HotelRoomInfoAdapter;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.dianping.DianPingApiTool;
import so.contacts.hub.thirdparty.dianping.DianpingAsyncTask;
import so.contacts.hub.thirdparty.dianping.DianpingAsyncTask.IAsyncCallback;
import so.contacts.hub.thirdparty.elong.bean.ELongHotelItem;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelRoomBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelRoomsWithPolicy;
import so.contacts.hub.thirdparty.tongcheng.bean.TongChengHotelItem;
import so.contacts.hub.thirdparty.tongcheng.ui.YellowPageHotelDetailActivity;
import so.contacts.hub.thirdparty.tongcheng.ui.YellowPageHotelOrderActivity;
import so.contacts.hub.thirdparty.tongcheng.ui.YellowPageHotelDetailActivity.IQueryRoomPolicyDataCallback;
import so.contacts.hub.thirdparty.tongcheng.util.QueryRoomPolicyDataTask;
import so.contacts.hub.ui.yellowpage.bean.CalendarBean;
import so.contacts.hub.ui.yellowpage.bean.DianpingCustomsInfo;
import so.contacts.hub.ui.yellowpage.bean.DianpingReviewsInfo;
import so.contacts.hub.ui.yellowpage.bean.FastServiceItem;
import so.contacts.hub.ui.yellowpage.bean.GaoDePoiItem;
import so.contacts.hub.ui.yellowpage.bean.NumberItem;
import so.contacts.hub.ui.yellowpage.bean.PuTaoResultItem;
import so.contacts.hub.ui.yellowpage.bean.SerchItem;
import so.contacts.hub.ui.yellowpage.bean.SougouHmtItem;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.ModelFactory;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.Utils;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.putao.findplug.City58Item;
import so.putao.findplug.Deal;
import so.putao.findplug.DianPingBusiness;
import so.putao.findplug.DianpingCoupon;
import so.putao.findplug.DianpingDeal;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YelloPageItemSougou;
import so.putao.findplug.YellowPageCollectData;
import so.putao.findplug.YellowPageELongItem;
import so.putao.findplug.YellowPageItemCity58;
import so.putao.findplug.YellowPageItemDianping;
import so.putao.findplug.YellowPageItemDianpingCoupon;
import so.putao.findplug.YellowPageItemDianpingDeal;
import so.putao.findplug.YellowPageItemGaoDe;
import so.putao.findplug.YellowPageItemPutao;
import so.putao.findplug.YellowPageTongChengItem;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.google.gson.Gson;
import com.loader.DataLoader;
import com.loader.DataLoaderListener;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

/**
 * 商户详情界面
 * 
 * @author hyl 2014-7-14
 */
@SuppressWarnings("rawtypes")
public class YellowPageShopDetailActivity extends BaseRemindActivity implements DataLoaderListener,
        OnLongClickListener,OnItemClickListener{

    private ImageView backImg; // 返回

    private ImageView logoImg; // 店铺Logo

    private TextView shopNameTv; // 店铺名称

    private TextView sourceTv; // 数据来源

    private TextView avgPriceTv; // 人均消费

    private RatingBar ratingBar; // 用户评级

    private LinearLayout moreLayout;// 详情信息扩展布局

    /**
     * 团购信息
     */
    private ImageView mCustomsImgView = null;

    private TextView mCustomsTitleTView = null;

    private TextView mCustomsInfosTView = null;

    private TextView mCustomsPriceTView = null;

    private TextView mCustomsLastPriceTView = null;
    
    /**
     * 酒店房型信息
     */
    private LinearLayout mHotelRoomInfoLayout;
    private TextView mHotelComDateTView;
    private TextView mHotelLeaveDateTView;
    private ListView mHotelRoomInfoListView;

    /**
     * 评论信息
     */
    private TextView mReviewsTView = null;

    DataLoader mImageLoader;

    DataLoader mDealImageLoader;

    Bitmap circleBitamp;

    private long categoryId = -1;

    private long itemId = -1;
    
    private int remindCode = -1;

    private String itemName = "";

    private int itemSourceId = 0;

    private String itemContent = "";

    // is collected.
    private boolean mCollected = false;

    private int mDefaultCollectType = ConstantsParameter.YELLOWPAGE_DATA_TYPE_DEFAULT;

    private YellowPageDB mYellowPageDB = null;

    private YellowPageCollectData mCollectData = null;
    
    //酒店房型
    private QueryRoomPolicyDataTask mQueryDataTask = null;
    private QueryRoomPolicyDataTask mQueryRoomPolicyDataTask = null;
    private List<TC_HotelRoomBean> mHotelRoomList = new ArrayList<TC_HotelRoomBean>();
    private SharedPreferences mSharedPreferences;
    private HotelRoomInfoAdapter mAdapter = null;
    private DataLoader mDataLoader = null;
    private CalendarBean mInCalendarModel = null; // 入住日期
    private CalendarBean mOutCalendarModel = null; // 离店日期
    private String mComeDate = null;
    private String mLeaveDate = null;
    private String mHotelId;
    private String mCityName = null;
    private String mHotelName = null;
    private String mHotelAddress = null;
    private String[] mWeekTagList = null;// 星期中文列表： 周日，周一...
    private static final int REQUEST_CODE_DATE_IN = 101; // 入住时间选择 返回Code
    private static final int REQUEST_CODE_DATE_OUT = 102; // 离店时间选择 返回Code
    private static final int MSG_START_REFRESH_ROOMLIST_DATA_ACTION = 0x2000;
    private static final int MSG_UPDATE_ALL_ROOMLIST_DATA = 0x2002;
    private static final int MSG_UPDATE_DATE_IN_ACTION = 0x2003;
    private static final int MSG_REUPDATE_DATE_OUT_ACTION = 0x2004;
    private static final int MSG_UPDATE_DATE_OUT_ACTION = 0x2005;
    

    //add by lxh for Umeng 统计
    private long umengDetailId = -1;
    public static final  String EXTRA_UMENG_DETAIL_ID = "CategoryId";
    public static final  long DEFAULT_UMENG_DETAIL_ID = 2;//代表other
    public static final  long SEARCH_UMENG_DETAIL_ID = 0;//代表搜索进来的
    /*是否统计过启动 */
    private boolean mStartOnce=false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_yellow_page_shop_detail);
        int imageSize = getResources().getDimensionPixelSize(
                R.dimen.putao_yp_detail_item_image_size);
        mImageLoader = new ImageLoaderFactory(this).getYellowPageDealLoader(true, imageSize, 0);
        int dealImageSize = getResources().getDimensionPixelSize(
                R.dimen.putao_yp_detail_customs_img_width);
        mDealImageLoader = new ImageLoaderFactory(this).getYellowPageDetailLoader(true,
                dealImageSize);
        mYellowPageDB = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();

        initView(); // 初始化布局
        initData(); // 初始化数据
    }

    @Override
    protected void onResume() {
        MobclickAgentUtil.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgentUtil.onPause(this);
        super.onPause();
    }

    private void initView() {
        backImg = (ImageView)findViewById(R.id.back_img);
        logoImg = (ImageView)findViewById(R.id.logo_img);

        shopNameTv = (TextView)findViewById(R.id.name_text);
        sourceTv = (TextView)findViewById(R.id.source_text);
        avgPriceTv = (TextView)findViewById(R.id.average_price);
        ratingBar = (RatingBar)findViewById(R.id.rating_text);

        moreLayout = (LinearLayout)findViewById(R.id.more_layout);

        backImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

	private void initData() {
        if (getIntent() == null) {
            return;
        }

        Intent intent = getIntent();

        Object obj = intent.getExtras().getSerializable("YelloPageItem");
        itemId = intent.getLongExtra(ConstantsParameter.CATEGORY_ITEMID, -1);
        if (null == obj) {
            return;
        }
        YelloPageItem item = (YelloPageItem)obj;

        // 商户名称
        itemName = item.getName();
        shopNameTv.setText(itemName);
        shopNameTv.requestFocus();
        categoryId = intent.getLongExtra("CategoryId", -1);
        remindCode = intent.getIntExtra("RemindCode", -1);

        // add mask for coolpad
        Bitmap bitmapmask = BitmapFactory.decodeResource(getResources(), R.drawable.putao_mask);
        int source = 0;// 数据来源
        //putao_lhq add for share start
        StringBuilder shareContent = new StringBuilder(itemName);
        String str_Tel = getString(R.string.putao_text_shared_str_tel);
		String str_webUrl = getString(R.string.putao_text_shared_str_url);
		String str_Addr = getString(R.string.putao_text_shared_str_addr);
		//putao_lhq add for share end
		if (item instanceof YellowPageItemPutao) {// 葡萄
            itemSourceId = ConstantsParameter.YELLOWPAGE_SOURCETYPE_PUTAO;
            source = R.string.putao_yellow_page_source_pt;

            PuTaoResultItem putao = (PuTaoResultItem)item.getData();

            String photoName = putao.getPhotoUrl();
            int photoResouceId = getResources().getIdentifier(photoName, "drawable",
                    getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), photoResouceId);
            if (bitmap != null) {
                Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap, bitmapmask);
                logoImg.setImageBitmap(circlelogo);
            }

            // 电话
            List<NumberItem> numberItems = putao.getNumbers();
            if (numberItems != null) {
                addPhoneItem(numberItems);
                //putao_lhq add for share start
                if (numberItems.size() > 0) {
                	shareContent.append("\n");
                	shareContent.append(str_Tel);
                	shareContent.append(numberItems.get(0).getNumber());
                }
                //putao_lhq add for share end
            }

            // 快捷服务
            List<FastServiceItem> fastServices = putao.getFast_service();
            if (null != fastServices) {
                addFastServices(fastServices);
            }
            boolean showIcon = null == fastServices || fastServices.size() == 0 ? true : false;

            // 查看其他 分店
            List<SerchItem> searchItems = putao.getSearchInfo();
            if (searchItems != null) {
                addSerchOtherItem(searchItems, showIcon);
            }

            // 网址
            String website = putao.getWebsite();
            addWebSiteItem(website);
            //putao_lhq add for share start
            if (!TextUtils.isEmpty(website)) {
            	shareContent.append("\n");
            	shareContent.append(str_webUrl);
            	shareContent.append(website);
            }
            //putao_lhq add for share end
        } else {
			if (item instanceof YellowPageItemDianping) {// 大众点评
			    itemSourceId = ConstantsParameter.YELLOWPAGE_SOURCETYPE_DIANPING;
			    itemId = ((YellowPageItemDianping)item).getData().business_id;
			    source = R.string.putao_yellow_page_source_dp;

			    float avg_rating = item.getAvg_rating();
			    if (avg_rating > 0) {
			        ratingBar.setRating(avg_rating);
			        ratingBar.setVisibility(View.VISIBLE);
			    } else {
			        ratingBar.setVisibility(View.INVISIBLE);
			    }

                DianPingBusiness dianPingBusiness = (DianPingBusiness)item.getData();
                int avgPrice = dianPingBusiness.avg_price;
                if (avgPrice > 0) {
                    avgPriceTv.setText(String.format(
                            getResources().getString(R.string.putao_yellow_page_avr_price),
                            avgPrice));
                    avgPriceTv.setVisibility(View.VISIBLE);
                    findViewById(R.id.price_divider).setVisibility(View.VISIBLE);
                }

			    // 电话
			    String number = dianPingBusiness.telephone;
			    addPhoneItem(number, 0);
			    //putao_lhq add for share start
			    if (!TextUtils.isEmpty(number)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_Tel);
			    	shareContent.append(number);
			    }
			    //putao_lhq add for share end
			    // 头像
			    String photoUrl = dianPingBusiness.s_photo_url;
			    showHeadImg(item, bitmapmask, photoUrl);//modify by putao_lhq
			    // 地址
			    String address = dianPingBusiness.address;
			    float latitude = dianPingBusiness.latitude;
			    float longitude = dianPingBusiness.longitude;
			    addAddressItem(address, latitude, longitude, "");
			    //putao_lhq add for share start
			    if (!TextUtils.isEmpty(address)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_Addr);
			    	shareContent.append(address);
			    }
			    //putao_lhq add for share end
			    // 查看其它分店

			    // 团购信息
			    addDealItem(dianPingBusiness);

			    // 评论
			    addReviewsItem(dianPingBusiness);

			    // // 优惠券
			    // String couponDescription = dianPingBusiness.coupon_description;
			    // String couponUrl = dianPingBusiness.coupon_url;
			    // addCouponItem(couponDescription, couponUrl, name);

			    // 查看他的主页
			    String businessUrl = item.getBusinessUrl();
			    //putao_lhq add for share start
			    if (!TextUtils.isEmpty(businessUrl)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_webUrl);
			    	shareContent.append(businessUrl);
			    }
			    //putao_lhq add for share end
			    /*
			     * 去掉title栏 add by hyl 2014-8-11 start
			     */
			    if (!businessUrl.contains("hasheader")) {
			        businessUrl = businessUrl + "?hasheader=0";// 隐藏顶部栏 0-隐藏 1-打开
			    }
			    // update by hyl 2014-8-11 end

			    addHomePageItem(businessUrl, itemName, R.drawable.putao_icon_logo_dazong);

			} else if (item instanceof YellowPageItemDianpingDeal) {// sml 团购
			    source = R.string.putao_yellow_page_source_dp;
			    itemSourceId = ConstantsParameter.YELLOWPAGE_SOURCETYPE_DIANPING;
			    DianpingDeal dianPingDeal = (DianpingDeal)item.getData();

			    // 头像
			    String photoUrl = dianPingDeal.getPhotoUrl();
			    showHeadImg(item, bitmapmask, photoUrl);//putao_lhq modify for BUG #1534

			    // 查看他的主页
			    String businessUrl = item.getBusinessUrl();
			    //putao_lhq add for share start
			    if (!TextUtils.isEmpty(businessUrl)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_webUrl);
			    	shareContent.append(businessUrl);
			    }
			    //putao_lhq add for share end
			    addHomePageItem(businessUrl, itemName, R.drawable.putao_icon_logo_dazong);

			} else if (item instanceof YellowPageItemDianpingCoupon) {// sml 优惠
			    source = R.string.putao_yellow_page_source_dp;
			    itemSourceId = ConstantsParameter.YELLOWPAGE_SOURCETYPE_DIANPING;
			    DianpingCoupon dianPingCoupon = (DianpingCoupon)item.getData();

			    // 头像
			    String photoUrl = dianPingCoupon.getPhotoUrl();
			    showHeadImg(item, bitmapmask, photoUrl);//putao_lhq modify for BUG #1534
			    // look up its homepage
			    String businessUrl = item.getBusinessUrl();
			    //putao_lhq add for share start
			    if (!TextUtils.isEmpty(businessUrl)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_webUrl);
			    	shareContent.append(businessUrl);
			    }
			    //putao_lhq add for share end
			    addHomePageItem(businessUrl, itemName, R.drawable.putao_icon_logo_dazong);

			} else if (item instanceof YelloPageItemSougou) {// 搜狗号码通
			    itemSourceId = ConstantsParameter.YELLOWPAGE_SOURCETYPE_SOUGOU;
			    source = R.string.putao_yellow_page_source_sg;
			    SougouHmtItem hmtResultItem = (SougouHmtItem)item.getData();

			    // 头像
			    // modify by putao_lhq 2014年10月11日 for BUG #1534 start
			    //String localPhotoUrl = hmtResultItem.getLocalLogoUrl();
			    String localPhotoUrl = hmtResultItem.getLocalPhotoUrl();
			    // modify by putao_lhq 2014年10月11日 for BUG #1534 end
			    String defaultPhotoUrl = hmtResultItem.getDefaultPhotoUrl();
			    if (!TextUtils.isEmpty(localPhotoUrl)) {
			        int photoResouceId = getResources().getIdentifier(localPhotoUrl, "drawable",
			                getPackageName());
			        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), photoResouceId);
			        if (bitmap != null) {
			            Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap,
			                    bitmapmask);
			            logoImg.setImageBitmap(circlelogo);
			        }
			    } else {
			        String photoUrl = hmtResultItem.getPhotoUrl();
			        if (!TextUtils.isEmpty(photoUrl)) {
			            if (photoUrl.endsWith("no_photo_278.png")) {
			                if (TextUtils.isEmpty(defaultPhotoUrl)) {
			                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
			                            R.drawable.putao_icon_logo_placeholder);
			                    logoImg.setImageBitmap(ContactsHubUtils.makeRoundCornerforCoolPad(
			                            bitmap, bitmapmask));
			                } else {
			                    int photoResouceId = getResources().getIdentifier(defaultPhotoUrl,
			                            "drawable", getPackageName());
			                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
			                            photoResouceId);
			                    if (bitmap != null) {
			                        Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(
			                                bitmap, bitmapmask);
			                        logoImg.setImageBitmap(circlelogo);
			                    }
			                }
			            } else {
			                mImageLoader.loadData(photoUrl, logoImg, this);
			            }

			        } else {
			            if (TextUtils.isEmpty(defaultPhotoUrl)) {
			                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
			                        R.drawable.putao_icon_logo_placeholder);
			                logoImg.setImageBitmap(ContactsHubUtils.makeRoundCornerforCoolPad(bitmap,
			                        bitmapmask));
			            } else {
			                int photoResouceId = getResources().getIdentifier(defaultPhotoUrl,
			                        "drawable", getPackageName());
			                Bitmap bitmap = BitmapFactory
			                        .decodeResource(getResources(), photoResouceId);
			                if (bitmap != null) {
			                    Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap,
			                            bitmapmask);
			                    logoImg.setImageBitmap(circlelogo);
			                }
			            }
			        }
			    }

			    // 电话
			    String number = hmtResultItem.getNumber();
			    addPhoneItem(number, 0);
			    //putao_lhq add for share start
			    if (!TextUtils.isEmpty(number)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_Tel);
			    	shareContent.append(number);
			    }
			    //putao_lhq add for share end
			    // 查看他的主页
			    String businessUrl = item.getBusinessUrl();
			    //putao_lhq add for share start
			    if (!TextUtils.isEmpty(businessUrl)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_webUrl);
			    	shareContent.append(businessUrl);
			    }
			    //putao_lhq add for share end
			    addHomePageItem(businessUrl, itemName, R.drawable.putao_icon_logo_sougou);

			} else if (item instanceof YellowPageItemGaoDe) {// 高德的数据源
			    itemSourceId = ConstantsParameter.YELLOWPAGE_SOURCETYPE_GAODE;
			    source = R.string.putao_yellow_page_source_gdmap;
			    GaoDePoiItem poiItem = (GaoDePoiItem)item.getData();
			    // 电话
			    String tel = poiItem.getTelephone();
			    if (!TextUtils.isEmpty(tel)) {
			    	//putao_lhq add for share start
			    	shareContent.append("\n");
			    	shareContent.append(str_Tel);
			    	shareContent.append(tel);
			    	//putao_lhq add for share end
			        String tels[] = tel.split(";");
			        int index = 0;
			        for (String number : tels) {
			            addPhoneItem(number, index);
			            index++;
			        }
			    }
			    // 地址
			    String address = poiItem.getAddress();
			    float latitude = (float)poiItem.getLatitude();
			    float longitude = (float)poiItem.getLongitude();
			    addAddressItem(address, latitude, longitude, poiItem.getPoiId());
			    if (!TextUtils.isEmpty(address)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_Addr);
			    	shareContent.append(address);//putao_lhq add for share
			    }
			    // 头像
			    String photoUrl = poiItem.getPhotoUrl();
			    showHeadImg(item, bitmapmask, photoUrl);//putao_lhq modify for BUG #1534
			    // 网址
			    String website = poiItem.getWebsite();
			    //putao_lhq modify for BUG #1454 start
			    if (!TextUtils.isEmpty(website)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_webUrl);
			    	shareContent.append(website);
			    }
			    //putao_lhq modify for BUG #1454 end
			    addHomePageItem(website, itemName, R.drawable.putao_icon_logo_gaode);
			    //putao_lhq modify for 58 search start
			} else if (item instanceof YellowPageItemCity58) { // 58同城
				itemSourceId = ConstantsParameter.YELLOWPAGE_SOURCETYPE_WUBA;
				source = R.string.putao_text_from_58;
				// modify by putao_lhq 2014年10月11日 for BUG #1534 start
				/*
		    	Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_logo_placeholder);
		    	if (bitmap != null) {
		    		Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap, bitmapmask);
		    		logoImg.setImageBitmap(circlelogo);
		    	}
		    	*/
			    String localPhotoUrl = item.getData().getLocalPhotoUrl();
			    if (!TextUtils.isEmpty(localPhotoUrl)) {
			        int photoResouceId = getResources().getIdentifier(localPhotoUrl, "drawable",
			                getPackageName());
			        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), photoResouceId);
			        if (bitmap != null) {
			            Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap,
			                    bitmapmask);
			            logoImg.setImageBitmap(circlelogo);
			        }
			    } else {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                            R.drawable.putao_icon_logo_placeholder);
                    if (bitmap != null) {
                        Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap,
                                bitmapmask);
                        logoImg.setImageBitmap(circlelogo);
                    }
                }
			    // modify by putao_lhq 2014年10月11日 for BUG #1534 end
				City58Item city58item = (City58Item) item.getData();
				String website = city58item.targeturl;
				//putao_lhq add for share start
			    if (!TextUtils.isEmpty(website)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_webUrl);
			    	shareContent.append(website);
			    }
			    //putao_lhq add for share end
				addHomePageItem(website, itemName, R.drawable.putao_icon_58);
				//putao_lhq modify for 58 search end
			} else if (item instanceof YellowPageELongItem) { // 艺龙
				itemSourceId = ConstantsParameter.YELLOWPAGE_SOURCETYPE_ELONG;
				source = R.string.putao_text_from_elong;
				ELongHotelItem detail = (ELongHotelItem)item.getData();
				try{
			    	itemId = Integer.valueOf(detail.getHotelId());
			    }catch(Exception e){
			    	itemId = -1;
			    }
		    	Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.putao_icon_logo_placeholder);
		    	if (bitmap != null) {
		    		Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap, bitmapmask);
		    		logoImg.setImageBitmap(circlelogo);
		    	}
		    	String photoUrl = detail.getThumbNailUrl();
                showHeadImg(item, bitmapmask, photoUrl);//putao_lhq modify for BUG #1534
                // 电话
                String number = detail.getPhone();
                //putao_lhq add for share start
			    if (!TextUtils.isEmpty(number)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_Tel);
			    	shareContent.append(number);
			    }
			    //putao_lhq add for share end
                if( !TextUtils.isEmpty(number) ){
                	if( number.contains("、") ){
                		List<NumberItem> numberList = new ArrayList<NumberItem>();
                		String[] numbers = number.split("、");
                		if( numbers != null ){
                			for(int i = 0; i < numbers.length; i++){
                				String phone = numbers[i];
                				if( phone.length() < 7 ){
                					break;
                				}
                				NumberItem numberItem = new NumberItem();
                				numberItem.setNumber(phone);
                				numberList.add(numberItem);
                			}
                		}
                		if( numberList.size() > 0 ){
                			addPhoneItem(numberList);
                		}else{
                			addPhoneItem(number, 0);
                		}
                	}else{
                		addPhoneItem(number, 0);          
                	}
                }
                
			    final String address = detail.getAddress();
			    //putao_lhq add for share start
			    if (!TextUtils.isEmpty(address)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_Addr);
			    	shareContent.append(address);
			    }
			    //putao_lhq add for share end
				addAddressItem(address, detail.getLatitude(), detail.getLongitude(), "");
                
                String website = item.getBusinessUrl();
                //putao_lhq add for share start
			    if (!TextUtils.isEmpty(website)) {
			    	shareContent.append("\n");
			    	shareContent.append(str_webUrl);
			    	shareContent.append(website);
			    }
			    //putao_lhq add for share end
                addHomePageItem(website, itemName, R.drawable.putao_icon_elong);
            }else if (item instanceof YellowPageTongChengItem) { // 同程  
                // add xcx 2014_12_25 start 新增同程搜索
                itemSourceId = ConstantsParameter.YELLOWPAGE_SOURCETYPE_TONGCHENG;
                source = R.string.putao_text_from_tongcheng;
                TongChengHotelItem detail = (TongChengHotelItem)item.getData();
                try{
                    itemId = Integer.valueOf(detail.getHotelId());
                }catch(Exception e){
                    itemId = -1;
                }
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.putao_icon_logo_placeholder);
                if (bitmap != null) {
                    Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap,
                            bitmapmask);
                    logoImg.setImageBitmap(circlelogo);
                }
                String photoUrl = detail.getThumbNailUrl();
                showHeadImg(item, bitmapmask, photoUrl);
                // 电话
                String number = detail.getPhone();
                if (!TextUtils.isEmpty(number)) {
                    shareContent.append("\n");
                    shareContent.append(str_Tel);
                    shareContent.append(number);
                }
                if( !TextUtils.isEmpty(number) ){
                    if( number.contains("、") ){
                        List<NumberItem> numberList = new ArrayList<NumberItem>();
                        String[] numbers = number.split("、");
                        if( numbers != null ){
                            for(int i = 0; i < numbers.length; i++){
                                String phone = numbers[i];
                                if( phone.length() < 7 ){
                                    break;
                                }
                                NumberItem numberItem = new NumberItem();
                                numberItem.setNumber(phone);
                                numberList.add(numberItem);
                            }
                        }
                        if( numberList.size() > 0 ){
                            addPhoneItem(numberList);
                        }else{
                            addPhoneItem(number, 0);
                        }
                    }else{
                        addPhoneItem(number, 0);          
                    }
                }
                
                final String address = detail.getAddress();
                if (!TextUtils.isEmpty(address)) {
                    shareContent.append("\n");
                    shareContent.append(str_Addr);
                    shareContent.append(address);
                }
                addAddressItem(address, detail.getLatitude(), detail.getLongitude(), "");
                addHotelRoomInfo(detail);
                YellowPageTongChengItem tongChengItem = (YellowPageTongChengItem)item;
                addTongChengHotelHomePageItem(tongChengItem);
                float avg_rating = item.getAvg_rating();
                if (avg_rating > 0) {
                    ratingBar.setRating(avg_rating);
                    ratingBar.setVisibility(View.VISIBLE);
                } else {
                    ratingBar.setVisibility(View.INVISIBLE);
                }
            }
			// add xcx 2014_12_25 end 新增同程搜索
			
		}
        sourceTv.setText(source);

        // 空行
        addSpaceItem();

        // 加入收藏
        try {
            itemContent = new Gson().toJson(item.getData());
        } catch (Exception e) {
            itemContent = "";
        }
        addCollectItem(itemName, itemSourceId, itemId, itemContent);
        //putao_lhq add for 分享 start
        // 分享
        // modify by putao_lhq 2014年10月11日 for 去掉58 分享 
        if (!(item instanceof YellowPageItemCity58)) {
        	addShareItem(item.getName(), shareContent.toString());
        }
        //putao_lhq add for 分享  end
        // 内容纠错
        addErrorCollectItem(itemName, itemSourceId, itemId);

        // 关于
        addAboutItem();
    }
    
    /**
     * 显示顶部大图
     * @param photoUrl
     * modify by putao_lhq for BUG #1534
     */
    private void showHeadImg(YelloPageItem item, Bitmap bitmapmask, String photoUrl){
	    String defaultPhotoUrl = item.getData().getDefaultPhotoUrl();
	    String localPhotoUrl = item.getData().getLocalPhotoUrl();
	    if (!TextUtils.isEmpty(localPhotoUrl)) {
	        int photoResouceId = getResources().getIdentifier(localPhotoUrl, "drawable",
	                getPackageName());
	        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), photoResouceId);
	        if (bitmap != null) {
	            Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap,
	                    bitmapmask);
	            logoImg.setImageBitmap(circlelogo);
	        }
	    } else {
	    	if (TextUtils.isEmpty(photoUrl)) {
	    		if (TextUtils.isEmpty(defaultPhotoUrl)) {
	    			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
	                        R.drawable.putao_icon_logo_placeholder);
	                logoImg.setImageBitmap(ContactsHubUtils.makeRoundCornerforCoolPad(bitmap,
	                        bitmapmask));
	    		} else {
	                int photoResouceId = getResources().getIdentifier(defaultPhotoUrl,
	                        "drawable", getPackageName());
	                Bitmap bitmap = BitmapFactory
	                        .decodeResource(getResources(), photoResouceId);
	                if (bitmap != null) {
	                    Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(bitmap,
	                            bitmapmask);
	                    logoImg.setImageBitmap(circlelogo);
	                }
	            }
	    	} else {
	    		if (photoUrl.endsWith("no_photo_278.png")) {
	                if (TextUtils.isEmpty(defaultPhotoUrl)) {
	                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
	                            R.drawable.putao_icon_logo_placeholder);
	                    logoImg.setImageBitmap(ContactsHubUtils.makeRoundCornerforCoolPad(
	                            bitmap, bitmapmask));
	                } else {
	                    int photoResouceId = getResources().getIdentifier(defaultPhotoUrl,
	                            "drawable", getPackageName());
	                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
	                            photoResouceId);
	                    if (bitmap != null) {
	                        Bitmap circlelogo = ContactsHubUtils.makeRoundCornerforCoolPad(
	                                bitmap, bitmapmask);
	                        logoImg.setImageBitmap(circlelogo);
	                    }
	                }
	            } else {
	                mImageLoader.loadData(photoUrl, logoImg, this);
	            }
	    	}
	    }
    }

    /** 加入收藏 */
    private void addCollectItem(final String name, final int sourceType, final long itemId,
            final String content) {
        View itemLayout = LayoutInflater.from(this).inflate(R.layout.putao_detail_other_item, null);
        // delete by putao_lhq 2014年11月26日 for coolui6.0 start
        //ImageView headImg = (ImageView)itemLayout.findViewById(R.id.head_img);
        //headImg.setImageResource(R.drawable.putao_icon_data_collect);
        // delete by putao_lhq 2014年11月26日 for coolui6.0 end
        final TextView infoTView = (TextView)itemLayout.findViewById(R.id.info_text);

        mCollectData = mYellowPageDB.queryCollectedData(itemId, sourceType, name);
        if (mCollectData == null) {
            mCollected = false;
        } else {
            if (mCollectData.getDataType() != ConstantsParameter.YELLOWPAGE_DATA_TYPE_FAVORITE) {
                mCollected = false;
            } else {
                mCollected = true;
            }
            mDefaultCollectType = mCollectData.getDataType();
        }
        if (mCollected) {
            infoTView.setText(getResources().getString(R.string.putao_yellow_page_collectable));
        } else {
            infoTView.setText(getResources().getString(R.string.putao_yellow_page_collect));
        }

        itemLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCollected) {
                    // 已经收藏， 则取消收藏
                    mCollectData = null;
                    mCollected = false;
                    infoTView.setText(getResources().getString(R.string.putao_yellow_page_collect));
                    showToast(R.string.putao_yellow_page_collect_cancel);
                    mYellowPageDB.delCollectData(itemId, sourceType, name);
                    MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                            UMengEventIds.DISCOVER_YELLOWPAGE_AGGREGATED_FAVORITE_CANCEL);
                } else {
                    if (mCollectData == null) {
                        initCollectData(ConstantsParameter.YELLOWPAGE_DATA_TYPE_DEFAULT);
                    }
                    // 未收藏
                    mCollected = true;
                    if (mCollectData.getDataType() == ConstantsParameter.YELLOWPAGE_DATA_TYPE_HISTORY) {
                        // 历史，则更新数据
                        mCollectData.setDataType(ConstantsParameter.YELLOWPAGE_DATA_TYPE_FAVORITE);
                        infoTView.setText(getResources().getString(
                                R.string.putao_yellow_page_collectable));
                        showToast(R.string.putao_yellow_page_collect_ok);
                        mYellowPageDB.updateYellowPageCollerViewTime(itemId,
                                mCollectData.getDataType(), itemSourceId, name,
                                System.currentTimeMillis());
                    } else {
                        // 加入收藏
                        infoTView.setText(getResources().getString(
                                R.string.putao_yellow_page_collectable));
                        showToast(R.string.putao_yellow_page_collect_ok);
                        mCollectData.setTime(System.currentTimeMillis());
                        mCollectData.setDataType(ConstantsParameter.YELLOWPAGE_DATA_TYPE_FAVORITE);
                        mYellowPageDB.inseartCollectData(mCollectData);
                    }
                    MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                            UMengEventIds.DISCOVER_YELLOWPAGE_AGGREGATED_FAVORITE_SUCCESS);
                }
            }
        });
        moreLayout.addView(itemLayout, getLayoutParams(true));
    }

    private void showToast(int resId) {
        mHandler.removeMessages(MSG_SHOW_COLLECT_TAOST_ACTION);
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SHOW_COLLECT_TAOST_ACTION;
        msg.obj = resId;
        mHandler.sendMessageDelayed(msg, 300);
    }

    private static final int MSG_SHOW_COLLECT_TAOST_ACTION = 0x2001;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                    case MSG_SHOW_COLLECT_TAOST_ACTION:
                        mHandler.removeMessages(MSG_SHOW_COLLECT_TAOST_ACTION);
                        int resId = (Integer)msg.obj;
                        Utils.showToast(YellowPageShopDetailActivity.this, resId, false);
                        break;
                    case MSG_UPDATE_ALL_ROOMLIST_DATA:
                        if( mHotelRoomList != null){
                            mAdapter.setData(mHotelRoomList);
                        }
                        break;
                    case MSG_UPDATE_DATE_IN_ACTION:
                        // 入住日期
                        CalendarBean inModel = (CalendarBean) msg.obj;
                        if( inModel == null ){
                            return;
                        }
                        mInCalendarModel = inModel;
                        String comeDate = mInCalendarModel.getFormatStr();
                        if( !mComeDate.equals(comeDate) ){
                            // 日期更改，则需要重新刷新酒店房型列表
                            mComeDate = comeDate;
                            showHotelDate(true);
                            mHandler.sendEmptyMessage(MSG_START_REFRESH_ROOMLIST_DATA_ACTION);
                        }
                        break;
                    case MSG_REUPDATE_DATE_OUT_ACTION:
                        // 清除了之前的离店日期，根据入住日期生成明天的日期
                        mOutCalendarModel = null;
                        CalendarBean reOutModel = (CalendarBean) msg.obj;
                        if( reOutModel == null ){
                            return;
                        }
                        mInCalendarModel = reOutModel;
                        String reComeDate = mInCalendarModel.getFormatStr();
                        if( !mComeDate.equals(reComeDate) ){
                            mComeDate = reComeDate;
                            showHotelDate(true);
                        }
                        String tomorrowDate = CalendarUtil.getAppointTomorrowDate(reOutModel.getYear(), reOutModel.getMonth(), reOutModel.getDay());
                        mOutCalendarModel = getCalendarData(tomorrowDate, CalendarUtil.getWeekNumByDate(tomorrowDate));
                        mLeaveDate = mOutCalendarModel.getFormatStr();
                        showHotelDate(false);
                        mHandler.sendEmptyMessage(MSG_START_REFRESH_ROOMLIST_DATA_ACTION);
                        break;
                    case MSG_UPDATE_DATE_OUT_ACTION:
                        // 离店日期
                        CalendarBean outModel = (CalendarBean) msg.obj;
                        if( outModel == null ){
                            return;
                        }
                        mOutCalendarModel = outModel;
                        String leaveDate = mOutCalendarModel.getFormatStr();
                        if( !mLeaveDate.equals(leaveDate) ){
                            // 日期更改，则需要重新刷新酒店房型列表
                            mLeaveDate = leaveDate;
                            showHotelDate(false);
                            mHandler.sendEmptyMessage(MSG_START_REFRESH_ROOMLIST_DATA_ACTION);
                        }
                        break;
                    case MSG_START_REFRESH_ROOMLIST_DATA_ACTION:
                        // 刷新房型列表信息
                        if(NetUtil.isNetworkAvailable(YellowPageShopDetailActivity.this) && !TextUtils.isEmpty(mHotelId)){
                            if ( (mQueryDataTask != null && mQueryDataTask.getStatus() != AsyncTask.Status.RUNNING)
                                    || mQueryDataTask == null ) {
                                if(mHotelRoomList != null){
                                    mHotelRoomList.clear();
                                }
                                // add by XCX 2015-3-4 start 
                                if(mAdapter!=null){
                                    mAdapter.notifyDataSetChanged();
                                }
                                // add by XCX 2015-3-4 end 
                                mQueryDataTask = new QueryRoomPolicyDataTask(mHotelId, mComeDate, mLeaveDate,
                                        new IQueryRoomPolicyDataCallback() {

                                            @Override
                                            public void onPreExecute() {
                                            }

                                            @Override
                                            public void onPostExecute(TC_Response_HotelRoomsWithPolicy result) {
                                                if( result == null ){
                                                    return;
                                                }
                                                // modify by XCX 2015-3-4 start 

                                                List<TC_HotelRoomBean> roomList=result.getHotelroomlist();
                                                mHotelRoomList.clear();
                                                if( roomList == null || roomList.size() == 0){
                                                    Utils.showToast(YellowPageShopDetailActivity.this, R.string.putao_no_net, false);
                                                }else{
                                                    mHotelRoomList.addAll(roomList);
                                                }
                                                mHandler.sendEmptyMessage(MSG_UPDATE_ALL_ROOMLIST_DATA);
                                               // modify by XCX 2015-3-4 end
                                            }
                                        });
                                mQueryDataTask.execute();// 开始刷新数据
                            }
                        }else{
                            Utils.showToast(YellowPageShopDetailActivity.this, R.string.putao_no_net, false);
                        }
                        break;
                default:
                    break;
            }
        };
    };
    
    /**
     * 去掉年份，将"2014-12-06"转化为"12-06"
     */
    private void showHotelDate(boolean isComeIn){
        if( isComeIn ){
            if( !TextUtils.isEmpty(mComeDate) ){
                mSharedPreferences.edit().putString(ConstantsParameter.PREFS_KEY_COM_DATE, mComeDate).commit();
                mHotelComDateTView.setText(getString(R.string.putao_hotel_in_date, mComeDate.substring(5)));
            }
        }else{
            if( !TextUtils.isEmpty(mLeaveDate) ){
                mSharedPreferences.edit().putString(ConstantsParameter.PREFS_KEY_LEAVE_DATE, mLeaveDate).commit();
                mHotelLeaveDateTView.setText(getString(R.string.putao_hotel_out_date, mLeaveDate.substring(5)));
            }
        }
    }

    // 内容纠错
    private void addErrorCollectItem(final String name, final int sourceType, final long itemId) {
        View itemLayout = LayoutInflater.from(this).inflate(R.layout.putao_detail_other_item, null);
        // delete by putao_lhq 2014年11月26日 for coolui6.0 start
        /*ImageView headImg = (ImageView)itemLayout.findViewById(R.id.head_img);
        headImg.setImageResource(R.drawable.putao_icon_data_amend);*/
        // delete by putao_lhq 2014年11月26日 for coolui6.0 END
        TextView infoTv = (TextView)itemLayout.findViewById(R.id.info_text);
        infoTv.setText(getResources().getString(R.string.putao_yellow_page_error_collect));
        itemLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YellowPageShopDetailActivity.this,
                        YellowPageErrorCollectActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("source_id", sourceType);
                intent.putExtra("item_id", itemId);
                startActivity(intent);
                MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                        UMengEventIds.DISCOVER_YELLOWPAGE_AGGREGATED_ERROR_COLLECT);
            }
        });
        moreLayout.addView(itemLayout, getLayoutParams(true));
    }

    //putao_lhq add by for 分享 start
	private void addShareItem(String subject, String content) {
        View itemLayout = LayoutInflater.from(this).inflate(R.layout.putao_detail_other_item, null);
        // delete by putao_lhq 2014年11月26日 for coolui6.0 start
        //ImageView headImg = (ImageView)itemLayout.findViewById(R.id.head_img);
        //headImg.setImageResource(R.drawable.putao_icon_data_fenxiang);
        // delete by putao_lhq 2014年11月26日 for coolui6.0 end
        TextView infoTv = (TextView)itemLayout.findViewById(R.id.info_text);
        infoTv.setText(getResources().getString(R.string.putao_yellow_page_share));
        //final String shareSubject = subject;//delete for BUG #1544 by putao_lhq
        final String shareContent = content;
        itemLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	Intent share = new Intent(android.content.Intent.ACTION_SEND); 
            	share.setType("text/plain"); 
            	//share.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject); //delete for BUG #1544 by putao_lhq
            	//share.putExtra(android.content.Intent.EXTRA_TITLE, shareSubject); //delete for BUG #1544 by putao_lhq
                share.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
                String title = YellowPageShopDetailActivity.this.getResources().getString(
                        R.string.putao_text_shared_str_share_to);
                startActivity(Intent.createChooser(share, title));
            }
        });
        
        moreLayout.addView(itemLayout, getLayoutParams(true));
    }
    //putao_lhq add by for 分享 end
    // 关于
    private void addAboutItem() {
        View itemLayout = LayoutInflater.from(this).inflate(R.layout.putao_detail_other_item, null);
        // delete by putao_lhq 2014年11月26日 for coolui6.0 start
        //ImageView headImg = (ImageView)itemLayout.findViewById(R.id.head_img);
        //headImg.setImageResource(R.drawable.putao_icon_data_about);
        // delete by putao_lhq 2014年11月26日 for coolui6.0 end
        TextView infoTv = (TextView)itemLayout.findViewById(R.id.info_text);
        infoTv.setText(getResources().getString(R.string.putao_yellow_page_about));
        itemLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(YellowPageShopDetailActivity.this,
                        YellowPageAboutActivity.class));
            }
        });
        moreLayout.addView(itemLayout, getLayoutParams(true));
    }

    // 空行
    private void addSpaceItem() {
        // moreLayout.addView(LayoutInflater.from(this).inflate(
        // R.layout.detail_space_item, null), getLayoutParams(false));
    }

    private void addDealItem(final DianPingBusiness dianPingBusiness) {
        List<Deal> deals = dianPingBusiness.deals;
        if (deals == null || deals.size() == 0) {
            return;
        }
        final View layout = LayoutInflater.from(this).inflate(R.layout.putao_detail_customers_item,
                null);
        mCustomsImgView = (ImageView)layout.findViewById(R.id.tuan_info_img);
        mCustomsTitleTView = (TextView)layout.findViewById(R.id.tuan_title);
        mCustomsInfosTView = (TextView)layout.findViewById(R.id.tuan_info);
        mCustomsPriceTView = (TextView)layout.findViewById(R.id.tuan_current_price);
        mCustomsLastPriceTView = (TextView)layout.findViewById(R.id.tuan_last_price);
        layout.setVisibility(View.GONE);
        moreLayout.addView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        final Deal dealInfo = deals.get(0);
        layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(YellowPageShopDetailActivity.this,
                        YellowPageDetailActivity.class);
                /*
                 * 去掉title栏 update by hyl 2014-8-11 start old code:
                 * intent.putExtra("url", dealInfo.url);
                 */
                String url = dealInfo.url;
                if (!url.contains("hasheader")) {
                    url = url + "?hasheader=0";// 隐藏顶部栏 0-隐藏 1-打开
                }
                intent.putExtra("url", url);
                // update by hyl 2014-8-11 end

                intent.putExtra("title", dianPingBusiness.getName());
                startActivity(intent);
                MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                        UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_DIANPING_HEADER + categoryId);
            }
        });
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("format", "json");
        paramMap.put("deal_id", dealInfo.id);

        new DianpingAsyncTask(DianPingApiTool.URL_GET_CUSTOMS_INFO, paramMap, new IAsyncCallback() {

            @Override
            public void onPostExecute(String result) {
                // TODO Auto-generated method stub
                // result =
                // "{\"status\": \"OK\",\"count\": 1,\"deals\": [{\"deal_id\": \"1-5097286\",\"title\": \"Le Camelia法式浪漫拿破仑\",\"description\": \"仅售138元,价值238元法式浪漫拿破仑1个!2店通用!月星环球港新店开业!法式甜品经典,三十多层香脆起酥皮,融合清新奶油,口感层次丰富,幸福甜蜜妙不可言!\",\"list_price\": 238.2,\"current_price\": 138,\"image_url\": \"http://t2.s2.dpfile.com/pc/mc/e75efd4b642251e21af2691a9df77399(450x280)/thumb.jpg\"}]}";
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                DianpingCustomsInfo dataInfo = ModelFactory.getCustomsData(result);
                if (dataInfo == null) {
                    return;
                }

                layout.setVisibility(View.VISIBLE);
                mDealImageLoader.loadData(dataInfo.getImage_url(), mCustomsImgView);
                mCustomsTitleTView.setText(dataInfo.getTitle());
                mCustomsInfosTView.setText(dataInfo.getDescription());
                mCustomsPriceTView.setText(String.format(
                        getResources().getString(R.string.putao_yellow_page_detail_customsprice),
                        String.valueOf(dataInfo.getCurrent_price())));
                mCustomsLastPriceTView.setText(String.format(
                        getResources()
                                .getString(R.string.putao_yellow_page_detail_customslastprice),
                        String.valueOf(dataInfo.getList_price())));
            }
        }).execute();
    }
    
    private void addHotelRoomInfo(TongChengHotelItem hotel) {
        if (hotel == null) {
            return;
        }
        mHotelId = hotel.getHotelId();
        mHotelAddress = hotel.getAddress();
        mHotelName = hotel.getHotelName();
        if (mHotelRoomInfoLayout == null) {
            mHotelRoomInfoLayout = (LinearLayout)View.inflate(this,
                    R.layout.putao_hotel_room_layout, null);
            mHotelComDateTView = (TextView)mHotelRoomInfoLayout
                    .findViewById(R.id.hoteldetail_comedate);
            mHotelLeaveDateTView = (TextView)mHotelRoomInfoLayout
                    .findViewById(R.id.hoteldetail_leavedate);
            mHotelRoomInfoListView = (ListView)mHotelRoomInfoLayout
                    .findViewById(R.id.hoteldetail_roominfo_list);
            moreLayout.addView(mHotelRoomInfoLayout, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            mSharedPreferences = getSharedPreferences(ConstantsParameter.SHARED_PREFS_YELLOW_PAGE,
                    Context.MODE_PRIVATE);
            mDataLoader = new ImageLoaderFactory(this).getYellowPageLoader(R.drawable.putao_a0114, 0);
            mAdapter = new HotelRoomInfoAdapter(this, mHotelRoomList, mDataLoader, 2);
            mHotelRoomInfoListView.setAdapter(mAdapter);
            
            mHotelRoomInfoListView.setOnItemClickListener(this);
            
            mHotelComDateTView.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(YellowPageShopDetailActivity.this, YellowPageCalendarActivity.class);
                    intent.putExtra("DateType", CalendarBean.MODEL_SELECT_IN); // 入住
                    intent.putExtra("InCalendarData", mInCalendarModel);
                    intent.putExtra("OutCalendarData", mOutCalendarModel);
                    startActivityForResult(intent, REQUEST_CODE_DATE_IN);
                }
            });
            
            mHotelLeaveDateTView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(YellowPageShopDetailActivity.this, YellowPageCalendarActivity.class);
                    intent.putExtra("DateType", CalendarBean.MODEL_SELECT_OUT); // 离店
                    intent.putExtra("InCalendarData", mInCalendarModel);
                    intent.putExtra("OutCalendarData", mOutCalendarModel);
                    startActivityForResult(intent, REQUEST_CODE_DATE_OUT);
                }
            });
            
        }
        
        mComeDate =  CalendarUtil.getNowDateStr();
        mLeaveDate = CalendarUtil.getTomorrowDateStr();
        if (mSharedPreferences.contains(ConstantsParameter.PREFS_KEY_COM_DATE)
                && CalendarUtil.getDateFromString(mSharedPreferences.getString(ConstantsParameter.PREFS_KEY_COM_DATE, "")) != null
                && !new Date().after(CalendarUtil.getDateFromString(mSharedPreferences.getString(ConstantsParameter.PREFS_KEY_COM_DATE, "")))) {
            mComeDate = mSharedPreferences.getString(ConstantsParameter.PREFS_KEY_COM_DATE, mComeDate);
            mLeaveDate = mSharedPreferences.getString(ConstantsParameter.PREFS_KEY_LEAVE_DATE, mLeaveDate);
        }
        
        mWeekTagList = getResources().getStringArray(R.array.putao_week_list);
        // 初始化入住的信息
        if( !TextUtils.isEmpty(mComeDate) ){
            mInCalendarModel = getCalendarData(mComeDate, CalendarUtil.getWeekNumByDate(mComeDate));
        }
        // 初始化离店的信息
        if( !TextUtils.isEmpty(mLeaveDate) ){
            mOutCalendarModel = getCalendarData(mLeaveDate, CalendarUtil.getWeekNumByDate(mLeaveDate));
        }
        showHotelDate(true);
        showHotelDate(false);
        mHandler.sendEmptyMessage(MSG_START_REFRESH_ROOMLIST_DATA_ACTION);
    }
    
    /**
     * 生成日历项实例
     * @param date 如："2014-09-22"
     */
    private CalendarBean getCalendarData(String date, int week){
        String[] strList = date.split(CalendarUtil.DATE_FORMATTER_GAP);
        if(strList == null ){
            return null;
        }
        CalendarBean calendarBean = new CalendarBean();
        if( strList.length == CalendarUtil.DATE_FORMATTER_NUM ){
            try{
                calendarBean.setYear(Integer.parseInt(strList[0]));
                calendarBean.setMonth(Integer.parseInt(strList[1]));
                calendarBean.setDay(Integer.parseInt(strList[2]));
            }catch(Exception e){
                
            }
        }
        if( week >= 0 && week < CalendarUtil.WEEK_NUM ){
            calendarBean.setWeekInfo(mWeekTagList[week]);
        }
        return calendarBean;
    }
    
    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        if( !NetUtil.isNetworkAvailable(this) ){ //网络异常
            Utils.showToast(this, R.string.putao_no_net, false);
            return;
        }
        TC_HotelRoomBean hotelRoomBean = mHotelRoomList.get(position);
        if( hotelRoomBean == null ){
            return;
        }
        int bookingFlag = hotelRoomBean.getBookingFlag();
        if( bookingFlag != 0 ){
            //房间已满
            Toast.makeText(this, R.string.putao_hoteldetail_cannot_book, Toast.LENGTH_SHORT).show();
            return;
        }
        checkRoomPolicy(hotelRoomBean);
    }
    
    private void checkRoomPolicy(TC_HotelRoomBean hotelRoomBean){
        if ( (mQueryRoomPolicyDataTask != null && mQueryRoomPolicyDataTask.getStatus() != AsyncTask.Status.RUNNING)
                || mQueryRoomPolicyDataTask == null ) {
            
            mQueryRoomPolicyDataTask = new QueryRoomPolicyDataTask(mHotelId, mComeDate, mLeaveDate,
                    hotelRoomBean.getRoomTypeId(), hotelRoomBean.getPolicyId(), getCurrentArriveTime(),
                    new IQueryRoomPolicyDataCallback() {
                @Override
                public void onPreExecute() {
                }

                @Override
                public void onPostExecute(TC_Response_HotelRoomsWithPolicy result) {
                    if( result == null ){
                        Utils.showToast(YellowPageShopDetailActivity.this, R.string.putao_hoteldetail_gethotelpolicy_hint, false);
                        return;
                    }
                    List<TC_HotelRoomBean> hotelroomlist = result.getHotelroomlist();
                    if( hotelroomlist == null ){
                        Utils.showToast(YellowPageShopDetailActivity.this, R.string.putao_hoteldetail_gethotelpolicy_hint, false);
                        return;
                    }
                    TC_HotelRoomBean roomBean = hotelroomlist.get(0);
                    if( roomBean == null ){
                        Utils.showToast(YellowPageShopDetailActivity.this, R.string.putao_hoteldetail_gethotelpolicy_hint, false);
                        return;
                    }
                    Intent intent = new Intent(YellowPageShopDetailActivity.this, YellowPageHotelOrderActivity.class);
                    intent.putExtra("CityName", mCityName);
                    intent.putExtra("HotelId", mHotelId);
                    intent.putExtra("HotelName", mHotelName);
                    intent.putExtra("HotelImg", roomBean.getPhotoUrl()); //保存酒店房型Url
                    intent.putExtra("HotelAddress", mHotelAddress);
                    intent.putExtra("HotelRoomName", roomBean.getRoomName());
                    intent.putExtra("RoomTypeId", roomBean.getRoomTypeId());
                    intent.putExtra("PolicyId", roomBean.getPolicyId());
                    intent.putExtra("DaysAmountPrice", roomBean.getRoomAdviceAmount());
                    intent.putExtra("AvgAmount", roomBean.getAvgAmount());
                    intent.putExtra("DanbaoType", roomBean.getDanBaoType());
                    intent.putExtra("GuaranteeType", roomBean.getGuaranteeType());
                    intent.putExtra("OverTime", roomBean.getOverTime());
                    intent.putExtra("ComeDate", mComeDate);
                    intent.putExtra("LeaveDate", mLeaveDate);
                    startActivity(intent);

//                  // add xcx 2014-12-30 start 统计埋点
//                  MobclickAgentUtil.onEvent(ContactsApp.getInstance()
//                          .getApplicationContext(),
//                          UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_DETAIL_ROOM_SELECT);
//                  // add xcx 2014-12-30 end 统计埋点
                }
            });
    
    mQueryRoomPolicyDataTask.execute();
}
            }
    
    
    /**
     * 根据当前时间来设置正确的最晚到店时间
     * 注：最晚到店时间为
     */
    private String getCurrentArriveTime(){
        String currentTime = "1900-01-02 05:00"; 
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if( hour < 18 ){
            currentTime = "1900-01-01 18:00";
        }else if( hour < 20){
            currentTime = "1900-01-01 20:00";
        }else if( hour < 22){
            currentTime = "1900-01-01 22:00";
        }else if( hour < 24){
            currentTime = "1900-01-02 05:00";
        }
        return currentTime;
    }

    private void addReviewsItem(final DianPingBusiness dianPingBusiness) {
        final View layout = LayoutInflater.from(this).inflate(R.layout.putao_detail_reviews_item, null);
        mReviewsTView = (TextView)layout.findViewById(R.id.reviews_info);
        layout.setVisibility(View.GONE);
        moreLayout.addView(layout/*, new LayoutParams(LayoutParams.MATCH_PARENT, getResources()
                .getDimensionPixelSize(R.dimen.putao_yp_detail_reviews_height))*/);

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("business_id", String.valueOf(dianPingBusiness.business_id));
        paramMap.put("format", "json");
        paramMap.put("platform", String.valueOf(2));
        new DianpingAsyncTask(DianPingApiTool.URL_GET_REVIEWS_INFO, paramMap, new IAsyncCallback() {

            @Override
            public void onPostExecute(String result) {
                // TODO Auto-generated method stub
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                final DianpingReviewsInfo dataInfo = ModelFactory.getReviewsData(result);
                if (dataInfo == null) {
                    return;
                }
                layout.setVisibility(View.VISIBLE);
                mReviewsTView.setText(dataInfo.getText_excerpt());
                if (TextUtils.isEmpty(dataInfo.getReview_url())) {
                    return;
                }
                layout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(YellowPageShopDetailActivity.this,
                                YellowPageDetailActivity.class);

                        /*
                         * 去掉title栏 update by hyl 2014-8-11 start old code:
                         * intent.putExtra("url", dataInfo.getReview_url());
                         */
                        String url = dataInfo.getReview_url();
                        if (!url.contains("hasheader")) {
                            url = url + "?hasheader=0";// 隐藏顶部栏 0-隐藏 1-打开
                        }
                        intent.putExtra("url", url);
                        // update by hyl 2014-8-11 end

                        intent.putExtra("title", dianPingBusiness.getName());
                        startActivity(intent);
                        MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                                UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_COMMENT_HEADER
                                        + categoryId);
                    }
                });
            }
        }).execute();
    }

    private void addCouponItem(String couponDescription, final String couponUrl, final String name) {
        if (TextUtils.isEmpty(couponDescription))
            return;

        View view = LayoutInflater.from(this).inflate(R.layout.putao_detail_homepage_item, null);
        /*ImageView headImg = (ImageView)view.findViewById(R.id.head_img); */
        TextView infoTv = (TextView)view.findViewById(R.id.info_text);
        ImageView sourceImg = (ImageView)view.findViewById(R.id.source_img);

//        headImg.setImageResource(R.drawable.putao_icon_data_coupon);
        sourceImg.setImageResource(R.drawable.putao_icon_logo_hui);
        infoTv.setText(couponDescription);

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(YellowPageShopDetailActivity.this,
                        YellowPageDetailActivity.class);

                /*
                 * 去掉title栏 update by hyl 2014-8-11 start old code:
                 * intent.putExtra("url", couponUrl);
                 */
                String url = couponUrl;
                if (!url.contains("hasheader")) {
                    url = url + "?hasheader=0";// 隐藏顶部栏 0-隐藏 1-打开
                }
                intent.putExtra("url", url);
                // update by hyl 2014-8-11 end

                intent.putExtra("title", name);
                startActivity(intent);
            }
        });

        moreLayout.addView(view, getLayoutParams(true));
    }

    private void addHomePageItem(final String businessUrl, final String name, int typeImgId) {
        if (TextUtils.isEmpty(businessUrl)) {
            return;
        }

        // 空行
        addSpaceItem();

        View view = LayoutInflater.from(this).inflate(R.layout.putao_detail_homepage_item, null);
        TextView infoTv = (TextView)view.findViewById(R.id.info_text);
        ImageView sourceImg = (ImageView)view.findViewById(R.id.source_img);
		//putao_lhq delete for coolui6.0
        //ImageView headerImg = (ImageView)view.findViewById(R.id.head_img);
        //headerImg.setImageResource(R.drawable.putao_icon_data_qt);

        infoTv.setText(R.string.putao_yellow_page_watch_homepage);
        sourceImg.setImageResource(typeImgId);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //add ljq 2014_11_10 start 如果是Web型则做进入优化
                Intent intent = new Intent(YellowPageShopDetailActivity.this,
                        YellowPageJumpH5Activity.class);
                if (businessUrl.startsWith("www.")) {
                    intent.putExtra("url", "http://" + businessUrl);//
                } else {
                    intent.putExtra("url", businessUrl);// +businessUrl
                }
                //add ljq 2014_11_10 end 如果是Web型则做进入优化
                intent.putExtra("title", name);
                startActivity(intent);

                // 网络数据- 显示来源网站
                MobclickAgentUtil
                        .onEvent(YellowPageShopDetailActivity.this,
                                UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_VIEWSOURCESITE_HEADER
                                        + categoryId);
            }
        });
        moreLayout.addView(view, getLayoutParams(true));
    }

    private void addTongChengHotelHomePageItem(final YellowPageTongChengItem item){
        
        addSpaceItem();

        View view = LayoutInflater.from(this).inflate(R.layout.putao_detail_homepage_item, null);
        TextView infoTv = (TextView)view.findViewById(R.id.info_text);
        ImageView sourceImg = (ImageView)view.findViewById(R.id.source_img);
        sourceImg.setImageResource(R.drawable.putao_icon_logo_tongcheng);
        infoTv.setText(R.string.putao_yellow_page_watch_homepage_tongcheng);
//        sourceImg.setImageResource(typeImgId);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YellowPageShopDetailActivity.this,
                        YellowPageHotelDetailActivity.class);
                intent.putExtra("HotelId", item.getHotelid());
                intent.putExtra("HotelImg",item.getPhotoUrl());
                intent.putExtra("HotelName", item.getName());
                intent.putExtra("HotelAddress", item.getAddress());
                intent.putExtra("Longitude", item.getLongitude());
                intent.putExtra("Latitude", item.getLatitude());
                intent.putExtra("HotelMarkNum", item.getMarkNum());
                intent.putExtra("StarRatedName", item.getStarRatedName());
                intent.putExtra("ComeDate", CalendarUtil.getNowDateStr());
                intent.putExtra("LeaveDate", CalendarUtil.getTomorrowDateStr());
                startActivity(intent);
            }
        });
        moreLayout.addView(view, getLayoutParams(true));
        
        
       
    }
    

            private void addAddressItem(final String address, final double latitude,
                    final double longitude, final String poiId) {
                if (TextUtils.isEmpty(address)) {
                    return;
                }

                View view = LayoutInflater.from(this).inflate(R.layout.putao_detail_homepage_item, null);
                //ImageView headImg = (ImageView)view.findViewById(R.id.head_img);//putao_lhq delete for coolui6.0
                TextView infoTv = (TextView)view.findViewById(R.id.info_text);
                //headImg.setImageResource(R.drawable.putao_icon_data_address);//putao_lhq delete for coolui6.0
                ImageView locImg = (ImageView)view.findViewById(R.id.source_img);
                locImg.setImageResource(R.drawable.putao_icon_logo_loction);
                infoTv.setText(address);

                view.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Uri uri = null;
                        Intent intent = null;
                        try {
                            // 高德地图intent
                            intent = getGaodeMapIntent(latitude, longitude, address, poiId);

                            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                                    PackageManager.GET_ACTIVITIES);
                            if (list == null || list.size() == 0) {// 高德地图 未安装，选择百度地图
                                // 百度地图intent
                                CoordinateConverter converter = new CoordinateConverter();// 坐标转换工具
                                converter.from(CoordinateConverter.CoordType.COMMON);
                                converter.coord(new LatLng(latitude, longitude));
                                LatLng latLng = converter.convert();

                                intent = getBaiduMapIntent(latLng.latitude, latLng.longitude, address);
                                list = getPackageManager().queryIntentActivities(intent,
                                        PackageManager.GET_ACTIVITIES);

                                if (list == null || list.size() == 0) {// 百度地图
                                                                       // 未安装，选择百度地图网页版
                                    intent = new Intent(Intent.ACTION_VIEW);
                                    uri = Uri.parse("http://api.map.baidu.com/marker?" + "location="
                                            + latLng.latitude + "," + latLng.longitude + "&title="
                                            + address + "&content=" + address + "&output=html");
                                    intent.setData(uri);
                                }
                            }
                            startActivity(intent);

                        } catch (Exception e) {
                            showToast(R.string.putao_yellow_page_no_mapapp);
                        }
                        MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                                UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_LOCATION_HEADER + categoryId);
                    }
                });

                view.setTag(address);
                view.setOnLongClickListener(this);
                moreLayout.addView(view, getLayoutParams(true));
            }

            protected Intent getBaiduMapIntent(double latitude, double longitude, String address) {
                // Uri uri = Uri.parse("geo:0,0?q=" + address);
                // Intent intent = new Intent(Intent.ACTION_VIEW);
                // intent.setData(uri);
                // intent.setPackage("com.baidu.BaiduMap");// 百度地图

                String url = "intent://map/marker?location="
                        + latitude
                        + ","
                        + longitude
                        + "&title="
                        + address
                        + "&content"
                        + address
                        + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";

                Intent intent = null;
                try {
                    intent = Intent.getIntent(url);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return intent;
            }

            protected Intent getGaodeMapIntent(double latitude, double longitude, String address,
                    String poiId) {
                // Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" +
                // address);
                // Intent intent = new Intent(Intent.ACTION_VIEW);
                // intent.setData(uri);
                // intent.setPackage("com.autonavi.minimap");// 高德地图
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory("android.intent.category.DEFAULT");
                Uri uri = Uri.parse("androidamap://viewMap?sourceApplication=appname&poiid=" + poiId
                        + "&poiname=" + address + "&lat=" + latitude + "&lon=" + longitude + "&dev=0");
                intent.setData(uri);
                intent.setPackage("com.autonavi.minimap");// 高德地图

                return intent;
            }

    private void addWebSiteItem(final String website) {
        if (TextUtils.isEmpty(website)) {
            return;
        }

        // 空行
        addSpaceItem();

        View view = LayoutInflater.from(this).inflate(R.layout.putao_detail_other_item, null);
        // delete by putao_lhq 2014年11月26日 for coolui6.0 start
        /*ImageView headImg = (ImageView)view.findViewById(R.id.head_img);
        headImg.setImageResource(R.drawable.putao_icon_data_qt);
        */
        // delete by putao_lhq 2014年11月26日 for coolui6.0 end
        TextView infoTv = (TextView)view.findViewById(R.id.info_text);
        infoTv.setMaxLines(1);
        infoTv.setText(R.string.putao_yellow_page_watch_homepage);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String url = website;
                if (!url.startsWith("http://")) {
                    url = "http://" + url;
                }
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);

                // 葡萄数据- 显示官方网站
                MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                        UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_VIEWOFFICIALWEBSITE_HEADER
                                + categoryId);
            }
        });

        moreLayout.addView(view, getLayoutParams(true));
    }

            private void addSerchOtherItem(List<SerchItem> searchItems, boolean showIcon) {
                for (int i = 0; i < searchItems.size(); i++) {
                    SerchItem searchItem = searchItems.get(i);
                    final String key = searchItem.getSearchKey();
                    final String category = searchItem.getSearch_category();
                    String show = searchItem.getSearchShow();
                    View view = LayoutInflater.from(this).inflate(R.layout.putao_detail_other_item, null);
                    TextView infoTv = (TextView)view.findViewById(R.id.info_text);
                    // delete by putao_lhq 2014年11月26日 for coolui6.0 start
                    /*ImageView headerImageView = (ImageView)view.findViewById(R.id.head_img);
                    ImageView headerImg = (ImageView)view.findViewById(R.id.head_img);
                    headerImg.setImageResource(R.drawable.putao_icon_data_other);
                    if (i == 0 && showIcon) {
                        headerImageView.setVisibility(View.VISIBLE);
                    } else {
                        headerImageView.setVisibility(View.INVISIBLE);
                    }
                    headerImg.setVisibility(View.GONE);
                     */
                    // delete by putao_lhq 2014年11月26日 for coolui6.0 start
                    infoTv.setText(show);

                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(YellowPageShopDetailActivity.this,
                                    YellowPageSearchActivity.class);
                            YellowParams params = new YellowParams();
                            params.setWords(key);
                            params.setCategory(category);
                            intent.putExtra(YellowUtil.TargetIntentParams, params);
                            startActivity(intent);
                            MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                                    UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_NEARBY_HEADER + categoryId);
                        }
                    });
                    moreLayout.addView(view, getLayoutParams(true));
                }
            }


            
            private void addPhoneItem(String number, int index) {
                addPhoneItemView(number, null, index);
            }
            
            private void addPhoneItem(List<NumberItem> numberItems) {
                int num = 0;
                for (NumberItem numberItem : numberItems) {
                    String number = numberItem.getNumber();
                    String decription = numberItem.getNumberDescription();
                    addPhoneItemView(number, decription, num);
                    num++;
                }
            }

            private void addPhoneItemView(final String number, String decription, int num) {
                if (TextUtils.isEmpty(number))
                    return;

                View view = LayoutInflater.from(this).inflate(R.layout.putao_detail_number_item, null);
                //putao_lhq delete for coolui6.0 start
                /*ImageView headImg = (ImageView)view.findViewById(R.id.head_img);
                if (num == 0) {
                    headImg.setVisibility(View.VISIBLE);
                } else {
                    headImg.setVisibility(View.INVISIBLE);
                }*/
                //putao_lhq delete for coolui6.0 end
                TextView numberTv = (TextView)view.findViewById(R.id.number_text);
                TextView decriptionTv = (TextView)view.findViewById(R.id.decription_text);
                numberTv.setText(number);

                if (!TextUtils.isEmpty(decription)) {
                    decriptionTv.setText(decription);
                } else {
                    decriptionTv.setVisibility(View.GONE);
                }
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContactsHubUtils.call(YellowPageShopDetailActivity.this, number);
                        MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                                UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_CALL_HEADER + categoryId);
                        MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                                UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_CALL);
                    }
                });

                /**
                 * add code by putao_lhq
                 * @start
                 * */
                view.findViewById(R.id.callPhone).setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View arg0) {
                        ContactsHubUtils.call(YellowPageShopDetailActivity.this, number);
                        MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                                UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_CALL_HEADER + categoryId);
                        MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                                UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_CALL);
                    }
                });/*@end*/
                
                view.setTag(number);
                view.setOnLongClickListener(this);
                moreLayout.addView(view, getLayoutParams(true));
            }

            private void addFastServices(List<FastServiceItem> fServices) {
                for (int i = 0; i < fServices.size(); i++) {
                    FastServiceItem fsi = fServices.get(i);
                    final String name = fsi.getService_name();
                    final String url = fsi.getService_url();
                    View view = LayoutInflater.from(this).inflate(R.layout.putao_detail_other_item, null);
                    TextView infoTv = (TextView)view.findViewById(R.id.info_text);
                    /**
                     * delete code by putao_lhq
                     * @start
                     *
                    view.findViewById(R.id.head_img).setVisibility(i == 0 ? View.VISIBLE : View.INVISIBLE);
                    @end*/
                    infoTv.setText(name);

                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Intent i = new Intent(YellowPageShopDetailActivity.this,
                                    YellowPageDetailActivity.class);
                            i.putExtra("title", name);
                            i.putExtra("url", url);
                            startActivity(i);
                            MobclickAgentUtil.onEvent(YellowPageShopDetailActivity.this,
                                    UMengEventIds.DISCOVER_YELLOWPAGE_DETAIL_FAST_SERVICE_HEADER
                                            + categoryId);
                        }
                    });
                    moreLayout.addView(view, getLayoutParams(true));
                }
            }
            
            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if( data == null ){
                    return;
                }
                if( requestCode == REQUEST_CODE_DATE_IN ){
                    // 入住日期 选择
                    Object obj = null;
                    try{
                        obj = data.getSerializableExtra("SelectCalendar");
                    }catch(Exception e){
                        obj = null;
                    }
                    if( obj == null ){
                        return;
                    }
                    // 更新入住时间
                    CalendarBean calendarModel = (CalendarBean)obj ;
                    Message msg = mHandler.obtainMessage();
                    boolean needClearOutCalendar = data.getBooleanExtra("NeedClearOutCalendar", false);
                    if( needClearOutCalendar ){
                        // 更新离店时间
                        msg.what = MSG_REUPDATE_DATE_OUT_ACTION;
                    }else{
                        // 不需要更新离店时间
                        msg.what = MSG_UPDATE_DATE_IN_ACTION;
                    }
                    msg.obj = calendarModel;
                    mHandler.sendMessage(msg);
                }else if( requestCode == REQUEST_CODE_DATE_OUT ){
                    // 离店日期 选择
                    Object obj = null;
                    try{
                        obj = data.getSerializableExtra("SelectCalendar");
                    }catch(Exception e){
                        obj = null;
                    }
                    if( obj == null ){
                        return;
                    }
                    CalendarBean calendarModel = (CalendarBean)obj ;
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_UPDATE_DATE_OUT_ACTION;
                    msg.obj = calendarModel;
                    mHandler.sendMessage(msg);
                }
            };

            private LayoutParams getLayoutParams(boolean isValidItem) {
                int height = 0;
                if (isValidItem) {
                    height = getResources().getDimensionPixelSize(R.dimen.putao_yp_detail_item_height);
                } else {
                    height = getResources().getDimensionPixelSize(R.dimen.putao_yp_detail_spaceitem_height);
                }
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
                return params;
            }
            
            @Override
            public void fillDataInView(Object result, View view) {
                if (result == null || view == null)
                    return;

                if (result instanceof Bitmap) {
                    Bitmap sourceBitmap = (Bitmap)result;
                    try {
                        // add mask for coolpad
                        Bitmap bitmapmask = BitmapFactory.decodeResource(getResources(),
                                R.drawable.putao_mask);
                        circleBitamp = ContactsHubUtils.makeRoundCornerforCoolPad(sourceBitmap, bitmapmask);
                        // circleBitamp = ContactsHubUtils.corner(sourceBitmap,10);
                        // circleBitamp =
                        // ContactsHubUtils.addbackground4onlyicon(getResources(),sourceBitmap);
                        ((ImageView)view).setImageDrawable(null);
                        if (circleBitamp != null) {
                            ((ImageView)view).setImageBitmap(circleBitamp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onStop() {
                super.onStop();
                if (mDefaultCollectType == ConstantsParameter.YELLOWPAGE_DATA_TYPE_FAVORITE) {
                    if (mCollectData != null) {
                        // 进入时已收藏，更新浏览时间
                        mYellowPageDB.updateYellowPageCollerViewTime(itemId, mCollectData.getDataType(),
                                itemSourceId, mCollectData.getName(), System.currentTimeMillis());
                    }
                } else if (mDefaultCollectType == ConstantsParameter.YELLOWPAGE_DATA_TYPE_HISTORY) {
                    // 进入时已有历史
                    if (mCollectData == null) {
                        initCollectData(ConstantsParameter.YELLOWPAGE_DATA_TYPE_HISTORY);
                        mYellowPageDB.inseartCollectData(mCollectData);
                    } else { // 更新浏览时间
                        mYellowPageDB.updateYellowPageCollerViewTime(itemId, mCollectData.getDataType(),
                                itemSourceId, mCollectData.getName(), System.currentTimeMillis());
                    }
                } else {
                    // 第一次进入（未有历史、收藏记录）
                    if (mCollectData == null) {
                        initCollectData(ConstantsParameter.YELLOWPAGE_DATA_TYPE_HISTORY);
                        mYellowPageDB.inseartCollectData(mCollectData);
                    }
                }
            }

            private void initCollectData(int dataType) {
                mCollectData = new YellowPageCollectData();
                mCollectData.setItemId((int)itemId);
                mCollectData.setDataType(dataType);
                mCollectData.setName(itemName);
                mCollectData.setType(itemSourceId);
                mCollectData.setContent(itemContent);
                mCollectData.setTime(System.currentTimeMillis());
            }
            
            
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent intent = new Intent();
                    int collectType = 0;
                    if (mCollectData == null) {
                        // 第一次进入 或者 从历史中进入
                        if (mDefaultCollectType != ConstantsParameter.YELLOWPAGE_DATA_TYPE_FAVORITE) {
                            collectType = 1;
                        }
                    } else if (mDefaultCollectType == mCollectData.getDataType()) {
                        // collectType = 1 ，进入黄页详情没有改变 收藏结果
                        collectType = 1;
                    }
                    intent.putExtra("COLLECT_RESULT", collectType);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return super.onKeyDown(keyCode, event);
            }

            @Override
            protected void onDestroy() {
                if (circleBitamp != null && !circleBitamp.isRecycled()) {
                    circleBitamp.recycle();
                    circleBitamp = null;
                }
                if(mQueryDataTask != null){
                    mQueryDataTask.cancel(true);
                    mQueryDataTask = null;
                }
                if(mQueryRoomPolicyDataTask != null){
                    mQueryRoomPolicyDataTask.cancel(true);
                    mQueryRoomPolicyDataTask = null;
                }
                mImageLoader.clearCache();
                mClipboardDialog = null;
                super.onDestroy();
            }

            @Override
            public Integer remindCode() {
                return remindCode;
            }

            @Override
            public String getServiceNameByUrl() {
                return null;
            }

            @Override
            public String getServiceName() {
                return this.getClass().getName();
            }

            @Override
            public boolean needMatchExpandParam() {
                return true;
            }
            
            private CommonDialog mClipboardDialog = null;

            @Override
            public boolean onLongClick(View view) {
                // TODO Auto-generated method stub
                Object object = view.getTag();
                if(object == null){
                    return false;
                }
                final String content = (String) object;
                if( mClipboardDialog == null ){
                    mClipboardDialog = CommonDialogFactory.getOkCancelCommonLinearLayoutDialog(this);
                    mClipboardDialog.setTitle(R.string.putao_copy_to_clipboard);
                    mClipboardDialog.setCancelButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mClipboardDialog.dismiss();
                        }
                    });
                    mClipboardDialog.setOkButtonClickListener(new OnClickListener() {
                        
                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            /** 添加数据到剪切板 */
                            ClipboardManager clipboarManager = (ClipboardManager)YellowPageShopDetailActivity.this
                                    .getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboarManager.setPrimaryClip(ClipData.newPlainText(null, content));
                            mClipboardDialog.dismiss();
                        }
                    });
                }
                mClipboardDialog.getMessageTextView().setText(content);
                mClipboardDialog.show();
                return false;
            }

            @Override
            protected boolean needReset() {
                return true;
            }
            
        }
