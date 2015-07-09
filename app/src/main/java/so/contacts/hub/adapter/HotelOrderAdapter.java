package so.contacts.hub.adapter;

import so.contacts.hub.thirdparty.elong.bean.EnumOrderStatus;
import so.contacts.hub.util.CalendarUtil;
import android.widget.TextView;
import com.loader.DataLoader;
import com.yulong.android.contacts.discover.R;
import android.content.Context;
import so.contacts.hub.thirdparty.elong.bean.OrderHistory;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;

public class HotelOrderAdapter extends CustomListViewAdapter {

	private List<OrderHistory> mOrderHistoryList = null;
	
	private Context mContext = null;
	
	private int mGreenColor = 0;
	
	private int mGrayColor = 0;
	
	public HotelOrderAdapter(Context context, List<OrderHistory> orderHistoryList){
		mContext = context;
		mGreenColor = mContext.getResources().getColor(R.color.putao_light_green);
		mGrayColor = mContext.getResources().getColor(R.color.putao_pt_deep_gray);
		mOrderHistoryList = orderHistoryList;
	}
	
	public void setData(List<OrderHistory> orderHistoryList){
		mOrderHistoryList = orderHistoryList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mOrderHistoryList == null ? 0 : mOrderHistoryList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mOrderHistoryList == null ? null : mOrderHistoryList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if( convertView == null ){
			convertView = View.inflate(mContext, R.layout.putao_myorder_hotel_item, null);
		}
		TextView nameTView = (TextView) convertView.findViewById(R.id.hotelitem_name);
		TextView typeTView = (TextView) convertView.findViewById(R.id.hotelitem_type);
		TextView moneyTView = (TextView) convertView.findViewById(R.id.hotelitem_paymoney);
		TextView inDateTView = (TextView) convertView.findViewById(R.id.hotelitem_indate);
		TextView outDateTView = (TextView) convertView.findViewById(R.id.hotelitem_outdate);
		TextView stateTView = (TextView) convertView.findViewById(R.id.hotelitem_state);
		
		OrderHistory orderHistory = mOrderHistoryList.get(position);
		nameTView.setText(orderHistory.getHotelName());
		typeTView.setText(orderHistory.getRoomTypeName());
		moneyTView.setText(String.format(mContext.getResources().getString(R.string.putao_order_item_showmoney), 
				orderHistory.getTotalPrice().intValue()));
		inDateTView.setText(String.format(mContext.getResources().getString(R.string.putao_order_item_indate), 
				CalendarUtil.getFormatDate(orderHistory.getArrivalDate())));
		outDateTView.setText(String.format(mContext.getResources().getString(R.string.putao_order_item_outdate), 
				CalendarUtil.getFormatDate(orderHistory.getDepartureDate())));
		EnumOrderStatus statusEnum = orderHistory.getStatus();
		String status = statusEnum.value();
		if( EnumOrderStatus.N.value().equals(status) ){
			// 状态：新单 - 显示：处理中 - 绿色
			stateTView.setText(mContext.getResources().getString(R.string.putao_order_item_status_N));
			stateTView.setTextColor(mGreenColor);
		}else if( EnumOrderStatus.A.value().equals(status) ){
			// 状态：已确认- 显示：已确认- 绿色
			stateTView.setText(mContext.getResources().getString(R.string.putao_order_item_status_A));
			stateTView.setTextColor(mGreenColor);
		}else if( EnumOrderStatus.D.value().equals(status) || EnumOrderStatus.E.value().equals(status) ){
			// 状态：删除/取消- 显示：取消 - 灰色
			stateTView.setText(mContext.getResources().getString(R.string.putao_order_item_status_D));
			stateTView.setTextColor(mGrayColor);
		}else if( EnumOrderStatus.C.value().equals(status) ){
			// 状态：结账 - 显示：已结账 -灰色
			stateTView.setText(mContext.getResources().getString(R.string.putao_order_item_status_C));
			stateTView.setTextColor(mGrayColor);
		}else{
			stateTView.setText("");
		}
		convertView.setTag(orderHistory);
		return convertView;
	}

	@Override
	public DataLoader getmImageLoader() {
		// TODO Auto-generated method stub
		return null;
	}

}
