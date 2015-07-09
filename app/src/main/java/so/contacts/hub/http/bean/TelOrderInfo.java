package so.contacts.hub.http.bean;


import so.contacts.hub.ui.yellowpage.YellowPageChargeResultActivity;

/**
 * 订单详细信息
 * @author change
 */
@SuppressWarnings("serial")
public class TelOrderInfo extends OrderInfo{
	
	public String mobile; 
	
	public TelOrderInfo(){
	    recharge_content = YellowPageChargeResultActivity.CONTENT_TEL;
	}
}
