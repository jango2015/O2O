
package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import so.contacts.hub.adapter.SearchHistoryAdapter;
import so.contacts.hub.adapter.SearchHistoryAdapter.onDeleteButtonClickListener;
import so.contacts.hub.adapter.SearchRecommendwordsAdapter;
import so.contacts.hub.adapter.YellowPageAdapter;
import so.contacts.hub.city.CityListDB;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.search.SearchResultListener;
import so.contacts.hub.search.SearchStrategyController;
import so.contacts.hub.search.SearchUtils;
import so.contacts.hub.search.Solution;
import so.contacts.hub.search.bean.SearchInfo;
import so.contacts.hub.search.rule.DuplicateRemoveRule;
import so.contacts.hub.search.rule.IMatchRule;
import so.contacts.hub.search.rule.MatchSimilarityRule;
import so.contacts.hub.search.rule.ResultSortedRule;
import so.contacts.hub.ui.web.YellowPageH5Activity;
import so.contacts.hub.ui.yellowpage.bean.PuTaoResultItem;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.AnimationUtil;
import so.contacts.hub.util.AnimationUtil.IAnimListener;
import so.contacts.hub.util.CommonValueUtil;
import so.contacts.hub.util.InputMethodUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.SearchHotwordUtil;
import so.contacts.hub.util.SearchRecommendwordUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.CustomListView;
import so.contacts.hub.widget.CustomListView.OnLoadMoreListener;
import so.contacts.hub.widget.ProgressDialog;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import so.putao.findplug.YelloPageDataManager;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YelloPageItemNumber;
import so.putao.findplug.YellowPageItemPutao;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

@SuppressWarnings("rawtypes")
public class YellowPageSearchNumberActivity extends BaseRemindActivity implements OnClickListener,
        TextWatcher, OnItemClickListener, LBSServiceListener,
        OnEditorActionListener, OnLoadMoreListener, SearchResultListener {

    public static final String TAG = "YellowPageSearchNumberActivity";

    /** 搜索界面布局 start */
    private CustomListView mListView;

    private RelativeLayout searchHistoryParentLayout;

    private ListView searchHistoryListView;
    
    private GridView mSearchRecommendwordListView; //运营推荐词集合

    private EditText mSearchEditText;

    //private TextView mCancelSearchBtn;//putao_lhq delete for coolui6.0

    private ImageView mClearSearchBtn;

    private TextView mPosition;

    private LinearLayout mEmptyView;

    private ProgressDialog mProgressDialog;

    private LinearLayout mSearchHeadLayout = null;//putao_lhq modify for coolui6.0
    /** 搜索界面布局 end */
    
    /** 搜索操作相关数据 start */
    private List<YelloPageItem> mPageItemList;
    
    private Map<Integer, List<YelloPageItem>> mPageItemMap = new HashMap<Integer, List<YelloPageItem>>();

    private String words;

    private boolean mHasMore;
    
    private boolean mHasLoadMore = false;  //是否已经加载过更多
    /** 搜索操作相关数据 end */
    
    private YellowPageAdapter mAdapter = null;

    private DataLoader mImageLoader = null;

    private SearchStrategyController mSearchController = null;

    private SharedPreferences mSharedPreferences = null;

    public static final int HISTORY_COUNT = 20;

    private ArrayList<String> historyWordsList = new ArrayList<String>();

    private SearchHistoryAdapter searchHistoryAdapter;
    private SearchRecommendwordsAdapter mSearchRecommendwordsAdapter ;
    
    private double location_latitude;			//用户当前所处 纬度
    private double location_longtitude;			//用户当前所处 经度
    private String location_city;				//用户当前定位到的 所处 城市
    boolean isNeedShowCityChangeDialog = true;	//是否需要提示用户切换城市

    private String mSelectedCity = "";			//用户选择的城市
    private double mSelectedLocation_latitude;	//用户选择的城市所处纬度
    private double mSelectedLocation_longtitude;//用户选择的城市所处经度
    private String last_search_city = "";//记录上一次搜索的城市
    
    // 网络状态变化action
    private static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    
    // 标识定位状态
    private int mLocationState = 0;
    
    // 定位状态：正在定位
    private static final int LOCATION_STATE_LOCATING = 1;

    // 定位状态：定位失败
    private static final int LOCATION_STATE_FAILED = 2;

    // 定位状态：定位成功，显示所在城市
    private static final int LOCATION_STATE_SUCCESS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_yellow_page_search_number);
        YelloPageDataManager.createInstance(this.getApplicationContext());
        mImageLoader = new ImageLoaderFactory(this).getDefaultYellowPageLoader();
        mSharedPreferences = getSharedPreferences(ConstantsParameter.SHARED_PREFS_YELLOW_PAGE,
                Context.MODE_MULTI_PROCESS);
        
        initViews();

        mSelectedCity = YellowUtil.selectedCity(this);
        mPageItemList = new ArrayList<YelloPageItem>();
        mAdapter = new YellowPageAdapter(this, mPageItemList, mImageLoader, null);
        mListView.setAdapter(mAdapter);
        
        MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_NUMBER);
        registerReceiver(mNetworkReceiver, new IntentFilter(CONNECTIVITY_CHANGE_ACTION));
        
        //尝试自动搜索
        mhandelr.sendEmptyMessage(MSG_TRY_AUTO_SEARCH_ACTION);

    }

    @SuppressLint("NewApi")
    private void initViews() {
	    //putao_lhq delete for coolui6.0 start
/*        TextView title = (TextView)findViewById(R.id.title);
        title.setText(R.string.putao_yellow_page_search);*/
       /* ImageView locImgView = (ImageView)findViewById(R.id.next_step_img);
        locImgView.setVisibility(View.VISIBLE);
        locImgView.setImageResource(R.drawable.putao_icon_marker_white);*/
		//putao_lhq delete for coolui6.0 end

        mSearchEditText = (EditText)findViewById(R.id.search_edit_text);
        // 轮训显示 搜索关键字
        String showText = SearchHotwordUtil.getInstance().getHotword();
        if( !TextUtils.isEmpty(showText) ){
        	mSearchEditText.setHint(showText);
        }
        mSearchEditText.setOnEditorActionListener(this);
        mSearchEditText.addTextChangedListener(this);
        mSearchEditText.setOnClickListener(this);
        mSearchEditText.requestFocus();
        /**
         *delete code
         *modify by putao_lhq for coolui6.0
         *@start 
        mCancelSearchBtn = (TextView)findViewById(R.id.cancel_search_btn);
        @end*/
        mClearSearchBtn = (ImageView)findViewById(R.id.clear_search_content_btn);
        mClearSearchBtn.setOnClickListener(this);
        mListView = (CustomListView)findViewById(R.id.search_list);
        mListView.setVisibility(View.GONE);
        mEmptyView = (LinearLayout)findViewById(R.id.empty_view);
        mListView.setOnItemClickListener(this);
        mListView.setOnLoadListener(this);
        mListView.setCanLoadMore(true);
        mListView.setAutoLoadMore(true);

        mSearchHeadLayout = (LinearLayout)findViewById(R.id.search_head_layout);//putao_lhq modify for coolui6.0
        searchHistoryParentLayout = (RelativeLayout)findViewById(R.id.search_history_parent);
        searchHistoryListView = (ListView)findViewById(R.id.search_history_list);
        searchHistoryListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(YellowPageSearchNumberActivity.this
                                    .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                }
                return false;
            }
        });
        mSearchRecommendwordListView  = (GridView)findViewById(R.id.recommond_word_list);
        mSearchRecommendwordListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(YellowPageSearchNumberActivity.this
                                    .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                }
                return false;
            }
        });
        mSearchRecommendwordsAdapter = new SearchRecommendwordsAdapter(this, SearchRecommendwordUtil.getInstance().getRecommendwordList());
        mSearchRecommendwordListView.setAdapter(mSearchRecommendwordsAdapter);
        mSearchRecommendwordListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	// add by putao_lhq 2014年10月22日 for UM start
            	MobclickAgentUtil.onEvent(YellowPageSearchNumberActivity.this, 
            			UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_HOT_WORDS);
            	// add by putao_lhq 2014年10月22日 for UM end
                if(position == 0){
                	return;
                }
                words = (String)parent.getItemAtPosition(position);
                if( TextUtils.isEmpty(words) ){
                	return;
                }
                mSearchRecommendwordListView.setVisibility(View.GONE);
                searchHistoryParentLayout.setVisibility(View.GONE);
                
                mSearchEditText.setText(words);
                mSearchEditText.setSelection(words.length());
                startSearch(false);
			}
        });
        
        
        searchHistoryAdapter = new SearchHistoryAdapter(this, historyWordsList);
        searchHistoryAdapter.setOnDeleteButtonClickListener(new onDeleteButtonClickListener() {
            @Override
            public void onDeleteButtonClicked(int position, String words) {
                if (!TextUtils.isEmpty(words)) {
                    // mSearchEditText.setText(words);
                    deleteSearchHistoryByWords(words);
                    loadSearchHistory();
                }
            }
        });
        searchHistoryListView.setAdapter(searchHistoryAdapter);
        searchHistoryListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Object object = view.getTag();
                if (null == object) {
                    return;
                }
                words = (String)object;
                if( TextUtils.isEmpty(words) ){
                	return;
                }
                MobclickAgentUtil.onEvent(YellowPageSearchNumberActivity.this, 
                		UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_HISTORY);
                mSearchRecommendwordListView.setVisibility(View.GONE);
                searchHistoryParentLayout.setVisibility(View.GONE);
                
                mSearchEditText.setText(words);
                mSearchEditText.setSelection(words.length());
                startSearch(true);

            }
        });

        mPosition = (TextView)findViewById(R.id.city_btn);
        findViewById(R.id.back_layout).setOnClickListener(this);
        // findViewById(R.id.next_setp_layout).setOnClickListener(this);
        
        //modify start xcx 修改城市选择点击区域范围  2014-12-23
        mSearchHeadLayout.setOnClickListener(this);
        findViewById(R.id.city_select_area).setOnClickListener(this);
        //modify end xcx 修改城市选择点击区域范围  2014-12-23
        
        //mCancelSearchBtn.setOnClickListener(this);//putao_lhq delete for coolui6.0
        
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
			//putao_lhq delete for coolui6.0 start
                /*mCancelSearchBtn.setEnabled(true);
                mCancelSearchBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(
                        R.color.putao_pt_deep_gray)));*/
						//putao_lhq delete for coolui6.0 end
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int touchY = (int)event.getRawY();
            int searchLayoutBottom = mSearchHeadLayout.getBottom();
            if (touchY > searchLayoutBottom) {
                if (searchHistoryParentLayout.getVisibility() == View.VISIBLE) {
                    showSearchHistoryLayout(false);
                }else if(mSearchRecommendwordListView.getVisibility() == View.VISIBLE ){//add ljq 2014-10-10 增加推荐词消失
                    showSearchRecommendwordsLayout(false);
                }
                
                doQuitActivity(searchLayoutBottom);
                
            }
        }
        return super.onTouchEvent(event);
    }

    //add ljq 2014-10-10
    private void showSearchRecommendwordsLayout(final boolean isShow) {
        int height = mSearchRecommendwordListView.getHeight();
        if (height == 0) {
            mSearchRecommendwordListView.setVisibility(View.GONE);
            return;
        }
        int startY = 0;
        int endY = 0;
        if( isShow ){
            startY = 0 - height;
        }else{
            endY = 0 - height;
        }
        AnimationUtil.translateAnim(mSearchRecommendwordListView, true, startY, endY, 300, new IAnimListener() {

            @Override
            public void onAnimationStart() {
                // TODO Auto-generated method stub
                if( isShow ){
                    mSearchRecommendwordListView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd() {
                // TODO Auto-generated method stub
                if( !isShow ){
                    mSearchRecommendwordListView.setVisibility(View.GONE);
                }
                
            }
        });
    }
    
    private void showSearchHistoryLayout(final boolean isShow) {
        int height = searchHistoryListView.getHeight();
        if (height == 0) {
            searchHistoryParentLayout.setVisibility(View.GONE);
            return;
        }
        int startY = 0;
        int endY = 0;
        if( isShow ){
            startY = 0 - height;
        }else{
            endY = 0 - height;
        }
        AnimationUtil.translateAnim(searchHistoryListView, true, startY, endY, 300, new IAnimListener() {

            @Override
            public void onAnimationStart() {
                // TODO Auto-generated method stub
            	if( isShow ){
            		searchHistoryParentLayout.setVisibility(View.VISIBLE);
            	}
            }

            @Override
            public void onAnimationEnd() {
                // TODO Auto-generated method stub
            	if( !isShow ){
            		searchHistoryParentLayout.setVisibility(View.GONE);
            	}
            	
            }
        });
    }

    private void doQuitActivity(int searchLayoutBottom) {
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(YellowPageSearchNumberActivity.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        
        finishSelf();
    }

    /**
     * 初始化搜索定位数据
     */
    private void initSearchLoctionData() {
    	if( TextUtils.isEmpty(mSelectedCity) || isNeedShowCityChangeDialog){//没有选择其他城市 也没有定位成功过
            if ( NetUtil.isNetworkAvailable(this) ) {
            	showLocationText(LOCATION_STATE_LOCATING, "");
            	new Thread(new Runnable() {
            		@Override
            		public void run() {
            			LBSServiceGaode.process_activate(YellowPageSearchNumberActivity.this,YellowPageSearchNumberActivity.this);
            		}
            	}).start();
            }else{
            	mhandelr.sendEmptyMessageDelayed(MSG_NETWORK_EXCEPTION_ACTION, 300);
            	if( TextUtils.isEmpty(mSelectedCity) ){
            		showLocationText(LOCATION_STATE_FAILED, "");
            	}else{
            		Message msg = mhandelr.obtainMessage();
                    msg.what = MSG_LOCATION_SUCCESS_ACTION;
                    msg.obj = mSelectedCity;
                    mhandelr.sendMessage(msg);
            	}
            }
        }else{
            Message msg = mhandelr.obtainMessage();
            msg.what = MSG_LOCATION_SUCCESS_ACTION;
            msg.obj = mSelectedCity;
            mhandelr.sendMessage(msg);
        }
    }
    
    private void doSearchSolution() {
		int entry = 0;
		if(!TextUtils.isEmpty(words) && (!TextUtils.isEmpty(location_city) || !TextUtils.isEmpty(mSelectedCity))){
			clearAllDataAndRefreshLayout();
            mListView.setVisibility(View.VISIBLE);
            mListView.setFooterViewVisibility(View.GONE);
            //开始搜索
            showProgress();
            
			if(!TextUtils.isEmpty(words) && YellowUtil.isNumeric(words)){
				entry = SearchUtils.SEARCH_ENTRY_NUMBER;  // 号码搜索入口
			} else {
				entry = SearchUtils.SEARCH_ENTRY_KEYWORD;  // 关键字搜索入口
			}
			
			SearchInfo defSearchInfo = new SearchInfo();
			defSearchInfo.setWords(words);
			defSearchInfo.setEntry_type(entry);
			
			if( TextUtils.isEmpty(mSelectedCity) ){//使用定位信息 进行搜索
				mSelectedCity = location_city;
				mSelectedLocation_latitude = location_latitude;
				mSelectedLocation_longtitude = location_longtitude;
			}
			defSearchInfo.setCity(mSelectedCity);
			defSearchInfo.setLatitude(mSelectedLocation_latitude);
			defSearchInfo.setLongitude(mSelectedLocation_longtitude);
			last_search_city = mSelectedCity;
			
			IMatchRule RuleImpl = new MatchSimilarityRule();
			if( mSearchController != null ){
				mSearchController.release();
			}
			mSearchController = new SearchStrategyController(this);
			mSearchController.setSearchInfo(defSearchInfo, mhandelr, RuleImpl, this, SearchUtils.isUseNetSearchStrategy());
			mSearchController.search();
		}
	}
    
    private void showLocationText(int locationState, String city){
        mLocationState = locationState;
        switch(locationState){
        case LOCATION_STATE_LOCATING:
            mPosition.setText(R.string.putao_location_text);//modify by putao_lhq
            break;
        case LOCATION_STATE_FAILED:
            mPosition.setText(R.string.putao_yellow_page_location_failed);
            break;
        case LOCATION_STATE_SUCCESS:
            mPosition.setText(city);
            break;
        default:
            break;
        }
    }

    private void showProgress() {
        if (mProgressDialog != null && !mProgressDialog.isShowing() && !isFinishing()) {
            mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            mEmptyView.setVisibility(View.GONE);
        }
    }
    
    /**
     * 关闭该页面
     * 注：关闭之前，隐藏输入法
     * add by zjh 2014-11-18
     */
    private void finishSelf(){
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(YellowPageSearchNumberActivity.this
                                    .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
		finish();
		overridePendingTransition(R.anim.putao_down_in, R.anim.putao_down_out);
    }
    
    /**
     * 开始搜索
     */
    private void startSearch(boolean fromHistory){
    	if( TextUtils.isEmpty(words) ){
    		return;
    	}
		((InputMethodManager) mSearchEditText.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				mSearchEditText.getApplicationWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
    	
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("search_key", words);
		MobclickAgentUtil.onEvent(this, "hot_words", map);
		
		if( fromHistory ){
			MobclickAgentUtil.onEvent(YellowPageSearchNumberActivity.this, 
					UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_HISTORY);
		}else{
			MobclickAgentUtil.onEvent(YellowPageSearchNumberActivity.this,
					UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH);
		}

		saveSearchHistory(words);
		initSearchLoctionData();
		/**
		 * delete code
		 * modify by putao_lhq
		 * coolui6.0
		 * mCancelSearchBtn.setEnabled(false);
		mCancelSearchBtn.setTextColor(ColorStateList.valueOf(getResources()
				.getColor(R.color.putao_pt_gray)));*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
		if (id == R.id.back_layout) {
			finishSelf();
		} else if (id == R.id.city_select_area) {
		    Intent intent = new Intent(this, YellowPageCitySelectActivity.class);
		    intent.putExtra(YellowPageCitySelectActivity.ISCHANGE_SEARCHNUMCITY_KEY, true);
	        intent.putExtra("source_type", CityListDB.CITY_SOURCE_TYPE_GAODE);
			startActivityForResult(intent, 0);
		} else if (id == R.id.cancel_search_btn) {
			finishSelf();
		} else if (id == R.id.clear_search_content_btn) {
			mSearchEditText.getText().clear();
			words = "";
			//add ljq 2014-09-29 BUG #1308 start
			//查号，输入关键字，点击收索后，清空输入框中的关键字，然后切换城市，会再次使用已清空的关键字进行查找；
			if( mSearchController != null ){
				mSearchController.release();
			}
			//add ljq 2014-09-29 BUG #1308 finish
			if (mPageItemList == null || mPageItemList.size() == 0) {
				mEmptyView.setVisibility(View.GONE);
			}
			clearAllDataAndRefreshLayout();
			loadSearchHistory();
			loadSearchRecommendWord();
		} else if (id == R.id.search_edit_text) {
			if( mSearchRecommendwordListView.getVisibility() != View.VISIBLE ){
			    showSearchRecommendwordsLayout(true);
			}
			if( searchHistoryParentLayout.getVisibility() != View.VISIBLE ){
				showSearchHistoryLayout(true);
			}
		} else {
		}
    }
    
    /**
     * 清空数据并刷新界面
     */
    private void clearAllDataAndRefreshLayout(){
    	mHasMore = true;
    	mHasLoadMore = false;
        mPageItemMap.clear();
        if( mPageItemList != null && mAdapter != null ){
        	mPageItemList.clear();
        	mAdapter.setmPageItemList(mPageItemList);
        	mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( mSearchController != null ){
        	mSearchController.release();
        }
        
        LBSServiceGaode.deactivate();
        mSearchEditText.getText().clear();
        mSearchEditText.clearFocus();
        YelloPageDataManager.closeInstance();
        mImageLoader.clearCache();
        disMissDialog();
    	unregisterReceiver(mNetworkReceiver);
    }

    // 友盟统计：进入时间
    private long startTime = 0L;

    @Override
    protected void onResume() {
        MobclickAgentUtil.onResume(this);
        startTime = System.currentTimeMillis();
//        if (LBSServiceGaode.hasPreInfo() && mPosition != null && TextUtils.isEmpty(mSelectedCity)) {
//            mPosition.setText(LBSServiceGaode.getPreCity());
//        }
        super.onResume();

        loadSearchHistory();
        loadSearchRecommendWord();
        // searchHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgentUtil.onPause(this);
        if( mSearchController != null ){
        	mSearchController.release();
        }
        try {
            int time = ((int)((System.currentTimeMillis() - startTime) / 1000));
            Map<String, String> map_value = new HashMap<String, String>();
            map_value.put("type", this.getClass().getName());
//            com.putao.analytics.MobclickAgentUtil.onEventValue(this, UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_0,
//                    map_value, time);
            MobclickAgentUtil.onEventValue(this, UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_0,
                    map_value, time);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position <= mPageItemList.size()) {
            final YelloPageItem<?> item = mPageItemList.get(position - 1);
            if( item == null ){
            	return;
            }
            Intent intent = null;
            if (item instanceof YelloPageItemNumber) {
                // 号码数据
            }else if( item instanceof YellowPageItemPutao ){
                // 葡萄静态数据
                PuTaoResultItem putaoItem = ((YellowPageItemPutao)item).getData();
                int sourceType = putaoItem.getSource_type();
                if( sourceType == PuTaoResultItem.SOURCE_TYPE_DETAIL ){
                    // 进入葡萄静态数据 - 详情
                    String targetActivityName = putaoItem.getIntent_activity();
                    if(!TextUtils.isEmpty(targetActivityName)){
                        Class<?> cls = null;
                        try {
                            cls = Class.forName(targetActivityName);
                            if (YellowPageH5Activity.class.isAssignableFrom(cls)) {
                                intent = new Intent(this, YellowPageJumpH5Activity.class);
                                intent.putExtra("targetActivityName", targetActivityName);
                            }else{
                                intent = new Intent(this, cls);
                            }
                        } catch (ClassNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }else{
                        intent = new Intent(YellowPageSearchNumberActivity.this,
                                YellowPageShopDetailActivity.class);
                    }
                	intent.putExtra("YelloPageItem", item);
                    intent.putExtra("ItemId", putaoItem.getItemId());
                    intent.putExtra("RemindCode", putaoItem.getRemind_code());
                    intent.putExtra("url", putaoItem.getWebsite());
                    intent.putExtra("title", putaoItem.getTitle());
            	}else if( sourceType == PuTaoResultItem.SOURCE_TYPE_SERVER ){
            		// 进入葡萄静态数据 - 应用服务
            		String targetActivityName = putaoItem.getIntent_activity();
            		if( TextUtils.isEmpty(targetActivityName) ){
            			return;
            		}
            		Class<?> cls = null;
					try {
						cls = Class.forName(targetActivityName);
						if (YellowPageH5Activity.class.isAssignableFrom(cls)) {
							intent = new Intent(this, YellowPageJumpH5Activity.class);
							intent.putExtra("targetActivityName", targetActivityName);
						}else{
							intent = new Intent(this, cls);
						}
						YellowParams params = new YellowParams();
						params.setCategory_id(putaoItem.getCategory_id());
			            params.setCategory_name(putaoItem.getName());
			            params.setRemindCode(putaoItem.getRemind_code());
			            params.setTitle(putaoItem.getName());
			            params.setUrl(putaoItem.getIntent_url());
			            params.setEntry_type(YellowParams.ENTRY_TYPE_SEARCH_PAGE);
			            intent.putExtra(YellowUtil.TargetIntentParams, params);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}else if( sourceType == PuTaoResultItem.SOURCE_TYPE_CARD ){
            		// 进入葡萄静态数据 - 卡片
            	}
            }else{
            	intent = new Intent(YellowPageSearchNumberActivity.this,
            			YellowPageShopDetailActivity.class);
            	Bundle bundle = new Bundle();
            	bundle.putSerializable("YelloPageItem", item);
            	
            	// 传入查号类别id
            	intent.putExtra("CategoryId", 0L);
            	intent.putExtras(bundle);
            }
            if( intent != null ){
            	startActivity(intent);
            }
        }
    }

    private void showDialog() {
        final CommonDialog dialog = CommonDialogFactory.getOkCancelCommonLinearLayoutDialog(this);
        dialog.getTitleTextView().setText(R.string.putao_yellow_page_position_change);
        String msg = getString(R.string.putao_yellow_page_position_change_msg,
                LBSServiceGaode.getPreCity());
        dialog.getMessageTextView().setText(msg);
        dialog.getCancelButton().setText(R.string.putao_cancel);
        dialog.setCancelButtonClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isNeedShowCityChangeDialog = false;
                showLocationText(LOCATION_STATE_SUCCESS, mSelectedCity);
                dialog.dismiss();
                mhandelr.sendEmptyMessage(MSG_TO_SEARCH);
                
                /*LogUtil.d(TAG, "refreshData 1");
                refreshData();
                LogUtil.d(TAG, "createSolution 1");
                createSolution();*/
            }
        });

        dialog.getOkButton().setText(R.string.putao_yellow_page_change_position);
        dialog.setOkButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // isPreValid = true;
                showLocationText(LOCATION_STATE_SUCCESS, LBSServiceGaode.getPreCity());
                YellowUtil.saveCity(YellowPageSearchNumberActivity.this, "");
                mSelectedCity = "";
                dialog.dismiss();
                
                mhandelr.sendEmptyMessage(MSG_TO_SEARCH);
                
                /*LogUtil.d(TAG, "refreshData 2");
                refreshData();
                LogUtil.d(TAG, "createSolution 2");
                createSolution();*/
            }
        });
        dialog.show();
    }

    private void disMissDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing() && !isFinishing()) {
            mProgressDialog.dismiss();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            mSelectedCity = data.getStringExtra("cityName");
            
            /*
             * 判断用户选择的和 上次搜索的城市是不是同一个城市
             * add by hyl 2014-8-22
             */
            boolean isNeedLoad = false;
            if( !last_search_city.equals(mSelectedCity) ){
            	mSelectedLocation_latitude = 0;
            	mSelectedLocation_longtitude = 0;
                isNeedLoad = true;
            }
            //add by hyl 2014-8-22 end
            
            showLocationText(LOCATION_STATE_SUCCESS, mSelectedCity);
			//putao_lhq delete for coolui6.0 start
            /*mCancelSearchBtn.setEnabled(true);
            mCancelSearchBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.putao_pt_deep_gray)));*/
			//putao_lhq delete for coolui6.0 end
//            if (LBSServiceGaode.hasPreInfo() && mSelectedCity.equals(LBSServiceGaode.getPreCity())) {
//                YellowUtil.saveCity(SearchNumberActivity.this, "");
//                mSelectedCity = "";
//            }
            if (!TextUtils.isEmpty(location_city) && mSelectedCity.equals(location_city)) {
                YellowUtil.saveCity(YellowPageSearchNumberActivity.this, "");
                mSelectedCity = "";
            }else{
                isNeedShowCityChangeDialog = false;
            }
            
            /*
             * 如果当前选择的城市和 上次搜索的城市不是同一个城市 则重新开始搜索
             * add by hyl 2014-8-22
             */
            if(isNeedLoad){
                initSearchLoctionData();
            }
            //add by hyl 2014-8-22 end
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s)) {
            if (mPageItemList != null && mPageItemList.size() > 0 && mAdapter != null) {
            	clearAllDataAndRefreshLayout();
                mListView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            /**
             * delete code
             * modify by putao_lhq
             * for cooui6.0
             * @start 
            mCancelSearchBtn.setText(R.string.putao_cancel);
            mCancelSearchBtn.setEnabled(true);
            mCancelSearchBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(
                    R.color.putao_pt_deep_gray)));
             @end*/
            mClearSearchBtn.setVisibility(View.GONE);
            loadSearchHistory();
        } else {
            /**
             * delete code
             * modify by putao_lhq for coolui6.0
             * @start 
            mCancelSearchBtn.setText(R.string.putao_yellow_page_search_btn);
            @end*/
            mClearSearchBtn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLocationChanged(String city, double latitude, double longitude, long time) {
        if ( !TextUtils.isEmpty(city) ) {
            LBSServiceGaode.deactivate();
            if (city.endsWith(CommonValueUtil.getInstance().getCityData())) {
                city = city.substring(0, city.length() - 1);
            }
            location_latitude = latitude;
            location_longtitude = longitude;
            location_city = city;
            
            if( location_city.equals(mSelectedCity) ){
            	// 如果定位城市 与 当前城市一直，则不需要提示
            	mSelectedCity = "";
            	isNeedShowCityChangeDialog = false;
            }
            
            Message msg = mhandelr.obtainMessage();
            msg.what = MSG_LOCATION_SUCCESS_ACTION;
            msg.obj = city;
            mhandelr.sendMessage(msg);
            
        } else {
            onLocationFailed();
        }
    }

    @Override
    public void onLocationFailed() {
        disMissDialog();
        LBSServiceGaode.deactivate();        
        
        /**
         * 偶现toast显示crash问题,将toast显示移到handler中显示
         * modify by cj at 2014/10/08 start
         */
        mhandelr.sendEmptyMessage(MSG_LOCATION_FAILED_ACTION);
        // modify by cj at 2014/10/08 end
    }
    
    
    private final int MSG_LOCATION_SUCCESS_ACTION = 0x2001;

    private final int MSG_NETWORK_EXCEPTION_ACTION = 0x2002;
    
    private final int MSG_LOCATION_FAILED_ACTION = 0x2003;
    
    //add ljq 2014-10-10 尝试去解析然后自动搜索
    private final int MSG_TRY_AUTO_SEARCH_ACTION = 0x2004;
    
    private final int MSG_TO_SEARCH = 0x2005;

    private final int MSG_COMPUTE_AND_SHOW_DATA_SEARCH = 0x2006;

    private final int MSG_NO_MORE_DATA_ACTION = 0x2007;

    private final int MSG_SEARCH_BTN_ENABLE_ACTION = 0x2008;

    private final int MSG_STOP_SEARCH_ACTION = 0x2009;

    private final int MSG_NOT_REFRESH_AND_HAS_MORE_ACTION = 0x2010;

    private final int MSG_NOT_REFRESH_AND_NO_MORE_ACTION = 0x2011;
    
    @SuppressLint("NewApi")
    private Handler mhandelr = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOCATION_SUCCESS_ACTION://定位成功，开始处理搜索
                    String city = (String)msg.obj;
                    if ( TextUtils.isEmpty(mSelectedCity) ) {
                        showLocationText(LOCATION_STATE_SUCCESS, city);
                        LogUtil.d(TAG, "doSearchSolution 3");
                        doSearchSolution();
                    } else if ( !mSelectedCity.equals(city) ) {
                        // 如果选择切换 isPreValid = true;
                        // 弹框
                        if( isNeedShowCityChangeDialog ){
                            showDialog();
                        }else{
                            showLocationText(LOCATION_STATE_SUCCESS, mSelectedCity);
                            LogUtil.d(TAG, "doSearchSolution 4");
                            doSearchSolution();
                        }
                    } else{
                        showLocationText(LOCATION_STATE_SUCCESS, city);
                        LogUtil.d(TAG, "doSearchSolution 5");
                        doSearchSolution();

                    }
					//putao_lhq delete for coolui6.0 start
                    //mCancelSearchBtn.setEnabled(true);
                    //mCancelSearchBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.putao_pt_deep_gray)));
                    //putao_lhq delete for coolui6.0 end
					break;
                case MSG_NETWORK_EXCEPTION_ACTION:
                	mhandelr.removeMessages(MSG_NETWORK_EXCEPTION_ACTION);
                	Toast.makeText(YellowPageSearchNumberActivity.this, R.string.putao_no_net, Toast.LENGTH_SHORT).show();
                	break;
                
                case MSG_LOCATION_FAILED_ACTION:
                    if( mLocationState == LOCATION_STATE_LOCATING ){
                        // 如果页面显示的是“正在定位中..."，则此时toast "正在定位，请稍后再试"
                        Toast.makeText(YellowPageSearchNumberActivity.this, R.string.putao_yellow_page_locating_try, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(YellowPageSearchNumberActivity.this, R.string.putao_yellow_page_location_failed, Toast.LENGTH_SHORT).show();
                    }
                	break;
                case MSG_TRY_AUTO_SEARCH_ACTION:
                    //解析YellowParams words参数 现在只用到了words参数
                    if (getIntent() != null) {
                        YellowParams keys = (YellowParams) getIntent()
                                .getSerializableExtra(YellowUtil.TargetIntentParams);
                        if (keys != null) {
                            if (!TextUtils.isEmpty(keys.getWords())) {
                            	//add by ffh 2014-10-19 #1589
                            	InputMethodUtil.hideInputMethod(YellowPageSearchNumberActivity.this);
                            	//end
                                words = keys.getWords();
                                mSearchEditText.setText(words);
                                mSearchEditText.setSelection(words.length());
                                //return;
                            }
                        }
                    }
                    initSearchLoctionData();
                    break;
                case MSG_TO_SEARCH:
                    LogUtil.d(TAG, "doSearchSolution 1");
                    doSearchSolution();
                    break;
                case MSG_COMPUTE_AND_SHOW_DATA_SEARCH:
                	 disMissDialog();
                	 mEmptyView.setVisibility(View.GONE);
                	 mListView.setVisibility(View.VISIBLE);
                	 searchHistoryParentLayout.setVisibility(View.GONE);
                	 mAdapter.setmPageItemList(mPageItemList);
                	 mListView.onLoadMoreComplete(false);
                	break;
                case MSG_NO_MORE_DATA_ACTION:
                	// 没有更多数据
                	disMissDialog();
            		if( mPageItemList.size() == 0 ){
            			mEmptyView.setVisibility(View.VISIBLE);
            			mListView.setVisibility(View.GONE);
            			searchHistoryParentLayout.setVisibility(View.GONE);
            		}else{
            			if (!NetUtil.isNetworkAvailable(YellowPageSearchNumberActivity.this)) {
            				mhandelr.sendEmptyMessageDelayed(MSG_NETWORK_EXCEPTION_ACTION, 300);
            				mListView.onLoadMoreComplete(true);
            			} else {
            				// 解决查询有数据但是最后一次返回size=0时没有显示“没有更多数据” 
            				// add cj 2014-10-17 start 
            				mListView.onLoadMoreComplete(true);
            				mAdapter.setmPageItemList(mPageItemList);
            				mListView.setVisibility(View.VISIBLE);
            				// add cj 2014-10-17 end
            			}
            		}
            		mListView.onLoadMoreComplete(false);
                	break;
                case MSG_SEARCH_BTN_ENABLE_ACTION:
                	// 获取到搜索结果后，改变搜索按钮 状态
                	/**
                	 * delete code
                	 * modify by putao_lhq
                	 * for coolui6.0
                	mCancelSearchBtn.setEnabled(true);
                	mCancelSearchBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.putao_pt_deep_gray)));
                	*/
                	break;
                case MSG_STOP_SEARCH_ACTION:
                	// 停止搜索
                	clearAllDataAndRefreshLayout();
                	if( mSearchController != null ){
                		mSearchController.release();
                	}
                	break;
                case MSG_NOT_REFRESH_AND_HAS_MORE_ACTION:
                	// 没有下拉刷新加载更多时，还有更多数据未加载
                	mListView.setLoadingAndWaitState();
                	break;
                case MSG_NOT_REFRESH_AND_NO_MORE_ACTION:
                	// 没有下拉刷新加载更多时，没有更多数据可加载
                	mListView.setHasNoMoreDataState();
                	break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (event != null && event.getAction() != KeyEvent.ACTION_UP) {
            return true;
        }
        if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
        	words = v.getText().toString();
            startSearch(false);
            return true;
        }
        return false;
    }

    @Override
    public void onLoadMore() {
    	mHasLoadMore = true;
        if (!NetUtil.isNetworkAvailable(this)) {
            mhandelr.sendEmptyMessageDelayed(MSG_NETWORK_EXCEPTION_ACTION, 300);
            mHasMore = false;
            mListView.onLoadMoreComplete(true);
            return;
        }
        if ( mHasMore && mSearchController != null) {
            LogUtil.d(TAG, "onLoadMore");
            mSearchController.searchMore();
        } else {
            mListView.onLoadMoreComplete(true);
        }
    }
    
    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if( CONNECTIVITY_CHANGE_ACTION.equals(action) ){
				LogUtil.i(TAG, "network is changed!");
				if( NetUtil.isNetworkAvailable(YellowPageSearchNumberActivity.this)){
					mHasMore = true;
				}
			}
		}
    	
    };

    private void saveSearchHistory(String newWords) {
        if (null == newWords || "".equals(newWords.trim())) {
            return;
        }
        newWords = newWords.trim();

        String historyStr = mSharedPreferences.getString(
                ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY, "");
        String[] historys = historyStr.split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);

        StringBuffer newLine = new StringBuffer();

        if (null == historys || "".equals(historyStr) || historys.length == 0) {
            newLine.append(newWords);
        } else {
            // 判断是否存在关键字
            ArrayList<String> historyWordsList = new ArrayList<String>();
            boolean isExists = false;
            String existsWords = null;
            for (int i = 0; i < historys.length; i++) {
                historyWordsList.add(historys[i]);
                if (newWords.equals(historys[i])) {
                    isExists = true;
                    existsWords = historys[i];
                }
            }

            if (isExists && null != existsWords) {
                historyWordsList.remove(existsWords);
                historyWordsList.add(0, existsWords);
                for (int i = 0; i < historyWordsList.size(); i++) {
                    newLine.append(historyWordsList.get(i));
                    if (i == historyWordsList.size() - 1) {
                        break;
                    }
                    newLine.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
                }
            } else {
                newLine.append(newWords)
                        .append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
                for (int i = 0; i < historys.length; i++) {
                    if (i >= HISTORY_COUNT - 1) {
                        break;
                    }
                    newLine.append(historys[i]);
                    if (i == historys.length - 1) {
                        break;
                    }
                    newLine.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
                }
            }
        }
        LogUtil.d(TAG, "newLine: " + newLine.toString());
        mSharedPreferences.edit()
                .putString(ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY, newLine.toString())
                .commit();
    }

    private void loadSearchHistory() {
        historyWordsList.clear();

        String historyStr = mSharedPreferences.getString(
                ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY, "");
        LogUtil.d(TAG, "historyStr=" + historyStr);
        if (null == historyStr || "".equals(historyStr)) {
            searchHistoryParentLayout.setVisibility(View.GONE);
        } else {
            String[] historys = historyStr
                    .split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);

            for (String s : historys) {
                historyWordsList.add(s);
            }
            LogUtil.d(TAG, "historyWordsList.size()=" + historyWordsList.size());
            if (historyWordsList.size() == 0) {
                searchHistoryParentLayout.setVisibility(View.GONE);
            } else {
                searchHistoryAdapter.setData(historyWordsList);
                searchHistoryParentLayout.setVisibility(View.VISIBLE);
            }
        }
    }
    //add ljq 2014-10-10 start
    private void loadSearchRecommendWord() {
        List<String> hotwordList = SearchRecommendwordUtil.getInstance().getRecommendwordList();
        //因为数组中有 固定【热词】的存在 不会少于1
        if (hotwordList.size() == 1) {
            mSearchRecommendwordListView.setVisibility(View.GONE);
        } else {
            //在自定义搜索下回到页面会发生视图错误的问题 以下代码是手动刷新一遍视图  start
            if(mSearchRecommendwordsAdapter!=null){
                mSearchRecommendwordListView.setAdapter(mSearchRecommendwordsAdapter);
                mSearchRecommendwordsAdapter.notifyDataSetChanged();
                mSearchRecommendwordListView.invalidate();
            }
            //end
            mSearchRecommendwordListView.setVisibility(View.VISIBLE);
        }
        
    }
    //add ljq 2014-10-10 end

    private void deleteSearchHistoryByWords(String words) {
        if (null == words || "".equals(words)) {
            return;
        }

        String historyStr = mSharedPreferences.getString(
                ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY, "");

        if (null == historyStr || "".equals(historyStr)) {
            searchHistoryParentLayout.setVisibility(View.GONE);
        } else {
            String[] historys = historyStr
                    .split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
            StringBuffer newLine = new StringBuffer();
            for (int i = 0; i < historys.length; i++) {
                String s = historys[i];
                if (!words.equals(s)) {
                    newLine.append(s);
                    if (i == historys.length - 1) {
                        break;
                    }
                    newLine.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
                }
            }
            mSharedPreferences.edit()
                    .putString(ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY, newLine.toString())
                    .commit();
        }
    }

    /**
     * sol.isHasMore()表示所有搜索任务的最终结果
     * hasMore表示当期搜索任务的最终街，如：大众点评
     */
    @Override
    public void onResult(Solution sol, Map<Integer, List<YelloPageItem>> itemMaps, List<YelloPageItem> itemList, boolean hasMore) {
        boolean hasMoreTask = mSearchController.hasMoreTask();
        mHasMore = mSearchController.hasMore();
        LogUtil.d(TAG, "onResult hasMore: " + sol.isHasMore() + " ,map_size:" + itemMaps.size() + " ,solutionHit: " 
        		+ sol.getHit() + " ,item_size: " + (itemList!=null?itemList.size():0) + " ,mHasMore: " + mHasMore);
        if( !TextUtils.isEmpty(words) && sol != null ){
            doDuplicateAndSort(itemMaps, hasMoreTask);
        }else if( TextUtils.isEmpty(words) ){
    		mhandelr.sendEmptyMessage(MSG_STOP_SEARCH_ACTION);
    	}else if(sol == null) {
            LogUtil.e(TAG, "onResult sol is null");
        }
        if( !hasMoreTask ){
    		// 没有更多数据时，搜索按钮可点击
    		mhandelr.sendEmptyMessage(MSG_SEARCH_BTN_ENABLE_ACTION);
    	}
    }
    
    /**
     * 对数据进行去重、排序，然后显示.
     * 注：只对网络数据进行去重排序
     */
    private void doDuplicateAndSort(Map<Integer, List<YelloPageItem>> itemMap, boolean hasMoreTask){
        List<YelloPageItem> putaoValues = null;
        if( itemMap != null && itemMap.size() > 0 ){
            List<YelloPageItem> itemValues = new ArrayList<YelloPageItem>();
            // 1、先获取相同数据源的数据
            for(int key : itemMap.keySet() ){
                itemValues = itemMap.get(key);
                if( itemValues == null || itemValues.size() == 0 ){
                    continue;
                }
                List<YelloPageItem> mapItemValues = null;
                if( mPageItemMap.containsKey(key) ){
                    mapItemValues = mPageItemMap.get(key);
                    if( mapItemValues == null ){
                        mapItemValues = new ArrayList<YelloPageItem>();
                    }
                    mapItemValues.addAll(itemValues);
                }else{
                    mapItemValues = itemValues;
                }
                mPageItemMap.put(key, mapItemValues);
            }
            
            if( mPageItemMap != null && mPageItemMap.size() > 0 ){
                
                /*
                 * modify by XCX 2015-2-6 start
                 * 修改排序无效的问题
                 */
                
                // 2、对数据按照key进行排序
//              List<Map.Entry<Integer, List<YelloPageItem>>> mappingList = new ArrayList<Map.Entry<Integer, List<YelloPageItem>>>(
//                      mPageItemMap.entrySet());
//              Collections.sort(mappingList,
//                      new Comparator<Map.Entry<Integer, List<YelloPageItem>>>() {
//                  public int compare(Map.Entry<Integer, List<YelloPageItem>> lMap, Map.Entry<Integer, List<YelloPageItem>> rMap) {
//                      return lMap.getKey() - rMap.getKey();
//                  }
//              });
                
                mPageItemList.clear();
                List<Integer>keyList=new ArrayList<Integer>();
                for(int key : mPageItemMap.keySet() ){
                    keyList.add(key);
                }
                Collections.sort(keyList,
                        new Comparator<Integer>() {
                    public int compare(Integer key1, Integer key2) {
                        return key1 - key2;
                    }
                });
                for(int key : keyList ){
                    
                    /*
                     * modify by XCX 2015-2-6 end
                     * 修改排序无效的问题
                     */
                    
                    List<YelloPageItem> values = mPageItemMap.get(key);
                    if( values == null || values.size() == 0 ){
                        continue;
                    }
                    if( key == 0 ){
                        // 本地葡萄数据，不进行操作，待对网络数据操作完后直接插入顶部
                        putaoValues = values;
                        continue;
                    }
                    
                    // 3、对同一数据源的数据进行排序
                    String orderBy = mSearchController.getOrderBy(key);
                    if( !TextUtils.isEmpty(orderBy) ){
                        int[] orderList = null;
                        try{
                            if( orderBy.contains(",") ){
                                String[] strOrderList = orderBy.split(",");
                                int size = strOrderList.length;
                                orderList = new int[size];
                                for(int i = 0; i < size; i++){
                                    orderList[i] = Integer.valueOf(strOrderList[i]);
                                }
                            }else{
                                orderList = new int[1];
                                orderList[0] = Integer.valueOf(orderBy);
                            }
                        }catch(Exception e){
                            orderList = null;
                        }
                        
                        if(orderList == null || orderList.length ==0) {
                            orderList = new int[]{ResultSortedRule.SORT_TYPE_NAME, ResultSortedRule.SORT_TYPE_DISTANCE};
                        }
                        
                        if( orderList != null ){
                            ResultSortedRule.resultSorted(values, words, orderList);
                        }
                    }
                    mPageItemList.addAll(values);
                }
                
                // 4、去除重复数据
                DuplicateRemoveRule.duplicateRemoved(mPageItemList);
            }
            
            // 将葡萄静态数据插入到最顶部
            if( putaoValues != null ){
                mPageItemList.addAll(0, putaoValues);
            }
        }

        LogUtil.i(TAG, "doDuplicateAndSort hasMore: " + hasMoreTask);
        if( mPageItemList != null && mPageItemList.size() > 0 ){ // 有数据刷新界面
            mhandelr.sendEmptyMessage(MSG_COMPUTE_AND_SHOW_DATA_SEARCH);
            if( !mHasLoadMore ){ // 没有加载过更多
                if( hasMoreTask ){ // 有更多任务
                    if( mSearchController.hasNetworkMore() ){
                        mhandelr.sendEmptyMessage(MSG_NOT_REFRESH_AND_HAS_MORE_ACTION);
                    }else{
                        mhandelr.sendEmptyMessage(MSG_NOT_REFRESH_AND_NO_MORE_ACTION);
                    }
                }else{
                    mhandelr.sendEmptyMessage(MSG_NOT_REFRESH_AND_NO_MORE_ACTION);
                }
            }
        }else if( mPageItemList == null || mPageItemList.size() == 0 ){ // 
            if( hasMoreTask ){
                // 还有更多数据
            }else{
                // 没有更多数据了
                mhandelr.sendEmptyMessage(MSG_NO_MORE_DATA_ACTION);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            doQuitActivity(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

	@Override
	public String getServiceNameByUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceName() {
		return this.getClass().getName();
	}

	@Override
	public boolean needMatchExpandParam() {
		return false;
	}

	@Override
	public Integer remindCode() {
		return mRemindCode;
	}

	@Override
	protected boolean needReset() {
	    return true;
	}
    
    
}
