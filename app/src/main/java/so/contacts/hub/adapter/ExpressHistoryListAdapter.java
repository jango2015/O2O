package so.contacts.hub.adapter;

import java.util.ArrayList;

import so.contacts.hub.businessbean.ExpressHistoryBean;

import com.yulong.android.contacts.discover.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpressHistoryListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<ExpressHistoryBean> historyList;
	private boolean isDeleteMode = false;

	public ExpressHistoryListAdapter(Context c,
			ArrayList<ExpressHistoryBean> dataList) {
		this.context = c;
		this.historyList = dataList;
	}

	public void setData(ArrayList<ExpressHistoryBean> dataList) {
		this.historyList = dataList;
		notifyDataSetChanged();
	}

	public void setMode(boolean deleteMode) {
		this.isDeleteMode = deleteMode;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (null != historyList) {
			return historyList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (null != historyList) {
			return historyList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = View.inflate(context,
					R.layout.putao_express_history_list_item, null);
			holder.chbox = (CheckBox) convertView
					.findViewById(R.id.express_history_check_box);
			holder.company = (TextView) convertView
					.findViewById(R.id.express_company_name_tv);
			holder.numInfo = (TextView) convertView
					.findViewById(R.id.express_num_tv);
			holder.statusTextView = (TextView) convertView
					.findViewById(R.id.exp_status_textview);
			holder.arrow = (ImageView) convertView
					.findViewById(R.id.arrow_imageview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ExpressHistoryBean hb = historyList.get(position);

		//putao_lhq modify for BUG #1343 start
		if (hb.status == ExpressHistoryBean.STATUS_NODTA) {
			holder.company.setText(hb.comName);
		} else {
			holder.company.setText(context.getString(
					R.string.putao_express_history_item_info, hb.comName, hb.date));
		}
		//putao_lhq modify for BUG #1343 end
		if (isDeleteMode) {
			holder.chbox.setVisibility(View.VISIBLE);
			holder.chbox.setChecked(hb.isCheck);
			holder.arrow.setVisibility(View.GONE);
			holder.statusTextView.setVisibility(View.GONE);
		} else {
			holder.chbox.setVisibility(View.GONE);
			holder.arrow.setVisibility(View.VISIBLE);
			holder.statusTextView.setVisibility(View.VISIBLE);
		}
		
		int statusResId = R.string.putao_express_result_status_complete;
		switch(hb.status){
		case ExpressHistoryBean.STATUS_SIGNOFF:
			statusResId = R.string.putao_express_result_status_complete;
			break;
		case ExpressHistoryBean.STATUS_IN_TRANSIT:
			statusResId = R.string.putao_express_result_status_in_transit;
			break;
		case ExpressHistoryBean.STATUS_UNKNOW:
			statusResId = R.string.putao_express_result_status_unknow;
			break;
		case ExpressHistoryBean.STATUS_NODTA:
			statusResId = -1;
			break;
		}
		
		if( statusResId == -1 ){
			holder.statusTextView.setText("");
		}else{
			holder.statusTextView.setText(statusResId);
		}
		
		holder.numInfo.setText(context.getString(
				R.string.putao_express_history_item_number, hb.num));

		return convertView;
	}

	class ViewHolder {
		CheckBox chbox;
		TextView company;
		TextView numInfo;
		TextView statusTextView;
		ImageView arrow;
	}

}
