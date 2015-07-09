package so.contacts.hub.adapter;

import so.contacts.hub.util.Utils;

import java.util.ArrayList;

import so.contacts.hub.ui.yellowpage.bean.ChargeHistoryItem;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class ChargeTelAdapter extends BaseAdapter {

	private ArrayList<ChargeHistoryItem> historyWordsList;
	private Context context;
	private onDeleteButtonClickListener mOnCopyButtonClickListener;

	public ChargeTelAdapter(Context ctx, ArrayList<ChargeHistoryItem> data) {
		this.historyWordsList = data;
		this.context = ctx;
	}
	
	public void setData(ArrayList<ChargeHistoryItem> data){
		this.historyWordsList = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (null != historyWordsList) {
			return historyWordsList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (null != historyWordsList) {
			return historyWordsList.get(position);
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
			view = View.inflate(context, R.layout.putao_yellow_page_chargetel_history_item, null);
		} else {
			view = convertView;
		}
		//modify 2015-1-06 xcx start格式化号码
		ImageButton copyImgBtn = (ImageButton) view.findViewById(R.id.copy_imgbtn);
		ChargeHistoryItem historyItem = historyWordsList.get(position);
		String phoneNum = historyItem.getPhoneNum();
		copyImgBtn.setTag(phoneNum);
		phoneNum=Utils.formatPhoneNum(phoneNum);
		//modify 2015-1-06 xcx end格式化号码
		TextView phoneNumTView = (TextView) view.findViewById(R.id.phonenum);
		TextView phoneOperatorTView = (TextView) view.findViewById(R.id.phoneoperator);
		phoneNumTView.setText(phoneNum);
		phoneOperatorTView.setText(historyItem.getProvinceAndOperator());
		
		copyImgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String words = (String) v.getTag();
				if(null != mOnCopyButtonClickListener){
					mOnCopyButtonClickListener.onDeleteButtonClicked(position, words);
				}
			}
		});
		
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
