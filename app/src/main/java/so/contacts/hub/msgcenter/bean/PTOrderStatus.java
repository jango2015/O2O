package so.contacts.hub.msgcenter.bean;


import com.yulong.android.contacts.discover.R;

import so.contacts.hub.payment.data.ResultCode.OrderStatus;

/**
 * 订单状态枚举类，调用getStatus方法传入状态值得到状态枚举对象，toString转成状态字符形式
 * 
 * @author peku
 * 
 */
public enum PTOrderStatus {

	/**
	 * modify by zjh 2015-01-13
	 * 修改订单状态描述
	 */
	ORDER_CANCEL(R.string.putao_order_status_hint_cancel, OrderStatus.Cancel), // 订单取消
	WAIT_BUYER_PAY(R.string.putao_order_status_hint_waitforpayment, OrderStatus.WaitForPayment), // 等待付款
	PAY_FAIL(R.string.putao_order_status_hint_failed, OrderStatus.Failed), // 支付失败
	TRADE_PROCESS(R.string.putao_order_status_hint_pending, OrderStatus.Pending), // 付款成功，等待处理
	TRADE_SUCCESS(R.string.putao_order_status_hint_success, OrderStatus.Success), // 订单交易成功
	REFUND_PROCESS(R.string.putao_order_status_hint_askforrefund, OrderStatus.AskForRefund), // 退款中（暂不显示）
	REFUND_SUCCESS(R.string.putao_order_status_hint_refunded, OrderStatus.Refunded), // 退款成功
	ORDER_CLOSED(R.string.putao_order_status_hint_outofdate, OrderStatus.OutOfDate); // 格瓦拉状态：过期

	private int strStatusResId;		//订单状态描述资源负
	private int intStatus;			//订单状态

	private PTOrderStatus(int strStatus, int intStatus) {
		this.strStatusResId = strStatus;
		this.intStatus = intStatus;
	}

	public int getStatusStr() {
		return this.strStatusResId;
	}

	public int getStatusInt() {
		return this.intStatus;
	}

	public static PTOrderStatus getStatusBeen(int status) {
		for (PTOrderStatus been : PTOrderStatus.values()) {
			if (been.intStatus == status)
				return been;
		}
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "" + this.strStatusResId;
	}
}
