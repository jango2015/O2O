package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.ui.yellowpage.bean.Express;
import so.contacts.hub.util.ContactsAppUtils;
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

import com.yulong.android.contacts.discover.R;

public class YellowPageExpressSelectActivity extends BaseRemindActivity implements OnClickListener,OnItemClickListener{

	private List<Express> mExpressList;
	private ListView mListView;
	private IndexBarContacts mIndexBar;
	private TextView mPopTips;
	private HashMap<String, Integer> mAlphaIndexer;
	private YellowPageAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_city_list);
		initViews();
		initData();
	}
	
	private void initViews() {
		findViewById(R.id.back_layout).setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.putao_yellow_page_select_express);
		mListView = (ListView) findViewById(R.id.city_list);
		mListView.setOnItemClickListener(this);
		mPopTips = (TextView) findViewById(R.id.tips);

		mIndexBar = (IndexBarContacts) findViewById(R.id.sideBar);
		mIndexBar.setIndexes(UiHelper.SECTION_ADD_CONTACTS, null);
		mAdapter = new YellowPageAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	private void initData() {
		mExpressList = new ArrayList<Express>();
		String[] specialExpresses = getResources().getStringArray(
				R.array.putao_special_expresses);
		for (int i = 0; i < specialExpresses.length; i++) {
			Express express = new Express();
			String[] items = specialExpresses[i].split(",");
			if(items.length == 1){
				express.setName(specialExpresses[i]);
				mExpressList.add(express);
			}else if(items.length == 2){
				express.setName(items[0]);
				express.setPinyin(items[1]);
				mExpressList.add(express);
			}
		}
		
		YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
		List<Express> expressList = db.getExpressList();
		if(expressList == null){
			expressList = new ArrayList<Express>();
		}
		Collections.sort(expressList, new Comparator<Express>() {
			@Override
			public int compare(Express lhs, Express rhs) {
				String lStr = lhs.getSortKey().toLowerCase(Locale.US);
				String rStr = rhs.getSortKey().toLowerCase(Locale.US);
				return lStr.compareTo(rStr);
			}
		});

		mExpressList.addAll(expressList);

		mAlphaIndexer = new HashMap<String, Integer>();
		initAlphaIndex(mExpressList);

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
					}
					mPopTips.setText(indexChar);
				}

			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (position == 0 || position == 7) {

		} else {
			Intent intent = new Intent();
			intent.putExtra("express", mExpressList.get(position));
			setResult(RESULT_OK, intent);
			finish();
		}
		
	}

	private void initAlphaIndex(List<Express> expresses) {
		for (int i = 0; i < expresses.size(); i++) {
			if(i < 8){
				continue;
			}
			String name = getAlpha(expresses.get(i).getSortKey());
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
			if (mExpressList == null) {
				return 0;
			} else {
				return mExpressList.size();
			}

		}

		@Override
		public Object getItem(int position) {
			if (mExpressList == null) {
				return null;
			} else {
				return mExpressList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String name = mExpressList.get(position).getName();
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.putao_yellow_page_city_list_item, null);
			}
			TextView title = (TextView) convertView
					.findViewById(R.id.city_name);
			TextView category = (TextView) convertView
					.findViewById(R.id.category_name);
			if (position == 0 || position == 7) {
				category.setVisibility(View.VISIBLE);
				title.setVisibility(View.GONE);
				category.setText(name);
			} else {
				category.setVisibility(View.GONE);
				title.setVisibility(View.VISIBLE);
				title.setText(name);
			}
			return convertView;
		}

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
	
	
}
