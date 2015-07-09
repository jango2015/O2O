package so.contacts.hub.adapter;

import java.util.List;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.DianpingDeal;
import so.putao.findplug.YelloPageItem;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loader.DataLoader;
import com.yulong.android.contacts.discover.R;

public class YellowPageDealAdapter extends CustomListViewAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<YelloPageItem> mPageItemList;
	private DataLoader mImageLoader;

	public YellowPageDealAdapter(Context context,List<YelloPageItem> itemList,DataLoader imageLoader) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mPageItemList = itemList;
		this.mImageLoader = imageLoader;
	}

	@Override
	public int getCount() {
		if (mPageItemList == null) {
			return 0;
		} else {
			LogUtil.i("count", "count1 :" + String.valueOf(mPageItemList.size()));
			return mPageItemList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (mPageItemList == null) {
			return null;
		} else {
			return mPageItemList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		YelloPageItem item = mPageItemList.get(position);
		DianpingDeal deal = (DianpingDeal) item.getData();
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.putao_detail_deal_item, null);
			holder = new ViewHolder();
			holder.dealinfoimg = (ImageView) convertView.findViewById(R.id.tuan_info_img);
			holder.dealtitle = (TextView) convertView.findViewById(R.id.tuan_title);
			holder.dealinfo = (TextView) convertView.findViewById(R.id.tuan_info);
			holder.cprice = (TextView) convertView.findViewById(R.id.tuan_current_price);
			holder.lprice = (TextView) convertView.findViewById(R.id.tuan_last_price);
			holder.purchase_count = (TextView) convertView.findViewById(R.id.deal_purchase_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}		

		if (TextUtils.isEmpty(item.getPhotoUrl()) || item.getPhotoUrl().endsWith("no_photo_278.png")) {
			holder.dealinfoimg.setImageResource(R.drawable.putao_icon_logo_placeholder);
		} else {
			mImageLoader.loadData(item.getPhotoUrl(), holder.dealinfoimg);
		}

		holder.dealtitle.setText(deal.title);
		holder.dealinfo.setText(deal.description);
		holder.cprice.setText(String.format(mContext.getString(R.string.putao_yellow_page_detail_customsprice),
				String.valueOf(deal.current_price)));
		holder.lprice.setText(String.format(mContext.getString(R.string.putao_yellow_page_detail_customslastprice), 
				String.valueOf(deal.list_price)));
		holder.purchase_count.setText(String.valueOf(String.format(mContext.getString(R.string.putao_purchase_count_prefix), 
				String.valueOf(deal.purchase_count))));
		return convertView;
	}
	
	public void setmPageItemList(List<YelloPageItem> mPageItemList) {
		this.mPageItemList = mPageItemList;
	}

	public DataLoader getmImageLoader() {
		return mImageLoader;
	}

	public void setmImageLoader(DataLoader mImageLoader) {
		this.mImageLoader = mImageLoader;
	}

	public static class ViewHolder {
		private ImageView dealinfoimg;
		private TextView dealtitle;
		private TextView dealinfo;
		private TextView cprice;
		private TextView lprice;
		private TextView purchase_count;
	}
}
