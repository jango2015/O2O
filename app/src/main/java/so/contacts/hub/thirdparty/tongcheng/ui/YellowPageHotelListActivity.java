package so.contacts.hub.thirdparty.tongcheng.ui;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.adapter.HotelListAdapter;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.tongcheng.bean.TCRequestData;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Request_HotelList;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelList;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Common;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Http;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Request_DataFactory;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.Utils;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.CustomListView;
import so.contacts.hub.widget.CustomListView.OnLoadMoreListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

public class YellowPageHotelListActivity extends BaseRemindActivity implements OnClickListener, OnItemClickListener, 
				OnLoadMoreListener, TextWatcher, OnEditorActionListener {
	
	private static final String TAG = "YellowPageHotelListActivity";
	
	/** view start. */
	private EditText mEditText = null;
	private ImageView mClearSearchImgView = null;
	private TextView mSelectAreaTView = null;
	private TextView mSelectPriceTView = null;
	private TextView mSelectStarTView = null;
	private TextView mSelectSortTView = null;
	private CustomListView mListView = null;
	
	private RelativeLayout mNoDataLayout = null;
	private TextView mShowHintTView = null;
	/** view end. */
	
	/** data start. */
	private HotelListAdapter mAdapter = null;
	private DataLoader mDataLoader = null;
	private List<TC_HotelBean> mHotelList = new ArrayList<TC_HotelBean>();
	
	private String mCityId = null;
	private String mCityName = null;
	private String mComeDate = null;
	private String mLeaveDate = null;
	private String mClientIp = null;
	private double mLatitude = 0; // 定位的维度
	private double mLongitude = 0; // 定位的经度
	
	private int mSelectAreaIndex = 0;
	private int mSelectPriceIndex = 0;
	private int mSelectStarIndex = 0;
	private int mSelectSortIndex = 0;
	
	private String mKeyword = null;
	/** data end. */
	
	/** dialog data start. */
    
	/**
	 * [1]: 选择区域
	 * [2]: 选择价格
	 * [3]: 选择星级
	 * [4]: 选择排序
	 */
	private int mDialogType = 0;
	
	private static final int DIALOG_TYPE_AREA = 1;
	private static final int DIALOG_TYPE_PRICE = 2;
	private static final int DIALOG_TYPE_STAR = 3;
	private static final int DIALOG_TYPE_SORT = 4;

	private ArrayList<String> mHotelAreaList = new ArrayList<String>(); // 区域列表
	private ArrayList<Integer> mHotelAreaIdList = new ArrayList<Integer>();

	private String[] mHotelPriceList = null; // 价格列表

	private String[] mHotelPriceLevelList = null; // 实际请求 价格列表
	
	private String[] mHotelStarList = null; // 星级列表

	private String[] mHotelStarLevelList = null; // 实际请求 星级列表

	private String[] mHotelSortList = null; // 排序列表

	private int[] mHotelSortLevelList = null; // 实际请求 排序列表
	/** dialog data end. */
	
	/** tag data start. */
	private QueryDataTask mQueryDataTask = null;

	private CommonDialog mCommonDialog = null; // 弹出的选择框
	
    private static final int PAGE_DATA_SIZE = 20; // 每次请求的数据位20条

    private int mRequestPageNum = 0; // 每次请求的页码
    
    private boolean mLoadingMore = false; // 是否正在加载更多数据
    
    private boolean mHasMoreData = true; //是否有更多数据（上一次请求的数据小于PAGE_DATA_SIZE，则说明没有更多数据）
    
    private boolean mNeedReloadData = false; // 是否需要重新加载所有数据
	
	private static final int MSG_REFRESH_DATA_ACTION = 0x2001;

	private static final int MSG_SHOW_DIALOG_ACTION = 0x2002;
	
	private static final int MSG_DISMISS_DIALOG_ACTION = 0x2003;
	
	private static final int MSG_SHOW_NODATA_ACTION = 0x2004;
	
	private static final int MSG_SHOW_NODATA_NONET_ACTION = 0x2005;

    public static final int MSG_NETWORK_EXCEPTION_ACTION = 0x2006;
	/** tag data end. */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_hotellist);
		
		parseIntent();
		initView();
		initData();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgentUtil.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgentUtil.onPause(this);
	}
	
	private void parseIntent(){
		Intent intent = getIntent();
		if( intent ==  null ){
			return;
		}
		mCityId = intent.getStringExtra("CityId"); 	
		mCityName = intent.getStringExtra("CityName"); 	
		mComeDate = intent.getStringExtra("ComeDate");		
		mLeaveDate = intent.getStringExtra("LeaveDate");
		mClientIp = intent.getStringExtra("ClintIp");
		mLatitude = intent.getDoubleExtra("Latitude", 0);
		mLongitude = intent.getDoubleExtra("Longitude", 0);
		mSelectPriceIndex = intent.getIntExtra("PriceRangeIndex", 0);
		mSelectStarIndex = intent.getIntExtra("StarIndex", 0);
		mKeyword = intent.getStringExtra("HotWord");
		if( mLatitude != 0 && mLongitude != 0 ){
			// 有经纬度时才按照距离排序
			mSelectSortIndex = 5;
		}
	}
	
	private void initView(){
        findViewById(R.id.back_layout).setOnClickListener(this);
        
        mEditText = (EditText) findViewById(R.id.search_edit_text);
        mEditText.addTextChangedListener(this);
        mEditText.setOnEditorActionListener(this);
        mClearSearchImgView = (ImageView) findViewById(R.id.clear_search_content_btn);
        mClearSearchImgView.setOnClickListener(this);
        
        mSelectAreaTView = (TextView) findViewById(R.id.select_area_tview);
        mSelectPriceTView = (TextView) findViewById(R.id.select_price_tview);
        mSelectStarTView = (TextView) findViewById(R.id.select_star_tview);
        mSelectSortTView = (TextView) findViewById(R.id.select_sort_tview);
        mSelectAreaTView.setOnClickListener(this);
        mSelectStarTView.setOnClickListener(this);
        mSelectPriceTView.setOnClickListener(this);
        mSelectSortTView.setOnClickListener(this);
        
        mNoDataLayout = (RelativeLayout) findViewById(R.id.network_exception_layout);
        mNoDataLayout.setOnClickListener(this);
        mShowHintTView = (TextView) findViewById(R.id.exception_desc);
        
        mListView = (CustomListView) findViewById(R.id.search_list);
        mListView.setOnItemClickListener(this);
        mListView.setOnLoadListener(this);
        mListView.setCanLoadMore(true);
        mListView.setAutoLoadMore(true);
        
		mCommonDialog = CommonDialogFactory.getListCommonDialog(this);
		mCommonDialog.setListViewItemClickListener(mOnDialogItemClickListener);
		
	}
	
	private void initData(){
		mHotelAreaList.add(getString(R.string.putao_hotel_unlimited));// 默认的作为第一个
		mHotelAreaIdList.add(0);
		
		mHotelPriceList = getResources().getStringArray(R.array.putao_hotel_price);
		mHotelPriceLevelList = getResources().getStringArray(R.array.putao_hotel_price_level);
		mHotelStarList = getResources().getStringArray(R.array.putao_hotel_star);
		mHotelStarLevelList = getResources().getStringArray(R.array.putao_hotel_star_level);
		mHotelSortList = getResources().getStringArray(R.array.putao_hotel_sort);
		mHotelSortLevelList = getResources().getIntArray(R.array.putao_hotel_sort_level);
		
		mDataLoader = new ImageLoaderFactory(this).getYellowPageLoader(R.drawable.putao_a0114, 0);
		mAdapter = new HotelListAdapter(this, mHotelList, mDataLoader);
		mListView.setAdapter(mAdapter);
		
		// 初始化从酒店首页传过来的数据 start
		if( !TextUtils.isEmpty(mKeyword) ){
			mEditText.setText(mKeyword);
		}
		if( mSelectPriceIndex != 0 ){
			mSelectPriceTView.setText(mHotelPriceList[mSelectPriceIndex]);
		}
		if( mSelectStarIndex != 0 ){
			mSelectStarTView.setText(mHotelStarList[mSelectStarIndex]);
		}
		if( mSelectSortIndex != 0 ){
			mSelectSortTView.setText(mHotelSortList[mSelectSortIndex]);
		}
		// 初始化从酒店首页传过来的数据 end
		
		if( NetUtil.isNetworkAvailable(this) ){
			// 请求数据
			doQueryHotelListData(mRequestPageNum + 1, true);
		}else{
            mListView.setVisibility(View.GONE);
            mNoDataLayout.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 执行查询充值历史记录
	 * @param pageNum
	 */
	private void doQueryHotelListData(int pageNum, boolean needDialog){
		if( !mHasMoreData ){
			// 没有更多数据 或者 上一次请求数据不超过20条
			mListView.onLoadMoreComplete(true);
			return;
		}
		if( pageNum == mRequestPageNum && mLoadingMore){
			// 当前请求正在执行 
			mLoadingMore = false;
			mListView.onLoadMoreComplete(false);
			return;
		}
		if( needDialog ){
			mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
		}
		LogUtil.i(TAG, "doQueryChargeHistoryData pageNum: " + pageNum);
		mRequestPageNum = pageNum;
		if ( (mQueryDataTask != null && mQueryDataTask.getStatus() != AsyncTask.Status.RUNNING)
				|| mQueryDataTask == null ) {
			mQueryDataTask = new QueryDataTask();
			mQueryDataTask.execute();
        }
	}
	
	private class QueryDataTask extends AsyncTask<Void, Void, TC_Response_HotelList>{
		
		@Override
		protected TC_Response_HotelList doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			TC_Request_HotelList hotelRequestBody = new TC_Request_HotelList();
			hotelRequestBody.setCityId(mCityId);
			hotelRequestBody.setComeDate(mComeDate);
			hotelRequestBody.setLeaveDate(mLeaveDate);
			hotelRequestBody.setClientIp(mClientIp);
			hotelRequestBody.setLatitude(mLatitude);
			hotelRequestBody.setLongitude(mLongitude);
			hotelRequestBody.setBizSectionId(mHotelAreaIdList.get(mSelectAreaIndex));
			hotelRequestBody.setPriceRange(mHotelPriceLevelList[mSelectPriceIndex]); 
			hotelRequestBody.setStarRatedId(mHotelStarLevelList[mSelectStarIndex]);
			hotelRequestBody.setSortType(mHotelSortLevelList[mSelectSortIndex]);
			hotelRequestBody.setKeyword(mKeyword);
			hotelRequestBody.setPage(mRequestPageNum);
			hotelRequestBody.setPageSize(PAGE_DATA_SIZE);
			
			String requestBody = hotelRequestBody.getBody();
			String requestHead = TC_Request_DataFactory.getRequestHead("GetHotelList");
			String url = TC_Common.TC_URL_SEARCH_HOTEL;
			TCRequestData requestData = new TCRequestData(requestHead, requestBody);
			Object object = TC_Http.getDataByPost(url, requestData.getReqeustData(), TC_Response_HotelList.class);
			if( object == null ){
				return null;
			}
			return (TC_Response_HotelList)object;
		}
		
		@Override
		protected void onPostExecute(TC_Response_HotelList result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if( mLoadingMore ){
        		mListView.onLoadMoreComplete(false);
        		mLoadingMore = false;
        	}
        	mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
        	
        	if( result == null ){
        		// 无数据 
        		mHasMoreData = false;
            	mHandler.sendEmptyMessage(MSG_SHOW_NODATA_NONET_ACTION);
            	LogUtil.i(TAG, "query hotellist responsedata is null.");
            	mNeedReloadData = false;
        		return;
        	}
        	if( mNeedReloadData ){
            	mNeedReloadData = false;
            	mHotelList.clear();
            }
        	
        	List<TC_HotelBean> hotelList = result.getHotelList();
        	if( hotelList == null || hotelList.size() <= 0 ){
        		// 无数据
        		mHasMoreData = false;
            	mHandler.sendEmptyMessage(MSG_SHOW_NODATA_ACTION);
            	LogUtil.i(TAG, "query hotellist responsedata is empty.");
        		return;
        	}else{
        		// 有数据则添加进去
        		int hotelListSize = hotelList.size();
        		if( hotelListSize < PAGE_DATA_SIZE ){
        			// 服务器没有更多数据
        			mHasMoreData = false;
        		}else{
        			// 服务器可能还有更多数据
        			mHasMoreData = true;
        		}
        		mHotelList.addAll(hotelList);
            	LogUtil.i(TAG, "query hotellist responsedata size: " + hotelListSize);
            	
            	// 初始化 区域数据
            	for(int i = 0; i < hotelListSize; i++){
            		int bizSectionId = hotelList.get(i).getBizSectionId();
            		String bizSectionName = hotelList.get(i).getBizSectionName(); 
            		if( !TextUtils.isEmpty(bizSectionName) && !mHotelAreaList.contains(bizSectionName) ){
            			mHotelAreaList.add(bizSectionName);
            			mHotelAreaIdList.add(bizSectionId);
            		}
            	}
        	}
        	mAdapter.setData(mHotelList);
		}
		
	};
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch(what){
			case MSG_REFRESH_DATA_ACTION:
				if( mHotelList != null && mHotelList.size() > 0 ){
					mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
					mAdapter.setData(mHotelList);
				}
				break;
			case MSG_SHOW_DIALOG_ACTION:
				mListView.setVisibility(View.VISIBLE);
				mNoDataLayout.setVisibility(View.GONE);
				showLoadingDialog(false);
				break;
			case MSG_DISMISS_DIALOG_ACTION:
				dismissLoadingDialog();
				break;
			case MSG_SHOW_NODATA_ACTION:
				// 网络正常，请求无数据
				if( mHotelList.size() == 0 ){
					mListView.setVisibility(View.GONE);
					mNoDataLayout.setVisibility(View.VISIBLE);
					mShowHintTView.setText(R.string.putao_hotel_nomoredata);
				}else{
					Utils.showToast(YellowPageHotelListActivity.this, R.string.putao_hotel_nomoredata, false);
				}
				break;
			case MSG_SHOW_NODATA_NONET_ACTION:
				// 网络异常 导致 请求无数据
				if( mHotelList.size() == 0 ){
					mListView.setVisibility(View.GONE);
					mNoDataLayout.setVisibility(View.VISIBLE);
					mShowHintTView.setText(R.string.putao_netexception_hint);
				}else{
					Utils.showToast(YellowPageHotelListActivity.this, R.string.putao_netexception, false);
				}
				break;
			case MSG_NETWORK_EXCEPTION_ACTION:
				mHandler.removeMessages(MSG_NETWORK_EXCEPTION_ACTION);
				Utils.showToast(YellowPageHotelListActivity.this, R.string.putao_no_net, false);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		// 如果有更多的数据则进行加载
		if( NetUtil.isNetworkAvailable(this) ){
        	mLoadingMore = true;
			doQueryHotelListData(mRequestPageNum + 1, false);
		}else{
			mHandler.sendEmptyMessageDelayed(MSG_NETWORK_EXCEPTION_ACTION, 300);
			mListView.onLoadMoreComplete(true);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		// TODO Auto-generated method stub
		if( !NetUtil.isNetworkAvailable(this) ){ //网络异常
			Utils.showToast(this, R.string.putao_no_net, false);
			return;
		}
		TC_HotelBean hotelBean = null;
		if( position <= mHotelList.size() ){
			hotelBean = mHotelList.get(position - 1);
		}
		if( hotelBean == null ){
			return;
		}
		Intent intent = new Intent(this, YellowPageHotelDetailActivity.class);
		intent.putExtra("CityName", mCityName);
		intent.putExtra("HotelId", hotelBean.getHotelId());
		intent.putExtra("HotelImg", hotelBean.getImg());
		intent.putExtra("HotelName", hotelBean.getHotelName());
		intent.putExtra("HotelAddress", hotelBean.getAddress());
		intent.putExtra("Longitude", hotelBean.getLongitude());
		intent.putExtra("Latitude", hotelBean.getLatitude());
		intent.putExtra("HotelMarkNum", hotelBean.getMarkNum());
		intent.putExtra("StarRatedName", hotelBean.getStarRatedName());
		intent.putExtra("ComeDate", mComeDate);
		intent.putExtra("LeaveDate", mLeaveDate);
		
		startActivity(intent);
		
		//add xcx 2014-12-30 start 统计埋点
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_SEARCH_RESULT_LIST_ITEM_CLICK);
        //add xcx 2014-12-30 end 统计埋点
	}
	
	private OnItemClickListener mOnDialogItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if (mDialogType == DIALOG_TYPE_AREA) {
				if( position == 0 ){
					mSelectAreaTView.setText(R.string.putao_hotelbook_area);
					mSelectAreaIndex = 0;
				}else if( position <= mHotelAreaList.size() - 1 ){
					String area = mHotelAreaList.get(position);
					mSelectAreaTView.setText(area);
					mSelectAreaIndex = position;
				 }else{
					 return;
				 }
			} else if (mDialogType == DIALOG_TYPE_PRICE) {
				if( position == 0 ){
					mSelectPriceTView.setText(R.string.putao_hotelbook_price);
					mSelectPriceIndex = 0;
				}else if( position <= mHotelPriceList.length - 1 ){
					 mSelectPriceTView.setText(mHotelPriceList[position]);
					 mSelectPriceIndex = position;
				 }else{
					 return;
				 }
			} else if (mDialogType == DIALOG_TYPE_STAR) {
				if( position == 0 ){
					mSelectStarTView.setText(R.string.putao_hotelbook_star);
					mSelectStarIndex = 0;
				}else if( position <= mHotelStarList.length - 1 ){
					mSelectStarTView.setText(mHotelStarList[position]);
					mSelectStarIndex = position;
				 }else{
					 return;
				 }
			} else if (mDialogType == DIALOG_TYPE_SORT) {
				if( position == 0 ){
					mSelectSortTView.setText(R.string.putao_hotelbook_sort);
					mSelectSortIndex = 0;
				}else if( position <= mHotelSortList.length - 1 ){
					if (mLatitude == 0 && position == mHotelSortList.length - 1) {
						// 没有定位 且 选择的排序时按照“离我最近”排序，则不需要重新筛选
						Utils.showToast(YellowPageHotelListActivity.this, 
								R.string.putao_hotel_orderlist_location_failed_search_hint, false);
						if (mCommonDialog != null) {
							mCommonDialog.dismiss();
						}
						return;
					}else{
						mSelectSortTView.setText(mHotelSortList[position]);
						mSelectSortIndex = position;
					}
				 }else{
					 return;
				 }
			}
			if (mCommonDialog != null) {
				mCommonDialog.dismiss();
			}
			startReloadData();
		}
	};
	
	/**
	 * 重新加载所有数据
	 */
	private void startReloadData(){
		if( NetUtil.isNetworkAvailable(YellowPageHotelListActivity.this) ){
			mRequestPageNum = 0;
			mHasMoreData = true;
			mNeedReloadData = false;
			mHotelList.clear();
			mAdapter.setData(mHotelList);
			mListView.onManualComplete();
			doQueryHotelListData(mRequestPageNum + 1, true);
		}else{
			Utils.showToast(this, R.string.putao_no_net, false);
		}
	}
	
	/**
	 * 显示区域列表
	 */
	private void showAreaLayout(){
		if( mHotelAreaList.size() <= 1 ){
			// 无区域选择(默认为添加一条)
			Toast.makeText(this, R.string.putao_hotel_no_morearea, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotel_title_area));
			mCommonDialog.setSingleChoiceListViewDatas(mHotelAreaList);
			mCommonDialog.getListView().setItemChecked(mSelectAreaIndex, true);
		}
		mDialogType = DIALOG_TYPE_AREA;
		mCommonDialog.show();
	}
	
	/**
	 * 显示价格列表
	 */
	private void showPriceLayout(){
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotel_title_price));
			mCommonDialog.setSingleChoiceListViewDatas(mHotelPriceList);
			mCommonDialog.getListView().setItemChecked(mSelectPriceIndex, true);
		}
		mDialogType = DIALOG_TYPE_PRICE;
		mCommonDialog.show();
	}

	/**
	 * 显示星级
	 */
	private void showStarLayout(){
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotel_title_star));
			mCommonDialog.setSingleChoiceListViewDatas(mHotelStarList);
			mCommonDialog.getListView().setItemChecked(mSelectStarIndex, true);
		}
		mDialogType = DIALOG_TYPE_STAR;
		mCommonDialog.show();
	}
	
	/**
	 * 显示排序列表
	 */
	private void showSortLayout(){
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotel_title_sort));
			mCommonDialog.setSingleChoiceListViewDatas(mHotelSortList);
			mCommonDialog.getListView().setItemChecked(mSelectSortIndex, true);
		}
		mDialogType = DIALOG_TYPE_SORT;
		mCommonDialog.show();
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int viewId = view.getId();
		if( viewId == R.id.back_layout ){
			finish();
		}else if( viewId == R.id.clear_search_content_btn ){
			mEditText.getText().clear();
			mKeyword = null;
			startReloadData();
		}else if( viewId == R.id.select_area_tview ){
			showAreaLayout();
		}else if( viewId == R.id.select_price_tview ){
			showPriceLayout();
		}else if( viewId == R.id.select_star_tview ){
			showStarLayout();
		}else if( viewId == R.id.select_sort_tview ){
			showSortLayout();
		}else if( viewId == R.id.network_exception_layout ){
			// 无网络，点击重新刷新数据
			if ( NetUtil.isNetworkAvailable(this) ) {
			    mNoDataLayout.setVisibility(View.GONE);
			    mListView.setVisibility(View.VISIBLE);
			    mRequestPageNum = -1;
			    mHasMoreData = true;
			    doQueryHotelListData(0, true);
			} else {
				Toast.makeText(this, R.string.putao_no_net, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event != null && event.getAction() != KeyEvent.ACTION_UP) {
			return true;
		}
		if (actionId == EditorInfo.IME_ACTION_SEARCH
				|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
			mKeyword = v.getText().toString();
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(YellowPageHotelListActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			startReloadData();
			return true;
		}
		return false;
	}

	@Override
	public void afterTextChanged(Editable editable) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(editable)) {
			mClearSearchImgView.setVisibility(View.GONE);
		}else{
			mClearSearchImgView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mQueryDataTask != null){
			mQueryDataTask.cancel(true);
			mQueryDataTask = null;
    	}
	}

	@Override
	public String getServiceNameByUrl() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	@Override
	public String getServiceName() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	@Override
	public boolean needMatchExpandParam() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer remindCode() {
		// TODO Auto-generated method stub
		return mRemindCode;
	}

	@Override
	protected boolean needReset() {
	    return true;
	}
}
