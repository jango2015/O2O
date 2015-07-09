package so.contacts.hub.ui.yellowpage.bean;

import java.util.List;

import so.putao.findplug.SourceItemObject;

/**
 * 葡萄本地数据结构
 * @author hyl 2014-7-14
 */
public class PuTaoResultItem extends SourceItemObject {

	private static final long serialVersionUID = 1L;

	private String webSite; // 网址
	private List<SerchItem> search_info; // 搜索信息
	private List<NumberItem> numbers; // 号码信息
	private List<FastServiceItem> fast_service; // 快捷服务
	
	private long category_id;
	private long itemId; // 
    private int remind_code; // 对应打点服务节点
	
	public static final int SOURCE_TYPE_DETAIL = 1;
	public static final int SOURCE_TYPE_SERVER = 2;
	public static final int SOURCE_TYPE_CARD = 3;
	
	/**
	 * 葡萄静态数据类型
	 * 1: 静态详情数据，跳转进入详情
	 * 2：应用服务数据，跳转进入服务
	 * 3：卡片数据（图片样式展示）
	 */
	private int source_type; 
	
	private String intent_activity; // 跳转activity
	
	private String intent_url; // 点击跳转的H5页面Url
	
	private String title; // 跳转进入activity的title
	
	private long start_time;  // 数据有效开始时间
	
	private long end_time;  // 数据有效结束时间

	public String getWebsite() {
		return webSite;
	}

	public void setWebsite(String website) {
		this.webSite = website;
	}

	public List<SerchItem> getSearchInfo() {
		return search_info;
	}

	public void setSearchInfo(List<SerchItem> searchInfo) {
		this.search_info = searchInfo;
	}

	public List<NumberItem> getNumbers() {
		return numbers;
	}

	public void setNumbers(List<NumberItem> numbers) {
		this.numbers = numbers;
	}

	public List<FastServiceItem> getFast_service() {
		return fast_service;
	}

	public void setFast_service(List<FastServiceItem> fast_service) {
		this.fast_service = fast_service;
	}
	
	public long getCategory_id() {
		return category_id;
	}

	public void setCategory_id(long category_id) {
		this.category_id = category_id;
	}

	public int getRemind_code() {
		return remind_code;
	}

	public void setRemind_code(int remind_code) {
		this.remind_code = remind_code;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public int getSource_type() {
		return source_type;
	}

	public void setSource_type(int source_type) {
		this.source_type = source_type;
	}

	public String getIntent_activity() {
		return intent_activity;
	}

	public void setIntent_activity(String intent_activity) {
		this.intent_activity = intent_activity;
	}

	public String getIntent_url() {
		return intent_url;
	}

	public void setIntent_url(String intent_url) {
		this.intent_url = intent_url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getStart_time() {
		return start_time;
	}

	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	public long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	@Override
	public double getLatitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getLongitude() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
