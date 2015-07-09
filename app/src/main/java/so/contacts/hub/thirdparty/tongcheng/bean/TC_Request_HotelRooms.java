package so.contacts.hub.thirdparty.tongcheng.bean;

public class TC_Request_HotelRooms extends TC_BaseData {

	private static final long serialVersionUID = 1L;
	
	private String hotelId;
	
	private String comeDate;
	
	private String leaveDate;
	
	private int page;
	
	private int pageSize;
	
	private int roomTypeId;
	
	private int pricePolicyId;
	
	private int defaultPolicyFlag;
	
	private int isSort;

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public String getComeDate() {
		return comeDate;
	}

	public void setComeDate(String comeDate) {
		this.comeDate = comeDate;
	}

	public String getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(String leaveDate) {
		this.leaveDate = leaveDate;
	}

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

	public int getRoomTypeId() {
		return roomTypeId;
	}

	public void setRoomTypeId(int roomTypeId) {
		this.roomTypeId = roomTypeId;
	}

	public int getPricePolicyId() {
		return pricePolicyId;
	}

	public void setPricePolicyId(int pricePolicyId) {
		this.pricePolicyId = pricePolicyId;
	}

	public int getDefaultPolicyFlag() {
		return defaultPolicyFlag;
	}

	public void setDefaultPolicyFlag(int defaultPolicyFlag) {
		this.defaultPolicyFlag = defaultPolicyFlag;
	}

	public int getIsSort() {
		return isSort;
	}

	public void setIsSort(int isSort) {
		this.isSort = isSort;
	}
	
	public String getBody(){
		StringBuffer bodyBuffer = new StringBuffer();
		bodyBuffer.append("<body>");
		
		bodyBuffer.append("<hotelId>");
		bodyBuffer.append(hotelId);
		bodyBuffer.append("</hotelId>");
		
		bodyBuffer.append("<comeDate>");
		bodyBuffer.append(comeDate);
		bodyBuffer.append("</comeDate>");
		
		bodyBuffer.append("<leaveDate>");
		bodyBuffer.append(leaveDate);
		bodyBuffer.append("</leaveDate>");
		
		if( page != 0 ){
			bodyBuffer.append("<page>");
			bodyBuffer.append(page);
			bodyBuffer.append("</page>");
		}
		
		if( pageSize != 0 ){
			bodyBuffer.append("<pageSize>");
			bodyBuffer.append(pageSize);
			bodyBuffer.append("</pageSize>");
		}

		if( roomTypeId != 0 ){
			bodyBuffer.append("<roomTypeId>");
			bodyBuffer.append(roomTypeId);
			bodyBuffer.append("</roomTypeId>");
		}

		if( pricePolicyId != 0 ){
			bodyBuffer.append("<pricePolicyId>");
			bodyBuffer.append(pricePolicyId);
			bodyBuffer.append("</pricePolicyId>");
		}

		if( defaultPolicyFlag != 0 ){
			bodyBuffer.append("<defaultPolicyFlag>");
			bodyBuffer.append(defaultPolicyFlag);
			bodyBuffer.append("</defaultPolicyFlag>");
		}

		if( isSort != 0 ){
			bodyBuffer.append("<isSort>");
			bodyBuffer.append(isSort);
			bodyBuffer.append("</isSort>");
		}

		bodyBuffer.append("</body>");
		return bodyBuffer.toString();
	}
	
	
	
}
