package so.contacts.hub.thirdparty.tongcheng.bean;

import java.util.List;

public class TC_Response_HotelList extends TC_Response_BaseData {
	
	private static final long serialVersionUID = 1L;

	private int page;
	
	private int pageSize;
	
	private int totalPage;
	
	private String imageBaseUrl;
	
	private List<TC_HotelBean> mHotelList;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public String getImageBaseUrl() {
		return imageBaseUrl;
	}

	public void setImageBaseUrl(String imageBaseUrl) {
		this.imageBaseUrl = imageBaseUrl;
	}

	public List<TC_HotelBean> getHotelList() {
		return mHotelList;
	}

	public void setHotelList(List<TC_HotelBean> mHotelList) {
		this.mHotelList = mHotelList;
	}
	
	
}
