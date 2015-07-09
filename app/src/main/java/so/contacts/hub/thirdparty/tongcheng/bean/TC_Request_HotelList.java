package so.contacts.hub.thirdparty.tongcheng.bean;

import android.text.TextUtils;

public class TC_Request_HotelList extends TC_BaseData{

	private static final long serialVersionUID = 1L;

	private String cityId;
	
	private String comeDate; // "yyyy-MM-dd"
	
	private String leaveDate;
	
	private String keyword;
	
	private String priceRange;
	
	private String starRatedId;
	
	private int sortType;
	
	private int cs; //坐标系统
	
	private double latitude;
	
	private double longitude;
	
	private int radius; 
	
	private String clientIp; //客户请求Ip  【120.0.0.1】Ipv4格式
	
	/**
	 * 酒店状态
	 * 1 全部酒店 默认
	 * 2 可预订的 (去除全满房、全价格未定) 
	 */
	private int hbs;
	
	private int bizSectionId; //商区Id
	
	private int page;
	
	private int pageSize;

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
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

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getPriceRange() {
		return priceRange;
	}

	public void setPriceRange(String priceRange) {
		this.priceRange = priceRange;
	}

	public String getStarRatedId() {
		return starRatedId;
	}

	public void setStarRatedId(String starRatedId) {
		this.starRatedId = starRatedId;
	}

	public int getSortType() {
		return sortType;
	}

	public void setSortType(int sortType) {
		this.sortType = sortType;
	}

	public int getCs() {
		return cs;
	}

	public void setCs(int cs) {
		this.cs = cs;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public int getHbs() {
		return hbs;
	}

	public void setHbs(int hbs) {
		this.hbs = hbs;
	}
	
	public int getBizSectionId() {
		return bizSectionId;
	}

	public void setBizSectionId(int bizSectionId) {
		this.bizSectionId = bizSectionId;
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
	
	public String getBody(){
		StringBuffer bodyBuffer = new StringBuffer();
		bodyBuffer.append("<body>");
		
		bodyBuffer.append("<cityId>");
		bodyBuffer.append(cityId);
		bodyBuffer.append("</cityId>");
		
		bodyBuffer.append("<comeDate>");
		bodyBuffer.append(comeDate);
		bodyBuffer.append("</comeDate>");
		
		bodyBuffer.append("<leaveDate>");
		bodyBuffer.append(leaveDate);
		bodyBuffer.append("</leaveDate>");

		if( !TextUtils.isEmpty(keyword) ){
			bodyBuffer.append("<keyword>");
			bodyBuffer.append(keyword);
			bodyBuffer.append("</keyword>");

			// 当有keyword时必传入searchFields
			bodyBuffer.append("<searchFields>");
			bodyBuffer.append("hotelName,address,nearby,chainName");
			bodyBuffer.append("</searchFields>");
		}

		if( !TextUtils.isEmpty(priceRange) ){
			bodyBuffer.append("<priceRange>");
			bodyBuffer.append(priceRange);
			bodyBuffer.append("</priceRange>");
		}
		
		if( !TextUtils.isEmpty(starRatedId) ){
			bodyBuffer.append("<starRatedId>");
			bodyBuffer.append(starRatedId);
			bodyBuffer.append("</starRatedId>");
		}
		
		if( sortType != 0 ){
			bodyBuffer.append("<sortType>");
			bodyBuffer.append(sortType);
			bodyBuffer.append("</sortType>");
		}

		if (latitude != 0 && longitude != 0) {
			bodyBuffer.append("<latitude>");
			bodyBuffer.append(latitude);
			bodyBuffer.append("</latitude>");
			
			bodyBuffer.append("<longitude>");
			bodyBuffer.append(longitude);
			bodyBuffer.append("</longitude>");
			
			if( cs == 0 ){
				cs = 1; // 默认为百度坐标系统
			}
			bodyBuffer.append("<cs>");
			bodyBuffer.append(cs);
			bodyBuffer.append("</cs>");
			
			if( radius == 0 ){
				radius = 5000; //有经纬度时必传,单位:米
			}
			bodyBuffer.append("<radius>");
			bodyBuffer.append(radius);
			bodyBuffer.append("</radius>");
		}
		
		bodyBuffer.append("<clientIp>");
		bodyBuffer.append(clientIp);
		bodyBuffer.append("</clientIp>");
		
		if( bizSectionId != 0 ){
			bodyBuffer.append("<bizSectionId>");
			bodyBuffer.append(bizSectionId);
			bodyBuffer.append("</bizSectionId>");
		}
		
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
		
		bodyBuffer.append("</body>");
		return bodyBuffer.toString();
	}
	
	
	
}
