package so.contacts.hub.adapter;

import java.util.ArrayList;

import so.contacts.hub.trafficoffence.bean.Vehicle;
import so.contacts.hub.ui.yellowpage.bean.ChargeHistoryItem;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class TrafficOffenceInfoAdapter extends BaseAdapter {

	private ArrayList<Vehicle> historyList;
	private Context context;
	private onDeleteButtonClickListener mOnCopyButtonClickListener;

	public TrafficOffenceInfoAdapter(Context ctx, ArrayList<Vehicle> data) {
		this.historyList = data;
		this.context = ctx;
	}
	
	public void setData(ArrayList<Vehicle> data){
		this.historyList = data;
		notifyDataSetChanged();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		if (null == convertView) {
			view = View.inflate(context, R.layout.putao_yellow_page_traffic_offence_history_item, null);
		} else {
			view = convertView;
		}

		Vehicle historyItem = historyList.get(position);
		String carNum = historyItem.getCar_no();
		TextView carNumTView = (TextView) view.findViewById(R.id.carnum);
		ImageButton copyImgBtn = (ImageButton) view.findViewById(R.id.copy_imgbtn);
		
		carNumTView.setText(carNum);
		copyImgBtn.setTag(carNum);
		copyImgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String words = (String) v.getTag();
				if(null != mOnCopyButtonClickListener){
					mOnCopyButtonClickListener.onDeleteButtonClicked(position, words);
				}
			}
		});
		copyImgBtn.setVisibility(View.INVISIBLE);
		view.setTag(historyItem);

		return view;
	}
	
	public void setOnDeleteButtonClickListener(
			onDeleteButtonClickListener mOnCopyButtonClickListener) {
		this.mOnCopyButtonClickListener = mOnCopyButtonClickListener;
	}

	public interface onDeleteButtonClickListener{
		void onDeleteButtonClicked(int position, String words);
	}

}
