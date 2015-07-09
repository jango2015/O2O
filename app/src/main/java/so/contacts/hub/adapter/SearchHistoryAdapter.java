package so.contacts.hub.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class SearchHistoryAdapter extends BaseAdapter {

	private ArrayList<String> historyWordsList;
	private Context context;
	private onDeleteButtonClickListener mOnCopyButtonClickListener;

	public SearchHistoryAdapter(Context ctx, ArrayList<String> data) {
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
			view = View.inflate(context,
					R.layout.putao_yellow_page_search_history_item, null);
		} else {
			view = convertView;
		}

		String words = historyWordsList.get(position);
		ImageButton copyImgBtn = (ImageButton) view.findViewById(R.id.copy_imgbtn);
		TextView searchWordsTV = (TextView) view.findViewById(R.id.search_words_tv);
		searchWordsTV.setText(words);
		copyImgBtn.setTag(words);
		copyImgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String words = (String) v.getTag();
				if(null != mOnCopyButtonClickListener){
					mOnCopyButtonClickListener.onDeleteButtonClicked(position, words);
				}
			}
		});

		/**
		 * delete code
		 * modify by putao_lhq
		 * coolui6.0
		 * @start 
		if (position == 0) {
			view.findViewById(R.id.divider_view_top).setVisibility(View.VISIBLE);
		} else {
			view.findViewById(R.id.divider_view_top).setVisibility(View.GONE);
		} @end*/
		
		view.setTag(words);

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
