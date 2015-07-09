package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import so.contacts.hub.ad.AdCode;
import so.contacts.hub.thirdparty.baidu.BaiduUriApiUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.PoiInfo.POITYPE;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.yulong.android.contacts.discover.R;

/**
 * 
 * @author putao_lhq
 *
 */
public class BusQueryFragment extends BusBaseFragment implements OnGetPoiSearchResultListener, OnClickListener {
    private static final String TAG = "BusQueryFragment";
    
	private ListView mNearStation;
	private YellowPageBusQActivity mTarget;
	/*
	 * modify by putao_lhq
	 * coolui6.0
	 * @start
	private TextView mNearStationTv;*/
	private LinearLayout mNearStationTv;
	/*@end*/
	private EditText mInput;
	private ImageView mClear;
	private BusTextWatcher mTextWatcher;
	private List<ListItem> mDataNear = new ArrayList<ListItem>();
	private SharedPreferences mShared;
	private static final String DB = "bus_line_db";
	private List<String> mHistory = new ArrayList<String>();
	private static final String HISTORY_KEY = "history";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.putao_bus_query_fragment, container, false);
		mNearStation = (ListView) view.findViewById(R.id.near_station);
		/*
		 * modify by putao_lhq
		 * coolui6.0
		 * @start
		mNearStationTv = (TextView) view.findViewById(R.id.near_station_tv);*/
		mNearStationTv = (LinearLayout)view.findViewById(R.id.near_station_tv);
		/*@end*/
		
		mInput = (EditText) view.findViewById(R.id.input);
		mClear = (ImageView) view.findViewById(R.id.clear);
		mInput.requestFocus();
		mTextWatcher = new BusTextWatcher(-1);
		mInput.addTextChangedListener(mTextWatcher);
		mInput.setOnClickListener(this);
		mListView = (ListView) view.findViewById(R.id.expand);
		mListView.setOnItemClickListener(this);
		mNearStation.setOnItemClickListener(this);
		mTarget = (YellowPageBusQActivity) getActivity();
		mBtnQuery = (Button) view.findViewById(R.id.bus_line_query_btn);
		mBtnQuery.setOnClickListener(this);
		mClear.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mShared = getActivity().getSharedPreferences(DB, Context.MODE_MULTI_PROCESS);
		Set<String> history = mShared.getStringSet(HISTORY_KEY, null);
		if (history != null) {
			for (String info: history) {
				mHistory.add(info);
			}
		}
		queryNearStation();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		LogUtil.d(TAG, "BusBaseFragment onResume");
		MobclickAgentUtil.onEvent(getActivity(), UMengEventIds.DISCOVER_YELLOWPAGE_BUS_PATH);
	}
	
	private void queryNearStation() {
		LatLng latlng = mTarget.getCurLatLng();
		if (latlng == null) {
			LogUtil.d(TAG, "latlng is null");
			latlng = new LatLng(100, 200);
		}
		mPoiSearch.searchNearby(new PoiNearbySearchOption().location(mTarget.getCurLatLng()).radius(1000).keyword("公交"));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mInput.removeTextChangedListener(mTextWatcher);
		if (parent == mListView) {
			ListItem item = mData.get(position);
			mInput.setText(item.getContent());
			mListView.setVisibility(View.GONE);
			mBtnQuery.setVisibility(View.VISIBLE);
			mClear.setVisibility(View.VISIBLE);
			if (mAdapter.isShowDelete()) {
				doSearchLine(mCurCity, mInput.getText().toString());
			}
		} else {
			MobclickAgentUtil.onEvent(getActivity(), UMengEventIds.DISCOVER_YELLOWPAGE_BUS_PATH_SELECT_NEAR);
			mInput.setText(mDataNear.get(position).getContent());
			doSearchLine(mCurCity, mInput.getText().toString());
		}
		mInput.addTextChangedListener(mTextWatcher);
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		LogUtil.d(TAG, "onGetPoiDetailResult");
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		LogUtil.d(TAG, "onGetPoiResult: " + result);
		if (result == null || result.getAllPoi() == null || 
				result.getAllPoi().size() <= 0) {
			return;
		}
		mDataNear.clear();
		String miStr = getString(R.string.putao_common_mi);
		for (PoiInfo info : result.getAllPoi()) {
			if (info.type == POITYPE.BUS_STATION) {
				ListItem item = new ListItem();
				item.setContent(info.name);
				double distance = DistanceUtil.getDistance(mTarget.getCurLatLng(), info.location);
				item.setDistance(distance);
				item.setCategory(String.valueOf((int)distance) + miStr);
				mDataNear.add(item);
			}
		}
		Collections.sort(mDataNear, new Comparator<ListItem>() {

			@Override
			public int compare(ListItem lhs, ListItem rhs) {
				if (lhs.getDistance() > rhs.getDistance()) {
					return 1;
				} else if (lhs.getDistance() < rhs.getDistance()) {
					return -1;
				}
				return 0;
			}
		});
		mAdapter = new ListViewAdapter(mDataNear);
		mAdapter.setGayColor(true);
		LogUtil.d(TAG, "size: " + mAdapter.getCount());
		mNearStationTv.setVisibility(View.VISIBLE);
		mNearStation.setAdapter(mAdapter);
		mNearStation.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.input) {
			showHistory();
		} else if (id == R.id.bus_line_query_btn) {
			String input = mInput.getText().toString();
			if (TextUtils.isEmpty(input)) {
				Toast.makeText(getActivity(), getActivity().getResources().
						getString(R.string.putao_bus_query_input_empty), Toast.LENGTH_SHORT).show();
			} else {
				MobclickAgentUtil.onEvent(getActivity(), UMengEventIds.DISCOVER_YELLOWPAGE_BUS_PATH_QUERY);
				saveHistory(input);
				doSearchLine(mCurCity, input);
			}
		} else if (id == R.id.clear) {
			mInput.setText("");
		} else {
		}
		
	}

	/**
     * 搜索公交车线路
     */
    private void doSearchLine(String city, String name) {
        StringBuffer reqUrl = new StringBuffer(BaiduUriApiUtil.HOST_LINE_URL);
        reqUrl.append("?").append("region=").append(city).append("&name=").append(name);
        reqUrl.append("&output=html");
        reqUrl.append("&src=putao|yellowpage");
//        String url = "http://api.map.baidu.com/line?region=北京&name=518&output=html&src=yourCompanyName|yourAppName";
        startWebRequest(reqUrl.toString());
    }
    
    private void startWebRequest(String reqUrl) {
        LogUtil.d(TAG, "startWebRequest="+reqUrl);
        
        mYellowParams.setUrl(reqUrl);
        mYellowParams.setTitle(getString(R.string.putao_common_query_bus));
                                    
        Intent intent = new Intent(getActivity(), so.contacts.hub.ui.web.YellowPageBusActivity.class);
        intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);

        startActivity(intent);
    }
    
	private void showHistory() {
		LogUtil.v(TAG, "show history");
		if (mHistory.size() <= 0) {
			return;
		}
		mData.clear();
		for (int i = mHistory.size() - 1; i >= 0; i--) {
			ListItem item = new ListItem();
			String info = mHistory.get(i);
			item.setContent(info);
			mData.add(item);
		}
		mAdapter = new ListViewAdapter(mData);
		mListView.setAdapter(mAdapter);
		mAdapter.setShowDelete(true);
		mBtnQuery.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void deleteHistory(int position) {
		mData.remove(position);
		mHistory.remove(position);
		if (mHistory.size() <= 0) {
			mListView.setVisibility(View.GONE);
			mBtnQuery.setVisibility(View.VISIBLE);
			SharedPreferences.Editor edit = mShared.edit();
			edit.remove(HISTORY_KEY);
			edit.commit();
		} else {
			mAdapter.notifyDataSetChanged();
			saveHistory();
		}
	}
	
	private void saveHistory(String input) {
		if (!mHistory.contains(input)) {
			mHistory.add(input);
			saveHistory();
		}
	}

	private void saveHistory() {
		SharedPreferences.Editor edit = mShared.edit();
		Set<String> set = new HashSet<String>();
		if (mHistory.size() > 3) {
			mHistory.remove(0);
		}
		for (String h : mHistory) {
			set.add(h);
		}
		edit.putStringSet(HISTORY_KEY, set);
		edit.commit();
	}

	@Override
	protected void doTextChanged(CharSequence s, int which) {
		if (TextUtils.isEmpty(s)) {
			mClear.setVisibility(View.GONE);
			showHistory();
		} else {
			mClear.setVisibility(View.VISIBLE);
			// add by putao_lhq 2014年10月11日 for BUG #1523 start
			mListView.setVisibility(View.GONE);
			mBtnQuery.setVisibility(View.VISIBLE);
			// add by putao_lhq 2014年10月11日 for BUG #1523 end
		}
	}
	
	@Override
	public Integer getAdId() {
	    return AdCode.ADCODE_BusQueryFragment;
	}
}
