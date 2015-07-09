package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.List;
import so.contacts.hub.adapter.SearchHistoryAdapter;
import so.contacts.hub.adapter.SearchHistoryAdapter.onDeleteButtonClickListener;
import so.contacts.hub.adapter.YellowPageDealAdapter;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.util.CommonValueUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.CustomListView;
import so.contacts.hub.widget.CustomListView.OnLoadMoreListener;
import so.contacts.hub.widget.ProgressDialog;
import so.putao.findplug.DianpingDeal;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import so.putao.findplug.ResultListener;
import so.putao.findplug.SearchData;
import so.putao.findplug.YelloPageDataManager;
import so.putao.findplug.YelloPageItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import so.contacts.hub.util.MobclickAgentUtil;
import com.yulong.android.contacts.discover.R;

@Deprecated
public class YellowPageDealSearchActivity extends Activity implements
		OnClickListener, ResultListener, TextWatcher, OnItemClickListener,
		LBSServiceListener, OnEditorActionListener, OnLoadMoreListener {

	public static final String TAG = "YellowPageDealSearchActivity";
	
	private CustomListView mListView;
	private RelativeLayout searchHistoryParentLayout;
	private ListView searchHistoryListView;
	private EditText mSearchEditText;
	private TextView mCancelSeatchBtn;
	private ImageView mClearSearchBtn;
	private TextView mPosition;
	private LinearLayout mEmptyView;

	private ProgressDialog mProgressDialog;

	private YellowPageDealAdapter mAdapter;
	private DataLoader mImageLoader;
	private SearchData mAsyncSearchData;
	private List<YelloPageItem> mPageItemList;

	private String category;
	private String words;
	private boolean mHasMore;

	private SharedPreferences mSharedPreferences;
	private String mSelectedCity = "";
	private boolean isPreValid = true;

	public static final int HISTORY_COUNT = 20;
	private ArrayList<String> historyWordsList = new ArrayList<String>();
	private SearchHistoryAdapter searchHistoryAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_deal_search);
		if (LBSServiceGaode.isTimeOut()
				&& NetUtil.isNetworkAvailable(this)) {
			isPreValid = false;
			LBSServiceGaode.activate(this, this);
		}
		YelloPageDataManager.createInstance(this.getApplicationContext());
		mImageLoader = new ImageLoaderFactory(this).getDefaultYellowPageDealLoader();
		mSharedPreferences = getSharedPreferences(
				ConstantsParameter.SHARED_PREFS_YELLOW_PAGE_DEAL,
				Context.MODE_MULTI_PROCESS);
		initViews();
		initData();
		
	}

	@SuppressLint("NewApi")
    private void initViews() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.putao_yellow_page_deal_title);
		ImageView locImgView = (ImageView) findViewById(R.id.next_step_img);
		locImgView.setVisibility(View.GONE);
		locImgView.setImageResource(R.drawable.putao_icon_marker_white);

		mSearchEditText = (EditText) findViewById(R.id.search_edit_text);
		mSearchEditText.setOnEditorActionListener(this);
		mSearchEditText.addTextChangedListener(this);
		mSearchEditText.requestFocus();
		mCancelSeatchBtn = (TextView) findViewById(R.id.cancel_search_btn);
		mClearSearchBtn = (ImageView) findViewById(R.id.clear_search_content_btn);
		mClearSearchBtn.setOnClickListener(this);

		mListView = (CustomListView) findViewById(R.id.search_list);
		mListView.setVisibility(View.GONE);
		mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
		mListView.setOnItemClickListener(this);
		mListView.setOnLoadListener(this);
		mListView.setCanLoadMore(true);
		mListView.setAutoLoadMore(true);

		searchHistoryParentLayout = (RelativeLayout) findViewById(R.id.search_history_parent);
		searchHistoryListView = (ListView) findViewById(R.id.search_history_list);
		searchHistoryListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(
									YellowPageDealSearchActivity.this
											.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				} catch (Exception e) {
				}
				return false;
			}
		});
		searchHistoryAdapter = new SearchHistoryAdapter(this, historyWordsList);
		searchHistoryAdapter.setOnDeleteButtonClickListener(new onDeleteButtonClickListener() {
			@Override
			public void onDeleteButtonClicked(int position, String words) {
				if(!TextUtils.isEmpty(words)){
//					mSearchEditText.setText(words);
					deleteSearchHistoryByWords(words);
					loadSearchHistory();
				}
			}
		});
		searchHistoryListView.setAdapter(searchHistoryAdapter);
		searchHistoryListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(null == view.getTag()){
					return;
				}
				
				String words = (String) view.getTag();
				if(!TextUtils.isEmpty(words)){
					mSearchEditText.setText(words);
					mSearchEditText.setSelection(words.length());
					findViewById(R.id.cancel_search_btn).callOnClick();
				}
			}
		});
		
//		mPosition = (TextView) findViewById(R.id.next_step_btn);
//		mPosition.setVisibility(View.VISIBLE);
		mPosition = (TextView) findViewById(R.id.city_btn);

		findViewById(R.id.back_layout).setOnClickListener(this);
//		findViewById(R.id.next_setp_layout).setOnClickListener(this);
		findViewById(R.id.city_btn).setOnClickListener(this);
		findViewById(R.id.cancel_search_btn).setOnClickListener(this);
	}
	
	private void initData() {
//	    mSelectedCity = YellowUtil.selectedCity(this);
		mPageItemList = new ArrayList<YelloPageItem>();
		mAdapter = new YellowPageDealAdapter(this, mPageItemList, mImageLoader);
		mListView.setAdapter(mAdapter);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				findViewById(R.id.cancel_search_btn).setEnabled(true);				
			}
		});
		if (NetUtil.isNetworkAvailable(this)) {
			if (isPreValid) {
				if (LBSServiceGaode.hasPreInfo()) {
					if (TextUtils.isEmpty(mSelectedCity)) {
						mPosition.setText(LBSServiceGaode.getPreCity());
					} else if (!mSelectedCity.equals(LBSServiceGaode
							.getPreCity())) {
						// 如果选择取消 isPreValid = false; 否则切换
						mPosition.setText(mSelectedCity);
//						showDialog();
					} else {
						mPosition.setText(mSelectedCity);
					}
				}
			} else {
				if (TextUtils.isEmpty(mSelectedCity)) {
					mPosition.setText(R.string.putao_yellow_page_locating);
				} else {
					mPosition.setText(mSelectedCity);
				}
			}
		} else {
			Toast.makeText(this, R.string.putao_no_net, Toast.LENGTH_LONG).show();
			mPosition.setText(R.string.putao_yellow_page_location_failed);
		}
	}

	private void refreshData() {
		mPageItemList.clear();
		mAdapter.setmPageItemList(mPageItemList);
		mAdapter.notifyDataSetChanged();
		mListView.setVisibility(View.VISIBLE);
		mListView.setFooterViewVisibility(View.GONE);
//		mSelectedCity = YellowUtil.selectedCity(this);
		if (TextUtils.isEmpty(mSelectedCity)) {
			if (isPreValid && LBSServiceGaode.hasPreInfo()) {
				showProgress();
				mAsyncSearchData = new SearchData(
						LBSServiceGaode.getPreLatitude(),
						LBSServiceGaode.getPreLongitude(),
						LBSServiceGaode.getPreCity(), category, words, 0,true,false);
				YelloPageDataManager.getInstance().asyncDealSearch(
						mAsyncSearchData, this);
			} else {
				Toast.makeText(this, R.string.putao_yellow_page_location_failed,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			if (isPreValid && LBSServiceGaode.hasPreInfo()
					&& mSelectedCity.equals(LBSServiceGaode.getPreCity())) {
			    showProgress();
				mAsyncSearchData = new SearchData(
						LBSServiceGaode.getPreLatitude(),
						LBSServiceGaode.getPreLongitude(),
						LBSServiceGaode.getPreCity(), category, words, 0,true,false);
				YelloPageDataManager.getInstance().asyncDealSearch(
						mAsyncSearchData, this);
			} else {
			    showProgress();
				mAsyncSearchData = new SearchData(0, 0, mSelectedCity,
						category, words, 0,true,false);
				YelloPageDataManager.getInstance().asyncDealSearch(
						mAsyncSearchData, this);
			}
		}
	}

	private void showProgress() {
	    if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            mEmptyView.setVisibility(View.GONE);
        }
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_layout) {
			Intent ib = new Intent(YellowPageDealSearchActivity.this,
		    		YellowPageDealActivity.class);
			setResult(RESULT_OK, ib);
			finish();
		} else if (id == R.id.city_btn) {
			startActivityForResult(new Intent(this,
					YellowPageCitySelectActivity.class), 0);
		} else if (id == R.id.cancel_search_btn) {
			words = mSearchEditText.getText().toString();
			if (TextUtils.isEmpty(words)) {
			    Intent ic = new Intent(YellowPageDealSearchActivity.this,
			    		YellowPageDealActivity.class);
			    setResult(RESULT_OK, ic);
				finish();
			} else {
				// save words
				
				saveSearchHistory(words);
				if (NetUtil.isNetworkAvailable(this)) {
					refreshData();
					findViewById(R.id.cancel_search_btn).setEnabled(false);
					mCancelSeatchBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.putao_pt_gray)));
				} else {
					Toast.makeText(this, R.string.putao_no_net, Toast.LENGTH_LONG)
							.show();
				}
			}
		} else if (id == R.id.clear_search_content_btn) {
			mSearchEditText.getText().clear();
			if (mPageItemList != null && mAdapter != null) {
				if (mPageItemList.size() == 0) {
					mEmptyView.setVisibility(View.GONE);
				} else {
					mPageItemList.clear();
					mAdapter.setmPageItemList(mPageItemList);
					mAdapter.notifyDataSetChanged();
//					mListView.setVisibility(View.GONE);
				}
			}
			searchHistoryParentLayout.setVisibility(View.VISIBLE);
			loadSearchHistory();
		} else {
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LBSServiceGaode.deactivate();
		mSearchEditText.getText().clear();
		mSearchEditText.clearFocus();
		YelloPageDataManager.closeInstance();
		mImageLoader.clearCache();		
	}
	
	@Override
    public void onBackPressed() {
	    Log.i(TAG, "onBackPressed");
	    Intent i = new Intent(YellowPageDealSearchActivity.this,
	    		YellowPageDealActivity.class);
	    setResult(RESULT_OK, i);
	    super.onBackPressed();
	}

	@Override
	protected void onResume() {
		MobclickAgentUtil.onResume(this);
		if (LBSServiceGaode.hasPreInfo() && mPosition != null
				&& TextUtils.isEmpty(mSelectedCity)) {
			mPosition.setText(LBSServiceGaode.getPreCity());
		}
		super.onResume();
		
		loadSearchHistory();
		// searchHistoryAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
        MobclickAgentUtil.onPause(this);
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public Handler getHandler() {
		return new Handler(Looper.getMainLooper());
	}

	@Override
	public void onResult(SearchData searchData,
			ArrayList<YelloPageItem> itemList, boolean hasMore,boolean isTimeOut) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		findViewById(R.id.cancel_search_btn).setEnabled(true);
		mCancelSeatchBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.putao_pt_deep_gray)));
		if(isTimeOut){
			Toast.makeText(this, R.string.putao_search_time_out, Toast.LENGTH_SHORT).show();
		}else if (itemList.size() == 0 && !hasMore && mPageItemList.size() == 0) {
			mEmptyView.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
			searchHistoryParentLayout.setVisibility(View.GONE);
		} else {
			mEmptyView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			searchHistoryParentLayout.setVisibility(View.GONE);
			mHasMore = hasMore;
			mPageItemList.addAll(itemList);
			mAdapter.setmPageItemList(mPageItemList);
			mAdapter.notifyDataSetChanged();
		}
		mListView.onLoadMoreComplete(false);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position <= mPageItemList.size()) {
			final YelloPageItem item = mPageItemList.get(position - 1);
			DianpingDeal deal = (DianpingDeal) item.getData();
			if (item != null) {
				Intent intent = new Intent(YellowPageDealSearchActivity.this,
						YellowPageDetailActivity.class);
				intent.putExtra("url", deal.deal_h5_url);
				intent.putExtra("title", deal.title);
				startActivity(intent);
			}
		}
	}

	private void showDialog() {
		final CommonDialog dialog = CommonDialogFactory
				.getOkCancelCommonLinearLayoutDialog(this);
		dialog.getTitleTextView().setText(R.string.putao_yellow_page_position_change);
		String msg = getString(R.string.putao_yellow_page_position_change_msg,
				LBSServiceGaode.getPreCity());
		dialog.getMessageTextView().setText(msg);
		dialog.getCancelButton().setText(R.string.putao_cancel);
		dialog.setCancelButtonClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isPreValid = false;
				mPosition.setText(mSelectedCity);
				dialog.dismiss();
			}
		});

		dialog.getOkButton().setText(R.string.putao_yellow_page_change_position);
		dialog.setOkButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isPreValid = true;
				mPosition.setText(LBSServiceGaode.getPreCity());
				mSharedPreferences
						.edit()
						.putString(
								ConstantsParameter.YELLOW_PAGE_SELECTED_CITY,
								"").commit();
				mSelectedCity = "";
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 0) {
			mSelectedCity = data.getStringExtra("cityName");
//			mSharedPreferences
//					.edit()
//					.putString(ConstantsParameter.YELLOW_PAGE_SELECTED_CITY,
//							mSelectedCity).commit();
			mPosition.setText(mSelectedCity);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (TextUtils.isEmpty(s)) {
			if (mPageItemList != null && mPageItemList.size() > 0
					&& mAdapter != null) {
				mPageItemList.clear();
				mAdapter.setmPageItemList(mPageItemList);
				mAdapter.notifyDataSetChanged();
				mListView.setVisibility(View.GONE);
			} else {
				mEmptyView.setVisibility(View.GONE);
			}

			mCancelSeatchBtn.setText(R.string.putao_cancel);
			mClearSearchBtn.setVisibility(View.GONE);
			
			searchHistoryParentLayout.setVisibility(View.VISIBLE);
			loadSearchHistory();
		} else {
			mCancelSeatchBtn.setText(R.string.putao_yellow_page_search_btn);
			mClearSearchBtn.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onLocationChanged(String city, double latitude,
			double longitude, long time) {

		if (!TextUtils.isEmpty(city)) {
			if (city.endsWith(CommonValueUtil.getInstance().getCityData())) {
				city = city.substring(0, city.length() - 1);
			}
			if (TextUtils.isEmpty(mSelectedCity)) {
				mPosition.setText(city);
				isPreValid = true;
			} else if (!city.equals(mSelectedCity)) {
				// 如果选择切换 isPreValid = true;
				// 弹框
//				showDialog();
			} else {
				isPreValid = true;
			}
			LBSServiceGaode.deactivate();
		} else if (latitude != 0 && longitude != 0) {
			mPosition.setText(R.string.putao_yellow_page_location_failed);
			// GPS 定位
			isPreValid = true;
			LBSServiceGaode.deactivate();
		} else {
			mPosition.setText(R.string.putao_yellow_page_location_failed);
			LBSServiceGaode.deactivate();
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (event != null && event.getAction() != KeyEvent.ACTION_UP) {
			return true;
		}
		if (actionId == EditorInfo.IME_ACTION_SEARCH
				|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
			words = v.getText().toString();
			if (TextUtils.isEmpty(words)) {
				return true;
			}
			((InputMethodManager) v.getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					v.getApplicationWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			if (NetUtil.isNetworkAvailable(this)) {
				saveSearchHistory(words);
				refreshData();
			} else {
				Toast.makeText(this, R.string.putao_no_net, Toast.LENGTH_LONG).show();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onLoadMore() {
		if (mHasMore) {
			YelloPageDataManager.getInstance()
					.asyncSearchDealMore(mAsyncSearchData);
		} else {
			if (NetUtil.isNetworkAvailable(this)) {
				if (mPageItemList.size() > 0) {
					mListView.onLoadMoreComplete(true);
				}
			} else {
				Toast.makeText(this, R.string.putao_no_net, Toast.LENGTH_LONG).show();
			}
		}

	}
	
	private void saveSearchHistory(String newWords) {
		
		if (null == newWords || "".equals(newWords.trim())) {
			return;
		}
		newWords = newWords.trim();
		
		String historyStr = mSharedPreferences.getString(
				ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY, "");
		String[] historys = historyStr
				.split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
		
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
				if(newWords.equals(historys[i])){
					isExists = true;
					existsWords = historys[i];
				}
			}
			
			if(isExists && null != existsWords){
				historyWordsList.remove(existsWords);
				historyWordsList.add(0, existsWords);
				for (int i = 0; i < historyWordsList.size(); i++) {
					newLine.append(historyWordsList.get(i));
					if(i == historyWordsList.size() -1){
						break;
					}
					newLine.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
				}
			} else {
				newLine.append(newWords).append(
						ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
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
		Log.d(TAG, "yh history = " + newLine.toString());
		mSharedPreferences
				.edit()
				.putString(ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY,
						newLine.toString()).commit();
	}

	private void loadSearchHistory() {

		historyWordsList.clear();
		
		long start = System.currentTimeMillis();
		
		String historyStr = mSharedPreferences.getString(
				ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY, "");
		
		if(null == historyStr || "".equals(historyStr)){
			searchHistoryParentLayout.setVisibility(View.GONE);
		} else {
			String[] historys = historyStr
					.split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
			
			for(String s : historys){
				historyWordsList.add(s);
				Log.d(TAG, "yh string = " + s);
			}
			Log.d(TAG, "yh use time = " + (System.currentTimeMillis() - start));
			if(historyWordsList.size() == 0){
				searchHistoryParentLayout.setVisibility(View.GONE);
			} else {
				searchHistoryAdapter.setData(historyWordsList);
				searchHistoryParentLayout.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void deleteSearchHistoryByWords(String words){
		if(null == words || "".equals(words)){
			return;
		}
		
		String historyStr = mSharedPreferences.getString(
				ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY, "");
		
		if(null == historyStr || "".equals(historyStr)){
			searchHistoryParentLayout.setVisibility(View.GONE);
		} else {
			String[] historys = historyStr
					.split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
			StringBuffer newLine = new StringBuffer();
			for (int i = 0; i < historys.length; i++) {
				String s = historys[i];
				if(!words.equals(s)){
					newLine.append(s);
					if(i == historys.length - 1){
						break;
					}
					newLine.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
				}
			}
			mSharedPreferences
					.edit()
					.putString(ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY,
							newLine.toString()).commit();
		}
	}
	
	private void clearSearchHistory() {
		mSharedPreferences.edit()
				.putString(ConstantsParameter.YELLOW_PAGE_SEARCH_HISTORY, null)
				.commit();
	}

    @Override
    public void onLocationFailed() {
        // TODO Auto-generated method stub
        
    }
}
