package so.contacts.hub.lottery;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.lottery.bean.LotteryBodyBean;
import so.contacts.hub.lottery.bean.LotteryResultBean;
import so.contacts.hub.msgcenter.AbstractMessageBussiness;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.PTMessageCenterSettings;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.ui.yellowpage.YellowPageJumpH5Activity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.URLUtil;
import so.contacts.hub.util.YellowUtil;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yulong.android.contacts.discover.R;

public class LotteryMessageBusiness extends AbstractMessageBussiness {

	private static final String TAG = LotteryMessageBusiness.class
			.getSimpleName();
	private Context mContext;
	private YellowParams mYellowParams = null;
	private static final String CAN_LOTTERY_SHOW = "can_lottery_show";
	private static LotteryMessageBusiness mInstance = null;
	private String[] order_status = null;
	

	private LotteryMessageBusiness(Context context) {
		// TODO 需要完善;
		super(context);
		super.productType = MsgCenterConfig.Product.lottery.getProductType();
		super.logoId = R.drawable.putao_icon_order_cp;
		super.smallLogoId = R.drawable.putao_icon_order_cp_s;
		super.title = R.string.lottery;
		this.mContext = context;
		PTOrderCenter.getInstance().register(this);
		order_status = context.getResources().getStringArray(
				R.array.putao_lottery_order_status);
		umengInsertDataEventId = UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_LOTTERY_TICKET_ITEM_NUM;
	}

	public static LotteryMessageBusiness getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new LotteryMessageBusiness(context);
		}
		return mInstance;
	}

	@Override
	public View getOrderView(PTOrderBean bean, View convertView) {
		// if(!checkOrder(bean)) {
		// return null;
		// }

		if (convertView == null) {
			convertView = View.inflate(mContext,
					R.layout.putao_lottery_order_tag_layout, null);
		}
		convertView.setVisibility(View.VISIBLE);
		ImageView lotteryLogo = (ImageView) convertView.findViewById(R.id.logo);
		ImageView bigLotteryLogo =(ImageView) convertView.findViewById(R.id.lottery_logo); 
		lotteryLogo.setBackgroundResource(R.drawable.putao_icon_quick_cp_s);
		TextView payment_amount = null;
		try {
			LotteryResultBean lottery = new Gson().fromJson(bean.getExpand(),
					LotteryResultBean.class);
			
			/** 增加竞彩足球和福彩的不同的图标的显示 add by ls 2015-01-21*/
			
			if("竞彩足球".equals(lottery.getBodyBean().getType())){
				bigLotteryLogo.setBackgroundResource(R.drawable.putao_icon_btn_id_caipiao_b);
			}else{
				bigLotteryLogo.setBackgroundResource(R.drawable.putao_icon_btn_id_caipiao_a);
			}
			
			/**add by ls end*/
			
			
			if (lottery != null) {
				TextView lotterySubject = (TextView) convertView
						.findViewById(R.id.title);
				lotterySubject.setText(mContext.getResources().getString(
						R.string.lottery)
						+ "-"
						+ lottery.getBodyBean().getType()
						+ lottery.getBodyBean().getPeriod()
						+ mContext.getResources().getString(
								R.string.putao_lottery_period));
				lotterySubject.setTextColor(mContext.getResources().getColor(R.color.putao_black));
				TextView payed = (TextView) convertView
						.findViewById(R.id.payed);// 彩票状态;
				payed.setText(lottery.getExpand_status());
				TextView lottery_result = (TextView) convertView
						.findViewById(R.id.lottery_result);// 中奖结果
				lottery_result.setText(convertResult(lottery));
				payment_amount = (TextView) convertView
						.findViewById(R.id.money);// 订单金额;
				String price = lottery.getOrder_price();
				DecimalFormat df = new DecimalFormat("#######0.0");
				String p = df.format(Double.valueOf(price));
				payment_amount.setText("￥" + p);
				SimpleDateFormat sf = new SimpleDateFormat(
						CalendarUtil.DATE_FORMATTER_CN_SIX);
				((TextView) convertView.findViewById(R.id.lottery_time))
						.setText(sf.format(lottery.getC_time()));// 购买时间

			}
			/*
			 * modify by putao_lhq @start add code
			 */
			else {
				return null;
			}
			/* @end by putao_lhq */
		} catch (NumberFormatException e) {
			e.printStackTrace();
			/*
			 * modify by putao_lhq @start add code
			 */
			return null;
			/* @end by putao_lhq */
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			/*
			 * modify by putao_lhq @start
			 * 
			 * old code: convertView.setVisibility(View.GONE);
			 */
			return null;
			/* @end by putao_lhq */
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}

		return convertView;
	}

	private String convertResult(LotteryResultBean lottery) {
		if (lottery == null) {
			return "";
		}
		String expand_status = lottery.getExpand_status();
		if (order_status[0].equals(expand_status)
				|| order_status[1].equals(expand_status)
				|| order_status[2].equals(expand_status)) {
			return expand_status;
		}

		LotteryBodyBean body = null;
		try {
			body = lottery.getBodyBean();
			if (body != null) {
				if (order_status[3].equals(expand_status)
						|| order_status[4].equals(expand_status)) {
					return expand_status
							+ body.getBonus()
							+ mContext.getResources().getString(
									R.string.putao_querytel_balance_rmb);
				} else {
					return order_status[5];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void click(PTMessageBean bean, Activity context) {
		super.click(bean, context);
		if (bean == null) {
			return;
		}
		String expand = bean.getExpand_param();
		if (expand == null) {
			return;
		}
		LotteryResultBean result = null;
		try {
			result = new Gson().fromJson(expand, LotteryResultBean.class);
		} catch (JsonSyntaxException e1) {
			LogUtil.d(TAG, "click-JsonSyntaxException: " + e1);
			e1.printStackTrace();
		}

		if (result == null) {
			return;
		}

		/*
		 * modify by cj 2015/01/07 start 彩票的消息提醒传body字段，保护订单号和url等基本信息
		 */
		if (result == null || result.getBodyBean() == null
				|| TextUtils.isEmpty(result.getBodyBean().getUrl())) {
			return;
		}
		onClick(result.getBodyBean().getUrl(), context);
		// modify by cj 2015/01/07 end

		// add 2014-12-31 xcx start 统计埋点
		MobclickAgentUtil
				.onEvent(
						ContactsApp.getInstance().getApplicationContext(),
						UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_LOTTERY_TICKET_ITEM_CLICK);
		// add 2014-12-31 xcx end 统计埋点
	}

	@Override
	public void click(PTOrderBean bean, Activity context) {
		if (bean == null) {
			return;
		}
		String expand = bean.getExpand();
		if (expand == null) {
			return;
		}
		LotteryResultBean result = null;
		try {
			result = new Gson().fromJson(expand, LotteryResultBean.class);
		} catch (JsonSyntaxException e1) {
			LogUtil.d(TAG, "click-JsonSyntaxException: " + e1);
			e1.printStackTrace();
		}

		/*
		 * modify by cj 2015/01/07 start 彩票的消息提醒传body字段，保护订单号和url等基本信息
		 */
		if (result == null || result.getBodyBean() == null
				|| TextUtils.isEmpty(result.getBodyBean().getUrl())) {
			return;
		}
		onClick(result.getBodyBean().getUrl(), context);
		// modify by cj 2015/01/07 end

		// add xcx 2014-12-30 start 统计埋点
		MobclickAgentUtil
				.onEvent(
						ContactsApp.getInstance().getApplicationContext(),
						UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_LOTTERY_TICKET_ITEM_CLICK);
		// add xcx 2014-12-30 end 统计埋点

	}

	private void onClick(String url, Activity context) {
		Intent intent = new Intent(context, YellowPageJumpH5Activity.class);
		if (url != null) {
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			intent.putExtra("targetActivityName",
					YellowPageLotteryOrderDetailActivity.class.getName());
			intent.putExtra("title", mContext.getResources().getString(R.string.putao_lottery_title));

			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put(LotteryConfig.CHANNEL_AID_KEY,
					LotteryConfig.CHANNEL_AID_VAL);
			paramMap.put("open_token", PutaoAccount.getInstance()
					.getOpenToken());
			url = URLUtil.addParamForUrl(url, paramMap);

			mYellowParams = new YellowParams();
			mYellowParams.setUrl(url);
			mYellowParams.setTitle(mContext.getResources().getString(R.string.putao_lottery_title));
			intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);
			context.startActivity(intent);
		}
	}

	@Override
	public void handleBusiness(PTMessageBean message) {
		if (message == null) {
			return;
		}
		if (getEnable()&& message.getIs_notify()!=0) {
			Intent intent = null;
			String exparam = message.getExpand_param();
			if (exparam == null) {
				return;
			}
			LotteryResultBean result = null;
			try {
				result = new Gson().fromJson(exparam, LotteryResultBean.class);
				if (result == null || result.getBody() == null) {
					return;
				}
				intent = new Intent(mContext, YellowPageJumpH5Activity.class);
				String url = result.getBodyBean().getUrl();
				if (url != null) {
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					intent.putExtra("targetActivityName",
							YellowPageLotteryOrderDetailActivity.class
									.getName());
					intent.putExtra("title", mContext.getResources().getString(R.string.putao_lottery_title));
					mYellowParams = new YellowParams();
					mYellowParams.setUrl(URLUtil.addParamForUrl(
							URLUtil.addTokenForUrl(url),
							LotteryConfig.CHANNEL_AID_KEY,
							LotteryConfig.CHANNEL_AID_VAL));
					mYellowParams.setTitle(mContext.getResources().getString(R.string.putao_lottery_title));
					intent.putExtra(YellowUtil.TargetIntentParams,
							mYellowParams);
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			// TODO 具体内容需改进;
			Notification notification = new Notification.Builder(mContext)
					.setContentTitle(message.getSubject())
					.setContentText(message.getDigest())
					.setWhen(System.currentTimeMillis())
					.setSmallIcon(R.drawable.putao_ic_launcher).build();
			if (intent != null) {
				sendNotification(notification, intent);
			}
		}
	}

	@Override
	public boolean checkOrder(PTOrderBean bean) {
		if (bean == null || !super.checkOrder(bean)) {
			return false;
		}

		if (TextUtils.isEmpty(bean.getExpand())) {
			return false;
		}
		//
		// try {
		// LotteryResultBean lottery = new Gson().fromJson(
		// bean.getExpand(), LotteryResultBean.class);
		//
		// LotteryBodyBean bodyBean = lottery.getBodyBean();
		// if(bodyBean == null || TextUtils.isEmpty(bodyBean.getPeriod()) ||
		// TextUtils.isEmpty(bodyBean.getUrl())) {
		// return false;
		// }
		// } catch (Exception e) {
		// LogUtil.d(TAG, bean.getExpand());
		// e.printStackTrace();
		// LogUtil.e(TAG, e.getMessage());
		// return false;
		// }

		LotteryResultBean lottery = null;
		LotteryBodyBean bodyBean = null;
		try {
			lottery = new Gson().fromJson(bean.getExpand(),
					LotteryResultBean.class);
			if (lottery != null) {
				bodyBean = lottery.getBodyBean();
			}
		} catch (Exception e) {
			LogUtil.d(TAG, bean.getExpand());
			e.printStackTrace();
			LogUtil.e(TAG, e.getMessage());
			return false;
		}
		if (bodyBean != null && !TextUtils.isEmpty(bodyBean.getUrl())
				&& !TextUtils.isEmpty(bean.getOrder_no())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean checkMsg(PTMessageBean bean) {
		if (null != bean && productType == bean.getProductType())
			return true;
		return false;
	}

	@Override
	public boolean isOrderExpire(PTOrderBean bean) {
		if (bean == null) {
			return true;
		}
		if (TextUtils.isEmpty(bean.getExpand())) {
			return true;
		}
		LotteryResultBean result = null;
		try {
			result = new Gson().fromJson(bean.getExpand(),
					LotteryResultBean.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return true;
		}

		if (result == null) {
			return true;
		}

		long open_bonus_time = 0;// 2015-01-11 21:30:00开奖时间;
		try {
			SimpleDateFormat sf = new SimpleDateFormat(
					CalendarUtil.DATE_FORMATTER_SIX);
			if (sf != null
					&& result.getBodyBean() != null
					&& !TextUtils.isEmpty(result.getBodyBean()
							.getOpen_bonus_time())) {
				open_bonus_time = sf.parse(
						result.getBodyBean().getOpen_bonus_time()).getTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			open_bonus_time = 0;
		}
		long orderExpireTimes = 24 * 60 * 60 * 1000;// 订单一天后过期;
		
		/**modify by ls 2015-01-22  修改过期订单的判断;*/
		
		String expandStatus =result.getExpand_status();
		if (!TextUtils.isEmpty(expandStatus)) {
			if(order_status[2].equals(expandStatus)||order_status[4].equals(expandStatus)||order_status[5].equals(expandStatus)){
				if (open_bonus_time > 0
						&& (System.currentTimeMillis() > (open_bonus_time + orderExpireTimes))) {
					// 数字彩才有开奖时间
					return true;
				} else if ((System.currentTimeMillis() > (bean.getM_time()+ orderExpireTimes))) {
					// 竞彩没有开奖时间, 用u_time最后更新时间代替
					return true;
				}
			}
		}
		
		/**end*/
		
		return false;

	}

	@Override
	public void setEnable(boolean enable) {
		SharedPreferences sp = ContactsApp.getInstance()
				.getSharedPreferences(PTMessageCenterSettings.SHARED_NAME,
						Context.MODE_MULTI_PROCESS);
		sp.edit().putBoolean(CAN_LOTTERY_SHOW, enable).commit();

	}

	@Override
	public boolean getEnable() {
		SharedPreferences sp = ContactsApp.getInstance()
				.getSharedPreferences(PTMessageCenterSettings.SHARED_NAME,
						Context.MODE_MULTI_PROCESS);
		return sp.getBoolean(CAN_LOTTERY_SHOW, true);
	}

	@Override
	public View getConfigView(Activity context) {
		View view = View.inflate(context,
				R.layout.putao_lottery_remind_setting_layout, null);
		TextView tv = (TextView) view.findViewById(R.id.lottery_setting);
		tv.setText(mContext.getResources().getString(R.string.putao_lottery_remind_text));
		return view;
	}

}
