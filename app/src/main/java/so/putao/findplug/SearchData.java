package so.putao.findplug;

public class SearchData {
	public volatile double latitude;
	public volatile double longitude;
	public volatile String city;
	public volatile String category;
	public volatile String keyword;
	public volatile int source;// 1 ：来自搜附近 、0：来自查号
	public volatile boolean showDianping;
	public volatile boolean showSougou;
	
	public volatile double token;//搜索的数据监测标志

	public SearchData(double latitude, double longitude, String city,
			String category, String keyword,int source, boolean showDianping, boolean showSougou) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.city = city;
		this.category = category;
		this.keyword = keyword;
		this.source = source;
		this.showDianping = showDianping;
		this.showSougou = showSougou;
	}

	public SearchData() {

	}
}