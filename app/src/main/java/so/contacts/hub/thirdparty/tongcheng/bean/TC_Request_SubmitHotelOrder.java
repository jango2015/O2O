package so.contacts.hub.thirdparty.tongcheng.bean;

import android.text.TextUtils;

public class TC_Request_SubmitHotelOrder extends TC_BaseData {

	private static final long serialVersionUID = 1L;
	
	private String hotelId;						//酒店id
	
	private String roomTypeId;					//房型id
	
	private String policyId;					//政策id
	
	private String daysAmountPrice;				//每日房价
	
	private String comeDate;
	
	private String leaveDate;
	
	private int rooms;							//房间数
	
	private String arriveTime;					//到店时间(注：当日 如;1900-01-01 18:00 ；次日 如:1900-01-02 01:00。1900-01-01 代表当日；1900-01-02 代表次日 )
	
	private String TotalAmountPrice;			//总房价
	
	private String contactName;					//预订人姓名
	
	private String contactMobile;				//预订人手机
	
	private String guestName;					//入住人姓名
	
	private String guestMobile;					//入住人手机
	
	// 担保信息（有担保必填，无担保不要如下信息）start
	private String cardNumber;					//卡号
	
	private String cardType;					//卡种
	
	private String valiCode;					//验证码(当卡号不为空时，必传节点内容需要加密)
	
	private String masterName;					//持卡人姓名
	
	private String masterMobileNumber;			//持卡人手机号码
	
	private String periodDate;					//有效期
	
	private String certificatesType;			//持卡人证件类型
	
	private String certificatesNumber;			//持卡人证件号码
	// 担保信息 end

	private String orderIP;						//下单IP
	
	private int isReturnOrderInfo;				//是否返回订单详情(1 是;0 否)

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public String getRoomTypeId() {
		return roomTypeId;
	}

	public void setRoomTypeId(String roomTypeId) {
		this.roomTypeId = roomTypeId;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getDaysAmountPrice() {
		return daysAmountPrice;
	}

	public void setDaysAmountPrice(String daysAmountPrice) {
		this.daysAmountPrice = daysAmountPrice;
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

	public int getRooms() {
		return rooms;
	}

	public void setRooms(int rooms) {
		this.rooms = rooms;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getTotalAmountPrice() {
		return TotalAmountPrice;
	}

	public void setTotalAmountPrice(String totalAmountPrice) {
		TotalAmountPrice = totalAmountPrice;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public String getGuestName() {
		return guestName;
	}

	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getGuestMobile() {
		return guestMobile;
	}

	public void setGuestMobile(String guestMobile) {
		this.guestMobile = guestMobile;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getValiCode() {
		return valiCode;
	}

	public void setValiCode(String valiCode) {
		this.valiCode = valiCode;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public String getMasterMobileNumber() {
		return masterMobileNumber;
	}

	public void setMasterMobileNumber(String masterMobileNumber) {
		this.masterMobileNumber = masterMobileNumber;
	}

	public String getPeriodDate() {
		return periodDate;
	}

	public void setPeriodDate(String periodDate) {
		this.periodDate = periodDate;
	}

	public String getCertificatesType() {
		return certificatesType;
	}

	public void setCertificatesType(String certificatesType) {
		this.certificatesType = certificatesType;
	}

	public String getCertificatesNumber() {
		return certificatesNumber;
	}

	public void setCertificatesNumber(String certificatesNumber) {
		this.certificatesNumber = certificatesNumber;
	}

	public String getOrderIP() {
		return orderIP;
	}

	public void setOrderIP(String orderIP) {
		this.orderIP = orderIP;
	}

	public int getIsReturnOrderInfo() {
		return isReturnOrderInfo;
	}

	public void setIsReturnOrderInfo(int isReturnOrderInfo) {
		this.isReturnOrderInfo = isReturnOrderInfo;
	}
	
	public String getBody(){
		StringBuffer bodyBuffer = new StringBuffer();
		bodyBuffer.append("<body>");
		
		// hotelInfo start
		bodyBuffer.append("<hotelInfo>");
		bodyBuffer.append("<hotelId>");
		bodyBuffer.append(hotelId);
		bodyBuffer.append("</hotelId>");
		
		bodyBuffer.append("<roomTypeId>");
		bodyBuffer.append(roomTypeId);
		bodyBuffer.append("</roomTypeId>");

		bodyBuffer.append("<policyId>");
		bodyBuffer.append(policyId);
		bodyBuffer.append("</policyId>");

		bodyBuffer.append("<daysAmountPrice>");
		bodyBuffer.append(daysAmountPrice);
		bodyBuffer.append("</daysAmountPrice>");
		bodyBuffer.append("</hotelInfo>");
		// hotelInfo end
		
		//bookInfo start
		bodyBuffer.append("<bookInfo>");
		bodyBuffer.append("<comeDate>");
		bodyBuffer.append(comeDate);
		bodyBuffer.append("</comeDate>");
		
		bodyBuffer.append("<leaveDate>");
		bodyBuffer.append(leaveDate);
		bodyBuffer.append("</leaveDate>");
		
		bodyBuffer.append("<rooms>");
		bodyBuffer.append(rooms);
		bodyBuffer.append("</rooms>");
		
		bodyBuffer.append("<arriveTime>");
		bodyBuffer.append(arriveTime);
		bodyBuffer.append("</arriveTime>");
		
		bodyBuffer.append("<TotalAmountPrice>");
		bodyBuffer.append(TotalAmountPrice);
		bodyBuffer.append("</TotalAmountPrice>");
		bodyBuffer.append("</bookInfo>");
		//bookInfo end
		
		//guestInfo start
		bodyBuffer.append("<guestInfo>");
		bodyBuffer.append("<contactName>");
		bodyBuffer.append(contactName);
		bodyBuffer.append("</contactName>");
		
		bodyBuffer.append("<contactMobile>");
		bodyBuffer.append(contactMobile);
		bodyBuffer.append("</contactMobile>");
		
		bodyBuffer.append("<guestName>");
		bodyBuffer.append(guestName);
		bodyBuffer.append("</guestName>");
		
		bodyBuffer.append("<guestMobile>");
		bodyBuffer.append(guestMobile);
		bodyBuffer.append("</guestMobile>");
		bodyBuffer.append("</guestInfo>");
		//guestInfo end
		
		
		//creditCardInfo start
		if( !TextUtils.isEmpty(cardNumber) ){
			bodyBuffer.append("<creditCardInfo>");
			bodyBuffer.append("<cardNumber>");
			bodyBuffer.append(cardNumber);
			bodyBuffer.append("</cardNumber>");

			bodyBuffer.append("<cardType>");
			bodyBuffer.append(cardType);
			bodyBuffer.append("</cardType>");

			bodyBuffer.append("<valiCode>");
			bodyBuffer.append(valiCode);
			bodyBuffer.append("</valiCode>");

			bodyBuffer.append("<masterName>");
			bodyBuffer.append(masterName);
			bodyBuffer.append("</masterName>");

			bodyBuffer.append("<masterMobileNumber>");
			bodyBuffer.append(masterMobileNumber);
			bodyBuffer.append("</masterMobileNumber>");

			bodyBuffer.append("<periodDate>");
			bodyBuffer.append(periodDate);
			bodyBuffer.append("</periodDate>");

			bodyBuffer.append("<certificatesType>");
			bodyBuffer.append(certificatesType);
			bodyBuffer.append("</certificatesType>");

			bodyBuffer.append("<certificatesNumber>");
			bodyBuffer.append(certificatesNumber);
			bodyBuffer.append("</certificatesNumber>");
			bodyBuffer.append("</creditCardInfo>");
		}
		//creditCardInfo end
		
		//platInfo start
		bodyBuffer.append("<platInfo>");
		bodyBuffer.append("<orderIP>");
		bodyBuffer.append(orderIP);
		bodyBuffer.append("</orderIP>");
		bodyBuffer.append("</platInfo>");
		//platInfo end

		bodyBuffer.append("<isReturnOrderInfo>");
		bodyBuffer.append(isReturnOrderInfo);
		bodyBuffer.append("</isReturnOrderInfo>");
		
		bodyBuffer.append("</body>");
		return bodyBuffer.toString();
	}
	
}
