package so.contacts.hub.thirdparty.tongcheng.bean;

public class TC_Request_HotelRoomsWithGuaranteePolicy extends TC_BaseData {

	private static final long serialVersionUID = 1L;
	
	private String hotelId;
	
	private String comeDate;
	
	private String leaveDate;
	
	private int page;
	
	private int pageSize;
	
	private String roomTypeId;
	
	private String pricePolicyId;
	
	private String defaultPolicyFlag;
	
	private int isSort;
	
	private int isGP;
	
	private String comeTime;
	
	private int isSubmitOrder;
	
	private int rooms;
	
	private int guestCome;

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

	public String getRoomTypeId() {
		return roomTypeId;
	}

	public void setRoomTypeId(String roomTypeId) {
		this.roomTypeId = roomTypeId;
	}

	public String getPricePolicyId() {
		return pricePolicyId;
	}

	public void setPricePolicyId(String pricePolicyId) {
		this.pricePolicyId = pricePolicyId;
	}

	public String getDefaultPolicyFlag() {
		return defaultPolicyFlag;
	}

	public void setDefaultPolicyFlag(String defaultPolicyFlag) {
		this.defaultPolicyFlag = defaultPolicyFlag;
	}

	public int getIsSort() {
		return isSort;
	}

	public void setIsSort(int isSort) {
		this.isSort = isSort;
	}

	public int getIsGP() {
		return isGP;
	}

	public void setIsGP(int isGP) {
		this.isGP = isGP;
	}

	public String getComeTime() {
		return comeTime;
	}

	public void setComeTime(String comeTime) {
		this.comeTime = comeTime;
	}

	public int getIsSubmitOrder() {
		return isSubmitOrder;
	}

	public void setIsSubmitOrder(int isSubmitOrder) {
		this.isSubmitOrder = isSubmitOrder;
	}

	public int getRooms() {
		return rooms;
	}

	public void setRooms(int rooms) {
		this.rooms = rooms;
	}

	public int getGuestCome() {
		return guestCome;
	}

	public void setGuestCome(int guestCome) {
		this.guestCome = guestCome;
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

		bodyBuffer.append("<roomTypeId>");
		bodyBuffer.append(roomTypeId);
		bodyBuffer.append("</roomTypeId>");

		bodyBuffer.append("<pricePolicyId>");
		bodyBuffer.append(pricePolicyId);
		bodyBuffer.append("</pricePolicyId>");
		
		bodyBuffer.append("<comeTime>");
		bodyBuffer.append(comeTime);
		bodyBuffer.append("</comeTime>");
		
		if( isSubmitOrder == 1 ){
			bodyBuffer.append("<isSubmitOrder>");
			bodyBuffer.append(isSubmitOrder);
			bodyBuffer.append("</isSubmitOrder>");
			
			bodyBuffer.append("<isGP>");
			bodyBuffer.append(isGP);
			bodyBuffer.append("</isGP>");
			
			bodyBuffer.append("<rooms>");
			bodyBuffer.append(rooms);
			bodyBuffer.append("</rooms>");

			bodyBuffer.append("<guestCome>");
			bodyBuffer.append(guestCome);
			bodyBuffer.append("</guestCome>");
		}
		
		bodyBuffer.append("</body>");
		return bodyBuffer.toString();
	}
	
}
