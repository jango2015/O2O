package so.contacts.hub.thirdparty.tongcheng.bean;

public class TC_HotelOrderBean extends TC_BaseData {

	private static final long serialVersionUID = 1L;

	private String serialId;
	
	private double amount;

	public String getSerialId() {
		return serialId;
	}

	public void setSerialId(String serialId) {
		this.serialId = serialId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}
