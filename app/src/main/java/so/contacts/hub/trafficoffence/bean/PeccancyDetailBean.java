package so.contacts.hub.trafficoffence.bean;

public class PeccancyDetailBean {
	private String reason;//违章条款
	private String time;//违章时间
	private String point;//扣分
	private String fine;//罚款
	private String address;//违章地点
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public String getFine() {
		return fine;
	}
	public void setFine(String fine) {
		this.fine = fine;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "PeccancyDetailBean [reason=" + reason + ", time=" + time
				+ ", point=" + point + ", fine=" + fine + ", address="
				+ address + "]";
	}

}
