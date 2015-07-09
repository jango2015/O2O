package so.contacts.hub.shuidianmei;


import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.adapter.CustomListViewAdapter;
import so.contacts.hub.charge.ChargeConst;
import so.contacts.hub.shuidianmei.bean.WEGHistoryBean;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loader.DataLoader;
import com.yulong.android.contacts.discover.R;

public class YellowPageWEGHistoryAdapter extends CustomListViewAdapter {

	private Context mContext = null;
	
	private List<WEGHistoryBean> mHistoryList = new ArrayList<WEGHistoryBean>();
	
	private int mGrayColor = 0;
	
	private int mBlackColor = 0;
	
	private int mGreenColor = 0;
	
	private int mRedColor = 0;
	
	private String mTypeText = "";
	
	
	private OnClickListener mOnClickListener = null;
	
	public YellowPageWEGHistoryAdapter(Context context){
		mContext = context;
		mGrayColor = mContext.getResources().getColor(R.color.putao_pt_deep_gray);
		mBlackColor = mContext.getResources().getColor(R.color.putao_contents_text);
		mGreenColor = mContext.getResources().getColor(R.color.putao_light_green);
		mRedColor = Color.RED;
	}
	
	public void setData(List<WEGHistoryBean> historyList){
		if( historyList == null ){
			return;
		}
		mHistoryList = historyList;
		notifyDataSetChanged();
	}
	
	public void setOnClickListener(OnClickListener onClickListener){
		mOnClickListener = onClickListener;
	}
	
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
		// TODO Auto-generated method stub
		View layout = null;
		if( convertView == null ){
			layout = View.inflate(mContext, R.layout.putao_weg_yellow_page_weg_history_item, null);
		}else{
			layout = convertView;
		}
		WEGHistoryBean historyBean = mHistoryList.get(position);
		layout.setTag(historyBean);
		if(historyBean.weg_type != 0){
		    mTypeText = WEGUtil.getRechargeStringByType(mContext,historyBean.weg_type);
		}
		
		//充值类型
		TextView rechargeTypeTView = (TextView) layout.findViewById(R.id.historyitem_recharge_type);
		rechargeTypeTView.setText(mTypeText);
		
		// 标价
		TextView markPriceTView = (TextView) layout.findViewById(R.id.historyitem_markprice);
		markPriceTView.setText(mContext.getResources().getString(R.string.putao_yellow_page_detail_customsprice, historyBean.mark_price));
		
		// 账户
		TextView phoneTView = (TextView) layout.findViewById(R.id.historyitem_account);
		phoneTView.setText(historyBean.account);

		// 售价
		TextView priceHintTView = (TextView) layout.findViewById(R.id.historyitem_price_hint);
		TextView priceTView = (TextView) layout.findViewById(R.id.historyitem_price);
		priceTView.setText(mContext.getResources().getString(R.string.putao_charge_chy_data, historyBean.sale_price));

		// 日期
		TextView dateTView = (TextView) layout.findViewById(R.id.historyitem_date);
		View devideView = (View) layout.findViewById(R.id.devide_view);
		String date = historyBean.c_time; 
		if( TextUtils.isEmpty(date) ){
			devideView.setVisibility(View.GONE);
			dateTView.setVisibility(View.GONE);
		}else{
			devideView.setVisibility(View.VISIBLE);
			dateTView.setVisibility(View.VISIBLE);
			dateTView.setText(date);
		}

		// 订单号
		TextView orderTView = (TextView) layout.findViewById(R.id.historyitem_order);
		orderTView.setText(mContext.getResources().getString(R.string.putao_charge_history_order, historyBean.order_no));
		
		// 订单状态
		TextView stateTView = (TextView) layout.findViewById(R.id.historyitem_state);
		/**
		 * modify by putao_lhq @start
		 * old code: 
		 Button chargeBtn = (Button) layout.findViewById(R.id.historyitem_charge);*/
		 TextView chargeBtn = (TextView)layout.findViewById(R.id.historyitem_charge);/*@end by putao_lhq*/
		
		int chargeState = historyBean.status_code;
		String chargeStateStr = getStateInfo(historyBean.status_code);
		if( TextUtils.isEmpty(chargeStateStr) ){
			//无状态
			chargeBtn.setVisibility(View.GONE);
			stateTView.setVisibility(View.GONE);
			priceHintTView.setTextColor(mGrayColor);
			priceTView.setTextColor(mGrayColor);
		}else if( chargeState == ChargeConst.ChargeHistoryStatus_Waitcharge ){
			//状态：等待付款
			chargeBtn.setVisibility(View.VISIBLE);
			chargeBtn.setText(chargeStateStr);
			stateTView.setVisibility(View.GONE);

			// 改变 售价 颜色
			priceHintTView.setTextColor(mBlackColor);
			priceTView.setTextColor(mGreenColor);
			chargeBtn.setText(chargeStateStr);//add by putao_lhq
		}else{
			chargeBtn.setVisibility(View.GONE);
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
		case TRADE_SUCCESS:
			//充值成功
			stateInfo = mContext.getResources().getString(R.string.putao_charge_history_success);
			break;
		case ORDER_CANCEL:
		case ORDER_OUT_OF_DATE:
			//已关闭
			stateInfo = mContext.getResources().getString(R.string.putao_charge_history_closed);
			break;
		case PAY_FAIL:
		case WAIT_BUYER_PAY:
			//等待付款
			stateInfo = mContext.getResources().getString(R.string.putao_charge_history_waitcharge);
			break;
		case REFUND_PROCESS:
			//充值失败,正在退款
			stateInfo = mContext.getResources().getString(R.string.putao_charge_history_failed_return);
			break;
		case REFUND_SUCCESS:
			//充值失败,退款成功
			stateInfo = mContext.getResources().getString(R.string.putao_charge_history_chargefailed_returnsuccess);
			break;
		case TRADE_PROCESS:
            //支付成功正在充值
            stateInfo = mContext.getResources().getString(R.string.putao_charge_history_pay_sucess);
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
	
	//add ljq start 2015-01-07 暂时将常量修改在此类保存
    
    // 订单取消
    public static final int ORDER_CANCEL = 0;
    // 订单创建 未支付
    public static final int WAIT_BUYER_PAY = 1;
    // 支付失败
    public static final int PAY_FAIL = 2;
    // 支付成功 交易进行中
    public static final int TRADE_PROCESS = 3;
    // 交易成功
    public static final int TRADE_SUCCESS = 4;
    // 支付成功 交易失败 退款中
    public static final int REFUND_PROCESS = 5;
    // 退款成功
    public static final int REFUND_SUCCESS = 6;
    // 订单过期
    public static final int ORDER_OUT_OF_DATE = 7;
    // 暂存订单
    public static final int TEMP_ORDER = 8;
    
    //add ljq start 2015-01-07 暂时将常量修改在此类保存

    
//    private String getStateInfo(int chargeState){  old code
//        String stateInfo = "";
//        switch(chargeState){
//        case ChargeConst.ChargeHistoryStatus_Sussess:
//        case ChargeConst.ChargeHistoryStatus_WAIT_RECHARGE:
//            //充值成功
//            stateInfo = mContext.getResources().getString(R.string.putao_charge_history_success);
//            break;
//        case ChargeConst.ChargeHistoryStatus_Closed:
//            //已关闭
//            stateInfo = mContext.getResources().getString(R.string.putao_charge_history_closed);
//            break;
//        case ChargeConst.ChargeHistoryStatus_Waitcharge:
//        case ChargeConst.ChargeHistoryStatus_PAY_FAILED:
//            //等待付款
//            stateInfo = mContext.getResources().getString(R.string.putao_charge_history_waitcharge);
//            break;
//        case ChargeConst.ChargeHistoryStatus_Failed_Return:
//            //充值失败,正在退款
//            stateInfo = mContext.getResources().getString(R.string.putao_charge_history_failed_return);
//            break;
//        case ChargeConst.ChargeHistoryStatus_Failed_Success:
//            //充值失败,退款成功
//            stateInfo = mContext.getResources().getString(R.string.putao_charge_history_chargefailed_returnsuccess);
//            break;
//        case ChargeConst.CHARGEHISTORYSTATUS_PAY_SUSSESS:
//            //支付成功正在充值
//            stateInfo = mContext.getResources().getString(R.string.putao_charge_history_pay_sucess);
//            break;
//        default:
//            break;
//        }
//        return stateInfo;
//    }
    
    
    
}
