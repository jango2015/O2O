package so.contacts.hub.adapter;

import java.util.ArrayList;
import java.util.List;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelRoomBean;
import com.loader.DataLoader;
import com.yulong.android.contacts.discover.R;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HotelRoomInfoAdapter extends BaseAdapter {

	private static final String TAG = "HotelRoomInfoAdapter";
	
	protected Context mContext = null;
	private LayoutInflater mInflater = null;
	private DataLoader mDataLoader = null;
	private List<TC_HotelRoomBean> mHotelRoomList = new ArrayList<TC_HotelRoomBean>();
	
	private boolean mIsExpanded = false; //是否展开(默认为收缩)
	
	private int mMaxShowNum = 5;
	
	public HotelRoomInfoAdapter(Context context, List<TC_HotelRoomBean> hotelRoomList, DataLoader imageLoader, int maxShowNum){
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mDataLoader = imageLoader;
		mHotelRoomList = hotelRoomList;
		mMaxShowNum = maxShowNum;
	}
	
	public void setData(List<TC_HotelRoomBean> hotelRoomList){
		mHotelRoomList = hotelRoomList;
		notifyDataSetChanged();
	}
	
	public void setExpanded(boolean isExpanded){
		mIsExpanded = isExpanded;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if( mIsExpanded ){
			// 展开
			return mHotelRoomList == null ? 0 : mHotelRoomList.size();
		}else{
			// 收缩时
			if( mHotelRoomList == null ){
				return 0;
			}else{
				return mHotelRoomList.size() > mMaxShowNum ? mMaxShowNum : mHotelRoomList.size();
			}
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mHotelRoomList == null ? null : mHotelRoomList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.putao_hotel_roominfo_item, null);
			holder = new ViewHolder();
			
			holder.mImgView = (ImageView) convertView.findViewById(R.id.item_img);
			holder.mNameTView = (TextView) convertView.findViewById(R.id.item_name);
			holder.mMoneyTView = (TextView) convertView.findViewById(R.id.item_money);
			holder.mMoneyKefanTView = (TextView) convertView.findViewById(R.id.item_money_kefan);
			holder.mStateTView = (TextView) convertView.findViewById(R.id.item_hotelstate);
			holder.mLiTView = (TextView) convertView.findViewById(R.id.item_li);
			holder.mDanbaoTView = (TextView) convertView.findViewById(R.id.item_danbao);
			holder.mBedTView = (TextView) convertView.findViewById(R.id.item_bed);
			holder.mBreakfastTView = (TextView) convertView.findViewById(R.id.item_breakfast);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		TC_HotelRoomBean hotelRoomBean = mHotelRoomList.get(position);
		
		mDataLoader.loadData(hotelRoomBean.getPhotoUrl(), holder.mImgView);
		holder.mNameTView.setText(hotelRoomBean.getRoomName());

		int bookingFlag = hotelRoomBean.getBookingFlag();
		if (bookingFlag == 0) {
			// 可预定 
			holder.mStateTView.setTextColor(mContext.getResources().getColor(R.color.putao_white));
			holder.mStateTView.setBackgroundResource(R.drawable.putao_bg_state_green);
			holder.mStateTView.setText(mContext
					.getString(R.string.putao_hotel_state_yuding));
		} else {
			// 不可预定   
			holder.mStateTView.setTextColor(mContext.getResources().getColor(R.color.putao_darkgray));
			holder.mStateTView.setBackgroundResource(R.drawable.putao_bg_white_p);
			holder.mStateTView.setText(mContext
					.getString(R.string.putao_hotel_state_manfang));
		}

		// 价格
		holder.mMoneyTView
				.setText(mContext.getString(R.string.putao_hoteldetail_money,
						hotelRoomBean.getAvgAmount()));

		// 可返金额
		String roomPrize = hotelRoomBean.getRoomPrize();
		if (!TextUtils.isEmpty(roomPrize)) {
			if (roomPrize.contains(";")) {
				roomPrize = roomPrize.split(";")[0];
			}
		}
		if (TextUtils.isEmpty(roomPrize) || "0".equals(roomPrize)) {
			holder.mMoneyKefanTView.setVisibility(View.INVISIBLE);
		} else {
			holder.mMoneyKefanTView.setVisibility(View.VISIBLE);
			holder.mMoneyKefanTView.setText(mContext.getString(
					R.string.putao_hoteldetail_money_kefan, roomPrize));
		}

//      modified by ffh 20150122 注释点担保逻辑
//		// 担保
//		int guaranteeType = hotelRoomBean.getGuaranteeType();
//		if (guaranteeType == 1 || guaranteeType == 2 ) {
//			holder.mDanbaoTView.setVisibility(View.VISIBLE);
//		} else {
//			holder.mDanbaoTView.setVisibility(View.GONE);
//		}

		// 礼包
		int presentFlag = hotelRoomBean.getPresentFlag();
		if (presentFlag > 0) {
			holder.mLiTView.setVisibility(View.VISIBLE);
		} else {
			holder.mLiTView.setVisibility(View.INVISIBLE);
		}

		// 早餐
		String breakfast = hotelRoomBean.getBreakfast();
		if (TextUtils.isEmpty(breakfast)) {
			holder.mBreakfastTView.setVisibility(View.GONE);
		} else {
			holder.mBreakfastTView.setVisibility(View.VISIBLE);
			holder.mBreakfastTView.setText(breakfast);
		}

		// 床型
		String bed = hotelRoomBean.getBed();
		if (TextUtils.isEmpty(bed)) {
			holder.mBedTView.setVisibility(View.INVISIBLE);
		} else {
			holder.mBedTView.setVisibility(View.VISIBLE);
			holder.mBedTView.setText(bed);
		}

		return convertView;
	}

	public static class ViewHolder {
		public ImageView mImgView;
		public TextView mNameTView;
		public TextView mMoneyTView;
		public TextView mMoneyKefanTView;
		public TextView mStateTView;
		public TextView mLiTView;
		public TextView mDanbaoTView;
		public TextView mBedTView;
		public TextView mBreakfastTView;
	}

}
