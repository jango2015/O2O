package so.contacts.hub.thirdparty.tongcheng.bean;


public class TC_HotelBean extends TC_BaseData {

	private static final long serialVersionUID = 1L;
	
	private String hotelId;
	
	private String hotelName; //名称
	
	private double distance; //距离
	
	private String img; //图片
	
	private float bonusRate;// 可返(返还的奖金)
	
	private double lowestPrice;//价格（最低价）
	
	//评分 = (好评数 + 中评数) / 总点评数 * 5
	private int commentTotal; //总点评数 (commentCount - commentTotal)
	
	private int commentGood; //好评数 (commentCount - commentGood)
	
	private int commentMid; //中评数 (commentCount - commentMid)
	
	private String starRatedName;// 类型（starRated - starRatedName）
	
	private int starRatedId;

    private String bizSectionName; // 商区 (bizSection - bizSectionName)
	
	private int bizSectionId;	// 商区 ID
	
	private String address; 	// 酒店地址
	
	private String longitude;
	
	private String latitude;

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public float getBonusRate() {
		return bonusRate;
	}

	public void setBonusRate(float bonusRate) {
		this.bonusRate = bonusRate;
	}

	public double getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(double lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	public int getCommentTotal() {
		return commentTotal;
	}

	public void setCommentTotal(int commentTotal) {
		this.commentTotal = commentTotal;
	}

	public int getCommentGood() {
		return commentGood;
	}

	public void setCommentGood(int commentGood) {
		this.commentGood = commentGood;
	}

	public int getCommentMid() {
		return commentMid;
	}

	public void setCommentMid(int commentMid) {
		this.commentMid = commentMid;
	}

	public String getStarRatedName() {
		return starRatedName;
	}

	public void setStarRatedName(String starRatedName) {
		this.starRatedName = starRatedName;
	}

	public String getBizSectionName() {
		return bizSectionName;
	}

	public void setBizSectionName(String bizSectionName) {
		this.bizSectionName = bizSectionName;
	}

	public int getBizSectionId() {
		return bizSectionId;
	}

	public void setBizSectionId(int bizSectionId) {
		this.bizSectionId = bizSectionId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public double getMarkNum(){
		return commentTotal == 0 ? 0 : (commentGood + commentMid) * 5.0d / commentTotal;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public int getStarRatedId() {
		return starRatedId;
	}

	public void setStarRatedId(int starRatedId) {
		this.starRatedId = starRatedId;
	}
	
}
