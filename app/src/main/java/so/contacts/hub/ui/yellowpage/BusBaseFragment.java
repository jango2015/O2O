package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ui.BaseFragment;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.YellowUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.yulong.android.contacts.discover.R;

/**
 * 公交查询基类
 * 
 * @author putao_lhq
 * 
 */
public abstract class BusBaseFragment extends BaseFragment implements
		OnGetSuggestionResultListener, OnItemClickListener {

	protected static final String TAG = "BusBaseFragment";
	protected ListView mListView;
	protected SuggestionSearch mSuggestion;
	protected PoiSearch mPoiSearch;
	protected String mCurCity;
	protected List<ListItem> mData = new ArrayList<ListItem>();
	protected ListViewAdapter mAdapter;
	protected int mCurWhich = -1;
	protected static final int WHICH_FROM = 1;
	protected static final int WHICH_TO = 2;
	protected static final int WHICH_INPUT = 2;
	protected YellowParams mYellowParams;
	protected Button mBtnQuery;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mYellowParams = (YellowParams)getActivity().getIntent().getSerializableExtra(
                YellowUtil.TargetIntentParams);
    	if (mYellowParams == null) {
    		mYellowParams = new YellowParams();
    	}
        /* 
         * 酷派反馈bug修改，进入公交出现crash问题
         * modified by hyl 2014-12-29 start
         * old code:
         * mSuggestion = SuggestionSearch.newInstance();
         */
        try {
            mSuggestion = SuggestionSearch.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            SDKInitializer.initialize(getActivity().getApplicationContext());
            mSuggestion = SuggestionSearch.newInstance();
        }
        //modified by hyl 2014-12-29 end
        
		mPoiSearch = PoiSearch.newInstance();
		mSuggestion.setOnGetSuggestionResultListener(this);
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult result) {
		List<SuggestionInfo> queriedResult = result.getAllSuggestions();
		if (result == null || queriedResult == null || queriedResult.size() <= 0) {
			LogUtil.d(TAG, "no data");
			mListView.setVisibility(View.GONE);
			if (null != mBtnQuery) {
				mBtnQuery.setVisibility(View.VISIBLE);
			}
			return;
		}
		mData.clear();
		LogUtil.d(TAG, "has data");
		for (SuggestionInfo info : queriedResult) {
			ListItem item = new ListItem();
			item.setContent(info.key);
			mData.add(item);
		}
		mAdapter = new ListViewAdapter(mData);
		mListView.setAdapter(mAdapter);
		mListView.setVisibility(View.VISIBLE);
		if (null != mBtnQuery) {
			mBtnQuery.setVisibility(View.GONE);
		}
		mAdapter.notifyDataSetChanged();
	}

	protected class ListItem {
		private String content;
		private String category;
		private double distance;
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public double getDistance() {
			return distance;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
	}
	
	protected class ListViewAdapter extends BaseAdapter implements Filterable {

		private List<ListItem> data;
		private boolean showDelete = false;
		private boolean isGay = false;
		private int colorGay;
		
		public ListViewAdapter(List<ListItem> data){
			this.data = data;
			colorGay = getResources().getColor(android.R.color.tertiary_text_light);
		}
		
		public void setShowDelete(boolean show) {
			this.showDelete = show;
		}
		
		public boolean isShowDelete() {
			return showDelete;
		}
		
		public void setGayColor(boolean gay) {
			this.isGay = gay;
		}
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.putao_bus_list_item, parent, false);
				holder = new ViewHolder();
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				holder.cancel = (ImageView) convertView
						.findViewById(R.id.delete);
				holder.category = (TextView) convertView
						.findViewById(R.id.category);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.content.setText(data.get(position).getContent());
			String category = data.get(position).getCategory();
			if (category != null) {
				holder.category.setText(category);
			}
			if (showDelete) {
				holder.cancel.setVisibility(View.VISIBLE);
				holder.category.setVisibility(View.GONE);
				final int pos = position;
				holder.cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (v.getId() == R.id.delete) {
							deleteHistory(pos);
						}
					}
				});
			} else {
				holder.cancel.setVisibility(View.GONE);
				holder.category.setVisibility(View.VISIBLE);
			}
			if (isGay) {
				holder.content.setTextColor(colorGay);
				holder.category.setTextColor(colorGay);
			} 
			return convertView;
		}

		class ViewHolder {
			TextView content;
			TextView category;
			ImageView cancel;
		}

		@Override
		public Filter getFilter() {
			return null;
		}

	}
	
	protected void deleteHistory(int position) {}
	protected class BusTextWatcher implements TextWatcher {

		private int which = -1;
		public BusTextWatcher(int which) {
			this.which = which;
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			doTextChanged(s, which);
		}

		@Override
		public void afterTextChanged(Editable s) {
			LogUtil.d(TAG, "afterTextChanged: " + s.toString());
			if (TextUtils.isEmpty(s.toString()) || "当前位置".equals(s.toString())) {
				if (mListView != null && which != -1) {
					mListView.setVisibility(View.GONE);
					if (null != mBtnQuery) {
						mBtnQuery.setVisibility(View.VISIBLE);
					}
				}
				return;
			}
			if (mCurCity == null) {
				mCurCity = "深圳";
			}
			LogUtil.d(TAG, "afterTextChanged: " + s.toString() + " which: " + this.which);
			try {
				boolean sug = mSuggestion.requestSuggestion(new SuggestionSearchOption().keyword(
						s.toString()).city(mCurCity));
				mCurWhich = this.which;
				if (!sug) {
					LogUtil.d(TAG, "requestSuggestion fail");
				}
			} catch (Exception e) {
				LogUtil.d(TAG, "sug: " + e.getMessage());
			}
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mSuggestion.destroy();
		mPoiSearch.destroy();
	}

	protected abstract void doTextChanged(CharSequence s, int which);
}
