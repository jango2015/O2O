package so.contacts.hub.train.bean;

public class TongChengCity {
	private int stationId;//
	
	private String stationCode;//车站code
	
	private String stationName;//车站名
	
	private String stationPY;//混合拼音
	
	private String quanPin;//车站全拼
	
	private String jianPin;//车站简拼

	public int getStationId() {
		return stationId;
	}

	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	public String getStationCode() {
		return stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getStationPY() {
		return stationPY;
	}

	public void setStationPY(String stationPY) {
		this.stationPY = stationPY;
	}

	public String getQuanPin() {
		return quanPin;
	}

	public void setQuanPin(String quanPin) {
		this.quanPin = quanPin;
	}

	public String getJianPin() {
		return jianPin;
	}

	public void setJianPin(String jianPin) {
		this.jianPin = jianPin;
	}

	@Override
	public String toString() {
		return "TongChengCity [stationId=" + stationId + ", stationCode="
				+ stationCode + ", stationName=" + stationName + ", stationPY="
				+ stationPY + ", quanPin=" + quanPin + ", jianPin=" + jianPin
				+ "]";
	}
	
	

	
	
	
	

}
