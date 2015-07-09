package so.contacts.hub.http.bean;

import java.io.Serializable;

/**
 * 订单详细信息
 * @author change
 */
@SuppressWarnings("serial")
public class OrderInfo implements Serializable{
    //add ljq 2014-12-1 start 充值内容 如 话费 水电煤
    public int recharge_content;
    //add ljq 2014-12-1 end 充值内容 如 话费 水电煤
    
	public String product_id;
	
	public int resultStatus;
	
	public String memo;
	
	public boolean success;
	
	public  String service;
	
	public  String partner;
	
	public  String sign;
	
	public  String notify_url;
		
	public  String out_trade_no;
	
	public  String subject;
	
	public  String payment_type;
	
	public  String seller_id;
	
	public  String total_fee;
	
	//标价
	public String mark_price;
	
	public String body;
	
	public String it_b_pay;
	
	/*
	 * 添加微信支付返回值
	 * add by hyl 2014-10-13 start
	 */
	public String appId = "";
	public String partnerId = "";
	public String prepayId = "";
	public String nonceStr = "";
	public long timeStamp = 0;
	public String packageValue = "";
    //add by hyl 2014-10-13 end
    @Override
    public String toString() {
        return "OrderInfo [recharge_type=" + recharge_content + ", product_id=" + product_id
                + ", resultStatus=" + resultStatus + ", memo=" + memo + ", success=" + success
                + ", service=" + service + ", partner=" + partner + ", sign=" + sign
                + ", notify_url=" + notify_url + ", out_trade_no=" + out_trade_no + ", subject="
                + subject + ", payment_type=" + payment_type + ", seller_id=" + seller_id
                + ", total_fee=" + total_fee + ", mark_price=" + mark_price + ", body=" + body
                + ", it_b_pay=" + it_b_pay + ", appId=" + appId + ", partnerId=" + partnerId
                + ", prepayId=" + prepayId + ", nonceStr=" + nonceStr + ", timeStamp=" + timeStamp
                + ", packageValue=" + packageValue + "]";
    }
	
	
	
}
