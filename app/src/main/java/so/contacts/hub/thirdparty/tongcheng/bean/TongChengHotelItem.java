package so.contacts.hub.thirdparty.tongcheng.bean;

import so.putao.findplug.SourceItemObject;

@SuppressWarnings("serial")
public class TongChengHotelItem extends SourceItemObject {

	private String hotelId;
	private String HotelName;
	private int StarRate;
	private int Category;
	private double Latitude;
	private double Longitude;
	private String Address;
	private String Phone;
	private String ThumbNailUrl;
	private String City;
	private String District;
	private String BusinessZone;
	private double Distance;
    private String StarRatedName;
    private double MarkNum;
	public double getMarkNum() {
        return MarkNum;
    }

    public void setMarkNum(double markNum) {
        MarkNum = markNum;
    }

    public String getStarRatedName() {
        return StarRatedName;
    }

    public void setStarRatedName(String starRatedName) {
        StarRatedName = starRatedName;
    }

    public TongChengHotelItem() {

	}

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public String getHotelName() {
		return HotelName;
	}

	public void setHotelName(String hotelName) {
		HotelName = hotelName;
	}

	public int getStarRate() {
		return StarRate;
	}

	public void setStarRate(int starRate) {
		StarRate = starRate;
	}

	public int getCategory() {
		return Category;
	}

	public void setCategory(int category) {
		Category = category;
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		Longitude = longitude;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getThumbNailUrl() {
		return ThumbNailUrl;
	}

	public void setThumbNailUrl(String thumbNailUrl) {
		ThumbNailUrl = thumbNailUrl;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getDistrict() {
		return District;
	}

	public void setDistrict(String district) {
		District = district;
	}

	public String getBusinessZone() {
		return BusinessZone;
	}

	public void setBusinessZone(String businessZone) {
		BusinessZone = businessZone;
	}

	public double getDistance() {
		return Distance;
	}

	public void setDistance(double distance) {
		Distance = distance;
	}

}
