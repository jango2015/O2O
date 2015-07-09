package so.contacts.hub.thirdparty.elong.request;

import so.contacts.hub.thirdparty.elong.bean.HotelListBean;

public class HotelListRequest extends BaseRequestAndResult<HotelListBean> {


	private HotelListBean mRequestData = null;
	
	@Override
	public HotelListBean getRequestData() {
		// TODO Auto-generated method stub
		return mRequestData;
	}

	@Override
	public String getRequestMethod() {
		// TODO Auto-generated method stub
		return "hotel.list";
	}

	@Override
	public void setRequestData(HotelListBean t) {
		// TODO Auto-generated method stub
		mRequestData = t;
	}

}
