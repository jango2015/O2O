package so.contacts.hub.thirdparty.tongcheng.bean;

import java.util.List;

public class TC_Response_SubmitHotelOrder extends TC_Response_BaseData {

	private static final long serialVersionUID = 1L;
	
	private TC_HotelOrderBean hotelorder = null;
	
	private List<TC_HotelSameOrderBean> sameOrderInfoList;

	public TC_HotelOrderBean getHotelorder() {
		return hotelorder;
	}

	public void setHotelorder(TC_HotelOrderBean hotelorder) {
		this.hotelorder = hotelorder;
	}

	public List<TC_HotelSameOrderBean> getSameOrderInfoList() {
		return sameOrderInfoList;
	}

	public void setSameOrderInfoList(List<TC_HotelSameOrderBean> sameOrderInfoList) {
		this.sameOrderInfoList = sameOrderInfoList;
	}
	
}
