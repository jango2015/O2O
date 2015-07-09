package so.contacts.hub.thirdparty.cinema.bussiness;


import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.gamecenter.utils.SharedPreferenceUtils;
import so.contacts.hub.msgcenter.AbstractMessageBussiness;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.PTMessageCenterSettings;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.msgcenter.bean.PTOrderStatus;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.thirdparty.cinema.CinemaConstants;
import so.contacts.hub.thirdparty.cinema.bean.DetailMovieOrder;
import so.contacts.hub.thirdparty.cinema.ui.MovieOrderDetailsActivity;
import so.contacts.hub.thirdparty.cinema.utils.CinemaUtils;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

/**
 * 电影消息业务类
 * @author peku
 *
 */

@SuppressLint("ResourceAsColor") 
public class MovieMessageBussiness extends AbstractMessageBussiness {

	
	private Context mContext=null;

	private DataLoader imageLoader;
	
	private final static int NOTIFY=1;  //
	private final static int ORDER=2;
	private final static String movieSwitch="MOVIE_SWITCH"; //电影提醒开关字段
	
	//add ljq start 2015/01/21 订单过期时限 1天
	private final static long EXPIRE_TIME = 24*60*60*1000;
	//add ljq end 2015/01/21
	
	
	class TicketHolder 
	{
		ImageView filmIcon;
		TextView cinemaName;
		TextView movieName;
		TextView time;
		TextView seat;
		TextView payState;
		TextView price;
		ImageView movieLogo;
	}
	
	public MovieMessageBussiness(Context cotext) {
		super(cotext);
		super.productType = MsgCenterConfig.Product.cinema.getProductType();// 服务类别 
	    super.logoId=R.drawable.putao_icon_order_dy; //订单view logo，本地生成
	    super.smallLogoId=R.drawable.putao_icon_order_dy_s;
	    super.title=R.string.putao_film_ticket; 
		mContext=cotext;
		PTOrderCenter.getInstance().register(this);
		imageLoader = new ImageLoaderFactory(cotext).getMovieListLoader();
		umengInsertDataEventId=UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_MOVIE_TICKETS_ITEM_NUM;
	}

	@Override
	public View getOrderView(PTOrderBean bean, View convertView) {
		TicketHolder holder = null;
		if(null == convertView){
			convertView = View.inflate(mContext,R.layout.putao_view_order_filmticket, null);
			holder = new TicketHolder();
			holder.filmIcon=(ImageView) convertView.findViewById(R.id.logo);
			holder.cinemaName=(TextView) convertView.findViewById(R.id.title);
			holder.time=(TextView) convertView.findViewById(R.id.tv_filmtime);
			holder.seat=(TextView) convertView.findViewById(R.id.tv_filmseat);
			holder.payState=(TextView) convertView.findViewById(R.id.payed);
			holder.price=(TextView) convertView.findViewById(R.id.money);
			holder.movieName=(TextView) convertView.findViewById(R.id.tv_moviename);
			holder.movieLogo=(ImageView) convertView.findViewById(R.id.orderdetail_movie_logo);
			convertView.setTag(holder);
			holder.filmIcon.setImageResource(R.drawable.icon_dianying);//电影默认图标
		}else{
			holder=(TicketHolder) convertView.getTag();
		}
		
		DetailMovieOrder order = getMovieOrderBeenFromMessage(bean);
		if (order != null) {
			holder.cinemaName.setTextColor(Color.WHITE);
			holder.cinemaName.setText(mContext.getString(R.string.putao_movie_order_name_prefix) + order.getCinema_name());	
			//holder.ticketCode.setText(order.getTicketCode());	//取票码
			String time=CinemaUtils.getNeedFormatTime(CinemaUtils.timeStr2Timestamp(order.getPlay_time()), "HH:mm MM月dd日");
			holder.time.setText(time);	
			
			/*
			 * 当订单包含多个座位信息时，只显示一张
			 * modified by hyl 2015-1-7 start
			 */
			String seats = CinemaUtils.formatOrderItemSeat(order.getSeat());
			String seatInfo = seats;
			if(order.quantity > 1){
				seatInfo = seats.split(",")[0]+"...";
			}
			holder.seat.setText(seatInfo);	
			//modified by hyl 2015-1-7 end
			
			/*
			 * modified by hyl 2015-1-6 start
			 * old code：
			 * holder.payState.setText(PTOrderStatus.getStatusBeen(order.getPt_status()).toString());
			 */
			holder.payState.setText(order.showStatus());
			//modified by hyl 2015-1-6 end
			
			//add by hyl 2015-1-10 start 待付款状态字体显示红色
			if(order.pt_status == PTOrderStatus.WAIT_BUYER_PAY.getStatusInt() && !order.isTimeOut()){
                holder.payState.setTextColor(mContext.getResources().getColor(R.color.putao_text_color_importance));
            }else{
                holder.payState.setTextColor(mContext.getResources().getColor(R.color.putao_text_color_second));
            }
			//add by hyl 2015-1-10 end
			
			/*
			 * 价格应该采用  PTOrderBean 中的价格
			 * modified by hyl 2015-1-23 start
			 * old code:
			 * holder.price.setText("￥"+CinemaUtils.getDouble2(order.getAmount()/100));
			 */
			holder.price.setText("￥"+CinemaUtils.getDouble2(((double)bean.getPrice())/100));
			//modified by hyl 2015-1-23 end
			
			holder.movieName.setText(order.getMovie_name());
			Log.d("Pt_status", order.getPt_status()+"--------------");
			imageLoader.loadData(order.getMovie_photo_url(), holder.movieLogo);
		}
		/*
         * modify by putao_lhq @start
         * add code
         */
        else {
            return null;
        }
        /*@end by putao_lhq*/
		return convertView;
	}

	@Override
	public void click(PTMessageBean bean, Activity context) {
	    super.click(bean, context);
		Intent intent=null;
		
		JSONObject obj;
		intent=new Intent(mContext, MovieOrderDetailsActivity.class);
        try {
            obj = new JSONObject(bean.getExpand_param());
            String pt_order_no = obj.getString("pt_order_no");
            String order_no = obj.getString("order_no");
            intent.putExtra("pt_order_no",pt_order_no);
            intent.putExtra("order_no",order_no);
            intent.putExtra(CinemaConstants.ENTRY_TYPE, CinemaConstants.ORDER_DETAIL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        context.startActivity(intent);
        
        // add 2014-12-31 xcx start 统计埋点
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_MOVIE_TICKET_ITEM_CLICK);
        // add 2014-12-31 xcx end 统计埋点
	}
	
    @Override
    public void click(PTOrderBean bean, Activity context) {
        Intent intent=null;
        DetailMovieOrder orderBeen = getMovieOrderBeenFromMessage(bean);
        intent = new Intent(mContext, MovieOrderDetailsActivity.class);
        intent.putExtra("order_code",orderBeen==null? null:orderBeen.getTrade_no());
        intent.putExtra("pt_order_no",orderBeen==null?null:orderBeen.getOrder_no());
        intent.putExtra(CinemaConstants.ENTRY_TYPE, CinemaConstants.ORDER_DETAIL);
        intent.putExtra(CinemaConstants.MOVIE_PHOTO_URL, orderBeen==null?null:orderBeen.getMovie_photo_url());
        intent.putExtra("fromMyOrder", true);
        context.startActivity(intent);
        
        //add xcx 2014-12-30 start 统计埋点
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_MOVIE_TICKET_ITEM_CLICK);
        //add xcx 2014-12-30 end 统计埋点
    }
	

	@Override
	public void handleBusiness(PTMessageBean message) {
	    if(message == null) {
	        return;
	    }
	    
	    if(message.getIs_notify() == 0) {
	        return;
	    }
	    
	    if (getEnable()) {
    	    JSONObject obj;
            Intent intent = new Intent(mContext, MovieOrderDetailsActivity.class);
            try {
                obj = new JSONObject(message.getExpand_param());
                String pt_order_no=obj.getString("pt_order_no");
                String order_no=obj.getString("order_no");
                intent.putExtra(MovieOrderDetailsActivity.FLAG_PTORDER_CODE,pt_order_no);
                intent.putExtra(MovieOrderDetailsActivity.FLAG_ORDER_CODE,order_no);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    		intent.putExtra(CinemaConstants.ENTRY_TYPE, CinemaConstants.ORDER_DETAIL);
    		Notification notification = new Notification.Builder(mContext)
    		.setContentTitle(message.getSubject())
    		.setContentText(message.getDigest())
    		.setWhen(System.currentTimeMillis())
    		.setSmallIcon(R.drawable.putao_ic_launcher)
    		.build();
    		sendNotification(notification, intent);
	    }
	}

	@Override
	public boolean isOrderExpire(PTOrderBean orderContent) {
		DetailMovieOrder orderBeen = getMovieOrderBeenFromMessage(orderContent);
		if(null != orderBeen){
			
			/*
			 * 重新整理电影票订单是否筛选如历史订单的条件
			 * modified by hyl 2015-1-7 start
			 * old code:
			 	if ("paid".equals(orderBeen.getStatus())||"success".equals(orderBeen.getStatus())) {
	                if (Timestamp.valueOf(orderBeen.getPlay_time()).getTime()>System.currentTimeMillis()) {
	                    return false;
	                }
	            }else {
	                if(CinemaUtils.timeStr2Long(orderBeen.getValid_time())>System.currentTimeMillis())
	                    return false;
	            }
			 */
		    /*
             * modify by putao_lhq at 2015年1月10日 @start
             * fix bug: 2657
             * add code:
             */
		    // modify by putao_ljq at 2015/01/21 一天以后算过期 start
		    if (orderBeen.pt_status == PTOrderStatus.TRADE_SUCCESS.getStatusInt()){
		        if(Timestamp.valueOf(orderBeen.getPlay_time()).getTime() + EXPIRE_TIME < System.currentTimeMillis() ){
		            return true;
		        }
		    }else if(orderBeen.pt_status == PTOrderStatus.ORDER_CANCEL.getStatusInt() ||
		            (orderBeen.pt_status == PTOrderStatus.WAIT_BUYER_PAY.getStatusInt() && orderBeen.isTimeOut()) || 
		            orderBeen.pt_status == PTOrderStatus.REFUND_SUCCESS.getStatusInt() ||
		            orderBeen.pt_status == PTOrderStatus.ORDER_CLOSED.getStatusInt()
		            ){//订单已取消
		        if(orderContent.getM_time() + EXPIRE_TIME < System.currentTimeMillis()){
		            return true;
		        }
		    }
		}
		return false;
	}

	@Override
	public void setEnable(boolean enable) {
		// TODO Auto-generated method stub
		SharedPreferenceUtils.setPreference(PTMessageCenterSettings.SHARED_NAME,movieSwitch , enable);
	}

	@Override
	public boolean getEnable() {
		// TODO Auto-generated method stub
		return SharedPreferenceUtils.getPreference(PTMessageCenterSettings.SHARED_NAME, movieSwitch, true);
	}

	@Override
	public View getConfigView(Activity context) {
		View view = View.inflate(mContext, R.layout.putao_config_movie_hint, null);
		TextView movie_hint = (TextView) view.findViewById(R.id.tv_config_movie_hint);
		movie_hint.setText(R.string.putao_config_movie_hint);
		return view;
	}
	
	/**
	 * @author peku
	 * @param bean 消息统一类
	 * @return 订单View been
	 */
	private DetailMovieOrder getMovieOrderBeenFromMessage(PTOrderBean bean){
		if(checkOrder(bean)){
			String expand_param = bean.getExpand();
			if(!TextUtils.isEmpty(expand_param)){
				DetailMovieOrder order=null;
				try {
					Gson gson = new Gson();
					order = gson.fromJson(expand_param, DetailMovieOrder.class);
					
					order.pt_status = bean.getStatus_code();//add by hyl 2015-1-6
					
					return order;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return order;
			}
		}
		return null;
	}
}
