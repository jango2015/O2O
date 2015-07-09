package so.contacts.hub.shuidianmei;

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

public class WEGAccountAdapter extends BaseAdapter {

	private ArrayList<String> historyWordsList;
	private Context context;
	private onDeleteButtonClickListener mOnCopyButtonClickListener;

	public WEGAccountAdapter(Context ctx, ArrayList<String> data) {
		this.historyWordsList = data;
		this.context = ctx;
	}
	
	public void setData(ArrayList<String> data){
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
			view = View.inflate(context, R.layout.putao_weg_yellow_page_weg_account_history_item, null);
		} else {
			view = convertView;
		}

		String accountNum = historyWordsList.get(position);
		TextView accountNumTView = (TextView) view.findViewById(R.id.accountnum);
		ImageButton clearImgBtn = (ImageButton) view.findViewById(R.id.copy_imgbtn);
		
		accountNumTView.setText(accountNum);
		clearImgBtn.setTag(accountNum);
		clearImgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String words = (String) v.getTag();
				if(null != mOnCopyButtonClickListener){
					mOnCopyButtonClickListener.onDeleteButtonClicked(position, words);
				}
			}
		});
		
		view.setTag(accountNum);

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
