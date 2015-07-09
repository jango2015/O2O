package so.contacts.hub.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import so.contacts.hub.charge.ChargeConst;
import so.contacts.hub.ui.yellowpage.bean.ChargeTelephoneHistoryBean;
import com.loader.DataLoader;
import com.yulong.android.contacts.discover.R;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class YellowPageChargeHistoryAdapter extends CustomListViewAdapter {

	private Context mContext = null;
	
	private List<ChargeTelephoneHistoryBean> mHistoryList = new ArrayList<ChargeTelephoneHistoryBean>();
	
	private int mGrayColor = 0;
	
	private int mBlackColor = 0;
	
	private int mGreenColor = 0;
	
	private int mRedColor = 0;
	
//	private OnClickListener mOnClickListener = null;
	
	public YellowPageChargeHistoryAdapter(Context context){
		mContext = context;
		mGrayColor = mContext.getResources().getColor(R.color.putao_pt_deep_gray);
		mBlackColor = mContext.getResources().getColor(R.color.putao_contents_text);
		mGreenColor = mContext.getResources().getColor(R.color.putao_light_green);
		mRedColor = Color.RED;
	}
	
	public void setData(List<ChargeTelephoneHistoryBean> historyList){
		if( historyList == null ){
			return;
		}
		mHistoryList = historyList;
		notifyDataSetChanged();
	}
	
//	public void setOnClickListener(OnClickListener onClickListener){
//		mOnClickListener = onClickListener;
//	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mHistoryList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mHistoryList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View layout = null;
		if( convertView == null ){
			layout = View.inflate(mContext, R.layout.putao_yellow_page_chargehistory_item, null);
		}else{
			layout = convertView;
		}
		ChargeTelephoneHistoryBean historyBean = mHistoryList.get(position);
		layout.setTag(historyBean);
		
		// 标价
		TextView markPriceTView = (TextView) layout.findViewById(R.id.historyitem_markprice);
		markPriceTView.setText(mContext.getResources().getString(R.string.putao_yellow_page_detail_customsprice, historyBean.getRemark_price()));
		
		// 电话号码
		TextView phoneTView = (TextView) layout.findViewById(R.id.historyitem_phone);
		phoneTView.setText(historyBean.getMobile());

		// 售价
		TextView priceHintTView = (TextView) layout.findViewById(R.id.historyitem_price_hint);
		TextView priceTView = (TextView) layout.findViewById(R.id.historyitem_price);
		priceTView.setText(mContext.getResources().getString(R.string.putao_charge_chy_data, historyBean.getPay_price()));

		// 日期
		TextView dateTView = (TextView) layout.findViewById(R.id.historyitem_date);
		View devideView = (View) layout.findViewById(R.id.devide_view);
		String date = historyBean.getCTime(); 
		if( TextUtils.isEmpty(date) ){
			devideView.setVisibility(View.GONE);
			dateTView.setVisibility(View.GONE);
		}else{
			devideView.setVisibility(View.VISIBLE);
			dateTView.setVisibility(View.VISIBLE);
			dateTView.setText(date);
		}
		
		// 优惠券
		TextView favorableTView = (TextView) layout.findViewById(R.id.historyitem_favorable);
		String favorable = historyBean.getFavo_price();
		float favorablePrice = 0;
		try{
			favorablePrice = Float.valueOf(favorable);
		}catch(Exception e){
			favorablePrice = 0;
		}
		if( favorablePrice == 0 ){
			favorableTView.setVisibility(View.GONE);
		}else{
			favorableTView.setVisibility(View.VISIBLE);
			favorableTView.setText(mContext.getResources().getString(R.string.putao_charge_favorable, 
					new DecimalFormat("0.0").format(favorablePrice)));
		}

		// 订单号
		TextView orderTView = (TextView) layout.findViewById(R.id.historyitem_order);
		orderTView.setText(mContext.getResources().getString(R.string.putao_charge_history_order, historyBean.getOrder_id()));
		
		// 订单状态
		TextView stateTView = (TextView) layout.findViewById(R.id.historyitem_state);
		TextView chargeTView = (TextView) layout.findViewById(R.id.historyitem_charge);
		
		int chargeState = historyBean.getCharge_state();
		String chargeStateStr = getStateInfo(chargeState);
		if( TextUtils.isEmpty(chargeStateStr) ){
			//无状态
			chargeTView.setVisibility(View.GONE);
			stateTView.setVisibility(View.GONE);
			priceHintTView.setTextColor(mGrayColor);
			priceTView.setTextColor(mGrayColor);
		}else if( chargeState == ChargeConst.ChargeHistoryStatus_Waitcharge ){
			//状态：等待付款
			chargeTView.setVisibility(View.VISIBLE);
			chargeTView.setText(chargeStateStr);
			stateTView.setVisibility(View.GONE);
			/*
			 * 点击效果修改，改为整条处理，所以将按钮的点击事件注释
			 * modified by hyl 2014-12-23 start
			 */
//			chargeTView.setOnClickListener(mOnClickListener);
			//modified by hyl 2014-12-23 end

			// 改变 售价 颜色
			priceHintTView.setTextColor(mBlackColor);
			priceTView.setTextColor(mGreenColor);
		}else{
			chargeTView.setVisibility(View.GONE);
			stateTView.setVisibility(View.VISIBLE);
			stateTView.setText(chargeStateStr);

			// 改变 售价 颜色
			priceHintTView.setTextColor(mGrayColor);
			priceTView.setTextColor(mGrayColor);
			if( chargeState == ChargeConst.ChargeHistoryStatus_Failed_Return || chargeState == ChargeConst.ChargeHistoryStatus_Failed_Success){
				//状态："充值失败，正在退款"  或 "退款成功"
				stateTView.setTextColor(mRedColor);
			}else{
				stateTView.setTextColor(mGrayColor);
			}
		}
		
		return layout;
	}
	
	private String getStateInfo(int chargeState){
		String stateInfo = "";
		switch(chargeState){
		case ChargeConst.ChargeHistoryStatus_Sussess:
		case ChargeConst.ChargeHistoryStatus_WAIT_RECHARGE:
			//充值成功
			stateInfo = mContext.getResources().getString(R.string.putao_charge_history_success);
			break;
		case ChargeConst.ChargeHistoryStatus_Closed:
			//已关闭
			stateInfo = mContext.getResources().getString(R.string.putao_charge_history_closed);
			break;
		case ChargeConst.ChargeHistoryStatus_Waitcharge:
		case ChargeConst.ChargeHistoryStatus_PAY_FAILED:
			//等待付款
			stateInfo = mContext.getResources().getString(R.string.putao_charge_history_waitcharge);
			break;
		case ChargeConst.ChargeHistoryStatus_Failed_Return:
			//充值失败,正在退款
			stateInfo = mContext.getResources().getString(R.string.putao_charge_history_failed_return);
			break;
		case ChargeConst.ChargeHistoryStatus_Failed_Success:
			//充值失败,退款成功
			stateInfo = mContext.getResources().getString(R.string.putao_charge_history_chargefailed_returnsuccess);
			break;
		default:
			break;
		}
		return stateInfo;
	}

	@Override
	public DataLoader getmImageLoader() {
		// TODO Auto-generated method stub
		return null;
	}

}
