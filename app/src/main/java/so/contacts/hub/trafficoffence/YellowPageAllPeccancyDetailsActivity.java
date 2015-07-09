package so.contacts.hub.trafficoffence;

import java.util.ArrayList;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yulong.android.contacts.discover.R;

import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.trafficoffence.bean.PeccancyDetailBean;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.widget.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class YellowPageAllPeccancyDetailsActivity extends BaseRemindActivity {

	private static final String TAG = "YellowPageAllPeccancyDetailsActivity";
	
	// layout
	private ListView mListView = null;
	private ProgressDialog mProgressDialog = null;
	
	private YellowPageAllPeccancyDetailsListAdapter mAllDataAdapter = null;
	
	private ArrayList<PeccancyDetailBean> mDetailList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_all_peccancy_details);
		parseIntent();
		initView();
	}
	
	private void parseIntent(){
		Intent intent = getIntent();
		if (null == intent) {
			finish();
		}
		
		String json_detail = intent.getStringExtra("peccancydetaillist");
		if(json_detail != null)
		{
			mDetailList = getPeccancyDetailList(json_detail);
		}		
	}
	
	private void initView() {
		((TextView) findViewById(R.id.title)).setText(getIntent().getStringExtra("title"));

		mListView = (ListView) findViewById(R.id.all_peccancy_details_listview);
		mAllDataAdapter = new YellowPageAllPeccancyDetailsListAdapter(this, mDetailList);
		mAllDataAdapter.notifyDataSetChanged();
		mListView.setAdapter(mAllDataAdapter);

		mProgressDialog = new ProgressDialog(this);
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private ArrayList<PeccancyDetailBean> getPeccancyDetailList(String json_detail) {
		ArrayList<PeccancyDetailBean> list =null;
		if(json_detail==null){
			return list;
		}
		list = new ArrayList<PeccancyDetailBean>();
		try {
			JSONArray array = new JSONArray(json_detail);
			for(int i =0;i<array.length();i++){
				PeccancyDetailBean bean = new PeccancyDetailBean();
				JSONObject obj = array.getJSONObject(i);
				bean.setAddress(obj.optString("address"));
				bean.setFine(obj.optString("fine"));
				bean.setPoint(obj.optString("point"));
				bean.setReason(obj.optString("reason"));
				bean.setTime(obj.optString("time"));
				list.add(bean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public class YellowPageAllPeccancyDetailsListAdapter extends BaseAdapter {

		protected Context mContext = null;
		private LayoutInflater mInflater = null;
		private List<PeccancyDetailBean> mDataList = new ArrayList<PeccancyDetailBean>();

		public YellowPageAllPeccancyDetailsListAdapter(Context context,	List<PeccancyDetailBean> itemList) {
			this.mContext = context;
			this.mInflater = LayoutInflater.from(mContext);
			this.mDataList = itemList;
		}
		
		public void setData(List<PeccancyDetailBean> mDataList) {
			this.mDataList = mDataList;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			if (mDataList == null) {
				return 0;
			} else {
				LogUtil.i("count",
						"count1 :" + String.valueOf(mDataList.size()));
				return mDataList.size();
			}
		}

		@Override
		public Object getItem(int position) {
			if (mDataList == null) {
				return null;
			} else {
				return mDataList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PeccancyDetailBean item = mDataList.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.putao_yellow_page_all_peccancy_details_item, null);
				holder = new ViewHolder();
				holder.address = (TextView)convertView.findViewById(R.id.address);
				holder.fine = (TextView)convertView.findViewById(R.id.fine);				
				holder.point = (TextView) convertView.findViewById(R.id.point);
				holder.reason = (TextView) convertView.findViewById(R.id.reason);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				
				holder.address.setText(item.getAddress());
				holder.fine.setText("-"+item.getFine());
				
				/**add by ls for bug#3086 */
				String points = item.getPoint();
				if(!TextUtils.isEmpty(points)){
					if(!points.startsWith("0")){
						holder.point.setText("-"+points);
					}else{
						holder.point.setText(points);
					}
				}
//				holder.point.setText(item.getPoint());
				/**end*/
				
				holder.reason.setText(item.getReason());
				holder.time.setText(item.getTime());
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}			
			
			return convertView;
		}

		public class ViewHolder {
			private TextView address;
			private TextView fine;
			private TextView point;
			private TextView reason;
			private TextView time;
		}

	}

}
