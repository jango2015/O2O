package so.contacts.hub.thirdparty.tongcheng.bean;

import java.util.List;

public class TC_Response_HotelRoomsWithPolicy extends TC_Response_BaseData {

	private static final long serialVersionUID = 1L;

	private String hotelId;

	private String imagebaseurl;

	private int totalcount;

	private List<TC_HotelRoomBean> hotelroomlist;

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelid) {
		this.hotelId = hotelid;
	}

	public String getImagebaseurl() {
		return imagebaseurl;
	}

	public void setImagebaseurl(String imagebaseurl) {
		this.imagebaseurl = imagebaseurl;
	}

	public int getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

	public List<TC_HotelRoomBean> getHotelroomlist() {
		return hotelroomlist;
	}

	public void setHotelroomlist(List<TC_HotelRoomBean> hotelroomlist) {
		this.hotelroomlist = hotelroomlist;
	}

}
