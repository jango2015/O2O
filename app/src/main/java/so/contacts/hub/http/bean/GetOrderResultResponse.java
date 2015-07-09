package so.contacts.hub.http.bean;

public class GetOrderResultResponse extends BaseResponseData {

	private String payStatus;
	
	private String tradeStatus;

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}
}
