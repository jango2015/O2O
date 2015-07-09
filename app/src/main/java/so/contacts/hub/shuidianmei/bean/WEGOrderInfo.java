package so.contacts.hub.shuidianmei.bean;

import java.io.Serializable;

import so.contacts.hub.http.bean.OrderInfo;
import so.contacts.hub.ui.yellowpage.YellowPageChargeResultActivity;

/**
 * 订单详细信息
 * @author change
 */
@SuppressWarnings("serial")
public class WEGOrderInfo extends OrderInfo implements Serializable{
	
	public String accountNum;
	
	public String company;
	
	public String timeStr;
	
	public int weg_type;
	
	public WEGOrderInfo(){
	    recharge_content = YellowPageChargeResultActivity.CONTENT_WEG;
	}

    @Override
    public String toString() {
        return "WEGOrderInfo [accountNum=" + accountNum + ", company=" + company + ", timeStr="
                + timeStr + ", weg_type=" + weg_type + ", recharge_type=" + recharge_content
                + ", product_id=" + product_id + ", resultStatus=" + resultStatus + ", memo="
                + memo + ", success=" + success + ", service=" + service + ", partner=" + partner
                + ", sign=" + sign + ", notify_url=" + notify_url + ", out_trade_no="
                + out_trade_no + ", subject=" + subject + ", payment_type=" + payment_type
                + ", seller_id=" + seller_id + ", total_fee=" + total_fee + ", mark_price="
                + mark_price + ", body=" + body + ", it_b_pay=" + it_b_pay + ", appId=" + appId
                + ", partnerId=" + partnerId + ", prepayId=" + prepayId + ", nonceStr=" + nonceStr
                + ", timeStamp=" + timeStamp + ", packageValue=" + packageValue + "]";
    }
	
	
	
}
