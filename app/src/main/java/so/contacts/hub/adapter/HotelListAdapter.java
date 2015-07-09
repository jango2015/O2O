package so.contacts.hub.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.loader.DataLoader;
import com.yulong.android.contacts.discover.R;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelBean;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HotelListAdapter extends CustomListViewAdapter {

	private static final String TAG = "HotelListAdapter";
	
	protected Context mContext = null;
	private LayoutInflater mInflater = null;
	private DataLoader mDataLoader = null;
	private List<TC_HotelBean> mHotelList = new ArrayList<TC_HotelBean>();
	
	public HotelListAdapter(Context context, List<TC_HotelBean> hotelList, DataLoader imageLoader){
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mDataLoader = imageLoader;
		mHotelList = hotelList;
	}
	
	public void setData(List<TC_HotelBean> hotelList){
		mHotelList = hotelList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mHotelList == null ? 0 : mHotelList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mHotelList == null ? null : mHotelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.putao_hotellist_item, null);
			holder = new ViewHolder();
			holder.mImgView = (ImageView) convertView.findViewById(R.id.item_img);
			holder.mNameTView = (TextView) convertView.findViewById(R.id.item_name);
			holder.mLiTView = (TextView) convertView.findViewById(R.id.item_li);
			holder.mKefanTView = (TextView) convertView.findViewById(R.id.item_kefan);
			holder.mMoneyTView = (TextView) convertView.findViewById(R.id.item_money);
			holder.mMarkNumTView = (TextView) convertView.findViewById(R.id.item_marknum);
			holder.mHotelTypeTView = (TextView) convertView.findViewById(R.id.item_hoteltype);
			holder.mAreaTView = (TextView) convertView.findViewById(R.id.item_area);
			holder.mDistanceTView = (TextView) convertView.findViewById(R.id.item_distance);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TC_HotelBean hotelBean = mHotelList.get(position);
		
		mDataLoader.loadData(hotelBean.getImg(), holder.mImgView);
		holder.mNameTView.setText(hotelBean.getHotelName());
		
		holder.mLiTView.setVisibility(View.INVISIBLE);
		float bonusRate = hotelBean.getBonusRate();
		if( bonusRate > 0 ){
			holder.mKefanTView.setVisibility(View.VISIBLE);
		}else{
			holder.mKefanTView.setVisibility(View.INVISIBLE);
		}
		
		double lowestPrice = hotelBean.getLowestPrice();
		holder.mMoneyTView.setText(mContext.getResources().getString(R.string.putao_hotel_item_showmoney, new DecimalFormat("0").format(lowestPrice)));
		
		holder.mMarkNumTView.setText(mContext.getResources().getString(R.string.putao_hotel_item_marknum, new DecimalFormat("0.0").format(hotelBean.getMarkNum())));
		
		String starRatedName = hotelBean.getStarRatedName();
		if( TextUtils.isEmpty(starRatedName) ){
			holder.mHotelTypeTView.setVisibility(View.GONE);
		}else{
			holder.mHotelTypeTView.setText(starRatedName);
			holder.mHotelTypeTView.setVisibility(View.VISIBLE);
		}
		
		String bizSectionName = hotelBean.getBizSectionName();
		if( TextUtils.isEmpty(bizSectionName) ){
			holder.mAreaTView.setVisibility(View.GONE);
		}else{
			holder.mAreaTView.setText(bizSectionName);
			holder.mAreaTView.setVisibility(View.VISIBLE);
		}
		
		double distance = hotelBean.getDistance();
		if( distance > 0 ){
			holder.mDistanceTView.setText(mContext.getResources().getString(R.string.putao_hotel_item_distance, new DecimalFormat("0.00").format(distance / 1000)));
			holder.mDistanceTView.setVisibility(View.VISIBLE);
		}else{
			holder.mDistanceTView.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	public static class ViewHolder {
		public ImageView mImgView;
		public TextView mNameTView;
		public TextView mLiTView;
		public TextView mKefanTView;
		public TextView mMoneyTView;
		public TextView mMarkNumTView;
		public TextView mHotelTypeTView;
		public TextView mAreaTView;
		public TextView mDistanceTView;
	}

	@Override
	public DataLoader getmImageLoader() {
		// TODO Auto-generated method stub
		return null;
	}
}
