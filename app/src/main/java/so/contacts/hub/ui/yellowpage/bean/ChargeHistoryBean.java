package so.contacts.hub.ui.yellowpage.bean;


public class ChargeHistoryBean {
	// 订单编号
	private String order_id;
	
//	// 支付时间,yyyy-MM-dd hh:mm:ss
//	private String pay_time;
	
	// 标价
	private String remark_price;
	
	// 支付价格
	private String pay_price;
	
	//产品编号
	private String product_id;
	
//	// 支付状态
//	private String pay_status;
//	// 充值状态
//	private String worker_status;
//	// 退款状态
//	private String refund_status;
	
	// 话费充值历史状态
	private int charge_status;
	
	// 支付宝流水号
	private String trade_no;
	
//	// 退款时间
//	private String refund_time;
	
	//订单创建时间
	private String c_time;
	
	// 优惠劵ID
	private long favo_id;
	
	// 优惠劵金额
	private String favo_price;
	
	/*
	 * 新增支付类型 字段 1-支付宝 2-微信支付
	 * add by hyl 2014-10-13 start
	 */
	public int payType;
	//add by hyl 2014-10-13 end

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

//	public String getPay_time() {
//		return pay_time;
//	}
//
//	public void setPay_time(String pay_time) {
//		this.pay_time = pay_time;
//	}

	public String getRemark_price() {
		return remark_price;
	}

	public void setRemark_price(String remark_price) {
		this.remark_price = remark_price;
	}

	public String getPay_price() {
		return pay_price;
	}

	public void setPay_price(String pay_price) {
		this.pay_price = pay_price;
	}

	public int getCharge_state(){
		return charge_status;
	}
	
	public void setProduct_id(String product_id){
		this.product_id = product_id;
	}
	
	public String getProduct_id(){
		return product_id;
	}
	
	public void setCharge_state(int charge_status){
		this.charge_status = charge_status;
	}
	
	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

//	public String getRefund_time() {
//		return refund_time;
//	}
//
//	public void setRefund_time(String refund_time) {
//		this.refund_time = refund_time;
//	}
	
	public String getCTime(){
		return c_time;
	}
	
	public void setCTime(String cTime){
		this.c_time = cTime;
	}

	public long getFavo_id() {
		return favo_id;
	}

	public void setFavo_id(long favo_id) {
		this.favo_id = favo_id;
	}

	public String getFavo_price() {
		return favo_price;
	}

	public void setFavo_price(String favo_price) {
		this.favo_price = favo_price;
	}
	
}
