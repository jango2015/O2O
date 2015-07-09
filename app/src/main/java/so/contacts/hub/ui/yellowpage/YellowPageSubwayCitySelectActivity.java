package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.ui.yellowpage.bean.SubwayCity;
import so.contacts.hub.ui.yellowpage.bean.SubwayManager;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UiHelper;
import so.contacts.hub.widget.IndexBarContacts;
import so.contacts.hub.widget.IndexBarContacts.OnIndexChangeListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import so.contacts.hub.util.MobclickAgentUtil;
import com.yulong.android.contacts.discover.R;

/**
 * 选择地铁城市
 * @author putao_lhq
 *
 */
public class YellowPageSubwayCitySelectActivity extends BaseRemindActivity implements
		OnClickListener, OnItemClickListener {

	private ListView mListView;
	private IndexBarContacts mIndexBar;
	private TextView mPopTips;
	private YellowPageAdapter mAdapter;
	private HashMap<String, Integer> mAlphaIndexer;
	private List<SubwayCity> mCityList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_city_list);
		initViews();
		initData();
	}

	private void initData() {
		mCityList = SubwayManager.getCities();
		if(mCityList == null){
			mCityList = new ArrayList<SubwayCity>();
		}
		Collections.sort(mCityList, new Comparator<SubwayCity>() {
			@Override
			public int compare(SubwayCity lhs, SubwayCity rhs) {
				String lStr = lhs.getCityPY();
				String rStr = rhs.getCityPY();
				return lStr.compareTo(rStr);
			}
		});

		mAlphaIndexer = new HashMap<String, Integer>();
		initAlphaIndex(mCityList);

		mAdapter.notifyDataSetChanged();

		mIndexBar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mPopTips.setVisibility(View.VISIBLE);
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					mPopTips.setVisibility(View.GONE);
					break;
				}
				return false;
			}
		});

		mIndexBar.setOnIndexChangeListener(new OnIndexChangeListener() {

			@Override
			public void onChange(int index, String indexChar) {
				if (mAdapter != null) {
					int pos = -1;

					if (mAlphaIndexer.containsKey(indexChar)) {
						pos = mAlphaIndexer.get(indexChar);
					}
					if (pos != -1) {
						mListView.setSelection(pos);
						mPopTips.setText(indexChar);
					}
				}

			}
		});
	}

	private void initViews() {
		findViewById(R.id.back_layout).setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.putao_yellow_page_city_list);
		mListView = (ListView) findViewById(R.id.city_list);
		mListView.setOnItemClickListener(this);
		mPopTips = (TextView) findViewById(R.id.tips);

		mIndexBar = (IndexBarContacts) findViewById(R.id.sideBar);
		mIndexBar.setIndexes(UiHelper.SECTION_ADD_CONTACTS, null);
		mAdapter = new YellowPageAdapter(this);
		mListView.setAdapter(mAdapter);

	}

	private void initAlphaIndex(List<SubwayCity> specialCityList) {
		for (int i = 0; i < specialCityList.size(); i++) {
			String name = getAlpha(specialCityList.get(i).getCityPY());
			if (!mAlphaIndexer.containsKey(name)) {
				mAlphaIndexer.put(name, i);
			}
		}
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);

		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(Locale.US);
		} else {
			return "#";
		}
	}

	class YellowPageAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater mInflater;

		public YellowPageAdapter(Context context) {
			this.mContext = context;
			this.mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			if (mCityList == null) {
				return 0;
			} else {
				return mCityList.size();
			}

		}

		@Override
		public Object getItem(int position) {
			if (mCityList == null) {
				return null;
			} else {
				return mCityList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String name = mCityList.get(position).getCityName();
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.putao_yellow_page_city_list_item, null);
			}
			TextView title = (TextView) convertView
					.findViewById(R.id.city_name);
			title.setVisibility(View.VISIBLE);
			title.setText(name);
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_layout) {
			finish();
		} else {
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.putExtra("cityName", mCityList.get(position).getCityName());
		setResult(RESULT_OK, intent);
		finish();
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
		return false;
	}

	@Override
	public Integer remindCode() {
		return null;
	}

}
