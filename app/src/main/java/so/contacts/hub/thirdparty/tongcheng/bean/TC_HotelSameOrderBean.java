package so.contacts.hub.thirdparty.tongcheng.bean;

public class TC_HotelSameOrderBean extends TC_BaseData {

	private static final long serialVersionUID = 1L;

	private String tcOrder; // 同程订单号（rspType = 1；表示重复订单）
	
	private String allianceOrder; //联盟订单号（rspType = 1；表示重复订单）

	public String getTcOrder() {
		return tcOrder;
	}

	public void setTcOrder(String tcOrder) {
		this.tcOrder = tcOrder;
	}

	public String getAllianceOrder() {
		return allianceOrder;
	}

	public void setAllianceOrder(String allianceOrder) {
		this.allianceOrder = allianceOrder;
	}
	
}
