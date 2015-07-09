package so.contacts.hub.thirdparty.elong.bean;

import java.math.BigDecimal;

/**
 * 艺龙酒店 订单历史
 */
public class OrderHistory {

	private Long OrderId;   //订单编号
	private EnumOrderStatus Status;  //状态
	private BigDecimal TotalPrice; // 总价Decimal  
	private EnumCurrencyCode CurrencyCode;  //货币类型
	private String HotelId; //酒店编号
	private String HotelName; //酒店名称
	private String RoomTypeId; //房型编号
	private String RoomTypeName; //房型名称
	private int RatePlanId; //产品编号
	private String RatePlanName; //产品名称
	private String ArrivalDate; //入住日期
	private String DepartureDate; //离店日期
	private EnumCustomerType CustomerType; //客人类型
	private int NumberOfRooms; //房间数量
	private int NumberOfCustomers; //客人数量
	private EnumPaymentType PaymentType; //付款类型
	private String EarliestArrivalTime; //最早到店时间
	private String LatestArrivalTime; //最晚到店时间
	private EnumConfirmationType ConfirmationType; //确认类型
	private String NoteToHotel; //给酒店备注
	private String NoteToElong; //给艺龙备注
	
	public Long getOrderId() {
		return OrderId;
	}
	public void setOrderId(Long orderId) {
		OrderId = orderId;
	}
	public EnumOrderStatus getStatus() {
		return Status;
	}
	public void setStatus(EnumOrderStatus status) {
		Status = status;
	}
	public BigDecimal getTotalPrice() {
		return TotalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		TotalPrice = totalPrice;
	}
	public EnumCurrencyCode getCurrencyCode() {
		return CurrencyCode;
	}
	public void setCurrencyCode(EnumCurrencyCode currencyCode) {
		CurrencyCode = currencyCode;
	}
	public String getHotelId() {
		return HotelId;
	}
	public void setHotelId(String hotelId) {
		HotelId = hotelId;
	}
	public String getHotelName() {
		return HotelName;
	}
	public void setHotelName(String hotelName) {
		HotelName = hotelName;
	}
	public String getRoomTypeId() {
		return RoomTypeId;
	}
	public void setRoomTypeId(String roomTypeId) {
		RoomTypeId = roomTypeId;
	}
	public String getRoomTypeName() {
		return RoomTypeName;
	}
	public void setRoomTypeName(String roomTypeName) {
		RoomTypeName = roomTypeName;
	}
	public int getRatePlanId() {
		return RatePlanId;
	}
	public void setRatePlanId(int ratePlanId) {
		RatePlanId = ratePlanId;
	}
	public String getRatePlanName() {
		return RatePlanName;
	}
	public void setRatePlanName(String ratePlanName) {
		RatePlanName = ratePlanName;
	}
	public String getArrivalDate() {
		return ArrivalDate;
	}
	public void setArrivalDate(String arrivalDate) {
		ArrivalDate = arrivalDate;
	}
	public String getDepartureDate() {
		return DepartureDate;
	}
	public void setDepartureDate(String departureDate) {
		DepartureDate = departureDate;
	}
	public EnumCustomerType getCustomerType() {
		return CustomerType;
	}
	public void setCustomerType(EnumCustomerType customerType) {
		CustomerType = customerType;
	}
	public int getNumberOfRooms() {
		return NumberOfRooms;
	}
	public void setNumberOfRooms(int numberOfRooms) {
		NumberOfRooms = numberOfRooms;
	}
	public int getNumberOfCustomers() {
		return NumberOfCustomers;
	}
	public void setNumberOfCustomers(int numberOfCustomers) {
		NumberOfCustomers = numberOfCustomers;
	}
	public EnumPaymentType getPaymentType() {
		return PaymentType;
	}
	public void setPaymentType(EnumPaymentType paymentType) {
		PaymentType = paymentType;
	}
	public String getEarliestArrivalTime() {
		return EarliestArrivalTime;
	}
	public void setEarliestArrivalTime(String earliestArrivalTime) {
		EarliestArrivalTime = earliestArrivalTime;
	}
	public String getLatestArrivalTime() {
		return LatestArrivalTime;
	}
	public void setLatestArrivalTime(String latestArrivalTime) {
		LatestArrivalTime = latestArrivalTime;
	}
	public EnumConfirmationType getConfirmationType() {
		return ConfirmationType;
	}
	public void setConfirmationType(EnumConfirmationType confirmationType) {
		ConfirmationType = confirmationType;
	}
	public String getNoteToHotel() {
		return NoteToHotel;
	}
	public void setNoteToHotel(String noteToHotel) {
		NoteToHotel = noteToHotel;
	}
	public String getNoteToElong() {
		return NoteToElong;
	}
	public void setNoteToElong(String noteToElong) {
		NoteToElong = noteToElong;
	}
	
}
