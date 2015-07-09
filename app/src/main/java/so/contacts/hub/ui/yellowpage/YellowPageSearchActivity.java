package so.contacts.hub.ui.yellowpage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import so.contacts.hub.adapter.YellowPageAdapter;
import so.contacts.hub.core.Config;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.search.SchduleManager;
import so.contacts.hub.search.SearchResultListener;
import so.contacts.hub.search.SearchUtils;
import so.contacts.hub.search.Solution;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.search.rule.DuplicateRemoveRule;
import so.contacts.hub.ui.yellowpage.bean.ItemBean;
import so.contacts.hub.ui.yellowpage.bean.SearchItemImage;
import so.contacts.hub.ui.yellowpage.bean.SearchItemLocalImage;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.CustomListView;
import so.contacts.hub.widget.CustomListView.OnLoadMoreListener;
import so.contacts.hub.widget.CustomListView.OnRefreshListener;
import so.contacts.hub.widget.ProgressDialog;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import so.putao.findplug.SearchData;
import so.putao.findplug.SourceItemObject;
import so.putao.findplug.YelloPageDataManager;
import so.putao.findplug.YelloPageItem;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;

import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

public class YellowPageSearchActivity extends BaseRemindActivity implements
		OnItemClickListener, OnClickListener,
		LBSServiceListener, OnLoadMoreListener, OnRefreshListener, SearchResultListener {

	private static final String TAG = "YellowPageSearchActivity";

	private CustomListView mListView;

	private ImageView nextStepImage;

	private YellowPageAdapter mAdapter;

	private DataLoader mImageLoader;

	private SearchData mAsyncSearchData;

	private boolean mHasMore = false;
	
	private Solution mSolution;

    private SchduleManager mSchduler;

	private List<YelloPageItem> mPageItemList;

	private int remindCode;
	
	private String category;

	private String words;
	
	private String searchName;

	private boolean showDianping = true;

	private boolean showSougou = true;

	private SearchItemImage mItemImage;

	private long mCurrentCategoryId = -1;

	private ProgressDialog mProgressDialog;

	// 下拉刷新最小时间间隔15s
	private static final int REFRESH_MIN_TIME_GAP = 15000;

	// 上次下拉刷新时间
	private long mLastRefreshTime = 0L;

	private RelativeLayout mException;

	private RelativeLayout mSearchContent;
	
	//上一次网络状态
	private boolean mLastNetworkAvaliable = true;
	
    // 网络状态变化action
    private static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    
    //进入页面传入的定位信息（有这信息，则不需要定位） start 
    private double mLocLatitude = 0;
    private double mLocLongtitude = 0;
    private String mLocCity = null;
    //进入页面传入的定位信息（有这信息，则不需要定位） end

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_search_list);
		YelloPageDataManager.createInstance(this.getApplicationContext());
		YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
		showDialog();

		if (getIntent() != null) {
			YellowParams keys = (YellowParams) getIntent()
					.getSerializableExtra(YellowUtil.TargetIntentParams);
			if (keys != null) {
			    searchName = keys.getName();
				words = keys.getWords();
				category = keys.getCategory();
				showDianping = keys.isShowDianping();
				showSougou = keys.isShowSougou();
				//add ljq 2014-11-26 start 如果推送搜索服务在本地有同名服务（比如推送 附近的酒店）则去取默认图片数据
				if(mItemImage != null){
				    mItemImage = keys.getItemImageStr();
				}else{
				    if(!TextUtils.isEmpty(searchName)){
	                    ItemBean itemBean = db.queryItemByName(searchName);
	                    if(itemBean != null){
	                        YellowParams params = new Gson().fromJson(itemBean.getTarget_params(), YellowParams.class);
	                        SearchItemImage images = params.getItemImageStr();
	                        if(images != null){
	                            mItemImage = images;
	                        }
	                    }
				    }
				}
				//add ljq 2014-11-26 end 如果推送搜索服务在本地有同名服务（比如推送 附近的酒店）则去取默认图片数据
				remindCode = keys.getRemindCode();
			} else {
			    searchName = getIntent().getStringExtra("name");
				words = getIntent().getStringExtra("keyword");
				category = getIntent().getStringExtra("category");
		        remindCode = getIntent().getIntExtra("RemindCode", -1);
		        
		        mLocCity = getIntent().getStringExtra("City");
		        mLocLatitude = getIntent().getDoubleExtra("Latitude", 0);
		        mLocLongtitude = getIntent().getDoubleExtra("Longtitude", 0);
			}
			mCurrentCategoryId = getIntent().getLongExtra(YellowPageShopDetailActivity.EXTRA_UMENG_DETAIL_ID, -1);
		}
		int resId = R.drawable.putao_icon_logo_placeholder;
		if (mItemImage != null
				&& !TextUtils.isEmpty(mItemImage.getDefaultImg())) {
			resId = getResources().getIdentifier(mItemImage.getDefaultImg(),
					"drawable", getPackageName());
		}
		int imageSize = getResources().getDimensionPixelSize(R.dimen.putao_listview_multirow_item_imgsize);
		mImageLoader = new ImageLoaderFactory(this).getYellowPageLoader(resId,imageSize);
		mPageItemList = new ArrayList<YelloPageItem>();
		initViews();

        mSchduler = new SchduleManager();

		refreshRelocation();
        registerReceiver(mNetworkReceiver, new IntentFilter(CONNECTIVITY_CHANGE_ACTION));
	}
	
    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if( CONNECTIVITY_CHANGE_ACTION.equals(action) ){
				LogUtil.i(TAG, "network is changed!");
				if( NetUtil.isNetworkAvailable(YellowPageSearchActivity.this)){
					LogUtil.i(TAG, "network is changed and network is avaliable.");
					if( !mLastNetworkAvaliable ){
						reLoadData();
					}
					mLastNetworkAvaliable = true;
				}else{
					LogUtil.i(TAG, "network is changed and network is exception.");
					mLastNetworkAvaliable = false;
				}
			}
		}
    	
    };

	private void showDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this,false);
			mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
		}
		mProgressDialog.show();
	}


	private void disMissDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void initViews() {
		findViewById(R.id.back_layout).setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		String showTitle = "";
		if( !TextUtils.isEmpty(searchName) ){
			showTitle = searchName;
		} else if ( !TextUtils.isEmpty(words) ) {
		    showTitle = words.split(",")[0];
		} else {
            if (null != category) {
                String[] data = category.split(",");
                if (null != data && data.length > 0) {
                    showTitle = category.split(",")[0];
                }
            }
		}
		
		if( TextUtils.isEmpty(showTitle) ){
			title.setText(mTitleContent);
		}else{
			title.setText(showTitle);
		}
		
		findViewById(R.id.next_setp_layout).setVisibility(View.GONE);
		//remove by xn 定位crash 2014-12-20
//		nextStepImage = (ImageView) findViewById(R.id.next_step_img);
//		nextStepImage.setVisibility(View.VISIBLE);
//		nextStepImage.setImageResource(R.drawable.putao_icon_marker_m);
//		findViewById(R.id.next_step_btn).setVisibility(View.VISIBLE);
//		findViewById(R.id.next_setp_layout).setOnClickListener(this);

		mSearchContent = (RelativeLayout) findViewById(R.id.search_content);
		mException = (RelativeLayout) findViewById(R.id.network_exception_layout);
		mException.setOnClickListener(this);
		mListView = (CustomListView) findViewById(R.id.search_list);
		mListView.setOnItemClickListener(this);
		mListView.setOnLoadListener(this);
		mListView.setOnRefreshListener(this);
		mListView.setAutoLoadMore(true);
		mListView.setCanLoadMore(true);
		mListView.setCanRefresh(true);

		mAdapter = new YellowPageAdapter(this, mPageItemList, mImageLoader,mItemImage);
		mListView.setAdapter(mAdapter);
	}

	private void refreshData(double latitude,double longtitude,String city) {
//		if (LBSServiceGaode.hasPreInfo()) {
//			mAsyncSearchData = new SearchData(LBSServiceGaode.getPreLatitude(),
//					LBSServiceGaode.getPreLongitude(),
//					LBSServiceGaode.getPreCity(), category, words, 1,
//					showDianping, showSougou);
//			YelloPageDataManager.getInstance().asyncSearch(mAsyncSearchData,
//					this, UMengUtil.SEARCH_SOURCE_NEAR);
//			isLoaded = true;
//		} else {
//			isLoaded = false;
//		}
		
//		mAsyncSearchData = new SearchData(latitude,longtitude,city, category, words, 1,
//				showDianping, showSougou);
//		YelloPageDataManager.getInstance().asyncSearch(mAsyncSearchData,
//				this, UMengUtil.SEARCH_SOURCE_NEAR);
	}
	
    private void createSolution(double latitude,double longtitude,String city) {
        int entry = SearchUtils.SEARCH_ENTRY_CATEGORY; // 分类类别查询(附近的xx)
        SearchInfo defSearchInfo = new SearchInfo();
        defSearchInfo.setWords(words);
        defSearchInfo.setCategory(category);

        LogUtil.d(TAG, "createSolution entry="+entry+" searchName="+searchName+" words="+words+" category="+category);
        mSolution = SearchUtils.createSolution(searchName, entry, defSearchInfo, false, false);
        if (mSolution == null)
            return;

        mSolution.setActivity(this);
        mSolution.setEntry(SearchUtils.SEARCH_ENTRY_CATEGORY);
        mSolution.setMainHandler(mHandler);
        mSolution.setInputCity(city);
//        mSolution.setInputKeyword(searchName);  // 附近的搜索不需要把搜索关键字传入，避免搜索:附近的xx
        mSolution.setInputLatitude(latitude);
        mSolution.setInputLongtitude(longtitude);

        LogUtil.d(TAG, mSolution.toString());
        mSchduler.setSolution(mSolution, this);        
        mSchduler.schdule("1");
    }	
	/**
	 * 重新定位并刷新
	 */
	private void refreshRelocation() {
		LogUtil.d(TAG, "refreshRelocation");
		if (NetUtil.isNetworkAvailable(this)) {
			mSearchContent.setVisibility(View.VISIBLE);
			mException.setVisibility(View.GONE);
			if( !TextUtils.isEmpty(mLocCity) && mLocLatitude != 0 && mLocLongtitude != 0 ){
				//有传入定位信息，直接搜索
				createSolution(mLocLatitude, mLocLongtitude, mLocCity);
			}else{
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
						LogUtil.d(TAG, "开启定位");
						LBSServiceGaode.process_activate(
								YellowPageSearchActivity.this,
								YellowPageSearchActivity.this);
//					}
//				}).start();
			}
		} else {
			disMissDialog();
			mSearchContent.setVisibility(View.GONE);
			mException.setVisibility(View.VISIBLE);
			// Toast.makeText(this, R.string.putao_no_net, Toast.LENGTH_LONG).show();
		}
	}

	@SuppressWarnings("rawtypes")
	private void setItemResId(YelloPageItem item) {
		List<SearchItemLocalImage> localImageList = mItemImage.getLocalImages();
		String resId = mItemImage.getDefaultImg();
		if (!TextUtils.isEmpty(resId)) {
			SourceItemObject itemObj = item.getData();
			itemObj.setDefaultPhotoUrl(resId);
		}
		String itemName = item.getName();
		if (localImageList != null && localImageList.size() > 0) {
			for (SearchItemLocalImage localImage : localImageList) {
				if (itemName.contains(localImage.getKeyword())) {
					item.getData().setLocalPhotoUrl(localImage.getImgId());
					break;
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YelloPageDataManager.closeInstance();
		mImageLoader.clearCache();
		LBSServiceGaode.deactivate();
		if (mAdapter != null) {
			mAdapter.recyleAllImage();
		}
    	unregisterReceiver(mNetworkReceiver);
        mSchduler.release();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgentUtil.onResume(this);
		mImageLoader.setExitTasksEarly(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgentUtil.onPause(this);
		mSchduler.stop();
		mImageLoader.setExitTasksEarly(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position <= mPageItemList.size()) {
			final YelloPageItem item = mPageItemList.get(position - 1);
			if (item != null) {
				Intent intent = new Intent(YellowPageSearchActivity.this,
						YellowPageShopDetailActivity.class);
				Bundle bundle = new Bundle();
				if (mItemImage != null) {
					setItemResId(item);
				}
				bundle.putSerializable("YelloPageItem", item);
				intent.putExtra("CategoryId", mCurrentCategoryId);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_layout) {
			finish();
		} else if (id == R.id.next_setp_layout) {
			Intent intent = new Intent(this, YellowPageMapActivity.class);
			if (!TextUtils.isEmpty(words)) {
				intent.putExtra("title", words);
			} else {
				intent.putExtra("title", category);
			}
			List<YelloPageItem> pageItems = new ArrayList<YelloPageItem>();
			// 将所有数据传入地图中
			// if (mPageItemList.size() >= 10) {
			// pageItems.addAll(mPageItemList.subList(0, 10));
			// } else {
			// pageItems.addAll(mPageItemList);
			// }
			pageItems.addAll(mPageItemList);
			intent.putExtra("result_key", (Serializable) pageItems);
			if (mItemImage != null) {
				intent.putExtra("defaultLogo", mItemImage.getDefaultImg());
			}
			startActivity(intent);
		} else if (id == R.id.network_exception_layout) {
			refreshRelocation();
		} else {
		}

	}

	@Override
	public void onLocationChanged(final String city, final double latitude,
	        final double longitude, long time) {
		LogUtil.d(TAG, "定位完成 city:"+city+" latitude:"+latitude+" longitude:"+longitude+" time:"+time);
        LBSServiceGaode.deactivate();

		mHandler.sendEmptyMessage(MSG_REFRESH_COMPLETE_ACTION);
		if (!TextUtils.isEmpty(city)) {
			if (NetUtil.isNetworkAvailable(YellowPageSearchActivity.this)) {
				
				Config.execute(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d(TAG, "createSolution");
                        mHandler.sendEmptyMessage(MSG_LOCATION_SUCCESS_CLEARDATA_ACTION);
                        LogUtil.d(TAG, "refreshData");
                        refreshData(latitude,longitude,city);
                        createSolution(latitude,longitude,city);
                    }
				    
				});
			} else {
				mHandler.sendEmptyMessage(MSG_NETWORK_EXCEPTION_ACTION);
			}
		} else {
			// disMissDialog();
			// Toast.makeText(this, R.string.putao_yellow_page_location_failed,
			// Toast.LENGTH_SHORT).show();
			// LBSServiceGaode.deactivate();
			onLocationFailed();
		}
	}

	private static final int MSG_LOCATION_SUCCESS_CLEARDATA_ACTION = 0x2001;

	private static final int MSG_LOCATION_FAILED_ACTION = 0x2002;

	private static final int MSG_REFRESH_COMPLETE_ACTION = 0x2003;

	private static final int MSG_NETWORK_EXCEPTION_ACTION = 0x2004;

	private Handler mHandler = new Handler() {
		@Override
        public void handleMessage(Message msg) {
		    if(YellowPageSearchActivity.this == null || YellowPageSearchActivity.this.isFinishing()){
		        return;
		    }
		    
			int what = msg.what;
			switch (what) {
			case MSG_LOCATION_SUCCESS_CLEARDATA_ACTION:
				// 定位成功，清空数据
				if (mPageItemList != null && mPageItemList.size() > 0) {
					// 下拉刷新，如果能取到数据，则清空之前所有数据
					mPageItemList.clear();
					mAdapter.setmPageItemList(mPageItemList);
					mAdapter.notifyDataSetChanged();
					mListView.setVisibility(View.INVISIBLE);
				}
				showDialog();
				break;
			case MSG_LOCATION_FAILED_ACTION:
				// 定位失败
				mListView.onRefreshComplete();
				disMissDialog();
				if (mPageItemList != null && mPageItemList.size() > 0) {
				    Toast.makeText(YellowPageSearchActivity.this,
	                        R.string.putao_yellow_page_location_failed,
	                        Toast.LENGTH_SHORT).show();
				}else{
				    mSearchContent.setVisibility(View.GONE);
                    mException.setVisibility(View.VISIBLE);
				}
				LBSServiceGaode.deactivate();
				break;
			case MSG_REFRESH_COMPLETE_ACTION:
				// 加载完成，隐藏下拉框
				mListView.onRefreshComplete();
				break;
			case MSG_NETWORK_EXCEPTION_ACTION:
				Toast.makeText(YellowPageSearchActivity.this, R.string.putao_no_net, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onLocationFailed() {
		LogUtil.d(TAG, "定位失败");
		mHandler.sendEmptyMessage(MSG_LOCATION_FAILED_ACTION);
	}

	@Override
	public void onLoadMore() {
		if( !NetUtil.isNetworkAvailable(this) ){
			mHandler.sendEmptyMessageDelayed(MSG_NETWORK_EXCEPTION_ACTION, 300);
    		mHasMore = false;
			mListView.onLoadMoreComplete(true);
    		return;
		}
		if (mHasMore) {
		    LogUtil.d(TAG, "onLoadMore");
		    mSchduler.schdule("2");
		} else {
			mSearchContent.setVisibility(View.VISIBLE);
			mException.setVisibility(View.GONE);
			mListView.onLoadMoreComplete(true);
		}

	}
	
	private void reLoadData(){
		initLoadDataState();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
			    LogUtil.d(TAG, "reLoadData to location");
				LBSServiceGaode.activate(YellowPageSearchActivity.this,
						YellowPageSearchActivity.this);
//			}
//		}).start();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		long nowTime = System.currentTimeMillis();
		if (nowTime - mLastRefreshTime < REFRESH_MIN_TIME_GAP) {
			// 避免用户不停的刷新导致不停的定位, 结束刷新
			mHandler.sendEmptyMessage(MSG_REFRESH_COMPLETE_ACTION);
			return;
		}
		mLastRefreshTime = nowTime;

		// 下拉刷新时 初始化所有状态
		reLoadData();
	}

	/**
	 * 初始化所有状态
	 */
	private void initLoadDataState() {
		mHasMore = false;
		showDianping = true;
		showSougou = true;
	}
    
    @Override
    public void onResult(Solution sol, Map<Integer, List<YelloPageItem> > itemMaps, List<YelloPageItem> itemList, boolean hasMore) {
        if(YellowPageSearchActivity.this == null || YellowPageSearchActivity.this.isFinishing()){
            return;
        }

        if(sol == null) {
            LogUtil.e(TAG, "onResult sol is null");
            return; 
        }

        if(itemList == null)
            itemList = new ArrayList<YelloPageItem>();
        
        LogUtil.d(TAG, "onResult sol.hasMore="+sol.isHasMore()+" hasMore="+hasMore+" size="+itemList.size()+" total_size="+mPageItemList.size());
        //disMissDialog();//delete by putao_lhq for BUG #1591
        if ((itemList == null || itemList.size() == 0) && !sol.isHasMore()
                && mPageItemList.size() == 0) {
        	disMissDialog();//add by putao_lhq for BUG #1591
            Toast.makeText(this, R.string.putao_search_empty_result1,
                    Toast.LENGTH_SHORT).show();
        } else {
        	/**
        	 * modify by zjh 2014-11-27 start
        	 * //重复数据删除
        	 */
        	//mPageItemList.addAll(itemList);
        	DuplicateRemoveRule.duplicateRemoved(mPageItemList, itemList); 
        	/**modify by zjh 2014-11-27 end */
        	
            mHasMore = sol.isHasMore();
            // modify by putao_lhq 2014年10月13日 for BUG #1591 start
            //mAdapter.setmPageItemList(mPageItemList);
            //mAdapter.notifyDataSetChanged();
            // modify by putao_lhq 2014年10月13日 for BUG #1591 end
            if (itemList.size() < 20 && sol.isHasMore()) {
                LogUtil.d(TAG, "onResult reschdule");
                mSchduler.schdule("3");
            } else {
                LogUtil.d(TAG, "onResult reschdule");
                // add by putao_lhq 2014年10月13日 for BUG #1591 start
                disMissDialog();
                mAdapter.setmPageItemList(mPageItemList);
                mAdapter.notifyDataSetChanged();
                mListView.setVisibility(View.VISIBLE);
                // add by putao_lhq 2014年10月13日 for BUG #1591 end
            }
        }
        
        //mListView.setVisibility(View.VISIBLE);// delete by putao_lhq 2014年10月13日 for BUG #1591
        
        if (mPageItemList.size() == 0) {
            findViewById(R.id.next_setp_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.next_setp_layout).setVisibility(View.VISIBLE);
        }
        mListView.onLoadMoreComplete(false);    
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
	
	@Override
	protected boolean needReset() {
	    return true;
	}
	
}
