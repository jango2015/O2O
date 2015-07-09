package so.contacts.hub.thirdparty.tongcheng.bean;

public class TC_HotelRoomBean extends TC_BaseData {

	private static final long serialVersionUID = 1L;

	private String photoUrl;			//图片
	
	private String roomTypeId;			//房型Id
	
	private String roomName;			//标题
	
	private String bed;					//床型
	
	private String breakfast;			//早餐

	private String policyId;			//政策id

	private String roomAdviceAmount;	//房型价格(包含每日房价，以;分开)
	
	private String roomPrize;			//可返:房型奖金(英文分号分隔多天价格例如:20;10)

	private String avgAmount;			//均价
	
	private int danBaoType;				//担保政策类型
	
	private int overTime;				//超时点钟
	
	private int presentFlag;  			//是否含有礼包(默认0不含)
	
	private int guaranteeType;			//担保类型(0-无担保；1-担保冻结；2-担保预付；3-代收代付)(用来判断是否需要支付再提交订单)
	
	private int guaranteeFlag;			//担保标示(0-非担保;1-担保)
	
	private int surplusRooms;			//剩余房间数(N=0表示有房，N>0:表示有几间房)
	
	/**
		0-	可预订
		1-	不可预定
		2-	不可预定
		3-	酒店价格未定
		4-	酒店已经售完
		5-	提前预定
		6-	预定需连住
	 */
	private int bookingFlag;			//预定标示
	
	public TC_HotelRoomBean(){
		overTime = -1; //默认设置为-1
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getRoomTypeId() {
		return roomTypeId;
	}

	public void setRoomTypeId(String roomTypeId) {
		this.roomTypeId = roomTypeId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getBed() {
		return bed;
	}

	public void setBed(String bed) {
		this.bed = bed;
	}

	public String getBreakfast() {
		return breakfast;
	}

	public void setBreakfast(String breakfast) {
		this.breakfast = breakfast;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getRoomAdviceAmount() {
		return roomAdviceAmount;
	}

	public void setRoomAdviceAmount(String roomAdviceAmount) {
		this.roomAdviceAmount = roomAdviceAmount;
	}

	public String getRoomPrize() {
		return roomPrize;
	}

	public void setRoomPrize(String roomPrize) {
		this.roomPrize = roomPrize;
	}

	public String getAvgAmount() {
		return avgAmount;
	}

	public void setAvgAmount(String avgAmount) {
		this.avgAmount = avgAmount;
	}

	public int getDanBaoType() {
		return danBaoType;
	}

	public void setDanBaoType(int danBaoType) {
		this.danBaoType = danBaoType;
	}

	public int getOverTime() {
		return overTime;
	}

	public void setOverTime(int overTime) {
		this.overTime = overTime;
	}

	public int getPresentFlag() {
		return presentFlag;
	}

	public void setPresentFlag(int presentFlag) {
		this.presentFlag = presentFlag;
	}

	public int getGuaranteeType() {
		return guaranteeType;
	}

	public void setGuaranteeType(int guaranteeType) {
		this.guaranteeType = guaranteeType;
	}

	public int getGuaranteeFlag() {
		return guaranteeFlag;
	}

	public void setGuaranteeFlag(int guaranteeFlag) {
		this.guaranteeFlag = guaranteeFlag;
	}

	public int getSurplusRooms() {
		return surplusRooms;
	}

	public void setSurplusRooms(int surplusRooms) {
		this.surplusRooms = surplusRooms;
	}

	public int getBookingFlag() {
		return bookingFlag;
	}

	public void setBookingFlag(int bookingFlag) {
		this.bookingFlag = bookingFlag;
	}
	
	
}
