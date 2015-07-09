//sml
package so.contacts.hub.adapter;

import java.util.List;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.DianpingCoupon;
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

public class YellowPageCouponAdapter extends CustomListViewAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<YelloPageItem> mPageItemList;
	private DataLoader mImageLoader;

	public YellowPageCouponAdapter(Context context,List<YelloPageItem> itemList,DataLoader imageLoader) {
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
		DianpingCoupon deal = (DianpingCoupon) item.getData();
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.putao_detail_coupon_item, null);
			holder = new ViewHolder();
			holder.dealinfoimg = (ImageView) convertView.findViewById(R.id.tuan_info_img);
			holder.dealtitle = (TextView) convertView.findViewById(R.id.tuan_title);
			holder.dealinfo = (TextView) convertView.findViewById(R.id.tuan_info);
			holder.cprice = (TextView) convertView.findViewById(R.id.tuan_current_price);
			holder.lprice = (TextView) convertView.findViewById(R.id.tuan_last_price);
			holder.category = (TextView) convertView.findViewById(R.id.coupon_category);
			holder.region = (TextView) convertView.findViewById(R.id.coupon_region);
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
		holder.cprice.setText(String.format(mContext.getString(R.string.putao_time_suffix),
				String.valueOf(deal.download_count)));
		holder.lprice.setText(R.string.putao_download_count_suffix);

		if(deal.categories.size() != 0 && !TextUtils.isEmpty(deal.categories.get(0))){
			holder.category.setText(deal.categories.get(0));
		}else {
			holder.category.setVisibility(View.GONE);
		}	
		
		if(deal.regions.size() != 0 && !TextUtils.isEmpty(deal.regions.get(0))){
			holder.region.setText(deal.regions.get(0));
			if(deal.categories.size() != 0 && !TextUtils.isEmpty(deal.categories.get(0))){
				holder.category.setText(deal.categories.get(0));
			}else {
				holder.category.setVisibility(View.GONE);
			}				
		}else {
			holder.category.setVisibility(View.GONE);
			if(deal.categories.size() != 0 && !TextUtils.isEmpty(deal.categories.get(0))){
				holder.region.setText(deal.categories.get(0));
			}else {
				holder.region.setVisibility(View.GONE);
			}				
		}		
		
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
		private TextView category;
		private TextView region;
	}
}
