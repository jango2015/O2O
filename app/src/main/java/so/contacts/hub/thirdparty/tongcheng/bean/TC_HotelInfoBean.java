package so.contacts.hub.thirdparty.tongcheng.bean;

public class TC_HotelInfoBean extends TC_BaseData {

	private static final long serialVersionUID = 1L;

	private String img;
	
	private String hotelId;
	
	private String hotelName;  //名称
	
	private String starRatedName; //酒店类型
	
	private double markNum; //评分
	
	private String openingDate; //开业时间
	
	private String address; // 地址

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public String getStarRatedName() {
		return starRatedName;
	}

	public void setStarRatedName(String starRatedName) {
		this.starRatedName = starRatedName;
	}

	public double getMarkNum() {
		return markNum;
	}

	public void setMarkNum(double markNum) {
		this.markNum = markNum;
	}

	public String getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(String openingDate) {
		this.openingDate = openingDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
	
}
