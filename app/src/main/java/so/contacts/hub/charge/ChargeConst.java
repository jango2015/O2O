package so.contacts.hub.charge;

public class ChargeConst {
	
	// 后台返回码
	public static final int ServRtnStatus_Internal_Error = -1;							// 服务器错误
	public static final int ServRtnStatus_Sign_Error = -2;                              // 安全签名错误
	public static final int ServRtnStatus_Coupon_Error = -3;                            // 优惠券无效
	public static final int ServRtnStatus_Parameter_Error = -4;     					// 参数错误
	public static final int ServRtnStatus_Product_Miss = -5;							// 无产品报价
	public static final int ServRtnStatus_Service_Stop = -7;							// 服务器停机检查
	public static final int ServRtnStatus_Unknow_Error = -99;						    // 服务器未知错误或本地错误
	
	//支付宝返回码
	public static final int AlipayRtnOrderStatus_Success = 9000;                        // 订单支付成功
	public static final int AlipayRtnOrderStatus_Processing = 8000;			            // 订单正在处理中
	public static final int AlipayRtnOrderStatus_Failed = 4000;                         // 订单支付失败
	public static final int AlipayRtnOrderStatus_Parameter_Error = 4001;                // 请求参数错误
	public static final int AlipayRtnOrderStatus_Canceled = 6001;                       // 用户中途取消
	public static final int AlipayRtnOrderStatus_NetError = 6002;                       // 网络连接出错
	
	// 连连返回的充值状态
	public static final int LianLianRtnStatus_Success = 1001;
	public static final int LianLianRtnStatus_Process = 1002;
	public static final int LianLianRtnStatus_Failed 	  = 1003;
	public static final int LianLianRtnStatus_Timeout 	  = 1004;
	public static final int LianLianRtnStatus_CancelRequired 	  = 1005;
	public static final int LianLianRtnStatus_CancelConfirmed 	  = 1006;

	//最终返回给用户的充值状态： 1-充值成功, 2-充值处理中, 3-充值失败 , 4-网络异常，5-超时, 6-服务器繁忙,  10-支付失败
	public static final int ChargeRtnStatus_Ok = 1;
	public static final int ChargeRtnStatus_Pending = 2;
	public static final int ChargeRtnStatus_Failed = 3;
	public static final int ChargeRtnStatus_Neterror = 4;
	public static final int ChargeRtnStatus_Timeout = 5;
	public static final int ChargeRtnStatus_Serv_busy = 6;
	
	public static final int ChargeRtnStatus_Pay_failed = 10;
	
	
	/**
	 * 充值历史 状态值
	 */
	//充值成功(支付成功、充值成功均OK)
	public static final int ChargeHistoryStatus_Sussess = 1;
	//账单已过期，关闭
	public static final int ChargeHistoryStatus_Closed = 2;
	//等待付款
	public static final int ChargeHistoryStatus_Waitcharge = 3;
	//充值失败,正在退款
	public static final int ChargeHistoryStatus_Failed_Return = 4;
	//充值失败,退款成功
	public static final int ChargeHistoryStatus_Failed_Success = 5;
	//正在充值（作为“充值成功”处理）
	public static final int ChargeHistoryStatus_WAIT_RECHARGE = 6;
	//充值失败（作为“等待付款”处理）
	public static final int ChargeHistoryStatus_PAY_FAILED = 7;
	//支付成功(支付成功、充值在进行)
    public static final int CHARGEHISTORYSTATUS_PAY_SUSSESS = 8;
	
	
	
	
}







