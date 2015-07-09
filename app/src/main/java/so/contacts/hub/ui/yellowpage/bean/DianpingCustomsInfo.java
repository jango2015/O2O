package so.contacts.hub.ui.yellowpage.bean;

/**
 * 黄页详情（大众点评）团购信息
 * @author Michael
 *
 */
public class DianpingCustomsInfo implements java.io.Serializable {

	private String deal_id; // 团购单ID
	private String title; // 团购标题
	private String description; // 团购描述
	private String city;
	private double list_price; // 团购包含商品原价值
	private double current_price; // 团购价格
	private double regions; // 团购适用商户所在商区
	private int purchase_count; // 团购当前已购买数
	private String publish_date; // 团购发布上线日期
	private String details; // 团购详情
	private String purchase_deadline; // 团购单的截止购买日期
	private String image_url; // 团购图片链接，最大图片尺寸450×280
	private String s_image_url; // 小尺寸团购图片链接，最大图片尺寸160×100
	private String more_s_image_urls; // 更多小尺寸图片
	private int is_popular; // 是否为热门团购，0：不是，1：是
	private String notice;
	private String deal_url;
	private String deal_h5_url;
	private double commission_ratio; // 当前团单的佣金比例

	public DianpingCustomsInfo() {

	}

	public String getDeal_id() {
		return deal_id;
	}

	public void setDeal_id(String deal_id) {
		this.deal_id = deal_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public double getList_price() {
		return list_price;
	}

	public void setList_price(double list_price) {
		this.list_price = list_price;
	}

	public double getCurrent_price() {
		return current_price;
	}

	public void setCurrent_price(double current_price) {
		this.current_price = current_price;
	}

	public double getRegions() {
		return regions;
	}

	public void setRegions(double regions) {
		this.regions = regions;
	}

	public int getPurchase_count() {
		return purchase_count;
	}

	public void setPurchase_count(int purchase_count) {
		this.purchase_count = purchase_count;
	}

	public String getPublish_date() {
		return publish_date;
	}

	public void setPublish_date(String publish_date) {
		this.publish_date = publish_date;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getPurchase_deadline() {
		return purchase_deadline;
	}

	public void setPurchase_deadline(String purchase_deadline) {
		this.purchase_deadline = purchase_deadline;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getS_image_url() {
		return s_image_url;
	}

	public void setS_image_url(String s_image_url) {
		this.s_image_url = s_image_url;
	}

	public String getMore_s_image_urls() {
		return more_s_image_urls;
	}

	public void setMore_s_image_urls(String more_s_image_urls) {
		this.more_s_image_urls = more_s_image_urls;
	}

	public int getIs_popular() {
		return is_popular;
	}

	public void setIs_popular(int is_popular) {
		this.is_popular = is_popular;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public String getDeal_url() {
		return deal_url;
	}

	public void setDeal_url(String deal_url) {
		this.deal_url = deal_url;
	}

	public String getDeal_h5_url() {
		return deal_h5_url;
	}

	public void setDeal_h5_url(String deal_h5_url) {
		this.deal_h5_url = deal_h5_url;
	}

	public double getCommission_ratio() {
		return commission_ratio;
	}

	public void setCommission_ratio(double commission_ratio) {
		this.commission_ratio = commission_ratio;
	}

}
