package so.contacts.hub.train.message;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.train.TongChengConfig;
import so.contacts.hub.train.bean.TrainTricketOrderHistoryBean;
import so.contacts.hub.ui.yellowpage.YellowPageJumpH5Activity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.MyCenterConstant;
import so.contacts.hub.util.PinyinHelper;
import so.contacts.hub.util.URLUtil;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.util.UMengEventIds;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.Config;
import so.contacts.hub.msgcenter.AbstractMessageBussiness;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.PTMessageCenterSettings;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;

import com.yulong.android.contacts.discover.R;

public class TrainMsgBusiness extends AbstractMessageBussiness{
	
	private Context mContext;
	protected YellowParams mYellowParams = null;
	private static final String TAG =TrainMsgBusiness.class.getSimpleName();
	private static String CAN_TRAIN_SHOW ="can_train_show";
	private static TrainMsgBusiness mInstance =null;
	private String [] order_status =null;
	
	
	public TrainMsgBusiness(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext =context;
		super.logoId = R.drawable.putao_icon_order_hcp;
		super.smallLogoId = R.drawable.putao_icon_order_huochedingpiao_s;
		super.title = R.string.train;
		super.productType = MsgCenterConfig.Product.train
				.getProductType();
		PTOrderCenter.getInstance().register(this);
		order_status = mContext.getResources().getStringArray(R.array.putao_train_order_status);
		umengInsertDataEventId=UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_TRAIN_TICKET_ITEM_NUM;
	}

	public static TrainMsgBusiness getInstance(Context context){
		if(mInstance==null){
			return new TrainMsgBusiness(context);
		}
		return mInstance;
	}
	
	
	@Override
	public View getOrderView(PTOrderBean bean, View convertView) {
		// TODO Auto-generated method stub
		try {
			TrainTricketOrderHistoryBean trainTicket = new Gson().fromJson(bean.getExpand(), TrainTricketOrderHistoryBean.class);
			if(trainTicket != null){
				ViewHolder holder =null;
				if(convertView ==null){
					convertView =View.inflate(mContext, R.layout.putao_train_order_tagview_layout, null);
					holder = new ViewHolder();
					holder.logo =  (ImageView) convertView.findViewById(R.id.logo);
					holder.titelNameTView = (TextView) convertView.findViewById(R.id.title);
					holder.departStation = (TextView) convertView.findViewById(R.id.train_from);
					holder.departStationPinyin = (TextView) convertView.findViewById(R.id.train_from_pinyin);
					holder.departdate = (TextView) convertView.findViewById(R.id.train_from_data);
					holder.departtime = (TextView) convertView.findViewById(R.id.train_from_time);
					holder.arriveStation = (TextView) convertView.findViewById(R.id.train_to);
					holder. arriveStationPinyin = (TextView) convertView.findViewById(R.id.train_to_pinyin);
//					holder.arrivedate = (TextView) convertView.findViewById(R.id.train_to_data);
//					holder.arrivetime = (TextView) convertView.findViewById(R.id.train_to_time);
					holder. status = (TextView) convertView.findViewById(R.id.payed);
					holder.money = (TextView) convertView.findViewById(R.id.money);
					convertView.setTag(holder);
				}else {
					holder =(ViewHolder) convertView.getTag();
				}
				convertView.setVisibility(View.VISIBLE);
				
				holder.logo.setBackground(mContext.getResources().getDrawable(R.drawable.putao_icon_quick_ditie_s));
				holder.titelNameTView.setText(mContext.getResources().getString(R.string.putao_train_train_number_hint, trainTicket.getTrain_num()));//modify by ls2015-01-22 
				holder.titelNameTView.setTextColor(mContext.getResources().getColor(R.color.putao_black));
				
				
				String depart_station = trainTicket.getDepart_station_name();
				holder.departStation.setText(depart_station);
//				holder.departStationPinyin.setText(PinyinHelper.getInstance().getPinYin(depart_station));
				holder.departStationPinyin.setText(ContactsAppUtils.getInstance().getDatabaseHelper()
						.getTrainDBHelper().getPinYin(depart_station));
				
				String date_depart =trainTicket.getDepart_time();
				holder.departdate.setText(getDate(date_depart));
				holder.departtime.setText(date_depart.substring(11,16));//从时间中截取 小时:分 
				
				String arrive_station = trainTicket.getArrive_station_name();
				holder.arriveStation.setText(arrive_station);
				holder.arriveStationPinyin.setText(ContactsAppUtils.getInstance().getDatabaseHelper()
						.getTrainDBHelper().getPinYin(arrive_station));
				
//				String date_arrive =trainTicket.getArrive_time();
//				holder.arrivedate.setText(getDate(date_arrive));
//				holder.arrivetime.setText(date_arrive.substring(11, 16));
				String status = trainTicket.getOrder_status();
				
				if(!TextUtils.isEmpty(status)){
					if(status.contains(order_status[0])){//待付款
						holder.status.setText(mContext.getResources().getString(R.string.wait_for_pay));
					}else if(status.contains(order_status[1])){//占座中
						holder.status.setText(mContext.getResources().getString(R.string.occupying_seat));
					}else if(status.contains(order_status[2])){//出票中
						holder.status.setText(mContext.getResources().getString(R.string.putao_train_chupiaoing));
					}else if(status.contains(order_status[3])){//已出票
						holder.status.setText(mContext.getResources().getString(R.string.putao_train_chupiaoed));
					}else if(status.contains(order_status[4])){//已发车
						holder.status.setText(mContext.getResources().getString(R.string.departed));
					}else if(status.contains(order_status[5])){//占座失败
						holder.status.setText(mContext.getResources().getString(R.string.occupy_seat_failed));
					}else if(status.contains(order_status[6])){//已取消
						holder.status.setText(mContext.getResources().getString(R.string.train_cancel));
					}else if(status.contains(order_status[7])){//过期
						holder.status.setText(mContext.getResources().getString(R.string.train_expire));
					}else if(status.contains(order_status[8])){//已退票
						holder.status.setText(mContext.getResources().getString(R.string.putao_train_tuipiaoed));
					}else if(status.contains(order_status[9])){//驳回
						holder.status.setText(mContext.getResources().getString(R.string.train_reject));
					}
				}
				holder.money.setText("￥"+trainTicket.getOrder_price());
			}
			/*
             * modify by putao_lhq @start
             * add code
             */
            else {
                return null;
            }
            /*@end by putao_lhq*/
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			/*
             * modify by putao_lhq @start
             * old code
			convertView.setVisibility(View.GONE);
             */
             return null;
            /*@end by putao_lhq*/
		}
		return convertView;
		
	}

	@Override
	public void click(PTMessageBean bean, Activity context) {
	    super.click(bean, context);
	    handleClick(context);
	    
        // add 2014-12-31 xcx start 统计埋点
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_TRAIN_TICKET_ITEM_CLICK);
        // add 2014-12-31 xcx end 统计埋点
	}

	@Override
	public void click(PTOrderBean bean, Activity context) {
		// TODO Auto-generated method stub
		handleClick(context);
		  // add xcx 2014-12-30 start 统计埋点
        MobclickAgentUtil
                .onEvent(
                        ContactsApp.getInstance().getApplicationContext(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_TRAIN_TICKET_ITEM_CLICK);
        // add xcx 2014-12-30 end 统计埋点
	}
	
	/**处理提醒和订单的点击跳转,当前是直接跳到订单列表的H5*/
	public void handleClick(Activity context){
		String url =TongChengConfig.YELLOW_PAGE_TONGCHENG_ORDERQUERY;
		Intent intent = new Intent(mContext, YellowPageJumpH5Activity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		String open_token = PutaoAccount.getInstance().getOpenToken();
		if (!TextUtils.isEmpty(open_token)) {
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put(TongChengConfig.PUTAO_TONGCHENG_TRAIN_REFID, TongChengConfig.PUTAO_TONGCHENG_TRAIN_REFVAL);
            paramMap.put("open_token", open_token);
            
            url = URLUtil.addParamForUrl(url, paramMap);
		}else{
			return ;
		}
		
		String targetActivity = MyCenterConstant.MY_NODE_TONGCHENG_TRAIN;
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra("targetActivityName", targetActivity);
		intent.putExtra("url",url);
		intent.putExtra("title", mContext.getResources().getString(R.string.putao_traintriket));
//		intent.putExtra(name, value);
		mYellowParams = new YellowParams();
		mYellowParams.setUrl(url);
//		mYellowParams.setUrl("http://121.41.60.51:7899/_plugin/head/train.html");
		mYellowParams.setTitle(mContext.getResources().getString(R.string.putao_traintriket));
//			mYellowParams.setUrl("http://121.41.60.51:9200/_plugin/head/train.html");
		intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);
		context.startActivity(intent);
	}
	

	@Override
	public void handleBusiness(PTMessageBean message) {
		LogUtil.d(TAG, message.toString());
		try {
			if(getEnable()&&message.getIs_notify()!=0){
				String expand_param = message.getExpand_param();
				if(expand_param==null){
					return ;
				}
				TrainTricketOrderHistoryBean bean =null;
				bean  = Config.mGson.fromJson(expand_param, TrainTricketOrderHistoryBean.class);
				if(bean == null){
					return ;
				}
				LogUtil.d(TAG, "TrainOrderRemindBean ="+bean.toString());
				
				Intent intent = new Intent(mContext, YellowPageJumpH5Activity.class);
				String url =TongChengConfig.YELLOW_PAGE_TONGCHENG_ORDERQUERY;
				String targetActivity = MyCenterConstant.MY_NODE_TONGCHENG_TRAIN;
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				String open_token = PutaoAccount.getInstance().getOpenToken();
				
	            Map<String, String> paramMap = new HashMap<String, String>();
	            paramMap.put(TongChengConfig.PUTAO_TONGCHENG_TRAIN_REFID, TongChengConfig.PUTAO_TONGCHENG_TRAIN_REFVAL);
	            paramMap.put("open_token", open_token);
	            
	            url = URLUtil.addParamForUrl(url, paramMap);
				
				intent.putExtra("targetActivityName", targetActivity);
				intent.putExtra("url",url);
				intent.putExtra("title", mContext.getResources().getString(R.string.putao_traintriket));
//				intent.putExtra(name, value);
				mYellowParams = new YellowParams();
				mYellowParams.setUrl(url);
//				mYellowParams.setUrl("http://121.41.60.51:7899/_plugin/head/train.html");
				mYellowParams.setTitle(mContext.getResources().getString(R.string.putao_traintriket));
//					mYellowParams.setUrl("http://121.41.60.51:9200/_plugin/head/train.html");
				intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);
				Notification notification = new Notification.Builder(mContext)
						.setContentTitle(message.getSubject())
						.setContentText(message.getDigest())
						.setWhen(System.currentTimeMillis())
						.setSmallIcon(R.drawable.putao_ic_launcher).build();
				sendNotification(notification, intent);
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean isOrderExpire(PTOrderBean order) {
		try {
			String status = "";
			TrainTricketOrderHistoryBean bean = Config.mGson.fromJson(
					order.getExpand(), TrainTricketOrderHistoryBean.class);
			if (bean == null) {
				return false;
			}
			status = bean.getOrder_status();

			/** modify by ls 2015-01-21 修改过期判断的字段 */
			if ((order_status != null)
					&& (status.contains(order_status[5])
							|| status.contains(order_status[6])
							|| status.contains(order_status[7])
							|| status.contains(order_status[8]) || status
								.contains(order_status[9]))) {
				long time = order.getM_time();
				if (System.currentTimeMillis() > time + 24 * 60 * 60 * 1000) {
					return true;
				} else {
					return false;
				}

			} else if (System.currentTimeMillis() > (getMillisTime(bean
					.getDepart_time()) + 3 * 24 * 60 * 60 * 1000)) {// 目前定在出发日期3天后过期
				return true;
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void setEnable(boolean enable) {
		SharedPreferences sp = ContactsApp.getInstance()
				.getSharedPreferences(PTMessageCenterSettings.SHARED_NAME,
						Context.MODE_MULTI_PROCESS);
		sp.edit().putBoolean(CAN_TRAIN_SHOW, enable).commit();
	}

	@Override
	public boolean getEnable() {
		SharedPreferences sp = ContactsApp.getInstance()
				.getSharedPreferences(PTMessageCenterSettings.SHARED_NAME,
						Context.MODE_MULTI_PROCESS);
		return sp.getBoolean(CAN_TRAIN_SHOW, true);
	}

	@Override
	public View getConfigView(Activity context) {
		View view = View.inflate(context,
				R.layout.putao_lottery_remind_setting_layout, null);
		TextView tv = (TextView) view.findViewById(R.id.lottery_setting);
		tv.setText(mContext.getResources().getString(R.string.putao_train_journey_depart_alert));
		return view;
	}
	
	@Override
	public boolean checkMsg(PTMessageBean bean) {
		if (bean != null
				&& bean.getProductType() == MsgCenterConfig.Product.train
						.getProductType()
				&& !TextUtils.isEmpty(bean.getSubject())
				&& !TextUtils.isEmpty(bean.getDigest())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean checkOrder(PTOrderBean bean) {
		// TODO Auto-generated method stub
		if(bean == null || !super.checkOrder(bean)) {
	            return false;
	        }

	        if(TextUtils.isEmpty(bean.getExpand())) {
	            return false;
	        }
		TrainTricketOrderHistoryBean trainTicket = null;
		try {
			trainTicket = new Gson().fromJson(bean.getExpand(),
					TrainTricketOrderHistoryBean.class);
			if (trainTicket != null
					&& !TextUtils.isEmpty(trainTicket.getDepart_station_name())&&!TextUtils.isEmpty(trainTicket.getOrder_status())) {
				return true;
			}
		} catch (JsonSyntaxException e) {
			LogUtil.d(TAG, bean.getExpand());
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
            return false;
		}
		return false;
	}

	@SuppressLint("SimpleDateFormat")
	public long getMillisTime(String date){
		if(TextUtils.isEmpty(date)){
			return 0;
		}
		try {
			SimpleDateFormat sf  = new SimpleDateFormat(CalendarUtil.DATE_FORMATTER_SIX);
			return sf.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**dtae格式 yyyy-MM-dd HH:mm:ss 返回 格式如: 12月11日*/
	@SuppressLint("SimpleDateFormat")
	public String getDate(String date){
		try {
			SimpleDateFormat sf  = new SimpleDateFormat(CalendarUtil.DATE_FORMATTER_SIX);
			Date d = sf.parse(date);
			String result =DateFormat.getDateInstance(DateFormat.FULL).format(d);
			return result.substring(5,10);//从 '2015年1月1日 星期一 ' 中截取1月1日
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private class ViewHolder{
		ImageView logo ;
		TextView titelNameTView;
		TextView departStation ;
		TextView departStationPinyin ;
		TextView departdate ;
		TextView departtime ;
		TextView arriveStation ;
		TextView arriveStationPinyin ;
		TextView arrivedate ;
		TextView arrivetime ;
		TextView status ;
		TextView money;
	}
	
}
