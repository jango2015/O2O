package so.contacts.hub.thirdparty.tongcheng.message;

import java.util.Calendar;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.msgcenter.AbstractMessageBussiness;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.PTMessageCenterSettings;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.smartscene.BaseDetailAcitvity;
import so.contacts.hub.thirdparty.cinema.utils.CinemaUtils;
import so.contacts.hub.thirdparty.tongcheng.bean.HotelOrderInfoBean;
import so.contacts.hub.thirdparty.tongcheng.ui.YellowPageHotelOrderDetailActivity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

public class HotelMessageBusiness extends AbstractMessageBussiness {

	private String TAG = HotelMessageBusiness.class.getSimpleName();
	
	private Context mContext = null;
	private YellowParams mYellowParams = null;
	private static final String CAN_HOTEL_SHOW = "can_hotel_show";
	private static HotelMessageBusiness mInstance = null;

	private DataLoader mImageLoader = null;
	
	private HotelMessageBusiness(Context context) {
		super(context);
		this.mContext = context;
		super.productType = MsgCenterConfig.Product.hotel.getProductType();
		super.logoId = R.drawable.putao_icon_order_jd;
		super.smallLogoId = R.drawable.icon_btn_id_jiudian;
		super.title = R.string.hotel;
		PTOrderCenter.getInstance().register(this);
		umengInsertDataEventId = UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_HOTEL_ITEM_NUM;
		mImageLoader = new ImageLoaderFactory(context).getYellowPageLoader(R.drawable.putao_a0114, 0);
	}

	public static HotelMessageBusiness getInstance(Context context){
		if(mInstance==null){
			mInstance = new HotelMessageBusiness(context);
		}
		return mInstance;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public View getOrderView(PTOrderBean bean, View convertView) {
	    ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.putao_hotel_order_tagview_layout, null);
            holder = new ViewHolder();
            holder.logoImgView = (ImageView)convertView.findViewById(R.id.logo);
            holder.titelNameTView = (TextView)convertView.findViewById(R.id.title);
            holder.roomNameTView = (TextView)convertView
                    .findViewById(R.id.orderlist_hotel_roomname);
            holder.outTView = (TextView)convertView.findViewById(R.id.orderlist_hotel_out_date);
            holder.inTView = (TextView)convertView.findViewById(R.id.orderlist_hotel_in_date);
            holder.imgView = (ImageView)convertView.findViewById(R.id.orderlist_hotel_img);
            holder.payStatusTView = (TextView)convertView.findViewById(R.id.payed);
            holder.moneyTView = (TextView)convertView.findViewById(R.id.money);
            holder.point = (ImageView)convertView.findViewById(R.id.point);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        convertView.setVisibility(View.VISIBLE);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        convertView.setLayoutParams(lp);
        
        if( bean == null ){
            lp = new AbsListView.LayoutParams(0, 1);
            convertView.setVisibility(View.INVISIBLE);
            convertView.setLayoutParams(lp);
            return convertView;
        }
        
        String expand = bean.getExpand();
        if (TextUtils.isEmpty(expand)) {
            lp = new AbsListView.LayoutParams(0, 1);
            convertView.setVisibility(View.INVISIBLE);
            convertView.setLayoutParams(lp);
            return convertView;
        }
        HotelOrderInfoBean room = null;
        try {
            room = new Gson().fromJson(bean.getExpand(), HotelOrderInfoBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (room == null) {
            lp = new AbsListView.LayoutParams(0, 1);
            convertView.setVisibility(View.INVISIBLE);
            convertView.setLayoutParams(lp);
            return convertView;
        }
        room.setReceiveMsgTime(System.currentTimeMillis());

        holder.logoImgView.setImageResource(smallLogoId);
        holder.titelNameTView.setText(mContext.getString(
                R.string.putao_hotelorderdetail_name_pfrfix, room.getHotel_name()));
        holder.roomNameTView.setText(room.getRoom_name());
        holder.titelNameTView.setTextColor(mContext.getResources().getColor(R.color.putao_white));//mofify by ls for bug 3034;

        Calendar calendar = Calendar.getInstance();
        String checkOutTime = room.getCheckout_time();
        if (!TextUtils.isEmpty(checkOutTime)) {
            calendar.setTime(new Date(checkOutTime));
            holder.outTView.setText(mContext.getString(R.string.putao_calendar_showdate_month,
                    CalendarUtil.getFormatTwoDecimal((calendar.get(Calendar.MONTH) + 1)),
                    CalendarUtil.getFormatTwoDecimal(calendar.get(Calendar.DAY_OF_MONTH))));
        }
        String checkInTime = room.getCheckin_time();
        if (!TextUtils.isEmpty(checkInTime)) {
            calendar.setTime(new Date(checkInTime));
            holder.inTView.setText(mContext.getString(R.string.putao_calendar_showdate_month,
                    CalendarUtil.getFormatTwoDecimal((calendar.get(Calendar.MONTH) + 1)),
                    CalendarUtil.getFormatTwoDecimal(calendar.get(Calendar.DAY_OF_MONTH))));
        }
        mImageLoader.loadData(room.getPic_url(), holder.imgView);
        
        //显示卡片底部  状态描述 与 时间 start
        String orderStateStr = "";
        long msgTime = 0;
        PTMessageBean messageBean = null;//bean.getMessageBean();
        if (messageBean != null) {
            msgTime = messageBean.getTime();
            String digestStr = messageBean.getDigest();
            if ( !TextUtils.isEmpty(digestStr) ) {
                orderStateStr = digestStr;
            }
        }
        if( TextUtils.isEmpty(orderStateStr) ){
            int orderState = room.getOrder_status();
            if( orderState == 1 ){
                orderStateStr = mContext.getString(R.string.putao_hotelorderdetail_state_new);
            }else if( orderState == 2 ){
                orderStateStr = mContext.getString(R.string.putao_hotelorderdetail_state_cancel);
            }else if( orderState == 3 || orderState == 4 || orderState == 5){
                orderStateStr = mContext.getString(R.string.putao_hotelorderdetail_state_confirm);
            }else if( orderState == 6){
                orderStateStr = mContext.getString(R.string.putao_hotelorderdetail_state_zancun);
            }
            
        }
        holder.payStatusTView.setText(orderStateStr);
        
        /*
         * 酒店显示价格 采用PTOrderBean中的价格
         * modified by hyl 2015-1-23 start
         * old code:
         * holder.moneyTView.setText("￥"+CinemaUtils.getDouble2(room.getOrder_amount()));
         */
        holder.moneyTView.setText("￥"+CinemaUtils.getDouble2(((double)bean.getPrice())/100));
        //modified by hyl 2015-1-23 end
        
        //显示卡片底部  状态描述 与 时间 end
        
        holder.payStatusTView.setTextColor(mContext.getResources().getColor(
                R.color.putao_text_color_second));
//        holder.moneyTView.setTextColor(mContext.getResources().getColor(
//                R.color.putao_order_list_subcontent_color));
        return convertView;
    }

	@Override
	public void click(PTMessageBean bean, Activity context) {
	    super.click(bean, context);
		// TODO Auto-generated method stub
		if (bean == null) {
			return;
		}
		String expand = bean.getExpand_param();
		if (expand == null) {
			return;
		}
		
		JSONObject obj = null;
		Intent intent = new Intent(mContext,
				YellowPageHotelOrderDetailActivity.class);
		try {
			obj = new JSONObject(bean.getExpand_param());
			String order_no = obj.getString("order_no");
			intent.putExtra("Order_SerialId", order_no);
			String pt_order_no = obj.getString("pt_order_no");
			PTOrderBean orderBean = PTOrderCenter.getInstance().getOrderByOrderNumber(pt_order_no); //获取酒店订单信息
			if( orderBean != null ){
				HotelOrderInfoBean room = new Gson().fromJson(orderBean.getExpand(), HotelOrderInfoBean.class);
				if( room != null ){
					intent.putExtra("Hotel_Img", room.getPic_url());
					intent.putExtra("Hotel_Order_State", room.getOrder_status());
				}
			}
			context.startActivity(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
        // add 2014-12-31 xcx start 统计埋点
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_HOTEL_ITEM_CLICK);
        // add 2014-12-31 xcx end 统计埋点
	}
	
    @Override
    public void click(PTOrderBean bean, Activity context) {

        // TODO Auto-generated method stub
        if (bean == null) {
            return;
        }
        String expand = bean.getExpand();
        if (TextUtils.isEmpty(expand)) {
            return;
        }

        HotelOrderInfoBean result = null;
        try {
            result = new Gson().fromJson(expand, HotelOrderInfoBean.class); // TODO
                                                                            // 酒店跳转需要的信息

        } catch (JsonSyntaxException e1) {
            e1.printStackTrace();
        }
        if (result == null) {
            return;
        }
        Intent intent = new Intent(context, YellowPageHotelOrderDetailActivity.class);
        intent.putExtra(BaseDetailAcitvity.ORDER_NO, bean.getOrder_no());
        intent.putExtra(BaseDetailAcitvity.ENTRY, bean.getEntry());
        intent.putExtra("Order_SerialId", result.getHotel_order_no());
        intent.putExtra("Hotel_Img", result.getPic_url());
        intent.putExtra("Hotel_Order_State", result.getOrder_status());
        intent.putExtra("Putao_Order_No", result.getPt_order_no());
        context.startActivity(intent);
        
        //add xcx 2014-12-30 start 统计埋点
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_HOTEL_ITEM_CLICK);
        //add xcx 2014-12-30 end 统计埋点
    }
    
	@Override
	public void handleBusiness(PTMessageBean message) {
        if (!getEnable()) {
            return;
        }
        if (message == null) {
            return;
        }
        
        if(message.getIs_notify() == 0) {
            return;
        }
        
        String exparam = message.getExpand_param();
        if (TextUtils.isEmpty(exparam)) {
            return;
        }
        JSONObject obj = null;
        Intent intent = new Intent(mContext, YellowPageHotelOrderDetailActivity.class);
        try {
            obj = new JSONObject(message.getExpand_param());
            String order_no = obj.getString("order_no");
            intent.putExtra("Order_SerialId", order_no);
            String pt_order_no = obj.getString("pt_order_no");
            PTOrderBean orderBean = PTOrderCenter.getInstance().getOrderByOrderNumber(pt_order_no); // 获取酒店订单信息
            intent.putExtra(BaseDetailAcitvity.ORDER_NO, pt_order_no);
            intent.putExtra(BaseDetailAcitvity.ENTRY, BaseDetailAcitvity.ENTRY_NOTIFICATION);
            if (orderBean != null) {
                HotelOrderInfoBean room = new Gson().fromJson(orderBean.getExpand(),
                        HotelOrderInfoBean.class);
                if (room != null) {
                    intent.putExtra("Hotel_Img", room.getPic_url());
                    intent.putExtra("Hotel_Order_State", room.getOrder_status());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        if(message.getIs_notify() != 0) {
            Notification notification = new Notification.Builder(mContext)
                    .setContentTitle(message.getSubject()).setContentText(message.getDigest())
                    .setWhen(System.currentTimeMillis()).setSmallIcon(notificationLogoId).build();
            sendNotification(notification, intent);
        }
    }

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOrderExpire(PTOrderBean orderContent) {
		// TODO Auto-generated method stub
		if( orderContent == null ){
			return true;
		}
		String expand = orderContent.getExpand();
		if( TextUtils.isEmpty(expand) ){
			return true;
		}
		HotelOrderInfoBean room = null;
		try{
			room = new Gson().fromJson(expand, HotelOrderInfoBean.class);
			if( room == null ){
				return true;
			}
			int orderStatus = room.getOrder_status();
			if( orderStatus == 2 ){
				//取消状态  -> 已过期(到一天后算作过期)
				long cancelTime = orderContent.getM_time();
				if( cancelTime == 0 ){
					return true;
				}
				String cancelDate = CalendarUtil.getNowDateStr(new Date(cancelTime));
				if( TextUtils.isEmpty(cancelDate) ){
					return true;
				}
				String nowDate = CalendarUtil.getNowDateStr();
				if( CalendarUtil.getGapBetweenTwoDay(cancelDate, nowDate) > 0 ){
					//取消时间超过一天
					return true;
				}
			}else{
				//判断 "最晚离店日期" 是否过期
				String checkout_time = room.getCheckout_time();
				if( TextUtils.isEmpty(checkout_time) ){
					return true;
				}
				String startDate = CalendarUtil.getNowDateStr(new Date(checkout_time));
				String nowDate = CalendarUtil.getNowDateStr();
				if( CalendarUtil.getGapBetweenTwoDay(startDate, nowDate) > 0 ){
					//时间超过最晚离店日期
					return true;
				}
			}
			
		}catch(Exception e){
		    LogUtil.e(TAG, e.getMessage());
		    e.printStackTrace();
		}
		return false;
	}

	@Override
	public void setEnable(boolean enable) {
		// TODO Auto-generated method stub
		 SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(PTMessageCenterSettings.SHARED_NAME, 
	                Context.MODE_MULTI_PROCESS);
		 sp.edit().putBoolean(CAN_HOTEL_SHOW, enable).commit();
		
	}

	@Override
	public boolean getEnable() {
		SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(PTMessageCenterSettings.SHARED_NAME, 
                Context.MODE_MULTI_PROCESS);
		return sp.getBoolean(CAN_HOTEL_SHOW, true);
	}

	@Override
	public View getConfigView(Activity context) {
		// TODO Auto-generated method stub
		if( context == null ){
			return null;
		}
		View view = View.inflate(context, R.layout.putao_lottery_remind_setting_layout, null);
		TextView tv = (TextView) view.findViewById(R.id.lottery_setting);
		tv.setText(context.getString(R.string.putao_hotel_configview_hint));
		return view;
	}
	
	private class ViewHolder {
        private ImageView logoImgView;

        private TextView titelNameTView;

        private TextView roomNameTView;

        private TextView outTView;

        private TextView inTView;

        private ImageView imgView;

        private TextView payStatusTView;

        private TextView moneyTView;

        private ImageView point;
    }

}
